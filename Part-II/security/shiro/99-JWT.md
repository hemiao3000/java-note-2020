# JWT（JSON Web Token）

Json web token （JWT），是为了在网络应用环境声明而执行的一种基于 JSON 的开放标准。

## Session 的弊病和 JWT 的起源

**http 协议本身是无状态的协议** 这是所有问题的起点和关键。在这个前提下，服务器为了弄明白这『次请求和上次请求是不是同一个人/客户端』发来的，在第一次发起请求时，服务器（Tomcat）会创建一个 Session 对象，以表示有人/客户端开始了一次会话，并将这个 Session 对象对应的 id（SessionID）返回给客户端浏览器。在后续的请求中，客户端再发出的请求都需要携带这个 SessionID，以表示当前请求和之前的请求是在同一个会话中。

传统的 seesion 认证存在的问题：

1. 用户请求规模大之后增加服务器内存开销；
2. 不利于服务端搭建集群。

当然，可以有一些其他的方案<small>（比如使用 Redis 实现 Session 共享）</small>来解决上述 Session 的两个问题，但是 JWT 则是完全提供了另一种不同的思路：服务端不负责存储用户信息 。

JWT 的认证流程：

1. 客户端发送用户名和密码至服务端进行认证。
2. 服务端认证通过后，生成一个具有唯一性标识的字符串（Token）
3. 服务端将 Token 发还给客户端，客户端后续的请求都需要带上这个 Token 
4. 服务端再次收到客户端请求时，认证这个 Token 是否是自己当初生成且未经篡改过的。如果没毛病，那么就认为该用户曾经通过了登陆认证的，本次请求该干嘛干嘛。

> JWT 和 SessionID 相比表面上看起来好像并没有多大区别，以前服务端回传的是叫 SessionID 的字符串，现在回传的是叫 Token 的字符串，但是它代表着『保存用户信息』这个责任从后端转移到了前端。

## JWT 的组成

一个 JWT 实际上就是一个字符串，它由三部分组成：头部、荷载 与 签名。

这个 JWT 的标准形式为 `头部.荷载.签名`

例如：

```
eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJ0b20iLCJleHAiOjE1NjY5MjA4MzIsImlhdCI6MTU2NjkxNzIzMiwianRpIjoiMGM4ODRhNzAtOTdkNy00MTczLTgyYzItMTcyNzA2ZmMyZDU4In0.IKO9rBpLz-u2m2gA1S2gR-8CFn1Z1qs-AZvW55A1SoY
```



### 头部（Header）

JWT 都有一个头部，头部用于描述关于该 JWT 的最基本的信息，例如其类型以及签名所用的算法等。这也可以被表示成一个 JSON 对象。

```js
{
  "typ": "JWT",
  "alg": "HS256"
}
```

它表示当前信息是一个 `JWT`，且是被 `HS256` 算法加密过的。

当然，一个 JWT 的头部信息真正的样子并不是上面这个样子，它会被 Base64 算法编码。

上述 JWT 的头部信息被编码后长成的是 `eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9` 这个样子。

### 荷载（Payload）部分

JWT 的荷载是 JWT 的最重要部分，它就代表着 JWT 的信息内容。JWT 的荷载部分和头部部分一样，其具体内容也是一个 JSON 格式字符串。例如：

```js
{
    "iss": "John Wu JWT",
    "iat": 1441593502,
    "exp": 1441594722,
    "aud": "www.example.com",
    "sub": "jrocket@example.com",
    "from_user": "B",
    "target_user": "A"
}
```

荷载部分中有些字段是 JWT 的标准字段（例如上例的前五个），除此之外，你可以向 JWT 中添加对你有用的任意的信息（例如上例的后两个）。

和头部部分一样，真正的荷载部分也是要经过 Base64 算法编码的。上例真正长的是这个样子：

```
eyJpc3MiOiJKb2huIFd1IEpXVCIsImlhdCI6MTQ0MTU5MzUwMiwiZXhwIjoxNDQxNTk0NzIyLCJhdWQiOiJ3d3cuZXhhbXBsZS5jb20iLCJzdWIiOiJqcm9ja2V0QGV4YW1wbGUuY29tIiwiZnJvbV91c2VyIjoiQiIsInRhcmdldF91c2VyIjoiQSJ9
```

