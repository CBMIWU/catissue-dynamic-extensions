
package edu.common.dynamicextensions.domain.userinterface;

import java.util.List;

import edu.common.dynamicextensions.domaininterface.AbstractAttributeInterface;
import edu.common.dynamicextensions.domaininterface.AttributeMetadataInterface;
import edu.common.dynamicextensions.domaininterface.userinterface.TextFieldInterface;
import edu.common.dynamicextensions.exception.DynamicExtensionsSystemException;
import edu.common.dynamicextensions.processor.ProcessorConstants;
import edu.common.dynamicextensions.ui.util.Constants;

/**
 * This Class represents the TextField (TextBox) of the HTML page.
 * @version 1.0
 * @created 28-Sep-2006 12:20:09 PM
 * @hibernate.joined-subclass table="DYEXTN_TEXTFIELD"
 * @hibernate.joined-subclass-key column="IDENTIFIER"
 */
public class TextField extends Control implements TextFieldInterface
{

	/**
	 *
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * Size of the text field to be shown on UI.
	 */
	protected Integer columns;

	/**
	 * Boolean value indicating whether this text field is password field.
	 */
	protected Boolean isPassword;

	/**
	 * Boolean value indicating whether this text field is password field.
	 */
	protected Boolean isUrl;

	/**
	 * @hibernate.property name="columns" type="integer" column="NO_OF_COLUMNS"
	 * @return Returns the columns.
	 */
	public Integer getColumns()
	{
		return columns;
	}

	/**
	 * @param columns The columns to set.
	 */
	public void setColumns(Integer columns)
	{
		this.columns = columns;
	}

	/**
	 * @hibernate.property name="isPassword" type="boolean" column="IS_PASSWORD"
	 * @return Returns the isPassword.
	 */
	public Boolean getIsPassword()
	{
		return isPassword;
	}

	/**
	 * @param isPassword The isPassword to set.
	 */
	public void setIsPassword(Boolean isPassword)
	{
		this.isPassword = isPassword;
	}

	/**
	 * This method generates the HTML code for TextField control on the HTML form
	 * @return HTML code for TextField
	 * @throws DynamicExtensionsSystemException
	 */
	public String generateEditModeHTML() throws DynamicExtensionsSystemException
	{
		String defaultValue = getDefaultValue();

		String htmlComponentName = getHTMLComponentName();
		String htmlString = "";
		htmlString += "<input type='hidden' name='skipLogicControl' id='skipLogicControl' value = '"
					+ getHTMLComponentName() + "_div' /><div id='"
					+ getHTMLComponentName() + "_div' name='"
					+ getHTMLComponentName() + "_div'>";
		if (isUrl != null && isUrl)
		{
			htmlString += "<a href='javascript:void(0)' onclick=\"window.open('"
					+ defaultValue
					+ "','','width=800,height=600,toolbar=yes,location=yes,directories=yes,status=yes,menubar=yes,scrollbars=yes,copyhistory=yes,resizable=yes')\">"
					+ defaultValue + "</a>";
		}
		else
		{
			htmlString += "<INPUT " + "class='font_bl_nor' " + "name='" + htmlComponentName + "' "
					+ "id='" + htmlComponentName + "' onchange='isDataChanged();' value='" + defaultValue + "' ";

			int columnSize = columns.intValue() - 2;
			if (columnSize > 0)
			{
				htmlString += "size='" + columnSize + "' ";
			}
			else
			{
				htmlString += "size='" + Constants.DEFAULT_COLUMN_SIZE + "' ";
			}

			if (isPassword != null && isPassword.booleanValue())
			{
				htmlString += " type='password' ";
			}
			else
			{
				htmlString += " type='text' ";
			}

			//set isdisabled property

			if ((this.isReadOnly != null && this.isReadOnly) || (this.isSkipLogicReadOnly != null && this.isSkipLogicReadOnly))
			{
				htmlString += " readonly='" + ProcessorConstants.TRUE + "' ";
			}

			int maxChars = 0;
			AttributeMetadataInterface attibute = this.getAttibuteMetadataInterface();
			if (attibute != null)
			{
				maxChars = attibute.getMaxSize();
			}
			//Changed by: Kunal
			//Incase of input type is chosen as number 
			//the max char size is -1
			if (maxChars > 0)
			{
				htmlString += " onblur='textCounter(this," + maxChars + ")'  ";
			}

			htmlString += "/>";
			if (this.isCalculated != null && this.isCalculated)
			{
				htmlString += "<img src=\"de/images/b_calculate.gif\" alt=\"Calculate\" width=\"62\" height=\"21\" hspace=\"3\" align=\"absmiddle\" onClick=\"calculateAttributes();\" >";
				htmlString += "<map alt=\"Calculate\">";
				htmlString += "<area href=\"javascript:calculateAttributes()\" shape=\"default\">";
				htmlString += "</map>";
			}
			//String measurementUnit = getMeasurementUnit(this.getAbstractAttribute());
			String measurementUnit = this.getAttibuteMetadataInterface().getMeasurementUnit();
			if (measurementUnit != null)
			{
				if (measurementUnit.equalsIgnoreCase("none"))
				{
					measurementUnit = "";
				}
				htmlString += " " + measurementUnit;
			}
		}
		htmlString += "</div>";
		return htmlString;
	}

