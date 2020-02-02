# Shiro 中的加密功能

从数据安全的角度来看，不应该将用户的密码以明文的形式存储于数据库中，以免数据的泄露，从而造成用户的损失。

通常数据库中存放的是用户密码的加密形式（甚至，其中还可以加盐 Salt）。

在自动以 Realm 的基础上加上密码加密功能：

```java
public class CustomRealm extends AuthorizingRealm {

    /* 因演示需要，简化代码，此处并未连接真实数据库。使用 Map 模拟数据库中的数据 */
    private static Map<String, String> userMap = new HashMap<String, String>(16);

    static {
        // 密码是123，通过main方法运算得到加密后的字符串。
        userMap.put("tom", "5caf72868c94f184650f43413092e82c");
    }

    public CustomRealm() {
        super.setName(CustomRealm.class.getName());
    }

    // 认证
    @Override
    protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken t) throws AuthenticationException {

        UsernamePasswordToken token = (UsernamePasswordToken) t;

        String username = token.getUsername();

        // 2、通过用户名到数据库中获取凭证信息
        String password = getPasswordByUsername(username);

        if (password == null)
            return null;

        // 注意，对于密码，字符串“123”和数字123是不同的密码。一定要注意。
        SimpleAuthenticationInfo authenticationInfo
                = new SimpleAuthenticationInfo("tom", password, CustomRealm.class.getName());

        // 在返回之前，要将盐值加进来
        authenticationInfo.setCredentialsSalt(ByteSource.Util.bytes("tom"));
        return authenticationInfo;
    }

    private String getPasswordByUsername(String username) {
        return userMap.get(username);
    }

    // 授权
    @Override
    protected AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection principals) {
        // 为演示，简化代码，授权逻辑略
        return null;
    }

    public static void main(String[] args) {
        // 第一个是原密码，第二个是加盐的值
        Md5Hash md5Hash = new Md5Hash("123","tom");
        System.out.println(md5Hash);
    }

}
```


测试代码：

```java
@Test
public void test() {

    /* “告知” Shiro 从自定义的 Realm 中获取 『标准答案』*/
    CustomRealm customRealm = new CustomRealm();
    DefaultSecurityManager defaultSecurityManager = new DefaultSecurityManager();
    defaultSecurityManager.setRealm(customRealm);

    /* 
     * “告知” Shiro 『标准答案』 中的密码使用 md5 加密算法加密过。
     * 让它在进行比对时，把用户传入的密码也用 md5 加密后，再进行比对。否则，是肯定不一样的。
     */
    HashedCredentialsMatcher matcher = new HashedCredentialsMatcher();
    matcher.setHashAlgorithmName("md5");

    // 加密操作可以反复执行。设置加密次数。
    matcher.setHashIterations(1);
    customRealm.setCredentialsMatcher(matcher);

    /* 让 Shiro 正式开始生效/工作 */
    SecurityUtils.setSecurityManager(defaultSecurityManager);

    /* 模拟用户登录 */
    Subject subject = SecurityUtils.getSubject();
    UsernamePasswordToken token = new UsernamePasswordToken("tom", "123");
    subject.login(token);

    // 判断用户是否登录成功
    logger.info("isAuthenticated: [{}]", subject.isAuthenticated());
}
```

『完』