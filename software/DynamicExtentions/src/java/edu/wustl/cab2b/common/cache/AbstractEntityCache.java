
package edu.wustl.cab2b.common.cache;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

import org.apache.log4j.Logger;

import edu.common.dynamicextensions.domaininterface.AbstractEntityInterface;
import edu.common.dynamicextensions.domaininterface.AssociationInterface;
import edu.common.dynamicextensions.domaininterface.AttributeInterface;
import edu.common.dynamicextensions.domaininterface.AttributeTypeInformationInterface;
import edu.common.dynamicextensions.domaininterface.BaseAbstractAttributeInterface;
import edu.common.dynamicextensions.domaininterface.CategoryAssociationInterface;
import edu.common.dynamicextensions.domaininterface.CategoryAttributeInterface;
import edu.common.dynamicextensions.domaininterface.CategoryEntityInterface;
import edu.common.dynamicextensions.domaininterface.CategoryInterface;
import edu.common.dynamicextensions.domaininterface.EntityGroupInterface;
import edu.common.dynamicextensions.domaininterface.EntityInterface;
import edu.common.dynamicextensions.domaininterface.PermissibleValueInterface;
import edu.common.dynamicextensions.domaininterface.UserDefinedDEInterface;
import edu.common.dynamicextensions.domaininterface.userinterface.ContainerInterface;
import edu.common.dynamicextensions.domaininterface.userinterface.ControlInterface;
import edu.common.dynamicextensions.entitymanager.AbstractMetadataManager;
import edu.common.dynamicextensions.entitymanager.CategoryManager;
import edu.common.dynamicextensions.entitymanager.EntityManager;
import edu.common.dynamicextensions.exception.DynamicExtensionsApplicationException;
import edu.common.dynamicextensions.exception.DynamicExtensionsCacheException;
import edu.common.dynamicextensions.exception.DynamicExtensionsSystemException;
import edu.common.dynamicextensions.util.DynamicExtensionsUtility;
import edu.wustl.cab2b.common.beans.MatchedClass;
import edu.wustl.cab2b.common.beans.MatchedClassEntry;
import edu.wustl.cab2b.common.exception.RuntimeException;
import edu.wustl.cab2b.common.util.Utility;
import edu.wustl.cab2b.server.util.DynamicExtensionUtility;
import edu.wustl.common.querysuite.metadata.category.Category;
import edu.wustl.dao.HibernateDAO;
import edu.wustl.dao.exception.DAOException;

/**
 * This is an abstract class for caching metadata.
 * It holds the data needed structures, methods to populate those and public methods to access cache.
 *
 * @author Chandrakant Talele
 * @author gautam_shetty
 * @author Rahul Ner
 * @author pavan_kalantri
 */
public abstract class AbstractEntityCache implements IEntityCache
{

	private static final long serialVersionUID = 1234567890L;

	public static boolean isCacheReady = true;

	private static final Logger LOGGER = edu.wustl.common.util.logger.Logger
			.getLogger(AbstractEntityCache.class);

	/**
	 * List of all the categories loaded in caB2B local database.
	 */
	protected List<Category> categories = new ArrayList<Category>(0);

	/**
	 * Set of all the entity groups loaded as metadata in caB2B.
	 */
	private final Set<EntityGroupInterface> cab2bEntityGroups = new HashSet<EntityGroupInterface>();

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
	protected Map<PermissibleValueInterface, EntityInterface> permissibleValueVsEntity = new HashMap<PermissibleValueInterface, EntityInterface>();

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
	protected Map<Long, CategoryAttributeInterface> idVsCategoryAttribute = new HashMap<Long, CategoryAttributeInterface>();

	/**
	 * Map with KEY as dynamic extension CategoryEntity's's identifier and Value as CategoryEntity object
	 */
	protected Map<Long, CategoryEntityInterface> idVsCaegoryEntity = new HashMap<Long, CategoryEntityInterface>();

