<span class="title">SSM 整合</span>

# logback.xml

[logback.xml](/Part-I/Gist/logback.xml.md#logback.xml%201.0)

# web.xml

[web.xml](/Part-I/Gist/web.xml.md#web.xml%203.0)

# pom.xml

[pom.xml 的 properties 部分](/Part-I/Gist/pom.xml.md#properties%201.0)

[pom.xml 的 dependencies 和 build 部分](/Part-I/Gist/pom.xml.md#dependencies%201.0)


需要注意的是：

*`mybatis-spring`* 是用于 mybatis 和 spring 整合的包。它们三者之间有一个简单的版本要求问题：

| MyBatis-Spring | MyBatis | Spring 框架 | Java |
| :-: | :-: | :-: | :-: |
| 2.0	| 3.5+	| 5.0+   | Java 8+ |
| 1.3	| 3.4+	| 3.2.2+ | Java 6+ |


# 整合 Web 层：spring-web.xml

[spring-web.xml](/Part-I/Gist/spring-web.xml.md#spring-web.xml)


# 整合 Service 层：spring-service.xml


[spring-service.xml](/Part-I/Gist/spring-service.xml.md#for%20Mybatis)


# 整合 Dao 层：spring-dao.xml

[spring-dao.xml](/Part-I/Gist/spring-dao.xml.md#for%20Mybatis)
 

# mybatis-config.xml

[mybatis-config.xml](/Part-I/Gist/mybatis-config.xml.md#mybatis-config.xml%203.0)
