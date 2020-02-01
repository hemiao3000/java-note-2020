<span class="title">事务</span>

# 事务的 ACID 属性

## 原子性（Atomicity）

原子性指事务是一个不可分割的工作单位，事务中的操作要么都发生，要么都不发生。

##  一致性（Consistency）

事务必须使数据库从一个一致状态变为另一个一致状态。

## 隔离性（Isolation）

事务的隔离性是指一个事务的执行过程中不被其他事务干扰，即一个事务内部的操作及使用的数据对并发的其他事务是隔离的，并发执行的事务之间不能互相干扰。

## 持久性（Durability）

一旦事务被提交，它对数据库中数据的改变就是永久性的。


# 事务的隔离级别

对于同时运行的多个事务，如果没有采取必要的隔离措施，会造成各种并发问题：


## 脏读

对于两个事务 T1 和 T2 可能会出现如下情况，T1 读取了已经被 T2 **更新但是还未提交** 的字段，若此时 T2 回滚，那么 T1 读取到的内容就是临时且无效的。


## 不可重复读

对于两个事务 T1 和 T2 可能会出现如下情况，T1 读取了一个字段，然后 T2 更新了这个字段。之后，T1 再次读取该字段时，会发现值发生了变化。


## 幻读

对于两个事务 T1 和 T2 可能会出现如下情况，T1 从一个表中读取了一个字段，然后 T2 在该表中插入了一些新的数据。之后，T1 再次读取该字段时，会发现多出来几行。


**不可重复读** 和 **幻读** 在一定程度上是可接受的，而 **脏读** 是完全不可接受的。

一个事务与另一个事务的隔离程度被称为隔离级别。隔离级别越高，事务间的相互干扰就越小，数据的一致性就越好，但同时并发性就越弱。

以上三个问题现象为“界限”，数据库提供四种隔离级别：

| 隔离级别 | 说明 |
| :- | :- |

- Read Uncommited 
  
  允许读取未提交数据 
  
  这种情况下，脏读、不可重复读 和 幻读 现象都会出现。这种隔离级别的隔离程度最低。

- Read Commited 
  
  只允许读取已提交数据（不允许读取未提交数据）
  
  这种情况下，脏读不会出现，但 不可重复读 和 幻读 会出现

- Repeatable Read 

  确保同一个事务中多次读取同一个字读，必须是同样的值。
  
  这种情况下，禁止了其他事务的“更新”操作，脏读 和 不可重复读 现象不会出现。幻读 现象会出现。
  
  A 用户对是数据库进行 **增删改** 操作时，其他用户不能对数据库进行 **增删改** 操作。

- Serializable 
  
  最严格的隔离级别。

  一个事务在操作表中的某行数据时，禁止其他事务对该行数据进行任何增删改操作。
  
  这种情况下，脏读、不可重复读 和 幻读 现象都不会出现。但这种隔离级别的性能代价也是最大的。

MySQL 支持四种隔离级别，默认事务隔离级别为 **Repeatable Read** 。

## JDBC 事务的自动提交和隔离级别 

!FILENAME Connection 类
```java
void setAutoCommit(boolean autoCommit) throws SQLException;

void setTransactionIsolation(int level) throws SQLException
Connection.TRANSACTION_NONE
Connection.TRANSACTION_READ_UNCOMMITTED
Connection.TRANSACTION_READ_COMMITTED
Connection.TRANSACTION_REPEATABLE_READ
Connection.TRANSACTION_SERIALIZABLE
```
