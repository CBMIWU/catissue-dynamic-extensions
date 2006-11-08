
package edu.common.dynamicextensions.domain;

import edu.common.dynamicextensions.domaininterface.DoubleAttributeInterface;
import edu.wustl.common.actionForm.AbstractActionForm;
import edu.wustl.common.exception.AssignDataException;

/**
 * @hibernate.joined-subclass table="DYEXTN_DOUBLE_ATTRIBUTE" 
 * @hibernate.joined-subclass-key column="IDENTIFIER"  
 * @author sujay_narkar
 *
 */
public class DoubleAttribute extends Attribute implements DoubleAttributeInterface
{

	/**
	 * Serial Version Unique Identifier
	 */
	private static final long serialVersionUID = -748391527722552390L;
	
	/**
	 * Default value for this attribute.
	 */
	protected Double defaultValue;
	
	/**
	 * Measurement units of the Attribute.
	 */
	protected String measurementUnits;

	/**
	 * Number of digits
	 */
	protected String digits;
	
	/**
	 * The places after the decimal point.
	 */
	protected String decimalPlaces;
	
	/**
	 * The size of the Number.
	 */	
	//protected String size;
	
	/**
	 * Empty Constructor.
	 */
	public DoubleAttribute()
	{
	}

	/**
	 * Set all values from the form
	 * @param abstractActionForm the ActionForm
	 * @throws AssignDataException if data is not in proper format.
	 */
	public void setAllValues(AbstractActionForm abstractActionForm) throws AssignDataException
	{
	}

	/**
	 * This method returns the default value of this Attribute.
	 * @hibernate.property name="defaultValue" type="double" column="DEFAULT_VALUE"
	 * @return the default value of this Attribute.
	 */
	public Double getDefaultValue()
	{
		return defaultValue;
	}

	/**
	 * This method sets the default value of DoubleAttribute to given Double value.
	 * @param defaultValue the value to be set as default.
	 */
	public void setDefaultValue(Double defaultValue)
	{
		this.defaultValue = defaultValue;
	}

	/**
	 * This method returns the measurement units of this Attribute.
	 * @hibernate.property name="measurementUnits" type="string" column="MEASUREMENT_UNITS"  
	 * @return the measurement unints of this Attribute.
	 */
	public String getMeasurementUnits()
	{
		return measurementUnits;
	}

	/**
	 * This method sets the measurement units of this Attribute.
	 * @param measurementUnits the measurement units to be set.
	 */
	public void setMeasurementUnits(String measurementUnits)
	{
		this.measurementUnits = measurementUnits;
	}

	/**
	 * This method returns the places after the decimal point.
	 * @return the places after the decimal point.
	 */
	public String getDecimalPlaces()
	{
		return this.decimalPlaces;
	}

	/**
	 * This method sets the places after the decimal point of the DoubleAttribue.
	 * @param decimalPlaces the places after the decimal point to be set.
	 */
	public void setDecimalPlaces(String decimalPlaces)
	{
		this.decimalPlaces = decimalPlaces;
	}

	/**
	 * This method returns the length of the number in digits.
	 * @return the length of the number in digits. 
	 */
	public String getDigits()
	{
		return this.digits;
	}

	/**
	 * This method sets the length of the number in digits.
	 * @param digits the length of the number in digits.
	 */
	public void setDigits(String digits)
	{
		this.digits = digits;
	}

//	public String getSize()
//	{
//		return this.size;
//	}
//
//	public void setSize(String size)
//	{
//		this.size = size;
//	}
	
}
