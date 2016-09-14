package org.openmrs.module.muzima.api.db;

import org.openmrs.module.muzima.MuzimaForm;
import org.openmrs.module.muzima.MuzimaXForm;
import org.openmrs.module.xforms.Xform;

import java.util.Date;
import java.util.List;

public interface MuzimaFormDAO {
    public List<MuzimaForm> getAll();

    public List<MuzimaXForm> getXForms();

    void saveForm(MuzimaForm form);

    MuzimaForm findById(Integer id);

    Xform getXform(int id);

    MuzimaForm findByUuid(String uuid);

    List<MuzimaForm> findByName(final String name, final Date syncDate);

    List<MuzimaForm> findByForm(String form);
}
