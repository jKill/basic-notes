## Redis 如何实现高可用？
Redis Sentinel为Redis提供了高可用特性
### 发现故障/故障转移的时机

- 每个Sentinel使用一个+sdown(主观下线)事件监测master是否在线。
- 检测到master挂了的Sentinel数量越来越多(**达到quorum**)，事件会扩大为+odown(客观下线)事件。
- Sentinel之间投票选出一个Sentinel(票数**达到quorum并且超过Sentinel数量的一半**)。
- 被选出的Sentinel执行故障转移。

### 故障转移流程

- master内部会启动一个CLIENT PAUSE WRITE，暂停接收新的写请求，防止新的数据堆积在副本流(replication stream，用来把master的数据同步到从节点)里。
- master监控所有从节点，等待第一个完全消费掉replication stream数据的从节点出现。
- master把自己降级为从节点。
- 前master会发送一个特殊的PSYNC请求到目标从节点，```PSYNC FAILOVER```表示目标从节点会成为master。
- 前master收到```PSYNC FAILOVER```的确认后，会重新开始接收客户端的写请求。如果PSYNC请求被拒绝了，master会放弃failover并回滚。

### 如何选择晋升的从节点
Sentinel根据配置参数优先级```replica-priority```、消费位移replication offset和run ID等，决定在failover后选择哪个从节点晋升为master。

- 比较```replica-priority```的值，越小越有可能晋升master（特殊地，为0时不可能晋升。）
- 如果```replica-priority```相同，则比较replication offset，选择接收到更多来自master数据的节点。
- 如果也相同，则选择run ID较小的从节点。

### 自动故障转移(Auto failover)
一个Redis集群有两个watchdog进程检测错误：

- 节点watchdog(Node watchdog)：监控一个节点的所有进程，如：分片不响应的时候，触发分片的故障转移(failover)。
- 集群watchdog(Cluster watchdog)：监控使用gossip协议管理集群内的节点。如：节点发生错误或者网络脑裂的时候，触发节点故障转移(failover)。

```
port 5000
sentinel monitor mymaster 127.0.0.1 6379 2
sentinel down-after-milliseconds mymaster 5000
sentinel failover-timeout mymaster 60000
sentinel parallel-syncs mymaster 1
```

- down-after-milliseconds：如果master在这个时间没能响应sentinel的ping，会被认为挂了。