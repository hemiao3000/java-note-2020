<intput type="hidden" value="Java 访问 ActiveMQ" />

在 JMS 规范中传递消息的方式有两种：

- <strong>点对点</strong> 模型的 <strong>队列</strong> 方式；
- <strong>发布/订阅</strong> 模型的 <strong>主题</strong> 方式。

<small>由于不太可能直接通过 Java 代码访问操作 ActiveMQ，（更多的是通过整合 Spring 后，通过 Spring 操作访问 ActiveMQ），因此以下仅以主题方式传递为例。</small>

# 启动 ActiveMQ

略

# 引入依赖

```xml
<dependency>
  <groupId>org.apache.activemq</groupId>
  <artifactId>activemq-all</artifactId>
  <version>${activemq.version}</version>
</dependency>

<dependency> <!-- 依赖于它 -->
  <groupId>com.fasterxml.jackson.core</groupId>
  <artifactId>jackson-databind</artifactId>
  <version>${jackson.version}</version>
</dependency>
```

# 消息生产者

!FILENAME 消费者/消息订阅者
```java
import org.apache.activemq.*;
import javax.jms.*;

public class TopicSubscriber {

  // 默认用户名
  private static final String USERNAME = ActiveMQConnection.DEFAULT_USER;
  // 默认密码
  private static final String PASSWORD = ActiveMQConnection.DEFAULT_PASSWORD;
  // 默认连接地址。记得要启动ActiveMQ 服务器
  private static final String BROKER_URL = ActiveMQConnection.DEFAULT_BROKER_URL;
  // Topic 的名称
  private static final String TOPIC_NAME = "activemq-topic";

  public static void main(String[] args) throws Exception {
  // 创建连接工厂
  ConnectionFactory factory = new ActiveMQConnectionFactory(USERNAME, PASSWORD, BROKER_URL);
  // 创建连接
  Connection connection = factory.createConnection();
  // 开启连接。不要忘记这一步。
  connection.start();
  // 创建会话，不需要事务，自动确认。事务和消息确认机制后续专项讲解。
  Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
  // 创建主题。
  Topic testTopic = session.createTopic(TOPIC_NAME);

  // 消息消费者1。
  MessageConsumer consumer1 = session.createConsumer(testTopic);
  // 注册消息监听器。即注册接收消息后所执行的代码。
  consumer1.setMessageListener(message -> {
    try {
      System.out.println("消费者1收到消息：" + ((TextMessage) message).getText());
    } catch (JMSException e) {
      e.printStackTrace();
    }
  });

  // 消息消费者2。
  MessageConsumer consumer2 = session.createConsumer(testTopic);
  // 注册消息监听器。即注册接收消息后所执行的代码。
  consumer2.setMessageListener(message -> {
    try {
      System.out.println("消费者2收到消息：" + ((TextMessage) message).getText());
    } catch (JMSException e) {
      e.printStackTrace();
    }
  });

  // 让主线程休眠 30s，使消息消费者对象
  Thread.sleep(30 * 1000);

  // 关闭资源
  session.close();
  connection.close();
  }
}
```

!FILENAME 生产者/消息发布者
```java
import org.apache.activemq.*;
import javax.jms.*;

public class TopicProducer {

  // 默认用户名
  private static final String USERNAME = ActiveMQConnection.DEFAULT_USER;
  // 默认密码
  private static final String PASSWORD = ActiveMQConnection.DEFAULT_PASSWORD;
  // 默认连接地址。记得要启动ActiveMQ 服务器
  private static final String BROKER_URL = ActiveMQConnection.DEFAULT_BROKER_URL;
  // Topic 的名称
  private static final String TOPIC_NAME = "activemq-topic";

  public static void main(String[] args) throws Exception {
    // 创建连接工厂
    ConnectionFactory factory = new ActiveMQConnectionFactory(USERNAME, PASSWORD, BROKER_URL);
    // 创建连接
    Connection connection = factory.createConnection();
    // 开启连接。不要忘记这一步。
    connection.start();
    // 创建会话，不需要事务，自动确认。事务和消息确认机制后续专项讲解。
    Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
    // 创建主题。
    Topic testTopic = session.createTopic(TOPIC_NAME);

    // 消息生产者。
    MessageProducer producer = session.createProducer(testTopic);

    for (int i = 0; i < 10; i++) {
      // 创建消息
      TextMessage message = session.createTextMessage("发送消息-" + i);
      // 发送消息
      producer.send(testTopic, message);
    }

    // 关闭资源
    session.close();
    connection.close();
  }
}
```

# 运行

先运行消费者/订阅者，后运行生产者/发布者。

如果反过来的话，消息先发布出去，但没有任何订阅者在运行，则将看不到消息被消费。