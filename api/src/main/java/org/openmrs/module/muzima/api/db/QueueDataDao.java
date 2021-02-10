package org.openmrs.module.muzima.api.db;

import org.openmrs.module.muzima.model.QueueData;

import java.util.List;

/**
 */
public interface QueueDataDao extends DataDao<QueueData> {
    List<Object[]> queueDataCountGroupedByDiscriminator();
}
