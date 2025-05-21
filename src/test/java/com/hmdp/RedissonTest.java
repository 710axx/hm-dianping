package com.hmdp;

import org.junit.jupiter.api.BeforeEach;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.junit.jupiter.api.Test;

import javax.annotation.Resource;

@SpringBootTest
class RedissonTest {

    @Resource
    private RedissonClient redissonClient;

    // 创建锁对象
    private RLock lock;

    @BeforeEach
    void setUp() {
        lock = redissonClient.getLock("order");
    }

    @Test
    void method1() {
        boolean isLock = lock.tryLock();
        if (!isLock) {
            System.out.println("获取锁失败");
            return;
        }
        try {
            System.out.println("获取锁成功, 1");
            method2();
        } finally {
            System.out.println("释放锁, 1");
            lock.unlock();
        }
    }

    void method2() {
        boolean isLock = lock.tryLock();
        if (!isLock) {
            System.out.println("获取锁失败, 2");
            return;
        }
        try {
            System.out.println("获取锁成功, 2");
        } finally {
            System.out.println("释放锁, 2");
            lock.unlock();
        }
    }
}
