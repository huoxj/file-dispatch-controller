package com.fdc.serviceImpl.auth.onlineAuthUtil;

import org.springframework.stereotype.Service;

/**
 * OnlineAuthLDAP 类实现了 OnlineAuthAdapter 接口，
 * 可以在这个类中加入 LDAP 在线认证的逻辑，
 * 并替换 AuthServiceImpl 中的 Qualifier 为 ldap。
 * 其他接口无需 cookie 或 session 来验证用户身份，
 * 所以只需要返回一个 boolean 表示认证是否成功。
 */
@Service("ldap")
public class OnlineAuthLDAP implements OnlineAuthAdapter{
    @Override
    public boolean handleAuth(String username, String password) {
        // TODO: 实现 LDAP 认证逻辑
        return false;
    }
}
