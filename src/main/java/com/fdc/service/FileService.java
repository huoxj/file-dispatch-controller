package com.fdc.service;

import com.fdc.po.File;
import com.fdc.vo.file.ExcelUploadVO;
import com.fdc.vo.file.FileVO;
import com.fdc.vo.file.OtherUploadVO;
import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.util.Date;
import java.util.List;

public interface FileService {

    String uploadExcelFile(MultipartFile file, ExcelUploadVO excelUploadVO);

    String uploadOtherFiles(List<MultipartFile> files, OtherUploadVO otherUploadVO);

    ResponseEntity<Resource> downloadFile(String fileId);

    List<FileVO> listFiles(Date startTime, Date endTime);

    File getFileById(String fileId);

    InputStream getXlsmStream(String fileId);

    void updateFile(String fileId, InputStream newData);
}
