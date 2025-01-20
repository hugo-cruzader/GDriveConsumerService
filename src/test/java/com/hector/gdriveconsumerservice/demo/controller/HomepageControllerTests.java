package com.hector.gdriveconsumerservice.demo.controller;

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeRequestUrl;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeTokenRequest;
import com.google.api.client.googleapis.auth.oauth2.GoogleTokenResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.util.ReflectionTestUtils;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@TestPropertySource(properties = { "google.oauth.callback.uri=http://localhost:8080/callback" })
public class HomepageControllerTests {

    @Mock
    private HttpServletResponse mockResponse;

    @Mock
    private HttpServletRequest mockRequest;

    @Mock
    private GoogleAuthorizationCodeRequestUrl mockRequestUrl;

    @Mock
    private GoogleAuthorizationCodeTokenRequest mockTokenRequest;

    @Mock
    private GoogleTokenResponse mockTokenResponse;

    @Mock
    private Credential mockCredential;

    @Mock
    private GoogleAuthorizationCodeFlow flow;

    @InjectMocks
    private HomepageController home;

    private final static String TEST_CALLBACK_URI = "http://test:8080/callback";

    @Test
    public void showHomePage_showsDashboard_whenCredentialOk() throws IOException {
        when(flow.loadCredential(eq("DEMO_USER"))).thenReturn(mockCredential);
        when(mockCredential.getAccessToken()).thenReturn("accessToken");
        when(mockCredential.getExpirationTimeMilliseconds()).thenReturn(10_413_820_800_000L);
        assertEquals(HomepageController.DASHBOARD_URL, home.showHomePage());
    }

    @Test
    public void showHomePage_showsIndex_whenCredentialExpired() throws IOException {
        when(flow.loadCredential(eq("DEMO_USER"))).thenReturn(mockCredential);
        when(mockCredential.getAccessToken()).thenReturn("accessToken");
        when(mockCredential.getExpirationTimeMilliseconds()).thenReturn(System.currentTimeMillis() - 60_000);
        assertEquals(HomepageController.INDEX_URL, home.showHomePage());
    }

    @Test
    public void showHomePage_showsDashboard_whenExpirationNull() throws IOException {
        when(flow.loadCredential(eq("DEMO_USER"))).thenReturn(mockCredential);
        when(mockCredential.getAccessToken()).thenReturn("accessToken");
        when(mockCredential.getExpirationTimeMilliseconds()).thenReturn(null);
        assertEquals(HomepageController.DASHBOARD_URL, home.showHomePage());
    }

    @Test
    public void showHomePage_showsIndex_whenNoAccessToken() throws IOException {
        when(flow.loadCredential(eq("DEMO_USER"))).thenReturn(mockCredential);
        when(mockCredential.getAccessToken()).thenReturn(null);
        assertEquals(HomepageController.INDEX_URL, home.showHomePage());
    }

    @Test
    public void showHomePage_showsIndex_whenNoCredentials() throws IOException {
        when(flow.loadCredential(eq("DEMO_USER"))).thenReturn(null);
        assertEquals(HomepageController.INDEX_URL, home.showHomePage());
    }

    @Test
    public void doGoogleSignIn() throws IOException {
        ReflectionTestUtils.setField(home, "CALLBACK_URI", TEST_CALLBACK_URI);
        final String redirect = "https://accounts.google.com/o/oauth2/auth?redirect_uri=http://localhost:8080/callback";
        when(flow.newAuthorizationUrl()).thenReturn(mockRequestUrl);
        when(mockRequestUrl.setRedirectUri(eq(TEST_CALLBACK_URI))).thenReturn(mockRequestUrl);
        when(mockRequestUrl.setAccessType("offline")).thenReturn(mockRequestUrl);
        when(mockRequestUrl.build()).thenReturn(redirect);
        home.doGoogleSignIn(mockResponse);
        verify(mockResponse).sendRedirect(eq(redirect));
    }

    @Test
    public void saveAuthorizationCode() throws IOException {
        ReflectionTestUtils.setField(home, "CALLBACK_URI", TEST_CALLBACK_URI);
        when(mockRequest.getParameter(eq("code"))).thenReturn("CODE");
        when(flow.newTokenRequest(eq("CODE"))).thenReturn(mockTokenRequest);
        when(mockTokenRequest.setRedirectUri(eq(TEST_CALLBACK_URI))).thenReturn(mockTokenRequest);
        when(mockTokenRequest.execute()).thenReturn(mockTokenResponse);
        assertEquals(HomepageController.DASHBOARD_URL, home.saveAuthorizationCode(mockRequest));
        verify(flow).createAndStoreCredential(eq(mockTokenResponse), eq("DEMO_USER"));
    }

    @Test
    public void saveAuthorizationCode_whenCodeIsNull() throws IOException {
        when(mockRequest.getParameter(eq("code"))).thenReturn(null);
        assertEquals(HomepageController.INDEX_URL, home.saveAuthorizationCode(mockRequest));
    }
}
