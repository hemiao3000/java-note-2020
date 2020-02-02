# Spring 内置的事件监听

Spring 的事件监听有三个部分组成：

| # | 说明 | 类/方法|
|:-:| :- | :- |
| 1 | 事件 | ***`ApplicationEvent`*** |
| 2 | 监听器 | ***`ApplicationListener`*** |
| 3 | 事件发布 | *`publishEvent()`* |

## 事件

事件类需要继承 ***`ApplicationEvent`*** 。

- 例如：

  ```java
  public class UserAddedEvent extends ApplicationEvent {

    public UserAddedEvent(Object source) {
        super(source);
    }
  }
  ```

- 除了必要的 *`source`* 之外，你可以添加更多的属性

  ```java
  private String xxx;
  private Integer yyy;
  private Date zzz;

  public UserAddedEvent(Object source, String xxx, Integer yyy, Date zzz) {
      super(source);
      this.xxx = xxx;
      this.yyy = yyy;
      this.zzz = zzz;
  }
  ```

这些属性（加上默认的 *`Object source`*）就是未来事件发送方需要『传递』给事件接收方的数据。

> 考虑到可以有多个事件的监听者，为了避免这些数据的无意中的篡改，这些属性最好是 final 的，或者只提供 getter，不提供 setter 。

## 事件监听器

有两种方式创建事件监听器：

- 实现 `ApplicationListener` 接口：

  ```java
  @Component
  public class UserAddedEventListener1 implements ApplicationListener<UserAddedEvent> {

      @Override
      public void onApplicationEvent(UserAddedEvent userAddedEvent) {
          System.out.println("收到了 UserAdded 事件1");
          System.out.println(userAddedEvent.getSource());
      }
  }
  ```

- 使用 `@EventListener` 注解

  ```java
  @Component
  public class UserAddedEventListener2 {

      @EventListener(value = UserAddedEvent.class)
      public void onApplicationEvent(UserAddedEvent userAddedEvent) {
          System.out.println("收到了 UserAdded 事件");
          System.out.println(userAddedEvent.getSource());
      }
  }
  ```

可以有多个监听器监听同一个事件。


## 事件发布操作

- 有了事件和监听器，不发布事件也不用，事件发布方式很简单

  ```java
  applicationContext.publishEvent(new UserAddedEvent("hello world"));
  ```

发布事件之后，监听器中的方法会被触发执行。这里本质上还是 <strong>同步调用</strong>，发布方、监听器1、监听器2 仍然在同一个线程中执行。