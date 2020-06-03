package org.openmrs.module.muzima.api.db;

import org.openmrs.module.muzima.model.MuzimaForm;
import org.openmrs.module.muzima.model.MuzimaXForm;

import java.util.Date;
import java.util.List;

public interface MuzimaFormDAO {

    List<MuzimaForm> getAll();

    List<MuzimaXForm> getXForms();

    Number countXForms(String search);

    List<MuzimaXForm> getPagedXForms(String search, Integer pageNumber, Integer pageSize);

    void saveForm(MuzimaForm form);

    MuzimaForm getFormById(Integer id);

    MuzimaForm getFormByUuid(String uuid);

    List<MuzimaForm> getFormByName(final String name, final Date syncDate);

    List<MuzimaForm> getFormByDiscriminator(final String name);

    List<MuzimaForm> getMuzimaFormByForm(String form, boolean includeRetired);
}