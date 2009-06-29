
package edu.wustl.cab2b.common.cache;

import java.io.Serializable;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.log4j.Logger;

import edu.common.dynamicextensions.domaininterface.AbstractEntityInterface;
import edu.common.dynamicextensions.domaininterface.AssociationInterface;
import edu.common.dynamicextensions.domaininterface.AttributeInterface;
import edu.common.dynamicextensions.domaininterface.CategoryAssociationInterface;
import edu.common.dynamicextensions.domaininterface.CategoryAttributeInterface;
import edu.common.dynamicextensions.domaininterface.CategoryEntityInterface;
import edu.common.dynamicextensions.domaininterface.CategoryInterface;
import edu.common.dynamicextensions.domaininterface.EntityGroupInterface;
import edu.common.dynamicextensions.domaininterface.EntityInterface;
import edu.common.dynamicextensions.domaininterface.PermissibleValueInterface;
import edu.common.dynamicextensions.domaininterface.userinterface.ContainerInterface;
import edu.common.dynamicextensions.domaininterface.userinterface.ControlInterface;
import edu.wustl.cab2b.common.beans.MatchedClass;
import edu.wustl.cab2b.common.beans.MatchedClassEntry;
import edu.wustl.cab2b.common.exception.RuntimeException;
import edu.wustl.cab2b.common.util.Utility;
import edu.wustl.cab2b.server.util.DynamicExtensionUtility;
import edu.wustl.common.querysuite.metadata.category.Category;

/**
 * This is an abstract class for caching metadata. 
 * It holds the data needed structures, methods to populate those and public methods to access cache.
 * 
 * @author Chandrakant Talele
 * @author gautam_shetty
 * @author Rahul Ner
 * @author pavan_kalantri
 */
public abstract class AbstractEntityCache implements IEntityCache, Serializable
{

	private static final long serialVersionUID = 1234567890L;

	private static final Logger logger = edu.wustl.common.util.logger.Logger
			.getLogger(AbstractEntityCache.class);

	/**
	 * List of all the categories loaded in caB2B local database.  
	 */
	protected List<Category> categories = new ArrayList<Category>(0);

	/**
	 * Set of all the entity groups loaded as metadata in caB2B.
	 */
	private Set<EntityGroupInterface> cab2bEntityGroups = new HashSet<EntityGroupInterface>();

	/**
	 * The EntityCache object. Needed for singleton
	 */
	protected static AbstractEntityCache entityCache = null;

	/**
	 * Map with KEY as dynamic extension Entity's identifier and Value as Entity object
	 */
	protected Map<Long, EntityInterface> idVsEntity = new HashMap<Long, EntityInterface>();

	/**
	 * Map with KEY as dynamic extension Association's identifier and Value as Association object
	 */
	protected Map<Long, AssociationInterface> idVsAssociation = new HashMap<Long, AssociationInterface>();

	/**
	 * Map with KEY as dynamic extension Attribute's identifier and Value as Attribute object
	 */
	protected Map<Long, AttributeInterface> idVsAttribute = new HashMap<Long, AttributeInterface>();

	/**
	 * This map holds all the original association. Associations which are
	 * replicated by cab2b are not present in this map Key : String to identify
	 * a parent association uniquely.Generated by
	 * {InheritanceUtil#generateUniqueId(AssociationInterface)} Value : Original
	 * association for given string identifier
	 */
	protected Map<String, AssociationInterface> originalAssociations = new HashMap<String, AssociationInterface>();

	/**
	 * Map with KEY as a permissible value (PV) and VALUE as its Entity. This is
	 * needed because there is no back pointer from PV to Entity
	 */
	protected Map<PermissibleValueInterface, EntityInterface> permissibleValueVsEntity = 
			new HashMap<PermissibleValueInterface, EntityInterface>();

