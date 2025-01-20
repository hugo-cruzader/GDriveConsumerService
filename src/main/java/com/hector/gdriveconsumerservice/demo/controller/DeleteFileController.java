package com.hector.gdriveconsumerservice.demo.controller;

import com.hector.gdriveconsumerservice.demo.component.GoogleDriveComponent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

@Slf4j
@RestController
@RequiredArgsConstructor
public class DeleteFileController {

    private final GoogleDriveComponent driveComponent;

    /**
     * Entrypoint for the deleteFile operation.
     * @param fileId the identifier in Google Drive of the element to delete.
     * @return An empty successful response if the element has been deleted correctly.
     * @throws IOException if drive component presents an issue.
     */
    @DeleteMapping(value ={"/delete/{fileId}"})
    public ResponseEntity<Void> deleteFile(@PathVariable String fileId) throws IOException {
        log.info("Entering deleteFile API rest endpoint...");
        driveComponent.deleteFile(fileId);
        return ResponseEntity.noContent().build();
    }
}
