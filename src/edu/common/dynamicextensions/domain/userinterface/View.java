package edu.common.dynamicextensions.domain.userinterface;

import java.io.Serializable;
import java.util.Collection;
import java.util.HashSet;

import edu.wustl.common.actionForm.AbstractActionForm;
import edu.wustl.common.domain.AbstractDomainObject;
import edu.wustl.common.exception.AssignDataException;

/**
 * @version 1.0
 * @created 28-Sep-2006 12:20:09 PM
 * @hibernate.class table="DYEXTN_VIEW"
 */
public class View extends AbstractDomainObject implements Serializable{

    /**
     * Unique identifier for the object
     */
	protected Long id;
	/**
	 * Name of the view.
	 */
	protected String name;
	/**
	 * Collection of container for this view.
	 */
	protected Collection containerCollection = new HashSet();

	public View(){

	}

	public void finalize() throws Throwable {

	}
	

    /**
     * @hibernate.id name="id" column="IDENTIFIER" type="long"
     * length="30" unsaved-value="null" generator-class="native"
     * @hibernate.generator-param name="sequence" value="DYEXTN_VIEW_SEQ"
     * @return Returns the id.
     */
    public Long getId() {
        return id;
    }
    /**
     * @param id The id to set.
     */
    public void setId(Long id) {
        this.id = id;
    }
    /**
     * @hibernate.property name="name" type="string" column="NAME" 
     * @return Returns the name.
     */
    public String getName() {
        return name;
    }
    /**
     * @param name The name to set.
     */
    public void setName(String name) {
        this.name = name;
    }
    

    /**
     * @hibernate.set name="containerCollection" table="DYEXTN_CONTAINER"
     * cascade="none" inverse="false" lazy="false"
     * @hibernate.collection-key column="VIEW_ID"
     * @hibernate.collection-one-to-many class="edu.common.dynamicextensions.domain.userinterface.Container"
     * @return Returns the containerCollection.
     */
    public Collection getContainerCollection() {
        return containerCollection;
    }
    /**
     * @param containerCollection The containerCollection to set.
     */
    public void setContainerCollection(Collection containerCollection) {
        this.containerCollection = containerCollection;
    }

    /* (non-Javadoc)
     * @see edu.wustl.common.domain.AbstractDomainObject#setAllValues(edu.wustl.common.actionForm.AbstractActionForm)
     */
    public void setAllValues(AbstractActionForm arg0) throws AssignDataException {
        // TODO Auto-generated method stub
        
    }

    /* (non-Javadoc)
     * @see edu.wustl.common.domain.AbstractDomainObject#getSystemIdentifier()
     */
    public Long getSystemIdentifier() {
        // TODO Auto-generated method stub
        return null;
    }

    /* (non-Javadoc)
     * @see edu.wustl.common.domain.AbstractDomainObject#setSystemIdentifier(java.lang.Long)
     */
    public void setSystemIdentifier(Long arg0) {
        // TODO Auto-generated method stub
        
    }
}