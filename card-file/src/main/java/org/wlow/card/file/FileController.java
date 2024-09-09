package org.wlow.card.file;

import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.wlow.card.data.data.DTO.Response;

@RestController
@RequestMapping("/fileService")
public class FileController {
    @Resource
    private FileService fileService;

    @PostMapping("/uploadFile")
    public Response<String> upload(@RequestParam MultipartFile file) {
        return fileService.putFile(file);
    }

    @PostMapping("/uploadImage")
    public Response<String> uploadImage(@RequestParam MultipartFile image) {
        return fileService.putImage(image);
    }
}
