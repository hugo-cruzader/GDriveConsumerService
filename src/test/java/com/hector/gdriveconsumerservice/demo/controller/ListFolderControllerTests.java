package com.hector.gdriveconsumerservice.demo.controller;

import com.google.api.client.util.DateTime;
import com.google.api.services.drive.model.File;
import com.google.api.services.drive.model.FileList;
import com.hector.gdriveconsumerservice.demo.component.GoogleDriveComponent;
import com.hector.gdriveconsumerservice.demo.entity.ObjectMetadata;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;

import java.io.IOException;
import java.util.Date;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class ListFolderControllerTests {

    @Mock
    private GoogleDriveComponent driveComponent;

    @InjectMocks
    private ListFolderController listFolderController;

    @Test
    public void listFolder() throws IOException {
        final FileList response = new FileList();
        response.setFiles(List.of(getFile(1),getFile(2),getFile(3)));
        when(driveComponent.getFiles()).thenReturn(response);
        final ResponseEntity<List<ObjectMetadata>> actualResponse = listFolderController.listFolder("root");
        assertEquals(3, actualResponse.getBody().size());
        int count = 1;
        for (ObjectMetadata obj : actualResponse.getBody()) {
            assertEquals("ID" + count, obj.id());
            assertEquals("File" + count + ".txt", obj.name());
            assertEquals("text/plain", obj.type());
            count ++;
        }
    }

    private File getFile(final int index) {
        final File file =  new File();
        file.setName("File" + index + ".txt");
        file.setId("ID" + index);
        file.setModifiedTime(new DateTime(new Date()));
        file.setMimeType("text/plain");
        return file;
    }
}
