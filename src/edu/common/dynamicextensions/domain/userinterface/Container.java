
package edu.common.dynamicextensions.domain.userinterface;

import java.io.Serializable;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;

import edu.common.dynamicextensions.domain.DynamicExtensionBaseDomainObject;
import edu.common.dynamicextensions.domain.Entity;
import edu.common.dynamicextensions.domaininterface.EntityInterface;
import edu.common.dynamicextensions.domaininterface.userinterface.ContainerInterface;
import edu.common.dynamicextensions.domaininterface.userinterface.ControlInterface;
import edu.wustl.common.actionForm.AbstractActionForm;
import edu.wustl.common.domain.AbstractDomainObject;
import edu.wustl.common.exception.AssignDataException;

/**
 * @version 1.0
 * @created 28-Sep-2006 12:20:07 PM
 * @hibernate.class table="DYEXTN_CONTAINER"
 */
public class Container extends DynamicExtensionBaseDomainObject implements Serializable, ContainerInterface
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 8092366994778601914L;
	/**
	 * @return
	 * @hibernate.id name="id" column="IDENTIFIER" type="long"
	 * length="30" unsaved-value="null" generator-class="native"
	 * @hibernate.generator-param name="sequence" value="DYEXTN_CONTAINER_SEQ"
	 */
	public Long getId()
	{
		return id;
	}

	/**
	 * css for the buttons on the container.
	 */
	protected String buttonCss;
	/**
	 * Caption to be displayed on the container.
	 */
	protected String caption;
	/**
	 * css for the main table in the container.
	 */
	protected String mainTableCss;
	/**
	 * Specifies the indicator symbol that will be used to denote a required field.
	 */
	protected String requiredFieldIndicatior;
	/**
	 * Specifies the warning mesaage to be displayed in case required fields are not entered by the user.
	 */
	protected String requiredFieldWarningMessage;
	/**
	 * css of the title in the container.
	 */
	protected String titleCss;
	/**
	 * Collection of controls that are in this container.
	 */
	protected Collection<ControlInterface> controlCollection = new HashSet<ControlInterface>();
	/**
	 * Entity to which this container is associated.
	 */
	protected Entity entity;

	/**
	 * Empty constructor
	 */
	public Container()
	{
	}

	/**
	 * @hibernate.property name="buttonCss" type="string" column="BUTTON_CSS" 
	 * @return Returns the buttonCss.
	 */
	public String getButtonCss()
	{
		return buttonCss;
	}

	/**
	 * @param buttonCss The buttonCss to set.
	 */
	public void setButtonCss(String buttonCss)
	{
		this.buttonCss = buttonCss;
	}

	/**
	 * @hibernate.property name="caption" type="string" column="CAPTION" 
	 * @return Returns the caption.
	 */
	public String getCaption()
	{
		return caption;
	}

	/**
	 * @param caption The caption to set.
	 */
	public void setCaption(String caption)
	{
		this.caption = caption;
	}

	/**
	 * @hibernate.set name="controlCollection" table="DYEXTN_CONTROL"
	 * cascade="save-update" inverse="false" lazy="false"
	 * @hibernate.collection-key column="CONTAINER_ID"
	 * @hibernate.collection-one-to-many class="edu.common.dynamicextensions.domain.userinterface.Control"
	 * @return Returns the controlCollection.
	 */
	public Collection<ControlInterface> getControlCollection()
	{
		return controlCollection;
	}

	/**
	 * @param controlCollection The controlCollection to set.
	 */
	public void setControlCollection(Collection<ControlInterface> controlCollection)
	{
		this.controlCollection = controlCollection;
	}

	/**
	 * @hibernate.many-to-one column ="ENTITY_ID" class="edu.common.dynamicextensions.domain.Entity"
	 * cascade="none" 
	 * @return Returns the entity.
	 */
	public EntityInterface getEntity()
	{
		return entity;
	}

	/**
	 * @hibernate.property name="mainTableCss" type="string" column="MAIN_TABLE_CSS" 
	 * @return Returns the mainTableCss.
	 */
	public String getMainTableCss()
	{
		return mainTableCss;
	}

	/**
	 * @param mainTableCss The mainTableCss to set.
	 */
	public void setMainTableCss(String mainTableCss)
	{
		this.mainTableCss = mainTableCss;
	}

	/**
	 * @hibernate.property name="requiredFieldIndicatior" type="string" column="REQUIRED_FIELD_INDICATOR" 
	 * @return Returns the requiredFieldIndicatior.
	 */
	public String getRequiredFieldIndicatior()
	{
		return requiredFieldIndicatior;
	}

	/**
	 * @param requiredFieldIndicatior The requiredFieldIndicatior to set.
	 */
	public void setRequiredFieldIndicatior(String requiredFieldIndicatior)
	{
		this.requiredFieldIndicatior = requiredFieldIndicatior;
	}

	/**
	 * @hibernate.property name="requiredFieldWarningMessage" type="string" column="REQUIRED_FIELD_WARNING_MESSAGE" 
	 * @return Returns the requiredFieldWarningMessage.
	 */
	public String getRequiredFieldWarningMessage()
	{
		return requiredFieldWarningMessage;
	}

	/**
	 * @param requiredFieldWarningMessage The requiredFieldWarningMessage to set.
	 */
	public void setRequiredFieldWarningMessage(String requiredFieldWarningMessage)
	{
		this.requiredFieldWarningMessage = requiredFieldWarningMessage;
	}

	/**
	 * @hibernate.property name="titleCss" type="string" column="TITLE_CSS" 
	 * @return Returns the titleCss.
	 */
	public String getTitleCss()
	{
		return titleCss;
	}

	/**
	 * @param titleCss The titleCss to set.
	 */
	public void setTitleCss(String titleCss)
	{
		this.titleCss = titleCss;
	}

	/**
	 * 
	 */
	public void addControl(ControlInterface controlInterface)
	{
		if (controlCollection == null)
		{
			controlCollection = new HashSet<ControlInterface>();
		}
		controlCollection.add(controlInterface);
	}

	/**
	 * 
	 */

	public void setEntity(EntityInterface entityInterface)
	{
		entity = (Entity) entityInterface;

	}

	/**
	 * 
	 * @param sequenceNumber
	 * @return
	 */
	public ControlInterface getControlInterfaceBySequenceNumber(String sequenceNumber)
	{
		Collection controlsCollection = this.getControlCollection();
		if (controlsCollection != null)
		{
			Iterator controlsIterator = controlsCollection.iterator();
			ControlInterface controlInterface;
			while (controlsIterator.hasNext())
			{
				controlInterface = (ControlInterface) controlsIterator.next();
				if (controlInterface.getSequenceNumber().equals(new Integer(sequenceNumber)))
				{
					return controlInterface;
				}
			}
		}
		return null;
	}

	/**
	 * 
	 */
	public void removeControl(ControlInterface controlInterface)
	{
		if ((controlInterface != null) && (controlCollection != null))
		{
			if (controlCollection.contains(controlInterface))
			{
				controlCollection.remove(controlInterface);
			}
		}
	}

}