package com.fdc.service;

import com.fdc.vo.file.ExcelUploadVO;
import com.fdc.vo.file.FileVO;
import com.fdc.vo.file.OtherUploadVO;
import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;

import java.util.Date;
import java.util.List;

public interface FileService {

    String uploadExcelFile(MultipartFile file, ExcelUploadVO excelUploadVO) throws Exception;

    String uploadOtherFiles(List<MultipartFile> files, OtherUploadVO otherUploadVO) throws Exception;

    ResponseEntity<Resource> downloadFile(String fileId) throws Exception;

    List<FileVO> listFiles(Date startTime, Date endTime);
}
