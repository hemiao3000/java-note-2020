# 配置中心（Spring Cloud Config）

在实际使用场景中，集群中会存在多个服务（Service），而每个服务又有可能存在多个实例（Instance）。如果每一个微服务都是靠自己的 ***`application.properties`*** 或 ***`application.yml`*** 进行配置，那么整个项目的配置就会繁琐为混乱。

> <small>例如，多个 Service Producer 使用的是用一个数据库。理论上，可以在各个项目中配置数据库连接相关配置。但是，如果一旦有变动，如数据库所在主机的 IP 变动了，那么你需要改很多处地方，而且还必须确保无一遗漏。</small>

因此 Spring Cloud 为我们集成了『配置中心』：***`spring-cloud-config`*** 组件。

Spring Cloud Config 是一个高可用的分布式配置中心，专门用于管理系统的所有配置，也就是我们将所有配置文件放到统一的地方进行管理。它支持将配置存放到 Git 仓库进行统一管理。

![](./_img/config-01.png)


Config Server 的主要功能：

- 提供访问配置的服务接口

Config Client 的主要功能：

- 绑定 Config Server，使用远程的属性来初始化自己。

- 属性改变时，对它们重新加载。


实现原理：

我们都知道当 Spring Boot 的程序启动时，会加载 classpath 下的 *`application`* 配置文件的属性用于初始化 Spring 容器。其实，在此之前，Spring Boot 会加载一个名为 *`bootstrap`* 的配置文件。

<small>如果两个配置文件都存在，且存在 key 相同的配置，则 *`application`* 的配置会覆盖 *`bootstrap`* 的配置。</small>

Spring Config Client 就是利用加载 *`bootstrap`* 这个阶段去外部（即，Spring Config Server）读取外部的属性，而又『故意』在 *`applicaiton`* 阶段缺省这些配置，从而达到统一配置的效果。

从分工角度来看，*`bootstrap`* 配置文件中存放的是公用/共用配置，而 *`application`* 配置文件中存放的是各个项目各自/独立的配置。

# Git 服务器


<small>由于 github 网速较慢，因此，这里使用的是 gitee 。两者本质上是一样的。</small>

