package com.fdc.serviceImpl.file;

import com.fdc.exception.BusinessException;
import com.fdc.exception.FileIOException;
import com.fdc.po.File;
import com.fdc.serviceImpl.share.ShareUtil;
import com.fdc.util.CryptoUtil;
import com.fdc.util.StringUtil;
import com.fdc.vo.file.ExcelUploadVO;
import org.apache.poi.ss.usermodel.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.*;

@Service
public class ExcelUtil {

    @Autowired
    ShareUtil shareUtil;

    @Value("classpath:excel_template.xlsm")
    private Resource excelTemplateResource;

    @Value("${fdc.base-url}")
    private String requestBaseUrl;

    // 将 Excel 数据应用到模板中，并返回处理后的 InputStream
    public InputStream applyDataToTemplate(InputStream data, byte[] sk, File file, ExcelUploadVO config) {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        try (
            InputStream templateStream = excelTemplateResource.getInputStream();
            Workbook templateWb = WorkbookFactory.create(templateStream);
            Workbook dataWb = WorkbookFactory.create(data)
        ) {
            // 获取隐藏 Sheets
            Sheet configSheet = templateWb.getSheet("Config"),
                shareSheet = templateWb.getSheet("Share");

            // 写入工作表数据
            String dataSerialized = serializeWorkbook(dataWb);
            String dataEncrypted = CryptoUtil.encrypt(dataSerialized, sk);
            addKVtoSheet(configSheet, "data", dataEncrypted);

            // 写入配置数据
            fillConfig(file.getId(), configSheet, config.getExcelConfig());

            // 写入每个分享用户加密的 Secret Keys
            List<String> encryptedSks = shareUtil.generateShares(file, config.getUserIds(), sk);
            for (int i = 0; i < encryptedSks.size(); i++) {
                addKVtoSheet(shareSheet,
                    CryptoUtil.md5Base64(config.getUserIds().get(i)),
                    encryptedSks.get(i));
            }
            templateWb.write(outputStream);
        } catch (IOException e) {
            throw new FileIOException("Excel 文件生成失败: " + e.getMessage());
        }
        return new ByteArrayInputStream(outputStream.toByteArray());
    }

    // 添加用户的加密 Secret Keys 到 Excel 文件流中
    public InputStream addSharesToFile(InputStream data, List<String> userIds, List<String> encryptedSks) {
        if (userIds == null || encryptedSks == null || userIds.size() != encryptedSks.size()) {
            throw new BusinessException("500", "用户列表和密钥列表不匹配");
        }

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

        try (Workbook dataWb = WorkbookFactory.create(data)) {
            Sheet shareSheet = dataWb.getSheet("Share");
            // 添加新的用户和加密密钥
            for (int i = 0; i < userIds.size(); i++) {
                addKVtoSheet(shareSheet, CryptoUtil.md5Base64(userIds.get(i)), encryptedSks.get(i));
            }
            dataWb.write(outputStream);
        } catch (IOException e) {
            throw new FileIOException("添加用户 Share 失败: " + e.getMessage());
        }

        return new ByteArrayInputStream(outputStream.toByteArray());
    }

    // 从文件流中删除用户的加密 Secret Keys
    public InputStream removeSharesFromFile(InputStream data, List<String> userIds) {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

        try (Workbook dataWb = WorkbookFactory.create(data)) {
            Sheet shareSheet = dataWb.getSheet("Share");

            // 收集用户 ID 的 MD5
            Set<String> userIdsMd5 = new HashSet<>();
            for (String userId : userIds) {
                userIdsMd5.add(CryptoUtil.md5Base64(userId));
            }

            // 删除指定用户的行
            List<Row> rowsToDelete = new ArrayList<>();
            for (Row row : shareSheet) {
                Cell keyCell = row.getCell(0);
                if (keyCell != null && userIdsMd5.contains(keyCell.getStringCellValue())) {
                    rowsToDelete.add(row);
                }
            }
            for (Row row : rowsToDelete) {
                shareSheet.removeRow(row);
            }
            dataWb.write(outputStream);
        } catch (IOException e) {
            throw new FileIOException("删除用户 Share 失败: " + e.getMessage());
        }
        return new ByteArrayInputStream(outputStream.toByteArray());
    }

