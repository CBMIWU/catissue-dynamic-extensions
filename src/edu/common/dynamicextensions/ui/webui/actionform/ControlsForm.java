
package edu.common.dynamicextensions.ui.webui.actionform;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.struts.action.ActionError;
import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionMapping;

import edu.common.dynamicextensions.domaininterface.AbstractAttributeInterface;
import edu.common.dynamicextensions.processor.ProcessorConstants;
import edu.common.dynamicextensions.ui.interfaces.AbstractAttributeUIBeanInterface;
import edu.common.dynamicextensions.ui.interfaces.ControlUIBeanInterface;
import edu.common.dynamicextensions.util.global.Constants;
import edu.wustl.common.actionForm.AbstractActionForm;
import edu.wustl.common.domain.AbstractDomainObject;
import edu.wustl.common.util.global.ApplicationProperties;
import edu.wustl.common.util.global.Validator;

/**
 * This represents a ActionForm for BuildForm.jsp
 * @author deepti_shelar
 *
 */
public class ControlsForm extends AbstractActionForm implements ControlUIBeanInterface, AbstractAttributeUIBeanInterface
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	/**
	 * Attribute Name
	 */
	AbstractAttributeInterface abstractAttribute;
	/**
	 * 
	 */
	String description;
	/**
	 * Concept code
	 */

	String attributeConceptCode;

	/**
	 * 
	 */
	String dataType;

	/**
	 * 
	 */
	List dataTypeList;

	/**
	 * 
	 */
	String attributeSize;

	/**
	 * 
	 */
	String attributeDefaultValue;

	/**
	 * 
	 */
	String format;
	/**
	 * 
	 */
	String attributeValidationRules;

	/**
	 * 
	 */
	String attributeDisplayUnits;

	/**
	 * 
	 */
	String referenceValues;
	/**
	 * 
	 */
	List displayChoiceList;
	/**
	 * 
	 */
	String displayChoice;
	/**
	 *  number of decimal places
	 */
	String attributeDecimalPlaces;
	/**
	 * Number of digits in number
	 */
	String attributeDigits;
	/**
	 * 
	 */
	String choiceList;
	/**
	 * 
	 */
	String htmlFile;
	/**
	 * Data type changed
	 */
	String dataTypeChanged;
	/**
	 * Attribute identifier
	 */
	String attributeIdentifier;
	/**
	 * 
	 */
	String caption;
	/**
	 * 
	 */
	String attributeIsPassword;
	/**
	 * 
	 */
	Boolean isPassword;
	/**
	 * 
	 */
	List toolsList = new ArrayList();
	/**
	 * 
	 */
	Boolean isHidden;
	/**
	 * 
	 */
	Integer sequenceNumber;
	/**
	 * 
	 */
	protected String cssClass;
	/**
	 * 
	 */
	protected String name;
	/**
	 * 
	 */
	protected String tooltip;
	/**
	 * 
	 */
	protected String attributeNoOfRows;
	/**
	 * 
	 */
	protected String attributenoOfCols;
	/**
	 * 
	 */
	protected String attributeMultiSelect;
	/**
	 * 
	 */
	protected String attributeSequenceNumber;
	/**
	 * 
	 */
	protected String attributeMeasurementUnits;
	/**
	 * 
	 */
	protected String attributeScale;
	/**
	 * 
	 */
	protected String userSelectedTool;
	/**
	 * 
	 */
	protected Integer columns;
	/**
	 * 
	 */
	protected Integer rows;
	/**
	 * 
	 */
	protected Boolean isMultiSelect;

	/**
	 * 
	 */
	protected String controlOperation;
	/**
	 * 
	 */
	protected String selectedControlId;
	/**
	 * 
	 */

	protected String rootName;

	/**
	 * 
	 */
	protected List childList;
	
	/**
	 * 
	 */
	//protected String showPreview;
	/**
	 * 
	 */
	protected String linesType;
	/**
	 * 
	 */
	protected String dateValueType;
	/**
	 * 
	 */
	protected String[] validationRules = new String[0];
	/**
	 * 
	 */
	protected String[] tempValidationRules = new String[] {""};
	
	/**
	 * 
	 */
	protected String min;
	/**
	 * 
	 */
	protected String max;
	/**
	 * 
	 */
	protected Map controlRuleMap;
	/**
	 * 
	 */
	protected String selectedControlCaption;
	/**
	 * is attribute identified
	 */
	protected String attributeIdentified;
	
	/**
	 * is attribute mandatory
	 */
	protected String attributeMandatory;
	/**
	 * List of measurement units
	 */
	protected List measurementUnitsList;
	
	
	/**
	 * This field will only be used if measurement unit "other" has been selected
	 */
	protected String measurementUnitOther;

	/**
	 * list of file formats
	 */
	protected List fileFormatsList;
	/**
	 * display as url
	 */
	protected String attributeDisplayAsURL;
	
	
	/**
	 * 
	 * @return List MeasurementUnitsList
	 */
	public List getMeasurementUnitsList()
	{
		return this.measurementUnitsList;
	}

	/**
	 * 
	 * @param measurementUnitsList list of measurementUnits
	 */
	public void setMeasurementUnitsList(List measurementUnitsList)
	{
		this.measurementUnitsList = measurementUnitsList;
	}

	/**
	 *  
	 * @return String AttributeIdentified
	 */
	public String getAttributeIdentified()
	{
		return this.attributeIdentified;
	}

	/**
	 * 
	 * @param attributeIdentified attributeIdentified
	 */
	public void setAttributeIdentified(String attributeIdentified)
	{
		this.attributeIdentified = attributeIdentified;
	}

	/**
	 * 
	 * @return String selectedControlCaption
	 */
	public String getSelectedControlCaption()
	{
		return this.selectedControlCaption;
	}

	/**
	 * 
	 * @param selectedControlCaption selectedControlCaption
	 */
	public void setSelectedControlCaption(String selectedControlCaption)
	{
		this.selectedControlCaption = selectedControlCaption;
	}

	/**
	 * 
	 */
	public void reset()
	{

	}

	/**
	 * Returns the id assigned to form bean.
	 * @return the id assigned to form bean.
	 */
	public int getFormId()
	{
		return Constants.ATTRIBUTE_FORM_ID;
	}

	/**
	 * @param abstractDomain abstractDomain
	 */
	public void setAllValues(AbstractDomainObject abstractDomain)
	{

	}

	/**
	 * @return Returns the attributeDescription.
	 */
	public String getDescription()
	{
		return description;
	}

	/**
	 * @param description The attributeDescription to set.
	 */
	public void setDescription(String description)
	{
		this.description = description;
	}

	/**
	 * @return Returns the dataTypeList.
	 */
	public List getDataTypeList()
	{
		return dataTypeList;
	}

	/**
	 * @param dataTypeList The dataTypeList to set.
	 */
	public void setDataTypeList(List dataTypeList)
	{
		this.dataTypeList = dataTypeList;
	}

	/**
	 * @return Returns the dataType.
	 */
	public String getDataType()
	{
		return dataType;
	}

	/**
	 * @param dataType The dataType to set.
	 */
	public void setDataType(String dataType)
	{
		this.dataType = dataType;
	}

	/**
	 * @return Returns the dataTypeChanged.
	 */
	public String getDataTypeChanged()
	{
		return dataTypeChanged;
	}

	/**
	 * @param dataTypeChanged The dataTypeChanged to set.
	 */
	public void setDataTypeChanged(String dataTypeChanged)
	{
		this.dataTypeChanged = dataTypeChanged;
	}

	/**
	 * @return Returns the htmlFile.
	 */
	public String getHtmlFile()
	{
		return htmlFile;
	}

	/**
	 * @param htmlFile The htmlFile to set.
	 */
	public void setHtmlFile(String htmlFile)
	{
		this.htmlFile = htmlFile;
	}

	/**
	 * @return Returns the attributeSize.
	 */
	public String getAttributeSize()
	{
		return attributeSize;
	}

	/**
	 * @param attributeSize The attributeSize to set.
	 */
	public void setAttributeSize(String attributeSize)
	{
		this.attributeSize = attributeSize;
	}

	/**
	 * @return Returns the choiceList.
	 */
	public String getChoiceList()
	{
		return choiceList;
	}

	/**
	 * @param choiceList The choiceList to set.
	 */
	public void setChoiceList(String choiceList)
	{
		this.choiceList = choiceList;
	}

	/**
	 * @return Returns the attributeDefaultValue.
	 */
	public String getAttributeDefaultValue()
	{
		return attributeDefaultValue;
	}

	/**
	 * @param attributeDefaultValue The attributeDefaultValue to set.
	 */
	public void setAttributeDefaultValue(String attributeDefaultValue)
	{
		this.attributeDefaultValue = attributeDefaultValue;
	}

	/**
	 * @return Returns the attributeDisplayUnits.
	 */
	public String getAttributeDisplayUnits()
	{
		return attributeDisplayUnits;
	}

	/**
	 * @param attributeDisplayUnits The attributeDisplayUnits to set.
	 */
	public void setAttributeDisplayUnits(String attributeDisplayUnits)
	{
		this.attributeDisplayUnits = attributeDisplayUnits;
	}

	/**
	 * @return Returns the attributeFormat.
	 */
	public String getFormat()
	{
		return format;
	}

	/**
	 * @param format The attributeFormat to set.
	 */
	public void setFormat(String format)
	{
		this.format = format;
	}

	/**
	 * @return Returns the attributeValidationRules.
	 */
	public String getAttributeValidationRules()
	{
		return attributeValidationRules;
	}

	/**
	 * @param attributeValidationRules The attributeValidationRules to set.
	 */
	public void setAttributeValidationRules(String attributeValidationRules)
	{
		this.attributeValidationRules = attributeValidationRules;
	}

	/**
	 * @return Returns the attributeIdentifier.
	 */
	public String getAttributeIdentifier()
	{
		return attributeIdentifier;
	}

	/**
	 * @param attributeIdentifier The attributeIdentifier to set.
	 */
	public void setAttributeIdentifier(String attributeIdentifier)
	{
		this.attributeIdentifier = attributeIdentifier;
	}

	/**
	 * @return Returns the displayChoiceList.
	 */
	public List getDisplayChoiceList()
	{
		return displayChoiceList;
	}

	/**
	 * @param displayChoiceList The displayChoiceList to set.
	 */
	public void setDisplayChoiceList(List displayChoiceList)
	{
		this.displayChoiceList = displayChoiceList;
	}

	/**
	 * @return Returns the attributeDecimalPlaces.
	 */
	public String getAttributeDecimalPlaces()
	{
		return attributeDecimalPlaces;
	}

	/**
	 * @param attributeDecimalPlaces The attributeDecimalPlaces to set.
	 */
	public void setAttributeDecimalPlaces(String attributeDecimalPlaces)
	{
		this.attributeDecimalPlaces = attributeDecimalPlaces;
	}

	/**
	 * @return Returns the referenceValues.
	 */
	public String getReferenceValues()
	{
		return referenceValues;
	}

	/**
	 * @param referenceValues The referenceValues to set.
	 */
	public void setReferenceValues(String referenceValues)
	{
		this.referenceValues = referenceValues;
	}

	/**
	 * @return Returns the displayChoice.
	 */
	public String getDisplayChoice()
	{
		return displayChoice;
	}

	/**
	 * @param displayChoice The displayChoice to set.
	 */
	public void setDisplayChoice(String displayChoice)
	{
		this.displayChoice = displayChoice;
	}

	/**
	 * @return the attributeCssClass
	 */
	public String getCssClass()
	{
		return cssClass;
	}

	/**
	 * @param cssClass the cssClass to set
	 */
	public void setCssClass(String cssClass)
	{
		this.cssClass = cssClass;
	}

	/**
	 * @return the attributeMeasurementUnits
	 */
	public String getAttributeMeasurementUnits()
	{
		return attributeMeasurementUnits;
	}

	/**
	 * @param attributeMeasurementUnits the attributeMeasurementUnits to set
	 */
	public void setAttributeMeasurementUnits(String attributeMeasurementUnits)
	{
		this.attributeMeasurementUnits = attributeMeasurementUnits;
	}

	/**
	 * @return the attributeMultiSelect
	 */
	public String getAttributeMultiSelect()
	{
		return attributeMultiSelect;
	}

	/**
	 * @param attributeMultiSelect the attributeMultiSelect to set
	 */
	public void setAttributeMultiSelect(String attributeMultiSelect)
	{
		this.attributeMultiSelect = attributeMultiSelect;
		if(attributeMultiSelect!=null)
		{
			isMultiSelect = new Boolean(attributeMultiSelect.equals(ProcessorConstants.LIST_TYPE_MULTI_SELECT));
		}
	}

	/**
	 * @return the attributenoOfCols
	 */
	public String getAttributenoOfCols()
	{
		return attributenoOfCols;
	}

	/**
	 * @param attributenoOfCols the attributenoOfCols to set
	 */
	public void setAttributenoOfCols(String attributenoOfCols)
	{
		this.attributenoOfCols = attributenoOfCols;
	}

	/**
	 * @return the attributeNoOfRows
	 */
	public String getAttributeNoOfRows()
	{
		return attributeNoOfRows;
	}

	/**
	 * @param attributeNoOfRows the attributeNoOfRows to set
	 */
	public void setAttributeNoOfRows(String attributeNoOfRows)
	{
		this.attributeNoOfRows = attributeNoOfRows;
	}

	/**
	 * @return the attributeScale
	 */
	public String getAttributeScale()
	{
		return attributeScale;
	}

	/**
	 * @param attributeScale the attributeScale to set
	 */
	public void setAttributeScale(String attributeScale)
	{
		this.attributeScale = attributeScale;
	}

	/**
	 * @return the toolsList
	 */
	public List getToolsList()
	{
		return toolsList;
	}

	/**
	 * @param toolsList the toolsList to set
	 */
	public void setToolsList(List toolsList)
	{
		this.toolsList = toolsList;
	}

	/**
	 * @return the userSelectedTool
	 */
	public String getUserSelectedTool()
	{
		return userSelectedTool;
	}

	/**
	 * @param userSelectedTool the userSelectedTool to set
	 */
	public void setUserSelectedTool(String userSelectedTool)
	{
		this.userSelectedTool = userSelectedTool;
	}

	/**
	 * @return the caption
	 */
	public String getCaption()
	{
		return caption;
	}

	/**
	 * @param caption the caption to set
	 */
	public void setCaption(String caption)
	{
		this.caption = caption;
	}

	/**
	 * 
	 * @return Boolean isPassword
	 */
	public Boolean getIsPassword()
	{
		return this.isPassword;
	}

	/**
	 * 
	 * @param isPassword isPassword
	 */
	public void setIsPassword(Boolean isPassword)
	{
		this.isPassword = isPassword;
		if (isPassword != null)
		{
			this.attributeIsPassword = isPassword.toString();
		}
	}

	/**
	 * @return the tooltip
	 */
	public String getTooltip()
	{
		return tooltip;
	}

	/**
	 * @param tooltip the tooltip to set
	 */
	public void setTooltip(String tooltip)
	{
		this.tooltip = tooltip;
	}

	/**
	 * 
	 * @return AbstractAttributeInterface abstractAttributeInterface
	 */
	public AbstractAttributeInterface getAbstractAttribute()
	{
		return abstractAttribute;
	}

	/**
	 * 
	 * @param abstractAttributeInterface abstractAttributeInterface
	 */
	public void setAbstractAttribute(AbstractAttributeInterface abstractAttributeInterface)
	{
		abstractAttribute = abstractAttributeInterface;
	}

	/**
	 * 
	 * @return Integer columns
	 */

	public Integer getColumns()
	{
		if((attributenoOfCols!=null)&&(attributenoOfCols.trim().equals("")))
		{
			columns = new Integer(0);	//blank values will be considered as 0
		}
		else
		{
			columns = new Integer(attributenoOfCols);
		}
		return columns;
	}

	/**
	 * 
	 * @return Boolean isHidden
	 */
	public Boolean getIsHidden()
	{
		return isHidden;
	}

	/**
	 * 
	 * @return name name 
	 */
	public String getName()
	{
		return name;
	}

	/**
	 * 
	 * @return Integer  rows
	 */
	public Integer getRows()
	{
		if((attributeNoOfRows!=null)&&(attributeNoOfRows.trim().equals("")))
		{
			rows = new Integer(0);	//blank values will be considered as 0
		}
		else
		{
			rows = new Integer(attributeNoOfRows);
		}

		return rows;
	}

	/**
	 * 
	 * @return Integer sequenceNumber
	 */
	public Integer getSequenceNumber()
	{
		if((attributeSequenceNumber!=null)&&(attributeSequenceNumber.trim().equals("")))
		{
			sequenceNumber = new Integer(0);	//blank values will be considered as 0
		}
		else
		{
			sequenceNumber = new Integer(attributeSequenceNumber);
		}
		return sequenceNumber;
	}

	/**
	 * 
	 * @param columns Integer columns
	 */
	public void setColumns(Integer columns)
	{
		this.columns = columns;
		if (columns != null)
		{
			this.attributenoOfCols = columns.toString();
		}

	}

	/**
	 * 
	 * @param isHidden isHidden
	 */
	public void setIsHidden(Boolean isHidden)
	{
		this.isHidden = isHidden;
	}

	/**
	 * 
	 * @param name name to be set
	 */
	public void setName(String name)
	{
		this.name = name;
	}

	/**
	 * 
	 * @param rows Integer rows
	 */
	public void setRows(Integer rows)
	{
		this.rows = rows;
		if (rows != null)
		{
			this.attributeNoOfRows = rows.toString();
		}
	}

	/**
	 * 
	 * @param sequenceNumber Integer sequenceNumber
	 */
	public void setSequenceNumber(Integer sequenceNumber)
	{
		this.sequenceNumber = sequenceNumber;
	}

	/**
	 * @return Returns the isMultiSelect.
	 */
	public Boolean getIsMultiSelect()
	{
		return isMultiSelect;
	}

	/**
	 * @param isMultiSelect The isMultiSelect to set.
	 */
	public void setIsMultiSelect(Boolean isMultiSelect)
	{
		this.isMultiSelect = isMultiSelect;
		if ((isMultiSelect != null)&&(isMultiSelect==true))
		{
			this.attributeMultiSelect = ProcessorConstants.LIST_TYPE_MULTI_SELECT;
		}
	}

	/** 
	 * (non-Javadoc)
	 * @see edu.common.dynamicextensions.ui.interfaces.AbstractAttributeUIBeanInterface#getAttributeDigits()
	 */
	public String getAttributeDigits()
	{
		return attributeDigits;
	}

	/**
	 * (non-Javadoc)
	 * @see edu.common.dynamicextensions.ui.interfaces.AbstractAttributeUIBeanInterface#setAttributeDigits(java.lang.String)
	 */
	public void setAttributeDigits(String attribDigits)
	{
		attributeDigits = attribDigits;
	}

	/**
	 * 
	 * @return attributeSequenceNumber attributeSequenceNumber
	 */
	public String getAttributeSequenceNumber()
	{
		return this.attributeSequenceNumber;
	}

	/**
	 *  
	 * @param attributeSequenceNumber attributeSequenceNumber
	 */
	public void setAttributeSequenceNumber(String attributeSequenceNumber)
	{
		this.attributeSequenceNumber = attributeSequenceNumber;
	}

	/**
	 * 
	 * @return String attributeIsPassword
	 */
	public String getAttributeIsPassword()
	{
		return this.attributeIsPassword;
	}

	/**
	 * 
	 * @param attributeIsPassword attributeIsPassword
	 */
	public void setAttributeIsPassword(String attributeIsPassword)
	{
		this.attributeIsPassword = attributeIsPassword;
		isPassword = new Boolean(attributeIsPassword);
	}

	/**
	 * @return Returns the controlOperation.
	 */
	public String getControlOperation()
	{
		return controlOperation;
	}

	/**
	 * @param controlOperation The controlOperation to set.
	 */
	public void setControlOperation(String controlOperation)
	{
		this.controlOperation = controlOperation;
	}

	/**
	 * @return Returns the selectedControlId.
	 */
	public String getSelectedControlId()
	{
		return selectedControlId;
	}

	/**
	 * @param selectedControlId The selectedControlId to set.
	 */
	public void setSelectedControlId(String selectedControlId)
	{
		this.selectedControlId = selectedControlId;
	}

	/**
	 * @return Returns the childList.
	 */
	public List getChildList()
	{
		return childList;
	}

	/**
	 * @param childList The childList to set.
	 */
	public void setChildList(List childList)
	{
		this.childList = childList;
	}

	/**
	 * @return Returns the rootName.
	 */
	public String getRootName()
	{
		return rootName;
	}

	/**
	 * @param rootName The rootName to set.
	 */
	public void setRootName(String rootName)
	{
		this.rootName = rootName;
	}

	
	/**
	 * @return Returns the showPreview.
	 *//*
	public String getShowPreview()
	{
		return showPreview;
	}

	*//**
	 * @param showPreview The showPreview to set.
	 *//*
	public void setShowPreview(String showPreview)
	{
		this.showPreview = showPreview;
	}*/

	/**
	 * @return the linesType
	 */
	public String getLinesType()
	{
		return linesType;
	}

	/**
	 * @param linesType the linesType to set
	 */
	public void setLinesType(String linesType)
	{
		this.linesType = linesType;
	}

	/**
	 * @return attributeConceptCode
	 */
	public String getAttributeConceptCode()
	{
		return this.attributeConceptCode;
	}

	/**
	 * @param attributeConceptCode attributeConceptCode
	 */
	public void setAttributeConceptCode(String attributeConceptCode)
	{
		this.attributeConceptCode = attributeConceptCode;
	}

	/**
	 * @return String dateValueType
	 */
	public String getDateValueType()
	{
		return this.dateValueType;
	}

	/**
	 * @param dateValueType String dateValueType
	 */
	public void setDateValueType(String dateValueType)
	{
		this.dateValueType = dateValueType;
	}

	/**
	 * @return the validationRules
	 */
	public String[] getValidationRules()
	{
		return validationRules;
	}

	/**
	 * @param validationRules the validationRules to set
	 */
	public void setValidationRules(String[] validationRules)
	{
		this.validationRules = validationRules;
	}

	/**
	 * @return the controlRuleMap
	 */
	public Map getControlRuleMap()
	{
		return controlRuleMap;
	}

	/**
	 * @param controlRuleMap the controlRuleMap to set
	 */
	public void setControlRuleMap(Map controlRuleMap)
	{
		this.controlRuleMap = controlRuleMap;
	}

	/**
	 * @param dataTypeName String dataTypeName
	 * @return List ListOfRules
	 */
	public List getListOfRules(String dataTypeName)
	{
		return (List) controlRuleMap.get(dataTypeName);
	}

	/**
	 * @return the max
	 */
	public String getMax()
	{
		return max;
	}

	/**
	 * @param max the max to set
	 */
	public void setMax(String max)
	{
		this.max = max;
	}

	/**
	 * @return the min
	 */
	public String getMin()
	{
		return min;
	}

	/**
	 * @param min the min to set
	 */
	public void setMin(String min)
	{
		this.min = min;
	}

	/**
	 * 
	 * @return String measurementUnitOther
	 */
	public String getMeasurementUnitOther()
	{
		return this.measurementUnitOther;
	}

	/**
	 * 
	 * @param measurementUnitOther measurementUnitOther
	 */
	public void setMeasurementUnitOther(String measurementUnitOther)
	{
		this.measurementUnitOther = measurementUnitOther;
	}

	/**
	 * Overrides the validate method of ActionForm.
	 * @param mapping ActionMapping mapping
	 * @param request HttpServletRequest request
	 * @return ActionErrors ActionErrors
	 */
	public ActionErrors validate(ActionMapping mapping, HttpServletRequest request)
	{
		ActionErrors errors = new ActionErrors();
		Validator validator = new Validator();

		if (caption == null || validator.isEmpty(String.valueOf(caption)))
		{
			errors.add(ActionErrors.GLOBAL_ERROR, new ActionError("errors.item.required", ApplicationProperties.getValue("eav.att.Label")));
		}
		validateControlFields(validator,errors);
		return errors;
	}

	/**
	 * @param validator :validator
	 * @param errors : action errors 
	 * 
	 */
	private void validateControlFields(Validator validator, ActionErrors errors)
	{
		if(userSelectedTool!=null)
		{
			//Special case for text Control
			if (userSelectedTool.equalsIgnoreCase((ProcessorConstants.TEXT_CONTROL)))
			{
				getErrorsForTextControl(validator, errors);
			}else if (userSelectedTool.equalsIgnoreCase((ProcessorConstants.COMBOBOX_CONTROL)))
			{
				//Special case for combobox Control
				getErrorsForComboboxControl(validator, errors);
			}else if (userSelectedTool.equalsIgnoreCase((ProcessorConstants.DATEPICKER_CONTROL)))
			{
				//Special case for Date picker Control
				getErrorsForDatePickerControl(validator, errors);
			}else if (userSelectedTool.equalsIgnoreCase((ProcessorConstants.FILEUPLOAD_CONTROL)))
			{
				//Special case for File upload Control
				getErrorsForFileUploadControl(validator, errors);
			}

		} 
	}

	/**
	 * @param validator :validator
	 * @param errors : action errors
	 */
	private void getErrorsForFileUploadControl(Validator validator, ActionErrors errors)
	{
	}

	/**
	 * @param validator :validator
	 * @param errors : action errors
	 */
	private void getErrorsForDatePickerControl(Validator validator, ActionErrors errors)
	{
		//check for date format of default value fld
		//if dateValueType is "Select" then default value cannot be blank and shld be valid date
		if(dateValueType!=null)
		{
			if(dateValueType.trim().equalsIgnoreCase(ProcessorConstants.DATE_VALUE_SELECT))
			{
				if((attributeDefaultValue==null)||(validator.checkDate(attributeDefaultValue)==false))
				{
					errors.add(ActionErrors.GLOBAL_ERROR, new ActionError("errors.date.format", ApplicationProperties.getValue("eav.att.DefaultValue")));
				}
			}
		}
		else
		{
			errors.add(ActionErrors.GLOBAL_ERROR, new ActionError("errors.item.required", ApplicationProperties.getValue("eav.att.DefaultValue")));
		}
	}



	/**
	 * @param validator :validator
	 * @param errors : action errors
	 */
	private void getErrorsForComboboxControl(Validator validator, ActionErrors errors)
	{
		if (attributeMultiSelect == null || validator.isEmpty(String.valueOf(attributeMultiSelect)))
		{
			errors.add(ActionErrors.GLOBAL_ERROR, new ActionError("errors.item.required", ApplicationProperties.getValue("eav.att.ListBoxType")));
		}
		if (dataType == null || validator.isEmpty(String.valueOf(dataType)))
		{
			errors.add(ActionErrors.GLOBAL_ERROR, new ActionError("errors.item.required", ApplicationProperties.getValue("eav.att.DataInput")));
		}
		//NUMBER OF ROWS SHLD BE NUMERIC
		if((attributeMultiSelect!=null)&&(attributeMultiSelect.equals(ProcessorConstants.LIST_TYPE_MULTI_SELECT)))
		{
			if(!isNumeric(attributeNoOfRows,validator))
			{
				errors.add(ActionErrors.GLOBAL_ERROR, new ActionError("errors.item.numericField", ApplicationProperties.getValue("eav.att.ListBoxDisplayLines")));
			}
		}
	}

	/**
	 * @param validator :validator
	 * @param errors : action errors
	 */
	private void getErrorsForTextControl(Validator validator, ActionErrors errors)
	{
		//REQUIRED FIELDS VALIDATION
		checkRequiredFieldsForTextControl(validator,errors);

		//NUMERIC FIELDS VALIDATION

		//1. Check for text field width
		if(!isNumeric(attributenoOfCols,validator))
		{
			errors.add(ActionErrors.GLOBAL_ERROR, new ActionError("errors.item.numericField", ApplicationProperties.getValue("eav.att.TextFieldWidth")));
		}

		//check errors if datatype is String
		if((dataType!=null)&&(dataType.equals(ProcessorConstants.DATATYPE_STRING)))
		{
			getErrorsForStringDatatype(validator,errors);
		}

		//Check errors if datatype is number
		if((dataType!=null)&&(dataType.equals(ProcessorConstants.DATATYPE_NUMBER)))
		{
			getErrorsForNumericDatatype(validator,errors);
		}


	}

	/** @param validator
	 * @param errors
	 */
	private void getErrorsForNumericDatatype(Validator validator, ActionErrors errors)
	{
		/*
		 *	For number datatype validate:
		 *	1) Precision : numeric
		 *	2) Default value is numeric
		 */
		if(!isNumeric(attributeDecimalPlaces,validator))
		{
			errors.add(ActionErrors.GLOBAL_ERROR, new ActionError("errors.item.numericField", ApplicationProperties.getValue("eav.att.AttributeDecimalPlaces")));
		}

		//Numeric default value
		if(!(isNumeric(attributeDefaultValue,validator)||(validator.isDouble(attributeDefaultValue))))
		{
			errors.add(ActionErrors.GLOBAL_ERROR, new ActionError("errors.item.numericField", ApplicationProperties.getValue("eav.att.DefaultValue")));
		}
	}

	/**
	 * @param validator
	 * @param errors
	 */
	private void getErrorsForStringDatatype(Validator validator, ActionErrors errors)
	{
		/*
		 *	For string datatype validate:
		 *  1) Lines type : Singleline/Multiline specified  : REQUIRED 
		 *	2) Max number of chars(size)  : Numeric
		 *	3) If linesType is multiline the Number of rows()  : Numeric
		 */

		//Atleast one of singleline/ multiline should be selected		
		if (linesType == null || validator.isEmpty(String.valueOf(linesType)))
		{
			errors.add(ActionErrors.GLOBAL_ERROR, new ActionError("errors.item.required", ApplicationProperties.getValue("eav.control.type")));
		}

		//Size : maximum characters shld be numeric
		if(!isNumeric(attributeSize,validator))
		{
			errors.add(ActionErrors.GLOBAL_ERROR, new ActionError("errors.item.numericField", ApplicationProperties.getValue("eav.att.MaxCharacters")));
		}

		//Number of lines for multiline textbox shld be numeric
		if((linesType!=null)&&(linesType.equals(ProcessorConstants.LINE_TYPE_MULTILINE)))
		{
			if(!isNumeric(attributeNoOfRows,validator))
			{
				errors.add(ActionErrors.GLOBAL_ERROR, new ActionError("errors.item.numericField", ApplicationProperties.getValue("eav.text.noOfLines")));
			}
		}
	}

	/**
	 * 
	 */
	private void checkRequiredFieldsForTextControl(Validator validator, ActionErrors errors)
	{
		//Datatype either numeric or string shld be selected
		if (dataType == null || validator.isEmpty(String.valueOf(dataType)))
		{
			errors.add(ActionErrors.GLOBAL_ERROR, new ActionError("errors.item.required", ApplicationProperties.getValue("eav.att.DataInput")));
		}

	}

	/**
	 * Test if a string represents a numeric fld.
	 * Will return false if fld is null or is not a numeric fld. Blank fld will be considered as valid
	 * @param stringFld
	 * @return
	 */
	private boolean isNumeric(String stringFld,Validator validator)
	{
		if(stringFld!=null)
		{
			if(stringFld.trim().equals(""))
			{
				return true;	//Blank fields are considered valid. Assume thier value as 0
			}
			else
			{
				return (validator.isNumeric(stringFld));
			}
		}
		return false;
	}
	/**
	 * @return the tempValidationRules
	 */
	public String[] getTempValidationRules()
	{
		return tempValidationRules;
	}

	/**
	 * @param tempValidationRules the tempValidationRules to set
	 */
	public void setTempValidationRules(String[] tempValidationRules)
	{
		this.tempValidationRules = tempValidationRules;
	}

	public List getFileFormatsList()
	{
		return this.fileFormatsList;
	}

	public void setFileFormatsList(List fileFormatsList)
	{
		this.fileFormatsList = fileFormatsList;
	}

	public String getAttributeMandatory()
	{
		return this.attributeMandatory;
	}

	public void setAttributeMandatory(String attributeMandatory)
	{
		this.attributeMandatory = attributeMandatory;
	}

	public String getAttributeDisplayAsURL()
	{
		return this.attributeDisplayAsURL;
	}

	public void setAttributeDisplayAsURL(String attributeDisplayAsURL)
	{
		this.attributeDisplayAsURL = attributeDisplayAsURL;
	}


}