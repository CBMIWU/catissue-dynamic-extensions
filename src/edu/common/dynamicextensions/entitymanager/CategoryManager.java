
package edu.common.dynamicextensions.entitymanager;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Stack;
import java.util.Map.Entry;

import org.hibernate.HibernateException;

import edu.common.dynamicextensions.domain.BaseAbstractAttribute;
import edu.common.dynamicextensions.domain.CategoryAttribute;
import edu.common.dynamicextensions.domain.CategoryEntity;
import edu.common.dynamicextensions.domain.DomainObjectFactory;
import edu.common.dynamicextensions.domain.PathAssociationRelationInterface;
import edu.common.dynamicextensions.domaininterface.AbstractAttributeInterface;
import edu.common.dynamicextensions.domaininterface.AbstractMetadataInterface;
import edu.common.dynamicextensions.domaininterface.AssociationInterface;
import edu.common.dynamicextensions.domaininterface.BaseAbstractAttributeInterface;
import edu.common.dynamicextensions.domaininterface.CategoryAssociationInterface;
import edu.common.dynamicextensions.domaininterface.CategoryAttributeInterface;
import edu.common.dynamicextensions.domaininterface.CategoryEntityInterface;
import edu.common.dynamicextensions.domaininterface.CategoryInterface;
import edu.common.dynamicextensions.domaininterface.DESQLAuditInterface;
import edu.common.dynamicextensions.domaininterface.DynamicExtensionBaseDomainObjectInterface;
import edu.common.dynamicextensions.domaininterface.EntityGroupInterface;
import edu.common.dynamicextensions.domaininterface.EntityInterface;
import edu.common.dynamicextensions.domaininterface.PathInterface;
import edu.common.dynamicextensions.domaininterface.PermissibleValueInterface;
import edu.common.dynamicextensions.domaininterface.UserDefinedDEInterface;
import edu.common.dynamicextensions.exception.DynamicExtensionsApplicationException;
import edu.common.dynamicextensions.exception.DynamicExtensionsSystemException;
import edu.common.dynamicextensions.util.DynamicExtensionsUtility;
import edu.common.dynamicextensions.util.global.Constants;
import edu.wustl.common.dao.DAOFactory;
import edu.wustl.common.dao.HibernateDAO;
import edu.wustl.common.security.exceptions.UserNotAuthorizedException;
import edu.wustl.common.util.dbManager.DAOException;
import edu.wustl.common.util.dbManager.DBUtil;

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
	 * Instance of entity manager utility class
	 */
	EntityManagerUtil entityManagerUtil = new EntityManagerUtil();

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
	 * Method to persist a category.
	 * @param categoryInterface interface for Category
	 * @throws DynamicExtensionsSystemException, DynamicExtensionsApplicationException
	 */
	public CategoryInterface persistCategory(CategoryInterface categoryInterface) throws DynamicExtensionsSystemException,
			DynamicExtensionsApplicationException
	{
		CategoryInterface category = (CategoryInterface) persistDynamicExtensionObject(categoryInterface);

		// Update the dynamic extension cache for all containers within entity group
		CategoryEntityInterface catEntityInterface = category.getRootCategoryElement();
		EntityGroupInterface entityGroupInterface = catEntityInterface.getEntity().getEntityGroup();
		DynamicExtensionsUtility.updateDynamicExtensionsCache(entityGroupInterface.getId());
		return category;
	}

	/**
	 * Method to persist category meta-data.
	 * @param categoryInterface interface for Category
	 * @throws DynamicExtensionsSystemException, DynamicExtensionsApplicationException
	 */
	public CategoryInterface persistCategoryMetadata(CategoryInterface categoryInterface) throws DynamicExtensionsSystemException,
			DynamicExtensionsApplicationException
	{
		CategoryInterface category = (CategoryInterface) persistDynamicExtensionObjectMetdata(categoryInterface);

		// Update the dynamic extension cache for all containers within entity group
		CategoryEntityInterface catEntityInterface = category.getRootCategoryElement();
		EntityGroupInterface entityGroupInterface = catEntityInterface.getEntity().getEntityGroup();
		DynamicExtensionsUtility.updateDynamicExtensionsCache(entityGroupInterface.getId());
		return category;
	}

	/* (non-Javadoc)
	 * @see edu.common.dynamicextensions.entitymanager.AbstractMetadataManager#preProcess(edu.common.dynamicextensions.domaininterface.DynamicExtensionBaseDomainObjectInterface, java.util.List, edu.wustl.common.dao.HibernateDAO, java.util.List)
	 */
	protected void preProcess(DynamicExtensionBaseDomainObjectInterface dynamicExtensionBaseDomainObject, List<String> reverseQueryList,
			HibernateDAO hibernateDAO, List<String> queryList) throws DynamicExtensionsSystemException, DynamicExtensionsApplicationException
	{
		CategoryInterface category = (CategoryInterface) dynamicExtensionBaseDomainObject;

		getDynamicQueryList(category, reverseQueryList, hibernateDAO, queryList);
	}

	/**
	 * This method gets a list of dynamic tables creation queries. 
	 * @param category
	 * @param reverseQueryList
	 * @param hibernateDAO
	 * @param queryList
	 * @return list of dynamic tables creation queries
	 * @throws DynamicExtensionsSystemException
	 * @throws DynamicExtensionsApplicationException
	 */
	protected List<String> getDynamicQueryList(CategoryInterface category, List<String> reverseQueryList, HibernateDAO hibernateDAO,
			List<String> queryList) throws DynamicExtensionsSystemException, DynamicExtensionsApplicationException
	{
		List<CategoryEntityInterface> categoryEntityList = new ArrayList<CategoryEntityInterface>();
		//Use HashMap instead of List to ensure entitylist contains unique entity only
		HashMap<String, CategoryEntityInterface> objCategoryMap = new HashMap<String, CategoryEntityInterface>();
		DynamicExtensionsUtility.getUnsavedCategoryEntityList(category.getRootCategoryElement(), objCategoryMap);
		Iterator keyIterator = objCategoryMap.keySet().iterator();
		while(keyIterator.hasNext())
		{
			categoryEntityList.add(objCategoryMap.get(keyIterator.next()));
		}	
		
		for (CategoryEntityInterface categoryEntityInterface : categoryEntityList)
		{
			List<String> createQueryList = queryBuilder.getCreateCategoryQueryList(categoryEntityInterface, reverseQueryList, hibernateDAO);
			if (createQueryList != null && !createQueryList.isEmpty())
			{
				queryList.addAll(createQueryList);
			}
		}
		for (CategoryEntityInterface categoryEntityInterface : categoryEntityList)
		{
			List<String> createQueryList = queryBuilder.getUpdateCategoryEntityQueryList(categoryEntityInterface, reverseQueryList, hibernateDAO);
			if (createQueryList != null && !createQueryList.isEmpty())
			{
				queryList.addAll(createQueryList);
			}
		}
		//Use HashMap instead of List to ensure entitylist contains unique entity only
		List<CategoryEntityInterface> savedCategoryEntityList = new ArrayList<CategoryEntityInterface>();
		objCategoryMap = new HashMap<String, CategoryEntityInterface>();
		DynamicExtensionsUtility.getSavedCategoryEntityList(category.getRootCategoryElement(), objCategoryMap);
	    keyIterator = objCategoryMap.keySet().iterator();
		while(keyIterator.hasNext())
		{
			savedCategoryEntityList.add(objCategoryMap.get(keyIterator.next()));
		}
		for (CategoryEntityInterface savedCategoryEntity : savedCategoryEntityList)
		{
			CategoryEntity databaseCopy = (CategoryEntity) DBUtil.loadCleanObj(CategoryEntity.class, savedCategoryEntity.getId());
			//Only for category entity for which table is getting created
			if(databaseCopy.isCreateTable())
			{	
				List<String> updateQueryList = queryBuilder
						.getUpdateEntityQueryList((CategoryEntity) savedCategoryEntity, databaseCopy, reverseQueryList);
	
				if (updateQueryList != null && !updateQueryList.isEmpty())
				{
					queryList.addAll(updateQueryList);
				}
			}
		}

		return queryList;
	}

	/* (non-Javadoc)
	 * @see edu.common.dynamicextensions.entitymanager.AbstractMetadataManager#postProcess(java.util.List, java.util.List, java.util.Stack)
	 */
	protected void postProcess(List<String> queryList, List<String> reverseQueryList, Stack rollbackQueryStack)
			throws DynamicExtensionsSystemException
	{
		queryBuilder.executeQueries(queryList, reverseQueryList, rollbackQueryStack);
	}

	/* (non-Javadoc)
	 * @see edu.common.dynamicextensions.entitymanager.CategoryManagerInterface#insertData(edu.common.dynamicextensions.domaininterface.CategoryInterface, java.util.Map)
	 */
	public Long insertData(CategoryInterface category, Map<BaseAbstractAttributeInterface, Object> dataValue, Long... userId)
			throws DynamicExtensionsApplicationException, DynamicExtensionsSystemException
	{
		List<Map<BaseAbstractAttributeInterface, Object>> dataValueMapList = new ArrayList<Map<BaseAbstractAttributeInterface, Object>>();
		dataValueMapList.add(dataValue);
		Long id = ((userId != null || userId.length > 0) ? userId[0] : null);
		List<Long> recordIdList = insertData(category, dataValueMapList, id);
		return recordIdList.get(0);
	}

	/* (non-Javadoc)
	 * @see edu.common.dynamicextensions.entitymanager.CategoryManagerInterface#insertData(edu.common.dynamicextensions.domaininterface.CategoryInterface, java.util.List)
	 */
	public List<Long> insertData(CategoryInterface category, List<Map<BaseAbstractAttributeInterface, Object>> categoryDataValueMapList,
			Long... userId) throws DynamicExtensionsApplicationException, DynamicExtensionsSystemException
	{
		List<Long> recordIdList = new ArrayList<Long>();

		HibernateDAO hibernateDAO = null;
		try
		{
			DAOFactory factory = DAOFactory.getInstance();
			hibernateDAO = (HibernateDAO) factory.getDAO(Constants.HIBERNATE_DAO);
			hibernateDAO.openSession(null);
			Long id = ((userId != null || userId.length > 0) ? userId[0] : null);

			for (Map<BaseAbstractAttributeInterface, ?> categoryDataValue : categoryDataValueMapList)
			{
				Long recordId = insertDataForHierarchy(category.getRootCategoryElement(), categoryDataValue, hibernateDAO, id);
				recordIdList.add(recordId);
			}

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

	/**
	 * This method inserts the data for hierarchy of category entities one by one.
	 * @param entity
	 * @param dataValue
	 * @param hibernateDAO
	 * @param userId 
	 * @return parent record identifier
	 * @throws DynamicExtensionsSystemException
	 * @throws DynamicExtensionsApplicationException
	 * @throws HibernateException
	 * @throws SQLException
	 * @throws DAOException
	 * @throws UserNotAuthorizedException
	 */
	private Long insertDataForHierarchy(CategoryEntityInterface categoryEntity, Map<BaseAbstractAttributeInterface, ?> dataValue,
			HibernateDAO hibernateDAO, Long... userId) throws DynamicExtensionsSystemException, DynamicExtensionsApplicationException,
			HibernateException, SQLException, DAOException, UserNotAuthorizedException
	{
		List<CategoryEntityInterface> categoryEntityList = getParentEntityList(categoryEntity);
		Map<CategoryEntityInterface, Map> entityValueMap = initialiseEntityValueMap(dataValue);
		Long parentRecordId = null;
		Long parentCategoryRecordId = null;
		Map testdatamap = new HashMap();
		Long id = ((userId != null || userId.length > 0) ? userId[0] : null);
		for (CategoryEntityInterface categoryEntityInterface : categoryEntityList)
		{
			Map valueMap = entityValueMap.get(categoryEntityInterface);
			//if parent category entity table not created ,then add its attributemap  to valuemap
			CategoryEntity  objParentCategoryEntity = (CategoryEntity) categoryEntityInterface.getParentCategoryEntity() ; 
			while(objParentCategoryEntity!=null &&  !objParentCategoryEntity.isCreateTable())
			{
				Map innerValueMap = entityValueMap.get(objParentCategoryEntity);
				if(innerValueMap!=null)
				valueMap.putAll(innerValueMap);
				objParentCategoryEntity = (CategoryEntity) objParentCategoryEntity.getParentCategoryEntity();
				
			}
			parentRecordId = insertDataForSingleCategoryEntity(categoryEntityInterface, valueMap, hibernateDAO, parentRecordId, testdatamap, id);
			parentCategoryRecordId = getRootCategoryRecordId(categoryEntityInterface,parentRecordId);
		}
		
		
		return parentCategoryRecordId;
	}

	/**
	 * @param categoryEntity
	 * @param parentRecordId
	 * @return
	 * @throws SQLException
	 * @throws DynamicExtensionsSystemException
	 */
	private Long getRootCategoryRecordId(CategoryEntityInterface categoryEntity, Long parentRecordId) throws SQLException,
	DynamicExtensionsSystemException
	{
		CategoryInterface category = categoryEntity.getCategory();
		CategoryEntityInterface rootCategoryEntity = category.getRootCategoryElement();
		
		StringBuffer query = new StringBuffer();
		query.append(SELECT_KEYWORD + WHITESPACE + IDENTIFIER + WHITESPACE + FROM_KEYWORD + WHITESPACE
				+ rootCategoryEntity.getTableProperties().getName() + WHITESPACE + WHERE_KEYWORD + WHITESPACE + RECORD_ID + EQUAL
				+ parentRecordId);

		Long rootCategoryRecordId = null;

		List<Long> resultList = getResultIDList(query.toString(), IDENTIFIER);
		if (resultList.size() > 0)
		{
			rootCategoryRecordId = (Long) resultList.get(0);
		}
		
		return rootCategoryRecordId;
	}

	/**
	 * This method inserts data for a single category entity.
	 * @param categoryEntity
	 * @param dataValue
	 * @param hibernateDAO
	 * @param parentRecordId
	 * @param dataMap
	 * @param userId 
	 * @return parent record identifier
	 * @throws DynamicExtensionsSystemException
	 * @throws DynamicExtensionsApplicationException
	 * @throws HibernateException
	 * @throws SQLException
	 * @throws DAOException
	 * @throws UserNotAuthorizedException
	 */
	private Long insertDataForSingleCategoryEntity(CategoryEntityInterface categoryEntity, Map dataValue, HibernateDAO hibernateDAO,
			Long parentRecordId, Map dataMap, Long... userId) throws DynamicExtensionsSystemException, DynamicExtensionsApplicationException,
			HibernateException, SQLException, DAOException, UserNotAuthorizedException
	{
		Long identifier = null;

		Map<String, Long> keyMap = new HashMap<String, Long>();
		Map<String, Long> fullKeyMap = new HashMap<String, Long>();
		Map<String, List<Long>> recordsMap = new HashMap<String, List<Long>>();
		Long id = ((userId != null || userId.length > 0) ? userId[0] : null);
		CategoryInterface category = categoryEntity.getCategory();

		if (categoryEntity == null)
		{
			throw new DynamicExtensionsSystemException("Input to insert data is null");
		}

		CategoryEntityInterface rootCategoryEntity = category.getRootCategoryElement();
		String rootCategoryEntityName = rootCategoryEntity.getName();

		// If root category entity does not have any attribute, or all its category attributes
		// are related attributes, then explicitly insert identifier into entity table and
		// insert this identifier as record identifier in category entity table.
		if (rootCategoryEntity.getCategoryAttributeCollection().size() == 0 || isAllRelatedCategoryAttributesCollection(rootCategoryEntity))
		{
			///--insert blank record in all parent entity of rootcategoryentity  so use insertDataForHeirarchy and add all keys to  keymap,recordmap,fullkeymap
			Map<AbstractAttributeInterface, Object> attributeMap = new HashMap<AbstractAttributeInterface, Object>();
			EntityManagerInterface entityManager = EntityManager.getInstance();
			Long  entityIdentifier = entityManager.insertDataForHeirarchy(categoryEntity.getEntity(), attributeMap, hibernateDAO, id);
			keyMap.put(rootCategoryEntityName, entityIdentifier);
			fullKeyMap.put(rootCategoryEntityName, entityIdentifier);

			List<Long> idList = new ArrayList<Long>();
			idList.add(entityIdentifier);
			recordsMap.put(rootCategoryEntityName, idList);
			
			while(categoryEntity.getParentCategoryEntity()!=null)
			{
				keyMap.put(categoryEntity.getParentCategoryEntity().getName(),entityIdentifier);
				fullKeyMap.put(categoryEntity.getParentCategoryEntity().getName(),entityIdentifier);

				List<Long> recoIdList	 = recordsMap.get(categoryEntity.getParentCategoryEntity().getName());
				if (recoIdList == null)
				{
					recoIdList = new ArrayList<Long>();
				}
				recoIdList.add(entityIdentifier);
				recordsMap.put(categoryEntity.getParentCategoryEntity().getName(), recoIdList);
				categoryEntity = categoryEntity.getParentCategoryEntity();
				
			}
			//
			Long categoryIdentifier = entityManagerUtil.getNextIdentifier(rootCategoryEntity.getTableProperties().getName());
			String categoryEntityTableInsertQuery = "INSERT INTO " + rootCategoryEntity.getTableProperties().getName()
					+ " (IDENTIFIER, ACTIVITY_STATUS, " + RECORD_ID + ") VALUES (" + categoryIdentifier + ", 'ACTIVE', " + entityIdentifier + ")";

			executeUpdateQuery(categoryEntityTableInsertQuery, id, hibernateDAO);
			logDebug("insertData", "categoryEntityTableInsertQuery is : " + categoryEntityTableInsertQuery);
		}

		boolean isMultipleRecords = false;
		boolean isNoCategoryAttributePresent = false;

		String entityForeignKeyColumnName = null;
		String categoryEntityForeignKeyColumnName = null;
		Long sourceCategoryEntityId = null;
		Long sourceEntityId = null;
		//Separate out cat. attribute and cat. association ,as from UI map it can contains both in anyorder but we requires to insert attribute first for rootcatgoryentity
		Map<CategoryAttributeInterface, Object> categoryAttributeMap = new HashMap<CategoryAttributeInterface, Object>();
		Map<CategoryAssociationInterface, Object> categoryAssociationMap = new HashMap<CategoryAssociationInterface, Object>();
			
		Set<BaseAbstractAttributeInterface> keySet = dataValue.keySet();
		Iterator<BaseAbstractAttributeInterface> iter = keySet.iterator();

		while (iter.hasNext())
		{
			Object obj = iter.next();
			if (obj instanceof CategoryAttributeInterface)
			{
				categoryAttributeMap.put((CategoryAttributeInterface) obj, dataValue.get(obj));
			}
			else
			{
				categoryAssociationMap.put((CategoryAssociationInterface) obj, dataValue.get(obj));
			}
		}
		insertRecordsForCategoryEntityTree(entityForeignKeyColumnName, categoryEntityForeignKeyColumnName, sourceCategoryEntityId, sourceEntityId,
					categoryEntity, categoryAttributeMap, keyMap, fullKeyMap, recordsMap, isMultipleRecords, isNoCategoryAttributePresent, hibernateDAO, id);
	    
		
		insertRecordsForCategoryEntityTree(entityForeignKeyColumnName, categoryEntityForeignKeyColumnName, sourceCategoryEntityId, sourceEntityId,
				categoryEntity, categoryAssociationMap, keyMap, fullKeyMap, recordsMap, isMultipleRecords, isNoCategoryAttributePresent, hibernateDAO, id);

		Long rootCategoryEntityRecordId = getRootCategoryEntityRecordId(category.getRootCategoryElement(), (Long) fullKeyMap.get(rootCategoryEntity
				.getName()));

		insertRecordsForRelatedAttributes(rootCategoryEntityRecordId, category.getRootCategoryElement(), recordsMap, hibernateDAO, id);

		if (parentRecordId != null)
		{
			identifier = parentRecordId;
		}
		else
		{
			identifier = (Long) keyMap.get(rootCategoryEntity.getName());
		}

		return identifier;
	}

	/**
	 * This method checks if all category attributes in a category entity are related attributes.
	 * @param categoryEntity
	 * @return true if all category attributes are related attributes, false otherwise
	 */
	private boolean isAllRelatedCategoryAttributesCollection(CategoryEntityInterface categoryEntity)
	{
		Collection<CategoryAttributeInterface> categoryAttributes = categoryEntity.getAllCategoryAttributes(); //.getCategoryAttributeCollection();

		for (CategoryAttributeInterface categoryAttribute : categoryAttributes)
		{
			if (categoryAttribute.getIsVisible() == null)
			{
				return false;
			}
		}

		return true;
	}

	/**
	 * Insert records for related attributes in each category entity.
	 * @param rootRecordId
	 * @param rootCategoryeEntity
	 * @param recordsMap
	 * @param hibernateDAO
	 * @param id
	 * @throws SQLException
	 * @throws DynamicExtensionsSystemException
	 */
	private void insertRecordsForRelatedAttributes(Long rootRecordId, CategoryEntityInterface rootCategoryeEntity,
			Map<String, List<Long>> recordsMap, HibernateDAO hibernateDAO, Long id) throws SQLException, DynamicExtensionsSystemException
	{
		CategoryInterface category = rootCategoryeEntity.getCategory();
		Collection<CategoryEntityInterface> relatedAttributeCategoryEntities = category.getRelatedAttributeCategoryEntityCollection();

		for (CategoryEntityInterface categoryEntity : relatedAttributeCategoryEntities)
		{
			StringBuffer columnNames = new StringBuffer();
			StringBuffer columnValues = new StringBuffer();
			StringBuffer columnNamesValues = new StringBuffer();

			// Fetch column names and column values for related category attributes.
			getColumnNamesAndValuesForRelatedCategoryAttributes(categoryEntity, columnNames, columnValues, columnNamesValues);

			CategoryAssociationInterface categoryAssociation = getCategoryAssociationWithRootCategoryEntity(rootCategoryeEntity, categoryEntity);

			if (categoryAssociation == null)
			{
				//pass the category entity this is  parent category entity of root categoryentity so we have to insertinto parent entity table
				insertRelatedAttributeRecordsForRootCategoryEntity(categoryEntity, columnNamesValues, recordsMap, hibernateDAO, id);
			}
			else
			{
				insertRelatedAttributeRecordsForCategoryEntity(categoryEntity, categoryAssociation, columnNames, columnValues, columnNamesValues,
						rootRecordId, recordsMap, hibernateDAO, id);
			}
		}
	}

	/**
	 * This method clubs column names and column values for related category attributes.
	 * @param categoryEntity
	 * @param columnNames
	 * @param columnValues
	 * @param columnNamesValues
	 */
	private void getColumnNamesAndValuesForRelatedCategoryAttributes(CategoryEntityInterface categoryEntity, StringBuffer columnNames,
			StringBuffer columnValues, StringBuffer columnNamesValues)
	{
		Collection<CategoryAttributeInterface> categoryAttributes = new HashSet<CategoryAttributeInterface>();
	
		categoryAttributes = categoryEntity.getCategoryAttributeCollection();		
		for (CategoryAttributeInterface categoryAttribute : categoryAttributes)
			{
				if (categoryAttribute.getIsVisible() != null && categoryAttribute.getIsVisible() == false)
				{
					String columnName = categoryAttribute.getAttribute().getColumnProperties().getName();
	
					if (columnNames.toString().length() > 0)
					{
						columnNames.append(", ");
						columnValues.append(", ");
						columnNamesValues.append(", ");
					}
					columnNames.append(columnName);
					columnValues.append("'" + categoryAttribute.getDefaultValue() + "'");
					columnNamesValues.append(columnName);
					columnNamesValues.append(" = ");
					columnNamesValues.append("'" + categoryAttribute.getDefaultValue() + "'");
				}
			}
		
	}

	/**
	 * This method returns a category association between root category entity and category entity passed to this method.
	 * @param rootCategoryeEntity
	 * @param categoryEntity
	 * @return category association between root category entity and category entity passed
	 */
	private CategoryAssociationInterface getCategoryAssociationWithRootCategoryEntity(CategoryEntityInterface rootCategoryeEntity,
			CategoryEntityInterface categoryEntity)
	{
		Collection<CategoryAssociationInterface> categoryAssociations = rootCategoryeEntity.getCategoryAssociationCollection();

		for (CategoryAssociationInterface categoryAssociation : categoryAssociations)
		{
			if (categoryAssociation.getTargetCategoryEntity().equals(categoryEntity))
			{
				return categoryAssociation;
			}
		}

		return null;
	}

	/**
	 * This method inserts records for related category attributes of root category entity.
	 * @param rootCategoryeEntity
	 * @param columnNamesValues
	 * @param recordsMap
	 * @param hibernateDAO
	 * @param userId
	 * @throws SQLException
	 */
	private void insertRelatedAttributeRecordsForRootCategoryEntity(CategoryEntityInterface rootCategoryeEntity, StringBuffer columnNamesValues,
			Map<String, List<Long>> recordsMap, HibernateDAO hibernateDAO, Long userId) throws SQLException
	{
		String entityTableName = rootCategoryeEntity.getEntity().getTableProperties().getName();
		List<Long> recordIdList = recordsMap.get(rootCategoryeEntity.getName());
		if(recordIdList!=null)
		{
			for (Long identifer : recordIdList)
			{
				String updateEntityQuery = "UPDATE " + entityTableName + " SET " + columnNamesValues + " WHERE IDENTIFIER = " + identifer;
				executeUpdateQuery(updateEntityQuery, userId, hibernateDAO);
			}
		}
	}

	/**
	 * This method inserts records for related category attributes of category entities other than root category entity.
	 * @param categoryEntity
	 * @param categoryAssociation
	 * @param columnNames
	 * @param columnValues
	 * @param relatedAttributeUpdateQuery
	 * @param rootRecordId
	 * @param recordsMap
	 * @param hibernateDAO
	 * @param userId
	 * @throws SQLException
	 * @throws DynamicExtensionsSystemException
	 */
	private void insertRelatedAttributeRecordsForCategoryEntity(CategoryEntityInterface categoryEntity,
			CategoryAssociationInterface categoryAssociation, StringBuffer columnNames, StringBuffer columnValues,
			StringBuffer relatedAttributeUpdateQuery, Long rootRecordId, Map<String, List<Long>> recordsMap, HibernateDAO hibernateDAO, Long userId)
			throws SQLException, DynamicExtensionsSystemException
	{
		String categoryEntityTableName = categoryEntity.getTableProperties().getName();
		String entityTableName = categoryEntity.getEntity().getTableProperties().getName();
		String categoryEntityForeignKey = categoryAssociation.getConstraintProperties().getTargetEntityKey();

		List<Long> sourceEntityId = recordsMap.get(categoryAssociation.getCategoryEntity().getName());

		if (recordsMap.get(categoryEntity.getName()) != null)
		{
			//insertRelatedAttributesRecordForCategoryEntitiesOnUI(recordsMap, categoryEntity, entityTableName, relatedAttributeUpdateQuery, userId, hibernateDAO, categoryEntityTableName, categoryEntityForeignKey, rootRecordId);
			List<Long> recordIdList = recordsMap.get(categoryEntity.getName());
			for (Long id : recordIdList)
			{
				String updateEntityQuery = "UPDATE " + entityTableName + " SET " + relatedAttributeUpdateQuery + " WHERE IDENTIFIER = " + id;
				executeUpdateQuery(updateEntityQuery, userId, hibernateDAO);

				String selectQuery = "SELECT IDENTIFIER FROM " + categoryEntityTableName + " WHERE " + RECORD_ID + " = " + id;
				List<Long> resultIdList = getResultIDList(selectQuery, "IDENTIFIER");

				if (resultIdList.size() == 0)
				{
					Long categoryIdentifier = entityManagerUtil.getNextIdentifier(categoryEntityTableName);
					String insertCategoryEntityQuery = "INSERT INTO " + categoryEntityTableName + " (IDENTIFIER, ACTIVITY_STATUS, " + RECORD_ID
							+ ", " + categoryEntityForeignKey + ") VALUES (" + categoryIdentifier + ", 'ACTIVE', " + id + ", " + rootRecordId + ")";
					executeUpdateQuery(insertCategoryEntityQuery, userId, hibernateDAO);
				}
			}
		}
		else
		{
			//insertRelatedAttributeRecordsForCategoryEntitiesInPath(recordsMap, categoryEntity, columnNames, columnValues, categoryEntityForeignKey, sourceEntityId, rootRecordId, entityTableName, categoryEntityTableName, userId, hibernateDAO);
			PathInterface path = categoryEntity.getPath();
			Collection<PathAssociationRelationInterface> pathAssociationRelations = path.getSortedPathAssociationRelationCollection();

			for (PathAssociationRelationInterface par : pathAssociationRelations)
			{
				AssociationInterface association = par.getAssociation();

				if (association.getTargetEntity().getId() != categoryEntity.getEntity().getId())
				{
					if (recordsMap.get(association.getTargetEntity().getName() + "[" + par.getTargetInstanceId() + "]") == null)
					{
						sourceEntityId = new ArrayList<Long>();
						for (Long sourceId : sourceEntityId)
						{
							Long entityIdentifier = entityManagerUtil.getNextIdentifier(association.getTargetEntity().getTableProperties().getName());
							String insertQuery = "INSERT INTO " + association.getTargetEntity().getTableProperties().getName()
									+ "(IDENTIFIER, ACTIVITY_STATUS, " + association.getConstraintProperties().getTargetEntityKey() + ") VALUES ("
									+ entityIdentifier + ", 'ACTIVE'," + sourceId + ")";
							executeUpdateQuery(insertQuery, userId, hibernateDAO);
							sourceEntityId.add(entityIdentifier);
						}

						recordsMap.put(association.getTargetEntity().getName() + "[" + par.getTargetInstanceId() + "]", sourceEntityId);
					}
					else
					{
						sourceEntityId = recordsMap.get(association.getTargetEntity().getName() + "[" + par.getTargetInstanceId() + "]");
					}
				}
				else
				{
					for (Long sourceId : sourceEntityId)
					{
						Long entityIdentifier = entityManagerUtil.getNextIdentifier(entityTableName);
						String insertEntityQuery = "INSERT INTO " + entityTableName + " (IDENTIFIER, ACTIVITY_STATUS, " + columnNames + ", "
								+ association.getConstraintProperties().getTargetEntityKey() + ") VALUES (" + entityIdentifier + ", " + "'ACTIVE', "
								+ columnValues + ", " + sourceId + ")";
						executeUpdateQuery(insertEntityQuery, userId, hibernateDAO);

						Long categoryIdentifier = entityManagerUtil.getNextIdentifier(categoryEntityTableName);
						String insertCategoryEntityQuery = "INSERT INTO " + categoryEntityTableName + " (IDENTIFIER, ACTIVITY_STATUS, " + RECORD_ID
								+ ", " + categoryEntityForeignKey + ") VALUES (" + categoryIdentifier + ", 'ACTIVE', " + entityIdentifier + ", "
								+ rootRecordId + ")";
						executeUpdateQuery(insertCategoryEntityQuery, userId, hibernateDAO);
					}
				}
			}
		}
	}

	/**
	 * @param recordsMap
	 * @param categoryEntity
	 * @param columnNames
	 * @param columnValues
	 * @param categoryEntityForeignKey
	 * @param sourceEntityId
	 * @param rootRecordId
	 * @param entityTableName
	 * @param categoryEntityTableName
	 * @param userId
	 * @param hibernateDAO
	 * @throws DynamicExtensionsSystemException
	 * @throws SQLException
	 */
	/*private void insertRelatedAttributeRecordsForCategoryEntitiesInPath(Map<String, List<Long>> recordsMap, CategoryEntityInterface categoryEntity, StringBuffer columnNames, StringBuffer columnValues, String categoryEntityForeignKey, List<Long> sourceEntityId, Long rootRecordId,
			String entityTableName, String categoryEntityTableName, Long userId, HibernateDAO hibernateDAO) throws DynamicExtensionsSystemException, SQLException
	{
		PathInterface path = categoryEntity.getPath();
		Collection<PathAssociationRelationInterface> pathAssociationRelations = path.getSortedPathAssociationRelationCollection();
		
		for (PathAssociationRelationInterface par: pathAssociationRelations)
		{
			AssociationInterface association = par.getAssociation();

			if (association.getTargetEntity().getId() != categoryEntity.getEntity().getId())
			{						
				if (recordsMap.get(association.getTargetEntity().getName()+"["+par.getTargetInstanceId()+"]") == null)
				{
					sourceEntityId = new ArrayList<Long>();
					for (Long sourceId: sourceEntityId)
					{
						Long entityIdentifier = entityManagerUtil.getNextIdentifier(association.getTargetEntity().getTableProperties().getName());
						String insertQuery = "INSERT INTO "+association.getTargetEntity().getTableProperties().getName()+"(IDENTIFIER, ACTIVITY_STATUS, "+association.getConstraintProperties().getTargetEntityKey()+") VALUES ("+entityIdentifier+", 'ACTIVE',"+sourceId+")";
						executeUpdateQuery(insertQuery, userId, hibernateDAO);
						sourceEntityId.add(entityIdentifier);
					}
					
					recordsMap.put(association.getTargetEntity().getName()+"["+par.getTargetInstanceId()+"]", sourceEntityId);
				}
				else
				{
					sourceEntityId = recordsMap.get(association.getTargetEntity().getName()+"["+par.getTargetInstanceId()+"]");
				}
			}
			else
			{
				for (Long sourceId: sourceEntityId)
				{
					Long entityIdentifier = entityManagerUtil.getNextIdentifier(entityTableName);
					String insertEntityQuery = "INSERT INTO "+entityTableName+" (IDENTIFIER, ACTIVITY_STATUS, "+columnNames+", "+association.getConstraintProperties().getTargetEntityKey()+") VALUES ("+entityIdentifier+", "+ "'ACTIVE', "+columnValues+", "+sourceId+")";
					executeUpdateQuery(insertEntityQuery, userId, hibernateDAO);
					
					Long categoryIdentifier = entityManagerUtil.getNextIdentifier(categoryEntityTableName);
					String insertCategoryEntityQuery = "INSERT INTO "+categoryEntityTableName+" (IDENTIFIER, ACTIVITY_STATUS, "+RECORD_ID+", "+categoryEntityForeignKey+") VALUES ("+categoryIdentifier+", 'ACTIVE', "+entityIdentifier+", "+rootRecordId+")";
					executeUpdateQuery(insertCategoryEntityQuery, userId, hibernateDAO);
				}
			}
		}
	}*/

	/**
	 * @param recordsMap
	 * @param categoryEntity
	 * @param entityTableName
	 * @param relatedAttributeUpdateQuery
	 * @param userId
	 * @param hibernateDAO
	 * @param categoryEntityTableName
	 * @param categoryEntityForeignKey
	 * @param rootRecordId
	 * @throws SQLException
	 * @throws DynamicExtensionsSystemException
	 */
	/*private void insertRelatedAttributesRecordForCategoryEntitiesOnUI(Map<String, List<Long>> recordsMap, CategoryEntityInterface categoryEntity, String entityTableName,
			StringBuffer relatedAttributeUpdateQuery, Long userId, HibernateDAO hibernateDAO, String categoryEntityTableName,
			String categoryEntityForeignKey, Long rootRecordId) throws SQLException, DynamicExtensionsSystemException
	{
		List<Long> recordIdList = recordsMap.get(categoryEntity.getName());
		for (Long id: recordIdList)
		{
			String updateEntityQuery = "UPDATE "+entityTableName+" SET "+relatedAttributeUpdateQuery+" WHERE IDENTIFIER = "+id;
			executeUpdateQuery(updateEntityQuery, userId, hibernateDAO);
			
			String selectQuery = "SELECT IDENTIFIER FROM "+categoryEntityTableName+" WHERE "+RECORD_ID+" = "+id;
			List<Long> resultIdList = getResultIDList(selectQuery, "IDENTIFIER");
			
			if (resultIdList.size() == 0)
			{
				Long categoryIdentifier = entityManagerUtil.getNextIdentifier(categoryEntityTableName);
				String insertCategoryEntityQuery = "INSERT INTO "+categoryEntityTableName+" (IDENTIFIER, ACTIVITY_STATUS, "+RECORD_ID+", "+categoryEntityForeignKey+") VALUES ("+categoryIdentifier+", 'ACTIVE', "+id+", "+rootRecordId+")";
				executeUpdateQuery(insertCategoryEntityQuery, userId, hibernateDAO);
			}
		}
	}*/

	/* (non-Javadoc)
	 * @see edu.common.dynamicextensions.entitymanager.CategoryManagerInterface#editData(edu.common.dynamicextensions.domaininterface.CategoryEntityInterface, java.util.Map, java.lang.Long)
	 */
	public boolean editData(CategoryEntityInterface rootcategoryEntity, Map<BaseAbstractAttributeInterface, Object> attributeValueMap, Long recordId,
			Long... userId) throws DynamicExtensionsApplicationException, DynamicExtensionsSystemException, SQLException
	{
		CategoryInterface category = rootcategoryEntity.getCategory();

		Long entityRecordId = getRootCategoryEntityRecordId(rootcategoryEntity, recordId);
		Long id = ((userId != null || userId.length > 0) ? userId[0] : null);
		List<Long> entityRecordIdList = new ArrayList<Long>();
		entityRecordIdList.add(entityRecordId);

		Map<AbstractAttributeInterface, Object> rootEntityRecordsMap = new HashMap<AbstractAttributeInterface, Object>();
		populateRootEntityRecordMap(rootcategoryEntity, rootEntityRecordsMap, attributeValueMap);

		HibernateDAO hibernateDAO = null;

		Boolean isEdited = false;
		Stack<String> categoryEntityReverseQueryStack = new Stack<String>();

		try
		{
			DAOFactory factory = DAOFactory.getInstance();
			hibernateDAO = (HibernateDAO) factory.getDAO(Constants.HIBERNATE_DAO);
			hibernateDAO.openSession(null);

			// Clear all records from entity table.
			EntityManagerInterface entityManager = EntityManager.getInstance();
			isEdited = entityManager.editDataForHeirarchy(rootcategoryEntity.getEntity(), rootEntityRecordsMap, entityRecordId, hibernateDAO, id);

			// Clear all records from category entity table.
			clearCategoryEntityData(rootcategoryEntity, recordId, categoryEntityReverseQueryStack, id, hibernateDAO);

			if (isEdited)
			{
				Map<String, Long> keyMap = new HashMap<String, Long>();
				Map<String, Long> fullKeyMap = new HashMap<String, Long>();
				Map<String, List<Long>> recordsMap = new HashMap<String, List<Long>>();

				keyMap.put(rootcategoryEntity.getName(), entityRecordId);
				fullKeyMap.put(rootcategoryEntity.getName(), entityRecordId);
				List<Long> idList = new ArrayList<Long>();
				idList.add(entityRecordId);
				recordsMap.put(rootcategoryEntity.getName(), idList);
				
				CategoryEntityInterface catEntity = rootcategoryEntity;
				//add parent's record id also as parent entiy tables are edited in editDataForHeirarchy
				while(catEntity.getParentCategoryEntity()!=null)
				{
					keyMap.put(catEntity.getParentCategoryEntity().getName(),entityRecordId);
					fullKeyMap.put(catEntity.getParentCategoryEntity().getName(),entityRecordId);

					List<Long> recoIdList	 = recordsMap.get(catEntity.getParentCategoryEntity().getName());
					if (recoIdList == null)
					{
						recoIdList = new ArrayList<Long>();
					}
					recoIdList.add(entityRecordId);
					recordsMap.put(catEntity.getParentCategoryEntity().getName(), recoIdList);
					catEntity = catEntity.getParentCategoryEntity();
					
				}
				

				for (CategoryAttributeInterface categoryAttribute : rootcategoryEntity.getAllCategoryAttributes())
				{
					attributeValueMap.remove(categoryAttribute);
				}

				boolean isMultipleRecords = false;
				boolean isNoCategoryAttributePresent = false;

				String entityForeignKeyColumnName = null;
				String categoryEntityForeignKeyColumnName = null;
				Long sourceCategoryEntityId = null;
				Long sourceEntityId = null;

				insertRecordsForCategoryEntityTree(entityForeignKeyColumnName, categoryEntityForeignKeyColumnName, sourceCategoryEntityId,
						sourceEntityId, rootcategoryEntity, attributeValueMap, keyMap, fullKeyMap, recordsMap, isMultipleRecords,
						isNoCategoryAttributePresent, hibernateDAO, id);

				Long rootCategoryEntityId = getRootCategoryEntityRecordId(category.getRootCategoryElement(), (Long) fullKeyMap.get(rootcategoryEntity
						.getName()));

				insertRecordsForRelatedAttributes(rootCategoryEntityId, category.getRootCategoryElement(), recordsMap, hibernateDAO, id);

				hibernateDAO.commit();
			}
		}
		catch (DynamicExtensionsApplicationException e)
		{
			//rollbackQueries(categoryEntityReverseQueryStack);
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

		return isEdited;
	}

	/**
	 * This helper method recursively inserts records for a single category entity 
	 * and all its category associations i.e. in turn for a whole category entity tree.
	 * @param entityForeignKeyColumnName
	 * @param categoryEntityForeignKeyColumnName
	 * @param sourceCategoryEntityIdentifier
	 * @param sourceEntityIdentifier
	 * @param categoryEntity
	 * @param dataValue
	 * @param keyMap
	 * @param fullKeyMap
	 * @param isMultipleRecords
	 * @param isNoCategoryAttributePresent
	 * @param hibernateDAO
	 * @throws DynamicExtensionsSystemException
	 * @throws SQLException
	 * @throws HibernateException
	 * @throws DynamicExtensionsApplicationException
	 * @throws UserNotAuthorizedException
	 * @throws DAOException
	 */
	private void insertRecordsForCategoryEntityTree(String entityForeignKeyColumnName, String categoryEntityForeignKeyColumnName,
			Long sourceCategoryEntityIdentifier, Long sourceEntityIdentifier, CategoryEntityInterface categoryEntity, Map dataValue,
			Map<String, Long> keyMap, Map<String, Long> fullKeyMap, Map<String, List<Long>> recordsMap, boolean isMultipleRecords,
			boolean isNoCategoryAttributePresent, HibernateDAO hibernateDAO, Long userId) throws DynamicExtensionsSystemException, SQLException,
			HibernateException, DynamicExtensionsApplicationException, UserNotAuthorizedException, DAOException
	{
		Object value = null;
		boolean isCategoryEntityRecordInserted = false;
		Map<AbstractAttributeInterface, Object> attributeMap = null;

		Set uiColumnSet = dataValue.keySet();
		Iterator uiColumnSetIter = uiColumnSet.iterator();

		while (uiColumnSetIter.hasNext())
		{
			BaseAbstractAttribute attribute = (BaseAbstractAttribute) uiColumnSetIter.next();
			value = dataValue.get(attribute);

			if (value == null)
			{
				continue;
			}

			if (attribute instanceof CategoryAttributeInterface && !isCategoryEntityRecordInserted)
			{
				String categoryEntityTableName = categoryEntity.getTableProperties().getName();//((CategoryAttribute) attribute).getCategoryEntity().getTableProperties().getName();

				Long entityIdentifier = null;
				EntityManagerInterface entityManager = EntityManager.getInstance();

				if (isNoCategoryAttributePresent)
				{
					attributeMap = null;
				}
				else
				{
					attributeMap = createAttributeMap(dataValue);
				}

				if (keyMap.get(((CategoryAttribute) attribute).getCategoryEntity().getName()) != null && !isMultipleRecords)
				{
					entityIdentifier = (Long) keyMap.get(((CategoryAttribute) attribute).getCategoryEntity().getName());
					//Edit data for entity heirarchy 
					entityManager.editDataForHeirarchy(categoryEntity.getEntity(), attributeMap,entityIdentifier,hibernateDAO,userId);
				}
				else
				{
					entityIdentifier = entityManager.insertDataForHeirarchy(categoryEntity.getEntity(), attributeMap, hibernateDAO, userId);
				
				}
				Long categoryEntityidentifier = null;
				//Check whether table is created for categoryentity 
				if(((CategoryEntity)categoryEntity).isCreateTable())
				{
					categoryEntityidentifier = entityManagerUtil.getNextIdentifier(categoryEntity.getTableProperties().getName());	
					String insertQueryForCategoryEntity = "INSERT INTO " + categoryEntityTableName + " (IDENTIFIER, ACTIVITY_STATUS, " + RECORD_ID
					+ ") VALUES (" + categoryEntityidentifier + ", 'ACTIVE', " + entityIdentifier + ")";
					executeUpdateQuery(insertQueryForCategoryEntity, userId, hibernateDAO);
				}				

				if (categoryEntityForeignKeyColumnName != null && entityForeignKeyColumnName != null)
				{
					if(((CategoryEntity)categoryEntity).isCreateTable())
					{
						String updateCategoryEntityQuery = "UPDATE " + categoryEntityTableName + " SET " + categoryEntityForeignKeyColumnName + " = "
						+ sourceCategoryEntityIdentifier + " WHERE IDENTIFIER = " + categoryEntityidentifier;
						executeUpdateQuery(updateCategoryEntityQuery, userId, hibernateDAO);
					}
						
					String updateEntityQuery = "UPDATE "
							+ ((CategoryAttribute) attribute).getCategoryEntity().getEntity().getTableProperties().getName() + " SET "
							+ entityForeignKeyColumnName + " = " + sourceEntityIdentifier + " WHERE IDENTIFIER = " + entityIdentifier;
					executeUpdateQuery(updateEntityQuery, userId, hibernateDAO);
				}

				CategoryEntityInterface catEntity = 	categoryEntity;
				keyMap.put(catEntity.getName(),entityIdentifier);
				fullKeyMap.put(catEntity.getName(),entityIdentifier);

				List<Long> recIdList = recordsMap.get(catEntity.getName());
				if (recIdList == null)
				{
					recIdList = new ArrayList<Long>();
				}
				recIdList.add(entityIdentifier);
				recordsMap.put(catEntity.getName(), recIdList);
				while(catEntity.getParentCategoryEntity()!=null)
				{
					keyMap.put(catEntity.getParentCategoryEntity().getName(),entityIdentifier);
					fullKeyMap.put(catEntity.getParentCategoryEntity().getName(),entityIdentifier);

					List<Long> recoIdList	 = recordsMap.get(catEntity.getParentCategoryEntity().getName());
					if (recoIdList == null)
					{
						recoIdList = new ArrayList<Long>();
					}
					recoIdList.add(entityIdentifier);
					recordsMap.put(catEntity.getParentCategoryEntity().getName(), recoIdList);
					catEntity = catEntity.getParentCategoryEntity();
					
				}
			
				isCategoryEntityRecordInserted = true;
			}
			else if (attribute instanceof CategoryAssociationInterface)
			{
				CategoryAssociationInterface categoryAssociation = (CategoryAssociationInterface) attribute;

				PathInterface path = categoryAssociation.getTargetCategoryEntity().getPath();
				Collection<PathAssociationRelationInterface> pathAssociationRelations = path.getSortedPathAssociationRelationCollection();

				Long sourceEntityId = (Long) fullKeyMap.get(categoryAssociation.getCategoryEntity().getName());
				String foreignKeyColumnName = new String();

				String selectQuery = "SELECT IDENTIFIER FROM " + categoryAssociation.getCategoryEntity().getTableProperties().getName() + " WHERE "
						+ RECORD_ID + " = " + sourceEntityId;

				List<Long> idList = getResultIDList(selectQuery, "IDENTIFIER");

				Long resultId = null;
				if (idList != null && idList.size() > 0)
				{
					resultId = idList.get(0);
				}

				Long sourceCategoryEntityId = resultId;

				String categoryForeignKeyColName = categoryAssociation.getConstraintProperties().getTargetEntityKey();

				EntityInterface entity = categoryAssociation.getTargetCategoryEntity().getEntity();

				for (PathAssociationRelationInterface par : pathAssociationRelations)
				{
					AssociationInterface association = par.getAssociation();

					foreignKeyColumnName = association.getConstraintProperties().getTargetEntityKey();

					if (association.getTargetEntity().getId() != entity.getId())
					{
						if (fullKeyMap.get(association.getTargetEntity().getName() + "[" + par.getTargetInstanceId() + "]") == null)
						{
							Long entityIdentifier = entityManagerUtil.getNextIdentifier(association.getTargetEntity().getTableProperties().getName());
							String insertQuery = "INSERT INTO " + association.getTargetEntity().getTableProperties().getName()
									+ "(IDENTIFIER, ACTIVITY_STATUS, " + association.getConstraintProperties().getTargetEntityKey() + ") VALUES ("
									+ entityIdentifier + ", 'ACTIVE'," + sourceEntityId + ")";
							executeUpdateQuery(insertQuery, userId, hibernateDAO);

							sourceEntityId = entityIdentifier;

							fullKeyMap.put(association.getTargetEntity().getName() + "[" + par.getTargetInstanceId() + "]", sourceEntityId);
							keyMap.put(association.getTargetEntity().getName() + "[" + par.getTargetInstanceId() + "]", sourceEntityId);

							List<Long> recIdList = recordsMap.get(association.getTargetEntity().getName() + "[" + par.getTargetInstanceId() + "]");
							if (recIdList == null)
							{
								recIdList = new ArrayList<Long>();
							}
							recIdList.add(entityIdentifier);
							recordsMap.put(association.getTargetEntity().getName() + "[" + par.getTargetInstanceId() + "]", recIdList);
						}
						else
						{
							sourceEntityId = (Long) fullKeyMap.get(association.getTargetEntity().getName() + "[" + par.getTargetInstanceId() + "]");
						}
					}
					else
					{
						sourceEntityId = (Long) fullKeyMap.get(association.getEntity().getName() + "[" + par.getSourceInstanceId() + "]");
					}
				}

				List<Map<BaseAbstractAttributeInterface, Object>> listOfMapsForContainedEntity = (List) value;

				Map<CategoryAttributeInterface, Object> categoryAttributeMap = new HashMap<CategoryAttributeInterface, Object>();
				Map<CategoryAssociationInterface, Object> categoryAssociationMap = new HashMap<CategoryAssociationInterface, Object>();

				for (Map<BaseAbstractAttributeInterface, Object> valueMapForContainedEntity : listOfMapsForContainedEntity)
				{
					Set<BaseAbstractAttributeInterface> keySet = valueMapForContainedEntity.keySet();
					Iterator<BaseAbstractAttributeInterface> iter = keySet.iterator();

					while (iter.hasNext())
					{
						Object obj = iter.next();
						if (obj instanceof CategoryAttributeInterface)
						{
							categoryAttributeMap.put((CategoryAttributeInterface) obj, valueMapForContainedEntity.get(obj));
						}
						else
						{
							categoryAssociationMap.put((CategoryAssociationInterface) obj, valueMapForContainedEntity.get(obj));
						}
					}

					if (listOfMapsForContainedEntity.size() > 1)
					{
						isMultipleRecords = true;
					}
					else
					{
						isMultipleRecords = false;
					}

					// Insert data for category attributes.
					if (categoryAttributeMap.size() == 0)
					{
						isNoCategoryAttributePresent = true;
						CategoryAttributeInterface dummyCategoryAttribute = DomainObjectFactory.getInstance().createCategoryAttribute();
						dummyCategoryAttribute.setCategoryEntity(categoryAssociation.getTargetCategoryEntity());
						categoryAttributeMap.put(dummyCategoryAttribute, "");

						insertRecordsForCategoryEntityTree(foreignKeyColumnName, categoryForeignKeyColName, sourceCategoryEntityId, sourceEntityId,
								categoryAssociation.getTargetCategoryEntity(), categoryAttributeMap, keyMap, fullKeyMap, recordsMap,
								isMultipleRecords, isNoCategoryAttributePresent, hibernateDAO, userId);

						isNoCategoryAttributePresent = false;
					}
					else
					{
						insertRecordsForCategoryEntityTree(foreignKeyColumnName, categoryForeignKeyColName, sourceCategoryEntityId, sourceEntityId,
								categoryAssociation.getTargetCategoryEntity(), categoryAttributeMap, keyMap, fullKeyMap, recordsMap,
								isMultipleRecords, isNoCategoryAttributePresent, hibernateDAO, userId);
					}
					categoryAttributeMap.clear();

					// Insert data for category associations.
					insertRecordsForCategoryEntityTree(foreignKeyColumnName, categoryForeignKeyColName, sourceCategoryEntityId, sourceEntityId,
							categoryAssociation.getTargetCategoryEntity(), categoryAssociationMap, keyMap, fullKeyMap, recordsMap, isMultipleRecords,
							isNoCategoryAttributePresent, hibernateDAO, userId);
					categoryAssociationMap.clear();

					fullKeyMap.putAll(keyMap);
					keyMap.remove(categoryAssociation.getTargetCategoryEntity().getName());
				}
			}
		}
	}

	/**
	 * This method returns the category data value map for the given root category entity.
	 * @param rootCategoryEntity
	 * @param recordId
	 * @return map of category entity data
	 * @throws DynamicExtensionsSystemException
	 * @throws DynamicExtensionsApplicationException
	 * @throws SQLException
	 */
	public Map<BaseAbstractAttributeInterface, Object> getRecordById(CategoryEntityInterface rootCategoryEntity, Long recordId)
			throws DynamicExtensionsSystemException, DynamicExtensionsApplicationException, SQLException
	{
		Map<BaseAbstractAttributeInterface, Object> categoryDataValueMap = new HashMap<BaseAbstractAttributeInterface, Object>();

		HibernateDAO hibernateDAO = null;

		try
		{
			DAOFactory factory = DAOFactory.getInstance();
			hibernateDAO = (HibernateDAO) factory.getDAO(Constants.HIBERNATE_DAO);
			hibernateDAO.openSession(null);
			retrieveRecords(rootCategoryEntity, categoryDataValueMap, recordId);
		}
		catch (Exception e)
		{
			throw (DynamicExtensionsSystemException) handleRollback(e, "Error while retrieving data", hibernateDAO, true);
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

		return categoryDataValueMap;
	}

	/**
	 * @param categoryEntity
	 * @param categoryDataValueMap
	 * @param rootCategoryEntityRecordId
	 * @throws SQLException
	 * @throws DynamicExtensionsSystemException
	 * @throws DynamicExtensionsApplicationException
	 */
	private void retrieveRecords(CategoryEntityInterface categoryEntity, Map<BaseAbstractAttributeInterface, Object> categoryDataValueMap,
			long rootCategoryEntityRecordId) throws SQLException, DynamicExtensionsSystemException, DynamicExtensionsApplicationException
	{
		Long recordId = null;
		String categoryEntityTableName = "";
		String selectRecordIdQuery = "";

		boolean isRecordIdNull = false;

		// Only the root category entity has the category object set in it.
		if (categoryEntity.getCategory() != null)
		{
			categoryEntityTableName = categoryEntity.getTableProperties().getName();

			selectRecordIdQuery = "SELECT " + RECORD_ID + " FROM " + categoryEntityTableName + " WHERE IDENTIFIER = " + rootCategoryEntityRecordId;

			List<Long> idList = getResultIDList(selectRecordIdQuery, RECORD_ID);

			if (idList != null && idList.size() > 0)
			{
				recordId = idList.get(0);
			}
		}

		// If entity model is different than category model then entity data is inserted
		// according to entity model and category data is inserted according to category model.
		// In this case recordId can be NULL in category entity table.
		if (recordId == null)
		{
			isRecordIdNull = true;
			recordId = rootCategoryEntityRecordId;
		}

		Map<AbstractAttributeInterface, Object> entityRecordsMap = new HashMap<AbstractAttributeInterface, Object>();
		entityRecordsMap.putAll(EntityManager.getInstance().getEntityRecordById(categoryEntity.getEntity(), recordId));
		
		//If root catentity has parent entity then get data from parent entitytable  with same record id
		CategoryEntityInterface objParentCatEntity = categoryEntity.getParentCategoryEntity();
		while(objParentCatEntity!=null)
		{
			Map<AbstractAttributeInterface, Object> innerValueMap = EntityManager.getInstance().getEntityRecordById(objParentCatEntity.getEntity(), recordId);
			if(innerValueMap!=null)
			entityRecordsMap.putAll(innerValueMap);
			objParentCatEntity = objParentCatEntity.getParentCategoryEntity();
			
		}

		if (!isAllRelatedCategoryAttributesCollection(categoryEntity))
		{
			for (CategoryAttributeInterface categoryAttribute : categoryEntity.getAllCategoryAttributes())
			{
				categoryDataValueMap.put(categoryAttribute, entityRecordsMap.get(categoryAttribute.getAttribute()));
			}
		}

		Collection<CategoryAssociationInterface> categoryAssociationCollection = new ArrayList<CategoryAssociationInterface>(categoryEntity
				.getCategoryAssociationCollection());

		for (CategoryAssociationInterface categoryAssociation : categoryAssociationCollection)
		{
			CategoryEntityInterface targetCategoryEntity = categoryAssociation.getTargetCategoryEntity();
			if (!isAllRelatedCategoryAttributesCollection(targetCategoryEntity) && (((CategoryEntity)targetCategoryEntity).isCreateTable()))
			{
				categoryEntityTableName = targetCategoryEntity.getTableProperties().getName();

				if (isRecordIdNull)
				{
					String selectQuery = "SELECT IDENTIFIER FROM " + categoryAssociation.getCategoryEntity().getTableProperties().getName()
							+ " WHERE " + RECORD_ID + " = " + recordId;

					List<Long> idList = getResultIDList(selectQuery, "IDENTIFIER");

					if (idList != null && idList.size() > 0)
					{
						rootCategoryEntityRecordId = idList.get(0);
					}
				}

				selectRecordIdQuery = "SELECT " + RECORD_ID + " FROM " + categoryEntityTableName + " WHERE "
						+ categoryAssociation.getConstraintProperties().getTargetEntityKey() + " = " + rootCategoryEntityRecordId;

				List<Map<BaseAbstractAttributeInterface, Object>> innerList = new ArrayList<Map<BaseAbstractAttributeInterface, Object>>();
				categoryDataValueMap.put(categoryAssociation, innerList);

				List<Long> recordIdList = getResultIDList(selectRecordIdQuery, RECORD_ID);

				for (Long recId : recordIdList)
				{
					Map<BaseAbstractAttributeInterface, Object> innerMap = new HashMap<BaseAbstractAttributeInterface, Object>();
					innerList.add(innerMap);

					retrieveRecords(targetCategoryEntity, innerMap, recId);
				}
			}
		}
	}

	/**
	 * @param entity
	 * @param dataValue
	 * @return
	 */
	private Map<CategoryEntityInterface, Map> initialiseEntityValueMap(Map<BaseAbstractAttributeInterface, ?> dataValue)
	{
		Map<CategoryEntityInterface, Map> categoryEntityMap = new HashMap<CategoryEntityInterface, Map>();

		for (BaseAbstractAttributeInterface baseAbstractAttributeInterface : dataValue.keySet())
		{
			CategoryEntityInterface attributeCategoryEntity = null;
			if (baseAbstractAttributeInterface instanceof CategoryAttributeInterface)
			{
				attributeCategoryEntity = ((CategoryAttributeInterface) baseAbstractAttributeInterface).getCategoryEntity();
			}
			else
			{
				attributeCategoryEntity = ((CategoryAssociationInterface) baseAbstractAttributeInterface).getCategoryEntity();
			}
			Object value = dataValue.get(baseAbstractAttributeInterface);

			Map<BaseAbstractAttributeInterface, Object> entityDataValueMap = (Map) categoryEntityMap.get(attributeCategoryEntity);
			if (entityDataValueMap == null)
			{
				entityDataValueMap = new HashMap<BaseAbstractAttributeInterface, Object>();
				categoryEntityMap.put(attributeCategoryEntity, entityDataValueMap);
			}
			entityDataValueMap.put(baseAbstractAttributeInterface, value);
		}

		return categoryEntityMap;
	}

	/**
	 * @param categoryEntity
	 * @return
	 */
	private List<CategoryEntityInterface> getParentEntityList(CategoryEntityInterface categoryEntity)
	{

		//As here the parent category entity whose table is not created  is blocked so its not added in list 
		List<CategoryEntityInterface> categoryEntityList = new ArrayList<CategoryEntityInterface>();
		categoryEntityList.add(categoryEntity);
		CategoryEntity objCategoryEntity = (CategoryEntity) categoryEntity.getParentCategoryEntity();
		while (objCategoryEntity != null && objCategoryEntity.isCreateTable())
		{
			categoryEntityList.add(0, categoryEntity.getParentCategoryEntity());
			categoryEntity = categoryEntity.getParentCategoryEntity();
		}

		return categoryEntityList;
	}

	/**
	 * This method populates record map for entity which belongs to root category entity.
	 * @param rootCategoryEntity
	 * @param rootEntityRecordsMap
	 * @param attributeValueMap
	 */
	private void populateRootEntityRecordMap(CategoryEntityInterface rootCategoryEntity,
			Map<AbstractAttributeInterface, Object> rootEntityRecordsMap, Map<BaseAbstractAttributeInterface, Object> attributeValueMap)
	{
		Set<Entry<BaseAbstractAttributeInterface, Object>> categoryDataMapEntries = attributeValueMap.entrySet();

		AbstractAttributeInterface abstractAttribute = null;
		Object entityValue = null;
		CategoryAttributeInterface categoryAttribute;

		BaseAbstractAttributeInterface baseAbstractAttribute;

		Object categoryValue;
		for (Entry<BaseAbstractAttributeInterface, Object> entry : categoryDataMapEntries)
		{
			baseAbstractAttribute = entry.getKey();
			categoryValue = entry.getValue();
			if (baseAbstractAttribute instanceof CategoryAttributeInterface)
			{
				categoryAttribute = (CategoryAttributeInterface) baseAbstractAttribute;
				abstractAttribute = categoryAttribute.getAttribute();
				entityValue = categoryValue;
				
				//add root cat entity and its parent category entity's attribute
				for (CategoryAttributeInterface rootcategoryAttribute : rootCategoryEntity.getAllCategoryAttributes())
				{
					if((categoryAttribute == rootcategoryAttribute) && categoryAttribute.getIsVisible() == null)
					{
						rootEntityRecordsMap.put(abstractAttribute, entityValue);
					}
					
				}
			}
		}

		for (CategoryAssociationInterface categoryAssociation : rootCategoryEntity.getCategoryAssociationCollection())
		{
			//add all root catentity's association
			for (AssociationInterface association : categoryAssociation.getCategoryEntity().getEntity().getAssociationCollection())
			{
				rootEntityRecordsMap.put(association, new ArrayList());
			}
			//also add any association which are related to parent entity's association--association between rootcategoryentity's parententity and another class whose category association is created   
			EntityInterface entity = categoryAssociation.getCategoryEntity().getEntity();
			while(entity.getParentEntity()!=null)
			{
				for (AssociationInterface association : entity.getParentEntity().getAssociationCollection())
				{
					if(association.getTargetEntity() == categoryAssociation.getTargetCategoryEntity().getEntity())
					 {
						rootEntityRecordsMap.put(association, new ArrayList());
					 }
				}
				entity = entity.getParentEntity();
			}
		}
	}

	/**
	 * @param categoryEntity
	 * @param recordId
	 * @param hibernateDAO 
	 * @throws SQLException
	 */
	private void clearCategoryEntityData(CategoryEntityInterface categoryEntity, Long recordId, Stack<String> categoryEntityReverseQueryStack,
			Long userId, HibernateDAO hibernateDAO) throws SQLException
	{
		CategoryEntityInterface catEntity = categoryEntity;

		for (CategoryAssociationInterface categoryAssociation : catEntity.getCategoryAssociationCollection())
		{
			if (categoryAssociation.getTargetCategoryEntity().getChildCategories().size() != 0)
			{
				String selectQuery = "SELECT IDENTIFIER FROM " + categoryAssociation.getTargetCategoryEntity().getTableProperties().getName()
						+ " WHERE " + categoryAssociation.getConstraintProperties().getTargetEntityKey() + " = " + recordId;

				List<Long> recordIdList = getResultIDList(selectQuery, "IDENTIFIER");
				for (Long recId : recordIdList)
				{
					clearCategoryEntityData(categoryAssociation.getTargetCategoryEntity(), recId, categoryEntityReverseQueryStack, userId,
							hibernateDAO);
					String deleteQuery = "DELETE FROM " + categoryAssociation.getTargetCategoryEntity().getTableProperties().getName()
							+ " WHERE IDENTIFIER = " + recId;
					executeUpdateQuery(deleteQuery, userId, hibernateDAO);
					categoryEntityReverseQueryStack.push(deleteQuery);
				}
			}
			else
			{
				String deleteQuery = "DELETE FROM " + categoryAssociation.getTargetCategoryEntity().getTableProperties().getName() + " WHERE "
						+ categoryAssociation.getConstraintProperties().getTargetEntityKey() + " = " + recordId;
				executeUpdateQuery(deleteQuery, userId, hibernateDAO);
				categoryEntityReverseQueryStack.push(deleteQuery);
			}
		}
	}

	/**
	 *
	 */
	protected DynamicExtensionBaseQueryBuilder getQueryBuilderInstance()
	{
		return queryBuilder;
	}

	/**
	 * @param rootCategoryEntityInterface
	 * @param recordId
	 * @return the record id of the hook entity
	 * @throws SQLException
	 * @throws DynamicExtensionsSystemException
	 */
	private Long getRootCategoryEntityRecordId(CategoryEntityInterface rootCategoryEntityInterface, Long recordId) throws SQLException,
			DynamicExtensionsSystemException
	{
		StringBuffer query = new StringBuffer();
		query.append(SELECT_KEYWORD + WHITESPACE + RECORD_ID + WHITESPACE + FROM_KEYWORD + WHITESPACE
				+ rootCategoryEntityInterface.getTableProperties().getName() + WHITESPACE + WHERE_KEYWORD + WHITESPACE + IDENTIFIER + EQUAL
				+ recordId);

		Long rootEntityRecordId = null;

		List<Long> resultList = getResultIDList(query.toString(), RECORD_ID);
		if (resultList.size() > 0)
		{
			rootEntityRecordId = (Long) resultList.get(0);
		}
		return rootEntityRecordId;
	}

	/* (non-Javadoc)
	 * @see edu.common.dynamicextensions.entitymanager.CategoryManagerInterface#isPermissibleValuesSubsetValid(edu.common.dynamicextensions.domaininterface.AttributeInterface, java.util.List)
	 */
	public boolean isPermissibleValuesSubsetValid(UserDefinedDEInterface userDefinedDE, List<String> desiredPermissibleValues)
	{
		boolean arePermissibleValuesCorrect = true;

		if (userDefinedDE != null)
		{
			List<String> attributePermissibleValues = new ArrayList<String>();

			for (PermissibleValueInterface pv : userDefinedDE.getPermissibleValueCollection())
			{
				attributePermissibleValues.add(pv.getValueAsObject().toString());
			}

			for (String s : desiredPermissibleValues)
			{
				if (!attributePermissibleValues.contains(s))
				{
					arePermissibleValuesCorrect = false;
				}
			}
		}

		return arePermissibleValuesCorrect;
	}

	/**
	 * This method executes a SQL query and returns a list of identifier, record identifier
	 * depending upon column name passed.
	 * @param query
	 * @param columnName
	 * @return a list of identifier, record identifier depending upon column name passed.
	 * @throws SQLException
	 */
	private List<Long> getResultIDList(String query, String columnName) throws SQLException
	{
		List<Long> recordIdList = new ArrayList<Long>();
		Connection conn = null;
		Statement statement = null;
		ResultSet resultSet = null;
		Object value = null;

		try
		{
			conn = DBUtil.getConnection();
			statement = conn.createStatement();
			resultSet = statement.executeQuery(query);

			while (resultSet.next())
			{
				value = resultSet.getObject(columnName);
				recordIdList.add(new Long(value.toString()));
			}
		}
		catch (Exception e)
		{
			throw new SQLException(e.getMessage());
		}
		finally
		{
			try
			{
				resultSet.close();
				statement.close();
			}
			catch (SQLException e)
			{
				e.printStackTrace();
				throw new SQLException(e.getMessage());
			}
		}

		return recordIdList;
	}

	/**
	 * This method executes a SQL query.
	 * @param query
	 * @throws SQLException
	 */
	private void executeUpdateQuery(String query, Long userId, HibernateDAO hibernateDAO) throws SQLException
	{
		Connection conn = null;
		Statement statement = null;

		try
		{
			conn = DBUtil.getConnection();
			statement = conn.createStatement();
			statement.executeUpdate(query);

			DESQLAuditInterface auditInterface = DomainObjectFactory.getInstance().createDESQLAudit(userId, query);
			hibernateDAO.insert(auditInterface, null, false, false);
		}
		catch (Exception e)
		{
			throw new SQLException(e.getMessage());
		}
		finally
		{
			try
			{
				statement.close();
			}
			catch (SQLException e)
			{
				e.printStackTrace();
				throw new SQLException(e.getMessage());
			}
		}
	}

	/**
	 * 
	 * @param dataValueMap
	 * @return
	 */
	private Map<AbstractAttributeInterface, Object> createAttributeMap(Map<BaseAbstractAttributeInterface, Object> dataValueMap)
	{
		Map<AbstractAttributeInterface, Object> attributeMap = new HashMap<AbstractAttributeInterface, Object>();

		Iterator<BaseAbstractAttributeInterface> attributeIterator = dataValueMap.keySet().iterator();

		while (attributeIterator.hasNext())
		{
			Object obj = attributeIterator.next();
			if (obj instanceof CategoryAttributeInterface)
			{
				attributeMap.put(((CategoryAttributeInterface) obj).getAttribute(), dataValueMap.get(obj));
			}
		}

		return attributeMap;
	}

	//	private void rollbackQueries(Stack<String> reverseQueryStack) throws DynamicExtensionsSystemException
	//	{
	//		if (reverseQueryStack != null && !reverseQueryStack.isEmpty())
	//		{
	//			try
	//			{
	//				while (!reverseQueryStack.empty())
	//				{
	//					String query = (String) reverseQueryStack.pop();
	//					executeUpdateQuery(query);
	//				}
	//			}
	//			catch (SQLException e)
	//			{
	//				throw new DynamicExtensionsSystemException(e.getMessage());
	//			}
	//		}
	//	}

}