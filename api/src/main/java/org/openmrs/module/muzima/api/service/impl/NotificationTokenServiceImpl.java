package org.openmrs.module.muzima.api.service.impl;

import org.openmrs.User;
import org.openmrs.api.impl.BaseOpenmrsService;
import org.openmrs.module.muzima.api.db.NotificationTokenDataDao;
import org.openmrs.module.muzima.api.service.NotificationTokenService;
import org.openmrs.module.muzima.model.NotificationToken;

import java.util.List;

public class NotificationTokenServiceImpl extends BaseOpenmrsService implements NotificationTokenService {

    private NotificationTokenDataDao dao;

    public void setDao(NotificationTokenDataDao dao) {
        this.dao = dao;
    }

    public NotificationTokenDataDao getDao(){
        return dao;
    }

    @Override
    public NotificationToken getNotificationTokenById(String id) {
        return dao.getNotificationTokenById(id);
    }

    @Override
    public NotificationToken saveNotificationToken(NotificationToken notificationToken) {
        return dao.saveNotificationToken(notificationToken);
    }

    @Override
    public List<NotificationToken> getNotificationByUserId(User user) {
        List<NotificationToken> notificationTokens = dao.getNotificationByUserId(user);
        System.out.println("XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXx "+notificationTokens.size());
        return notificationTokens;
    }
}
