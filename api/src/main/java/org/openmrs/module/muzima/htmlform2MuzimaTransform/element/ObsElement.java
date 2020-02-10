package org.openmrs.module.muzima.htmlform2MuzimaTransform.element;

import org.apache.commons.lang.StringUtils;
import org.openmrs.Concept;
import org.openmrs.ConceptAnswer;
import org.openmrs.ConceptClass;
import org.openmrs.ConceptDatatype;
import org.openmrs.ConceptNumeric;
import org.openmrs.Drug;
import org.openmrs.Location;
import org.openmrs.Obs;
import org.openmrs.Person;
import org.openmrs.Role;
import org.openmrs.api.context.Context;
import org.openmrs.module.muzima.htmlform2MuzimaTransform.Htmlform2MuzimaTransformUtil;
import org.openmrs.module.muzima.htmlform2MuzimaTransform.Htmlform2MuzimaTransformConstants;
import org.openmrs.module.htmlformentry.HtmlFormEntryConstants;
import org.openmrs.module.htmlformentry.HtmlFormEntryService;
import org.openmrs.module.htmlformentry.FormEntryContext.Mode;
import org.openmrs.module.htmlformentry.compatibility.ConceptCompatibility;

import org.openmrs.module.muzima.htmlform2MuzimaTransform.*;
import org.openmrs.module.muzima.htmlform2MuzimaTransform.formField.CheckboxField;
import org.openmrs.module.muzima.htmlform2MuzimaTransform.formField.ConceptSearchAutoCompleteField;
import org.openmrs.module.muzima.htmlform2MuzimaTransform.formField.DateTimeField;
import org.openmrs.module.muzima.htmlform2MuzimaTransform.formField.DateField;
import org.openmrs.module.muzima.htmlform2MuzimaTransform.formField.DropdownField;
import org.openmrs.module.muzima.htmlform2MuzimaTransform.formField.NumberField;
import org.openmrs.module.muzima.htmlform2MuzimaTransform.formField.Option;
import org.openmrs.module.muzima.htmlform2MuzimaTransform.formField.RadioButtonsField;
import org.openmrs.module.muzima.htmlform2MuzimaTransform.formField.SingleOptionField;
import org.openmrs.module.muzima.htmlform2MuzimaTransform.formField.TextField;
import org.openmrs.module.muzima.htmlform2MuzimaTransform.formField.TimeField;
import org.openmrs.module.muzima.htmlform2MuzimaTransform.formField.ToggleCheckbox;
import org.openmrs.module.muzima.htmlform2MuzimaTransform.formField.ToggleField;
import org.openmrs.module.muzima.htmlform2MuzimaTransform.formField.UploadField;
import org.openmrs.module.muzima.htmlform2MuzimaTransform.formField.FormField;
import org.openmrs.obs.ComplexData;
import org.openmrs.util.LocaleUtility;
import org.openmrs.util.OpenmrsUtil;
import org.openmrs.util.RoleConstants;
import org.openmrs.web.WebConstants;

import ca.uhn.hl7v2.conf.spec.message.Field;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.StringTokenizer;
import java.util.Vector;


/**
 * Holds the field used to represent a specific Observation
 */

public class ObsElement implements HtmlGeneratorElement {
	
	
	private Locale locale = Context.getLocale();
	
	private Concept concept;
	
	private String valueLabel;
	
	protected FormField valueField;
	
	private String defaultValue;
	
	private String dateLabel;
	
	private DateField dateField;
	
	private boolean allowFutureDates = false;
	
	private Concept answerConcept;
	
	private Drug answerDrug;
	
	private List<Concept> conceptAnswers = new ArrayList<Concept>();
	
	private List<Number> numericAnswers = new ArrayList<Number>();
	
	private List<String> textAnswers = new ArrayList<String>();
	
	private List<String> answerLabels = new ArrayList<String>();
	
	private String answerLabel;
	
	private boolean required;
	
	private String jsString = null;
	
	//these are for conceptSelects:
	private List<Concept> concepts = null; //possible concepts
	
	private List<String> conceptLabels = null; //the text to show for possible concepts
	
	private String answerSeparator = null;
	
	// these are for location and provider options
	
	private List<Option> locationOptions = new ArrayList<Option>();
	
	private Map<Object, String> whenValueThenDisplaySection = new LinkedHashMap<Object, String>();
	
	private Map<Object, String> whenValueThenJavascript = new LinkedHashMap<Object, String>();
	
	private Map<Object, String> whenValueElseJavascript = new LinkedHashMap<Object, String>();
	
	private Boolean isLocationObs; // determines whether the valueText for this obs should be a location_id;
	
	private Double absoluteMaximum;
	
	private Double absoluteMinimum;
	
