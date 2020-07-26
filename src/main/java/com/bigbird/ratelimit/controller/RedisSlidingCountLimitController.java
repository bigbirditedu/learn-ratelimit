package com.bigbird.ratelimit.controller;

import com.bigbird.ratelimit.rediscount.RedisSlidingCountLimit;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;

/**
 * 基于Redis的滑动窗口计数器限流demo
 */
@RestController
public class RedisSlidingCountLimitController {
    @Autowired
    RedisSlidingCountLimit redisSlidingCountLimit;

    @RequestMapping("/slidecount")
    public String redisCount(HttpServletRequest request) {
        String servletPath = request.getServletPath();
        boolean triggerLimit = redisSlidingCountLimit.triggerLimit(servletPath);
        if (triggerLimit) {
            return LocalDateTime.now() + " " + servletPath + " 系统忙，稍后再试";
        } else {
            return LocalDateTime.now() + " " + servletPath + "请求成功";
        }
    }

    @RequestMapping("/slidecount2")
    public String redisCount2(HttpServletRequest request) {
        String servletPath = request.getServletPath();
        boolean triggerLimit = redisSlidingCountLimit.triggerLimit(servletPath);
        if (triggerLimit) {
            return LocalDateTime.now() + " " + servletPath + " 系统忙，稍后再试";
        } else {
            return LocalDateTime.now() + " " + servletPath + "请求成功";
        }
    }
}
