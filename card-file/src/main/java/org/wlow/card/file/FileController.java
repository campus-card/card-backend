package org.wlow.card.file;

import jakarta.annotation.Resource;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.wlow.card.data.data.DTO.Response;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

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

    @GetMapping("/download/{fileId}")
    public ResponseEntity<InputStreamResource> downloadStream(@PathVariable("fileId") Integer fileId) throws FileNotFoundException {
        return fileService.downloadFile(fileId);
    }
}
