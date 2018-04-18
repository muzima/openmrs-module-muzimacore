package org.openmrs.module.muzima.api.service;

import org.openmrs.Person;
import org.openmrs.Role;
import org.openmrs.api.OpenmrsService;
import org.openmrs.module.muzima.model.*;

import java.util.Date;
import java.util.List;

/**
 */
public interface DataService extends OpenmrsService {

    /**
     * Return the queue data with the given id.
     *
     * @param id the queue data id.
     * @return the queue data with the matching id.
     * @should return queue data with matching id.
     * @should return null when no queue data with matching id.
     */
    QueueData getQueueData(final Integer id);

    /**
     * Return the queue data with the given uuid.
     *
     * @param uuid the queue data uuid.
     * @return the queue data with the matching uuid.
     * @should return queue data with matching uuid.
     * @should return null when no queue data with matching uuid.
     */
    QueueData getQueueDataByUuid(final String uuid);

    /**
     * Return all saved queue data.
     *
     * @return all saved queue data.
     * @should return empty list when no queue data are saved in the database.
     * @should return all saved queue data.
     */
    List<QueueData> getAllQueueData();

    /**
     * Save queue data into the database.
     *
     * @param queueData the queue data.
     * @return saved queue data.
     * @should save queue data into the database.
     */
    QueueData saveQueueData(final QueueData queueData);

    /**
     * Delete queue data from the database.
     *
     * @param queueData the queue data
     * @should remove queue data from the database
     */
    void purgeQueueData(final QueueData queueData);

    /**
     * Get the total number of the queue data in the database with partial matching search term on the payload.
     *
     *
     * @param search the search term.
     * @return the total number of the queue data in the database.
     */
    Number countQueueData(final String search);

    /**
     * Get queue data with matching search term for a particular page.
     *
     * @param search     the search term.
     * @param pageNumber the page number.
     * @param pageSize   the size of the page.
     * @return list of all queue data with matching search term for a particular page.
     */
    List<QueueData> getPagedQueueData(final String search, final Integer pageNumber, final Integer pageSize);

    /**
     * Return the error data with the given id.
     *
     * @param id the error data id.
     * @return the error data with the matching id.
     * @should return error data with matching id.
     * @should return null when no error data with matching id.
     */
    ErrorData getErrorData(final Integer id);

    /**
     * Return the error data with the given uuid.
     *
     * @param uuid the error data uuid.
     * @return the error data with the matching uuid.
     * @should return error data with matching uuid.
     * @should return null when no error data with matching uuid.
     */
    ErrorData getErrorDataByUuid(final String uuid);

    /**
     * Return the registration error data with the given patientUuid.
     *
     * @param patientUuid the error data patientUuid.
     * @return the error data with the matching uuid.
     * @should return error data with matching uuid.
     * @should return null when no error data with matching uuid.
     */
    ErrorData getRegistrationErrorDataByPatientUuid(final String patientUuid);

    /**
     * Return all saved error data.
     *
     * @return all saved error data.
     * @should return empty list when no error data are saved in the database.
     * @should return all saved error data.
     */
    List<ErrorData> getAllErrorData();

    /**
     * Save error data into the database.
     *
     * @param ErrorData the error data.
     * @return saved error data.
     * @should save error data into the database.
     */
    ErrorData saveErrorData(final ErrorData ErrorData);

    /**
     * Delete error data from the database.
     *
     * @param ErrorData the error data
     * @should remove error data from the database
     */
    void purgeErrorData(final ErrorData ErrorData);

    /**
     * Get the total number of the error data in the database with partial matching search term on the payload.
     *
     *
     * @param search the search term.
     * @return the total number of the error data in the database.
     */
    Number countErrorData(final String search);

    /**
     * Get error data with matching search term for a particular page.
     *
     * @param search     the search term.
     * @param pageNumber the page number.
     * @param pageSize   the size of the page.
     * @return list of all error data with matching search term for a particular page.
     */
    List<ErrorData> getPagedErrorData(final String search, final Integer pageNumber, final Integer pageSize);

    /**
     * Return the archive data with the given id.
     *
     * @param id the archive data id.
     * @return the archive data with the matching id.
     * @should return archive data with matching id.
     * @should return null when no archive data with matching id.
     */
    ArchiveData getArchiveData(final Integer id);

    /**
     * Return the archive data with the given uuid.
     *
     * @param uuid the archive data uuid.
     * @return the archive data with the matching uuid.
     * @should return archive data with matching uuid.
     * @should return null when no archive data with matching uuid.
     */
    ArchiveData getArchiveDataByUuid(final String uuid);

