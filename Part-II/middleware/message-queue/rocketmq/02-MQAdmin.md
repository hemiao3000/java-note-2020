<span class="title">MQAdmin</span>

MQAdmin 是 RocketMQ 自带的命令行管理工具，在 *`bin`* 目录下，运行 *`mqadmin`* 即可执行。使用 mqadmin 命令，可以进行创建、修改 Topic，更新 Broker 的配置信息，查询特定消息等操作。

## 创建 Topic

消息的发送和接收都需要有对应的 Topic，需要向某个 Topic 发送或接收消息，所以在正式使用 RocketMQ 进行消息发送和接收之前，要先创建 Topic，创建 Topic 指令是 `updateTopic`，如：

```sh
> mqadmin updateTopic \
    -b localhost:10911 \
    -n localhost:9876 \
    -t hello-topic
...
...
create topic to localhost:10911 success.
...
```

参数说明如下:

| 参数 | 必须 | 说明 |
| :- | :-: | :- |
| -b | 是<small>（与 -c 互斥)</small> | Broker 地址。表示该 Topic 建在哪个 Broker 上 |
| -c | 是<small>（与 -b 互斥）</small>| Cluster 名称，表示该 Topic 建立在哪个集群上。<br>该集群下的所有 master 身份的 Broker ，都将建立这个队列。|
| -n | 是 | NameServer 的地址。|
| -t | 是 | Topic 名称 |


## 删除 Topic 

把 RocketMQ 系统中不用的 Topic 彻底清除，指令是 `deleteTopic` 。


```
> mqadmin deleteTopic \
    -c DefultCluster \
    -n localhost:9876 \
    -t test-topic 
...
...
delete topic [test-topic] from NameServer success.
```

| 参数 | 必须 | 说明 |
| :- | :-: | :- |
| -c | 是 | Cluster 名称。所谓的单机，实际上是一个叫 `DefultCluster` 集群下的一台主机 |
| -n | 是 | NameServer 的地址。|
| -t | 是 | Topic 名称 |


## 创建 Topic Group

Topic Group<smalL>（订阅组）</small>在提高系统的高可用性和吞吐量方面扮演者重要角色。<strong>同属于一个消费组中的消费者会并行消费 Topic 中的消息</strong> 。

