/**
 * The contents of this file are subject to the OpenMRS Public License
 * Version 1.0 (the "License"); you may not use this file except in
 * compliance with the License. You may obtain a copy of the License at
 * http://license.openmrs.org
 *
 * Software distributed under the License is distributed on an "AS IS"
 * basis, WITHOUT WARRANTY OF ANY KIND, either express or implied. See the
 * License for the specific language governing rights and limitations
 * under the License.
 *
 * Copyright (C) OpenMRS, LLC.  All Rights Reserved.
 */
package org.openmrs.module.muzima.model.handler;

import org.openmrs.annotation.Handler;
import org.openmrs.module.muzima.model.Data;

/**
 * TODO: Write brief description about the class here.
 */
@Handler(supports = Data.class)
public class DefaultDataHandler implements DataHandler {
    /**
     * Flag whether the current data handler can handle certain data.
     *
     * @param data the data.
     * @return true if the handler can handle the data.
     */
    @Override
    public boolean accept(final Data data) {
        return false;
    }

    /**
     * Handler that will be executed when a data is retrieved.
     *
     * @param data the data.
     */
    @Override
    public void handleGet(final Data data) {
    }

    /**
     * Handler that will be executed when a data is saved.
     *
     * @param data the data.
     */
    @Override
    public void handleSave(final Data data) {
    }

    /**
     * Handler that will be executed when a data is deleted.
     *
     * @param data the data.
     */
    @Override
    public void handleDelete(final Data data) {
    }
}
