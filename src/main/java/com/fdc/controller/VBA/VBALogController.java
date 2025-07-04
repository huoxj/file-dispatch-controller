package com.fdc.controller.VBA;

import com.fdc.service.LogService;
import com.fdc.util.IpUtil;
import com.fdc.vo.log.LogVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

/**
 * 用于处理 VBA 的日志请求
 */
@RestController
@RequestMapping("/api/vba/log")
public class VBALogController {

    @Autowired
    LogService logService;

    /**
     * VBA 行为日志上传
     * @param request HTTP 请求对象，用于获取客户端 IP
     * @param logVO 日志信息
     */
    @PostMapping()
    public void log(HttpServletRequest request, @RequestBody LogVO logVO) {
        logVO.setIp(IpUtil.getIp(request));
        logService.log(logVO);
    }
}
