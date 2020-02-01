## Guava 学习之 CharMatcher

### 概览

之前，Guava 中的 StringUtil 在无节制地增长，具有很多方法，如：

- allAscii
- collapse
- collapseControlChars
- collapseWhitespace
- lastIndexNotOf
- numSharedChars
- removeChars
- removeCrLf
- retainAllChars
- strip
- stripAndCollapse
- stripNonDigits

这些函数本质上是以下两个方面的乘积（M x N 种情况）：

1. 何如定义一个“匹配”的字符？ （M 种情况）
2. 对“匹配”的字符进行怎样的操作？ （N 种情况）

为了解决这样的爆炸式增长，Guava 提供了 CharMatcher。一个 CharMacher 实例本身，界定了一个匹配字符的集合，而 CharMacher 实例的方法，解决了要对匹配字符做什么的问题。然后我们就可以用最小化的 API 来处理字符匹配和字符操作，把 M×N 的复杂度下降到了 M+N。

直观地说，你可以把 CharMatcher 看做是一些特别字符串的表示，例如：数字、空格等。而事实上，CharMatcher 只是一个针对字符串的布尔断言（它实现了 `Predicate<Character>`），但考虑到“所有空白字符串”、“所有小写单词”等相关需求是很普遍的，Guava 还是为字符串提供了专门的语法和 API。

CharMatcher 的功能主要在于对特定类或字符串执行这些操作：trimming、collapsing、removing、retaining 等。

### 使用示例

#### 创建 CharMatcher

很多需求都可以被 CharMatcher 的工厂方法满足：

- any()
- none()
- whitespace()
- breakingWhitespace()
- invisible()
- digit()
- javaLetter()
- javaDigit()
- javaLetterOrDigit()
- javaIsoControl()
- javaLowerCase()
- javaUpperCase()
- ascii()
- singleWidth()

其它一些常用的获得一个 CharMatcher 的方法包括：

| 方法 	| 描述 |
| :-                    | :- |
| anyOf(CharSequence) 	| 表明你想匹配的所有字符，例如：`CharMatcher.anyOf("aeiou")` 可以匹配小写元音字母。|
| is(char) 	            | 表明你想匹配的一个确定的字符。|
| inRange(char, char) 	| 表明你想匹配的一个字符范围，例如：`CharMatcher.inRange('a', 'z')`。|

此外，CharMatcher 还有这些方法：`negate()`、`and(CharMatcher)`、`or(CharMatcher)`。这些方法可以为 CharMatcher 提供方便的布尔运算。

#### 使用 CharMatcher

CharMatcher 提供了很多方法来对匹配的字符序列 CharSequence 进行操作。以下只是列出了一些常用方法。

| 方法 	| 描述 | 
| :- | :- |
| collapseFrom(CharSequence, char) 	| 将一组连续匹配的字符串替换为一个指定的字符。例如：`WHITESPACE.collapseFrom(string, ' ')` 可以将连续的空字符串替换为单个字符。|
| matchesAllOf(CharSequence) 	    | 测试字符序列是否全部匹配。例如：ASCII.matchesAllOf(string) 可以测试字符是否全部是 ASCII。|
| removeFrom(CharSequence) 	        | 将匹配的字符序列移除 |
| retainFrom(CharSequence) 	        | 将没有匹配的字符序列移除 |
| trimFrom(CharSequence) 	        | 去除开头和结尾匹配的部分 |
| replaceFrom(CharSequence, CharSequence) 	| 将匹配的字符替换为给定的序列 |

### 方法分类

根据函数的返回值和名称将这些方法分为三类。

#### 第一类是判定型函数，判断 CharMacher 和入参字符串的匹配关系。

```java
CharMatcher.is('a').matchesAllOf("aaa");//true
CharMatcher.is('a').matchesAnyOf("aba");//true
CharMatcher.is('a').matchesNoneOf("aba");//true
```


#### 第二类是计数型函数，查找入参字符串中第一次、最后一次出现目标字符的位置，或者目标字符出现的次数，比如 indexIn，lastIndexIn 和 countIn。

```java
CharMatcher.is('a').countIn("aaa");     // 3
CharMatcher.is('a').indexIn("java");    // 1
```


#### 第三类就是对匹配字符的操作。包括 removeFrom、retainFrom、replaceFrom、trimFrom、collapseFrom 等。

```java
CharMatcher.is('a').retainFrom("bazaar"); // "aaa"
CharMatcher.is('a').removeFrom("bazaar"); // "bzr"
CharMatcher.anyOf("ab").trimFrom("abacatbab"); // "cat"
```


### Example

```java
// 1.获取字符串中数字：12387
String str1 = "mahesh123ff87f";
String result1 = CharMatcher.digit().retainFrom(str1);
```

```java
// 2.把多个空格替换,并去掉首位的空格Mahesh Parashar
String str2 = "     Mahesh     Parashar  ";
String result2 = CharMatcher.whitespace().trimAndCollapseFrom(str2, ' ');
```

```java
// 3.去掉转义字符(\t,\n,\b...)： abcdefg
String str3 = " ab\tcd\nef\bg";
String result3 = CharMatcher.javaIsoControl().removeFrom(str3);
```

