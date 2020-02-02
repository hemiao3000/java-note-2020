# Shiro 和 SpringMVC 整合

## 一、整合

```xml
<shiro.version>1.4.0</shiro.version>
```

```xml
<dependency>
    <groupId>org.apache.shiro</groupId>
    <artifactId>shiro-core</artifactId>
    <version>${shiro.version}</version>
</dependency>
<dependency>
    <groupId>org.apache.shiro</groupId>
    <artifactId>shiro-web</artifactId>
    <version>${shiro.version}</version>
</dependency>
<dependency>
    <groupId>org.apache.shiro</groupId>
    <artifactId>shiro-spring</artifactId>
    <version>${shiro.version}</version>
</dependency>
```

**web.xml**

```xml
<?xml version="1.0" encoding="UTF-8"?>
<web-app xmlns="http://java.sun.com/xml/ns/javaee" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
    xsi:schemaLocation="http://java.sun.com/xml/ns/javaee
        http://java.sun.com/xml/ns/javaee/web-app_3_0.xsd"
    version="3.0">

    ...

    <listener>
        <listener-class>org.springframework.web.context.ContextLoaderListener</listener-class>
    </listener>

    <context-param>
        <param-name>contextConfigLocation</param-name>
        <param-value>
            classpath:spring/spring-shiro.xml,
            classpath:spring/spring-dao.xml,
            classpath:spring/spring-service.xml
        </param-value>
    </context-param>

    <!-- filter 过滤器，用以拦截请求进行身份和权限校验 -->
    <filter>
        <filter-name>shiroFilter</filter-name>
        <filter-class>org.springframework.web.filter.DelegatingFilterProxy</filter-class>
        <init-param>
            <param-name>targetFilterLifecycle</param-name>
            <param-value>true</param-value>
        </init-param>
    </filter>
    <filter-mapping>
        <filter-name>shiroFilter</filter-name>
        <url-pattern>*.do</url-pattern>
    </filter-mapping>

    ...

</web-app>
```

**spring-shiro.xml**

> <small>由于要求某些单例对象要先于另一些单例对象创建，因此，shiro 的某些配置要第一次加载（单独写在 spring-shiro.xml 中），而另一些配置（注解配置的相关配置）又要求和 Spring MVC 的配置放在一起（写在 spring-web.xml 中）。</small>

```xml
<?xml version="1.0" encoding="UTF-8"?>
<beans xmlns="http://www.springframework.org/schema/beans"
    xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
    xsi:schemaLocation="
    http://www.springframework.org/schema/beans
    http://www.springframework.org/schema/beans/spring-beans.xsd">

    <!-- Shiro 核心 Bean -->
    <bean id="securityManager" class="org.apache.shiro.web.mgt.DefaultWebSecurityManager">
        <property name="realm" ref="myShiroRealm"/>
    </bean>

    <!-- Shiro web 拦截器配置 -->
    <bean id="shiroFilter" class="org.apache.shiro.spring.web.ShiroFilterFactoryBean">
        <property name="securityManager" ref="securityManager" />
        <property name="loginUrl" value="login.jsp" /> <!-- 这些都是位于 context-path 之后的 url 路径（不一定是 jsp 路径）。可以不用指定 / -->
        <property name="unauthorizedUrl" value="403.jsp" />
        <!-- 定义过滤器链，从上向下进行匹配 -->
        <property name="filterChainDefinitions">
            <value>
                /login.jsp = anon
                /login.do = anon
                /logout.do = logout
                /* = authc
            </value>
        </property>
    </bean>

    <!-- 自定义 Realm -->
    <bean id="myShiroRealm" class="web.shiro.CustomRealm">
        <property name="credentialsMatcher" ref="credentialsMatcher"/>
    </bean>

    <!-- 如果没有使用到 Shiro 的加密功能，则不需要这个 bean -->
    <bean id="credentialsMatcher" class="org.apache.shiro.authc.credential.HashedCredentialsMatcher">
        <property name="hashAlgorithmName" value="md5" />
        <property name="hashIterations" value="1" />    <!-- 设置加密次数 -->
    </bean>

    <!-- 截止目前为止，配置中还未开启 Shiro 注解功能。因此，暂未能使用 Shiro 注解功能，只能通过 XML 进行 URL 配置。 -->

</beans>
```



