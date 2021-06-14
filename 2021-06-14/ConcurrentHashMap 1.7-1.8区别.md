## ConcurrentHashMap 1.7/1.8区别

||JDK1.7|JDK1.8|
|:--|:--|:--|
|数据结构|数组+链表(Segment)|数组+链表(Node)/红黑树(TreeNode)，ForwardingNode|
|定位数据|需要两次哈希运算，一次定位到具体的segment，第二次定位到具体的HashEntry|只需一次哈希运算，定位到具体的Node|
|实现线程安全的方式|使用分段锁对桶数组分成了多个Segment。Segment继承包含多个HashEntry，一次只锁住一个Segment里的几个桶|使用synchronized和CAS操作，一次只锁住一个桶|
|求size|遍历两次segments，比较两次的modCount，如果相同则说明期间没有改变过，返回遍历结果。否则锁住所有的segment再统计。|baseCount加上CounterCell数组的所有值。（添加元素时，先CAS尝试更新baseCount，失败则随机在CounterCell里的一个位置执行更新）|