	public ObsElement(Map<String, String> parameters) {

		
		if (parameters.get("locale") != null) {
			this.locale = LocaleUtility.fromSpecification(parameters.get("locale"));
		}
		String conceptId = parameters.get("conceptId");
		String conceptIds = parameters.get("conceptIds");
		defaultValue = parameters.get("defaultValue");
		if (parameters.get("answerSeparator") != null) {
			answerSeparator = parameters.get("answerSeparator");
		}
		
		if (conceptId != null && conceptIds != null)
			throw new RuntimeException("You can't use conceptId and conceptIds in the same tag!");
		else if (conceptId == null && conceptIds == null)
			throw new RuntimeException("You must include either conceptId or conceptIds in an obs tag");
		
		if (conceptId != null) {
			concept = Htmlform2MuzimaTransformUtil.getConcept(conceptId);
			if (concept == null)
				throw new IllegalArgumentException("Cannot find concept for value " + conceptId
				        + " in conceptId attribute value. Parameters: " + parameters);
		} else {
			concepts = new ArrayList<Concept>();
			for (StringTokenizer st = new StringTokenizer(conceptIds, ","); st.hasMoreTokens();) {
				String s = st.nextToken().trim();
				Concept concept = Htmlform2MuzimaTransformUtil.getConcept(s);
				if (concept == null)
					throw new IllegalArgumentException("Cannot find concept for value " + s
					        + " in conceptIds attribute value. Parameters: " + parameters);
				concepts.add(concept);
			}
			if (concepts.size() == 0)
				throw new IllegalArgumentException(
				        "You must provide some valid conceptIds for the conceptIds attribute. Parameters: " + parameters);
		}
		
		// test to make sure the answerConceptId, if it exists, is valid
		String answerConceptId = parameters.get("answerConceptId");
		if (StringUtils.isNotBlank(answerConceptId)) {
			if (Htmlform2MuzimaTransformUtil.getConcept(answerConceptId) == null)
				throw new IllegalArgumentException("Cannot find concept for value " + answerConceptId
				        + " in answerConceptId attribute value. Parameters: " + parameters);
		}
		
		// test to make sure the answerConceptIds, if they exist, are valid
		String answerConceptIds = parameters.get("answerConceptIds");
		if (StringUtils.isNotBlank(answerConceptIds)) {
			for (StringTokenizer st = new StringTokenizer(answerConceptIds, ","); st.hasMoreTokens();) {
				String s = st.nextToken().trim();
				Concept concept = Htmlform2MuzimaTransformUtil.getConcept(s);
				if (concept == null)
					throw new IllegalArgumentException("Cannot find concept for value " + s
					        + " in answerConceptIds attribute value. Parameters: " + parameters);
			}
		}
		
		if ("true".equals(parameters.get("allowFutureDates")))
			allowFutureDates = true;
		if ("true".equals(parameters.get("required"))) {
			required = true;
		}
		
		isLocationObs = "location".equals(parameters.get("style"));
		
		if (StringUtils.isNotEmpty(parameters.get("absoluteMaximum"))) {
			absoluteMaximum = Double.parseDouble(parameters.get("absoluteMaximum"));
		}
		
		if (StringUtils.isNotEmpty(parameters.get("absoluteMinimum"))) {
			absoluteMinimum = Double.parseDouble(parameters.get("absoluteMinimum"));
		}
		prepareFields(parameters);
		
	}
	
