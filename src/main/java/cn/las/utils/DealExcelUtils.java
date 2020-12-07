package cn.las.utils;

import jxl.Cell;
import jxl.Sheet;
import jxl.Workbook;
import jxl.WorkbookSettings;
import jxl.read.biff.BiffException;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class DealExcelUtils {

    public synchronized static void excelToCsv(String oldFileName, String newFileName) {
        StringBuffer buffer = new StringBuffer();
        try {
            // 读取原始的文件
            File file = new File(oldFileName);
            // 设置读文件编码
            WorkbookSettings setEncode = new WorkbookSettings();
            setEncode.setEncoding("GB2312");
            // 从文件流中获取Excel工作区对象（WorkBook）
            Workbook wb = Workbook.getWorkbook(file,setEncode);
            Sheet sheet = wb.getSheet(0);

            for (int i = 0; i < sheet.getRows(); i++) {
                for (int j = 0; j < sheet.getColumns(); j++) {
                    Cell cell = sheet.getCell(j, i);
                    // 替换单元当中的数据，替换换行符，并且单元格之间添加一个逗号
                    buffer.append(cell.getContents().replaceAll("\n", " ")).append(",");
                }
                // 删除最后的逗号 之后加上换行符
                buffer.deleteCharAt(buffer.length() - 1).append("\n");
            }
        } catch (BiffException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        // 存储到新的csv文件当中
        File saveCSV = new File(newFileName);
        try {
            // 如果文件不存在，创建新的文件
            if(!saveCSV.exists()) {
                saveCSV.createNewFile();
            }
            // 使用字符流进行文件的写入
            BufferedWriter writer = new BufferedWriter(new FileWriter(saveCSV));
            writer.write(buffer.toString());
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
