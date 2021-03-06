## 简述 ArrayList 与 LinkedList 的底层实现以及常见操作的时间复杂度

#### ArrayList
ArrayList底层实现为数组，非线程安全。

ArrayList内存上连续，可以通过下标访问数据，访问和更新时间复杂度O(1)。

插入、删除需要移动目标元素后面的元素，时间复杂度O(n)。

每次扩容为原大小的1.5倍，支持高效的位运算。

#### LinkedList
LinkedList底层实现为双向链表，非线程安全，

LinkedList内存不连续，访问只能通过节点之间的指针进行，时间复杂度O(n)

插入，删除都可以通过修改前后节点的指针指向完成，时间复杂度O(1)。

内存不连续，增删节点只需要简单修改指针，不需要扩容。