## Java 中 int 的最大值是多少？

### 最大最小值
Java中int使用32位存储，最大值为0x7fffffff，即十进制的2147483647。最小值为0x80000000，即十进制的-2147483648。java.lang.Integer的源码也注明了这一点。

```java
/**
 * A constant holding the minimum value an {@code int} can
 * have, -2<sup>31</sup>.
 */
@Native public static final int   MIN_VALUE = 0x80000000;

/**
 * A constant holding the maximum value an {@code int} can
 * have, 2<sup>31</sup>-1.
 */
@Native public static final int   MAX_VALUE = 0x7fffffff;
```

我们可以自己跑一下代码，看下int的极限值是多少：

```java
int max = Integer.MAX_VALUE;
System.out.println("max value: " + max);
System.out.println("max value+1: " + (max + 1));
int min = Integer.MIN_VALUE;
System.out.println("min value: " + min);
System.out.println("min value-1: " + (min - 1));
```

输出结果为：

```java
max value: 2147483647
max value+1: -2147483648
min value: -2147483648
min value-1: 2147483647
```

- 可以看到最大值就是2147483647，在其基础上再+1的话，会导致最大值溢出，变成-2147483648。
- 而最小值为-2147483648，在其基础上再-1的话，最小值也会溢出，变成2147483647。

### 拓展
虽然Java中int的最大值可以达到Integer.MAX_VALUE=2147483647，但是实际上使用ArrayList时，能分配的最大长度只有Integer.MAX_VALUE - 8。原因同样可以通过javadoc看到：

```java
/**
 * The maximum size of array to allocate.
 * Some VMs reserve some header words in an array.
 * Attempts to allocate larger arrays may result in
 * OutOfMemoryError: Requested array size exceeds VM limit
 */
private static final int MAX_ARRAY_SIZE = Integer.MAX_VALUE - 8;
```

有些虚拟机实现上，会将保留数组的一些头信息，因此ArrayList预留了8个字节来保存这些头信息。所以如果我们为ArrayList申请了超过Integer.MAX_VALUE - 8的大小，会发生：

 > OutOfMemoryError: Requested array size exceeds VM limit