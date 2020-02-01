<span class="title">Web Magic 爬虫框架</span>

<small>使用 Web Magic 时，如果使用的是较低版本的 JDK，会导致一些 *<code>https://</code>* 网页无法爬取，所以确保你安装的是 JDK 8，并且使用的是 JDK 8 的编译标准。</small>

# pom.xml

```xml
<dependency>
    <groupId>us.codecraft</groupId>
    <artifactId>webmagic-core</artifactId>
    <version>0.7.3</version>
</dependency>
<dependency>
    <groupId>us.codecraft</groupId>
    <artifactId>webmagic-extension</artifactId>
    <version>0.7.3</version>
</dependency>
```

maven 项目的默认编译级别是 JDK 5，为了『告诉』maven 使用 JDK 8 的标准进行编译<small>（前提是你安装的 JDK 确实是 8 ）</small>，需要加入两个 maven 环境变量：

```xml
<properties>
    <project.build.sourceEncoding>UTF-8</project.build.sourceEncoding>
    <maven.compiler.source>1.8</maven.compiler.source>
    <maven.compiler.target>1.8</maven.compiler.target>
    ...
</properties>
```

webmagic 使用了 log4j2 日志框架，如果你使用的是其他的日志框架，那记得将它排除掉：

```xml
<exclusions>
    <exclusion>
        <groupId>org.slf4j</groupId>
        <artifactId>slf4j-log4j12</artifactId>
    </exclusion>
</exclusions>
```

或者干脆就使用 log4j2 日志框架。在 classpath 下提供 log4j2 的配置文件：

!FILENAME log4j.properties

```properties
log4j.rootLogger=WARN, stdout

log4j.logger.xxx.yyy.zzz=WARN, stdout

log4j.appender.stdout=org.apache.log4j.ConsoleAppender
log4j.appender.stdout.layout=org.apache.log4j.PatternLayout
log4j.appender.stdout.layout.ConversionPattern=%20t %5p | %m%n
```

# 一个简单的例子

!FILENAME ExamplePageProcessor.java
```java
import us.codecraft.webmagic.Page;
import us.codecraft.webmagic.Site;
import us.codecraft.webmagic.Spider;
import us.codecraft.webmagic.pipeline.ConsolePipeline;
import us.codecraft.webmagic.processor.PageProcessor;
import us.codecraft.webmagic.selector.Selectable;

public class ExamplePageProcessor implements PageProcessor {

	// 抓取网站的相关配置，包括：编码、抓取间隔、重试次数等
	private Site site = Site.me().setRetryTimes(3).setSleepTime(1000);

	@Override
	public void process(Page page) {
		Selectable selectable = page.getHtml().css("div.content");
		System.out.println(selectable);
	}

	@Override
	public Site getSite() {
		return site;
	}

	public static void main(String[] args) {
		Spider.create(new ExamplePageProcessor())
			.addUrl("http://my.oschina.net/flashsword/blog")
			.run();
	}
}
```

# Page 对象

当指定的页面被 webmagic 爬下来之后，***`PageProcessor`*** 的 *`.process()`* 方法会被触发执行。

webmagic 在调用 *`.process()`* 方法时，传入的 Page 对象就代表着所爬下来的页面。其中，最常用的是获得该页面的 URL 和页面内容。

```java
System.out.println(page.getUrl().toString()); 
System.out.println(page.getHtml().toString()); // 这个字符串会很长...
```

# <big>*`.css()`*</big> 方法

通过 *`page.getHtml()`* 我们可以从代码中拿到所请求的页面的全部内容。但是，通常我们感兴趣/需要的是页面上的某个或某些内容，而非整个页面内容全部。

*`.css()`* 方法借用 CSS 选择器的语法，可以让我们从这个页面内容中选中我们感兴趣/需要的元素片段。例如：

```java
Selectable selectable = page.getHtml().css("div.content");
System.out.println(selectable.toString());
```

这里的 *`.css()`* 方法的参数 *`div.content`* 的写法和 CSS 选择器的写法是一摸一样，它匹配/选中的是页面上 *`<div class="content">`* 元素。

