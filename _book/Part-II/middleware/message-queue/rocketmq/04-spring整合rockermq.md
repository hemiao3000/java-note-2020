# Spring 整合 RocketMQ

Spring 整合 RocketMQ 有两种<small>（三种）</small>方案：

1. 将消息生产者和消息消费者定义成 bean 对象交由 Spring 容器管理；

2. 使用 RocketMQ 社区的 rocketmq-jms，通过 spring-jms 方式整合。

3. 如果是在 sprig-boot 项目中整合 RocketMQ，可以使用 *`rocketmq-spring-boot-starter`* 。

由于 <strong>RocketMQ 并未完全遵守 JMS 规范</strong>，因此通过 *`rocketmq-jms`* 整合 RocketMQ 虽然更为简便，但是无法发挥 RocketMQ 的全部功能<small>（只能使用 RocketMQ 符合 JMS 规范的那一部分功能）</small>。因此，<storng>从 API 的使用上看，最灵活的还是第一种方式。</strong>

```xml
<dependency>
    <groupId>org.apache.rocketmq</groupId>
    <artifactId>rocketmq-client</artifactId>
    <version>4.6.0</version>
</dependency>
```

# 消息生产者

```java
public class SimpleProducer {

    private static final Logger log = LoggerFactory.getLogger(SimpleProducer.class);

    private String producerGroupName;

    private String nameServerAddr;

    private DefaultMQProducer producer;

    private SimpleProducer() {
    }

    public SimpleProducer(String producerGroupName, String nameServerAddr) {
        this.producerGroupName = producerGroupName;
        this.nameServerAddr = nameServerAddr;
    }

    public void init() throws Exception {
        log.info("开始启动消息生产者服务...");
        // 创建一个消息生产者，并设置一个消息生产者组
        producer = new DefaultMQProducer(producerGroupName);
        // 指定 NameServer 地址
        producer.setNamesrvAddr(nameServerAddr);
        // 初始化 SpringProducer，在整个应用生命周期内只需要初始化一次
        producer.start();
        log.info("生产者服务启动成功");
    }

    public void destroy() {
        log.info("开始关闭消息生产者服务...");
        producer.shutdown();
        log.info("消息生产者服务已关闭.");
    }

    public DefaultMQProducer getProducer() {
        return producer;
    }
}
```

```java
@Bean(initMethod = "init", destroyMethod = "destroy")
public SimpleProducer producer() {
    return new SimpleProducer(
                "spring-producer-group",
                "127.0.0.1:9876");
}
```

# 消息消费者

```java
public class SimpleConsumer {

    private static final Logger log = LoggerFactory.getLogger(SimpleConsumer.class);

    private String consumerGroupName;
    private String nameServerAddr;
    private String topicName;
    private DefaultMQPushConsumer consumer;
    private MessageListenerConcurrently messageListener;

    private SimpleConsumer() {
    }

    public SimpleConsumer(String consumerGroupName, String nameServerAddr, String topicName, MessageListenerConcurrently messageListener) {
        this.consumerGroupName = consumerGroupName;
        this.nameServerAddr = nameServerAddr;
        this.topicName = topicName;
        this.messageListener = messageListener;
    }

    public void init() throws Exception {
        log.info("开始启动消息消费者服务...");

        // 创建一个消息消费者，并设置一个消息消费者组
        consumer = new DefaultMQPushConsumer(consumerGroupName);
        // 指定 NameServer 地址
        consumer.setNamesrvAddr(nameServerAddr);
        // 设置 Consumer 第一次启动时是从队列头部，还是队列尾部开始消费
        consumer.setConsumeFromWhere(ConsumeFromWhere.CONSUME_FROM_FIRST_OFFSET);
        // 订阅指定 Topic 下的所有消息
        consumer.subscribe(topicName, "*");

        // 注册消息监听器
        consumer.registerMessageListener(messageListener);

        // 消费者对象在使用之前必须要调用 start 方法初始化
        consumer.start();

        log.info("消息消费者服务启动成功.");
    }

    public void destroy() {
        log.info("开始关闭消息消费者服务...");
        consumer.shutdown();
        log.info("消息消费者服务已关闭.");
    }

    public DefaultMQPushConsumer getConsumer() {
        return consumer;
    }
}
```

```java
public class MessageListener implements MessageListenerConcurrently {

    private static final Logger log = LoggerFactory.getLogger(MessageListener.class);

    @Override
    public ConsumeConcurrentlyStatus consumeMessage(List<MessageExt> msgs, ConsumeConcurrentlyContext context) {
        if (msgs != null) {
            for (MessageExt ext : msgs) {
                try {
                    log.info("监听到消息 : {}", new String(ext.getBody(), RemotingHelper.DEFAULT_CHARSET));
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
            }
        }

        return ConsumeConcurrentlyStatus.CONSUME_SUCCESS;
    }
}
```

```java
@Bean(initMethod = "init", destroyMethod = "destroy")
public SimpleConsumer consumer() {
    return new SimpleConsumer(
            "spring-consumer-group",
            "localhost:9876",
            "spring-rocketMq-topic",
            new MessageListener());
}
```

# 测试代码

```java
@SpringBootTest
@RunWith(SpringJUnit4ClassRunner.class)
public class SpringRocketmqApplicationTests {

	@Autowired
	private SimpleProducer producer;

	@Before
	public void before() {
		Assert.assertNotNull(producer);
	}

	@Test
	public void contextLoads() throws InterruptedException, RemotingException, MQClientException, MQBrokerException {
		producer.getProducer().send(new Message("spring-rocketMq-topic", "hello world".getBytes(Charset.defaultCharset())));
	}

}
```
