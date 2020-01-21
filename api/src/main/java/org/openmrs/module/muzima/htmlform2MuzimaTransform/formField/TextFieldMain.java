package org.openmrs.module.muzima.htmlform2MuzimaTransform.formField;

public class TextFieldMain {
	
	public static void main(String[] args) {
		String placeholder, initialValue, fieldLabel, name, dataConcept;
		Integer textFieldSize, textAreaRows, textAreaColumns, textFieldMaxLength;
		Boolean textArea;
		
		placeholder = "type something";
		initialValue = "50";
		fieldLabel = "What is your complaint";
		name = "freetext_general";
		dataConcept = "1915^FREETEXT GENERAL^99DCT";
		textFieldSize = 15;
		textAreaRows = 20;
		textAreaColumns = 20;
		textFieldMaxLength = 3;
		
		textArea = true;
		System.out.println("textarea is true");
		StringBuilder sb = new StringBuilder();
		
		if (textArea) {
			sb.append("<div class=\"form-group freetext\">\r\n" + "    <label for=\"" + name + "\">" + fieldLabel
			        + "</label>\r\n" + "<textarea name=\"" + name + "\" id=\"" + name + "\"");
			
			if (textAreaRows != null)
				sb.append(" rows=\"" + textAreaRows + "\"");
			if (textAreaColumns != null)
				sb.append(" cols=\"" + textAreaColumns + "\"");
			if (textFieldMaxLength != null && textFieldMaxLength.intValue() > 0) {
				sb.append(" maxlength=\"" + textFieldMaxLength.intValue() + "\"");
			}
			if (placeholder != null) {
				// TODO escape
				sb.append(" placeholder=\"").append(placeholder).append("\"");
			}
			sb.append(">");
			if (initialValue != null)
				sb.append(initialValue);
			sb.append("</textarea>\r\n" + "</div>\r\n");
			
			System.out.println(sb);
			System.out.println("now its text");
			
			System.out.println("textarea is false");
			sb.append("<div class=\"form-group freetext\">\r\n" + "    <label for=\"" + name + "\">" + fieldLabel
			        + "</label>\r\n" + "    <input class=\"form-control\" id=\"" + name + "\" name=\"" + name + "\"\r\n"
			        + "           type=\"text\" data-concept=\"" + dataConcept + "\"");
			if (textFieldSize != null)
				sb.append(" size=\"" + textFieldSize + "\"");
			if (initialValue != null)
				sb.append(" value=\"" + initialValue + "\"");
			if (textFieldMaxLength != null && textFieldMaxLength.intValue() > 0) {
				sb.append(" maxlength=\"" + textFieldMaxLength.intValue() + "\"");
			}
			if (placeholder != null) {
				// TODO escape
				sb.append(" placeholder=\"").append(placeholder).append("\"");
			}
			sb.append("\">\r\n" + "</div>\r\n");
		}
		System.out.println(sb);
		
	}
}
