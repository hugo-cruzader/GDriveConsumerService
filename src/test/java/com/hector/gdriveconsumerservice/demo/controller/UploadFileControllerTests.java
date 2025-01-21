package com.hector.gdriveconsumerservice.demo.controller;

import com.hector.gdriveconsumerservice.demo.component.GoogleDriveComponent;
import com.hector.gdriveconsumerservice.demo.entity.ObjectMetadata;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class UploadFileControllerTests {
    @Mock
    private GoogleDriveComponent driveComponent;

    @Mock
    private MultipartFile file;

    @InjectMocks
    private UploadFileController uploadFileController;

    @Test
    public void uploadFile() throws IOException {
        final String folderId = "FOLDER_ID";
        final ObjectMetadata uploadedFile = ObjectMetadata.builder()
                .id("ABC")
                .name("FakeFile.txt")
                .build();
        when(driveComponent.uploadFile(eq(file), eq(folderId))).thenReturn(uploadedFile);
        final ResponseEntity<ObjectMetadata> expected = ResponseEntity.status(HttpStatus.CREATED).body(uploadedFile);
        assertEquals(expected, uploadFileController.uploadFile(file, folderId));
    }
}
