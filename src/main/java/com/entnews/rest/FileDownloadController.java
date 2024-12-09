package com.entnews.rest;

import org.springframework.core.io.FileSystemResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.io.File;
import java.nio.file.Paths;

@RestController
public class FileDownloadController {

    // 这里假设文件所在的根目录，你可以根据实际情况修改
    private static final String FILES_DIRECTORY = "/home/entnews/newsfiles/";

    // 定义一个接口方法，用于根据文件名下载文件，这里使用路径变量来接收文件名
    @GetMapping("/download/{fileName}")
    public ResponseEntity<FileSystemResource> downloadFile(@PathVariable("fileName") String fileName) {
        try {
            // 拼接文件的完整路径
            String filePath = Paths.get(FILES_DIRECTORY, fileName).toString();
            File file = new File(filePath);
            if (file.exists()) {
                // 创建一个FileSystemResource对象，用于表示要下载的文件资源
                FileSystemResource resource = new FileSystemResource(file);
                // 设置响应头信息，包括文件名、文件类型等
                HttpHeaders headers = new HttpHeaders();
                headers.add("Content-Disposition", "attachment; filename=" + file.getName());
                headers.add("Cache-Control", "no-cache, no-store, must-revalidate");
                headers.add("Pragma", "no-cache");
                headers.add("Expires", "0");
                // 根据文件的扩展名来设置合适的媒体类型（MIME类型），这里只是简单示例，可进一步完善
                String contentType = getContentType(file.getName());
                if (contentType!= null) {
                    headers.setContentType(MediaType.parseMediaType(contentType));
                }
                // 返回包含文件资源、响应头以及状态码的ResponseEntity对象，实现文件下载
                return ResponseEntity.ok()
                        .headers(headers)
                        .body(resource);
            } else {
                // 如果文件不存在，返回相应的错误提示，可以根据实际需求调整返回内容和状态码
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            // 如果出现异常，也返回相应的错误提示，可进一步优化错误处理逻辑
            e.printStackTrace();
            return ResponseEntity.status(500).build();
        }
    }

    // 简单方法用于根据文件名获取大致的媒体类型（MIME类型），可完善
    private String getContentType(String fileName) {
        if (fileName.endsWith(".pdf")) {
            return "application/pdf";
        } else if (fileName.endsWith(".txt")) {
            return "text/plain";
        } else if (fileName.endsWith(".jpg") || fileName.endsWith(".jpeg")) {
            return "image/jpeg";
        }
        // 其他文件类型可继续添加判断逻辑，若无法确定返回null
        return null;
    }
}
