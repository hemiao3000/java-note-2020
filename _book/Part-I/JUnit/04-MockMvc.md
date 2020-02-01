# MockMvc 对 Spring MVC 进行测试

Spring 3.2 之后出现了 `org.springframework.test.web.servlet.MockMvc` 类,对 Restful 风格的 Spring MVC 单元测试进行支持。

```java
package com.xja.test;

import com.jiaoyiping.baseproject.privilege.controller.MeunController;
import com.jiaoyiping.baseproject.training.bean.Person;
import junit.framework.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.servlet.ModelAndView;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@RunWith(SpringJUnit4ClassRunner.class)
@WebAppConfiguration
@ContextConfiguration(locations={
    "classpath:spring/spring-*.xml" 
}) 
public class TestMockMvc {

    // @Autowired
    // private org.springframework.web.context.WebApplicationContext context;

    MockMvc mockMvc;

    @Before
    public void before() {
        // mockMvc = MockMvcBuilders.webAppContextSetup(context).build();
        mockMvc = MockMvcBuilders.standaloneSetup(new HelloController()).build();
    }


    /**
     * 打印全部信息
     */
    @Test
    public void testDemo() throws Exception {
        mockMvc.perform(
            get("/dept.do")
            .param("deptno", "10")
        ).andDo(print());
    }

    /**
     * 测试普通请求
     */
    @Test
    public void testDemo1() throws Exception {
        mockMvc.perform(
            get("/dept.do")
            .param("deptno", "10")
        )
        .andExpect(status().isOk())
        .andExpect(view().name("hello"))
        .andExpect(model().attribute("name", "tom"))
        .andExpect(model().attribute("age", 20));
    }

    /**
     * 测试 Rest 请求
     */
    @Test
    public void testDemo2() throws Exception {
        mockMvc.perform(
                get("/dept/10")
        )
        .andExpect(status().isOk())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(jsonPath("$.id").value(10))
        .andExpect(jsonPath("$.name").value("Testing"))
        .andExpect(jsonPath("$.location").value("武汉"))
        .andDo(print());
    }

}
```