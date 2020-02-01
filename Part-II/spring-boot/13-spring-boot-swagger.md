<span class="title">Swagger<span>

# 什么是 Swagger

Swagger 是一系列 RESTful API 的工具，通过 Swagger 可以获得项目的⼀种交互式文档，客户端 SDK 的⾃ 动生成等功能。

Swagger 的目标是为 REST APIs 定义一个标准的、与语⾔言无关的接口，使人和计算机在看不到源码或者看不到文档或者不能通过网络流量检测的情况下，能发现和理解各种服务的功能。当服务通过 Swagger 定义，消费者就能与远程的服务互动通过少量的实现逻辑。

Swagger（丝袜哥）是世界上最流行的 API 表达工具。

# 快速上手

使用 Spring Boot 集成 Swagger 的理念是，使⽤用注解来标记出需要在 API 文档中展示的信息，Swagger 会根据项目中标记的注解来生成对应的 API 文档。Swagger 被号称世界上最流行的 API 工具，它提供了 API 管理的全套解决方案，API 文档管理需要考虑的因素基本都包含，这里将讲解最常用的定制内容。

Spring Boot 集成 Swagger 2.X 很简单，需要引入依赖并做基础配置即可。

```xml
<dependency>
    <groupId>io.springfox</groupId>
    <artifactId>springfox-swagger2</artifactId>
    <version>2.8.0</version>
</dependency>
<dependency>
    <groupId>io.springfox</groupId>
    <artifactId>springfox-swagger-ui</artifactId>
    <version>2.8.0</version>
</dependency>
```

# 创建 SwaggerConfig 配置类

```java
@Configuration
@EnableSwagger2
public class SwaggerConfig {
}
```

在 SwaggerConfig 的类上添加两个注解：

| 注解 | 说明 |
| :- | :- |
| @Configuration | 启动时加载此类 |
| @EnableSwagger2 | 表示此项目启用 Swagger API 文档功能 |


在 SwaggerConfig 中添加两个方法：<small>（其中一个方法是为另一个方法作辅助的准备工作）</small>

```java
@Bean
public Docket api() {
    return new Docket(DocumentationType.SWAGGER_2)
        .apiInfo(apiInfo())
        .select()
        // 此处自行修改为自己的 Controller 包路径。
        .apis(RequestHandlerSelectors.basePackage("xxx.yyy.zzz"))
        .paths(PathSelectors.any())
        .build();
}
```

此方法使用 *`@Bean`*，在启动时初始化，返回实例 Docket（Swagger API 摘要对象），这里需要注意的是 `.apis(RequestHandlerSelectors.basePackage("xxx.yyy.zzz"))` 指定需要扫描的包路路径，只有此路径下的
Controller 类才会自动生成 Swagger API 文档。

```java
private ApiInfo apiInfo() {
    return new ApiInfoBuilder()
       .title("XXX 项目接口文挡")
       .description("XXX Project Swagger2 UserService Interface")
       .termsOfServiceUrl("http://www.163.com")
       .version("1.0")
       .build();
}
```

这块配置相对重要一些，主要配置页面展示的基本信息包括，标题、描述、版本、服务条款等，查看 ApiInfo 类的源码还会发现支持 license 等更多的配置。

配置完成之后启动项目，在浏览器中输入网址 *`http://localhost:8080/swagger-ui.html`*，即可看到上面的配置信息，效果如下：


# Swagger 常用注解

Swagger 通过注解表明该接口会生成文档，包括接口名、请求方法、参数、返回信息等，常用注解内容如下：

| 作用范围 | API | 使⽤用位置 |
| :- | :- | :- |
| 协议集描述 | `@Api` | 用于 Controller 类上 |
| 协议描述 | `@ApiOperation` | 用在 Controller 的方法上 |
| 非对象参数集 | `@ApiImplicitParams` | 用在 Controller 的方法上 |
| 非对象参数描述 | `@ApiImplicitParam` | 用在 `@ApiImplicitParams` 的方法里边 |
| 响应集 | `@ApiResponses` | 用在 Controller 的方法上 |
| 响应信息参数 | `@ApiResponse` | 用在 `@ApiResponses` 里边 |
| 描述返回对象的意义 | `@ApiModel` | 用在返回对象类上 |
| 对象属性 | `@ApiModelProperty` | 用在出入参数对象的字段上|

例如：

```java
@Api(value = "用户服务", description = "用户操作 API")
@RestController
@RequestMapping("/user")
public class UserController {

    @ApiOperation(value = "获取用户信息", notes = "根据id在取用户信息", produces = "application/json", response = Result.class)
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", value = "用户Id", required = true, dataType = "int", paramType = "path")
    })
    @GetMapping(value = "/{id}")
    public Result<DomainUser> getUser(@PathVariable int id) {
        System.out.println(id);
        return new Result<DomainUser>(ResultCode.OK, new DomainUser());
    }
}
```
