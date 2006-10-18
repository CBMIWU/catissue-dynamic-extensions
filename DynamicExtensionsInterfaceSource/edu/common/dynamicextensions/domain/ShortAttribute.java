
package edu.common.dynamicextensions.domain;

import edu.common.dynamicextensions.domaininterface.ShortAttributeInterface;
import edu.wustl.common.actionForm.AbstractActionForm;
import edu.wustl.common.exception.AssignDataException;

/**
 * @hibernate.joined-subclass table="DYEXTN_SHORT_ATTRIBUTE" 
 * @hibernate.joined-subclass-key column="IDENTIFIER"  
 * @author sujay_narkar
 *
 */
public class ShortAttribute extends Attribute implements ShortAttributeInterface{ 
     /**
        * Default value for this attribute.
        */
       protected Short defaultValue;
       /**
        * 
        */
       protected String measurementUnits;
       
       /**
        * Number of digits
        */
       protected String digits;
       /**
        * Number of decimal places
        */
       protected String decimalPlaces="0";
       protected String size;

       
       public String getSize()
	{
		return this.size;
	}

	public void setSize(String size)
	{
		this.size = size;
	}

	public String getDecimalPlaces()
	{
		return this.decimalPlaces;
	}

	public void setDecimalPlaces(String decimalPlaces)
	{
		this.decimalPlaces = decimalPlaces;
	}

	public String getDigits()
	{
		return this.digits;
	}

	public void setDigits(String digits)
	{
		this.digits = digits;
	}

	/**
        * Empty Constructor.
        */
       public ShortAttribute () {
           
       }
       
       /**
        * 
        */
       public void setAllValues(AbstractActionForm arg0) throws AssignDataException {
       }
       /**
        * @hibernate.property name="defaultValue" type="short" column="DEFAULT_VALUE"
        * @return Returns the defaultValue.
        */
       public Short getDefaultValue() {
           return defaultValue;
       }
       /**
        * @param defaultValue The defaultValue to set.
        */
       public void setDefaultValue(Short defaultValue) {
           this.defaultValue = defaultValue;
       }
       /**
        * @hibernate.property name="measurementUnits" type="string" column="MEASUREMENT_UNITS"  
        * @return Returns the measurementUnits.
        */
       public String getMeasurementUnits() {
           return measurementUnits;
       }
       /**
        * @param measurementUnits The measurementUnits to set.
        */
       public void setMeasurementUnits(String measurementUnits) {
           this.measurementUnits = measurementUnits;
       }
    }




