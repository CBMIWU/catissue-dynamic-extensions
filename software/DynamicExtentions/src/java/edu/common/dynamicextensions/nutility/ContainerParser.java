package edu.common.dynamicextensions.nutility;

import java.io.File;
import java.io.FileReader;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import au.com.bytecode.opencsv.CSVReader;
import edu.common.dynamicextensions.domain.nui.CheckBox;
import edu.common.dynamicextensions.domain.nui.ComboBox;
import edu.common.dynamicextensions.domain.nui.Container;
import edu.common.dynamicextensions.domain.nui.Control;
import edu.common.dynamicextensions.domain.nui.Control.LabelPosition;
import edu.common.dynamicextensions.domain.nui.DataType;
import edu.common.dynamicextensions.domain.nui.DatePicker;
import edu.common.dynamicextensions.domain.nui.DatePicker.DefaultDateType;
import edu.common.dynamicextensions.domain.nui.FileUploadControl;
import edu.common.dynamicextensions.domain.nui.Label;
import edu.common.dynamicextensions.domain.nui.ListBox;
import edu.common.dynamicextensions.domain.nui.MultiSelectCheckBox;
import edu.common.dynamicextensions.domain.nui.MultiSelectListBox;
import edu.common.dynamicextensions.domain.nui.NumberField;
import edu.common.dynamicextensions.domain.nui.Page;
import edu.common.dynamicextensions.domain.nui.PermissibleValue;
import edu.common.dynamicextensions.domain.nui.PvDataSource;
import edu.common.dynamicextensions.domain.nui.PvVersion;
import edu.common.dynamicextensions.domain.nui.RadioButton;
import edu.common.dynamicextensions.domain.nui.SelectControl;
import edu.common.dynamicextensions.domain.nui.SkipRule;
import edu.common.dynamicextensions.domain.nui.SkipRuleBuilder;
import edu.common.dynamicextensions.domain.nui.SkipRuleBuilder.ActionBuilder;
import edu.common.dynamicextensions.domain.nui.SkipRuleBuilder.ConditionBuilder;
import edu.common.dynamicextensions.domain.nui.StringTextField;
import edu.common.dynamicextensions.domain.nui.SubFormControl;
import edu.common.dynamicextensions.domain.nui.SurveyContainer;
import edu.common.dynamicextensions.domain.nui.TextArea;
import edu.common.dynamicextensions.domain.nui.TextField;
import edu.common.dynamicextensions.exception.DynamicExtensionsSystemException;
import edu.common.dynamicextensions.util.parser.FormulaParser;

public class ContainerParser {

	private final String formXml;
	
	private final String pvDir;
	
	public ContainerParser(String formXml, String pvDir) {
		this.formXml = formXml;
		this.pvDir = pvDir;
	}
	
	public Container parse() throws Exception {
		DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder docBuilder = docFactory.newDocumentBuilder();
		Document doc = docBuilder.parse(formXml);
		doc.getDocumentElement().normalize();

		NodeList nodes = doc.getElementsByTagName("view");
		Node viewNode = nodes.item(0);

		Container container = null;

		nodes = doc.getElementsByTagName("survey-form");
		if (nodes != null && nodes.getLength() == 1) {
			container = parseSurveyContainer((Element) viewNode);
		} else {
			container = parseContainer((Element) viewNode);
		}

		nodes = doc.getElementsByTagName("skipRules");
		if (nodes != null && nodes.getLength() == 1) {
			parseAndSetSkipRules(container, (Element) nodes.item(0));
		}
		updateCalculatedSourceControls(container, container);
		return container;
	}


	private void updateCalculatedSourceControls(Container container, Container rootContainer)
			throws DynamicExtensionsSystemException {

		FormulaParser formulaParser = new FormulaParser();
		for (Control control : container.getControls()) {
			if (control instanceof NumberField && ((NumberField) control).isCalculated()) {
				updateSourceCalculatedControls(rootContainer, formulaParser, control);

			} else if (control instanceof SubFormControl) {
				updateCalculatedSourceControls(((SubFormControl) control).getSubContainer(), rootContainer);
			}
		}
	}

