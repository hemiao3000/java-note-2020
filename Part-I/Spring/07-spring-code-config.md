<span class="title">Spring 的 Java 代码配置方式</span>

- <small>Spring 从 2.x 开始，提供注解，用以简化 XML 配置文件配置</small>
- <small>Spring 从 3.x 开始，基于注解功能，提供了全新的配置方式：Java 代码配置方式。</small>
- <small>到 4.x 时代，Spring 官方推荐使用 Java 代码配置，以完全替代 XML 配置，实现零配置文件。</small>
- <small>到了 Springboot 时代，Spring 官方甚至直接将 `推荐使用` 中的 `推荐` 二字给拿掉了。</small>

需要注意的是，在 Java 项目中使用 Spring 的 Java 代码配置时，使用过 ***`AnnotationConfigApplicationContext`*** 加载配置类。

```java
AbstractApplicationContext container = new AnnotationConfigApplicationContext(XxxConfig.class);

...

container.close();
```

# spring-dao.xml 演变

!FILENAME spring-dao.xml
```xml
<!--
<bean class="com.alibaba.druid.pool.DruidDataSource" init-method="init" destroy-method="close">
    <property name="driverClassName" value="com.mysql.jdbc.Driver"/>
    <property name="url" value="jdbc:mysql://127.0.0.1:3306/scott?useUnicode=true&amp;characterEncoding=utf-8&amp;useSSL=false&amp;serverTimezone=UTC"/>
    <property name="username" value="root"/>
    <property name="password" value="123456"/>
</bean>
-->

<!-- HikariCP -->
<bean id="dataSource" class="com.zaxxer.hikari.HikariDataSource" destroy-method="close">
  <property name="driverClassName" value="com.mysql.jdbc.Driver"/>
  <property name="jdbcUrl" value="jdbc:mysql://127.0.0.1:3306/scott?useUnicode=true&amp;characterEncoding=utf-8&amp;useSSL=false&amp;serverTimezone=UTC"/> 
  <property name="username" value="root"/>
  <property name="password" value="123456"/>
</bean>
```

演变为：

!SpringDaoConfig.java

```java
@Configuration
public class SpringDaoConfig {

    /*
    @Bean(name = "dataSource", initMethod = "init", destroyMethod = "close")
    public DruidDataSource dataSource() {
        DruidDataSource dataSource = new DruidDataSource();
        dataSource.setDriverClassName("com.mysql.jdbc.Driver");
        dataSource.setUrl("jdbc:mysql://127.0.0.1:3306/scott?useUnicode=true&characterEncoding=utf-8&useSSL=false&serverTimezone=UTC");
        dataSource.setUsername("root");
        dataSource.setPassword("123456");
        return dataSource;
    }
    */

    
    @Bean(name = "dataSource", destroyMethod = "close")
    public HikariDataSource dataSource() {
        HikariDataSource dataSource = new HikariDataSource();
        dataSource.setDriverClassName("com.mysql.jdbc.Driver");
        dataSource.setJdbcUrl("jdbc:mysql://127.0.0.1:3306/scott?useUnicode=true&characterEncoding=utf-8&useSSL=false&serverTimezone=UTC");
        dataSource.setUsername("root");
        dataSource.setPassword("123456");
        return dataSource;
    }

}
```

加载配置文件的代码，也从加载 *`.xml`* 配置文件，演变为加载 *`.class`* 类。

```java
ApplicationContext context = new AnnotationConfigApplicationContext(SpringDaoConfig.class);
```

# Bean 的引用的体现

在 Spring 的 Java 代码配置中，Bean 的引用关系有 2 种体现方式：

## 一个 Bean 方法内部调用另一个 Bean 方法：

```java
@Bean
public DataSourceTransactionManager txManager() {
    DataSourceTransactionManager manager = new DataSourceTransactionManager();
    manager.setDataSource(dataSource()); // 看这里
    return manager;
}
```

## 通过 Bean 方法的参数引用另一个 Bean：

```java
@Bean
public DataSourceTransactionManager txManager(DataSource ds) { // 看这里
    DataSourceTransactionManager manager = new DataSourceTransactionManager();
    manager.setDataSource(ds);
    return manager;
}
```

**推荐使用上述第二种方式。**

# SSM 的 Java 代码配置

## `web.xml` 被 `WebAppInitializer` 替代

从 Servlet 3.0 开始 *`web.xml`* 的作用就逐渐被其它新增的特性所替代，直到可以完全不存在！

在没有 *`web.xml`* 的情况下，Web 容器在启动 webapp 时，会在代码中查找一个 *`WebApplicationInitializer`* 接口的实现类，该实现类就起到了 *`web.xml`* 等同的配置功能。

Spring MVC 进一步简化这个过程，我们只需要继承 *`AbstractAnnotationConfigDispatcherServletInitializer`* 类，就间接实现了 *`WebApplicationInitializer`* 接口。

> <small>*`WebAppInitializer`* 替代的是 *`web.xml`*，它并非 Spring 的配置文件，因此它的头上是不需要 *`@Configuration`* 注解的。</small>

!FILENAME 样例

