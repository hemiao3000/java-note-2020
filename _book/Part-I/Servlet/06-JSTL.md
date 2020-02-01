<span class="title">JSTL 标签库</span>

JSP 标准标签库<small>（JSTL）</small>是一个定制标签库的集合，它的出现是为了实现呈现层与业务层的分离功能。使用 JSTL<small>（结合EL表达式）</small>在绝大多数情况下，JSP 页面中不再需要“嵌入”Java 代码<small>（scriplet）</small>。

<font color="#0088dd">**使用 JSTL 需要额外导入 jstl 库。**</font>

根据 JSTL 标签所提供的功能，可以将其分为 5 个类别：核心<small>（Core）</small>标签、格式化标签、SQL 标签、XML 标签、JSTL 函数。其中以核心<small>（Core）</small>标签最为常用。

使用不同类别的 JSTL 库，需要在 JSP 页面的头文件中做出相应的“声明”。例如：

```jsp
<%@ taglib prefix="c"  uri="http://java.sun.com/jsp/jstl/core" %>
```

如果忘记引入 jstl 库，上述声明会报错。

# `c:out`

`<c:out>` 标签用来显示一个表达式的结果，与 `<%= ... %>` 作用相似，它们的区别就是 `<c:out>` 标签可以直接通过 "." 操作符来访问属性。

out 的语法有两种形式：

#### 形式一：

```jsp
<c:out value = "VALUE" [escapeXml = {true | false}] [default = "默认值"] />
```

#### 形式二：

```jsp
<c:out value = "VALUE" [escapeXml = {true | false}]>
    默认内容
</c:out>
```

其中 value 属性是必要部分。

# `c:set`

set 标签常见 2 种形式/作用：

- 第一种用于创建一个有界变量，并用 value 属性在其中定义一个要创建的字符串或者现存的有界对象：

```jsp
<c:set value="VALUE"
       var="VAR NAME"
       [scope="{ page | request | session | application }"]/>
```

- 第二种形式是设置有界变量的属性。

例如：

```jsp
<%
    request.setAttribute("username", "tom");
    session.setAttribute("", "");
    application.setAttribute("", "");
%>

<c:set var="password" value="123" scope="request"></c:set>
<c:set var="key" value="value" scope="session"></c:set>
<c:set var="key" value="value" scope="application"></c:set>

${requestScope.username}, ${requestScope.password}
```

```jsp
<c:set target="TARGET"
       property="PROPERTY NAME"
       value="VALUE"/>
```

注意，这种形式中，target 属性中 **必须使用一个 EL 表达式** 来引用这个有界变量。

例如：

```jsp
<%
    Department dept = new Department(10, "Testing","BeiJing");
    request.setAttribute("dept", dept);
%>

<c:set target="${requestScope.dept}" property="dname" value="System"></c:set>

<p> ${requestScope.dept.deptno} </p>
<p> ${requestScope.dept.dname} </p>
<p> ${requestScope.dept.loc} </p>
```

# `c:remove`

remove 标签用于删除有界变量。

```xml
<c:remove var = "VAR NAME"
          [scope="{ page | request | session | application }"] />
```

# `c:if`

if 标签是对某一个条件进行测试，假如结果为 true，就处理它的 body content 。另外，测试的结果可以保存在一个 Boolean 对象中，并创建有界变量来引用这个 Boolean 对象。

if 的语法有两种形式。第一种是没有 body content：

```xml
<c:if test="bool 型 EL 表达式"
      [var="VAR NAME"]
      [scope="{ page | request | session | application }"]/>
```

第二种形式使用了一个 body content：

```jsp
<c:if test="bool 型 EL 表达式" [var="变量名"] [scope="{ page | request | session | applicationi }"]>
    body content
</c:if>
```

# `c:choose`、`c:when` 和 `c:otherwise`

choose-when-otherwise 标签的作用与 Java 中的 switch-case-default 类似。

choose 标签中必须嵌有一个或多个 when 标签，并且每个 when 标签都有一种可以计算和处理的情况。otherwise 标签则用于默认的条件块。

choose 和 otherwise 标签没有属性。when 标签必须带有定义的测试条件 test 属性，来决定是否应该处理 body content 。

```jsp
<c:choose>
    <c:when test="${boolean 表达式}"> ... </c:when>
    <c:when test="${boolean 表达式}"> ... </c:when>
    ...
    <c:otherwise> ... </c:otherwise>
</c:choose>
```

# `c:forEach`

forEach 标签会无数次反复便利 body content 或者对象集合。

forEach 标签的语法有两种。第一种形式是固定次数地重返 body content：

```xml
<c:forEach [var="VAR NAME"] begin="BEGIN" end="END" step="STEP">
    body content
</c:forEach>
```

这种形式与集合对象无关。类似于 Java 代码中的 `for (int i = 0; i < 10; i++)`

例如：

```jsp
<c:forEach var="item" begin="1" end="5" step="2">
    <p>hello world ${item} </p>
</c:forEach>
```

第二种形式用于遍历对象集合。类似于 Java 代码中的 `for (String string : list)`

```jsp
<c:forEach items="COLLECTIONS" [var="变量名"] [varStatus="变量名"]>
    body content
</c:forEach>
```

对于每一次遍历，forEach 标签都将创建一个有界变量，变量名通过 var 属性定义，可在 body content 中使用。 该有界变量只能在 body content 部分使用。

forEach 标签有一个类型为 javax.servlet.jsp.jstl.core.LoopTagStatus 的变量 varStatus，这个变量有一个 count 属性，其中记录了当循环遍历的次数，该数值从1开始。

例如：

```xml
<c:forEach items="${requestScope.depts}" var="dept" varStatus="loop">
    <p>第 ${loop.count} 个：${dept.deptno}, ${dept.dname}, ${dept.loc}</p>
</c:forEach>
```

# fmt 进行日期格式化

引入申明：

```jsp
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
```

```xml
<fmt:formatDate value="<%=new Date()%>" type="date" pattern="yyyy-MM-dd"%/>
<fmt:formatDate value="${date}" type="date" pattern="yyyy-MM-dd"//>
```