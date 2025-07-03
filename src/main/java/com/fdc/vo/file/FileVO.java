package com.fdc.vo.file;

import com.fdc.po.File;
import lombok.Data;

import java.util.Date;

@Data
public class FileVO {
    private String id; // 文件唯一标识符
    private String url; // 文件访问 URL
    private String privateKey; // Base64 编码的 AES128 密钥或压缩包密码
    private Date createTime; // 文件创建时间

    public File toPO() {
        File file = new File();
        file.setId(this.id);
        file.setUrl(this.url);
        file.setPrivateKey(this.privateKey);
        file.setCreateTime(this.createTime);
        return file;
    }
}
