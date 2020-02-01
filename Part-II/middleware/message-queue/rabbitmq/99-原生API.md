<span class="title">原生 API</span>

RabbitMQ Java 客户端使用 *`com.rabbitmq.client`* 包下的内容进行开发，关键的 Class 和 Interface 有：***`Channel`***、***`Connection`***、***`ConnectionFactory`***、***`Consumer`*** 等。

与 RabbitMQ 相关的开发工作，基本上都是围绕 ***`Connection`*** 和 ***`Channel`*** 这两个类展开的：

- AMQP 协议层面的操作通过 Channel 接口实现；
- Connection 用来开启 Channel（信道），可以注册时间处理器，也可以在应用结束时关闭连接。

# 连接 RabbitMQ Broker






