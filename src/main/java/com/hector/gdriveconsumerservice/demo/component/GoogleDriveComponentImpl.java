package com.hector.gdriveconsumerservice.demo.component;

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.services.drive.Drive;
import com.google.api.services.drive.model.FileList;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Slf4j
@Component
@RequiredArgsConstructor
public class GoogleDriveComponentImpl implements GoogleDriveComponent {

    private static final String USER_IDENTIFIER_KEY = "DEMO_USER";

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
    }

    private Drive getDriveClient() throws IOException {
        final Credential credential = flow.loadCredential(USER_IDENTIFIER_KEY);
        return new Drive.Builder(HTTP_TRANSPORT, JSON_FACTORY, credential).setApplicationName(APPLICATION_NAME).build();
    }
}
