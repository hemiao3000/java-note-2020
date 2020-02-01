Iterators 是 Guava 中对 Iterator 迭代器操作的帮助类，这个类提供了很多有用的方法来简化 Iterator 的操作。

## all 方法：判断迭代器中的元素是否都满足某个条件 

```java
List<String> list = Lists.newArrayList("Apple","Pear","Peach","Banana");

Predicate<String> condition = new Predicate<String>() {
    @Override
    public boolean apply(String input) {
        return ((String)input).startsWith("P");
    }
};

boolean allIsStartsWithP = Iterators.all(list.iterator(), condition);
System.out.println("all result == " + allIsStartsWithP);
```        

all 方法的第一个参数是 Iterator，第二个参数是 `Predicate<String>` 的实现，这个方法的意义是不需要我们自己去写 while 循环了，他的内部实现中帮我们做了循环，把循环体中的条件判断抽象出来了。

## any 方法：判断迭代器中是否至少有一个满足条件

any ( ) 方法的参数和 all ( ) 方法一样，就不再具体举例了。

## get 方法：获得迭代器中的第 x 个元素

```java
String secondElement = Iterators.get(list.iterator(), 1);
```

## filter 方法：过滤/选中符合条件的项

```java
Iterator<String> startPElements = Iterators.filter(list.iterator(), new Predicate<String>() {
    @Override
    public boolean apply(String input) {
        return input.startsWith("P");
    }
});
```

filter 方法的第一个参数是源迭代器，第二个参数是 Predicate 的实现，其 apply 方法会返回当前元素是否符合条件。

## find 方法：返回符合条件的第一个元素

```java
String length5Element = Iterators.find(list.iterator(), new Predicate<String>() {
    @Override
    public boolean apply(String input) {
        return input.length() == 5;
    }
});
```        

## transform 方法：对迭代器元素做转换

```java
Iterator<Integer> countIterator = Iterators.transform(list.iterator(), new Function<String, Integer>() {
    @Override
    public Integer apply(String input) {
        return input.length();
    }
});
```

上面的例子中我们将字符串转换成了其长度，transform 方法输出的是另外一个 Iterator 。
