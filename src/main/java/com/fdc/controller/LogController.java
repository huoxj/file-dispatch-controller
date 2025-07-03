package com.fdc.controller;

import com.fdc.service.LogService;
import com.fdc.util.TimeUtil;
import com.fdc.vo.ResponseVO;
import com.fdc.vo.log.LogType;
import com.fdc.vo.log.LogVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;

/**
 * 用于处理 Web 端或者后端日志相关的查询请求
 */
@RestController
@RequestMapping("/api/log")
public class LogController {

    @Autowired
    LogService logService;

    /**
     * 查询日志，支持多条件过滤。条件间为或关系。
     * 条件可以为空，表示不过滤。
     *
     * @param fileIds    文件 ID 列表
     * @param userIds    用户 ID 列表
     * @param types     日志类型列表
     * @param ips       IP 地址列表
     * @param startTime 起始时间，使用 ISO 8601 格式（例如：2023-01-01T00:00:00）
     * @param endTime   结束时间，同样使用 ISO 8601 格式
     * @return 日志列表
     */
    @GetMapping()
    public ResponseVO<List<LogVO>> queryLogs(
        @RequestParam(value = "fileIds", required = false) List<String> fileIds,
        @RequestParam(value = "userIds", required = false) List<String> userIds,
        @RequestParam(value = "types", required = false) List<LogType> types,
        @RequestParam(value = "ips", required = false) List<String> ips,
        @RequestParam(value = "startTime", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startTime,
        @RequestParam(value = "endTime", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endTime
    ) {
        List<LogVO> logs = logService.queryLogs(
            fileIds, userIds, types, ips,
            TimeUtil.localDateTimeToDate(startTime),
            TimeUtil.localDateTimeToDate(endTime)
        );
        return ResponseVO.buildSuccess(logs);
    }
}
