package hu.psprog.leaflet.bridge.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.github.tomakehurst.wiremock.client.ResponseDefinitionBuilder;
import com.github.tomakehurst.wiremock.client.WireMock;
import com.github.tomakehurst.wiremock.matching.StringValuePattern;
import hu.psprog.leaflet.api.rest.request.entry.EntryCreateRequestModel;
import hu.psprog.leaflet.api.rest.response.common.WrapperBodyDataModel;
import hu.psprog.leaflet.api.rest.response.entry.EditEntryDataModel;
import hu.psprog.leaflet.api.rest.response.entry.EntryDataModel;
import hu.psprog.leaflet.api.rest.response.entry.EntryListDataModel;
import hu.psprog.leaflet.api.rest.response.entry.ExtendedEntryDataModel;
import hu.psprog.leaflet.bridge.client.domain.OrderBy;
import hu.psprog.leaflet.bridge.client.domain.OrderDirection;
import hu.psprog.leaflet.bridge.client.exception.CommunicationFailureException;
import hu.psprog.leaflet.bridge.client.request.Path;
import hu.psprog.leaflet.bridge.it.config.LeafletBridgeITContextConfig;
import hu.psprog.leaflet.bridge.service.EntryBridgeService;
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
import static com.github.tomakehurst.wiremock.client.WireMock.post;
import static com.github.tomakehurst.wiremock.client.WireMock.postRequestedFor;
import static com.github.tomakehurst.wiremock.client.WireMock.put;
import static com.github.tomakehurst.wiremock.client.WireMock.putRequestedFor;
import static com.github.tomakehurst.wiremock.client.WireMock.urlEqualTo;
import static com.github.tomakehurst.wiremock.client.WireMock.urlPathEqualTo;
import static com.github.tomakehurst.wiremock.client.WireMock.verify;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;

