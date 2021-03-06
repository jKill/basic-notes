## 简述 TCP 三次握手以及四次挥手的流程。为什么需要三次握手以及四次挥手？
### 三次握手流程

- 首先，客户端通过调用connect发起主动打开（active open），向服务端发出SYN分节
- 服务端接收到这个SYN分节后，将SYN的序列号+1作为ACK回复这个分节，同时自己也得发送一个SYN分节。
- 客户端接收到服务端发送的SYN分节后，也需要确认，将这个SYN序列号+1作为ACK。

### 为什么需要三次握手
看到三次握手，我们很容易想到，为什么需要三次握手，两次握手不行吗？

第一次握手，是为了告知服务端，有客户端需要建立连接；

第二次握手，是为了让客户端知道，服务端状态正常并且能收到他的消息，同时询问客户端能否接收到服务端的消息。

第三次握手，是为了让服务端知道，客户端能接收到他的消息。

至此，双方都能接收到对方的消息，并且确认自己的消息能被对方接收，**双方已经在建立连接这件事上达成一致**。现在再来看为什么不能用两次握手。TCP作为双向通信的可靠传输协议，必须保证通信两端都能发送消息，并且接收到对端的消息。如果仅仅两次握手，服务端没收到客户端的ACK，无法确认客户端能否接收自己的消息。下面以两个人远程对话举个例子：

```
Bob <--- Alice         SYN
Bob ---> Alice     SYN ACK 
Bob <--- Alice     ACK 
```

- Alice向Bob发起聊天，她首先发送了一句SYN。
- Bob收到了，并且回复了一个ACK来告诉Alice她收到了她的SYN；同时加了一句SYN询问Alice能否收到他的消息。

如果是两次握手，那么流程到此结束，Bob不会收到Alice对他发的SYN的回复，也无法确认Alice能否接收到他的消息。第三次握手，就是为了让Bob（服务端）接收来自Alice客户端的ACK，确认客户端能否接收到他的消息。

### 四次挥手流程
四次挥手发生在某应用进程调用close，发起主动关闭（active close）时。客户端和服务端，都可以发起主动关闭。通常情况下，由客户端发起。但某些情况（如HTTP/1.0）下是服务器发起主动关闭。下面以客户端发起说明流程：

- 第一次挥手，客户端向服务端发送FIN分节。
- 第二次挥手，服务端接收到FIN后，将这个FIN序列号+1作为ACK，回复客户端。
- 第三次挥手，第二次挥手中的FIN接收作为一个文件结束符（end-of-file），传递给接收端应用程序。因为这个FIN的接收意味着接收端的应用程序在这个连接上再没有额外数据可以接收了。**一段时间后**，接收到这个文件结束符的应用进程调用close关闭它的Socket，导致它的TCP也发送一个FIN。
- 第四次挥手，发起主动关闭的一方，接收到最后的这个FIN后，对这个FIN做出应答（同样是FIN序列号+1作为ACK）。

### 为什么需要四次挥手
#### 为什么握手只需要三次，而挥手需要四次
与三次握手一样，进行四次挥手的两端也需要告知对方自己的动作（SYN/FIN）以及接受相应的确认（ACK）。因此同样是两组FIN-ACK的对话。不同的是，三次握手把服务端发送的SYN与第一个ACK合并了。而四次挥手里这两个动作是分开的，因此比三次握手多了一次通信。
#### 第二、三次挥手分开进行的原因
半关闭状态（half-close）。在服务端接收到客户端发来的FIN后，虽然可以马上响应一个ACK（第二次挥手）。但是在这个连接上，服务端（执行被动关闭的一方）可能还有事情没处理完，例如还有数据要发送到客户端。此时服务端处于瓣关闭状态。在服务端处理完这些事情后，认为自己已经准备好关闭连接了，才会发出第二个FIN给客户端（第三次挥手）。

### TCP协议的可靠性
TCP向另一端发送数据时，要求对端返回一个确认。如果没收到确认，TCP就自动重传数据并等待更长时间。