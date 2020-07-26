package com.bigbird.ratelimit.aop;

import com.bigbird.ratelimit.annotation.ExtRateLimiter;
import com.google.common.util.concurrent.RateLimiter;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.lang.reflect.Method;
import java.time.LocalTime;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

/**
 * 封装基于RateLimiter的限流注解
 */
@Component
@Aspect
public class RateLimiterAop {

    /**
     * 保存接口路径和限流器的对应关系
     */
    private ConcurrentHashMap<String, RateLimiter> rateLimiters = new ConcurrentHashMap();

    @Pointcut("execution(public * com.bigbird.ratelimit.controller.*.*(..))")
    public void rateLimiterAop() {
    }

    /**
     * 使用环绕通知拦截所有Controller请求
     *
     * @param proceedingJoinPoint
     * @return
     */
    @Around("rateLimiterAop()")
    public Object doBefore(ProceedingJoinPoint proceedingJoinPoint) throws Throwable {
        MethodSignature signature = (MethodSignature) proceedingJoinPoint.getSignature();
        Method method = signature.getMethod();
        if (method == null) {
            return null;
        }

        ExtRateLimiter extRateLimiter = method.getDeclaredAnnotation(ExtRateLimiter.class);
        if (extRateLimiter == null) {
            return proceedingJoinPoint.proceed();
        }

        double permitsPerSecond = extRateLimiter.permitsPerSecond();
        long timeout = extRateLimiter.timeout();

        ServletRequestAttributes requestAttributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        String requestURI = requestAttributes.getRequest().getRequestURI();
        RateLimiter rateLimiter = rateLimiters.get(requestURI);
        if (rateLimiter == null) {
            rateLimiter = RateLimiter.create(permitsPerSecond);
            RateLimiter rateLimiterPrevious = rateLimiters.putIfAbsent(requestURI, rateLimiter);
            if (rateLimiterPrevious != null) {
                rateLimiter = rateLimiterPrevious;
            }
        }

        boolean tryAcquire = rateLimiter.tryAcquire(timeout, TimeUnit.MILLISECONDS);
        if (!tryAcquire) {
            System.out.println(LocalTime.now() + " " + requestURI + " 触发限流");
            doFallback();
            return null;
        }

        System.out.println(LocalTime.now() + " " + requestURI + " 请求成功");
        return proceedingJoinPoint.proceed();
    }

    private void doFallback() {
        ServletRequestAttributes requestAttributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        HttpServletResponse response = requestAttributes.getResponse();
        response.setContentType("text/html;charset=UTF-8");
        PrintWriter writer = null;
        try {
            writer = response.getWriter();
            writer.println("系统忙，请稍后再试！");
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            writer.close();
        }
    }
}
