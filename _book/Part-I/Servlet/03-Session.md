# HttpSession

一个用户有且最多有一个 HttpSession，并且不会被其他用户访问到。HttpSession 对象在用户第一次访问网站时自动被创建，你可以通过调用 HttpServeltRequest 的 `getSession()` 方法获得该对象。

会话（Session）是一个比请求（Request）更“大”的概念。一个会话中可以包含一个或多个请求；一个请求必定是在某个会话中。

`getSession()` 方法会返回当前的 HttpSession，若当前没有，则创建一个返回。

可以通过 HttpSession 的 `setAttribute()` 方法将值放入 HttpSesion 中。

> 注意：<small>
> - 放到 HttpSesion 中的值不仅限于 String 类型，可以是任意实现了 java.io.Serializable 接口的 java 对象。<br>
> - 其实，你也可以将不支持序列化的对象放入 HttpSession，只不过这样做会有隐患。</small>

调用 `setAttribute()` 方法时，若传入的 name 参数此前已经使用过，则会用新值覆盖旧值。通过调用 HttpSession 的 `getAttribute()` 方法可以取回之前放入的对象。

所有保存在 HttpSession 的数据不会发送到客户端。容器为每个 HttpSession 生成唯一的表示，并将该标识发送给客户端，或创建一个名为 **JSESSIONID** 的 cookie，或者在 URL 后附加一个名为 jsessionid 的参数。在后续的请求总，浏览器会将该标识发送给客户端，这样服务器就可以识别该请求是由哪个用户发起的（**这个过程无须开发人员介入**）。

默认情况下，HttpSession 会在用户不活动一段时间之后自动过期，该时间由 web.xml 中的 `session-timeout` 元素配置，单位为分钟（如果不设置，则过期时间由容器自行决定）。此外，HttpSession还定义可一个 `invalidate()` 方法强制会话立即过期失效。

```xml
<session-config>
    <session-timeout>2</session-timeout>
</session-config>
```
