## Netty 线程模型
Netty 是基于 IO多路复用 来处理网络请求的。但是它的独特之处在于，把接收连接与处理请求交给不同的线程池去做，分别是 Boss 线程池和 Worker 线程池。

- Boss 线程池负责监听端口，接收客户端的连接，并将请求转发给 Worker。
- Worker 线程池负责处理请求（最佳实践是 Worker 只处理 IO，业务逻辑交给业务线程池处理）。

Worker 线程池的实现为```EventLoopGroup```（继承自 JDK 的```ScheduledExecutorService```），管理着多个```EventLoop```，Boss 线程转发的任务，最终会分配到```EventLoop```处理。
### 事件循环 
事件循环 ```EventLoop``` 同样继承了```ScheduledExecutorService```，因此它可以实现灵活定时调度的线程池。也是 Netty 线程模型高性能之处。因为：

- 针对同一个任务所产生的事件（出站/入站），都在同一个```EventLoop``` 里的同一个线程处理，省去了```ChannelHandler```之间同步的开销。同时保证了线程安全。

#### 如何保证在同一个线程里

- 独立线程：每个```EventLoop``` 都有一个永不改变的线程（IO 线程）。```EventLoop```内部所有的事件产生的任务都由它处理。
- 身份判定：判断当前执行的 ```Thread``` 是否是 IO 线程，是则立即执行任务，否则放入队列（```EventLoop```私有）方便**下次处理它的事件时**执行。

#### 事件循环主体逻辑

```java
while (!terminated) {
	List<Runnable> readEvents = blockUntilEventReady();
	for (Runnable ev : readEvents) {
		ev.run();
	}
}
```

如上代码所示，在事件就绪之前阻塞，事件一旦就绪，处理就绪的事件。

> 如何知道事件就绪？出入站事件 ```Inbound/Outbound ChannelHandler``` 向```EventLoop``` 注册自己。后续状态变化/收到数据的时候，会通知```EventLoop```。

### 避免阻塞事件循环

- 避免在 IO 线程执行阻塞调用，或长时间运行的任务。
- 同理，避免把需要长时间运行的任务放入```EventLoop```的任务队列里。

### 注意在 IO 线程使用 ThreadLocal 的风险

由于同一个```EventLoop```内的所有任务，都是同一个 IO 线程处理，因此在 IO 线程里使用```ThreadLocal```可能会导致业务在多个线程里读到的值一样。
