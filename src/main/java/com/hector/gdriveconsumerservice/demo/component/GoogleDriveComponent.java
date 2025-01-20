package com.hector.gdriveconsumerservice.demo.component;

import com.google.api.services.drive.model.FileList;
import com.hector.gdriveconsumerservice.demo.entity.DownloadableResource;
import com.hector.gdriveconsumerservice.demo.entity.ObjectMetadata;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

/**
 * Class that abstracts the basic operations between the G Drive Consumer Service and the user's Google Drive, through a GoogleDriveClient
 */
public interface GoogleDriveComponent {

    /**
     * Function that retrieves the metadata of files of a given G Drive.
     * @return The List of elements as FileList object.
     * @throws IOException when G Drive clients present and I/O exception while retrieving the elements.
     */
    FileList getFiles() throws IOException;

    /**
     * Function that removes a file from G Drive given its id.
     * @throws IOException when the G Drive client present and I/O exception while deleting the element.
     */
    void deleteFile(String fileId) throws IOException;

    /**
     * Function that handles the processing of a file from G Drive to download it to the user client.
     * @param fileId the identifier in G Drive of the file to download.
     * @return {@link DownloadableResource} containing the name and the content of the downloaded file.
     * @throws IOException when the G Drive client presents issues while downloading the file.
     */
    DownloadableResource downloadFile(String fileId) throws IOException;

    /**
     * Function that handles the processing of an incoming file to upload it to G Drive from the user client.
     * @param multipartFile the file to upload to the G Drive.
     * @return the file metadata after uploading to the user G Drive.
     * @throws IOException when the G Drive client presents an issue while uploading the file to G Drive.
     */
    ObjectMetadata uploadFile(MultipartFile multipartFile) throws IOException;
}
