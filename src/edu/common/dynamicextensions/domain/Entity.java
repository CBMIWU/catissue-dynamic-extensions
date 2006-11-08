
package edu.common.dynamicextensions.domain;

import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;

import edu.common.dynamicextensions.domaininterface.AbstractAttributeInterface;
import edu.common.dynamicextensions.domaininterface.AssociationInterface;
import edu.common.dynamicextensions.domaininterface.AttributeInterface;
import edu.common.dynamicextensions.domaininterface.EntityGroupInterface;
import edu.common.dynamicextensions.domaininterface.EntityInterface;
import edu.common.dynamicextensions.domaininterface.databaseproperties.TablePropertiesInterface;
import edu.wustl.common.actionForm.AbstractActionForm;
import edu.wustl.common.exception.AssignDataException;

/**
 * An entity is something that has a distinct, separate existence, though it need not be a material 
 * existence. In particular, abstractions and legal fictions are usually regarded as entities.
 * An entity can either have many attributes or many associations.
 * @version 1.0
 * @created 28-Sep-2006 12:20:08 PM
 * @hibernate.joined-subclass table="DYEXTN_ENTITY"
 * @hibernate.joined-subclass-key column="IDENTIFIER"
 */
public class Entity extends AbstractMetadata implements EntityInterface
{

	/**
	 * Serial Version Unique Identifier
	 */
	private static final long serialVersionUID = -552600540977483821L;

	/**
	 * Collection of attributes in this entity.
	 */
	protected Collection<AbstractAttributeInterface> abstractAttributeCollection;

	/**
	 * Table property for this entity.
	 */
	protected Collection<TablePropertiesInterface> tablePropertiesCollection;

	/**
	 * Collection of EntityGroup.
	 */
	protected Collection<EntityGroupInterface> entityGroupCollection;

	/**
	 * Empty Constructor.
	 */
	public Entity()
	{
	}

	/**
	 * This method returns the Collection of the EntityGroups.
	 * @hibernate.set name="entityGroupCollection" table="DYEXTN_ENTITY_GROUP_REL" 
	 * cascade="save-update" inverse="true" lazy="false"
	 * @hibernate.collection-key column="ENTITY_ID"
	 * @hibernate.collection-many-to-many class="edu.common.dynamicextensions.domain.EntityGroup" column="ENTITY_GROUP_ID"
	 * @return the Collection of the Entities.
	 */
	public Collection<EntityGroupInterface> getEntityGroupCollection()
	{
		return entityGroupCollection;
	}

	/**
	 * This method sets the entityGroupCollection to the given Collection of the Entities.
	 * @param entityGroupCollection The entityGroupCollection to set.
	 */
	public void setEntityGroupCollection(Collection<EntityGroupInterface> entityGroupCollection)
	{
		this.entityGroupCollection = entityGroupCollection;
	}

	/**
	 * Set all values from the form
	 * @param abstractActionForm the ActionForm
	 * @throws AssignDataException if data is not in proper format.
	 */
	public void setAllValues(AbstractActionForm abstractActionForm)
	{
	}

	/**
	 * This method returns the Collection of TableProperties of this Entity.
	 * @hibernate.set name="tablePropertiesColletion" table="DYEXTN_TABLE_PROPERTIES" cascade="save-update"
	 * inverse="false" lazy="false"
	 * @hibernate.collection-key column="ENTITY_ID"
	 * @hibernate.collection-one-to-many class="edu.common.dynamicextensions.domain.databaseproperties.TableProperties"
	 * @return the Collection of TableProperties of this Entity.
	 */
	private Collection<TablePropertiesInterface> getTablePropertiesCollection()
	{
		return tablePropertiesCollection;
	}

	/**
	 * This method sets the tablePropertiesColletion to the given Collection of TableProperties. 
	 * @param tablePropertiesColletion Collection of TableProperties to be set.
	 */
	private void setTablePropertiesCollection(Collection<TablePropertiesInterface> tablePropertiesColletion)
	{
		this.tablePropertiesCollection = tablePropertiesColletion;
	}

