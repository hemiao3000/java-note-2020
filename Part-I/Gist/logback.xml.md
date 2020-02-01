<span class="title">logback.xml</span>

# logback.xml 1.0

!FILENAME 简单的例子
```xml
<?xml version="1.0" encoding="UTF-8"?>
<configuration debug="false">
    <appender name="CONSOLE" class="ch.qos.logback.core.ConsoleAppender">
      <encoder>
        <pattern> %5p --- %-40.40logger{39} : %m%n</pattern>
          <charset>utf8</charset>
        </encoder>
      </appender>
      <logger name="hemiao3000.gitee.io" level="DEBUG" additivity="false">
        <appender-ref ref="CONSOLE"/>
      </logger>
    <root level="WARN">
      <appender-ref ref="CONSOLE" />
    </root>
</configuration>
```

# logback.xml 2.0

!FILENAME 更复杂的例子
```xml
<?xml version="1.0" encoding="UTF-8"?>
<configuration debug="false">

    <property name="LOG_PATH" value="${catalina.base}/logs/webapps"/>
    <property name="LOG_FILE" value="${LOG_PATH}/spring.log"/>

    <appender name="CONSOLE" class="ch.qos.logback.core.ConsoleAppender">
        <encoder>
            <pattern>%d{yyyy-MM-dd HH:mm:ss} %5p ---- %-40.40logger{39} : %m%n</pattern>
            <charset>utf8</charset>
        </encoder>
    </appender>

    <appender name="FILE" class="ch.qos.logback.core.rolling.RollingFileAppender">
        <encoder>
            <pattern>%d{yyyy-MM-dd HH:mm:ss.SSS} %5p ---- [%15.15t] %-40.40logger{39} : %m%n</pattern>
            <charset>utf8</charset>
        </encoder>
        <file>${LOG_FILE}</file>
        <rollingPolicy class="ch.qos.logback.core.rolling.SizeAndTimeBasedRollingPolicy">
            <fileNamePattern>${LOG_FILE}.%d{yyyy-MM-dd}.%i.gz</fileNamePattern>
            <maxFileSize>10MB</maxFileSize>
            <maxHistory>0</maxHistory>
        </rollingPolicy>
    </appender>

    <!-- 指定类与 <looger> 的关联关系 -->
    <logger name="com.sxnd" level="DEBUG" additivity="false">
        <appender-ref ref="CONSOLE"/>
        <appender-ref ref="FILE" />
    </logger>

    <root level="INFO">
        <appender-ref ref="CONSOLE" />
        <appender-ref ref="FILE" />
    </root>

</configuration>
```