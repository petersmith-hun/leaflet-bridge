package hu.psprog.leaflet.bridge.service.impl;

import hu.psprog.leaflet.api.rest.request.file.DirectoryCreationRequestModel;
import hu.psprog.leaflet.api.rest.request.file.FileUploadRequestModel;
import hu.psprog.leaflet.api.rest.request.file.UpdateFileMetaInfoRequestModel;
import hu.psprog.leaflet.api.rest.response.file.DirectoryListDataModel;
import hu.psprog.leaflet.api.rest.response.file.FileDataModel;
import hu.psprog.leaflet.api.rest.response.file.FileListDataModel;
import hu.psprog.leaflet.bridge.adapter.impl.FileUploadMultipartRequestBodyAdapter;
import hu.psprog.leaflet.bridge.client.BridgeClient;
import hu.psprog.leaflet.bridge.client.domain.BridgeService;
import hu.psprog.leaflet.bridge.client.exception.CommunicationFailureException;
import hu.psprog.leaflet.bridge.client.request.Path;
import hu.psprog.leaflet.bridge.client.request.RESTRequest;
import hu.psprog.leaflet.bridge.client.request.RequestMethod;
import hu.psprog.leaflet.bridge.service.FileBridgeService;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.InputStream;
import java.util.UUID;

/**
 * Implementation of {@link FileBridgeService}.
 *
 * @author Peter Smith
 */
@BridgeService(client = "leaflet")
class FileBridgeServiceImpl implements FileBridgeService {

    private static final String FILE_IDENTIFIER = "fileIdentifier";
    private static final String STORED_FILENAME = "storedFilename";

    private BridgeClient bridgeClient;
    private FileUploadMultipartRequestBodyAdapter multipartAdapter;

    @Autowired
    public FileBridgeServiceImpl(BridgeClient bridgeClient, FileUploadMultipartRequestBodyAdapter multipartAdapter) {
        this.bridgeClient = bridgeClient;
        this.multipartAdapter = multipartAdapter;
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
    public InputStream downloadFile(UUID fileIdentifier, String storedFilename) throws CommunicationFailureException {

        RESTRequest restRequest = RESTRequest.getBuilder()
                .method(RequestMethod.GET)
                .path(Path.FILES_BY_ID)
                .addPathParameter(FILE_IDENTIFIER, fileIdentifier.toString())
                .addPathParameter(STORED_FILENAME, storedFilename)
                .build();

        return bridgeClient.call(restRequest, InputStream.class);
    }

    @Override
    public FileDataModel getFileDetails(UUID fileIdentifier) throws CommunicationFailureException {

        RESTRequest restRequest = RESTRequest.getBuilder()
                .method(RequestMethod.GET)
                .path(Path.FILES_ONLY_UUID)
                .addPathParameter(FILE_IDENTIFIER, fileIdentifier.toString())
                .authenticated()
                .build();

        return bridgeClient.call(restRequest, FileDataModel.class);
    }

    @Override
    public FileDataModel uploadFile(FileUploadRequestModel fileUploadRequestModel) throws CommunicationFailureException {

        RESTRequest restRequest = RESTRequest.getBuilder()
                .method(RequestMethod.POST)
                .path(Path.FILES)
                .requestBody(fileUploadRequestModel)
                .multipart()
                .adapter(multipartAdapter)
                .authenticated()
                .build();

        return bridgeClient.call(restRequest, FileDataModel.class);
    }

    @Override
    public void deleteFile(UUID fileIdentifier) throws CommunicationFailureException {

        RESTRequest restRequest = RESTRequest.getBuilder()
                .method(RequestMethod.DELETE)
                .path(Path.FILES_ONLY_UUID)
                .addPathParameter(FILE_IDENTIFIER, fileIdentifier.toString())
                .authenticated()
                .build();

        bridgeClient.call(restRequest);
    }

    @Override
    public void createDirectory(DirectoryCreationRequestModel directoryCreationRequestModel) throws CommunicationFailureException {

        RESTRequest restRequest = RESTRequest.getBuilder()
                .method(RequestMethod.POST)
                .path(Path.FILES_DIRECTORIES)
                .requestBody(directoryCreationRequestModel)
                .authenticated()
                .build();

        bridgeClient.call(restRequest);
    }

    @Override
    public void updateFileMetaInfo(UUID fileIdentifier, UpdateFileMetaInfoRequestModel updateFileMetaInfoRequestModel)
            throws CommunicationFailureException {

        RESTRequest restRequest = RESTRequest.getBuilder()
                .method(RequestMethod.PUT)
                .path(Path.FILES_ONLY_UUID)
                .requestBody(updateFileMetaInfoRequestModel)
                .addPathParameter(FILE_IDENTIFIER, fileIdentifier.toString())
                .authenticated()
                .build();

        bridgeClient.call(restRequest);
    }

    @Override
    public DirectoryListDataModel getDirectories() throws CommunicationFailureException {

        RESTRequest restRequest = RESTRequest.getBuilder()
                .method(RequestMethod.GET)
                .path(Path.FILES_DIRECTORIES)
                .authenticated()
                .build();

        return bridgeClient.call(restRequest, DirectoryListDataModel.class);
    }
}
