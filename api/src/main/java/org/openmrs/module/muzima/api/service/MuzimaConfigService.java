package org.openmrs.module.muzima.api.service;

import org.openmrs.api.OpenmrsService;
import org.openmrs.module.muzima.model.MuzimaConfig;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface MuzimaConfigService extends OpenmrsService {

    @Transactional(readOnly = true)
    List<MuzimaConfig> getAll();

    @Transactional
    MuzimaConfig findById(Integer id);

    @Transactional
    MuzimaConfig getConfigByUuid(String uuid);

    @Transactional
    MuzimaConfig save(MuzimaConfig config);

    @Transactional
    void delete(MuzimaConfig config);

    @Transactional
    Number countConfigs(final String search);

    @Transactional
    List<MuzimaConfig> getPagedConfigs(final String search, final Integer pageNumber, final Integer pageSize);

    List<MuzimaConfig> getConfigByName(String configName, boolean includeRetired);
}
