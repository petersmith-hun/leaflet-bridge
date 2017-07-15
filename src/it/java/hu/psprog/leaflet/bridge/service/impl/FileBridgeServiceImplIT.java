package hu.psprog.leaflet.bridge.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.github.tomakehurst.wiremock.client.ResponseDefinitionBuilder;
import com.github.tomakehurst.wiremock.matching.StringValuePattern;
import hu.psprog.leaflet.api.rest.request.file.DirectoryCreationRequestModel;
import hu.psprog.leaflet.api.rest.request.file.FileUploadRequestModel;
import hu.psprog.leaflet.api.rest.request.file.UpdateFileMetaInfoRequestModel;
import hu.psprog.leaflet.api.rest.response.file.FileDataModel;
import hu.psprog.leaflet.api.rest.response.file.FileListDataModel;
import hu.psprog.leaflet.bridge.client.exception.CommunicationFailureException;
import hu.psprog.leaflet.bridge.client.request.Path;
import hu.psprog.leaflet.bridge.it.config.LeafletBridgeITContextConfig;
import hu.psprog.leaflet.bridge.service.FileBridgeService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.UUID;

import static com.github.tomakehurst.wiremock.client.WireMock.delete;
import static com.github.tomakehurst.wiremock.client.WireMock.deleteRequestedFor;
import static com.github.tomakehurst.wiremock.client.WireMock.equalToJson;
import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static com.github.tomakehurst.wiremock.client.WireMock.getRequestedFor;
import static com.github.tomakehurst.wiremock.client.WireMock.givenThat;
import static com.github.tomakehurst.wiremock.client.WireMock.ok;
import static com.github.tomakehurst.wiremock.client.WireMock.post;
import static com.github.tomakehurst.wiremock.client.WireMock.postRequestedFor;
import static com.github.tomakehurst.wiremock.client.WireMock.put;
import static com.github.tomakehurst.wiremock.client.WireMock.putRequestedFor;
import static com.github.tomakehurst.wiremock.client.WireMock.urlEqualTo;
import static com.github.tomakehurst.wiremock.client.WireMock.verify;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;

