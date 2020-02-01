# Cookies

Cookie 本质上是一个小文件，由浏览器创建/管理，保存在浏览器本地，即用户自己的电脑上。

当你访问一个网站/网址时，浏览器会“帮”你将这个文件的内容发送至服务端（Tomcat）。这个小文件的内容都是“名值（name-value）对”。只有浏览器本地有这个网站/网址的相关Cookie（小文件），浏览器 **一定** 会把它的内容“帮”你发送到服务端。这个过程无需程序员参与，不受程序员的控制。

浏览器“帮”你发送的Cookie，可能不止一个。服务端获得浏览器发送来的所有Cookie的方法是通过 request 对象的 `getCookies()`。

Cookie（小文件）是由浏览器在本地创建，但是，它是由服务端“通知/要求”浏览器去创建时，才会创建的。

浏览器通常支持每个网站20个cookies。

`注意`，cookie 中不能直接存放中文，所以需要做相应的处理。常见处理办法是使用 URLEncoder 和 URLDecoder 将中文字符串编码/解码成URL格式编码字符串。

可以通过传递 name 和 value 两个参数给Cookie类的构造函数来创建一个 cookie。在创建完 cookie 之后，可以设置它的 `maxAge` 属性，这个属性决定了cookie 何时过期（单位为秒）。

要将 cookie 发送到浏览器，需要调用 HttpServletResponse 的 `add()` 方法。

服务器若要读取浏览器提交的 cookie，可以通过  HttpServletRequest 接口的 `getCookie()` 方法，该方法返回一个 Cookie 数组，若没有 cookies 则返回 null 。你需要遍历整个数组来查询某个特定名称的 cookie 。

注意，并没有一个直接的方法来删除一个 cookie，你只能创建一个同名的 cookie，并将 `maxAge` 设置为0，并添加到 HttpServletResponse 中来“覆盖”原来的那个cookie。

Cookie 最大的问题在于用户可以通过设置禁用浏览器的 cookie 功能。