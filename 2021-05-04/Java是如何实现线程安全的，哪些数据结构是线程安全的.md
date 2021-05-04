## Java 是如何实现线程安全的，哪些数据结构是线程安全的？
#### 线程安全性
如果一个类/方法/变量，在被多线程访问的情况下，都能正确地执行，并且在调用代码不需要额外的同步机制，那么就可以说这个类/方法/变量时线程安全的。通常线程安全有这三个特性：可见性，有序性，原子性。
#### 如何实现线程安全
1、加锁：Java常用的内置锁和显式锁，都能保证同步代码块的代码同时只能一个线程能访问。

2、volatile变量：变量声明为volatile后，对这变量的操作不会与其他内存操作一起被重排序（有序性）。同时volatile变量也不会被缓存在寄存器或者对其他处理器可见的地方（可见性）。在多线程读，少于等于一个线程写的场景下可以保证线程安全，与CAS操作配合可以拓展到多线程写的场景。

3、线程封闭：一种可以避免使用同步的方式就是不共享数据。线程封闭技术保证数据只在单线程内被访问到，比如ThreadLocal、栈封闭。

4、final关键字：被final关键字修饰的对象，在初始化后不能再被改变，java.lang.String类就是通过这个关键字实现的线程安全。

### 线程安全的数据结构

- HashTable、Vector：简单地对读、写等方法加上synchronized修饰，保证线程安全，实现简单，但是并发性很差。
- AtomicInteger、AtomicLong等原子类：并发包中Atomic...开头等原子类通过volatile关键字保证有序性和可见性，通过CAS操作保证原子性。属于乐观并发策略，并且也需要阻塞线程，相对轻量级，性能较好。
- ConcurrentHashMap、LongAdder等分段加锁并发类：ConcurrentHashMap通过synchronized和CAS对内部数组的每个Node节点单独加锁；LongAdder则是内部数组的每个Cell节点，单独通过CAS操作保持同步。
- BlockingQueue等阻塞队列的实现类：在take、offer、poll、remove等读写操作都会通过ReentrantLock显式锁加锁。
- String字符串对象：字符串对象通过final关键字，使对象变为不可变的（Immutable），来实现线程安全。