/**
 * Integration tests for {@link EntryBridgeServiceImpl}.
 *
 * @author Peter Smith
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = LeafletBridgeITContextConfig.class)
@ActiveProfiles(LeafletBridgeITContextConfig.INTEGRATION_TEST_CONFIG_PROFILE)
public class EntryBridgeServiceImplIT extends WireMockBaseTest {

    @Autowired
    private EntryBridgeService entryBridgeService;

    @Test
    public void shouldGetAllEntries() throws CommunicationFailureException {

        // given
        EntryListDataModel entryListDataModel = prepareEntryListDataModel();
        givenThat(get(Path.ENTRIES.getURI())
                .willReturn(ResponseDefinitionBuilder.okForJson(entryListDataModel)));

        // when
        EntryListDataModel result = entryBridgeService.getAllEntries();

        // then
        assertThat(result, equalTo(entryListDataModel));
        verify(getRequestedFor(urlEqualTo(Path.ENTRIES.getURI()))
                .withHeader(AUTHORIZATION_HEADER, VALUE_PATTERN_BEARER_TOKEN));
    }

    @Test
    public void shouldGetPageOfPublicEntries() throws CommunicationFailureException {

        // given
        EntryListDataModel entryListDataModel = prepareEntryListDataModel();
        WrapperBodyDataModel<EntryListDataModel> wrappedEntryListDataModel = prepareWrappedListDataModel(entryListDataModel);
        int page = 1;
        int limit = 10;
        OrderBy.Entry orderBy = OrderBy.Entry.CREATED;
        OrderDirection orderDirection = OrderDirection.ASC;
        String uri = prepareURI(Path.ENTRIES_PAGE.getURI(), page);
        givenThat(get(urlPathEqualTo(uri))
                .willReturn(ResponseDefinitionBuilder.okForJson(wrappedEntryListDataModel)));

        // when
        WrapperBodyDataModel<EntryListDataModel> result = entryBridgeService.getPageOfPublicEntries(page, limit, orderBy, orderDirection);

        // then
        assertThat(result, equalTo(wrappedEntryListDataModel));
        verify(getRequestedFor(urlPathEqualTo(uri))
                .withQueryParam(LIMIT, WireMock.equalTo(String.valueOf(limit)))
                .withQueryParam(ORDER_BY, WireMock.equalTo(orderBy.name()))
                .withQueryParam(ORDER_DIRECTION, WireMock.equalTo(String.valueOf(orderDirection))));
    }

    @Test
    public void shouldGetPageOfEntries() throws CommunicationFailureException {

        // given
        EntryListDataModel entryListDataModel = prepareEntryListDataModel();
        WrapperBodyDataModel<EntryListDataModel> wrappedEntryListDataModel = prepareWrappedListDataModel(entryListDataModel);
        int page = 1;
        int limit = 10;
        OrderBy.Entry orderBy = OrderBy.Entry.CREATED;
        OrderDirection orderDirection = OrderDirection.ASC;
        String uri = prepareURI(Path.ENTRIES_PAGE_ALL.getURI(), page);
        givenThat(get(urlPathEqualTo(uri))
                .willReturn(ResponseDefinitionBuilder.okForJson(wrappedEntryListDataModel)));

        // when
        WrapperBodyDataModel<EntryListDataModel> result = entryBridgeService.getPageOfEntries(page, limit, orderBy, orderDirection);

        // then
        assertThat(result, equalTo(wrappedEntryListDataModel));
        verify(getRequestedFor(urlPathEqualTo(uri))
                .withQueryParam(LIMIT, WireMock.equalTo(String.valueOf(limit)))
                .withQueryParam(ORDER_BY, WireMock.equalTo(orderBy.name()))
                .withQueryParam(ORDER_DIRECTION, WireMock.equalTo(String.valueOf(orderDirection)))
                .withHeader(AUTHORIZATION_HEADER, VALUE_PATTERN_BEARER_TOKEN));
    }

    @Test
    public void shouldGetPageOfPublicEntriesByCategory() throws CommunicationFailureException {

        // given
        EntryListDataModel entryListDataModel = prepareEntryListDataModel();
        WrapperBodyDataModel<EntryListDataModel> wrappedEntryListDataModel = prepareWrappedListDataModel(entryListDataModel);
        Long categoryID = 1L;
        int page = 1;
        int limit = 10;
        OrderBy.Entry orderBy = OrderBy.Entry.CREATED;
        OrderDirection orderDirection = OrderDirection.ASC;
        String uri = prepareURI(Path.ENTRIES_CATEGORY_PAGE.getURI(), categoryID, page);
        givenThat(get(urlPathEqualTo(uri))
                .willReturn(ResponseDefinitionBuilder.okForJson(wrappedEntryListDataModel)));

        // when
        WrapperBodyDataModel<EntryListDataModel> result = entryBridgeService.getPageOfPublicEntriesByCategory(categoryID, page, limit, orderBy, orderDirection);

        // then
        assertThat(result, equalTo(wrappedEntryListDataModel));
        verify(getRequestedFor(urlPathEqualTo(uri))
                .withQueryParam(LIMIT, WireMock.equalTo(String.valueOf(limit)))
                .withQueryParam(ORDER_BY, WireMock.equalTo(orderBy.name()))
                .withQueryParam(ORDER_DIRECTION, WireMock.equalTo(String.valueOf(orderDirection))));
    }

    @Test
    public void shouldGetEntryByLink() throws CommunicationFailureException {

        // given
        ExtendedEntryDataModel extendedEntryDataModel = prepareExtendedEntryDataModel(1L);
        WrapperBodyDataModel<ExtendedEntryDataModel> wrappedEntryDataModel = prepareWrappedListDataModel(extendedEntryDataModel);
        String link = "entry-1";
        String uri = prepareURI(Path.ENTRIES_BY_LINK.getURI(), link);
        givenThat(get(uri)
                .willReturn(ResponseDefinitionBuilder.okForJson(wrappedEntryDataModel)));

        // when
        WrapperBodyDataModel<ExtendedEntryDataModel> result = entryBridgeService.getEntryByLink(link);

        // then
        assertThat(result, equalTo(wrappedEntryDataModel));
        verify(getRequestedFor(urlEqualTo(uri)));
    }

    @Test
    public void shouldGetEntryByID() throws CommunicationFailureException {

        // given
        EditEntryDataModel extendedEntryDataModel = prepareEditEntryDataModel(1L);
        WrapperBodyDataModel<EditEntryDataModel> wrappedEntryDataModel = prepareWrappedListDataModel(extendedEntryDataModel);
        Long entryID = 1L;
        String uri = prepareURI(Path.ENTRIES_BY_ID.getURI(), entryID);
        givenThat(get(uri)
                .willReturn(ResponseDefinitionBuilder.okForJson(wrappedEntryDataModel)));

        // when
        WrapperBodyDataModel<EditEntryDataModel> result = entryBridgeService.getEntryByID(entryID);

        // then
        assertThat(result, equalTo(wrappedEntryDataModel));
        verify(getRequestedFor(urlEqualTo(uri))
                .withHeader(AUTHORIZATION_HEADER, VALUE_PATTERN_BEARER_TOKEN));
    }

    @Test
    public void shouldCreateEntry() throws JsonProcessingException, CommunicationFailureException {

        // given
        EntryCreateRequestModel entryCreateRequestModel = prepareEntryCreateRequestModel();
        EditEntryDataModel extendedEntryDataModel = prepareEditEntryDataModel(1L);
        StringValuePattern requestBody = equalToJson(OBJECT_MAPPER.writeValueAsString(entryCreateRequestModel));
        givenThat(post(Path.ENTRIES.getURI())
                .withRequestBody(requestBody)
                .willReturn(ResponseDefinitionBuilder.okForJson(extendedEntryDataModel)));

        // when
        EditEntryDataModel result = entryBridgeService.createEntry(entryCreateRequestModel);

        // then
        assertThat(result, equalTo(extendedEntryDataModel));
        verify(postRequestedFor(urlEqualTo(Path.ENTRIES.getURI()))
                .withHeader(AUTHORIZATION_HEADER, VALUE_PATTERN_BEARER_TOKEN));
    }

    @Test
    public void shouldUpdateEntry() throws JsonProcessingException, CommunicationFailureException {

        // given
        EntryCreateRequestModel entryCreateRequestModel = prepareEntryCreateRequestModel();
        EditEntryDataModel extendedEntryDataModel = prepareEditEntryDataModel(1L);
        StringValuePattern requestBody = equalToJson(OBJECT_MAPPER.writeValueAsString(entryCreateRequestModel));
        Long entryID = 1L;
        String uri = prepareURI(Path.ENTRIES_BY_ID.getURI(), entryID);
        givenThat(put(uri)
                .withRequestBody(requestBody)
                .willReturn(ResponseDefinitionBuilder.okForJson(extendedEntryDataModel)));

        // when
        EditEntryDataModel result = entryBridgeService.updateEntry(entryID, entryCreateRequestModel);

        // then
        assertThat(result, equalTo(extendedEntryDataModel));
        verify(putRequestedFor(urlEqualTo(uri))
                .withHeader(AUTHORIZATION_HEADER, VALUE_PATTERN_BEARER_TOKEN));
    }

    @Test
    public void shouldChangeStatus() throws CommunicationFailureException {

        // given
        EditEntryDataModel extendedEntryDataModel = prepareEditEntryDataModel(1L);
        Long entryID = 1L;
        String uri = prepareURI(Path.ENTRIES_STATUS.getURI(), entryID);
        givenThat(put(uri)
                .willReturn(ResponseDefinitionBuilder.okForJson(extendedEntryDataModel)));

        // when
        EditEntryDataModel result = entryBridgeService.changeStatus(entryID);

        // then
        assertThat(result, equalTo(extendedEntryDataModel));
        verify(putRequestedFor(urlEqualTo(uri))
                .withHeader(AUTHORIZATION_HEADER, VALUE_PATTERN_BEARER_TOKEN));
    }

    @Test
    public void shouldDeleteEntry() throws CommunicationFailureException {

        // given
        Long entryID = 1L;
        String uri = prepareURI(Path.ENTRIES_BY_ID.getURI(), entryID);
        givenThat(delete(uri));

        // when
        entryBridgeService.deleteEntry(entryID);

        // then
        verify(deleteRequestedFor(urlEqualTo(uri))
                .withHeader(AUTHORIZATION_HEADER, VALUE_PATTERN_BEARER_TOKEN));
    }

    private EntryCreateRequestModel prepareEntryCreateRequestModel() {
        EntryCreateRequestModel entryCreateRequestModel = new EntryCreateRequestModel();
        entryCreateRequestModel.setContent("entry");
        return entryCreateRequestModel;
    }

    private EntryListDataModel prepareEntryListDataModel() {
        return EntryListDataModel.getBuilder()
                .withItem(prepareEntryDataModel(1L))
                .withItem(prepareEntryDataModel(2L))
                .build();
    }

    private EntryDataModel prepareEntryDataModel(Long entryID) {
        return EntryDataModel.getBuilder()
                .withId(entryID)
                .withLink("entry-" + entryID)
                .withTitle("Entry #" + entryID)
                .build();
    }

    private ExtendedEntryDataModel prepareExtendedEntryDataModel(Long entryID) {
        return ExtendedEntryDataModel.getExtendedBuilder()
                .withId(entryID)
                .withLink("entry-" + entryID)
                .withTitle("Entry #" + entryID)
                .build();
    }

    private EditEntryDataModel prepareEditEntryDataModel(Long entryID) {
        return EditEntryDataModel.getExtendedBuilder()
                .withId(entryID)
                .withLink("entry-" + entryID)
                .withTitle("Entry #" + entryID)
                .withRawContent("raw-content" + entryID)
                .build();
    }
}
