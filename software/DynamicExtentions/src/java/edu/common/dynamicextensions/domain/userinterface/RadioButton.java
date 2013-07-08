
package edu.common.dynamicextensions.domain.userinterface;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import edu.common.dynamicextensions.category.beans.UIProperty;
import edu.common.dynamicextensions.category.enums.RadioButtonEnum;
import edu.common.dynamicextensions.domaininterface.AbstractAttributeInterface;
import edu.common.dynamicextensions.domaininterface.CategoryAttributeInterface;
import edu.common.dynamicextensions.domaininterface.CategoryEntityInterface;
import edu.common.dynamicextensions.domaininterface.DoubleTypeInformationInterface;
import edu.common.dynamicextensions.domaininterface.FloatTypeInformationInterface;
import edu.common.dynamicextensions.domaininterface.LongTypeInformationInterface;
import edu.common.dynamicextensions.domaininterface.userinterface.ContainerInterface;
import edu.common.dynamicextensions.domaininterface.userinterface.RadioButtonInterface;
import edu.common.dynamicextensions.exception.DynamicExtensionsApplicationException;
import edu.common.dynamicextensions.exception.DynamicExtensionsSystemException;
import edu.common.dynamicextensions.ui.util.Constants;
import edu.common.dynamicextensions.ui.util.ControlsUtility;
import edu.common.dynamicextensions.util.DynamicExtensionsUtility;
import edu.wustl.common.beans.NameValueBean;

/**
 * @version 1.0
 * @created 28-Sep-2006 12:20:08 PM
 * @hibernate.joined-subclass table="DYEXTN_RADIOBUTTON"
 * @hibernate.joined-subclass-key column="IDENTIFIER"
 */
public class RadioButton extends SelectControl implements RadioButtonInterface
{

	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = 1L;

