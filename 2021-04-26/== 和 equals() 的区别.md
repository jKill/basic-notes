## == 和 equals() 的区别？
### == 比较的是两个对象的内存地址。

```java
int a = 123;
        int b = 123;
        System.out.println(a == b);
```


结果是true，因为Java中的基本数据类型（byte,short,int,long,float,double,char,boolean）作为常量存储在常量池里。

```java
Integer a = 127;
        Integer b = 127;
        System.out.println(a == b);
        Integer c = 128;
        Integer d = 128;
        System.out.println(c == d);
```

上面的结果是true，fasle。对于作为包装类型的Integer来说，会在常量池里缓存[-128,127]范围的对象，而范围之外的128，会在堆内存中创建一个新的对象保存。所以128指向了堆中不同的对象地址，导致了结果为false。

### equals()
查看JDK里Object.equals()方法的源码

```java
public boolean equals(Object obj) {
	return (this == obj);
}
```

Object是Java所有对象的父类，equals()方法也是返回 == 比较的结果。区别是开发者可以根据自己的需要重写equals()方法，如果不重写，那么默认就是和 == 一样，比较两个对象的内存地址。