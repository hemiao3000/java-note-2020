# Spring Boot 和 Spring Cloud 的版本

Spring Cloud 是依赖于 Spring Boot。关于如何查询 Spring Cloud 与 Spring Boot 之间的版本关系，可采用如下办法：

进入 Spring.io [官网](https://spring.io/projects/spring-cloud)，点击 PROJECTS 导航，进入 SpringCloud 页面，选择 Learn 标签，选择对应的 Reference Doc <small>（GA 版）</small>。

选择单页（single）模式，以 `spring-boot-starter-parent` 作为关键字进行搜索，会看到类似如下内容：

!FILENAME Spring Cloud Greenwich.SR3 版
```xml
<parent>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-parent</artifactId>
    <version>2.1.7.RELEASE</version>
    <relativePath/>
</parent>
```

!FILENAME Spring Cloud Finchley.SR4 版
```xml
<parent>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-parent</artifactId>
    <version>2.0.9.RELEASE</version>
    <relativePath />
</parent>
```
