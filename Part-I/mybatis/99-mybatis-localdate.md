<span class="title">MyBatis 中使用 LocalDateTime</span>

# 背景

项目中使用 MySQL 数据库，然后用 mybatis 做数据持久化。最近使用时，想把一些 model 类的 gmtCreate、gmtModified 等字段从 *`java.util.Date`* 改成 Java8 的 *`java.time.LocalDateTime`*，此类是不可变类，且自带很好用的日期函数 api 。

原本依赖如下：

``` 
compile "org.mybatis:mybatis:3.3.0"
compile "org.mybatis:mybatis-spring:1.2.5"
compile "org.mybatis.generator:mybatis-generator-core:1.3.2"
```

直接修改代码有两个问题：

1. mapper、model、xml 文件都是用 generator 插件生成，维护成本高。

2. 会报错如下：

   ```java
   Caused by: java.lang.IllegalStateException: No typehandler found for property createTime
     at org.apache.ibatis.mapping.ResultMapping$Builder.validate(ResultMapping.java:151)
     at org.apache.ibatis.mapping.ResultMapping$Builder.build(ResultMapping.java:140)
     at org.apache.ibatis.builder.MapperBuilderAssistant.buildResultMapping(MapperBuilderAssistant.java:382)
     at org.apache.ibatis.builder.xml.XMLMapperBuilder.buildResultMappingFromContext(XMLMapperBuilder.java:378)
     at org.apache.ibatis.builder.xml.XMLMapperBuilder.resultMapElement(XMLMapperBuilder.java:280)
     at org.apache.ibatis.builder.xml.XMLMapperBuilder.resultMapElement(XMLMapperBuilder.java:252)
     at org.apache.ibatis.builder.xml.XMLMapperBuilder.resultMapElements(XMLMapperBuilder.java:244)
     at org.apache.ibatis.builder.xml.XMLMapperBuilder.configurationElement(XMLMapperBuilder.java:116)
   ```

## 修改

首先，添加了以下依赖：（jsr310 是 java 规范 310）

```
compile "org.mybatis:mybatis-typehandlers-jsr310:1.0.2"
```

接着，在 *`mybatis-config.xml`* 中添加如下配置：（该配置来源于[官网](https://mybatis.org/mybatis-3/zh/configuration.html#typeHandlers)）

```xml
<typeHandlers>
  <!-- ... -->
  <typeHandler handler="org.apache.ibatis.type.InstantTypeHandler" />
  <typeHandler handler="org.apache.ibatis.type.LocalDateTimeTypeHandler" />
  <typeHandler handler="org.apache.ibatis.type.LocalDateTypeHandler" />
  <typeHandler handler="org.apache.ibatis.type.LocalTimeTypeHandler" />
  <typeHandler handler="org.apache.ibatis.type.OffsetDateTimeTypeHandler" />
  <typeHandler handler="org.apache.ibatis.type.OffsetTimeTypeHandler" />
  <typeHandler handler="org.apache.ibatis.type.ZonedDateTimeTypeHandler" />
  <typeHandler handler="org.apache.ibatis.type.YearTypeHandler" />
  <typeHandler handler="org.apache.ibatis.type.MonthTypeHandler" />
  <typeHandler handler="org.apache.ibatis.type.YearMonthTypeHandler" />
  <typeHandler handler="org.apache.ibatis.type.JapaneseDateTypeHandler" />
</typeHandlers>
```

看了文档发现需要 *`mybatis 3.4.0`* 版本

不过这里有个问题了，会出现版本不兼容问题，报错如下：

```
java.lang.AbstractMethodError: org.mybatis.spring.transaction.SpringManagedTransaction.getTimeout()Ljava/lang/Integer;
    at org.apache.ibatis.executor.SimpleExecutor.prepareStatement(SimpleExecutor.java:85)
    at org.apache.ibatis.executor.SimpleExecutor.doQuery(SimpleExecutor.java:62)
    at org.apache.ibatis.executor.BaseExecutor.queryFromDatabase(BaseExecutor.java:325)
    at org.apache.ibatis.executor.BaseExecutor.query(BaseExecutor.java:156)
    at org.apache.ibatis.executor.CachingExecutor.query(CachingExecutor.java:109)
    at org.apache.ibatis.executor.CachingExecutor.query(CachingExecutor.java:83)
    at sun.reflect.NativeMethodAccessorImpl.invoke0(Native Method)
    at sun.reflect.NativeMethodAccessorImpl.invoke(NativeMethodAccessorImpl.java:62)
    at sun.reflect.DelegatingMethodAccessorImpl.invoke(DelegatingMethodAccessorImpl.java:43)
    at java.lang.reflect.Method.invoke(Method.java:498)
```

查看发现 *`SpringManagedTransaction`* 未实现 *`Transaction`* 中的 *`getTimeout()`* 方法。

```java
public interface Transaction {
    Connection getConnection() throws SQLException;
    void commit() throws SQLException;
    void rollback() throws SQLException;
    void close() throws SQLException;
} 
```

查了文档发现：Spring 版本跟 Mybatis 版本有个对照表要满足，详情见[官网](http://mybatis.org/spring/zh/)

而我需要的 `mapper.xml` 文件类似如下：

```xml
<mapper namespace="com.my.MyMapper">
  <resultMap id="BaseResultMap" type="com.my.MyModel">
    <id column="id" jdbcType="BIGINT" property="id"/>
    <result column="gmt_create" jdbcType="OTHER" property="gmtCreate" typeHandler="org.apache.ibatis.type.LocalDateTimeTypeHandler"/>
 
  </resultMap>
  <!-- 省略 -->
</mapper>
```

之后，查了 generator 的官方文档，发现还是可以通过 *`generator.xml`* 的配置文件做到的。

```xml
<table tableName="my_table" domainObjectName="MyModel">
  <generatedKey column="id" sqlStatement="JDBC" identity="true"/>
  <columnOverride column="gmt_create" property="gmtCreate" typeHandler="org.apache.ibatis.type.LocalDateTimeTypeHandler" jdbcType="OTHER" javaType="java.time.LocalDateTime" />
  <columnOverride column="gmt_modified" property="gmtModified" typeHandler="org.apache.ibatis.type.LocalDateTimeTypeHandler" jdbcType="OTHER" 
<!-- 省略其他 -->
</table>
```


