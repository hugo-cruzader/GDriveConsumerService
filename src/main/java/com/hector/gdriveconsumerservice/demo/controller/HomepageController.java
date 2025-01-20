package com.hector.gdriveconsumerservice.demo.controller;

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeRequestUrl;
import com.google.api.client.googleapis.auth.oauth2.GoogleTokenResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

import java.io.IOException;

/**
 * Class that holds the welcome page, redirect to Oauth 2.0 for login and then the dashboard of the GDriveConsumerService
 * when credentials OK.
 */
@Slf4j
@Controller
@RequiredArgsConstructor
public class HomepageController {

    public static final String DASHBOARD_URL = "dashboard.html";
    public static final String INDEX_URL = "index.html";

    private static final String USER_IDENTIFIER_KEY = "DEMO_USER";

    @Value("${google.oauth.callback.uri}")
    private String CALLBACK_URI;

    private final GoogleAuthorizationCodeFlow flow;

    /**
     * Show the welcome/login page or the Dashboard given the current state of the credentials in the Google Authorization flow.
     * @return Either the dashboard uri or the index uri.
     * @throws IOException when GoogleAuthorizationCodeFlow presents a reading issue of the USER_IDENTIFIER_KEY.
     */
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
                return DASHBOARD_URL;
            }
        }
        return INDEX_URL;
    }

    /**
     * Redirect the user to perform Oauth2.0 login and return to the callback uri.
     * @param response to process the redirection and callback.
     * @throws IOException when GoogleAuthorizationCodeFlow presents an issue when retrieving the AuthorizationURL.
     */
    @GetMapping(value={"/googlesignin"})
    public void doGoogleSignIn(final HttpServletResponse response) throws IOException {
        final GoogleAuthorizationCodeRequestUrl url = flow.newAuthorizationUrl();
        final String redirect = url.setRedirectUri(CALLBACK_URI).setAccessType("offline").build();
        response.sendRedirect(redirect);
    }

    /**
     * Retrieves the code from the oauth callback response from sign in and stores a GoogleTokenResponse in the credential storage of the
     * GoogleAuthorizationCodeFlow for the single user.
     *
     * @param request to retrieve the code value from the incoming HTTP Request.
     * @return the redirection to either the Dashboard view or the Index view, depending on the status of the code retrieved from the request.
     * @throws IOException if GoogleAuthorizationCodeFlow presents an issue while retrieving a new tokenRequest and/or storing the credentials.
     */
    @GetMapping(value={"/oauth"})
    public String saveAuthorizationCode(final HttpServletRequest request) throws IOException {
        final String code = request.getParameter("code");
        if (code != null) {
            saveToken(code);
            return DASHBOARD_URL;
        }
        return INDEX_URL;
    }

    private void saveToken(final String code) throws IOException {
        GoogleTokenResponse response = flow.newTokenRequest(code).setRedirectUri(CALLBACK_URI).execute();
        flow.createAndStoreCredential(response, USER_IDENTIFIER_KEY);
    }
}
