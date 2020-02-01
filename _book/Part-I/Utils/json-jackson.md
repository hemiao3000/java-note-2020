<span class="title">Jackson</span>

jackson 是 SpringMVC 默认的 json 库。

# 基本使用

jackson 提供了 *`writeValueAsString()`* 和 *`readValue()`* 两个直接用于生成和解析的方法，前者实现序列化，后者实现了反序列化。

```java
public class User {
    private String name;
    private int age;
    private String emailAddress;

    // 省略 getter/setter
}
```

## Object to JSON-String

```java
ObjectMapper mapper = new ObjectMapper();
User user = new User("怪盗kidou",24);
String jsonString = mapper.writeValueAsString(user);

```

## JSON-String to Object

```java
ObjectMapper mapper = new ObjectMapper();
String jsonString = "{\"name\":\"怪盗kidou\",\"age\":24}";
User user = mapper.readValue(str, User.class);
```

jackson 被认为功能丰富而强大的原因是，它除了提供 `对象-字符串` 之间的相互转换，还提供了 `对象-各种流` 之间的转换。

# @JsonProperty 注解

默认/一般情况下，JSON 字段中的名字和类的属性名是一致的。但是也有不一致的情况，因为本身 **驼峰命名法**<small>（如 Java）</small>和 **下划线命名法**<small>（如 C）</small>本身就是两大命名规则【流派】。

对类的属性使用 *`@JsonProperty`* 注解，可以重新指定与该属性对应的 JSON 字符串中的名字。

```java
@JsonProperty("email_address")
private String emailAddress;
```

你甚至可以重新命名为另一个看起来毫不相关的名字：

```java
@JsonProperty("xxx")
public String emailAddress;
```

# 数组的序列化和反序列化

数组的序列化和反序列化比较简单，与普通对象类似，唯一需要注意的就是填写正确的数组类型：

```java
jsonStr = mapper.writeValueAsString(arr);

arr = mapper.readValue(jsonStr, int[].class);
arr = mapper.readValue(jsonStr, String[].class);
arr = mapper.readValue(jsonStr, User[].class);
```

# 集合的序列化和反序列化

相较于数组，集合的序列化和反序列化就复杂一些，因为泛型的 **类型檫除**，Java 【分辨】不出 ***`List<String>`*** 和 ***`List<User>`***，对 Java 而言它们的类型都是 ***`List.class`*** 。

为了解决的上面的问题，jackson 为我们提供了 ***`TypeReference`*** 来实现对泛型的支持，所以当我们希望使用将以上的数据解析为 *`List<String>`* 时需要讲 *`List<String>`* 【套在】*`new TypeReference<T>() { }`* 中的 ***`T`*** 部分。

```java
List<User> list = mapper.readValue(jsonStr, new TypeReference<List<User>>() { });
```


# 处理对象的 NULL 属性

默认情况下，对于对象的值为 NULL 的属性，jackson 默认也是会【包含】在所生成的 JSON 字符串中。

```java
ObjectMapper mapper = new ObjectMapper();

User tom = new User("tom", 21);
String jsonStr = mapper.writeValueAsString(tom);

System.out.println(jsonStr);
// {"name":"tom","age":21,"emailAddress":null}
```

如果你不希望在所生成的 JSON 格式的字符串中含有值为 NULL 的属性，有两种方案：<strong>注解</strong> 和 <strong>配置</strong> 。

## 方案一：注解

```java
@JsonInclude(Include.NON_NULL)
public class User {
  ...
}
```

## 方案二：配置

```java
ObjectMapper mapper = new ObjectMapper();
mapper.setSerializationInclusion(Include.NON_NULL);

...
```

# 格式化 Date 类型属性

当所需要序列化和反序列化的对象的属性有 Date 类型时，这里就涉及到 Date 类型的字符串形式的格式问题，为此 @JsonFormat 注解提供了 pattern 属性用以自定义其字符串格式

```java
@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
private Date birthDate;
```

# 循环引用

在 Java 中，两个对象可能会互相持有，此时就是循环引用现象。当序列化其中一个对象时，会涉及到它的相关对象，而序列化它的相关对象时，又会再次序列化它自己，而序列化它自己时又需要去序列化它的相关对象... ...，从而造成一个死循环。


*`@JsonIgnore`* 注解用于排除某个属性，这样该属性就不会被 Jackson 序列化和反序列化：

```java
@JsonIgnore
private String emailAddress;
```