## 注意事项一：

`web.xml` 配置中，Filter 要求要比 Servlet 先加载。因此，上述配置文件中所配置的 shiroFilter 必须要在 SSM 项目的第一次加载配置文件时机中加载。

如果是在第二次加载时机加载该配置文件，那么，会出现 `No bean named 'shiroFilter' is defined` 错误。

## 整合后的用户身份校验

为 `/login.do` 提供一个 Controller，并在 Congroller 中手动调用 `subject.login()` 再根据结果执行页面跳转： 

```java
@Slf4j
@Controller
public class LoginController {

    @ExceptionHandler({Exception.class })
    public String exception(Exception e) {
        if (e instanceof IncorrectCredentialsException) {
            log.warn("密码错误", e);
        }
        else {
            log.warn("其它错误", e);
        }

        return "redirect:failure.jsp";
    }

    @RequestMapping("/login.do")
    public String login(String username, String password) {

        Subject subject = SecurityUtils.getSubject();
        UsernamePasswordToken token = new UsernamePasswordToken(username, password);

        subject.login(token);

        log.info("登录成功");
        return "redirect:success.jsp";
    }
}
```

## 整合后的用户权限校验（通过配置文件）

```xml
<bean id="shiroFilter" class="org.apache.shiro.spring.web.ShiroFilterFactoryBean">
    <property name="securityManager" ref="securityManager" />
    <property name="loginUrl" value="login.jsp" />
    <property name="unauthorizedUrl" value="403.jsp" />
    <property name="filterChainDefinitions">
        <value>
            /login.jsp = anon
            /login.do = anon
            /logout.do = logout
            /role-admin.do = roles["admin"]
            /role-user.do = roles["user"]
            /opr-1.do = perms["user:add"]
            /opr-2.do = perms["user:delete"]
            /* = authc
        </value>
    </property>
</bean>
```

## 整合后启用注解校验用户权限

```xml
<!-- 管理 Shiro bean生命周期 -->
<bean id="lifecycleBeanPostProcessor" class="org.apache.shiro.spring.LifecycleBeanPostProcessor"/>

<bean class="org.springframework.aop.framework.autoproxy.DefaultAdvisorAutoProxyCreator" depends-on="lifecycleBeanPostProcessor">
	<property name="proxyTargetClass" value="true"/>
</bean>

<bean class="org.apache.shiro.spring.security.interceptor.AuthorizationAttributeSourceAdvisor">
    <property name="securityManager" ref="securityManager"/>
</bean>
```



## 注意事项二：

在使用 Shiro 注解的时候，与 Shiro 注解有关的 3 个 Bean（也就是上面 3 个）的配置，必须和 Spring MVC 的相关配置写在一起，即写在我们的 `spring-web.xml` 文件中。否则会出现 Shiro 注解不生效的显现。



## 注意事项三：

Shiro 的注解功能使用到了 Spring 的 AOP，而 Spring AOP 的底层实现有两种方案：`JDK 动态代理` 和 `CGLIB 字节码增强` 。

Shiro 默认/优先使用的是 JDK 动态代理。如果一个项目中，每个类最多用生成动态一个代理类，那么 Everything is ok 。但是，一旦不同的框架，不同的业务都需要为同一个类生成自己所需的代理类，那么 JDK 动态代理方案会出现  `$Proxy40` 这样的异常。

因此，通常（不仅仅是 Shiro 中），我们常常会直接设置要求（无论什么情况都）直接使用 `CGLIB 字节码增强` 方案来实现 Spring 的 AOP 功能，而不是默认的 JDK 动态代理。

上面配置 `DefaultAdvisorAutoProxyCreator` Bean 时，设定它的 `proxyTargetClass` 属性，就是为了实现这个目的。





