package org.openmrs.module.muzima.api.service.impl;

import org.openmrs.api.impl.BaseOpenmrsService;
import org.openmrs.module.muzima.api.db.MuzimaConfigDAO;
import org.openmrs.module.muzima.api.service.MuzimaConfigService;
import org.openmrs.module.muzima.model.MuzimaConfig;

import java.util.List;

public class MuzimaConfigServiceImpl extends BaseOpenmrsService implements MuzimaConfigService {

    private MuzimaConfigDAO dao;

    public MuzimaConfigServiceImpl(MuzimaConfigDAO dao) {
        this.dao = dao;
    }

    @Override
    public List<MuzimaConfig> getAll() {
        return dao.getAll();
    }

    @Override
    public void save(MuzimaConfig config) {
        dao.save(config);
    }

    @Override
    public MuzimaConfig findById(Integer id) {
        return dao.findById(id);
    }

    @Override
    public MuzimaConfig getConfigByUuid(String uuid) {
        return dao.getConfigByUuid(uuid);
    }

    @Override
    public void delete(MuzimaConfig config) {
        dao.delete(config);
    }

    @Override
    public Number countConfigs(String search) {
        return dao.countConfigs(search);
    }

    @Override
    public List<MuzimaConfig> getPagedConfigs(String search, Integer pageNumber, Integer pageSize) {
        return dao.getPagedConfigs(search, pageNumber, pageSize);
    }
}