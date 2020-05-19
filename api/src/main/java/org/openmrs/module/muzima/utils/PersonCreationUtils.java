package org.openmrs.module.muzima.utils;

import net.minidev.json.JSONObject;
import org.apache.commons.lang.StringUtils;
import org.openmrs.PersonAddress;
import org.openmrs.PersonAttribute;
import org.openmrs.PersonAttributeType;
import org.openmrs.api.PersonService;
import org.openmrs.api.context.Context;

import java.util.Date;

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

        if(personAddress.isBlank()){
            return null;
        } else {
            return personAddress;
        }
    }
}
