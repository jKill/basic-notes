## 集合类中的 List 和 Map 的线程安全版本是什么，如何保证线程安全的？

集合类中SynchronizedList和SynchronizedMap是List和Map的线程安全版本。

### 如何保证线程安全
SynchronizedList和SynchronizedMap实现线程安全的方法是一样的。都是在类内部持有一个final修饰的Object mutex，作为内部锁加锁的对象。

SynchronizedList部分源码：

```java
public E get(int index) {
    synchronized (mutex) {return list.get(index);}
}
public boolean add(E e) {
    synchronized (mutex) {return c.add(e);}
}
public boolean remove(Object o) {
    synchronized (mutex) {return c.remove(o);}
}
```

SynchronizedMap部分源码：

```java
public V get(Object key) {
    synchronized (mutex) {return m.get(key);}
}

public V put(K key, V value) {
    synchronized (mutex) {return m.put(key, value);}
}
public V remove(Object key) {
    synchronized (mutex) {return m.remove(key);}
}
```

通过源码我们可以看到，在SynchronizedList的方法，不管是读取还是添加、删除等操作，都使用内置锁进行了同步，同步块里直接调用List的get()、add()、remove()完成。

### 拓展：相似的类
这种统一加锁的操作很容易让我们想到Vector和HashTable，这两个类也是通过对所有方法加内置锁这种简单粗暴的方法实现线程安全的。唯一的不同是Vector和HashTable使用synchronized修饰方法，而SynchronizedList和SynchronizedMap是持有锁对象和原集合，使用同步代码块的加锁方式。这样尽管锁的粒度没有降低，但是程序员多了一个选择：在不需要加锁时，可以直接操作原集合。