# PatternMatchUtils


PatternMatchUtils 类来自 org.springframework.util 包 。

它用于进行简单地正则匹配。

判断规则有：`xxx*`, `*xxx`, `*xxx*` 和 `xxx*yyy` 。 `*` 通配任意个字符。

`注意`，正则规则字符串在前。

```java
// 判断字符串是否符合规则。
static boolean	simpleMatch(String pattern, String str) 

// 判断字符串是否同时满足多个规则。
static boolean	simpleMatch(String[] patterns, String str) 
```