package com.hector.gdriveconsumerservice.demo.controller;

import com.hector.gdriveconsumerservice.demo.entity.ObjectMetadata;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
public class UploadFileController {
    @PostMapping("/files/upload")
    public ResponseEntity<ObjectMetadata> uploadFile(@RequestParam("file") MultipartFile file) {
        // Logic to upload the file
        return ResponseEntity.status(HttpStatus.CREATED).body(ObjectMetadata.builder().build());
    }
}
