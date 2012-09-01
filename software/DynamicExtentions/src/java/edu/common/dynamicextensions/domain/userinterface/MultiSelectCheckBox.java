
package edu.common.dynamicextensions.domain.userinterface;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import edu.common.dynamicextensions.domaininterface.AbstractAttributeInterface;
import edu.common.dynamicextensions.domaininterface.AssociationInterface;
import edu.common.dynamicextensions.domaininterface.AttributeInterface;
import edu.common.dynamicextensions.domaininterface.AttributeMetadataInterface;
import edu.common.dynamicextensions.domaininterface.CategoryAttributeInterface;
import edu.common.dynamicextensions.domaininterface.userinterface.ContainerInterface;
import edu.common.dynamicextensions.domaininterface.userinterface.MultiSelectCheckBoxInterface;
import edu.common.dynamicextensions.exception.DynamicExtensionsApplicationException;
import edu.common.dynamicextensions.exception.DynamicExtensionsSystemException;
import edu.common.dynamicextensions.processor.ProcessorConstants;
import edu.common.dynamicextensions.ui.util.Constants;
import edu.common.dynamicextensions.ui.util.ControlsUtility;
import edu.common.dynamicextensions.util.DynamicExtensionsUtility;
import edu.wustl.common.beans.NameValueBean;

/**
 * This class is used to create new control of multiselectCheckBox type
 * @author suhas_khot
 * @version 1.3
 * @created 09-Oct-2009 12:22:00 PM
 * @hibernate.joined-subclass table="DYEXTN_MULTISELECT_CHECK_BOX"
 * @hibernate.joined-subclass-key column="IDENTIFIER"
 */
public class MultiSelectCheckBox extends SelectControl implements MultiSelectCheckBoxInterface
{

	/**
	 * Serial Version Unique Identifier
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * check if checkBox is of multiSelect type or not
	 */
	private Boolean isMultiSelect = false;

	/**
	 * @return the listOfValues
	 */
	public List getListOfValues()
	{
		return listOfValues;
	}

	/**
	 * @param listOfValues the listOfValues to set
	 */
	public void setListOfValues(List listOfValues)
	{
		this.listOfValues = listOfValues;
	}

	/**
	 * This method returns whether the multiSelectCheckBox has a multiselect property or not.
	 * @hibernate.property name="isMultiSelect" type="boolean" column="MULTISELECT"
	 * @return whether the multiSelectCheckBox has a multiselect property or not.
	 */
	public Boolean getIsMultiSelect()
	{
		return isMultiSelect;
	}

	/**
	 * @param isMultiSelect the isMultiSelect to set
	 */
	public void setIsMultiSelect(Boolean isMultiSelect)
	{
		this.isMultiSelect = isMultiSelect;
	}

	/**
	 * List of values for multiSelectCheckBox.
	 */
	private List listOfValues = null;

