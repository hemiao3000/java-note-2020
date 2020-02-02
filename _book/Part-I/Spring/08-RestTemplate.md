# RestTemplate

SpringRestTemplate 是 Spring 提供的用于访问 Rest 服务的客端。 RestTemplate 提供了多种便捷访问远程 HTTP 服务的方法，能够大大提高客户端的编写效率，所以很多客户端比如 Android 或者第三方服务商都是使用 RestTemplate 请求 restful 服务。

| method | content-type | body | SpringMVC 参数注解 |
| :-- | :- | :- | :- |
| GET | none | none | @RequestParam |
| POST | x-www-form-urlencoded | username=tom&age=20 | @RequestParam |
| PUT | json | { username:tom, age:20 } | @RequestBody |
| DELETE | none | none | @PathVariable |

另外，GET 如果像 DELETE 一样将参数作为 URL 的一部分，那么也是用 @PathVariable 。

<font color="red">**有一点需要注意：**</font>

[参见 Spring MVC 中 PUT、PATCH 请求参数](/Restful/03-springmvc-restful.md#SpringMVC中PUT、PATCH请求参数)

使用 Spring Boot 实现 Server 端时，Spring Boot 自动配置了使用 *`HttpPutFormContentFilter`* 解决 PUT 请求获取不到参数问题。 因此，RestTemplate 发出 PUT 请求时，content-type 反而要设置成 `x-www-form-urlencoded` 这种『好似错误』的情况。


## API 方法介绍

常见方法有：

| 请求类型 | API	| 说明 |
| :- | :- | :- |
| GET     | `getForEntity()` | 返回的 ResponseEntity 包含了响应体所映射成的对象 |
| GET     | `getForObject()`	| 返回的请求体将映射为一个对象 |
| POST    | `postForEntity()`	| 返回包含一个对象的 ResponseEntity，这个对象是从响应体中映射得到的 |
| POST    | `postForObject()`	| 返回根据响应体匹配形成的对象 |
| DELETE  | `delete()`	| 对资源执行 HTTP DELETE 操作 |
| PUT     | `put()`	| PUT 资源到特定的 URL |
| any     | `exchange()`	| 返回包含对象的 ResponseEntity，这个对象是从响应体中映射得到的 |
| any     | `execute()`	| 返回一个从响应体映射得到的对象 |


GET / POST / DELETE / PUT 都有专门的方法发出对应方式的请求。`exchange()` 方法一个能打四个，它能够以统一的方式发出任意一种方法。<small>另外，这些方法的底层方式是 `execute()` 方法，不过该方法的使用有些麻烦。因此，统一使用 `exchange()` 就够了。</small>

```java
exchange(String url, HttpMeghod method, HttpEntity requestEntity, 
         Class responseType, Object... uriVariables);

exchange(String url, HttpMethod method, HttpEntity requestEntity, 
         Class responseType, Map uriVariables);

exchange(String url, HttpMethod method, HttpEntity requestEntity, 
         ParameterizedTypeReference responseType, Object... uriVariables);

exchange(String url, HttpMethod method, HttpEntity requestEntity, 
         ParameterizedTypeReference responseType, Map uriVariables);
```

- <font color="#0088dd">**参数 url**</font>：向哪个 url 发起请求。
- <font color="#0088dd">**参数 method**</font>：发起哪种请求。
- <font color="#0088dd">**参数 requestEntity**</font>：用以封装请求头（header）和请求体（body）的对象。
- <font color="#0088dd">**参数 responseType**</font>：指定返回响应中的 body 的数据类型。
- <font color="#0088dd">**返回值 ResponseEntity**</font>：其中封装的响应数据。包括了几个重要的元素，如响应码、contentType、contentLength、响应消息体等。在输出结果中我们能够看到这些与 HTTP 协议有关的数据。


## 发起 GET 请求

回顾一下 GET 请求的特点：

- GET 请求不用『管』header 中的 content-type 的值。 
- GET 请求的参数是『追加』到 URL 中，而不是附带在请求的 body 部分的。

因此，<small>如果没有其他的设置请求头的要求，</small>GET 请求在调用 `exchange()` 方法时，不需要 HttpEntity 参数，因为它就是用来封装请求 body 和请求 header 的。

### 无参的情况

!FILENAME 服务端代码
```java
略。服务端返回一个简单的 String 。
```

!FILENAME 客户端调用
```java
@Test
public void test1() {
  String url = "http://localhost:8080/get1";
  RestTemplate template = new RestTemplate();
  ResponseEntity<String> responseEntity = template.exchange(url, HttpMethod.GET, null, String.class);

  log.info("{}", responseEntity.getStatusCode());
  log.info("{}", responseEntity.getHeaders());
  log.info("{}", responseEntity.getBody());
}
```

### 有参的情况

由于 GET 请求的参数是拼接在 URL 后面传递到后台的，因此我们的参数也可以出现在 URL 中。

参数出现在 URL 有两种方式，一种是使用数字作为占位符（从 1 开始），另一种是利用 Map 的 key 值作占位符。

!FILENAME 客户端调用
```java
@Test
public void test2() {
  // 方式一
  String url1 = "http://localhost:8080/get2?username={1}&password={2}";
  RestTemplate template = new RestTemplate();
  ResponseEntity<String> responseEntity1 = template.exchange(url1, HttpMethod.GET, null, String.class, "tom", 10);
  log.info("{}", responseEntity1.getBody());

  // 方式二
  String url2 = "http://localhost:8080/get2?username={xxx}&password={yyy}";
  Map<String, Object> map = new HashMap<>();
  map.put("xxx", "tom");
  map.put("yyy", 20);
  ResponseEntity<String> responseEntity2 = template.exchange(url2, HttpMethod.GET, null, String.class, map);
  log.info("{}", responseEntity2.getBody());
}
```


### 返回一个对象

这里的关键在于，要在调用 `exchange()` 方法时明确说明返回的是一个 User 类型的对象。<small>（这背后是因为，RestTemplate 需要知道要将收到的 JSON 格式的字符串按什么规则转换）。</small>

!FILENAME 服务端代码
```java
略。服务端返回一个 User 对象。
```

!FILENAME 客户端调用
```java
@Test
public void test3() {
  String url = "http://localhost:8080/get3?username={1}&age={2}";
  RestTemplate template = new RestTemplate();
  ResponseEntity<User> responseEntity1 = template.exchange(url, HttpMethod.GET, null, User.class, "tom", 10);
  log.info("{}", responseEntity1.getBody());
}
```


### 返回一个对象的集合

本质上和上面的返回一个普通对象的情况类似，只不过如何『描述』对象的集合需要实现 ParameterizedTypeReference 接口。<small>好在这是一段只用复制粘贴稍作修改的代码。</small>

!FILENAME 描述对象的集合
```java
ParameterizedTypeReference<看这里> pr = new ParameterizedTypeReference<看这里>() {};
```

只需要在上述 `看这里` 部分替换成对象的集合的类型即可，如下例子：

!FILENAME 服务端代码
```java
略。服务端返回一个 List<User>
```

!FILENAME 客户端调用
```java
@Test
public void testGet4() {
  String url = "http://localhost:8080/get4?username={1}&age={2}";
  RestTemplate template = new RestTemplate();
  ParameterizedTypeReference<List<User>> pr = new ParameterizedTypeReference<List<User>>() {};
  ResponseEntity<List<User>> responseEntity1 = template.exchange(url, HttpMethod.GET, null, pr, "tom", 10);
  log.warn("{}", responseEntity1.getBody().size());
}
```


## 发起 POST 请求

回顾一下 POST 请求的特点：

- POST 请求的 header 中的 content-type 的值是 `application/x-www-form-urlencoded` 。 
- POST 请求的参数是附加到请求的 body 部分的。

因此，在调用 `exchange()` 方法时，需要为其提供一个 `HttpEntity` 类型参数。

### 无参的情况

!FILENAME 客户端调用
```java
@Test
public void testPost1() {
  String url = "http://localhost:8080/post1";
  RestTemplate template = new RestTemplate();

  // 准备请求头部信息
  HttpHeaders headers = new HttpHeaders();
  headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

  // 无参数情况下，不需要设置请求 body 部分
  HttpEntity<?> entity = new HttpEntity<>(null, headers);

  ResponseEntity<String> responseEntity = template.exchange(url, HttpMethod.POST, entity, String.class);

  log.warn("{}", responseEntity.getStatusCode());
  log.warn("{}", responseEntity.getHeaders());
  log.warn("{}", responseEntity.getBody());
}
```


### 有参的情况

!FILENAME 客户端调用
```java
@Test
public void testPost2() {
  // 准备请求头部信息
  HttpHeaders headers = new HttpHeaders();
  headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

  // 准备要提交的数据
  MultiValueMap<String, Object> parameters = new LinkedMultiValueMap<>();
  parameters.add("username", "tom");
  parameters.add("age", 20);

  HttpEntity<MultiValueMap<String, Object>> entity = new HttpEntity<>(parameters, headers);

  String url = "http://localhost:8080/post2";
  ResponseEntity<String> responseEntity = new RestTemplate().exchange(url, HttpMethod.POST, entity, String.class);
  log.warn("{}", responseEntity.getBody());
}
```

### 返回一个对象

理论上，和 GET 请求方式是一样的，只需要按照上面所述做出响应修改即可。

### 返回一个对象的集合

理论上，和 GET 请求方式是一样的，只需要按照上面所述做出响应修改即可。


## 发起 PUT 请求

回顾一下 PUT 请求的特点：

- 由于底层的 Servlet 的原因，Spring MVC（和 Servlet）可以接收 PUT 请求，但是不接收 PUT 请求提交的参数。参见 `RESTfule API` > `设计 RESTful API` > `Spring MVC 中 PUT、PATCH 请求参数` 。
- 基于上一点，PUT 请求的 `header` 中的 `content-type` 通常需要设值为 `application/json` 。
- PUT 请求的参数是附带在请求的 `body` 部分，是一个 JSON 格式的字符串内容。


### 无参的情况

!FILENAME 客户端调用
```java
@Test
public void testPut1() {
  String url = "http://localhost:8080/put1";
  RestTemplate template = new RestTemplate();

  // 准备请求头部信息
  HttpHeaders headers = new HttpHeaders();
  headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

  // 无参数情况下，不需要设置请求 body 部分
  HttpEntity<?> entity = new HttpEntity<>(null, headers);

  ResponseEntity<String> responseEntity = template.exchange(url, HttpMethod.PUT, entity, String.class);

  log.warn("{}", responseEntity.getStatusCode());
  log.warn("{}", responseEntity.getHeaders());
  log.warn("{}", responseEntity.getBody());
}
```

### 有参的情况

!FILENAME 客户端调用
```java
@Test
public void testPut2() {
  // 准备请求头部信息
  HttpHeaders headers = new HttpHeaders();
  headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

  // 准备要提交的数据
  MultiValueMap<String, Object> parameters = new LinkedMultiValueMap<>();
  parameters.add("username", "tom");
  parameters.add("age", 20);

  HttpEntity<MultiValueMap<String, Object>> entity = new HttpEntity<>(parameters, headers);

  String url = "http://localhost:8080/put2";
  ResponseEntity<String> responseEntity = new RestTemplate().exchange(url, HttpMethod.PUT, entity, String.class);
  log.warn("{}", responseEntity.getBody());
}
```


### 返回一个对象

!FILENAME 服务端代码
```java
略。服务端返回一个 User 对象。
```

!FILENAME 客户端调用
```java
```


### 返回一个对象的集合

!FILENAME 服务端代码
```java
略。服务端返回一个 List<User>
```

!FILENAME 客户端调用
```java
```

## 发起 DELETE 请求

回顾一下 DELETE 请求的特点：

- DELETE 请求和 GET 请求一样不用『管』header 中的 `content-type` 的值。 

- DELETE 请求的参数是『内嵌』在 URL 中的，是 RUL 一部分。
  
  <small>注意，这和 GET 请求的 `?xxx=xxx` 『追加在 URL 屁股后面』的性质是不一样的，GET 请求追加的部分不属于 URL 内容本身。</small>

- DELETE 请求不要试图在请求的 body 中附带参数数据。
  
  <small>因为根据 HTTP 协议标准，DELETE 请求的 body 部分无意义，因此，一些网关、代理、防火墙在收到 DELET 请求后会直接删除/忽略其 bdoy 部分内容。</small>

### 无参的情况

略，基本原理和 GET 请求一致。

### 有参的情况

略，基本原理和 GET 请求一致。

### 返回一个对象

略，基本原理和 GET 请求一致。

### 返回一个对象的集合

略，基本原理和 GET 请求一致。

### 实现 DELETE 的批量删除

#### 方案一：少量的参数情况

将多个 id 以 `,` 作为分隔符分隔，并作为一个整体存在于 URL 中，后台以 String 的形式从路径中接收它，再作后续处理。

!FILENAME 例如
```java
http://localhost:8080/del5/100,200,300"

@DeleteMapping("/del5/{ids}")
public void demo5(@PathVariable String ids) {
  // 在这里 ids == "100,200,300"
}
```

#### 方案二：多参数时不使用 DELETE，使用 POST

如果只有三五个，哪怕是七八个 id 时，使用上述方案问题不大，但是如果需要向后台传递几十上百个 id ，那么上述方案就不太合适。

此时，最简单的办法就是放弃 DELETE 方法/语义，改为 POST 方法传参。

#### 方案三：多参数时仍硬刚 DELETE

如果不想放弃 DELETE 语义，仍坚持删除用删除（DELETE），那么就只能采用下述麻烦的办法了：将逻辑上的一个 `DELET` 操作拆分成 `POST` + `DELETE` 两步操作。

- Step 1：

  发起 POST 请求，将多个 id 提交到服务端。无论服务端是存到数据库，还是 Redis，还是什么地方，要求服务端返回一个能代表这批 id 的一个唯一性表示。例如：9527 。

- Step 2：

  发起 DELETE 请求，以上一步的哪个唯一性标识作为参数，要求服务器删除它们。例如：`DELETE /student/batch/9527` 。



