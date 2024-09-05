package org.wlow.card.file;

import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.wlow.card.data.data.DTO.Response;

@RestController
@RequestMapping("/file-service")
public class FileController {
    @Resource
    private FileService fileService;

    @PostMapping("/upload")
    public Response upload(@RequestParam MultipartFile file) {
        return fileService.putFile(file);
    }
}