	/**
	 * This method generates the HTML code to display the MultiselectCheckBox Control on the form.
	 * @return HTML code for MultiselectCheckBox Control.
	 * @throws DynamicExtensionsSystemException
	 * @throws DynamicExtensionsApplicationException 
	 */
	@Override
    protected String generateEditModeHTML(ContainerInterface container)
			throws DynamicExtensionsSystemException, DynamicExtensionsApplicationException
	{
		String htmlString = "";
		List<NameValueBean> nameValueBeans = null;
		List<String> values = getValueAsStrings();
		String disabled = "";
		//		If control is defined as readonly through category CSV file,make it Disabled
		if ((isReadOnly != null && getIsReadOnly())
				|| (isSkipLogicReadOnly != null && isSkipLogicReadOnly))
		{
			disabled = ProcessorConstants.DISABLED;
		}
		String htmlComponentName = getHTMLComponentName();
		String parentContainerId = "";
		if (getParentContainer() != null && getParentContainer().getId() != null)
		{
			parentContainerId = getParentContainer().getId().toString();
		}
		String identifier = "";
		if (getId() != null)
		{
			identifier = getId().toString();
		}
		if (getIsSkipLogicTargetControl())
		{
			htmlString += "<div id='" + getHTMLComponentName() + "_div' name='"
					+ getHTMLComponentName() + "_div'>";
		}
		if (listOfValues == null)
		{
			List<String> sourceControlValues = null;
			if (getSourceSkipControl() != null)
			{
				sourceControlValues = getSourceSkipControl().getValueAsStrings();
			}
			nameValueBeans = ControlsUtility.populateListOfValues(this, sourceControlValues,
					(Date) container.getContextParameter(Constants.ENCOUNTER_DATE));
		}

		if (nameValueBeans != null && !nameValueBeans.isEmpty())
		{
			int COLUMN_COUNT = getColumnCount();
			htmlString += "<table cellspacing='7'>";
			int columnNum = 0;
			for (NameValueBean nameValueBean : nameValueBeans)
			{
				if(columnNum % COLUMN_COUNT == 0)
				{
					columnNum = 0;
					htmlString += "<tr>";
				}
				if (values != null && !values.isEmpty()
						&& values.contains(nameValueBean.getValue()))
				{
					htmlString += "<td><input type='checkbox' class="
							+ getCSS()
							+ " name='"
							+ htmlComponentName
							+ "' checkedValue='"
							+ DynamicExtensionsUtility.getValueForCheckBox(true)
							+ "' uncheckedValue='"
							+ DynamicExtensionsUtility.getValueForCheckBox(false)
							+ "'"
							+ " value='"
							+ nameValueBean.getValue()
							+ "' "
							+ "id='"
							+ nameValueBean.getValue()
							+ "'"
							+ " checked"
							+ disabled
							+ " onchange=\""
							+ "\" ondblclick=\"changeValueForAllCheckBoxes(this);"
							+ (isSkipLogic ? "getSkipLogicControl('" + htmlComponentName
									+ "','" + identifier + "','" + parentContainerId + "');" : "")
							+ "\" onclick=\"changeValueForMultiSelectCheckBox(this);"
							+ getOnchangeServerCall()
							+ (isSkipLogic ? "getSkipLogicControl('" + htmlComponentName + "','"
									+ identifier + "','" + parentContainerId + "');" : "")
							+ "\" /></td><td>"
							+ "<label for=\""
							+ htmlComponentName
							+ "\">"
							+ DynamicExtensionsUtility.getUnEscapedStringValue(nameValueBean
									.getName())
							+ "</label> </td>";
				}
				else
				{
					htmlString += "<td><input type='checkbox' class="
							+ getCSS()
							+ " name='"
							+ htmlComponentName
							+ "' checkedValue='"
							+ DynamicExtensionsUtility.getValueForCheckBox(true)
							+ "' uncheckedValue='"
							+ DynamicExtensionsUtility.getValueForCheckBox(false)
							+ "'"
							+ " value='"
							+ DynamicExtensionsUtility.getValueForCheckBox(false)
							+ "' "
							+ disabled
							+ "id='"
							+ nameValueBean.getValue()
							+ "'"
							+ " onchange=\""
							+ "\" ondblclick=\"changeValueForAllCheckBoxes(this);"
							+ (isSkipLogic ? "getSkipLogicControl('" + htmlComponentName
									+ "','" + identifier + "','" + parentContainerId + "');" : "")
							+ "\" onclick=\"changeValueForMultiSelectCheckBox(this);"
							+ getOnchangeServerCall()
							+ (isSkipLogic ? "getSkipLogicControl('" + htmlComponentName + "','"
									+ identifier + "','" + parentContainerId + "');" : "")
							+ "\" /></td><td>"
							+ "<label for=\"" + htmlComponentName + "\">" + nameValueBean.getName()
							+ "</label> </td>";
				}
				if (columnNum % COLUMN_COUNT == COLUMN_COUNT - 1)
				{
					htmlString += "</tr>";
				}
				columnNum++;

			}
			if (columnNum % COLUMN_COUNT < COLUMN_COUNT - 1)
			{
				htmlString += "</tr>";
			}

		}
		htmlString += "</table>";
		if (getIsSkipLogicTargetControl())
		{
			htmlString += "<input type='hidden' name='skipLogicControl' id='skipLogicControl' value = '"
					+ getHTMLComponentName() + "_div' />";
			htmlString += "</div>";
		}
		return htmlString;
	}

