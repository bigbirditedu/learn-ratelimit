package com.bigbird.ratelimit.controller;

import com.bigbird.ratelimit.tokenbucket.TokenBucket;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;

/**
 * 令牌桶限流算法demo
 */
@RestController
public class TokenBucketLimitController {
    /**
     * 每秒钟限速1
     */
    TokenBucket bucket1 = new TokenBucket(1);
    /**
     * 每秒钟限速2
     */
    TokenBucket bucket2 = new TokenBucket(2);

    @RequestMapping("/tokenBucket1")
    public String leakyBucket1(HttpServletRequest request) {
        String servletPath = request.getServletPath();
        boolean triggerLimit = bucket1.triggerLimit(servletPath);
        if (triggerLimit) {
            return LocalDateTime.now() + " " + servletPath + " 系统忙，稍后再试";
        } else {
            return LocalDateTime.now() + " " + servletPath + "请求成功";
        }
    }

    @RequestMapping("/tokenBucket2")
    public String leakyBucket2(HttpServletRequest request) {
        String servletPath = request.getServletPath();
        boolean triggerLimit = bucket2.triggerLimit(servletPath);
        if (triggerLimit) {
            return LocalDateTime.now() + " " + servletPath + " 系统忙，稍后再试";
        } else {
            return LocalDateTime.now() + " " + servletPath + "请求成功";
        }
    }
}
