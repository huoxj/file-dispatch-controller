package com.fdc.serviceImpl.auth.onlineAuthUtil;

/**
 * OnlineAuthAdapter 接口定义了处理在线认证的适配器。
 * 通过实现此接口，可以将不同的在线认证方式（如 LDAP）集成到系统中。
 * 返回值为 true 表示认证成功，false 表示认证失败。
 */
public interface OnlineAuthAdapter {
    boolean handleAuth(String username, String password);
}
