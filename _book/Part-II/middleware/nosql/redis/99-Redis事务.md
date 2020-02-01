## 循环添加

```java
@Test
public void demo1() {
    long startTime = System.currentTimeMillis();
    for (int i = 0; i < 1000; i++) {
        redisTemplate.opsForValue().set("hello" + i, "world" + i);
    }
    long endTime = System.currentTimeMillis();
    System.out.println("duration is " + (endTime - startTime));
}
```

```
duration is 739
```


## 批量添加

```java
@Test
public void demo2() {
    long startTime = System.currentTimeMillis();
    HashMap<String, String> map = new HashMap<>();
    for (int i = 0; i < 1000; i++) {
        map.put("hello" + i, "world" + i);
    }
    redisTemplate.opsForValue().multiSet(map);
    long endTime = System.currentTimeMillis();
    System.out.println("duration is " + (endTime - startTime));
}
```

```
duration is 311
```

## 事务添加

```java
@Test
public void demo3() {
    long startTime = System.currentTimeMillis();
    redisTemplate.setEnableTransactionSupport(true);
    redisTemplate.multi();
    for (int i = 0; i < 1000; i++) {
        redisTemplate.opsForValue().set("hello" + i, "world" + i);
    }
    redisTemplate.exec();
    long endTime = System.currentTimeMillis();
    System.out.println("duration is " + (endTime - startTime));
}
```

```
duration is 686
```