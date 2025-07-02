package com.fdc.util;

import javax.servlet.http.HttpServletRequest;

public class IpUtil {
    public static String getIp(HttpServletRequest request) {
        String ip = request.getHeader("X-Forwarded-For");
        if (ip != null && !ip.isEmpty() && !"unknown".equalsIgnoreCase(ip)) {
            // 可能有多个 IP，用逗号分隔，取第一个
            return ip.split(",")[0];
        }
        return request.getRemoteAddr();
    }
}
