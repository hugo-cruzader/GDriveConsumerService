package com.hector.gdriveconsumerservice.demo.controller;

import com.hector.gdriveconsumerservice.demo.component.GoogleDriveComponent;
import com.hector.gdriveconsumerservice.demo.entity.DownloadableResource;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

@Slf4j
@RestController
@RequiredArgsConstructor
public class DownloadFileController {

    private final GoogleDriveComponent driveComponent;

    /**
     * Entrypoint to the downloadFile operation.
     * @param fileId the Google identifier on G Drive to initiate a download.
     * @return the file content to be processed/downloaded by the calling entity.
     * @throws IOException if drive component presents an issue.
     */
    @GetMapping("/download/{fileId}")
    public ResponseEntity<Resource> downloadFile(@PathVariable String fileId) throws IOException {
        log.info("Entering downloadFile API rest endpoint...");
        final DownloadableResource downloadedResource = driveComponent.downloadFile(fileId);
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + downloadedResource.fileName() + "\"")
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .body(downloadedResource.content());
    }
}
