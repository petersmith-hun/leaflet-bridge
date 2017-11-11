package hu.psprog.leaflet.bridge.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.github.tomakehurst.wiremock.client.ResponseDefinitionBuilder;
import com.github.tomakehurst.wiremock.matching.StringValuePattern;
import hu.psprog.leaflet.api.rest.request.category.CategoryCreateRequestModel;
import hu.psprog.leaflet.api.rest.response.category.CategoryDataModel;
import hu.psprog.leaflet.api.rest.response.category.CategoryListDataModel;
import hu.psprog.leaflet.api.rest.response.common.WrapperBodyDataModel;
import hu.psprog.leaflet.bridge.client.exception.CommunicationFailureException;
import hu.psprog.leaflet.bridge.client.request.Path;
import hu.psprog.leaflet.bridge.it.config.LeafletBridgeITContextConfig;
import hu.psprog.leaflet.bridge.service.CategoryBridgeService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

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
 * Integration tests for {@link CategoryBridgeServiceImpl}.
 *
 * @author Peter Smith
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = LeafletBridgeITContextConfig.class)
@ActiveProfiles(LeafletBridgeITContextConfig.INTEGRATION_TEST_CONFIG_PROFILE)
public class CategoryBridgeServiceImplIT extends WireMockBaseTest {

    @Autowired
    private CategoryBridgeService categoryBridgeService;

    @Test
    public void shouldGetAllCategories() throws CommunicationFailureException {

        // given
        CategoryListDataModel categoryListDataModel = prepareCategoryListDataModel();
        givenThat(get(Path.CATEGORIES.getURI())
                .willReturn(ResponseDefinitionBuilder.okForJson(categoryListDataModel)));

        // when
        CategoryListDataModel result = categoryBridgeService.getAllCategories();

        // then
        verify(getRequestedFor(urlEqualTo(Path.CATEGORIES.getURI()))
                .withHeader(AUTHORIZATION_HEADER, VALUE_PATTERN_BEARER_TOKEN));
        assertThat(result, equalTo(categoryListDataModel));

    }

    @Test
    public void shouldGetPublicCategories() throws CommunicationFailureException {

        // given
        WrapperBodyDataModel<CategoryListDataModel> wrapperBodyDataModel =
                prepareWrappedListDataModel(prepareCategoryListDataModel());
        givenThat(get(Path.CATEGORIES_PUBLIC.getURI())
                .willReturn(ResponseDefinitionBuilder.okForJson(wrapperBodyDataModel)));

        // when
        WrapperBodyDataModel<CategoryListDataModel> result = categoryBridgeService.getPublicCategories();

        // then
        verify(getRequestedFor(urlEqualTo(Path.CATEGORIES_PUBLIC.getURI())));
        assertThat(result, equalTo(wrapperBodyDataModel));

    }

    @Test
    public void shouldGetCategory() throws CommunicationFailureException {

        // given
        Long categoryID = 1L;
        CategoryDataModel categoryDataModel = prepareCategoryDataModel(categoryID);
        String uri = prepareURI(Path.CATEGORIES_BY_ID.getURI(), categoryID);
        givenThat(get(uri)
                .willReturn(ResponseDefinitionBuilder.okForJson(categoryDataModel)));

        // when
        CategoryDataModel result = categoryBridgeService.getCategory(categoryID);

        // then
        verify(getRequestedFor(urlEqualTo(uri))
                .withHeader(AUTHORIZATION_HEADER, VALUE_PATTERN_BEARER_TOKEN));
        assertThat(result, equalTo(categoryDataModel));
    }

