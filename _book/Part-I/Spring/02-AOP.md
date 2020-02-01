<span class="title">Spring AOP</span>

# AOP 基本概念

显示中有一些内容并不是面向对象技术（OOP）可以解决的，比如事务处理。在 JDBC 代码中，最繁琐的问题就是无穷无尽的 try ... catch ... finally ... 语句 和 数据库资源关闭 的问题，而且代码会存在大量的重复，而你又不能不写。

一个正常执行的 SQL 的逻辑步骤如下：

1. 打开通过数据库连接池获得数据库链接资源，并做一定的设置工作。
2. 执行对应的 SQL 语句（通常是增删改），对数据进行操作。
3. 如果 SQL 执行过程中发生异常，回滚事务。
4. 如果 SQL 执行过程中没有发生异常，最后提交事物。
5. 到最后的阶段，需要关闭一些连接资源。

参看上述流程，你会发现无论是执行什么具体的 SQL，流程都是一样的！即，*到了特定时刻一定会执行某个特定操作*，并不因 SQL 的不同而不同 !

在 OOP 中，模块化单元是『类』（Class），而在 AOP 中，模块化的单元是『 切面』（Aspect）。

AOP 最早由 AOP 联盟的组织提出的，并制定了一套规范。Spring AOP 遵守 AOP 联盟的规范。

Spring 的 AOP 的底层用到两种代理机制：

1. JDK 动态代理

   如果目标类遵循某个接口，Spring AOP 底层采用 JDK 方案生成代理对象

2. Cglib 动态代理

   如果目标类不遵循任何接口，Spring AOP 底层采用 cglib 方案生成代理对象。


# 核心概念

AOP 涉及到如下问题：在 <font color="#0088dd">**什么类**</font> 的 <font color="#0088dd">**什么方法**</font> 的 <font color="#0088dd">**什么地方**</font>，做出 <font color="#0088dd">**什么样**</font> 的增强。AOP 的功能简而言之就是：*在不修改方法源文件的情况下，为源文件的特定部位增加新的代码* 。


## 切入点表达式

切入点表达式决定了哪些类的哪些方法会被『插入』新代码。它『回答』了对 <font color="#0088dd">**什么类**</font> 的 <font color="#0088dd">**什么方法**</font> 做出增强。

最常用的切入点表达式是 **`execution`** 表达式，其语法格式如下：

```java
[方法访问修饰符] 方法返回值 包名.类名.方法名(方法的参数)
```

 
<small>『方法访问修饰符』部分是**可选**部分；『其它』部分是**必要**部分。</small>


例如：

```java
execution( * com.demo.dao.EmployeeDao.*(..) )

execution( public * demo.dao.EmployeeDao.*(..) )

execution( public String demo.dao.EmployeeDao.*(..) )

execution( public String demo.dao.EmployeeDao.*(String, ..) )

execution( * demo.dao.*.*(..) )
```

- **返回值匹配**: 

  可以为 `*`，表示任何返回值，全路径的类名等。

- **方法名匹配**: 

  指定方法名。

  例如：`*` 代表所有方法； `set*`，代表以 `set` 开头的所有方法.

- **参数匹配**: 

  指定方法参数(数量、类型及顺序)。

  例如：`(..)` 代表所有参数；`(*)` 代表一个参数； `(*, String)` 代表第一个参数为任何值，第二个为 String 类型。


## 通知类型

通知类型回答了 <font color="#0088dd">**什么位置**</font> 增强 <font color="#0088dd">**什么样**</font> 的代码。

| 注解 | 通知 | 说明 |
| :- | :- | :- |
| @Before | 在被代理对象的方法前调用 | |
| @Around | 将被代理方法封装起来 | 环绕通知，它将覆盖原有方法，但是允许通过反射调用原有方法 |
| @After | 在被代理对象方法后调用 | |
| @AfterReturning | 在被代理对象正常返回后调用|要求被代理对象的方法执行过程中没有发生异常 |
| @AfterThrowing | 在被代理对象的方法抛出异常后调用|要求被代理对象的方法执行过程中发生异常 |

Spring 只支持方法拦截 。

# 使用 @AspectJ 注解配置 Spring AOP

Spring 实现 AOP 功能的方式有两种：**`@AspectJ 注解`** 方式，要求会写；**`XML 配置`** 方式要求会看。

`补充`，<small>AOP 概念并非 Spring 所特有，Spring 也并非支持 AOP 编程的唯一框架。在 Spring 之前提供 AOP 功能的，具有里程碑式的框架叫 <font color="#088dd">**AspectJ 框架**</font>。AspectJ 框架的使用方式比较独特（不简便），在 Spring AOP 出现后就慢慢被 Spring AOP 所取代。但是，AspectJ 框架设计了一套注解，非常简便和合理，并且被广大 AspectJ 的使用者所熟知，所以 Spring AOP 直接借用这套注解，也就是我们这里所说的 @AspectJ 注解。</small>

