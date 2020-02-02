<span class="title">基于 jjwt 的 jwt 工具类</span>

```java
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtBuilder;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Data
class User {
    private int id;
    private String username;
    private String password;

    public User(int id, String username, String password) {
        this.id = id;
        this.username = username;
        this.password = password;
    }
}

public class JwtUtil {

    /**
     * 用户登录成功后生成 Jwt
     * 使用 Hs256 算法  私匙使用用户密码
     *
     * @param ttlMillis jwt 过期时间
     * @param user      登录成功的user对象
     */
    public static String createJWT(long ttlMillis, User user) {
        // 指定签名的时候使用的签名算法，也就是 header 那部分，jjwt 已经将这部分内容封装好了。
        SignatureAlgorithm signatureAlgorithm = SignatureAlgorithm.HS256;

        //生成 JWT 的时间
        long nowMillis = System.currentTimeMillis();
        Date now = new Date(nowMillis);

        //创建 payload 的私有声明（根据特定的业务需要添加，如果要拿这个做验证，一般是需要和 jwt 的接收方提前沟通好验证方式的）
        Map<String, Object> claims = new HashMap<String, Object>();
        claims.put("id", user.getId());
        claims.put("username", user.getUsername());
        claims.put("password", user.getPassword());

        // 生成签名的时候使用的秘钥 secret,这个方法本地封装了的，一般可以从本地配置文件中读取，切记这个秘钥不能外露哦。
        // 它就是你服务端的私钥，在任何场景都不应该流露出去。
        // 一旦客户端得知这个 secret, 那就意味着客户端是可以自我签发 jwt 了。
        String key = user.getPassword();

        // 生成签发人
        String subject = user.getUsername();

        // 下面就是在为 payload 添加各种标准声明和私有声明了
        // 这里其实就是 new 一个 JwtBuilder，设置 jwt 的 body
        JwtBuilder builder = Jwts.builder()
                // 如果有私有声明，一定要先设置这个自己创建的私有的声明，这个是给 builder 的 claim 赋值，一旦写在标准的声明赋值之后，就是覆盖了那些标准的声明的
                .setClaims(claims)
                // 设置 jti(JWT ID)：是 JWT 的唯一标识，根据业务需要，这个可以设置为一个不重复的值，主要用来作为一次性 token，从而回避重放攻击。
                .setId(UUID.randomUUID().toString())
                // iat: jwt的签发时间
                .setIssuedAt(now)
                // 代表这个JWT的主体，即它的所有人，这个是一个json格式的字符串，可以存放什么userid，roldid之类的，作为什么用户的唯一标志。
                .setSubject(subject)
                // 设置签名使用的签名算法和签名使用的秘钥
                .signWith(signatureAlgorithm, key)
                // 这是额外/故意添加的一组自定义信息
                .claim("role", "admin");

        if (ttlMillis >= 0) {
            long expMillis = nowMillis + ttlMillis;
            Date exp = new Date(expMillis);
            // 设置过期时间
            builder.setExpiration(exp);
        }

        return builder.compact();
    }


    /**
     * Token 的解密
     * @param token 加密后的 token
     * @param user  用户的对象
     */
    public static Claims parseJWT(String token, User user) {
        // 签名秘钥，和生成的签名的秘钥一模一样
        String key = user.getPassword();

        // 得到 DefaultJwtParser
        Claims claims = Jwts.parser()
                // 设置签名的秘钥
                .setSigningKey(key)
                // 设置需要解析的jwt
                .parseClaimsJws(token)
                .getBody();

        return claims;
    }

    /**
     * 校验 token
     * 在这里可以使用官方的校验，我这里校验的是 token 中携带的密码于数据库一致的话就校验通过
     */
    public static Boolean isVerify(String token, User user) {
        // 签名秘钥，和生成的签名的秘钥一模一样
        String key = user.getPassword();

        // 得到 DefaultJwtParser
        Claims claims = Jwts.parser()
                // 设置签名的秘钥
                .setSigningKey(key)
                // 设置需要解析的jwt
                .parseClaimsJws(token).getBody();

        return claims.get("password").equals(user.getPassword());
    }

/*

public static void main( String[] args )
{
        String token = JwtUtil.createJWT(6 * 1000, new User(10, "tom", "123456"));

        System.out.println(token);

        // Claims 这个 claims 你就可以认为它是一个 map，里面是一个个 key-value
        Claims claims = JwtUtil.parseJWT(token, new User(10, "tom", "123456"));
        System.out.println("用户id：" + claims.getId());
        System.out.println("用户名称：" + claims.getSubject());
        System.out.println("登录时间：" + new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(claims.getIssuedAt()));
        System.out.println("过期时间：" + new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(claims.getExpiration()));
        System.out.println("用户角色：" + claims.get("role"));

}

*/

}
```