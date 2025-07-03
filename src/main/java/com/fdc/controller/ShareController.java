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

    @GetMapping("/list")
    public ResponseVO<List<ShareVO>> listShares(
        @RequestParam(value = "userIds", required = false) List<String> userIds,
        @RequestParam(value = "fileIds", required = false) List<String> fileIds) {
        List<ShareVO> shares = shareService.listShares(userIds, fileIds);
        return ResponseVO.buildSuccess(shares);
    }

    @PostMapping("/grant/single")
    public ResponseVO<ShareVO> grantSingle(@RequestParam String userId, @RequestParam String fileId) {
        ShareVO share = shareService.grantSingle(userId, fileId);
        return ResponseVO.buildSuccess(share);
    }

    @PostMapping("/grant/user")
    public ResponseVO<List<ShareVO>> grantFilesToUser(@RequestParam String userId, @RequestParam List<String> fileIds) {
        List<ShareVO> shares = shareService.grantFilesToUser(userId, fileIds);
        return ResponseVO.buildSuccess(shares);
    }

    @PostMapping("/grant/file")
    public ResponseVO<List<ShareVO>> grantUsersToFile(@RequestParam List<String> userIds, @RequestParam String fileId) {
        List<ShareVO> shares = shareService.grantUsersToFile(userIds, fileId);
        return ResponseVO.buildSuccess(shares);
    }

    @PostMapping("/revoke/single")
    public ResponseVO<Void> revokeSingle(@RequestParam String userId, @RequestParam String fileId) {
        shareService.revokeSingle(userId, fileId);
        return ResponseVO.buildSuccess(null);
    }

    @PostMapping("/revoke/user")
    public ResponseVO<Void> revokeUserFromFiles(@RequestParam String userId, @RequestParam List<String> fileIds) {
        shareService.revokeUserFromFiles(userId, fileIds);
        return ResponseVO.buildSuccess(null);
    }

    @PostMapping("/revoke/file")
    public ResponseVO<Void> revokeFileFromUsers(@RequestParam List<String> userIds, @RequestParam String fileId) {
        shareService.revokeFileFromUsers(userIds, fileId);
        return ResponseVO.buildSuccess(null);
    }

}
