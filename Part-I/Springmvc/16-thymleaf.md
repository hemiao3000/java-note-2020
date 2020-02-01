# Thymeleaf

JSP 不是唯一的模板引擎，除它之外还有 Velocity、FreeMarker。而 Thymeleaf 则是这个领域的后起之秀。

Thymeleaf 旨在提供一个优雅的、高度可维护的创建模板的方式。为了实现这一目标，Thymeleaf 建立在<font color="#0088dd">**自然模板**</font>的概念上，将其逻辑注入到模板文件中，不会影响模板设计原型，从而改善了设计的沟通，弥合了设计和开发团队之间的差距。

Thymeleaf 从设计之初就遵循 Web 标准——特别是 HTML 5 标准，如果需要，Thymeleaf 允许创建完全符合 HTML 5 验证标准的模板。 

Spring Boot 体系内推荐使用 Thymeleaf 作为前端页面模板，并且 Spring Boot 2.0 中默认使用 Thymeleaf 3.0，性能提升幅度很大。

- Thymeleaf 特点
  1. Thymeleaf 支持 HTML 原型，可以在浏览器查看页面的静态效果。
  2. Thymeleaf 开箱即用的特性。它支持标准方言和 Spring 方言，可以直接套用模板实现 JSTL、 OGNL 表达式效果，便于使用。
  3. Thymeleaf 方便与 Spring MVC 集成。


- Thymeleaf 和常用的模板引擎：Velocity、Freemaker 的对比：

  ```html
  Velocity: <p>$message</p>
  FreeMarker: <p>${message}</p>
  Thymeleaf: <p th:text="${message}">Hello World!</p>
  ```

## 快速上手

- pom.xml

  ```xml
  <dependency>
    <groupId>org.thymeleaf</groupId>
    <artifactId>thymeleaf</artifactId>
    <version>3.0.9.RELEASE</version>
  </dependency>
  <dependency>
    <groupId>org.thymeleaf</groupId>
    <artifactId>thymeleaf-spring5</artifactId>
    <version>3.0.9.RELEASE</version>
  </dependency>
  ```

- SpringWebConfig.java
  ```java
  /**
   * 模板解析器
   */
  @Bean
  public SpringResourceTemplateResolver templateResolver() {
      SpringResourceTemplateResolver templateResolver = new SpringResourceTemplateResolver();
      templateResolver.setPrefix("/WEB-INF/templates/");
      templateResolver.setSuffix(".html");
      templateResolver.setCacheable(false);
      templateResolver.setCharacterEncoding("UTF-8");
      templateResolver.setTemplateMode("HTML5");
      templateResolver.setOrder(1);
      return templateResolver;
  }

  /**
   * 模板引擎
   */
  @Bean
  public SpringTemplateEngine springTemplateEngine(SpringResourceTemplateResolver templateResolver) {
      SpringTemplateEngine templateEngine = new SpringTemplateEngine();
      templateEngine.setTemplateResolver(templateResolver);
      return templateEngine;
  }

  /**
   * 视图解析器
   * 就不再需要 InternalViewResolver 了。
   */
  @Bean
  public ThymeleafViewResolver viewResolver(SpringTemplateEngine springTemplateEngine) {
      ThymeleafViewResolver viewResolver = new ThymeleafViewResolver();
      viewResolver.setTemplateEngine(springTemplateEngine);
      viewResolver.setCharacterEncoding("UTF-8");
      return viewResolver;
  }
  ```


#### 一个简单的页面

```html
<!DOCTYPE html><html lang="en" xmlns:th="http://www.thymeleaf.org">
<head>
    <meta charset="UTF-8"></meta>
    <title>Hello</title>
</head>
<body>
    <h1 th:text="${message}">Hello World</h1>
    </body>
</html>
```

- 所有使用 Thymeleaf 的页面必须在 HTML 标签声明 Thymeleaf：

  ```html
  <html xmlns:th="http://www.thymeleaf.org">
  ```

  表明页面使用的是 Thymeleaf 语法。

#### Controller

```java
@Controller
public class HelloController {
    @RequestMapping("/")
    public String index(ModelMap map) {
        map.addAttribute("message", "http://www.ityouknow.com");
        return "hello";
    }
}
```

启动项目后在浏览器器中输入网址：http://localhost:8080/，会出现下面的结果：

```
http://www.ityouknow.com
```

说明页面的值，已经成功的被后端传入的内容所替换。

