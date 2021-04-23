package org.openmrs.module.muzima.model;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.openmrs.BaseOpenmrsData;

@JsonIgnoreProperties(ignoreUnknown = true)
public class NotificationToken extends BaseOpenmrsData implements Data {

    private Integer id;
    private Integer userId;
    private String token;

    @Override
    public Integer getId() {
        return id;
    }

    @Override
    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    @Override
    public String getPayload() {
        return null;
    }
}