### 签名（Signature）部分

签名部分是用前面的头部和荷载部分的内容生成的。

将上面的两个编码后的字符串都用句号 `.` 连接在一起（头部在前，荷载在后），再使用 `HS256` 算法进行加密。

之所以是 HS256 算法，是因为要与头部中所生成的加密算法呼应。

在加密过程中，需要提供一个秘钥（secret）， 例如，以 `mystar` 作为秘钥，上述的头部和荷载部分生成的签名就是：

```
rSWamyAYwuHCo7IFAgd1oRpSP7nzL7BF5t7ItqpKViM
```

最终，上例中的整个 JWT 的内容就是：

```
eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJmcm9tX3VzZXIiOiJCIiwidGFyZ2V0X3VzZXIiOiJBIn0.rSWamyAYwuHCo7IFAgd1oRpSP7nzL7BF5t7ItqpKViM
```

一旦服务端生成并发回这个 JWT 字符串之后，后续用户的请求就应该带上这个 JWT，以证明自己成功登陆过 ：

```
http://.../xxx.do?jwt=eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJmcm9tX3VzZXIiOiJCIiwidGFyZ2V0X3VzZXIiOiJBIn0.rSWamyAYwuHCo7IFAgd1oRpSP7nzL7BF5t7ItqpKViM
```

## JWT 的安全性和注意事项

JWT 的签名部分杜绝了 JWT 被篡改的可能。它从两点实现了这个功能：

1. 被篡改后的荷载部分加上头部后与签名部分对不上（因为签名部分使用之前没改过的内容生成的），这种情况下就是非法的 JWT
2. 前端不知道后端生成 JWT 时使用的是什么秘钥。理论上而言，无法重新生成新的签名部分。


在 JWT 中，不应该在载荷里面加入任何敏感的数据。在上面的例子中，我们传输的是用户的 User ID。这个值实际上不是什么敏感内容，一般情况下被知道也是安全的。

> 理论上来说，只要使用 HTTP 协议都有可能被人拦截/观测到你在网络上所发送的数据。这种情况下，使用什么方案都防不住信息的泄露。这也是现在越来越鼓励使用 https（http+ssl） 的原因。

## Java 操作 JWT

JWT 是一种理念/方案，它与具体的语言无关。Java 语言中有 `jjwt` 包来简化我们创建/解析 JWT 。

```xml
<dependency>
    <groupId>io.jsonwebtoken</groupId>
    <artifactId>jjwt</artifactId>
    <version>0.9.1</version>
</dependency>

<!-- jjwt 依赖于 java-jwt -->
<dependency>
    <groupId>com.auth0</groupId>
    <artifactId>java-jwt</artifactId>
    <version>3.4.0</version>
</dependency>
```

代码：

