## MySQL MVCC 的实现原理
> 多版本并发控制（multiversion concurrency control，以下简称MVCC）是用来提供数据库并发访问能力的。

数据库要实现并发访问，最简单的方法就是给访问中的数据加上读写锁。然而读写锁的效率太低了，在长事务中会更加慢得无法忍受。为了高效地并发访问数据，MVCC保存了数据的多个拷贝。每个连接数据库的用户看到的都是数据库在某个时间点的一个快照。

### MVCC的实现原理
在实现上，数据库会创建一个视图，访问的时候以视图的逻辑结果为准。这个视图就是InnoDB在实现MVCC用到的一致性读视图，即consistent read view。

- 在“可重复读”级别下，这个视图是在启动时创建的，整个事务存在期间都用这个视图。
- 在“读提交”级别下，这个视图是在每个SQL语句开始执行时创建的。
- “读未提交”级别下直接返回记录上的最新值，**没有视图概念**。
- “串行化”级别，直接用加锁的方式避免并行访问。

#### “数据快照”到底是什么
“快照”是基于整库的。但实际上快照并不需要完整拷贝库里所有的数据。原因在于快照的实现有以下特点：

- InnoDB里的每个事务都有一个唯一的事务ID，并且这个ID是严格递增的。
- 每行数据有多个版本，每次事务更新数据，都生成一个新的数据版本，并且把事务ID赋值给这个数据版本的事务ID（数据行的三个隐藏字段之一，为了和事务的ID字段区分，这里记为row trx\_id）。
- 旧的数据版本要保留，并且有对应的row trx\_id。
- 新的数据版本，可以通过undo log得到旧的数据版本。

由于最新的数据版本，我们肯定是要保存的。而row trx_id作为数据行的隐藏字段，可以忽略不计。所以实际上“数据快照”的大小，和undo log的大小差不多。

#### 如何找到正确的“数据快照”
既然所有的数据版本都有保留，那么当前事务如何找到自己应该看到的数据快照呢？以默认隔离级别—**可重复读**作为例子，问题可以进一步具体化为：

> 如何找到正确的数据快照，使得当前事务启动后，读到的数据都是同一个版本？

这就要从一致性视图的实现上说起：

- InnoDB为每个事务构造了一个数组，保存事务启动瞬间，当前的活跃事务（启动了但还没提交的事务）。
- 数组里事务ID的最小值记为低水位，当前系统里创建过的事务ID的最大值+1记为高水位。
- 这个视图数组和高水位，构成了一致性视图。

数据版本的可见性，就是基于数据的row trx\_id和这个一致性视图得对比结果得到的。视图数组把row trx\_id分成了几种情况。

![](https://static001.geekbang.org/resource/image/88/5e/882114aaf55861832b4270d44507695e.png)

这样，在一个事务启动时，一个数据版本的row trx\_id，有以下几种情况：

1、落在绿色部分，表示这个数据版本在当前事务启动前已经提交，可见；

2、落在黄色部分，表示产生这个数据版本的事务仍未提交。不可见。

3、至于红色部分，产生这个数据版本的事务还没启动，显然也不可见。

根据以上规则，当前事务会找它可见范围内最“新”的数据版本，在整个事务期间都以这个版本为准。那么开始的问题也有了答案：事务启动前，已提交的“最新”数据版本，就是当前事务正确的快照（或者说认可的快照）。

#### 更新事务的数据版本可见性
对于更新操作事务，除了考虑上述规则外，还需要加入“当前读”（current read）规则去分析。

> 更新数据都是先读后写，这里的读只能读当前值，即“当前读”。
 
除了update以外，select加锁，如 ```select ... for share``` 或 ```select ... for update```，也是当前读。如果当前读的记录行锁被占用了的话，需要进入锁等。

### 总结

- 可重复读，查询只看事务启动前提交完成的数据。
- 读提交，查询只看SQL语句启动前提交完成的数据。
- 当前读，总是读取数据的最新版本。