	/**
	 * This method returns the TableProperties of the Entity.
	 * @return the TableProperties of the Entity.
	 */
	public TablePropertiesInterface getTableProperties()
	{
		TablePropertiesInterface tableProperties = null;
		if (tablePropertiesCollection != null)
		{
			Iterator tabletPropertiesIterator = tablePropertiesCollection.iterator();
			tableProperties = (TablePropertiesInterface) tabletPropertiesIterator.next();
		}
		return tableProperties;
	}

	/**
	 * This method sets the TableProperties of the Entity to the given TableProperties.
	 * @param tableProperties the TableProperties to be set.
	 */
	public void setTableProperties(TablePropertiesInterface tableProperties)
	{
		if (tablePropertiesCollection == null)
		{
			tablePropertiesCollection = new HashSet<TablePropertiesInterface>();
		}
		this.tablePropertiesCollection.add(tableProperties);
	}

	/**
	 * This method adds an AbstractAttribute to the Entity's Collection of AbstractAttribute.
	 * @param abstractAttribute AbstractAttribute to be added.
	 */
	public void addAbstractAttribute(AbstractAttributeInterface abstractAttribute)
	{
		if (abstractAttribute == null)
		{
			return;
		}
		
		if (abstractAttributeCollection == null)
		{
			abstractAttributeCollection = new HashSet<AbstractAttributeInterface>();
		}
		abstractAttributeCollection.add(abstractAttribute);
	}

     /**
     * 
     */
    public void addEntityGroupInterface(
            EntityGroupInterface entityGroupInterface)
    {
        if (this.entityGroupCollection == null) {
            entityGroupCollection = new HashSet<EntityGroupInterface>();
        }
        entityGroupCollection.add(entityGroupInterface);

    }

	/**
	 * This method returns the Collection of AbstractAttribute.
	 * @hibernate.set name="abstractAttributeCollection" table="DYEXTN_ATTRIBUTE"
	 * cascade="all-delete-orphan" inverse="false" lazy="false"
	 * @hibernate.collection-key column="ENTIY_ID"
	 * @hibernate.collection-one-to-many class="edu.common.dynamicextensions.domain.AbstractAttribute" 
	 * @return the Collection of AbstractAttribute.
	 */
	public Collection<AbstractAttributeInterface> getAbstractAttributeCollection()
	{
		return abstractAttributeCollection;
	}

	/**
	 * This method sets the abstractAttributeCollection to the given Collection of AbstractAttribute.
	 * @param abstractAttributeCollection The abstractAttributeCollection to set.
	 */
	public void setAbstractAttributeCollection(Collection<AbstractAttributeInterface> abstractAttributeCollection)
	{
		this.abstractAttributeCollection = abstractAttributeCollection;
	}

	/**
	 * This method return the Collection of Attributes.
	 * @return the Collection of Attributes.
	 */
	public Collection getAttributeCollection()
	{
		return this.abstractAttributeCollection;
	}

	/**
	 * This method return the Collection of Association.
	 * @return the Collection of Association.
	 */
	public Collection getAssociationCollection()
	{
		return null;
	}

	/**
	 * This method removes an AbstractAttribute from the Entity's Collection of AbstractAttribute.
	 * @param abstractAttribute an AbstractAttribute to be removed.
	 */
	public void removeAbstractAttribute(AbstractAttributeInterface abstractAttribute)
	{
		if (abstractAttributeCollection != null)
		{
			if (abstractAttributeCollection.contains(abstractAttribute))
			{
				abstractAttributeCollection.remove(abstractAttributeCollection);
			}
		}
	}

	/**
	 * This method returns the AbstractAttribute for the given corresponding identifier.
	 * @param id identifier of the desired AbstractAttribute.
	 * @return the matched instance of AbstractAttribute.
	 */
	public AbstractAttributeInterface getAttributeByIdentifier(Long id)
	{
		AbstractAttributeInterface abstractAttribute = null;

		if (this.abstractAttributeCollection != null && id != null)
		{
			for (AbstractAttributeInterface attributeIterator : abstractAttributeCollection)
			{
				if (attributeIterator.getId().equals(id))
				{
					abstractAttribute = attributeIterator;
					break;
				}
			}
		}
		return abstractAttribute;
	}

}