    @Test
    public void shouldCreateCategory() throws CommunicationFailureException, JsonProcessingException {

        // given
        CategoryDataModel categoryDataModel = prepareCategoryDataModel(1L);
        CategoryCreateRequestModel categoryCreateRequestModel = prepareCategoryCreateRequestModel();
        StringValuePattern requestBody = equalToJson(OBJECT_MAPPER.writeValueAsString(categoryCreateRequestModel));
        givenThat(post(Path.CATEGORIES.getURI())
                .withRequestBody(requestBody)
                .willReturn(ResponseDefinitionBuilder.like(ResponseDefinitionBuilder
                        .jsonResponse(categoryDataModel, 201))));

        // when
        CategoryDataModel result = categoryBridgeService.createCategory(categoryCreateRequestModel);

        // then
        verify(postRequestedFor(urlEqualTo(Path.CATEGORIES.getURI()))
                .withRequestBody(requestBody)
                .withHeader(AUTHORIZATION_HEADER, VALUE_PATTERN_BEARER_TOKEN));
        assertThat(result, equalTo(categoryDataModel));
    }

    @Test
    public void shouldUpdateCategory() throws JsonProcessingException, CommunicationFailureException {

        // given
        Long categoryID = 1L;
        CategoryDataModel categoryDataModel = prepareCategoryDataModel(1L);
        CategoryCreateRequestModel categoryCreateRequestModel = prepareCategoryCreateRequestModel();
        StringValuePattern requestBody = equalToJson(OBJECT_MAPPER.writeValueAsString(categoryCreateRequestModel));
        String uri = prepareURI(Path.CATEGORIES_BY_ID.getURI(), categoryID);
        givenThat(put(uri)
                .withRequestBody(requestBody)
                .willReturn(ResponseDefinitionBuilder.like(ResponseDefinitionBuilder
                        .jsonResponse(categoryDataModel, 201))));

        // when
        CategoryDataModel result = categoryBridgeService.updateCategory(categoryID, categoryCreateRequestModel);

        // then
        verify(putRequestedFor(urlEqualTo(uri))
                .withRequestBody(requestBody)
                .withHeader(AUTHORIZATION_HEADER, VALUE_PATTERN_BEARER_TOKEN));
        assertThat(result, equalTo(categoryDataModel));
    }

    @Test
    public void shouldChangeStatus() throws CommunicationFailureException {

        // given
        Long categoryID = 1L;
        String uri = prepareURI(Path.CATEGORIES_STATUS.getURI(), categoryID);
        CategoryDataModel categoryDataModel = prepareCategoryDataModel(1L);
        givenThat(put(uri)
                .willReturn(ResponseDefinitionBuilder.like(ResponseDefinitionBuilder
                        .jsonResponse(categoryDataModel, 201))));

        // when
        CategoryDataModel result = categoryBridgeService.changeStatus(categoryID);

        // then
        verify(putRequestedFor(urlEqualTo(uri))
                .withHeader(AUTHORIZATION_HEADER, VALUE_PATTERN_BEARER_TOKEN));
        assertThat(result, equalTo(categoryDataModel));
    }

    @Test
    public void shouldDeleteCategory() throws CommunicationFailureException {

        // given
        Long categoryID = 1L;
        String uri = prepareURI(Path.CATEGORIES_BY_ID.getURI(), categoryID);
        givenThat(delete(uri).willReturn(ok()));

        // then
        categoryBridgeService.deleteCategory(categoryID);

        // then
        verify(deleteRequestedFor(urlEqualTo(uri))
                .withHeader(AUTHORIZATION_HEADER, VALUE_PATTERN_BEARER_TOKEN));
    }

    private CategoryCreateRequestModel prepareCategoryCreateRequestModel() {
        CategoryCreateRequestModel categoryCreateRequestModel = new CategoryCreateRequestModel();
        categoryCreateRequestModel.setTitle("Category #1");
        categoryCreateRequestModel.setDescription("Description for category");
        return categoryCreateRequestModel;
    }

    private CategoryListDataModel prepareCategoryListDataModel() {
        return CategoryListDataModel.getBuilder()
                .withItem(prepareCategoryDataModel(1L))
                .withItem(prepareCategoryDataModel(2L))
                .build();
    }

    private CategoryDataModel prepareCategoryDataModel(Long categoryID) {
        return CategoryDataModel.getBuilder()
                .withID(categoryID)
                .withTitle("Category #" + categoryID.toString())
                .withDescription("Description for category")
                .withCreated("Creation date")
                .withLastModified("Last modification date")
                .build();
    }
}
