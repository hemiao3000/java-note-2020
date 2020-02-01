<span class="title">Java 代码配置</span>

从 Spring 3.0 开始，Spring 官方就推荐大家使用 java 代码配置来代替以前的 xml 文件配置。而到了 SpringBoot，Java 代码配置更是成了标配。

Java 代码配置主要靠 Java 类和一些注解，比较常用的注解有：

| 常用注解 | 说明 |
| :- | :- |
| `@Configuration` | 声明一个类作为配置类，代替 *`.xml`* 文件 |
| `@Bean` | 声明在方法上，将方法的返回值加入Bean容器，代替 *`<bean>`* 标签 |
| `@Value` | 属性注入 |
| `@PropertySource` | 指定外部属性文件 |


# 配置 Druid 数据库连接池的简单方式

Spring Boot 默认的数据库连接池是 HikariCP 。spring-boot-starter-parent 中设置了 HIkariCP 的版本信息。因此，如果使用 HikariCP 只用声明你要用它就行，而声明要使用 Druid，你需要自己指定所使用的版本。

引入依赖：

```xml
<dependency>
    <groupId>mysql</groupId>
    <artifactId>mysql-connector-java</artifactId>
</dependency>
<dependency>
    <groupId>com.alibaba</groupId>
    <artifactId>druid</artifactId>
    <version>1.1.6</version>
</dependency>
```

编写 Java 配置类：

```java
@Configuration
public class JdbcConfig {

    @Bean(initMethod = "init", destroyMethod = "close")
    public DataSource dataSource() {
        DruidDataSource dataSource = new DruidDataSource();
        dataSource.setDriverClassName("com.mysql.cj.jdbc.Driver");
        dataSource.setUrl("jdbc:mysql://192.168.119.130:3306/scott?serverTimezone=UTC&useUnicode=true&characterEncoding=utf-8&useSSL=false");
        dataSource.setUsername("root");
        dataSource.setPassword("123456");
        return dataSource;
    }
}
```

以上代码配置等同于如下的 `.xml` 配置：

```xml
<beans ...>
    <bean id="dataSource" class="com.alibaba.druid.pool.DruidDataSource.DruidDataSource">
        <propertie name="driverClassName" value="com.mysql.cj.jdbc.Driver"/>
        <propertie name="url" value="jdbc:mysql://192.168.119.130:3306/scott?serverTimezone=UTC&amp;useUnicode=true&amp;characterEncoding=utf-8&amp;useSSL=false"/>
        <propertie name="username" value="root"/>
        <propertie name="password" value="123456"/>
    </bean>
</beans>
```

# 代码配置中引用关系的表示

上面的 DruidDataSource ，我们为其属性赋值时，它的四个属性都是简单类型属性，因此十分容易处理。但是，在 Spring 的容器中，Java Bean 可能会存在引用。

在 *`.xml`* 配置文件中，我们是通过 *`ref`* 属性为引用类型的属性赋值的。

例如：

```xml
<beans>

  <bean id="teacher" class="xxx.xxx.xxx.Teacher">
    <property name="name" value="tom"/>
    <property name="age" value="40"/>
  </bean>

  <bean id="student" class="xxx.xxx.xxx.Student">
    <property name="name" value="jerry"/>
    <property name="age" value="20"/>
    <property name="teacher" ref="teacher"/>
  </bean>

</beans>
```

而在 Java 代码配置中，有三种方式来配置 Java Bean 的引用关系<small>（其中，推荐第三种）</small>：

## 通过方法调用表示引用关系

如果两个 Java Bean <strong>在同一个配置类中</strong>，可使用这种方式：

```java
@Bean
public Employee employee() {
    Employee employee = new Employee();
    employee.setId(10);
    employee.setName("tom");
    employee.setSalary(2000.0);
    employee.setDepartment(department());

    return employee;
}

@Bean
public Department department() {
    Department department = new Department();
    department.setId(1);
    department.setName("研发部");
    department.setLocation("北京");

    return department;
}
```

