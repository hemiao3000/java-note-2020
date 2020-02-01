<intput type="hidden" value="Spring 整合 ActiveMQ" />

在实际项目中，如果使用原生的 ActiveMQ API 开发显得十分啰嗦，这中间 `创建连接工厂`、`创建连接` 之类的代码完全可以抽出来由框架统一做。

<small>实话实说，Spring 整合 ActiveMQ 的相关配置也比较啰嗦... Spring Boot 整合 ActiveMQ 倒是十分简洁。</small>

# 启动 ActiveMQ

略。

或者使用 <font color="#0088dd">**内嵌 ActiveMQ**</font> 。只需要修改连接协议，使用 `vm` 开头即可。

# 引入依赖

!FILENAME pom.xml
```xml
<dependency>
  <groupId>org.apache.activemq</groupId>
  <artifactId>activemq-all</artifactId>
  <version>${activemq.version}</version>
</dependency>
<dependency>
  <groupId>com.fasterxml.jackson.core</groupId>
  <artifactId>jackson-databind</artifactId>
  <version>${jackson.version}</version>
</dependency>

<dependency>
  <groupId>org.apache.activemq</groupId>
  <artifactId>activemq-pool</artifactId>
  <version>${activemq.version}</version>
</dependency>
<dependency>
  <groupId>org.springframework</groupId>
  <artifactId>spring-jms</artifactId>
  <version>${spring.version}</version>
</dependency>
```

前两个包和 `Java 访问 ActiveMQ` 中所引入的是一样的，后两个包是 Spring 整合 ActiveMQ 所需要的『多』出来的。

# spring-activemq.xml


!FILENAME part-1
```xml
<?xml version="1.0" encoding="UTF-8"?>
<beans xmlns="http://www.springframework.org/schema/beans"
    xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
    xmlns:context="http://www.springframework.org/schema/context"
    xsi:schemaLocation="
      http://www.springframework.org/schema/beans
      http://www.springframework.org/schema/beans/spring-beans.xsd
      http://www.springframework.org/schema/context
      http://www.springframework.org/schema/context/spring-context.xsd">

  <bean id="jmsFactory" class="org.apache.activemq.pool.PooledConnectionFactory" destroy-method="stop">
    <property name="connectionFactory">
      <bean class="org.apache.activemq.ActiveMQConnectionFactory">
        <property name="brokerURL" value="vm://localhost:61616"/>
      </bean>
    </property>
    <property name="maxConnections" value="100"/>
  </bean>

  <bean id="cachingConnectionFactory" class="org.springframework.jms.connection.CachingConnectionFactory">
    <property name="targetConnectionFactory"  ref="jmsFactory"/>
    <property name="sessionCacheSize" value="1"/>
  </bean>

  <bean id="jmsTemplate" class="org.springframework.jms.core.JmsTemplate">
    <property name="connectionFactory" ref="cachingConnectionFactory"/>
    <property name="messageConverter">
      <bean class="org.springframework.jms.support.converter.SimpleMessageConverter"/>
    </property>
  </bean>

  ...

</beans>
```

`part-1` 中配置了 ActiveMQ 的连接工厂，由于连接、会话、消息生产者的创建会消耗大量系统资源，为此这里使用了连接池和缓存来复用这些资源。这也是为什么在 pom 的依赖中会『多出来』一个 `activemq-pool` 。


!FILENAME part-2
```xml
...

<bean id="jmsTemplate" class="org.springframework.jms.core.JmsTemplate">
  <property name="connectionFactory" ref="cachingConnectionFactory"/>
  <property name="messageConverter">
    <bean class="org.springframework.jms.support.converter.SimpleMessageConverter"/>
  </property>
</bean>

...
```

*`part-2`* 中配置了 Spring 整合 ActiveMQ 后，我们在代码中所要使用的核心对象：*`JmsTemplate`* 。

在后续的代码中，消息的生产者/发布者就是直接通过 *`JsmTemplate`* 发送消息。


