# 基本概念

- pom.xml

  ```xml
  <!-- 非 Shiro 必须，仅用于以简单的默认的格式打印日志 -->
  <dependency>
    <groupId>org.slf4j</groupId>
    <artifactId>slf4j-simple</artifactId>
    <version>1.7.25</version>
  </dependency>

  <dependency>
    <groupId>org.apache.shiro</groupId>
    <artifactId>shiro-core</artifactId>
    <version>1.4.0</version>
  </dependency>

  <dependency>
    <groupId>junit</groupId>
    <artifactId>junit</artifactId>
    <version>4.12</version>
    <scope>test</scope>
  </dependency>
  ```

## 一、启用 Shiro

使用 Shiro 的核心组件在于：**SecurityManager** 。

- 注意：
  > 它是 `org.apache.shiro.mgt.SecurityManager`，而非 `java.lang.SecurityManager`，如果发现项目中 SecurityManager 无缘无故报错，确认前面是否 import 了正确的包 。

在每一个使用到 Shiro 的项目中，必须存在 **SecurityManager** 对象，创建并配置 SecurityManager 对象，是启用 Shiro 的第一步。

Shiro 提供了多种内置的方式来创建-配置 Shiro 对象（而且还提供了灵活的自定义创建-配置方式）。其中最简便（也是最不可能在实际中使用）的方法是『硬编码』方式。

```java
public class AuthenticationTest {

  private Logger log = LoggerFactory.getLogger(AuthenticationTest.class);

  // 定义一个 realm
  private SimpleAccountRealm simpleAccountRealm = new SimpleAccountRealm();

  @Before
  public void addUser() {
    simpleAccountRealm.addAccount("tommy", "123", "admin", "user");
  }

  @Test
  public void testAuthentication() {
    // 1. 构建 SecurityManager 环境
    DefaultSecurityManager defaultSecurityManager = new DefaultSecurityManager();
    // “告知” Shiro 在 simpelAccoutnRealm 中找『标准答案』
    defaultSecurityManager.setRealm(simpleAccountRealm);

    // 启用 Shiro
    SecurityUtils.setSecurityManager(defaultSecurityManager);

    // 2. 主体提交认证请求
    // 导入 shiro 的 org.apache.shiro.subject.Subject;
    Subject subject = SecurityUtils.getSubject();

    // token 是用户要认证的用户数据
    UsernamePasswordToken token = new UsernamePasswordToken("tommy", "123");

    // shiro 提供是否认证的方法
    log.info("{}", subject.isAuthenticated() ? "登陆过" : "未登录");

    // 登入
    subject.login(token);

    log.info("{}", subject.isAuthenticated() ? "登陆过" : "未登录");

    log.info("isAuthenticated: [{}]", subject.isAuthenticated());

    // 退出
    subject.logout();

    log.info("isAuthenticated: [{}]", subject.isAuthenticated());
  }

  @Test
  public void testAuthorization() {
    // 1.构建 SecurityManager 环境
    DefaultSecurityManager defaultSecurityManager = new DefaultSecurityManager();
    defaultSecurityManager.setRealm(simpleAccountRealm);

    // 2.主体提交认证请求
    SecurityUtils.setSecurityManager(defaultSecurityManager);
    // 导入shiro的org.apache.shiro.subject.Subject;
    Subject subject = SecurityUtils.getSubject();

    UsernamePasswordToken token = new UsernamePasswordToken("tommy", "123", "admin", "user");

    log.info("{}", subject.isAuthenticated() ? "已登陆" : "未登录");

    subject.login(token);

    log.info("{}", subject.isAuthenticated() ? "已登陆" : "未登录");


    log.info("{} 'admin' 角色", subject.hasRole("admin") ? "有" : "没有");
    log.info("{} 'user' 角色", subject.hasRole("user") ? "有" : "没有");
  }
}
```

