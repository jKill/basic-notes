## MySQL 和 PostgreSQL 的区别

||MySQL|PostgreSQL|
|---| --- | --- |
|架构|对象-关系数据库；多进程|关系数据库；单进程|
|数据类型|数字，日期/时间，字符，JSON，数组，布尔，枚举，几何，网络地址|数字，日期/时间，字符，JSON，空间|
|索引|R树|B+树，哈希|
|连接|每个连接分配一个线程|每个连接分配一个进程|
|性能|写操作需要加写锁|并发写操作不需要读/写锁，并且完全符合ACID|
|场景|高性能读|处理海量数据，复杂查询和写操作性能更优|

### 结论
MySQL 更适合用在读比较多的业务。由于大多数应用只需读取和展示数据，所以实现简单的 MySQL 显得更优秀。而 PostgresSQL 在为每个新连接都分配一个进程，需要消耗大量内存（约10MB），在一个简单的应用里，PostgresSQL 性能显得很糟糕。