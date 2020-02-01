# Shiro 内置的 JdbcRealm

很显然将用户信息（特别是密码）存在 `.ini` 这样的文本文件中，也并非合适的做法。更常见也更合理的做法是将相关信息存在数据库中。

Shiro 内置的 JdbcRealm 就是自动从数据库中读取相关用户信息，并用以校验当前登录用户的密码、身份和权限。

从 JdbcRealm 的源码中可以看到其默认的数据库的表和表结构：

- String DEFAULT_AUTHENTICATION_QUERY =
  ```sql
  SELECT password FROM users WHERE username = ?
  ```

- String DEFAULT_SALTED_AUTHENTICATION_QUERY =
  ```sql
  SELECT password, password_salt FROM users WHERE username = ?
  ```

- String DEFAULT_USER_ROLES_QUERY = 
  ```sql
  SELECT role_name FROM user_roles WHERE username = ?
  ```

- String DEFAULT_PERMISSIONS_QUERY = 
  ```sql 
  SELECT permission FROM roles_permissions WHERE role_name = ?
  ```

> 从其默认的 SQL 查询语句来看，默认的表格的结构非常简单，有可能无法满足你的业务逻辑需求。

jdbcRealm 会用到数据库中的三张表：

- 用户表：users

| id  | username | password | password_salt |
| :-- | :------- | :------- | :------------ |
| xxx | xxx      | xxx      | xxx           |


- 角色表：roles

| id   | username | role_name |
| :--- | :------- | :-------- |
| xxx  | xxx      | xxx       |
  

- 角色权限表：roles_permissions

| id   | role_name | permission |
| :--- | :-------- | :--------- |
| xxx  | xxx       | xxx        |

  

```sql
SET FOREIGN_KEY_CHECKS = off;

-- 用户表
CREATE TABLE `users`
(
  `id`            bigint(20)   PRIMARY KEY AUTO_INCREMENT,
  `username`      varchar(100) UNIQUE DEFAULT NULL,
  `password`      varchar(100)        DEFAULT NULL,
  `password_salt` varchar(100)        DEFAULT NULL
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4;

INSERT INTO `users` VALUE (null, 'tommy', '123', null);
INSERT INTO `users` VALUE (null, 'jerry', '123', null);

-- 角色表
DROP TABLE IF EXISTS `user_roles`;
CREATE TABLE `user_roles`
(
  `id`        bigint(20) PRIMARY KEY AUTO_INCREMENT,
  `username`  varchar(100) DEFAULT NULL,
  `role_name` varchar(100) DEFAULT NULL,
  UNIQUE KEY (`username`, `role_name`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4;

INSERT INTO `user_roles` VALUE (NULL, 'tommy', 'admin');
INSERT INTO `user_roles` VALUE (NULL, 'tommy', 'user');
INSERT INTO `user_roles` VALUE (NULL, 'jerry', 'user');

-- 权限表
DROP TABLE IF EXISTS `roles_permissions`;
CREATE TABLE `roles_permissions`
(
  `id`         bigint(20) PRIMARY KEY AUTO_INCREMENT,
  `role_name`  varchar(100) DEFAULT NULL,
  `permission` varchar(100) DEFAULT NULL,
  UNIQUE KEY (`role_name`, `permission`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4;

INSERT INTO `roles_permissions` VALUE (NULL, 'admin', 'user:insert');
INSERT INTO `roles_permissions` VALUE (NULL, 'admin', 'user:delete');
INSERT INTO `roles_permissions` VALUE (NULL, 'admin', 'user:update');
INSERT INTO `roles_permissions` VALUE (NULL, 'user', 'user:query');

SET FOREIGN_KEY_CHECKS = on;
```

```java
private static DruidDataSource dataSource;
private JdbcRealm realm;

static {
    dataSource = new DruidDataSource();
    dataSource.setUrl("jdbc:mysql://localhost:3306/shiro?useUnicode=true&characterEncoding=utf-8&useSSL=false");
    dataSource.setUsername("root");
    dataSource.setPassword("123456");
}

@Before
public void before() {
    realm = new JdbcRealm();
    realm.setDataSource(dataSource);
    realm.setPermissionsLookupEnabled(true); // 注意此处
}

@Test
public void test() {
	// 和 SimpleAccountRealm 以及 IniRealm 中的测试代码一样
}
```

上面的测试代码中有一个小细节： `jdbcRealm.setPermissionsLookupEnabled(true);`

这是一个检测权限的开关，jdbcRealm 默认是关闭 check permission 功能的。如果需要调用 `subject.checkPermission()` 方法检测用户的权限的话，需要将这个功能开关打开。

## 自定义 SQL 语句

在上面的测试代码中，我们一直使用的是 JdbcRealm 中自带的默认的 SQL 语句查询用户的密码、身份和权限。如果你的数据库中的表因为某些原因无法满足 Shiro 的默认的要求，而是自建的，从而需要手动指定查询 SQL 语句时，让 Shiro 使用你所指定的 SQL 语句，而不是默认的 SQL 语句去查询用户相关的密码、橘色、权限。

这时需要为 jdbcRealm 手动指定查询语句：

```java
// 使用自定义的 sql 验证表中的用户
String sql = "select `password` from `test_users` where `user_name` = ?";

realm.setAuthenticationQuery(sql);
```

这里有个小矛盾：此时，可能就不会手动指定 SQL 语句，而是干脆直接自定 Realm，一切按照我们自己写的代码来。