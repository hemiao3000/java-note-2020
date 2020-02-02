<span class="title">Spring 中各种数据库连接池配置</span>

使用 mysql-connector 6+ 连接 MySQL 时『多』出来两个小要求：

- MySQL 驱动类发生了变化。原来的 Driver 被标注为废弃，改用新的 Driver：`com.mysql.cj.jdbc.Driver`
- 需要指定时区 `serverTimezone` 参数：`serverTimezone=UTC` 

例如：

```
url=jdbc:mysql://localhost:3306/scott?serverTimezone=UTC&?useUnicode=true&characterEncoding=utf8&useSSL=false
```

注意：`.xml` 配置配置文件中不能出现 `&` 符号，因此 `url` 中需要的 `&` 符号，要使用它的 `&amp;` 符号来代替。

```xml
<context:property-placeholder location="classpath:jdbc.properties" />

<!-- HikariCP -->
<bean id="dataSource" class="com.zaxxer.hikari.HikariDataSource" destroy-method="close">
  <property name="driverClassName" value="${datasource.driver-class-name}" />
  <property name="jdbcUrl" value="${datasource.url}" />
  <property name="username" value="${datasource.username}" />
  <property name="password" value="${datasource.password}" />
</bean>

<!-- Druid -->
<bean id="dataSource" class="com.alibaba.druid.pool.DruidDataSource" init-method="init" destroy-method="close">
  <property name="driverClassName" value="${datasource.driver-class-name}"/>
  <property name="url" value="${datasource.url}" />
  <property name="username" value="${datasource.username}"/>
  <property name="password" value="${datasource.password}"/>
</bean>

<!-- DBCP2 -->
<bean id="dataSource" class="org.apache.commons.dbcp2.BasicDataSource" destroy-method="close">
  <property name="driverClassName" value="${datasource.driver-class-name}" />
  <property name="url" value="${datasource.url}"/>
  <property name="username" value="${datasource.username}" />
  <property name="password" value="${datasource.password}" />
</bean>
```

