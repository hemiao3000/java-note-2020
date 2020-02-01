
Multiset 是对 Set 的功能扩展，其中允许存放多个重复的对象。

  > 注意，MultiSet 虽然在功能/逻辑上是扩展了 Set ，但是它继承的是 Collection 接口，而不是 Set 接口。

Multiset 的常用实现类有：

  - HashMultiset
  - TreeMultiset

| 方法名 | 说明 |
| -: | : |
| `boolean add(E element)` | 添加一个出现的指定元素这个 multiset 。|
| `int add(E element, int occurrences)` | 增加大量的元素到这个 multiset 。|
| `boolean contains(Object element)` |  确定此多集是否包含指定的元素 。|
| `boolean containsAll(Collection<?> elements)` | 返回true，如果这个多集至少包含一个出现的指定集合中的所有元素。|
| `int count(Object element)` | 返回出现的元素的在该multiset的数目（元素的数量）。|
| `Set<E> elementSet()` | 返回集包含在此多集不同的元素。|
| `Set<Multiset.Entry<E>> entrySet()` | 返回此多集的内容的视图，分组在 Multiset.Entry 实例中，每一个都提供了多集的一个元素和元素的计数。|
| `boolean equals(Object object)` | 比较指定对象与此multiset是否相等。|
| `int hashCode()` | 返回此multiset的哈希码。|
| `Iterator<E> iterator()` | 返回一个迭代在这个集合中的元素。|
| `boolean remove(Object element)` | 移除此多集multiset的单个出现的指定元素，如果存在。|
| `int remove(Object element, int occurrences)` | 删除了一些出现，从该多集multiset的指定元素。|
| `boolean removeAll(Collection<?> c)` | 删除所有这一切都包含在指定集合（可选操作）在此集合的元素。|
| `boolean retainAll(Collection<?> c)`  | 保持那些包含在指定collection（可选操作）在此只集合中的元素。|
| `int setCount(E element, int count)` | 添加或删除，使得该元素达到所期望的计数的元件的必要出现。|
| `boolean setCount(E element, int oldCount, int newCount)` | 有条件设置元素的计数为一个新值，如在 `setCount(对象，INT)` 中所述，条件是该元素预期的当前计数。|
| `String toString()` | 返回该对象的字符串表示。|