	private void updateSourceCalculatedControls(Container rootContainer, FormulaParser formulaParser, Control control)
			throws DynamicExtensionsSystemException {
		formulaParser.parseExpression(((NumberField) control).getFormula());
		for (String symbol : formulaParser.getSymobols()) {
			Control sourceControl = rootContainer.getControl(symbol,"\\.");
			sourceControl.setCalculatedSourceControl(true);
		}
	}

	private Container parseContainer(Element viewElement) {
		Container container = new Container();
		int currentRow = 0;
		
		container.useAsDto();
		setContainerProps(container, viewElement);
		
		NodeList viewNodes = viewElement.getChildNodes();
		for (int i = 0; i < viewNodes.getLength(); ++i) {
			if (viewNodes.item(i).getNodeType() != Node.ELEMENT_NODE) {
				continue;
			}
			
			Node row = viewNodes.item(i);
			if (row.getNodeName().equals("row")) {
				++currentRow;
				List<Control> controls = parseFormRow(row, currentRow);
				for (Control control : controls) {
					container.addControl(control);
				}				
			}
		}
		return container;
	}

	private Container parseSurveyContainer(Element viewElement) {
		SurveyContainer surveyContainer = new SurveyContainer();
		
		surveyContainer.useAsDto();
		setContainerProps(surveyContainer, viewElement);
		
		NodeList viewNodes = viewElement.getChildNodes();
		for (int i = 0; i < viewNodes.getLength(); ++i) {

			if (viewNodes.item(i).getNodeType() != Node.ELEMENT_NODE) {
				continue;
			}

			Node pageElement = viewNodes.item(i);

			if (pageElement.getNodeName().equals("page")) {
				Page page = new Page();
				setPageProps((Element) pageElement, page);
				page.setContainer(surveyContainer);
				surveyContainer.addPage(page);
				parsePage(page, (Element) pageElement);

			}
		}		
		return surveyContainer;
	}

	protected void setPageProps(Element pageElement, Page page) {

		String name = getTextValue(pageElement, "name");
		if (name == null) {
			throw new RuntimeException("Page name can't be null");
		}
		page.setName(name);

		String caption = getTextValue(pageElement, "caption");
		if (caption == null) {
			throw new RuntimeException("Page caption can't be null");
		}
		page.setCaption(caption);

	}

	private void parsePage(Page page, Element pageElement) {

		int currentRow = 0;

		NodeList rowList = pageElement.getElementsByTagName("row");

		for (int i = 0; i < rowList.getLength(); i++) {
			Node row = rowList.item(i);

			if (row.getNodeType() != Node.ELEMENT_NODE) {
				continue;
			}

			if (row.getNodeName().equals("row")) {
				List<Control> controls = parseFormRow(row, ++currentRow);

				for (Control control : controls) {
					control.setLabelPosition(LabelPosition.TOP);
					page.addControl(control);
				}
			}
		}
	}

	private void setContainerProps(Container container, Element viewElement) {
		Long id = getLongValue(viewElement, "id");
		container.setId(id);

		String name = getTextValue(viewElement, "name");
		container.setName(name);
		
		String caption = getTextValue(viewElement, "caption");
		if (caption == null) {
			throw new RuntimeException("Form caption can't be null");
		}
		container.setCaption(caption);		
		
		String table = getTextValue(viewElement, "table");
		if (table != null) {
			container.setDbTableName(table);
		}
	}
	