/**
 * Integration tests for {@link FileBridgeServiceImpl}.
 *
 * @author Peter Smith
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = LeafletBridgeITContextConfig.class)
@ActiveProfiles(LeafletBridgeITContextConfig.INTEGRATION_TEST_CONFIG_PROFILE)
public class FileBridgeServiceImplIT extends WireMockBaseTest {

    @Autowired
    private FileBridgeService fileBridgeService;

    @Test
    public void shouldGetUploadedFiles() throws CommunicationFailureException {

        // given
        FileListDataModel fileListDataModel = prepareFileListDataModel();
        givenThat(get(Path.FILES.getURI()).willReturn(ResponseDefinitionBuilder
                .okForJson(fileListDataModel)));

        // when
        FileListDataModel result = fileBridgeService.getUploadedFiles();

        // then
        assertThat(result, equalTo(fileListDataModel));
        verify(getRequestedFor(urlEqualTo(Path.FILES.getURI()))
                .withHeader(AUTHORIZATION_HEADER, VALUE_PATTERN_BEARER_TOKEN));
    }

    @Test
    public void shouldDownloadFile() throws CommunicationFailureException {

        // given
        UUID fileIdentifier = UUID.randomUUID();
        String filename = "filename";
        String uri = prepareURI(Path.FILES_BY_ID.getURI(), fileIdentifier, filename);
        givenThat(get(uri)
                .willReturn(ok()));

        // when
        fileBridgeService.downloadFile(fileIdentifier, filename);

        // then
        verify(getRequestedFor(urlEqualTo(uri)));
    }

    @Test
    public void shouldUploadFile() throws CommunicationFailureException, JsonProcessingException {

        // given
        FileUploadRequestModel fileUploadRequestModel = prepareFileUploadRequestModel();
        FileDataModel fileDataModel = prepareFileDataModel();
        StringValuePattern requestBody = equalToJson(OBJECT_MAPPER.writeValueAsString(fileUploadRequestModel));
        givenThat(post(Path.FILES.getURI())
                .willReturn(ResponseDefinitionBuilder.okForJson(fileDataModel)));

        // when
        FileDataModel result = fileBridgeService.uploadFile(fileUploadRequestModel);

        // then
        assertThat(result, equalTo(fileDataModel));
        verify(postRequestedFor(urlEqualTo(Path.FILES.getURI()))
                .withRequestBody(requestBody)
                .withHeader(AUTHORIZATION_HEADER, VALUE_PATTERN_BEARER_TOKEN));
    }

    @Test
    public void shouldDeleteFile() throws CommunicationFailureException {

        // given
        UUID fileIdentifier = UUID.randomUUID();
        String filename = "filename";
        String uri = prepareURI(Path.FILES_BY_ID.getURI(), fileIdentifier, filename);
        givenThat(delete(uri));

        // when
        fileBridgeService.deleteFile(fileIdentifier, filename);

        // then
        verify(deleteRequestedFor(urlEqualTo(uri))
                .withHeader(AUTHORIZATION_HEADER, VALUE_PATTERN_BEARER_TOKEN));
    }

    @Test
    public void shouldCreateDirectory() throws CommunicationFailureException, JsonProcessingException {

        // given
        DirectoryCreationRequestModel directoryCreationRequestModel = prepareDirectoryCreationRequestModel();
        StringValuePattern requestBody = equalToJson(OBJECT_MAPPER.writeValueAsString(directoryCreationRequestModel));
        givenThat(post(Path.FILES_DIRECTORY.getURI())
                .withRequestBody(requestBody));

        // when
        fileBridgeService.createDirectory(directoryCreationRequestModel);

        // then
        verify(postRequestedFor(urlEqualTo(Path.FILES_DIRECTORY.getURI()))
                .withRequestBody(requestBody)
                .withHeader(AUTHORIZATION_HEADER, VALUE_PATTERN_BEARER_TOKEN));
    }

    @Test
    public void shouldUpdateFileMetaInfo() throws CommunicationFailureException, JsonProcessingException {

        // given
        UUID fileIdentifier = UUID.randomUUID();
        String filename = "filename";
        String uri = prepareURI(Path.FILES_BY_ID.getURI(), fileIdentifier, filename);
        UpdateFileMetaInfoRequestModel updateFileMetaInfoRequestModel = prepareUpdateFileMetaInfoRequestModel();
        StringValuePattern requestBody = equalToJson(OBJECT_MAPPER.writeValueAsString(updateFileMetaInfoRequestModel));
        givenThat(put(uri)
                .withRequestBody(requestBody));

        // when
        fileBridgeService.updateFileMetaInfo(fileIdentifier, filename, updateFileMetaInfoRequestModel);

        // then
        verify(putRequestedFor(urlEqualTo(uri))
                .withRequestBody(requestBody)
                .withHeader(AUTHORIZATION_HEADER, VALUE_PATTERN_BEARER_TOKEN));
    }

    private UpdateFileMetaInfoRequestModel prepareUpdateFileMetaInfoRequestModel() {
        UpdateFileMetaInfoRequestModel updateFileMetaInfoRequestModel = new UpdateFileMetaInfoRequestModel();
        updateFileMetaInfoRequestModel.setDescription("new description");
        updateFileMetaInfoRequestModel.setOriginalFilename("new filename");
        return updateFileMetaInfoRequestModel;
    }

    private DirectoryCreationRequestModel prepareDirectoryCreationRequestModel() {
        DirectoryCreationRequestModel directoryCreationRequestModel = new DirectoryCreationRequestModel();
        directoryCreationRequestModel.setName("folder");
        directoryCreationRequestModel.setParent("parent");
        return directoryCreationRequestModel;
    }

    private FileUploadRequestModel prepareFileUploadRequestModel() {
        FileUploadRequestModel fileUploadRequestModel = new FileUploadRequestModel();
        fileUploadRequestModel.setDescription("file");
        return fileUploadRequestModel;
    }

    private FileListDataModel prepareFileListDataModel() {
        return FileListDataModel.getBuilder()
                .withItem(prepareFileDataModel())
                .withItem(prepareFileDataModel())
                .build();
    }

    private FileDataModel prepareFileDataModel() {
        return FileDataModel.getBuilder()
                .withReference(UUID.randomUUID().toString())
                .build();
    }
}
