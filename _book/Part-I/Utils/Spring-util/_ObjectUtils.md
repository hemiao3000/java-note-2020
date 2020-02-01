# ObjectUtils

`org.springframework.util.ObjectUtils` 有很多处理 null object 的方法. 如 nullSafeHashCode, nullSafeEquals, isArray, containsElement, addObjectToArray, 等有用的方法


- <big>**获取对象基本信息**</big>

  ```java
  // 获取对象的类名。参数为 null 时，返回"null"。
  static String nullSafeClassName(Object obj)

  // 获取对象 HashCode（十六进制形式字符串）。参数为 null 时，返回 0 。
  static String getIdentityHexString(Object obj) 

  // 获取对象的类名和 HashCode。 参数为 null 时，返回 "" 。
  static String identityToString(Object obj)

  // 相当于 toString() 方法，但参数为 null 时，返回：""
  static String getDisplayString(Object obj)

  // 被废弃，建议使用 jdk8 原生 hashCode 方法。
  static int	hashCode(boolean bool)
  ```


- <big>**判断工具**</big>

  ```java
  // 判断数组是否为空。
  static boolean isEmpty(Object[] array)

  // 判断参数对象是否是数组。
  static boolean isArray(Object obj)

  // 判断数组中是否包含指定元素。
  static boolean containsElement(Object[] array, Object element)

  /**
   * 判断参数对象是否为空，判断标准为：
   *     Optional: considered empty if Optional.empty()
   *        Array: considered empty if its length is zero
   * CharSequence: considered empty if its length is zero
   *   Collection: delegates to Collection.isEmpty()
   *          Map: delegates to Map.isEmpty()
   */
  static boolean isEmpty(Object obj)
  ```


- <big>**NullSafe “替代” Object 原生方法**</big>

  ```java
  // 相等，或同为 null 时，返回 true
  static boolean nullSafeEquals(Object o1, Object o2) 

  // 参数为 null 时，返回 0
  static int nullSafeHashCode(Object object)          

  // 参数为 null 时，返回"null"
  static String nullSafeToString(boolean[] array)   
  ```


- <big>**其他工具**</big>

  ```java
  // 向参数数组的末尾追加新元素，并返回一个新数组。
  static <A,O extends A> A[]	addObjectToArray(A[] array, O obj)

  // 原生基础类型数组 --> 包装类数组
  static Object[]	toObjectArray(Object source)  
  ```
