# 自定义 Realm

『认证』和『授权』是 Shiro 要进行的两大操作：

- 认证，是判断用户的身份是否合法。
  - 类比于，判断一个人是否是本公司员工，他能否进公司办公大楼。
- 授权，是判断用户所能做哪些操作。
  - 类比于，判断一个人（本公司员工）的身份，他能进办公大楼的哪些层，哪些个办公区。

在 Shiro 的使用过程中，Shiro 需要从某处获得『标准答案』，以便于来验证当前用户的身份和权限。Shiro 获取标准答案的来源就是 Realm 。

以 IniRealm 为例，『标准答案』在 `.ini` 文件中，Realm 就是去获取这个『标准答案』的途径，Shiro 就是通过 IniRealm 从 .ini 文件中获取有关用户认证和授权的『标准答案』。

我们的应用程序中要做的就是自定义一个 Realm 类，继承 `AuthorizingRealm` 抽象类，并重写其中两个必要方法：


| 方法 | 说明 |
| :- | :- |
| `doGetAuthenticationInfo()` | 认证用户，判断用户身份的合法性 |
| `doGetAuthorizationInfo()` | 授权用户，判断用户的使用权限 |

- Shiro 进行『认证校验』时，以 Realm 的 `doGetAuthenticationInfo()` 方法的返回值作为『标准答案』，以校验当前用户是否是合法用户。
- Shiro 进行『授权校验』时，以 Realm 的 `doGetAuthorizationInfo()` 方法的返回值作为『标准答案』，以校验当前用户是否有权限执行当前操作。

## 一、doGetAuthenticationInfo 方法

`doGetAuthenticationInfo()` 方法的功能是：<big>以传入的 `用户名` 为“线索”，查询/获取到该用户的密码的 **标准答案**，而后将标准答案交给 Shiro，让 Shiro 去比较实际密码和标准答案的异同。</big>

```java
@Override
protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken token) throws AuthenticationException {

    String username = (String) token.getPrincipal();

    if (username == null) {
        throw new UnknownAccountException();	// 没找到帐号
    }

    // 这里的密码“123”应该是从数据库中查询出来的结果
    SimpleAuthenticationInfo authenticationInfo = new SimpleAuthenticationInfo(username, "123", getName());

    return authenticationInfo;
}
```

1. 判断用户传入的用户名不为null
2. 判断用户传入的用户名确实是存在的
3. 查询用户名『配套』的密码
4. 将用这套户名-密码组成一个『标准答案』返回给Shiro，让 Shiro 去和用户实际输入的密码匹配。





## 二、doGetAuthorizationInfo 方法

同理，doGetAuthorizationInfo 方法的功能是：<big>以传入的 `用户名` 为“线索”，查询获取该用户的权限的 **标准答案**，而后将标准答案交给 Shiro，让 Shiro 去判断该用户是否具有权限执行当前操作。</big>

```java
@Override
protected AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection principals) {

    Set<String> roles = new HashSet<String>();
    roles.add("admin");
    roles.add("user");

    Set<String> permissions = new HashSet<String>();
    permissions.add("insert");
    permissions.add("delete");
    permissions.add("update");
    permissions.add("select");

    SimpleAuthorizationInfo info = new SimpleAuthorizationInfo();
    info.setRoles(roles);
    info.setStringPermissions(permissions);

    return info;

/*
    // 标准代码
    String username = (String) principals.getPrimaryPrincipal();
    username = MoreObjects.firstNonNull(username, Strings.EMPTY);

    ShiroUser user = userMapper.selectByUsername(username);

    // 从数据库或者缓存中获取角色数据
    Set<String> roleNames= user.getRoleNameSet();

    // 从数据库或者缓存中获取权限数据
    Set<String> permissionNames = user.getPermissionNameSet();

    SimpleAuthorizationInfo authorizationInfo = new SimpleAuthorizationInfo();
    authorizationInfo.setRoles(roleNames);
    authorizationInfo.setStringPermissions(permissionNames);

    log.info("[用户 [{}] 的角色是 [{}]", username, roleNames);
    log.info("[用户 [{}] 的权限是 [{}]", username, permissionNames);

    return authorizationInfo;
*/
}
```

在实际项目中，这些 **标准答案** 必然是来源于数据库中，而非在代码中写死的！

『完』