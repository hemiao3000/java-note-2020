# 索引数据的查询

- 查询索引的步骤

  > 1. 创建一个 `Director` 对象，指定索引库的位置 
  > 2. 创建一个 `IndexReader` 对象 
  > 3. 创建一个 `IndexSearcher` 对象，构造方法中的参数 indexReader 对象。 
  > 4. 创建一个 `Query` 对象，TermQuery 
  > 5. 执行查询，得到一个 `TopDocs` 对象 
  > 6. 取查询结果的总记录数 
  > 7. 取文档列表 
  > 8. 打印文档中的内容 
  > 9. 关闭 `IndexReader` 对象


## TermQuery 根据关键词查询

​TermQuery，通过项查询，TermQuery 不会使用分词器，因此用于以 `不分词的域` 为查询条件的查询。

```java
// 1: 创建一个 Director 对象，指定索引库的位置
Directory directory = FSDirectory.open(new File("E:\\微潮案例\\Lucene\\temp\\index").toPath());

// 2: 创建一个 IndexReader 对象。会用到 1 中创建的 Directory 对象。
IndexReader indexReader = DirectoryReader.open(directory);

// 3: 创建一个 IndexSearcher 对象。会用到 2 中创建的 IndexSearcher 对象。
IndexSearcher indexSearcher = new IndexSearcher(indexReader);

// 4: 创建一个 Query 对象（TermQuery）
Query query = new TermQuery(new Term("content", "web"));

// 5: 执行查询，得到一个 TopDocs 对象
// 参数1：4 中创建的 TermQuery 对象
// 参数2：查询结果返回的最大记录数
TopDocs topDocs = indexSearcher.search(query, 10);

// 6: 取查询结果的总记录数
System.out.println("查询总记录数：" + topDocs.totalHits);

// 7: 取文档列表
ScoreDoc[] scoreDocs = topDocs.scoreDocs;

// 8: 打印文档中的内容
for (ScoreDoc doc : scoreDocs) {
    // 取文档id
    int docId = doc.doc;
    // 根据id取文档对象
    Document document = indexSearcher.doc(docId);
    System.out.println("文件名:"+document.get("filename"));
    System.out.println("文件路径:"+document.get("path"));
    System.out.println("文件大小:"+document.get("size"));
    // System.out.println(document.get("content"));
    System.out.println("-----------------寂寞的分割线");
}

// 9: 关闭IndexReader对象
indexReader.close();
```

## TermQuery 进行数值范围查询

进行数值范围查询的前提是：作为搜索条件的域的类型，「当初」在加入到索引库中时，设置的应该是 Int、Long 这样的数值类型。

```java
/**
 * 使用 RangeQuery 根据数值范围查询
 */
public void testRangeQuery() throws Exception {
    // 1. 创建一个Director对象，指定索引库的位置
    Directory directory = FSDirectory.open(new File("E:\\微潮案例\\Lucene\\temp\\index").toPath());

    // 2. 创建IndexReader对象
    IndexReader indexReader = DirectoryReader.open(directory);

    // 3. 初始化IndexSearcher
    IndexSearcher indexSearcher = new IndexSearcher(indexReader);

    // 4. 创建查询条件对象
    // 第一个参数为要查询的域
    // 第二个参数为起始值
    // 第天个参数为终止值
    Query query = LongPoint.newRangeQuery("size", 0L, 10000L);

    // 5. 执行查询，第一个参数为固定名称，第二个参数为每页显示几条
    TopDocs topDocs = indexSearcher.search(query,10);
    System.out.println("查询总记录数：" + topDocs.totalHits);

    // 6. 取文档列表
    ScoreDoc[] scoreDocs = topDocs.scoreDocs;
    // 7. 打印文档中的内容
    for (ScoreDoc doc : scoreDocs) {
        // 取文档id
        int docId = doc.doc;
        // 根据id取文档对象
        Document document = indexSearcher.doc(docId);
        System.out.println("文件名:"+document.get("filename"));
        System.out.println("文件路径:"+document.get("path"));
        System.out.println("文件大小:"+document.get("size"));
        // System.out.println(document.get("content"));
        System.out.println("-----------------寂寞的分割线");
    }
}
```

## 使用 queryparser 查询

上述的 Query 查询不会使用分词。通过 `QueryParser` 也可以创建 Query，而它可以利用上分词器进行分词查询。

QueryParser 提供一个 Parse 方法，此方法可以直接根据查询语法来查询。Query 对象执行的查询语法可通过 `System.out.println(query);` 查询。需要使用到分析器。建议创建索引时使用的分析器和查询索引时使用的分析器要一致。

QueryParser 可以对要查询的内容先分词，然后基于分词的结果进行查询。

需要加入 queryParser 依赖的 jar 包

```xml
<dependency>
    <groupId>org.apache.lucene</groupId>
    <artifactId>lucene-queryparser</artifactId>
    <version>7.4.0</version>
</dependency>
```

```java
/**
 * 使用 queryparser 查询
 */
public void testQueryParser() throws Exception {
    // 1. 创建一个 Director 对象，指定索引库的位置
    Directory directory = FSDirectory.open(new File("E:\\微潮案例\\Lucene\\temp\\index").toPath());

    // 2. 创建 IndexReader 对象
    IndexReader indexReader=DirectoryReader.open(directory);

    // 3. 初始化 IndexSearcher
    IndexSearcher indexSearcher = new IndexSearcher(indexReader);

    // 4. 创建 queryparser 对象
    // 第一个参数默认搜索的域
    // 第二个参数就是分析器对象
    QueryParser queryParser = new QueryParser("filename", new IKAnalyzer());
    Query query = queryParser.parse("Lucene 是一个 java 开发的全文检索工具包");

    // 5. 执行查询
    TopDocs topDocs = indexSearcher.search(query, 10);

    // 6. 共查询到的 document 个数
    System.out.println("查询结果总数量：" + topDocs.totalHits);

    // 7. 遍历查询结果
    for (ScoreDoc scoreDoc : topDocs.scoreDocs) {
        Document document = indexSearcher.doc(scoreDoc.doc);
        System.out.println(document.get("filename"));
        // System.out.println(document.get("content"));
        System.out.println(document.get("path"));
        System.out.println(document.get("size"));
    }

    // 8. 关闭 indexreader
    indexSearcher.getIndexReader().close();
}
```