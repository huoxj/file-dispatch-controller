package com.fdc.serviceImpl.file.FileStorageHandler;

import java.io.InputStream;

/**
 * 处理文件存储的接口。实现文件持久化。
 * 可通过实现接口来支持不同的存储方式，如本地存储、OSS存储等。
 * 目前只实现了本地存储 (FSLocalTemp)。
 */
public interface FSHandler {

    /**
     * 存储文件到指定的存储介质。如果文件已存在，则覆盖原有
     * 文件。
     *
     * @param fileStream 输入流，包含要存储的文件内容。
     * @param fileName 文件标识符，用于唯一标识存储的文件，
     *               推荐将文件记录到数据库后获取自动生成
     *               的 UUID 作为 fileId。
     * @return 存储后的文件 url，可直接用于下载文件。
     * @throws Exception 如果存储过程中发生错误。
     */
    String storeFile(InputStream fileStream, String fileName) throws Exception;

    /**
     * 删除指定的文件。
     *
     * @param fileName 文件标识符，用于唯一标识要删除的文件。
     * @throws Exception 如果删除过程中发生错误。
     */
    void deleteFile(String fileName) throws Exception;

}
