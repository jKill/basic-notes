## HashMap 与 ConcurrentHashMap 的实现原理是怎样的？ConcurrentHashMap 是如何保证线程安全的？

### HashMap（JDK1.8）
#### 数据结构
HashMap底层实现为数组+链表/红黑树，即内部的Node数组。

- 链表：链表Node实现了Map.Entry接口。
- 红黑树：TreeNode继承了LinkedHashMap.Entry，而后者又继承了HashMap.Node

#### 散列算法
- 位运算优化：使用更高效的按位与（哈希值&数组长度减一），代替求余运算。
- 右移的原因：对哈希值右移后再异或，是为了**让哈希值高位的特征也体现在低位上，而散列的时候通常只有低位参与运算**，使数据散列得更均匀。

#### 碰撞处理
- 方法：使用拉链法解决，1.8使用“尾插法”，1.7则是“头插法”。
- 过程：遇到哈希冲突时，首先会在Node链表追加一个Node节点，如果长度超过了8，则转化为红黑树。

#### 扩容
- 时机：添加元素时，大小(size)达到阈值(threshold)触发。
- 过程：把容量(capacity)和阈值都扩大一倍，然后拷贝元素（需要重新计算下标，数组长度改变了，是原来的2倍）。

### ConcurrentHashMap（JDK1.8）
#### 数据结构
ConcurrentHashMap底层实现与HashMap类似，为数组+链表/红黑树，内部同样有一个Node数组。同样是”分段锁“的思想，但JDK1.7的实现相比，少了一次哈希定位，锁的粒度更细，并且使用了更多的CAS操作、synchronized代替ReentrantLock等优化。

- 链表：链表Node同样实现了Map.Entry接口。
- 红黑树：与HashMap不同，1、直接操作的数据结构是TreeBin而不是TreeNode，TreeBin作为树的根节点，也负责给树的重建操作加读-写锁；2、树节点**TreeNode直接继承ConcurrentHashMap.Node**
- ForwardingNode：一个用于连接两个table的节点类。它包含一个nextTable指针，用于指向下一张表。而且这个节点的key value next指针全部为null，它的hash值为-1. 这里面定义的find的方法是从nextTable里进行查询节点，而不是以自身为头节点进行查找

```java
/**
     * TreeNodes used at the heads of bins. TreeBins do not hold user
     * keys or values, but instead point to list of TreeNodes and
     * their root. They also maintain a parasitic read-write lock
     * forcing writers (who hold bin lock) to wait for readers (who do
     * not) to complete before tree restructuring operations.
     */
    static final class TreeBin<K,V> extends Node<K,V> {
        TreeNode<K,V> root;
        volatile TreeNode<K,V> first;
        volatile Thread waiter;
        volatile int lockState;
        // values for lockState
        static final int WRITER = 1; // set while holding write lock
        static final int WAITER = 2; // set when waiting for write lock
        static final int READER = 4; // increment value for setting read lock
```

#### 散列算法
- 位运算优化：同上HashMap
- 右移的原因：同上HashMap


#### 碰撞处理
- 方法：同样使用拉链法解决
- 过程：与HashMap不同，在链表长度达到8，**会首先判断大小(size)是否达到64了，如果没达到会首先尝试tryPresize()扩容**，直到size达到64才会把长度8以上的链表转化为红黑树。

#### 读写的线程安全
- 读：读操作没有加锁。
- 写：添加节点时，如果目标位置为空，CAS操作添加。否则判断节点hash值是否为-1（表示正在扩容），当前线程会加入帮忙扩容，否则synchronized锁住Node节点执行插入/更新。

#### 扩容时机：

- 链表长度达到8，且当前大小(size)小于64。
- 添加元素时，大小(size)达到阈值(threshold)。

#### 扩容过程：
添加元素时，看到有ForwardingNode(判断hash为-1)就知道正在扩容，当前线程加入帮忙扩容。

- 第一步：根据CPU核数和数组长度计算每个线程迁移多少个桶(用strike记录)，每个线程至少负责16个。
- 第二步：每个加入扩容过程的线程，根据transferIndex和strike得到当前本线程需要迁移的区间。迁移后在旧表设置一个ForwardingNode占位。
    
- 第三步：最后一个完成迁移的线程，检查所有桶是否都被正确迁移了。扩容冲突时，多个线程申请到了同一个transfer任务，当前线程申请的任务会作废。检查就是为了把因扩容冲突遗漏的桶，迁移到新表里。

```java 
/**
 * The next table index (plus one) to split while resizing.
 */
private transient volatile int transferIndex;
```

ForwardingNode作用：

- 判断是否在扩容。
- 最后一个线程完成迁移后，遍历节点看是否都为ForwardingNode，是则整个扩容完成了。
- 转发get请求，通过newTable属性，把get请求转发到扩容后的新数组。

#### size方法的实现
- volatile成员变量baseCount+CounterCell数组记录。
- 添加元素时，先CAS尝试更新baseCount，失败则随机在CounterCell里的一个位置执行更新。
