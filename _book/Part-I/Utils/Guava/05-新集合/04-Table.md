
Table 代表一个特殊的映射，必须提供两个键才找到一个对应的值。它类似于创建映射的映射。

Table 接口没有继承任何接口，常用的实现类有：

  - ArrayTable
  - HashBasedTable
  - TreeBasedTable

| 方法 | 说明 |
| -: | :- |
| `Set<Table.Cell<R,C,V>> cellSet()` | 返回集合中的所有行键/列键/值三元组。|
| `void clear()` | 从表中删除所有映射。|
| `Map<R,V> column(C columnKey)` | 返回在给定列键的所有映射的视图。 |
| `Set<C> columnKeySet()` | 返回一组具有表中的一个或多个值的列键。|
| `Map<C,Map\<R,V>> columnMap()` | 返回关联的每一列键与行键对应的映射值的视图。|
| `boolean contains(Object rowKey, Object columnKey)` | 返回 true，如果表中包含与指定的行和列键的映射。|
| `boolean containsColumn(Object columnKey)` | 返回 true，如果表中包含与指定列的映射。|
| `boolean containsRow(Object rowKey)` | 返回 true，如果表中包含与指定的行键的映射关系。|
| `boolean containsValue(Object value)` | 返回 true，如果表中包含具有指定值的映射。|
| `boolean equals(Object obj)` | 比较指定对象与此表是否相等。|
| `V get(Object rowKey, Object columnKey)` | 返回对应于给定的行和列键，如果没有这样的映射存在值，返回 null。<br> 返回此表中的哈希码。|
| `boolean isEmpty()` | 返回true，如果表中没有映射。 |
| `V put(R rowKey, C columnKey, V value)` | 关联指定值与指定键。|
| `void putAll(Table<? extends R,? extends C,? extends V> table)` | 复制从指定的表中的所有映射到这个表。|
| `V remove(Object rowKey, Object columnKey)` | 如果有的话，使用给定键相关联删除的映射。|
| `Map<C,V> row(R rowKey)` | 返回包含给定行键的所有映射的视图。|
| `Set<R> rowKeySet()` | 返回一组行键具有在表中的一个或多个值。 |
| `Map<R, Map<C,V>> rowMap()` | 返回关联的每一行按键与键列对应的映射值的视图。|
| `int size()` | 返回行键/列键/表中的值映射关系的数量。|
| `Collection<V> values()` | 返回所有值，其中可能包含重复的集合。|




