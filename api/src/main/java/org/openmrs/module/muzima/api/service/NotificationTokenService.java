package org.openmrs.module.muzima.api.service;

import org.openmrs.User;
import org.openmrs.api.OpenmrsService;
import org.openmrs.module.muzima.model.NotificationToken;

import java.util.List;

public interface NotificationTokenService extends OpenmrsService {
     NotificationToken getnotificationTokenById(String id);
     NotificationToken saveNotificationToken(NotificationToken notificationToken);
     List<NotificationToken> getNotificationByUserId(User user);
}