*`.css()`* 方法返回的是被选中/符合条件的页面元素中的第一个。如果，想获得所有符合条件的元素<small>（而不仅仅是第一个）</small>，那么需要多调用一个 *`.nodes()`* 方法：

```java
List<Selectable> list = 
    page.getHtml().css("div.content").nodes();
```

当然，如果页面上有且仅有一个元素符合选中条件，那么，返回的 ***`List`*** 中也就只有一个 ***`Selectable`*** 对象。

CSS 选择器语法见最后。

对于 *`.css()`* 方法返回的 Selectable 对象，可以 <strong>再次调用</strong> *`.css()`* 方法，表示语句此选中的内容<small>（HTML片段）</small>再进一步在其内部进行选择。例如：

```java
Selectable selectable1 = page.getHtml().css("div.content");
Selectable selectable2 = selectable1.css("div.text");
System.out.println(selectable2);
```


# XPath 和 <big>*`.xpath()`*</big> 方法

当通过 *`.css()`* 方法选中了你所需要的元素后，你感兴趣的内容可能是这个/这些元素的属性值，或文本内容。更有甚者，还可能是它们的子孙元素的属性值，或文本内容。

这时，你需要对 *`.css()`* 方法的返回的 Selectable 再进一步调用 *`.xpath()`* 方法。

显而易见，*`.xpath()`* 方法利用了 XPath 概念。XPath 是一门在 XML 文档中查找信息的语言。XPath 可用来在 XML 文档中对元素和属性进行遍历。

XPath 最常用的路径表达式：

| 表达式	| 描述 |
| :- | :- |
| `nodename`	| 选取此节点的所有子节点。|
| `/`	| 从根节点选取。|
| `//`	| 从匹配选择的当前节点选择文档中的节点，而不考虑它们的位置。|
| `.` 	| 选取当前节点。|
| `..`	| 选取当前节点的父节点。|
| `@`	| 选取属性。|

例如：

| 路径表达式	| 结果 |
| :- | :- |
| `bookstore`  | 选取 *`bookstore`* 元素，<small>不强求它必须是根元素。</small>|
| `/bookstore` | 选取 *`bookstore`* 元素，并且，它必须是根元素。|
| `bookstore/book` | 选取 *`bookstore`* 元素的名为 `book` 的子元素。|
| `//book`	| 选取所有 *`book`* 子元素，<small>不强求它必须是根元素</small>|
| `bookstore//book`	| 选择属于 *`bookstore`* 元素的后代中名为  *`book`* 的元素，<small>不强求 *`book`* 必须是 *`bookstore`* 的直接子元素。</small>|
| `/bookstore/book/title` | 选取 *`bookstore`* 的子元素 *`book`* 的子元素 *`title`* |
| `/bookstore/book[1]/title` | 选取 *`bookstore`* 的第一个 *`book`* 子元素的 *`title`* 子元素。|
| `/bookstore/book/price/text()` | 选取 *`booksotre`* 元素的 *`book`* 子元素的 *`price`* 子元素的文本内容 |
| `/bookstore/book/@lang` | 选取 *`booksotre`* 元素的 *`book`* 子元素的 *`lang`* 属性的属性值 |

- 一个更复杂的 XPATH：`/bookstore/book[@lang='eng']/price/text()` 

  选取 *`booksotre`* 元素的 *`lang`* 属性值为 *`eng`* 的 *`book`* 子元素的 *`price`* 子元素的文本内容。|

