## HashMap 1.7 / 1.8 的实现区别

#### hash冲突的解决
1.7 解决hash冲突使用”头插法“，1.8使用尾插法；
#### 底层数据结构
1.7底层实现为数组+链表。

```java
static class Entry<K,V> implements Map.Entry<K,V> {  
final K key;  
V value;  
Entry<K,V> next;  
int hash;  
} 
```

1.8底层实现为数组+Node类，Node默认实现为链表，节点达到8以上时会转化为红黑树，达到6时退化为链表。

```java
static class Node<K,V> implements Map.Entry<K,V> {  
final int hash;  
final K key;  
V value;  
Node<K,V> next;  
} 
```

```java
static final class TreeNode<K,V> extends LinkedHashMap.Entry<K,V> {  
TreeNode<K,V> parent;  // red-black tree links  
TreeNode<K,V> left;  
TreeNode<K,V> right;  
TreeNode<K,V> prev;    // needed to unlink next upon deletion  
boolean red;  
} 
```

#### hash值可变性：
1.7hash是可变的，因为有rehash的操作；1.8hash则用final修饰，也就是说hash值一旦确定，就不会再重新计算hash值了。
#### hash算法：
1.7会先判断Object是否是String，如果是则不使用String默认实现的hashcode()方法，以减少hash冲突

```java
final int hash(Object k) {  
int h = hashSeed;  
if (0 != h && k instanceof String) {  
return sun.misc.Hashing.stringHash32((String) k);  
}  
  
h ^= k.hashCode();  
  
// This function ensures that hashCodes that differ only by  
// constant multiples at each bit position have a bounded  
// number of collisions (approximately 8 at default load factor).  
h ^= (h >>> 20) ^ (h >>> 12);  
return h ^ (h >>> 7) ^ (h >>> 4);  
}  
```

1.8算出来的hash值只可能是一个，用final修饰

```java
static final int hash(Object key) {  
int h;  
return (key == null) ? 0 : (h = key.hashCode()) ^ (h >>> 16);  
} 
```
