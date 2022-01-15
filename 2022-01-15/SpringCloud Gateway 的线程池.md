## SpringCloud Gateway 的线程池
SpringCloud Zuul 的 IO 模型和 Spring MVC 一样，基于 servlet，使用 tomcat 线程池处理请求，每个请求绑定一个线程，通过 dispatcher servlet 分发到每个线程上。有以下缺点：

- 请求量大的时候需要创建大量的线程。
- 过多线程带来的上下文切换。

SpringCloud Gateway 应运而生，改为基于 Netty 的 Reactor 模型 ```spring-boot-starter-reactor-netty```。

- 使用少量的 loop 线程处理 request 和 response。
- 阻塞的操作交给 work 线程，loop 线程只处理非阻塞操作。
- Webflux 底层可以兼容多个框架，但一般还是 Netty，Webflux 的loop 线程间就是 Reactor 模型的 Reactor 线程，也就是 Netty 的 EventLoop 线程。

