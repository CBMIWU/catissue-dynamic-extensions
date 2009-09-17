
package edu.common.dynamicextensions.domain.userinterface;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import edu.common.dynamicextensions.domain.CategoryAttribute;
import edu.common.dynamicextensions.domaininterface.AbstractAttributeInterface;
import edu.common.dynamicextensions.domaininterface.AttributeInterface;
import edu.common.dynamicextensions.domaininterface.AttributeMetadataInterface;
import edu.common.dynamicextensions.domaininterface.AttributeTypeInformationInterface;
import edu.common.dynamicextensions.domaininterface.CategoryAttributeInterface;
import edu.common.dynamicextensions.domaininterface.DateTypeInformationInterface;
import edu.common.dynamicextensions.domaininterface.userinterface.DatePickerInterface;
import edu.common.dynamicextensions.exception.DynamicExtensionsSystemException;
import edu.common.dynamicextensions.processor.ProcessorConstants;
import edu.common.dynamicextensions.ui.util.ControlsUtility;
import edu.common.dynamicextensions.util.DynamicExtensionsUtility;
import edu.wustl.common.util.Utility;
import edu.wustl.common.util.global.CommonServiceLocator;

/**
 * @version 1.0
 * @created 28-Sep-2006 12:20:07 PM
 * @hibernate.joined-subclass table="DYEXTN_DATEPICKER"
 * @hibernate.joined-subclass-key column="IDENTIFIER"
 */
public class DatePicker extends Control implements DatePickerInterface
{

	/**
	 * default serial UID
	 */
	private static final long serialVersionUID = 1L;
	private String dateValueType = null;
	private String dateFormatString = "Date format : ";

