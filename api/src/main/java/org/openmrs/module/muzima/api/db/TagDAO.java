package org.openmrs.module.muzima.api.db;


import org.openmrs.module.muzima.MuzimaFormTag;

import java.util.List;

public interface TagDAO {
    List<MuzimaFormTag> getAll();

    void save(MuzimaFormTag tag);
}
