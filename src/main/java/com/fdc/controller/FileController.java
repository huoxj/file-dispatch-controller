package com.fdc.controller;

import com.fdc.service.FileService;
import com.fdc.util.TimeUtil;
import com.fdc.vo.ResponseVO;
import com.fdc.vo.file.ExcelUploadVO;
import com.fdc.vo.file.FileVO;
import com.fdc.vo.file.OtherUploadVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/file")
public class FileController {

    @Autowired
    private FileService fileService;

    /**
     * 上传 Excel 文件
     *
     * @param file   要上传的 Excel 文件
     * @param config 生成文件的配置信息
     * @return 返回处理好的文件 Url
     */
    @PostMapping("/excel")
    public ResponseVO<String> uploadExcelFile(@RequestPart("file") MultipartFile file,
                                              @RequestPart("config") ExcelUploadVO config) throws Exception {
        return ResponseVO.buildSuccess(fileService.uploadExcelFile(file, config));
    }

    /**
     * 上传其他类型的文件
     *
     * @param files 要打包的文件列表
     * @param config 包含其他类型的文件和上传配置
     * @return 返回打好的压缩包的 Url
     */
    @PostMapping("/other")
    public ResponseVO<String> uploadOtherFiles(@RequestPart("files") List<MultipartFile> files,
                                               @RequestPart("config") OtherUploadVO config) throws Exception {
        return ResponseVO.buildSuccess(fileService.uploadOtherFiles(files, config));
    }

    /**
     * 使用本地存储方式(FSLocalTemp)的下载文件临时接口
     * 仅作为临时接口使用，实际应用中应使用更安全的存储方
     * 式，比如 OSS 或其他云存储服务。
     *
     * @param fileId 文件唯一标识符
     * @return 返回文件
     */
    @GetMapping("/download/{fileId}")
    public ResponseEntity<Resource> downloadFile(@PathVariable String fileId) throws Exception {
        return fileService.downloadFile(fileId);
    }

    @GetMapping("/list")
    public ResponseVO<List<FileVO>> listFiles(
        @RequestParam(value = "startTime", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startTime,
        @RequestParam(value = "endTime", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endTime
    ) {
        List<FileVO> files = fileService.listFiles(
            TimeUtil.localDateTimeToDate(startTime),
            TimeUtil.localDateTimeToDate(endTime)
        );
        return ResponseVO.buildSuccess(files);
    }

}
