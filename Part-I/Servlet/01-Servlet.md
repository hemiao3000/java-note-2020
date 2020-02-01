# Servlet 技术

## Servlet 的版本和对应的 Tomcat

| Servlet 版本 | JSP 版本 | EL 版本 | WebSocket 版本 | Tomcat 版本 | Jetty 版本 | 
|:-|:-|:-|:-|:-|:-|:-|
| 4.0 | 2.4 | 3.1 | 1.2 | 9.0.x | 10.0.x |
| 3.1 | 2.3 | 3.0 | 1.1 | 8.0.x | 9.1.x | 
| 3.0 | 2.2 | 2.2 | 1.1 | 7.0.x | 8.0.x |
| 2.5 | 2.1 | 2.1 | N/A | 6.0.x | 7.0.x | 

不同的 Servelt 版本会影响到 **`web.xml`** 配置文件中的头部的声明。越高版本的 Servlet 功能越丰富也越强大。

## 不同版本的 web.xml 声明

!FILENAME Servlet 3.1
```xml
<?xml version="1.0" encoding="UTF-8"?>
<web-app xmlns="http://xmlns.jcp.org/xml/ns/javaee"
    xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
    xsi:schemaLocation="http://xmlns.jcp.org/xml/ns/javaee
    http://xmlns.jcp.org/xml/ns/javaee/web-app_3_1.xsd"
    version="3.1">
        
    <display-name>Servlet 3.1 Web Application</display-name>  

</web-app>
```


!FILENAME Servlet 4.0
```xml
<?xml version="1.0" encoding="UTF-8"?>
<web-app xmlns="http://xmlns.jcp.org/xml/ns/javaee"
    xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" 
    xsi:schemaLocation="http://xmlns.jcp.org/xml/ns/javaee  
    http://xmlns.jcp.org/xml/ns/javaee/web-app_4_0.xsd" 
    version="4.0" >
        
    <display-name>Servlet 4.0 Web Application</display-name>  

</web-app>
```

## 概述

Servlet 技术的核心是 Servlet 接口，它是所有 Servlet 类必须直接或间接实现的接口。

```java
public class AServlet implements Servlet { ... }
public class BServlet extends GenericServlet { ... }
public class CServlet extends HttpServlet { ... }
```

*注意，Servlet 容器有且不仅只有 Tomcat 一种。*

Servlet 接口定义了 Servlet 类和 Servlet 容器（例如 Tomcat）之间的契约。这个契约归结起来就是，**Tomcat 将 Servlet 类载入内存，并由 Tomcat 调用 Servlet 对象的具体的方法。**<small>这些方法所需的参数也是由 Tomcat 准备并传入的</small>

在 web 项目运行期间，每个 Servlet 类最多只能有一个对象。

用户请求致使 Servlet 容器调用 Servlet 的 `service()` 方法，并传入一个 `ServletRequest` 实例和一个 `ServletResponse` 实例。

- ServletRequest 中封装了当前的 HTTP 请求，因此，Servlet 开发人员不必解析和操作原始的 HTTP 数据。
- ServletResponse 表示对当前用户的 HTTP 响应，它使得将响应发回给用户变得十分容易。

对于每一个 WebApp，Servlet 容器还会创建一个 `ServletContext` 实例。这个实例中封装了上下文（WebApp）的环境详情。每个 WebApp 只有一个 ServletContext 实例。

每个 Servlet 实例也都有一个封装 Servlet 配置的 `ServletCongfig` 。

简而言之，一个 WebApp 在运行时，有：

- 1 个 ServletContext 实例 
- N 个 Servlet 实例 <small>（取决于 Servlet 类的数量）</small>
- N 个 ServletConfig 实例 <small>（取决于 Servlet 类的数量）</small>
- 任意个 HTTPRequest / HTTPResponse 实例 <small>（取决于用户请求的次数）</small>

### Servlet / GenericServlet / HttpServlet

- Servlet 是个接口，是 Servlet 体系中的最顶层。
- GenericServlet 是 Servlet 的实现类，它是一个抽象类。
- HttpServlet 是 GenericServlet 的子类。

### Servlet 接口

Servlet 接口中定义了5个方法：`init()`、`service()`、`destroy()`、`getServletInfo()`、`getServlet()`。

init、service 和 destroy 方法是 Servlet 的生命周期方法。

- `init()` 方法在该 Servlet 第一次被请求时，被 Servlet 容器调用。调用该方法时，容器会传入一个 ServletConfig 对象。
- `service()` 方法在每次用户发起请求时，被容器调用。调用该方法时，容器会传入代表用户请求和相应的 HTTPRequest 对象和 HTTPResponse 对象。
- `destroy()` 方法在销毁 Servlet 时，被容器调用。一般发生在卸载WebApp或关闭容器时。


Servlet 中另外两个方法是非生命周期方法。

- `getServletInfo()`，这个方法返回一个用于描述 Servlet 的字符串。
- `getServletConfig()`，这个方法用于返回由 Servlet 传给 `init()` 方法的 ServletConfig 对象。

## GenericServlet

