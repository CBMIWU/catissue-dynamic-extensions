package edu.common.dynamicextensions.domain;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import edu.common.dynamicextensions.domaininterface.AbstractEntityInterface;
import edu.common.dynamicextensions.domaininterface.databaseproperties.TablePropertiesInterface;

/**
 * This is an abstract class extended by Entity, Entity group, Attribute.
 * This class stores basic information needed for metadata objects.  
 * @version 1.0
 * @created 28-Sep-2006 12:20:06 PM
 * @hibernate.joined-subclass table="DYEXTN_ABSTRACT_ENTITY"
 * @hibernate.joined-subclass-key column="id"
 * @hibernate.cache usage="read-write"
 */
public abstract class AbstractEntity extends AbstractMetadata implements AbstractEntityInterface {

    /**
     * Serial Version UID
     */
    private static final long serialVersionUID = 1234523890L;
    
    protected Set<TablePropertiesInterface> tablePropertiesCollection = new HashSet<TablePropertiesInterface>();
    
    public AbstractEntity()
    {
        
    }

    /**
     * This method returns the Collection of TableProperties of this Entity.
     * @hibernate.set name="tablePropertiesColletion" table="DYEXTN_TABLE_PROPERTIES" cascade="all-delete-orphan" inverse="false" lazy="false"
     * @hibernate.collection-key column="ENTITY_ID"
     * @hibernate.cache usage="read-write"
     * @hibernate.collection-one-to-many class="edu.common.dynamicextensions.domain.databaseproperties.TableProperties"
     * @return the Collection of TableProperties of this Entity.
     */
    public Set<TablePropertiesInterface> getTablePropertiesCollection() {
        return tablePropertiesCollection;
    }

    /**
     * @param tablePropertiesCollection the tablePropertiesCollection to set
     */
    public void setTablePropertiesCollection(Set<TablePropertiesInterface> tablePropertiesCollection) {
        this.tablePropertiesCollection = tablePropertiesCollection;
    }
    
    /**
     * This method returns the TableProperties of the Entity.
     * @return the TableProperties of the Entity.
     */
    public TablePropertiesInterface getTableProperties()
    {
        TablePropertiesInterface tableProperties = null;
        if (tablePropertiesCollection != null && !tablePropertiesCollection.isEmpty())
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
          else
          {
              this.tablePropertiesCollection.clear();
          }
          this.tablePropertiesCollection.add(tableProperties);
      }
      
      

}
