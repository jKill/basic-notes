## Netty
### Netty 是什么
Netty 是一个能帮助使用者简单快速地开发网络应用的C-S架构框架。

### 主要逻辑
NioEventLoop 方法中，Reactor 线程主要做以下事情

- 首先轮询所有注册到 Reactor 线程对应的 selector 上所有 channel 的 IO 事件。（轮询IO事件）
- 处理产生网络 IO 事件的 channel。（处理轮询到的事件）
- 处理任务队列。（执行任务队列的任务）

### 解决 JDK 的 nio bug
这个 bug 会导致 Selector 一直空轮询，最终 CPU 100%。

- 记录 select 耗时，已判断是否触发了空轮询。
- 空轮询超过阈值512，开始重建 Selector
- 新建一个 Selector，把老 Selector 的 Channel 转移到新的 Selector 上。

- Boss EventLoop：基本就是处理连接事件，然后通过 pipeline 将连接扔给一个 Worker EventLoop。
- Worker EventLoop：基本就是处理 IO 读写事件，然后通过 pipeline 将读写到的字节流传递给每个 ChannelHandler 处理。

### Netty 的优点
#### 性能

- 更高的吞吐量，低延时
- 更少的资源消耗
- 最小化不必要的内存拷贝（零拷贝）

#### 使用便捷

- 对不同的传输类型——阻塞/非阻塞的socket，有着统一的API。
- 基于灵活和高可拓展的事件模型。
- 高度可定制化的线程模型：单线程、线程池（单线程池和多线程池）。

#### 安全

- 安全支持 SSL/TLS 和 StartTLS

### Netty 为什么快

- 非阻塞IO：1、非阻塞网络调用使得线程不必等待一个操作完成；2、选择器使得少量线程就能监视很多连接上的事件。
- 异步、事件驱动：Netty 里所有的操作都是异步的。
- 零拷贝：最小化内存复制。
- 池化：Bytebuf 池化技术，提高了字节缓冲区的复用性，降低资源消耗。

#### Netty 的 ByteBuf 相对于 Java 的 ByteBuffer 优点

- 池化：堆外内存分配和释放开销较高，ByteBuf 为池化实现，提高了复用性。（引用计数法对池化实现来说很重要，降低了内存分配的开销）
- ByteBuf 切换读写模式不需要调用 flip()。
- ByteBuf 读和写都有独立的索引。

#### FastThreadLocal 为什么快
```java
/**
 * ...
 * Internally, a {@link FastThreadLocal} uses a constant index in an array, instead of using hash code and hash table,
 * to look for a variable.  Although seemingly very subtle, it yields slight performance advantage over using a hash
 * table, and it is useful when accessed frequently.
 * ...
 */
```
FastThreadLocal使用了数组里的index索引来定位元素，比ThreadLocalMap通过哈希定位快一点。在请求频繁时，这个优化的效果就很明显了。

### Netty 适用场景

- 高吞吐量、低时延的中间件，如MQ等。
- 各大高性能通信框架。
- 对：作为通信框架负责内部模块的数据传输、通信等。