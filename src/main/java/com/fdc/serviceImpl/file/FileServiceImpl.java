package com.fdc.serviceImpl.file;

import com.fdc.exception.BusinessException;
import com.fdc.exception.FileNotFoundException;
import com.fdc.po.File;
import com.fdc.repository.FileRepository;
import com.fdc.service.FileService;
import com.fdc.serviceImpl.file.FileStorageHandler.FSHandler;
import com.fdc.serviceImpl.file.FileStorageHandler.FSLocalTemp;
import com.fdc.util.CryptoUtil;
import com.fdc.vo.file.ExcelUploadVO;
import com.fdc.vo.file.OtherUploadVO;
import net.lingala.zip4j.ZipFile;
import net.lingala.zip4j.model.ZipParameters;
import net.lingala.zip4j.model.enums.CompressionMethod;
import net.lingala.zip4j.model.enums.EncryptionMethod;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.nio.file.Files;
import java.security.SecureRandom;
import java.util.Date;
import java.util.List;

@Service
public class FileServiceImpl implements FileService {

    @Autowired
    FileRepository fileRepository;

    @Autowired
    ExcelUtil excelProcesser;

    FSHandler fsHandler;

    // 通过配置来初始化文件存储方式，默认本地存储
    FileServiceImpl(@Value("${fdc.file-storage.type:local}") String storageType,
                    ApplicationContext context) {
        fsHandler = context.getBean(storageType, FSHandler.class);
    }

    @Override
    public String uploadExcelFile(MultipartFile file, ExcelUploadVO excelUploadVO) throws Exception {
        InputStream data = file.getInputStream();

        // 随机生成 AES-128 密钥
        byte[] sk = new byte[16];
        new SecureRandom().nextBytes(sk);
        // 生成文件实体
        File fileEntity = new File();
        fileEntity.setCreateTime(new Date());
        fileEntity.setPrivateKey(CryptoUtil.base64Encode(sk));
        File savedFileEntity = fileRepository.save(fileEntity);  // 需要先获取 FileId

        InputStream processedTemplate = excelProcesser.applyDataToTemplate(data, sk, savedFileEntity, excelUploadVO);

        return saveAndPersistFile(processedTemplate, "xlsm", savedFileEntity);
    }

    @Override
    public String uploadOtherFiles(List<MultipartFile> files, OtherUploadVO otherUploadVO) throws Exception {

        java.io.File tempZip = java.io.File.createTempFile("upload-" + System.currentTimeMillis(), ".zip");

        ZipParameters zipParams = new ZipParameters();
        zipParams.setCompressionMethod(CompressionMethod.DEFLATE);
        zipParams.setEncryptFiles(true);
        zipParams.setEncryptionMethod(EncryptionMethod.AES);

        try (ZipFile zipFile = new ZipFile(tempZip, otherUploadVO.getPassword().toCharArray())) {
            for (MultipartFile file : files) {
                java.io.File tempFile = java.io.File.createTempFile("upload-" + System.currentTimeMillis(), file.getOriginalFilename());
                file.transferTo(tempFile);
                zipParams.setFileNameInZip(file.getOriginalFilename());
                zipFile.addFile(tempFile, zipParams);
            }
        }

        File fileEntity = new File();
        fileEntity.setCreateTime(new Date());
        fileEntity.setPrivateKey(CryptoUtil.base64Encode(otherUploadVO.getPassword().getBytes()));
        return saveAndPersistFile(Files.newInputStream(tempZip.toPath()), "zip", fileEntity);
    }

    // 入库与持久化
    private String saveAndPersistFile(InputStream fileStream, String fileExt, File fileEntity) throws Exception {
        File savedFileEntity = fileEntity.getId() == null ? fileRepository.save(fileEntity) : fileEntity;
        String url;
        try {
            url = fsHandler.storeFile(fileStream, savedFileEntity.getId() + "." + fileExt);
        } catch (Exception e) {
            fileRepository.delete(savedFileEntity);
            throw e;
        }
        savedFileEntity.setUrl(url);
        fileRepository.save(savedFileEntity);
        return url;
    }

    @Override
    public ResponseEntity<Resource> downloadFile(String fileId) throws Exception {
        if (!(fsHandler instanceof FSLocalTemp)) {
            throw new BusinessException("500", "服务端当前存储方式不支持此下载文件接口");
        }

        FSLocalTemp fsLocalTemp = (FSLocalTemp) fsHandler;

        String filePath = fsLocalTemp.getFilePathByFileId(fileId);

        java.io.File file = new java.io.File(filePath);
        if (!file.exists()) {
            throw new FileNotFoundException("文件不存在: " + fileId);
        }
        Resource resource = new UrlResource(file.toURI());
        return ResponseEntity.ok()
            .contentType(MediaType.APPLICATION_OCTET_STREAM)
            .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + file.getName() + "\"")
            .body(resource);
    }
}
