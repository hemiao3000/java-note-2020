# pom.xml

为避免包版本冲突，以下版本信息来源自 `spring-boot-dependencies`，以 Spring `5.1.5.RELEASE` 为基准。

## properties

```xml
<properties>
    <!-- version just like spring-boot-starter 2.1.3.RELEASE -->
    <project.build.sourceEncoding>UTF-8</project.build.sourceEncoding>
    <maven.compiler.source>1.8</maven.compiler.source>
    <maven.compiler.target>1.8</maven.compiler.target>
    <lombok.version>1.18.6</lombok.version>
    <logback.version>1.2.3</logback.version>
    <spring.version>5.1.5.RELEASE</spring.version>
    <aspectj.version>1.9.2</aspectj.version>
    <servlet.version>3.1.0</servlet.version>
    <jsp.version>2.2</jsp.version>
    <jstl.version>1.2</jstl.version>
    <jackson.version>2.9.8</jackson.version>
    <hibernate-validator.version>6.0.14.Final</hibernate-validator.version>
    <mysql.version>8.0.15</mysql.version>
    <hikaricp.version>3.2.0</hikaricp.version>
    <mybatis.version>3.5.1</mybatis.version>
    <mybatis-spring.version>2.0.1</mybatis-spring.version>
    <pagehelper.version>5.1.8</pagehelper.version>
    <junit.version>4.12</junit.version>
</properties>
```

## dependencies 和 build

!FILENAME dependencies 和 build 部分
```xml
<dependencies>

    <!-- Base -->

    <dependency> <!-- lombok 工具库 -->
        <groupId>org.projectlombok</groupId>
        <artifactId>lombok</artifactId>
        <version>${lombok.version}</version>
    </dependency>

    <dependency> <!-- logback 日志包 -->
        <groupId>ch.qos.logback</groupId>
        <artifactId>logback-classic</artifactId>
        <version>${logback.version}</version>
    </dependency>

    <dependency> <!-- core/beans/expression/aop 被依赖，自动导入 -->
        <groupId>org.springframework</groupId>
        <artifactId>spring-context</artifactId>
        <version>${spring.version}</version>
    </dependency>

    <dependency> <!-- spring aop 的 aspectj 功能 -->
        <groupId>org.aspectj</groupId>
        <artifactId>aspectjrt</artifactId>          
        <version>${aspectj.version}</version>
    </dependency>

    <dependency> <!-- spring aop 的 aspectj 功能 -->
        <groupId>org.aspectj</groupId>
        <artifactId>aspectjweaver</artifactId>      
        <version>${aspectj.version}</version>
    </dependency>

    <dependency> <!-- jackson: 处理 json 的库 -->
        <groupId>com.fasterxml.jackson.core</groupId>
        <artifactId>jackson-databind</artifactId>   
        <version>${jackson.version}</version>
    </dependency>

    <!-- for Web -->
    
    <dependency> <!-- spring mvc：spring-web 被依赖，自动导入 -->
        <groupId>org.springframework</groupId>
        <artifactId>spring-webmvc</artifactId> 
        <version>${spring.version}</version>
    </dependency>

    <dependency> <!-- servlet 接口声明 -->
        <groupId>javax.servlet</groupId>
        <artifactId>javax.servlet-api</artifactId>  
        <version>${servlet.version}</version>
        <scope>provided</scope>
    </dependency>

    <dependency> <!-- jsp 接口声明 -->
        <groupId>javax.servlet.jsp</groupId>
        <artifactId>jsp-api</artifactId>            
        <version>${jsp.version}</version>
        <scope>provided</scope>
    </dependency>

    <dependency> <!-- jsp 上使用 jstl 标签库所 -->
        <groupId>javax.servlet</groupId>
        <artifactId>jstl</artifactId>               
        <version>${jstl.version}</version>
    </dependency>

    <dependency> <!-- jsr303 的 hibernate 实现-->
        <groupId>org.hibernate</groupId>
        <artifactId>hibernate-validator</artifactId>
        <version>${hibernate-validator.version}</version>
    </dependency>

    <!-- for Dao -->
    <dependency> <!-- Spring 整合 Dao 需要 -->
        <groupId>org.springframework</groupId>
        <artifactId>spring-jdbc</artifactId>
        <version>${spring.version}</version>
    </dependency>

    <dependency> <!-- druid 数据库连接池 -->
        <groupId>com.alibaba</groupId>
        <artifactId>druid</artifactId>
        <version>${druid.version}</version>
    </dependency>

    <dependency> <!-- mysql 数据库驱动 -->
        <groupId>mysql</groupId>
        <artifactId>mysql-connector-java</artifactId>   
        <version>${mysql.version}</version>
    </dependency>

    <dependency> <!-- spring 整合 mybatis -->
        <groupId>org.mybatis</groupId>
        <artifactId>mybatis-spring</artifactId>     
        <version>${mybatis-spring.version}</version>
    </dependency>

    <dependency> <!-- mybatis -->
        <groupId>org.mybatis</groupId>
        <artifactId>mybatis</artifactId>            
        <version>${mybatis.version}</version>
    </dependency>

    <dependency> <!-- mybatis 分页插件 -->
        <groupId>com.github.pagehelper</groupId>
        <artifactId>pagehelper</artifactId>
        <version>${pagehelper.version}</version>
    </dependency>

    <!-- for Test -->
    <dependency> <!-- sprint test -->
        <groupId>org.springframework</groupId>
        <artifactId>spring-test</artifactId> 
        <version>${spring.version}</version>
        <scope>test</scope>
    </dependency>

    <dependency> <!-- junit -->
        <groupId>junit</groupId>
        <artifactId>junit</artifactId>
        <version>${junit.version}</version>
        <scope>test</scope>
    </dependency>

</dependencies>

<build>
    <finalName>${project.artifactId}</finalName>
    <plugins>
        <plugin> <!-- tomcat 7 插件 -->
            <groupId>org.apache.tomcat.maven</groupId>
            <artifactId>tomcat7-maven-plugin</artifactId>
            <version>2.2</version>
            <configuration>
                <port>8080</port>
                <path>/${project.artifactId}</path>
                <uriEncoding>UTF-8</uriEncoding>
            </configuration>
        </plugin>
    </plugins>

</build>
```