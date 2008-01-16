
package edu.common.dynamicextensions.entitymanager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Stack;

import edu.common.dynamicextensions.domain.Association;
import edu.common.dynamicextensions.domain.Category;
import edu.common.dynamicextensions.domain.CategoryAssociation;
import edu.common.dynamicextensions.domain.CategoryAttribute;
import edu.common.dynamicextensions.domain.PathAssociationRelationInterface;
import edu.common.dynamicextensions.domaininterface.AbstractAttributeInterface;
import edu.common.dynamicextensions.domaininterface.AbstractMetadataInterface;
import edu.common.dynamicextensions.domaininterface.AssociationInterface;
import edu.common.dynamicextensions.domaininterface.AttributeInterface;
import edu.common.dynamicextensions.domaininterface.BaseAbstractAttributeInterface;
import edu.common.dynamicextensions.domaininterface.CategoryAssociationInterface;
import edu.common.dynamicextensions.domaininterface.CategoryAttributeInterface;
import edu.common.dynamicextensions.domaininterface.CategoryEntityInterface;
import edu.common.dynamicextensions.domaininterface.CategoryInterface;
import edu.common.dynamicextensions.domaininterface.DynamicExtensionBaseDomainObjectInterface;
import edu.common.dynamicextensions.domaininterface.EntityInterface;
import edu.common.dynamicextensions.exception.DynamicExtensionsApplicationException;
import edu.common.dynamicextensions.exception.DynamicExtensionsSystemException;
import edu.common.dynamicextensions.util.DynamicExtensionsUtility;
import edu.common.dynamicextensions.util.global.Constants;
import edu.wustl.common.dao.DAOFactory;
import edu.wustl.common.dao.HibernateDAO;
import edu.wustl.common.util.dbManager.DAOException;

/**
 * @author rajesh_patil
 * @author mandar_shidhore
 * @author kunal_kamble
 */
public class CategoryManager extends AbstractMetadataManager implements CategoryManagerInterface
{
	/**
	 * Static instance of the CategoryManager.
	 */
	private static CategoryManagerInterface categoryManager = null;

	/**
	 * Static instance of the queryBuilder.
	 */
	private static DynamicExtensionBaseQueryBuilder queryBuilder = null;

	/**
	 * Empty Constructor.
	 */
	protected CategoryManager()
	{

	}

	/**
	 * Returns the instance of the Entity Manager.
	 * @return entityManager singleton instance of the Entity Manager.
	 */
	public static synchronized CategoryManagerInterface getInstance()
	{
		if (categoryManager == null)
		{
			categoryManager = new CategoryManager();
			DynamicExtensionsUtility.initialiseApplicationVariables();
			queryBuilder = QueryBuilderFactory.getQueryBuilder();
		}
		return categoryManager;
	}

	/**
	 * LogFatalError.
	 */
	protected void LogFatalError(Exception e, AbstractMetadataInterface abstractMetadata)
	{
		// TODO Auto-generated method stub
	}

	/**
	 * Method to persist categroy metadata.
	 * @param categoryInterface interface for Category
	 * @throws DynamicExtensionsSystemException, DynamicExtensionsApplicationException
	 */
	public CategoryInterface persistCategory(CategoryInterface categoryInterface) throws DynamicExtensionsSystemException,
			DynamicExtensionsApplicationException
	{
		CategoryInterface category = (CategoryInterface) persistDynamicExtensionObject(categoryInterface);
		return category;
	}

	/**
	 * Method to persist categroy metadata.
	 * @param categoryInterface interface for Category
	 * @throws DynamicExtensionsSystemException, DynamicExtensionsApplicationException
	 */
	public CategoryInterface persistCategoryMetadata(CategoryInterface categoryInterface) throws DynamicExtensionsSystemException,
			DynamicExtensionsApplicationException
	{
		CategoryInterface category = (CategoryInterface) persistDynamicExtensionObjectMetdata(categoryInterface);
		return category;
	}

	/**
	 *
	 */
	protected void preProcess(DynamicExtensionBaseDomainObjectInterface dynamicExtensionBaseDomainObject, List reverseQueryList,
			HibernateDAO hibernateDAO, List queryList) throws DynamicExtensionsSystemException, DynamicExtensionsApplicationException
	{
		CategoryInterface category = (CategoryInterface) dynamicExtensionBaseDomainObject;

		queryList.addAll(getDynamicQueryList(category, reverseQueryList, hibernateDAO, queryList));
	}

