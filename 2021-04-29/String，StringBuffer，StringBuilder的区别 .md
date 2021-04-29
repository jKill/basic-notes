## String，StringBuffer，StringBuilder 之间有什么区别？
String是不可变对象，每次创建新的字符串变量都要在堆上开辟新的内存空间（如果字符串常量池没有这个字符串还要在常量池也开辟一份空间）。

String在使用”+“进行字符串拼接的时候，线程封闭的情况下，可能会被JVM解释成StringBuilder.append()，这时候String拼接的效率和StringBuilder差不多，

如果是多线程环境下，则可能会被解释成StringBuilder.append()，这时候效率和StringBuffer差不多。

另一个优化是对于常量直接拼接的情况，如：String s = "Php" + " is" + " the" + " best" + " language"，通常会被优化为String s = "Php is the best language"，这种情况String的效率甚至优于StringBuilder。

|String|StringBuilder|StringBuffer|
|:--|:--|:--|
|不可变字符序列|可变字符序列|可变字符序列|
|不可变|可变|可变|
|线程安全|线程安全|线程不安全|


