<span class="title">Commons lang3 常用方法</span>

# ObjectUtils

## defaultIfNull 提供默认值

检查第一个参数是否为 null。不为 null，则返回第一个参数；为 null，则返回第二个参数。

逻辑含义就是，在第一个参数为 null 的情况下，用第二参数去替代它<small>（让程序继续执行下去）</small>。

```java
T defaultIfNull(T object, T defaultValue)
```

## allNotNull 要求所有参数都不为 null

集合 *`Validate`* 工具类，可以用于参数的批量非空检查。

```java
boolean anyNotNull(final Object... values)
```

## equals 空安全的比较

被标记为过期。被 java 7 中的 *`java.util.Objects.equals(Object, Object)`* 所替代。

```java
boolean equals(final Object object1, final Object object2) 
```


# StringUtils

## isEmpty 字符串是否为空 

```java
isEmpty(CharSequence cs)
```

检查参数字符串是否为 null、empty。是，则返回 true；不是，则返回 false。

<font color="red">**注意**</font>，Empty 的判断标准涵盖了 Null，但没有涵盖 Blank 。

例如：

```java
StringUtils.isEmpty(null)      = true
StringUtils.isEmpty("")        = true
StringUtils.isEmpty(" ")       = false
StringUtils.isEmpty("bob")     = false
StringUtils.isEmpty("  bob  ") = false
```

## isBlank  字符串是否为空 

```java
isBlank(CharSequence cs) 
```

检查参数字符串是否为 null、empty 或 blank。是，则返回 true；不是，则返回 false。

Blank 的判断标准涵盖了 Null 和 Empty 。

例如：

```java
StringUtils.isBlank(null)      = true
StringUtils.isBlank("")        = true
StringUtils.isBlank(" ")       = true
StringUtils.isBlank("bob")     = false
StringUtils.isBlank("  bob  ") = false
```


## equals  字符串是否相等

```java
equals(CharSequence cs1, CharSequence cs2)
```
比较两个参数字符串是否相等，返回一个 boolean

例如：

```java
StringUtils.equals(null, null)   = true
StringUtils.equals(null, "abc")  = false
StringUtils.equals("abc", null)  = false
StringUtils.equals("abc", "abc") = true
StringUtils.equals("abc", "ABC") = false
```

## join 合并数组为单一字符串，可传分隔符

```java
join(byte[] array, char separator)
// 其它重载方法略
```
将字节数组转换成 string，以指定字符分隔

例如：

```java
StringUtils.join(null, *)         = null
StringUtils.join([], *)           = ""
StringUtils.join([null], *)       = ""
StringUtils.join([1, 2, 3], ';')  = "1;2;3"
StringUtils.join([1, 2, 3], null) = "123"
```

### split 分割字符串

```java
split(String str, String separatorChars)
```

将字符串以指定字符分隔，返回数组。

例如：

```java
StringUtils.split(null, *)         = null
StringUtils.split("", *)           = []
StringUtils.split("abc def", null) = ["abc", "def"]
StringUtils.split("abc def", " ")  = ["abc", "def"]
StringUtils.split("abc  def", " ") = ["abc", "def"]
StringUtils.split("ab:cd:ef", ":") = ["ab", "cd", "ef"]
```

### replace 替换字符串

```java
replace(String text, String searchString, String replacement) 
```

在参数 `text` 中查找 `searchString`，找到后，将 `searchString` 替换成参数 `replacement` 。

注意，这里的替换并非替换 text 原内容，而是生成一个新的替换后的字符串对象。

```java
StringUtils.replace(null, *, *)        = null
StringUtils.replace("", *, *)          = ""
StringUtils.replace("any", null, *)    = "any"
StringUtils.replace("any", *, null)    = "any"
StringUtils.replace("any", "", *)      = "any"
StringUtils.replace("aba", "a", null)  = "aba"
StringUtils.replace("aba", "a", "")    = "b"
StringUtils.replace("aba", "a", "z")   = "zbz"
```

# ArrayUtils

## contains 是否包含某字符串

```java
contains(CharSequence seq, CharSequence searchSeq)
```

检查字符串<small>（第一个参数）</small>中是否包含指定字符<small>（第二个参数）</small>，返回 boolean

```java
StringUtils.contains(null, *)     = false
StringUtils.contains(*, null)     = false
StringUtils.contains("", "")      = true
StringUtils.contains("abc", "")   = true
StringUtils.contains("abc", "a")  = true
StringUtils.contains("abc", "z")  = false
```

## addAll  添加所有

```java
addAll(boolean[] array1, boolean... array2)
// 其它重载方法略
```

将给定的多个数据添加到指定的数组中，返回一个新的数组

```java
ArrayUtils.addAll(array1, null)   = cloned copy of array1
ArrayUtils.addAll(null, array2)   = cloned copy of array2
ArrayUtils.addAll([], [])         = []
```

## clone 克隆一个数组

```java
clone(boolean[] array)
// 其它重载方法略
```

复制数组并返回。如果参数数组为空将返回空

## isEmpty 是否空数组

```java
isEmpty(boolean[] array)
// 其它重载方法略
```
判断该数组是否为空，返回一个boolean值

## add 向数组添加元素

```java
add(boolean[] array, boolean element)
```

将给定的数据添加到指定的数组中，返回一个新的数组

```java
ArrayUtils.add(null, true)          = [true]
ArrayUtils.add([true], false)       = [true, false]
ArrayUtils.add([true, false], true) = [true, false, true]
```

`add(boolean[] array, int index, boolean element)` 将给定的数据添加到指定的数组下标中，返回一个新的数组。

如果所指定的下标处已有元素，则原元素<small>（及其后续元素）</small>会被『挤』到下一个单元。

如果所指定的下标超出数组长度，则会抛出异常：*`IndexOutOfBoundsException`* 。

```java
ArrayUtils.add(null, 0, true)          = [true]
ArrayUtils.add([true], 0, false)       = [false, true]
ArrayUtils.add([false], 1, true)       = [false, true]
ArrayUtils.add([true, false], 1, true) = [true, true, false]
```

byte, int, char, double, float, int, long ,short, T[] 同理

## subarray 截取数组

```java
subarray(boolean[] array, int startIndexInclusive, int endIndexExclusive)
// 其它重载函数略
```

截取数组，按指定位置区间截取并返回一个新的数组

<font color="red">**注意**</font>，截取区间左闭右开，包括起始下标索引，不包括截至下标索引。


## indexOf 查找下标

```java
indexOf(boolean[] array, boolean valueToFind) 
// 其它重载函数略
```

从数组的第一位开始查询该数组中是否有指定的数值，存在返回 index 的数值，否则返回 -1 。

```java
indexOf(boolean[] array, boolean valueToFind, int startIndex)
// 其它重载函数略
```

从数组的第 startIndex 位开始查询该数组中是否有指定的数值，存在返回 index 的数值，否则返回 -1 。

## toObject  基础类型数据数组转换为对应的 Object 数组

```java
toObject(boolean[] array)
// 其它重载函数略
```
将基本类型数组转换成对象类型数组并返回

另外，toObject 的反相操作时：

```java
toPrimitive(Boolean[] array)
// 其它重载函数略
```

将对象类型数组转换成基本类型数组并返回
