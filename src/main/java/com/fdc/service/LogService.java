package com.fdc.service;

import com.fdc.vo.log.LogType;
import com.fdc.vo.log.LogVO;

import java.util.Date;
import java.util.List;

public interface LogService {

    void log(LogVO logVO);

    List<LogVO> queryLogs(List<String> fileIds, List<String> userIds, List<LogType> types, List<String> ips, Date startTime, Date endTime);
}
