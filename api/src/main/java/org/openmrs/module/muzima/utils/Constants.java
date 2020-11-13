package org.openmrs.module.muzima.utils;

public class Constants {
    public static final class MuzimaSettings{
        public static final String PATIENT_IDENTIFIER_AUTOGENERATTION_SETTING_PROPERTY = "PatientIdentifier.AutoGeneration";
        public static final String PATIENT_IDENTIFIER_AUTOGENERATTION_SOURCE_NAME
                = "PatientIdentifier.AutoGenerationSourceName";
        public static final String MAXIMUM_ENCOUNTERS_DOWNLOAD_SETTING_PROPERTY = "Encounter.maxDownloadSize";
        public static final Boolean PATIENT_IDENTIFIER_AUTOGENERATTION_SETTING_DEFAULT_VALUE = false;
        public static final String DEFAULT_MUZIMA_VISIT_TYPE_SETTING_PROPERTY = "mUzima.DefaultVisitType";
        public static final String MUZIMA_VISIT_GENERATION_SETTING_PROPERTY = "VisitCreation.isEnabled";
        public static final String DEMOGRAPHICS_UPDATE_MANUAL_REVIEW_SETTING_PROPERTY = "DemographicsUpdateManualReviewEnforcement.isEnabled";
        public static final String MAXIMUM_OBS_DOWNLOAD_SETTING_PROPERTY = "Obs.maximumDownloadSize";
    }
}
