package hu.psprog.leaflet.bridge.adapter.impl;

import hu.psprog.leaflet.api.rest.request.file.FileUploadRequestModel;
import hu.psprog.leaflet.bridge.adapter.RequestBodyAdapter;
import org.glassfish.jersey.media.multipart.FormDataMultiPart;
import org.springframework.stereotype.Component;

/**
 * {@link RequestBodyAdapter} implementation for {@link FileUploadRequestModel}.
 * Adapter will convert the {@link FileUploadRequestModel} to Jersey-compatible
 * multipart/form-data wrapper object.
 *
 * @author Peter Smith
 */
@Component
public class FileUploadMultipartRequestBodyAdapter extends AbstractMultipartRequestBodyAdapter<FileUploadRequestModel> {

    private static final String FIELD_DESCRIPTION = "description";
    private static final String FIELD_SUB_FOLDER = "subFolder";
    private static final String FIELD_INPUT_FILE = "inputFile";

    @Override
    public FormDataMultiPart adapt(FileUploadRequestModel source) {

        return getMultipartBuilderFor(source)
                .withStringField(FIELD_DESCRIPTION, FileUploadRequestModel::getDescription)
                .withStringField(FIELD_SUB_FOLDER, FileUploadRequestModel::getSubFolder)
                .withFileField(FIELD_INPUT_FILE, FileUploadRequestModel::getInputFile)
                .build();
    }
}