	/**
	 * Set of all the DyanamicExtensions categories loaded in the database. 
	 */
	protected Set<CategoryInterface> deCategories = new HashSet<CategoryInterface>();

	/**
	 *  Map with KEY as dynamic extension Containers identifier and Value as Container object.
	 */
	protected Map<Long, ContainerInterface> idVscontainers = new HashMap<Long, ContainerInterface>();

	/**
	 * Map with KEY as dynamic extension CategoryAttribute's identifier and Value as CategoryAttribute object
	 */
	protected Map<Long, CategoryAttributeInterface> idVsCategoryAttribute = 
			new HashMap<Long, CategoryAttributeInterface>();

	/**
	 * Map with KEY as dynamic extension CategoryEntity's's identifier and Value as CategoryEntity object
	 */
	protected Map<Long, CategoryEntityInterface> idVsCaegoryEntity = new HashMap<Long, CategoryEntityInterface>();

	/**
	 * Map with KEY as dynamic extension CategoryAssociations's identifier and Value as CategoryAssociations object
	 */
	protected Map<Long, CategoryAssociationInterface> idVsCaegoryAssociation = 
			new HashMap<Long, CategoryAssociationInterface>();

	/**
	 * Map with KEY as dynamic extension Controls's identifier and Value as Control object
	 */
	protected Map<Long, ControlInterface> idVsControl = new HashMap<Long, ControlInterface>();

	/**
	 * This method gives the singleton cache object. If cache is not present then it 
	 * throws {@link UnsupportedOperationException}
	 * @return The singleton cache object.
	 */
	public static AbstractEntityCache getCache()
	{
		if (entityCache == null)
		{
			throw new UnsupportedOperationException("Cache not present.");
		}
		return entityCache;
	}

	/**
	 * Private default constructor. To restrict the user from instantiating
	 * explicitly.
	 * @throws RemoteException 
	 */
	protected AbstractEntityCache()
	{
		refreshCache();
	}

	/**
	 * Refresh the entity cache.
	 * @throws RemoteException 
	 */
	public final void refreshCache()
	{
		logger.info("Initializing cache, this may take few minutes...");
		clearCache();
		Collection<EntityGroupInterface> entityGroups = null;
		List<CategoryInterface> categoryList = null;
		try
		{
			entityGroups = getSystemGeneratedEntityGroups();
			categoryList = DynamicExtensionUtility.getAllCategories();
		}
		catch (RemoteException e)
		{
			logger.error("Error while collecting caB2B entity groups. Error: " + e.getMessage());
		}

		createCache(categoryList, entityGroups);

		logger.info("Initializing cache DONE");
	}

	/**
	 * It will clear all the in memory maps 
	 */
	private void clearCache()
	{
		cab2bEntityGroups.clear();
		deCategories.clear();
		idVscontainers.clear();
		idVsAssociation.clear();
		idVsAttribute.clear();
		idVsCaegoryEntity.clear();
		idVsCategoryAttribute.clear();
		idVsCaegoryAssociation.clear();
		idVsControl.clear();
		idVsEntity.clear();
	}

	/**
	 * Initializes the data structures by processing container & entity group one by one at a time.
	 * @param categoryList list of containers to be cached.
	 * @param entityGroups list of system generated entity groups to be cached.
	 */
	private void createCache(List<CategoryInterface> categoryList,
			Collection<EntityGroupInterface> entityGroups)
	{
		for (EntityGroupInterface entityGroup : entityGroups)
		{
			cab2bEntityGroups.add(entityGroup);
			for (EntityInterface entity : entityGroup.getEntityCollection())
			{
				addEntityToCache(entity);
			}
		}
		for (CategoryInterface category : categoryList)
		{
			deCategories.add(category);
			createCategoryEntityCach(category.getRootCategoryElement());
		}

	}

