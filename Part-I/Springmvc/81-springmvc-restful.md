<span class="title">PUT、PATCH、DELETE 请求参数</span>

# PUT、PATCH 的参数问题

RESTful API 要求服务端能响应：get / post / put / delete 请求，以对应增删改查四大功能。对此 Spring MVC 都能支持，只需要在请求处理方法头上加上：

```java
@RequestMapping(value="/...", method = RequestMethod.GET)
```

或者使用：

```java
@GetMapping("/...")
@PostMapping("/...")
@PutMapping("/...")
@DeleteMapping("/...")
```

就能表示该方法仅针对于特定请求方式作出响应。

<strong>默认 Spring MVC 可以接受 PUT 请求和 PATCH 请求，但是，不接受 PUT 请求和 PATCH 请求发送来参数！！！</strong>

Spring MVC 通过　`@RequestParam` 注解接收的请求只有两类：

1. 接收 GET 请求提交的参数
2. 接收 `application/x-www-form-urlencoded` 方式的 POST 请求提交的参数。
3. <strong>不支持</strong> PUT、PATCH<small>（也包括 DELETE）</small>请求的参数的获取。

当然，究其原因这个锅不该 Spring MVC 背：

1. Servlet 的 *`request.getParameter("");`* 本身就只支持 GET 和 POST 方法传参。

2. Servlet-api 中并没有 *`doPatch()`* 方法，更谈不上获取请求参数了。

# 解决方案

解决 PUT、PATCH 的参数传递的方案有两种：

## 方案一：使用 HttpPutFormContentFilter

Spring MVC 从 *`3.1`* 开始提供了一个 Filter<small>（过滤器）</small>来解决这个传参问题。

Spring MVC 从 *`5.1`* 开始将 *`HttpPutFormContentFilter`* 标注为过时，并提供了一个新的 ***`FormContentFilter`*** 作为替代。`which is the same but also handles DELETE` 。

```xml
<filter>	<!-- 默认 Spring MVC 无法接受 PUT 请求参数 -->
    <filter-name>httpPutFormContentFilter</filter-name>
    <filter-class>
      org.springframework.web.filter.HttpPutFormContentFilter
    </filter-class>
</filter>

<filter-mapping>
    <filter-name>httpPutFormContentFilter</filter-name>
    <url-pattern>/*</url-pattern>   
    <!-- 注意匹配规则，这里是 /* ，表示拦截所有请求 -->
</filter-mapping>
```

这个过滤器会拦截所有的 PUT 和 PATCH 请求，将它们的底层处理方式转变为 POST 请求处理方式，这样这些 POST 请求有的功能 PUT 和 PATCH 请求都有。

## Spring Boot 中配置 HttpPutFormContentFilter

springboot 中自带 HttpPutFormContentFilter 用于处理 PUT 请求。

不过，有人反映因为 Filter 注册的有限的问题，导致 PUT、PATCH 请求还没有来得及被 ***`HttpPutFormContentFilter`*** 处理，就被 Spring MVC 执行了参数绑定，而此时自然就获取不到任何请求参数。

这种情况下，可以通过提高 ***`HttpPutFormContentFilter`*** 过滤器优先级提高来解决这个问题。但是，由于我们无法将 *`@Order`* 注解标注于 HttpPutFormContentFilter 类的源码中，因此需要自定义一个 HttpPutFormContentFilter 子类，并注册使用。

```java
@Component
@WebFilter(urlPatterns = "/*", filterName = "putFilter")
@Order(Integer.MIN_VALUE)
// public class PutFilter extends HttpPutFormContentFilter {
public class PutFilter extends FormContentFilter {
}
```

## 方法二：使用 application/json 曲线救国

PUT 和 PATCH 的请求参数默认是以 *`x-www-form-urlencoded`* 的 *`contentType`* 来发送信息，即请求参数是放在 request 的 body 中，以 `aaa=xxx&bbb=yyy&ccc=zzz`。

在默认情况下，*`@ReqeustParam`* 是不会去获取它们在 body 中的请求参数的<small>（除非采用方案一，挂羊头卖狗肉）</small>。

方案二的本质就是索性 <strong>不使用 `@RequestParam` 获取参数，而使用 `@RequestBody`</strong> 。

利用 *`application/json`* 方式传递请求参数，将参数以 JSON 格式字符串的形式放在 request 的 *`body`* 中，在 Controller 中再配合 *`@RequestBody`* 进行参数绑定，从而获取参数。<strong>绕开</strong> *`@RequestParam`* / *`request.getParam()`* 的限制。

# 批量删除时传入多个 id 实现

## 方案一：@PathVariable

接收用数组，调用 api 时，url 后面可跟多个 id，用逗号隔开，如：localhost/user/1234,1235,1236

```java
@DeleteMapping("/{id}")
public JsonData delete(@PathVariable("id") String[] userIds) {
    userService.delete(userIds);
    return JsonData.ok();
}
```

## 方案二：@RequestParam

在使用 *`HttpPutFormContentFilter`* 或 *`FormContentFilter`* 后，DELETE 请求的底层处理方式会被 Spring MVC 偷换成 POST 方式，这样，实际上就完全可以像 POST 请求那样传递多 id 。

```java
@DeleteMapping("/delete")
public String delete(int[] ids) {
    log.info("[{}]", Arrays.toString(ids));
    return "hello";
}
```

客户端发出的请求参数类似是：`?ids=1&ids=2&ids=3` 。
