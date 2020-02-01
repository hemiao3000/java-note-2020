Guava 提供 Preconditions 类，用于帮助逻辑上的前置校验工作。这些方法都是静态方法。

每个方法都有三个变种：

- 没有额外参数：抛出的异常中没有错误消息；
- 有一个 Object 对象作为额外参数：抛出的异常使用 Object.toString() 作为错误消息；可用于抛出自定义的异常对象。
- 有一个 String 对象作为额外参数，并且有一组任意数量的附加 Object 对象：这个变种处理异常消息的方式有点类似 printf，但考虑 GWT的兼容性和效率，只支持 `%s` 指示符。例如：

```java
checkArgument(i >= 0, "Argument was %s but expected nonnegative", i);
checkArgument(i < j, "Expected i < j, but %s > %s", i, j);
```

| 方法 | 条件不满足时抛出的异常 |
| :- | :- | 
| checkArgument(boolean表达式 )           | IllegalArgumentException |
| checkNotNull(object)                    | NullPointerException |
| checkState(boolean表达式)               | IllegalStateException |
| checkElementIndex(int index, int size)  |	IndexOutOfBoundsException |
| checkPositionIndex(int index, int size) | IndexOutOfBoundsException |
| checkPositionIndexes(int start, int end, int size) | IndexOutOfBoundsException |

注意 Index 和 Position 的区别：

- 索引（Index）通常适用于定位字符串或容器中的某个元素，所以，其合法范围范围 0 ... n-1
- 位置值（Position）通常用于截取字符串或容器，在截取时由于区间是作开右比 [a, b)，所以 b == n 时，b 也是合法的。

使用 Predication 的理由：


- 在静态导入后，Guava 方法非常清楚明晰。checkNotNull 清楚地描述做了什么，会抛出什么异常；
- checkNotNull 直接返回检查的参数，让你可以在构造函数中保持字段的单行赋值风格：`this.field = checkNotNull(field)`
- 简单的、参数可变的 printf 风格异常信息。鉴于这个优点，在 JDK7 已经引入 `Objects.requireNonNull` 的情况下，我们仍然建议你使用 checkNotNull 。

在编码时，如果某个值有多重的前置条件，我们建议你把它们放到不同的行，这样有助于在调试时定位。此外，把每个前置条件放到不同的行，也可以帮助你编写清晰和有用的错误消息。
