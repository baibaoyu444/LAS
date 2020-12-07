package cn.las.utils;

import org.apache.log4j.Logger;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;

public class FileProcessUtils {

    private static final Logger logger = Logger.getLogger(FileProcessUtils.class);

    public static String upload(MultipartFile multipartFile) {
        // 获取返回类型
        String contentType = multipartFile.getContentType();
        logger.info("contextType = " + contentType);
        // 获取文件的原始类型
        String originalFilename = multipartFile.getOriginalFilename();
        logger.info("originalFilename = " + originalFilename);

        if(originalFilename == null) return null;

        // 拼接文件保存路径
        String dir = new StringBuffer().append("F:").append(File.separator).append("excel").toString();
        // 进行文件存储
        File file = new File(dir);
        // 如果文件目录不存在，创建文件
        if(!file.exists()) {
            file.mkdirs();
        }

        // 获取新的文件的名称
        String newFileName = new StringBuffer().append(dir)
                .append(File.separator).append(originalFilename).append(".")
                .append(originalFilename.substring(originalFilename.lastIndexOf(".") + 1))
                .toString();

        InputStream input = null;
        OutputStream output = null;

        // 使用文件复制工具进行文件的传输
        try {
            input = multipartFile.getInputStream();
            output = new FileOutputStream(newFileName);

            FileCopyUtils.copy(input, output);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if(input != null) {
                try {
                    input.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if(input != null) {
                try {
                    input.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return newFileName;
    }
}
