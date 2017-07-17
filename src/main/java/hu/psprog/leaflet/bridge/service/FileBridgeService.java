package hu.psprog.leaflet.bridge.service;

import hu.psprog.leaflet.api.rest.request.file.DirectoryCreationRequestModel;
import hu.psprog.leaflet.api.rest.request.file.FileUploadRequestModel;
import hu.psprog.leaflet.api.rest.request.file.UpdateFileMetaInfoRequestModel;
import hu.psprog.leaflet.api.rest.response.file.FileDataModel;
import hu.psprog.leaflet.api.rest.response.file.FileListDataModel;
import hu.psprog.leaflet.bridge.client.exception.CommunicationFailureException;
import org.springframework.core.io.Resource;

import java.util.UUID;

/**
 * BridgeClient interface for file related calls.
 *
 * @author Peter Smith
 */
public interface FileBridgeService {

    /**
     * Returns list of uploaded files.
     *
     * @return list of uploaded files as {@link FileDataModel}
     * @throws CommunicationFailureException if client fails to reach backend application
     */
    FileListDataModel getUploadedFiles() throws CommunicationFailureException;

    /**
     * Downloads given file.
     *
     * @param fileIdentifier UUID of the uploaded file
     * @param storedFilename stored filename of the uploaded file (currently only the fileIdentifier is used for identification)
     * @return file as resource
     * @throws CommunicationFailureException if client fails to reach backend application
     */
    Resource downloadFile(UUID fileIdentifier, String storedFilename) throws CommunicationFailureException;

    /**
     * Uploads a new file.
     *
     * @param fileUploadRequestModel file to upload and additional data wrapped as {@link FileUploadRequestModel}
     * @return data of uploaded file as {@link FileDataModel}
     * @throws CommunicationFailureException if client fails to reach backend application
     */
    FileDataModel uploadFile(FileUploadRequestModel fileUploadRequestModel) throws CommunicationFailureException;

    /**
     * Deletes an existing file.
     *
     * @param fileIdentifier UUID of the uploaded file
     * @param storedFilename stored filename of the uploaded file (currently only the fileIdentifier is used for identification)
     * @throws CommunicationFailureException if client fails to reach backend application
     */
    void deleteFile(UUID fileIdentifier, String storedFilename) throws CommunicationFailureException;

    /**
     * Creates a new directory under given parent directory.
     *
     * @param directoryCreationRequestModel model holding directory information
     * @throws CommunicationFailureException if client fails to reach backend application
     */
    void createDirectory(DirectoryCreationRequestModel directoryCreationRequestModel) throws CommunicationFailureException;

    /**
     * Updates given file's meta information.
     *
     * @param fileIdentifier UUID of the uploaded file
     * @param storedFilename stored filename of the uploaded file (currently only the fileIdentifier is used for identification)
     * @param updateFileMetaInfoRequestModel updated meta information
     * @throws CommunicationFailureException if client fails to reach backend application
     */
    void updateFileMetaInfo(UUID fileIdentifier, String storedFilename, UpdateFileMetaInfoRequestModel updateFileMetaInfoRequestModel)
            throws CommunicationFailureException;
}
