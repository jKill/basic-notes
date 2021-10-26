## Redis 有几种数据结构？Zset 是如何实现的？
Redis**常用**的数据结构有以下5种：

- String：缓存、计数器、分布式锁等。
- List：链表、队列、微博关注人时间轴列表等。
- Hash：用户信息、哈希表等。
- Set：去重、赞、踩、共同好友。
- Zset：访问量排行榜、点击量排行榜。

### SDS
Redis用C开发，但是没使用C的字符串。而是用SDS结构体保存

```c
struct sdshdr {
    int len;
    int free;
    char buf[];
}
```

- len：记录buf中已用空间长度。
- free：buf中空闲空间长度。
- buf[]：存储实际内容。

#### SDS优点：

- 常数时间获取字符串长度：C字符串查询长度的时间为O(N)，而SDS只需要O(1)。
- 空间预分配和惰性释放：当SDS需要扩容时，会分配额外的空间。缩容时不会回收多余的空间，而是用free记录，后面需要append时直接使用free中的空间。
- 二进制安全：二进制数据并不是规则的字符串，可能包含'\0'。C中'\0'表示字符串结束，但SDS中标志字符串结束的是len属性。

### 哈希
类似于Java的HashMap，除了Redis本身的数据库，字典也是哈希键的底层实现。

```c
typedef struct dict{
      dictType *type;
    void *privdata;
    dictht ht[2];
    int trehashidx;
}
```

#### Rehash
Rehash过程如下：

- 为ht[1] 分配空间
- 将ht[0]的键值Rehash到ht[1]中。
- 当ht[0]全部迁移到ht[1]中后，释放ht[0]，将ht[1]置为ht[0]，并为ht[1]创建一张新表，为下次Rehash做准备。

#### 渐进式Rehash
如果ht[0]中的数据量很大，迁移过程会很久，并且影响其他用户的使用。为了避免Rehash对服务器性能造成影响。Redis使用了渐进式Rehash。

- 在字典中维护一个```rehashidx```，置为0，表示Rehash开始
- Rehash期间，每次对字典操作时，程序还顺便把ht[0]在```rehashidx```索引上的所有键值对rehash到ht[1]中，完成后将```rehashidx```+1。全部Rehash完成后，将```rehashidx```置为-1，表示rehash完成。

### 有序集合
Zset 是一个有序的链表结构，其底层的数据结构是跳跃表 skiplist，其结构如下：

```c
typedef struct zskiplistNode {
	//成员对象
	robj *obj;
	//分值
	double score;
	//后退指针
	struct zskiplistNode *backward;
	//层
	struct zskiplistLevel {
	    struct zskiplistNode *forward;//前进指针
	    unsigned int span;//跨度
	} level[];
} zskiplistNode;
```
 
- 前进指针：从表头向表尾遍历，一次可跨越多个节点。
- 后退指针：从表尾向表头回退，一次只能回退一个节点。
- 跨度：表示当前节点和下一个节点的距离。
- 分值：用于排序，搜索时会比较。

#### Redis中跳表与传统跳表的区别

- 传统的跳表上下两层链表的节点数为1:2，为了维护上下两层节点1:2，在增删节点都需要改变节点的层数，导致时间复杂度从O(logN)退化到O(N)。
- 而跳表每个节点的层数是随机的，增删节点只需要修改前后节点的指针即可。

#### 与红黑树和平衡树的对比

- 平衡树查找性能更好。增删因为需要旋转保持平衡，不如跳表。
- 红黑树查找和增删性能都与跳表相似。区别是实现不如跳表简单。

### 压缩列表
压缩列表 ziplist 是为 Redis 节约内存而开发的，数据量少的时候，是列表、字典和有序集合的底层实现之一。

- 元素较少时，Redis用ziplist存储数据。
- 元素个数达到一定数量时，列表键ziplist会转化为linkedlist，字典键会把ziplist转化为hashtable。
- ziplist内存连续分配，所以遍历速度快。