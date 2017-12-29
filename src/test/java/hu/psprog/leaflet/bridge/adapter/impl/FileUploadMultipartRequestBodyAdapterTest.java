package hu.psprog.leaflet.bridge.adapter.impl;

import hu.psprog.leaflet.api.rest.request.file.FileUploadRequestModel;
import org.glassfish.jersey.media.multipart.FormDataMultiPart;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.util.ReflectionUtils;

import java.lang.reflect.Field;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;

/**
 * Unit tests for {@link FileUploadMultipartRequestBodyAdapter}.
 *
 * author Peter Smith
 */
@RunWith(MockitoJUnitRunner.class)
public class FileUploadMultipartRequestBodyAdapterTest {

    private static final String DESCRIPTION = "Mocked file upload";
    private static final String SUB_FOLDER = "mocked_test";
    private static final String MOCKED_FILE_NAME = "inputFile";
    private static final String MOCKED_FILE_FILENAME = "mocked_input_file.jpg";
    private static final String MOCKED_FILE_MEDIA_TYPE = "image/jpeg";
    private static final byte[] MOCKED_FILE_CONTENT = "file_content".getBytes();

    private static final String FIELD_DESCRIPTION = "description";
    private static final String FIELD_SUB_FOLDER = "subFolder";
    private static final String FIELD_INPUT_FILE = "inputFile";

    @InjectMocks
    private FileUploadMultipartRequestBodyAdapter adapter;

    @Before
    public void setup() throws IllegalAccessException {
        setBaseDirectory();
    }

    @Test
    public void shouldAdaptFileUploadRequest() {

        // given
        FileUploadRequestModel request = prepareFileUploadRequestModel(true);

        // when
        FormDataMultiPart result = adapter.adapt(request);

        // then
        assertThat(result, notNullValue());
        assertThat(result.getBodyParts().size(), equalTo(3));
        assertThat(result.getField(FIELD_DESCRIPTION).getValue(), equalTo(DESCRIPTION));
        assertThat(result.getField(FIELD_SUB_FOLDER).getValue(), equalTo(SUB_FOLDER));
        assertThat(result.getField(FIELD_INPUT_FILE).getMediaType().toString(), equalTo(MOCKED_FILE_MEDIA_TYPE));
    }

    @Test
    public void shouldAdaptFileUploadRequestWithoutFile() {

        // given
        FileUploadRequestModel request = prepareFileUploadRequestModel(false);

        // when
        FormDataMultiPart result = adapter.adapt(request);

        // then
        assertThat(result, notNullValue());
        assertThat(result.getBodyParts().size(), equalTo(2));
        assertThat(result.getField(FIELD_DESCRIPTION).getValue(), equalTo(DESCRIPTION));
        assertThat(result.getField(FIELD_SUB_FOLDER).getValue(), equalTo(SUB_FOLDER));
    }

    private void setBaseDirectory() throws IllegalAccessException {
        Field baseDirectoryField = ReflectionUtils.findField(FileUploadMultipartRequestBodyAdapter.class, "baseDirectory");
        baseDirectoryField.setAccessible(true);
        baseDirectoryField.set(adapter, System.getProperty("java.io.tmpdir"));
    }

    private FileUploadRequestModel prepareFileUploadRequestModel(boolean withFile) {

        FileUploadRequestModel request = new FileUploadRequestModel();
        request.setDescription(DESCRIPTION);
        request.setSubFolder(SUB_FOLDER);
        if (withFile) {
            request.setInputFile(new MockMultipartFile(MOCKED_FILE_NAME, MOCKED_FILE_FILENAME, MOCKED_FILE_MEDIA_TYPE, MOCKED_FILE_CONTENT));
        }

        return request;
    }
}