	private void prepareFields(Map<String, String> parameters) {
		String userLocaleStr = locale.toString();
		try {
			if (answerConcept == null)
				answerConcept = Htmlform2MuzimaTransformUtil.getConcept(parameters.get("answerConceptId"));
		}
		catch (Exception ex) {}
		
		try {
			if (answerDrug == null)
				answerDrug = Htmlform2MuzimaTransformUtil.getDrug(parameters.get("answerDrugId"));
		}
		catch (Exception ex) {}
		
		Integer size = 1;
		try {
			size = Integer.valueOf(parameters.get("size"));
		}
		catch (Exception ex) {}
		
		String answerConceptSetIds = parameters.get("answerConceptSetIds");
		boolean isAutocomplete = "autocomplete".equals(parameters.get("style"));
		
		//		if (concept != null) {
		//			
		//			//below is for boolean checkbox
		//			if (concept.getDatatype().isBoolean() && "checkbox".equals(parameters.get("style"))) {
		//				// since a checkbox has one value we need to look for an exact
		//				// match for that value
		//				if ("false".equals(parameters.get("value"))) {
		//					answerConcept = Context.getConceptService().getConcept(1066);
		//					
		//				} else {
		//					// if not 'false' we treat as 'true'
		//					answerConcept = Context.getConceptService().getConcept(1065);
		//				}
		//				Option booleanCheckBoxOption = new Option(answerConcept, locale);
		//				valueField = new CheckboxField(answerConcept, locale, valueLabel);
		//				((CheckboxField) valueField).addOption(booleanCheckBoxOption);
			
			/**
			 * Handles creation of ValueLabel for
			 * {@code <obs conceptIds="1441,3017,2474" answerConceptId="656" labelText=
			 * "ISONIAZID"/>} It is possible to create an obs tag where you're choosing the
			 * 'question' for a predefined answerConceptId. This attribute can'tbe used in the same
			 * obs tag as the usual 'conceptId' attribute, and for the moment requires an
			 * answerConceptId.
			 */
			if (parameters.containsKey("labelNameTag")) {
				if (parameters.get("labelNameTag").equals("default"))
					if (concepts != null)
						valueLabel = answerConcept.getName(locale, false).getName();
					else
						valueLabel = concept.getName(locale, false).getName();
				else
					throw new IllegalArgumentException("Name tags other than 'default' not yet implemented");
			} else if (parameters.containsKey("labelText")) {
				valueLabel = parameters.get("labelText");
			} else if (parameters.containsKey("labelCode")) {
				//translations have been done already with applyTranslations()
				valueLabel = parameters.get("labelCode");
			} else {
				if (concepts != null)
					valueLabel = answerConcept.getName(locale, false).getName();
				else
				valueLabel = concept.getName(locale, false).getName();
			}
			if (parameters.get("answerLabels") != null) {
				answerLabels = Arrays.asList(parameters.get("answerLabels").split(","));
			}
			if (parameters.get("answerCodes") != null) {
				//translations have been done already with applyTranslations()
				String[] split = parameters.get("answerCodes").split(",");
				for (String s : split) {
					answerLabels.add(s);
				}
			}
			
			/**
			 * Handles {@code <obs conceptIds="1441,3017,2474" answerConceptId="656" labelText=
			 * "ISONIAZID"/>} It is possible to create an obs tag where you're choosing the
			 * 'question' for a predefined answerConceptId. This attribute can'tbe used in the same
			 * obs tag as the usual 'conceptId' attribute, and for the moment requires an
			 * answerConceptId.
			 */
			if (concepts != null) {
				conceptLabels = new ArrayList<String>();
				if (parameters.get("conceptLabels") != null)
					conceptLabels = Arrays.asList(parameters.get("conceptLabels").split(","));
				if (conceptLabels.size() != 0 && (conceptLabels.size() != concepts.size()))
					throw new IllegalArgumentException(
					        "If you want to use the conceptLabels attribute, you must to provide the same number of conceptLabels as there are conceptIds.  Parameters: "
					                + parameters);
				if ("radio".equals(parameters.get("style"))) {
					valueField = new RadioButtonsField(answerConcept, locale, valueLabel);
					//TODO create radioButtonsField
					if (answerSeparator != null) {
						((RadioButtonsField) valueField).setAnswerSeparator(answerSeparator);
					}
				} else { // dropdown
					//TODO create Dropdown
					valueField = buildDropdownField(answerConcept, locale, valueLabel, size);
				}
				for (int i = 0; i < concepts.size(); i++) {
					Concept c = concepts.get(i);
					String label = null;
					if (conceptLabels != null && i < conceptLabels.size()) {
						label = conceptLabels.get(i);
					} else {
						label = c.getName(locale, false).getName();
					}
					((SingleOptionField) valueField).addOption(new Option(answerConcept, valueLabel, locale, false));
				}
				if (defaultValue != null) {
					Concept initialValue = Htmlform2MuzimaTransformUtil.getConcept(defaultValue);
					if (initialValue == null) {
						throw new IllegalArgumentException("Invalid default value. Cannot find concept: " + defaultValue);
					}
					valueField.setDefaultValue(initialValue);
				}
				answerLabel = getValueLabel();
			} else {
				
				// Obs of datatypes date, time, and datetime support the attributes
				// defaultDatetime. 
				String defaultDatetimeFormat = "yyyy-MM-dd-HH-mm";
				
				if (concept.getDatatype().isNumeric()) {
					
					ConceptNumeric cn;
					if (concept instanceof ConceptNumeric) {
						cn = (ConceptNumeric) concept;
					} else {
						cn = Context.getConceptService().getConceptNumeric(concept.getConceptId());
					}
					
					ConceptCompatibility conceptCompatibility = Context
					        .getRegisteredComponent("htmlformentry.ConceptCompatibility", ConceptCompatibility.class);
					boolean isPrecise = cn != null ? conceptCompatibility.isAllowDecimal(cn) : true;
					
					if (parameters.get("answers") != null) {
						try {
							for (StringTokenizer st = new StringTokenizer(parameters.get("answers"), ", "); st
							        .hasMoreTokens();) {
								Number answer = Double.valueOf(st.nextToken());
								numericAnswers.add(answer);
							}
						}
						catch (Exception ex) {
							throw new RuntimeException("Error in answer list for concept " + concept.getConceptId() + " ("
							        + ex.toString() + "): " + conceptAnswers);
						}
					}
					
					if (numericAnswers.size() == 0) {
						if (!"checkbox".equals(parameters.get("style"))) {
							valueField = new NumberField(cn, locale, valueLabel, parameters.get("size"), absoluteMinimum,
							        absoluteMaximum);
						} else {
							
							if (parameters.get("answer") != null) {
								try {
									Number number = Double.valueOf(parameters.get("answer"));
									numericAnswers.add(number);
									answerLabel = (parameters.get("answerLabel") != null) ? parameters.get("answerLabel")
									        : cn.getName(locale, false).getName();
									if (number != null) {
										valueField = createToggleCheckbox(cn, null,
										    (isPrecise ? number.toString() : Integer.valueOf(number.intValue()).toString()),
										    locale, valueLabel, answerLabel, parameters.get("toggle"));
									}
									
								}
								catch (Exception ex) {
									throw new RuntimeException("Error in answer for concept " + concept.getConceptId() + " ("
									        + ex.toString() + "): ");
								}
							}
						}
					} else {
						if ("radio".equals(parameters.get("style"))) {
							
							valueField = new RadioButtonsField(concept, locale, valueLabel);
							if (answerSeparator != null) {
								((RadioButtonsField) valueField).setAnswerSeparator(answerSeparator);
							}
						} else if ("checkbox".equals(parameters.get("style"))) {
							valueField = new CheckboxField(concept, locale, valueLabel);
						} else {
							valueField = buildDropdownField(concept, locale, valueLabel, size);
						}
						
						for (int i = 0; i < numericAnswers.size(); i++) {
							Number n = numericAnswers.get(i);
							
							String label = null;
							if (answerLabels != null && i < answerLabels.size()) {
								label = answerLabels.get(i);
							} else {
								label = n.toString();
							}
							((SingleOptionField) valueField).addOption(new Option(label,
							        isPrecise ? n.toString() : Integer.valueOf(n.intValue()).toString(), false));
						}
						
					}
					
					if (valueField != null) {
						Number initialValue = null;
						
						if (defaultValue != null) {
							try {
								initialValue = isPrecise ? ((Number) Double.valueOf(defaultValue))
								        : Integer.valueOf(defaultValue);
							}
							catch (NumberFormatException e) {
								throw new IllegalArgumentException("Default value " + defaultValue + " is not a valid "
								        + (isPrecise ? "double" : "integer"), e);
							}
						}
						valueField.setDefaultValue(initialValue);
					}
				} else if (concept.isComplex()) {
					
					valueField = new UploadField(concept, locale, valueLabel);
				} else if (concept.getDatatype().isText()) {
					
					String initialValue = null;
					if (defaultValue != null) {
						initialValue = defaultValue;
					}
					
					//TODO below may be handling auto complete
					if (parameters.get("answers") != null) {
						try {
							for (StringTokenizer st = new StringTokenizer(parameters.get("answers"), ","); st
							        .hasMoreTokens();) {
								textAnswers.add(st.nextToken());
							}
						}
						catch (Exception ex) {
							throw new RuntimeException("Error in answer list for concept " + concept.getConceptId() + " ("
							        + ex.toString() + "): " + conceptAnswers);
						}
					}
					
					// configure the special obs type that allows selection of a location (the location_id PK is stored as the valueText)
					if (isLocationObs) {
						
						valueField = new DropdownField(concept, locale, valueLabel, null);
						
						// if "answerLocationTags" attribute is present try to get locations by tags
						List<Location> locationList = Htmlform2MuzimaTransformUtil
						        .getLocationsByTags(HtmlFormEntryConstants.ANSWER_LOCATION_TAGS, parameters);
						if ((locationList == null) || (locationList != null && locationList.size() < 1)) {
							// if no locations by tags are found then get all locations
							locationList = Context.getLocationService().getAllLocations();
						}
						
						for (Location location : locationList) {
							String label = Htmlform2MuzimaTransformUtil.format(location);
							Option option = new Option(label, location.getId().toString(),
							        location.getId().toString().equals(initialValue));
							locationOptions.add(option);
						}
						Collections.sort(locationOptions, new OptionComparator());
						
						// if initialValueIsSet=false, no initial/default location, hence this shows the 'select input' field as first option
						boolean initialValueIsSet = !(initialValue == null);
						((DropdownField) valueField).addOption(
						    new Option(Context.getMessageSourceService().getMessage("htmlformentry.chooseALocation"), "",
						            !initialValueIsSet));
						if (!locationOptions.isEmpty()) {
							for (Option option : locationOptions)
								((DropdownField) valueField).addOption(option);
						}
						//TODO implement Persons
						//				} else if ("person".equals(parameters.get("style"))) {
						//					
						//					List<PersonStub> options = new ArrayList<PersonStub>();
						//					List<Option> personOptions = new ArrayList<Option>();
						//					
						//					// If specific persons are specified, display only those persons in order
						//					String personsParam = parameters.get("persons");
						//					if (personsParam != null) {
						//						for (String s : personsParam.split(",")) {
						//							Person p = tmlform2MuzimaTransformUtil.getPerson(s);
						//							if (p == null) {
						//								throw new RuntimeException("Cannot find Person: " + s);
						//							}
						//							options.add(new PersonStub(p));
						//						}
						//					}
						//					
						//					// Only if specific person ids are not passed in do we get by user Role
						//					if (options.isEmpty()) {
						//						
						//						List<PersonStub> users = new ArrayList<PersonStub>();
						//						
						//						// If the "role" attribute is passed in, limit to users with this role
						//						if (parameters.get("role") != null) {
						//							Role role = Context.getUserService().getRole(parameters.get("role"));
						//							if (role == null) {
						//								throw new RuntimeException("Cannot find role: " + parameters.get("role"));
						//							} else {
						//								users = Context.getService(HtmlFormEntryService.class).getUsersAsPersonStubs(role.getRole());
						//							}
						//						}
						//						
						//						// Otherwise, limit to users with the default OpenMRS PROVIDER role, 
						//						else {
						//							String defaultRole = RoleConstants.PROVIDER;
						//							Role role = Context.getUserService().getRole(defaultRole);
						//							if (role != null) {
						//								users = Context.getService(HtmlFormEntryService.class).getUsersAsPersonStubs(role.getRole());
						//							}
						//							// If this role isn't used, default to all Users
						//							if (users.isEmpty()) {
						//								users = Context.getService(HtmlFormEntryService.class).getUsersAsPersonStubs(null);
						//							}
						//						}
						//						options.addAll(users);
						//						//    					sortOptions = true;
						//					}
						//					
						//					valueField = new PersonStubField(options);
						//					
						//				} 
						else {
							if (textAnswers.size() == 0) {
								Integer rows = null;
								Integer cols = null;
								
								try {
									rows = Integer.valueOf(parameters.get("rows"));
								}
								catch (Exception ex) {}
								try {
									cols = Integer.valueOf(parameters.get("cols"));
								}
								catch (Exception ex) {}
								
								if (rows != null || cols != null || "textarea".equals(parameters.get("style"))) {
									valueField = new TextField(concept, locale, valueLabel, rows, cols);
								} else {
									Integer textFieldSize = null;
									try {
										textFieldSize = Integer.valueOf(parameters.get("size"));
									}
									catch (Exception ex) {}
									
									valueField = new TextField(concept, locale, valueLabel, textFieldSize);
								}
								((TextField) valueField).setPlaceholder(parameters.get("placeholder"));
								
								try {
									Integer maxlength = Integer.valueOf(parameters.get("maxlength"));
									((TextField) valueField).setTextFieldMaxLength(maxlength);
								}
								catch (Exception ex) {}
							} else {
								if ("radio".equals(parameters.get("style"))) {
									valueField = new RadioButtonsField(concept, locale, valueLabel);
									if (answerSeparator != null) {
										((RadioButtonsField) valueField).setAnswerSeparator(answerSeparator);
									}
								} else { // dropdown
									valueField = buildDropdownField(concept, locale, valueLabel, size);
								}
								
								for (int i = 0; i < textAnswers.size(); i++) {
									String s = textAnswers.get(i);
									String label = null;
									if (answerLabels != null && i < answerLabels.size()) {
										label = answerLabels.get(i);
									} else {
										label = s;
									}
									((SingleOptionField) valueField).addOption(new Option(label, s, false));
								}
								
							}
						}
						
						if (initialValue != null) {
							if (isLocationObs) {
								Location l = Htmlform2MuzimaTransformUtil.getLocation(initialValue);
								if (l == null) {
									throw new RuntimeException("Cannot find Location: " + initialValue);
								}
								valueField.setDefaultValue(l);
							}
							//TODO handle person
							//					else if ("person".equals(parameters.get("style"))) {
							//						Person p = Htmlform2MuzimaTransformUtil.getPerson(initialValue);
							//						if (p == null) {
							//							throw new RuntimeException("Cannot find Person: " + initialValue);
							//						}
							//						valueField.setDefaultValue(new PersonStub(p));
							//					} 
							else {
								valueField.setDefaultValue(initialValue);
							}
						}
					} else if (concept.getDatatype().isCoded()) {
						if (parameters.get("answerConceptIds") != null) {
							try {
								for (StringTokenizer st = new StringTokenizer(parameters.get("answerConceptIds"), ","); st
								        .hasMoreTokens();) {
									Concept c = Htmlform2MuzimaTransformUtil.getConcept(st.nextToken());
									if (c == null)
										throw new RuntimeException("Cannot find concept " + st.nextToken());
									conceptAnswers.add(c);
								}
							}
							catch (Exception ex) {
								throw new RuntimeException("Error in answer list for concept " + concept.getConceptId()
								        + " (" + ex.toString() + "): " + conceptAnswers);
							}
						} else if (answerConceptSetIds != null && !isAutocomplete) {
							try {
								for (StringTokenizer st = new StringTokenizer(answerConceptSetIds, ","); st
								        .hasMoreTokens();) {
									Concept answerConceptSet = Htmlform2MuzimaTransformUtil.getConcept(st.nextToken());
									conceptAnswers
									        .addAll(Context.getConceptService().getConceptsByConceptSet(answerConceptSet));
								}
							}
							catch (Exception ex) {
								throw new RuntimeException(
								        "Error loading answer concepts from answerConceptSet " + answerConceptSetIds, ex);
							}
						} else if (parameters.get("answerClasses") != null && !isAutocomplete) {
							try {
								for (StringTokenizer st = new StringTokenizer(parameters.get("answerClasses"), ","); st
								        .hasMoreTokens();) {
									String className = st.nextToken().trim();
									ConceptClass cc = Context.getConceptService().getConceptClassByName(className);
									if (cc == null) {
										throw new RuntimeException("Cannot find concept class " + className);
									}
									conceptAnswers.addAll(Context.getConceptService().getConceptsByClass(cc));
								}
								Collections.sort(conceptAnswers, conceptNameComparator);
							}
							catch (Exception ex) {
								throw new RuntimeException("Error in answer class list for concept " + concept.getConceptId()
								        + " (" + ex.toString() + "): " + conceptAnswers);
							}
						}
						
						if (answerConcept != null) {
							// if there's also an answer concept specified, this is a single
							// checkbox
							answerLabel = parameters.get("answerLabel");
							if (answerLabel == null) {
								String answerCode = parameters.get("answerCode");
								if (answerCode != null) {
									//translations have been handled
									answerLabel = answerCode;
								} else {
									answerLabel = answerConcept.getName(locale, false).getName();
								}
							}
							valueField = createToggleCheckbox(concept, answerConcept, null, locale, valueLabel, answerLabel,
							    parameters.get("toggle"));
							
							if (defaultValue != null) {
								Concept initialValue1 = Htmlform2MuzimaTransformUtil.getConcept(defaultValue);
								if (initialValue1 == null) {
									throw new IllegalArgumentException(
									        "Invalid default value. Cannot find concept: " + defaultValue);
								}
								if (!answerConcept.equals(initialValue1)) {
									throw new IllegalArgumentException("Invalid default value: " + defaultValue
									        + ". The only allowed answer is: " + answerConcept.getId());
								}
								valueField.setDefaultValue(initialValue1);
							}
						} else if ("true".equals(parameters.get("multiple"))) {
							// if this is a select-multi, we need a group of checkboxes
							//this was not implemented in htmlformentry but is needed in muzima
							if (conceptAnswers != null) {
								valueField = new CheckboxField(concept, locale, valueLabel);
							}
							
						} else {
							// allow selecting one of multiple possible coded values
							
							// if no answers are specified explicitly (by conceptAnswers or conceptClasses), get them from concept.answers.
							if (!parameters.containsKey("answerConceptIds") && !parameters.containsKey("answerClasses")
							        && !parameters.containsKey("answerDrugs") && !parameters.containsKey("answerDrugId")
							        && !parameters.containsKey("answerConceptSetIds")) {
								conceptAnswers = new ArrayList<Concept>();
								for (ConceptAnswer ca : concept.getAnswers(false)) {
									conceptAnswers.add(ca.getAnswerConcept());
								}
								Collections.sort(conceptAnswers, conceptNameComparator);
							}
							
							if (isAutocomplete) {
								List<ConceptClass> cptClasses = new ArrayList<ConceptClass>();
								if (parameters.get("answerClasses") != null) {
									for (StringTokenizer st = new StringTokenizer(parameters.get("answerClasses"), ","); st
									        .hasMoreTokens();) {
										String className = st.nextToken().trim();
										ConceptClass cc = Context.getConceptService().getConceptClassByName(className);
										cptClasses.add(cc);
									}
								}
								if ((conceptAnswers == null || conceptAnswers.isEmpty())
								        && (cptClasses == null || cptClasses.isEmpty()) && answerConceptSetIds == null) {
									throw new RuntimeException(
									        "style \"autocomplete\" but there are no possible answers. Looked for answerConcepts and "
									                + "answerClasses attributes, answerConceptSetIds, and answers for concept "
									                + concept.getConceptId());
								}
								//TODO handle dynamic autocomplete, ask if it even needed because it is not in the htmlform specification
								//								if ("true".equals(parameters.get("selectMulti"))) {
								//									DynamicAutocompleteField dacw = new DynamicAutocompleteField(conceptAnswers, cptClasses);
								//									dacw.setAllowedConceptSetIds(answerConceptSetIds);
								//									valueField = dacw;
								//								} else 
								
								ConceptSearchAutoCompleteField csaw = new ConceptSearchAutoCompleteField(concept, locale,
								        valueLabel, conceptAnswers, cptClasses);
								csaw.setAllowedConceptSetIds(answerConceptSetIds);
								valueField = csaw;
								//TODO handle drugs autocomplete		
								//							} else if (parameters.get("answerDrugs") != null) {
								//								// we support searching through all drugs via AJAX
								//								RemoteJsonAutocompleteField widget = new RemoteJsonAutocompleteField(
								//								        "/" + WebConstants.WEBAPP_NAME + "/module/htmlformentry/drugSearch.form");
								//								widget.setValueTemplate("Drug:{{id}}");
								//								if (parameters.get("displayTemplate") != null) {
								//									widget.setDisplayTemplate(parameters.get("displayTemplate"));
								//								} else {
								//									widget.setDisplayTemplate("{{name}}");
								//								}
								//								if (existingObs != null && existingObs.getValueDrug() != null) {
								//									widget.setDefaultValue(new Option(existingObs.getValueDrug().getName(),
								//									        existingObs.getValueDrug().getDrugId().toString(), true));
								//								}
								//								valueField = widget;
								
							} else if (parameters.get("answerDrugId") != null) {
								String answerDrugId = parameters.get("answerDrugId");
								if (StringUtils.isNotBlank(answerDrugId)) {
									Drug drug = Htmlform2MuzimaTransformUtil.getDrug(answerDrugId);
									if (drug == null) {
										throw new IllegalArgumentException(
										        "Cannot find Drug for answerDrugId: " + answerDrugId
										                + " in answerDrugId attribute value. Parameters: " + parameters);
									}
									valueField = createToggleCheckbox(concept, null, "Drug:" + drug.getId().toString(),
									    locale, valueLabel, drug.getName(), null);
								}
							} else {
								// Show Radio Buttons if specified, otherwise default to DropDown 
								boolean isRadio = "radio".equals(parameters.get("style"));
								if (isRadio) {
									valueField = new RadioButtonsField(concept, locale, valueLabel);
									if (answerSeparator != null) {
										((RadioButtonsField) valueField).setAnswerSeparator(answerSeparator);
									}
								} else {
									valueField = buildDropdownField(concept, locale, valueLabel, size);
								}
								for (int i = 0; i < conceptAnswers.size(); ++i) {
									Concept c = conceptAnswers.get(i);
									String label = null;
									if (answerLabels != null && i < answerLabels.size()) {
										label = answerLabels.get(i);
									} else {
										label = c.getName(locale, false).getName();
									}
									((SingleOptionField) valueField).addOption(new Option(c, label, locale, false));
								}
							}
							if (defaultValue != null) {
								Concept initialValue2 = Htmlform2MuzimaTransformUtil.getConcept(defaultValue);
								if (initialValue2 == null) {
									throw new IllegalArgumentException(
									        "Invalid default value. Cannot find concept: " + defaultValue);
								}
								
								if (!conceptAnswers.contains(initialValue2)) {
									String allowedIds = "";
									for (Concept conceptAnswer : conceptAnswers) {
										allowedIds += conceptAnswer.getId() + ", ";
									}
									allowedIds = allowedIds.substring(0, allowedIds.length() - 2);
									throw new IllegalArgumentException("Invalid default value: " + defaultValue
									        + ". The only allowed answers are: " + allowedIds);
								}
								valueField.setDefaultValue(initialValue2);
							}
						}
					} else if (concept.getDatatype().isBoolean()) {
						String noStr = parameters.get("noLabel");
						Concept yesConcept = Htmlform2MuzimaTransformUtil.getConcept("1065");
						Concept noConcept = Htmlform2MuzimaTransformUtil.getConcept("1066");
						if (StringUtils.isEmpty(noStr)) {
							noStr = noConcept.getName(locale, false).getName();
						}
						String yesStr = parameters.get("yesLabel");
						if (StringUtils.isEmpty(yesStr)) {
							yesStr = yesConcept.getName(locale, false).getName();
						}
						
						if ("checkbox".equals(parameters.get("style"))) {
							valueField = createToggleCheckbox(concept,
							    parameters.get("value") != null ? noConcept : yesConcept, null, locale, valueLabel,
							    parameters.get("value") != null ? noStr : yesStr, parameters.get("toggle"));
							valueLabel = "";
						} else if ("no_yes".equals(parameters.get("style"))) {
							valueField = new RadioButtonsField(concept, locale, valueLabel);
							((RadioButtonsField) valueField).addOption(new Option(noConcept, noStr, locale, false));
							((RadioButtonsField) valueField).addOption(new Option(yesConcept, yesStr, locale, false));
						} else if ("yes_no".equals(parameters.get("style"))) {
							valueField = new RadioButtonsField(concept, locale, valueLabel);
							((RadioButtonsField) valueField).addOption(new Option(yesConcept, yesStr, locale, false));
							((RadioButtonsField) valueField).addOption(new Option(yesConcept, yesStr, locale, false));
						} else if ("no_yes_dropdown".equals(parameters.get("style"))) {
							valueField = new DropdownField(concept, locale, valueLabel);
							((DropdownField) valueField).addOption(new Option());
							((DropdownField) valueField).addOption(new Option(noConcept, noStr, locale, false));
							((DropdownField) valueField).addOption(new Option(yesConcept, yesStr, locale, false));
						} else if ("yes_no_dropdown".equals(parameters.get("style"))) {
							valueField = new DropdownField();
							((DropdownField) valueField).addOption(new Option());
							((DropdownField) valueField).addOption(new Option(yesConcept, yesStr, locale, false));
							((DropdownField) valueField).addOption(new Option(noConcept, noStr, locale, false));
						} else {
							throw new RuntimeException("Boolean with style = " + parameters.get("style")
							        + " not yet implemented (concept = " + concept.getConceptId() + ")");
						}
						
						if (defaultValue != null) {
							defaultValue = defaultValue.trim();
							
							//Check the default value. Do not use Boolean.valueOf as it only tests for 'true'.
							Boolean initialValue3 = null;
							if (defaultValue.equalsIgnoreCase(Boolean.TRUE.toString())) {
								initialValue3 = true;
							} else if (defaultValue.equalsIgnoreCase(Boolean.FALSE.toString())) {
								initialValue3 = false;
							} else if (defaultValue.isEmpty()) {
								initialValue3 = null;
							} else {
								throw new IllegalArgumentException(
								        "Invalid default value " + defaultValue + ". Must be 'true', 'false' or ''.");
							}
							valueField.setDefaultValue(initialValue);
						}
						
					}
					
					else {
						DateField dateField = null;
						TimeField timeField = null;
						boolean disableTime = "false".equalsIgnoreCase(parameters.get("allowTime"));
						boolean hideSeconds = "true".equalsIgnoreCase(parameters.get("hideSeconds"));
						
						if (ConceptDatatype.DATE.equals(concept.getDatatype().getHl7Abbreviation())
						        || (ConceptDatatype.DATETIME.equals(concept.getDatatype().getHl7Abbreviation())
						                && disableTime)) {
							valueField = new DateField(concept, locale, valueLabel);
						} else if (ConceptDatatype.TIME.equals(concept.getDatatype().getHl7Abbreviation())) {
							valueField = new TimeField(concept, locale, valueLabel, null);
							if (hideSeconds) {
								((TimeField) valueField).setHideSeconds(true);
							}
						} else if (ConceptDatatype.DATETIME.equals(concept.getDatatype().getHl7Abbreviation())) {
							valueField = new DateTimeField(concept, locale, valueLabel);
						} else {
							throw new RuntimeException("Cannot handle datatype: " + concept.getDatatype().getName()
							        + " (for concept " + concept.getConceptId() + ")");
						}
						
						if (defaultValue != null && parameters.get("defaultDatetime") != null) {
							throw new IllegalArgumentException(
							        "Cannot set defaultDatetime and defaultValue at the same time.");
						} else if (defaultValue == null) {
							defaultValue = parameters.get("defaultDatetime");
						}
						if (defaultValue != null) {
							valueField.setDefaultValue(
							    Htmlform2MuzimaTransformUtil.translateDatetimeParam(defaultValue, defaultDatetimeFormat));
						}
						
					}
				}
				
				//TODO NOTE Fields registered to context here
				
				// if a date is requested, do that too
				if ("true".equals(parameters.get("showDate")) || parameters.containsKey("dateLabel")) {
					if (parameters.containsKey("dateLabel")) {
						dateLabel = parameters.get("dateLabel");
					}
					dateField = new DateField(concept, locale, valueLabel);
					if (parameters.get("defaultObsDatetime") != null) {
						// Make sure this format continues to match
						// the <obs> attribute defaultObsDatetime documentation at
						// https://wiki.openmrs.org/display/docs/HTML+Form+Entry+Module+HTML+Reference#HTMLFormEntryModuleHTMLReference-%3Cobs%3E
						String supportedDateFormat = "yyyy-MM-dd-HH-mm";
						dateField.setDefaultValue(Htmlform2MuzimaTransformUtil
						        .translateDatetimeParam(parameters.get("defaultObsDatetime"), supportedDateFormat));
					}
				}
			}
		}
	
	

