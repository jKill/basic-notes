## Linux 下如何查看端口被哪个进程占用？
Linux查看端口被占用情况可以通过```sudo netstat```或者```sudo lsof```命令查看。
#### 使用netstat
通常有```sudo netstat -ano```或```sudo netstat -ltnp```等用法，具体参数含义如下：

- a 显示所有连接和监听中的端口（Displays all connections and listening ports）
- o 显示连接对应的进程id（Displays owning process Id associated with each connection）
- n 以数字形式显示地址和端口号（Displays addresses and port numbers in numerical forms）
- l 仅显示监听中的socket（display only listening sockets）
- t 显示tcp连接（display tcp connection）
- u 显示udp连接（display udp connection）
- p显示进程id/进程名称（display process ID/ Program name）

使用示例：

![](https://vitux.com/wp-content/uploads/2018/10/word-image-81.png)

如果要找特定的端口，可以在加上```grep```管道符进行过滤，如下
```sudo netstat -ltnp | grep 8080```

#### 使用lsof
用法为```sudo lsof -i : <PORT>```

![](https://vitux.com/wp-content/uploads/2018/10/word-image-84.png)

#### 带sudo的原因
Linux的权限控制只允许拥有root权限，或者进程的owner本身能访问进程的相关信息，加上sudo就是为了以root权限去获得这些信息，否则输出信息是没有进程id的。