<span class="title">Zookeeper 实现分布式锁</span>

# 原理

Zookeeper 实现分布式锁功能的核心流程在于创建【临时顺序节点 ZNode】以及采用 Watch 监听机制监听临时节点的增减，从而判断当前的线程是否能够获得【锁】。

# Curator Framework

```xml
<dependency>
    <groupId>org.apache.curator</groupId>
    <artifactId>curator-recipes</artifactId>
    <version>4.0.1</version>
    <!-- <version>4.2.0</version> -->
</dependency>
```

Curator 是 Zookeeper 开源的客户端框架，它封装了原生的 Zookeeper API，使用起来更加方便简洁。

curator-framework 的常用 API

| 方法名	| 描述 |
| :- | :- |
| `create()` | 开始创建操作， 可以调用额外的方法（比如方式mode 或者后台执行 background）并在最后调用 `forPath()` 指定要操作的 ZNode |
| `delete()` | 开始删除操作. 可以调用额外的方法（版本 version 或者后台处理 background）<br>并在最后调用 `forPath()` 指定要操作的 ZNode |
| `checkExists() `| 开始检查 ZNode 是否存在的操作. 可以调用额外的方法（监控或者后台处理）<br>并在最后调用 `forPath()` 指定要操作的 ZNode |
| `getData()` | 开始获得 ZNode 节点数据的操作. 可以调用额外的方法（监控、后台处理或者获取状态）<br>并在最后调用 `forPath()` 指定要操作的 ZNode |
| `setData()` | 开始设置 ZNode 节点数据的操作. 可以调用额外的方法（版本或者后台处理）<br>并在最后调用 `forPath()` 指定要操作的 ZNode |
| `getChildren()` | 开始获得 ZNode 的子节点列表。 以调用额外的方法（监控、后台处理或者获取状态），<br>并在最后调用forPath()指定要操作的 ZNode |
| `inTransaction()` | 开始是原子 ZooKeeper 事务. 可以复合 create, setData, check, 和/或 delete 等操作<br>然后调用 `commit()` 作为一个原子操作提交 |


```java
 @Bean
    public CuratorFramework curatorFramework() {
        CuratorFramework curatorFramework = CuratorFrameworkFactory.builder()
                .connectString("127.0.0.1:2181")
                .namespace("...")
            //  .namespace("middleware-distribute-lock")
                .retryPolicy(new RetryNTimes(5, 1000)) // 重试 5 次，间隔 1 秒。
                .build();

        curatorFramework.start();

        return curatorFramework;
    }
```

# Curator 的分布式互斥锁 

Curator 封装了分布式互斥锁的实现，最为常用的是 ***`InterProcessMutex`*** 。

***`InterProcessMutex`*** 基于 Zookeeper 实现了 <strong>分布式的公平可重入互斥锁</strong> 。<small>类似于单个 JVM 进程内的 `ReentrantLock`</small>。


## 构造函数

```java
// 最常用
public InterProcessMutex(CuratorFramework client, String path);
```

## 获取锁

```java
// 无限等待
public void acquire();

// 限时等待
public boolean acquire(long time, TimeUnit unit);
```

## 释放锁

```java
public void release();
```
