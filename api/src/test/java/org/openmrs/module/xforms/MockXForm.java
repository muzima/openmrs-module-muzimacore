package org.openmrs.module.xforms;

import org.openmrs.User;

import java.util.Date;

//TODO: Have to refer this class from the XFORM module.
// This is here because including the xform module causes a problem
public class MockXForm {
    private int formId;
    private String uuid;
    private String xformXml;
    private String layoutXml;
    private String localeXml;
    private String javaScriptSrc;
    private String css;
    private User creator;
    private Date dateCreated;
    private User changedBy;
    private Date dateChanged;

    public Date getDateChanged() {
        return dateChanged;
    }

    public void setDateChanged(Date dateChanged) {
        this.dateChanged = dateChanged;
    }

    public User getChangedBy() {
        return changedBy;
    }

    public void setChangedBy(User changedBy) {
        this.changedBy = changedBy;
    }

    public Date getDateCreated() {
        return dateCreated;
    }

    public void setDateCreated(Date dateCreated) {
        this.dateCreated = dateCreated;
    }

    public User getCreator() {
        return creator;
    }

    public void setCreator(User creator) {
        this.creator = creator;
    }

    public String getCss() {
        return css;
    }

    public void setCss(String css) {
        this.css = css;
    }

    public String getJavaScriptSrc() {
        return javaScriptSrc;
    }

    public void setJavaScriptSrc(String javaScriptSrc) {
        this.javaScriptSrc = javaScriptSrc;
    }

    public String getLocaleXml() {
        return localeXml;
    }

    public void setLocaleXml(String localeXml) {
        this.localeXml = localeXml;
    }

    public String getLayoutXml() {
        return layoutXml;
    }

    public void setLayoutXml(String layoutXml) {
        this.layoutXml = layoutXml;
    }

    public String getXformXml() {
        return xformXml;
    }

    public void setXformXml(String xformXml) {
        this.xformXml = xformXml;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public int getFormId() {
        return formId;
    }

    public void setFormId(int formId) {
        this.formId = formId;
    }
}
