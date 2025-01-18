package com.hector.gdriveconsumerservice.demo.component;

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.services.drive.Drive;
import com.google.api.services.drive.model.File;
import com.google.api.services.drive.model.FileList;
import com.hector.gdriveconsumerservice.demo.entity.DownloadableResource;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.stereotype.Component;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Map;

@Slf4j
@Component
@RequiredArgsConstructor
public class GoogleDriveComponentImpl implements GoogleDriveComponent {

    private static final String USER_IDENTIFIER_KEY = "DEMO_USER";
    private static final Map<String, String> mimeTypeMap = Map.of(
            "application/vnd.google-apps.document","application/pdf",
            "application/vnd.google-apps.presentation","application/pdf",
            "application/vnd.google-apps.spreadsheet","application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"
    );

    @Value("${google.application.name}")
    private String APPLICATION_NAME;

    private final HttpTransport HTTP_TRANSPORT;
    private final JsonFactory JSON_FACTORY;

    private final GoogleAuthorizationCodeFlow flow;

    @Override
    public FileList getFiles() throws IOException {
        final Drive drive = getDriveClient();
        final FileList fileList = drive.files().list().setFields("files(id,name,mimeType,modifiedTime)").execute();
        log.info("API ListFile call successful returning {} elements...", fileList.getFiles().size());
        return fileList;
    }

    @Override
    public void deleteFile(final String fileId) throws IOException {
        final Drive drive = getDriveClient();
        drive.files().delete(fileId).execute();
        log.info("API DeleteFile call successful...");
    }

    @Override
    public DownloadableResource downloadFile(final String fileId) throws IOException {
        final Drive drive = getDriveClient();
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

    private Drive getDriveClient() throws IOException {
        final Credential credential = flow.loadCredential(USER_IDENTIFIER_KEY);
        return new Drive.Builder(HTTP_TRANSPORT, JSON_FACTORY, credential).setApplicationName(APPLICATION_NAME).build();
    }
}
