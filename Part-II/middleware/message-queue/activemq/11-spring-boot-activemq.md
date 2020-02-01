<span class="title">SpringBoot 中使用 ActiveMQ</span>

# ActiveMQ 介绍

ActiveMQ 是 Apache 软件基金下的一个开源软件，它遵循 JMS 1.1 规范，为企业消息传递提供高可用、出色性能、可扩展、稳定和安全保障的服务。

ActiveMQ 实现了 JMS 规范，并在此之上提供大量额外的特性。ActiveMQ 支持 <strong>队列</strong> 和 <strong>订阅</strong> 两种模式的消息发送方式。

Spring Boot 提供了 ActiveMQ 组件 ***`spring-boot-starter-activemq`***，用来支持 ActiveMQ 在 Spring Boot 体系内使用。

## 添加依赖

主要添加组件：***`spring-boot-starter-activemq`*** 。

```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-activemq</artifactId>
</dependency>
```

## 配置文件

在 *`application.properties`* 中添加配置。

```yml
# 基于内存的 ActiveMQ，在开发/调试阶段建议使用这种方式
spring.activemq.in-memory=true
# 不使用连接池
spring.activemq.pool.enabled=false

# 独立安装的 ActiveMQ，在生产环境推荐使用这种
# spring.activemq.broker-url=tcp://192.168.0.1:61616
# spring.activemq.user=admin
# spring.activemq.password=admin
```

# 队列(Queue)

队列发送的消息，只能被一个消费者接收。

## 创建队列

```java
@Configuration
public class MqConfig {

    @Bean
    public Queue queue() {
        return new ActiveMQQueue("ben.queue");
    }
}
```

使用 @Configuration 注解在项目启动时，定义了一个队列 queue 命名为：`ben.queue` 。


## 消息生产者

创建一个消息的生产者:

```java
@Component
public class Producer {

    @Autowired
    private JmsMessagingTemplate jmsMessagingTemplate;

    @Autowired
    private Queue queue;

    public void sendQueue(String msg) {
        System.out.println("send queue msg :" + msg);
        jmsMessagingTemplate.convertAndSend(queue, msg);
    }
}
```

***`JmsMessagingTemplate`*** 是 Spring 提供发送消息的工具类，使用 JmsMessagingTemplate 和创建好的 queue 对消息进行发送。

## 消息消费者

```java
@Component
public class Consumer {

    @JmsListener(destination = "ben.queue")
    public void receiveQueue(String text) {
        System.out.println("Consumer queue msg : " + text);
    }
}
```

使用注解 `@JmsListener(destination = "ben.queue")`，表示此方法监控了名为 `ben.queue` 的队列。当队列 `ben.queue` 中有消息发送时会触发此方法的执行，text 为消息内容。

## 测试

创建 SampleActiveMqTests 测试类，注入入创建好的消息生生产者。

```java
@RunWith(SpringRunner.class)
@SpringBootTest
public class SampleActiveMqTests {

    @Autowired
    private Producer producer;

    @Rule
    public OutputCapture outputCapture = new OutputCapture();

}
```

*`OutputCapture`* 是 Spring Boot 提供的一个测试类，它能捕获 *`System.out`* 和 *`System.err`* 的输出，我们可以利用这个特性来判断程序中的输出是否执行。

```java
@Test
public void sendSimpleQueueMessage() throws InterruptedException {
    producer.sendQueue("Test queue message");
    Thread.sleep(1000L);
    assertTrue(outputCapture.toString().contains("Test queue"));
}
```

创建测试方式，使用 producer 发送消息，为了保证容器可以接收到消息，让测试方法等待 1 秒，最后使用 outputCapture 判断是否执行成功。

## 测试多消费者

上面的案例只是一个生产者一个消费者，我们在模拟一个生产者和多个消费者队列的执行情况。我们复制上面的消费者 Consumer 重新命名为 Consumer2，并且将输出内容加上 2 的关键字，如下:

```java
@Component
public class Consumer2 {
    @JmsListener(destination = "ben.queue")
    public void receiveQueue(String text) {
        System.out.println("Consumer2 queue msg : "+text);
    }
}
```

在刚才的测试类中添加一个 *`send100QueueMessage()`* 方法，模式发送 100 条消息时，两个消费者是如何消费消息的。

```java
@Test
public void send100QueueMessage() throws InterruptedException {
    for (int i = 0; i < 100; i++) {
        producer.sendQueue("Test queue message" + i);
    }

    Thread.sleep(1000L);
}
```

控制台输出结果:

```
Consumer queue msg : Test queue message0
Consumer2 queue msg : Test queue message1
Consumer queue msg : Test queue message2
Consumer2 queue msg : Test queue message3
...
```

