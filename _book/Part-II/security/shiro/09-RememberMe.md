# RememberMe

## 一、user 过滤器和 Remember Me 功能

Shiro 的内置拦截器中有一个名为 `user` 的过滤器，它的功能和 `authc` 有点类似，拦截用户请求，要求该用户必须是 “已登录 / 经过认证” 的用户<small>，否则不予放行</small>。

不过 user 拦截器的『拦截规则/安全级别』比 authc 低。

- `authc` 过滤器本质上，是从 Session 中获取用户名和密码。也就意味着，在本次会话中，在本次请求之前，一定有一个登录请求，从表单提交用户名和密码。因为，只有这样，Shiro 才会将用户名和密码存在 Session 中。

- `user` 过滤器本质上，是从请求附带的 Cookie 中获取用户名和密码。而 Cookie 中的用户名和密码，可能是一天前、一个星期前、一个月前，甚至一年前登录时（在服务端的要求下）留下的用户名和密码。

简而言之，通过 “记住密码 / Cookie“ 功能实现的自动登录，会被 `authc` 拦下来，要求重新登录（明确要求手输用户名密码）；而 `user` 过滤器器则会放行，显示首页，或登录成功页面。

开启 Shiro 的 Remember Me 功能后，Shiro 在登录认证（`subject.login()`）成功后，会要求客户端浏览器创建 Cookie 并记下类似如下内容：

```
key: rememberMe
value: 6gYvaCGZaDXt1c0xwriXj/Uvz6g8OMT3VSaAK4WL0Fvqvkcm0nf3CfTwkWWTT4EjeSS/EoQjRfCPv4WKUXezQDvoNwVgFMtsLIeYMAfTd17ey5BrZQMxW+xU1lBSDoEM1yOy/i11ENh6eXjmYeQFv0yGbhchGdJWzk5W3MxJjv2SljlW4dkGxOSsol3mucoShzmcQ4VqiDjTcbVfZ7mxSHF/0M1JnXRphi8meDaIm9IwM4Hilgjmai+yzdVHFVDDHv/vsU/fZmjb+2tJnBiZ+jrDhl2Elt4qBDKxUKT05cDtXaUZWYQmP1bet2EqTfE8eiofa1+FO3iSTJmEocRLDLPWKSJ26bUWA8wUl/QdpH07Ymq1W0ho8EIdFhOsELxM66oMcj7a/8LVzypJXAXZdMFaNe8cBSN2dXpv4PwiktCs3J9P9vP4XrmYees5x27UmXNqYFk86xQhRjFdJsw5A9ctDKXzPYvJmWFouo3qT5hugX0uxWALCfWg8MHJnG9w7QgVKM8oy3Xy4Ut8lSvYlA==
```

这里的 value 的值是用户登录成功后，对代表用户身份信息的 Principal 对象的序列化后再 Base64 的结果。以后再发起登录请求时，Shiro 会检查用户的登录请求参数，在没有相关参数的情况下，Shiro 如果发现请求附带的 Cookie 中有 rememberMe 信息，并且拦截权限是 `user`，那么 Shiro 会放行，显示登录成功页面，或首页。


> <small>从这里可以看出，Shiro 认为严格意义上的登录必须是输入用户名密码登录，而 rememberMe 实现的自动登不等同于已经登陆了，这样不安全。</small>

我们一般设置路径拦截是这样设置的：

```properties
/** = authc
```

## 二、RememberMe 的简单实现

只需去设置 UsernamePasswordToken 的 RememberMe 属性值为 true，表示启用该功能即可：

```java
Subject currentUser = SecurityUtils.getSubject();
UsernamePasswordToken token = new UsernamePasswordToken(username, password);
token.setRememberMe(true);	// 这句话的背后，Shiro 会要求客户端浏览器创建 RememberMe Cookie
try {
    currentUser.login(token);
} catch (AuthenticationException e) {
	 System.out.println("认证失败");
}
```

这种情况下，Shiro 会去『通知』客户端浏览器创建 Cookie 记录该用户的认证信息。至此以后的请求中，都会附带有这个 Cookie 信息。


不过，一般情况下，不会在代码中“写死”启用 RememberMe 功能。一般情况下，是否启用它，取决于用户是否选中页面上的记住密码 checkbox 。

当启用 Shiro 的 RememberMe 功能后，Shiro 会自动将用户名和密码加密后保存在客户端浏览器的 Cookie 中，并在后续的请求中检查请求附带的 Cookie 中是否携带用户名和加密后的密码，如果携带有，下次访问时，直接认为用户登陆成功。

默认情况下，该 Cookie 文件的过期时间为一年。

调用 Shiro 的 **`logout()`** 方法, 在实现原有的退出功能（改变 Subject 的状态）之外，Shiro 会再去『通知』客户端浏览器删除那个名为 rememberMe 的 Cookie 文件。 <small>（直到再一次调用 shiro 的 `login()` 方法，再次创建这个 Cookie 文件，记录登录用户的信息）</small>。

调用 **setRememberMe(false)**，表示禁用 RememberMe 功能，会使得 Shiro 去“通知”客户端浏览器删除名为 rememberMe 的 cookie，Shiro 在调用 login 时，也不会创建这个文件了。

## 三、Spring MVC 整合并配置 RememberMe 功能

RememberMe 默认的一年的过期时间很显然是不太合适的，因此通常会在 Shiro 与 SpringMVC 整合时重新设置 Cookie 的过期时效。

```xml
<!--Shiro 核心对象-->
<bean id="securityManager" class="...">
    ...
    <property name="rememberMeManager" ref="rememberMeManager"/>
...
</bean>

<!-- rememberMe 管理器，被 securityManager 使用/依赖 -->
<bean id="rememberMeManager" class="org.apache.shiro.web.mgt.CookieRememberMeManager">
    <property name="cipherKey" value="#{T(org.apache.shiro.codec.Base64).decode('4AvVhmFLUs0KTA3Kprsdag==')}" />
    <property name="cookie" ref="rememberMeCookie"/>
</bean>

<!-- 手动指定 cookie，被 rememberMemanager 使用/依赖-->
<bean id="rememberMeCookie" class="org.apache.shiro.web.servlet.SimpleCookie">
    <constructor-arg value="rememberMe"/>
    <property name="maxAge" value="604800"/> <!-- 7天 -->
</bean>
```

## 四、看起来很美

自动登录功能有个致命的安全缺陷<small>（与 Shiro 无关）</small>就是随便谁把这个 Cookie 值拿到别的浏览器都可以登录。就算你用再厉害的加密，都无法防止表单伪造。

所以自动登录功能仍然只能使用在查看一些无关紧要的信息的功能上。
