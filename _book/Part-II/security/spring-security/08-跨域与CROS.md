# 跨域与CORS

## 跨域问题原由

### 什么是 ajax 跨域问题？
  
  简单来说，就是前台调用后台接口的时候，由于 <font color="#0088dd">**浏览器**</font> 的同源策略（Same-origin Policy）过于严格，如果这个接口不是同一个域的，就会产生跨域问题。

### 什么是同源请求，什么是跨域请求？

所谓同源是指：域名、协议、端口相同。

如果发出去的请求不是本域的，**协议**、**域名**、**端口**，任何一个不一样，浏览器就认为是跨域的。

| URL | 说明 | 是否跨域 |
| :- | :- | :- |
| http://www.a.com/a.js <br> http://www.a.com/b.js | 同一域名 | 没有跨域 |
| http://www.a.com/a/a.js <br> http://www.a.com/b/b.js | 同一域名下的不同文件夹 | 没有跨域 |
| http://www.a.com/a.js <br> http://www.a.com`:8080`/b.js | 端口不同 | 跨域 |
| `http:`//www.a.com/a.js <br> `https:`//www.a.com/b.js | 协议不同 | 跨域 |
| http://`www.a.com`/a.js <br> http://`70.32.97.74`/b.js | 域名和域名的 IP| 跨域 |
| http://`www`.a.com/a.js <br> http://`xxx`.a.com/a.js <br> http://`yyy`.a.com/b.js | 二级域名不同 | 跨域（此时，cookie 也不会共享）|
| http://`www.a.com`/a.js <br> http://`www.b.com`/b.js | 域名不同 | 跨域 |

发送的是 xhr（XMLHTTPRequest）请求才会产生跨域问题。如果发出去的请求不是 XHR 请求的话，即使跨域，浏览器也不会报错。

## CORS 之前的跨域问题的解决办法

### 方案一：关闭浏览器的跨域限制功能

这是最简单粗暴，也是最不可能采用的方案。

### 方案二：JSONP（JSON with Padding）

JSONP 方案是一个早期方案，也是一个非标准方案。

它的解题思路是，既然只有发送的是 xhr（XMLHTTPRequest）请求才会有可能产生跨域问题<small>（请求被浏览器拦截/阻止）</small>，那么我们就想办法 <font color="#0088dd">**把 XHR 请求改为非 XHR 请求**</font> 。只要不是 XHR 请求，浏览器就不会报跨域预警，就会放行请求。

由于该方案是一个早期的非标准方案，且存在若干缺点，因此慢慢被后续的 `CORS` 所取代。此处不作讲解。

## 现行的标准的方案：CORS

CORS<small>（Cross-origin resource sharing，跨域资源共享）</small>是一个 W3C 标准，定义了在必须访问跨域资源时，浏览器与服务器应该如何沟通。

简单来说，就是浏览器『一刀切式地阻止一切跨域请求』过于的粗暴，因此，CORS 的方案就是 <font color="#0088dd">**让服务器决定是否响应这个跨域请求**</font>。服务器不响应，浏览器就阻止；服务器愿意响应，浏览器就放行，展示响应结果。

> <small>整个 CORS 通信过程，都是浏览器自动完成<small>（基本上目前所有的浏览器都实现了 CORS 标准）</small>，不需要用户参与。对于开发者来说，CORS 通信与同源的 AJAX 通信没有差别，代码完全一样。浏览器一旦发现 AJAX 请求跨源，就会自动添加一些附加的头信息，有时还会多出一次附加的请求，但用户不会有感觉。</small>

浏览器首先会发起一个请求方法为 *`OPTIONS`* 的 <font color="#0088dd">**预检请求**</font>，用于询问/确认浏服务器是否允许跨域请求，只有在得到服务器的许可后才会发出实际请求。

## Spring Boot 对 CORS 的支持

### 环境准备

首先是支持 Restful 的 Controller，这里就不使用数据库了，简单一点。 

- UserController

  ```java
  @RestController
  public class UserController {

    /**
     * 查询用户列表
     */
    @GetMapping(value = "/users")
    public ResponseEntity<Map<String, Object>> getUserList() {
      Map<String, Object> map = new HashMap<>();
      List<User> userList = new ArrayList<>();
      userList.add(new User(1, "tommy", 20));
      userList.add(new User(2, "jerry", 19));
      map.put("result", userList);
      map.put("status", 200);

      return ResponseEntity.ok(map);
    }
  }
  ```

  记得将 `/users` 路径的权限放开，以免在验证时造成不必要的干扰：

  ```java
  .antMatchers("/users").permitAll()
  ```

- User

  ```java
  public class User {
	private int id;
	private String username;
	private int age;

    // getter / setter
  }
  ```

- 页面

  不是使用 IDEA 的用户，自己找个 tomcat 启动 test.html 页面，修改端口为其他，然后启动 springboot 。

  ```html
  <!DOCTYPE html>
  <html lang="en"> 
    <head> 
      <meta charset="UTF-8"> 
      <title>Title</title> 
      <script src="https://cdn.bootcss.com/jquery/2.1.0/jquery.min.js"></script> 
      <script type="text/javascript">
        function crosRequest(){
          $.ajax({
            url:'http://localhost:8080/users',
            type: 'get',
            dataType: 'json',
            success: function(data){
              console.log(data);
            }
          });
        }
      </script> 
    </head> 
    <body> 
      <button onclick="crosRequest()">请求跨域资源</button> 
    </body> 
  </html>
  ```

<font color="red">**注意**</font>：无论是那种请求记得都要在配置开中将支持 CORS 的开关属性打开:

```java
http.cors();
```

### CORS 配置方案一：@CrossOrigin 注解


在请求处理方法，或者是在 Controller 类上标注 `@CrossOrigin` 注解，表示本方法，或本类中的所有方法接收来自『远方』的跨域请求。

```java
@CrossOrigin
@GetMapping(value = "/users")
public ResponseEntity<Map<String, Object>> getUserList() {
    ...
}
```

`@CrossOrigin` 注解的属性的默认值是：

```
"Access-Control-Allow-Origin" : "*"
"Access-Control-Allow-Methods" : "GET,POST,PUT,OPTIONS"
"Access-Control-Allow-Credentials" : "true"
```

如果有需要的话，你可以通过注解的属性进行手动指定。例如：

```java
@CrossOrigin(
    origins = {"", ""}, 
    methods = {RequestMethod.GET, RequestMethod.POST}, 
    allowCredentials = "true")
```

### 方案二：全局 CORS 配置

```java
@Bean 
public WebMvcConfigurer corsConfigurer() {
  return new WebMvcConfigurer() {
    @Override
    public void addCorsMappings(CorsRegistry registry) {
      registry.addMapping("/**")
        .allowedOrigins("http://domain.com", "http://domain2.com", "http://localhost:63342")
        .allowedMethods("GET", "POST", "DELETE", "PUT", "OPTIONS")
        .allowCredentials(false);
    }
  };
}
```


『完』
