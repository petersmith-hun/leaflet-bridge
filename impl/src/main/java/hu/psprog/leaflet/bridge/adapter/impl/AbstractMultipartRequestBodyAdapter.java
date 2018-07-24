package hu.psprog.leaflet.bridge.adapter.impl;

import hu.psprog.leaflet.bridge.adapter.RequestBodyAdapter;
import org.apache.commons.io.FilenameUtils;
import org.glassfish.jersey.media.multipart.BodyPart;
import org.glassfish.jersey.media.multipart.FormDataMultiPart;
import org.glassfish.jersey.media.multipart.file.FileDataBodyPart;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.multipart.MultipartFile;

import javax.ws.rs.core.MediaType;
import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.util.Objects;
import java.util.function.Function;

/**
 * {@link RequestBodyAdapter} implementation for sending multipart/form-data requests.
 * Jersey requires {@link FormDataMultiPart} wrapper for sending such request, therefore this
 * abstract implementation provides the common tools for building up the proper request.
 *
 * @author Peter Smith
 */
abstract class AbstractMultipartRequestBodyAdapter<S extends Serializable> implements RequestBodyAdapter<S, FormDataMultiPart> {

    private static final Logger LOGGER = LoggerFactory.getLogger(AbstractMultipartRequestBodyAdapter.class);
    private static final String EXTENSION_SEPARATOR = ".";

    private String baseDirectory;

    public AbstractMultipartRequestBodyAdapter(String baseDirectory) {
        this.baseDirectory = baseDirectory;
    }

    /**
     * Returns a {@link FormBuilder} instance, that can be used to build the multipart form.
     *
     * @param source instance stores the source object and executes operations on stored instance
     * @return FormBuilder instance
     */
    FormBuilder getMultipartBuilderFor(S source) {
        return new FormBuilder(source);
    }

    /**
     * Flow-like multipart form builder tool.
     */
    class FormBuilder {

        private FormDataMultiPart multiPartForm;
        private S source;

        private FormBuilder(S source) {
            this.multiPartForm = new FormDataMultiPart();
            this.source = source;
        }

        /**
         * Adds given field value of type String to the form.
         *
         * @param name field name
         * @param reference String value extractor {@link Function}
         * @return FormBuilder instance
         */
        FormBuilder withStringField(String name, Function<S, String> reference) {
            multiPartForm.field(name, reference.apply(source));
            return this;
        }

        /**
         * Adds given {@link MultipartFile} field to the form.
         *
         * @param name field name
         * @param reference Multipart value extractor {@link Function}
         * @return FormBuilder instance
         */
        FormBuilder withFileField(String name, Function<S, MultipartFile> reference) {
            BodyPart fileBodyPart = createFileDataBodyPart(name, reference.apply(source));
            if (Objects.nonNull(fileBodyPart)) {
                multiPartForm.bodyPart(fileBodyPart);
            }
            return this;
        }

        /**
         * Retrieves built multipart form instance.
         *
         * @return multipart form as {@link FormDataMultiPart}
         */
        FormDataMultiPart build() {
            return multiPartForm;
        }

        private FileDataBodyPart createFileDataBodyPart(String name, MultipartFile multipartFile) {

            FileDataBodyPart fileDataBodyPart = null;
            if (Objects.nonNull(multipartFile)) {
                fileDataBodyPart = new FileDataBodyPart(name, extractTempFile(multipartFile), extractMediaType(multipartFile));
            }

            return fileDataBodyPart;
        }

        private File extractTempFile(MultipartFile multipartFile) {

            File tempFile = null;
            try {
                tempFile = new File(baseDirectory, getFilename(multipartFile));
                multipartFile.transferTo(tempFile);
            } catch (IOException e) {
                LOGGER.warn("Failed to create temporary file for [{}]. Returning null instance.", multipartFile.getOriginalFilename());
            }

            return tempFile;
        }

        private String getFilename(MultipartFile multipartFile) {
            return getBaseName(multipartFile) + getExtension(multipartFile);
        }

        private String getBaseName(MultipartFile multipartFile) {
            return FilenameUtils.getBaseName(multipartFile.getOriginalFilename());
        }

        private String getExtension(MultipartFile multipartFile) {
            return EXTENSION_SEPARATOR + FilenameUtils.getExtension(multipartFile.getOriginalFilename());
        }

        private MediaType extractMediaType(MultipartFile multipartFile) {
            return MediaType.valueOf(multipartFile.getContentType());
        }
    }
}
