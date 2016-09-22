package org.openmrs.module.muzima.api.db;

import org.openmrs.module.muzima.model.MuzimaForm;
import org.openmrs.module.muzima.model.MuzimaXForm;
import org.openmrs.module.xforms.Xform;

import java.util.Date;
import java.util.List;

public interface MuzimaFormDAO {

    List<MuzimaForm> getAll();

    List<MuzimaXForm> getXForms();

    void saveForm(MuzimaForm form);

    MuzimaForm getFormById(Integer id);

    Xform getXform(int id);

    MuzimaForm getFormByUuid(String uuid);

    List<MuzimaForm> getFormByName(final String name, final Date syncDate);

    List<MuzimaForm> getMuzimaFormByForm(String form, boolean includeRetired);
}
