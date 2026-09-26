package com.ohc.bok.mngr.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.servlet.HandlerInterceptor;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * 타임라인 게시물/댓글 쓰기 엔드포인트에 대한 IP별 요청 제한.
 * 인증 없이 누구나 작성 가능한 엔드포인트라 자동화 스캐너(sqlmap 등)의
 * 반복 요청에 노출되어 있어, 최소한의 방어로 단시간 요청 횟수를 제한한다.
 */
public class WriteRateLimitInterceptor implements HandlerInterceptor {

    private static final int MAX_REQUESTS = 10;
    private static final long WINDOW_MILLIS = 60_000;

    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    private final RateLimiter rateLimiter;

    public WriteRateLimitInterceptor(RateLimiter rateLimiter) {
        this.rateLimiter = rateLimiter;
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String ip = request.getRemoteAddr();
        if (!rateLimiter.allow(ip, MAX_REQUESTS, WINDOW_MILLIS)) {
            logger.warn("--- 요청 제한 초과. ip=[{}], uri=[{}]", ip, request.getRequestURI());
            response.setStatus(429); // Too Many Requests
            response.setContentType("text/plain; charset=UTF-8");
            response.getWriter().write("요청이 너무 많습니다. 잠시 후 다시 시도해주세요.");
            return false;
        }
        return true;
    }
}
