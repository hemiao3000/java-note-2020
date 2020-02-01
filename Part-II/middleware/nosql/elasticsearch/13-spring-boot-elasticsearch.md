<span class="title">Spring Boot 整合 ElasticSearch</span>

ElasticSearch 是一个开源的搜索引擎，建立在一个全文搜索引擎库 Apache Lucene 基础之上。<small>（Lucene 可以说是当下最先进、高性能、全功能的搜索引擎库。）</small>

ElasticSearch 使用 Java 编写的，它的内部使用的是 Lucene 做索引与搜索，它的目的是使全文检索变得简单<small>（因为 Lucene 只是个库）</small>，通过隐藏 Lucene 的复杂性，取而代之提供了一套简单一致的 RESTful API 。

然而，ElasticSearch 不仅仅是 Lucene 的一层『壳子』，并且也不仅仅只是一个全文搜索引擎，更多的时候它被当作 NoSQL 数据库来使用。


- [Elastic Search 在 Windows 上的安装](/Soft-install/windows/ElasticSearch-解压版.md)

- [通过 Docker 安装 Elastic Search](/Soft-install/docker/03-docker-elasticsearch.md)

- [Elastic Search 的素材](/NoSQL/Elasticsearch/ES-99-素材.md)

- [Elastic Search 的查](/NoSQL/Elasticsearch/ES-04-操作文档-查.md)


# 版本问题

Spring Data ElasticSearch 和 ElasticSearch 是有对应关系的，不同的版本之间不兼容。

