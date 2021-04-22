## Synchronized 关键字底层是如何实现的？它与 Lock 相比优缺点分别是什么？
#### synchronized底层实现
1、被synchronized所包围的范围会形成一个临界区，JVM编译的时候会在临界区的前后生成monitorenter和monitorexit指令。monitorenter申请获得monitor对象，monitorexit释放monitor对象。

2、任何对象都有一个monitor对象与之关联，monitor基于操作系统底层的Mutex Lock实现的。

3、Mutex Lock（互斥锁）保护临界区对共享资源的访问。先对互斥量加锁，如互斥量已经上锁，调用线程会阻塞，直到互斥量被解锁。

4、Mutex工作方式：

- 申请Mutex；
- 成功则持有该Mutex，失败则进行spin自旋，不断发起mutex gets，直到获得mutex或者达到spin_count限制
- 根据工作模式选择yield还是sleep
- 若达到sleep限制或者被唤醒或者完成yield，重复上述3步，直到获得mutex。

#### 锁消除
1、如果是线程封闭的锁对象，锁对象只能由一个线程访问，JVM会通过优化去掉这个获取锁的动作

2、JVM可以通过逸出分析（Escape Analysis）来找出不会发布到堆的本地对象引用（封闭在栈中的变量，ThreadLocal等），也会消除锁的获取

3、锁粒度粗化（Lock Coarsening）将临近的锁合并为一个锁，如下代码，3个add和toString合并为一次加锁操作。
```
	public String getLisiName() {
		List<String> lsit = new Vector();
		list.add("a");
		list.add("b");
		list.add("c");
		return list.toString();
	}
```
#### 加锁过程
1、锁对象第一次被线程获取的时候，偏向模式置为1，同时CAS记录线程id到对象的Mark Word中。持有偏向锁的线程后续每次进入不再需要任何同步操作（加锁，解锁以及对MarkWord的更新等）

2、一旦另一个线程尝试获取这个锁，偏向模式马上结束了，申请锁的线程会在栈帧的锁记录（Lock record
）存储锁对象Mark Word的拷贝，并且通过CAS才做把Mark Word指向锁记录.

3、如果第2步中的CAS操作失败，说明有2条以上线程争抢一个锁，轻量级锁膨胀为重量级锁。

#### 与Lock相比
JDK1.6以后的synchronied通过偏向锁，轻量级锁等优化提高了性能。在那之前synchronized加锁都是重量级锁，性能明显低于Lock，因为Java的线程是映射到操作系统的线程上的，每次线程挂起和唤醒都需要从用户态切换到内核态，比较耗时间。

Lock的优点：

- Lock可以实现定时的锁等待；
- Lock也可以在锁等待的时候响应中断；
- Lock可以选择实现为公平锁还是非公平锁。

Lock的缺点：

- 危险性较高，如果忘记在finally释放，虽然代码能跑，但是埋下了“坑”；
- 没有synchronized简洁紧凑；
- 目前性能上已被synchronized反超，未来更可能继续提升synchronized而不是Lock的性能。

