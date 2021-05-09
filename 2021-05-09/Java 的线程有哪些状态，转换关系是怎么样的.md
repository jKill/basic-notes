## Java 的线程有哪些状态，转换关系是怎么样的？
Java线程有以下状态：

- 新建(New) 刚被创建的线程默认处于这个状态。
- 运行（Runnable）启动后的线程处于这个状态，可能正在等待CPU时间片，也可能已经获得了CPU时间片执行任务。
- 无限期等待（Waiting）等待被其他线程唤醒。
- 限期等待（Timed-Waiting）等待被其他线程唤醒或者时间到后系统自动唤醒。
- 阻塞（Blocked）这个状态的线程正阻塞等待monitor锁。
- 结束（Terminated）线程结束状态。

### 状态转换
- 新建-运行：线程新建后调用start()方法进入运行状态。
- 运行-阻塞：运行状态的线程，在进入synchronized临界区前等待锁时，切换到阻塞状态。
- 阻塞-运行：阻塞到运行有以下几种情况：1、进入synchronized临界区前等待一段时间后获取到锁；2、处于无限期/限期等待状态时被唤醒后，首先进入阻塞状态，再获取锁并切换到运行状态。
- 运行-无限期等待：线程调用wait()、join()、park()等方法后进入无限期等待状态。
- **无限期等待-阻塞**：处于无限期等待的线程，被其他线程调用notify()、notifyAll()唤醒后会先进入阻塞状态，再获取锁继续执行。（网上很多文章都是唤醒后从Waiting直接切到Runnable的）。
- 运行-限期等待：线程调用sleep()、带参数的wait()、join()、partNanos()、parkUntil()等方法时，从运行状态切换到限期等待。
- **限期等待-阻塞**：处于限期等待的线程，被其他线程调用notify()、notifyAll()唤醒、或者计时结束系统自动唤醒后会先进入阻塞状态，再获取锁继续执行。（网上很多文章都是唤醒后从Timed-Waiting直接切到Runnable的）。
- 运行-结束：处于运行状态的线程，在run()方法或main()方法运行结束后，进入结束状态。

Thread类源码中关于Blocked状态的解释：

```java
/**
 * Thread state for a thread blocked waiting for a monitor lock.
 * A thread in the blocked state is waiting for a monitor lock
 * to enter a synchronized block/method or
 * reenter a synchronized block/method after calling
 * {@link Object#wait() Object.wait}.
 */
BLOCKED,
```