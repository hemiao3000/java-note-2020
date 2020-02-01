<span class="title">Spring Boot 整合 RabbitMQ</span>

Spring Boot 提供了 *`spring-boot-starter-amqp`* 组件对实现了 AMQP 协议的消息队列<small>（RabbitMQ）</small>的快速整合。

# pom.xml

![rabbitmq-01](./img/spring-boot-rabbitmq-01.png)

```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-amqp</artifactId>
</dependency>
```

# 配置文件

配置 rabbitmq 的安装地址、端口以及账户信息：

```properties
spring.application.name=spring-rabbitmq-demo

spring.rabbitmq.host=127.0.0.1
spring.rabbitmq.port=5672
spring.rabbitmq.username=guest
spring.rabbitmq.password=guest

logging.level.root=INFO
logging.level.hemiao3000.gitee.io=DEBUG
logging.pattern.console=${CONSOLE_LOG_PATTERN:\
  %clr([%15.15t]){faint} \
  %clr(${LOG_LEVEL_PATTERN:%5p}) \
  %clr(|){faint} \
  %clr(%-40.40logger{39}){cyan} \
  %clr(:){faint} %m%n\
  ${LOG_EXCEPTION_CONVERSION_WORD:%wEx}}
```

# 简单示例

<small>官方文档称使用下述注解时，需要先使用 ***`@EnableRabbit`*** 注解标注于配置类上，以表示使用 RabbitMQ 的注解功能。不过，经测试，不使用它似乎也可行。稳妥起见的话还是用上。</small>

## 定义队列

!FILENAME RabbitMQConfig.java
```java
// import org.springframework.amqp.core.Queue;

@Configuration
public class RabbitMQConfig {

    @Bean
    public Queue queue() {
        return new Queue("hello");
    }
}
```


## 定义消息接收者/消费者

!FILENAME HelloReceiver.java
```java
@Slf4j
@Component
@RabbitListener(queues = "hello")
public class HelloReceiver {

    @RabbitHandler
    public void process(String hello) {
        log.info("Receiver : {}", hello);
    }

}
```

注意使用注解 *`@RabbitListener`* 的 *`queues`* 属性指明队列名称，*`@RabbitHandler`* 为具体接收的方法。

## 发送消息（测试并验证）

```java
@Autowired
private RabbitTemplate rabbitTemplate;

@Test
public void send() throws InterruptedException {
    String context = "hello " + new Date();
    log.info("Sender : {}", context);
    rabbitTemplate.convertAndSend("hello", context);
    Thread.sleep(1000l);
}
```

这是一个最简单的案例。上述的代码种，并未明确涉及到 <strong>Exchange</strong> 的概念，因为这里使用到的是 <strong>Default Exchange</strong> 。

Default Exchange 的投递规则是：以 **Queue Name** 作为 **Binding Key**。因此，我们在代码中，使用 *`hello`* 作为 Binding Key 时，消息最终被投递到了 hello 队列。

# 创建 Exchange、Queue 和 Binding


虽然可以，但是还是不建议在代码中去创建 Exchange、Queue 和 Binding。通常还是在 RabbitMQ 中把这些东西都建好，然后再在代码中访问/操作。

<small>其实，这很类似于 Hibernate/JPA 的建表功能。虽然有这种功能存在，但是使用的机会和场景并不多。通常都是现在数据库中把表建好之后直接使用。除非是测试环境中的临时表，才会用到这种功能。</small>


## 创建 Exchange

```java
@Bean
public Exchange exchange() {
//  return new TopicExchange("test-exchange-1"); 
    return new TopicExchange("test-exchange-1", true, false);
}
```

参数说明：

| 参数 | 说明 |
| :- | :- |
| name | 字符串值，exchange 的名称。|
| durable | 布尔值，表示该 exchage 是否持久化。<br> 持久化意味着当 RabbitMQ 重启后，该 exchange 是否会恢复/仍存在。|
| autoDelete | 布尔值，表示当该 exchange 没“人”<small>（queue）</small>用时，是否会被自动删除。|

不指定 durable 和 autoDelete 时，默认为 *`true`* 和 *`false`* 。表示持久化、不用自动删除。

<small>补充，这背后调用的是原生 API 中的 ***`Channel`*** 的 *`.exchangeDeclare()`* 方法。</small>

## 创建 Queue


```java
@Bean
public Queue queue() {
//  return new Queue("test-queue-1"); 
    return new Queue("test-queue-1", true, false, false);
}
```

参数说明：