	private List<Control> parseFormRow(Node row, int currentRow) {
		List<Control> controls = new ArrayList<Control>();		
		NodeList ctrlNodes = row.getChildNodes();
		int xpos = 0;
		for (int i = 0; i < ctrlNodes.getLength(); ++i) {
			if (ctrlNodes.item(i).getNodeType() != Node.ELEMENT_NODE) {
				continue;
			}
			
			xpos++;
			Element ctrlEle = (Element)ctrlNodes.item(i);
			String ctrlName = ctrlEle.getNodeName();
			Control ctrl = null;
				
			if (ctrlName.equals("label")) {
				ctrl = parseLabel(ctrlEle, currentRow, xpos);
			} else if (ctrlName.equals("textField")) {
				ctrl = parseTextField(ctrlEle, currentRow, xpos);
			} else if (ctrlName.equals("textArea")) {
				ctrl = parseTextArea(ctrlEle, currentRow, xpos);
			} else if (ctrlName.equals("numberField")) {
				ctrl = parseNumberField(ctrlEle, currentRow, xpos);
			} else if (ctrlName.equals("booleanCheckBox")) {
				ctrl = parseBooleanCheckBox(ctrlEle, currentRow, xpos);
			} else if (ctrlName.equals("dropDown")) {
				ctrl = parseComboBox(ctrlEle, currentRow, xpos); 
			} else if (ctrlName.equals("datePicker")) {
				ctrl = parseDatePicker(ctrlEle, currentRow, xpos);
			} else if (ctrlName.equals("fileUpload")) {
				ctrl = parseFileUpload(ctrlEle, currentRow, xpos);
			} else if (ctrlName.equals("listBox")) {
				ctrl = parseListBox(ctrlEle, currentRow, xpos);
			} else if (ctrlName.equals("checkBox")) {
				ctrl = parseMultiSelectCheckBox(ctrlEle, currentRow, xpos);
			} else if (ctrlName.equals("radioButton")) {
				ctrl = parseRadioButton(ctrlEle, currentRow, xpos);
			} else if (ctrlName.equals("subForm")) {
				ctrl = parseSubForm(ctrlEle, currentRow, xpos);
			}
			
			controls.add(ctrl);
		}
		
		return controls;		
	}
	
	
	private Control parseSubForm(Element subFormEle, int currentRow, int i) {
		SubFormControl subForm = new SubFormControl();
		setControlProps(subForm, subFormEle, currentRow, i);
		
		Integer numEntries = getIntValue(subFormEle, "maxEntries", null);
		if (numEntries != null) {
			subForm.setNoOfEntries(numEntries);
		} 
		
		subForm.setShowAddMoreLink(getBooleanValue(subFormEle, "showAddMoreLink"));
		subForm.setPasteButtonEnabled(getBooleanValue(subFormEle, "pasteButtonEnabled"));
		subForm.setParentKey(getTextValue(subFormEle, "parentKey"));
		subForm.setForeignKey(getTextValue(subFormEle, "foreignKey"));

		Container subContainer = parseContainer(subFormEle);
		subForm.setSubContainer(subContainer);
		return subForm;
	}

	private Control parseRadioButton(Element radioEle, int currentRow, int i) {
		RadioButton radioButton = new RadioButton();
		setSelectProps(radioButton, radioEle, currentRow, i);
		
		Integer optionsPerRow = getIntValue(radioEle, "optionsPerRow", null);
		if (optionsPerRow != null) {
			radioButton.setOptionsPerRow(optionsPerRow);
		}
		return radioButton;
	}

	private Control parseMultiSelectCheckBox(Element msCbEle, int currentRow, int i) {
		MultiSelectCheckBox msCheckBox = new MultiSelectCheckBox();
		setSelectProps(msCheckBox, msCbEle, currentRow, i);
		
		Integer optionsPerRow = getIntValue(msCbEle, "optionsPerRow", null);
		if (optionsPerRow != null) {
			msCheckBox.setOptionsPerRow(optionsPerRow);
		}
		
		msCheckBox.setParentKey(getTextValue(msCbEle, "parentKey"));
		msCheckBox.setForeignKey(getTextValue(msCbEle, "foreignKey"));
		msCheckBox.setTableName(getTextValue(msCbEle, "table"));
		
		return msCheckBox;
	}

