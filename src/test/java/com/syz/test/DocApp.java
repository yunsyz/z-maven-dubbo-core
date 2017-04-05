package com.syz.test;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.util.List;

import com.syz.tool.doc.ClassScanner;
import com.syz.tool.doc.ConfigurationConvertor;
/**
 * md转换工具下载地址：https://github.com/jgm/pandoc/releases/download/1.19.1/pandoc-1.19.1-windows.msi
 * cmd命令：pandoc -f markdown_github -t docx d:/data/test.md -o d:/data/test.docx 
 * @author yunsyz
 *
 */
public class DocApp {
    public static void main(String[] args) throws Exception {
    	String resourceRoot = "src/main/java";
        ClassScanner scanner = new ClassScanner(resourceRoot);
        StringBuilder sb = new StringBuilder();
        List<String> list = ConfigurationConvertor.convert("com.syz.service",
                null);
        for (String clazzName : list) {
            Class<?> clazz = Class.forName(clazzName);
            sb.append(scanner.scan(clazz, false));
        }
        String str = sb.toString();
        System.out.println(str);
        // String filePath = "d:/data/test.md";
        // writeToFile(str, filePath);
    }

    public static void writeToFile(String str, String filePath) {
        try {
            File file = new File(filePath);
            ByteArrayInputStream bis = new ByteArrayInputStream(str.getBytes());
            FileOutputStream fos = new FileOutputStream(file);
            byte[] b = new byte[1024];
            int n = 0;
            while ((n = bis.read(b)) > -1) {
                fos.write(b, 0, n);
            }
            fos.close();
            bis.close();
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }
}