### 常用语法

我们新建 ExampleController 来封装不同的方法进行演示。

#### 赋值、字符串串拼接

- 赋值和拼接：

  ```html
  <p th:text="${userName}">neo</p>
  <span th:text="'Welcome to our application, ' + ${userName} + '!'"></span>
  ```

- 字符串拼接还有另外一种简洁的写法：
  ```html
  <span th:text="|Welcome to our application, ${userName}!|"></span>
  ```

#### 条件判断 If/Unless

Thymeleaf 中使用 `th:if` 和 `th:unless` 属性进行条件判断。

- `<a>` 标签只有在 `th:if` 中条件成立时才显示：

  ```html
  <a th:if="${flag == 'yes'}" th:href="@{http://www.baidu.com}">百度</a>
  <a th:unless="${flag != 'no'}" th:href="@{http://www.163.com/}">网易</a>
  ```

`th:unless` 与 `th:if` 恰好相反，只有表达式中的条件立，才会显示其内容。

#### for 循环

for 循环在我们项目中使用的频率太高，一般结合前端的表格来使用。

- 在页面进行数据展示：

  ```html
  <h1>for 循环</h1>
  <table>
    <tr th:each="user,iterStat : ${users}">
      <td th:text="${user.name}">neo</td>
      <td th:text="${user.age}">6</td>
      <td th:text="${user.pass}">213</td>
      <td th:text="${iterStat.index}">index</td>
    </tr>
  </table>
  ```

- `iterStat` 称作状态变量，属性有：

    | 属性 | 说明 |
    | :- | :- |
    | index | 当前迭代对象的 index（从 0 开始计算）|
    | count | 当前迭代对象的 index（从 1 开始计算）|
    | size | 被迭代对象（数组 / 链表）的大小 |
    | current | 当前迭代变量 |
    | even/odd | 布尔值，当前循环是否是偶数/奇数（从 0 开始计算） |
    | first | 布尔值，当前循环是否是第一个一个 |
    | last | 布尔值，当前循环是否是最后一个 |


#### URL

URL 在 Web 应用模板中占据着十分重要的地位，需要特别注意的是 Thymeleaf 对于 URL 的处理是通过语法 `@{...}` 来处理的。如果需要 Thymeleaf 对 URL 进行渲染，那么务必使用 `th:href`、`th:src` 等属性，

- 例子：

  ```html
  <a th:href="@{http://www.ityouknow.com/{xxx}(xxx=${type})}">link1</a>
  <a th:href="@{http://www.ityouknow.com/{yyy}/can-use-springcloud.html(yyy=${pageId})}">view</a>

  <div th:style="'background:url(' + @{${img}} + ');'">
  ```

几点说明：

- 上例中 URL 最后的 `(yyy=${pageId})` 表示将括号内的内容作为 URL 参数处理，该语法避免使用字符串拼接，大大提高了可读性；
- `@{...}` 表达式中可以通过 `{pageId}` 访问 Context 中的 pageId 变量；
- `@{/order}` 是 Context 相关的相对路径，在渲染时会自动添加上当前 Web 应用的 Context 名字，假设 context 名字为 app，那么结果应该是 `/app/order` 。


#### 三目运算

三目运算是我们常用的功能之一，普遍应用在各个项目中，下面来做一下演示。

- 三目运算及表单显示：

  ```html
  <input th:value="${name}"/>
  <input th:value="${age gt 30 ? '中年':'青年'}"/>
  ```

说明：在表单标签中显示内容使用： `th:value;${age gt 30 ? '中年':'青年'}` 表示如果 age 大于 30 则显示中年，否则显示青年。

| 关键字 | 说明 |
| :- | :- |
| gt|great than（大于）|
| ge|great equal（大于等于）|
| eq|equal（等于）|
| lt|less than（小于）|
| le|less equal（小于等于）|
| ne|not equal（不等于）|

- 结合三目运算也可以用于 `th:if`

  ```html
  <a th:if="${flag eq 'yes'}" th:href="@{http://favorites.ren/}"> favorites </a>
  ```


#### switch 选择

switch/case 多用于多条件判断的场景下，以性别举例：

```html
<div th:switch="${sex}">
    <p th:case="'woman'">她是一个姑娘...</p>
    <p th:case="'man'">这是一个爷们!</p>
    <!-- *: case 的默认的选项 -->
    <p th:case="*">未知性别的一个家伙。</p>
</div>
```