## 通过 @Autowire 表示引用关系

如果两个 Java Bean <strong>分别在不同的</strong> 配置类中，可使用这种方式：

```java
@Autowired
public Department department;

@Bean
public Employee employee() {
    Employee employee = new Employee();
    employee.setId(10);
    employee.setName("tom");
    employee.setSalary(2000.0);
    employee.setDepartment(department);

    return employee;
}
```

## 通过参数表示引用关系

无论两个 Java Bean 是在不同的配置类中，还是在同一个配置类中，都可使用这种方式：

```java
@Bean
public Employee employee(Department department) {
    Employee employee = new Employee();
    employee.setId(10);
    employee.setName("tom");
    employee.setSalary(2000.0);
    employee.setDepartment(department);

    return employee;
}
```

# Druid 数据库连接池配置 v2

在上面的 Druid 的配置中，数据库连接的四大属性值是在代码中『写死』的。我们可以把它们『抽取』出来放在配置文件中。

## 配置文件 jdbc.properties

```properties
jdbc.driverClassName = com.mysql.cj.jdbc.Driver
jdbc.url = jdbc:mysql://127.0.0.1:3306/scott
jdbc.username = root
jdbc.password = 123456
```

在配置类中可以结合 `@PropertySource` 和 `@Value` 注解，让 Spring 从指定配置文件中读取数据，再注入到配置类中。

## 配置类

```java
@Configuration
@PropertySource("classpath:jdbc.properties")    // 这里
public class JdbcConfig {

    @Value("${jdbc.url}")                       // 这里
    String url;

    @Value("${jdbc.driverClassName}")           // 这里
    String driverClassName;

    @Value("${jdbc.username}")                  // 这里
    String username;

    @Value("${jdbc.password}")                  // 这里
    String password;

    @Bean
    public DataSource dataSource() {
        DruidDataSource dataSource = new DruidDataSource();
        dataSource.setUrl(url);
        dataSource.setDriverClassName(driverClassName);
        dataSource.setUsername(username);
        dataSource.setPassword(password);
        return dataSource;
    }
}
```


# Druid 数据库连接池配置 v3

由于 Spring Boot 项目启动时，一定会加载项目的配置文件 *`application.properties`*，所以，我们可以直接将自定义的配置项写在 *`application.properties`* 中，而不必再单独的创建一个配置文件。

这种写法本质上，和上述的 `jdbc.properties` 的写法是一样的。

截至到这里，日常编程中的配置工作大体也就是如此。理论上，还有再进一步改进优化的余地，但是总体来说，必要性已经不大了。<small>（可能，还把简单问题复杂化了）</small>。


# 附：Druid 数据库连接池配置 v4

在配置类中，我们有四个属性用于接收/获取配置文件中的四项配置的值。我们可以将这个四个属性<small>（及其功能）</small>抽取到另一个单独的类中，从而使配置类的代码更简洁。

注意，这里我们需要使用一个叫 *`@EnableConfigurationProperties`* 的注解，为此，我们需要在 pom 中多引入一个依赖：

```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-configuration-processor</artifactId>
    <optional>true</optional>
</dependency>
```

JdbcProperties：

```java
@ConfigurationProperties(prefix = "jdbc")   // 去掉前缀
@PropertySource("classpath:application.properties")
public class JdbcProperties {
    private String url;
    private String driverClassName;
    private String username;
    private String password;

    /* 
     * 其实，并不是严格要求属性文件中的属性名与成员变量名一致。
     * 支持驼峰，中划线，下划线等转换。
     */

    // getter / setter
    ...
}
```

配置类：

```java
@Configuration
@EnableConfigurationProperties(JdbcProperties.class)    // 这里
public class SpringDaoConfig {

    @Bean
    public DataSource dataSource(JdbcProperties jdbc) { // 这里
        ...
    }
}
```
