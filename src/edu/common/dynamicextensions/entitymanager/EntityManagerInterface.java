
package edu.common.dynamicextensions.entitymanager;

import java.util.Collection;
import java.util.Map;

import edu.common.dynamicextensions.domaininterface.AbstractAttributeInterface;
import edu.common.dynamicextensions.domaininterface.AssociationInterface;
import edu.common.dynamicextensions.domaininterface.AttributeInterface;
import edu.common.dynamicextensions.domaininterface.EntityGroupInterface;
import edu.common.dynamicextensions.domaininterface.EntityInterface;
import edu.common.dynamicextensions.domaininterface.userinterface.ContainerInterface;
import edu.common.dynamicextensions.exception.DynamicExtensionsApplicationException;
import edu.common.dynamicextensions.exception.DynamicExtensionsSystemException;

/**
 * The methods only work on domain object interfaces and also return domain object interfaces or
 * collection of domain object interfaces.
 * 
 * These methods may throw  DynamicExtensionsApplicationException or DynamicExtensionsSystemException.
 * 
 * DynamicExtensionsApplicationException are application specific exceptions and system can recover from those 
 * exceptions.
 * 
 * DynamicExtensionsSystemException are system specific exceptions and system can not recover from those
 * exception.An error page should be displayed in the case.
 * @author sujay_narkar
 *
 */
public interface EntityManagerInterface
{

	/**
	 * Sets an instance of entity manager.A mock entity manager can be set using this method.
	 * @param entityManager
	 */
	void setInstance(EntityManagerInterface entityManagerInterface);

	/**
	 * Saves the entity into the database.Also prepares the dynamic tables and associations 
	 * between those tables using the metadata information in the entity object.
	 * EntityInterface can be obtained from DomainObjectFactory.
	 * @param entityInterface
	 * @return
	 * @throws DynamicExtensionsSystemException
	 * @throws DynamicExtensionsApplicationException
	 */
	EntityInterface persistEntity(EntityInterface entityInterface)
			throws DynamicExtensionsSystemException, DynamicExtensionsApplicationException;

	/**
	 * This method creates an entity group.The entities in the group are also saved.
	 * @param entityGroupInterface entity group to be saved.
	 * @return entityGroupInterface Saved  entity group. 
	 * @throws DynamicExtensionsSystemException
	 * @throws DynamicExtensionsApplicationException
	 */
	EntityGroupInterface createEntityGroup(EntityGroupInterface entityGroupInterface)
			throws DynamicExtensionsSystemException, DynamicExtensionsApplicationException;

	/**
	 * This method returns the EntityGroupInterface given the short name for the 
	 * entity.
	 * @param entityGroupShortName short name for entity group
	 * @return entityGroupInterface entity group interface 
	 * @throws DynamicExtensionsSystemException
	 */
	EntityGroupInterface getEntityGroupByShortName(String entityGroupShortName)
			throws DynamicExtensionsSystemException;

	/**
	 * Returns a collection of association objects given the source entity id and
	 * target entity id.
	 * @param sourceEntityId
	 * @param targetEntityId
	 * @return
	 * @throws DynamicExtensionsSystemException
	 * @throws DynamicExtensionsApplicationException
	 */
	Collection<AssociationInterface> getAssociations(Long sourceEntityId, Long targetEntityId)
			throws DynamicExtensionsSystemException, DynamicExtensionsApplicationException;

	/**
	 * Returns an entity object given the entity name; 
	 * @param entityName
	 * @return
	 * @throws DynamicExtensionsSystemException
	 * @throws DynamicExtensionsApplicationException
	 */
	EntityInterface getEntityByName(String entityName) throws DynamicExtensionsSystemException,
			DynamicExtensionsApplicationException;

	/**
	 * Returns an attribute given the entity name and attribute name.
	 * @param entityName
	 * @param attributeName
	 * @return
	 * @throws DynamicExtensionsSystemException
	 * @throws DynamicExtensionsApplicationException
	 */
	AttributeInterface getAttribute(String entityName, String attributeName)
			throws DynamicExtensionsSystemException, DynamicExtensionsApplicationException;

	/**
	 * Returns an association object given the entity name and source role name.
	 * @param entityName
	 * @param sourceRoleName
	 * @return
	 * @throws DynamicExtensionsSystemException
	 * @throws DynamicExtensionsApplicationException
	 */
	AssociationInterface getAssociation(String entityName, String sourceRoleName)
			throws DynamicExtensionsSystemException, DynamicExtensionsApplicationException;

	/**
	 * Returns a collection of entities given the entity concept code.
	 * @param entityConceptCode
	 * @return
	 * @throws DynamicExtensionsSystemException
	 * @throws DynamicExtensionsApplicationException
	 */
	Collection<EntityInterface> getEntitiesByConceptCode(String entityConceptCode)
			throws DynamicExtensionsSystemException, DynamicExtensionsApplicationException;

	/**
	 * Returns all entities in the whole system
	 * @return
	 * @throws DynamicExtensionsSystemException
	 * @throws DynamicExtensionsApplicationException
	 */
	Collection<EntityInterface> getAllEntities() throws DynamicExtensionsSystemException,
			DynamicExtensionsApplicationException;

