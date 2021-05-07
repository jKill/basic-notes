## Java 中 sleep() 与 wait() 的区别
sleep()和wait()都会使线程进入等待状态，不分配CPU执行时间，但有以下区别：

- sleep()会使线程进入限期等待（Timed Waiting），而wait()可能进入无限期等待（Waiting）或者限期等待。
- 调用wait()的线程可以被另一个线程调用notify()唤醒，而sleep()不能
- wait()必须在synchronized块内调用，而sleep()没有这个要求。
- wait()会释放当前线程持有的锁（是所在的synchronized块对应的锁，不是所有的锁），而sleep()不会。如下代码所示：

```java
synchronized(LOCK) {
    Thread.sleep(1000); // LOCK is held
}


synchronized(LOCK) {
    LOCK.wait(); // LOCK is not held
}
```