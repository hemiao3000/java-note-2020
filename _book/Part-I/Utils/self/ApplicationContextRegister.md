<span class="title">获取 ApplicationContext 的工具类</span>

有时你需要在非托管对象中获取 Spring 的 ApplicationContext

```java
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

@Component
@Lazy(false)
public class ApplicationContextRegister implements ApplicationContextAware {
    private static final Logger LOGGER = LoggerFactory.getLogger(ApplicationContextRegister.class);
    private static ApplicationContext APPLICATION_CONTEXT;

    /**
     * 设置 spring 上下文
     */
    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        LOGGER.debug("ApplicationContext registed-->{}", applicationContext);
        APPLICATION_CONTEXT = applicationContext;
    }

    public static ApplicationContext getApplicationContext() {
        return APPLICATION_CONTEXT;
    }
}
```

使用时调用：

```java
ApplicationContextRegister.getApplicationContext().getBean(Xxx.class);
```

---

一个不通用的通用方案

还有一种方案可以实现同样效果，直接调用 Spring 提供的工具类：

```java
ContextLoader.getCurrentWebApplicationContext().getBean(Xxx.class);
```

经测试发现，在 Spring Boot 项目中该方案无效。有人跟踪源码分析，因为 Spring Boot 的内嵌 Tomcat 和真实 Tomcat 还是有一定的区别，从而导致 Spring Boot 中该方案无法起到一起效果。
