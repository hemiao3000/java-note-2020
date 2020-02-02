<span class="title">使用 Spring 配合 JUnit 进行单元测试</span>

- pom.xml

```xml
<dependency>
  <groupId>org.springframework</groupId>
  <artifactId>spring-test</artifactId>
  <version>${spring.version}</version>
  <scope>test</scope>
</dependency>

<dependency>
  <groupId>junit</groupId>
  <artifactId>junit</artifactId>
  <version>4.12</version>
  <scope>test</scope>
</dependency>
```


在测试类上添加 `@RunWith` 注解指定使用 `SpringJunit` 的测试运行器，`@ContextConfiguration` 注解指定测试用的 Spring 配置文件的位置

><small>JUnit 不是唯一能让我们写出代码测试用例的框架，它的同行和竞争对少还有 TestNG。spring-test 和 Junit 的关系，类似于 slf4j 和 logback 的关系。spring-test 并不是『真正干活』的那个，它『调用』的 Junit 才是在背后默默干活的那个。</small>

之后我们就可以注入我们需要测试的 bean 进行测试，Junit 在运行测试之前会先解析 spring 的配置文件,初始化 spring 中配置的 bean 。

注意，在 Maven 项目中，test 代码加载的是 test 配套的资源文件。

```java
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {
    "classpath:spring/spring-dao.xml"
})
public class AppTest {

    private static final Logger log = LoggerFactory.getLogger(AppTest.class);

    @Autowired
    private DepartmentDAO departmentDao;

    @Test
    public void demo1() {
        Department dept = departmentDao.selectByPrimaryKey(40);
        log.info("{}", dept);
    }
}
```
