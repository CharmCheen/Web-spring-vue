package com.example.exp2.util;

import org.springframework.stereotype.Component;
import java.util.Base64;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.UUID;

@Component
public class ImageUtil {
    
    private static final String UPLOAD_DIR = "uploads/avatars/";
    
    public String saveBase64Image(String base64Image) throws IOException {
        // 确保上传目录存在
        File uploadDir = new File(UPLOAD_DIR);
        if (!uploadDir.exists()) {
            uploadDir.mkdirs();
        }
        
        // 生成唯一文件名
        String fileName = UUID.randomUUID().toString() + ".jpg";
        String filePath = UPLOAD_DIR + fileName;
        
        // 解码Base64并保存文件
        try {
            String base64Data = base64Image.split(",")[1];
            byte[] imageBytes = Base64.getDecoder().decode(base64Data);
            
            try (FileOutputStream fos = new FileOutputStream(filePath)) {
                fos.write(imageBytes);
            }
            
            return fileName;
        } catch (Exception e) {
            throw new IOException("保存图片失败: " + e.getMessage());
        }
    }
    
    public void deleteImage(String fileName) {
        if (fileName != null && !fileName.equals("default.jpg")) {
            File file = new File(UPLOAD_DIR + fileName);
            if (file.exists()) {
                file.delete();
            }
        }
    }
} 