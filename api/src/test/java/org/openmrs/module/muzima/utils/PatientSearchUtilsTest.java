package org.openmrs.module.muzima.utils;

import org.junit.Before;
import org.junit.Test;
import org.openmrs.Patient;
import org.openmrs.Person;
import org.openmrs.PersonName;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

public class PatientSearchUtilsTest {

    Patient gsolingPatient = new Patient();
    List<Patient> patientList  = new ArrayList();
    Patient unsavedPatient = new Patient();


    @Before
    public void setUp() throws Exception {
        Person gosling = new Person();
        gosling.setId(1);

        PersonName gsolingName = new PersonName();

        gsolingName.setFamilyName("James");
        gsolingName.setGivenName("Gosling");
        gsolingName.setMiddleName("W");
        gsolingName.setPerson(gosling);

        Set<PersonName> gsoPersonNames = new HashSet<>();

        gsoPersonNames.add(gsolingName);
        gsolingPatient.setId(1);
        gsolingPatient.setBirthdate( new Date());
        gsolingPatient.setNames(gsoPersonNames);

        Person unsavedPerson = new Person();
        gosling.setId(1);

        PersonName unsavedPersonName = new PersonName();

        unsavedPersonName.setFamilyName("James");
        unsavedPersonName.setGivenName("Gosling");
        unsavedPersonName.setMiddleName("W");
        unsavedPersonName.setPerson(gosling);

        Set<PersonName> unsavedPersonsNames = new HashSet<>();

        unsavedPersonsNames.add(unsavedPersonName);
        unsavedPatient.setId(1);
        unsavedPatient.setNames(gsoPersonNames);
        unsavedPatient.setBirthdate( new Date());

        patientList.add(gsolingPatient);
        patientList.add(unsavedPatient);
    }

    @Test
    public void findPatientTest() throws Exception {

        PatientSearchUtils.findPatient(patientList, unsavedPatient);

        assertThat(PatientSearchUtils.findPatient(patientList,unsavedPatient)).isNotNull();
        assertThat(PatientSearchUtils.findPatient(patientList,unsavedPatient)).isInstanceOf(Patient.class);
        assertThat(PatientSearchUtils.findPatient(patientList,unsavedPatient).getPersonName().getFamilyName()).isEqualTo("James");
        assertThat(PatientSearchUtils.findPatient(patientList,unsavedPatient).getId()).isEqualTo(1);
    }

    @Test
    public void findSavedPatientTest() throws Exception {
    }

}