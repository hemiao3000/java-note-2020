<span class="title">pom.xml 文件模板</span>

# 版本信息来源说明

以下的版本信息截取自 spring-boot-starter 2.0.9 和 mybatis-spring-boot 2.0.1 。

- spring-boot-starter `2.0.9` 是 spring cloud Finchley.SR4<small>（
Apr, 2019）</small>使用的版本；
- mybatis-spring-boot `2.0.1` 最低支持的 spring boot 版本刚好最低也是支持 `2.0.9` 。

# 几个『高版』本的注意事项：

- lombok 的高版本依赖于 JDK 9，因此版本不要过高。（1.6 和 1.8 版本出现过这种情况）。

- hibernate-validator 的高版本<small>（邮箱注解）</small>依赖于高版本的 el-api，tomcat 8 的 el-api 是 3.0 满足需要，但是 tomcat 7 的 el-api 只有 2.2，不满足其要求。

- mysql-connector-java 高版本<small>（6 及以上）</small>有两处变动:

  1. 驱动类变为：`com.mysql.cj.jdbc.Driver`，多了个 `cj`

  2. url 中需要明确指定 serverTimezone，例如：`...&serverTimezone=UTC`

# properties 1.0

!FILENAME pom 的 properties 部分
```xml
<properties>
    <project.build.sourceEncoding>UTF-8</project.build.sourceEncoding>
    <maven.compiler.source>1.8</maven.compiler.source>
    <maven.compiler.target>1.8</maven.compiler.target>

    <!-- base -->
    <lombok.version>1.14.8</lombok.version>     <!-- 降低了版本 -->
    <slf4j.version>1.7.26</slf4j.version>
    <logback.version>1.2.3</logback.version>
    <commons-lang3.version>3.7</commons-lang3.version>
    <spring.version>5.0.13.RELEASE</spring.version>
    <aspectj.version>1.8.14</aspectj.version>

    <!-- web -->
    <servlet-api.version>3.1.0</servlet-api.version>
    <jsp-api.version>2.2</jsp-api.version>
    <jstl.version>1.2</jstl.version>
    <jackson.version>2.9.8</jackson.version>
    <hibernate-validator.version>5.4.3.Final</hibernate-validator.version> <!-- 降低了版本 -->

    <!-- service and dao -->
    <mysql.version>5.1.47</mysql.version>
    <hikaricp.version>2.7.9</hikaricp.version>
    <mybatis.version>3.5.1</mybatis.version>
    <mybatis-spring.version>2.0.1</mybatis-spring.version>
    <pagehelper.version>5.1.8</pagehelper.version> <!-- 单独指定 -->

    <hibernate.version>5.2.18.Final</hibernate.version>
    <hibernate-jpa-2.1-api.version>1.0.2.Final</hibernate-jpa-2.1-api.version>

    <!-- other -->
    <javax-jms.version>2.0.1</javax-jms.version>
    <activemq.version>5.15.9</activemq.version>
    <rabbit-amqp-client.version>5.4.3</rabbit-amqp-client.version>
    <spring-rabbit.version>2.0.12.RELEASE</spring-rabbit.version>
    <spring-security.version>5.0.12.RELEASE</spring-security.version>

    <!-- test -->
    <junit.version>4.12</junit.version>
    <h2.version>1.4.199</h2.version>
</properties>
```

# dependencies 1.0

- SSM 
- logback + lombok + jackson + hibernate-validator 
- hikaricp + mybatis-pagehelper

