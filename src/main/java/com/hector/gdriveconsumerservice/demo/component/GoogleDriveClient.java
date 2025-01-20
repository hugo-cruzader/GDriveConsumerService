package com.hector.gdriveconsumerservice.demo.component;

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.services.drive.Drive;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.IOException;

/**
 * Component class to handle the creation of the Google Drive Client based on the provided credentials by the GoogleAuthorizationCodeFlow
 * object.
 */
@Component
@RequiredArgsConstructor
public class GoogleDriveClient {

    public static final String USER_IDENTIFIER_KEY = "DEMO_USER";

    @Value("${google.application.name}")
    private String APPLICATION_NAME;

    private final HttpTransport HTTP_TRANSPORT;
    private final JsonFactory JSON_FACTORY;

    private final GoogleAuthorizationCodeFlow flow;

    public Drive getDriveClient() throws IOException {
        final Credential credential = flow.loadCredential(USER_IDENTIFIER_KEY);
        return new Drive.Builder(HTTP_TRANSPORT, JSON_FACTORY, credential).setApplicationName(APPLICATION_NAME).build();
    }
}
