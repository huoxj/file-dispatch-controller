package com.fdc.auth;

import com.fdc.controller.VBA.VBAAuthController;
import com.fdc.po.File;
import com.fdc.po.Share;
import com.fdc.repository.FileRepository;
import com.fdc.repository.ShareRepository;
import com.fdc.vo.auth.OfflineLoginFormVO;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class OfflineAuthTests {

    @Autowired
    VBAAuthController vbaAuthController;

    @Autowired
    FileRepository fileRepository;

    @Autowired
    ShareRepository shareRepository;

    String testPrivateKey = "testPrivateKey";
    String testUrl = "testUrl";
    File testFile;

    String testUserId = "ladpuser_1";

    @BeforeAll
    void setUp() {
        File file = new File();
        file.setPrivateKey(testPrivateKey);
        file.setUrl(testUrl);
        fileRepository.save(file);
        testFile = fileRepository.findAll().get(0);

        // 模拟用户对文件的分享权限
        Share share = new Share();
        share.setFile(testFile);
        share.setUserId(testUserId);
        shareRepository.save(share);
    }

    @Test
    void testOfflineAuthSuccess() {
        OfflineLoginFormVO loginFormVO = new OfflineLoginFormVO(testUserId, "114514");
        String fileId = testFile.getId();

        boolean authSuccess = vbaAuthController.authOffline(fileId, loginFormVO).getResult();

        assert authSuccess : "离线认证应该成功，但实际失败了";
    }

    @Test
    void testOfflineAuthWrongUser() {
        OfflineLoginFormVO loginFormVO = new OfflineLoginFormVO("invalidUser", "114514");
        String fileId = testFile.getId();

        boolean authSuccess = vbaAuthController.authOffline(fileId, loginFormVO).getResult();

        assert !authSuccess : "离线认证应该失败，但实际成功了";
    }

}
