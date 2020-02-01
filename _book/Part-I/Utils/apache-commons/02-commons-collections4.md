<span class="title">Commons Collections 常用方法</span>

# IterableUtils 工具类

*`Iterable`* 的概念涵盖了 **数组** 和 **集合** 。所以，下述的方法可以作用于数组，而后续的 CollectionUtils 中的方法就只能作用于集合。

## find ( )

```java
E find(Iterable<E> iterable, Predicate<? super E> predicate)
```

从 数组或集合<small>（第一个参数）</small>中查找/选中指定元素。

至于是哪个元素被选中取决于第二个参数<small>（接口）</small>的执行结果。

<font color="red">**注意**</font>，如果有多个元素满足标准条件，只会返回满足条件的第一个元素。


```java
// 伪代码
list = [1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17];

Integer ret = IterableUtils.find(list, obj -> {
    if ((obj + 1) % 5 == 0 && obj > 7)
        return true;
    else
        return false;
});

ret == 9
```

## forEach ( )

```java
void forEach(Iterable<E> iterable, Closure<? super E> closure)
```

循环遍历集合，对集合中的每个元素一次执行指定代码。

```java
// 伪代码
list = [1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17];

IterableUtils.forEach(list, input -> {
    System.out.print(input + " ");
});

System.out.println();
```


# CollectionUtils 工具类

## isEmpty ( )

```java
boolean isEmpty(Collection<?> coll)
```

判断（要求）集合为 null 或为空 。

```java
CollectionUtils.isEmpty(null)       == true
CollectionUtils.isEmpty([10])       == false
CollectionUtils.isEmpty([10, 20])   == false
CollectionUtils.isEmpty(["a"])      == false
CollectionUtils.isEmpty(["a", "b"]) == false
```

## isNotEmpty ( )

```java
boolean isNotEmpty(Collection<?> coll)
```

判断（要求）集合部位 null 且不为空 。

```java
// 伪代码
CollectionUtils.isNotEmpty(null)        == false
CollectionUtils.isNotEmpty([10])        == true
CollectionUtils.isNotEmpty([10, 20])    == true
CollectionUtils.isNotEmpty(["a"])       == true
CollectionUtils.isNotEmpty(["a", "b"])  == true
```

## addAll ( )

```java
boolean addAll(Collection<C> collection,
               Iterable<? extends C> iterable)
```

将 参数2 中的元素，添加至 参数1 中。
如果 参数1 并未发生变化，则返回 false 。

## isEqualCollection ( )

```java
boolean isEqualCollection(Collection<?> a, Collection<?> b)
```

判断两个集合是否相等。

## select ( )

```java
Collection<O> select(Iterable<? extends O> inputCollection,
                     Predicate<? super O> predicate)
```

从参数集合中“挑选/选中”一部分元素，并存入返回的结果集合（新集合）中。

参数集合中的哪些元素会被“选中”，取决于第二个参数（接口）的执行结果。

*`IterableUtils.find()`* 最多只会挑选出一个；而 *`CollectionUtils.select()`* 可以挑选出一批。

```java
// 伪代码
Collections list = [1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 14, 15];
Collection otherList = CollectionUtils.select(list, (n) -> {
    return (n % 2 == 0) ? true: false;
});

otherList == [2, 4, 6, 8, 10, 12, 14];
```

## filter ( )

```java
boolean filter(Iterable<T> collection,
               Predicate<? super T> predicate)
```

该方法和上面的 `find()` 即相似又不相似。它们都会从参数集合中挑出某个/某些元素，不同的是 `find()` 的使用场景是：要这些挑出来的元素，而 `filter()` 的使用场景是：要它们之外的留下来的元素。

对参数集合进行过滤，过滤标准取决于第二个参数（接口）的执行结果，对于每一个元素而言，<font color="#0088dd">**返回 true 时，会被留下**</font>；返回 false 时，会被移除 。

如果整个集合没有元素被移除，则 filter 方法返回 false，否则，但凡至少有一个元素被移除，则返回 true 。


```java
// 伪代码
Collections list = [1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 14, 15];

CollectionUtils.filter(list, n -> {
    return n % 2 == 0 ? true : false;
});

list == [2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 14, 15, 16];
```


## transform ( )

```java
void transform(Collection<C> collection,
               Transformer<? super C, ? extends C> transformer)
```

对参数集合进行“变形”。即，改动原集合中的元素。

参数集合中的每个元素会作何种“变形”，取决于第二个参数（接口）的执行结果。

*`IterableUtils.foreach()`* 对数据和容器做迭代，但不会改动其中元素的内容。常用于 `循环打印` 这样的场景；而 *`CollectionUtils.transform()`* 则会改动容器中的元素的内容。


```java
// 伪代码
Collections list = [1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 14, 15];
CollectionUtils.transform(list, (n) -> {
    return n + 1;
});

list == [2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 14, 15, 16];
```

### collect ( )

