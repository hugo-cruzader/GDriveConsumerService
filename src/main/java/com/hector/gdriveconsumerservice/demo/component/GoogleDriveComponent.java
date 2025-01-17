package com.hector.gdriveconsumerservice.demo.component;

import com.google.api.services.drive.model.FileList;

import java.io.IOException;

public interface GoogleDriveComponent {

    FileList getFiles() throws IOException;
    void deleteFile(String fileId) throws IOException;

}
