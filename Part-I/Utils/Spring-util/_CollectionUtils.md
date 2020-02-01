# CollectionUtils

CollectionUtils 类来自 `org.springframework.util` 包，它用于处理集合的工具。

- <font>**判断工具**</font>

  ```java
  // 判断集合是否为空。
  static boolean isEmpty(Collection<?> collection) 

  // 判断 Map 是否为空
  static boolean isEmpty(Map<?,?> map) 

  // 判断集合中是否包含某个对象
  static boolean containsInstance(Collection<?> collection, Object element) 

  // 通过迭代器判断某个对象是否在集合中。
  static boolean contains(Iterator<?> iterator, Object element) 

  // 判断集合中是否包含某些对象中的任意一个。
  static boolean containsAny(Collection<?> source, Collection<?> candidates) 

  // 判断集合中的每个元素是否唯一。即集合中不存在重复元素。
  static boolean hasUniqueObject(Collection<?> collection) 
  ```


- <font>**向集合中添加**</font>

  ```java
  // 将数组中的元素都添加找集合中。
  static <E> void	mergeArrayIntoCollection(Object array, Collection<E> collection) 

  // 将 Properties 中的键值对都添加到 Map 中。
  static <K,V> void	mergePropertiesIntoMap(Properties props, Map<K,V> map) 
  ```


- <font>**在集合中查找**</font>

  ```java
  // 返回 List 中最后一个元素。
  static <T> T lastElement(List<T> list)  

  // 返回 Set 中最后一个元素。
  static <T> T lastElement(Set<T> set)    

  // 返回 candidates 中第一个存在于 source 中的元素。
  static <E> E findFirstMatch(Collection<?> source, Collection<E> candidates) 

  // 返回集合中指定类型的元素。
  static <T> T findValueOfType(Collection<?> collection, Class<T> type) 

  // 返回集合中指定类型的元素。如果第一种类型未找到，则查找第二种类型，以此类推。
  static Object findValueOfType(Collection<?> collection, Class<?>[] types) 

  // 返回集合中元素的类型
  static Class<?>	findCommonElementType(Collection<?> collection)   
  ```

- <big>**MultiMap 相关**</font>

  ```java
  // 将一个 Map<K, List<V>> 对象转换成一个 MultiValueMap <K, V> 对象。
  static <K,V> MultiValueMap<K,V>	toMultiValueMap(Map<K, List<V>> map) 

  // 返回 MultiValueMap 对象的一个不可变视图。
  static <K,V> MultiValueMap<K,V>	unmodifiableMultiValueMap(MultiValueMap<? extends K,? extends V> map)  
  ```

- <big>**其他**</big>

  ```java
  // 将数组转换成链表。
  static List	arrayToList(Object source) 
  ```

