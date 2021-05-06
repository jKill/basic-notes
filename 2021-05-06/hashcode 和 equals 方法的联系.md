## hashcode 和 equals 方法的联系
javadoc中和《Effective Java》都指出：
> 如果两个对象使用equals()方法比较的结果相等，他们的hashCode()方法返回值也必须相等。

通过下面的例子，可以看到如果不遵守这个规则，会出现什么情况。
#### 示例

```java

public class Apple {
    private String color;
 
    public Apple(String color) {
        this.color = color;
    }
 
    public boolean equals(Object obj) {
        if(obj==null) return false;
        if (!(obj instanceof Apple))
            return false;   
        if (obj == this)
            return true;
        return this.color.equals(((Apple) obj).color);
    }
 
    public static void main(String[] args) {
        Apple a1 = new Apple("green");
        Apple a2 = new Apple("red");
 
        //hashMap stores apple type and its quantity
        HashMap<Apple, Integer> map = new HashMap<Apple, Integer>();
        map.put(a1, 10);
        map.put(a2, 20);
        System.out.println(map.get(new Apple("green")));
    }
}

```

以上代码中，我们创建了一个”青苹果“和一个”红苹果“的Apple对象，放在HashMap中，但是当我们去map中获取”青苹果“时，结果却为null，但是debug时可以看到map中”青苹果“a2是存在的。

#### 原因
这是因为Java中所有类都继承自Object类，而Object默认的equals()方法实现就是比较两个对象的内存地址。hashCode()方法是Native方法，返回值是根据内存地址计算而来的。所以，如果如果重写了equals()方法，而不重写hashCode()方法，在一些依赖hashcode的类、方法会出错。要使上面的例子正确运行，只需要重写hashCode()方法即可，如下：

```java

public int hashCode(){
    return this.color.hashCode();   
}
```