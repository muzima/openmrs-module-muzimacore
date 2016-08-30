package org.openmrs.module.muzima.api.service;

import org.openmrs.api.OpenmrsService;
import org.openmrs.module.muzima.MuzimaFormTag;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface MuzimaTagService extends OpenmrsService {
    @Transactional(readOnly = true)
    public List<MuzimaFormTag> getAll();

    @Transactional
    public MuzimaFormTag add(String name);
}
