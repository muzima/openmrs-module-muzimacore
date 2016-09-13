package org.openmrs.module.muzima.api.service.impl;

import org.dom4j.DocumentException;
import org.javarosa.xform.parse.ValidationMessages;
import org.javarosa.xform.parse.XFormParser;
import org.openmrs.api.context.Context;
import org.openmrs.api.impl.BaseOpenmrsService;
import org.openmrs.module.muzima.api.db.MuzimaConfigDAO;
import org.openmrs.module.muzima.api.service.MuzimaConfigService;
import org.openmrs.module.muzima.model.CompositeEnketoResult;
import org.openmrs.module.muzima.model.MuzimaConfig;
import org.openmrs.module.muzima.model.MuzimaXForm;
import org.openmrs.module.muzima.xForm2MuzimaTransform.ModelXml2JsonTransformer;
import org.openmrs.module.muzima.xForm2MuzimaTransform.ODK2HTML5Transformer;
import org.openmrs.module.muzima.xForm2MuzimaTransform.ODK2JavarosaTransformer;
import org.openmrs.module.muzima.xForm2MuzimaTransform.XForm2Html5Transformer;
import org.openmrs.module.xforms.Xform;

import java.io.StringReader;
import java.util.Date;
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
    public void save(MuzimaConfig config) throws Exception {
        dao.save(config);
    }

    @Override
    public MuzimaConfig findById(Integer id) {
        return dao.findById(id);
    }

    @Override
    public MuzimaConfig findByUuid(String uuid) {
        return dao.findByUuid(uuid);
    }

    @Override
    public void delete(MuzimaConfig config) throws Exception {
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
