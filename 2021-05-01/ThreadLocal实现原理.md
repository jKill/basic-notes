## ThreadLocal 实现原理是什么？
一个ThreadLocal对象对应一个线程，线程Thread持有ThreadLocalMap成员变量threadLocals，ThreadLocalMap是一个以ThreadLocal为key，实际存储的对象为value的K-V数组。ThreadLocal对象通过当前线程拿到对应的ThreadLocalMap，然后以自己为key(this)定位到对应的数组下标，拿到当前线程存储的value。是一种无锁的线程安全方案。

- 通过ThreadLocal对象定位到线程：Thread.currentThread()
- 通过ThreadLocal对象拿到所在的ThreadLocalMap: ThreadLocal.getMap(Thread t)
- 通过Thread拿到ThreadLocalMap：Thread.threadLocals

### 内存泄漏问题
为了避免内存泄漏问题，ThreadLocalMap的key设计成弱引用对象（extends WeakReference<ThreadLocal<?>>)，而弱引用会在JVM的下一次GC的时候被回收。所以在key被回收后，value作为Entry数组成员，还被Entry[]对象引用着，可能会造成内存泄漏问题。需要在使用结束后手动调用ThreadLocal.remove()释放。