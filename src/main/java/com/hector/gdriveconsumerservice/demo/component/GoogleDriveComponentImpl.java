package com.hector.gdriveconsumerservice.demo.component;

import com.google.api.client.http.InputStreamContent;
import com.google.api.services.drive.Drive;
import com.google.api.services.drive.model.File;
import com.google.api.services.drive.model.FileList;
import com.hector.gdriveconsumerservice.demo.entity.DownloadableResource;
import com.hector.gdriveconsumerservice.demo.entity.ObjectMetadata;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map;

@Slf4j
@Component
@RequiredArgsConstructor
public class GoogleDriveComponentImpl implements GoogleDriveComponent {

    private static final Map<String, String> mimeTypeMap = Map.of(
            "application/vnd.google-apps.document","application/pdf",
            "application/vnd.google-apps.presentation","application/pdf",
            "application/vnd.google-apps.spreadsheet","application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"
    );

    private final GoogleDriveClient driveClient;

    @Override
    public FileList getFiles() throws IOException {
        final Drive drive = driveClient.getDriveClient();
        final FileList fileList = drive.files().list().setFields("files(id,name,mimeType,modifiedTime)").execute();
        log.info("API ListFile call successful returning {} elements...", fileList.getFiles().size());
        return fileList;
    }

    @Override
    public void deleteFile(final String fileId) throws IOException {
        final Drive drive = driveClient.getDriveClient();
        drive.files().delete(fileId).execute();
        log.info("API DeleteFile call successful...");
    }

    @Override
    public DownloadableResource downloadFile(final String fileId) throws IOException {
        final Drive drive = driveClient.getDriveClient();
        final File fileMetadata = drive.files().get(fileId).setFields("name, mimeType").execute();
        String fileName = fileMetadata.getName();
        final String mimeType = fileMetadata.getMimeType();
        log.info("Found file [{}] with id [{}]", fileName, fileId);
        final ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        if (isGoogleDocsEditorFile(mimeType)) {
            final String exportMimeType = mimeTypeMap.get(mimeType);
            drive.files().export(fileId, exportMimeType).executeMediaAndDownloadTo(outputStream);
            fileName = adjustFileNameExtension(fileName, exportMimeType);
        } else {
            drive.files().get(fileId).executeMediaAndDownloadTo(outputStream);
        }
        log.info("API DownloadFile call successful, transmitting to user...");
        return DownloadableResource.builder()
                .content(new ByteArrayResource(outputStream.toByteArray()))
                .fileName(fileName)
                .build();
    }

    @Override
    public ObjectMetadata uploadFile(final MultipartFile multipartFile) throws IOException {
        final Drive drive = driveClient.getDriveClient();
        // Convert MultipartFile to InputStream
        final InputStream inputStream = multipartFile.getInputStream();
        // Create metadata for the Google Drive file
        final File fileMetadata = new File();
        fileMetadata.setName(multipartFile.getOriginalFilename());
        final File uploadedFile = drive.files()
                .create(fileMetadata, new InputStreamContent(multipartFile.getContentType(), inputStream))
                .setFields("id, name")
                .execute();
        log.info("API UploadFile call successful. New File:[id={}, name={}]", uploadedFile.getId(), uploadedFile.getName());
        return ObjectMetadata.builder().id(uploadedFile.getId()).name(uploadedFile.getName()).build();
    }

    private String adjustFileNameExtension(final String fileName, final String exportMimeType) {
        if (exportMimeType.equals("application/pdf")) {
            return fileName + ".pdf";
        }
        if (exportMimeType.equals("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet")) {
            return fileName + ".xlsx";
        }
        return fileName;
    }

    private boolean isGoogleDocsEditorFile(final String mimeType) {
        return mimeType.contains("vnd.google-apps");
    }
}
