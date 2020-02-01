# 服务网关（Zuul）

Spring Cloud 的另一个组件：<strong>zuul</strong>，它提供微服务的 <strong>网关</strong> 功能，即中转站，通过它提供的接口，可以转发不同的服务。

在实际的项目中，一个项目可能会包含很多个服务，每个服务的端口和 IP 都可能不一样。那么，如果我们以这种形式提供接口给外部调用，代价是非常大的。

这个时候，我们需要统一的入口，接口地址全部由该入口进入，而服务只部署在局域网内供这个统一的入口调用，这个入口就是我们通常说的服务网关。

Spring Cloud 给我们提供了这样一个解决方案，那就是 <strong>zuul</strong>，它的作用就是进行路由转发、异常处理和过滤拦截。

建 gateway 工程
## 创
新建工程，命名为 *`gateway`*。在 *`pom.xml`* 中添加如下内容：

在 Spring Initializer 中引入两项依赖：

- `Web` > `Spring Web`
- `Spring Cloud Discovery` > `Eureka Discovery Client`
- `Spring Cloud Routing` > `Zuul`


```xml
<parent>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-parent</artifactId>
    <version>2.1.12.RELEASE</version>
    <relativePath/> <!-- lookup parent from repository -->
</parent>

...

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
        <artifactId>spring-cloud-starter-netflix-zuul</artifactId>
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

创建 EurekaZuulApplication 启动类，并增加 <font color="#0088dd">**@EnableZuulProxy**</font> 注解:

```java
@SpringBootApplication
@EnableEurekaClient
@EnableZuulProxy
public class EurekaZuulApplication {
    public static void main(String[] args) {
        SpringApplication.run(EurekaZuulApplication.class， args);
    }
}
```

最后添加 *`application.properties`* 配置文件，内容如下:

```properties
server.port=8080
spring.application.name=gateway