	private Control parseListBox(Element listEle, int currentRow, int i) {
		ListBox listBox = null;
		
		boolean isMultiSelect = getBooleanValue(listEle, "multiSelect", false);
		boolean autoComplete  = getBooleanValue(listEle, "autoCompleteDropdown", false);

		if (!isMultiSelect && autoComplete) {
			throw new RuntimeException("Autocomplete dropdown cannot be used for non-multiselect listBox");
		}
		
		if (isMultiSelect) {
			MultiSelectListBox msLb = new MultiSelectListBox();						
			msLb.setTableName(getTextValue(listEle, "table"));
			msLb.setParentKey(getTextValue(listEle, "parentKey"));
			msLb.setForeignKey(getTextValue(listEle, "foreignKey"));
			listBox = msLb;
		} else {
			listBox = new ListBox();
		}
		
		setSelectProps(listBox, listEle, currentRow, i);
				
		Integer minQueryChars = getIntValue(listEle, "minQueryChars", null);
		if (minQueryChars != null) {
			listBox.setMinQueryChars(minQueryChars);
		}
		
		Integer noOfRows = getIntValue(listEle, "noOfRows", null);
		if (noOfRows != null) {
			listBox.setNoOfRows(noOfRows);
		}
				
		listBox.setAutoCompleteDropdownEnabled(autoComplete);		
		return listBox;
	}


	private Control parseComboBox(Element cbEle, int currentRow, int i) {
		ComboBox comboBox = new ComboBox();
		setSelectProps(comboBox, cbEle, currentRow, i);
		
		comboBox.setLazyPvFetchingEnabled(getBooleanValue(cbEle, "lazyLoad"));
		
		Integer minQueryChars = getIntValue(cbEle, "minQueryChars", null);
		if (minQueryChars != null) {
			comboBox.setMinQueryChars(minQueryChars);
		}
		
		Integer width = getIntValue(cbEle, "width", null);
		if (width != null) {
			comboBox.setNoOfColumns(width);
		}
		
		return comboBox;
	}

	private Control parseBooleanCheckBox(Element chkEle, int currentRow, int i) {
		CheckBox checkBox = new CheckBox();
		setControlProps(checkBox, chkEle, currentRow, i);		
		checkBox.setDefaultValueChecked(getBooleanValue(chkEle, "checked"));
		return checkBox;
	}

	private Control parseFileUpload(Element fileEle, int currentRow, int i) {
		FileUploadControl fileControl = new FileUploadControl();
		setControlProps(fileControl, fileEle, currentRow, i);
		return fileControl;
	}

	private Control parseDatePicker(Element dateEle, int currentRow, int i) {
		DatePicker datePicker = new DatePicker();
		setControlProps(datePicker, dateEle, currentRow, i);
		
		String format = getTextValue(dateEle, "format", "MM-dd-yyyy");
		datePicker.setFormat(format);
		datePicker.setShowCalendar(getBooleanValue(dateEle, "showCalendar", true));
		
		String defaultDate = getTextValue(dateEle, "default", "none");
		if (defaultDate.equals("none")) {
			datePicker.setDefaultDateType(DefaultDateType.NONE);
		} else if (defaultDate.equals("current_date")) {
			datePicker.setDefaultDateType(DefaultDateType.CURRENT_DATE);
		} else {
			try {
				SimpleDateFormat sdf = new SimpleDateFormat(format);
				Date date = sdf.parse(defaultDate);
				datePicker.setDefaultDate(date);				
			} catch (Exception e) {
				throw new RuntimeException("Invalid default date: " + defaultDate);
			}
		}
		
		return datePicker;
	}
	
	private Control parseNumberField(Element numEle, int currentRow, int i) {
		NumberField numberField = new NumberField();
		setTextFieldProps(numberField, numEle, currentRow, i);
		
		Integer digitsAfterDecimal = getIntValue(numEle, "noOfDigitsAfterDecimal", null);
		if (digitsAfterDecimal != null) {
			numberField.setNoOfDigitsAfterDecimal(digitsAfterDecimal);
		}
		numberField.setNoOfDigits(getIntValue(numEle, "noOfDigits", 19));
		numberField.setMeasurementUnits(getTextValue(numEle, "measurementUnits"));
		
		numberField.setFormula(getTextValue(numEle, "formula"));
		if (numberField.getFormula() != null && !numberField.getFormula().trim().isEmpty()) {
			numberField.setCalculated(true);
		}
		
		numberField.setMinValue(getTextValue(numEle, "minValue"));
		numberField.setMaxValue(getTextValue(numEle, "maxValue"));
		return numberField;
	}

