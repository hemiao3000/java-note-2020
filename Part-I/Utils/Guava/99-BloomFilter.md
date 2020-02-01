<span class="title">布隆过滤器</span>


在实际工作中，布隆过滤器常见的应用场景如下：

- 网页爬虫对 URL 去重，避免爬取相同的 URL 地址；
- 反垃圾邮件，从数十亿个垃圾邮件列表中判断某邮箱是否垃圾邮箱；
- Google Chrome 使用布隆过滤器识别恶意 URL；
- Medium 使用布隆过滤器避免推荐给用户已经读过的文章；
- Google BigTable，Apache HBbase 和 Apache Cassandra 使用布隆过滤器减少对不存在的行和列的查找。 除了上述的应用场景之外，布隆过滤器还有一个应用场景就是解决缓存穿透的问题。所谓的缓存穿透就是服务调用方每次都是查询不在缓存中的数据，这样每次服务调用都会到数据库中进行查询，如果这类请求比较多的话，就会导致数据库压力增大，这样缓存就失去了意义。
- Redis 场景中用于防止缓存击穿

布隆过滤器实际上是一个很长的二进制向量和一系列随机映射函数。布隆过滤器可以用于检索一个元素是否在一个集合中。它的优点是空间效率和查询时间都远远超过一般的算法，缺点是有一定的误识别率和删除困难。

当一个元素被加入集合时，通过 K 个散列函数将这个元素映射成一个位数组中的 K 个点，把它们置为 1 。检索时，我们只要看看这些点是不是都是 1 就（大约）知道集合中有没有它了：如果这些点有任何一个 0 ，则被检元素一定不在；如果都是 1 ，则被检元素很可能在。因为存在哈希冲突导致 3% 左右的误判，即没有存在的判断存在，但是在的一定就是在的。

[动画演示效果](https://www.jasondavies.com/bloomfilter/?spm=a2c4e.11153940.blogcont683602.11.21181fe6hVAGjH)


> 你可以将布隆过滤器想象成是要给【大】超市的老板，因为是大超市，所以他们家卖的东西比不卖东西的种类要多。老板【心怀怨念】哪些东西是他们家没有的，他一清二楚。但是他们家有什么东西他可能会有一定的记忆误差，会记错<small>（毕竟数量太多）</small>。因此，当你问老板他们家有没有 xxx 时，老板说【没有】，那么就一定时没有；老板要是说【有】，那么大概率是有的，不过也不能打包票一定就有。

# 使用 Guava 的布隆过滤器

Guava 的布隆过滤器通过调用 BloomFilter 类中的静态函数创建， 传递一个 Funnel 对象以及一个代表预期插入数量整数。同样来自于 Guava 11 中的 Funnel 对象，用于将数据发送给一个接收器（Sink）。 下面的例子是一个默认的实现，有着3%的误报率。Guava 提供的 Funnels 类拥有两个静态方法提供了将CharSequence 或byte数组插入到过滤器的 Funnel 接口的实现。

```java
//Creating the BloomFilter
BloomFilter<byte[]> bloomFilter = BloomFilter.create(Funnels.byteArrayFunnel(), 1000);

BigInteger bigInteger = new BigInteger("1234567890");
bloomFilter.put(bigInteger.toByteArray());

boolean mayBeContained = bloomFilter.mightContain(bigInteger.toByteArray());

System.out.println(mayBeContained ? "存在" : "不存在");
```

# 改进版

```java
public class BigIntegerFunnel implements Funnel<BigInteger> {

    @Override
    public void funnel(BigInteger from, PrimitiveSink into) {
        into.putBytes(from.toByteArray());
    }

}

BloomFilter<BigInteger> bloomFilter = BloomFilter.create(new BigIntegerFunnel(), 1000);

BigInteger bigInteger = new BigInteger("1234567890");
bloomFilter.put(bigInteger);

boolean mayBeContained = bloomFilter.mightContain(bigInteger);

System.out.println(mayBeContained ? "存在" : "不存在");

```



[stream-lib](https://blog.csdn.net/zjerryj/article/details/77628694)