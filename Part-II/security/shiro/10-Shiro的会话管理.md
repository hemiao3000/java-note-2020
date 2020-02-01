# Shiro 的会话管理

## 一、会话管理的基本概念

Shiro 的『野心』很大，它的自我定位并非是一个『Web 安全框架』，而是一个『安全框架』。<small>（当然，Shiro 有点想多了，大家实际上还是只把它当做 『Web 安全框架』）</small>。

Shiro 提供了完整的会话管理功能，即便是对于 JavaSE 项目，仍然可以像 Web 项目一样有会话功能

所谓 **会话**，即用户访问应用时保持的连接关系，在多次交互中应用能够识别出当前访问的用户是谁，且可以在多次交互中保存一些数据。<small>类似于 Web 项目中的 `sesstion.setAttribute()` 功能。</small>

```java
Subject currentUser = SecurityUtils.getSubject();

Session session = currentUser.getSession();
session.setAttribute("someKey", someValue);
```

Shiro 提供了三个默认实现：

- **DefaultSessionManager**：

  `DefaultSecurityManager` 使用的默认实现，用于 JavaSE 环境；

- **ServletContainerSessionManager**：
  
  `DefaultWebSecurityManager` 使用的默认实现，用于 Web 环境，其直接使用 Servlet 容器的会话；
  
  在这种情况下，你存到 Shiro 的 Session 中的东西，实际上就是存到了 HttpSession 中。

- **DefaultWebSessionManager**
  
  用于 Web 环境的实现，可以替代 `ServletContainerSessionManager`，表示（程序员）自己维护着会话。<small>常用于集群 Web 环境（因为要共享 Session）。</small>
  
  这种情况下，你存到 Shiro 的 Session 中的东西，具体存到了哪里，你自己写代码决定/控制。

在 Web 应用程序中，Shiro 其实是通过 **ServletContainerSessionManager** 获取到并借用容器创建的 HttpSession 。也就是说，`Subject.login()` 登录成功后用户的认证信息实际上是保存在 HttpSession 中的。

以 `.ini` 配置为例：

```properties
[main]  
sessionManager=org.apache.shiro.web.session.mgt.ServletContainerSessionManager  
securityManager.sessionManager=$sessionManager
```

## Session 超时

SessionManager 的默认超时时间是 30 分钟。如果你对此不满意，可以通过 SessionManager 的 `globalSessionTimeout` 属性进行调整。

以 `.ini` 配置为例

```properties
[main]
# 3,600,000 milliseconds = 1 hour
securityManager.sessionManager.globalSessionTimeout = 3600000
...
```

很明显 `globalSessionTimeout` 设置的是全局性的 Session 超时，如果针对某一个 Session 你对它的超时时长有特殊要求，你可以通过对 Session 对象的 timeout 属性赋值，来指定它的超时时长，而不受全局配置的影响。

```java
Subject currentUser = SecurityUtils.getSubject();
Session session = subject.getSession();
session.setTimeout(3000);   // 单位微秒
```

Session 超时后，再使用 subject，Shiro 会抛出 ExpiredSessionException 异常。

另外，在 Web 项目中，Shiro 使用 `ServletContainerSessionManager` 进行会话管理时，而 Shiro Session 又是直接利用 Http Session，因此它的超时设置依赖于底层 Servlet 容器的超时时间，即 `web.xml` 中配置其会话的超时时间（分钟为单位）：

web.xml
```xml
<session-config>  
  <session-timeout>30</session-timeout>  
</session-config>
```

## Session 数据存储

（以 Web 项目的 Session 管理为例），之所以我们对 Shiro Session 的操作（新增、更新、删除数据）会同步到 HttpServlet Session，是因为这中间有一个叫 `SessionDAO` 的部件在起作用。

SessionDAO 的作用就是决定 Shiro Session 中的数据的真正的去处。

HttpServlet Session 只是可选的去处之一。你完全可以自定义一个 SessionDAO 类，在这个类中，将 Shiro Session 中的数据同步到 Redis 甚至是 MySQL 中。如果是这样的话，你从 Shiro Session 中存取数据，实际上就是在 Redis、MySQL 中存取数据。

> 我们这里介绍 SessionDAO 的目的不是为了使用它来实现更复杂的功能，而是为了禁用它。现在流行的分布式的方案（前后端分离的 Restful 方案）中，已经不会（甚至是禁止）在服务端创建 Session，以实现『无状态』化。

以 `.ini` 配置为例，禁用 SessionDAO 的配置如下：

```properties
securityManager.subjectDAO.sessionStorageEvaluator.sessionStorageEnabled = false
```

禁用掉 SessionDAO 功能之后，你在 Shiro Session 中存放的数据，就没有人帮你『送』到 HttpSession、Redis、MySQL 等地方进行存储。

需要注意的是，此时你只是禁用了 SessionDAO 功能，而不是整个 SessionManager 功能，如果一旦你在代码中调用 `subject.getSession()` 或 `subject.getSession(true)` 这样的代码，Shiro 仍然会在你的内存中创建 Shiro Session 对象。不过考虑到在无状态的服务中，我们逻辑上不会用到这样的代码，所以也无须担心这些 Shiro Session 对象的创建。

如果想彻底禁掉 Shiro Session，那么需要自己实现 DefaultWebSubjectFactory 的子类，例如：`StatelessDefaultSubjectFactory`。这样，由该 Factory 创建的 Subject 不支持  getSession() 方法。

```java
public class StatelessDefaultSubjectFactory extends DefaultWebSubjectFactory {  
  public Subject createSubject(SubjectContext context) {  
    // 不创建 session  
    context.setSessionCreationEnabled(false);  
    return super.createSubject(context);  
  }  
}
```

```xml
<bean id="securityManager" class="org.apache.shiro.mgt.DefaultSecurityManager">
    <property name="realm">
      <bean class="..." />
    </property>
    <!-- 注意:不存储session -->
    <property name="subjectDAO.sessionStorageEvaluator.sessionStorageEnabled" value="false"/>
    <property name="sessionManager">
      <bean class="org.apache.shiro.session.mgt.DefaultSessionManager">
        <property name="sessionValidationSchedulerEnabled" value="false" />
      </bean>
    </property>
    <property name="subjectFactory">
      <!-- 不生产会话 -->
      <bean class="xxx.xxx.StatelessDefaultSubjectFactory" />
    </property>
</bean>
```

这样再在程序中获得的 Subject 对象调用 `getSession()` 方法时，Shiro 会直接抛出异常。

『完』