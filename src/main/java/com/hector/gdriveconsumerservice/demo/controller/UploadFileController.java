package com.hector.gdriveconsumerservice.demo.controller;

import com.hector.gdriveconsumerservice.demo.component.GoogleDriveComponent;
import com.hector.gdriveconsumerservice.demo.entity.ObjectMetadata;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@Slf4j
@RestController
@RequiredArgsConstructor
public class UploadFileController {

    private final GoogleDriveComponent driveComponent;

    @PostMapping("/upload")
    public ResponseEntity<ObjectMetadata> uploadFile(@RequestParam("file") MultipartFile file) throws IOException {
        log.info("Entering uploadFile API rest endpoint...");
        final ObjectMetadata response = driveComponent.uploadFile(file);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
}
