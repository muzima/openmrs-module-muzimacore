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
package org.openmrs.module.muzima.api;

import junit.framework.Assert;
import org.junit.Test;
import org.openmrs.api.context.Context;
import org.openmrs.module.muzima.api.service.RegistrationDataService;
import org.openmrs.module.muzima.model.RegistrationData;
import org.openmrs.test.BaseModuleContextSensitiveTest;

import static org.junit.Assert.assertNotNull;

/**
 * Tests {@link RegistrationDataService}.
 */
public class RegistrationDataServiceTest extends BaseModuleContextSensitiveTest {
	
	@Test
	public void shouldSetupContext() {
        RegistrationDataService service = Context.getService(RegistrationDataService.class);
		assertNotNull(service);

        RegistrationData registrationData = new RegistrationData();
        registrationData.setAssignedUuid("a");
        registrationData.setTemporaryUuid("b");
        RegistrationData savedRegistrationData = service.saveRegistrationData(registrationData);

        Assert.assertNotNull(savedRegistrationData.getId());
    }
}
