/**
 *<p>Title: </p>
 *<p>Description:  </p>
 *<p>Copyright:TODO</p>
 *@author
 *@version 1.0
 */

package edu.common.dynamicextensions.domain;

import edu.common.dynamicextensions.domaininterface.TaggedValueInterface;

/**
 * For every abstract metadata object tagged values are associated.
 * This Class represents the tagged values of a Metadata.
 * @version 1.0
 * @created 28-Sep-2006 12:20:08 PM
 * @hibernate.class table="DYEXTN_TAGGED_VALUE"
 * @hibernate.cache  usage="read-write"
 *
 */
public class TaggedValue extends DynamicExtensionBaseDomainObject implements TaggedValueInterface
{

	public TaggedValue()
	{
		
	}
	public TaggedValue(String key, String value)
	{
		super();
		this.key = key;
		this.value = value;
	}

	/**
	 * DEfault serial id
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * Key part of the tagged value.
	 */
	private String key;

	/**
	 * Value part of the tagged value
	 */
	private String value;

	/**
	 * This method returns the Unique identifier.
	 * @hibernate.id name="id" column="IDENTIFIER" type="long"
	 * length="30" unsaved-value="null" generator-class="native"
	 * @hibernate.generator-param name="sequence" value="DYEXTN_TAGGED_VALUE_SEQ"
	 * @return the Unique identifier.
	 */
	public Long getId()
	{
		return id;
	}

	/**
	 * This method returns the key.
	 * @hibernate.property name="key" type="string" column="T_KEY"
	 * @return the concept code.
	 */
	public String getKey()
	{
		return key;
	}

	public void setKey(String key)
	{
		this.key = key;
	}

	/**
	 * This method returns the concept code.
	 * @hibernate.property name="value" type="string" column="T_VALUE"
	 * @return the concept code.
	 */
	public String getValue()
	{
		return value;
	}

	/**
	 * @see edu.common.dynamicextensions.domaininterface.TaggedValueInterface#setValue(java.lang.String)
	 */
	public void setValue(String value)
	{
		this.value = value;
	}

	/**
	 * @see java.lang.Object#clone()
	 */
	public TaggedValue getObjectCopy()
	{
		TaggedValue taggedValue = new TaggedValue();
		taggedValue.setKey(String.valueOf(key));
		taggedValue.setValue(String.valueOf(value));
		return taggedValue;
	}
}