```java
Collection<O> collect(Iterable<I> inputCollection,
                      Transformer<? super I, ? extends O> transformer)
```

*`collect()`* 是 *`transform()`* 方法的另一个版本：它改动的不是原容器中的元素，而是生成新元素，并将它们放入新集合。<font color="#0088dd">**原集合中的元素不变**</font> 。

```java
// 伪代码
Collections list = [1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 14, 15];

Collections oth = CollectionUtils.collect(list, n -> {
    return n % 2 == 0 ? true : false;
});

oth == [2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 14, 15, 16]
```


集合的遍历总结

| 方法                         | 备注                                     |
| :--------------------------- | :-------------------------------------- | 
| IterableUtils.forEach( )     | 仅仅是遍历 <font color="#0088dd">**数组**</font>和集合                     |
| IterableUtils.find( )        | 遍历数组和集合，选中 <font color="#0088dd">**一个**</font> 元素并返回        |
| CollectionUtils.select( )    | 遍历集合，选中 <font color="#0088dd">**一批**</font> 元素并返回               | 
| CollectionUtils.filter( )    | 遍历集合，选中一批元素并从原集合中<font color="#0088dd">**移除**</font>       | 
| CollectionUtils.transform( ) | 遍历集合，对 <font color="#0088dd">**原集合**</font> 中的元素进行形变           |
| CollectionUtils.collect( )   | 遍历集合，对集合中的元素进行形变，并返回 <font color="#0088dd">**新集合**</font> | 



# 扩展的数据结构

## Bag 接口

Bag 的行为似乎是介于 Set 和 Map 之间，向 Bag 中添加重复数据时，Bag 不会记录数据本身，而是去记录数据出现/添加的次数。

Bag 接口继承自 Collection 接口，它有两个常见实现类：

- **HashBag** 
- **TreeBag** 

```java
Bag<Integer> b1 = new HashBag<>();

b1.add(10);
b1.add(10);
b1.add(10);
b1.add(10);
b1.add(20);

b1.getCount(10) == 4
b1.getCount(20) == 1

b1.size() == 5
b1.uniqueSet().size() == 2
```

<font color="red">**注意**</font>，从 bag 中移除元素时，<font color="red">**并非**</font>将其数显次数减一，而是类似移除 Map 中的键值对一样，将该元素及其所有出现次数都移除。

## MultiSet 接口

MultiSet 接口与 Bag 接口的功能有些类似：当你向 Set 中存入重复的数据时，它会记录下该数据出现次数，而非重复存储数据本身。

```java
// 伪代码
MultiSet<String> set = new HashMultiSet<>();

set.add("one");
set.add("two");
set.add("two");
set.add("three", 3);

set == [one:1, two:2, three:3]
set.size() ==  6
set.getCount("one") == 1
set.getCount("two") == 2
set.getCount("three") == 3
```

HashMultiSet 比 Bag 『高级』的地方在于，它可以对对象出现的次数作『加减法』：

```java
// 伪代码
set.remove("three");
set.getCount("three") == 2

set.remove("three", 2);
set.getCount("three") == 0
```

## BidiMap 接口

BidiMap 接口继承自 Map 接口，它除了支持 Map 接口的<font color="#0088dd">**根据键找值**</font> ，还支持反向的<font color="#0088dd">**根据值找键**</font> 。因为这种行为它被称为 **双向Map** 。

BidiMap 接口的实现类有：

- TreeBidiMap（底层是红黑树）
- DualHashBidiMap（底层是两个 HashMap）
- DualLinkedHashBidiMap（底层是两个LinkedHashMap）
- DualTreeBidiMap（底层是两个 TreeMap）

```java
map.get(key);       // 根据 key 找 value
map.getKey(value);  // 根据 value 找 key
```

删除时也是一样：

```java
map.remove(key);        // 以 key 为依据删除键值对
map.removeValue(value); // 以 value 为依据删除键值对
```

## MultiKeyMap 类

MultiKeyMap 实现了由两个 key（甚至更多），来对应一个 value 的数据结构。现实中类似的使用场景有：地理位置和矩阵 。

MultiKeyMap 底层采用 MultiKey 作为普通 Map 的key，采用 HashedMap 存储。

```java
// 伪代码
MultiKeyMap<Integer, String> map = new MultiKeyMap<>();

// 映射键值对关系，方式一
MultiKey<Integer> key = new MultiKey<Integer>(10, 20);
map.put(key, "hello world");

// 映射键值对关系，方式二
map.put(20, 10, "goodbye");

map.get(key)      // hello world
map.get(10, 20)   // hello world
map.get(20, 10)   // goodbye
```


### MultiValuedMap 接口

MultiValuedMap 和 MultiKeyMap 相反，它是支持一个 key 对应多个 值。

它有两个实现类；

- ArrayListValuedHashMap<small>，等价于 Map<K, List\<V>></small>
- HashSetValuedHashMap<small>，等价于 Map<K, Set\<V>></small>
