# Guava 不为人知的 5 个特性

## 无符号基础类型

Java 8 中一个不太为人所知的特性就是它为 <font color="#0088dd">**无符号基础类型**</font> 所提供的新的解决方案。但更加不为人所知的是在 Java 8 发布很久之前 Guava 库就已经有了这个功能了，目前在 Java 6 及以后的版本中均能使用。我们来看下 Guava 是如何解决这个问题的。现在在我们面前有两种选择，到底使用哪种最好保持一致：

直接将基础类型当 int 来使用，但要记清楚它可是无符号的：
	
```java
int notReallyInt = UnsignedInts.parseUnsignedInt(4294967295); // Max unsigned int
  
String maxUnsigned = UnsignedInts.toString(notReallyInt); // We’re legit!
```

UnsignedInts 与 UnsignedLongs 还支持 compare, divide, min, max 等方法。

你还可以使用包装类型，这样能避免直接使用基础类型容易带来的混淆：

UnsignedInteger newType = UnsignedInteger.valueOf(maxUnsigned);
  
newType = newType.plus(UnsignedInteger.valueOf("1")); // Increment

UnsignedInts 与 UnsignedLongs 还支持 minus, times, dividedBy 以及 mod 方法。

## 哈希：128 位的 MurmurHash

看一下 Java 标准库中的非加密哈希算法你会发现少了 MurmurHash，这是一个简单高效且还是分布式的算法，在许多语言中都有着很好的支持。我们并不是说要用它来取代 Java 的 hashCode 方法，不过如果你想要生成大量的哈希值而 32 位已经不够用了，但又希望能有一个高效而不会影响到性能的算法，那肯定就是它了。下面是 Guava 中的实现：

```java
HashFunction hf = Hashing.murmur3_128(); // 32 bit version available as well
HashCode hc = hf.newHasher()
        .putLong(id)
        .putString(name, Charsets.UTF_8)
        .putObject(person, personFunnel)
        .hash();
```

你可以使用 Funnel 来对对象进行分解，里面包含了用于读取对象的指令，假设我们有一个带 ID，名字以及出生年份的 Person 对象：

```java
Funnel<Person> personFunnel = new Funnel<Person>() {
    @Override
    public void funnel(Person person, PrimitiveSink into) {
        into.putInt(person.id)
                .putString(person.firstName, Charsets.UTF_8)
                .putString(person.lastName, Charsets.UTF_8)
                .putInt(birthYear);
   }
};
```

## InternetDomainName：用它来取代你的域名校验器

Guava 还有一个很酷的功能就是它的 InternetDomainName，用它来解析及修改域名简直是得心应手。如果你自己写过类似的功能的话，你就会知道它提供的方式是多高效优雅了。它是 Mozilla 基金会发起的项目，遵循最新的 RFC 规范，它采用的是公共后缀列表（Public Suffix List， PSL）中的域名列表。与 apache-common 库中的竞争者相比，它还提供了许多专门的方法。我们来看一个简单的例子：

```java
InternetDomainName owner = InternetDomainName.from("blog.takipi.com").topPrivateDomain(); // returns takipi.com
  
InternetDomainName.isValid(“takipi.monsters"); // returns false
```

关于域名有几个概念是比较容易混淆的：publicSuffix()返回的是对应着公共后缀列表中的独立实体的顶级域名。因此返回的可能会有co.uk, .com, .cool这样的结果（没错，.cool是一个真实的后缀，比如javais.cool, scalais.cool以及cppis.cool）。而topPrivateDomain()，这是对应公共后缀列表的一个独立实体的私有域名。在blog.takipi.com上调用这个方法会返回takipi.com，但如果你把它用于某个github主页，比如username.github.io的话则会返回username.github.io，因为这在PSL上是一个单独的实体。

当你需要校验域名的时候这个功能就派上用场了，比如我们最近给将JIRA集成进Takipi的时候，首先我们要检查你的JIRA域名，然后才能连接到Takipi的生产环境的错误分析工具中。

## ClassPath 反射：魔镜，魔镜

看一下 Java 的反射机制，也就是它的查看自身代码的能力，你会发现，要想列出所在包或者项目中的所有类可不是一件简单的事情。这是 Guava 中我们非常喜欢的一个特性，它还能获取当前运行环境的许多相关信息。使用起来非常简单：


```java
ClassPath classpath = ClassPath.from(classloader);
for (ClassPath.ClassInfo classInfo : classpath.getTopLevelClasses("com.mycomp.mypackage")) {
   System.out.println(classInfo.getName());
}
```

这段代码会遍历你指定包中的所有类并打印出它们的名字。这里要说明的是它只会扫描我们指定的包的物理路径下的类。如果类是从其它地方加载进来的则不在此列，因此使用它的时候请务必小心，不然你得到的结果就是错误的了。

## CharMatcher：简化版正则？


我们用一个你肯定会碰到过的问题来结束这最后一个特性。假设你有一个字符串，或者许多字符串，你希望对它们进行格式化，比如删除空格或者别的字符，替换某个字符等等。总的来说，就是提取匹配某个模式的字符然后进行某个操作。Guava 提供了 CharMatcher，使得这类问题的处理更得更加优雅。

对于这类任务，库里有许多预定义好的模式，比如 `JAVAUPPERCASE`（大写字符），`JAVA_DIGIT`（数字），`INVISIBLE`（不可见 UNICODE 字符）等。除了这些预定义的模式外，你还可以创建自己想要的模式。我们用一段简短的示例来看下它是如何使用的：

```java
String spaced = CharMatcher.WHITESPACE.trimAndCollapseFrom(string, ' ');
```

它会截取掉字符串末尾的空格并将中间连续的空格合并成一个。

```java
String keepAlex = CharMatcher.anyOf(“alex”).retainFrom(someOtherString);
```

而这行会将一个字符串中我的名字里没有的字符都去掉。如果我是一名说唱歌手的话，这将是我的歌曲扬名之时。
