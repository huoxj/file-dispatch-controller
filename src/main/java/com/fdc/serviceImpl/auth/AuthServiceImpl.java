package com.fdc.serviceImpl.auth;

import com.fdc.exception.AuthFailedException;
import com.fdc.exception.FileNotFoundException;
import com.fdc.po.File;
import com.fdc.repository.FileRepository;
import com.fdc.repository.ShareRepository;
import com.fdc.service.AuthService;
import com.fdc.serviceImpl.auth.onlineAuthUtil.OnlineAuthAdapter;
import com.fdc.vo.auth.OfflineLoginFormVO;
import com.fdc.vo.auth.OnlineLoginFormVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class AuthServiceImpl implements AuthService {

    @Autowired
    @Qualifier("template")
    private OnlineAuthAdapter onlineAuthAdapter;

    @Autowired
    private FileRepository fileRepository;

    @Autowired
    private ShareRepository shareRepository;


    @Override
    public String authOnline(OnlineLoginFormVO loginForm, String fileId) {
        // 调用在线认证校验适配器进行认证
        boolean isAuthSuccess = onlineAuthAdapter.handleAuth(
            loginForm.getUserId(), loginForm.getPassword());
        if (!isAuthSuccess) {
            throw new AuthFailedException("在线认证失败");
        }

        // 获取文件密钥
        Optional<File> fileOptional = fileRepository.findById(fileId);
        if (!fileOptional.isPresent()) {
            throw new FileNotFoundException("文件不存在");
        }

        return fileOptional.get().getPrivateKey();
    }

    @Override
    public Boolean authOffline(OfflineLoginFormVO loginForm, String fileId) {
        // 校验用户是否有文件的访问权限（分享权）
        boolean hasShare = shareRepository.existsByFileIdAndUserId(fileId, loginForm.getUserId());

        return hasShare;
    }
}