创建的仓库地址为：[配置中心仓库](https://gitee.com/hemiao3020/config-only-a-demo.git)

目录结构如下：

```
仓库
│── config
│   │── config-eureka-client-dev.yml
│   │── config-eureka-client-prod.yml
│   │── config-single-client-dev.yml
│   │── config-single-client-prod.yml
│   └── test.yml
│── LICENSE 
└── README.md
```

其中 `LICENSE` 和 `README.md` 与 Spring Cloud Config 功能无关。

注意，文件的名称不是乱起的，例如上面的 

- *`config-single-client-dev.yml`*
- *`config-single-client-prod.yml`*

这两个是同一个项目的不同版本，项目名称为 *`config-single-client`*， 一个对应开发版，一个对应正式版。

- *`config-eureka-client-dev.yml`*
- *`config-eureka-client-prod.yml`* 

则是另外一个项目的，项目的名称就是 *`config-eureka-client`* 。

配置文件的内容大致如下，用于区分，略有不同。

```yml
data:
  env: config-eureka-dev
  user:
    username: eureka-client-user
    password: 1291029102
```

注意，并非必须是 *`.yml`* 文件，配置文件格式为 *`.properties`* 文件也型。


# 一个简单的 Config Server

在 spring starter 中引入：

- `Web` > `Spring Web`
- `Spring Cloud Config` > `Config Server` 

```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-web</artifactId>
</dependency>
<dependency>
    <groupId>org.springframework.cloud</groupId>
    <artifactId>spring-cloud-config-server</artifactId>
</dependency>
```

编写 *`application.properties`* 配置文件，并写入如下内容：

```properties
server.port=3301
spring.application.name=config-single-server
# 配置文件所在仓库
spring.cloud.config.server.git.uri=https://gitee.com/hemiao3020/config-only-a-demo.git
spring.cloud.config.server.git.username=<gitee 登录账号>
spring.cloud.config.server.git.password=<gtiee 登录密码>
# 配置文件分支
spring.cloud.config.server.git.default-label=master
# 配置文件所在根目录
spring.cloud.config.server.git.search-paths=config
```

在启动类上加入 `@EnableConfigServer` 注解，标识本项目的【身份】是 Config Server，并激活相关配置 。 

打开浏览器，访问网址访问 [*http://localhost:3301/config-single-client/prod*](http://localhost:3301/config-single-client/prod) 。会看到类似如下内容：

```json
{
    "name":"config-single-client",
    "profiles":["prod"],
    "label":null,
    "version":"bcbf31dfa30218c9282f6d5eca23364188b803cf",
    "state":null,
    "propertySources":[{
        "name":"https://gitee.com/hemiao3020/config-only-a-demo.git/config/config-single-client-prod.yml",
        "source":{
            "data.env":"localhost-online",
            "data.user.username":"fengzheng-online",
            "data.user.password":"password-online"
        }
    }]
}
```

在 *`propertySources`* - *`source`* 下就是对应的配置文件的内容。

我们在浏览器中输入的网址是有固定规则的，我们使用的规则是：

```
/{application}/{profile}[/{label}]
```

需要注意的有两点：

- 这里的 *`application`* 并非 Config Server 项目的名字<small>（例如，*`git-config-server`*）</small>，而是配置文件所对应的项目的项目名。我们的配置文件叫作：*`config-single-client-dev.yml`*，因此，这里的 application 指的就是 *`config-single-client`* 。
- 我们在 Config Server 项目的配置文件中指定了 *`....git.default-label=master`*，因此，我们这里默认访问的就是 *`master`* 分支。

除了上述规则，URL 还可以使用如下规则：

```
/{application}-{profile}.yml
/{application}-{profile}.properties

/{label}/{application}-{profile}.yml
/{label}/{application}-{profile}.properties
```

# 一个简单的 Config Client

在 spring starter 中引入：

- `Web` > `Spring Web`
- `Spring Cloud Config` > `Config Client` 

<small>注意，不是 `Config Client (PCF)` 那个。</small>

```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-web</artifactId>
</dependency>
<dependency>
    <groupId>org.springframework.cloud</groupId>
    <artifactId>spring-cloud-starter-config</artifactId>
</dependency>
```

这里有个小细节，虽然我们选择的是 `Config Client`，但是它背后引入的包的名字是叫 *`spring-cloud-starter-config`*，并非 *`...-config-client`* 。

在 *`bootstrap.properties`* 配置文件中写入如下内容：

```properties
spring.application.name=config-single-client
spring.cloud.config.uri=http://localhost:3301
spring.cloud.config.label=master
spring.cloud.config.profile=dev
```

这里的这四个配置与 URL *`http://localhost:3301/config-single-client/dev/master`* 相呼应。


如果使用 *`.yml`* 配置，可以写成更高级的形式：

```yml
spring:
  profiles:
    active: dev

---
spring:
  profiles: prod
  application:
    name: config-single-client
  cloud:
    config:
      uri: http://localhost:3301
      label: master
      profile: prod

---
spring:
  profiles: dev
  application:
    name: config-single-client
  cloud:
    config:
      uri: http://localhost:3301
      label: master
      profile: dev
```

在 yaml 文件中，以 `---` 作为配置段的开始的标识。上述配置中配置了两套配置，并通过 *`spring.profiles.active`* 来激活使用名为 *`dev`* 的那套配置。

在 *`application.properties`* 的配置文件中编写如下内容：

```java
server.port=3302

# 当无法访问 Config Server 时，使用如下默认值
data.env=NaN
data.user.username=NaN
data.user.password=NaN
```

在 Config Client 的启动类中编写类似如下代码：

```java
@Value("${data.env}")
private String env;

@Value("${data.user.username}")
private String username;

@Value("${data.user.password}")
private String password;

public static void main(String[] args) {
    SpringApplication.run(Producer2Application.class, args);
}

@GetMapping("/demo")
public String demo() {
    return String.format("%s, %s, %s", env, username, password);
}
```

访问 *`http://localhost:3302/demo`*，会有如下内容：

```
localhost-dev-edit888, fengzheng-dev999, password-dev
```

而这三项配置就是 gitee 中的配置文件中的配置。



『』








