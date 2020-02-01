
Multimap 是多重映射接口扩展映射，使得其键一次可被映射到多个值。

  > 注意，Multimap 没有继承任何集合框架中的接口

Multimap 接口的实现类有：

  - ArrayListMultimap，key 的行为类似 HashMap，value的行为类似 ArrayList 。
  - HashMultimap，key 的行为类似 HashMap，value 的行为类似 HashSet 。
  - TreeMultimap，key 的行为类似 TreeMap，value 的行为类似 TreeSet。

| 方法 | 说明 |
| -: | :- |
| `Map<K, Collection<V>> asMap()` | 返回此multimap中的视图，从每个不同的键在键的关联值的非空集合映射。|
| `void clear()` | 将删除所有multimap中的键值对，留下空。|
| `boolean containsEntry(Object key, Object value)` | 返回 true 如果此多重映射包含至少一个键值对，键键和值 value。|
| `boolean containsKey(Object key)` | 返回 true，如果这个 multimap 中至少包含一个键值对的键 key。|
| `boolean containsValue(Object value)` | 返回true，如果这个multimap至少包含一个键值对的值值。|
| `Collection<Map.Entry<K,V>> entries()` | 返回包含在此multimap中，为Map.Entry的情况下，所有的键 - 值对的视图集合。|
| `boolean equals(Object obj)` | 比较指定对象与此多重映射是否相等。|
| `Collection<V> get(K key)` | 返回，如果有的话，在这个multimap中键关联的值的视图集合。|
| `int hashCode()` | 返回此多重映射的哈希码。|
| `boolean isEmpty()` | 返回true，如果这个multimap中未包含键 - 值对。|
| `Multiset<K> keys()` | 返回一个视图集合包含从每个键值对这个multimap中的关键，没有折叠重复。|
| `Set<K> keySet()` | Returns a view collection of all distinct keys contained in this multimap.| 
| `boolean put(K key, V value)` | 存储键 - 值对在这个multimap中。| 
| `boolean putAll(K key, Iterable<? extends V> values)` | 存储一个键 - 值对在此multimap中的每个值，都使用相同的键 key。 |
| `boolean putAll(Multimap<? extends K,? extends V> multimap)` | 存储了所有键 - 值对多重映射在这个multimap中，通过返回 multimap.entries() 的顺序. |
| `boolean remove(Object key, Object value)` | 删除一个键 - 值对用键键，并从该多重映射的值的值，如果这样的存在。| 
| `Collection<V> removeAll(Object key)` | 删除与键键关联的所有值。| 
| `Collection<V> replaceValues(K key, Iterable<? extends V> values)` | 存储与相同的键值，替换任何现有值的键的集合。 |
| `int size()` | 返回此多重映射键 - 值对的数量。| 
| `Collection<V> values()` | 返回一个视图集合包含从包含在该 multimap 中的每个`键值对`的值，而不发生重复 (`values().size() == size()`) |
