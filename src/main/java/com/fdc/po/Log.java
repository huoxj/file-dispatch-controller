package com.fdc.po;

import com.fdc.vo.log.LogType;
import com.fdc.vo.log.LogVO;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.util.Date;

@Data
@NoArgsConstructor
@Entity
@Table(name = "Log")
public class Log {

    @Id
    @GeneratedValue(generator = "UUID")
    @GenericGenerator(name = "UUID", strategy = "org.hibernate.id.UUIDGenerator")
    @Column(name = "id")
    private String id;

    @Basic
    @Column(name = "file_id")
    private String fileId;

    @Basic
    @Column(name = "user_id")
    private String userId;

    @Basic
    @Column(name = "time")
    private Date time;

    @Basic
    @Enumerated(EnumType.STRING)
    @Column(name = "type")
    private LogType type;

    @Basic
    @Column(name = "ip")
    private String ip;

    @Basic
    @Column(name = "msg")
    private String msg;

    public LogVO toVO() {
        LogVO logVO = new LogVO();
        logVO.setFileId(this.fileId);
        logVO.setUserId(this.userId);
        logVO.setTime(this.time);
        logVO.setType(this.type);
        logVO.setIp(this.ip);
        logVO.setMsg(this.msg);
        return logVO;
    }
}