	/**
	 * It will add the categoryEntity & there containers to the cache.
	 * It will then recursively call the same method for the child category Entities.
	 * @param categoryEntity
	 */
	private void createCategoryEntityCach(CategoryEntityInterface categoryEntity)
	{
		for (Object container : categoryEntity.getContainerCollection())
		{
			ContainerInterface containerInterface = (ContainerInterface) container;
			addContainerToCache(containerInterface);
		}
		for (CategoryAssociationInterface categoryAssociation : categoryEntity
				.getCategoryAssociationCollection())
		{
			CategoryEntityInterface targetCategoryEntity = categoryAssociation
					.getTargetCategoryEntity();
			createCategoryEntityCach(targetCategoryEntity);

		}
	}

	/**
	 * It will add the given container to the cache & also update the cache
	 * for its controls and AbstractEntity
	 * @param container
	 */
	private void addContainerToCache(ContainerInterface container)
	{
		idVscontainers.put(container.getId(), container);
		createControlCache(container.getControlCollection());
		addAbstractEntityToCache(container.getAbstractEntity());
	}

	/**
	 * Adds all controls into cache.
	 * @param controlCollection collection of control objects which are  to be cached.
	 */
	private void createControlCache(Collection<ControlInterface> controlCollection)
	{
		for (ControlInterface control : controlCollection)
		{
			idVsControl.put(control.getId(), control);
		}
	}

	/**
	 * Adds abstract Entity (which can be 'CategoryEnity' or 'Entity') into cache.
	 * @param abstractEntity which should be cached.
	 * @param entityGroupsSet in which the entityGroup of the abstractEntity is cached. 
	 * @param categorySet in which the category of the abstractEntity is cached.
	 */
	private void addAbstractEntityToCache(AbstractEntityInterface abstractEntity)
	{
		if (abstractEntity instanceof CategoryEntityInterface)
		{
			CategoryEntityInterface categoryEntity = (CategoryEntityInterface) abstractEntity;
			addCategoryEntityToCache(categoryEntity);
		}
		else
		{
			EntityInterface entity = (EntityInterface) abstractEntity;
			createEntityCache(entity);

		}
	}

	/**
	 * Adds CategoryEnity into cache.
	 * @param categoryEntity which should be cached.
	 */
	private void addCategoryEntityToCache(CategoryEntityInterface categoryEntity)
	{
		idVsCaegoryEntity.put(categoryEntity.getId(), categoryEntity);
		createCategoryAttributeCache(categoryEntity);
		createCategoryAssociationCache(categoryEntity);
	}

	/**
	 * It will add all the categoryAssociations of the categoryEntity in the cache. 
	 * @param categoryEntity whose all categoryAssociations should be cached.
	 */
	private void createCategoryAssociationCache(CategoryEntityInterface categoryEntity)
	{
		for (CategoryAssociationInterface assocition : categoryEntity
				.getCategoryAssociationCollection())
		{
			idVsCaegoryAssociation.put(assocition.getId(), assocition);
		}

	}

	/**
	 * It will add all the categoryAttributes of the categoryEntity in the cache.
	 * @param categoryEntity whose all categoryAttributes should be cached.
	 */
	private void createCategoryAttributeCache(CategoryEntityInterface categoryEntity)
	{
		for (CategoryAttributeInterface categoryAttribute : categoryEntity
				.getCategoryAttributeCollection())
		{
			idVsCategoryAttribute.put(categoryAttribute.getId(), categoryAttribute);
		}

	}

	/**
	* Adds all attribute of given entity into cache
	* @param entity Entity to process
	*/
	private void createAttributeCache(EntityInterface entity)
	{
		for (AttributeInterface attribute : entity.getAttributeCollection())
		{
			idVsAttribute.put(attribute.getId(), attribute);
		}
	}

	/**
	 * Adds all associations of given entity into cache
	 * @param entity Entity to process
	 */
	private void createAssociationCache(EntityInterface entity)
	{
		for (AssociationInterface association : entity.getAssociationCollection())
		{
			idVsAssociation.put(association.getId(), association);
			if (!Utility.isInherited(association))
			{
				originalAssociations.put(Utility.generateUniqueId(association), association);
			}
		}
	}

