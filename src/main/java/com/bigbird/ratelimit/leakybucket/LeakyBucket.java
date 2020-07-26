package com.bigbird.ratelimit.leakybucket;

import java.time.LocalTime;

/**
 * 漏斗桶算法限流
 */
public class LeakyBucket {
    /**
     * 每秒处理数量(出水速率)
     */
    private int rate;

    /**
     * 桶容量
     */
    private int capacity;

    /**
     * 当前水量
     */
    private int water;

    /**
     * 最后刷新时间
     */
    private long refreshTime;

    public LeakyBucket(int rate, int capacity) {
        this.capacity = capacity;
        this.rate = rate;
    }

    private void refreshWater() {
        long now = System.currentTimeMillis();
        water = (int) Math.max(0, water - (now - refreshTime) / 1000 * rate);
        refreshTime = now;
    }

    public synchronized boolean triggerLimit(String reqPath) {
        refreshWater();
        if (water < capacity) {
            water++;
            System.out.println(LocalTime.now() + " " + reqPath + " current capacity is:" + (capacity - water) + ",water is:" + water + ",请求成功");
            return false;
        } else {
            System.out.println(LocalTime.now() + " " + reqPath + " current capacity is:" + (capacity - water) + ",water is:" + water + ",触发限流");
            return true;
        }
    }
}
