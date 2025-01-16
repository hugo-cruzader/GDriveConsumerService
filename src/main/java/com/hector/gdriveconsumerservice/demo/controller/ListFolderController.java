package com.hector.gdriveconsumerservice.demo.controller;

import com.hector.gdriveconsumerservice.demo.entity.ObjectMetadata;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Date;
import java.util.List;

@RestController
public class ListFolderController {

    @GetMapping("/files")
    public ResponseEntity<List<ObjectMetadata>> listFolder(@RequestParam(name="folder", defaultValue="/") String path) {
        return ResponseEntity.ok(List.of(
                        ObjectMetadata.builder().name("fake").type("text").lastModifiedDate(new Date()).build(),
                        ObjectMetadata.builder().name("fake2").type("text").lastModifiedDate(new Date()).build(),
                        ObjectMetadata.builder().name("fake3").type("text").lastModifiedDate(new Date()).build(),
                        ObjectMetadata.builder().name("Folder1").type("folder").lastModifiedDate(new Date()).build()
                ));
    }
}
