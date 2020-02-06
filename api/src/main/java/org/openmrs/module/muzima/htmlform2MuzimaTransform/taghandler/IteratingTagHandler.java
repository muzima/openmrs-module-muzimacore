package org.openmrs.module.muzima.htmlform2MuzimaTransform.taghandler;

import java.io.PrintWriter;

import org.openmrs.module.htmlformentry.FormEntrySession;
import org.w3c.dom.Node;

/**
 * Currently not used--the {@code <repeat>} tag is currently handled by
 * {@see org.openmrs.module.htmlformentry.HtmlFormEntryGenerator#applyRepeats(String)}.
 */
public interface IteratingTagHandler extends TagHandler {
	
	public boolean shouldRunAgain(PrintWriter outHtmlPrintWriter, PrintWriter outJsPrintWriter, Node parent, Node node);
	
}
