# 服务消费者（Feign）

## 什么是 Feign

对外提供接口通过 zuul 服务网关实现。一个大型的系统由多个微服务模块组成，各模块之间不可避免需要进行通信，一般我们可以通过内部接口调用的形式，服务 A 提供一个接口，服务 B 通过 HTTP 请求调用服务 A 的接口，为了简化开发，Spring Cloud 提供了一个基础组件方便不同服务之间的 HTTP 调用，那就是 **`Feign`** 。

Feign 是一个声明式的 HTTP 客户端，它简化了 HTTP 客户端的开发。使用 Feign，只需要创建一个接口并注解，就能很轻松的调用各服务提供的 HTTP 接口。Feign 默认集成了 Ribbon，默认实现了负载均衡。

## 创建 Feign 服务

在创建一个项目，命名为 springcloud_feign，然后在 pom.xml 添加如下内容:

```xml
<parent>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-parent</artifactId>
    <version>2.1.12.RELEASE</version>
    <relativePath/> <!-- lookup parent from repository -->
</parent>

<properties>
    <java.version>1.8</java.version>
    <spring-cloud.version>Greenwich.SR4</spring-cloud.version>
</properties>

<dependencies>
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-web</artifactId>
    </dependency>
    <dependency>
        <groupId>org.springframework.cloud</groupId>
        <artifactId>spring-cloud-starter-netflix-eureka-client</artifactId>
    </dependency>
    <dependency>
        <groupId>org.springframework.cloud</groupId>
        <artifactId>spring-cloud-starter-openfeign</artifactId>
    </dependency>
</dependencies>

<dependencyManagement>
    <dependencies>
        <dependency>
            <groupId>org.springframework.cloud</groupId>
            <artifactId>spring-cloud-dependencies</artifactId>
            <version>${spring-cloud.version}</version>
            <type>pom</type>
            <scope>import</scope>
        </dependency>
    </dependencies>
</dependencyManagement>

<build>
    <plugins>
        <plugin>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-maven-plugin</artifactId>
        </plugin>
    </plugins>
</build>
```

创建 *`application.properties`*，内容如下:

```properties
spring.application.name=feign
server.port=8081

eureka.client.serviceUrl.defaultZone=http://127.0.0.1:8761/eureka/
```

最后创建一个启动类 FeignApplication：

```java
@SpringBootApplication
@EnableEurekaClient
@EnableFeignClients
public class EurekaFeignApplication {
    public static void main(String[] args) {
        SpringApplication.run(EurekaFeignApplication.class, args);
    }
}
```

我们可以看到启动类增加了一个新的注解: **`@EnableFeignClients`**，如果我们要使用 Feign 声明式 HTTP 客户端，必须要在启动类加入这个注解，以开启 Feign 。

这样，我们的 Feign 就已经集成完成了，那么如何通过 Feign 去调用之前我们写的 HTTP 接口呢?

首先创建一个接口 ApiService，并且通过注解配置要调用的服务地址:

```java
@FeignClient(value = "eurekaclient")
public interface ApiService {

    @GetMapping(value = "/hello")
    String hello();
}
```

分别启动注册中心 EurekaServer、服务提供者 EurekaClient（这里服务提供者启动两次，端口分别为 8762、8763，以观察 Feign 的负载均衡效果）。

然后在 Feign 里面通过单元测试来查看效果。

### 1. 添加单元测试依赖

```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-test</artifactId>
    <scope>test</scope>
</dependency>
```

### 2. 添加测试代码

```java

@SpringBootTest(classes = EurekaFeignApplication.class)
@RunWith(SpringJUnit4ClassRunner.class)
public class ApiServiceTest {

    @Autowired
    private ApiService apiService;

    @Test
    public void test() {
        try {
            System.out.println(apiService.hello());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
```

最后分别启动两次单元测试类，我们可以发现控制台分别打印如下信息:

```
端口: 8762
端口: 8763
```

由此可见，我们成功调用了服务提供者提供的接口，并且循环调用不同的接口，说明它自带了负载均衡效果。