	/**
	 * This method generates the HTML code for DatePicker control on the HTML
	 * form
	 * 
	 * @return HTML code for DatePicker
	 * @throws DynamicExtensionsSystemException
	 *             if couldn't generate the HTML name for the Control.
	 */
	protected String generateEditModeHTML(Integer rowId) throws DynamicExtensionsSystemException
	{
		AttributeTypeInformationInterface attributeTypeInformation = ((AttributeMetadataInterface) this
				.getBaseAbstractAttribute()).getAttributeTypeInformation();
		String dateFormat = ControlsUtility.getDateFormat(attributeTypeInformation);

		String defaultValue = (String) this.value;
		if (value == null)
		{
			defaultValue = this.getAttibuteMetadataInterface().getDefaultValue();
			if (defaultValue != null && this.getAttibuteMetadataInterface() != null
					&& this.getAttibuteMetadataInterface() instanceof CategoryAttribute)
			{
				CategoryAttributeInterface categoryAttribute = (CategoryAttributeInterface) this
						.getAttibuteMetadataInterface();
				DateTypeInformationInterface dateTypeInformation = (DateTypeInformationInterface) ((AttributeInterface) categoryAttribute
						.getAbstractAttribute()).getAttributeTypeInformation();
				defaultValue = reverseDate(defaultValue);
				Date date = null;
				try
				{
					if (dateFormat.equals(ProcessorConstants.DATE_TIME_FORMAT))
					{
						SimpleDateFormat format = new SimpleDateFormat(
								ProcessorConstants.DATE_TIME_FORMAT, CommonServiceLocator
										.getInstance().getDefaultLocale());
						date = format.parse(defaultValue);
					}
					else
					{
						SimpleDateFormat format = new SimpleDateFormat(
								ProcessorConstants.SQL_DATE_ONLY_FORMAT, CommonServiceLocator
										.getInstance().getDefaultLocale());
						date = format.parse(defaultValue);
					}
				}
				catch (ParseException e)
				{
					throw new DynamicExtensionsSystemException("Error while parsing date", e);
				}
				defaultValue = new SimpleDateFormat(ControlsUtility
						.getDateFormat(dateTypeInformation), CommonServiceLocator.getInstance()
						.getDefaultLocale()).format(date);
			}
			if (defaultValue == null)
			{
				if (this.getDateValueType() != null
						&& this.getDateValueType().equals(ProcessorConstants.DATE_VALUE_TODAY))
				{
					defaultValue = Utility.parseDateToString(new Date(), dateFormat);
				}
				else
				{
					defaultValue = "";
				}
			}
		}

		String htmlComponentName = getHTMLComponentName();
		String output = "";
		if (getIsSkipLogicTargetControl())
		{
			output += "<input type='hidden' name='skipLogicControl' id='skipLogicControl' value = '"
					+ getHTMLComponentName() + "_div' /><div id='"
					+ getHTMLComponentName() + "_div' name='"
					+ getHTMLComponentName() + "_div'>";
		}
		if (dateFormat.equals(ProcessorConstants.DATE_ONLY_FORMAT))
		{
			output += "<input class='font_bl_nor' name='"
					+ htmlComponentName
					+ "' id='"
					+ htmlComponentName
					+ "' title='Calendar [MM-DD-YYYY]' value='"
					+ ((defaultValue != null && defaultValue.trim().length()>0) ? defaultValue : "MM-DD-YYYY")
					+ "'"
					+ (((this.isReadOnly != null && this.isReadOnly) || (this.isSkipLogicReadOnly != null && this.isSkipLogicReadOnly)) ? " disabled='"
							+ ProcessorConstants.TRUE : "")
					+ " onchange='isDataChanged();' onfocus=\"javascript:clearDate('"+htmlComponentName+"','MM-DD-YYYY');\""
					+ " style=\"color:"
					+ ((defaultValue != null && defaultValue.trim().length()>0) ? "black;\"" : "#A9A9A9;\"")
					+ "/>"
					+ "<A onclick=\"showCalendar('"
					+ htmlComponentName
					+ "', "
					+ DynamicExtensionsUtility.getCurrentYear()
					+ ", "
					+ DynamicExtensionsUtility.getCurrentMonth()
					+ ", "
					+ DynamicExtensionsUtility.getCurrentDay()
					+ ", 'MM-dd-yyyy', 'dataEntryForm', '"
					+ htmlComponentName
					+ "', event, 1900, 2020);\" href=\"javascript://\">"
					+ "&nbsp;<IMG alt=\"" + dateFormatString + " [MM-DD-YYYY]\" src=\"de/images/calendar.gif\" border=0 /></A>"
					+ "<DIV id=slcalcod"
					+ htmlComponentName
					+ " style=\"Z-INDEX: 10; LEFT: 100px; VISIBILITY: hidden; POSITION: absolute; TOP: 100px\">";
			output += "<SCRIPT>printCalendar('" + htmlComponentName + "',"
					+ DynamicExtensionsUtility.getCurrentDay() + ","
					+ DynamicExtensionsUtility.getCurrentMonth() + ","
					+ DynamicExtensionsUtility.getCurrentYear() + ");</SCRIPT>" + "</DIV>";
		}
		else if (dateFormat.equals(ProcessorConstants.DATE_TIME_FORMAT))
		{
			output += "<input class='font_bl_nor' name='"
					+ htmlComponentName
					+ "' id='"
					+ htmlComponentName
					+ "' title='Calendar [MM-DD-YYYY HH:MM]' value='"
					+ ((defaultValue != null && defaultValue.trim().length()>0) ? defaultValue : "MM-DD-YYYY HH:MM")
					+ "'"
					+ (((this.isReadOnly != null && this.isReadOnly) || (this.isSkipLogicReadOnly != null && this.isSkipLogicReadOnly)) ? " disabled='"
							+ ProcessorConstants.TRUE : "")
					+ " onchange='isDataChanged();' onfocus=\"javascript:clearDate('"+htmlComponentName+"','MM-DD-YYYY HH:MM');\""
					+ " style=\"color:"
					+ ((defaultValue != null && defaultValue.trim().length()>0) ? "black;\"" : "#A9A9A9;\"")
					+ "/>"
					+ "<A onclick=\"showCalendar('"
					+ htmlComponentName
					+ "', "
					+ DynamicExtensionsUtility.getCurrentYear()
					+ ", "
					+ DynamicExtensionsUtility.getCurrentMonth()
					+ ", "
					+ DynamicExtensionsUtility.getCurrentDay()
					+ ", 'MM-dd-yyyy', 'dataEntryForm', '"
					+ htmlComponentName
					+ "', event, 1900, 2020);\" href=\"javascript://\">"
					+ "&nbsp;<IMG alt=\"" + dateFormatString + " [MM-DD-YYYY HH:MM]\" src=\"de/images/calendar.gif\" border=0 />&nbsp;</A>"
					+ "<DIV id=slcalcod"
					+ htmlComponentName
					+ " style=\"Z-INDEX: 10; LEFT: 100px; VISIBILITY: hidden; POSITION: absolute; TOP: 100px\">"
					+ "<SCRIPT>printTimeCalendar('" + htmlComponentName + "',"
					+ DynamicExtensionsUtility.getCurrentDay() + ","
					+ DynamicExtensionsUtility.getCurrentMonth() + ","
					+ DynamicExtensionsUtility.getCurrentYear() + ","
					+ DynamicExtensionsUtility.getCurrentHours() + ","
					+ DynamicExtensionsUtility.getCurrentMinutes() + ");</SCRIPT>" + "</DIV>";
		}
		else if (dateFormat.equals(ProcessorConstants.MONTH_YEAR_FORMAT))
		{
			output += "<input class='font_bl_nor' name='"
					+ htmlComponentName
					+ "' id='"
					+ htmlComponentName
					+ "' title='Calender [MM-YYYY]' value='"
					+ ((defaultValue != null && defaultValue.trim().length()>0) ? defaultValue : "MM-YYYY")
					+ "'"
					+ (((this.isReadOnly != null && this.isReadOnly) || (this.isSkipLogicReadOnly != null && this.isSkipLogicReadOnly)) ? " disabled='"
							+ ProcessorConstants.TRUE : "")
					+ " onchange='isDataChanged();' onfocus=\"javascript:clearDate('"+htmlComponentName+"','MM-YYYY');\""
					+ " style=\"color:"
					+ ((defaultValue != null && defaultValue.trim().length()>0) ? "black;\"" : "#A9A9A9;\"")
					+ "/>"
					+ "<A onclick=\"showCalendar('"
					+ htmlComponentName
					+ "', "
					+ DynamicExtensionsUtility.getCurrentYear()
					+ ", "
					+ DynamicExtensionsUtility.getCurrentMonth()
					+ ", "
					+ 0
					+ ", 'MM-yyyy', 'dataEntryForm', '"
					+ htmlComponentName
					+ "', event, 1900, 2020);\" href=\"javascript://\">&nbsp;<IMG alt=\"" + dateFormatString + "[MM-YYYY]\" src=\"de/images/calendar.gif\" border=0 />&nbsp;</A>"
					+ "<DIV id=slcalcod"
					+ htmlComponentName
					+ " style=\"Z-INDEX: 10; LEFT: 100px; VISIBILITY: hidden; POSITION: absolute; TOP: 100px\">"
					+ "<SCRIPT>printMonthYearCalendar('" + htmlComponentName + "',"
					+ DynamicExtensionsUtility.getCurrentMonth() + ","
					+ DynamicExtensionsUtility.getCurrentYear() + ");</SCRIPT>" + "</DIV>";
		}
		else if (dateFormat.equals(ProcessorConstants.YEAR_ONLY_FORMAT))
		{

			output += "<input class='font_bl_nor' name='"
					+ htmlComponentName
					+ "' id='"
					+ htmlComponentName
					+ "' title='[YYYY]' value='"
					+ ((defaultValue != null && defaultValue.trim().length()>0) ? defaultValue : "YYYY")
					+ "'"
					+ (((this.isReadOnly != null && this.isReadOnly) || (this.isSkipLogicReadOnly != null && this.isSkipLogicReadOnly)) ? " disabled='"
							+ ProcessorConstants.TRUE : "")
					+ " onchange='isDataChanged();' onfocus=\"javascript:clearDate('"+htmlComponentName+"','YYYY');\""
					+ " style=\"color:"
					+ ((defaultValue != null && defaultValue.trim().length()>0) ? "black;\"" : "#A9A9A9;\"")
					+ "/>"
					+ "<A onclick=\"showCalendar('"
					+ htmlComponentName
					+ "', "
					+ DynamicExtensionsUtility.getCurrentYear()
					+ ", "
					+ 0
					+ ", "
					+ 0
					+ ", 'yyyy', 'dataEntryForm', '"
					+ htmlComponentName
					+ "', event, 1900, 2020);\" href=\"javascript://\">&nbsp;<IMG alt=\"" + dateFormatString + " [YYYY]\" src=\"de/images/calendar.gif\" border=0 />&nbsp;</A>"
					+ "<DIV id=slcalcod"
					+ htmlComponentName
					+ " style=\"Z-INDEX: 10; LEFT: 100px; VISIBILITY: hidden; POSITION: absolute; TOP: 100px\">";
			output += "<SCRIPT>printYearCalendar('" + htmlComponentName + "',"
					+ DynamicExtensionsUtility.getCurrentYear() + ");</SCRIPT>" + "</DIV>";
		}
		if (getIsSkipLogicTargetControl())
		{
			output += "</div>";
		}
		return output;
	}

