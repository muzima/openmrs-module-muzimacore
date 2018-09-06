package org.openmrs.module.muzima.api;

import org.openmrs.api.OpenmrsService;
import org.openmrs.module.muzima.api.model.CohortUpdateHistory;

public interface CohortUpdateHistoryService extends OpenmrsService {
    CohortUpdateHistory saveCohortUpdateHistory(CohortUpdateHistory cohortUpdateHistory);
}
