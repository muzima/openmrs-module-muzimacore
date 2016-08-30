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
package org.openmrs.module.muzima.common;

import org.openmrs.Concept;
import org.openmrs.Obs;
import org.openmrs.Person;
import org.openmrs.api.context.Context;

import java.util.List;

/**
 * TODO: Write brief description about the class here.
 */
public class ObsServiceUtils {

    /**
     * Utility class to replace the obs service call without specifying nulls.
     *
     * @param persons the persons who own the obs.
     * @param concepts the concepts asked in the obs.
     * @return list of obs matching the persons and concepts.
     */
    public static List<Obs> get(final List<Person> persons, final List<Concept> concepts) {
        return Context.getObsService().getObservations(persons, null, concepts, null, null, null, null, null, null, null, null, false);
    }
}
