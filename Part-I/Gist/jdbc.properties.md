<span class="title">jdbc.properties</span>

# jdbc

`.properties` 和命令行类似，原则上一行就是一个键值对，如果因为值太长需要折行继续编写，那么上一行的行尾需要加 `\` 表示本行还未结束 。

!FILENAME jdbc.properties
```properties
datasource.driver-class-name=com.mysql.cj.jdbc.Driver
datasource.url=jdbc:mysql://127.0.0.1:3306/scott\
    ?useUnicode=true\
    &characterEncoding=utf-8\
    &useSSL=true\
    &serverTimezone=UTC
datasource.username=root
datasource.password=123456
```
