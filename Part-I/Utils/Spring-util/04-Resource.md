<span class="title">资源（Resource）及相关</span>

# URI 和 URL

在原生 JDK 中，提供了 **`java.net.URI`** 和 **`java.net.URL`** 类，来标识资源的定位。

## 概念

| # | 说明 |
| :-: | :- |
| **URI** | 统一资源标志符（Uniform Resource <font color="#0088dd">**Identifier**</font>）|
| **URL** | 统一资源定位符（uniform Resource <font color="#0088dd">**Location**</font>）|


- **相同点**：它们都能定位资源的位置，代表这个资源的位置信息。<small>就像经纬度一样可以表示你在世界的哪个角落。</small>

- **不同点**：***`URI`*** 更宽泛、更高级也更抽象，而 ***`URL`*** 则更具体<small>（*`URL`* 概念是 *`URI`* 概念的一个子集）</small>。所以，URL 有更多的方法<small>（ *`openConnection()`* ）</small>，可以 <font color="#0088dd">**获得资源对象的 IO 流**</font> ，进而再进行读写操作，***`URI`*** 则不能。

简单来说，URL 会带有协议信息：

    file:/Users/ben/opt/tomcat-8.5.37/webapps/...

而 URI 则没有：

    /Users/ben/opt/tomcat-8.5.37/webapps/...

简而言之，你有 URL，就意味着有 URI；反之则不然。<small>尽量获得/使用 URL 。</small>


# Resource 及其实现类

在日常项目开发过程中，从 classpath 下加载一个配置文件<small>（从中读取配置信息）</small>是一个常见需求。

Spring 提炼出 Resource（资源）的概念，并围绕它提供了一系列的工具类，能方便地实现上述功能<small>（当然，它们的能力不仅限于实现上述功能）</small>。

```
Resource
│ 
├── FileSystemResource
│ 
├── FileUrlResource
│ 
├── ClassPathResource
│ 
├── UrlResource
│ 
└── InputStreamResource
    │ 
    └── EncodedResource
```

InputStreamResource 是 Resource 接口的子接口，其实现类 ***`EncodedResource`*** 支持通过 InputStream 生成 Resource 对象，并且可以设置编码。（其他 Resource 都是使用 ISO-8859-1 编码）

Resource 接口定义了『资源』的概念，其中常用方法有：

| 实例方法 | 说明 |
| :- | :- |
| `boolean exists()` | 判断资源是否存在 |
| `File getFile()`  | 从资源中获得 File 对象 |
| `URI getURI()` | 从资源中获得 URI 对象 |
| `URL getURL()` | 从资源中获得 URI 对象 |
| `InputStream getInputStream()` | 获得资源的 InputStream |
| `String getDescription()` | 获得资源的描述信息 |

<small>Resource 接口的各个实现类中除了上述方法外，还有自己的一些方法。</small>


# Resource 的 `getFile()` 方法的一个问题

Resource 的 *`getFile()`* 方法需要 Resource 对象所代表的那个文件 <strong>在文件系统中是独立存在的！</strong>

因此，你在 Eclipse/Idea 源码运行中运行，或者是一个 *`.war`* 的项目在 tomcat 下运行，*`.getFile()`* 方法都是没有任何问题，能正常起作用：获得 File 对象。

但是，在 Spring Boot 中，*`.getFile()`* 方法无法获得 File 对象。因为，Spring Boot 的 *`.jar`* 包不像一般 Java Web 的 *`.war`* 那样会被 tomcat 解压开!

因此，Spring Boot 项目中的资源文件都是 <strong>内嵌</strong> 在 *`.jar`* 包中，而非独立存在的。不满足 *`.getFile()`* 的那个前提要求。

Spring Boot 的这种场景下，需要 <strong>使用 *`.getInputStream()`* 方法来替代 *`.getFile()`* 方法</strong> 。

简而言之，Resource 的 *`.getInputStream()`* 方法比 *`.getFile()`* 方法具有更好的普适性。


# ResourceUtils

对于常见的 `new Resource() 对象 + 操作 Resource 对象` 的常见使用方式，Spring 提供了一个 ***`ResourceUtils`*** 类来简化这样的代码。

***`ResourceUtils`*** 类位于 *`org.springframework.util`* 包。它用于处理表达资源字符串前缀描述资源的工具. 如: *`classpath:`*、*`file:`* 等。

不过，ResourceUtils 类也有上面所说的那个 *`.getFile()`* 的问题。因此，如果是 Spring Boot 项目，还是少用/不用 ResourceUtils 的 *`.getFile()`* 方法。

ResourceUtils 的常用方法：

```java
// 判断字符串是否是一个合法的 URL 字符串。
static boolean isUrl(String resourceLocation) 
static URL getURL(String resourceLocation) 
static File	getFile(String resourceLocation)

static URI toURI(URL url) 
static File	getFile(URI resourceUri)
static File	getFile(URL resourceUrl)
```

# PropertiesLoaderUtils

*`.properties`* 文件是最常见的资源文件。对此，Spring 框架提供了 ***`PropertiesLoaderUtils`*** 工具类来简化加载 *`.properties`* 配置类的工作。

<small>***`PropertiesLoaderUtils`*** 位于 *`org.springframework.core.io.support`* 包下。</small>

<small>以下方法都是 *`static`* 方法</small>

```java
// 查找资源，并生成 Properties 对象。使用 ISO-8859-1 编码。
Properties loadProperties(Resource resource) 

// 从 classpath 中查找资源，并生成 Properties 对象。使用 ISO-8859-1 编码。
Properties loadAllProperties(String resourceName) 

// 查找资源，并生成 Properties 对象。
Properties loadProperties(EncodedResource resource) 

// 查找资源，填充参数对象。
void fillProperties(Properties props, Resource resource)

// 查找资源，填充参数对象。
void fillProperties(Properties props, EncodedResource resource) 
```

