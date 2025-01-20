package com.hector.gdriveconsumerservice.demo.controller;

import com.google.api.services.drive.model.FileList;
import com.hector.gdriveconsumerservice.demo.component.GoogleDriveComponent;
import com.hector.gdriveconsumerservice.demo.entity.ObjectMetadata;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
public class ListFolderController {
    private final GoogleDriveComponent driveComponent;

    /**
     * Provides the entrypoint for the listfolder operation, at the moment the list is exhaustively and retrieves all files in the drive.
     * @param folderId variable to control which folder to open.
     * @return a {@link List} of {@link ObjectMetadata} containing all folders and files from the user G Drive.
     * @throws IOException if drive component presents an issue.
     */
    @GetMapping(value = {"/listfolder"})
    public ResponseEntity<List<ObjectMetadata>> listFolder(@RequestParam(required = false, defaultValue="root") String folderId)
            throws IOException {
        log.info("Entering listFolder API rest endpoint...");
        final FileList fileList = driveComponent.getFiles();
        return ResponseEntity.ok(fileList.getFiles().stream()
                .map(file-> ObjectMetadata.builder()
                        .id(file.getId())
                        .name(file.getName())
                        .lastModifiedDate(file.getModifiedTime())
                        .type(cleanGoogleMimeTypes(file.getMimeType()))
                        .build())
                .toList()
        );
    }
    private String cleanGoogleMimeTypes(final String mimeType) {
        return mimeType.replaceAll("application/vnd\\.google-apps\\.","Google ");
    }
}