	private FormField buildDropdownField(Integer size) {
		FormField dropdownField = new DropdownField(size);
		if (size == 1 || !required) {
			// show an empty option when size =1, even if required =true
			((DropdownField) dropdownField).addOption(new Option());
		}
		return dropdownField;
	}
	
	private FormField buildDropdownField(Concept concept, Locale locale, String label, Integer size) {
		FormField dropdownField = new DropdownField(concept, locale, label, size);
		if (size == 1 || !required) {
			// show an empty option when size =1, even if required =true
			((DropdownField) dropdownField).addOption(new Option());
		}
		return dropdownField;
	}
	
	private ToggleCheckbox createToggleCheckbox(Concept concept, Concept ansConcept, String ansValue, Locale locale,
	        String fieldLabel, String ansLabel, String toggleParameter) {
		if (toggleParameter != null) {
			ToggleField toggleField = new ToggleField(toggleParameter);
			if (ansConcept != null) {
				return new ToggleCheckbox(concept, ansConcept, locale, fieldLabel, ansLabel, toggleField.getTargetId(),
				        toggleField.isToggleDim());
			} else {
				return new ToggleCheckbox(concept, ansValue, locale, fieldLabel, ansLabel, toggleField.getTargetId(),
				        toggleField.isToggleDim());
			}
		} else {
			if (ansConcept != null) {
				return new ToggleCheckbox(concept, ansConcept, locale, fieldLabel, ansLabel, null);
			} else {
				return new ToggleCheckbox(concept, ansValue, locale, fieldLabel, ansLabel, null);
			}
		}
		
	}
	
