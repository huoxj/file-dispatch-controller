package com.fdc.controller.VBA;

import com.fdc.service.AuthService;
import com.fdc.vo.auth.OfflineLoginFormVO;
import com.fdc.vo.auth.OnlineLoginFormVO;
import com.fdc.vo.ResponseVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

@RestController
@RequestMapping("/api/vba/auth")
public class VBAAuthController {

    @Resource
    AuthService authService;

    /**
     * VBA 在线认证
     *
     * @param fileId 文件ID
     * @param loginForm LADP 用户名密码
     * @return 密钥
     */
    @PostMapping("/online")
    public ResponseVO<String> authOnline(@RequestHeader("file-id") String fileId,
                                         @RequestBody OnlineLoginFormVO loginForm) {
        return ResponseVO.buildSuccess(authService.authOnline(loginForm, fileId));
    }

    /**
     * VBA 离线认证，用于在用户连接网络时检验用户是否拥有权限访问
     * @param fileId 文件ID
     * @return 是否拥有认证权限
     */
    @PostMapping("/offline")
    public ResponseVO<Boolean> authOffline(@RequestHeader("file-id") String fileId,
                                           @RequestBody OfflineLoginFormVO loginForm) {
        return ResponseVO.buildSuccess(authService.authOffline(loginForm, fileId));
    }

}
