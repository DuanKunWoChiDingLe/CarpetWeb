package com.daocheng.api.common.controller;

import com.daocheng.api.common.dto.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.UUID;

@Slf4j
@RestController
@RequestMapping("/api/upload")
public class UploadController {

    @Value("${file.upload-path:./uploads}")
    private String uploadPath;

    @PostMapping("/image")
    public Result<UploadResponse> uploadImage(@RequestParam("file") MultipartFile file) {
        if (file.isEmpty()) {
            return Result.error(400, "文件不能为空");
        }

        // 检查文件类型
        String originalFilename = file.getOriginalFilename();
        String suffix = originalFilename.substring(originalFilename.lastIndexOf("."));
        if (!suffix.matches("\\.(jpg|jpeg|png|gif|webp)$")) {
            return Result.error(400, "仅支持 jpg、jpeg、png、gif、webp 格式的图片");
        }

        // 生成唯一文件名
        String newFileName = UUID.randomUUID().toString().replace("-", "") + suffix;
        String datePath = new java.text.SimpleDateFormat("yyyyMMdd").format(new java.util.Date());
        String fullPath = uploadPath + File.separator + datePath;

        File dir = new File(fullPath);
        if (!dir.exists() && !dir.mkdirs()) {
            log.error("创建目录失败：{}", fullPath);
            return Result.error(500, "服务器存储失败");
        }

        File dest = new File(dir, newFileName);
        try {
            file.transferTo(dest);
        } catch (IOException e) {
            log.error("文件保存失败", e);
            return Result.error(500, "文件上传失败");
        }

        String relativePath = "/uploads/" + datePath + "/" + newFileName;
        return Result.success(new UploadResponse(relativePath));
    }

    // 内部响应类
    static class UploadResponse {
        private String url;
        public UploadResponse(String url) { this.url = url; }
        public String getUrl() { return url; }
    }
}