	private Control parseTextArea(Element textEle, int currentRow, int i) {
		TextArea textArea = new TextArea();
		setTextFieldProps(textArea, textEle, currentRow, i);
		
		Integer height = getIntValue(textEle, "height", null);
		if (height != null) {
			textArea.setNoOfRows(height);
		}
		
		Integer minLen = getIntValue(textEle, "minLength", null);
		if (minLen != null) {
			textArea.setMinLength(minLen);
		}

		Integer maxLen = getIntValue(textEle, "maxLength", null);
		if (maxLen != null) {
			textArea.setMaxLength(maxLen);
		}

		return textArea;
	}

	private Control parseTextField(Element textEle, int currentRow, int i) {
		StringTextField textField = new StringTextField();
		setTextFieldProps(textField, textEle, currentRow, i);		
		
		textField.setUrl(getBooleanValue(textEle, "url"));
		textField.setPassword(getBooleanValue(textEle, "password"));
		
		Integer minLen = getIntValue(textEle, "minLength", null);
		if (minLen != null) {
			textField.setMinLength(minLen);
		}
		
		Integer maxLen = getIntValue(textEle, "maxLength", null);
		if (maxLen != null) {
			textField.setMaxLength(maxLen);
		}

		return textField;
	}

	private Control parseLabel(Element labelEle, int currentRow, int i) {
		Label label = new Label();
		setControlProps(label, labelEle, currentRow, i);
				
		String heading = getTextValue(labelEle, "heading");
		String note = getTextValue(labelEle, "note");
		if (heading != null && !heading.trim().isEmpty()) {
			label.setCaption(heading);
			label.setHeading(true);
		} else if (note != null && !note.trim().isEmpty()) {
			label.setCaption(note);
			label.setHeading(false);
			label.setNote(true);			
		}
		
		return label;
	}
	
	private void setSelectProps(SelectControl selectControl, Element ctrlEle, int currentRow, int i) {		
		setControlProps(selectControl, ctrlEle, currentRow, i);		

		List<PermissibleValue> permissibleValues = getPemissibleValues(ctrlEle);
		PvVersion pvVersion = new PvVersion();
		pvVersion.setPermissibleValues(permissibleValues);

		String defVal = getTextValue(ctrlEle, "defaultValue");
		if (defVal != null) {
			PermissibleValue pv = new PermissibleValue();
			pv.setOptionName(defVal);
			pv.setValue(defVal);
			pvVersion.setDefaultValue(pv);
		}
		
		
		List<PvVersion> pvVersions = new ArrayList<PvVersion>();
		pvVersions.add(pvVersion);
		
		PvDataSource pvDataSource = new PvDataSource();
		pvDataSource.setPvVersions(pvVersions);
		pvDataSource.setDataType(DataType.STRING); // TODO: Need to read data type of options as well
		selectControl.setPvDataSource(pvDataSource);
	}
	
	private List<PermissibleValue> getPemissibleValues(Element optionsParentEl) {
		List<PermissibleValue> pvs = null;
		
		Element options = (Element)optionsParentEl.getElementsByTagName("options").item(0);		
		NodeList optionFile = options.getElementsByTagName("optionsFile");
		if (optionFile != null && optionFile.getLength() == 1) { // read options from file
			String file = optionFile.item(0).getFirstChild().getNodeValue();
			pvs = getPermissibleValueFromFile(file);
		} else { // inline options
			NodeList optionList = options.getElementsByTagName("option");
			pvs = new ArrayList<PermissibleValue>();
			for (int i = 0; i < optionList.getLength(); ++i) {
				String optionVal = optionList.item(i).getFirstChild().getNodeValue();
				if (optionVal == null) {
					continue;
				}
				
				PermissibleValue pv = new PermissibleValue();
				pv.setOptionName(optionVal);
				pv.setValue(optionVal);
				pvs.add(pv);
			}
		}
		
		return pvs;
	}

