<span class="title">插入、更新和删除数据</span>

增删改操作属于 DML 和 DQL 两大类语句中的 DML 。

DML 语句用于操纵数据库表和视图的数据。

  > - <small>通过执行 **INSERT** 语句可以给表增加数据；</small>
  > - <small>通过执行 **UPDATE** 语句可以更新表的数据；</small>
  > - <small>通过执行 **DELETE** 语句可以删除表的数据。</small>

# 使用 INSERT 语句插入数据

`INSERT` 语句语法的一般形式为：

```sql
INSERT INTO 表名(字段名1, 字段名2, 字段名3, ...)
  VALUE(值1, 值2, 值3, ...);
```

- **INSERT INTO**: 关键字，用以指明要插入的表，以及表中的列。
- **VALUE**: 关键字，指明要插入对应列中的值。

注意，字段名和值的顺序和数量应该是一一对应的。

```sql
INSERT INTO emp(empno, ename, job)
  VALUE(7100, 'Maliy', 'ANALYS');
```

如果写的是 **全字段名，且顺序与字段定义顺序一致**，那么所有的字段名可以省略不写。即，

```sql
INSERT INTO emp(empno, ename, job, mgr, hiredate, sal, comm, deptno)
  VALUE(7700, 'JASH', 'ANALYS', 7902, '1989-06-05', 2640, 0, 20);
```

可以简写为：

```sql
INSERT INTO emp 
  VALUE(7700, 'JASH', 'ANALYS', 7902, '1989-06-05', 2640, 0, 20);
```

# 使用 UPDATE 语句更新表数据

SQL 使用 **UPDATE** 语句对数据表中符合更新条件的记录进行更新。其一般如法如下:

```sql
UPDATE 表名
  SET 字段1=值1, 字段2=值2, 字段3=值3, ...
  WHERE 条件表达式;
```

凡是符合 **WHERE** 子句判断标准的行都会被更新。

如果没有 **WEHERE** 子句，即没有指定更新条件，那么表中所有行信息都会被更新！

为雇员 JASH 加薪 10%

```sql
UPDATE emp SET sal = sal * 1.1 WHERE ename='JASH';
```

为所有雇员加薪 10%

```sql
UPDATE emp SET sal = sal * 1.1;
```

# 使用 DELETE 语句删除表数据

SQL 语言使用 **DELETE** 语句删除表中的记录。语法格式如下：

```sql
DELETE FROM 表名 WHERE 条件表达式;
```

表中凡是符合 **WHERE** 子句判断标准的行都将被删除。

```sql
DELETE FROM emp WHERE ename='JASH';
```

DELETE 语句可以没有 **WHERE** 子句，那么这将删空整张表（注意，清空表不等于删除表）。

```sql
DELETE FROM emp;
```