具体版本对应关键见[最后](#%e9%99%84-%e7%89%88%e6%9c%ac%e5%af%b9%e5%ba%94%e5%85%b3%e7%b3%bb)

简而言之：

1. 不要再使用 `ES 5`，因为太旧；
2. 优先考虑使用 `ES 6`，因为 `ES 7` 太新；
3. 优先考虑使用 `spring-boot 2.2.x`，因为支持 `ES 6` 的同时还支持 `ES 7`
4. 如果确定是使用 `spring-boot 2.1.x`，那么一定要使用 `ES 6`，因为既不支持 `ES 5`，也不支持 `ES 7` 。

以截至目前为止<small>（2019-11-23）</small>的最高的 6 版本 *`6.8.5`* 为例。Spring boot *`2.2.1`* 和 Spring boot *`2.1.7`* 均可访问操作。


# 回顾 Elasticsearch 搜索功能

Elastic Search 搜索没命中的原因无非关系到三点：

1. 中文分词器
2. document 的字段类型
3. 执行的是 term 查询，还是 match 查询

## 中文分词器的影响

text 类型的字符串数据存储进 Elastic Search 时，会被分词器分词。如果没有指定分词器，那么 Elastic Search 使用的就是默认的 Starndar 分词器。

以 `张三丰` 为例，<font color="#0088dd">**默认的分词器**</font> 会将其分为 `张`、`三` 和 `丰`<small>（注意，甚至都没有 `张三丰`!）</small>，此时，你以 `张三丰` 作为条件，进行 term 查询，Elastic Search 会告诉你：没有。因为 `张`、`三` 和 `丰` 都不是 `张三丰` 。

## document 的字段类型

Elastic Search 中的字符串类型<small>（以前有 String，现在细）</small>分为 `text` 和 `keyword` 两种 。

text 类型的字符串数据会被分词器分词，细化为多个 term；而 keyword 类型的字符串数据，则不会被分词器分词，或者说整体就是有且仅有的一个 term 。<small>当然，至于 string 被分词器分为 term 的结果科不科学？给不给力？合不合理？那就是另一码事了，见上文。</small>

以 `张三丰` 为例<small>（假设使用了 IK 中文分词器）</small>，以 text 类型存储时，会被分词器分为 `张三丰`、`张三`、`三丰`、`张`、`三`、`丰` 这好几个 term，也就是说，但凡以上述的哪个 term 作为关键字<small>（例如，`张三`）</small>进行搜索，都能定位到这条数据。

但是 `张三丰` 以 keyword 类型存储时，分词器不参与其中工作，`张三丰` 就是一个整体，`张三`、`三丰`、`张`、`三`、`丰` 跟它半毛钱关系都没有。

## term 查询和 match 查询

进行查询时，你提供的查询的关键字/线索，也有可能被分词器进行分词，这取决于你进行的是 term 查询，还是 match 查询。

你进行 term 查询时，你提供的线索，<small>例如 `张三丰`，</small>不会被分词器分词，也即是说，Elastic Search 就是实实在在地拿 `张三丰` 作为条件，去数据库中查找数据。

简单来说，你提供的一个查询线索就是一个查询线索。

你进行 match 查询时，你提供的线索，<small>还是 `张三丰`</small>，分词器会对其分词，也就是说，你以为你是在以 `张三丰` 为线索进行查询？不，实际上 Elastic Search 是分别以 `张三丰`、`张三`、`三丰`、`张`、`三`、`丰` 这几个，去数据库中查了好几次。

简单来说，<strong>你以为你提供的是一个查询线索，实际上是好几个</strong>。这样的查询命中自然就高了<small>（至于命中的是不是你想要的，那就是另一个问题了）</small>。

想知道中文字符串被分词器解析出几个词项，可以通过下面命令简介知道：

```js
POST _analyze
{
    "analyzer" : "ik_max_word 或 ik_smart",
    "text" : "内容"
}
```

# Spring Boot 集成 ElasticSearch

![spring-boot-es-01](./img/spring-boot-es-01.png)

!FILENAME pom 中的依赖
```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-data-elasticsearch</artifactId>
</dependency>
```

配置 ElasticSearch 集群地址：

```properties
# 集群名(默认值: elasticsearch，配置文件`cluster.name`: es-mongodb)
spring.data.elasticsearch.cluster-name=elasticsearch
# 集群节点地址列表，用逗号分隔
spring.data.elasticsearch.cluster-nodes=localhost:9300
```

```java
@Document(indexName = "customer", type = "customer", shards = 1, replicas = 0)
public class Customer {

    @Id
    @Field(type = FieldType.Keyword)
    private String id;

    @Field(type = FieldType.Keyword)
    private String userName;

    @Field(type = FieldType.Text, analyzer = "ik_max_word")
    private String address;

    @Field(type = FieldType.Integer)
    private int age;

    // getter / setter

}
```

- `@Document`：注解会对实体中的所有属性建立索引
- `indexName = "customer"`：表示创建一个名称为 "customer" 的索引
- `type = "customer"` 表示在索引中创建一个名为 "customer" 的 type
- `shards = 1` 表示只使用一个分片
- `replicas = 0` 表示不使用复制备份
- `@Field(type = FieldType.Keyword)` 用以指定字段的数据类型

# 创建操作的 repository

```java
@Repository
public interface CustomerRepository extends ElasticsearchRepository<Customer, String> {

    List<Customer> findByAddress(String address);   // 等同于 termQuery 。

    Page<Customer> findByAddress(String address, Pageable pageable);

    Customer findByUserName(String userName);

    public int deleteByUserName(String userName);

}
```

![spring-boot-es-02](./img/spring-boot-es-02.png)

我们自定义的 CustomerRepository 接口，从它的祖先们那里继承了大量的现成的方法，除此之外，它还可以按 spring data 的规则定义特定的方法。

# 测试 CustomerRepository

```java
@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest
public class CustomerRepositoryTest {

    @Autowired
    private CustomerRepository repository;

    @Test
    public void svaeCustomers() {
        repository.save(new Customer("Alice", "湖北武汉", 13));
        repository.save(new Customer("Bob", "湖北咸宁", 23));
        repository.save(new Customer("Neo", "湖北黄石", 30));
        repository.save(new Customer("Summer", "湖北襄阳", 22));
        repository.save(new Customer("Tommy", "湖北宜昌", 22));
        repository.save(new Customer("Jerry", "湖北荆州", 22));
        repository.save(new Customer("关羽", "大意失荆州", 22));
    }
}
```

很显然，上面执行的是插入操作<small>（在此之前还执行了建库建表操作）</small>。

需要注意的时，由于我们没有指定 id 属性的值，这意味着，我们希望 Elasticsearch 来为每一个文档的 id 属性赋值。不过这个属性是文档的原属性 `_id`，而不是 `id`。从 Kibana 中可以看到 `id` 属性的值是 null 。

如果我们指定了 id 属性值，那么，文档的原属性 `_id` 和我们自己的 `id` 属性值是一样的，都是这个我们指定的这个值。

!FILENAME 查询所有
```java
for (Customer customer : repository.findAll()) {
    log.info("{}", customer);
}
```

!FILENAME 删除
```java
repository.deleteAll();
repository.deleteByUserName("neo"); // 注意，大小写敏感。
```

!FILENAME 修改
```java
Customer customer = repository.findByUserName("Summer");
log.info("{}", customer);
customer.setAddress("湖北武汉武昌区");
repository.save(customer);
Customer other = repository.findByUserName("Summer");
log.info("{}", other);
```

# 高级查询

## 分页查询

!FILENAME  Spring Data 自带的分页方案
```java
Pageable pageable = PageRequest.of(0, 10);
Page<Customer> customers = repository.findByAddress("湖北武汉", pageable);
log.info("Page customers {}", customers.getContent().toString());
```

## term 查询

```java
QueryBuilder queryBuilder = QueryBuilders.termQuery("address", "湖北武汉");
Iterable<Customer> it = repository.search(queryBuilder);
for (Customer customer : it)
    log.info("{}", customer);
```

## match 查询

```java
QueryBuilder queryBuilder = QueryBuilders.matchQuery("address", "湖北武汉");
Iterable<Customer> it = repository.search(queryBuilder);
for (Customer customer : it)
    log.info("{}", customer);
```

## 多条件查询

- `QueryBuilders.boolQuery()`
- `QueryBuilders.boolQuery().must()`：相当于 and
- `QueryBuilders.boolQuery().should()`：相当于 or
- `QueryBuilders.boolQuery().mustNot()`：相当于 not

注意，合理的 and 和 or 的嵌套关系，有助于你理清逻辑。建议像 Mybatis 的 Example 对象学习，组织成 `(... and ...) or (... and ...)` 的形式。

```java
QueryBuilder query1 = QueryBuilders.termQuery("", "");
QueryBuilder query2 = QueryBuilders.termQuery("", "");
QueryBuilder query3 = QueryBuilders.termQuery("", "");
QueryBuilder query4 = QueryBuilders.termQuery("", "");

// ... and ...
QueryBuilder all = QueryBuilders.boolQuery()
    .must(query1)
    .must(query2);

// ... or ...
QueryBuilder all = QueryBuilders.boolQuery()
    .should(query1)
    .should(query2);

// (... and ...) or (... and ...)
QueryBuilder all = QueryBuilders.boolQuery()
    .should(QueryBuilders.boolQuery().must(query1).must(query2))
    .should(QueryBuilders.boolQuery().must(query3).must(query4));

```

# 附-版本对应关系 

Spring Data ElasticSearch 和 ElasticSearch 是有对应关系的，不同的版本之间不兼容。

官网描述的对应关系如下表：

| Spring Boot | Spring Data Elasticsearch | Elasticsearch |
| :-: | :-: | :-: |
| 2.2.x | 3.2.x | 6.8.4 | 
| 2.1.x | 3.1.x | 6.2.2 | 
| 2.0.x | 3.0.x | 5.5.0 | 
| 1.5.x | 2.1.x | 2.4.0 | 

注意，Spring Boot<small>（Spring Data Elasticsearch）</small>和 Elasticsearch 的版本匹配问题是网上反映较多的问题一定要注意。以上是官网列出的版本对应关系，但是实施情况好像也并非如此，实际情况比较混乱。

总体而言规则如下：

| spring-data-es / spring-boot | ES7 | ES6 | ES5 |
| :- | :- | :- | :- |
| 3.2.x / 2.2.x | 支持 | 支持 | 不支持 |
| 3.1.x / 2.1.x | 不支持 | 支持 | 不支持 |
| 3.0.x / 2.0.x | 不支持 | 支持 | 支持 |