!FILENAME dependencies和build/plugins部分
```xml
<dependencies>
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
  <dependency>
    <groupId>org.apache.commons</groupId>
    <artifactId>commons-lang3</artifactId>
    <version>${commons-lang3.version}</version>
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
  <dependency> <!-- spring mvc：spring-web 被依赖，自动导入 -->
    <groupId>org.springframework</groupId>
    <artifactId>spring-webmvc</artifactId>
    <version>${spring.version}</version>
  </dependency>
  <dependency> <!-- servlet 接口声明 -->
    <groupId>javax.servlet</groupId>
    <artifactId>javax.servlet-api</artifactId>
    <version>${servlet-api.version}</version>
    <scope>provided</scope>
  </dependency>
  <dependency>
    <groupId>javax.servlet.jsp</groupId>
    <artifactId>jsp-api</artifactId>
    <version>${jsp-api.version}</version>
    <scope>provided</scope>
  </dependency>
  <dependency>
    <groupId>javax.servlet</groupId>
    <artifactId>jstl</artifactId>
    <version>${jstl.version}</version>
  </dependency>
  <dependency>
    <groupId>org.hibernate</groupId>
    <artifactId>hibernate-validator</artifactId>
    <version>${hibernate-validator.version}</version>
  </dependency>
  <dependency> <!-- Spring 整合 Dao 需要 -->
    <groupId>org.springframework</groupId>
    <artifactId>spring-jdbc</artifactId>
    <version>${spring.version}</version>
  </dependency>
  <dependency>
    <groupId>com.zaxxer</groupId>
    <artifactId>HikariCP</artifactId>
    <version>${hikaricp.version}</version>
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
  <resources>
    <resource><!-- 资源文件包括 java 目录下的 .xml 和 .properties -->
      <directory>src/main/java</directory>
      <includes>
        <include>**/*.xml</include>
        <include>**/*.properties</include>
      </includes>
    </resource>
    <resource>
      <directory>src/main/resources</directory>
    </resource>
  </resources>
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

# dependencies 2.0

spring-mvc + mysql-driver + hikaricp

```xml
<dependencies>
  <dependency>
    <groupId>org.springframework</groupId>
    <artifactId>spring-webmvc</artifactId>
    <version>${spring.version}</version>
  </dependency>
  <dependency>
    <groupId>javax.servlet</groupId>
    <artifactId>javax.servlet-api</artifactId>
    <version>${servlet-api.version}</version>
    <scope>provided</scope>
  </dependency>
  <dependency>
    <groupId>javax.servlet.jsp</groupId>
    <artifactId>jsp-api</artifactId>
    <version>${jsp-api.version}</version>
    <scope>provided</scope>
  </dependency>
  <dependency>
    <groupId>javax.servlet</groupId>
    <artifactId>jstl</artifactId>
    <version>${jstl.version}</version>
  </dependency>
  <dependency>
    <groupId>org.hibernate</groupId>
    <artifactId>hibernate-validator</artifactId>
    <version>${hibernate-validator.version}</version>
  </dependency>
  <dependency>
    <groupId>mysql</groupId>
    <artifactId>mysql-connector-java</artifactId>
    <version>${mysql.version}</version>
  </dependency>
  <dependency>
    <groupId>com.zaxxer</groupId>
    <artifactId>HikariCP</artifactId>
    <version>${hikaricp.version}</version>
  </dependency>
</dependencies>

<build>
  <finalName>${project.artifactId}</finalName>
  <plugins>
    <plugin>    <!-- tomcat 7 插件 -->
      <groupId>org.apache.tomcat.maven</groupId>
      <artifactId>tomcat7-maven-plugin</artifactId>
      <version>2.2</version>
      <configuration>
        <path>/${project.artifactId}</path>
        <port>8080</port>
        <uriEncoding>UTF-8</uriEncoding>
      </configuration>
    </plugin>
  </plugins>
</build>
```


# settings.xml

```xml
<?xml version="1.0" encoding="UTF-8"?>

<settings xmlns="http://maven.apache.org/SETTINGS/1.0.0"
	xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
	xsi:schemaLocation="http://maven.apache.org/SETTINGS/1.0.0 http://maven.apache.org/xsd/settings-1.0.0.xsd">
	<!-- localRepository | The path to the local repository maven will use to 
		store artifacts. | | Default: ${user.home}/.m2/repository <localRepository>/path/to/local/repo</localRepository> -->

	<mirrors>
		<mirror>
			<id>nexus-aliyun</id>
			<name>Nexus aliyun</name>
			<mirrorOf>*</mirrorOf>
			<url>http://maven.aliyun.com/nexus/content/groups/public</url>
		</mirror>
	</mirrors>

	<profiles>
	  <profile>
		<id>jdk-1.8</id>
		<activation>
			<activeByDefault>true</activeByDefault>
			<jdk>1.8</jdk>
		</activation>
		<properties>
			<maven.compiler.source>1.8</maven.compiler.source>
			<maven.compiler.target>1.8</maven.compiler.target>
			<maven.compiler.compilerVersion>1.8</maven.compiler.compilerVersion>
		</properties>
	  </profile>
	</profiles>

</settings>
```