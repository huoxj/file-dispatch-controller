package com.fdc.service;

import com.fdc.vo.auth.OfflineLoginFormVO;
import com.fdc.vo.auth.OnlineLoginFormVO;

public interface AuthService {

    String authOnline(OnlineLoginFormVO loginForm, String fileId);
    Boolean authOffline(OfflineLoginFormVO loginForm, String fileId);
}
