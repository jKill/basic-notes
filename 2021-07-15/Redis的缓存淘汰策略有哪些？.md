## Redis的缓存淘汰策略有哪些？
Redis对key有主动淘汰和被动淘汰两种方式。
### 被动淘汰
客户端尝试访问过期的key时，会触发被动删除
#### 优点

- 惰性删除，性能较好
- 防止大批量集中删除（导致客户端请求的可能性增加）。

#### 缺点

- 惰性删除可能导致内存占比较高。
- 对于再也不被访问的key，没法删除。

### 主动淘汰
为了解决被动淘汰的缺点，Redis增加了主动淘汰的策略来处理被动淘汰的清理死角。Redis每秒如做10次以下步骤：

- 从过期列表随机选取20个key。
- 删除其中过期的key。
- 如果过期的占比超过25%，重复步骤1。

#### 优点：

- 能解决被动淘汰的问题。
- 过期的key基本能保持在25%以下。

#### 缺点

- 随机性选取策略可能会连续几次遗漏一部分过期的key，并且这些key也没被访问（触发被动淘汰），导致这些key一直存在。