package com.fdc.serviceImpl.file.FileStorageHandler;

import com.fdc.exception.FSException;
import com.fdc.exception.FileNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.stream.Stream;

@RequiredArgsConstructor
@Service("local")
public class FSLocalTemp implements FSHandler{

    @Value("${fdc.file-storage.local-temp-path}")
    private String localTempPath;

    private final HttpServletRequest request;

    @Override
    public String storeFile(InputStream fileStream, String fileName) throws Exception {
        String filePath = localTempPath + "/" + fileName;
        File file = new File(filePath);
        // 保证父目录存在
        File parent = file.getParentFile();
        if (parent != null && !parent.exists()) {
            if (!parent.mkdirs()) {
                throw new FSException("创建目录失败: " + file.getParent());
            }
        }

        try (OutputStream outputStream = Files.newOutputStream(file.toPath())) {
            byte[] buffer = new byte[8192];
            int bytesRead;
            while ((bytesRead = fileStream.read(buffer)) != -1) {
                outputStream.write(buffer, 0, bytesRead);
            }
        }

        String requestBaseUrl = request.getScheme() + "://" + request.getServerName() + ":" + request.getServerPort();
        return requestBaseUrl + "/api/file/download/" + fileName;
    }

    @Override
    public void deleteFile(String fileName) throws Exception {
        String filePath = localTempPath + "/" + fileName;
        File file = new File(filePath);
        if (file.exists()) {
            if (!file.delete()) {
                throw new FSException("删除文件失败: " + fileName);
            }
        }
    }

    public String getFilePathByFileId(String fileId) throws Exception {
        try (Stream<Path> files = Files.list(Paths.get(localTempPath))) {
            return files
                .filter(Files::isRegularFile)
                .filter(path -> {
                    return path.getFileName().toString().startsWith(fileId);
                })
                .findAny()
                .map(Path::toString)
                .orElseThrow(() -> new FileNotFoundException("本地存储未找到文件: " + fileId));
        }
    }
}
