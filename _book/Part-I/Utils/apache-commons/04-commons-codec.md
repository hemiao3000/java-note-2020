<span class="title">Commons Codec</span>

# 基本概念

最常用工具类是 ***`DigestUtils`*** 类：*`org.apache.commons.codec.digest.DigestUtils`* 类

| 方法 | 说明 |
| -: | :- |
| `md5Hex()`  | MD5 加密，返回 32 位 |
| `sha1Hex()` | SHA-1 加密 |
| `sha256Hex()` | SHA-256 加密 |
| `sha512Hex()` | SHA-512 加密 |
| `md5()` | MD5 加密，返回 16 位 |

# 加密算法

**`MD5`** 是哈希散列算法（也称摘要算法），对于 MD5 而言，有两个特性是很重要的，

1. 明文数据经过散列以后的值是定长的；
2. 是任意一段明文数据，经过散列以后，其结果必须永远是不变的。

MD5 曾一度被认为是非常安全的。但是 MD5 也不会完全不重复，从概率来说 16 的 32 次方遍历后至少出现两个相同的 MD5 值。

表面上看这个概率异常的小，但是山东大学王小云教授发现了 MD5 算法的缺陷，可以很快的找到 MD5 的“磕碰”，能让两个文件可以产生相同的“指纹”，让“碰撞”这种小概率事件变成必然事件。

<font color="#0088dd">**以 Google 公司为例，Google 公司明确指出不建议再使用 MD5 算法，而使用 SHA256 算法替代。**</font>

**`SHA-256`** 算法单向 Hash 函数是密码学和信息安全领域中的一个非常重要的基本算法，它是把任意长的消息转化为较短的、固定长度的消息摘要的算法。

SHA-256 算法是 SHA 算法族中的一员，由美国国家安全局<small>（NSA）</small>所设计，并由美国国家标准与技术研究院<small>（NIST）</small>发布；是美国的政府标准。

它的前辈还有 **`SHA-1`**。随着密码学<small>（破解）</small>的发展，<font color="#0088dd">**美国政府计划从 2010 年起不再使用 `SHA-1`**</font>，全面推广使用 SHA-256 和 SHA-512 等加密算法。

对于任意长度的消息，SHA256 都会产生一个 256bit 长的哈希值，称作消息摘要。

这个摘要相当于是个长度为 32 个字节的数组，通常用一个长度为 64 的十六进制字符串来表示。


# `sha512Hex()` 方法

以 SHA512 加密算法对数据源进行加密，返回加密后的十六进制形式字符串

```java
String sha512Hex(byte[] data)
String sha512Hex(InputStream data)
String sha512Hex(String data) {
```

# `sha256Hex()` 方法

以 SHA256 加密算法对数据源进行加密，返回加密后的十六进制形式字符串

```java
String sha256Hex(byte[] data)
String sha256Hex(InputStream data)
String sha256Hex(tring data)
```

# `sha1Hex()` 方法

以 SHA1 加密算法对数据源进行加密，返回加密后的十六进制形式字符串

```java
String sha1Hex(byte[] data)
String sha1Hex(InputStream data)
String sha1Hex(String data)
```

# `shaHex()` 方法

以 SHA1 加密算法对数据源进行加密，返回加密后的十六进制形式字符串

从 1.11 开始被标记为废弃，建议使用 sha1Hex() 方法替代。

```java
String shaHex(byte[] data)
String shaHex(InputStream data)
String shaHex(String data)
```

#  `md5Hex()` 方法

以 `MD5` 加密算法对数据源进行加密，返回加密后的十六进制形式字符串

```java
String md5Hex(byte[] data)
String md5Hex(InputStream data)
String md5Hex(String data)
```

# 其它

```java
//  同上，只不过返回的不是十六进制字符串，而是加密后的二进制的字节数据
md5()
sha()
sha1()
sha256()
sha512()
```

