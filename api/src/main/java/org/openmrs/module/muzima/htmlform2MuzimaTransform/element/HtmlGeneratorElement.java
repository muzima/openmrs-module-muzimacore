package org.openmrs.module.muzima.htmlform2MuzimaTransform.element;

/**
 * An element in a form that is capable of generating HTML. This would typically contain fields and
 * delegate most of its HTML-generation to them.
 */
public interface HtmlGeneratorElement {
	
	/**
	 * Generates the HTML for this element
	 * 
	 * @param context the Form Entry context
	 * @return the generated HTML
	 */
	public String generateHtml();
	
}