更多的语法和示例可参看 [w3school](https://www.w3school.com.cn/xpath/index.asp) 教程。

```java
@Override
public void process(Page page) {
    System.out.println(page.getUrl().toString());

    List<Selectable> selectableList = page.getHtml().css("li.fl").nodes();
    for (Selectable selectable : selectableList) {
        String content = selectable.xpath("/li/div/p[1]/a/@href").get();
        if (StringUtils.isNotBlank(content))
            System.out.println(content);
    }
}
```

# Pipeline 机制和 ConsolePipeline

我们对从『爬』到的页面上『抠』出来的数据的处理<small>（*`System.out.println()`*）</small>的处理是写死在 *`.process()`* 方法中的。

更优雅的方案是使用 webmagic 的 <font color="#0088dd">**Pipeline**</font> 机制。

在创建 Spider 对象<small>（并启动运行时）</small>，你可以指定一个或多个 Pipeline 对象：

```java
Spider.create(new ExamplePageProcessor())
      .addUrl("https://www.1905.com/mdb/film/list/year-2019/")
      .addPipeline(new ConsolePipeline()) // 看这里
      .run();
```

当 PageProcessor 的 *`.process()`* 方法执行完后，Pipeline 的 *`.process()`* 会执行。

在 PageProcessor 中，你可以将需要后续处理的数据放入到 page 对象中：

```java
page.putField("xxx", ...);
page.putField("yyy", ...);
page.putField("zzz", ...);
```

在 Pipeline 中，你可以再将上面存在 page 中的数据取出来，再进一步进行处理：

```java
resultItems.get("xxx");
resultItems.get("yyy");
resultItems.get("zzz");
```

另外，可以通过 lambda 表达式来简化自定义 Pipeline 。

另外，Spider 的 Pipeline 可以有多个。这多个 Pipeline 会依次执行<small>（这才是它较管道的原因）</small>。

```java
Spider.create(new App())
      .addUrl("http://my.oschina.net/flashsword/blog")
      .addPipeline((resultItems, task) -> {
           System.out.println("第 1 个 pipeline 被触发执行");
      })
      .addPipeline((resultItems, task) -> {
           System.out.println("第 2 个 pipeline 被触发执行");
      })
      .addPipeline((resultItems, task) -> {
           System.out.println("第 3 个 pipeline 被触发执行");
      })
      .run();
```

你可以利用这个特性实现这样的效果：在第 1 个 Pipeline 中做第一道数据加工操作，在第 2 个 Pipeline 中在做第二道数据加工操作，... 。

# <big>`page.addTargetRequest()`</big>

通常，我们需要爬取的并非一个页面，而是一系列页面。常见的需求是这样：爬取第一个页面，从其中摘取某些信息，并以此为依据构造第二个页面 URL，继续爬取第二个页面，...。

在 Processor 的 *`.process()`* 方法中，调用 *`page.addTargetRequest(<url>)`* 可以动态地告知 Spider 接下来要爬取的页面。整个代码结构类似于：

```java
@Override
public void process(Page page) {
    if ( page.getUrl().get() 是页面1 ) {
        ...
        page.addTargetRequest(页面2);
    }
    else if ( page.getUrl().get() 是页面2 ) {
        ...
        page.addTargetRequest(页面3);
    }
    else if ( page.getUrl().get() 是页面3 ) {
        ...
    }
    ...
}
```

例如：

```java
public class SimpleProcessor implements PageProcessor {

    private static final Logger log = LoggerFactory.getLogger(App.class);

    // 抓取网站的相关配置，包括：编码、抓取间隔、重试次数等
    private Site site = Site.me().setRetryTimes(3).setSleepTime(1000);

    @Override
    public void process(Page page) {
        if (Objects.equals(page.getUrl().get(), "https://www.1905.com/mdb/film/list/year-2019")) {
            List<Selectable> selectableList = page.getHtml().css("li.fl>div").nodes();
            for (Selectable selectable : selectableList) {
                String idContent = selectable.xpath("/div/p[1]/a/@href").get();
                if (StringUtils.isNotBlank(idContent)) {
                    String[] tokens = StringUtils.split(idContent, "/");
                    String id = tokens[tokens.length - 1];
                    log.info("{}", id);
                    page.addTargetRequest(String.format("https://www.1905.com/mdb/film/%s/performer/?fr=mdbypsy_dh_yzry", id));
                }
            }
        }
        else if (StringUtils.startsWith(page.getUrl().get(), "https://www.1905.com/mdb/film/")
                    && StringUtils.endsWith(page.getUrl().get(), "/performer/?fr=mdbypsy_dh_yzry")) {
            log.info("{}", page.getUrl().get());
        }
    }

    @Override
    public Site getSite() {
        return site;
    }

    public static void main(String[] args) {
        Spider.create(new SimpleProcessor())
                .addUrl("https://www.1905.com/mdb/film/list/year-2019")
                .run();
    }
}
```

# 多线程

默认情况下，Webmagic 仅使用一个线程（当前线程）爬取网页内容，如果有需要，可以指定多个线程：

```java
public static void main(String[] args) {
    Spider.create(new SimpleProcessor())
          .addUrl("https://www.1905.com/mdb/film/list/year-2019")
          .thread(5)
          .run();
}
```

# 一个关于空格的小问题

有时候你在页面上看到的空格有全角、半角之分，看起来并没有任何区别。但是字符串的 *`.split()`* 和 *`trim()`* 方法处理的都是半角空格。

所以，你会看到调用了 *`trim()`*  方法但是似乎没起作用的情况。

这种情况下可以先将全角空格替换为半角空格之后，再作处理。

```java
content = content.replaceAll("\\u00A0"," ");
```

# 附：CSS 选择器

CSS 选择器决定了后续规则将应用于哪些元素。

## CSS 核心选择器

| 选择器 | 解释 |
| :- | :- |
| *`*`* | 选中所有的元素 |
| *`xxx`* | 选中的元素必须是 *`xxx`* 类型元素。<br>即，*`<xxx>`* |
| *`.xxx`* | 选中的元素的 *`class`* 属性的值必须 **是/有** *`xxx`* 。<br>即，*`class="xxx ..."`* |
| *`#xxx`* | 选中的元素的 *`id`* 属性的值必须是 *`xxx`* 。<br>即，*`id=xxx`*|

注意，由于 *`class`* 属性的值可以有多个，选中同时具备两个 *`class`* 属性值的元素的写法为：*`.xxx.yyy`*，即，所选中元素的 *`class = "xxx yyy"`* 。

## CSS 属性选择器

属性选择器是基于属性名及属性的值进行选择页面元素。

| 选择器 | 解释 |
| :- | :- |
| `[attr]`        | 选中的元素必须具有 *`attr`* 属性。 |
| `[attr="val"]`  | 选中的元素必须具有 *`attr`* 属性，且属性值为 *`val`*。 |
| `[attr^="val"]` | 选中的元素必须具有 *`attr`* 属性，且属性值是以 `val` 开头。|
| `[attr$="val"]` | 选中的元素必须具有 *`attr`* 属性，且属性值是以 `val` 结尾。|
| `[attr*="val"]` | 选中的元素必须具有 *`attr`* 属性，且属性值含有 `val`<small>（可以有其他值）</small>。|


## 选择器的复合使用

选择器的复合使用，从程序员的角度来看，就是以 <font color="#0088dd">**与**</font> 和 <font color="#0088dd">**或**</font> 的关系组合使用多个选择器，选择页面元素。

选择器的“与”关系：*`<选择器1><选择器2>`*，将两个选择器“紧挨”在一起，就是以 <font color="#0088dd">**与**</font> 的关系使用选择器选择页面元素。其选择结果必定（必须）同时满足两个选择器。

<font color="red">**注意**</font>：此处两个选择器间不能有空格。有空格则表达了另一种关系，不再是 **与** 的关系。

选择器的“或”关系：*`<选择器1>, <选择器2>`*，将两个选择器以逗号（`,`）分隔，就是以 <font color="#0088dd">**或**</font> 的关系使用选择器选择页面元素。其选择结果只需满足两者其一即可。

***注意***，此处逗号后的空格可有可无。

## 亲属关系选择器

亲属关系选择器会涉及到元素的层次结构。

| 选择器 | 解释 |
| :- | :- |
| `<选择器1> <选择器2>` | 选中的元素要满足 *`选择器 2`* ，并且，<br><font color="#0088dd">**祖先**</font> 要满足 *`选择器 1`* 。|
| `<选择器1> > <选择器2>` | 选中的元素要满足 *`选择器 2`* ，并且，<br><font color="#0088dd">**爸爸**</font> 要满足 *`选择器 1`* 。|
| `<选择器1> + <选择器2>` | 选中的元素要满足 *`选择器 2`* ，并且，<br><font color="#0088dd">**紧邻的前一个元素**</font> 要满足 *`选择器 1`* 。|
| `<选择器1> ~ <选择器2>` | 选中的元素要满足 *`选择器 2`* ，并且，<br><font color="#0088dd">**哥哥**</font> 要满足 *`选择器 1`* 。|