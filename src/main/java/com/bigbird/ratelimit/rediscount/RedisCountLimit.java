package com.bigbird.ratelimit.rediscount;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.time.LocalTime;
import java.util.concurrent.TimeUnit;

/**
 * 计数法限流
 */
@Component
public class RedisCountLimit {
    public static final String KEY = "ratelimit_";
    public static final int LIMIT = 10;

    @Autowired
    StringRedisTemplate redisTemplate;

    public boolean triggerLimit(String reqPath) {
        String redisKey = KEY + reqPath;
        Long count = redisTemplate.opsForValue().increment(redisKey, 1);
        System.out.println(LocalTime.now() + " " + reqPath + " " + count);
        if (count != null && count == 1) {
            redisTemplate.expire(redisKey, 60, TimeUnit.SECONDS);
        }
        //防止出现并发操作未设置超时时间的场景,这样key就是永不过期,存在风险
        if (redisTemplate.getExpire(redisKey, TimeUnit.SECONDS) == -1) {
            redisTemplate.expire(redisKey, 60, TimeUnit.SECONDS);
        }

        if (count > LIMIT) {
            System.out.println(LocalTime.now() + " " + reqPath + " count is:" + count + ",触发限流");
            return true;
        }

        return false;
    }
}
