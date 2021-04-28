## 简述 Spring bean 的生命周期
Spring Bean的生命周期有以下四个阶段

- 实例化 Instaniation
- 属性赋值 Populatte
- 初始化 Initialization
- 销毁 Destruction

主要逻辑都在doCreate()里，顺序调用以下三个方法。

1. createBeanInstance()——实例化
2. populateBean()——属性赋值
3. initializeBean()——初始化

销毁是在容器关闭时触发的，调用了ConfigurableApplicationContext.close()。