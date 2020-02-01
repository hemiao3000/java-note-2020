# JSP

Servlet 有两个缺点无法克服：

- 写在 Servlet 中的所有 HTML 标签必须包含 Java 字符串，这使得处理 HTTP 响应报文的工作十分繁琐。
- 所有的文本和 HTML 标记是硬编码的，这导致即使是表现层的微小变化，也需要重新编译代码。

JSP 技术解决了上述两个问题。

## <font color="#0088dd">1. JSP 概述</font>

JSP 页面本质上是一个 Servlet。当一个 JSP 页面第一次被请求时，容器（Tomcat）会将 JSP 页面转换成 Servlet 代码，如果转换成功，容器随后编译该 Servlet 类，并实例化该 Servlet 的对象，之后就是 Servlet 代码执行正常流程。

```xml
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="java.util.Date" %>
<%@ page import="java.text.DateFormat" %>
<html>
<head>
    <title>Title</title>
</head>
<body>

<%
    DateFormat dateFormat = DateFormat.getDateInstance(DateFormat.LONG);
    String s = dateFormat.format(new Date());
    out.println("Today is " + s);
%>

</body>
</html>
```

在 JSP 页面中可以使用 Java 代码来生成动态页面。Java 代码可以出现在 JSP 页面中的任何位置，并通过 `<%` 和 `%>` 包括起来。其次可以使用 `page` 指令的 `import` 属性导入在 JSP 页面中使用的 Java 类型。

<% ... %> 块被称为 **scriplet** 。

在 JSP 页面中，可以使用 HTML 注释，也可以使用 JSP 特有的注释，它以 `<%--` 开始，以 `--%>` 结束。

## <font color="#0088dd">2. 隐式对象</font>

对于容器传入给 Servlet 的对象，在 JSP 中，可以通过 **隐式对象** 来访问。


| JSP隐式对象 | 类型 |
| :---------- | :-----------------  |
| request     | HttpServletRequest  |
| response    | HttpservletResponse |
| out         | JspWriter           |
| session     | HttpSession         |
| application | ServletContext      |
| config      | ServletConfig       |
| pageContext | PageContext         |
| page        | HttpJspPage         |
| exception   | Throwable           |


pageContext 它提供了有用的上下文信息，并通过它的方法可以获得用各种 Servlet 对象。如：`getRequest()`、`getResponse()`、`getServletContext()`、`getServletConfig()` 和 `getSession()`。

此外，pageContext 中提供了另一组有趣/有用的方法：用于获取和设置**属性**的方法，即 `getAttribute()` 和 `setAttribute()` 方法。属性值可以被存储于4个范围之一：页面（Page）、请求（Request）、会话（Session）和应用程序（Application）。

隐式对象 `out` 引用了一个 JspWriter 对象，它相当于 Servlet 中通过 `response.getWriter()` 方法获得的 PrintWriter 对象。


## <font color="#0088dd">3. 指令</font>

指令是JSP语法元素的第一种类型。它们指示JSP如何翻译为Servlet。JSP 2.2中定义了多个指令，其中以 page 最为重要。

***page 指令的语法如下*** ：

`<%@ page 属性1="值1" 属性2="值2" ... %>`


@ 和 page 之间的空格不是必须的。page 指令的常用属性如下：

| 指令 | 说明 | 默认值 |
| :- | :- | :- |
| import       | 定义一个或多个页面中将被导入和使用的 java 类型。<br>如果需要同时导入多个 java 类型时，用 “,” 分隔。| 默认值：无 |
| session      | 指定本页面是否参与一个 HTTP 会话，<br>即，当前页面中 session 对象是否可用。 | 默认值：`true` |
| pageEncoding | 指定本页面的字符编码| 默认值是：`ISO-8859-1` |
| contentType  | 定义隐式对象 response 的内容内省| 默认值：`text/html` |
| errorPage    | 定义当发生错误时用来处理错误/展现错误信息的页面| 默认值：无|
| isErrorPage  | 标识本页面是否是一个处理错误/展现错误信息的页面| 默认值：false |
| isELIgnored  | 配置是否在本页面中略 EL 表达式 | 默认值："fasle"  |
| language     | 定义本页面中的脚本语言类型。 | 默认值：java，这也是 JSP 2.2 中唯一的选项。|

## <font color="#0088dd">4. 脚本元素</font>

一个脚本程序是一个 Java 代码块，以 `<%` 开始， 以 `%>` 结束。

随着技术的发展，在 JSP 页面中嵌入大段的 Java 代码表现出了很大的局限性，随之而来的替代方案是 **表达式** 和 **动作** 。

每个 **表达式** 都会被容器执行，并使用隐式对象 out 的打印方法输出结果。

表达式以 `<%=` 开始，以 `%>` 结束。`注意`，表达式必须无分号结尾。

在JSP页面中，可以 **声明** 能在JSP页面中使用的变量和方法。声明以 `<%!` 开始，并以 `%>` 结束。

在JSP页面中，一个生命可以出现在任何地方，并且一个页面可以有多个声明。

## <font color="#0088dd">5. 动作</font>

**动作** 的目的是简化常见 scriplet 代码，将其“浓缩”成一个标签。它是早期用于分离表示层和业务层的手段，但随着其他技术的法阵（EL表达式），现在很少使用 **动作** 了。

`<jsp:useBean>` 本质上是创建一个Java对象，“变量名”为其id属性的值，变量类型为其class属性的值。该变量在当前JSP页面可用。

`<jsp:setProperty>` 和 `<jsp:getProperty>` 本质上就是调用已知对象的get/set方法，以为其设置属性值。被设置的变量由其name属性值决定，被设置的属性由其property属性决定。另外，`<jsp:setProperty>` 还需要靠value属性值来决定设置的值。

`<jsp:include page="">` 动作用来动态地引用另一个资源，“另一个资源”可以是另一个 JSP 页面，或一个静态的 HTML 页面。

通过其子元素 `<jsp:param name="" value="">` 可以在引入另一个JSP页面时，向其传参。

`<jsp:forward page="">` 将当前页转向其他资源。类似于`<jsp:include>`，通过其子元素 `<jsp:param>`可以向转向的页面传参。