eureka.client.serviceUrl.defaultZone=http://127.0.0.1:8761/eureka/
zuul.routes.api.path=/api/**
zuul.routes.api.serviceId=eurekaclient

# zuul.routes.xxx.path=/xxx/**
# zuul.routes.xxx.serviceId=...
```

我们可以看到，服务网关的配置多了几项，具体含义如下。

<dl>
<dt><big>zuul.routes.api.path</big></dt>
<dd>指定请求基础地址，其中 API 可以是任何字符。</dd>

<dt><big>serviceId</big></dt>
<dd>转发到的服务 ID，也就是指定服务的 <code>application.name</code>，上述实例的含义表示只要包含 <code>/api/</code> 的地址，都自动转发到 <code>eurekaclient</code> 的服务去。前提是需要有一个名为 <code>eurekaclient</code> 的服务提供者。</dd>
</dl>


依次启动启动服务注册中心、服务提供者、服务网关，访问地址: [http://localhost:8080/api/hello](http://localhost:8080/api/hello)，我们可以看到和之前的界面完全一样。注意，这的端口号是 `8080` 。


其实只要引入了 zuul，它就会自动帮我们实现反向代理和负载均衡。配置文件中的地址转发其实就是一个反向代理。

如果只是使用转发而不涉及负载均衡的功能，上述配置还可以写成：

```properties
#zuul.routes.api.path=/api/**
#zuul.routes.api.serviceId=Hello
zuul.routes.api.url=http://127.0.0.1:8762
```

我们新增一个项目，其内容与之前的服务提供者项目一致，仅将器端口改为 8673 。两个服务提供者的 *`spring.application.name`* 都是 eurekaclient 。

启动后，再不断访问地址：[http://localhost:8080/api/hello](http://localhost:8080/api/hello)，可以看到交替出现以下界面：

```
端口号: 8762
端口号: 8763
```

由此可以得出，当一个服务启动多个端口时，zuul 服务网关会依次请求不同端口，以达到负载均衡的目的。

## 服务拦截

前面我们提到，服务网关还有个作用就是接口的安全性校验，这个时候我们就需要通过 zuul 进行统一拦截，zuul 通过继承过滤器 <strong>ZuulFilter</strong> 进行处理，下面请看具体用法。 

新建一个类 ***`ApiFilter`*** 并继承 ***`ZuulFilter`***：

```java
@Component
public class ApiFilter extends ZuulFilter {

    @Override
    public String filterType() {
        return "pre";
    }

    @Override
    public int filterOrder() {
        return 0;
    }

    @Override
    public boolean shouldFilter() {
        return true;
    }

    @Override
    public Object run() throws ZuulException {
        // 这里写校验代码。例如 JWT 的校验。
        RequestContext context = RequestContext.getCurrentContext();
        HttpServletRequest request = context.getRequest();
        String token = request.getParameter("token");
        if (!"12345".equals(token)) {
            context.setSendZuulResponse(false);
            context.setResponseStatusCode(401);
            try {
                context.getResponse().getWriter().write("token is invalid.");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        return null;
    }
}
```

其中:

- <strong>filterType</strong> 为过滤类型，可选值有 `pre`（路由之前）、`routing`（路由之时）、`post`（路由之后）、`error`（发生错误时调用）。

- <strong>filterOrdery</strong> 为过滤的顺序，如果有多个过滤器，则数字越小越先执行

- <strong>shouldFilter</strong> 表示是否过滤，这里可以做逻辑判断，true 为过滤，false 不过滤

- <strong>run</strong> 为过滤器执行的具体逻辑，在这里可以做很多事情，比如:权限判断、合法性校验等。

启动 gateway，在浏览器输入地址: [http://localhost:8080/api/hello](http://localhost:8080/api/hello)，可以看到以下界面：

```
token is invalid
```

再通过浏览器输入地址: [http://localhost:8080/api/index?token=12345](http://localhost:8080/api/index?token=12345)，可以看到以下界面:

```
端口: 8762
```

## 错误拦截

在一个大型系统中，服务是部署在不同的服务器下面的，我们难免会遇到某一个服务挂掉或者请求不到的时候，如果不做任何处理，服务网关请求不到会抛出 500 错误，对用户是不友好的。

我们为了提供用户的友好性，需要返回友好性提示，zuul 为我们提供了一个名叫 ***`FallbackProvider`*** 的接口，通过它我们就可以对这些请求不到的服务进行错误处理。

新建一个类 ***`ApiFallbackProvider`*** 并且实现 ***`FallbackProvider`*** 接口:

```java
@Component
public class ApiFallbackProvider implements FallbackProvider {

    @Override
    public String getRoute() {
        return "eurekaclient";
    }

    @Override
    public ClientHttpResponse fallbackResponse(String route, Throwable cause) {
        return new ClientHttpResponse() {
            @Override
            public HttpStatus getStatusCode() throws IOException {
                return HttpStatus.OK;
            }

            @Override
            public int getRawStatusCode() throws IOException {
                return 200;
            }

            @Override
            public String getStatusText() throws IOException {
                return "{code:0, message:\"服务器异常！\"}";
            }

            @Override
            public void close() {
            }

            @Override
            public InputStream getBody() throws IOException {
                return new ByteArrayInputStream(getStatusText().getBytes());
            }

            @Override
            public HttpHeaders getHeaders() {
                HttpHeaders headers = new HttpHeaders();
                headers.setContentType(MediaType.APPLICATION_JSON);
                return headers;
            }
        };
    }
}
```

其中，getRoute 方法返回要处理错误的服务名，fallbackResponse 方法返回错误的处理规则。

现在开始测试这部分代码，首先停掉服务提供者 eurekaclient，再重启 gateway，请求地址: [http://localhost:8080/api/index?token=12345](http://localhost:8080/api/index?token=12345)，即可出现以下界面:

```
{code:0, message:"服务器异常！"}
```
