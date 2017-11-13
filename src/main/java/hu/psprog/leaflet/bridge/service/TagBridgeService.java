package hu.psprog.leaflet.bridge.service;

import hu.psprog.leaflet.api.rest.request.tag.TagAssignmentRequestModel;
import hu.psprog.leaflet.api.rest.request.tag.TagCreateRequestModel;
import hu.psprog.leaflet.api.rest.response.common.WrapperBodyDataModel;
import hu.psprog.leaflet.api.rest.response.tag.TagDataModel;
import hu.psprog.leaflet.api.rest.response.tag.TagListDataModel;
import hu.psprog.leaflet.bridge.client.exception.CommunicationFailureException;

/**
 * BridgeClient interface for tag related calls.
 *
 * @author Peter Smith
 */
public interface TagBridgeService {

    /**
     * Retrieves all existing tags.
     *
     * @return list of tags
     * @throws CommunicationFailureException if client fails to reach backend application
     */
    TagListDataModel getAllTags() throws CommunicationFailureException;

    /**
     * Retrieves existing public tags.
     *
     * @return list of public tags
     * @throws CommunicationFailureException if client fails to reach backend application
     */
    WrapperBodyDataModel<TagListDataModel> getAllPublicTags() throws CommunicationFailureException;

    /**
     * Retrieves tag identified by given ID.
     *
     * @param tagID ID of the tag to retrieve
     * @return tag data as {@link TagDataModel}
     * @throws CommunicationFailureException if client fails to reach backend application
     */
    TagDataModel getTag(Long tagID) throws CommunicationFailureException;

    /**
     * Creates a new tag.
     *
     * @param tagCreateRequestModel tag data as {@link TagCreateRequestModel}.
     * @return created tag's data
     * @throws CommunicationFailureException if client fails to reach backend application
     */
    TagDataModel createTag(TagCreateRequestModel tagCreateRequestModel) throws CommunicationFailureException;

    /**
     * Updates an existing tag.
     *
     * @param tagID ID of an existing tag
     * @param tagCreateRequestModel tag data as {@link TagCreateRequestModel}.
     * @return updated tag's data
     * @throws CommunicationFailureException if client fails to reach backend application
     */
    TagDataModel updateTag(Long tagID, TagCreateRequestModel tagCreateRequestModel) throws CommunicationFailureException;

    /**
     * Deletes an existing tag.
     *
     * @param tagID ID of an existing tag
     * @throws CommunicationFailureException if client fails to reach backend application
     */
    void deleteTag(Long tagID) throws CommunicationFailureException;

    /**
     * Changes given tag's status (enabled/disabled).
     *
     * @param tagID ID of an existing tag
     * @return updated tag's data
     * @throws CommunicationFailureException if client fails to reach backend application
     */
    TagDataModel changeStatus(Long tagID) throws CommunicationFailureException;

    /**
     * Assigns a tag to an entry.
     *
     * @param tagAssignmentRequestModel IDs of the entry and the tag to assign as {@link TagAssignmentRequestModel}
     * @throws CommunicationFailureException if client fails to reach backend application
     */
    void attachTag(TagAssignmentRequestModel tagAssignmentRequestModel) throws CommunicationFailureException;

    /**
     * Un-assigns a tag from an entry.
     *
     * @param tagAssignmentRequestModel IDs of the entry and the tag to un-assign as {@link TagAssignmentRequestModel}
     * @throws CommunicationFailureException if client fails to reach backend application
     */
    void detachTag(TagAssignmentRequestModel tagAssignmentRequestModel) throws CommunicationFailureException;
}
