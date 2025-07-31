package com.fdc.vo.file;

import lombok.Data;

import java.time.Instant;
import java.util.Date;
import java.util.List;

@Data
public class ExcelUploadVO {

    // 生成的文件有哪些用户有权限
    List<String> userIds;
    // 文件生成的配置信息
    ExcelConfig excelConfig;

    @Data
    public static class ExcelConfig {
        // 打开次数限制
        private Integer openLimit = 99999;
        // 开始访问时间
        private Date startTime = new Date(0L);
        // 结束访问时间
        private Date endTime = Date.from(Instant.parse("2099-12-31T23:59:59Z"));
        // 是否允许编辑
        private Boolean allowEdit = false;
        // 是否允许另存为
        private Boolean allowSaveAs = false;
        // 是否允许拷贝
        private Boolean allowCopy = false;
        // 是否允许打印
        private Boolean allowPrint = false;
        // 是否允许保存用户名密码
        private Boolean allowSaveToken = false;
        // 水印
        private Watermark watermark = new Watermark();

        @Data
        public static class Watermark {
            // 水印模板
            private String pattern = "";
            // 水印字号
            private Integer fontSize = 0;
            // 水印RGBA
            private String rgba = "0,0,0,0";
        }
    }

}
