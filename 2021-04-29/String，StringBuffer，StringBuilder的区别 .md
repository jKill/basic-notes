## String，StringBuffer，StringBuilder 之间有什么区别？
String是不可变对象，每次创建新的字符串变量都要在堆上开辟新的内存空间（如果字符串常量池没有这个字符串还要在常量池也开辟一份空间）。

一个优化是对于常量直接拼接的情况，如：String s = "Php" + " is" + " the" + " best" + " language"，通常会被优化为String s = "Php is the best language"，这种情况String的效率甚至优于StringBuilder。

|String|StringBuilder|StringBuffer|
|:--|:--|:--|
|不可变字符序列|可变字符序列|可变字符序列|
|不可变|可变|可变|
|线程安全|线程安全|线程不安全|