!FILENAME part-3
```xml
...

<bean id="testQueue" class="org.apache.activemq.command.ActiveMQQueue">
  <constructor-arg name="name" value="spring-queue"/>
</bean>

<bean id="queueListener" class="org.example.listener.QueueListener"/>

<bean id="queueContainer" class="org.springframework.jms.listener.DefaultMessageListenerContainer">
  <property name="connectionFactory" ref="cachingConnectionFactory"/>
  <property name="destination" ref="testQueue"/>
  <property name="messageListener" ref="queueListener"/>
</bean>

...
```

*`part-3`* 是以队列为信息载体的点对点模式下的队列的相关配置。它配置了:

- 定义了一个消息队列：*`spring-queue`*
- 定义了一个队列的监听者，即消息的消费者。<small>这个类是需要我们编码自定义的。具体代码的相关写法看后续内容。</small>
- 定义了一个用于 spring-jms 整合的队列容器。<small>这是配置出来给 s
- pring 整合 jms 的，我们的编码工作不涉及到它。</small>

!FILENAME part-4
```xml
...

<bean id="testTopic" class="org.apache.activemq.command.ActiveMQTopic">
  <constructor-arg index="0" value="spring-topic"/>
</bean>

<bean id="topic1Listener" class="org.example.listener.Topic1Listener"/>
<bean id="topic2Listener" class="org.example.listener.Topic2Listener"/>
    
<bean id="topic1Container" class="org.springframework.jms.listener.DefaultMessageListenerContainer">
  <property name="connectionFactory" ref="cachingConnectionFactory"/>
  <property name="destination" ref="testTopic"/>
  <property name="messageListener" ref="topic1Listener"/>
</bean>

<bean id="topic2Container" class="org.springframework.jms.listener.DefaultMessageListenerContainer">
  <property name="connectionFactory" ref="cachingConnectionFactory"/>
  <property name="destination" ref="testTopic"/>
  <property name="messageListener" ref="topic2Listener"/>
</bean>

...
```

*`part-4`* 这部分和 *`part-3`* 看起来很类似。很明显，它配置了：

- 定义了一个主题：`spring-topic`
- 定义了两个主题监听者，即，两个消费者/订阅者。<small>毫无疑问，这两个类也是需要我们编码自定义的。</small>
- 定义了两个主题容器。<small>同样，这是为 spring 整合 jms 用的。与我们自己的编码无关。</small>

# 定义队列消费者

!FILENAME QueueListener
```java
public class QueueListener implements MessageListener {

    private static final Logger log = LoggerFactory.getLogger(QueueListener.class);

    @Override
    public void onMessage(Message message) {
        if (!(message instanceof TextMessage)) {
            throw new IllegalArgumentException("只支持 Text 消息");
        }

        TextMessage textMessage = (TextMessage) message;
        try {
            String messageStr = textMessage.getText();
            log.info("QueueListener 收到了消息: {}", messageStr);
        } catch (JMSException e) {
            e.printStackTrace();
        }
    }
}
```

# 定义主题订阅者


*`Topic2Listener`* 和 *`Topic1Listener`* 一样。

!FILENAME Topic1Listener
```java
public class Topic1Listener implements MessageListener {

    private static final Logger log = LoggerFactory.getLogger(Topic1Listener.class);

    @Override
    public void onMessage(Message message) {
        if (!(message instanceof TextMessage)) {
            throw new IllegalArgumentException("只支持 Text 消息");
        }

        TextMessage textMessage = (TextMessage) message;
        try {
            String messageStr = textMessage.getText();
            log.info("Topic1Listener 收到了消息: {}", messageStr);
        } catch (JMSException e) {
            e.printStackTrace();
        }
    }
}
```

# 消息生产者发送消息

```java
public static void main(String[] args) throws InterruptedException {

  ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext("spring-activemq.xml");

  JmsTemplate template = context.getBean(JmsTemplate.class);
  Queue queue = context.getBean(Queue.class);
  Topic topic = context.getBean(Topic.class);

  template.send(topic, session -> session.createTextMessage("发给 Topic 的消息"));
  template.send(queue, session -> session.createTextMessage("发给 Queue 的消息"));

  Thread.sleep(3*1000);

  context.close();
}
```