
package edu.common.dynamicextensions.domain;

import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;

import edu.common.dynamicextensions.domaininterface.AttributeInterface;
import edu.common.dynamicextensions.domaininterface.AttributeMetadataInterface;
import edu.common.dynamicextensions.domaininterface.AttributeTypeInformationInterface;
import edu.common.dynamicextensions.domaininterface.CaDSRValueDomainInfoInterface;
import edu.common.dynamicextensions.domaininterface.DataElementInterface;
import edu.common.dynamicextensions.domaininterface.databaseproperties.ColumnPropertiesInterface;
import edu.common.dynamicextensions.ui.util.ControlsUtility;
import edu.common.dynamicextensions.util.DynamicExtensionsUtility;

/**
 * Entites have attributes that distinguishes them form other entities.
 * This Class represnts the Attribute of Entities.
 * @version 1.0
 * @created 28-Sep-2006 12:20:08 PM
 * @hibernate.joined-subclass table="DYEXTN_PRIMITIVE_ATTRIBUTE"
 * @hibernate.joined-subclass-key column="IDENTIFIER"
 * @hibernate.cache  usage="read-write"
 */
public class Attribute extends AbstractAttribute
		implements
			AttributeInterface,
			AttributeMetadataInterface
{

	/**
	 *
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * Specifies whether this is an identified field or not.
	 */
	protected Boolean isIdentified;

	/**
	 * Specifies whether this is a primary key.
	 */
	protected Boolean isPrimaryKey = Boolean.FALSE;

	/**
	 * Specifies whether the column is nullable or not
	 */
	protected Boolean isNullable = Boolean.TRUE;

	/**
	 * Column property associated to this primitive attribute.
	 */
	protected Collection<ColumnPropertiesInterface> columnPropertiesCollection = new HashSet<ColumnPropertiesInterface>();

	/**
	 *
	 */
	protected Collection<AttributeTypeInformationInterface> attributeTypeInformationCollection = new HashSet<AttributeTypeInformationInterface>();
	/**
	 *
	 */
	protected Collection<CaDSRValueDomainInfo> caDSRValueDomainInfoCollection = new HashSet<CaDSRValueDomainInfo>();

	/**
	 * @hibernate.set name="attributeTypeInformationCollection" table="DYEXTN_ATTRIBUTE_TYPE_INFO"
	 * cascade="all-delete-orphan" inverse="false" lazy="false"
	 * @hibernate.collection-key column="PRIMITIVE_ATTRIBUTE_ID"
	 * @hibernate.cache  usage="read-write"
	 * @hibernate.collection-one-to-many class="edu.common.dynamicextensions.domain.AttributeTypeInformation"
	 * @return Returns the attributeTypeInformationCollection.
	 */
	private Collection<AttributeTypeInformationInterface> getAttributeTypeInformationCollection()
	{
		return attributeTypeInformationCollection;
	}

	/**
	 * @param dataElementCollection The dataElementCollection to set.
	 */
	private void setAttributeTypeInformationCollection(
			Collection<AttributeTypeInformationInterface> attributeTypeInformationCollection)
	{
		this.attributeTypeInformationCollection = attributeTypeInformationCollection;
	}

	/**
	 *
	 * @return AttributeTypeInformationInterface
	 */
	public AttributeTypeInformationInterface getAttributeTypeInformation()
	{
		AttributeTypeInformationInterface attributeTypeInformationInterface = null;
		if (attributeTypeInformationCollection != null)
		{
			Iterator attributeTypeInformationIterator = attributeTypeInformationCollection
					.iterator();
			attributeTypeInformationInterface = (AttributeTypeInformationInterface) attributeTypeInformationIterator
					.next();
		}
		return attributeTypeInformationInterface;
	}

	/**
	 *
	 * @return AttributeTypeInformationInterface
	 */
	public void setAttributeTypeInformation(
			AttributeTypeInformationInterface attributeTypeInformationInterface)
	{
		if (attributeTypeInformationCollection == null)
		{
			attributeTypeInformationCollection = new HashSet();
		}
		else
		{
			attributeTypeInformationCollection.clear();
		}

		this.attributeTypeInformationCollection.add(attributeTypeInformationInterface);

	}

	/**
	 * Empty constructor.
	 */
	public Attribute()
	{
	}

	/**
	 * This method returns whether the Attribute is identifiable or not.
	 * @hibernate.property name="isIdentified" type="boolean" column="IS_IDENTIFIED"
	 * @return whether the Attribute is identifiable or not.
	 */
	public Boolean getIsIdentified()
	{
		return isIdentified;
	}

	/**
	 * This method sets whether the Attribute is identifiable or not.
	 * @param isIdentified the Boolean value to be set.
	 */
	public void setIsIdentified(Boolean isIdentified)
	{
		this.isIdentified = isIdentified;
	}

	/**
	 * This method retunrs whehter the Attribute is a primary key or not.
	 * @hibernate.property name="isPrimaryKey" type="boolean" column="IS_PRIMARY_KEY"
	 * @return whehter the Attribute is a primary key or not.
	 */
	public Boolean getIsPrimaryKey()
	{
		return isPrimaryKey;
	}

	/**
	 * This method sets whehter the Attribute is a primary key or not.
	 * @param isPrimaryKey the Boolean value to be set.
	 */
	public void setIsPrimaryKey(Boolean isPrimaryKey)
	{
		this.isPrimaryKey = isPrimaryKey;
	}

	/**
	 * This method returns the Collection of Column Properties of the Attribute.
	 * @hibernate.set name="columnPropertiesCollection" table="DYEXTN_COLUMN_PROPERTIES"
	 * cascade="all-delete-orphan" inverse="false" lazy="false"
	 * @hibernate.collection-key column="PRIMITIVE_ATTRIBUTE_ID"
	 * @hibernate.cache  usage="read-write"
	 * @hibernate.collection-one-to-many class="edu.common.dynamicextensions.domain.databaseproperties.ColumnProperties"
	 * @return the Collection of Column Properties of the Attribute.
	 */
	private Collection<ColumnPropertiesInterface> getColumnPropertiesCollection()
	{
		return columnPropertiesCollection;
	}

	/**
	 * This method sets the columnPropertiesCollection to given Collection of the ColumnProperties.
	 * @param columnPropertiesCollection the Collection of the ColumnProperties to be set.
	 */
	private void setColumnPropertiesCollection(
			Collection<ColumnPropertiesInterface> columnPropertiesCollection)
	{
		this.columnPropertiesCollection = columnPropertiesCollection;
	}

	/**
	 * This method returns the ColumnProperties of the Attribute.
	 * @return the ColumnProperties of the Attribute.
	 */
	public ColumnPropertiesInterface getColumnProperties()
	{
		ColumnPropertiesInterface columnProperties = null;
		if (columnPropertiesCollection != null && !columnPropertiesCollection.isEmpty())
		{
			Iterator columnPropertiesIterator = columnPropertiesCollection.iterator();
			columnProperties = (ColumnPropertiesInterface) columnPropertiesIterator.next();
		}
		return columnProperties;
	}

	/**
	 * This method sets the ColumnProperties of the Attribute.
	 * @param columnProperties the ColumnProperties to be set.
	 */
	public void setColumnProperties(ColumnPropertiesInterface columnProperties)
	{
		if (columnPropertiesCollection == null)
		{
			columnPropertiesCollection = new HashSet<ColumnPropertiesInterface>();
		}
		else
		{
			columnPropertiesCollection.clear();
		}
		this.columnPropertiesCollection.add(columnProperties);
	}

	/**
	 * This method returns whether the Attribute is nullable or not.
	 * @hibernate.property name="isNullable" type="boolean" column="IS_NULLABLE"
	 * @return whether the Attribute is nullable or not.
	 */
	public Boolean getIsNullable()
	{
		return isNullable;
	}

	/**
	 * This method sets the whether the Attribute is nullable or not.
	 * @param isNullable the Boolean value to be set.
	 */
	public void setIsNullable(Boolean isNullable)
	{
		this.isNullable = isNullable;
	}

	/**
	 * @see edu.common.dynamicextensions.domaininterface.AttributeInterface#getDataType()
	 */
	public String getDataType()
	{
		String dataType = null;
		if (getAttributeTypeInformation() != null)
		{
			dataType = getAttributeTypeInformation().getDataType();
		}
		return dataType;
	}

	/**
	 * @hibernate.set name="caDSRValueDomainInfoCollection" table="DYEXTN_CADSR_VALUE_DOMAIN_INFO"
	 * cascade="all-delete-orphan" inverse="false" lazy="false"
	 * @hibernate.collection-key column="PRIMITIVE_ATTRIBUTE_ID"
	 * @hibernate.cache  usage="read-write"
	 * @hibernate.collection-one-to-many class="edu.common.dynamicextensions.domain.CaDSRValueDomainInfo"
	 * @return the caDSRValueDomainInfoCollection
	 */
	public Collection<CaDSRValueDomainInfo> getCaDSRValueDomainInfoCollection()
	{
		return caDSRValueDomainInfoCollection;
	}

	/**
	 * @param caDSRValueDomainInfoCollection the caDSRValueDomainInfoCollection to set
	 */
	public void setCaDSRValueDomainInfoCollection(
			Collection<CaDSRValueDomainInfo> caDSRValueDomainInfoCollection)
	{
		this.caDSRValueDomainInfoCollection = caDSRValueDomainInfoCollection;
	}

	/**
	 *
	 * @return CaDSRValueDomainInfoInterface
	 */
	public CaDSRValueDomainInfoInterface getCaDSRValueDomainInfo()
	{
		CaDSRValueDomainInfoInterface caDSRValueDomainInfoInterface = null;
		if (caDSRValueDomainInfoCollection != null)
		{
			Iterator caDSRValueDomainInfoIterator = caDSRValueDomainInfoCollection.iterator();
			caDSRValueDomainInfoInterface = (CaDSRValueDomainInfoInterface) caDSRValueDomainInfoIterator
					.next();
		}
		return caDSRValueDomainInfoInterface;
	}

	/**
	 *
	 *
	 */
	public void setCaDSRValueDomainInfo(CaDSRValueDomainInfoInterface caDSRValueDomainInfoInterface)
	{
		if (caDSRValueDomainInfoCollection == null)
		{
			caDSRValueDomainInfoCollection = new HashSet<CaDSRValueDomainInfo>();
		}
		else
		{
			caDSRValueDomainInfoCollection.clear();
		}
		this.caDSRValueDomainInfoCollection
				.add((CaDSRValueDomainInfo) caDSRValueDomainInfoInterface);
	}

	public String getDefaultValue()
	{
		return ControlsUtility.getDefaultValue(this);

	}

	/* (non-Javadoc)
	 * @see edu.common.dynamicextensions.domaininterface.AttributeMetadataInterface#getMaxSize()
	 */
	public int getMaxSize()
	{
		int maxSize = -1;
		AttributeTypeInformationInterface attributeTypeInformationInterface = this
				.getAttributeTypeInformation();
		if (attributeTypeInformationInterface != null
				&& attributeTypeInformationInterface instanceof StringAttributeTypeInformation)
		{
			StringAttributeTypeInformation stringAttributeTypeInformation = (StringAttributeTypeInformation) attributeTypeInformationInterface;
			if (stringAttributeTypeInformation != null
					&& stringAttributeTypeInformation.getSize() != null)
			{
				maxSize = stringAttributeTypeInformation.getSize().intValue();
			}
		}
		return maxSize;
	}

	/* (non-Javadoc)
	 * @see edu.common.dynamicextensions.domaininterface.AttributeMetadataInterface#getMeasurementUnit()
	 */
	public String getMeasurementUnit()
	{
		String measurementUnit = null;
		AttributeTypeInformationInterface attributeTypeInformationInterface = DynamicExtensionsUtility
				.getAttributeTypeInformation(this);
		if (attributeTypeInformationInterface != null)
		{
			if (attributeTypeInformationInterface instanceof LongAttributeTypeInformation)
			{
				LongAttributeTypeInformation longAttribute = (LongAttributeTypeInformation) attributeTypeInformationInterface;
				measurementUnit = longAttribute.getMeasurementUnits();
			}
			else if (attributeTypeInformationInterface instanceof DoubleAttributeTypeInformation)
			{
				DoubleAttributeTypeInformation doubleAttribute = (DoubleAttributeTypeInformation) attributeTypeInformationInterface;
				measurementUnit = doubleAttribute.getMeasurementUnits();
			}
		}
		return measurementUnit;
	}

	/* (non-Javadoc)
	 * @see edu.common.dynamicextensions.domaininterface.AttributeMetadataInterface#getDecimalPlaces()
	 */
	public int getDecimalPlaces()
	{
		int decimalPlaces = -1;
		AttributeTypeInformationInterface attributeTypeInformationInterface = this
				.getAttributeTypeInformation();
		if (attributeTypeInformationInterface instanceof DoubleAttributeTypeInformation)
		{
			decimalPlaces = ((DoubleAttributeTypeInformation) attributeTypeInformationInterface)
					.getDecimalPlaces();
		}
		return decimalPlaces;
	}

	/**
	 *
	 */
	public DataElementInterface getDataElement()
	{
		return this.getAttributeTypeInformation().getDataElement();
	}
}