```java
import java.util.*;
import com.auth0.jwt.*;
import com.auth0.jwt.algorithms.Algorithm;
import io.jsonwebtoken.*;
import org.apache.commons.codec.binary.Base64;

import java.util.*;


public class JwtUtil {

    // 生成签名是所使用的秘钥
    private final String base64EncodedSecretKey;

    // 生成签名的时候所使用的加密算法
    private final SignatureAlgorithm signatureAlgorithm;

    public JwtUtil(String secretKey, SignatureAlgorithm signatureAlgorithm) {
        this.base64EncodedSecretKey = Base64.encodeBase64String(secretKey.getBytes());
        this.signatureAlgorithm = signatureAlgorithm;
    }

    /**
     * 生成 JWT Token 字符串
     *
     * @param iss       签发人名称
     * @param ttlMillis jwt 过期时间
     * @param claims    额外添加到荷部分的信息。
     *                  例如可以添加用户名、用户ID、用户（加密前的）密码等信息
     */
    public String encode(String iss, long ttlMillis, Map<String, Object> claims) {
        if (claims == null) {
            claims = new HashMap<>();
        }

        // 签发时间（iat）：荷载部分的标准字段之一
        long nowMillis = System.currentTimeMillis();
        Date now = new Date(nowMillis);

        // 下面就是在为payload添加各种标准声明和私有声明了
        JwtBuilder builder = Jwts.builder()
                // 荷载部分的非标准字段/附加字段，一般写在标准的字段之前。
                .setClaims(claims)
                // JWT ID（jti）：荷载部分的标准字段之一，JWT 的唯一性标识，虽不强求，但尽量确保其唯一性。
                .setId(UUID.randomUUID().toString())
                // 签发时间（iat）：荷载部分的标准字段之一，代表这个 JWT 的生成时间。
                .setIssuedAt(now)
                // 签发人（iss）：荷载部分的标准字段之一，代表这个 JWT 的所有者。通常是 username、userid 这样具有用户代表性的内容。
                .setSubject(iss)
                // 设置生成签名的算法和秘钥
                .signWith(signatureAlgorithm, base64EncodedSecretKey);

        if (ttlMillis >= 0) {
            long expMillis = nowMillis + ttlMillis;
            Date exp = new Date(expMillis);
            // 过期时间（exp）：荷载部分的标准字段之一，代表这个 JWT 的有效期。
            builder.setExpiration(exp);
        }

        return builder.compact();
    }


    /**
     * JWT Token 由 头部 荷载部 和 签名部 三部分组成。签名部分是由加密算法生成，无法反向解密。
     * 而 头部 和 荷载部分是由 Base64 编码算法生成，是可以反向反编码回原样的。
     * 这也是为什么不要在 JWT Token 中放敏感数据的原因。
     *
     * @param jwtToken 加密后的token
     * @return claims 返回荷载部分的键值对
     */
    public Claims decode(String jwtToken) {

        // 得到 DefaultJwtParser
        return Jwts.parser()
                // 设置签名的秘钥
                .setSigningKey(base64EncodedSecretKey)
                // 设置需要解析的 jwt
                .parseClaimsJws(jwtToken)
                .getBody();
    }


    /**
     * 校验 token
     * 在这里可以使用官方的校验，或，
     * 自定义校验规则，例如在 token 中携带密码，进行加密处理后和数据库中的加密密码比较。
     *
     * @param jwtToken 被校验的 jwt Token
     */
    public boolean isVerify(String jwtToken) {
        Algorithm algorithm = null;

        switch (signatureAlgorithm) {
            case HS256:
                algorithm = Algorithm.HMAC256(Base64.decodeBase64(base64EncodedSecretKey));
                break;
            default:
                throw new RuntimeException("不支持该算法");
        }

        JWTVerifier verifier = JWT.require(algorithm).build();
        verifier.verify(jwtToken);  // 校验不通过会抛出异常


        /*
            // 得到DefaultJwtParser
            Claims claims = decode(jwtToken);

            if (claims.get("password").equals(user.get("password"))) {
                return true;
            }
        */

        return true;
    }

    public static void main(String[] args) {
        JwtUtil util = new JwtUtil("tom", SignatureAlgorithm.HS256);

        Map<String, Object> map = new HashMap<>();
        map.put("username", "tom");
        map.put("password", "123456");
        map.put("age", 20);

        String jwtToken = util.encode("tom", 30000, map);

        System.out.println(jwtToken);
        /*
        util.isVerify(jwtToken);
        System.out.println("合法");
        */

        util.decode(jwtToken).entrySet().forEach((entry) -> {
            System.out.println(entry.getKey() + ": " + entry.getValue());
        });
    }
}
```

---

## JWT Token 使用技巧

1. 可以将用户的 Http 请求的 UserAgent 信息也放入 JWT 荷载部分。
  > 这样做的好处是在一定程度上 JWT Token 被盗用。一旦在另一个客户端使用同一个 token 发出 http 请求，http 请求的 UserAgent （大概率）会和前一个客户端不一样。

2. 服务端不需要考虑客户端将收到的 JWT Token 放哪。
  > Cookie 还是 local storage、session storage ？这是客户端（前端开发工程师、IOS/Andriod 客户端开发工程师）考虑的事情。

3. 前端向后端传递 JWT token 是，可以放在 http header 里，也可以拼在 url 里，也可以放 cookie 里。

