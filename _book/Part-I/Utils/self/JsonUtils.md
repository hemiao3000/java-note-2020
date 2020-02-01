<span class="title">基于 Jackson 的 JSON 库</span>

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