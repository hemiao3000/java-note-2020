# getMapper 方法


通过 SqlSession 的 insert、upate、delete 和 selectOne、selectList 方法可以去调用 Mapper.xml 中定义的配置文件，进而操作数据库。不过，MyBatis 提供了更『高端』的操作，『帮』程序员去实现 DAO 层代码。

如果将 Mapper.xml 配置文件的 namespace『故意』写的和一个 DAO 接口的完全路径名一样，并且该接口中的方法名有『碰巧』和 Mapp.xml 配置文件中的各个 SQL 语句的 id 值一样，那么 MyBatis 就会去为该接口动态生成一个实现类。

通过 SqlSession 的 getMapper() 方法传入接口的类对象，就可以获得这个由 MyBatis 动态生成的 DAO 接口的实现类。

三个『保持一致』：

- Mapper.xml 的 `namespace` 与接口的完全限定名保持一致。
- SQL 语句的 `id` 与接口中的方法名保持一致。
- SQL 语句的 `parameterType` 与接口的方法的参数类型保持一致。


```java
package com.xja.hemiao.dao;

public interface DepartmentDao {
    public List<Department> listDepartments();
    ...
}
```

```xml
<mapper namespace="com.hemiao.dao.DepartmentDao"> <!-- 注意，此处是接口的完全限定名字。-->
  <select id="listDepartments" resultType="dept">
    SELECT * FROM dept
  </select>
  ...
</mapper>
```

```java
DepartmentDao dao = session.getMapper(DepartmentDao.class);

List<Department> list = dao.listDepartments();
```
