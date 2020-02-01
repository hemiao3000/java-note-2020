<span class="title">JDBC 基础</span>

# 基本概念

JDBC（Java Database Connectivity）是 Java 应用程序访问数据库的应用程序接口（API），它为 Java 应用程序开发提供了统一的结构标准 API，并通过一组驱动程序实现了独立于 DBMS 的数据库访问。

数据库厂商提供面向 JDBC 标准接口的数据库驱动程序，JDBC API 通过这些驱动程序可实现对不同 DBMS 数据库的访问。

使用 JDBC 接口，可以对所连接的数据库进行 SQL 操作访问。JDBC API 是在 java.sql 中定义的，主要有：Connection、DriverManager、Statement、PreparedStatement 和 ResultSet 等类及接口。

JDBC Driver Manager 负责动态管理数据库连接所需要的驱动程序，实现 Java 应用程序通过驱动程序访问特定数据库。可以使用 java.sql.DriverManager 接口访问 JDBC Driver Manager，如设定数据库访问类型参数、加载数据库驱动和初始化 
JDBC 调用的。

各个数据库厂商为支持 JDBC 标注接口，都提供了自己 DBMS 的 JDBC 驱动程序，以便 Java 应用程序可以连接到相应的数据库，进行数据访问处理。

# 常用的 JDBC API

在 java.sql 包中，定义了 JDBC API 的各个接口与类，常用的有：

| 接口/类 | 说明 |
| :- | :- |
| java.sql.Connection | 与指定数据库建立会画连接接口 |
| java.sql.Date | 处理数据库中的日期类型（java.util.Date 的子类） |
| java.sql.Driver | 数据库驱动接口 |
| java.sql.DriverManager | 管理数据库厂商驱动程序类 |
| java.sql.PreparedStatement | 带参数的 Statement 接口|
| java.sql.ResultSet | 数据查询结果集接口 |
| java.sql.SQLException | 提供数据库访问异常信息类 |
| java.sql.Statement | 提供 SQL 执行和结果查询接口 |


# JDBC 数据库访问步骤

## 加载数据库驱动程序

Java 应用程序访问数据库前，必须先加载相应的数据库驱动，才能建立 Java 应用程序与目标数据库间的连接。

该数据库驱动程序需要加载到内存，并在 DriverManager 中进行注册。加载数据库驱动程序可通过调用 Class.forName ( ) 方法实现：

```java
Class.forName("com.mysql.jdbc.Driver");
```

Class.forName ( ) 为注册驱动语句，com.mysql.jdbc.Driver 为 MySQL 数据库的驱动类。

## MySQL 6+ 驱动的使用要求

使用 mysql-connector 6+ 连接 MySQL 时『多』出来两个小要求：

- MySQL 驱动类发生了变化。原来的 `com.mysql.jdbc.Driver` 被标注为废弃，改用新的 Driver：`com.mysql.cj.jdbc.Driver`
- 可选参数 serverTimezone 变成了必要参数：`serverTimezone=UTC`
- 例如：
  ```properties
  url=jdbc:mysql://localhost:3306/scott?useUnicode=true&characterEncoding=utf8&useSSL=false&serverTimezone=UTC
  ```

## 建立数据库连接

为了获取数据库连接对象 Connection ，需要调用 JDBC 接口 DriverManager 的 getConnection (url, username, password) 方法。

- 参数 url：数据库连接地址。格式为：“JDBC协议:IP地址:端口/数据库名称”
- 参数 username：数据库用户名
- 参数 password：用户密码

```java
String url = "jdbc:mysql://127.0.0.1:3306/scott";
Connection conn = DriverManager.getConnection(url, "username", "password");
```

## 创建执行 SQL 语句的 Statement 对象

建立连接后，可以向数据库发送 SQL 执行语句。而发送 SQL 执行语句依赖于 Connection 接口的 createStatement 方法创建的 Statement 对象。

Statement 对象有 3 种类型：

- Statement：用于执行不带参数的简单 SQL 语句
- PreparedStatement：用于执行带参数的预编译 SQL 语句
- CallableStatement：用于执行数据库的存储过程调用


```java
Statement st = conn.createStatement();
```

## 发送 SQL 执行语句

创建 Statement 对象之后，就可以使用 Statement 对象的 3 个基本方法之一向数据库发送 SQL 语句执行操作：

- .executeQeury ( )
- .executeUpdate ( )
- .execute ( )

execute ( )  方法可以执行“增删改查”四大操作，但是，考虑到“增删改”与“查”操作的差异性，通常是使用 executeQuery ( ) 方法执行“增删改”，而用 executeQuery ( ) 方法执行“查”。


## 结果集对象遍历

在执行 SQL 查询语句后，JDBC API 便将数据表查询结果返回到 ResultSet 结果对象中。

为了从结果集中对查询结果数据记性处理，需要调用 ResultSet 结果集对象的 getString 、getInt、getDate 等方法对结果集当前行的指定列进行读取处理。


## 关闭对象

当完成数据库操作访问后，需要关闭 Java 程序中为访问数据库所创建的各种对象：

> 注意，关闭顺序是“先创建的后关闭，后创建的先关闭”。

```java
rs.close();     // 关闭 ResultSet 对象
st.close();     // 关闭 Statemtnt 对象 
conn.close();   // 关闭 Connection 对象
```

