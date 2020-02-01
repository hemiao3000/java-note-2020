<span class="title">操作 Database</span>

# 创建 Database

```sql
SHOW DATABASES;
```

```sql
CREATE DATABASE <数据库名>;
```

> 注意，上面出现的 **`<>`** 表示此处内容由用户自定义。**`<>`** 本身并非 SQL 的内容的一部分。

# 删除 Database

```sql
DROP DATABASE <数据库名>;
```

```sql
DROP DATABASE IF EXISTS <数据库名>;
```

# 创建 Database 并指定默认字符集

```sql
CREATE DATABASE <数据库名>
  DEFAULT CHARACTER SET utf8mb4 
  COLLATE utf8mb4_unicode_ci;
```

<font color="red">**注意**</font>：因为历史原因，MySQL 中的 **`utf8`** 并非标准意义上的 utf8 字符编码，**`utf8mb4`** 才是。<small>`utf8` 实际上是 `utf8mb3` 的一个别名。</small>

*`utf8mb3`* 存储一个文字时，最多占 3 个字节，而 *`utf8mb4`* 最多可占 4 个字节。所以，如果遇到特别特别生僻的中日韩文字时，*`utf8mb3`* 可能仍会出现乱码问题。

# 数据库引擎

『数据库引擎』是数据库管理系统（DBMS）的底层组件和核心组件，DBMS 就靠它进行创建、查询、更新 和 删除数据操作。

MySQL 支持选用多种数据引擎，不同的数据库引擎具有不同的，各具特色的功能。MySQL 支持的数据库引擎有：

```sql
SHOW ENGINES;
```

**`InnoDB`** 引擎是 MySQL 5.5.5 之后的默认引擎。它支持『事务』和『外键』，这是它的两个核心特性。

**`MyISAM`** 引擎是 MySQL 5.5.5 之前的默认引擎。它支持『全文索引』，并且早期的版本比当时的 InnoDB 更快。
