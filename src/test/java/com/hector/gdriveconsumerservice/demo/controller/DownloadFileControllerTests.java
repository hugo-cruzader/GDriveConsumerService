package com.hector.gdriveconsumerservice.demo.controller;

import com.hector.gdriveconsumerservice.demo.component.GoogleDriveComponent;
import com.hector.gdriveconsumerservice.demo.entity.DownloadableResource;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class DownloadFileControllerTests {

    @Mock
    private GoogleDriveComponent driveComponent;

    @Mock
    private DownloadableResource mockResource;

    @InjectMocks
    private DownloadFileController downloadFileController;

    @Test
    public void downloadFile() throws IOException {
        final Resource resource = new ByteArrayResource("hello world!".getBytes());
        when(driveComponent.downloadFile(eq("ID_ABC"))).thenReturn(mockResource);
        when(mockResource.fileName()).thenReturn("FakeFileName.txt");
        when(mockResource.content()).thenReturn(resource);
        final ResponseEntity<Resource> actualResource = downloadFileController.downloadFile("ID_ABC");
        assertEquals(HttpStatusCode.valueOf(200), actualResource.getStatusCode());
        assertEquals("attachment; filename=\"FakeFileName.txt\"", actualResource.getHeaders().getFirst(HttpHeaders.CONTENT_DISPOSITION));
        assertEquals(MediaType.APPLICATION_OCTET_STREAM_VALUE, actualResource.getHeaders().getFirst(HttpHeaders.CONTENT_TYPE));
        try {
            final String content = new String(actualResource.getBody().getInputStream().readAllBytes());
            assertEquals("hello world!", content);
        } catch (Exception e) {
            fail("Exception while reading content: " + e.getMessage());
        }
    }
}
