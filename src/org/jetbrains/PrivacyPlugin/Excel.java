package org.jetbrains.PrivacyPlugin;
import javafx.scene.control.Cell;
import org.apache.poi.ss.formula.functions.Column;
import org.apache.poi.ss.usermodel.FormulaEvaluator;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import java.io.*;
// Reads from excel file and writes to csv file after filtering
public class Excel {
    public void readWrite() throws IOException {
        String comma=",";
        String newLine="\n";
        FileWriter fileWriter= new FileWriter("D:\\PhraseApi.csv");
        fileWriter.append("phrase,api");
        FileInputStream file= new FileInputStream("D:\\mappings_frequencyAndGoogle.xlsx");
        XSSFWorkbook workbook= new XSSFWorkbook(file);
        XSSFSheet sheet=workbook.getSheet("apiToPhrases");
        int rows=sheet.getLastRowNum();
        int columns=sheet.getRow(0).getLastCellNum()-1;
        for(int i=1;i<=rows;i++){
            double cell=sheet.getRow(i).getCell(columns).getNumericCellValue();
            if(cell==1.0){
                fileWriter.append(newLine);
                fileWriter.append(String.valueOf(sheet.getRow(i).getCell(columns - 7)));
                fileWriter.append(comma);
                fileWriter.append(String.valueOf(sheet.getRow(i).getCell(columns-6)));
                fileWriter.flush();
            }
        }
    }
}

