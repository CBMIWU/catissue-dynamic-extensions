
package edu.common.dynamicextensions.domain;

import java.util.Collection;
import java.util.Date;
import java.util.HashSet;

import edu.common.dynamicextensions.domaininterface.AbstractMetadataInterface;
import edu.common.dynamicextensions.domaininterface.SemanticPropertyInterface;
import edu.common.dynamicextensions.domaininterface.TaggedValueInterface;
import edu.wustl.common.domain.AbstractDomainObject;

/**
 * This is an abstract class extended by Entity, Entity group, Attribute.
 * This class stores basic information needed for metadata objects.  
 * @version 1.0
 * @created 28-Sep-2006 12:20:06 PM
 * @hibernate.class table="DYEXTN_ABSTRACT_METADATA" 
 */
public abstract class AbstractMetadata extends AbstractDomainObject implements java.io.Serializable, AbstractMetadataInterface
{

	/**
	 * Serial Version Unique Identifief
	 */
	protected static final long serialVersionUID = 1234567890L;

	/**
	 * Internally generated identifier.
	 */
	protected Long id;

	/**
	 * This method returns the unique identifier of the AbstractMetadata.
	 * @hibernate.id name="id" column="IDENTIFIER" type="long"
	 * length="30" unsaved-value="null" generator-class="native"
	 * @hibernate.generator-param name="sequence" value="DYEXTN_ABSTRACT_METADATA_SEQ"
	 * @return the identifier of the AbstractMetadata.
	 */
	public Long getId()
	{
		return id;
	}

	/**
	 * This method sets the unique identifier of the AbstractMetadata.
	 * @param id The identifier to set.
	 */
	public void setId(Long id)
	{
		this.id = id;
	}
	/**
	 * Name of metadata object
	 */
	protected String name;

	/**
	 * Description of metadata object
	 */
	protected String description;

	/**
	 * Last updated date for metadata object
	 */
	protected Date lastUpdated;

	/**
	 * Created date for metadata object
	 */
	protected Date createdDate;

	/**
	 * Semantic property collection.
	 */
	protected Collection<SemanticPropertyInterface> semanticPropertyCollection;

    /**
     * Semantic property collection.
     */
    protected Collection<TaggedValueInterface> taggedValueCollection;
	/**
	 * Empty Constructor
	 */
	public AbstractMetadata()
	{
	}

	/**
	 * This method returns the Created Date of the AbstractMetadata.
	 * @hibernate.property name="createdDate" type="date" column="CREATED_DATE" 
	 * @return the createdDate of the AbstractMetadata.
	 */
	public Date getCreatedDate()
	{
		return createdDate;
	}

	/**
	 * This method sets the Created Date of the AbstractMetadata.
	 * @param createdDate The createdDate to set.
	 */
	public void setCreatedDate(Date createdDate)
	{
		this.createdDate = createdDate;
	}

	/**
	 * This method returns the description of the AbstractMetadata.
	 * @hibernate.property name="description" type="string" column="DESCRIPTION"
	 * @return the description of the AbstractMetadata.
	 */
	public String getDescription()
	{
		return description;
	}

	/**
	 * This method sets the description of the AbstractMetadata.
	 * @param description The description to set.
	 */
	public void setDescription(String description)
	{
		this.description = description;
	}



	/**
	 * This method returns the date of last updation of the meta data.
	 * @hibernate.property name="lastUpdated" type="date" column="LAST_UPDATED" 
	 * @return the date of last updation of the meta data.
	 */
	public Date getLastUpdated()
	{
		return lastUpdated;
	}

	/**
	 * The method sets the date of last updation of the meta data to the given date.
	 * @param lastUpdated the date to be set as last updation date.
	 */
	public void setLastUpdated(Date lastUpdated)
	{
		this.lastUpdated = lastUpdated;
	}

	/**
	 * This method returns the name of the AbstractMetadata.
	 * @hibernate.property name="name" type="string" column="NAME"
	 * @return the name of the AbstractMetadata.
	 */
	public String getName()
	{
		return name;
	}

	/**
	 * This method sets the name of the AbstractMetadata to the given name.
	 * @param name the name to be set.
	 */
	public void setName(String name)
	{
		this.name = name;
	}