另外，功能相似的还有 *`@JsonIgnoreProperties`* 注解，不过它是类注解，可以批量设置：

```java
@JsonIgnoreProperties({"age", "emailAddress"})
public class User {
    ...
}
```

在从 JSON 字符串反序列化为 Java 类的时候，*`@JsonIgnoreProperties(ignoreUnknown=true)`* 会忽略所有没有  Getter 和 Setter 的属性。该注解在 Java 类和 JSON 不完全匹配的时候很有用。

不过，我们在设计系统时，所追求的对象的关系的目标应该是 <strong>有向无环</strong> 。所以，尽量从这个根本角度避免对象的相互引用。

# 其它

jackson 支持 JSON 字符串与 Map 对象之间的互转：

```java
ObjectMapper mapper = new ObjectMapper();

Map<String, Object> map = new HashMap<>();
map.put("name", "tom");
map.put("age", 20);
map.put("emailAddress", "123@qq.com");
map.put("birthDate", new Date());

String jsonStr = mapper.writeValueAsString(map);

System.out.println(jsonStr);

/***********************************************************/

Map<String, Object> oth = mapper.readValue(jsonStr, Map.class);

for (Map.Entry<String, Object> entry : oth.entrySet())
    System.out.println(entry.getKey() + ", " + entry.getValue());
```

# 基于 Jackson 的 JsonUtils 工具类

```java
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.*;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.core.*;

import lombok.extern.slf4j.Slf4j;
import java.io.IOException;
import java.text.SimpleDateFormat;

@Slf4j
public class JsonUtils {

	public static final String STANDARD_FORMAT = "yyyy-MM-dd hh:mm:ss";

	/**
	 * 对象的所有字段全部列入
	 */
	@JsonInclude
	private static final ObjectMapper objectMapper = new ObjectMapper();

	static {
		// 取消默认转换 timestamps 形式。
		objectMapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);

		// 忽略空 bean 转 json 时的错误。
		objectMapper.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);

		// 所有日期格式统一以 STANDARD_FORMAT 为准。
		objectMapper.setDateFormat(new SimpleDateFormat(STANDARD_FORMAT));

		// 忽略 json 字符串中存在，但 java 对象中不存在的属性的情况。
		objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

	}

	public static <T> String object2String(T object) {
		if (object == null)
			return "";

		if (object instanceof String)
			return (String) object;

		try {
			return objectMapper.writeValueAsString(object);
		} catch (JsonProcessingException e) {
			log.warn("Jackson 转换 [{}] 失败，返回空字符串。", object, e);
		}

		return "";
	}

	public static <T> String object2StringPretty(T object) {
		if (object == null)
			return "";

		if (object instanceof String)
			return (String) object;

		try {
			return objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(object);
		} catch (JsonProcessingException e) {
			log.warn("Jackson 转换对象 [{}] 失败，返回空字符串。", object, e);
		}

		return "";
	}

	@SuppressWarnings("unchecked")
	public static <T> T string2Object(String string, Class<T> clazz) {
		if (string == null || string.trim().length() == 0 || clazz == null)
			throw new IllegalArgumentException();

		if (clazz.equals(String.class))
			return (T) string;

		try {
			return objectMapper.readValue(string, clazz);
		} catch (IOException e) {
			log.warn("Jackson 转换字符串 [{}] 失败，抛出空指针异常。", string, e);
		}

		throw new NullPointerException();
	}

	public static <T> T string2Object(String string, TypeReference<T> typeReference) {
		if (string == null || string.trim().length() == 0 || typeReference == null)
			throw new IllegalArgumentException();

		if (typeReference.getType().equals(String.class))
			return (T) string;

		try {
			return objectMapper.readValue(string, typeReference);
		} catch (IOException e) {
			log.warn("Jackson 转换字符串 [{}] 失败，抛出空指针异常。", string, e);
		}

		throw new NullPointerException();
	}

	public static <T> T string2Object(String string, Class<?> collectionClass, Class<?>... elementClasses) {
		if (string == null || string.trim().length() == 0 || collectionClass == null)
			throw new IllegalArgumentException();

		JavaType javaType = objectMapper.getTypeFactory().constructParametricType(collectionClass, elementClasses);

		try {
			return objectMapper.readValue(string, javaType);
		} catch (IOException e) {
			log.warn("Jackson 转换字符串 [{}] 失败，抛出空指针异常。", string, e);
		}

		throw new NullPointerException();
	}
}
```