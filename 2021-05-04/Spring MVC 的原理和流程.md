## Spring MVC 的原理和流程

### 执行流程

- 用户请求发送到前端控制器DispatcherServlet；
- DispatcherServlet收到请求调用处理器映射HandlerMapping；
- 处理器映射根据请求URL找到具体处理器，生成处理器执行链HandlerExecutionChain（处理器对象和拦截器），返回给DispatcherServlet；
- DispatcherServlet根据处理器Handler获取处理器适配器HandlerAdapter执行参数封装，数据格式转换，数据校验等。
- 执行处理器Handler（Controller，页面控制器）；
- Handler执行完成返回ModelAndView；
- HandlerAdapter将Handler执行结果ModelAndView返回到DispatcherServlet；
- DispatcherServlet将ModelAndView传给ViewResolver视图解析器；
- ViewResolver解析后返回具体View；
- DispatcherServlet对View进行渲染视图（将模型数据model填充至视图中）。
- DispatcherServlet响应用户。

![](https://upload-images.jianshu.io/upload_images/5220087-d2a2c47dc335e91b.png)


ref：https://www.jianshu.com/p/8a20c547e245