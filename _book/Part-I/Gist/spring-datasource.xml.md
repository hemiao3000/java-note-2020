# Spring 中使用数据库连接池配置

## Hikaricp in Spring

Hikaricp 数据库连接池在 Spring 中的配置：

```xml
<context:property-placeholder location="classpath:jdbc.properties" ignore-unresolvable="true" />

<bean id="dataSource" class="com.zaxxer.hikari.HikariDataSource" destroy-method="shutdown">
    <property name="driverClassName" value="${datasource.driver-class-name}" />
    <property name="jdbcUrl" value="${datasource.url}"/>
    <property name="username" value="${datasource.username}" />
    <property name="password" value="${datasource.password}" />
</bean>
```