| 参数 | 说明 |
| :- | :- |
| name | 字符串值，exchange 的名称。|
| durable | 布尔值，表示该 queue 是否持久化。<br> 持久化意味着当 RabbitMQ 重启后，该 queue 是否会恢复/仍存在。<br>另外，需要注意的是，queue 的持久化不等于其中的消息也会被持久化。|
| exclusive | 布尔值，表示该 queue 是否排它式使用。排它式使用意味着仅声明他的连接可见/可用，其它连接不可见/不可用。 |
| autoDelete | 布尔值，表示当该 queue 没“人”<small>（connection）</small>用时，是否会被自动删除。|

不指定 durable、exclusive 和 autoDelete 时，默认为 *`true`*、 *`false`* 和 *`false`* 。表示持久化、非排它、不用自动删除。

<small>补充，这背后调用的是原生 API 中的 ***`Channel`*** 的 *`.queueDeclare()`* 方法。</small>


## 创建 Binding

```java
@Bean
public Binding binding() {
    return BindingBuilder
        .bind(queue())
        .to(exchange())
        .with("*.orange.*")
        .noargs();
}
```

# 发送消息

spring-rabbit 提供了 RabbitTemplate 来简化原生 API 的消息发送方法。

<small>（最简单的情况下），</small>你可以直接要求 Spring 给你注入一个 ***`RabbitTemplate`***，通过它来发送消息：

```java
@Autowired
private RabbitTemplate rabbitTemplate;

@Test
public void demo() {
    rabbitTemplate.convertAndSend("queue-demo-1", "hello world");
}
```

*`.convertAndSend()`* 方法的第一个参数是 **`routing-key`**，第二个参数是你所要发送的消息。

在没有明确指定 Exchange 的情况下，该消息发送给了 RabbitMQ 的 **`default-exchange`**。该 exchage 以队列名作为 **`routing-key`** 。

也就是说，上述代码中的 **`routing-key`** 是 *`queue-demo-1`*，那么该消息最终是发送给 *`queue-demo-1`* 队列。

<small>*`.convertAndSend()`* 方法是 *`.send()`* 方法的包装/简化。*`.send()`* 方法的调用相对比较繁琐。</small>

# 接收/消费消息（PUSH 型）

接收/消费消息的方式有两种：Push 型和 Pull 型。

Push 型表示由 RabbitMQ Broker 负责将消息推送给消费者。消费者在一开始指定/配置监听哪个队列的消息后，就无需考虑其它。当该队列收到消息后，消费者的指定方法就会被触发执行。

PUSH 消费的配置非常简单，对你的消费者类标注 ***`@RabbitListener`*** 注解，为你的消费方法标注 ***`@RabbitHandler`*** 注解即可。当然，前提是消费者类要托管给 Spring：

```java
@Component
@RabbitListener(queues = "queue-demo-1")
public class Consumer1 {

    private static final Logger log = LoggerFactory.getLogger(Consumer1.class);

    @RabbitHandler
    public void process(String message) {
        log.info("Consumer 1: {}", message);
    }

}
```

甚至，你可以直接将 ***`@RabbitListener`*** 标注在方法上，此时，不需要配套使用 ***`@RabbitHandler`*** 注解。


# 对象的支持

Spring Boot 已经完美支持对象的发送和接收，不需要额外的配置。不过，需要注意的是所传递的对象需要实现 ***`Serializable`*** 接口。

!声明队列
```java
@Bean
public Queue departmentQueue() {
    return new Queue("department");
}
```

```java
@Autowired
private RabbitTemplate rabbitTemplate;

@Test
public void demo() {
    Department department = new Department(10L, "测试", "武汉");
    rabbitTemplate.convertAndSend("department", department);
}


@Component
@RabbitListener(queues = "department")
public class DepartmentReceiver {

    private static final Logger log = LoggerFactory.getLogger(DepartmentReceiver.class);

    @RabbitHandler
    public void process(Department department) {
        log.info("Receiver : {}", department);
    }

}
```

# Topic Exchange

Topic 是 RabbitMQ 中最灵活的一种方式，可以根据 routing_key 自由地绑定不同的队列。

<small>考虑到环境中残留的之前的相关信息对测试的影响，如果发现测试代码的执行结果【莫名其妙】，记得在 RabbitMQ 的web 管理系统中将相关内容清除干净，构造一个纯净的测试环境测试。</small>

![rabbitmq](./img/spring-boot-rabbitmq-02.png)

首先对 Topic 规则配置：

