## Redis 大key问题
### 发现大key

- 使用Redis的大key分析功能：会遍历Redis实例的所有key，不能在业务高峰期用。
- 根据业务特点分析：简单，但是可能会有遗漏。

### 大key带来的问题

- 数据倾斜：数据量大的key，在经过分片后，存储这个大key的实例内存使用率会比其他实例大。
- 阻塞Redis实例：Redis读写是单线程操作，对大key的读写会导致阻塞实例的时间变长。
- 阻塞网络：网络传输大key需要花费的时间和占用的网络带宽也更多。

### 如何解决大key

- 数据拆分：对大key（如字典类型数据）拆分，使用```mget```，```mset```的方式读写。
- 活用hash数据结构：特别大的json不要用string存储，可以根据业务特点或者根据field进行hash取模，生成一个新的key。（能减少网络传输量，但依然存在数据倾斜）如下：

```
hash_key:mod1:{filed1:value}
hash_key:mod2:{filed2:value}
hash_key:mod3:{filed3:value}
```

### 删除大key
del命令直接删除会有性能问题，推荐用scan命令渐进式删除。

### 拓展：大key和大value的区别
key和value都是存储在DictEntry中的。所以基本上来说，大key和大value带来的内存不均和网络IO压力都是一致的，只是key相较于value还多一个做hashcode和比较的过程（链表中进行遍历比较key），会有更多的内存相关开销。