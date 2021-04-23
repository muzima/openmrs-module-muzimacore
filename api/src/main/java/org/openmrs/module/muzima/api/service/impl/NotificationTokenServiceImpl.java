package org.openmrs.module.muzima.api.service.impl;

import org.openmrs.User;
import org.openmrs.api.impl.BaseOpenmrsService;
import org.openmrs.module.muzima.api.db.NotificationTokenDataDao;
import org.openmrs.module.muzima.api.service.NotificationTokenService;
import org.openmrs.module.muzima.model.NotificationToken;

import java.util.List;

public class NotificationTokenServiceImpl extends BaseOpenmrsService implements NotificationTokenService {

    private NotificationTokenDataDao dao;

    @Override
    public NotificationToken getnotificationTokenById(String id) {
        return dao.getnotificationTokenById(id);
    }

    @Override
    public NotificationToken saveNotificationToken(NotificationToken notificationToken) {
        return dao.saveNotificationToken(notificationToken);
    }

    @Override
    public List<NotificationToken> getNotificationByUserId(User user) {
        return dao.getNotificationByUserId(user);
    }
}
