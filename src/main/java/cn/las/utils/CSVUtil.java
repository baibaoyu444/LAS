package cn.las.utils;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.List;

/**
 * 
 * CSV文件导出工具类
 * 
 * Created on 2017-01-07
 */
public class CSVUtil {

    /**
     * CSV文件生成方法
     * @param head
     * @param lsNew
     * @param outPutPath
     * @param filename
     */
    public static File createCSVFile(Object[] head, List<Object[]> lsNew,
            String outPutPath, String filename) {

        File csvFile = null;
        BufferedWriter csvWtriter = null;
        try {
            csvFile = new File(outPutPath + File.separator + filename + ".csv");
            File parent = csvFile.getParentFile();
            if (parent != null && !parent.exists()) {
                parent.mkdirs();
            }
            csvFile.createNewFile();

            // GB2312使正确读取分隔符","
            csvWtriter = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(
                    csvFile), "GB2312"), 1024);
            // 写入文件头部
            writeRow(head, csvWtriter);

            // 写入文件内容
            if (lsNew != null && lsNew.size() != 0){
                for (Object[] row : lsNew) {
                    writeRow(row, csvWtriter);
                }
                csvWtriter.flush();
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                csvWtriter.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return csvFile;
    }
    
    public static File createCSVFile(Object[] head, List<Object[]> lsNew,
            String Path) {

        File csvFile = null;
        BufferedWriter csvWtriter = null;
        try {
            csvFile = new File(Path);
            File parent = csvFile.getParentFile();
            if (parent != null && !parent.exists()) {
                parent.mkdirs();
            }
            csvFile.createNewFile();

            // GB2312使正确读取分隔符","
            csvWtriter = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(
                    csvFile), "GB2312"), 1024);
            // 写入文件头部
            writeRow(head, csvWtriter);

            // 写入文件内容
            if (lsNew != null && lsNew.size() != 0){
                for (Object[] row : lsNew) {
                    writeRow(row, csvWtriter);
                }
                csvWtriter.flush();
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                csvWtriter.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return csvFile;
    }
    
    
    /**
     * 写一行数据方法
     * @param row
     * @param csvWriter
     * @throws IOException
     */
    private static void writeRow(Object[] row, BufferedWriter csvWriter) throws IOException {
        // 写入文件头部
        for (Object data : row) {
            StringBuffer sb = new StringBuffer();
            String rowStr = sb.append("\"").append(data).append("\",").toString();
            csvWriter.write(rowStr);
        }
        csvWriter.newLine();
    }

    /** 
     * 导入 
     *  
     * @param file csv文件(路径+文件) 
     * @return 
     */  
    public static List<String> importCsv(File file){  
        List<String> dataList=new ArrayList<String>();  
          
        BufferedReader br=null;  
        try {   
            br = new BufferedReader(new FileReader(file));  
            String line = "";   
            while ((line = br.readLine()) != null) {   
                dataList.add(line);  
            }  
        } catch (Exception e) {
        	e.printStackTrace();
        } finally{
            if(br!=null){  
                try {  
                    br.close();  
                    br=null;  
                } catch (IOException e) {  
                    e.printStackTrace();  
                }  
            }  
        }  
   
        return dataList;  
    }  

}