	/**
	 * getDynamicQueryList.
	 */
	public List getDynamicQueryList(CategoryInterface category, List reverseQueryList, HibernateDAO hibernateDAO, List queryList)
			throws DynamicExtensionsSystemException, DynamicExtensionsApplicationException
	{
		return queryBuilder.getCreateCategoryQueryList((Category) category, reverseQueryList, hibernateDAO);
	}

	/**
	 * postProcess.
	 */
	protected void postProcess(List queryList, List reverseQueryList, Stack rollbackQueryStack) throws DynamicExtensionsSystemException
	{
		queryBuilder.executeQueries(queryList, reverseQueryList, rollbackQueryStack);
	}

	/**
	 *
	 */
	public Long insertData(CategoryInterface category, Map<BaseAbstractAttributeInterface, ?> dataValue) throws DynamicExtensionsApplicationException,
			DynamicExtensionsSystemException
	{
		List<Map<BaseAbstractAttributeInterface, ?>> dataValueMapList = new ArrayList<Map<BaseAbstractAttributeInterface, ?>>();
		dataValueMapList.add(dataValue);
		List<Long> recordIdList = insertData(category, dataValueMapList);
		return recordIdList.get(0);
	}

	/**
	 *
	 */
	public List<Long> insertData(CategoryInterface category, List<Map<BaseAbstractAttributeInterface, ?>> categoryDataValueMapList)
			throws DynamicExtensionsApplicationException, DynamicExtensionsSystemException
	{
		List<Map<AbstractAttributeInterface, ?>> EntityDataValueMapList = createEntityDataValueMapList(categoryDataValueMapList);
		CategoryEntityInterface rootCategoryEntity = category.getRootCategoryElement();
		EntityInterface entity = rootCategoryEntity.getEntity();
		List<Long> recordIdList = new ArrayList<Long>();
		HibernateDAO hibernateDAO = null;
		try
		{
			DAOFactory factory = DAOFactory.getInstance();
			hibernateDAO = (HibernateDAO) factory.getDAO(Constants.HIBERNATE_DAO);
			hibernateDAO.openSession(null);
			EntityManagerInterface entityManager = EntityManager.getInstance();
			entityManager.insertData(entity, EntityDataValueMapList);
			hibernateDAO.commit();
		}
		catch (DynamicExtensionsApplicationException e)
		{
			throw (DynamicExtensionsApplicationException) handleRollback(e, "Error while inserting data", hibernateDAO, false);
		}
		catch (Exception e)
		{
			throw (DynamicExtensionsSystemException) handleRollback(e, "Error while inserting data", hibernateDAO, true);
		}
		finally
		{
			try
			{
				hibernateDAO.closeSession();
			}
			catch (DAOException e)
			{
				throw (DynamicExtensionsSystemException) handleRollback(e, "Error while closing", hibernateDAO, true);
			}
		}

		return recordIdList;
	}

