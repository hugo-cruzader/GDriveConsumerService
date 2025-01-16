package com.hector.gdriveconsumerservice.demo.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class DeleteFileController {
    @DeleteMapping("/files/{fileId}/delete")
    public ResponseEntity<Void> deleteFile(@PathVariable String fileId) {
        return ResponseEntity.noContent().build();
    }
}
