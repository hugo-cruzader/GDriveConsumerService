package com.hector.gdriveconsumerservice.demo.controller;

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeRequestUrl;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.googleapis.auth.oauth2.GoogleTokenResponse;
import com.google.api.client.http.FileContent;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.client.util.store.FileDataStoreFactory;
import com.google.api.services.drive.Drive;
import com.google.api.services.drive.DriveScopes;
import com.google.api.services.drive.model.File;
import com.google.api.services.drive.model.FileList;
import com.hector.gdriveconsumerservice.demo.entity.ObjectMetadata;
import jakarta.annotation.PostConstruct;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Collections;
import java.util.List;

@Slf4j
@Controller
@RequiredArgsConstructor
public class HomepageController {

    private static final String USER_IDENTIFIER_KEY = "DEMO_USER";

    private final HttpTransport HTTP_TRANSPORT;
    private final JsonFactory JSON_FACTORY;

    @Value("${google.application.name}")
    private String APPLICATION_NAME;

    @Value("${google.oauth.callback.uri}")
    private String CALLBACK_URI;

    @Value("${demo.file}")
    private Resource demoFile;

    private final GoogleAuthorizationCodeFlow flow;

    @GetMapping(value= {"/"})
    public String showHomePage() throws IOException {
        log.info("Entering HomePage...");
        final Credential credential = flow.loadCredential(USER_IDENTIFIER_KEY);
        if (credential != null) {
            if (credential.getAccessToken() == null ||
                    (credential.getExpirationTimeMilliseconds() != null &&
                            System.currentTimeMillis() >= credential.getExpirationTimeMilliseconds())) {
                log.warn("Access token is invalid or expired.");
            } else {
                log.info("Access token is valid.");
                return "dashboard.html";
            }
        }
        return "index.html";
    }

    @GetMapping(value={"/googlesignin"})
    public void doGoogleSignIn(final HttpServletResponse response) throws IOException {
        final GoogleAuthorizationCodeRequestUrl url = flow.newAuthorizationUrl();
        final String redirect = url.setRedirectUri(CALLBACK_URI).setAccessType("offline").build();
        response.sendRedirect(redirect);
    }

    @GetMapping(value={"/oauth"})
    public String saveAuthorizationCode(final HttpServletRequest request) throws IOException {
        final String code = request.getParameter("code");
        if (code != null) {
            saveToken(code);
            return "dashboard.html";
        }
        return "index.html";
    }

    private void saveToken(final String code) throws IOException {
        GoogleTokenResponse response = flow.newTokenRequest(code).setRedirectUri(CALLBACK_URI).execute();
        flow.createAndStoreCredential(response, USER_IDENTIFIER_KEY);
    }

    @GetMapping(value={"/create"})
    public void createFile(HttpServletResponse response) throws IOException {
        final Credential credential = flow.loadCredential(USER_IDENTIFIER_KEY);
        final Drive drive =  new Drive.Builder(HTTP_TRANSPORT, JSON_FACTORY, credential).setApplicationName(APPLICATION_NAME).build();
        final File file = new File();
        file.setName("Sample.txt");
        final FileContent content = new FileContent("text/plain", demoFile.getFile());
        final File uploadedFile = drive.files().create(file, content).setFields("id").execute();
        final String fileReference = String.format("{fileId: %s}", uploadedFile.getId());
        response.getWriter().write(fileReference);
    }
}
