<span class="title">StreamUtils</span>

***`StreamUtils`*** 类位于 *`org.springframework.util`* 包下 。

<small>以下方法结尾 *`static`* 方法，故不再显示 *`static`* 关键字。</small>

# 输出

```java
void copy(byte[] in, OutputStream out)

int copy(InputStream in, OutputStream out)

void copy(String in, Charset charset, OutputStream out)

long copyRange(InputStream in, OutputStream out, long start, long end)
```


# 输入

```java
byte[] copyToByteArray(InputStream in)

String copyToString(InputStream in, Charset charset)

// 舍弃输入流中的内容
int drain(InputStream in) 
```


# 实例化

```java
InputStream emptyInput()

// 所生成的 输入流 close() 方法无效
InputStream nonClosing(InputStream in) 

// 所生成的 输出流 close() 方法无效
OutputStream nonClosing(OutputStream out) 
```