由于 Servlet 是接口，所以在使用 Servlet 时，必须为 Servlet 接口中的所有方法提供实现。这有些不方便。

GenericServlet 抽象类实现了 Servlet 接口，它为这些方法提供了默认的实现，并新增了一个 `servletConfig` 实例变量，用于在 `init()` 方法中将容器传入的 ServletConfig 对象保存起来。


## HTTPServlet

尽管 GenericServlet 抽象类为我们提供了方便，但它仍不是最常用的办法。HttpServlet 类扩展了 GenericServlet 类，另外它还使用了 ServletRequest/ServletResponse 的子类：HTTPServletRequest/HTTPServletResponse。

HTTPServlet 有两个特性是 GenericServlet 所不具备的：

不用覆盖 `service()` 方法，而覆盖 `doGet()` 或者 `doPost()` 方法。
使用 HttpServletRequest/HttpServletResponse，而非 ServletRequest/ServletResponse。

## ServletRequest 和 HTTPServletRequest

对于每一次HTTP请求，Tomcat 都会创建一个 ServletRequest 实例，并将它传给 Servlet 的 service 方法。

getParameter 方法是 ServletRequest 中最常用的方法，该方法用于返回 HTML 表单中的值。

除了 `getParameter()` 外，类似用于获取表单提交的数据的方法还有：`getParameterNames()`、`getParameterMap()` 和 `getParameterValues()` 方法。


HTTPServletRequest 实现并扩展了 ServletRequest 接口。

如果你的 Servlet 是继承自 HttpServlet，那么 tomcat 传入 doGet 和 doPost 方法的就是 HttpServletRequest 对象，<small>而非 ServletRequest 对象。</small>

HttpServletRequest 扩展的常用方法有：

- Stirng getRequestURL( )
- Stirng getRequestURI( )
- Stirng getContextPath( )
- String getMethod( )
- Cookie[] getCookies( )
- HttpSession getSession( )

## ServletResponse 和 HTTPServletResponse

Tomcat 在调用 Servlet 的 `service()` 方法前，容器首先会创建一个 ServletResponse 对象，并将它作为第二个参数传给 `service()` 方法。

ServletResponse 隐藏了向浏览器发送响应的复杂过程。

在 ServletResponse 所有的方法中，最常用的方法之一是 `getWriter()` 方法，它返回一个可以向客户端发送文本的 `java.io.PrintWriter` 对象。默认情况下，PrintWriter 对象使用 ISO-8859-1 编码。

注意，有另一个向浏览器发送数据的方法叫 `getOutputStream()`，但这个方法是用于发送二进制数据的。因此大多数情况下使用的是 `getWriter()`，而非 `getOutPutStream()`。不要调用错了方法。

在向客户端发送响应时，大多数时候是将它作为 HTML 发送。在发送任何 HTML 标签前，应该先调用 `setContentType()` 方法，设置响应的内容类型，并将“text/html”作为参数传入。这是告诉浏览器，所发送给它的数据内容是 HTML 格式内容。

如果你的 Servlet 是继承自 HttpServlet，那么 tomcat 传入 doGet 和 doPost 方法的就是 HttpServletResponse 对象，<small>而非 ServletResponse 对象。</small>

HTTPServletResponse 实现并扩展了 ServletResponse 接口。

HTTPServletResponse 扩展的常用方法有：

- void addCookie ( Cookie cookie )
- void sendRedirect ( String location )


## ServletConfig 和 ServletContext

当容器初始化 Servlet 时，Servlet 容器会给 Servlet 的 `init()` 方法传入一个 ServletConfig 对象。ServletConfig 对象中封装这由 `@WebServlet` 注解或者 **部署描述符** 传给 Servlet 的配置信息。这样传入的每一条信息就叫做 **初始化参数**，一个初始化参数由 key 和 value 组成。

为了获得 Servlet 的初始化参数，可以从容器传给 Servlet 的 ServletConfig 对象中调用 `getInitParameter()` 方法来获得。

```java
@WebServlet(name="HelloServlet",
            urlPatterns = {"/hello.do"},
            initParams = {
                @WebInitParam(name="author", value="ben"),
                @WebInitParam(name="email", value = "hemiao3000@126.com")
})
```

ServletContext 代表着 WebbApp。每个 WebApp 只有一个 ServletContext 对象。

通过调用 ServletConfig 实例的 `getServletContext()` 方法，可以获得该 Servlet 所属的 WebApp 的 ServietContext 对象。

## 部署描述符

在 servlet3.0 之前，不支持注解的形式来配置 servlet，而是在 web.xml 中使用配置描述符。

```xml
<servlet>
    <servlet-name>HelloServlet</servlet-name>
    <servlet-class>HelloServlet</servlet-class>
    <init-param>
        <param-name>author</param-name>
        <param-value>ben</param-value>
    </init-param>
    <init-param>
        <param-name>email</param-name>
        <param-value>hemiao3000@126.com</param-value>
    </init-param>
</servlet>

<servlet-mapping>
    <servlet-name>HelloServlet</servlet-name>
    <url-pattern>/HelloWorld/hello.do</url-pattern>
</servlet-mapping>
```
