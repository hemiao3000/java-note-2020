<span class="title">Spring Boot 对测试（Junit） 的支持</span>

# Junit5 和 Junit4 的不同

不同版本的 Spring Boot 依赖/使用了不同版本的 Junit。

- Spring Boot *`2.1.x.RELEASE`* 使用的是 Junit 4；
- Spring Boot *`2.2.x.RELEASE`* 使用的是 Junit 5。
 
Junit 4 和 Junit 5 的不同导致了 Spring Boot 的 *`2.1.x.RELEASE`* 和 *`2.2.1.RELEASE`* 版本中的相关配置又有所不同。

JUnit4 与 JUnit 5 常用部分注解对比：

| JUnit4 | JUnit5 | 
| :- | :- |
| @Test	        | @Test	        |
| @BeforeClass	| @BeforeAll    |
| @AfterClass	| @AfterAll	    |
| @Before	    | @BeforeEach   |
| @After	    | @AfterEach    |
| @Ignore	    | @Disabled     |
| @RunWith	    | @ExtendWith   |


# Junit 5 的测试类头部

```java
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

@ExtendWith(SpringExtension.class)
@SpringBootTest
class DemoApplicationTests {
    ...
}
```

# Junit 4 的测试类头部

```java
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest
class DemoApplicationTests {
   ...
}
```
