package com.example.demo.Service;


import org.apache.poi.xssf.usermodel.XSSFRow;
import org.springframework.stereotype.Service;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

@Service
public class Exercise1Service {

    private static final String UPLOAD_DIR = "C:\\Users\\2144296\\OneDrive - Cognizant\\Desktop\\";
    private final Logger logger = Logger.getLogger(Exercise1Service.class.getName());

    public String processExcelFile(String filePath) throws IOException {
        if (filePath.isEmpty()) {
            return "File path cannot be empty.";
        }


        try (Workbook workbook = new XSSFWorkbook(new FileInputStream(filePath))) {



            Sheet sheet = workbook.getSheetAt(0);

            // Sort the Excel by the first column in descending order
            sortColumnDescending(sheet, 0);

            // Get current date and time for renaming the file
            String currentDateAndTime = getCurrentDateAndTime();
            String originalFilename = new File(filePath).getName();
            String renamedFilename = "sorted_excel_" + currentDateAndTime + "_" + originalFilename;

            // Save the sorted Excel file with the renamed filename
            try (FileOutputStream fos = new FileOutputStream(UPLOAD_DIR + renamedFilename)) {
                workbook.write(fos);
                return "Excel file processed, sorted, and renamed successfully: " + renamedFilename;
            } catch (IOException e) {
                return "Failed to save the processed Excel file: " + e.getMessage();
            }
        } catch (IOException e) {
            return "Failed to process the Excel file: " + e.getMessage();
        }
    }

    private TreeMap<Double,String> readFirstColumn(Sheet sheet) {
        TreeMap<Double,String> columnData = new TreeMap<>();
        int pointer = 0;
        logger.info(String.valueOf(sheet.getActiveCell().getRow()+" "+sheet.getActiveCell().getColumn()));
        for (int r = 0; r <= sheet.getLastRowNum(); r++) {
            Double key = (Double) sheet.getRow(r)
                    .getCell(0)
                    .getNumericCellValue();
            String value = sheet.getRow(r)
                    .getCell(1)
                    .getStringCellValue();
            columnData.put(key, value);
        }
        return columnData;
    }

    private void writeFirstColumn(Sheet sheet,  TreeMap<Double,String>  data) throws FileNotFoundException {
        int rowno=0;

        for(HashMap.Entry entry:data.entrySet()) {
            XSSFRow row= (XSSFRow) sheet.createRow(rowno++);
            row.createCell(0).setCellValue((Double)entry.getKey());
            row.createCell(1).setCellValue((String)entry.getValue());
        }

    }

    private void sortColumnDescending(Sheet sheet, int columnIndex) {
        DataFormatter dataFormatter = new DataFormatter();
        Comparator<Row> comparator = (row1, row2) -> {
            Cell cell1 = row1.getCell(columnIndex);
            Cell cell2 = row2.getCell(columnIndex);

            String str1 = dataFormatter.formatCellValue(cell1);
            String str2 = dataFormatter.formatCellValue(cell2);

            // Convert string values to integers for numeric sorting
            Integer value1 = tryParseInt(str1);
            Integer value2 = tryParseInt(str2);

            // Handle null or non-numeric values by placing them at the end
            if (value1 != null && value2 != null) {
                return value2.compareTo(value1); // Compare integers in descending order
            } else if (value1 == null && value2 == null) {
                return 0; // Both values are non-numeric, treat as equal
            } else if (value1 == null) {
                return 1; // Non-numeric value should come after numeric value
            } else {
                return -1; // Non-numeric value should come before numeric value
            }
        };

        List<Row> rows = new ArrayList<>();
        sheet.forEach(rows::add);

       // rows.remove(0); // Remove header row for sorting

        rows.sort(comparator);

//        // Clear existing rows except for the header row
//        int lastRowNum = sheet.getLastRowNum();
//        for (int i = lastRowNum; i > 0; i--) {
//            sheet.removeRow(sheet.getRow(i));
//
//        }

        // Add sorted rows back to the sheet
        for (Row row : rows) {
            Row newRow = sheet.createRow(sheet.getLastRowNum() + 1);
            for (int i = 0; i < row.getLastCellNum(); i++) {
                Cell oldCell = row.getCell(i);
                Cell newCell = newRow.createCell(i, oldCell.getCellType());
                newCell.setCellValue(dataFormatter.formatCellValue(oldCell));
            }
        }
    }

    private Integer tryParseInt(String str) {
        try {
            return Integer.parseInt(str);
        } catch (NumberFormatException e) {
            return null; // Return null if parsing fails
        }
    }

    private String getCurrentDateAndTime() {
        LocalDateTime now = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd_HH-mm-ss");
        return now.format(formatter);

    }
}