	@Override
	public String generateHtml() {
		
		StringBuilder ret = new StringBuilder();
		ret.append(valueField.generateHtml());
		if (dateField != null) {
			ret.append(" ");
			ret.append(dateField.generateHtml());
		}
		this.jsString = valueField.getJs();
		return ret.toString();
	}
	
	private Comparator<Concept> conceptNameComparator = new Comparator<Concept>() {
		
		@Override
		public int compare(Concept c1, Concept c2) {
			String n1 = c1.getName(locale, false).getName();
			String n2 = c2.getName(locale, false).getName();
			return n1.compareTo(n2);
		}
	};
	
	/**
	 * Returns the concept associated with this Observation
	 */
	public Concept getConcept() {
		return concept;
	}
	
	/**
	 * Returns the concept associated with the answer to this Observation
	 */
	public Concept getAnswerConcept() {
		return answerConcept;
	}
	
	/**
	 * Returns the concepts that are potential answers to this Observation
	 */
	public List<Concept> getConceptAnswers() {
		return conceptAnswers;
	}
	
	/**
	 * Returns the Numbers that are potential answers for this Observation
	 */
	public List<Number> getNumericAnswers() {
		return numericAnswers;
	}
	
	/**
	 * Returns the potential text answers for this Observation
	 */
	public List<String> getTextAnswers() {
		return textAnswers;
	}
	
