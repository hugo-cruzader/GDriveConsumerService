package com.hector.gdriveconsumerservice.demo.component;


import com.google.api.client.http.InputStreamContent;
import com.google.api.services.drive.Drive;
import com.google.api.services.drive.model.File;
import com.google.api.services.drive.model.FileList;
import com.hector.gdriveconsumerservice.demo.entity.DownloadableResource;
import com.hector.gdriveconsumerservice.demo.entity.ObjectMetadata;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class GoogleDriveComponentImplTests {

    private static final String FILE_ID = "ID_ABC";

    @Mock
    private Drive mockDrive;

    @Mock
    private GoogleDriveClient driveClient;

    @Mock
    private Drive.Files mockDriveFiles;

    @Mock
    private Drive.Files.List mockDriveFilesList;

    @Mock
    private FileList mockFileList;

    @Mock
    private Drive.Files.Delete mockDriveFilesDelete;

    @Mock
    private Drive.Files.Create mockDriveFilesCreate;

    @Mock
    private Drive.Files.Get mockDriveFilesGet;

    @Mock
    private Drive.Files.Export mockDriveFilesExport;

    @Mock
    private File mockFile;

    @InjectMocks
    private GoogleDriveComponentImpl driveComponent;

    @BeforeEach
    public void setUp() throws IOException {
        when(driveClient.getDriveClient()).thenReturn(mockDrive);
        when(mockDrive.files()).thenReturn(mockDriveFiles);
    }

    @Test
    public void getFiles() throws IOException {
        when(mockDriveFiles.list()).thenReturn(mockDriveFilesList);
        when(mockDriveFilesList.setFields(eq("files(id,name,mimeType,modifiedTime)"))).thenReturn(mockDriveFilesList);
        when(mockDriveFilesList.execute()).thenReturn(mockFileList);
        final FileList actualfileList = driveComponent.getFiles();
        assertEquals(mockFileList, actualfileList);
        verify(mockDriveFilesList).execute();
    }

    @Test
    public void deleteFile() throws IOException {
        when(mockDriveFiles.delete(eq(FILE_ID))).thenReturn(mockDriveFilesDelete);
        driveComponent.deleteFile(FILE_ID);
        verify(mockDriveFilesDelete).execute();
    }

    @Test
    public void uploadFile() throws IOException {
        final String content = "Hello World";
        final String folderId = "folderId";
        final String fileName = "test.txt";
        final MockMultipartFile mockMultipartFile = new MockMultipartFile(
                "file", "test.txt", "text/plain", content.getBytes());

        final ArgumentCaptor<File> captor = ArgumentCaptor.forClass(File.class);
        final ArgumentCaptor<InputStreamContent> inputStreamCaptor = ArgumentCaptor.forClass(InputStreamContent.class);
        when(mockDriveFiles.create(captor.capture(), inputStreamCaptor.capture())).thenReturn(mockDriveFilesCreate);
        when(mockDriveFilesCreate.setFields(eq("id, name, parents"))).thenReturn(mockDriveFilesCreate);
        when(mockDriveFilesCreate.execute()).thenReturn(mockFile);
        when(mockFile.getId()).thenReturn("ID_UPLOAD");
        when(mockFile.getName()).thenReturn("NAME_UPLOAD");
        final ObjectMetadata actualObj = driveComponent.uploadFile(mockMultipartFile, folderId);
        final String result = new String(inputStreamCaptor.getValue().getInputStream().readAllBytes(), StandardCharsets.UTF_8);
        assertEquals(fileName, captor.getValue().getName());
        assertEquals(folderId, captor.getValue().getParents().getFirst());
        assertEquals(content, result);
        assertEquals("ID_UPLOAD", actualObj.id());
        assertEquals("NAME_UPLOAD", actualObj.name());
        verify(mockDriveFilesCreate).execute();
    }

    @Test
    public void downloadFile() throws IOException{
        final String fileName = "FAKE_FILE.txt";
        when(mockDriveFiles.get(eq(FILE_ID))).thenReturn(mockDriveFilesGet);
        when(mockDriveFilesGet.setFields(eq("name, mimeType"))).thenReturn(mockDriveFilesGet);
        when(mockDriveFilesGet.execute()).thenReturn(mockFile);
        when(mockFile.getName()).thenReturn(fileName);
        when(mockFile.getMimeType()).thenReturn("text/plain");
        final DownloadableResource resource = driveComponent.downloadFile(FILE_ID);
        verify(mockDriveFilesGet).executeMediaAndDownloadTo(any(OutputStream.class));
        assertEquals(fileName, resource.fileName());
    }

    @Test
    public void downloadFile_whenGoogleFileDownload() throws IOException{
        final String fileName = "FAKE_FILE.gDoc";
        when(mockDriveFiles.get(eq(FILE_ID))).thenReturn(mockDriveFilesGet);
        when(mockDriveFilesGet.setFields(eq("name, mimeType"))).thenReturn(mockDriveFilesGet);
        when(mockDriveFilesGet.execute()).thenReturn(mockFile);
        when(mockFile.getName()).thenReturn(fileName);
        when(mockFile.getMimeType()).thenReturn("application/vnd.google-apps.document");
        when(mockDriveFiles.export(eq(FILE_ID), eq("application/pdf"))).thenReturn(mockDriveFilesExport);
        final DownloadableResource resource = driveComponent.downloadFile(FILE_ID);
        verify(mockDriveFilesExport).executeMediaAndDownloadTo(any(OutputStream.class));
        assertEquals(fileName + ".pdf", resource.fileName());
    }
}
