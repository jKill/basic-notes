## Spanner 和 BigTable
||BigTable|Spanner|
|---|---|---|
|类型|NoSQL|可拓展的关系型数据库|
|一致性|通过异步复制提供最终一致性|同步复制提供强一致性。另外，Sappner支持根据用户表达的位置放置数据|
|SQL查询||提供原生的 SQL 查询和读取 API|

### BigTable的优劣
在写负载很高的场景，BigTable的弱一致性语义让它通过批处理提供较高的吞吐量。但是，如果有一些读操作被混进去和批处理机会的减少，写效率会降低。

### 选择 Spanner 的原因
之前我们需要在NoSQL（如BigTable）的垂直拓展性和传统数据库（如 MySQL 或 Postgres）的 ACID 保证之间做出抉择。Spanner 正在尝试同时提供两者。