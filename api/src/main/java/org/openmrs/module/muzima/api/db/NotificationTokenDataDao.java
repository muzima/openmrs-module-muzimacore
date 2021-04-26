package org.openmrs.module.muzima.api.db;

import org.openmrs.User;
import org.openmrs.module.muzima.model.NotificationToken;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface NotificationTokenDataDao {
    NotificationToken getNotificationTokenById(String id);

    @Transactional
    NotificationToken saveNotificationToken(NotificationToken notificationToken);

    List<NotificationToken> getNotificationByUserId(User user);
}
