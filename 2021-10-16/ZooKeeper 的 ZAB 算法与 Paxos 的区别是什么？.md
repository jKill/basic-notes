## ZooKeeper 的 ZAB 算法与 Paxos 的区别是什么？
### ZAB 协议简介
ZooKeeper 的高可用性是通过在节点之间复制(replicating)数据实现的，而复制的关键又在于节点状态变更的有序性。ZAB 协议正是用来保证有序复制的，另外 Leader 选举和崩溃恢复也是由 ZAB 协议处理的。

- 客户端可以从任意 ZooKeeper 节点读数据
- 客户端向任意节点写的数据（状态变更）都会被转发到 Leader 节点。

### ZAB 协议流程
ZooKeeper 用一种“两阶段提交协议”的变体来复制事务到 follower：

- Leader 会通过序列号 c 和纪元 e 产生一个事务，并发送给所有 follower。
- follower 会添加一个事务到自己的 history 队列并响应 ACK 给 Leader。
- Leader 收到 quorum 数量的 ACK 之后，会发送 quorum 个```COMMIT```请求提交事务。
- follower 收到```COMMIT```请求后会提交事务，除非序列号 c 比它的 history 队列里的序列号都要大。

### 节点的生命周期
节点的生命周期主要有以下4个。节点每次执行一个迭代，并且在任何时候，进程都可以放弃当前迭代，从阶段0开始一个显得迭代。

- 阶段0——选举预备 leader：节点直接互相投票。
- 阶段1——发现：follower 通过和它们未来的 Leader 通信，以便 Leader 能收集到 follower 之中最新的事务（因为至少有过半数（quorum）的 follower 拥有前 Leader 的最新数据，所以也就保证了至少有一个 follower 拥有最新数据）。生成新的纪元 e 防止前 Leader 继续提交新的提议。
- 阶段2——同步：Leader 把自身的事务同步给所有 follower，具体流程同样是上述两阶段提交的变体。这一步完成之后，它正式成为 Leader，不再是“预备”Leader了。
- 阶段3——广播：只要没有崩溃，节点都会保持在这个阶段。客户端提交一个写请求，节点会对事务进行广播。
- 节点的健康检查：Leader 和 follower 之间互相发送心跳。Leader 在 timeout 时间内没收到 quorum 个 follower 的心跳，就会放弃 Leader 身份，回到阶段0，进行选举。follower 同样是 timeout 时间没收到 Leader 的心跳，就转移到选举阶段。

### ZAB 同步
崩溃恢复后的同步，主要有以下两种场景

- 差异化同步：前 Leader 的数据都已经同步到 follower 再宕机，直接差异化同步。
- 先回滚再差异化同步：前 Leader 宕机前有还没来得及同步的数据，需要先回滚再差异化同步。

### ZAB 与 Paxos 的区别

- 流程：基本相同，但是 ZAB 多了一个同步过程。
- 设计目标：Paxos 作为一个共识性算法，用于构建分布式一致性系统。而 ZAB 除了一致性以外，更关注高可用