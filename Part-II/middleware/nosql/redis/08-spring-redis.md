<span class="title">Spring 与 Redis 整合</span>

# pom 文件

```xml
<!-- spring-data-redis.version>2.1.14.RELEASE</spring-data-redis.version -->
<spring-data-redis.version>1.8.18.RELEASE</spring-data-redis.version>
<jedis.version>2.9.1</jedis.version>

<dependency>
    <groupId>redis.clients</groupId>
    <artifactId>jedis</artifactId>
    <version>${jedis.version}</version>
</dependency>
<dependency>
    <groupId>org.springframework.data</groupId>
    <artifactId>spring-data-redis</artifactId>
    <version>${spring-data-redis.version}</version>
</dependency>
```

<small>spring-data-redis 2.x 在配置上有大量的变化，有部分 1.x 中的配置属性被标注为过期，此处不深究。</small>

# 与 spring 整合的配置

```properties
redis.host=127.0.0.1
redis.port=6379
redis.pass=123456
redis.maxIdle=200
redis.maxActive=1024
redis.maxWait=10000
redis.testOnBorrow=true
```

```xml
<?xml version="1.0" encoding="UTF-8"?>
<beans xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
       xmlns="http://www.springframework.org/schema/beans"
       xmlns:context="http://www.springframework.org/schema/context"
       xsi:schemaLocation="http://www.springframework.org/schema/beans
        http://www.springframework.org/schema/beans/spring-beans.xsd
        http://www.springframework.org/schema/context
        http://www.springframework.org/schema/context/spring-context.xsd">

    <!-- 连接池基本参数配置，类似数据库连接池 -->
    <context:property-placeholder location="classpath:redis.properties" ignore-unresolvable="true"/>

    <bean id="poolConfig" class="redis.clients.jedis.JedisPoolConfig">
        <property name="maxTotal" value="${redis.maxActive}"/>
        <property name="maxIdle" value="${redis.maxIdle}"/>
        <property name="testOnBorrow" value="${redis.testOnBorrow}"/>
    </bean>

    <!-- 连接池配置，类似数据库连接池 -->
    <bean id="jedisConnectionFactory" class="org.springframework.data.redis.connection.jedis.JedisConnectionFactory">
        <property name="hostName" value="${redis.host}"/>
        <property name="port" value="${redis.port}"/>
        <!-- <property name="password" value="${redis.pass}"/> -->
        <property name="poolConfig" ref="poolConfig"/>
    </bean>


    <bean id="redisTemplate" class="org.springframework.data.redis.core.RedisTemplate">
        <property name="connectionFactory" ref="jedisConnectionFactory" />
        <!--
        <property name="keySerializer" ref="stringRedisSerializer"/>
        <property name="hashKeySerializer" ref="stringRedisSerializer"/>
        -->
    </bean>

<!--
    <bean id="stringRedisSerializer" class="org.springframework.data.redis.serializer.StringRedisSerializer" />
-->

</beans>
```

上述配置中被注解的部分内容涉及到一个较高级的问题：spring-data-redis 的序列化策略。这里简单说下：

spring-data-redis 默认采用的序列化策略有两种，一种是 String 的序列化策略，一种是 JDK 的序列化策略。

## StringRedisTemplate 和 StringRedisSerializer:

- StringRedisTemplate 默认采用的是 String 的序列化策略，即保存的 key 和 value 都是采用此策略序列化保存的。

- Key 或者 value 为字符串的场景，根据指定的 charset 对数据的字节序列编码成 String，是 `new String(bytes, charset)` 和 `string.getBytes(charset)` 的直接封装。是最轻量级和高效的策略。

## RedisTemplate 和 JdkSerializationRedisSerializer:

- RedisTemplate 默认采用的是 JDK 的序列化策略，保存的 key 和 value 都是采用此策略序列化保存的。

- POJO 对象的存取场景，使用 JDK 本身序列化机制，将 pojo 类通过 `ObjectInputStream` / `ObjectOutputStream` 进行序列化操作，最终redis-server 中将存储字节序列。是目前最常用的序列化策略。

## RedisTemplate 和 StringRedisTemplate 不可混用

就是因为 <font color="#0088dd">**默认的**</font> 序列化策略的不同，即使是同一个 key 用不同的 Template 去序列化，结果是不同的。所以根据 key 去删除数据的时候就出现了删除失败的问题。

| 存 | 取/删 | 成功与否 |
| :- | :- | :- |
| StringRedisTemplate | StringRedisTemplate | 成功 |
| RedisTemplate | RedisTemplate | 成功 |
| StringRedisTemplate | RedisTemplate | 失败 |
| RedisTemplate | SpringRedisTemplate | 失败 |

除非，你为两者指定相同的序列化器，即指定同样一种序列化策略。

# 代码验证

```java
public static void main(String[] args) {
    ClassPathXmlApplicationContext container = new ClassPathXmlApplicationContext("spring-redis.xml");

    @SuppressWarnings("unchecked")
    RedisTemplate<String, Object> template = container.getBean(RedisTemplate.class);

    ValueOperations<String, Object> ops1 = template.opsForValue();
    HashOperations<String, String, Object> ops2 = template.opsForHash();

    System.out.println(ops1.get("hello"));
    ops1.set("hello", "world");
    System.out.println(ops1.get("hello"));

    ops2.put("user:1", "name", "tom");
    ops2.put("user:1", "age", 20);
    ops2.put("user:2", "name", "jerry");
    ops2.put("user:2", "age", 19);

    container.close();
}
```