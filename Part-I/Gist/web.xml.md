<span class="title">web.xml 文件模板</span>

## web.xml


## web.xml

只利用一次加载时机

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
      <url-pattern>*.do</url-pattern>     <!--    后缀拦截 -->
    </servlet-mapping>                    <!-- /  默认/兜底拦截 -->
</web-app>                                <!-- /* 拦截所有请求-->
```


## web.xml 3.0

利用两次加载时机

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

    <listener>
      <listener-class>
        org.springframework.web.context.ContextLoaderListener
      </listener-class>
    </listener>

    <context-param>
      <param-name>contextConfigLocation</param-name>
      <param-value>
        classpath:spring/spring-service.xml,
        classpath:spring/spring-dao.xml
      </param-value>
    </context-param>

    <servlet>
      <servlet-name>HelloWeb</servlet-name>
        <servlet-class>
          org.springframework.web.servlet.DispatcherServlet
        </servlet-class>
        <init-param>
          <param-name>contextConfigLocation</param-name>
          <param-value>classpath:spring/spring-web.xml</param-value>
          </init-param>
      <load-on-startup>1</load-on-startup>
    </servlet>

    <servlet-mapping>
      <servlet-name>HelloWeb</servlet-name>
      <url-pattern>*.do</url-pattern> 		<!--    后缀拦截 -->
    </servlet-mapping>                    <!-- /  默认/兜底拦截 -->
</web-app>                                <!-- /* 拦截所有请求-->
```

## web.xml 4.0

启用 Spring MVC 自带的 POST 请求编码过滤器，解决中文乱码问题。


