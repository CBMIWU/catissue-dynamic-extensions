
package edu.common.dynamicextensions.domain;

import edu.common.dynamicextensions.domaininterface.DoubleTypeInformationInterface;
import edu.common.dynamicextensions.domaininterface.DoubleValueInterface;
import edu.common.dynamicextensions.domaininterface.PermissibleValueInterface;
import edu.common.dynamicextensions.entitymanager.EntityManagerConstantsInterface;

/**
 * @hibernate.joined-subclass table="DYEXTN_DOUBLE_TYPE_INFO" 
 * @hibernate.joined-subclass-key column="IDENTIFIER"  
 * @author sujay_narkar
 *
 */
public class DoubleAttributeTypeInformation extends NumericAttributeTypeInformation
		implements
			DoubleTypeInformationInterface
{

	/**
	 * Empty Constructor.
	 */
	public DoubleAttributeTypeInformation()
	{

	}
	
	/** 
	 * @see edu.common.dynamicextensions.domaininterface.AttributeTypeInformationInterface#getDataType()
	 */
	public String getDataType()
	{
		
		return EntityManagerConstantsInterface.DOUBLE_ATTRIBUTE_TYPE;
	}
	
	@Override
	/**
	 * 
	 */
	public PermissibleValueInterface getPermissibleValueForString(String value) {
		
		DomainObjectFactory domainObjectFactory = DomainObjectFactory.getInstance();
		DoubleValueInterface doubleValueInterface = domainObjectFactory.createDoubleValue();
		doubleValueInterface.setValue(new Double(value));
		return doubleValueInterface ;
	}
}
