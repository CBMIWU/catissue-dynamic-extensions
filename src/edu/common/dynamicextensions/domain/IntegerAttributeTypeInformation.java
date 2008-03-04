
package edu.common.dynamicextensions.domain;

import edu.common.dynamicextensions.domaininterface.IntegerTypeInformationInterface;
import edu.common.dynamicextensions.domaininterface.IntegerValueInterface;
import edu.common.dynamicextensions.domaininterface.PermissibleValueInterface;
import edu.common.dynamicextensions.entitymanager.EntityManagerConstantsInterface;

/**
 * @author sujay_narkar
 * @hibernate.joined-subclass table="DYEXTN_INTEGER_TYPE_INFO" 
 * @hibernate.joined-subclass-key column="IDENTIFIER" 
 *
 */
public class IntegerAttributeTypeInformation extends NumericAttributeTypeInformation
		implements
			IntegerTypeInformationInterface
{

	/**
	 * Empty Constructor.
	 */
	public IntegerAttributeTypeInformation()
	{

	}
	
	/** 
	 * @see edu.common.dynamicextensions.domaininterface.AttributeTypeInformationInterface#getDataType()
	 */
	public String getDataType()
	{
		
		return EntityManagerConstantsInterface.INTEGER_ATTRIBUTE_TYPE;
	}
	
	/**
	 * 
	 */
	public PermissibleValueInterface getPermissibleValueForString(String value) {
		DomainObjectFactory domainObjectFactory = DomainObjectFactory.getInstance();
		IntegerValueInterface integerValueInterface = domainObjectFactory.createIntegerValue();
		integerValueInterface.setValue(new Integer(value));
		return integerValueInterface;
	}
}