	/**
	 * Adds permissible values of all the attributes of given entity into cache
	 * @param entity Entity whose permissible values are to be processed
	 */
	private void createPermissibleValueCache(EntityInterface entity)
	{
		for (AttributeInterface attribute : entity.getAttributeCollection())
		{
			for (PermissibleValueInterface value : Utility.getPermissibleValues(attribute))
			{
				permissibleValueVsEntity.put(value, entity);
			}
		}
	}

	
	/* (non-Javadoc)
	 * @see edu.wustl.cab2b.common.cache.IEntityCache#getEntityOnEntityParameters(java.util.Collection)
	 */
	public MatchedClass getEntityOnEntityParameters(
			Collection<EntityInterface> patternEntityCollection)
	{
		MatchedClass matchedClass = new MatchedClass();
		for (EntityInterface cachedEntity : idVsEntity.values())
		{
			for (EntityInterface patternEntity : patternEntityCollection)
			{
				MatchedClassEntry matchedClassEntry = CompareUtil.compare(cachedEntity,
						patternEntity);
				if (matchedClassEntry != null)
				{
					matchedClass.addEntity(cachedEntity);
					matchedClass.addMatchedClassEntry(matchedClassEntry);
				}
			}
		}
		return matchedClass;
	}

	/**
	 * Returns the Entity objects whose Attribute fields match with the
	 * respective not null fields in the passed Attribute object.
	 * 
	 * @param entity The entity object.
	 * @return the Entity objects whose Attribute fields match with the
	 *         respective not null fields in the passed Attribute object.
	 */
	public MatchedClass getEntityOnAttributeParameters(
			Collection<AttributeInterface> patternAttributeCollection)
	{
		MatchedClass matchedClass = new MatchedClass();
		for (EntityInterface entity : idVsEntity.values())
		{
			for (AttributeInterface cachedAttribute : entity.getAttributeCollection())
			{
				for (AttributeInterface patternAttribute : patternAttributeCollection)
				{
					MatchedClassEntry matchedClassEntry = CompareUtil.compare(cachedAttribute,
							patternAttribute);
					if (matchedClassEntry != null)
					{
						matchedClass.addMatchedClassEntry(matchedClassEntry);
						matchedClass.addAttribute(cachedAttribute);
						matchedClass.addEntity(cachedAttribute.getEntity());
					}
				}
			}
		}
		return matchedClass;
	}

	/**
	 * Returns the Entity objects whose Permissible value fields match with the
	 * respective not null fields in the passed Permissible value object.
	 * 
	 * @param entity The entity object.
	 * @return the Entity objects whose Permissible value fields match with the
	 *         respective not null fields in the passed Permissible value
	 *         object.
	 */
	public MatchedClass getEntityOnPermissibleValueParameters(
			Collection<PermissibleValueInterface> patternPermissibleValueCollection)
	{
		MatchedClass matchedClass = new MatchedClass();
		for (PermissibleValueInterface cachedPermissibleValue : permissibleValueVsEntity.keySet())
		{
			for (PermissibleValueInterface patternPermissibleValue : patternPermissibleValueCollection)
			{
				EntityInterface cachedEntity = permissibleValueVsEntity.get(cachedPermissibleValue);
				MatchedClassEntry matchedClassEntry = CompareUtil.compare(cachedPermissibleValue,
						patternPermissibleValue, cachedEntity);
				if (matchedClassEntry != null)
				{
					matchedClass.addEntity(cachedEntity);
					matchedClass.addMatchedClassEntry(matchedClassEntry);
				}
			}
		}
		return matchedClass;

	}

	
	/**
	 * It will return the EntityGroup With the given id if it present in cache, else will throw the 
	 * exception.
	 * @param identifier
	 * @return
	 */
	public EntityGroupInterface getEntityGroupById(Long identifier)
	{
		EntityGroupInterface entityGroup = null;
		for (EntityGroupInterface group : cab2bEntityGroups)
		{
			if (group.getId().equals(identifier))
			{
				entityGroup = group;
				break;
			}
		}
		if (entityGroup == null)
		{
			throw new RuntimeException("Entity Group with given id is not present in cache : "
					+ identifier);
		}
		return entityGroup;

	}