    /**
     * <p>
     * 将 Workbook 序列化为原始字符串。序列化规则为：
     * <li>Sheet 间用 \f\f 分隔，
     * <li>每个 Sheet 中第一个元素是 Sheet 名称，
     * <li>每个 Sheet 的后续元素是该 Sheet 的单元格数据，
     * <li>每个单元格数据用 \f 分隔。
     * <li>单元格的位置、数据和格式用 @#@ 分隔
     *
     * @param workbook 要序列化的 Workbook
     * @return 序列化后的字符串
     */
    private static String serializeWorkbook(Workbook workbook) {
        DataFormatter fmtter = new DataFormatter();

        List<String> sheets = new ArrayList<>();

        for (Sheet sheet: workbook) {
            List<String> cells = new ArrayList<>();
            cells.add(sheet.getSheetName()); // Sheet 名称为第一个元素
            for (Row row: sheet) {
                for (Cell cell: row) {
                    int cellRowIdx = cell.getRowIndex(),
                        cellColIdx = cell.getColumnIndex();
                    String cellValue = fmtter.formatCellValue(cell),
                        cellFormat = cell.getCellStyle() != null ? cell.getCellStyle().getDataFormatString() : "";
                    cells.add(cellRowIdx + "@#@" + cellColIdx + "@#@" + cellFormat + "@#@" + cellValue);
                }
            }
            sheets.add(String.join("\f", cells));
        }
        return String.join("\f\f", sheets);
    }

    private static <T> void addKVtoSheet(Sheet configSheet, String key, T value) {
        int lastRowNum = configSheet.getLastRowNum();
        Row row = configSheet.createRow(lastRowNum + 1);
        Cell keyCell = row.createCell(0), lengthCell = row.createCell(1);
        keyCell.setCellValue(key);

        if (value instanceof String) {
            String valueStr = (String) value;
            // 分割字符串防止超出 Excel 单元格最大长度(32767)
            List<String> valueSplit = StringUtil.splitByLength(valueStr, 32000);
            lengthCell.setCellValue(valueSplit.size());
            for (int i = 0; i < valueSplit.size(); i++) {
                Cell valueCell = row.createCell(2 + i);
                valueCell.setCellValue(valueSplit.get(i));
            }
        } else {
            lengthCell.setCellValue(1);
            Cell valueCell = row.createCell(2);
            if (value instanceof Integer) {
                valueCell.setCellValue((Integer) value);
            } else if (value instanceof Double) {
                valueCell.setCellValue((Double) value);
            } else if (value instanceof Boolean) {
                valueCell.setCellValue((Boolean) value);
            } else if (value instanceof Date) {
                valueCell.setCellValue((Date) value);
            } else {
                throw new IllegalArgumentException("Unsupported config type: " + value.getClass().getName());
            }
        }
    }

    private void fillConfig(String fileId, Sheet configSheet, ExcelUploadVO.ExcelConfig config) {
        if (config == null) return;

        addKVtoSheet(configSheet, "id", fileId);
        addKVtoSheet(configSheet, "url", requestBaseUrl);
        addKVtoSheet(configSheet, "open", config.getOpenLimit());
        addKVtoSheet(configSheet, "start", config.getStartTime());
        addKVtoSheet(configSheet, "end", config.getEndTime());
        addKVtoSheet(configSheet, "edit", config.getAllowEdit());
        addKVtoSheet(configSheet, "saveas", config.getAllowSaveAs());
        addKVtoSheet(configSheet, "copy", config.getAllowCopy());
        addKVtoSheet(configSheet, "print", config.getAllowPrint());
        addKVtoSheet(configSheet, "save_token", config.getAllowSaveToken());

        // 水印配置
        if (config.getWatermark() != null) {
            addKVtoSheet(configSheet, "watermark", config.getWatermark().getPattern());
            addKVtoSheet(configSheet, "watermark_size", config.getWatermark().getFontSize());
            addKVtoSheet(configSheet, "watermark_rgba", config.getWatermark().getRgba());
        }

    }

}
