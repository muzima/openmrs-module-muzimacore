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
package org.openmrs.module.muzima.extension.html;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.module.Extension;
import org.openmrs.module.web.extension.AdministrationSectionExt;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * This class defines the links that will appear on the administration page under the
 * "muzima.title" heading.
 */
public class AdminList extends AdministrationSectionExt {

    protected Log log = LogFactory.getLog(getClass());

    /**
     * @see AdministrationSectionExt#getMediaType()
     */
    public Extension.MEDIA_TYPE getMediaType() {
        return Extension.MEDIA_TYPE.html;
    }

    /**
     * @see AdministrationSectionExt#getTitle()
     */
    public String getTitle() {
        return "muzima.title";
    }

    /**
     * @see AdministrationSectionExt#getLinks()
     */
    public Map<String, String> getLinks() {
        LinkedHashMap<String, String> map = new LinkedHashMap<String, String>();
        map.put("/module/muzimacore/view.list#/sources", "muzimacore.view.sources");
        map.put("/module/muzimacore/view.list#/configs", "muzimacore.config.setup");
        map.put("/module/muzimacore/view.list#/queues", "muzimacore.view.queues");
        map.put("/module/muzimacore/view.list#/registrations", "muzimacore.view.registrations");
        map.put("/module/muzimacore/view.list#/forms", "muzimacore.form.manage");
        map.put("/module/muzimacore/view.list#/errors", "muzimacore.view.errors");
        return map;
    }
}