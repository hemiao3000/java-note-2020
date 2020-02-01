<span class="title">SSM 整合（代码配置）</span>

> - <small>Spring 从 2.x 开始，提供注解，用以简化 XML 配置文件配置</small>
> - <small>Spring 从 3.x 开始，基于注解功能，提供了全新的配置方式：Java 代码配置方式。</small>
> - <small>到 4.x 时代，Spring 官方推荐使用 Java 代码配置，以完全替代 XML 配置，实现零配置文件。</small>
> - <small>到了 Spring Boot 时代，Spring 官方甚至直接将 `推荐使用` 中的 `推荐` 二字给拿掉了。</small>

# 基本形式

Spring 的 Java 代码配置与 XML 配置文件配置有对应关系，本质上太大区别。

核心关键点有两处：

- *`.xml`* 配置文件演变为一个配置类，其头上标注 *`@Configuation`* 注解；

- *`<bean>`* 配置演变为配置类中的一个方法，其头上标注 *`@Bean`* 注解。


# logback.xml

[logback.xml](/Part-I/Gist/logback.xml.md)

# pom.xml

略

# WebInitializer（web.xml 替代品）


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

# SpringWebConfig（spring-web.xml 替代品）

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

# 整合 Service 层：spring-service.xml

[spring-service.xml](/part-c/Template/spring-service.xml.md#1-spring-service.xml)


# 整合 Dao 层：spring-dao.xml

[spring-dao.xml](/part-c/Template/spring-dao.xml.md#1-spring-dao.xml)
 

# mybatis-config.xml

[mybatis-config.xml](/part-c/Template/mybatis-config.xml.md#3-mybatis-config.xml)
