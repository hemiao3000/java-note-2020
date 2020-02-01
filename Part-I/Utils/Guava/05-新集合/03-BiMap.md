
BiMap 是一种特殊的映射其保持映射，同时确保没有重复的值是存在于该映射和一个值可以安全地用于获取键背面的倒数映射。

- 可以用 inverse()反转BiMap<K, V>的键值映射
- 保证值是唯一的，因此 values()返回Set而不是普通的Collection

BiMap 继承自 Map 接口。常用的实现类有：

- HashBiMap

| 方法 | 说明 |
| -: | :- |
| `V forcePut(K key, V value)` | 另一种put的形式是默默删除，在put(K, V)运行前的任何现有条目值值。| 
| `BiMap<V,K> inverse()` | 返回此bimap，每一个bimap的值映射到其相关联的键的逆视图。| 
| `V put(K key, V value)` | 关联指定值与此映射中(可选操作)指定的键。|
| `void putAll(Map<? extends K,? extends V> map)` | 将所有从指定映射此映射(可选操作)的映射。|
| `Set<V> values()` | 返回此映射中包含 Collection 的值视图。|