```java
// 4.把所有的数字用"*"代替：***abc**dds
String str4 = "124abc85dds";
String result4 = CharMatcher.javaDigit().replaceFrom(str4,"*");
```

```java
// 5.获取所有的数字和小写字母 ：124abc85dds1sd
String str5 = "124abc85ddsAF1HNsd";
String result5 = CharMatcher.javaDigit().or(CharMatcher.javaLowerCase()).retainFrom(str5);
```

```java
// 6.获取所有的大写字母：AFHN
String result6 = CharMatcher.javaUpperCase().retainFrom(str5);
```

```java
// 7.获取所有单字节长度的符号：,dg,123AH
String str7 = ",dg,123AH中国";
String result7 = CharMatcher.singleWidth().retainFrom(str7);
```

```java
// 8.获取字母：FirstNameLastName
String str8 = "FirstName LastName +1 123 456 789 !@#$%^&*()_+|}{:\"?><";
String result8 = CharMatcher.javaLetter().retainFrom(str8);
```


```java
// 9.获取字母和数字：FirstNameLastName1123456789
String result9 = CharMatcher.javaLetterOrDigit().retainFrom(str8);
```

```java
// 10.出现次数统计：54
Integer count10 = CharMatcher.any().countIn(str8);
```

```java
// 11.数字出现次数：10
Integer count11 = CharMatcher.digit().countIn(str8);;
```
```

```java
// 12.获得除大写字母外其他所有字符 ：FN LN +1 123 456 789 !@#$%^&*()_+|}{:"?><
String result12 = CharMatcher.javaLowerCase().negate().retainFrom(str8);
```

CharMatcher本身提供了很多 CharMatcher 的实现类,如下:

- ANY: 匹配任何字符
- ASCII: 匹配是否是ASCII字符
- BREAKING_WHITESPACE: 匹配所有可换行的空白字符(不包括非换行空白字符,例如"\u00a0")
- DIGIT: 匹配ASCII数字
- INVISIBLE: 匹配所有看不见的字符
- JAVA_DIGIT: 匹配UNICODE数字, 使用 Character.isDigit() 实现
- JAVA_ISO_CONTROL: 匹配ISO控制字符, 使用 Charater.isISOControl() 实现
- JAVA_LETTER: 匹配字母, 使用 Charater.isLetter() 实现
- JAVA_LETTER_OR_DIGET: 匹配数字或字母
- JAVA_LOWER_CASE: 匹配小写
- JAVA_UPPER_CASE: 匹配大写
- NONE: 不匹配所有字符
- SINGLE_WIDTH: 匹配单字宽字符, 如中文字就是双字宽
- WHITESPACE: 匹配所有空白字符

```java
//        CharMatcher is(char match): 返回匹配指定字符的Matcher
//        CharMatcher isNot(char match): 返回不匹配指定字符的Matcher
//        CharMatcher anyOf(CharSequence sequence): 返回匹配sequence中任意字符的Matcher
//        CharMatcher noneOf(CharSequence sequence): 返回不匹配sequence中任何一个字符的Matcher
//        CharMatcher inRange(char startInclusive, char endIncludesive): 返回匹配范围内任意字符的Matcher
//        CharMatcher forPredicate(Predicate<? super Charater> predicate): 返回使用predicate的apply()判断匹配的Matcher
//        CharMatcher negate(): 返回以当前Matcher判断规则相反的Matcher
//        CharMatcher and(CharMatcher other): 返回与other匹配条件组合做与来判断的Matcher
//        CharMatcher or(CharMatcher other): 返回与other匹配条件组合做或来判断的Matcher
//        boolean matchesAnyOf(CharSequence sequence): 只要sequence中有任意字符能匹配Matcher,返回true
//        boolean matchesAllOf(CharSequence sequence): sequence中所有字符都能匹配Matcher,返回true
//        boolean matchesNoneOf(CharSequence sequence): sequence中所有字符都不能匹配Matcher,返回true
//        int indexIn(CharSequence sequence): 返回sequence中匹配到的第一个字符的坐标
//        int indexIn(CharSequence sequence, int start): 返回从start开始,在sequence中匹配到的第一个字符的坐标
//        int lastIndexIn(CharSequence sequence): 返回sequence中最后一次匹配到的字符的坐标
//        int countIn(CharSequence sequence): 返回sequence中匹配到的字符计数
//        String removeFrom(CharSequence sequence): 删除sequence中匹配到到的字符并返回
//        String retainFrom(CharSequence sequence): 保留sequence中匹配到的字符并返回
//        String replaceFrom(CharSequence sequence, char replacement): 替换sequence中匹配到的字符并返回
//        String trimFrom(CharSequence sequence): 删除首尾匹配到的字符并返回
//        String trimLeadingFrom(CharSequence sequence): 删除首部匹配到的字符
//        String trimTrailingFrom(CharSequence sequence): 删除尾部匹配到的字符
//        String collapseFrom(CharSequence sequence, char replacement): 将匹配到的组(连续匹配的字符)替换成replacement
//        String trimAndCollapseFrom(CharSequence sequence, char replacement): 先trim在replace
```






