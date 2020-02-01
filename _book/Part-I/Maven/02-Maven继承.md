# Mavan 父子模块（继承）

<font color="#0088dd">**继承**</font>是 Maven 中很强大的一种功能，继承可以使得子 POM 可以获得 parent 中的各项配置，可以对子 pom 进行统一的配置和依赖管理。

简而言之，继承是为了抽取多个模块的公共配置，以实现统一/简化配置的目的。

父 POM 中的大多数元素都能被子 POM 继承。<font color="#0088dd">除了以下元素外</font>，其它元素均可被子模块继承：

- artifactId
- name
- prerequisites

## 父项目

通常 Maven 的父项目是一个特殊项目，本质上，它是因为了复用配置而存在的项目，而并非一个实际意义上的真实的项目。

<font color="red">注意</font> 父项目的 `package` 类型是 `pom` 类型，既非 `jar` 类型，又非 `war` 类型。

父项目并不是一个真正的项目，因此父项目下完全不需要存在 `java` 包和 `resources` 包，父项目下只需要存在一个 `pom.xml` 文件即可。


## 子项目

为一个子项目指定父项目只需要在其 pom.xml 中添加 `<parent>` 元素即可。

```xml
<project xmlns="http://maven.apache.org/POM/4.0.0"
    xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
    xsi:schemaLocation="http://maven.apache.org/POM/4.0.0
        https://maven.apache.org/xsd/maven-4.0.0.xsd">
    <modelVersion>4.0.0</modelVersion>
    <parent>
        <groupId>org.codehaus.mojo</groupId>
        <artifactId>my-parent</artifactId>
        <version>2.0</version>
        <relativePath></relativePath>
    </parent>
    <artifactId>my-project</artifactId>
</project>
```


`<relativePath>` 元素用于指明本项目<small>（子项目）</small>的父项目的 pom 文件的路径名。例如：

```xml
<!-- 对应写法，在硬盘上的目录层次结构中，子模块在父模块的『里面』。 -->
<relativePath>../pom.xml</relativePath>

<!-- 对应写法，在硬盘上的目录层次结构中，子模块和父模块『平级』。 -->
<relativePath>../parent-module/pom.xml</relativePath>

<!-- 对应写法，Maven 就不关系子木块和父木块在硬盘上的目录层次结构。 -->
<!-- 它要求父模块在本地仓库中存在 -->
<relativePath />
```

如果完全没有 `<relativePath>` 元素，等同于：`../pom.xml`

<font color="red">注意</font> 子项目不一定非得新建在父项目内。所以，默认的 `../pom.xml` 并一定总是对的。

## 父子项目的使用

通常，让子项目重用父项目的配置，有两种表现形式：

1. 父项目中定义 `<dependencies>`，子项目『天然』继承了父项目的 `<dependences>`，从而不再需要引入这些依赖。

2. 父项目通过配置 `<dependencyManagement>` 定义依赖包的版本，子项目不用则已，一旦使用这些包中的某个/某些，则不再需要指定它们的版本信息。

