package com.fdc.serviceImpl.file.excelUtils;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.fdc.util.CryptoUtil;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;

import java.nio.charset.StandardCharsets;



public class ExcelSerializer {

    /**
     * 将 Workbook 序列化为 Json 格式的字符串。
     *
     * @param workbook 要序列化的 Workbook
     * @return 序列化后的 Json 字符串
     */
    static String serializeWorkbook(Workbook workbook) {
        JSONArray sheetsJson = new JSONArray();

        for (int i = 0; i < workbook.getNumberOfSheets(); i++) {
            Sheet sheet = workbook.getSheetAt(i);
            JSONObject sheetJson = new JSONObject();

            // Sheet 名
            sheetJson.put("name", sheet.getSheetName());
            // 行高列宽
            serializeRowAndCol(sheetJson, sheet);
            // 单元格数据
            serializeCells(sheetJson, sheet, workbook);
            // 合并单元格
            serializeMergedRegions(sheetJson, sheet);

            sheetsJson.add(sheetJson);
        }

        return sheetsJson.toJSONString();
    }

    private static void serializeRowAndCol(JSONObject sheetJson, Sheet sheet) {
        JSONArray colWidths = new JSONArray(),
            rowHeights = new JSONArray();
        int maxCol = 0;
        for (int row = 0; row <= sheet.getLastRowNum(); row++) {
            Row r = sheet.getRow(row);
            if (r != null) {
                int height = r.getHeight();
                if (height != sheet.getDefaultRowHeight()) {
                    JSONObject rowHeight = new JSONObject();
                    rowHeight.put("row", row);
                    rowHeight.put("height", height);
                    rowHeights.add(rowHeight);
                }
                maxCol = Math.max(maxCol, r.getLastCellNum());
            }
        }

        for (int col = 0; col < maxCol; col++) {
            int width = sheet.getColumnWidth(col);
            if (width != sheet.getDefaultColumnWidth() * 256) {
                JSONObject colWidth = new JSONObject();
                colWidth.put("col", col);
                colWidth.put("width", width);
                colWidths.add(colWidth);
            }
        }

        sheetJson.put("colWidths", colWidths);
        sheetJson.put("rowHeights", rowHeights);
    }

    private static void serializeCells(JSONObject sheetJson, Sheet sheet, Workbook workbook) {
        JSONArray cells = new JSONArray();
        for (Row row: sheet) {
            for (Cell cell : row) {
                JSONObject cellJson = new JSONObject();
                // 位置和内容
                cellJson.put("row", cell.getRowIndex());
                cellJson.put("col", cell.getColumnIndex());
                cellJson.put("value", getCellValue(cell));

                CellStyle defaultStyle = workbook.getCellStyleAt(0);
                CellStyle style = cell.getCellStyle();
                if (style != null && style != defaultStyle) {
                    JSONObject styleJson = new JSONObject();
                    putIfNotDefault(styleJson, "format", style.getDataFormatString(), defaultStyle.getDataFormatString());
                    putIfNotDefault(styleJson, "align", style.getAlignment().name(), defaultStyle.getAlignment().name());
                    putIfNotDefault(styleJson, "valign", style.getVerticalAlignment().name(), defaultStyle.getVerticalAlignment().name());

                    putIfNotDefault(styleJson, "borderTop", style.getBorderTop().name(), defaultStyle.getBorderTop().name());
                    putIfNotDefault(styleJson, "borderBottom", style.getBorderBottom().name(), defaultStyle.getBorderBottom().name());
                    putIfNotDefault(styleJson, "borderLeft", style.getBorderLeft().name(), defaultStyle.getBorderLeft().name());
                    putIfNotDefault(styleJson, "borderRight", style.getBorderRight().name(), defaultStyle.getBorderRight().name());

                    putIfNotDefault(styleJson, "fillColor", style.getFillForegroundColor(), defaultStyle.getFillForegroundColor());

                    Font font = workbook.getFontAt(style.getFontIndex()),
                        defaultFont = workbook.getFontAt(defaultStyle.getFontIndex());
                    JSONObject fontJson = new JSONObject();
                    putIfNotDefault(fontJson, "name", font.getFontName(), defaultFont.getFontName());
                    putIfNotDefault(fontJson, "size", font.getFontHeightInPoints(), defaultFont.getFontHeightInPoints());
                    putIfNotDefault(fontJson, "bold", font.getBold(), defaultFont.getBold());
                    putIfNotDefault(fontJson, "italic", font.getItalic(), defaultFont.getItalic());
                    putIfNotDefault(fontJson, "underline", font.getUnderline() != Font.U_NONE, defaultFont.getUnderline() != Font.U_NONE);
                    putIfNotDefault(fontJson, "strikeout", font.getStrikeout(), defaultFont.getStrikeout());
                    putIfNotDefault(fontJson, "color", font.getColor(), defaultFont.getColor());

                    if (!fontJson.isEmpty()) styleJson.put("font", fontJson);
                    if(!styleJson.isEmpty()) cellJson.put("style", styleJson);
                }
                cells.add(cellJson);
            }
        }
        sheetJson.put("cells", cells);
    }

    private static void serializeMergedRegions(JSONObject sheetJson, Sheet sheet) {
        int total = sheet.getNumMergedRegions();
        JSONArray mergedRegions = new JSONArray();
        for (int i = 0; i < total; i++) {
            CellRangeAddress range = sheet.getMergedRegion(i);
            if (range != null) {
                JSONObject merged = new JSONObject();
                merged.put("firstRow", range.getFirstRow());
                merged.put("lastRow", range.getLastRow());
                merged.put("firstCol", range.getFirstColumn());
                merged.put("lastCol", range.getLastColumn());
                mergedRegions.add(merged);
            }
        }
        sheetJson.put("mergedRegions", mergedRegions);
    }

    private static Object getCellValue(Cell cell) {
        JSONObject cellValue = new JSONObject();
        switch (cell.getCellType()) {
            case STRING:
                String b64 = CryptoUtil.base64Encode(
                    cell.getStringCellValue()
                        .getBytes(StandardCharsets.UTF_8));
                cellValue.put("type", "b64");
                cellValue.put("value", b64);
                return cellValue;
            case NUMERIC:
                cellValue.put("type", "raw");
                if (DateUtil.isCellDateFormatted(cell)) {
                    cellValue.put("value", cell.getDateCellValue().getTime());
                } else {
                    cellValue.put("value", cell.getNumericCellValue());
                }
                return cellValue;
            case BOOLEAN:
                cellValue.put("type", "raw");
                cellValue.put("value", cell.getBooleanCellValue());
                return cellValue;
            case FORMULA:
                cellValue.put("type", "formula");
                cellValue.put("value", cell.getCellFormula());
                return cellValue;
            case BLANK:
            default:
                cellValue.put("type", "blank");
                return cellValue;
        }
    }

    private static void putIfNotDefault(JSONObject json, String key, Object value, Object defaultValue) {
        if (value != null && !value.equals(defaultValue)) {
            json.put(key, value);
        }
    }
}
