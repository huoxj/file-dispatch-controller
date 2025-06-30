package com.fdc.serviceImpl.share;

import com.fdc.po.File;
import com.fdc.po.Share;
import com.fdc.repository.ShareRepository;
import com.fdc.util.CryptoUtil;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class ShareUtil {

    @Autowired
    private ShareRepository shareRepository;

    /**
     * 生成一个用户对某文件的分享权，
     * 将用户 id 和离线口令拼接 + MD5 哈希，
     * 并用此结果即 key 对 ok|sk 进行加密与 base64 编码后的结果。
     * 同时，将离线口令存入数据库中。
     * @return 加密并编码后的 sk
     */
    public String generateShare(File file, String userId, byte[] sk) throws Exception {
        String accessToken = RandomStringUtils.randomAlphanumeric(16);

        // 创建 Share 实例
        Share share = new Share();
        share.setFile(file);
        share.setUserId(userId);
        share.setAccessToken(accessToken);
        shareRepository.save(share);

        // 拼接 userId 和 accessToken 作为加密的 key
        String concat = userId + "|" + accessToken;
        byte[] key = CryptoUtil.md5(concat);
        System.out.println("[Debug] 用户 " + userId + "的 AccessToken : " + accessToken);
        System.out.println("[Debug] 用户 " + userId + "的 Share key : " + CryptoUtil.hexEncode(key));

        // 拼接 sk 和 SHA-256 校验码的前 4 字节
        byte[] skWithChecksum = new byte[sk.length + 4];
        System.arraycopy(sk, 0, skWithChecksum, 0, sk.length);
        byte[] checksum = CryptoUtil.sha256(sk);
        System.arraycopy(checksum, 0, skWithChecksum, sk.length, 4);

        return CryptoUtil.encrypt(skWithChecksum, key);
    }

    public List<String> generateShares(File file, List<String> userIds, byte[] sk) throws Exception {
        List<String> encryptedSks = new ArrayList<>();
        for (String userId : userIds) {
            String encryptedSk = generateShare(file, userId, sk);
            encryptedSks.add(encryptedSk);
        }
        return encryptedSks;
    }
}
