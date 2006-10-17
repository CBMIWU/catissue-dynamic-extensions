/**
 *<p>Title: EntityProcessor</p>
 *<p>Description:  This class acts as a utility class which processes tne entity in various ways as needed
 *and provides methods to the UI layer.This processor class is a POJO and not a framework specific class so 
 *it can be used by all types of presentation layers.  </p>
 *<p>Copyright:TODO</p>
 *@author Vishvesh Mulay
 *@version 1.0
 */ 
package edu.common.dynamicextensions.processor;

import java.util.Iterator;

import edu.common.dynamicextensions.domain.DomainObjectFactory;
import edu.common.dynamicextensions.domain.SemanticProperty;
import edu.common.dynamicextensions.domaininterface.EntityInterface;
import edu.common.dynamicextensions.domaininterface.SemanticPropertyInterface;
import edu.common.dynamicextensions.entitymanager.EntityManager;
import edu.common.dynamicextensions.exception.DynamicExtensionsApplicationException;
import edu.common.dynamicextensions.exception.DynamicExtensionsSystemException;
import edu.common.dynamicextensions.ui.interfaces.EntityUIBeanInterface;


/**
 *<p>Title: EntityProcessor</p>
 *<p>Description:  This class acts as a utility class which processes tne entity in various ways as needed
 *and provides methods to the UI layer.This processor class is a POJO and not a framework specific class so 
 *it can be used by all types of presentation layers.  </p>
 *@author Vishvesh Mulay
 *@version 1.0
 */ 
public class EntityProcessor extends BaseDynamicExtensionsProcessor
{

	/**
	 * Protected constructor for entity processor
	 *
	 */
	protected EntityProcessor () {

	}

	/**
	 * this method gets the new instance of the entity processor to the caller.
	 * @return EntityProcessor EntityProcessor instance
	 */
	public static EntityProcessor getInstance () {
		return new EntityProcessor();
	}

	/**
	 * This method returns empty domain object of entityInterface.
	 * @return EntityInterface Returns new instance of EntityInterface from the domain object Factory.
	 */
	public EntityInterface createEntity() {
		return DomainObjectFactory.getInstance().createEntity();
	}

	/**
	 * This method creates a new instance of the EntityInterface from the domain object factory. After the creation
	 * of this instance it populates the entityInterface with the information that is provided through 
	 * the entityInformationInterface which is a parameter to the method.
	 * @param entityUIBeanInterface Implementation of entityInformationInterface 
	 * which has all the data required for the creation of the entity.
	 * @return EntityInterface Returns the unsaved instance of EntityInterface with populated values taken 
	 * from the entityInformationInterface.
	 * @throws DynamicExtensionsSystemException
	 * @throws DynamicExtensionsApplicationException
	 */
	public EntityInterface createAndSaveEntity(EntityUIBeanInterface entityUIBeanInterface) throws DynamicExtensionsSystemException, DynamicExtensionsApplicationException {
		EntityInterface entityInterface = DomainObjectFactory.getInstance().createEntity();
		populateEntity(entityUIBeanInterface, entityInterface);
		entityInterface = EntityManager.getInstance().createEntity(entityInterface);
		return entityInterface;
	}

	/**
	 * This method populates the given EntityInterface using the given entityInformationInterface.
	 * @param entityInterface Instance of EntityInterface which is populated using the informationInterface.
	 * @param entityUIBeanInterface Instance of EntityUIBeanInterface which is used to populate the entityInterface.
	 */
	public void populateEntity (EntityUIBeanInterface entityUIBeanInterface, EntityInterface entityInterface) {
		if (entityUIBeanInterface != null && entityInterface != null) {
			entityInterface.setName(entityUIBeanInterface.getFormName());
			entityInterface.setDescription(entityUIBeanInterface.getFormDescription());
			SemanticPropertyInterface semanticPropertyInterface = new SemanticProperty();
			semanticPropertyInterface.setConceptCode(entityUIBeanInterface.getConceptCode());
			if(entityInterface.getSemanticPropertyCollection() != null) {
				entityInterface.getSemanticPropertyCollection().clear();
			}
			entityInterface.addSemanticProperty(semanticPropertyInterface);
		}
	}

	/**
	 * This method will populate the EntityUIBeanInterface using the EntityInterface so that the 
	 * information of the Entity can be shown on the user page using the EntityUIBeanInterface.
	 * @param entityInterface Instance of EntityInterface from which to populate the informationInterface.
	 * @param entityUIBeanInterface Instance of EntityUIBeanInterface which will be populated using 
	 * the first parameter that is EntityInterface.
	 */
	public void populateEntityInformation (EntityInterface entityInterface, EntityUIBeanInterface entityUIBeanInterface) {
		if (entityInterface != null && entityUIBeanInterface != null) {
			entityUIBeanInterface.setFormName(entityInterface.getName());
			entityUIBeanInterface.setFormDescription(entityInterface.getDescription());
			if(!entityInterface.getSemanticPropertyCollection().isEmpty()) {
				Iterator iter = entityInterface.getSemanticPropertyCollection().iterator();
				while(iter.hasNext()) {
					String conceptCode = ((SemanticPropertyInterface)iter.next()).getConceptCode();
					entityUIBeanInterface.setConceptCode(conceptCode);
				}
			}
		}
	}
	/**
	 * This method creates a new instance of the EntityInterface from the domain object factory. After the creation
	 * of this instance it populates the entityInterface with the information that is provided through 
	 * the entityInformationInterface which is a parameter to the method.
	 * @param entityUIBeanInterface Implementation of entityInformationInterface 
	 * which has all the data required for the creation of the entity.
	 * @return EntityInterface Returns the unsaved instance of EntityInterface with populated values taken 
	 * from the entityInformationInterface.
	 * @throws DynamicExtensionsSystemException Exception
	 */
	public EntityInterface createAndPopulateEntity(EntityUIBeanInterface entityUIBeanInterface) throws DynamicExtensionsSystemException {
		EntityInterface entityInterface = DomainObjectFactory.getInstance().createEntity();
		populateEntity(entityUIBeanInterface, entityInterface);
		return entityInterface;
	}

	//public saveEntity(Enti)
}
