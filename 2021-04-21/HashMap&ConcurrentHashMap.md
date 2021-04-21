## HashMap 与 ConcurrentHashMap 的实现原理是怎样的？ConcurrentHashMap 是如何保证线程安全的？

#### HashMap
HashMap在Java中通过数组来存放一个个“桶”，“桶”的实现根据实际数据情况有链表和红黑树两种可能。通过位运算决定元素的数组下标,具体算法是对hash值对数组长度取余，但是因为扩容机制保证了数组长度为2的N次方，所以实际实现为更高效的位运算——hash值与数组长度-1进行按位与。hash冲突时采用“尾插法”解决，链表长度达到8转换成红黑树，在元素数量达到负载因子决定的阈值后会2倍扩容。
#### ConcurrentHashMap
和HashMap一样通过数组来存放“桶”Node。如果要插入的数组位置不存在Node，会CAS插入Node；若已存在Node，并且Node的hash值为MOVED（-1）移动中，表明正进行扩容，当前线程加入帮忙扩容。否则通过synchronized对当前Node加锁，进行插入。和HashMap一样，hash冲突采用“尾插法”解决，链表长度达到8转换成红黑树。

