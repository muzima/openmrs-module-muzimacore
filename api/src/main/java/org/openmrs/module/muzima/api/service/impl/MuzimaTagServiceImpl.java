package org.openmrs.module.muzima.api.service.impl;

import org.openmrs.api.impl.BaseOpenmrsService;
import org.openmrs.module.muzima.api.db.TagDAO;
import org.openmrs.module.muzima.api.service.MuzimaTagService;
import org.openmrs.module.muzima.model.MuzimaFormTag;

import java.util.List;

public class MuzimaTagServiceImpl extends BaseOpenmrsService implements MuzimaTagService {
    private TagDAO dao;

    public MuzimaTagServiceImpl(TagDAO dao) {
        this.dao = dao;
    }

    public List<MuzimaFormTag> getAll() {
        return dao.getAll();
    }

    public MuzimaFormTag add(String name) {
        MuzimaFormTag tag = new MuzimaFormTag(name);
        dao.save(tag);
        return tag;
    }
}
