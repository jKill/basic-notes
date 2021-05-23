## HTTP 中 GET 和 POST 区别
GET请求通常用来从服务端获取数据，POST请求通常用来对服务端数据做新增、更新等操作。
### 常见误区
不少框架规定了GET请求只能把请求参数放在URL的query string里，POST请求只能把数据放在body里。但实际上，协议并没有限制GET不能有body，POST不能把请求参数放在URL的query string等。如Elastic Search的_search api就用了带body的GET。也可以自己开发接口把POST的参数放在URL的query string里。
### 原因
但是太自由了会带来种种问题：开发人员需要每次约定参数放在url的path里，query string里，body里，header里，太低效了。于是有了像REST这样的接口规范/风格。REST充分利用GET、POST、PUT、DELETE，约定了这4个接口分别用来获取、创建、替换和删除“资源”。这样仅仅通过看HTTP的method就可以知道这个接口的用途。解析格式也统一起来，减少了开发中的沟通量。

|GET|HTTP|
|:--|:--|
|GET请求通常用来获取资源（规范但不强制）|POST通常用来创建资源|
|GET请求通常是幂等的（规范但不强制）|POST请求不幂等|
|GET请求可以被缓存（幂等的原因）|POST请求无法被缓存|
|GET数据放在URL，长度会被URL限制。（HTTP协议没限制URL长度，但是客户端/浏览器/服务器会限制，无限长的URL会使字符串解析非常麻烦）|数据在body，没有长度限制|
|不安全，携带私密信息的URL会展示到地址栏|保存在body相对安全。但网关、代理等access log也会记录body。HTTPS才是更好地安全方案|

