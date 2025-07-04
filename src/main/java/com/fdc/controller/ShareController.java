package com.fdc.controller;

import com.fdc.service.ShareService;
import com.fdc.vo.ResponseVO;
import com.fdc.vo.share.ShareVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/share")
public class ShareController {

    @Autowired
    private ShareService shareService;

    /**
     * 获取指定用户或文件的共享信息列表。
     *
     * @param userIds 可选，用户ID列表
     * @param fileIds 可选，文件ID列表
     * @return 共享记录
     */
    @GetMapping("/list")
    public ResponseVO<List<ShareVO>> listShares(
        @RequestParam(value = "userIds", required = false) List<String> userIds,
        @RequestParam(value = "fileIds", required = false) List<String> fileIds) {
        List<ShareVO> shares = shareService.listShares(userIds, fileIds);
        return ResponseVO.buildSuccess(shares);
    }

    /**
     * 将指定文件共享给单个用户。
     *
     * @param userId 用户ID
     * @param fileId 文件ID
     * @return 共享记录
     */
    @PostMapping("/grant/single")
    public ResponseVO<ShareVO> grantSingle(@RequestParam String userId, @RequestParam String fileId) {
        ShareVO share = shareService.grantSingle(userId, fileId);
        return ResponseVO.buildSuccess(share);
    }

    /**
     * 将指定的多个文件共享给单个用户。
     *
     * @param userId 用户ID列表
     * @param fileIds 文件ID
     * @return 共享记录
     */
    @PostMapping("/grant/user")
    public ResponseVO<List<ShareVO>> grantFilesToUser(@RequestParam String userId, @RequestParam List<String> fileIds) {
        List<ShareVO> shares = shareService.grantFilesToUser(userId, fileIds);
        return ResponseVO.buildSuccess(shares);
    }

    /**
     * 单个文件共享给多个用户。
     *
     * @param userIds 用户ID列表
     * @param fileId 文件ID
     * @return 共享记录
     */
    @PostMapping("/grant/file")
    public ResponseVO<List<ShareVO>> grantUsersToFile(@RequestParam List<String> userIds, @RequestParam String fileId) {
        List<ShareVO> shares = shareService.grantUsersToFile(userIds, fileId);
        return ResponseVO.buildSuccess(shares);
    }

    /**
     * 撤销单个用户对单个文件的共享。
     *
     * @param userId 用户ID
     * @param fileId 文件ID
     */
    @PostMapping("/revoke/single")
    public ResponseVO<Void> revokeSingle(@RequestParam String userId, @RequestParam String fileId) {
        shareService.revokeSingle(userId, fileId);
        return ResponseVO.buildSuccess(null);
    }

    /**
     * 撤销单个用户对多个文件的共享。
     *
     * @param userId 用户ID
     * @param fileIds 文件ID列表
     */
    @PostMapping("/revoke/user")
    public ResponseVO<Void> revokeUserFromFiles(@RequestParam String userId, @RequestParam List<String> fileIds) {
        shareService.revokeUserFromFiles(userId, fileIds);
        return ResponseVO.buildSuccess(null);
    }

    /**
     * 撤销多个用户对单个文件的共享。
     *
     * @param userIds 用户ID列表
     * @param fileId 文件ID
     */
    @PostMapping("/revoke/file")
    public ResponseVO<Void> revokeFileFromUsers(@RequestParam List<String> userIds, @RequestParam String fileId) {
        shareService.revokeFileFromUsers(userIds, fileId);
        return ResponseVO.buildSuccess(null);
    }

}
