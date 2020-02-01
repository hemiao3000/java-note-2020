<span class="title">第一个 SpringMVC 应用程序</span>

Spring MVC 是 Spring 提供的一个实现了 Web MVC 设计模式的轻量级 Web 框架。它与 Struts 2 框架一样，都属于 MVC 框架，但其使用和性能等方面都比 Struts 2 更加优异。

Spring MVC 的核心包是：

- spring-web
- spring-webmvc

依赖于：

- commons-logging
- spring-aeans
- spring-beans
- spring-context
- spring-core
- spring-expression



# 配置 web.xml

```xml
<?xml version="1.0" encoding="UTF-8"?>
<web-app xmlns="http://xmlns.jcp.org/xml/ns/javaee"
         xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://xmlns.jcp.org/xml/ns/javaee
        http://xmlns.jcp.org/xml/ns/javaee/web-app_3_1.xsd"
         version="3.1">
    <servlet>
        <servlet-name>springmvc</servlet-name>
        <servlet-class>org.springframework.web.servlet.DispatcherServlet</servlet-class>
        <init-param>
            <param-name>contextConfigLocation</param-name>
            <param-value>classpath:spring-web.xml</param-value>
        </init-param>
        <load-on-startup>1</load-on-startup>
    </servlet>

    <servlet-mapping>
        <servlet-name>springmvc</servlet-name>
        <url-pattern>/</url-pattern>
    </servlet-mapping>
</web-app>
```

在上述 *`web.xml`* 文件中主要对 *`<servlet>`* 和 *`<servlet-mapping>`* 元素进行了配置。

- 在 *`<servlet>`* 中配置了 Spring MVC 的前端控制器 **DispatcherServlet** 。
- *`<servlet>`* 子元素 *`<init-param>`* 配置了 Spring MVC 配置文件的位置。
- *`<load-on-startup>`* 元素中的 1 表示容器（Tomcat）在启动时立即加载这个 DispatcherServlet。
- 在 *`<servlet-mapping>`* 中，通过 *`<url-pattern>`* 元素的 *`/`*，会对所有 URL 拦截<small>（不包括 jsp / html 等）</small>，并交由 DispatcherServlet 处理。

# 创建 Controller 类

<small>注意，以下写法和相关配置是『上古时代』写法，仅作了解。实际中并不会这么写。</small>

```java
/**
 * 控制器类
 */
public class HelloController implements Controller {

    @Override
    public ModelAndView handleRequest(HttpServletRequest req, HttpServletResponse resp) throws Exception {
        ModelAndView mav = new ModelAndView();
        mav.addObject("msg", "这是我的第一个 SpringMVC 程序");
        mav.setViewName("/WEB-INF/jsp/hello.jsp");

        return mav;
    }
}
```

# 配置控制器映射信息

```xml
<?xml version="1.0" encoding="UTF-8"?>
<beans xmlns="http://www.springframework.org/schema/beans"
    xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
    xmlns:mvc="http://www.springframework.org/schema/mvc"
    xmlns:context="http://www.springframework.org/schema/context"
    xsi:schemaLocation="http://www.springframework.org/schema/beans 
        http://www.springframework.org/schema/beans/spring-beans.xsd
        http://www.springframework.org/schema/context
        http://www.springframework.org/schema/context/spring-context.xsd
        http://www.springframework.org/schema/mvc
        http://www.springframework.org/schema/mvc/spring-mvc.xsd">

    <!-- 处理器 Handler，映射 "/hello" 请求 -->
    <bean name="/hello" class="xxx.yyy.zzz.web.HelloController"/>

    <!-- 处理器映射器，以处理器 Handler 的 name 作为 url 进行匹配 -->
    <bean class="org.springframework.web.servlet.handler.BeanNameUrlHandlerMapping" />

    <!-- 处理器适配器，配置对处理器中 handleRequest() 方法的调用 -->
    <bean class="org.springframework.web.servlet.mvc.SimpleControllerHandlerAdapter" />

    <!-- 视图解析器 -->
    <bean class="org.springframework.web.servlet.view.InternalResourceViewResolver"/>
</beans>
```

> <small>实际上从 Spring 4.0 开始，如果不配置处理器映射器、处理器适配器和视图解析器，Spring 会使用默认配置来完成相应工作。</small>

# 创建视图

```jsp
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page isELIgnored="false" %>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>入门程序</title>
</head>
<body>

<h3>hello.jsp</h3>
<h4>${msg}</h4>
</body>
</html>
```