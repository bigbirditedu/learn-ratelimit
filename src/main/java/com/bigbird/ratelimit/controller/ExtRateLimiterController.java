package com.bigbird.ratelimit.controller;

import com.bigbird.ratelimit.annotation.ExtRateLimiter;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalTime;

/**
 * 自定义注解标识接口进行限流
 */
@RestController
public class ExtRateLimiterController {
    @RequestMapping("/extRate1")
    @ExtRateLimiter(permitsPerSecond = 0.5, timeout = 500)
    public String extRate1(HttpServletRequest request) {
        return LocalTime.now() + " " + request.getRequestURI() + "请求成功";
    }

    @RequestMapping("/extRate2")
    @ExtRateLimiter(permitsPerSecond = 2, timeout = 500)
    public String extRate2(HttpServletRequest request) {
        return LocalTime.now() + " " + request.getRequestURI() + "请求成功";
    }
}
