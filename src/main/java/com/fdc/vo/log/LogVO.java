package com.fdc.vo.log;

import com.fdc.po.Log;
import lombok.Data;

import java.util.Date;

@Data
public class LogVO {

    private String fileId;

    private String userId;

    private Date time;

    private LogType type;

    private String ip;

    private String msg;

    public Log toPO() {
        Log log = new Log();
        log.setFileId(this.fileId);
        log.setUserId(this.userId);
        log.setTime(this.time);
        log.setType(this.type);
        log.setIp(this.ip);
        log.setMsg(this.msg);
        return log;
    }
}
