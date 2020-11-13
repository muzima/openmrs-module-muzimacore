package org.openmrs.module.muzima.utils;

import net.minidev.json.JSONObject;
import org.apache.commons.lang.StringUtils;
import org.openmrs.Person;
import org.openmrs.PersonAddress;
import org.openmrs.PersonAttribute;
import org.openmrs.PersonAttributeType;
import org.openmrs.api.PersonService;
import org.openmrs.api.context.Context;

import java.util.Date;
import java.util.UUID;

import static org.openmrs.module.muzima.utils.JsonUtils.getElementFromJsonObject;

public class PersonCreationUtils {
    public static PersonAttribute getPersonAttributeFromJsonObject(JSONObject attributeJsonObject) throws Exception{
        if(attributeJsonObject == null){
            return null;
        }

        String attributeValue = (String) getElementFromJsonObject(attributeJsonObject,"attribute_value");
        if(StringUtils.isBlank(attributeValue)){
            return null;
        }

        String attributeTypeName = (String) getElementFromJsonObject(attributeJsonObject,"attribute_type_name");
        String attributeTypeUuid = (String) getElementFromJsonObject(attributeJsonObject,"attribute_type_uuid");

        PersonService personService = Context.getPersonService();
        PersonAttributeType attributeType = personService.getPersonAttributeTypeByUuid(attributeTypeUuid);

        if(attributeType == null){
            attributeType = personService.getPersonAttributeTypeByName(attributeTypeName);
        }

        if (attributeType == null) {
            throw new Exception("Unable to find Person Attribute Type by name: '" + attributeTypeName
                    + "' , uuid: '" +attributeTypeUuid + "'");
        }

        PersonAttribute personAttribute = new PersonAttribute();
        personAttribute.setAttributeType(attributeType);
        personAttribute.setValue(attributeValue);
        return personAttribute;
    }

    public static void copyPersonAddress(PersonAddress copyFrom, PersonAddress copyTo) throws Exception{
        if(copyFrom == null || copyTo == null){
            throw new Exception("unable to copy person address due to null object'");

        }
        copyTo.setAddress1(copyFrom.getAddress1());
        copyTo.setAddress2(copyFrom.getAddress2());
        copyTo.setAddress3(copyFrom.getAddress3());
        copyTo.setAddress4(copyFrom.getAddress4());
        copyTo.setAddress5(copyFrom.getAddress5());
        copyTo.setAddress6(copyFrom.getAddress6());
        copyTo.setCityVillage(copyFrom.getCityVillage());
        copyTo.setCountyDistrict(copyFrom.getCountyDistrict());
        copyTo.setStateProvince(copyFrom.getStateProvince());
        copyTo.setCountry(copyFrom.getCountry());
        copyTo.setPostalCode(copyFrom.getPostalCode());
        copyTo.setLatitude(copyFrom.getLatitude());
        copyTo.setLongitude(copyFrom.getLongitude());
        copyTo.setStartDate(copyFrom.getStartDate());
        copyTo.setEndDate(copyFrom.getEndDate());
        copyTo.setPreferred(copyFrom.getPreferred());
    }

    public static PersonAddress getPersonAddressFromJsonObject(JSONObject addressJsonObject){
        if(addressJsonObject == null){
            return null;
        }
        PersonAddress personAddress = new PersonAddress();
        personAddress.setAddress1((String)getElementFromJsonObject(addressJsonObject,"address1"));
        personAddress.setAddress2((String)getElementFromJsonObject(addressJsonObject,"address2"));
        personAddress.setAddress3((String)getElementFromJsonObject(addressJsonObject,"address3"));
        personAddress.setAddress4((String)getElementFromJsonObject(addressJsonObject,"address4"));
        personAddress.setAddress5((String)getElementFromJsonObject(addressJsonObject,"address5"));
        personAddress.setAddress6((String)getElementFromJsonObject(addressJsonObject,"address6"));
        personAddress.setCityVillage((String)getElementFromJsonObject(addressJsonObject,"cityVillage"));
        personAddress.setCountyDistrict((String)getElementFromJsonObject(addressJsonObject,"countyDistrict"));
        personAddress.setStateProvince((String)getElementFromJsonObject(addressJsonObject,"stateProvince"));
        personAddress.setCountry((String)getElementFromJsonObject(addressJsonObject,"country"));
        personAddress.setPostalCode((String)getElementFromJsonObject(addressJsonObject,"postalCode"));
        personAddress.setLatitude((String)getElementFromJsonObject(addressJsonObject,"latitude"));
        personAddress.setLongitude((String)getElementFromJsonObject(addressJsonObject,"longitude"));
        personAddress.setStartDate((Date) getElementFromJsonObject(addressJsonObject,"startDate"));
        personAddress.setEndDate((Date) getElementFromJsonObject(addressJsonObject,"endDate"));
        personAddress.setPreferred((Boolean) getElementFromJsonObject(addressJsonObject,"preferred"));
        personAddress.setUuid((String)getElementFromJsonObject(addressJsonObject,"uuid"));

        if(StringUtils.isEmpty(personAddress.getUuid())){
            personAddress.setUuid(UUID.randomUUID().toString());
        }

        if(personAddress.isBlank()){
            return null;
        } else {
            return personAddress;
        }
    }

    public static JSONObject createPersonPayloadStubForPerson(Person person){
        JSONObject personPayloadStub = new JSONObject();
        JsonUtils.writeAsString(personPayloadStub,"patient.uuid",person.getUuid());
        JsonUtils.writeAsString(personPayloadStub,"patient.given_name",person.getGivenName());
        JsonUtils.writeAsString(personPayloadStub,"patient.family_name",person.getFamilyName());
        JsonUtils.writeAsString(personPayloadStub,"patient.middle_name",person.getMiddleName());
        JsonUtils.writeAsString(personPayloadStub,"patient.sex",person.getGender());
        JsonUtils.writeAsDate(personPayloadStub,"patient.birth_date",person.getBirthdate());
        return personPayloadStub;
    }

    public static JSONObject createPersonPayloadStubFromIndexPatientStub(String payload){
        JSONObject personPayloadStub = new JSONObject();
        JsonUtils.writeAsString(personPayloadStub,"patient.uuid",
                JsonUtils.readAsString(payload,"$['index_patient']['index_patient.uuid']"));
        JsonUtils.writeAsString(personPayloadStub,"patient.given_name",
                JsonUtils.readAsString(payload,"$['index_patient']['index_patient.given_name']"));
        JsonUtils.writeAsString(personPayloadStub,"patient.family_name",
                JsonUtils.readAsString(payload,"$['index_patient']['index_patient.family_name']"));
        JsonUtils.writeAsString(personPayloadStub,"patient.middle_name",
                JsonUtils.readAsString(payload,"$['index_patient']['index_patient.middle_name']"));
        JsonUtils.writeAsString(personPayloadStub,"patient.sex",
                JsonUtils.readAsString(payload,"$['index_patient']['index_patient.sex']"));
        JsonUtils.writeAsDate(personPayloadStub,"patient.birth_date",
                JsonUtils.readAsDate(payload,"$['index_patient']['index_patient.birth_date']"));
        return personPayloadStub;
    }
}