	/**
	 * Returns the labels to use for the answers to this Observation
	 */
	public List<String> getAnswerLabels() {
		return answerLabels;
	}
	
	/**
	 * Returns the label to use for the answer to this Observation
	 */
	public String getAnswerLabel() {
		return answerLabel;
	}
	
	public String getValueLabel() {
		return valueLabel;
	}
	
	public void whenValueThenDisplaySection(Object value, String thenSection) {
		whenValueThenDisplaySection.put(value, thenSection);
	}
	
	public Map<Object, String> getWhenValueThenDisplaySection() {
		return whenValueThenDisplaySection;
	}
	
	public void whenValueThenJavaScript(Object value, String thenJavaScript) {
		whenValueThenJavascript.put(value, thenJavaScript);
	}
	
	public Map<Object, String> getWhenValueThenJavascript() {
		return whenValueThenJavascript;
	}
	
	public void whenValueElseJavaScript(Object value, String elseJavaScript) {
		whenValueElseJavascript.put(value, elseJavaScript);
	}
	
	public Map<Object, String> getWhenValueElseJavascript() {
		return whenValueElseJavascript;
	}
	
	public boolean hasWhenValueThen() {
		return whenValueThenDisplaySection.size() > 0 || whenValueThenJavascript.size() > 0
		        || whenValueElseJavascript.size() > 0;
	}
	
	public String getJsString() {
		return jsString;
	}
	
}
