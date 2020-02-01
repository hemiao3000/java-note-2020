<span class="title">Spring 反射和 Spring ResolvableType</span>

# 反射工具类：ReflectionUtils 

***`ReflectionUtils`*** 类位于 *`org.springframework.util`* 包。它封装了常用的反射相关的操作。

<small>`注意`，实验反射效果时，不要使用内部类，或同文件多类的形式，必须使用标准形式。即类前必须有 `public` 修饰。</small>

<small>以下方法均为 `static` 方法，因此省略显示 `static` 关键字</small>

## Field 相关操作

### 获取 Field 对象

在类定义中查找属性，返回代表这个属性的 ***`Field`*** 对象。

```java
/* 在类中查找指定属性 */
Field findField(Class<?> clazz, String name) 

/* 功能同上。多提供了属性的类型 */
Field findField(Class<?> clazz, String name, Class<?> type) 
```

### 读写属性值

通过所上述方法<small>（或其它方法）</small>获得代表属性的 ***`File`*** 对象后，可以去读写某个对象的这个属性。<small>（当然，前提是该对象有这么个属性）。</small>

```java
/* 获取 target 对象的 field 属性值 */
Object getField(Field field, Object target) 

/* 设置 target 对象的 field 属性值，值为 value */
void setField(Field field, Object target, Object value) 

/* 同类对象属性对等赋值。*/
void shallowCopyFieldState(Object src, Object dest)

/* 取消 Java 的权限控制检查。以便后续读写该私有属性 */
void makeAccessible(Field field) 
```

### 遍历 Field

```java
/* 对类的每个属性执行 callback */
void doWithFields(Class<?> clazz, ReflectionUtils.FieldCallback fc) 

/* 同上，多了个属性过滤功能。 */
void doWithFields(Class<?> clazz, ReflectionUtils.FieldCallback fc, ReflectionUtils.FieldFilter ff) 

/* 同 doWithFields，但不包括继承而来的属性。*/
void doWithLocalFields(Class<?> clazz, ReflectionUtils.FieldCallback fc) 
```


### 其他

```java
/* 是否为一个 "public static final" 属性。*/
boolean isPublicStaticFinal(Field field) 
```

## Method 相关方法

### 获得 Method

```java
/* 在类中查找指定方法。*/
Method findMethod(Class<?> clazz, String name) 

// 同上，额外提供方法参数类型作查找条件。
Method findMethod(Class<?> clazz, String name, Class<?>... paramTypes) 

// 获得类中所有方法，包括继承而来的。
Method[] getAllDeclaredMethods(Class<?> leafClass) 

// 在类中查找指定构造方法
<T> Constructor<T> accessibleConstructor(Class<T> clazz, Class<?>... parameterTypes) 
```

### 判断 Method

```java
/* 是否是 equals() 方法 */
boolean isEqualsMethod(Method method) 

/* 是否是 hashCode() 方法 */
boolean isHashCodeMethod(Method method) 

/* 是否是 toString() 方法 */
boolean isToStringMethod(Method method) 

/* 是否是从 Object 类继承而来的方法 */
boolean isObjectMethod(Method method) 

/* 检查一个方法是否声明抛出指定异常。*/
boolean declaresException(Method method, Class<?> exceptionType) 
```

### 执行 Field

```java
/* 执行方法 */
Object invokeMethod(Method method, Object target)  

/* 同上 */
Object invokeMethod(Method method, Object target, Object... args) 

/* 取消 Java 权限检查。以便后续执行该私有方法 */
void makeAccessible(Method method) 

/* 取消 Java 权限检查。以便后续执行私有构造方法 */
void makeAccessible(Constructor<?> ctor) 
```

### 遍历执行 Method

```java
/* 遍历执行类中的每个方法。包括从父类继承而来的方法。 */
void doWithMethods(Class<?> clazz, ReflectionUtils.MethodCallback mc) 

/* 同上。增加了匹配/过滤功能。*/
void doWithMethods(Class<?> clazz, ReflectionUtils.MethodCallback mc, ReflectionUtils.MethodFilter mf) 

/* 同上。不包括从父类继承而来的方法。*/
void doWithLocalMethods(Class<?> clazz, ReflectionUtils.MethodCallback mc) 
```

# ResolvableType

***`ResolvableType`*** 是 Spring `4.0` 的【亮点】之一。

***`ResolvableType`*** 为所有的 java 类型<small>（的类对象）</small>提供了统一的数据结构以及 API 。

简而言之，<strong>**任何**</strong> 一个 Java 中的类对象<small>（ 例如：*`String.class`* 或者  *`"hello".getClass()`* ）</small>，都【获得】/【求得】一个与之对应的 ***`ResolvableType`*** 对象。

```java
ResolvableType type1 = ResolvableType.forClass(String.class);
ResolvableType type2 = ResolvableType.forClass("hello".getClass());
ResolvableType type3 = ResolvableType.forClass(Integer.class);
ResolvableType type4 = ResolvableType.forClass(ArrayList.class);
```

于此同时，ResolvableType 还提供了一个 *`.resolve()`* 方法，用于反向从 type 对象求出 Class 对象。

```java
Class clazz1 = type1.resolve();
System.out.println(clazz1);          // class java.lang.String

System.out.println(type2.resolve()); // class java.lang.String
System.out.println(type3.resolve()); // class java.lang.Integer
System.out.println(type4.resolve()); // class java.util.ArrayList
```


借助于这个 ***`ResolvabType`*** 对象，可以很方便地求出它<small>（<small>`String.class`</small>）</small>的直接父类类型<small>（如果有的话）</small>、接口类型<small>（如果有的话）</small>、泛型参数类型<small>（如果有的话）</small> 等类型有关信息。

```java
System.out.println(type1.getSuperType());
System.out.println(Arrays.toString(type1.getInterfaces()));
```

需要注意<small>（有意思）</small>的是这些方法的返回值的类型，仍然是 ***`ResolvableType`*** 类型<small>（或 ***`ResolvableType`*** 类型的数组）</small>。

- 查询成员变量类型

  ```java
  ResolvableType forField(Field field);
  ```

- 查询局部变量类型

  ```java
  ResolvableType.forClass(局部变量.getClass());
  ```


- 查询方法的形参类型

  ```java
  ResolvableType forMethodParameter(
    MethodParameter methodParameter, 
    @Nullable Type targetType);
  ```

- 查询方法的返回值类型

  ```java
  ResolvableType forMethodReturnType(Method method);
  ```

# 泛型擦除

由于泛型擦除，使得泛型（Generic）无法获取自己的 Generic 的 Type 类型。实际上 *`new BadClass()`* 实例化以后 *`BadClass`* 对象的 *`Class`* 里面就不包括 ***`T`*** 的信息了。

对于它的 ***`Class`*** 而言，***`T`*** 已经被擦拭为 ***`Object`***，而真正的 ***`T`*** 参数被转到使用 ***`T`*** 的方法<small>（或者变量声明或者其它使用 *`T`* 的地方）</small>里面<small>（如果没有那就没有存根）</small>。

所以无法反射到 ***`T`*** 的具体类别，也就无法得到 `T.class`。 

而 getGenericSuperclass() 是 Generic 继承的特例，对于这种情况子类会保存父类的 Generic 参数类型，返回一个 ParameterizedType，这时可以获取到父类的 T.class 了，这也正是子类确定应该继承什么 T 的方法

