<span class="title">FileCopyUtils</span>

***`FileCopyUtils`*** 类来自 *`org.springframework.util`* 包。它用于文件的拷贝处理工具。

<small>以下方法结尾 *`static`* 方法，故不再显示 *`static`* 关键字。</small>

# 输出

```java
// 从字节数组到文件
void copy(byte[] in, File out)

// 从文件到文件
int	copy(File in, File out)

// 从字节数组到输出流
void copy(byte[] in, OutputStream out) 

// 从输入流到输出流
int	copy(InputStream in, OutputStream out) 

// 从输入流到输出流
int	copy(Reader in, Writer out)

// 从字符串到输出流
void copy(String in, Writer out)
```

# 输入

```java
// 从文件中读入到字节数组中
byte[] copyToByteArray(File in)

// 从输入流中读入到字节数组中
byte[] copyToByteArray(InputStream in)

// 从输入流中读入到字符串中
String copyToString(Reader in)
```
