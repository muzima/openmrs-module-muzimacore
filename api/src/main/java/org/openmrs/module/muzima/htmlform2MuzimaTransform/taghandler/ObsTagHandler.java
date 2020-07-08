package org.openmrs.module.muzima.htmlform2MuzimaTransform.taghandler;

import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.openmrs.Concept;
import org.openmrs.Drug;
import org.openmrs.LocationTag;
import org.openmrs.module.muzima.htmlform2MuzimaTransform.Htmlform2MuzimaTransformConstants;
import org.openmrs.module.muzima.htmlform2MuzimaTransform.element.ObsElement;
import org.w3c.dom.Node;

/**
 * Handles the {@code <obs>} tag
 */
public class ObsTagHandler extends AbstractTagHandler {
	
	private ObsElement element;
	
	@Override
	protected List<AttributeDescriptor> createAttributeDescriptors() {
		List<AttributeDescriptor> attributeDescriptors = new ArrayList<AttributeDescriptor>();
		attributeDescriptors.add(new AttributeDescriptor("conceptId", Concept.class));
		attributeDescriptors.add(new AttributeDescriptor("conceptIds", Concept.class));
		attributeDescriptors.add(new AttributeDescriptor("answerConceptId", Concept.class));
		attributeDescriptors.add(new AttributeDescriptor("answerDrugId", Drug.class));
		attributeDescriptors.add(new AttributeDescriptor("answerConceptIds", Concept.class));
		attributeDescriptors.add(new AttributeDescriptor(Htmlform2MuzimaTransformConstants.ANSWER_LOCATION_TAGS, LocationTag.class));
		return Collections.unmodifiableList(attributeDescriptors);
	}
	
	@Override
	public boolean doStartTag(PrintWriter outHtmlPrintWriter, PrintWriter outJsPrintWriter, Node parent, Node node) {
		this.element = new ObsElement(getAttributes(node));
		outHtmlPrintWriter.print(element.generateHtml());
		outJsPrintWriter.print(element.getJsString());
		return true;
	}
}
