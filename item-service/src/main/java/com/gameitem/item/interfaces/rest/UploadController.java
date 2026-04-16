package com.gameitem.item.interfaces.rest;

import com.gameitem.common.api.ApiResult;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

@RestController
@RequestMapping("/api")
public class UploadController {

    private static final String UPLOAD_DIR = "D:/ProjectPhoto/";

    @PostMapping("/upload")
    public ApiResult<String> upload(@RequestParam("file") MultipartFile file) {
        if (file.isEmpty()) {
            return ApiResult.fail(1, "上传文件不能为空");
        }

        String originalFilename = file.getOriginalFilename();
        if (originalFilename == null) {
            return ApiResult.fail(1, "文件名无效");
        }

        String suffix = originalFilename.substring(originalFilename.lastIndexOf("."));
        if (!suffix.equalsIgnoreCase(".jpg") && !suffix.equalsIgnoreCase(".jpeg")
            && !suffix.equalsIgnoreCase(".png")) {
            return ApiResult.fail(1, "只能上传 JPG/PNG 格式的图片");
        }

        long fileSize = file.getSize();
        if (fileSize > 2 * 1024 * 1024) {
            return ApiResult.fail(1, "图片大小不能超过 2MB");
        }

        try {
            File uploadDir = new File(UPLOAD_DIR);
            if (!uploadDir.exists()) {
                uploadDir.mkdirs();
            }

            String newFileName = UUID.randomUUID().toString() + suffix;
            Path filePath = Paths.get(UPLOAD_DIR, newFileName);
            Files.copy(file.getInputStream(), filePath);

            String url = filePath.toString();
            return ApiResult.ok(url);
        } catch (IOException e) {
            e.printStackTrace();
            return ApiResult.fail(1, "上传失败: " + e.getMessage());
        }
    }

    @GetMapping("/uploads/{filename}")
    public byte[] getFile(@PathVariable String filename) throws IOException {
        Path filePath = Paths.get(UPLOAD_DIR, filename);
        File file = filePath.toFile();
        if (file.exists()) {
            return Files.readAllBytes(filePath);
        }
        return new byte[0];
    }

    @GetMapping(value = "/uploads/img/{filename}", produces = MediaType.IMAGE_JPEG_VALUE)
    public byte[] getImage(@PathVariable String filename) throws IOException {
        Path filePath = Paths.get(UPLOAD_DIR, filename);
        File file = filePath.toFile();
        if (file.exists()) {
            return Files.readAllBytes(filePath);
        }
        return new byte[0];
    }

    @GetMapping(value = "/uploads/png/{filename}", produces = MediaType.IMAGE_PNG_VALUE)
    public byte[] getPngImage(@PathVariable String filename) throws IOException {
        Path filePath = Paths.get(UPLOAD_DIR, filename);
        File file = filePath.toFile();
        if (file.exists()) {
            return Files.readAllBytes(filePath);
        }
        return new byte[0];
    }
}
