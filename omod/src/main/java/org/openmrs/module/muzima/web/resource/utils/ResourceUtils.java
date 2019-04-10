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
package org.openmrs.module.muzima.web.resource.utils;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * TODO: Write brief description about the class here.
 */
public class ResourceUtils {

    private static final Log log = LogFactory.getLog(ResourceUtils.class);

    public static Date parseDate(final String iso8601String) {
        if (!StringUtils.isNotBlank(iso8601String)) {
            return null;
        }
        Date date = null;
        try {
            String s = iso8601String.replace("Z", "+00:00");
            s = s.substring(0, 22) + s.substring(23);
            date = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ").parse(s);
        } catch (ParseException e) {
            log.error("Unable to parse date information.");
        }
        return date;
    }
}
