package org.openmrs.module.muzima.htmlform2MuzimaTransform.taghandler;

import org.openmrs.Concept;
import org.openmrs.ConceptAnswer;
import org.openmrs.Drug;
import org.openmrs.LocationTag;
import org.openmrs.api.context.Context;
import org.openmrs.module.muzima.htmlform2MuzimaTransform.Htmlform2MuzimaTransformConstants;
import org.openmrs.module.muzima.htmlform2MuzimaTransform.Htmlform2MuzimaTransformUtil;
import org.openmrs.module.muzima.htmlform2MuzimaTransform.element.ObsElement;
import org.w3c.dom.Node;

import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;
import java.util.Collection;

/**
 * Handles the {@code <obs>} tag
 */
public class ObsTagHandler extends AbstractTagHandler {
	
	@Override
	protected List<AttributeDescriptor> createAttributeDescriptors() {
		List<AttributeDescriptor> attributeDescriptors = new ArrayList<AttributeDescriptor>();
		attributeDescriptors.add(new AttributeDescriptor("conceptId", Concept.class));
		attributeDescriptors.add(new AttributeDescriptor("conceptIds", Concept.class));
		attributeDescriptors.add(new AttributeDescriptor("answerConceptId", Concept.class));
		attributeDescriptors.add(new AttributeDescriptor("answerDrugId", Drug.class));
		attributeDescriptors.add(new AttributeDescriptor("answerConceptIds", Concept.class));
		attributeDescriptors
		        .add(new AttributeDescriptor(Htmlform2MuzimaTransformConstants.ANSWER_LOCATION_TAGS, LocationTag.class));
		return Collections.unmodifiableList(attributeDescriptors);
	}
	
	@Override
	public boolean doStartTag(PrintWriter outHtmlPrintWriter, PrintWriter outJsPrintWriter, Node parent, Node node) {
		ObsElement element = new ObsElement(getAttributes(node));
		outHtmlPrintWriter.print(element.generateHtml());
		outJsPrintWriter.print(element.getJsString());
		return true;
	}
	
	//	@Override
	//	public void doEndTag(PrintWriter outJsPrintWriter, Node parent, Node node) {
	//		if (!(popped instanceof ObsSubmissionElement)) {
	//			throw new IllegalStateException("Popped an element from the stack but it wasn't an ObsSubmissionElement!");
	//		}
	//		
	//		ObsElement element = (ObsElement) popped;
	//		if (session.getContext().getMode() != FormEntryContext.Mode.VIEW && element.hasWhenValueThen()) {
	//			if (element.getId() == null) {
	//				throw new IllegalStateException("<obs> must have an id attribute to define when-then actions");
	//			}
	//			out.println("<script type=\"text/javascript\">");
	//			out.println("jQuery(function() { htmlForm.setupWhenThen('" + element.getId() + "', "
	//			        + simplifyWhenThen(element.getWhenValueThenDisplaySection()) + ", "
	//			        + simplifyWhenThen(element.getWhenValueThenJavascript()) + ", "
	//			        + simplifyWhenThen(element.getWhenValueElseJavascript()) + "); });");
	//			out.println("</script>");
	//		}
	//	}
	
	//		private String simplifyWhenThen(Map<Object, String> whenThen) {
	//			Map<Object, String> simplified = new LinkedHashMap<Object, String>();
	//			if (whenThen.size() == 0) {
	//				return "null";
	//			}
	//			for (Map.Entry<Object, String> entry : whenThen.entrySet()) {
	//				Object key = entry.getKey();
	//				if (key instanceof Concept) {
	//					key = ((Concept) key).getConceptId();
	//				}
	//				simplified.put(key, entry.getValue());
	//			}
	//			return toJson(simplified);
	//		}
	
}
