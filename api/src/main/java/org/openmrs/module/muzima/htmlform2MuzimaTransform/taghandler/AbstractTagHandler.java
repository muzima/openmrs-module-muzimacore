package org.openmrs.module.muzima.htmlform2MuzimaTransform.taghandler;

import java.util.List;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;

import org.codehaus.jackson.map.ObjectMapper;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;

/**
 * This abstract handler provides a default implementation of getAttributeDescriptors that returns
 * null Tag handlers that extend this class can override createAttributeDescriptors if they need to
 * specify attribute descriptors
 */

public abstract class AbstractTagHandler implements TagHandler {
	
	/** Holds the attribute descriptors for this class **/
	private List<AttributeDescriptor> attributeDescriptors;
	
	/** Classes that extend this class can override this method to specify attribute descriptors */
	protected List<AttributeDescriptor> createAttributeDescriptors() {
		return null;
	}
	
	@Override
	public List<AttributeDescriptor> getAttributeDescriptors() {
		if (attributeDescriptors == null) {
			attributeDescriptors = createAttributeDescriptors();
		}
		return attributeDescriptors;
	}
	
	@Override
	abstract public boolean doStartTag(PrintWriter outHtmlPrintWriter, PrintWriter outJsPrintWriter, Node parent, Node node);
	
	//	@Override
	//	abstract public void doEndTag(PrintWriter outJsPrintWriter, Node parent, Node node);
	//	
	/**
	 * Helper method for getting an attribute value, with a default.
	 * 
	 * @param node
	 * @param attributeName
	 * @param defaultValue
	 * @return
	 */
	public String getAttribute(Node node, String attributeName, String defaultValue) {
		Node item = node.getAttributes().getNamedItem(attributeName);
		String ret = null;
		if (item != null) {
			ret = item.getNodeValue();
		}
		return ret != null ? ret : defaultValue;
	}
	
	public Map<String, String> getAttributes(Node node) {
		Map<String, String> attributes = new HashMap<String, String>();
		NamedNodeMap map = node.getAttributes();
		for (int i = 0; i < map.getLength(); ++i) {
			Node attribute = map.item(i);
			attributes.put(attribute.getNodeName(), attribute.getNodeValue());
		}
		return attributes;
	}
	
	protected String toJson(Object object) {
		try {
			return new ObjectMapper().writeValueAsString(object);
		}
		catch (IOException e) {
			throw new RuntimeException("Error generating JSON", e);
		}
	}
}