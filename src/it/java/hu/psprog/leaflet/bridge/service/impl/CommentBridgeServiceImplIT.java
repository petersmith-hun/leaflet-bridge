package hu.psprog.leaflet.bridge.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.github.tomakehurst.wiremock.client.ResponseDefinitionBuilder;
import com.github.tomakehurst.wiremock.client.WireMock;
import com.github.tomakehurst.wiremock.matching.StringValuePattern;
import hu.psprog.leaflet.api.rest.request.comment.CommentCreateRequestModel;
import hu.psprog.leaflet.api.rest.response.comment.CommentDataModel;
import hu.psprog.leaflet.api.rest.response.comment.CommentListDataModel;
import hu.psprog.leaflet.api.rest.response.comment.ExtendedCommentDataModel;
import hu.psprog.leaflet.api.rest.response.common.WrapperBodyDataModel;
import hu.psprog.leaflet.api.rest.response.user.UserDataModel;
import hu.psprog.leaflet.bridge.client.domain.OrderBy;
import hu.psprog.leaflet.bridge.client.domain.OrderDirection;
import hu.psprog.leaflet.bridge.client.exception.CommunicationFailureException;
import hu.psprog.leaflet.bridge.client.request.Path;
import hu.psprog.leaflet.bridge.it.config.LeafletBridgeITContextConfig;
import hu.psprog.leaflet.bridge.service.CommentBridgeService;
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
 * Integration tests for {@link CommentBridgeServiceImpl}.
 *
 * @author Peter Smith
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = LeafletBridgeITContextConfig.class)
@ActiveProfiles(LeafletBridgeITContextConfig.INTEGRATION_TEST_CONFIG_PROFILE)
public class CommentBridgeServiceImplIT extends WireMockBaseTest {

    @Autowired
    private CommentBridgeService commentBridgeService;

    @Test
    public void shouldGetPageOfPublicCommentsForEntry() throws CommunicationFailureException {

        // given
        Long entryID = 1L;
        int page = 1;
        int limit = 10;
        OrderBy.Comment orderBy = OrderBy.Comment.CREATED;
        OrderDirection orderDirection = OrderDirection.ASC;
        String uri = prepareURI(Path.COMMENTS_PUBLIC_PAGE_BY_ENTRY.getURI(), entryID, page);
        WrapperBodyDataModel<CommentListDataModel> wrappedCommentListDataModel = prepareWrappedListDataModel(prepareCommentListDataModel());
        givenThat(get(urlPathEqualTo(uri))
                .willReturn(ResponseDefinitionBuilder.okForJson(wrappedCommentListDataModel)));

        // when
        WrapperBodyDataModel<CommentListDataModel> result = commentBridgeService.getPageOfPublicCommentsForEntry(entryID, page, limit, orderBy, orderDirection);

        // then
        assertThat(result, equalTo(wrappedCommentListDataModel));
        verify(getRequestedFor(urlPathEqualTo(uri))
                .withQueryParam(LIMIT, WireMock.equalTo(String.valueOf(limit)))
                .withQueryParam(ORDER_BY, WireMock.equalTo(orderBy.getField()))
                .withQueryParam(ORDER_DIRECTION, WireMock.equalTo(String.valueOf(orderDirection))));
    }

    @Test
    public void shouldGetPageOfCommentsForEntry() throws CommunicationFailureException {

        // given
        Long entryID = 1L;
        int page = 1;
        int limit = 10;
        OrderBy.Comment orderBy = OrderBy.Comment.CREATED;
        OrderDirection orderDirection = OrderDirection.ASC;
        String uri = prepareURI(Path.COMMENTS_ALL_PAGE_BY_ENTRY.getURI(), entryID, page);
        WrapperBodyDataModel<CommentListDataModel> wrappedCommentListDataModel = prepareWrappedListDataModel(prepareCommentListDataModel());
        givenThat(get(urlPathEqualTo(uri))
                .willReturn(ResponseDefinitionBuilder.okForJson(wrappedCommentListDataModel)));

        // when
        WrapperBodyDataModel<CommentListDataModel> result = commentBridgeService.getPageOfCommentsForEntry(entryID, page, limit, orderBy, orderDirection);

        // then
        assertThat(result, equalTo(wrappedCommentListDataModel));
        verify(getRequestedFor(urlPathEqualTo(uri))
                .withQueryParam(LIMIT, WireMock.equalTo(String.valueOf(limit)))
                .withQueryParam(ORDER_BY, WireMock.equalTo(orderBy.getField()))
                .withQueryParam(ORDER_DIRECTION, WireMock.equalTo(String.valueOf(orderDirection)))
                .withHeader(AUTHORIZATION_HEADER, VALUE_PATTERN_BEARER_TOKEN));
    }

    @Test
    public void shouldGetComment() throws CommunicationFailureException {

        // given
        Long commentID = 1L;
        String uri = prepareURI(Path.COMMENTS_BY_ID.getURI(), commentID);
        ExtendedCommentDataModel extendedCommentDataModel = prepareExtendedCommentDataModel(commentID);
        givenThat(get(uri)
                .willReturn(ResponseDefinitionBuilder.okForJson(extendedCommentDataModel)));

        // when
        ExtendedCommentDataModel result = commentBridgeService.getComment(commentID);

        // then
        assertThat(result, equalTo(extendedCommentDataModel));
        verify(getRequestedFor(urlEqualTo(uri))
                .withHeader(AUTHORIZATION_HEADER, VALUE_PATTERN_BEARER_TOKEN));
    }