	/**
	 * Map with KEY as dynamic extension CategoryAssociations's identifier and Value as CategoryAssociations object
	 */
	protected Map<Long, CategoryAssociationInterface> idVsCaegoryAssociation = new HashMap<Long, CategoryAssociationInterface>();

	/**
	 * Map with KEY as dynamic extension Controls's identifier and Value as Control object
	 */
	protected Map<Long, ControlInterface> idVsControl = new HashMap<Long, ControlInterface>();

	/**
	 * This set contains all the categories which are in opened at this instance in Edit
	 * mode by any user.
	 */
	protected Set<CategoryInterface> categoriesInUse = new HashSet<CategoryInterface>();

	/**
	 *  This counter is used for creating the temporary directories in case of create
	 *  category task by more than one user at a time.
	 */
	protected long catFileNameCounter = 1L;

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
	 */
	protected AbstractEntityCache()
	{
		refreshCache();
	}

	/**
	 * Refresh the entity cache.
	 */
	public final synchronized void refreshCache()
	{

		LOGGER.info("Initializing cache, this may take few minutes...");
		clearCache();

		HibernateDAO hibernateDAO = null;
		Collection<EntityGroupInterface> entityGroups = null;
		List<CategoryInterface> categoryList = new ArrayList<CategoryInterface>();
		try
		{
			hibernateDAO = DynamicExtensionsUtility.getHibernateDAO();
			entityGroups = DynamicExtensionUtility.getSystemGeneratedEntityGroups(hibernateDAO);
			createCache(categoryList, entityGroups);
		}
		catch (final DAOException e)
		{
			LOGGER.error("Error while Creating EntityCache. Error: " + e.getMessage());
			throw new RuntimeException("Exception encountered while creating EntityCache!!", e);
		}
		catch (DynamicExtensionsSystemException e)
		{
			LOGGER.error("Error while Creating EntityCache. Error: " + e.getMessage());
			throw new RuntimeException("Exception encountered while creating EntityCache!!", e);
		}
		finally
		{
			try
			{
				DynamicExtensionsUtility.closeDAO(hibernateDAO);
			}
			catch (final DynamicExtensionsSystemException e)
			{
				LOGGER.error("Exception encountered while closing session In EntityCache."
						+ e.getMessage());
				throw new RuntimeException(
						"Exception encountered while closing session In EntityCache.", e);
			}

		}
		LOGGER.info("Initializing cache DONE");
	}

