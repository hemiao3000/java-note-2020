<span class="title">mybatis-config.xml</span>

# mybatis-config.xml 1.0

```xml
none
```

# mybatis-config.xml 2.0

```xml
none
```

# mybatis-config.xml 3.0

SSM 整合时使用，开启 id 主键回填功能。

```xml
<?xml version="1.0" encoding="UTF-8"?>
<!DOCTYPE configuration
    PUBLIC "-//mybatis.org//DTD Config 3.0//EN"
    "http://mybatis.org/dtd/mybatis-3-config.dtd">
<configuration>
    <!-- 配置全局属性 -->
    <settings>
        <setting name="useGeneratedKeys" value="true"/> <!-- 使用 jdbc 的 getGeneratedKeys 获取数据库自增主键值 -->
    </settings>
</configuration>
```