    /**
     * Return all saved archive data.
     *
     * @return all saved archive data.
     * @should return empty list when no archive data are saved in the database.
     * @should return all saved archive data.
     */
    List<ArchiveData> getAllArchiveData();

    /**
     * Save archive data into the database.
     *
     * @param ArchiveData the archive data.
     * @return saved archive data.
     * @should save archive data into the database.
     */
    ArchiveData saveArchiveData(final ArchiveData ArchiveData);

    /**
     * Delete archive data from the database.
     *
     * @param ArchiveData the archive data
     * @should remove archive data from the database
     */
    void purgeArchiveData(final ArchiveData ArchiveData);

    /**
     * Get the total number of the archive data in the database with partial matching search term on the payload.
     *
     *
     * @param search the search term.
     * @return the total number of the archive data in the database.
     */
    Number countArchiveData(final String search);

    /**
     * Get archive data with matching search term for a particular page.
     *
     * @param search     the search term.
     * @param pageNumber the page number.
     * @param pageSize   the size of the page.
     * @return list of all archive data with matching search term for a particular page.
     */
    List<ArchiveData> getPagedArchiveData(final String search, final Integer pageNumber, final Integer pageSize);

    /**
     * Return the data source with the given id.
     *
     * @param id the data source id.
     * @return the data source with the matching id.
     * @should return data source with matching id.
     * @should return null when no data source with matching id.
     */
    DataSource getDataSource(final Integer id);

    /**
     * Return the data source with the given uuid.
     *
     * @param uuid the data source uuid.
     * @return the data source with the matching uuid.
     * @should return data source with matching uuid.
     * @should return null when no data source with matching uuid.
     */
    DataSource getDataSourceByUuid(final String uuid);

    /**
     * Return all saved data source.
     *
     * @return all saved data source.
     * @should return empty list when no data source are saved in the database.
     * @should return all saved data source.
     */
    List<DataSource> getAllDataSource();

    /**
     * Save data source into the database.
     *
     * @param DataSource the data source.
     * @return saved data source.
     * @should save data source into the database.
     */
    DataSource saveDataSource(final DataSource DataSource);

    /**
     * Delete data source from the database.
     *
     * @param DataSource the data source
     * @should remove data source from the database
     */
    void purgeDataSource(final DataSource DataSource);

    /**
     * Get the total number of the data source in the database with partial matching search term on the payload.
     *
     *
     * @param search the search term.
     * @return the total number of the data source in the database.
     */
    Number countDataSource(final String search);

    /**
     * Get data source with matching search term for a particular page.
     *
     * @param search     the search term.
     * @param pageNumber the page number.
     * @param pageSize   the size of the page.
     * @return list of all data source with matching search term for a particular page.
     */
    List<DataSource> getPagedDataSource(final String search, final Integer pageNumber, final Integer pageSize);

    /**
     * Return the notification data with the given id.
     *
     * @param id the notification data id.
     * @return the notification data with the matching id.
     * @should return notification data with matching id.
     * @should return null when no notification data with matching id.
     */
    NotificationData getNotificationData(final Integer id);

    /**
     * Return the notification data with the given uuid.
     *
     * @param uuid the notification data uuid.
     * @return the notification data with the matching uuid.
     * @should return notification data with matching uuid.
     * @should return null when no notification data with matching uuid.
     */
    NotificationData getNotificationDataByUuid(final String uuid);

    /**
     * Return all saved notification data.
     *
     * @return all saved notification data.
     * @should return empty list when no notification data are saved in the database.
     * @should return all saved notification data.
     */
    List<NotificationData> getAllNotificationData();

    /**
     * Return all paged notification data for a particular person with matching search term for a particular page.
     *
     * @param search     the search term.
     * @param pageNumber the page number.
     * @param pageSize   the size of the page.
     * @return all saved notification data.
     * @should return empty list when no notification data are saved in the database.
     * @should return all saved notification data.
     */
    List<NotificationData> getNotificationDataByReceiver(final Person person, final String search,
                                                         final Integer pageNumber, final Integer pageSize,
                                                         final String status);

    /**
     * Return paged notification data from a particular person with matching search term for a particular page.
     *
     * @param search     the search term.
     * @param pageNumber the page number.
     * @param pageSize   the size of the page.
     * @return all saved notification data.
     * @should return empty list when no notification data are saved in the database.
     * @should return all saved notification data.
     */
    List<NotificationData> getNotificationDataBySender(final Person person, final String search,
                                                       final Integer pageNumber, final Integer pageSize,
                                                       final String status);

