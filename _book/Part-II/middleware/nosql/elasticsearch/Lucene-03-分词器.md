# 分词器（Analyzer）

分词器（Analyzer）直译也被称作分析器。它决定了 Lucene 以何种规则为原始的数据进行断句。

> 分词（断句）规则的不同，会决定你能否在 Lucene 中搜索到你所想要的数据。
> 例如，某句话中包含 `桃花汛` 三个字，如果 Lucene 根据某种断句规则，将它断成 `桃花` 和 `汛` 两部分，那么当你「问」Lucene 这句话中，是否有 `桃花` 这个词时，Lucene 会告诉你有；反之，如果 Lucene 根据另一种断句规则，将 `桃花汛` 当作一个整体<small>（而没有断开）</small>时，Lucene 会告诉你这句话中没有 `桃花` ，这句话不是你要找的内容。

Lucene 默认使用的分词器是标准分词器：`StandardAnalyzer`。它根据空格和符号来完成分词，还可以支持过滤表，以实现过滤功能。

## 查看分析器的分析效果

使用 `Analyzer` 对象的 `tokenStream()` 方法返回一个 `TokenStream` 对象。此对象中包含了最终分词结果。

实现步骤：

| 步骤 | 说明 |
| :-: | :- |
| 1 | 创建一个 Analyzer 对象，StandardAnalyzer 对象  |
| 2 | 使用分析器对象的 tokenStream 方法获得一个 TokenStream 对象 |
| 3 | 向 TokenStream 对象中设置一个引用，相当于是一个指针 |
| 4 | 调用 TokenStream 对象的 reset 方法。如果不调用抛异常 | 
| 5 | 使用 while 循环遍历 TokenStream 对象 |
| 6 | 关闭 TokenStream 对象 |


```java
public static void testTokenStream() throws Exception {
    final String filePathName = "D:\\Java框架部分\\Lucene\\资料\\searchsource\\1-A Puma at large.txt";
    // 1: 创建一个Analyzer对象，StandardAnalyzer对象
    Analyzer analyzer = new StandardAnalyzer();

    // 2: 使用分析器对象的tokenStream方法获得一个TokenStream对象
    // 第一个参数为要分析的文件名称
    // 第二个参数为要分析的内容
    BufferedReader reader = IOUtils.toBufferedReader(new FileReader(FileUtils.getFile(filePathName)));
    TokenStream tokenStream = analyzer.tokenStream(FilenameUtils.getName(filePathName), reader);

    // 3）向TokenStream对象中设置一个引用，相当于数一个指针
    CharTermAttribute charTermAttribute = tokenStream.addAttribute(CharTermAttribute.class);

    // 4）调用TokenStream对象的rest方法。如果不调用抛异常
    tokenStream.reset();

    // 5）使用while循环遍历TokenStream对象
    while (tokenStream.incrementToken()) {
        System.out.println(charTermAttribute.toString());
    }

    // 6）关闭TokenStream对象
    tokenStream.close();
}
```

## 中文分词器

很明显，默认的分词器 `StandardAnalyzer` 适合西文环境，而非中文环境。

对于原始数据是中文的情况，需要使用第三方的中文分词器：`IKAnalyzer` 。

```xml
<dependency>
    <groupId>cn.bestwu</groupId>
    <artifactId>ik-analyzers</artifactId>
    <version>5.1.0</version>
</dependency>
```

IKAnalyzer 第三方中文分析器使用步骤分析:

| 步骤 | 说明|
| :-: | :- |
| 1 | 把 IKAnalyzer 的 jar 包添加到工程中 |
| 2 | 把配置文件和扩展词典添加到工程的 classpath 的根路径下 |

> 注意：扩展词典严禁使用 windows 记事本编辑，以保证扩展词典的编码格式是 utf-8
> 
> 扩展词典：添加一些新词<br>
> 停用词词典：无意义的词或者是敏感词汇

```java
/**
 * IKAnalyzer第三方中文分析器
 */
public static void testIKAnalyzer() throws Exception {
    // 1: 创建一个 Analyzer 对象，IKAnalyzer 对象
    Analyzer analyzer = new IKAnalyzer();

    // 2: 使用分析器对象的 tokenStream 方法获得一个 TokenStream 对象
    // 第一个参数为要分析的文件名称
    // 第二个参数为要分析的内容
    TokenStream tokenStream = analyzer.tokenStream("", "2000年1月1日 - " +
                "Lucene概述Lucene是一款高性能的、可扩展的信息检索(IR)工具库。" +
                "信息检索是指文档搜索、文档内信息搜索或者文档相关的元数据搜索等操作。");

    // 3: 向 TokenStream 对象中设置一个引用，相当于数一个指针
    CharTermAttribute charTermAttribute = tokenStream.addAttribute(CharTermAttribute.class);

    // 4: 调用 TokenStream 对象的 reset 方法。如果不调用抛异常
    tokenStream.reset();

    // 5: 使用 while 循环遍历 TokenStream 对象
    while (tokenStream.incrementToken()) {
        System.out.println(charTermAttribute.toString());
    }

    // 6: 关闭 TokenStream 对象
    tokenStream.close();
}
```

### 使用 IKAnalyzer 分析器创建中文索引库

修改创建索引器方法 `createIndex()`

未修改前代码:

```java
// 创建 IndexWriterConfig 对象
IndexWriterConfig config = new IndexWriterConfig();
// 创建 IndexWriter 对象
IndexWriter indexWriter = new IndexWriter(directory, config);
```

修改后代码:

```java
// 创建 IndexWriterConfig 对象
IndexWriterConfig config = new IndexWriterConfig(new IkAnalyzer());
// 创建 IndexWriter 对象
IndexWriter indexWriter = new IndexWriter(directory, config);
```
