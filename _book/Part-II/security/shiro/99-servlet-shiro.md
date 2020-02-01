# Shiro 整合 Java Web（Servlet）

```properties
[main]
authc.loginUrl=/login.jsp
roles.unauthorizedUrl=/unauthorized.jsp
perms.unauthorizedUrl=/unauthorized.jsp
logout.redirectUrl=/login.jsp

# 数据库连接池配置
dataSource = ...
dataSource.driverClass = ...
dataSource.jdbcUrl = ...
dataSource.user = ...
dataSource.password = ...

credentialsMatcher = org.apache.shiro.authc.credential.HashedCredentialsMatcher
credentialsMatcher.hashAlgorithmName = md5
credentialsMatcher.hashIterations = 1
credentialsMatcher.storedCredentialsHexEncoded = true

# 数据库访问配置
jdbcRealm = ...
jdbcRealm.permissionsLookupEnabled = ...
jdbcRealm.dataSource = ...
jdbcRealm.saltStyle = COLUMN

jdbcRealm.authenticationQuery = ...
jdbcRealm.userRolesQuery = ...
jdbcRealm.permissionsQuery = ...

jdbcRealm.credentialsMatcher = $credentialsMatcher

# 启用 jdbcRealm
securityManager.realms=$jdbcRealm


# 定义请求的地址需要做什么验证
[urls]
# 请求login的时候不需要权限，游客身份即可(anon)
/login=anon
# 请求/admin的时候，需要身份认证(authc)。如果当前用户未登陆/认证过，页面会跳转至登陆页面。
/admin=authc
# 请求/student的时候，需要角色认证，必须是拥有teacher角色的用户才行
/student=roles[teacher]
# 请求/teacher的时候，需要权限认证，必须是拥有user:create权限的角色的用户才行
/teacher=perms["user:create"]
```

```properties
/admin* = authc 表示可以匹配零个或者多个字符，如 /admin，/admin1，/admin123，但是不能匹配 /admin/abc 这种
/admin/** = authc 表示可以匹配零个或者多个路径，如 /admin，/admin/ad/adfdf 等。
/admin*/** = authc 上面两者的结合
```

<big>**web.xml**</big>

```xml
<!-- 添加shiro支持 -->
<listener>
    <!-- 依赖于 shiro-web 包 -->
    <listener-class>org.apache.shiro.web.env.EnvironmentLoaderListener</listener-class>
</listener>

<filter>
    <filter-name>ShiroFilter</filter-name>
    <filter-class>org.apache.shiro.web.servlet.ShiroFilter</filter-class>
    <init-param>
        <param-name>configPath</param-name>
        <param-value>/WEB-INF/shiro.ini</param-value>
    </init-param>
</filter>

<filter-mapping>
    <filter-name>ShiroFilter</filter-name>
    <url-pattern>/*</url-pattern>
</filter-mapping>
```

```java
@WebServlet("/login")
public class LoginServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        process(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        process(req, resp);
    }

    private void process(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        System.out.println("login process");
        String username = request.getParameter("username");
        String password = request.getParameter("password");

        Subject currentUser = SecurityUtils.getSubject();
        UsernamePasswordToken token = new UsernamePasswordToken(username, password);
        try {
            currentUser.login(token);
            System.out.println("认证成功");
            request.getSession().setAttribute("username", username);
            request.getRequestDispatcher("/success.jsp").forward(request, response);
        } catch (AuthenticationException e) {
            System.out.println("认证失败");
            request.getRequestDispatcher("/login.jsp").forward(request, response);
        }
    }

}
```

```java
@WebServlet("/student")
public class TeacherRoleServlet extends HttpServlet{

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        process(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        process(req, resp);
    }

    private void process(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        System.out.println("TeacherRolesServlet");

        Subject currentUser = SecurityUtils.getSubject();

        if(currentUser.hasRole("teacher")) {
            System.out.println("拥有 teacher 权限");
            request.getRequestDispatcher("/success.jsp").forward(request, response);
        } else {
            request.getRequestDispatcher("/unauthorized.jsp").forward(request, response);
        }
    }

}
```

```java
@WebServlet("/teacher")
public class TeacherPermsServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        process(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        process(req, resp);
    }

    private void process(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        Subject currentUser = SecurityUtils.getSubject();

        // 其实是不用判断了，因为只要进来了，角色肯定是对的，否则进不来
        // 判断当前用户是否具有teacher角色
        if (currentUser.isPermitted("user:create"))
            request.getRequestDispatcher("/success.jsp").forward(request, response);
        else
            request.getRequestDispatcher("/unauthorized.jsp").forward(request, response);
    }
}
```
