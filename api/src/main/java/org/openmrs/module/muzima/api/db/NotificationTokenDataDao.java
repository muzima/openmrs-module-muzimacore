package org.openmrs.module.muzima.api.db;

import org.openmrs.User;
import org.openmrs.module.muzima.model.NotificationToken;

import java.util.List;

public interface NotificationTokenDataDao {
    NotificationToken getnotificationTokenById(String id);
    NotificationToken saveNotificationToken(NotificationToken notificationToken);
    List<NotificationToken> getNotificationByUserId(User user);
}
