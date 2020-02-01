# Shiro 内置的 IniRealm

IniRealm 是 Shiro 内置的一个 Realm，这种模式中，将用户名、密码、角色、权限编写在一个 `.ini` 文件中，Shiro 查找该文件并从中读取相关信息用以校验当前登录用户的身份和权限。

当然，`.ini` 文件有固定的格式上的语法规则。

在项目中建立 resources 目录，并创建 `user.ini` 文件。一个典型的简单 ini 配置文件内容类似如下：

```properties
# 登录用户名，密码，及其角色
[users]
tommy=123,admin,user
jerry=123,user

# 角色所具有的的权限
[roles]
admin=user:insert,user:delete,user:update
user=user:query
```

结合配置文件，创建-配置 SecurityManager 的典型代码如下：

```java
private IniRealm realm; 

@Before
public void before() {
    Ini ini = new Ini();
    ini.loadFromPath("classpath:user.ini");
    realm = new IniRealm(ini);
}

@Test
public void test() {
	// 和 SimpleAccoutRealm 中的测试代码一样
}
```

> 注意：`.ini` 文件中还可以有更多方面的相关配置，但是由于我们项目中并非使用 IniRealm，所以此处不作更多介绍和验证。

