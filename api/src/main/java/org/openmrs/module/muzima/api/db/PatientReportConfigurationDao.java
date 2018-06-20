package org.openmrs.module.muzima.api.db;

import org.openmrs.module.muzima.model.PatientReportConfiguration;

import java.util.List;

/**
 */
public interface PatientReportConfigurationDao extends SingleClassDao<PatientReportConfiguration> {

    /**
     * Return the patientReportConfiguration with the given uuid.
     *
     * @param uuid the patientReportConfiguration uuid.
     * @return the patientReportConfiguration with the matching uuid.
     * @should return data with matching uuid.
     * @should return null when no data with matching uuid.
     */
    PatientReportConfiguration getPatientReportConfigurationByUuid(final String uuid);

    /**
     * Get data patientReportConfiguration with matching search term for particular page.
     *
     * @param search     the search term.
     * @param pageNumber the page number.
     * @param pageSize   the size of the page.
     * @return list of patientReportConfiguration for the page.
     */
    List<PatientReportConfiguration> getPagedPatientReportConfigurations(final String search, final Integer pageNumber, final Integer pageSize);

    /**
     * Get the total number of patientReportConfiguration with matching search term.
     *
     *
     * @param search the search term.
     * @return total number of patientReportConfiguration in the database.
     */
    Number countPatientReportConfiguration(final String search);
}
