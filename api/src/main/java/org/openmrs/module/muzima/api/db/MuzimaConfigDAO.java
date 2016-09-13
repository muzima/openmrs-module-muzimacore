package org.openmrs.module.muzima.api.db;

import org.openmrs.module.muzima.model.MuzimaConfig;
import java.util.List;

public interface MuzimaConfigDAO {

    List<MuzimaConfig> getAll();

    MuzimaConfig findById(Integer id);

    MuzimaConfig findByUuid(String uuid);

    void save(MuzimaConfig config) throws Exception;

    void delete(MuzimaConfig config) throws Exception;

    Number countConfigs(String search);

    List<MuzimaConfig> getPagedConfigs(String search, Integer pageNumber, Integer pageSize);
}