	/**
	 * Returns a single  entity for given identifier
	 * @return
	 * @throws DynamicExtensionsSystemException
	 * @throws DynamicExtensionsApplicationException
	 */
	public EntityInterface getEntityByIdentifier(String identifier)
			throws DynamicExtensionsSystemException, DynamicExtensionsApplicationException;

	/**
	 * Returns a collection of entities having attribute with the given name  
	 * @param attributeName
	 * @return
	 * @throws DynamicExtensionsSystemException
	 * @throws DynamicExtensionsApplicationException
	 */
	Collection<EntityInterface> getEntitiesByAttributeName(String attributeName)
			throws DynamicExtensionsSystemException, DynamicExtensionsApplicationException;

	/**
	 * Returns all Containers in the whole system
	 * @return Collection of ContainerInterface
	 * @throws DynamicExtensionsSystemException
	 * @throws DynamicExtensionsApplicationException
	 */
	Collection<ContainerInterface> getAllContainers() throws DynamicExtensionsSystemException,
			DynamicExtensionsApplicationException;

	/**
	 * Returns a collection of entity objects given the entity description
	 * @param entityDescription
	 * @return
	 * @throws DynamicExtensionsSystemException
	 * @throws DynamicExtensionsApplicationException
	 */
	Collection<EntityInterface> getEntityByDescription(String entityDescription)
			throws DynamicExtensionsSystemException, DynamicExtensionsApplicationException;

	/**
	 * Returns a collection of Entity objects given the attribute description
	 * @param attributeDescription
	 * @return
	 * @throws DynamicExtensionsSystemException
	 * @throws DynamicExtensionsApplicationException
	 */
	Collection<EntityInterface> getEntitiesByAttributeDescription(String attributeDescription)
			throws DynamicExtensionsSystemException, DynamicExtensionsApplicationException;

	/**
	 * Returns a collection of entity objects given the entity concept name.
	 * @param entityConceptName
	 * @return
	 * @throws DynamicExtensionsSystemException
	 * @throws DynamicExtensionsApplicationException
	 */
	Collection<EntityInterface> getEntitiesByConceptName(String entityConceptName)
			throws DynamicExtensionsSystemException, DynamicExtensionsApplicationException;

	/**
	 * Returns a collection of entities given attribute concept code. 
	 * @param attributeConceptCode
	 * @return
	 * @throws DynamicExtensionsSystemException
	 * @throws DynamicExtensionsApplicationException
	 */
	Collection<EntityInterface> getEntitiesByAttributeConceptCode(String attributeConceptCode)
			throws DynamicExtensionsSystemException, DynamicExtensionsApplicationException;

	/**
	 * Returns a collection of entities given the attribute concept name
	 * @param attributeConceptname
	 * @return 
	 * @throws DynamicExtensionsSystemException
	 * @throws DynamicExtensionsApplicationException
	 */
	Collection<EntityInterface> getEntitiesByAttributeConceptName(String attributeConceptName)
			throws DynamicExtensionsSystemException, DynamicExtensionsApplicationException;

	/**
	 * Returns a collection of entity objects given the entity object with specific criteria. 
	 * @param entityInterface
	 * @return
	 */
	Collection<EntityInterface> findEntity(EntityInterface entityInterface);

	/**
	 * This method is used to save the container into the database.
	 * @param containerInterface container to save
	 * @return ContainerInterface container Interface that is saved.
	 * @throws DynamicExtensionsSystemException Thrown if for any reason operation can not be completed.
	 * @throws DynamicExtensionsApplicationException Thrown if the entity name already exists.
	 * @throws DynamicExtensionsSystemException 
	 */
	ContainerInterface persistContainer(ContainerInterface containerInterface)
			throws DynamicExtensionsApplicationException, DynamicExtensionsSystemException;

	/**
	 * This method inserts one record for the entity.
	 */
	Long insertData(EntityInterface entity, Map<AbstractAttributeInterface, String> dataValue)
			throws DynamicExtensionsApplicationException, DynamicExtensionsSystemException;

	/**
	 * Returns a particular record for the given recordId of the given entityId
	 * @param entityId
	 * @param recordId
	 * @return Map key - attribute name 
	 *             value - attribute value  
	 */
	Map<String, String> getRecordById(EntityInterface entity, Long recordId)
			throws DynamicExtensionsSystemException, DynamicExtensionsApplicationException;

	/**
	 * This method updates the existing record for the given entity.
	 * @param entity       Entity for which record needs to be updated
	 * @param dataValue    map that contains  name of the attribute whose value is changed and its new value
	 *                     If it is multiselect attribute then valu should be List<string>
	 * @param recordId     Id of the record
	 * @return true if success
	 * @throws DynamicExtensionsApplicationException
	 * @throws DynamicExtensionsSystemException
	 */
	boolean editData(EntityInterface entity, Map dataValue, Long recordId)
			throws DynamicExtensionsApplicationException, DynamicExtensionsSystemException;

}
