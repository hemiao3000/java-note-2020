<span class="title">spring jdbc</span>

```xml
<dependency>
    <groupId>org.springframework</groupId>
    <artifactId>spring-jdbc</artifactId>
    <version>${spring.version}</version>
</dependency>
```

Spring 为了提供对 Jdbc 的支持，在 Jdbc API 的基础上封装了一套实现，以此建立一个 JDBC 存取框架。

<small>（作为 Spring JDBC 框架的核心）</smalL>JDBC Template 的设计目的主要是两个：

1. 简化 JDBC 的操作代码。
2. 可以将事务的管理工作委托给 Spring，进一步简化代码。

JdbcTemplate 使用很简单：要求 Spring『帮』我们创建一个 JdbcTemplate 的单例对象，随后，我们在代码<small>（Dao）</small>中，注入这个 template 单例对象，使用它即可。


需要注意的是，Spirng 创建 JdbcTemplte 单例对象时，需要传入 DataSource 单例对象。传入 DataSource 的目的在于，JdbcTempate 会自己从 DataSource 中取 Connection 对象进行数据库操作。从而不再需要我们从 Service 层中传入 Connection 对象。

# 配置文件

!FILENAME web.xml
```xml
<?xml version="1.0" encoding="UTF-8"?>
<web-app 
    xmlns="http://xmlns.jcp.org/xml/ns/javaee"
    xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
    xsi:schemaLocation="
      http://xmlns.jcp.org/xml/ns/javaee
      http://xmlns.jcp.org/xml/ns/javaee/web-app_3_1.xsd"
    version="3.1">

    <display-name>Archetype Created Web Application</display-name>

    <servlet>
        <servlet-name>HelloWeb</servlet-name>
        <servlet-class>
            org.springframework.web.servlet.DispatcherServlet
        </servlet-class>
        <init-param>
            <param-name>contextConfigLocation</param-name>
            <param-value>classpath:application-context.xml</param-value>
        </init-param>
        <load-on-startup>1</load-on-startup>
    </servlet>

    <servlet-mapping>
        <servlet-name>HelloWeb</servlet-name>
        <url-pattern>/</url-pattern>    <!-- .do  后缀拦截 -->
    </servlet-mapping>                  <!-- /  默认/兜底拦截 -->
</web-app>                              <!-- /* 拦截所有请求-->
```


!FILENAME application-context.xml
```xml
<beans xmlns="http://www.springframework.org/schema/beans"
       xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
       xmlns:context="http://www.springframework.org/schema/context"
       xmlns:mvc="http://www.springframework.org/schema/mvc"
       xmlns:tx="http://www.springframework.org/schema/tx"
       xsi:schemaLocation="
        http://www.springframework.org/schema/beans
        http://www.springframework.org/schema/beans/spring-beans.xsd
        http://www.springframework.org/schema/context
        http://www.springframework.org/schema/context/spring-context.xsd
        http://www.springframework.org/schema/mvc
        http://www.springframework.org/schema/mvc/spring-mvc.xsd
        http://www.springframework.org/schema/tx
        http://www.springframework.org/schema/tx/spring-tx.xsd">

    <!-- Spring Web -->
    <mvc:annotation-driven />
    <context:component-scan base-package="xxx.yyy.zzz.web"/>
    <bean class="org.springframework.web.servlet.view.InternalResourceViewResolver">
        <property name="prefix" value="/WEB-INF/jsp/"/>
        <property name="suffix" value=".jsp"/>
    </bean>

    <!-- Spring Service -->
    <context:component-scan base-package="xxx.yyy.zzz.service"/>
    <tx:annotation-driven transaction-manager="transactionManager"/>
    <bean id="transactionManager" class="org.springframework.jdbc.datasource.DataSourceTransactionManager">
        <property name="dataSource" ref="dataSource"/>
    </bean>

    <!-- Spring Dao -->
    <context:component-scan base-package="xxx.yyy.zzz.dao" />
    <context:property-placeholder location="classpath:jdbc.properties" ignore-unresolvable="true" />
    <bean id="dataSource" class="com.zaxxer.hikari.HikariDataSource" destroy-method="close">
        <property name="driverClassName" value="${datasource.driver-class-name}"/>
        <property name="jdbcUrl" value="${datasource.url}"/>
        <property name="username" value="${datasource.username}"/>
        <property name="password" value="${datasource.password}"/>
    </bean>
    <bean id="jdbcTemplate" class="org.springframework.jdbc.core.JdbcTemplate">
        <property name="dataSource" ref="dataSource"/>
    </bean>
</beans>
```

# Dao 和 Service

!FILENAME DepartmentDAO.java
```java
@Repository
public class DepartmentDAO {

    private static final Logger log = LoggerFactory.getLogger(DepartmentDAO.class);

    @Autowired
    private JdbcTemplate template;

    public void delete(int id) {
        log.info("DAO: delete");
        template.update("delete from department where id = ?", id);
        if (id % 2 == 0)
            throw new RuntimeException();
    }
}
```

!FILENAME DepartmentService.java
```java
@Transactional
@Service
public class DepartmentService {

    private static final Logger log = LoggerFactory.getLogger(DepartmentService.class);

    @Autowired
    private DepartmentDAO dao;

    public void delete(Integer id) {
        log.info("Service: delete");
        dao.delete(id);
    }
}
```

# Spring-JDBC API：增删改

JdbcTemplate 为 DAO 中的 增删改操作提供了 `update()` 方法。

```java
template.update("INSERT INTO exam_user VALUE(NULL, ?, ?)", username, password);

template.update("DELETE FROM exam_user WHERE uid = ?", uid);

template.update("UPDATE exam_user SET username = ?, password = ? WHERE uid = ?", newUsername, newPassword, uid);
```

# Spring-JDBC API：查询

```java
User user = template.queryForObject("SELECT * FROM exam_user WHERE username = ?", new BeanPropertyRowMapper<>(User.class), username);

List<User> list = template.query("select * from exam_user", new BeanPropertyRowMapper<>(User.class));
```

在 JavaBean 的属性名与数据库名一致的情况下，Spring Jdbc 提供了自带的一个 ***`BeanPropertyRowMapper`*** 类，用于将 ResultSet 中的数据库数据『映射/转换』成 JavaBean 。

*`template.queryForObject()`* 方法有一个『问题』，由于涉及到 ResultSet 到 JavaBean 的转换，*`queryForObject()`* 默认是要求 **必须** 要查得到数据的。如果你的 SQL 在数据库中查不到任何数据<small>（也许本来就没有这样的一条数据）</small>，那么 *`queryForObject()`* 方法会抛出异常：***`EmptyResultDataAccessException`*** 。

因此，对于逻辑上本来就有可能查不到数据的情况，建议也使用 *`query`* 方法查询，得到一个 List 后，在通过判断 List 的 *`.size()`*，来确定查没查到数据，并进行后续处理。

# 自定义映射结果集

对于数据库中的字段的名字与 JavaBean 的属性名不一致的情况，如果无法将其两者统一，那么在使用 *`query()`* 和 *`queryForObject()`* 时，就需要自己『定制』ResultSet 到 JavaBean 的转换规则（即，实现 ***`RowMapper`*** 接口）。

```java
template.query("", (resultSet, n) -> {
    User user = new User();
    user.setUid(resultSet.getLong("uid"));
    user.setUsername(resultSet.getString("username"));
    user.setPassword(resultSet.getString("password"));
    return user;
});
```
