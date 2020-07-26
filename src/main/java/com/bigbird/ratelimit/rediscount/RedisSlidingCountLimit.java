package com.bigbird.ratelimit.rediscount;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.time.LocalTime;
import java.util.UUID;

/**
 * 滑动窗口计数法限流
 */
@Component
public class RedisSlidingCountLimit {
    public static final String KEY = "slidelimit_";
    public static final int LIMIT = 10;
    //限流时间间隔(秒)
    public static final int PERIOD = 60;

    @Autowired
    StringRedisTemplate redisTemplate;

    public boolean triggerLimit(String reqPath) {
        String redisKey = KEY + reqPath;

        if (redisTemplate.hasKey(redisKey)) {
            Integer count = redisTemplate.opsForZSet().rangeByScore(redisKey, System.currentTimeMillis() - PERIOD * 1000, System.currentTimeMillis()).size();
            System.out.println(count);
            if (count != null && count > LIMIT) {
                System.out.println(LocalTime.now() + " " + reqPath + " count is:" + count + ",触发限流");
                return true;
            }
        }

        long currentTime = System.currentTimeMillis();
        redisTemplate.opsForZSet().add(redisKey, UUID.randomUUID().toString(), currentTime);
        // 清除旧的访问数据,比如period=60s时,标识清除60s以前的记录
        redisTemplate.opsForZSet().removeRangeByScore(redisKey, 0, System.currentTimeMillis() - PERIOD * 1000);
        return false;
    }
}