	@SuppressWarnings("unchecked")
	private List<PermissibleValue>  getPermissibleValueFromFile(String optionsFile) {
		FileReader reader = null;
		CSVReader csvReader = null;
		
		try {
			List<PermissibleValue> pvs = new ArrayList<PermissibleValue>();
			
			String filePath = new StringBuilder(pvDir)
			    .append(File.separator).append(optionsFile).toString();			
			reader = new FileReader(filePath);			
			csvReader = new CSVReader(reader);
						
			List<String[]> allOptions = csvReader.readAll();
			for (String[] options : allOptions) {
				for (String option : options) {
					PermissibleValue pv = new PermissibleValue();
					pv.setOptionName(option);
					pv.setValue(option);
					pvs.add(pv);
				}
			}
			
			return pvs;
		} catch (Exception e) {
			throw new RuntimeException ("Error reading options file: " + optionsFile, e);
		} finally {
			if (csvReader != null) {
				try { csvReader.close(); } catch (Exception e) { }
			}
			
			if (reader != null) {
				try { reader.close(); } catch (Exception e) { }
			}			
		}
	}

	private void setTextFieldProps(TextField textField, Element ctrlEle, int currentRow, int i) {
		setControlProps(textField, ctrlEle, currentRow, i);
		
		Integer width = getIntValue(ctrlEle, "width", null);
		if (width != null) {
			textField.setNoOfColumns(width);
		}
		String defVal = getTextValue(ctrlEle, "defaultValue", "");
		textField.setDefaultValue(defVal);
	}
	
	private void setControlProps(Control ctrl, Element ctrlEle, int currentRow, int i) {	
		String name = getTextValue(ctrlEle, "name");
		if (name == null) {
			throw new RuntimeException("Control name can't be null. Type = " + ctrlEle.getNodeName());
		}
		ctrl.setName(name);
		ctrl.setCaption(getTextValue(ctrlEle, "caption"));
		ctrl.setCustomLabel(getTextValue(ctrlEle, "customLabel"));
		ctrl.setToolTip(getTextValue(ctrlEle, "toolTip", ""));
		ctrl.setPhi(getBooleanValue(ctrlEle, "phi"));
		ctrl.setMandatory(getBooleanValue(ctrlEle, "mandatory"));
		ctrl.setShowInGrid(getBooleanValue(ctrlEle, "showInGrid"));
		ctrl.setShowLabel(getBooleanValue(ctrlEle, "showLabel", true));
		ctrl.setSequenceNumber(currentRow);
		ctrl.setConceptCode(getTextValue(ctrlEle, "conceptCode", null));
		ctrl.setxPos(i);
		ctrl.setDbColumnName(getTextValue(ctrlEle, "column"));
	}

	private Long getLongValue(Element element, String name) {
		String value = getTextValue(element, name);
		if (value == null || value.trim().isEmpty()) {
			return null;
		} else {
			return Long.parseLong(value);
		}
	}
	
	private Integer getIntValue(Element element, String name, Integer defValue) {
		Integer value = getIntValue(element, name);

		if (value == null) {
			return defValue;
		}
		return value;
	}

	private Integer getIntValue(Element element, String name) {
		String value = getTextValue(element, name);
		if (value == null || value.trim().isEmpty()) {
			return null;
		} else {
			return Integer.parseInt(value);
		}
	}
	
	private boolean getBooleanValue(Element element, String name, boolean defVal) {
		String value = getTextValue(element, name);
		if (value == null || value.trim().isEmpty()) {
			return defVal;
		} else {
			return Boolean.parseBoolean(value);
		}
		
	}
	
	private boolean getBooleanValue(Element element, String name) {
		return getBooleanValue(element, name, false);
	}
		
	private String getTextValue(Element element, String name) {
		return getTextValue(element, name, null);
	}
	
	private String getTextValue(Element element, String name, String def) {
		NodeList nodeList = element.getElementsByTagName(name);
		if (nodeList == null || nodeList.getLength() == 0) {
			return def;
		}
		
		//
		// This check will ensure element with name is direct child of input element
		//
		if (element != nodeList.item(0).getParentNode()) { 
			return def;
		}
		return getTextValue(nodeList.item(0), def);		
	}
	
