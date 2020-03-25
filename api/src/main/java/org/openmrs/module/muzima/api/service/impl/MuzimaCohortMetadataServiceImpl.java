package org.openmrs.module.muzima.api.service.impl;

import org.openmrs.api.impl.BaseOpenmrsService;
import org.openmrs.module.muzima.api.db.MuzimaCohortMetadataDao;
import org.openmrs.module.muzima.api.service.MuzimaCohortMetadataService;
import org.openmrs.module.muzima.model.MuzimaCohortMetadata;

import java.util.List;

public class MuzimaCohortMetadataServiceImpl extends BaseOpenmrsService implements MuzimaCohortMetadataService {
    private MuzimaCohortMetadataDao dao;

    public void setDao(MuzimaCohortMetadataDao dao) {
        this.dao = dao;
    }

    public MuzimaCohortMetadataDao getDao(){
        return dao;
    }

    @Override
    public List<MuzimaCohortMetadata> saveMuzimaCohortMetadata(List<MuzimaCohortMetadata> muzimaCohortMetadata) {
        return dao.saveOrUpdate(muzimaCohortMetadata);
    }

    @Override
    public List<MuzimaCohortMetadata> getMuzimaCohortMetadata(List<Integer> patientIds, Integer cohortId) {
        return dao.getMuzimaCohortMetadata(patientIds,cohortId);
    }

    @Override
    public List<Object> executeFilterQuery(String filterQuery) {
        return dao.executeFilterQuery(filterQuery);
    }

    @Override
    public void deleteMuzimaCohortMetadata(List<MuzimaCohortMetadata> muzimaCohortMetadata) {
        dao.delete(muzimaCohortMetadata);
    }


}
