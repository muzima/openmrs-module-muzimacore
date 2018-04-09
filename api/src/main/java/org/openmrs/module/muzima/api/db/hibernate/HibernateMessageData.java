package org.openmrs.module.muzima.api.db.hibernate;

import com.sun.org.apache.xpath.internal.operations.Bool;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.Criteria;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.openmrs.Person;
import org.openmrs.module.muzima.api.db.MessageDataDao;
import org.openmrs.module.muzima.model.MessageData;

import java.util.ArrayList;
import java.util.List;

public class HibernateMessageData extends HibernateDataDao<MessageData> implements MessageDataDao {

    private Log log = LogFactory.getLog(HibernateMessageData.class);

    /**
     * Default constructor.
     */
    protected HibernateMessageData() {
        super(MessageData.class);
    }

    /**
     * Obtains the message based on the given uuid
     *
     * @param uuid String
     * @return MessageData
     */
    @Override
    public MessageData getMessageDataByUuid(String uuid) {
        Criteria criteria = getSessionFactory().getCurrentSession().createCriteria(MessageData.class);
        criteria.add(Restrictions.eq("uuid", uuid));
        return (MessageData) criteria.uniqueResult();
    }

    /**
     * Obtains the messageData based on the given id.
     *
     * @param id Integer
     * @return MessageData
     */
    @Override
    public MessageData getMessageDataById(Integer id) {
        Criteria criteria = getSessionFactory().getCurrentSession().createCriteria(MessageData.class);
        criteria.add(Restrictions.eq("id", id));
        return (MessageData) criteria.uniqueResult();
    }

    /**
     * Obtain the MessageData based on the sender (org.opnemrs.Person)
     *
     * @param sender org.openmrs.Person
     * @return MessageData as generic List
     */
    @SuppressWarnings("unchecked")
    @Override
    public List<MessageData> getMessageDataBySender(Person sender) {
        Criteria criteria = getSessionFactory().getCurrentSession().createCriteria(MessageData.class);
        criteria.add(Restrictions.eq("sender", sender));
        List<Object> results = criteria.list();
        List<MessageData> messageDataResult = new ArrayList<>();
        for (Object result : results) {
            if (result instanceof MessageData) {
                messageDataResult.add((MessageData) result);
            }
        }

        return messageDataResult;
    }

    /**
     * Obtains a List of messages based on receiver (org.openmrs.Person)
     *
     * @param receiver org.openmrs.Person
     * @return MessageData as generic List.
     */
    @Override
    @SuppressWarnings("unchecked")
    public List<MessageData> getMessageDataByReceiver(Person receiver) {
        Criteria criteria = getSessionFactory().getCurrentSession().createCriteria(MessageData.class);
        criteria.add(Restrictions.eq("receiver", receiver));
        List<Object> results = criteria.list();
        List<MessageData> messageDataList = new ArrayList<>();
        for (Object result : results) {
            if (result instanceof MessageData) {
                messageDataList.add((MessageData) result);
            }
        }
        return messageDataList;
    }

    /**
     * Count the existing messageData by sender(org.openmrs.Person)
     *
     * @param sender org.openmrs.Person
     * @return MessageData Count - Number
     */
    @Override
    public Number countMessageDataBySender(Person sender) {
        Criteria criteria = sessionFactory.getCurrentSession().createCriteria(MessageData.class);
        criteria.add(Restrictions.eq("sender", sender));
        criteria.add(Restrictions.eq("voided", Boolean.FALSE));
        criteria.setProjection(Projections.rowCount());
        return (Number) criteria.uniqueResult();
    }

    /**
     * Count the existing messageData by receiver(org.openmrs.Person
     *
     * @param receiver org.openmrs.Person
     * @return Number - A count of Persisted Messages.
     */
    @Override
    public Number countMessageDataByReceiver(Person receiver) {
        Criteria criteria = sessionFactory.getCurrentSession().createCriteria(MessageData.class);
        criteria.add(Restrictions.eq("receiver", receiver));
        criteria.add(Restrictions.eq("voided", Boolean.FALSE));
        criteria.setProjection(Projections.rowCount());
        return (Number) criteria.uniqueResult();
    }
}
