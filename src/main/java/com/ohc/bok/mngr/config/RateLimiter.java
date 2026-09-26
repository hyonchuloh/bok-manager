package com.ohc.bok.mngr.config;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedDeque;

import org.springframework.stereotype.Component;

/**
 * 별도 인프라(Redis 등) 없이 짧은 시간에 반복되는 스팸/스캐너성 요청을 걸러내기 위한
 * 간단한 키(IP)별 고정 윈도우 요청 제한기.
 */
@Component
public class RateLimiter {

    private final ConcurrentHashMap<String, ConcurrentLinkedDeque<Long>> requestLog = new ConcurrentHashMap<>();

    public boolean allow(String key, int maxRequests, long windowMillis) {
        long now = System.currentTimeMillis();
        ConcurrentLinkedDeque<Long> timestamps = requestLog.computeIfAbsent(key, k -> new ConcurrentLinkedDeque<>());
        synchronized (timestamps) {
            while (!timestamps.isEmpty() && now - timestamps.peekFirst() > windowMillis) {
                timestamps.pollFirst();
            }
            if (timestamps.size() >= maxRequests) {
                return false;
            }
            timestamps.addLast(now);
            return true;
        }
    }
}
