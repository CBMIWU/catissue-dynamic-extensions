
package edu.common.dynamicextensions.domain;

import edu.common.dynamicextensions.domaininterface.DoubleValueInterface;
import edu.wustl.common.actionForm.AbstractActionForm;
import edu.wustl.common.exception.AssignDataException;

/**
 * @author sujay_narkar
 * @hibernate.joined-subclass table="DYEXTN_DOUBLE_CONCEPT_VALUE" 
 * @hibernate.joined-subclass-key column="IDENTIFIER" 
 */
public class DoubleValue extends PermissibleValue implements DoubleValueInterface{
    
     /**
     * 
     */
    protected Double value;

    /**
     * 
     */
	public void setAllValues(AbstractActionForm arg0) throws AssignDataException {
		// TODO Auto-generated method stub
		
	}

	/**
     * @hibernate.property name="value" type="double" column="VALUE"   
	 * @return Returns the value.
	 */
	public Double getValue() {
		return value;
	}
	/**
	 * @param value The value to set.
	 */
	public void setValue(Double value) {
		this.value = value;
	}
	/* (non-Javadoc)
	 * @see edu.common.dynamicextensions.domain.PermissibleValue#getValueAsObject()
	 */
	public Object getValueAsObject()
	{
		return value;
	}
}
