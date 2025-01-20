package com.hector.gdriveconsumerservice.demo.component;

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.services.drive.Drive;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class GoogleDriveClientTests {

    private static final String FAKE_APPLICATION_NAME = "FAKE_APP";

    @Mock
    private Credential mockCredential;

    @Mock
    private HttpTransport httpTransport;

    @Mock
    private JsonFactory jsonFactory;

    @Mock
    private GoogleAuthorizationCodeFlow flow;

    @InjectMocks
    private GoogleDriveClient driveClient;

    @Test
    public void getDriveClient() throws IOException {
        ReflectionTestUtils.setField(driveClient, "APPLICATION_NAME", FAKE_APPLICATION_NAME);
        when(flow.loadCredential(eq(GoogleDriveClient.USER_IDENTIFIER_KEY))).thenReturn(mockCredential);
        final Drive actualDrive =  driveClient.getDriveClient();
        assertNotNull(actualDrive);
        assertEquals(FAKE_APPLICATION_NAME,actualDrive.getApplicationName());
        assertEquals(jsonFactory, actualDrive.getJsonFactory());
    }
}
