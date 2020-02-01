<span class="title">Redis 实现分布式锁</span>

# 实现原理

Redis 实现“分布式锁”功能的原子操作主要是 SET 和 EXPIRE 操作，从 Redis 的 2.6.x 版本开始，其提供的 SET 命令格式如下：

```
SET <key> <value> [EX seconds] [PX milliseconds] [NX | XX]
```

**`EX`** 值的是 `key` 的存活时间，单位为秒。**`PX`** 与 **`EX`** 作用一样，唯一的不同就是后者的单位是微秒<small>（使用较少）</small>。 

**`NX`** 和 **`XX`** 作用是相反的。**`NX`** 表示只有当 `key` <strong>不存在时</strong> 才会设置其值；**`XX`** 表示当 `key` 存在时才设置 `key` 的值。

对于使用 **`NX`** 选项的 `SET` 命令，Redis 提供了一个别名命令：**`SETNX`** 。

在使用 **`SETNX`** 操作实现分布式锁功能时，需要注意以下几点：

- 这里的【锁】指的是 Redis 中的一个认为约定的键值对。谁能创建这个键值对，就意味着谁拥有这整个【锁】。

- 使用 **`SETNX`** 命令获取【锁】时，如果操作返回结果是 0<small>（表示 key 已存在，设值失败）</small>，则意味着获取【锁】失败<small>（该锁被其它线程先获取）</small>，反之，则设值成功，表示获取【锁】成功。

- 为了防止其它线程获得【锁】之后，有意或无意，长期持有【锁】而不释放<small>（导致其它线程无法获得该【锁】）</small>。因此，需要为 key 设置一个合理的过期时间。

- 当成功获得【锁】并成功完成响应操作之后，需要释放【锁】<small>（可以执行 DEL 命令将【锁]删除）</small>。

# 工具类

!FILENAME Lock.java
```java
/**
 * 全局锁，包括锁的名称
 */
@NoArgsConstructor
@AllArgsConstructor
@Data
public class Lock {
    private String name;
    private String value;
}
```

!FILENAME DistributedLockUtils.java
```java

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

@Slf4j
@Component
public class DistributedLockUtils {

    private final static long LOCK_EXPIRE = 30 * 1000L; // 单个业务持有锁的时间3秒
    private final static long LOCK_TRY_INTERVAL = 30L;  // 默认30ms尝试一次获得锁
    private final static long LOCK_TRY_TIMEOUT = 20 * 1000L;    // 默认尝试20s后超时

    @Autowired
    private StringRedisTemplate template;

    /**
     * 尝试获取全局锁
     *
     * @param lock 锁的名称
     * @return true 获取成功，false 获取失败
     */
    public boolean tryLock(Lock lock) {
        return getLock(lock, LOCK_TRY_TIMEOUT, LOCK_TRY_INTERVAL, LOCK_EXPIRE);
    }

    /**
     * 尝试获取全局锁
     *
     * @param lock    锁的名称
     * @param timeout 获取超时时间 单位ms
     * @return true 获取成功，false 获取失败
     */
    public boolean tryLock(Lock lock, long timeout) {
        return getLock(lock, timeout, LOCK_TRY_INTERVAL, LOCK_EXPIRE);
    }

    /**
     * 尝试获取全局锁
     *
     * @param lock        锁的名称
     * @param timeout     获取锁的超时时间
     * @param tryInterval 多少毫秒尝试获取一次
     * @return true 获取成功，false 获取失败
     */
    public boolean tryLock(Lock lock, long timeout, long tryInterval) {
        return getLock(lock, timeout, tryInterval, LOCK_EXPIRE);
    }

    /**
     * 尝试获取全局锁
     *
     * @param lock           锁的名称
     * @param timeout        获取锁的超时时间
     * @param tryInterval    多少毫秒尝试获取一次
     * @param lockExpireTime 锁的过期
     * @return true 获取成功，false 获取失败
     */
    public boolean tryLock(Lock lock, long timeout, long tryInterval, long lockExpireTime) {
        return getLock(lock, timeout, tryInterval, lockExpireTime);
    }

    /**
     * 操作redis获取全局锁
     *
     * @param lock           锁的名称
     * @param timeout        获取的超时时间
     * @param tryInterval    多少ms尝试一次
     * @param lockExpireTime 获取成功后锁的过期时间
     * @return true 获取成功，false 获取失败
     */
    public boolean getLock(Lock lock, long timeout, long tryInterval, long lockExpireTime) {
        try {
            if (StringUtils.isEmpty(lock.getName()) || StringUtils.isEmpty(lock.getValue()))
                return false;

            long startTime = System.currentTimeMillis();
            do {
                if (!template.hasKey(lock.getName())) {
                    ValueOperations<String, String> ops = template.opsForValue();
                    ops.set(lock.getName(), lock.getValue(), lockExpireTime);
                    return true;
                }

                // 存在锁
                log.debug("lock is exist!！！");

                if (System.currentTimeMillis() - startTime > timeout) { //尝试
                    return false;
                }

                Thread.sleep(tryInterval);
            } while (template.hasKey(lock.getName()));

        } catch (InterruptedException e) {
            log.error(e.getMessage());
            return false;
        }

        return false;
    }

    /**
     * 释放锁
     */
    public void releaseLock(Lock lock) {
        if (!StringUtils.isEmpty(lock.getName())) {
            template.delete(lock.getName());
        }
    }
}
```


