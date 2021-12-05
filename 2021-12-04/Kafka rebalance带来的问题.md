## Kafka rebalance带来的问题
### eager rebalance 协议
#### 问题之一
eager rebalance 协议下，Kafka 在 rebalance 期间，必须 Stop the world，消费者组不能进行任何活动。
#### 问题之二
rebalance 时长会随着 partition 数量增长，因为每个消费者组成员都要释放(revoke)自己**全部**分区，然后在重新分配之后恢复消费。
#### 问题之三
rebalance 之后消费者重新拉取位移，产生重复消费。如上次提交 offset=30，这次拉取了31-35的消息。消费到34的时候发生 rebalance 了，在 rebalance 完成后重新从31开始消费。

#### 为什么会出现问题
为了在分布式架构中，不出现 partition 被同一个消费者组的多个 consumer 同时消费情况，consumer 在加入一次 rebalance 前需要释放自己正在消费的 partition。因此，这时 consumer 没法进行任何活动。

### incremental cooperative rebalance 协议
eager rebalance 协议对同步要求过于严格：整个 rebalance 期间，所有 consumer 都要停顿。实际上，真正需要同步的，只有涉及到变更 partition “所有权”（被哪个 consumer 消费）的部分。

- 第一次 rebalance，每个 consumer 收到自己分到的 partition 列表，并和自己当前已有的 partition 对比，做出以下调整。
- 自己已有，但 partition 列表里没有的，需要释放。
- 自己已有，partition 列表里也有的，不需要做变动。
- 自己没有，但 partition 列表里有的，需要添加并对其消费。
- 被释放的 partition 会触发第二次 rebalance，把它们分配给 consumer。

很少 rebalance 需要在 consumer 之间迁移大量分区，因此，大多数情况下，使用 incremental cooperative rebalance 协议的消费者组，其成员停顿时间很短，甚至不需要停顿。