```java
/* 两个 Queue */
@Bean
public Queue queue1() { return new Queue("Q1"); }

@Bean
public Queue queue2() { return new Queue("Q2"); }

/* 一个 Exchange */
@Bean
public Exchange exchange() { 
    return new TopicExchange("testTopic"); 
}

/* 三个 Binding：关联 Exchange 和 Queue */
@Bean
public Binding binding1() {
    return BindingBuilder
        .bind(queue1())
        .to(exchange())
        .with("*.orange.*")
        .noargs();
}

@Bean
public Binding binding21() {
    return BindingBuilder
        .bind(queue2())
        .to(exchange())
        .with("*.*.rabbit")
        .noargs();
}

@Bean
public Binding binding22() {
    return BindingBuilder
        .bind(queue2())
        .to(exchange())
        .with("lazy.#")
        .noargs();
}
```

创建两个消费者：

```java
@Component
@RabbitListener(queues = "Q1")
public class C1 {

    private static final Logger log = LoggerFactory.getLogger(C1.class);

    @RabbitHandler
    public void process(String message) {
        log.info("C1: {}", message);
    }

}

@Component
@RabbitListener(queues = "Q2")
public class C2 {

    private static final Logger log = LoggerFactory.getLogger(C2.class);

    @RabbitHandler
    public void process(String message) {
        log.info("C2: {}", message);
    }

}
```

测试：<small>（这里偷了个懒，没有去创建发送者类，直接在 Junit 中使用了 ***`AmqpTemplate`*** 发送消息）</small>。

```java
@Autowired
private AmqpTemplate rabbitTemplate;


@Test
public void demo1() throws InterruptedException {
    rabbitTemplate.convertAndSend("testTopic", "hello.orange", "hello orange");
    rabbitTemplate.convertAndSend("testTopic", "hello.orange.world", "hello orange world");
    rabbitTemplate.convertAndSend("testTopic", "hello.world.rabbit", "hello world rabbit");
    rabbitTemplate.convertAndSend("testTopic", "lazy", "lazy");
    rabbitTemplate.convertAndSend("testTopic", "lazy.good", "good");
    rabbitTemplate.convertAndSend("testTopic", "lazy.good.bye", "goodbye");
    Thread.sleep(1000L);
}
```

# Fanout Exchange

```java
@Bean 
public Queue green() { return new Queue("green"); }

@Bean 
public Queue red() { return new Queue("red"); }

@Bean 
public Queue orange() { return new Queue("orange"); }

@Bean
public FanoutExchange exchange() { return new FanoutExchange("testFanout"); }

@Bean
public Binding binging1() { return BindingBuilder.bind(green()).to(exchange()); }

@Bean
public Binding binging2() { return BindingBuilder.bind(red()).to(exchange()); }

@Bean
public Binding binging3() { return BindingBuilder.bind(orange()).to(exchange()); }
```

```java
@Test
public void demo2() throws InterruptedException {
    rabbitTemplate.convertAndSend("testFanout", "", "green");
    rabbitTemplate.convertAndSend("testFanout", "", "red");
    rabbitTemplate.convertAndSend("testFanout", "", "orange");
    Thread.sleep(1000L);
}
```

Customer-A、Customer-B、Customer-C 都会收到这三条消息，即，控制台会打印出 9 条日志。

# 接收/消费消息（PULL 型）

PULL 型消费意味着需要消费者主动从 RabbitMQ Broker 上【取】消息。

PULL 型消费不依靠 ***`@RabbitListener`*** 和 ***`@RabbitHandler`*** 注解。而是需要在代码中手动调用 *`.receiveAndConvert()`* 方法。

<small>*`.receiveAndConvert()`* 方法是 *`.receive()`* 方法的简化版。</small>

```java
@Test
public void demo5() {
    rabbitTemplate.convertAndSend("queue-demo-1", "hello world");
}

@Test
public void demo4() {
    log.info("{}", rabbitTemplate.receiveAndConvert("queue-demo-1"));
}
```

# 发送者确认

发送者如何知道自己所发送的消费成功抵达了 RabbitMQ Broker 中的 Exchange 中，乃至成功抵达了 RabbitMQ Broker 中的 Queue 中？

<strong>生产者确认</strong>

你可以为 RabbitTemplate 注册两个回调函数，当消息没有成功抵达 Queue 或者 Exchange 时，这两个回调函数会被调用。你<small>（消费生产者）</small>自然就知道消息发生失败了。

```
# 确认消息已发送到交换机（Exchange）
spring.rabbitmq.publisher-confirms=true
# 确认消息已发送到队列（Queue）
spring.rabbitmq.publisher-returns=true
```

<small>虽然可以不样，但是通常这两个属性都是同步开关保持一致的。</small>

