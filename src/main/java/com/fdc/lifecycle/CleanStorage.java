package com.fdc.lifecycle;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Comparator;

/**
 * 在应用关闭时清理临时存储目录
 */
@Component
public class CleanStorage {

    @Value("${fdc.file-storage.local-temp-path}")
    private String localTempPath;

    @PostConstruct
    public void registerShutdownHook() {
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            try {
                Path path = Paths.get(localTempPath);
                if (Files.exists(path) && Files.isDirectory(path)) {
                    Files.walk(path)
                        .sorted(Comparator.reverseOrder()) // 逆序删除，先删除子目录
                        .forEach(filePath -> {
                            try {
                                Files.deleteIfExists(filePath);
                            } catch (Exception e) {
                                System.err.println("删除文件失败: " + filePath + "，错误信息: " + e.getMessage());
                            }
                        });
                    System.out.println("临时本地存储目录已清理: " + localTempPath);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }));
    }
}
