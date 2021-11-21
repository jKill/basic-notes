## 简述 Spring bean 的生命周期
Spring Bean的生命周期有以下四个阶段

- 实例化
- 属性赋值
- 后置处理
- 销毁

1. 实例化，对象实例化。
2. 属性赋值（依赖注入）。
3. 后置处理，InitializeBean 接口只有一个方法 afterPropertiesSet()，用来在Bean构造完成前增加我们自定义的逻辑。

销毁是在容器关闭时触发的，调用了ConfigurableApplicationContext.close()。