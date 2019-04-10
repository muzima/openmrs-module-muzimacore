/**
 * The contents of this file are subject to the OpenMRS Public License
 * Version 1.0 (the "License"); you may not use this file except in
 * compliance with the License. You may obtain a copy of the License at
 * http://license.openmrs.org
 *
 * Software distributed under the License is distributed on an "AS IS"
 * basis, WITHOUT WARRANTY OF ANY KIND, either express or implied. See the
 * License for the specific language governing rights and limitations
 * under the License.
 *
 * Copyright (C) OpenMRS, LLC.  All Rights Reserved.
 */
package org.openmrs.module.muzima.api.db;

import org.openmrs.Person;
import org.openmrs.Role;
import org.openmrs.module.muzima.model.NotificationData;

import java.util.Date;
import java.util.List;

/**
 * TODO: Write brief description about the class here.
 */
public interface NotificationDataDao extends DataDao<NotificationData> {

    /**
     * Get all notification for this particular person.
     *
     * @param person the person for whom the notification designated to.
     * @return the list of all notification for that particular person.
     */
    List<NotificationData> getNotificationsByReceiver(final Person person, final String search,
                                                      final Integer pageNumber, final Integer pageSize,
                                                      final String status, final Date syncDate);

    /**
     * Get all notification from this particular person.
     *
     * @param person the person from where the notification originated from.
     * @return the list of all notification from that particular person.
     */
    List<NotificationData> getNotificationsBySender(final Person person, final String search,
                                                    final Integer pageNumber, final Integer pageSize,
                                                    final String status, final Date syncDate);

    Number countNotificationsByReceiver(final Person person, final String search, final String status);

    Number countNotificationsBySender(final Person person, final String search, final String status);

    List<NotificationData> getNotificationsByRole(Role role, String search, Integer pageNumber, Integer pageSizeInteger, final String status);

    Number countNotificationsByRole(final Role role, final String search, final String status);
}
