## 什么是 Spring 容器，有什么作用？
### 什么是容器
Spring 中容器可以理解为生成对象的地方。负责管理 Bean 的创建，组装，销毁等一系列生命周期。对象的管理控制权交给了 Spring 容器，是一种控制权的反转，称为IOC容器。大致分为以下两类：

- BeanFactory：最简单的容器，只能提供基本的 DI 功能。
- ApplicationContext：继承了 BeanFactory，能提供更多企业级服务，如解析配置文本等。

### 为什么 Spring 灵活
Spring 有很大的灵活性，提供了三种主要的**装配机制**

- 在 XML 中进行显示配置
- 在 Java 中进行显示配置
- 隐式的 Bean 发现机制和自动装配。

#### 隐式的 Bean 发现机制和自动装配

- 组件扫描(component scanning)：Spring 会自动发现应用上下文创建的 Bean。（如`@Component` 表明当前类为组件类，告诉 Spring 要为这个类创建 Bean）
- 自动装配(autowiring)：Spring 会自动满足 Bean 之间的依赖。

### BeanFactory 和 ApplicationContext 装载 Bean 的区别

- BeanFactory：在启动的时候不会实例化，在从容器中获取 Bean 时才实例化。
- ApplicationContext：启动时就实例化所有 Bean，也可以配置 lazy-init=true 来延迟实例化。

#### 延迟实例化的优点：

- 启动时占用资源少。加快启动速度。

#### 不延迟的优点：

- 启动时实例化所有 Bean， 运行速度更快。
- 启动时就实例化所有 Bean，尽早发现配置问题。