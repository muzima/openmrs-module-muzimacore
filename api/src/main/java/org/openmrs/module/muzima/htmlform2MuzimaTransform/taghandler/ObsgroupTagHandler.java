package org.openmrs.module.muzima.htmlform2MuzimaTransform.taghandler;

import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

import org.openmrs.Concept;
import org.openmrs.api.context.Context;
import org.openmrs.module.muzima.htmlform2MuzimaTransform.Htmlform2MuzimaTransformUtil;
import org.w3c.dom.Node;


public class ObsgroupTagHandler extends AbstractTagHandler {
	@Override
	protected List<AttributeDescriptor> createAttributeDescriptors() {
		List<AttributeDescriptor> attributeDescriptors = new ArrayList<AttributeDescriptor>();
		attributeDescriptors.add(new AttributeDescriptor("groupingConceptId", Concept.class));
		return Collections.unmodifiableList(attributeDescriptors);
	}
	
	@Override
	public boolean doStartTag(PrintWriter outHtmlPrintWriter, PrintWriter outJsPrintWriter, Node parent, Node node) {
		String groupingConceptId = getAttribute(node, "groupingConceptId", null);
		if (groupingConceptId != null) {
			Concept concept = Htmlform2MuzimaTransformUtil.getConcept(groupingConceptId);
			Locale locale = Context.getLocale();
			String dataConcept = Htmlform2MuzimaTransformUtil.createDataConceptAttributeFromConcept(concept, locale);
			String substituteString = "\n<div class=\"section\" data-concept=\"" + dataConcept + "\">";
			outHtmlPrintWriter.print(substituteString);
		}
		return true;
	}
}
