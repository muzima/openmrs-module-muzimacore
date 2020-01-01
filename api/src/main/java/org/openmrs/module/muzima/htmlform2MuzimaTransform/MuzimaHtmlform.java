//TODO move this to the org.openmrs.module.muzima.model
package org.openmrs.module.muzima.htmlform2MuzimaTransform;

import org.openmrs.Form;
import org.codehaus.jackson.annotate.JsonIgnore;

/**
 * The HTML Form data object
 */

public class MuzimaHtmlform {
	
	private Integer id;
	
	private Form form;
	
	//TODO ensure this is needed
	private String xmlData;
	
	/** Gets the unique identifying id for this HTML Form */
	public Integer getId() {
		return id;
	}
	
	/** Sets the unique identifying id for this HTML Form */
	public void setId(Integer id) {
		this.id = id;
	}
	
	/** Gets the Form object this HTML Form is associated with */
	@JsonIgnore
	public Form getForm() {
		return form;
	}
	
	/** Sets the Form object this HTML Form is associated with */
	public void setForm(Form form) {
		this.form = form;
	}
	
	public MuzimaHtmlform() {
	} //used by hibernate TODO : check?
	
	/**
	 * Gets the name (inherited from form)
	 */
	//Used when serialized to JSON 
	public String getName() {
		return form != null ? form.getName() : null;
	}
	
	/**
	 * Gets the description (inherited from form)
	 */
	public String getDescription() {
		return getForm().getDescription();
	}
	
	/** Gets the actual XML content of the form */
	public String getXmlData() {
		return xmlData;
	}
	
	/** Sets the actual XML content of the form */
	public void setXmlData(String xmlData) {
		this.xmlData = xmlData;
	}
	
	//Used when serialized to JSON
	public String getUuid() {
		return getForm().getUuid();
	}
	
	@Override
	public int hashCode() {
		return id != null ? id.hashCode() : 0;
	}
	
	@Override
	public boolean equals(Object o) {
		if (this == o)
			return true;
		if (o == null || getClass() != o.getClass())
			return false;
		
		MuzimaHtmlform html5Form = (MuzimaHtmlform) o;
		
		if (id != null ? !id.equals(html5Form.id) : html5Form.id != null)
			return false;
		
		return true;
	}
	
}
