package com.fdc.auth;

import com.fdc.controller.VBA.VBAAuthController;
import com.fdc.po.File;
import com.fdc.repository.FileRepository;
import com.fdc.vo.auth.OnlineLoginFormVO;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class OnlineAuthTests {

    @Autowired
    VBAAuthController vbaAuthController;

    @Autowired
    FileRepository fileRepository;

    String testPrivateKey = "testPrivateKey";
    String testUrl = "testUrl";
    File testFile;

    @BeforeAll
    void setUp() {
        File file = new File();
        file.setPrivateKey(testPrivateKey);
        file.setUrl(testUrl);
        fileRepository.save(file);
        assert !fileRepository.findAll().isEmpty() : "测试文件数据项保存失败，文件列表为空";
        testFile = fileRepository.findAll().get(0);
    }

    @Test
    void testOnlineAuthSuccess() {
        OnlineLoginFormVO loginFormVO = new OnlineLoginFormVO("ladpuser_1", "114514");
        String fileId = testFile.getId();

        String privateKey = vbaAuthController.authOnline(fileId, loginFormVO).getResult();

        assert privateKey.equals(testPrivateKey) : "认证失败，返回的密钥不匹配";
    }

    @Test
    void testOnlineAuthWrongUsername() {
        OnlineLoginFormVO loginFormVO = new OnlineLoginFormVO("wrongUsername", "114514");
        String fileId = testFile.getId();

        try {
            vbaAuthController.authOnline(fileId, loginFormVO);
            assert false : "认证应该失败，但未抛出异常";
        } catch (Exception ignored) {
        }
    }

    @Test
    void testOnlineAuthWrongFileId() {
        OnlineLoginFormVO loginFormVO = new OnlineLoginFormVO("ladpuser_1", "114514");
        String fileId = "wrongFileId";

        try {
            vbaAuthController.authOnline(fileId, loginFormVO);
            assert false : "认证应该失败，但未抛出异常";
        } catch (Exception ignored) {
        }
    }

}
