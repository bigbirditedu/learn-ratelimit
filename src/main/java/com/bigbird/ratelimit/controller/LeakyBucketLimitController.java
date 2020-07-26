package com.bigbird.ratelimit.controller;

import com.bigbird.ratelimit.leakybucket.LeakyBucket;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;

/**
 * 漏斗桶算法限流demo
 */
@RestController
public class LeakyBucketLimitController {
    LeakyBucket bucket1 = new LeakyBucket(2, 10);
    LeakyBucket bucket2 = new LeakyBucket(2, 20);

    @RequestMapping("/leakyBucket1")
    public String leakyBucket1(HttpServletRequest request) {
        String servletPath = request.getServletPath();
        boolean triggerLimit = bucket1.triggerLimit(servletPath);
        if (triggerLimit) {
            return LocalDateTime.now() + " " + servletPath + " 系统忙，稍后再试";
        } else {
            return LocalDateTime.now() + " " + servletPath + "请求成功";
        }
    }

    @RequestMapping("/leakyBucket2")
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