由于 @AspectJ 注解是 Spring『借用』的别人的注解，所以使用时需要引入它。

```xml
<dependency>
    <groupId>org.aspectj</groupId>
    <artifactId>aspectjrt</artifactId>
    <version>${aspectj.version}</version>
</dependency>

<dependency>
    <groupId>org.aspectj</groupId>
    <artifactId>aspectjweaver</artifactId>
    <version>${aspectj.version}</version>
</dependency>
```

在 Spring 的配置文件中，也需要引入/声明 AOP 的 namspace :

```xml
<beans
    ...
    xmlns:aop="http://www.springframework.org/schema/aop"
    xsi:schemaLocation="
        ...
        http://www.springframework.org/schema/aop
        http://www.springframework.org/schema/aop/spring-aop.xsd">
```

并且，由于 `@AspectJ` 注解并非 Spring 框架的一部分，所以需要在配置文件中声明 『启用 @AspectJ 注解』 功能，否则，Spring 并『不认识』 `@AspectJ` 的一系列注解。

```xml
<aop:aspectj-autoproxy />

<!-- 或者使用 @Component 注解 + 包扫描 -->
<bean id="dept" class="bean.Dept" />
<bean id="aspect1" class="bean.DeptAspect1" />
```


再重复一遍，使用 Spring AOP 的核心问题：在 <font color="#0088dd">**什么类**</font> 的 <font color="#0088dd">**什么方法**</font> 的 <font color="#0088dd">**什么地方**</font>，做出 <font color="#0088dd">**什么样**</font> 的增强。


```java
@Aspect // 注意，不要忘记在切面类的头上加 @Aspect 注解。
public class DeptAspect1 {

    @Before("execution(* bean.Dept.sayHi(..))")
    public void before() {
        System.out.println("before ...");
    }

    @After("execution(* bean.Dept.sayHi(..))")
    public void after() {
        System.out.println("after ...");
    }

    @Around("execution(* bean.Dept.sayHi(..))")
    public void around(ProceedingJoinPoint jp) {
        System.out.println("hello");
        try {
            jp.proceed();
        } catch (Throwable e) {
            e.printStackTrace();
        }
        System.out.println("world");
    }

    @AfterReturning("execution(* bean.Dept.sayHi(..))")
    public void afterReturuing() {
        System.out.println("afterReturning ...");
    }

    @AfterThrowing("execution(* bean.Dept.sayHi(..))")
    public void afterThrowing() {
        System.out.println("afterThrowing ...");
    }
}
```

可以发现，上述代码中 `execution(...)` 部分有大量重复现象。为此，可以提供一个 `@Pointcut` 来进行“缩写”。


```java
@Aspect
public class DeptAspect1 {

    @Pointcut("execution(* bean.Dept.sayHi(..))")
    public void xxx() {  // 这个方法是空的。需要的不是它的内容，需要的是它的名字。
    }

    @Before("xxx()")
    public void before() { ... }

    @After("xxx()")
    public void after() { ... }

    @Around("xxx()")
    public void around() { ... }

    @AfterReturning("xxx())")
    public void afterReturning() { ... }

    @AfterThrowing("xxx())")
    public void afterThrowing() { ... }
}

```

另外，有时你要拦截/增强的方法是有参数的，例如：

```java
public void sayHi(String name, int age) { ... }
```

为此，你也可以在增强方法中获得这些参数，

```java
@Pointcut("execution(* bean.Dept.sayHi(..))")
public void xxx() {}

@Before("execution(* bean.Dept.sayHi(..)) && args(name, age)")
public void before(String name, int age) {
    ...
}

@After("xxx() && args(name, age)")
public void after(String name, int age) {
    ...
}
```

# 使用 XML 配置 Spring AOP

通过 XML 配置 Spring AOP 功能，在 XML 文件中出现的各种『要素』本质上和 @AspectJ 注解中出现过的内容本质上并没有两样。

```java
public class DeptAspect2 {
    public void before() { ... }
    public void after() { ... }
    public void around(ProceedingJoinPoint jp) { ... }
    public void afterReturuing() { ... }
    public void afterThrowing() { ... }
}
```

```xml
<bean id="dept" class="bean.Dept" />
<bean id="aspect2" class="bean.DeptAspect2" />

<aop:config>
    <aop:aspect ref="aspect2">
        <aop:before method="before" pointcut="execution(* bean.Dept.sayHi(..)) and args(name, int)"/>
        <aop:after method="after" pointcut="execution(* bean.Dept.sayHi(..)) and args(name, int)"/>
    </aop:aspect>
</aop:config>
```