	/**
	 * Returns the Entity for given Identifier
	 * 
	 * @param identifier Id of the entity
	 * @return Actual Entity for given id.
	 */
	public EntityInterface getEntityById(Long identifier)
	{
		EntityInterface entity = idVsEntity.get(identifier);
		if (entity == null)
		{
			throw new RuntimeException("Entity with given id is not present in cache : "
					+ identifier);
		}
		return entity;
	}

	/**
	 * Checks if entity with given id is present in cache.
	 * 
	 * @param identifier the entity id
	 * @return <code>true</code> - if entity with given id is present in
	 *         cache; <code>false</code> otherwise.
	 */
	public boolean isEntityPresent(Long identifier)
	{
		return idVsEntity.containsKey(identifier);
	}

	/**
	 * Returns the Attribute for given Identifier
	 * 
	 * @param identifier Id of the Attribute
	 * @return Actual Attribute for given id.
	 */
	public AttributeInterface getAttributeById(Long identifier)
	{
		AttributeInterface attribute = idVsAttribute.get(identifier);
		if (attribute == null)
		{
			throw new RuntimeException("Attribute with given id is not present in cache : "
					+ identifier);
		}
		return attribute;
	}

	/**
	 * Returns the Association for given Identifier
	 * 
	 * @param identifier Id of the Association
	 * @return Actual Association for given id.
	 */
	public AssociationInterface getAssociationById(Long identifier)
	{
		AssociationInterface association = idVsAssociation.get(identifier);
		if (association == null)
		{
			throw new RuntimeException("Association with given id is not present in cache : "
					+ identifier);
		}
		return association;
	}

	/**
	 * Returns the Association for given string. 
	 * Passed string MUST be of format specified in {@link Utility#generateUniqueId(AssociationInterface)}
	 * @param uniqueStringIdentifier unique String Identifier
	 * @return Actual Association for given string identifier.
	 */
	public AssociationInterface getAssociationByUniqueStringIdentifier(String uniqueStringIdentifier)
	{
		AssociationInterface association = originalAssociations.get(uniqueStringIdentifier);
		if (association == null)
		{
			throw new RuntimeException(
					"Association with given uniqueStringIdentifier is not present in cache : "
							+ uniqueStringIdentifier);
		}
		return association;
	}

	/**
	 * It will add the Given Entity in the cache .
	 * @param entity
	 */
	private void createEntityCache(EntityInterface entity)
	{
		idVsEntity.put(entity.getId(), entity);
		createAttributeCache(entity);
		createAssociationCache(entity);
		createPermissibleValueCache(entity);
	}

	/**
	 * It will add the given Entity to the cache & will also update the corresponding 
	 * controls containers & attributes.
	 * @param entity
	 */
	public void addEntityToCache(EntityInterface entity)
	{
		if (entity.getContainerCollection() == null || entity.getContainerCollection().isEmpty())
		{
			createEntityCache(entity);
		}
		else
		{
			for (Object container : entity.getContainerCollection())
			{
				ContainerInterface containerInterface = (ContainerInterface) container;
				addContainerToCache(containerInterface);
			}
		}
	}

	/**
	 * Returns all the entity groups registered with the instance of cache.
	 * @return Returns all the registered entity groups
	 */
	public Collection<EntityGroupInterface> getEntityGroups()
	{
		return cab2bEntityGroups;
	}