	private String getTextValue(Node node, String def) {
		if (node.getFirstChild() == null) {
			//
			// Handles scenario where-in we've elements like
			// <toolTip/> or <toolTip></toolTip>
			//
			return def;
		}
		
		if (node.getFirstChild().getNodeType() == Node.TEXT_NODE) {
			return node.getFirstChild().getNodeValue();
		}
		
		throw new RuntimeException("Element " + node.getNodeName() + " is not a text element");
	}
	
	private void parseAndSetSkipRules(Container container, Element skipRulesEle) {
		NodeList skipRules = skipRulesEle.getElementsByTagName("skipRule");
		if (skipRules == null) {
			return;
		}

		for(int i = 0 ; i < skipRules.getLength() ; i++) {
			Node node = skipRules.item(i);
			if (node.getNodeType() != Node.ELEMENT_NODE) {
				continue;
			}
			
			SkipRule rule = createSkipRule(container, (Element)node);
			container.addSkipRule(rule);
		}
	}
		
	private SkipRule createSkipRule(Container container, Element skipRuleEle) {
		SkipRuleBuilder ruleBuilder = container.newSkipRule();
		ConditionBuilder condBuilder = null;
		
		NodeList exprs = skipRuleEle.getElementsByTagName("oneOf");
		if (exprs == null || exprs.getLength() == 0) {
			exprs = skipRuleEle.getElementsByTagName("all");
			condBuilder = ruleBuilder.when().allOf();
		} else {
			condBuilder = ruleBuilder.when().anyOf();
		}
		
		if (exprs == null || exprs.getLength() != 1) {
			throw new RuntimeException("More than one expression in skip rule");
		}			

		createSkipConditions(condBuilder, (Element)exprs.item(0));
		
		
		ActionBuilder actionBuilder = condBuilder.then().perform();
		Element actionsEle = (Element)skipRuleEle.getElementsByTagName("actions").item(0);
		NodeList actionList = actionsEle.getChildNodes();
		for (int i = 0; i < actionList.getLength(); ++i) {
			if (actionList.item(i).getNodeType() != Node.ELEMENT_NODE) {
				continue;
			}
						
			Element actionEle = (Element)actionList.item(i);
			String ctrlName = actionEle.getAttribute("field");
					
			if (actionEle.getNodeName().equals("hide")) {
				actionBuilder.hide(ctrlName);
			} else if (actionEle.getNodeName().equals("showPv")) {
				//
				// TODO: Handle default pv
				//				
				List<PermissibleValue> pvs = getPemissibleValues(actionEle);
				actionBuilder.subsetPv(ctrlName, pvs, null);
			} else if (actionEle.getNodeName().equals("show")) {
				actionBuilder.show(ctrlName);
			} else if (actionEle.getNodeName().equals("enable")) {
				actionBuilder.enable(ctrlName);
			} else if (actionEle.getNodeName().equals("disable")) {
				actionBuilder.disable(ctrlName);
			}				
		}
		
		return ruleBuilder.get();		
	}
	
	private void createSkipConditions(ConditionBuilder conditionBuilder, Element expressionEle) {	
		NodeList conditionEls = expressionEle.getElementsByTagName("condition");
		for (int i = 0; i < conditionEls.getLength(); ++i) {
			if (conditionEls.item(i).getNodeType() != Node.ELEMENT_NODE) {
				continue;
			}
			
			Element conditionEl = (Element)conditionEls.item(i);
			String op = conditionEl.getAttribute("op").toLowerCase();
			String value = conditionEl.getAttribute("value");
			String ctrlName = conditionEl.getAttribute("field");
			
			if (op.equals("eq")) {
				conditionBuilder.eq(ctrlName, value);
			} else if (op.equals("lt")) {
				conditionBuilder.lt(ctrlName, value);
			} else if (op.equals("le")) {
				conditionBuilder.le(ctrlName, value);
			} else if (op.equals("gt")) {
				conditionBuilder.gt(ctrlName, value);
			} else if (op.equals("ge")) {
				conditionBuilder.ge(ctrlName, value);
			}			
		}
	}
}
