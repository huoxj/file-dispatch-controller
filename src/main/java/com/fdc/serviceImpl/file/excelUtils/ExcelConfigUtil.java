package com.fdc.serviceImpl.file.excelUtils;

import com.fdc.util.StringUtil;
import org.apache.poi.ss.usermodel.*;
import java.util.Date;
import java.util.List;

public class ExcelConfigUtil {

    static <T> void addKVtoSheet(Sheet configSheet, String key, T value) {
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

}