    /**
     * Return count for the paged notification data for a particular person with matching search term for a particular page.
     *
     *
     * @param person the person.
     * @param search the search term.
     * @return all saved notification data.
     * @should return empty list when no notification data are saved in the database.
     * @should return all saved notification data.
     */
    Number countNotificationDataByReceiver(final Person person, final String search, final String status);

    /**
     * Return count for the paged notification data from a particular person with matching search term for a particular page.
     *
     *
     * @param person the person.
     * @param search the search term.
     * @return all saved notification data.
     * @should return empty list when no notification data are saved in the database.
     * @should return all saved notification data.
     */
    Number countNotificationDataBySender(final Person person, final String search, final String status);

    List<NotificationData> getNotificationDataByRole(final Role role, final String search,
                                                     final Integer pageNumber, final Integer pageSize,
                                                     final String status);

    Number countNotificationDataByRole(final Role role, final String search, final String status);

    /**
     * Save notification data into the database.
     *
     * @param notificationData the notification data.
     * @return saved notification data.
     * @should save notification data into the database.
     */
    NotificationData saveNotificationData(final NotificationData notificationData);

    /**
     * Delete notification data from the database.
     *
     * @param notificationData the notification data
     * @should remove notification data from the database
     */
    void purgeNotificationData(final NotificationData notificationData);

    /**
     * Void a single notification data.
     *
     * @param notificationData the notification data to be voided.
     * @return the voided notification data.
     */
    NotificationData voidNotificationData(final NotificationData notificationData, final String reason);

    /**
     * Return the message date by given uuid
     * @param uuid String - the uuid of the requested message.
     * @return Message
     */
    MessageData getMessageDataByUuid(final String uuid);

    /**
     * Return the message data by given id.
     * @param id -Integer
     * @return Message
     */
    MessageData getMessageDataById(final Integer id);

    /**
     * Returns the message data by the sender of the message.
     * @param sender org.openmrs.Sender
     * @return org.openmrs.module.muzimaconsultation.api.model.Message
     */
    List<MessageData> getMessageDataBySender(final Person sender);

    /**
     * Return the Message data by receiver of the message as filter
     * of the collection.
     * @param receiver org.openmrs.Person
     * @return org.openmrs.module.muzimaconsultation.api.model.Message
     */
    List<MessageData> getMessageDataByReceiver(final Person receiver);

    /**
     * Saves the message.
     */
    void saveMessageData(final MessageData messageData);

    /**
     * Purge the defined MessageData passed to this method call.
     * @param messageData MessageData.
     */
    void purgeMessageData(final MessageData messageData);

    /**
     * Void the given MessageData
     * @param uuid String
     * @param messageData MessageData.
     */
    void voidMessageData(final String uuid,final MessageData messageData,final Date voidedDate,final String voidReason);

    /**
     * Return the error message with the given id.
     *
     * @param id the error message id.
     * @return the error message with the matching id.
     * @should return error message with matching id.
     * @should return null when no error message with matching id.
     */
    ErrorMessage getErrorMessage(final Integer id);

    /**
     * Return the error message with the given uuid.
     *
     * @param uuid the error message uuid.
     * @return the error message with the matching uuid.
     * @should return error message with matching uuid.
     * @should return null when no error message with matching uuid.
     */
    ErrorMessage getErrorMessageByUuid(final String uuid);

    /**
     * Return all saved error message.
     *
     * @return all saved error message.
     * @should return empty list when no error message are saved in the messagebase.
     * @should return all saved error message.
     */
    List<ErrorMessage> getAllErrorMessage();

    /**
     * Save error message into the messagebase.
     *
     * @param Errormessage the error message.
     * @return saved error message.
     * @should save error message into the messagebase.
     */
    ErrorMessage saveErrorMessage(final ErrorMessage Errormessage);

    /**
     * Delete error message from the messagebase.
     *
     * @param Errormessage the error message
     * @should remove error message from the messagebase
     */
    void purgeErrorMessage(final ErrorMessage Errormessage);

    /**
     * Get the total number of the error message in the messagebase with partial matching search term on the payload.
     *
     *
     * @param search the search term.
     * @return the total number of the error message in the messagebase.
     */
    Number countErrorMessage(final String search);

    Number countMessageDataBySender(final Person sender);

    Number countMessageDataByReceiver(final Person receiver);

    Number countMessageData();

    /**
     * Get error message with matching search term for a particular page.
     *
     * @param search     the search term.
     * @param pageNumber the page number.
     * @param pageSize   the size of the page.
     * @return list of all error message with matching search term for a particular page.
     */
    List<ErrorMessage> getPagedErrorMessage(final String search, final Integer pageNumber, final Integer pageSize);

    List<ErrorMessage> validateData(String uuid, String formData);

    List<String> getDiscriminatorTypes();

}
