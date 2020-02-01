<span class="title">spring-dao.xml</span>

# for Myabtis

默认 Spring 的项目中只能出现一个 `context:property-placeholder`，你如果想加载多个配置文件，可以在一个配置文件中写多个文件路径名：

```xml
<context:property-placeholder location="classpath:jdbc.properties,classpath:...,classpath:..."/>
```

另一种办法是下面这样，通过为每一个 placeholder 指定 `ignore-unresolvable="true"` 让 spring 支持多个 placeholder 。

```xml
<?xml version="1.0" encoding="UTF-8"?>
<beans xmlns="http://www.springframework.org/schema/beans"
    xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
    xmlns:context="http://www.springframework.org/schema/context"
    xsi:schemaLocation="
        http://www.springframework.org/schema/beans
        http://www.springframework.org/schema/beans/spring-beans.xsd
        http://www.springframework.org/schema/context
        http://www.springframework.org/schema/context/spring-context.xsd">

    <context:property-placeholder location="classpath:jdbc.properties" ignore-unresolvable="true" />

    <bean id="dataSource" class="com.zaxxer.hikari.HikariDataSource" destroy-method="close">
      <property name="driverClassName" value="${datasource.driver-class-name}" />
      <property name="jdbcUrl" value="${datasource.url}"/>
      <property name="username" value="${datasource.username}" />
      <property name="password" value="${datasource.password}" />
    </bean>

    <bean id="sqlSessionFactory" class="org.mybatis.spring.SqlSessionFactoryBean">
      <property name="dataSource" ref="dataSource"/> 
      <property name="configLocation" value="classpath:mybatis/mybatis-config.xml"/>
      <property name="mapperLocations" value="classpath:mybatis/mapper/*.xml"/>
    </bean>

    <bean class="org.mybatis.spring.mapper.MapperScannerConfigurer">
      <property name="basePackage" value="hemiao3000.gitee.io.dao"/>
    </bean>

</beans>
```
