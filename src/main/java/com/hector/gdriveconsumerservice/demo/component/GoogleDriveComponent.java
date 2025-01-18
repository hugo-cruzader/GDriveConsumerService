package com.hector.gdriveconsumerservice.demo.component;

import com.google.api.services.drive.model.FileList;
import com.hector.gdriveconsumerservice.demo.entity.DownloadableResource;
import com.hector.gdriveconsumerservice.demo.entity.ObjectMetadata;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

public interface GoogleDriveComponent {

    FileList getFiles() throws IOException;
    void deleteFile(String fileId) throws IOException;
    DownloadableResource downloadFile(String fileId) throws IOException;
    ObjectMetadata uploadFile(MultipartFile multipartFile) throws IOException;
}