	/**
	 * This method sets the associated AbstractAttribute of this Control.
	 * @param abstractAttributeInterface AbstractAttribute to be associated.
	 */
	public void setAttribute(AbstractAttributeInterface abstractAttributeInterface)
	{
		// TODO Auto-generated constructor stub
	}

	/**
	 * This method returns the Boolean value that decides whether the value of this control should be treated as normal text or URL.
	 * @hibernate.property name="isUrl" type="boolean" column="IS_URL" 
	 * @return the Boolean value	true - value is URL
	 * 								false - value is normal text.
	 */
	public Boolean getIsUrl()
	{
		return isUrl;
	}

	/* (non-Javadoc)
	 * @see edu.common.dynamicextensions.domaininterface.userinterface.TextFieldInterface#setIsUrl(java.lang.Boolean)
	 */
	public void setIsUrl(Boolean isUrl)
	{
		this.isUrl = isUrl;
	}

	/* (non-Javadoc)
	 * @see edu.common.dynamicextensions.domain.userinterface.Control#generateViewModeHTML()
	 */
	protected String generateViewModeHTML() throws DynamicExtensionsSystemException
	{
		String defaultValue = getDefaultValue();
		String htmlString = "&nbsp;";
		if (defaultValue != null)
		{
			if (isUrl != null && (isUrl.booleanValue()))
			{
				htmlString = "<a href='javascript:void(0)' onclick=\"window.open('"
						+ defaultValue
						+ "','','width=800,height=600,toolbar=yes,location=yes,directories=yes,status=yes,menubar=yes,scrollbars=yes,copyhistory=yes,resizable=yes')\">"
						+ defaultValue + "</a>";
			}
			else
			{
				htmlString = "<span class = 'font_bl_nor'> " + defaultValue + "</span>";
			}
		}
		return htmlString;
	}

	private String getDefaultValue()
	{
		String defaultValue = "";
		if (!this.isSkipLogicReadOnly)
		{
			if (this.value == null)
			{
				defaultValue = "";
			}
			else
			{
				defaultValue = String.valueOf(this.value);
			}
	
			if (isUrl != null && (isUrl.booleanValue()))
			{
				defaultValue = this.getAttibuteMetadataInterface().getDefaultValue();
			}
			else
			{
				if (this.value == null)
				{
					defaultValue = this.getAttibuteMetadataInterface().getDefaultValue();
					if (defaultValue == null || (defaultValue.length() == 0))
					{
						defaultValue = "";
					}
				}
			}
		}
		return defaultValue;
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