```java
public class WebInitializer extends AbstractAnnotationConfigDispatcherServletInitializer {

    @Override
    protected Class<?>[] getRootConfigClasses() {
        return new Class<?>[]{
            SpringServiceConfig.class,
            SpringDaoConfig.class
        };
    }

    @Override
    protected Class<?>[] getServletConfigClasses() {
        return new Class<?>[]{
            SpringWebConfig.class
        };
    }

    @Override
    protected String[] getServletMappings() {
        return new String[]{"/"};
    }

    @Override
    protected Filter[] getServletFilters() {
        Filter encodingFilter = new CharacterEncodingFilter("UTF-8", true);

        return new Filter[]{encodingFilter};
    }
}
```

## `spring-web.xml` 被 `SpringWebConfig` 替代

如前面所说，在 Java 代码配置形势下，Spring 的 *`.xml`* 配置文件会被等价的标注了 *`@Configuration`* 的配置类所取代。

和后续的 *`SpringServiceConfig`*（替代了 *`spring-service.xml`*）、*`SpringDaoConfig`*<small>（替代了 *`spring-dao.xml`*）</small> 配置类不同的是，它有个『额外』的要求：**<font color="#0088dd">它必须实现 *`WebMvcConfigurer`* 接口**</font><small>（实际上Spring 之所以这么要求也是为了帮我们简化配置）</small>。

!FILENAME 样例

```java
@Configuration
@EnableWebMvc 
@ComponentScan("xxx.yyy.zzz.web")
public class SpringWebConfig implements WebMvcConfigurer {

    @Bean
    public InternalResourceViewResolver viewResolver() {
        InternalResourceViewResolver viewResolver = new InternalResourceViewResolver();
        viewResolver.setPrefix("/WEB-INF/jsp/");
        viewResolver.setSuffix(".jsp");

        return viewResolver;
    }

    // 配置启用 DefaultServletHandler。目的是通过使用它不去拦截静态资源。
    @Override
    public void configureDefaultServletHandling(DefaultServletHandlerConfigurer configurer) {
        configurer.enable();
    }

    /*
    // 另一种静态资源不拦截的配置。
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/static/**") // 过滤静态资源路径
                .addResourceLocations("/static/");// 定位资源
    }
    */

    /*
    // 配置 URL 路径的匹配规则。
    @Override
    public void configurePathMatch(PathMatchConfigurer configurer) {
        
        // 默认值是 true，这种情况下 SpringMVC 会忽略掉 URL 请求中的后缀。
        // 例如，URL hello.do 能触发 @RequestMapping("/hello")
        // xml 中配置为：
        // <mvc:annotation-driven>
        //    <mvc:path-matching suffix-pattern="false" />
        // </mvc:annotation-driven>

        configurer.setUseSuffixPatternMatch(false);
    }
    */
}
```

## `spring-service.xml` 被 `SpringServiceConfig` 替代

!FILENAME 样例

```java
@Configuration
@ComponentScan("xxx.yyy.zzz.service")
@EnableTransactionManagement
@EnableAspectJAutoProxy(proxyTargetClass = true)    // 强制指定使用 cglib 动态代理
public class SpringServiceConfig {

    /**
     * 需要注意。JPA 和 Mybatis 使用的 事务管理器不一样。
     * 不要无脑复制粘贴。
     */
    @Bean("txManager")
    public DataSourceTransactionManager getTXManager(DataSource ds) {
        DataSourceTransactionManager manager = new DataSourceTransactionManager();
        manager.setDataSource(ds);
        return manager;
    }

}
```


### `spring-dao.xml` 被 `SpringDaoConfig` 替代

!FILENAME 样例

```java
@Configuration
public class SpringDaoConfig {

    /*
    @Bean(name="dataSource", initMethod = "init", destroyMethod = "close")
    public DruidDataSource dataSource() {
        DruidDataSource ds = new DruidDataSource();
        ds.setDriverClassName("com.mysql.jdbc.Driver");
        ds.setUrl("jdbc:mysql://localhost:3306/month_exam?serverTimezone=UTC&useUnicode=true&characterEncoding=UTF8&useSSL=false");
        ds.setUsername("root");
        ds.setPassword("123456");

        return ds;
    }
    */

    @Bean(name = "dataSource", destroyMethod = "close")
    public HikariDataSource dataSource() {
        HikariDataSource dataSource = new HikariDataSource();
        dataSource.setDriverClassName("com.mysql.jdbc.Driver");
        dataSource.setJdbcUrl("jdbc:mysql://127.0.0.1:3306/scott?useUnicode=true&characterEncoding=utf-8&useSSL=false&serverTimezone=UTC");
        dataSource.setUsername("root");
        dataSource.setPassword("123456");
        return dataSource;
    }

    @Bean
    public SqlSessionFactoryBean sqlSessionFactoryBean(DataSource ds) throws IOException {
        SqlSessionFactoryBean factoryBean = new SqlSessionFactoryBean();
        factoryBean.setDataSource(ds);
        factoryBean.setConfigLocation(new ClassPathResource("mybatis/mybatis-config.xml"));
        factoryBean.setMapperLocations(new PathMatchingResourcePatternResolver().getResources("mybatis/mapper/*.xml"));

        return factoryBean;
    }

    @Bean
    public MapperScannerConfigurer mapperScannerConfigurer() {
        MapperScannerConfigurer configurer = new MapperScannerConfigurer();
        configurer.setBasePackage("dao");
        return configurer;
    }
}
```