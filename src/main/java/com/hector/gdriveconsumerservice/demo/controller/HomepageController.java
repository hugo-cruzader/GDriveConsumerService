package com.hector.gdriveconsumerservice.demo.controller;

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeRequestUrl;
import com.google.api.client.googleapis.auth.oauth2.GoogleTokenResponse;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.http.HttpTransport;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

import java.io.IOException;

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
}
