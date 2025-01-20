package com.hector.gdriveconsumerservice.demo.controller;

import com.hector.gdriveconsumerservice.demo.component.GoogleDriveComponent;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class DeleteFileControllerTests {
    @Mock
    private GoogleDriveComponent driveComponent;

    @InjectMocks
    private DeleteFileController deleteFileController;

    @Test
    public void deleteFile() throws IOException {
        final String fileId ="ABC";
        assertEquals(ResponseEntity.noContent().build(), deleteFileController.deleteFile(fileId));
        verify(driveComponent).deleteFile(eq(fileId));
    }
}
