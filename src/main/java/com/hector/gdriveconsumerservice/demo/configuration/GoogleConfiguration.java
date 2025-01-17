package com.hector.gdriveconsumerservice.demo.configuration;

import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.client.util.store.FileDataStoreFactory;
import com.google.api.services.drive.DriveScopes;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;

import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Collections;
import java.util.List;

@Configuration
public class GoogleConfiguration {

    @Value("${google.secret.key.path}")
    private Resource gdSecretKeys;

    @Value("${google.credentials.folder.path}")
    private Resource credentialsFolder;

    @Bean
    public GoogleAuthorizationCodeFlow provideCodeFlow(final HttpTransport httpTransport,
                                                       final JsonFactory jsonFactory,
                                                       final List<String> scopes) throws IOException {
        final GoogleClientSecrets secrets = GoogleClientSecrets.load(jsonFactory, new InputStreamReader(gdSecretKeys.getInputStream()));
        return new GoogleAuthorizationCodeFlow.Builder(httpTransport, jsonFactory, secrets, scopes)
                .setDataStoreFactory(new FileDataStoreFactory(credentialsFolder.getFile())).build();
    }

    @Bean
    public HttpTransport provideTransport() {
        return new NetHttpTransport();
    }

    @Bean
    public JsonFactory provideJsonFactory() {
        return GsonFactory.getDefaultInstance();
    }

    @Bean
    public List<String> provideScopes() {
        return Collections.singletonList(DriveScopes.DRIVE);
    }
}
