package com.hector.gdriveconsumerservice.demo.controller;

import com.hector.gdriveconsumerservice.demo.entity.ObjectMetadata;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class ListFolderController {

    @GetMapping("/files")
    public ResponseEntity<List<ObjectMetadata>> listFolder(@RequestParam(required = false, defaultValue="root") String folderId) {
        return ResponseEntity.ok(List.of(
                ObjectMetadata.builder().name("fake").type("text").build(),
                ObjectMetadata.builder().name("fake2").type("text").build(),
                ObjectMetadata.builder().name("fake3").type("text").build(),
                ObjectMetadata.builder().name("Folder1").type("folder").build()
        ));
    }
}