	/**
	 * This method returns the entity group of given name from cache.
	 * @param name name of the entity group
	 * @return entity group
	 */
	public EntityGroupInterface getEntityGroupByName(String name)
	{
		EntityGroupInterface entityGroup = null;
		for (EntityGroupInterface group : cab2bEntityGroups)
		{
			if (group.getName().equals(name))
			{
				entityGroup = group;
			}
		}
		return entityGroup;
	}

	/**
	 * It will return all the categories present in the Database .
	 * @return Collection of the CategoryInterface in the database.
	 */
	public Collection<CategoryInterface> getAllCategories()
	{
		return deCategories;
	}

	/**
	 * It will return the Category with the id as given identifier in the parameter.
	 * @param identifier.
	 * @return category with given identifier.
	 */
	public CategoryInterface getCategoryById(Long identifier)
	{
		CategoryInterface category = null;
		for (CategoryInterface deCategory : deCategories)
		{
			if (deCategory.getId().equals(identifier))
			{
				category = deCategory;
			}

		}
		if (category == null)
		{
			throw new RuntimeException("Category with given id is not present in cache : "
					+ identifier);
		}
		return category;
	}

	/**
	 * It will return the CategoryAttribute with the id as given identifier in the parameter.
	 * @param identifier
	 * @return categoryAttribute with given identifier
	 */
	public CategoryAttributeInterface getCategoryAttributeById(Long identifier)
	{
		CategoryAttributeInterface categoryAttribute = idVsCategoryAttribute.get(identifier);
		if (categoryAttribute == null)
		{
			throw new RuntimeException(
					"Category Attribute with given id is not present in cache : " + identifier);
		}
		return categoryAttribute;
	}

	/**
	 * It will return the CategoryAssociation with the id as given identifier in the parameter.
	 * @param identifier
	 * @return CategoryAssociation with given identifier
	 */
	public CategoryAssociationInterface getCategoryAssociationById(Long identifier)
	{
		CategoryAssociationInterface categoryAssociation = idVsCaegoryAssociation.get(identifier);
		if (categoryAssociation == null)
		{
			throw new RuntimeException(
					"Category Association with given id is not present in cache : " + identifier);
		}
		return categoryAssociation;

	}

	/**
	 * It will return the CategoryEntity with the id as given identifier in the parameter.
	 * @param identifier
	 * @return categoryEntity with given identifier
	 */
	public CategoryEntityInterface getCategoryEntityById(Long identifier)
	{
		CategoryEntityInterface categoryEntity = idVsCaegoryEntity.get(identifier);
		if (categoryEntity == null)
		{
			throw new RuntimeException("Category Entity with given id is not present in cache : "
					+ identifier);
		}
		return categoryEntity;

	}

	/**
	 * It will return the Container with the id as given identifier in the parameter.
	 * @param identifier
	 * @return Container with given identifier
	 */
	public ContainerInterface getContainerById(Long identifier)
	{
		ContainerInterface container = null;
		container = idVscontainers.get(identifier);
		if (container == null)
		{
			throw new RuntimeException("container with given id is not present in cache : "
					+ identifier);
		}
		return container;

	}

	/**
	 * It will return the Control with the id as given identifier in the parameter.
	 * @param identifier
	 * @return Control with given identifier
	 */
	public ControlInterface getControlById(Long identifier)
	{
		ControlInterface control = idVsControl.get(identifier);
		if (control == null)
		{
			throw new RuntimeException("Control with given id is not present in cache : "
					+ identifier);
		}
		return control;
	}

	/**
	 * This method returns all the entity groups which are to be cached. 
	 * These will typically be the metadata entitygroups present is local caB2B database. 
	 * It should not return entitygroups for data list or experiment
	 * @return Returns the entity groups
	 * @throws RemoteException 
	 */
	protected abstract Collection<EntityGroupInterface> getSystemGeneratedEntityGroups()
			throws RemoteException;
}