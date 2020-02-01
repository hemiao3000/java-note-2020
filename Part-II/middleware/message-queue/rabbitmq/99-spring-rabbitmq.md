<span class="title">Spring 整合 RabbitMQ</span>

[官网文档](https://docs.spring.io/spring-amqp/docs/2.1.13.RELEASE/reference/html/#message-converters)

# 原生 API 

```xml
<dependency>
    <groupId>com.rabbitmq</groupId>
    <artifactId>amqp-client</artifactId>
    <version>5.1.2</version>
</dependency>
```

*`amqp-client`* 包中提供了原生的访问 RabbitMQ 的 API 。使用流程如下：


1. 创建并配置 ***`ConnectionFactory`*** 。
2. 通过 ***`ConnectionFactory`*** 获取 ***`Connection`*** 对象。
3. 通过 ***`Connection`*** 对象获取 ***`Channel`*** 对象。
4. 通过 ***`Channel`*** 对象向 RabbitMQ Broker 收发消息。
5. 使用结束后，关闭 channel 和  connection 。


# spring-rabbit 和 spring-amqp

```xml
<dependency> <!-- 自动依赖 spring-amqp -->
    <groupId>org.springframework.amqp</groupId>
    <artifactId>spring-rabbit</artifactId>
    <version>2.0.12.RELEASE</version>
    <exclusions><!-- 它依赖的 spring 核心库的版本可能和你所使用的不一样 -->
        <exclusion>
            <artifactId>spring-beans</artifactId>
            <groupId>org.springframework</groupId>
        </exclusion>
        <exclusion>
            <artifactId>spring-context</artifactId>
            <groupId>org.springframework</groupId>
        </exclusion>
        <exclusion>
            <artifactId>spring-core</artifactId>
            <groupId>org.springframework</groupId>
        </exclusion>
    </exclusions>
</dependency>
```

spring 提供了 spring-rabbit 和  spring-amqp 用来整合 RabbitMQ 。在 spring-amqp 中提供了 AmqpTemplate 来简化对 RabbitMQ 的使用。<small>RabbitTemplate 是  AmqpTemplate 的实现类。</small>

AmqpTemplate 简化了上面的配置和使用：

1. 创建并配置 ***`ConnectionFactory`*** 。
2. 通过 ***`ConnectionFactory`*** 获取 ***`AmqpTemplate`*** / ***`RabbitTemplate`*** 对象。
3. 通过 ***`Template`*** 对象向 RabbitMQ Broker 收发消息。

# 创建 RabbitTemplate

```java
@Bean
public ConnectionFactory amqpConnectionFactory() {

    CachingConnectionFactory connectionFactory = new CachingConnectionFactory();
    connectionFactory.setAddresses("localhost:5672");
    connectionFactory.setUsername("guest");
    connectionFactory.setPassword("guest");
    connectionFactory.setVirtualHost("/");

    return connectionFactory;
}
```

创建 RabbitTemplate<small>（AmqpTemplate 的实现类）</small> 的前提是创建 ***`ConnectionFactory`*** 对象。

需要注意的是，在我们引入的包中存在两种 ConnectionFactory：
- `com.rabbitmq.client.ConnectionFactory` 
- `org.springframework.amqp.rabbit.connection.ConnectionFactory`
 
<strong>我们这里使用的是后者</strong>。

***`CachingConnectionFactory`*** 是 ConnectionFactory 接口的实现类。

ConnectionFactory 有很多属性需要进行设置，其中最关键的是上述四项：

| # | 配置项 | 说明 |
| :-: | :- | :- |
| 1 | Addresses | RabbitMQ 服务器 URL |
| 2 | Username | 登陆用户名 |
| 3 | Password | 登陆密码 |
| 4 | VirtualHost | 所连接的虚拟主机 |

至于其它更多的配置项，如果不指定则使用的是其默认值<small>（后续陆续介绍）</small>。

创建 ConnectionFactory 之后，就可以以他为依据创建 Template 对象：

```java
@Bean
public RabbitTemplate rabbitTemplate(ConnectionFactory amqpConnectionFactory) {

    RabbitTemplate rabbitTemplate = new RabbitTemplate();
    rabbitTemplate.setConnectionFactory(amqpConnectionFactory);
    return rabbitTemplate;
}
```

# 创建 Exchange、Queue 和 Binding

需要在代码中通过 spring-rabbit 去创建 Exchange、Queue、和 Binding 的话，那么需要多配置一个 RabbitAdmin：

```java
@Bean
public RabbitAdmin rabbitAdmin(ConnectionFactory connectionFactory) {
    return new RabbitAdmin(connectionFactory);
}
```

虽然我们不会直接使用到它，但是 spring-rabbit 要用它来创建 Exchange、Queue 和 Binding 。

## 创建 Queue

## 创建 Binding

# 发送消息

## Send API

发送消息使用下列 API：

```java
void send(Message message) 

void send(String routingKey, Message message) 

void send(String exchange, String routingKey, Message message) 
```

在没有指定 EXchange 的情况下，该消息是发送给【默认】Exchange，默认 Exchange 是一个 Direct 类型的 Exchange。它以消息队列的 name 作为 Binding Key 。

在上述 API 中会设计一个 Message 类，它代表着【消息】。

```java
public class Message {

    private final MessageProperties messageProperties;

    private final byte[] body;

    public Message(byte[] body, MessageProperties messageProperties) {
        this.body = body;
        this.messageProperties = messageProperties;
    }

    public byte[] getBody() {
        return this.body;
    }

    public MessageProperties getMessageProperties() {
        return this.messageProperties;
    }
}
```

从其定义中可以看到，一个【消息】包含消息体和消息属性。spring-rabbit 提供了一个 MessageBuilder 类用来帮助我们构造 Message 对象：

```java
public static MessageBuilder withBody(byte[] body) 

public static MessageBuilder withClonedBody(byte[] body) 

public static MessageBuilder withBody(byte[] body, int from, int to) 

public static MessageBuilder fromMessage(Message message) 

public static MessageBuilder fromClonedMessage(Message message) 
```

示例代码如下：

```java
Message message = MessageBuilder.withBody("foo".getBytes())
    .setContentType(MessageProperties.CONTENT_TYPE_TEXT_PLAIN)
    .setMessageId("123")
    .setHeader("bar", "baz")
    .build();

MessageProperties props = MessagePropertiesBuilder.newInstance()
    .setContentType(MessageProperties.CONTENT_TYPE_TEXT_PLAIN)
    .setMessageId("123")
    .setHeader("bar", "baz")
    .build();
Message message = MessageBuilder.withBody("foo".getBytes())
    .andProperties(props)
    .build();
```

## convertAndSend API 和  Converter

通常情况下，我们不太可能直接使用 *`.send()`* 方法<small>（因为使用它你需要先构建 Message 对象）</small>。*`spring-rabbit`* 提供了 *`.convertAndSend()`* 方法来简化发送消息。


```java
void convertAndSend(Object message)

void convertAndSend(String routingKey, Object message)

void convertAndSend(String exchange, String routingKey, Object message)
    throws AmqpException;
```

这些 API 的背后有一个被成为 **`Message Converters`** 的东西在帮你将你传给这些 API 的对象转换成可发送的内容。

*`spring-rabbit`* 默认使用的转换器是 ***`SimpleMessageConverter`***，它能将字符串和实现了 Serializable 接口的类的对象转换成字节数组<small>（及反向转换）</small>。


# 接收消息

在消息队列的理论中，有两种接收消息的方式：推<small>（PULL）</small>模式和拉<small>（PUSH）</small>模式。

<strong>推模式</strong> 指的是当 Rabbit Broker 收到消息后，主动将消息推送给消息的消费者，【触发】消费者对应方法的执行。

<strong>拉模式</strong> 指的是由消息的消费者自己主动去 Rabbit Broker 查询/获取消息。


## @RabbitListener 和 @RabbitHandler 注解




## receive 和 receiveAndConvert 方法





