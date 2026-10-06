package utilities;

import java.io.FileInputStream;
import java.io.IOException;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

public class ExcelReader {

    public static String getCellData(String filePath,
                                     String sheetName,
                                     int rowNum,
                                     int colNum) {

        try (FileInputStream fis = new FileInputStream(filePath);
             Workbook workbook = new XSSFWorkbook(fis)) {

            Sheet sheet = workbook.getSheet(sheetName);
            DataFormatter formatter = new DataFormatter();

            return formatter.formatCellValue(
                    sheet.getRow(rowNum).getCell(colNum));

        } catch (IOException e) {
            e.printStackTrace();
        }

        return "";
    }

    public static String getCellDataByColumnName(String filePath,
                                                 String sheetName,
                                                 int rowNum,
                                                 String columnName) {

        try (FileInputStream fis = new FileInputStream(filePath);
             Workbook workbook = new XSSFWorkbook(fis)) {

            Sheet sheet = workbook.getSheet(sheetName);
            Row headerRow = sheet.getRow(0);

            for (Cell cell : headerRow) {

                if (cell.getStringCellValue()
                        .trim()
                        .equalsIgnoreCase(columnName)) {

                    int columnIndex = cell.getColumnIndex();

                    DataFormatter formatter = new DataFormatter();

                    return formatter.formatCellValue(
                            sheet.getRow(rowNum)
                                 .getCell(columnIndex));
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return "";
    }
}