根据控制台输出的消息可以看出，当有多个消费者监听一个队列时，消费者会自动均衡负载的接收消息，并且每个消息只能有一个消费者所接收。

<small>注意：控制台输出 `javax.jms.JMSException: peer (vm://localhost#1) stopped.` 报错信息可以忽略，这是 Info 级别的错误，是 ActiveMQ 的一个 bug。</small>


# 广播（Topic）

广播发送的消息，可以被多个消费者接收。

实现广播模式需要修改如下配置：

```properties
# 默认值为 false，表示 queue 模式
spring.jms.pub-sub-domain=true  
```

## 创建 Topic

```java
@Configuration
public class MqConfig {

    @Bean
    public Topic topic() {
        return new ActiveMQTopic("ben.topic");
    }
}
```

使用 @Configuration 注解在项目启动时，定义了一个广播 Topic 命名为：`ben.topic`。

## 消息生产者

创建一个消息的生产者:

```java
@Slf4j
@Component
public class Producer {

    @Autowired
    private JmsMessagingTemplate jmsMessagingTemplate;

    @Autowired
    private Topic topic;

    public void sendTopic(String msg) {
        log.info("send topic msg : {}", msg);
        jmsMessagingTemplate.convertAndSend(topic, msg);
    }
}
```

和上面的生产者对比只是 convertAndSend() 方法传入的第一个参数变成了 Topic 。

## 消息消费者

```java
@Slf4j
@Component
public class Consumer {

    @JmsListener(destination = "ben.topic")
    public void receiveTopic(String text) {
        log.info("Consumer topic msg : {}", text);
    }
}
```

消费者也没有变化，只是监听的名改为上面的 ben.topic，因为模拟多个消费者，复制一份 Consumer 命名为 Consumer2，代码相同在输出中标明来自 Consumer2 。

## 测试

创建 SampleActiveMqTests 测试类，注入创建好的消息生产者。

```java
@Test
public void sendSimpleTopicMessage() throws InterruptedException {
    producer.sendTopic("Test Topic message");
    Thread.sleep(1000L);
}
```

测试方法执行成功后，会看到控制台输出信息，如下:

```
send topic msg : Test Topic message
Consumer topic msg : Test Topic message
Consumer2 topic msg : Test Topic message
```

可以看出两个消费者都收到了发送的消息，从而验证广播（Topic）是一个发送者多个消费者的模式。

# 同时支持队列（Queue）和广播（Topic）

Spring Boot 集成 ActiveMQ 的项目默认只支持队列或者广播中的一种，通过配置项 *`spring.jms.pub-sub-domain`* 的值来控制，`true` 为广播模式，`false` 为队列模式，默认情况下支持队列模式。

如果需要在同一项目中既支持队列模式也支持广播模式，可以通过 ***`DefaultJmsListenerContainerFactory`*** 创建自定义的 ***`JmsListenerContainerFactory`*** 实例，之后在 ***`@JmsListener`*** 注解中通过 *`containerFactory`* 属性引用它。

分别创建两个自定义的 JmsListenerContainerFactory 实例，通过 pubSubDomain 来控制是支持队列模式还是广播模式。

```java
@Configuration
@EnableJms
public class ActiveMQConfig {

    @Bean("queueListenerFactory")
    public JmsListenerContainerFactory<?> queueListenerFactory(ConnectionFactory connectionFactory) {
        DefaultJmsListenerContainerFactory factory = new DefaultJmsListenerContainerFactory();
        factory.setConnectionFactory(connectionFactory);
        factory.setPubSubDomain(false);
        return factory;
    }

    @Bean("topicListenerFactory")
    public JmsListenerContainerFactory<?> topicListenerFactory(ConnectionFactory connectionFactory) {
        DefaultJmsListenerContainerFactory factory = new DefaultJmsListenerContainerFactory();
        factory.setConnectionFactory(connectionFactory);
        factory.setPubSubDomain(true);
        return factory;
    }
}
```

然后在消费者接收的方法中，指明使用 ***`containerFactory`*** 接收消息。

```java
@Slf4j
@Component
public class Consumer {
    @JmsListener(destination = "ben.queue", containerFactory = "queueListenerFactory")
    public void receiveQueue(String text) {
        log.info("Consumer queue msg : {}", text);
    }

    @JmsListener(destination = "ben.topic", containerFactory = "topicListenerFactory")
        public void receiveTopic(String text) {
        log.info("Consumer topic msg : {}", text);
    }
}
```

改造完成之后，再次执行队列和广播的测试方法，就会发现项目同时支持了两种类型的消息收发。
