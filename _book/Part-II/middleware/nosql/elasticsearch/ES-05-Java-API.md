# Java API

所有的 Elasticsearch 操作都是通过一个客户端（Client）对象来执行的。

- pom.xml

  ```xml
  <dependency>
      <groupId>org.elasticsearch.client</groupId>
      <artifactId>transport</artifactId>
      <version>6.2.4</version>
  </dependency>
  ```

  需要注意的是，transport 库的版本必须与你的 Elasticsearch 的版本保持一致。

  另外，`transport` 依赖于 `log4j2` 日志库，因此，你最好在你的 classpath 下提供一个 `log4j2.properties` 日志配置文件。例如：

  ```properties
  appender.console.type = Console
  appender.console.name = console
  appender.console.layout.type = PatternLayout
  rootLogger.level = info
  rootLogger.appenderRef.console.ref = console
  ```

## 连接到 Elasticsearch

获得一个 Elasticsearch client 对象非常简单，最常用的方式是创建一个可以连接到Elasticsearch 的传输机对象（`TransportClient`）。

- 创建 `TransportClient` 对象的方法：

  ```java
  TransportClient client = new PreBuiltTransportClient(Settings.EMPTY)
      .addTransportAddress(new TransportAddress(InetAddress.getByName("127.0.0.1"), 9300))
      .addTransportAddress(new TransportAddress(InetAddress.getByName("xxx.x.x.x"), 9300))
      .addTransportAddress(new TransportAddress(InetAddress.getByName("xxx.x.x.x"), 9300)); // 连接 es
  ```

## 操作文档（Document）


### 新建文档 / 插入文档

- 样例

  ```java
  public static void createTest(TransportClient client) throw Exception {
      IndexRequestBuilder request = client.prepareIndex("microboom", "java", "1");

      // 使用 Map 创建文档结构
      Map<String, Object> map = new HashMap<String, Object>();
      map.put("id", 1);
      map.put("uaername", "张三");
      map.put("address", "武汉");

      // 将文档结构发送到 es
      request.setSource(map);

      // 获得响应对象
      IndexResponse indexResponse = request.get();
      System.out.println(indexResponse);
      client.close();
  }

  public static void createTest2(TransportClient client) throws Exception {
      IndexRequestBuilder response = client.prepareIndex("microboom", "java", "2");

      XContentBuilder doc = XContentFactory.jsonBuilder()
          .startObject()
          .field("id", 2)
          .field("username", "李四")
          .field("address", "北京")
          .endObject();

      response.setSource(doc);

      // 获得响应对象
      IndexResponse indexResponse = response.get();
      System.out.println(indexResponse);
      client.close();
  }
  ```

### 获取文档 / 查询文档

- 样例

  ```java
  public static void getTest(TransportClient client) throws Exception {
      GetResponse response = client.prepareGet("microboom", "java", "1").get();
      System.out.println(response.getSourceAsString());
  }
  ```

- GetResponse 对象提供的常用方法如下：

  | 方法 | 说明 |
  | :- | :- |
  | `isExists()` | 如果要读取的文档存在，就返回 true，否则返回 false。|
  | `getIndex()` | 返回请求文档的索引名。 |
  | `getType()` | 返回请求文档的类型名。 |
  | `getId()` | 返回请求文档的 ID 。|
  | `getVersion()` | 返回文档版本信息。|
  | `getSourceAsBytes()` | 以二进制数组方式读取文档内容 |
  | `getSourceAsMap()` | 以 map 的形式读取文档的内容 |
  | `getSourceAsString()` | 以文本方式读取文档的内容 |
  | `isSourceEmpty()` | 判断文档内容是否文空 |



### 删除文档

- 样例

  ```java
  DeleteResponse response = client.prepareDelete("microboom", "java", "2").get();

  System.out.println(response);

  client.close();
  ```

- DeleteResponse 对象提供的常用方法如下：

  | 方法 | 说明 |
  | :- | :- |
  | `status()` | 删除成功，返回 OK；删除失败，返回 NOT_FOUND 。|
  | `getType()` | 返回删除请求文档的类型 |
  | `getId()` | 返回删除请求文档的 ID 。|
  | `getVersion()` | 返回删除请求文档的版本信息。|



### 更新文档

Elasticsearch 提供了多种更新文档的 API，主要有 2 种方式：

- 使用 `UpdateRequest` 对象

  ```java
  UpdateRequest request = new UpdateRequest("microboom", "java", "1");
  request.doc(XContentFactory.jsonBuilder().startObject()
      .field("name", "李四")
      .endObject());

  UpdateResponse response = client.update(request).get();
  System.out.println(response);

  client.close();
  ```