	/**
	 * This method generates the HTML code in view mode to display the MultiselectCheckBox Control on the form.
	 * @return HTML code for MultiselectCheckBox Control.
	 * @throws DynamicExtensionsSystemException
	 */
	@Override
    protected String generateViewModeHTML(ContainerInterface container)
			throws DynamicExtensionsSystemException
	{
		List<String> selectedOptions = new ArrayList<String>();

		AssociationInterface association = getBaseAbstractAttributeAssociation();
		if (association == null)
		{
			if ((value instanceof List) || value == null)
			{
				selectedOptions = (List<String>) value;
			}
			else
			{
				List<String> options = new ArrayList<String>();
				options.add((String) value);
				selectedOptions = options;
			}
		}
		else
		{
			getValueList(association, selectedOptions);
		}

		StringBuffer htmlString = new StringBuffer();
		if (value != null)
		{
			htmlString.append("<span class = 'font_bl_s'>");
			for (String options : selectedOptions)
			{
				htmlString.append(options);
				htmlString.append("<br>");
			}
			htmlString.append("</span>");
		}
		return htmlString.toString();
	}

	/**
	 * setValues for a control
	 */
	@Override
    public void setValueAsStrings(List<String> listOfValues)
	{
		// TODO Auto-generated method stub

	}

	/**
	 * get all permissible values for this control
	 * @param rowId
	 * @return List of  all permissible values for this control
	 * @throws DynamicExtensionsSystemException 
	 * @throws DynamicExtensionsApplicationException 
	 */
	@Override
    public List<String> getValueAsStrings() throws DynamicExtensionsSystemException, DynamicExtensionsApplicationException
	{
		List<String> values = new ArrayList<String>();
		Date encounterDate = (Date) getParentContainer().getContextParameter(
				Constants.ENCOUNTER_DATE);
		AssociationInterface association = getBaseAbstractAttributeAssociation();
		if (association == null)
		{
			if ((value instanceof List) || value == null)
			{
				values = (List<String>) value;
			}
			else
			{
				List<String> temp = new ArrayList<String>();
				temp.add((String) value);
				values = temp;
			}
		}
		else
		{
			getValueList(association, values);
		}
		if (getIsSkipLogicDefaultValue())
		{
			if (values != null && values.isEmpty())
			{
				values.add(getSkipLogicDefaultValue());
			}
		}
		else
		{
			if (values == null || values.isEmpty())
			{
				String defaultValue = null;
				values = new ArrayList<String>();
				AttributeMetadataInterface attrMetadataInterface = getAttibuteMetadataInterface();
				if (attrMetadataInterface != null)
				{
					if (attrMetadataInterface instanceof CategoryAttributeInterface)
					{
						AbstractAttributeInterface abstractAttributeInterface = ((CategoryAttributeInterface) attrMetadataInterface)
								.getAbstractAttribute();
						if (abstractAttributeInterface instanceof AttributeInterface)
						{
							defaultValue = attrMetadataInterface.getDefaultValue(null);
						}
					}
					else
					{

						defaultValue = attrMetadataInterface.getDefaultValue(encounterDate);
					}
					if (defaultValue != null && !"".equals(defaultValue.trim()))
					{
						values.add(defaultValue);
					}
				}
			}
		}
		List<NameValueBean> nameValueBeans = ControlsUtility.getListOfPermissibleValues(
				getAttibuteMetadataInterface(), encounterDate);
		boolean isInavlidVaue = true;
		for(String value:values)
		{
			for (NameValueBean bean : nameValueBeans)
			{
				if (bean.getValue().equals(value))
				{
					isInavlidVaue = false;
					break;
				}
			}
			if(isInavlidVaue)
			{
				StringBuilder errorMessage=new StringBuilder();
				errorMessage.append('\'');
				errorMessage.append(value);
				errorMessage.append("' is not a valid value for '");
				errorMessage.append(this.getName());
				errorMessage.append("' anymore. Please select a new value.");
				errorList.add(errorMessage.toString());
			}
		}
		return values;
	}

	/**
	 *
	 * @return
	 */
	public AssociationInterface getBaseAbstractAttributeAssociation()
	{
		AssociationInterface associationInterface = null;
		if (baseAbstractAttribute instanceof AssociationInterface)
		{
			associationInterface = (AssociationInterface) baseAbstractAttribute;
		}
		else if (baseAbstractAttribute instanceof CategoryAttributeInterface)
		{
			CategoryAttributeInterface categoryAttr = (CategoryAttributeInterface) baseAbstractAttribute;
			AbstractAttributeInterface abstractAttr = categoryAttr.getAbstractAttribute();
			if (abstractAttr instanceof AssociationInterface)
			{
				associationInterface = (AssociationInterface) abstractAttr;
			}
		}
		return associationInterface;
	}

	/**
	 *
	 */
	@Override
    public boolean getIsEnumeratedControl()
	{
		return true;
	}
}
