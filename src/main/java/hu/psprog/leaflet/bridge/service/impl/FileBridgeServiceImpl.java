package hu.psprog.leaflet.bridge.service.impl;

import hu.psprog.leaflet.api.rest.request.file.DirectoryCreationRequestModel;
import hu.psprog.leaflet.api.rest.request.file.FileUploadRequestModel;
import hu.psprog.leaflet.api.rest.request.file.UpdateFileMetaInfoRequestModel;
import hu.psprog.leaflet.api.rest.response.file.FileDataModel;
import hu.psprog.leaflet.api.rest.response.file.FileListDataModel;
import hu.psprog.leaflet.bridge.client.BridgeClient;
import hu.psprog.leaflet.bridge.client.exception.CommunicationFailureException;
import hu.psprog.leaflet.bridge.client.request.Path;
import hu.psprog.leaflet.bridge.client.request.RESTRequest;
import hu.psprog.leaflet.bridge.client.request.RequestMethod;
import hu.psprog.leaflet.bridge.service.FileBridgeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.InputStream;
import java.util.UUID;

/**
 * Implementation of {@link FileBridgeService}.
 *
 * @author Peter Smith
 */
@Service
class FileBridgeServiceImpl implements FileBridgeService {

    private static final String FILE_IDENTIFIER = "fileIdentifier";
    private static final String STORED_FILENAME = "storedFilename";

    private BridgeClient bridgeClient;

    @Autowired
    public FileBridgeServiceImpl(BridgeClient bridgeClient) {
        this.bridgeClient = bridgeClient;
    }

    @Override
    public FileListDataModel getUploadedFiles() throws CommunicationFailureException {

        RESTRequest restRequest = RESTRequest.getBuilder()
                .method(RequestMethod.GET)
                .path(Path.FILES)
                .authenticated()
                .build();

        return bridgeClient.call(restRequest, FileListDataModel.class);
    }

    @Override
    public Resource downloadFile(UUID fileIdentifier, String storedFilename) throws CommunicationFailureException {

        RESTRequest restRequest = RESTRequest.getBuilder()
                .method(RequestMethod.GET)
                .path(Path.FILES_BY_ID)
                .addPathParameter(FILE_IDENTIFIER, fileIdentifier.toString())
                .addPathParameter(STORED_FILENAME, storedFilename)
                .build();

        InputStream response = bridgeClient.call(restRequest, InputStream.class);

        return convertInputStreamToByteArrayResource(response);
    }

    @Override
    public FileDataModel uploadFile(FileUploadRequestModel fileUploadRequestModel) throws CommunicationFailureException {

        RESTRequest restRequest = RESTRequest.getBuilder()
                .method(RequestMethod.POST)
                .path(Path.FILES)
                .requestBody(fileUploadRequestModel)
                .authenticated()
                .build();

        return bridgeClient.call(restRequest, FileDataModel.class);
    }

    @Override
    public void deleteFile(UUID fileIdentifier, String storedFilename) throws CommunicationFailureException {

        RESTRequest restRequest = RESTRequest.getBuilder()
                .method(RequestMethod.DELETE)
                .path(Path.FILES_BY_ID)
                .addPathParameter(FILE_IDENTIFIER, fileIdentifier.toString())
                .addPathParameter(STORED_FILENAME, storedFilename)
                .authenticated()
                .build();

        bridgeClient.call(restRequest);
    }

    @Override
    public void createDirectory(DirectoryCreationRequestModel directoryCreationRequestModel) throws CommunicationFailureException {

        RESTRequest restRequest = RESTRequest.getBuilder()
                .method(RequestMethod.POST)
                .path(Path.FILES_DIRECTORY)
                .requestBody(directoryCreationRequestModel)
                .authenticated()
                .build();

        bridgeClient.call(restRequest);
    }

    @Override
    public void updateFileMetaInfo(UUID fileIdentifier, String storedFilename, UpdateFileMetaInfoRequestModel updateFileMetaInfoRequestModel)
            throws CommunicationFailureException {

        RESTRequest restRequest = RESTRequest.getBuilder()
                .method(RequestMethod.PUT)
                .path(Path.FILES_BY_ID)
                .requestBody(updateFileMetaInfoRequestModel)
                .addPathParameter(FILE_IDENTIFIER, fileIdentifier.toString())
                .addPathParameter(STORED_FILENAME, storedFilename)
                .authenticated()
                .build();

        bridgeClient.call(restRequest);
    }

    private ByteArrayResource convertInputStreamToByteArrayResource(InputStream inputStream) {

        try {
            byte[] inputStreamByteArray = new byte[inputStream.available()];
            if (inputStream.read(inputStreamByteArray) <= 0 ) {
                throw new IllegalStateException("Empty InputStream received.");
            }

            return new ByteArrayResource(inputStreamByteArray);
        } catch (IOException e) {
            throw new IllegalStateException("Retrieved InputStream could not be read up.", e);
        }
    }
}
