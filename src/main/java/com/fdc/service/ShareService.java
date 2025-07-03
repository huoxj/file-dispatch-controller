package com.fdc.service;

import com.fdc.exception.BusinessException;
import com.fdc.vo.share.ShareVO;

import java.util.List;

public interface ShareService {

    List<ShareVO> listShares(List<String> userIds, List<String> fileIds);

    ShareVO grantSingle(String userId, String fileId);
    List<ShareVO> grantFilesToUser(String userId, List<String> fileIds);
    List<ShareVO> grantUsersToFile(List<String> userIds, String fileId);

    void revokeSingle(String userId, String fileId);
    void revokeUserFromFiles(String userId, List<String> fileIds);
    void revokeFileFromUsers(List<String> userIds, String fileId);
}
