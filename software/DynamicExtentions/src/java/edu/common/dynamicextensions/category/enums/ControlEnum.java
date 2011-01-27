
package edu.common.dynamicextensions.category.enums;

import java.util.Date;

import edu.common.dynamicextensions.domain.CategoryAttribute;
import edu.common.dynamicextensions.domain.DateAttributeTypeInformation;
import edu.common.dynamicextensions.domain.userinterface.Control;
import edu.common.dynamicextensions.domaininterface.AttributeInterface;
import edu.common.dynamicextensions.domaininterface.AttributeTypeInformationInterface;
import edu.common.dynamicextensions.domaininterface.BaseAbstractAttributeInterface;
import edu.common.dynamicextensions.domaininterface.PermissibleValueInterface;
import edu.common.dynamicextensions.domaininterface.userinterface.ControlInterface;
import edu.common.dynamicextensions.ui.util.Constants;
import edu.common.dynamicextensions.ui.util.ControlsUtility;
import edu.common.dynamicextensions.util.parser.CategoryCSVConstants;

/**
 * This Enum sets all UIProperties that are common for all type of controls.
 * @author rajesh_vyas
 *
 */
public enum ControlEnum {
	DEFAULTVALUE("defaultValue") {

		/**
		 * Returns String representation of Default value for a control.
		 * @param control
		 * @return String
		 */
		public String getControlProperty(Control control)
		{

			String defaultValue = null;
			BaseAbstractAttributeInterface baseAbstractAttribute = control
					.getBaseAbstractAttribute();
			if (baseAbstractAttribute instanceof CategoryAttribute)
			{
				defaultValue = defaultValueForControl(control, defaultValue);
			}
			return defaultValue;
		}

		/**
		 * @param control
		 * @param defaultValue
		 * @return
		 */
		private String defaultValueForControl(Control control, String defaultValue)
		{
			CategoryAttribute categoryAttribute = (CategoryAttribute) control
					.getBaseAbstractAttribute();
			if (control.getIsEnumeratedControl())
			{
				Date encounterDate = (Date) control.getParentContainer().getContextParameter(
						Constants.ENCOUNTER_DATE);
				defaultValue = categoryAttribute.getDefaultValue(encounterDate);
			}
			else
			{
				AttributeInterface attribute = categoryAttribute.getAttribute();
				AttributeTypeInformationInterface attributeTypeInformation = attribute
						.getAttributeTypeInformation();
				PermissibleValueInterface defaultValue2 = attributeTypeInformation
						.getDefaultValue();
				defaultValue = getValueStringFromPV(control, defaultValue,
						attributeTypeInformation, defaultValue2);
			}
			return defaultValue;
		}

		/**
		 * @param control
		 * @param defaultValue
		 * @param attributeTypeInformation
		 * @param defaultValue2
		 * @return
		 */
		private String getValueStringFromPV(ControlInterface control, String defaultValue,
				AttributeTypeInformationInterface attributeTypeInformation,
				PermissibleValueInterface defaultValue2)
		{
			if (defaultValue2 != null && defaultValue2.getValueAsObject() != null
					&& defaultValue2.getValueAsObject().toString().trim().length() != 0)
			{
				Object valueAsObject = defaultValue2.getValueAsObject();
				defaultValue = valueAsObject.toString();
				if (attributeTypeInformation instanceof DateAttributeTypeInformation)
				{
					defaultValue = getDateControlDefaultValue(control).replace('-', '/');
				}
			}
			return defaultValue;
		}

		/**
		 * @return
		 */
		private String getDateControlDefaultValue(ControlInterface control)
		{
			String defaultValue = control.getAttibuteMetadataInterface().getDefaultValue(null);
			if (defaultValue != null && defaultValue.length() > 0
					&& control.getAttibuteMetadataInterface() instanceof CategoryAttribute
					&& defaultValue.indexOf('-') == 4)
			{
				String date = defaultValue.substring(0, 10); // 1900-01-01
				String time = defaultValue.substring(10, defaultValue.length()); // 00:00:00.0
				String year = date.substring(0, 4); // 1900
				String month = date.substring(5, 7); // 01
				String day = date.substring(8, date.length()); // 01
				date = month + "-" + day + "-" + year;
				defaultValue = date + time;
			}
			return defaultValue;
		}

		/**
		 * Sets Control default value.
		 * @param control
		 * @param propertyToBeSet
		 */
		public void setControlProperty(Control control, String propertyToBeSet)
		{
			CategoryAttribute categoryAttribute = (CategoryAttribute) control
					.getBaseAbstractAttribute();
			AttributeInterface attribute = categoryAttribute.getAttribute();
			attribute.getAttributeTypeInformation();
		}
	},
	REQUIRED("required") {

		/**
		 * Returns string representation for Required rule of Control.
		 * @param control
		 * @return String
		 */
		public String getControlProperty(Control control)
		{
			String ruleName = null;
			boolean result = ControlsUtility.isRuleDefined(CategoryCSVConstants.REQUIRED, control);

			if (result)
			{
				ruleName = "yes";
			}
			return ruleName;
		}

		/**
		 * Sets required Rule.
		 * @param control
		 * @param propertyToBeSet
		 */
		public void setControlProperty(Control control, String propertyToBeSet)
		{
			if (propertyToBeSet.equalsIgnoreCase("yes"))
			{
				ControlsUtility.defineRule(CategoryCSVConstants.REQUIRED, control);
			}
		}
	};

	private String name;

	ControlEnum(String name)
	{
		this.name = name;
	}

	/** Abstract method for all enums to set control property */
	public abstract void setControlProperty(Control control, String propertyToBeSet);

	/** Abstract method for all enums to get control property */
	public abstract String getControlProperty(Control control);

	/**
	 * Returns name of Enum.
	 * @return
	 */
	public String getValue()
	{
		return name;
	}

	/**
	 * Returns Enum for a given String.
	 * @param nameToBeFound
	 * @return
	 */
	public static ControlEnum getValue(String nameToBeFound)
	{
		ControlEnum[] propertyTypes = ControlEnum.values();
		ControlEnum matchedEnum = null;
		for (ControlEnum propertyType : propertyTypes)
		{
			if (propertyType.getValue().equalsIgnoreCase(nameToBeFound))
			{
				matchedEnum = propertyType;
			}
		}
		return matchedEnum;
	}
}
