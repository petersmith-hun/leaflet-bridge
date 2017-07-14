package hu.psprog.leaflet.bridge.service;

import hu.psprog.leaflet.api.rest.request.comment.CommentCreateRequestModel;
import hu.psprog.leaflet.api.rest.request.comment.CommentUpdateRequestModel;
import hu.psprog.leaflet.api.rest.response.comment.CommentDataModel;
import hu.psprog.leaflet.api.rest.response.comment.CommentListDataModel;
import hu.psprog.leaflet.api.rest.response.comment.ExtendedCommentDataModel;
import hu.psprog.leaflet.api.rest.response.common.WrapperBodyDataModel;
import hu.psprog.leaflet.bridge.client.domain.OrderBy;
import hu.psprog.leaflet.bridge.client.domain.OrderDirection;
import hu.psprog.leaflet.bridge.client.exception.CommunicationFailureException;

/**
 * BridgeClient interface for comment related calls.
 *
 * @author Peter Smith
 */
public interface CommentBridgeService {

    /**
     * Retrieves given page of public comments for given entry.
     * Should only be used for retrieving page > 1 of comments as first page will be automatically loaded for entry.
     *
     * @param entryID ID of entry to retrieve comments for
     * @param page page number
     * @param limit number of comments on one page
     * @param orderBy order by {@link OrderBy.Comment} options
     * @param orderDirection order direction (ASC|DESC)
     * @return list of comments
     * @throws CommunicationFailureException if client fails to reach backend application
     */
    WrapperBodyDataModel<CommentListDataModel> getPageOfPublicCommentsForEntry(Long entryID, int page, int limit, OrderBy.Comment orderBy, OrderDirection orderDirection)
            throws CommunicationFailureException;

    /**
     * Retrieves given page of comments for given entry (public and non-public comments as well).
     * Should be used for admin operations.
     *
     * @param entryID ID of entry to retrieve comments for
     * @param page page number
     * @param limit number of comments on one page
     * @param orderBy order by {@link OrderBy.Comment} options
     * @param orderDirection order direction (ASC|DESC)
     * @return list of comments
     * @throws CommunicationFailureException if client fails to reach backend application
     */
    WrapperBodyDataModel<CommentListDataModel> getPageOfCommentsForEntry(Long entryID, int page, int limit, OrderBy.Comment orderBy, OrderDirection orderDirection)
            throws CommunicationFailureException;

    /**
     * Retrieves comment identified by given ID.
     *
     * @param commentID ID of comment to retrieve
     * @return comment data
     * @throws CommunicationFailureException if client fails to reach backend application
     */
    ExtendedCommentDataModel getComment(Long commentID) throws CommunicationFailureException;

    /**
     * Creates a new comment.
     *
     * @param commentCreateRequestModel comment data
     * @return created comment data
     * @throws CommunicationFailureException if client fails to reach backend application
     */
    CommentDataModel createComment(CommentCreateRequestModel commentCreateRequestModel) throws CommunicationFailureException;

    /**
     * Updates given comment.
     *
     * @param commentID ID of the comment to update
     * @param commentUpdateRequestModel new comment data
     * @return updated comment data
     * @throws CommunicationFailureException if client fails to reach backend application
     */
    CommentDataModel updateComment(Long commentID, CommentUpdateRequestModel commentUpdateRequestModel) throws CommunicationFailureException;

    /**
     * Changes comment status (enabled/disabled).
     *
     * @param commentID ID of the comment to change status of
     * @return updated comment data
     * @throws CommunicationFailureException if client fails to reach backend application
     */
    ExtendedCommentDataModel changeStatus(Long commentID) throws CommunicationFailureException;

    /**
     * Logically deletes a comment. Self-moderation function for registered users; can be reverted by admin.
     *
     * @param commentID ID of the comment to logically delete
     * @throws CommunicationFailureException if client fails to reach backend application
     */
    void deleteCommentLogically(Long commentID) throws CommunicationFailureException;

    /**
     * Permanently deletes a comment. Only for admins.
     *
     * @param commentID ID of the comment to permanently delete
     * @throws CommunicationFailureException if client fails to reach backend application
     */
    void deleteCommentPermanently(Long commentID) throws CommunicationFailureException;
}
