# 配置 Servlet 及其映射

不同版本的 Sevlet 的 web.xml 配置文件的头部信息是不一样的。<small>可以使用哪个版本的 Servlet 取决于你的 Tomcat 的版本。</small>

不建议使用 Servlet 3.0 和 3.0 以下版本，太过于老旧了。建议使用 3.1 和 4.0 版本。

Servlet 3.1 使用 tomcat8；Servlet 4.0 使用 tomcat9。


## <font color="#0088dd">1. 老式配置：web.xml 配置</font>

Servlet 3.0 以下版本，配置 Servlet 及其映射关系，只能在 web.xml 中配置。

语法如下：

```xml
<servlet>
    <servlet-name>字符串</servlet-name>
    <servlet-class>Servlet类的完全限定名</servlet-class>
</servlet>

<servlet-mapping>
    <servlet-name>字符串</servlet-name>
    <url-pattern>url匹配规则</url-pattern>
</servlet-mapping>
```

配置一个 Servlet 需要出现一对 `servlet` 和 `servlet-mapping`。简而言之，`servlet` 和 `servlet-mapping` 总是成对出现的。

配对的 `servlet` 和 `servelt-mapping` 中的 `servlet-name` **必须一样**。


## <font color="#0088dd">2. 新式配置：注解配置</font>

Servlet 3.0 开始支持注解配置。语法如下：

```java
@WebServlet(urlPatterns = "url匹配规则")
public class XxxServlet extends HttpServlet {
    ...
}
```

## <font color="#0088dd">3. URL 匹配规则</font>

### 首先需要明确几个容易混淆的规则：

- servlet 容器中的匹配规则既不是简单的通配，也不是正则表达式，而是特定的规则。所以不要用通配符或者正则表达式的匹配规则来看待servlet的url-pattern。
- Servlet 2.5 开始，一个 servlet 可以使用多个 url-pattern规则，`<servlet-mapping>` 标签声明了与该 servlet 相应的匹配规则，每个 `<url-pattern>` 标签代表 1 个匹配规则；
- 当 servlet 容器接收到浏览器发起的一个 url 请求后，容器会用 url 减去当前应用的上下文路径，以剩余的字符串作为 servlet 映射，假如 url 是 `http://localhost:8080/appDemo/index.html`，其应用上下文是 appDemo，容器会将 `http://localhost:8080/appDemo` 去掉，用剩下的 `/index.html` 部分拿来做servlet 的映射匹配
- url-pattern 映射匹配过程是**有优先顺序**的，而且当有一个 servlet 匹配成功以后，就不会去理会剩下的 servlet了。

### 精确匹配

精确匹配是优先级最高，最不会产生歧义的匹配。

```xml
<servlet-mapping>
    <servlet-name>...</servlet-name>
    <url-pattern>/user/users.html</url-pattern>
    <url-pattern>/index.html</url-pattern>
    <url-pattern>/user/addUser.action</url-pattern>
</servlet-mapping>
```

当在浏览器中输入如下几种 url 时，都会被匹配到该 servlet

```txt
http://localhost:8080/appDemo/user/users.html
http://localhost:8080/appDemo/index.html
http://localhost:8080/appDemo/user/addUser.action
```

注意：

`http://localhost:8080/appDemo/user/addUser/`（最后有斜杠符）是非法的 url，不会被当作 `http://localhost:8081/appDemo/user/addUser`（最后没有斜杠府）识别。

另外上述 url 后面可以跟任意的查询条件，都会被匹配，如

`http://localhost:8080/appDemo/user/addUser?username=Tom&age=23` 会被匹配。

### 路径匹配

路径匹配的优先级仅次于精确匹配。

以 `/` 字符开头，并以 `/*` 结尾的字符串都表示是路径匹配。

```xml
<servlet-mapping>
    <servlet-name>...</servlet-name>
    <url-pattern>/user/*</url-pattern>
</servlet-mapping>
```

上述规则表示 URL 以 `/user` 开始，后面的路径可以任意。比如下面的 url 都会被匹配。

```txt
http://localhost:8080/appDemo/user/users.html
http://localhost:8080/appDemo/user/addUser.action
http://localhost:8080/appDemo/user/updateUser.do
```

### 扩展名匹配

也叫 **后缀匹配**。

以 `*.` 开头的字符串被用于扩展名匹配

```xml
<servlet-mapping>
    <servlet-name>...</servlet-name>
    <url-pattern>*.jsp</url-pattern>
    <url-pattern>*.action</url-pattern>
</servlet-mapping>
```

则任何扩展名为 jsp 或 action 的 url 请求都会匹配，比如下面的url都会被匹配

```txt
http://localhost:8080/appDemo/user/users.jsp
http://localhost:8080/appDemo/toHome.action
```

### 缺省匹配

缺省匹配也是“兜底”的匹配，一个 url 不符合精确匹配、路径匹配、扩展品匹配的任何一种情况，那么它所触发的 Servlet 就是由缺省匹配决定。

```xml
<servlet-mapping>
    <servlet-name>...</servlet-name>
    <url-pattern>/</url-pattern>
</servlet-mapping>
```

### 注意事项 1：匹配规则不能混用

匹配规则不是正则表达式规则，不要想当然的使用通配符：精确匹配、路径匹配、后缀匹配 三者 **不能混用** 。

- 要么以 `/` 开头，并以 `/*` 结尾，表示路径匹配。
- 要么以 `*.` 开头，表示后缀匹配。
- 要么就是精确匹配。

例如：

- `<url-pattern>/user/*.action</url-pattern>` 是非法的

另外：

- `<url-pattern>/aa/*/bb</url-pattern>`是合法的。是精确匹配，合法，


### 注意事项 2：`/*` 和 `/` 含义并不相同

`/*` 优先级极高；`/` 优先级极低。

`/*` 属于路径匹配，并且可以匹配所有 request 。

由于路径匹配的优先级仅次于精确匹配，所以 `/*` 会覆盖所有的后缀匹配规则（<small>包括 *.jsp</small>），很多 404 错误均由此引起。

除非是真的需要，否则不要使用 `/*`！<br>
除非是真的需要，否则不要使用 `/*`！<br>
除非是真的需要，否则不要使用 `/*`！

`/*` 和 `/` 均会拦截静态资源的加载，需要特别注意。