在之前的代码中，是 `spring-rabbit` 帮我们创建 ***`ConnectionFactory`***，再进一步创建 ***`RabbitTemplate`***，并注入到我们的代码中进而被我们使用。

现在由于需要对 ***`RabbitTemplate`*** 进行设置，因此，我们需要自己创建并设置 ***`RabbitTemplate`***。<small>（不过，还是需要 spring-rabbit 帮我们创建 Connection Factory，并注入）</small>


```java
@Bean
public RabbitTemplate createRabbitTemplate(ConnectionFactory connectionFactory) {
    RabbitTemplate rabbitTemplate = new RabbitTemplate();
    rabbitTemplate.setConnectionFactory(connectionFactory);

    // 设置开启 Mandatory，才能触发回调函数，无论消息推送结果怎么样都强制调用回调函数
    rabbitTemplate.setMandatory(true);

    // 关键就是以下两句
    rabbitTemplate.setConfirmCallback( ... );
    rabbitTemplate.setReturnCallback( ... );

    return rabbitTemplate;
}
```

你可以使用 lamda 表达式来简化下列匿名实现类。

```java
rabbitTemplate.setConfirmCallback(new RabbitTemplate.ConfirmCallback() {
    @Override
    public void confirm(CorrelationData correlationData, boolean ack, java.lang.String cause) {
        if (ack) {
            log.info("消息已发送至 Exchange");
        } else {
            log.info("消息未能发送到 Exchange。{}", cause);
        }
    }
});

rabbitTemplate.setReturnCallback(new RabbitTemplate.ReturnCallback() {

    @Override
    public void returnedMessage(Message message, int replyCode, String replyText, String exchange, String routingKey) {
        log.info("ReturnCallback 消息：{}", message);
        log.info("ReturnCallback 回应码：{}", replyCode);
        log.info("ReturnCallback 回应信息：{}", replyText);
        log.info("ReturnCallback 交换机：{}", exchange);
        log.info("ReturnCallback 路由键：{}", routingKey);
    }
});
```

你可以向不存在的 Exchange 和 Queue 发送消息已验证效果。


# 消费端的确认与拒绝

默认情况下，RabbitMQ 启用的是消费端自动<small>（auto）</small>回复。即，当消费端收到消息，就会给 RabbitMQ Broker 作出回复，表示已收到。

<strong>只有在消费端回复 RabbitMQ Broker 之后，RabbitMQ Broker 才会将该消息从消息队列中移除。</strong>

回复的行为除了有 AUTO 之外，还有 NONE 和 MANUAL 。

NONE 表示不回复，即，RabbitMQ Broker 永远不可能知道消费者端到底有没有收到消息。RabbitMQ Broker 发出

MANUAL 则意味着需要在消费者端手动发送回复信息。在消费者回复前，该消息在消费端未回复前在 RabbitMQ Brocker 上一直处于**`Unacked`** 状态。

启用消费端的确认功能需要打开配置开关：

```properties
spring.rabbitmq.listener.simple.acknowledge-mode=manual
spring.rabbitmq.listener.direct.acknowledge-mode=manual
```

于此同时，消息消费者的处理方法需要改造成以下形式：

```java

@Component
@RabbitListener(queues = "queue-demo-1")
public class Consumer2 {

    @RabbitHandler
    public void process(String message, 
            Channel channel, 
            @Header(AmqpHeaders.DELIVERY_TAG) long tag) {

        ...

    }
}
```

## 确认消息

确认消息使用 *`channel`* 的 *`.basicAck()`* 方法：

```java
channel.basicAck(tag, false);
```

basicAck 方法需要传递两个参数：

- deliveryTag（唯一标识 ID）：当一个消费者向 RabbitMQ 注册后，会建立起一个 Channel ，RabbitMQ 会用 basic.deliver 方法向消费者推送消息，这个方法携带了一个 delivery tag， 它代表了 RabbitMQ 向该 Channel 投递的这条消息的唯一标识 ID，是一个单调递增的正整数，delivery tag 的范围仅限于 Channel 。

- multiple：为了减少网络流量，手动确认可以被批处理，当该参数为 true 时，则可以一次性确认 delivery_tag 小于等于传入值的所有消息。


## 拒绝消息

拒绝消息使用 *`channel`* 的 *`.basicReject()`* 方法：

```java
channel.basicReject(tag, false);
```

basicReject 方法也需要传力两个参数：

- deliveryTag（唯一标识 ID）：同上。

- requeue（重入标识）：标识该消息是否需要 RabbitMQ Broker 重新入队。<small>（有可能的话，会被该队列的其它消费者消费）。</small>

另外，拒绝的方法还有 *`.basicNack()`*，表示批量拒绝。
