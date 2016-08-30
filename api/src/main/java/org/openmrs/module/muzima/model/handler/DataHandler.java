package org.openmrs.module.muzima.model.handler;

import org.openmrs.module.muzima.model.Data;

/**
 */
public interface DataHandler {

    /**
     * Flag whether the current data handler can handle certain data.
     *
     * @param data the data.
     * @return true if the handler can handle the data.
     */
    boolean accept(final Data data);

    /**
     * Handler that will be executed when a data is retrieved.
     *
     * @param data the data.
     */
    void handleGet(final Data data);

    /**
     * Handler that will be executed when a data is saved.
     *
     * @param data the data.
     */
    void handleSave(final Data data);

    /**
     * Handler that will be executed when a data is deleted.
     *
     * @param data the data.
     */
    void handleDelete(final Data data);

}