	/**
	 * Initializes the data structures by processing container & entity group one by one at a time.
	 * @param categoryList list of containers to be cached.
	 * @param entityGroups list of system generated entity groups to be cached.
	 */
	private void createCache(final List<CategoryInterface> categoryList,
			final Collection<EntityGroupInterface> entityGroups)
	{
		for (final EntityGroupInterface entityGroup : entityGroups)
		{
			cab2bEntityGroups.add(entityGroup);
			for (final EntityInterface entity : entityGroup.getEntityCollection())
			{
				addEntityToCache(entity);
			}
		}
		for (final CategoryInterface category : categoryList)
		{
			deCategories.add(category);
			createCategoryEntityCach(category.getRootCategoryElement());
		}

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
	 * @param categoryList
	 */
	public synchronized void createCategoryCache(final CategoryInterface category)
	{
		deCategories.add(category);
		CategoryEntityInterface rootCategory = category.getRootCategoryElement();
		if (rootCategory != null)
		{
			createCategoryEntityCach(rootCategory);
		}
	}

	/**
	 * @param entityGroups
	 */
	public synchronized void createEntityGroupCache(final EntityGroupInterface entityGroup)
	{
		cab2bEntityGroups.add(entityGroup);
		if (entityGroup.getEntityCollection() != null)
		{
			for (final EntityInterface entity : entityGroup.getEntityCollection())
			{
				addEntityToCache(entity);
			}
		}
	}

	/**
	 * It will add the categoryEntity & there containers to the cache.
	 * It will then recursively call the same method for the child category Entities.
	 * @param categoryEntity
	 */
	private void createCategoryEntityCach(final CategoryEntityInterface categoryEntity)
	{
		if (categoryEntity.getContainerCollection() != null
				&& !categoryEntity.getContainerCollection().isEmpty())
		{
			for (final Object container : categoryEntity.getContainerCollection())
			{
				final ContainerInterface containerInterface = (ContainerInterface) container;
				addContainerToCache(containerInterface);
			}
			for (final CategoryAssociationInterface categoryAssociation : categoryEntity
					.getCategoryAssociationCollection())
			{
				final CategoryEntityInterface targetCategoryEntity = categoryAssociation
						.getTargetCategoryEntity();
				createCategoryEntityCach(targetCategoryEntity);

			}
		}
	}

	/**
	 * This method verifies weather the current category is in use by some user in
	 * edit category mode.
	 * @param category category which is to be checked for in use state.
	 * @return true if category is in use else will return false.
	 */
	public synchronized boolean isCategoryInUse(final CategoryInterface category)
	{
		boolean isInUse = false;
		if (categoriesInUse.contains(category))
		{
			isInUse = true;
		}
		return isInUse;

	}

	/**
	 * This method will remove the given category from in Use set.
	 * so that it will be available for other users to Edit.
	 * @param category which is to be released.
	 */
	public synchronized void releaseCategoryFromUse(final CategoryInterface category)
	{
		categoriesInUse.remove(category);
	}

	/**
	 * This method will add the given category in use.
	 * @param category which is to be marked as in use.
	 */
	public synchronized void markCategoryInUse(final CategoryInterface category)
	{
		categoriesInUse.add(category);
	}

	/**
	 * This method will return the next Counter for generating the temporary directory
	 * for category creation.
	 * @return category file name counter.
	 */
	public synchronized long getNextIdForCategoryFileGeneration()
	{
		return catFileNameCounter++;
	}

	/**
	 * This method will add the given category to cache.
	 * @param category category to be added.
	 */
	public synchronized void addCategoryToCache(final CategoryInterface category)
	{
		LOGGER.info("adding category to cache" + category);
		deCategories.remove(category);
		deCategories.add(category);
		createCategoryEntityCach(category.getRootCategoryElement());
		LOGGER.info("adding category to cache done");

	}

	/**
	 * It will add the given container to the cache & also update the cache
	 * for its controls and AbstractEntity
	 * @param container
	 */
	private void addContainerToCache(final ContainerInterface container)
	{
		idVscontainers.put(container.getId(), container);
		createControlCache(container.getControlCollection());
		addAbstractEntityToCache(container.getAbstractEntity());
	}

	/**
	 * Adds all controls into cache.
	 * @param controlCollection collection of control objects which are  to be cached.
	 */
	private void createControlCache(final Collection<ControlInterface> controlCollection)
	{
		if (controlCollection != null)
		{
			for (final ControlInterface control : controlCollection)
			{
				idVsControl.put(control.getId(), control);
			}
		}
	}

	/**
	 * Adds abstract Entity (which can be 'CategoryEnity' or 'Entity') into cache.
	 * @param abstractEntity which should be cached.
	 * @param entityGroupsSet in which the entityGroup of the abstractEntity is cached.
	 * @param categorySet in which the category of the abstractEntity is cached.
	 */
	private void addAbstractEntityToCache(final AbstractEntityInterface abstractEntity)
	{
		if (abstractEntity instanceof CategoryEntityInterface)
		{
			final CategoryEntityInterface categoryEntity = (CategoryEntityInterface) abstractEntity;
			addCategoryEntityToCache(categoryEntity);
		}
		else
		{
			final EntityInterface entity = (EntityInterface) abstractEntity;
			createEntityCache(entity);

		}
	}

	/**
	 * Adds CategoryEnity into cache.
	 * @param categoryEntity which should be cached.
	 */
	private void addCategoryEntityToCache(final CategoryEntityInterface categoryEntity)
	{
		idVsCaegoryEntity.put(categoryEntity.getId(), categoryEntity);
		createCategoryAttributeCache(categoryEntity);
		createCategoryAssociationCache(categoryEntity);
	}

	/**
	 * It will add all the categoryAssociations of the categoryEntity in the cache.
	 * @param categoryEntity whose all categoryAssociations should be cached.
	 */
	private void createCategoryAssociationCache(final CategoryEntityInterface categoryEntity)
	{
		if (categoryEntity.getCategoryAttributeCollection() != null)
		{

			for (final CategoryAssociationInterface assocition : categoryEntity
					.getCategoryAssociationCollection())
			{
				idVsCaegoryAssociation.put(assocition.getId(), assocition);
			}
		}
	}

	/**
	 * It will add all the categoryAttributes of the categoryEntity in the cache.
	 * @param categoryEntity whose all categoryAttributes should be cached.
	 */
	private void createCategoryAttributeCache(final CategoryEntityInterface categoryEntity)
	{
		if (categoryEntity.getCategoryAttributeCollection() != null)
		{
			for (final CategoryAttributeInterface categoryAttribute : categoryEntity
					.getCategoryAttributeCollection())
			{
				idVsCategoryAttribute.put(categoryAttribute.getId(), categoryAttribute);
			}
		}
	}

	/**
	 * Adds all attribute of given entity into cache
	 * @param entity Entity to process
	 */
	private void createAttributeCache(final EntityInterface entity)
	{
		for (final AttributeInterface attribute : entity.getAttributeCollection())
		{
			idVsAttribute.put(attribute.getId(), attribute);
		}
	}

	/**
	 * Adds all associations of given entity into cache
	 * @param entity Entity to process
	 */
	private void createAssociationCache(final EntityInterface entity)
	{
		for (final AssociationInterface association : entity.getAssociationCollection())
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
	private void createPermissibleValueCache(final EntityInterface entity)
	{
		for (final AttributeInterface attribute : entity.getAttributeCollection())
		{
			for (final PermissibleValueInterface value : Utility.getPermissibleValues(attribute))
			{
				permissibleValueVsEntity.put(value, entity);
			}
		}
	}

	/* (non-Javadoc)
	 * @see edu.wustl.cab2b.common.cache.IEntityCache#getEntityOnEntityParameters(java.util.Collection)
	 */
	public MatchedClass getEntityOnEntityParameters(
			final Collection<EntityInterface> patternEntityCollection)
	{
		final MatchedClass matchedClass = new MatchedClass();
		for (final EntityInterface cachedEntity : idVsEntity.values())
		{
			for (final EntityInterface patternEntity : patternEntityCollection)
			{
				final MatchedClassEntry matchedClassEntry = CompareUtil.compare(cachedEntity,
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
			final Collection<AttributeInterface> patternAttributeCollection)
	{
		final MatchedClass matchedClass = new MatchedClass();
		for (final EntityInterface entity : idVsEntity.values())
		{
			for (final AttributeInterface cachedAttribute : entity.getAttributeCollection())
			{
				for (final AttributeInterface patternAttribute : patternAttributeCollection)
				{
					final MatchedClassEntry matchedClassEntry = CompareUtil.compare(
							cachedAttribute, patternAttribute);
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
			final Collection<PermissibleValueInterface> patternPermValueColl)
	{
		final MatchedClass matchedClass = new MatchedClass();
		for (final PermissibleValueInterface cachedPermissibleValue : permissibleValueVsEntity
				.keySet())
		{
			for (final PermissibleValueInterface patternPermissibleValue : patternPermValueColl)
			{
				final EntityInterface cachedEntity = permissibleValueVsEntity
						.get(cachedPermissibleValue);
				final MatchedClassEntry matchedClassEntry = CompareUtil.compare(
						cachedPermissibleValue, patternPermissibleValue, cachedEntity);
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
	public EntityGroupInterface getEntityGroupById(final Long identifier)
	{
		EntityGroupInterface entityGroup = null;
		for (final EntityGroupInterface group : cab2bEntityGroups)
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
	public EntityInterface getEntityById(final Long identifier)
	{
		EntityInterface entity = idVsEntity.get(identifier);
		if (entity == null)
		{
			try
			{
				entity = EntityManager.getInstance().getEntityByIdentifier(identifier);
				if (entity == null)
				{
					throw new RuntimeException("Entity with given id is not present in cache : "
							+ identifier);
				}
			}
			catch (DynamicExtensionsSystemException e)
			{
				throw new RuntimeException("Entity with given id is not present in cache : "
						+ identifier, e);
			}
			catch (DynamicExtensionsApplicationException e)
			{
				throw new RuntimeException("Entity with given id is not present in cache : "
						+ identifier, e);
			}

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
	public boolean isEntityPresent(final Long identifier)
	{
		return idVsEntity.containsKey(identifier);
	}

	/**
	 * Returns the Attribute for given Identifier
	 *
	 * @param identifier Id of the Attribute
	 * @return Actual Attribute for given id.
	 */
	public AttributeInterface getAttributeById(final Long identifier)
	{
		final AttributeInterface attribute = idVsAttribute.get(identifier);
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
	public AssociationInterface getAssociationById(final Long identifier)
	{
		final AssociationInterface association = idVsAssociation.get(identifier);
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
	public AssociationInterface getAssociationByUniqueStringIdentifier(
			final String uniqueStringIdentifier)
	{
		final AssociationInterface association = originalAssociations.get(uniqueStringIdentifier);
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
	private void createEntityCache(final EntityInterface entity)
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
	public void addEntityToCache(final EntityInterface entity)
	{
		if ((entity.getContainerCollection() == null) || entity.getContainerCollection().isEmpty())
		{
			createEntityCache(entity);
		}
		else
		{
			for (final Object container : entity.getContainerCollection())
			{
				final ContainerInterface containerInterface = (ContainerInterface) container;
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
	public EntityGroupInterface getEntityGroupByName(final String name)
	{
		EntityGroupInterface entityGroup = null;
		for (final EntityGroupInterface group : cab2bEntityGroups)
		{
			if ((group.getName() != null) && group.getName().equals(name))
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
	* @throws DynamicExtensionsApplicationException
	* @throws DynamicExtensionsSystemException
	*/

	public CategoryInterface getCategoryById(final Long identifier)
	throws DynamicExtensionsSystemException, DynamicExtensionsApplicationException
	{
		CategoryInterface category = (CategoryInterface) ((AbstractMetadataManager) CategoryManager
				.getInstance()).getObjectByIdentifier(Category.class.getName(), identifier
				.toString());
		return category;
	}


	/**
	 * It will return the CategoryAttribute with the id as given identifier in the parameter.
	 * @param identifier
	 * @return categoryAttribute with given identifier
	 */
	public CategoryAttributeInterface getCategoryAttributeById(final Long identifier)
	{
		final CategoryAttributeInterface categoryAttribute = idVsCategoryAttribute.get(identifier);
		if (categoryAttribute == null)
		{
			throw new RuntimeException(
					"Category Attribute with given id is not present in cache : " + identifier);
		}
		return categoryAttribute;
	}

	/**
	 * It will return BaseAbstractAttribute with the id as given identifier in the parameter.
	 * @param identifier
	 * @return categoryAttribute with given identifier
	 */
	public BaseAbstractAttributeInterface getBaseAbstractAttributeById(final Long identifier)
	{
		BaseAbstractAttributeInterface baseAbstractAttribute = null;
		baseAbstractAttribute = idVsCategoryAttribute.get(identifier);
		if (baseAbstractAttribute == null)
		{
			baseAbstractAttribute = idVsAttribute.get(identifier);
		}
		if (baseAbstractAttribute == null)
		{
			baseAbstractAttribute = idVsAssociation.get(identifier);
		}
		if (baseAbstractAttribute == null)
		{
			throw new RuntimeException(
					"BaseAbstractAttribute with given id is not present in cache : " + identifier);
		}
		return baseAbstractAttribute;
	}

	/**
	 * It will return the CategoryAssociation with the id as given identifier in the parameter.
	 * @param identifier
	 * @return CategoryAssociation with given identifier
	 */
	public CategoryAssociationInterface getCategoryAssociationById(final Long identifier)
	{
		final CategoryAssociationInterface categoryAssociation = idVsCaegoryAssociation
				.get(identifier);
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
	public CategoryEntityInterface getCategoryEntityById(final Long identifier)
	{
		final CategoryEntityInterface categoryEntity = idVsCaegoryEntity.get(identifier);
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
	* @throws DynamicExceptionsCacheException
	*/

	public ContainerInterface getContainerById(final Long identifier)
			throws DynamicExtensionsCacheException
	{
		ContainerInterface container = idVscontainers.get(identifier);
		try
		{
			if (container == null)
			{
				container = (ContainerInterface) ((AbstractMetadataManager) CategoryManager
						.getInstance()).getObjectByIdentifier(ContainerInterface.class.getName(),
						identifier.toString());
			}
		}
		catch (DynamicExtensionsSystemException e)
		{
			throw new DynamicExtensionsCacheException(
					"Exception encounter while fetching the category" + identifier, e);
		}
		catch (DynamicExtensionsApplicationException e)
		{
			throw new DynamicExtensionsCacheException(
					"Exception encounter while fetching the category" + identifier, e);
		}
		if (container == null)
		{
			throw new DynamicExtensionsCacheException(
					"container with given id is not present in cache : " + identifier);
		}
		return container;
	}

	/**
	 * It will return the Control with the id as given identifier in the parameter.
	 * @param identifier
	 * @return Control with given identifier
	 */
	public ControlInterface getControlById(final Long identifier)
	{
		final ControlInterface control = idVsControl.get(identifier);
		if (control == null)
		{
			throw new RuntimeException("Control with given id is not present in cache : "
					+ identifier);
		}
		return control;
	}

	public synchronized void updatePermissibleValues(EntityInterface entity, Long attributeId,
			AttributeTypeInformationInterface attrTypeInfo)
	{
		AttributeInterface cachedattribute = entityCache.getAttributeById(attributeId);
		cachedattribute.setAttributeTypeInformation(attrTypeInfo);

		Set<Entry<PermissibleValueInterface, EntityInterface>> pvVsEntity = permissibleValueVsEntity
				.entrySet();
		List<PermissibleValueInterface> toBeRemovedPVs = new ArrayList<PermissibleValueInterface>();
		for (Entry<PermissibleValueInterface, EntityInterface> entry : pvVsEntity)
		{
			if (entry.getValue().getName().equalsIgnoreCase(entity.getName()))
			{
				toBeRemovedPVs.add(entry.getKey());
			}
		}
		for (PermissibleValueInterface pvList : toBeRemovedPVs)
		{
			permissibleValueVsEntity.remove(pvList);
		}
		updatePermissibleValueMap(entity);

	}

	private void updatePermissibleValueMap(EntityInterface entity)
	{
		Collection<AttributeInterface> allAttributes = entity.getAllAttributes();
		Collection<PermissibleValueInterface> allPVs;
		for (AttributeInterface attribute : allAttributes)
		{
			UserDefinedDEInterface userDefinedDE = (UserDefinedDEInterface) attribute
					.getAttributeTypeInformation().getDataElement();
			if (userDefinedDE != null)
			{
				allPVs = userDefinedDE.getPermissibleValues();
				for (PermissibleValueInterface permissibleValue : allPVs)
				{
					permissibleValueVsEntity.put(permissibleValue, entity);
				}
			}
		}
	}
}