- 使用内嵌脚本

  ```java
  // 为文档添加一个字段
  UpdateRequest request = new UpdateRequest("microboom", "java", "1");
  request.script(new Script("ctx._source.gender=\"male\""));
  UpdateResponse response = client.update(request).get();
  System.out.println(response);
  client.close();
  ```

### 批量获取

- 样例

  ```java
  MultiGetRequestBuilder request = client.prepareMultiGet()
        .add("index1", "type1", "1")
        .add("index1", "type1", "2", "3", "4")
        .add("index2", "type2", "x");

  MultiGetResponse responses = request.get();

  for (MultiGetItemResponse itemResponse: responses) {
      GetResponse response = itemResponse.getResponse();
      if (response != null && response.isExists()) {
          System.out.println(response.getSourceAsString());
      }
  }

  client.close();
  ```

### 批量操作

使用 Bulk API 可以通过一次请求完成批量索引文档、批量删除文档和批量更新文档。


- 样例

  ```java
  BulkRequestBuilder bulkRequest = client.prepareBulk();

  IndexRequestBuilder indexRequest = client
      .prepareIndex("microboom", "java", "3")
      .setSource(XContentFactory.jsonBuilder()
              .startObject()
              .field("id", "3")
              .field("username", "王五")
              .field("address", "北京")
              .endObject());

  DeleteRequestBuilder deleteRequest = client
      .prepareDelete("microboom", "java", "2");

  UpdateRequestBuilder updateRequest = client
      .prepareUpdate("microboom", "java", "1")
      .setDoc(XContentFactory.jsonBuilder()
              .startObject()
              .field("username", "tom")
              .endObject());

  BulkResponse bulkResponse = bulkRequest
      .add(indexRequest)
      .add(deleteRequest)
      .add(updateRequest)
      .execute()
      .actionGet();

  if (bulkResponse.hasFailures())
    System.out.println("操作失败");

  client.close();
  ```


### 查询

Elasticsearch 也提供了 Java 接口的查询 DSL 。构造查询对象的工厂类是 `QueryBuilders`，只要查询语句准备好了就可以使用相关的 API 。

- 样例

  ```java
  QueryBuilder matchQuery = QueryBuilders
        .matchQuery("username", "王五")
        .operator(Operator.AND);// 表示使用 AND 方式连接被分词后的词项

  SearchRequestBuilder request = 
      client.prepareSearch("microboom")
        .setQuery(matchQuery)
        .setFrom(0).setSize(10) // 分页，非必须
        .addSort("username", SortOrder.ASC)   // 排序，非必须

  SearchResponse response = request.get();

  SearchHits hits = response.getHits();
  System.out.println("共搜索到 " + hits.getTotalHits() + " 条数据");

  for (SearchHit hit : hits) {
      System.out.println("Sources: " + hit.getSourceAsString() + "\n"
          + "Source As Map: " + hit.getSourceAsMap() + "\n"
          + "Index: " + hit.getIndex() + "\n"
          + "ID: " + hit.getId() + "\n"
          + "Username: " + hit.getSourceAsMap().get("username") + "\n"
          + "Score: " + hit.getScore());
  }
  ```

### 全文检索

  - Match All Query
    ```java
      QueryBuilder query = QueryBuilders.matchAllQuery();
    ```
  - match_phrase query 
    ```java
    QueryBuilder query = QueryBuilders.matchPhraseQuery("field", "text");
    ```
  - multi_match query
    ```java
    QueryBuilder query= QueryBuilders.multiMatchQuery("text", "field1", "field2", "field3");
    ```

### 词项查询
  - term query
    ```java
    QueryBuilder query = QueryBuilders.termQuery("field1", "text");
    ```
  - terms query
    ```java
    QueryBuilder query = QueryBuilders.termsQuery("field1", "text1", "text2", "text3");
    ```
  - range query
    ```java
    QueryBuilder query = QueryBuilders.rangeQuery("field1")
        .from(50)
        .to(100)
        .includeLower(true)
        .includeUpper(false);
    ```
  - exists query
    ```java
    QueryBuilder query = QueryBuilders.existsQuery("field1");
    ```
  - prefix query
    ```java
    QueryBuilder query = QueryBuilders.prefixQuery("field1", "java");
    ```
  - wildcard query
    ```java
    QueryBuilder query = QueryBuilders.wildcardQuery("author", "张?");
    ```

### 复合查询
  - bool query
    ```java
    QueryBuilder query1 = QueryBuilders.matchQuery("field1", "text1");
    QueryBuilder query2 = QueryBuilders.matchQuery("field2", "text2");
    QueryBuilder query3 = QueryBuilders.matchQuery("field3", "text3");

    QueryBuilder query= QueryBuilders.boolQuery()
        .must(query1)
        .should(query2)
        .mustNot(query3);
    ```