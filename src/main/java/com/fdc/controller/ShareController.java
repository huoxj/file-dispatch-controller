package com.fdc.controller;

import com.fdc.service.ShareService;
import com.fdc.vo.ResponseVO;
import com.fdc.vo.share.ShareVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

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


}
