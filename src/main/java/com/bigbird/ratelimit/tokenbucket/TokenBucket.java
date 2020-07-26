package com.bigbird.ratelimit.tokenbucket;

import com.google.common.util.concurrent.RateLimiter;

import java.time.LocalTime;
import java.util.concurrent.TimeUnit;

/**
 * 令牌桶算法限流
 */
public class TokenBucket {
    /**
     * qps,即每秒处理数量
     */
    private int rate;
    private RateLimiter rateLimiter;

    public TokenBucket(int rate) {
        this.rate = rate;
        this.rateLimiter = RateLimiter.create(rate);
    }

    public boolean triggerLimit(String reqPath) {
        boolean acquireRes = rateLimiter.tryAcquire(500, TimeUnit.MILLISECONDS);
        if (acquireRes) {
            System.out.println(LocalTime.now() + " " + reqPath + ",请求成功");
            return false;
        } else {
            System.out.println(LocalTime.now() + " " + reqPath + ",触发限流");
            return true;
        }
    }
}