	/**
	 * This method generates the HTML code for RadioButton control on the HTML form
	 * @return HTML code for RadioButton
	 * @throws DynamicExtensionsSystemException
	 * @throws DynamicExtensionsApplicationException 
	 */
	@Override
    public String generateEditModeHTML(final ContainerInterface container)
			throws DynamicExtensionsSystemException, DynamicExtensionsApplicationException
	{
		List<NameValueBean> nameValueBeanList = null;
		String htmlString = "";
		if (getIsSkipLogicTargetControl())
		{
			htmlString += "<div id='" + getHTMLComponentName() + "_div' name='"
					+ getHTMLComponentName() + "_div'>";
		}
		final String defaultValue = getDefaultValueForControl();
		String disabled = "";
		//If control is defined as readonly through category CSV file,make it Disabled
		if ((isReadOnly != null && getIsReadOnly())
				|| (isSkipLogicReadOnly != null && isSkipLogicReadOnly))
		{
			disabled = " disabled='true' ";
		}
		List<String> sourceControlValues = null;
		if (getSourceSkipControl() != null)
		{
			sourceControlValues = getSourceSkipControl().getValueAsStrings();
		}
		nameValueBeanList = ControlsUtility.populateListOfValues(this, sourceControlValues,
				(Date) container.getContextParameter(Constants.ENCOUNTER_DATE));
		int COLUMN_COUNT = getColumnCount();
		final String htmlComponentName = getHTMLComponentName();
		htmlString += "<table cellspacing='3'>";
		if (nameValueBeanList != null && !nameValueBeanList.isEmpty())
		{
			int columnNum = 0;
			
			for (NameValueBean nameValueBean : nameValueBeanList)
			{
				if(columnNum % COLUMN_COUNT == 0)
				{
					columnNum = 0;
					htmlString += "<tr>";
				}
				final String optionName = DynamicExtensionsUtility.getUnEscapedStringValue(nameValueBean
						.getName());
				final String optionValue = nameValueBean.getValue();
				if (optionValue.equals(defaultValue))
				{
					htmlString += "<td><input type='radio' onClick=\""
							+ getOnchangeServerCall()
							+ "\""
							+ "class="
							+getCSS()
							+ "name='"
							+ htmlComponentName
							+ (id != null
									|| ((CategoryEntityInterface) getParentContainer()
											.getAbstractEntity()).getParentCategoryEntity() == null
									? ""
									: (((CategoryEntityInterface) getParentContainer()
											.getAbstractEntity()).getName())) + "' " + "value='"
							+ optionValue + "' " + "id='" + optionName + "' checked "
							+ disabled + "  " + "/></td>";
				}
				else
				{
					htmlString +="<td><input type='radio' onClick=\""
							+ getOnchangeServerCall()
							+ "\" class="
							+getCSS()
							+ "name='"
							+ htmlComponentName
							+ (id != null
									|| ((CategoryEntityInterface) getParentContainer()
											.getAbstractEntity()).getParentCategoryEntity() == null
									? ""
									: (((CategoryEntityInterface) getParentContainer()
											.getAbstractEntity()).getName())) + "' " + "value='"
							+ optionValue + "' " + "id='" + optionName + "' " + disabled

							+ " /></td>";
				}
				htmlString +="<td class='formRequiredLabel_withoutBorder'>  <label for=\"" + htmlComponentName + "\" onClick=\"selectRadioButton('"+getHTMLComponentName() +"','"+optionValue+"')\">"
				+ optionName+
						"</label></td>";
				if(columnNum % COLUMN_COUNT == COLUMN_COUNT - 1)
				{
					htmlString += "</tr>";
				}
				columnNum++;

			}
			if(columnNum % COLUMN_COUNT < COLUMN_COUNT - 1)
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
	 * Gets the default value for control.
	 * @return the default value for control
	 * @throws DynamicExtensionsSystemException 
	 * @throws DynamicExtensionsApplicationException 
	 */
	private String getDefaultValueForControl() throws DynamicExtensionsSystemException, DynamicExtensionsApplicationException
	{
		String defaultValue;
		final Date encounterDate = (Date) getParentContainer().getContextParameter(
				Constants.ENCOUNTER_DATE);
		if (value == null || value.toString().length() == 0)
		{
			if (isSkipLogicDefaultValue())
			{
				defaultValue = getDefaultSkipLogicValue();
			}
			else
			{

				defaultValue = getAttibuteMetadataInterface().getDefaultValue(encounterDate);
				if (defaultValue == null || defaultValue.length() == 0)
				{
					defaultValue = "";
				}
			}
		}
		else
		{
			defaultValue = value.toString();
			List<NameValueBean> nameValueBeans = ControlsUtility.getListOfPermissibleValues(
					getAttibuteMetadataInterface(), encounterDate);
			boolean isInavlidVaue = true;
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
				defaultValue = getAttibuteMetadataInterface().getDefaultValue(encounterDate);
				if (defaultValue == null || defaultValue.length() == 0)
				{
					defaultValue = "";
				}
				if(!"".equals(defaultValue))
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
		}
		return convertValueToAppropriateDataType(defaultValue);
	}

	/**
	 * Convert value to appropriate data type.
	 * @param defaultValue the default value
	 * @return the string
	 */
	private String convertValueToAppropriateDataType(String defaultValue)
	{
		if (defaultValue != null)
		{
			if (defaultValue.length() > 0
					&& getAttibuteMetadataInterface().getAttributeTypeInformation() instanceof DoubleTypeInformationInterface)
			{
				double doubleValue = Double.parseDouble(defaultValue);
				defaultValue = Double.toString(doubleValue);
			}
			else if (defaultValue.length() > 0
					&& getAttibuteMetadataInterface().getAttributeTypeInformation() instanceof LongTypeInformationInterface)
			{
				long longValue = Long.parseLong(defaultValue);
				defaultValue = Long.toString(longValue);

			}
			else if (defaultValue.length() > 0
					&& getAttibuteMetadataInterface().getAttributeTypeInformation() instanceof FloatTypeInformationInterface)
			{
				float floatValue = Float.parseFloat(defaultValue);
				defaultValue = Float.toString(floatValue);

			}
		}
		return defaultValue;
	}


	/**
	 * Gets the default skip logic value.
	 * @return the default skip logic value
	 */
	private String getDefaultSkipLogicValue()
	{
		CategoryAttributeInterface categoryAttribute = (CategoryAttributeInterface) getAttibuteMetadataInterface();
		return categoryAttribute.getDefaultSkipLogicValue().getValueAsObject().toString();
	}

	/**
	 * Checks if is skip logic default value.
	 * @return true, if is skip logic default value
	 */
	private boolean isSkipLogicDefaultValue()
	{
		return ((CategoryAttributeInterface) getAttibuteMetadataInterface())
				.getDefaultSkipLogicValue() != null;
	}

	/**
	 * This method sets the corresponding Attribute of this control.
	 * @param abstractAttribute the AbstractAttribute to be set.
	 */
	public void setAttribute(final AbstractAttributeInterface abstractAttribute)
	{
		// TODO Auto-generated constructor stub
	}

	@Override
    public String generateViewModeHTML(final ContainerInterface container)
			throws DynamicExtensionsSystemException
	{
		StringBuffer htmlStringBuffer=new StringBuffer();
		htmlStringBuffer.append("&nbsp;");
		if (value != null)
		{
			htmlStringBuffer.append("<span class = '" );
			htmlStringBuffer.append(cssClass);
			htmlStringBuffer.append("'> " );
			htmlStringBuffer.append(value.toString());
			htmlStringBuffer.append("</span>");
		}
		return htmlStringBuffer.toString();
	}

	/**
	 * @throws DynamicExtensionsSystemException 
	 * @throws DynamicExtensionsApplicationException 
	 *
	 */
	@Override
    public List<String> getValueAsStrings() throws DynamicExtensionsSystemException, DynamicExtensionsApplicationException
	{
		List<String> values = new ArrayList<String>();
		values.add(getDefaultValueForControl());
		return values;
	}

	/**
	 *
	 */
	@Override
    public void setValueAsStrings(final List<String> listOfValues)
	{
		if (!listOfValues.isEmpty())
		{
			setValue(listOfValues.get(0));
		}
	}

	/**
	 *
	 */
	@Override
    public boolean getIsEnumeratedControl()
	{
		return true;
	}

	/**
	 * Returns collection of key-value pairs.
	 */
	@Override
    public Collection<UIProperty> getControlTypeValues()
	{
		Collection<UIProperty> controlTypeValues = super.getControlTypeValues();
		RadioButtonEnum[] uiPropertyValues = RadioButtonEnum.values();

		for (RadioButtonEnum propertyType : uiPropertyValues)
		{
			String controlProperty = propertyType.getControlProperty(this, null);
			if (controlProperty != null)
			{
				controlTypeValues.add(new UIProperty(propertyType.getValue(), controlProperty));
			}
		}
		return controlTypeValues;
	}

	/**
	 * Set collection of key-value pairs for a control.
	 */
	@Override
    public void setControlTypeValues(final Collection<UIProperty> uiProperties)
	{
		super.setControlTypeValues(uiProperties);
		for (UIProperty uiProperty : uiProperties)
		{
			RadioButtonEnum propertyType = RadioButtonEnum.getValue(uiProperty.getKey());
			propertyType.setControlProperty(this, uiProperty.getValue(), null);
		}
	}
	
	public RadioButton()
	{
		super();
	}
	public RadioButton(RadioButton control)
	{
		super(control);
	}
}