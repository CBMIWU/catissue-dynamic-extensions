
package edu.common.dynamicextensions.domain;

import java.text.ParseException;

import edu.common.dynamicextensions.domaininterface.ObjectTypeInformationInterface;
import edu.common.dynamicextensions.domaininterface.PermissibleValueInterface;
import edu.common.dynamicextensions.entitymanager.EntityManagerConstantsInterface;

/**
 * @hibernate.joined-subclass table="DYEXTN_OBJECT_TYPE_INFO" 
 * @hibernate.joined-subclass-key column="IDENTIFIER"  
 * @author Rahul Ner
 *
 */
public class ObjectAttributeTypeInformation extends AttributeTypeInformation
		implements
			ObjectTypeInformationInterface
{

	/**
	 * Empty Constructor.
	 */
	public ObjectAttributeTypeInformation()
	{
		// TODO Auto-generated constructor stub
	}

	/**
	 * @see edu.common.dynamicextensions.domaininterface.AttributeTypeInformationInterface#getDataType()
	 */
	public String getDataType()
	{
		return EntityManagerConstantsInterface.OBJECT_ATTRIBUTE_TYPE;
	}

	/**
	 * 
	 */
	public PermissibleValueInterface getPermissibleValueForString(String value)
			throws ParseException
	{
		return null;
	}

}
