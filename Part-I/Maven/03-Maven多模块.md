# Maven 多模块（聚合）

Maven <font color="#0088dd">**聚合**</font> 功能是 <font color="#0088dd">**继承**</font> 功能的『升级』。

即，Maven 多模块项目一定是父子项目，Maven 父子项目则不一定是多模块项目。

- Maven 继承<small>（父子工程）</small>的目的为了配置文件的复用和配置信息的统一管理。
- Maven 聚合<small>（多模块工程）</small>目的是项目功能上的拆分。

> 例如，在 log4j1 时代，log4j 项目的『产出物』只有一个 `log4j.jar` 包。到了 log4j2 时代，log4j 项目的『产出物』就变成了两个 `log4j-api` 和 `log4j-core`。很明显，就是两部分相对独立的代码分别打成了两个包，而并不像以前那样打成一个包。

由于 Maven 多模块项目是父子项目的一种高级形式，因此，多模块项目也是有一个父模块包含一个或多个子模块，不过有几点不同：

1. 多模块项目中，子项目一定是在父项目『里面』的。

2. 多模块项目中，父项目中会『多出来』一个 `<modules>` 元素。

    ```xml
    <project ...>

        <modelVersion>4.0.0</modelVersion>
        <groupId>org.codehaus.mojo</groupId>
        <artifactId>my-parent</artifactId>
        <version>2.0</version>
        <packaging>pom</packaging>

        <modules>
            <module>parent-module</module>
            <module>child1-module</module>
            <module>child2-module</module>
            <module>...</module>
            <module>...</module>
            <module>...</module>
        </modules>
        ...
    ```
