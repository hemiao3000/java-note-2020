# 服务异常处理（Hystrix）

我们的服务最终是部署在服务器上，因为各种原因，服务难免会发生故障，那么其他服务去调用这个服务就会调不到，甚至会一直卡在那里，导致用户体验不好。

针对这个问题，我们就需要对服务接口做错误处理，一旦发现无法访问服务，则立即返回并报错，我们捕捉到这个异常就可以以可读化的字符串返回到前端。

为了解决这个问题，业界提出了熔断器模型。

## Hystrix 组件

SpringCloud 集成了 Netflix 开源的 **`Hystrix`** 组件，该组件实现了熔断器模型，它使得我们很方便地实现熔断器。

在实际项目中，一个请求调用多个服务是比较常见的，如果较底层的服务发生故障将会发生连锁反应。这对于一个大型项目是灾难性的。因此，我们需要利用 Hystrix 组件，当特定的服务不可用达到一个阈值（Hystrix 默认 5 秒 20 次），将打开熔断器，即可避免发生连锁反应。

## 代码实现

紧接上一篇的代码，Feign 是默认自带熔断器的，在 SpringCloud 中是默认关闭的，我们可以在 application.yml 中开启它:

```yml
spring.application.name=feign
server.port=8081
eureka.client.serviceUrl.defaultZone=http://127.0.0.1:8761/eureka/
# 开启熔断器
feign.hystrix.enabled=true

```

新建一个类 ApiServiceError.java 并实现 ApiService:

```java
@Component
public class ApiServiceError implements ApiService {

    @Override
    public String hello() {
        return "服务发生故障！";
    }
}
```

然后在 ApiService 的注解中指定 fallback:

```java
@FeignClient(value = "eurekaclient", fallback = ApiServiceError.class)
public interface ApiService {

    @GetMapping(value = "/hello")
    String hello();
}
```

再创建 Controller 类:ApiController，加入如下代码:

```java
@RestController
public class ApiController {

    @Autowired
    private ApiService apiService;

    @RequestMapping("/index")
    public String index() {
        return apiService.hello();
    }

}
```

## 测试熔断器

分别启动注册中心 EurekaServer、服务提供者 EurekaClient 和服务消费者 Feign，然后访问: [http://localhost:8081/index](http://localhost:8081/index)，可以看到顺利请求到接口:

```
端口: 8763
```

然后停止 EurekaClient，再次请求，可以看到熔断器生效了:

```
服务器发生故障！
```
