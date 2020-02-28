package org.openmrs.module.muzima.htmlform2MuzimaTransform.taghandler;

import java.io.PrintWriter;
import java.util.List;

import org.w3c.dom.Node;

/**
 * Implementations of this interface handle specific htmlform tags like {@code <obs/>, {@code
 * <encounterDate/>} etc.
 */
public interface TagHandler {
	
	/**
	 * Returns a list of attribute descriptors that specify the attributes associated with this tag
	 */
	public List<AttributeDescriptor> getAttributeDescriptors();
	
	/**
	 * Handles the start tag for a specific tag type. Generates the appropriate HTML and adds it to
	 * the associated PrintWriter. Returns whether or not to handle the body also. (True = Yes)
	 * 
	 * @param out the PrintWriter to append generated HTML to
	 * @param outJsPrintWriter the PrintWriter to append generated javascript to
	 * @param parent the parent node of the node in the XML associated with this tag
	 * @param node the node in the XML associated with this tag
	 * @return true/false whether to handle the body
	 */
	public boolean doStartTag(PrintWriter outHtmlPrintWriter, PrintWriter outJsPrintWriter, Node parent, Node node);
}
