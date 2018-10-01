package org.openmrs.module.muzima.api.db;

import org.openmrs.Person;
import org.openmrs.Role;
import org.openmrs.module.muzima.model.MessageData;

import java.util.List;

public interface MessageDataDao extends DataDao<MessageData>{

    /**
     * Obtains the message based on the given uuid
     * @param uuid String
     * @return MessageData
     */
    MessageData getMessageDataByUuid(String uuid);

    /**
     * Obtains the messageData based on the given id.
     * @param id Integer
     * @return MessageData
     */
    MessageData getMessageDataById(Integer id);

    /**
     * Obtain the MessageData based on the sender (org.opnemrs.Person)
     * @param sender org.openmrs.Person
     * @return MessageData as generic List
     */
    List<MessageData> getMessageDataBySender(Person sender);

    /**
     * Obtains a List of messages based on receiver (org.openmrs.Person)
     * @param receiver org.openmrs.Person
     * @return MessageData as generic List.
     */
    List<MessageData> getMessageDataByReceiver(Person receiver);

    /**
     * Count the existing messageData by sender(org.openmrs.Person)
     * @param sender org.openmrs.Person
     * @return MessageData Count - Number
     */
    Number countMessageDataBySender(final Person sender);

    /**
     * Count the existing messageData by receiver(org.openmrs.Person
     * @param receiver org.openmrs.Person
     * @return Number - A count of Persisted Messages.
     */
    Number countMessageDataByReceiver(final Person receiver);
}
