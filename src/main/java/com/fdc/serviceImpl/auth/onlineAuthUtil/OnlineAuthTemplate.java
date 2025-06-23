package com.fdc.serviceImpl.auth.onlineAuthUtil;

import org.springframework.stereotype.Service;

/**
 * OnlineAuthAdapter 接口使用模板
 * 目前的实现检查用户名是否以 "ladpuser" 开头，并且密码不为空。
 */
@Service("template")
public class OnlineAuthTemplate implements OnlineAuthAdapter{
    @Override
    public boolean handleAuth(String username, String password) {
        return username.startsWith("ladpuser") && !password.isEmpty();
    }
}
