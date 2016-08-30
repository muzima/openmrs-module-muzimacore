package org.openmrs.module.muzima.api.db.hibernate;

import org.openmrs.module.muzima.api.db.ErrorMessageDao;
import org.openmrs.module.muzima.model.ErrorData;
import org.openmrs.module.muzima.model.ErrorMessage;

/**
 * Created by vikas on 31/03/15.
 */
public class HibernateErrorMessageDao  extends HibernateDataDao<ErrorMessage> implements ErrorMessageDao {

    protected HibernateErrorMessageDao() {
        super(ErrorMessage.class);
    }
}