	private List<Map<AbstractAttributeInterface, ?>> createEntityDataValueMapList(List<Map<BaseAbstractAttributeInterface, ?>> categoryDataValueMapList) {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 *
	 */
	protected DynamicExtensionBaseQueryBuilder getQueryBuilderInstance()
	{
		return queryBuilder;
	}

	/**
	 * This method accepts a category data map and converts it to corresponding entity data map.
	 */
	private Map<AbstractAttributeInterface, Object> generateEntityDataValueMap(CategoryEntityInterface rootCategoryEntity, Map<BaseAbstractAttributeInterface, Object> categoryDataMap,
			List<AssociationInterface> existingAssociationsList)
	{
		Map<AbstractAttributeInterface, Object> entityDataValueMap = new HashMap<AbstractAttributeInterface, Object>();
		Set<BaseAbstractAttributeInterface> categoryDataMapKeys = categoryDataMap.keySet();
		
		AttributeInterface attributeInterface;
		Object attributeValue;

		for (BaseAbstractAttributeInterface baseAbstractAttributeInterface: categoryDataMapKeys)
		{
			if (baseAbstractAttributeInterface instanceof CategoryAssociation)
			{
				addDataForEntitiesOnPath(entityDataValueMap, categoryDataMap, (CategoryAssociation) baseAbstractAttributeInterface, existingAssociationsList);
			}
			else
			{
				attributeInterface = ((CategoryAttribute) baseAbstractAttributeInterface).getAttribute();
				attributeValue = categoryDataMap.get(baseAbstractAttributeInterface);
				entityDataValueMap.put(attributeInterface,attributeValue);
			}
		}
		return entityDataValueMap;
	}

	/**
	 * This method adds data for all entities present on the path between two category entities.
	 * @param entityDataMap
	 * @param categoryDataMap
	 * @param categoryAssociationInterface
	 * @param existingAssociationsList
	 */
	private void addDataForEntitiesOnPath(Map<AbstractAttributeInterface, Object> entityDataMap,
			Map<BaseAbstractAttributeInterface, Object> categoryDataMap, CategoryAssociationInterface categoryAssociationInterface,
			List<AssociationInterface> existingAssociationsList)
	{
		Map<AbstractAttributeInterface, Object> dynamicDataMap = entityDataMap;

		for (PathAssociationRelationInterface associationRelationInterface : categoryAssociationInterface.getCategoryEntity().getPath()
				.getSortedPathAssociationRelationCollection())
		{
			if (associationExists(existingAssociationsList, associationRelationInterface.getAssociation()))
			{
				continue;
			}
			if (associationRelationInterface.getAssociation().getTargetEntity() == categoryAssociationInterface.getCategoryEntity().getEntity())
			{
				List<Map<BaseAbstractAttributeInterface, Object>> oldDataList = (List<Map<BaseAbstractAttributeInterface, Object>>) categoryDataMap
						.get(categoryAssociationInterface);
				List<Map<AbstractAttributeInterface, Object>> newDataList = new ArrayList<Map<AbstractAttributeInterface, Object>>();

				// When an association is added, add the same to existing associations' list
				existingAssociationsList.add(associationRelationInterface.getAssociation());

				populateNewDataList(oldDataList, newDataList, entityDataMap, categoryDataMap, existingAssociationsList);
				dynamicDataMap.put(associationRelationInterface.getAssociation(), newDataList);
			}
			else
			{
				Map<AbstractAttributeInterface, Object> newDataMap = new HashMap<AbstractAttributeInterface, Object>();
				List<Map<AbstractAttributeInterface, Object>> newDataList = new ArrayList<Map<AbstractAttributeInterface, Object>>();
				newDataList.add(newDataMap);
				dynamicDataMap.put(associationRelationInterface.getAssociation(), newDataList);

				// When an association is added, add the same to existing associations' list
				existingAssociationsList.add(associationRelationInterface.getAssociation());

				dynamicDataMap = newDataMap;
			}
		}
	}

	/**
	 * Create a new data list, and populate it with the values from old data list.
	 * @param oldDataList
	 * @param newDataList
	 * @param entityDataMap
	 * @param categoryDataMap
	 * @param existingAssociationsList
	 */
	private void populateNewDataList(List<Map<BaseAbstractAttributeInterface, Object>> oldDataList,
			List<Map<AbstractAttributeInterface, Object>> newDataList, Map<AbstractAttributeInterface, Object> entityDataMap,
			Map<BaseAbstractAttributeInterface, Object> categoryDataMap, List<AssociationInterface> existingAssociationsList)
	{
		Map<AbstractAttributeInterface, Object> newDataMap = null;
		for (Map<BaseAbstractAttributeInterface, Object> oldDataMap : oldDataList)
		{
			newDataMap = new HashMap<AbstractAttributeInterface, Object>();
			Set<BaseAbstractAttributeInterface> dataSet = oldDataMap.keySet();
			for (AbstractMetadataInterface object : dataSet)
			{
				if (object instanceof CategoryAssociation)
				{
					addDataForEntitiesOnPath(newDataMap, oldDataMap, (CategoryAssociationInterface) object, existingAssociationsList);
				}
				else
				{
					newDataMap.put(((CategoryAttributeInterface) object).getAttribute(), oldDataMap.get(object));
				}
			}
			newDataList.add(newDataMap);
		}
	}

	/**
	 * This method checks if an association has already been added to entity data map.
	 * @param existingAssociationsList
	 * @param association
	 * @return true if an association has already been added, false otherwise.
	 */
	private boolean associationExists(List<AssociationInterface> existingAssociationsList, Association association)
	{
		for (AssociationInterface currentAssociation : existingAssociationsList)
		{
			if (currentAssociation.getName().equals(association.getName()))
				return true;
		}
		return false;
	}

}