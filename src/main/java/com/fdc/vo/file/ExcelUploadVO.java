package com.fdc.vo.file;

import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

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
        private Integer openLimit;
        // 开始访问时间
        private Date startTime;
        // 结束访问时间
        private Date endTime;
        // 是否允许编辑
        private Boolean allowEdit;
        // 是否允许另存为
        private Boolean allowSaveAs;
        // 是否允许拷贝
        private Boolean allowCopy;
        // 是否允许打印
        private Boolean allowPrint;
        // 水印
        private Watermark watermark;

        @Data
        public static class Watermark {
            // 水印模板
            private String pattern;
            // 水印字号
            private Integer fontSize;
            // 水印RGBA
            private String rgba;
        }
    }

}
