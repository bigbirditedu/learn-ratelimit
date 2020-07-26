package com.bigbird.ratelimit.controller;

import com.bigbird.ratelimit.rediscount.RedisCountLimit;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;

/**
 * 基于redis的计数器限流demo
 */
@RestController
public class RedisCountLimitController {
    @Autowired
    RedisCountLimit redisCountLimit;

    @RequestMapping("/rediscount")
    public String redisCount(HttpServletRequest request) {
        String servletPath = request.getServletPath();
        boolean triggerLimit = redisCountLimit.triggerLimit(servletPath);
        if (triggerLimit) {
            return LocalDateTime.now() + " " + servletPath + " 系统忙，稍后再试";
        } else {
            return LocalDateTime.now() + " " + servletPath + "请求成功";
        }
    }

    @RequestMapping("/rediscount2")
    public String redisCount2(HttpServletRequest request) {
        String servletPath = request.getServletPath();
        boolean triggerLimit = redisCountLimit.triggerLimit(servletPath);
        if (triggerLimit) {
            return LocalDateTime.now() + " " + servletPath + " 系统忙，稍后再试";
        } else {
            return LocalDateTime.now() + " " + servletPath + "请求成功";
        }
    }
}