	/**
	 * This method returns the Collection of SemanticProperties of the AbstractMetadata.
	 * @hibernate.set name="semanticPropertyCollection" cascade="save-update"
	 * inverse="false" lazy="false"
	 * @hibernate.collection-key column="ABSTRACT_METADATA_ID"
	 * @hibernate.collection-one-to-many class="edu.common.dynamicextensions.domain.SemanticProperty"
	 * @return the Collection of SemanticProperties of the AbstractMetadata.
	 */
	public Collection<SemanticPropertyInterface> getSemanticPropertyCollection()
	{
		return semanticPropertyCollection;
	}

	/**
	 * This method sets the semanticPropertyCollection to the given Collection of SemanticProperties.
	 * @param semanticPropertyCollection the Collection of SemanticProperties to be set.
	 */
	public void setSemanticPropertyCollection(Collection<SemanticPropertyInterface> semanticPropertyCollection)
	{
		this.semanticPropertyCollection = semanticPropertyCollection;
	}

	/**
	 * This method returns the System Identifier of the AbstractMetadata.
	 * @return Long the unique System Identifier of the AbstractMetadata.
	 */
	public Long getSystemIdentifier()
	{
		return id;
	}

	/**
	 * This method sets the unique System Identifier of the AbstractMetadata.
	 * @param systemIdentifier the System Identifier to be set.
	 */
	public void setSystemIdentifier(Long systemIdentifier)
	{
		this.id = systemIdentifier;
	}

	/**
	 * This method overrides the equals method of the Object Class.
	 * This method checks the equality of the AbstractMetadata objects.
	 * @return boolean true if the both AbstractMetadata objects are equal otherwise false. 
	 */
	public boolean equals(Object obj)
	{
		boolean equals = false;
		if (obj instanceof AbstractMetadata)
		{
			AbstractMetadata abstractMetadata = (AbstractMetadata) obj;
			Long thisId = getId();

			if (thisId != null && thisId.equals(abstractMetadata.getId()))
			{
				equals = true;
			}
		}
		return equals;
	}

	/**
	 * This method overrides the equals method of the Object Class.
	 * It returns the HashCode of this AttributeMetadata instance.
	 * @return int The HashCode of the AttributeMetadata instance.
	 */
	public int hashCode()
	{

		return 1;
//				int hashCode = 0;
//
//		if (getId() != null)
//		{
//			hashCode += getId().hashCode();
//		}
//		return hashCode;
	}

	/**
	 * This method adds a SemanticProperty to the AbstractMetadata.
	 * @param semanticPropertyInterface A SemanticProperty to be added.
	 */
	public void addSemanticProperty(SemanticPropertyInterface semanticPropertyInterface)
	{
		if (semanticPropertyCollection == null)
		{
			semanticPropertyCollection = new HashSet<SemanticPropertyInterface>();
		}
		semanticPropertyCollection.add(semanticPropertyInterface);
	}

    /**
     * This method returns the Collection of TaggedValue of the AbstractMetadata.
     * @hibernate.set name="taggedValueCollection" cascade="all-delete-orphan"
     * inverse="false" lazy="false"
     * @hibernate.collection-key column="ABSTRACT_METADATA_ID"
     * @hibernate.collection-one-to-many class="edu.common.dynamicextensions.domain.TaggedValue"
     * @return the Collection of TaggedValue of the AbstractMetadata.
     */
    public Collection<TaggedValueInterface> getTaggedValueCollection()
    {
        return taggedValueCollection;
    }

    /**
     * Setter method for taggedValueCollection
     * @param taggedValueCollection Collection of tagged values.
     */
    public void setTaggedValueCollection(
            Collection<TaggedValueInterface> taggedValueCollection)
    {
        this.taggedValueCollection = taggedValueCollection;
    }
    /**
     * 
     * @param taggedValueInterface
     */
    public void addTaggedValue(TaggedValueInterface taggedValueInterface)
    {
        if (taggedValueInterface == null)
        {
            return;
        }
        
        if (taggedValueCollection == null)
        {
        	taggedValueCollection = new HashSet<TaggedValueInterface>();
        }
        taggedValueCollection.add(taggedValueInterface);
        
        
    }

}