    @Test
    public void shouldCreateComment() throws JsonProcessingException, CommunicationFailureException {

        // given
        CommentCreateRequestModel commentCreateRequestModel = new CommentCreateRequestModel();
        commentCreateRequestModel.setContent("comment");
        StringValuePattern requestBody = equalToJson(OBJECT_MAPPER.writeValueAsString(commentCreateRequestModel));
        CommentDataModel commentDataModel = prepareCommentDataModel(1L);
        givenThat(post(Path.COMMENTS.getURI())
                .withRequestBody(requestBody)
                .willReturn(ResponseDefinitionBuilder.okForJson(commentDataModel)));

        // when
        CommentDataModel result = commentBridgeService.createComment(commentCreateRequestModel);

        // then
        assertThat(result, equalTo(commentDataModel));
        verify(postRequestedFor(urlEqualTo(Path.COMMENTS.getURI()))
                .withHeader(AUTHORIZATION_HEADER, VALUE_PATTERN_BEARER_TOKEN));
    }

    @Test
    public void shouldUpdateComment() throws CommunicationFailureException, JsonProcessingException {

        // given
        Long commentID = 1L;
        CommentCreateRequestModel commentCreateRequestModel = new CommentCreateRequestModel();
        commentCreateRequestModel.setContent("comment");
        StringValuePattern requestBody = equalToJson(OBJECT_MAPPER.writeValueAsString(commentCreateRequestModel));
        CommentDataModel commentDataModel = prepareCommentDataModel(1L);
        String uri = prepareURI(Path.COMMENTS_BY_ID.getURI(), commentID);
        givenThat(put(uri)
                .withRequestBody(requestBody)
                .willReturn(ResponseDefinitionBuilder.okForJson(commentDataModel)));

        // when
        CommentDataModel result = commentBridgeService.updateComment(commentID, commentCreateRequestModel);

        // then
        assertThat(result, equalTo(commentDataModel));
        verify(putRequestedFor(urlEqualTo(uri))
                .withHeader(AUTHORIZATION_HEADER, VALUE_PATTERN_BEARER_TOKEN));
    }

    @Test
    public void shouldChangeStatus() throws CommunicationFailureException {

        // given
        Long commentID = 1L;
        String uri = prepareURI(Path.COMMENTS_STATUS.getURI(), commentID);
        ExtendedCommentDataModel extendedCommentDataModel = prepareExtendedCommentDataModel(commentID);
        givenThat(put(uri)
                .willReturn(ResponseDefinitionBuilder.okForJson(extendedCommentDataModel)));

        // when
        ExtendedCommentDataModel result = commentBridgeService.changeStatus(commentID);

        // then
        assertThat(result, equalTo(extendedCommentDataModel));
        verify(putRequestedFor(urlEqualTo(uri))
                .withHeader(AUTHORIZATION_HEADER, VALUE_PATTERN_BEARER_TOKEN));
    }

    @Test
    public void shouldDeleteLogically() throws CommunicationFailureException {

        // given
        Long commentID = 1L;
        String uri = prepareURI(Path.COMMENTS_BY_ID.getURI(), commentID);
        givenThat(delete(uri));

        // when
        commentBridgeService.deleteCommentLogically(commentID);

        // then
        verify(deleteRequestedFor(urlEqualTo(uri))
                .withHeader(AUTHORIZATION_HEADER, VALUE_PATTERN_BEARER_TOKEN));
    }

    @Test
    public void shouldDeletePermanently() throws CommunicationFailureException {

        // given
        Long commentID = 1L;
        String uri = prepareURI(Path.COMMENTS_DELETE_PERMANENT.getURI(), commentID);
        givenThat(delete(uri));

        // when
        commentBridgeService.deleteCommentPermanently(commentID);

        // then
        verify(deleteRequestedFor(urlEqualTo(uri))
                .withHeader(AUTHORIZATION_HEADER, VALUE_PATTERN_BEARER_TOKEN));
    }

    private CommentListDataModel prepareCommentListDataModel() {
        return CommentListDataModel.getBuilder()
                .withItem(prepareCommentDataModel(1L))
                .withItem(prepareCommentDataModel(2L))
                .build();
    }

    private ExtendedCommentDataModel prepareExtendedCommentDataModel(Long commentID) {
        return ExtendedCommentDataModel.getExtendedBuilder()
                .withId(commentID)
                .withContent("Comment #" + commentID.toString())
                .withCreated("Creation date")
                .withLastModified("Last modification date")
                .withDeleted(false)
                .withOwner(UserDataModel.getBuilder()
                        .withId(1L)
                        .withUsername("User")
                        .build())
                .withEnabled(true)
                .withAssociatedEntry(null)
                .build();
    }

    private CommentDataModel prepareCommentDataModel(Long commentID) {
        return CommentDataModel.getBuilder()
                .withId(commentID)
                .withContent("Comment #" + commentID.toString())
                .withCreated("Creation date")
                .withLastModified("Last modification date")
                .withDeleted(false)
                .withOwner(UserDataModel.getBuilder()
                        .withId(1L)
                        .withUsername("User")
                        .build())
                .build();
    }
}