	/**
	 * @param defaultValue
	 * @return defaultValue
	 */
	private String reverseDate(String defaultValue)
	{
		// Date is like 1900-01-01 00:00:00.0 for MySQL5
		String date = defaultValue.substring(0, 10); // 1900-01-01
		String time = defaultValue.substring(10, defaultValue.length()); // 00:00:00.0

		String year = date.substring(0, 4); // 1900
		String month = date.substring(5, 7); // 01
		String day = date.substring(8, date.length()); // 01

		date = month + "-" + day + "-" + year;
		return (date + time);
	}

	/**
	 * @param attributeInterface
	 *            attribute type object
	 */
	public void setAttribute(AbstractAttributeInterface attributeInterface)
	{
		// TODO empty method.
	}

	/**
	 * This method returns the dateValueType of the DatePicker.
	 * 
	 * @hibernate.property name="dateValueType" type="string"
	 *                     column="DATE_VALUE_TYPE"
	 * @return Returns the dateValueType.
	 */
	public String getDateValueType()
	{
		return dateValueType;
	}

	/**
	 * @param dateValueType
	 *            set the date type value
	 */
	public void setDateValueType(String dateValueType)
	{
		this.dateValueType = dateValueType;
	}

	/**
	 * Generate HTML for viewing data
	 */
	protected String generateViewModeHTML(Integer rowId) throws DynamicExtensionsSystemException
	{
		String htmlString = "";
		if (value != null)
		{
			htmlString = "<span class = 'font_bl_nor'> " + this.value.toString() + "</span>";
		}

		return htmlString;
	}

	/**
	 * 
	 */
	public List<String> getValueAsStrings() 
	{
		return null;
	}

	/**
	 * 
	 */
	public void setValueAsStrings(List<String> listOfValues) 
	{
		// TODO Auto-generated method stub
		
	}

}