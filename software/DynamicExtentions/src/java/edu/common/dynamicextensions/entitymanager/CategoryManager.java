
package edu.common.dynamicextensions.entitymanager;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Stack;
import java.util.Map.Entry;

import edu.common.dynamicextensions.dao.impl.DynamicExtensionDAO;
import edu.common.dynamicextensions.domain.AbstractAttribute;
import edu.common.dynamicextensions.domain.BaseAbstractAttribute;
import edu.common.dynamicextensions.domain.BooleanAttributeTypeInformation;
import edu.common.dynamicextensions.domain.CategoryAttribute;
import edu.common.dynamicextensions.domain.CategoryEntity;
import edu.common.dynamicextensions.domain.CategoryEntityRecord;
import edu.common.dynamicextensions.domain.DateAttributeTypeInformation;
import edu.common.dynamicextensions.domain.DomainObjectFactory;
import edu.common.dynamicextensions.domain.PathAssociationRelationInterface;
import edu.common.dynamicextensions.domaininterface.AbstractAttributeInterface;
import edu.common.dynamicextensions.domaininterface.AbstractMetadataInterface;
import edu.common.dynamicextensions.domaininterface.AssociationInterface;
import edu.common.dynamicextensions.domaininterface.AttributeInterface;
import edu.common.dynamicextensions.domaininterface.AttributeMetadataInterface;
import edu.common.dynamicextensions.domaininterface.AttributeTypeInformationInterface;
import edu.common.dynamicextensions.domaininterface.BaseAbstractAttributeInterface;
import edu.common.dynamicextensions.domaininterface.CategoryAssociationInterface;
import edu.common.dynamicextensions.domaininterface.CategoryAttributeInterface;
import edu.common.dynamicextensions.domaininterface.CategoryEntityInterface;
import edu.common.dynamicextensions.domaininterface.CategoryInterface;
import edu.common.dynamicextensions.domaininterface.DynamicExtensionBaseDomainObjectInterface;
import edu.common.dynamicextensions.domaininterface.EntityInterface;
import edu.common.dynamicextensions.domaininterface.PathInterface;
import edu.common.dynamicextensions.domaininterface.PermissibleValueInterface;
import edu.common.dynamicextensions.domaininterface.SemanticPropertyInterface;
import edu.common.dynamicextensions.domaininterface.UserDefinedDEInterface;
import edu.common.dynamicextensions.exception.DynamicExtensionsApplicationException;
import edu.common.dynamicextensions.exception.DynamicExtensionsSystemException;
import edu.common.dynamicextensions.util.CategoryGenerationUtil;
import edu.common.dynamicextensions.util.DynamicExtensionsUtility;
import edu.common.dynamicextensions.util.FormulaCalculator;
import edu.common.dynamicextensions.util.global.DEConstants;
import edu.common.dynamicextensions.util.global.DEConstants.Cardinality;
import edu.wustl.cab2b.server.cache.EntityCache;
import edu.wustl.dao.HibernateDAO;
import edu.wustl.dao.JDBCDAO;
import edu.wustl.dao.daofactory.DAOConfigFactory;
import edu.wustl.dao.exception.DAOException;
import edu.wustl.dao.query.generator.ColumnValueBean;

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
	private final EntityManagerUtil entityManagerUtil = new EntityManagerUtil();

	/**
	 * Empty Constructor.
	 */
	protected CategoryManager()
	{
		super();
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
	@Override
	protected void logFatalError(Exception exception, AbstractMetadataInterface abstractMetadata)
	{
		// TODO Auto-generated method stub
	}

	/**
	 * Method to persist a category.
	 * @param categry interface for Category
	 * @throws DynamicExtensionsSystemException, DynamicExtensionsApplicationException
	 */
	public CategoryInterface persistCategory(CategoryInterface categry)
			throws DynamicExtensionsSystemException, DynamicExtensionsApplicationException
	{
		persistDynamicExtensionObject(categry);
		EntityCache.getInstance().addCategoryToCache(categry);
		return categry;
	}

	/**
	 * Method to persist category meta-data.
	 * @param category interface for Category
	 * @throws DynamicExtensionsSystemException, DynamicExtensionsApplicationException
	 */
	public CategoryInterface persistCategoryMetadata(CategoryInterface category)
			throws DynamicExtensionsSystemException, DynamicExtensionsApplicationException
	{
		CategoryInterface savedCategory = (CategoryInterface) persistDynamicExtensionObjectMetdata(category);
		EntityCache.getInstance().addCategoryToCache(category);
		return savedCategory;
	}

	/* (non-Javadoc)
	 * @see edu.common.dynamicextensions.entitymanager.AbstractMetadataManager#preProcess(edu.common.dynamicextensions.domaininterface.DynamicExtensionBaseDomainObjectInterface, java.util.List, edu.wustl.common.dao.HibernateDAO, java.util.List)
	 */
	@Override
	protected void preProcess(DynamicExtensionBaseDomainObjectInterface dyExtBsDmnObj,
			List<String> revQueries, List<String> queries) throws DynamicExtensionsSystemException,
			DynamicExtensionsApplicationException
	{
		CategoryInterface category = (CategoryInterface) dyExtBsDmnObj;

		getDynamicQueryList(category, revQueries, queries);
	}

	/**
	 * This method gets a list of dynamic tables creation queries.
	 * @param category
	 * @param revQueries
	 * @param queries
	 * @return
	 * @throws DynamicExtensionsSystemException
	 * @throws DynamicExtensionsApplicationException
	 */
	protected List<String> getDynamicQueryList(CategoryInterface category, List<String> revQueries,
			List<String> queries) throws DynamicExtensionsSystemException,
			DynamicExtensionsApplicationException
	{
		List<CategoryEntityInterface> catEntities = new ArrayList<CategoryEntityInterface>();

		// Use HashMap instead of List to ensure entity list contains unique entity only.
		HashMap<String, CategoryEntityInterface> catEntObjects = new HashMap<String, CategoryEntityInterface>();
		DynamicExtensionsUtility.getUnsavedCategoryEntityList(category.getRootCategoryElement(),
				catEntObjects);
		Iterator<String> keySetIter = catEntObjects.keySet().iterator();
		while (keySetIter.hasNext())
		{
			catEntities.add(catEntObjects.get(keySetIter.next()));
		}

		for (CategoryEntityInterface categoryEntity : catEntities)
		{
			List<String> createQueries = queryBuilder.getCreateCategoryQueryList(categoryEntity,
					revQueries);
			if (createQueries != null && !createQueries.isEmpty())
			{
				queries.addAll(createQueries);
			}
		}

		for (CategoryEntityInterface categoryEntity : catEntities)
		{
			List<String> createQueries = queryBuilder.getUpdateCategoryEntityQueryList(
					categoryEntity, revQueries);
			if (createQueries != null && !createQueries.isEmpty())
			{
				queries.addAll(createQueries);
			}
		}

		// Use HashMap instead of List to ensure entity list contains unique entity only.
		List<CategoryEntityInterface> savedCatEntities = new ArrayList<CategoryEntityInterface>();
		catEntObjects = new HashMap<String, CategoryEntityInterface>();
		DynamicExtensionsUtility.getSavedCategoryEntityList(category.getRootCategoryElement(),
				catEntObjects);
		keySetIter = catEntObjects.keySet().iterator();
		while (keySetIter.hasNext())
		{
			savedCatEntities.add(catEntObjects.get(keySetIter.next()));
		}

		HibernateDAO hibernateDAO = null;
		try
		{
			hibernateDAO = DynamicExtensionsUtility.getHibernateDAO();

			for (CategoryEntityInterface categoryEntity : savedCatEntities)
			{
				CategoryEntity dbaseCopy = (CategoryEntity) hibernateDAO.retrieveById(
						CategoryEntity.class.getCanonicalName(), categoryEntity.getId());

				// Only for category entity for which table is getting created.
				if (dbaseCopy.isCreateTable())
				{
					List<String> updateQueries = queryBuilder.getUpdateEntityQueryList(
							(CategoryEntity) categoryEntity, dbaseCopy, revQueries);

					if (updateQueries != null && !updateQueries.isEmpty())
					{
						queries.addAll(updateQueries);
					}
				}
			}
		}
		catch (DAOException exception)
		{
			throw new DynamicExtensionsSystemException("Not able to retrieve Object.", exception);
		}
		finally
		{
			if (hibernateDAO != null)
			{
				try
				{
					DynamicExtensionsUtility.closeHibernateDAO(hibernateDAO);
				}
				catch (DAOException e)
				{
					throw new DynamicExtensionsSystemException(
							"Exception encountered while closing session.", e);
				}
			}
		}

		return queries;
	}

	/* (non-Javadoc)
	 * @see edu.common.dynamicextensions.entitymanager.AbstractMetadataManager#postProcess(java.util.List, java.util.List, java.util.Stack)
	 */
	@Override
	protected void postProcess(List<String> queries, List<String> revQueries,
			Stack<String> rlbkQryStack) throws DynamicExtensionsSystemException
	{
		queryBuilder.executeQueries(queries, revQueries, rlbkQryStack);
	}

	/* (non-Javadoc)
	 * @see edu.common.dynamicextensions.entitymanager.CategoryManagerInterface#insertData(edu.common.dynamicextensions.domaininterface.CategoryInterface, java.util.Map)
	 */
	public Long insertData(CategoryInterface category,
			Map<BaseAbstractAttributeInterface, Object> dataValue, Long... userId)
			throws DynamicExtensionsApplicationException, DynamicExtensionsSystemException
	{
		List<Map<BaseAbstractAttributeInterface, Object>> dataValMaps = new ArrayList<Map<BaseAbstractAttributeInterface, Object>>();
		dataValMaps.add(dataValue);
		Long identifier = ((userId != null && userId.length > 0) ? userId[0] : null);
		List<Long> recordIds = insertData(category, dataValMaps, identifier);

		return recordIds.get(0);
	}

	/* (non-Javadoc)
	 * @see edu.common.dynamicextensions.entitymanager.CategoryManagerInterface#insertData(edu.common.dynamicextensions.domaininterface.CategoryInterface, java.util.List)
	 */
	public List<Long> insertData(CategoryInterface category,
			List<Map<BaseAbstractAttributeInterface, Object>> dataValMaps, Long... userId)
			throws DynamicExtensionsApplicationException, DynamicExtensionsSystemException
	{
		List<Long> recordIds = new ArrayList<Long>();

		JDBCDAO jdbcDao = null;
		try
		{
			jdbcDao = DynamicExtensionsUtility.getJDBCDAO();
			Long identifier = ((userId != null && userId.length > 0) ? userId[0] : null);

			for (Map<BaseAbstractAttributeInterface, Object> dataValMap : dataValMaps)
			{
				Long recordId = insertDataForHierarchy(category.getRootCategoryElement(),
						dataValMap, jdbcDao, identifier);
				recordIds.add(recordId);
			}
			jdbcDao.commit();
		}
		catch (DAOException e)
		{
			throw (DynamicExtensionsSystemException) handleRollback(e,
					"Error while inserting data", jdbcDao, true);
		}
		catch (SQLException e)
		{
			throw (DynamicExtensionsSystemException) handleRollback(e,
					"Error while inserting data", jdbcDao, true);
		}
		finally
		{
			try
			{
				DynamicExtensionsUtility.closeJDBCDAO(jdbcDao);
			}
			catch (DAOException e)
			{
				throw (DynamicExtensionsSystemException) handleRollback(e, "Error while closing",
						jdbcDao, true);
			}
		}

		return recordIds;
	}

	/**
	 * This method inserts the data for hierarchy of category entities one by one.
	 * @param catEntity
	 * @param dataValue
	 * @param jdbcDao
	 * @param userId
	 * @return
	 * @throws DynamicExtensionsSystemException
	 * @throws DynamicExtensionsApplicationException
	 * @throws SQLException
	 */
	private Long insertDataForHierarchy(CategoryEntityInterface catEntity,
			Map<BaseAbstractAttributeInterface, Object> dataValue, JDBCDAO jdbcDao, Long... userId)
			throws DynamicExtensionsSystemException, DynamicExtensionsApplicationException,
			SQLException
	{
		List<CategoryEntityInterface> catEntities = getParentEntityList(catEntity);
		Map<CategoryEntityInterface, Map<BaseAbstractAttributeInterface, Object>> entityValMap = initialiseEntityValueMap(dataValue);
		Long parentRecId = null;
		Long parntCatRecId = null;
		Long identifier = ((userId != null && userId.length > 0) ? userId[0] : null);

		for (CategoryEntityInterface categoryEntity : catEntities)
		{
			Map<BaseAbstractAttributeInterface, Object> valueMap = entityValMap.get(categoryEntity);

			// If parent category entity table not created, then add its attribute map to value map.
			CategoryEntity parntCatEntity = (CategoryEntity) categoryEntity
					.getParentCategoryEntity();
			while (parntCatEntity != null && !parntCatEntity.isCreateTable())
			{
				Map<BaseAbstractAttributeInterface, Object> innerValMap = entityValMap
						.get(parntCatEntity);

				if (innerValMap != null)
				{
					valueMap.putAll(innerValMap);
				}

				parntCatEntity = (CategoryEntity) parntCatEntity.getParentCategoryEntity();
			}

			parentRecId = insertDataForSingleCategoryEntity(categoryEntity, valueMap, dataValue,
					jdbcDao, parentRecId, identifier);
			parntCatRecId = getRootCategoryRecordId(categoryEntity, parentRecId, jdbcDao);
		}

		return parntCatRecId;
	}

	/**
	 * @param catEntity
	 * @param parentRecId
	 * @return
	 * @throws SQLException
	 * @throws DynamicExtensionsSystemException
	 */
	private Long getRootCategoryRecordId(CategoryEntityInterface catEntity, Long parentRecId,
			JDBCDAO jdbcDao) throws SQLException, DynamicExtensionsSystemException
	{
		CategoryInterface category = catEntity.getCategory();
		CategoryEntityInterface rootCatEntity = category.getRootCategoryElement();

		StringBuffer query = new StringBuffer();
		query.append(SELECT_KEYWORD + WHITESPACE + IDENTIFIER + WHITESPACE + FROM_KEYWORD
				+ WHITESPACE + rootCatEntity.getTableProperties().getName() + WHITESPACE
				+ WHERE_KEYWORD + WHITESPACE + RECORD_ID + EQUAL + QUESTION_MARK);
		LinkedList<ColumnValueBean> queryDataList = new LinkedList<ColumnValueBean>();
		queryDataList.add(new ColumnValueBean(RECORD_ID, parentRecId));
		Long rootCERecId = null;

		List<Long> results = getResultIDList(query.toString(), IDENTIFIER, jdbcDao,queryDataList);
		if (!results.isEmpty())
		{
			rootCERecId = results.get(0);
		}

		return rootCERecId;
	}

	/**
	 * This method inserts data for a single category entity.
	 * @param catEntity
	 * @param dataValue
	 * @param jdbcDao
	 * @param userId
	 * @return
	 * @throws DynamicExtensionsSystemException
	 * @throws DynamicExtensionsApplicationException
	 * @throws SQLException
	 */
	private Long insertDataForSingleCategoryEntity(CategoryEntityInterface catEntity,
			Map<BaseAbstractAttributeInterface, Object> dataValue,
			Map<BaseAbstractAttributeInterface, Object> fullDataValue, JDBCDAO jdbcDao,
			Long parentRecId, Long... userId) throws DynamicExtensionsSystemException,
			DynamicExtensionsApplicationException, SQLException
	{

		Map<String, Long> keyMap = new HashMap<String, Long>();
		Map<String, Long> fullKeyMap = new HashMap<String, Long>();
		Map<String, List<Long>> records = new HashMap<String, List<Long>>();
		Long identifier = ((userId != null && userId.length > 0) ? userId[0] : null);


		HibernateDAO hibernateDao = null;
		String rootCatEntName = null;

		try
		{
			hibernateDao = DynamicExtensionsUtility.getHibernateDAO();
			if (catEntity == null)
			{
				throw new DynamicExtensionsSystemException("Input to insert data is null");
			}
			CategoryInterface category = catEntity.getCategory();
			CategoryEntityInterface rootCatEntity = category.getRootCategoryElement();
			rootCatEntName = DynamicExtensionsUtility
					.getCategoryEntityName(rootCatEntity.getName());

			// If root category entity does not have any attribute, or all its category attributes
			// are related attributes, then explicitly insert identifier into entity table and
			// insert this identifier as record identifier in category entity table.
			if (rootCatEntity.getCategoryAttributeCollection().size() == 0
					|| isAllRelatedCategoryAttributesCollection(rootCatEntity))
			{
				// Insert blank record in all parent entity tables of root category entity so use insertDataForHeirarchy and add all keys to keymap, recordmap, fullkeymap.
				Map<AbstractAttributeInterface, Object> attributes = new HashMap<AbstractAttributeInterface, Object>();
				EntityManagerInterface entityManager = EntityManager.getInstance();
				//Long entityId = entityManager.insertDataForHeirarchy(catEntity.getEntity(), attributes,
				//jdbcDao, identifier);
				Long entityId = entityManager.insertData(catEntity.getEntity(), attributes,
						hibernateDao, identifier);
				keyMap.put(rootCatEntName, entityId);
				fullKeyMap.put(rootCatEntName, entityId);

				List<Long> identifiers = new ArrayList<Long>();
				identifiers.add(entityId);
				records.put(rootCatEntName, identifiers);

				while (catEntity.getParentCategoryEntity() != null)
				{
					String parentCatEntName = DynamicExtensionsUtility
							.getCategoryEntityName(catEntity.getParentCategoryEntity().getName());
					keyMap.put(parentCatEntName, entityId);
					fullKeyMap.put(parentCatEntName, entityId);

					List<Long> recordIds = records.get(parentCatEntName);
					if (recordIds == null)
					{
						recordIds = new ArrayList<Long>();
					}

					recordIds.add(entityId);
					records.put(parentCatEntName, recordIds);
					catEntity = catEntity.getParentCategoryEntity();
				}

				Long catEntId = entityManagerUtil.getNextIdentifier(rootCatEntity
						.getTableProperties().getName());

				// Query to insert record into category entity table.
				String insertQuery = INSERT_INTO_KEYWORD
						+ rootCatEntity.getTableProperties().getName()
						+ " (IDENTIFIER, ACTIVITY_STATUS, " + RECORD_ID + ") VALUES (?,?,?)";
				//creating the column value beans according to DAO1.1.5
				LinkedList<ColumnValueBean> colValBeanList = createColumnValueBeanListForDataEntry(catEntId,entityId);
				executeUpdateQuery(insertQuery, identifier, jdbcDao,colValBeanList);
				logDebug("insertData", "categoryEntityTableInsertQuery is : " + insertQuery);
			}

			boolean areMultplRecrds = false;
			boolean isNoCatAttrPrsnt = false;

			String entityFKColName = null;
			String catEntFKColName = null;
			Long srcCatEntityId = null;
			Long srcEntityId = null;

			// Separate out category attribute and category association. The map coming from UI can contain both in any order,
			// but we require to insert record for category attribute first for root category entity.
			Map<CategoryAttributeInterface, Object> catAttributes = new HashMap<CategoryAttributeInterface, Object>();
			Map<CategoryAssociationInterface, Object> catAssociations = new HashMap<CategoryAssociationInterface, Object>();

			Set<BaseAbstractAttributeInterface> keySet = dataValue.keySet();
			Iterator<BaseAbstractAttributeInterface> iter = keySet.iterator();
			while (iter.hasNext())
			{
				Object obj = iter.next();
				if (obj instanceof CategoryAttributeInterface)
				{
					catAttributes.put((CategoryAttributeInterface) obj, dataValue.get(obj));
				}
				else
				{
					catAssociations.put((CategoryAssociationInterface) obj, dataValue.get(obj));
				}
			}

			// Insert records for category attributes.
			insertRecordsForCategoryEntityTree(entityFKColName, catEntFKColName, srcCatEntityId,
					srcEntityId, catEntity, catAttributes, keyMap, fullKeyMap, records,
					areMultplRecrds, isNoCatAttrPrsnt, jdbcDao, hibernateDao, false, null,
					identifier);

			// Insert records for category associations.
			insertRecordsForCategoryEntityTree(entityFKColName, catEntFKColName, srcCatEntityId,
					srcEntityId, catEntity, catAssociations, keyMap, fullKeyMap, records,
					areMultplRecrds, isNoCatAttrPrsnt, jdbcDao, hibernateDao, false, null,
					identifier);

			Long rootCERecId = getRootCategoryEntityRecordId(category.getRootCategoryElement(),
					fullKeyMap.get(rootCatEntName), jdbcDao);

			insertRecordsForRelatedAttributes(fullDataValue, rootCERecId, category
					.getRootCategoryElement(), records, jdbcDao, hibernateDao, identifier);
			hibernateDao.commit();

			if (parentRecId == null)
			{
				identifier = keyMap.get(rootCatEntName);
			}
			else
			{
				identifier = parentRecId;
			}
		}
		catch (DAOException e)
		{
			throw new DynamicExtensionsApplicationException(
					"Exception encountered while inserting records!");
		}
		finally
		{
			if (hibernateDao != null)
			{
				try
				{
					DynamicExtensionsUtility.closeHibernateDAO(hibernateDao);
				}
				catch (DAOException e)
				{
					throw new DynamicExtensionsApplicationException(
							"Exception encountered while inserting records for related attributes!");
				}
			}
		}

		return identifier;
	}

	/**
	 * This method checks whether category attributes in a category entity's
	 * parent are related attributes or not. It true, it inserts records for the same first.
	 * @param rootCatEntity
	 * @param records
	 * @param jdbcDAO
	 * @param userId
	 * @throws SQLException
	 * @throws DynamicExtensionsSystemException
	 * @throws DynamicExtensionsApplicationException
	 */
	private void insertAllParentRelatedCategoryAttributesCollection(
			Map<BaseAbstractAttributeInterface, Object> valueMap,
			CategoryEntityInterface rootCatEntity, Map<String, List<Long>> records,
			 HibernateDAO hibernateDao, Long userId) throws SQLException,
			DynamicExtensionsSystemException, DynamicExtensionsApplicationException
	{
		try
		{
			Map<BaseAbstractAttribute, Object> attrVsValues = new HashMap<BaseAbstractAttribute, Object>();
			// Get all attributes including parent's attributes.
			Collection<CategoryAttributeInterface> catAttributes = rootCatEntity
					.getAllCategoryAttributes();
			for (CategoryAttributeInterface catAttribute : catAttributes)
			{
				if ((catAttribute.getIsRelatedAttribute() != null && catAttribute
						.getIsRelatedAttribute())
						&& (!catAttribute.getAbstractAttribute().getEntity().equals(
								rootCatEntity.getEntity())))
				{
					// In case of related attributes, check if it is parent's attribute.

					StringBuffer columnNames = new StringBuffer();
					StringBuffer columnValues = new StringBuffer();
					StringBuffer columnNamesValues = new StringBuffer();

					AttributeInterface attribute = catAttribute.getAbstractAttribute().getEntity()
							.getAttributeByName(catAttribute.getAbstractAttribute().getName());

					// Fetch column names and column values for related category attributes.
					populateColumnNamesAndValues(valueMap, attribute, catAttribute, columnNames,
							columnValues, columnNamesValues, attrVsValues);

					attrVsValues.put((BaseAbstractAttribute) attribute, columnValues);

					//String entTableName = attribute.getEntity().getTableProperties().getName();
					List<Long> recordIds = records.get(DynamicExtensionsUtility
							.getCategoryEntityName(rootCatEntity.getName()));
					if (recordIds != null && columnNamesValues.length() > 0)
					{
						for (Long identifer : recordIds)
						{
							String packageName = null;
							packageName = getPackageName(rootCatEntity.getEntity(), packageName);

							String entityClassName = packageName + "."
									+ rootCatEntity.getEntity().getName();
							Object object = hibernateDao.retrieveById(entityClassName, identifer);
							for (BaseAbstractAttribute attr : attrVsValues.keySet())
							{
								Object value = attrVsValues.get(attr);
								setRelatedAttributeValues(entityClassName, attr, value, object);
							}
							hibernateDao.update(object);
						}
					}
				}
			}
		}
		catch (Exception e)
		{
			throw new DynamicExtensionsApplicationException(
					"Exception encountered while inserting records for related attributes!", e);
		}
	}

	private void setRelatedAttributeValues(String entityClassName, BaseAbstractAttribute attr,
			Object value, Object object) throws DynamicExtensionsApplicationException
	{
		String dataType = ((AttributeMetadataInterface) attr).getAttributeTypeInformation()
				.getDataType();
		try
		{
			setObjectProperty((AbstractAttribute) attr, dataType, Class.forName(entityClassName),
					value.toString(), object);
		}
		catch (Exception e)
		{
			throw new DynamicExtensionsApplicationException(
					"Exception encountered while inserting records for related attributes!", e);
		}
	}

	/**
	 * This method checks if all category attributes in a category entity are related attributes.
	 * @param catEntity
	 * @return true if all category attributes are related attributes, false otherwise
	 */
	private boolean isAllRelatedCategoryAttributesCollection(CategoryEntityInterface catEntity)
	{
		Collection<CategoryAttributeInterface> catAttributes = catEntity.getAllCategoryAttributes();
		boolean flag = true;
		for (CategoryAttributeInterface catAttribute : catAttributes)
		{
			if (catAttribute.getIsRelatedAttribute() == null
					|| !catAttribute.getIsRelatedAttribute())
			{
				flag = false;
				break;
			}
		}

		return flag;
	}

	/**
	 * This method checks if all category attributes in a category entity are related attributes.
	 * @param catEntity
	 * @return true if all category attributes are related attributes, false otherwise
	 */
	private boolean isAllRelatedCategoryAttributesCollection(
			Map<CategoryAttributeInterface, Object> catAttributes)
	{
		boolean flag = true;
		for (CategoryAttributeInterface catAttribute : catAttributes.keySet())
		{
			if (catAttribute.getIsRelatedAttribute() == null
					|| !catAttribute.getIsRelatedAttribute())
			{
				flag = false;
				break;
			}
		}

		return flag;
	}

	/**
	 * This method checks whether all attributes are invisible type related attributes
	 * or visible type related attributes or all are normal attribute.
	 * @param catEntity
	 * @return
	 */
	private boolean isAllRelatedInvisibleCategoryAttributesCollection(
			CategoryEntityInterface catEntity)
	{
		Collection<CategoryAttributeInterface> catAttributes = catEntity.getAllCategoryAttributes();
		if (catAttributes == null || catAttributes.isEmpty())
		{
			return false;
		}

		for (CategoryAttributeInterface catAttribute : catAttributes)
		{
			if (catAttribute.getIsRelatedAttribute() == null
					|| !catAttribute.getIsRelatedAttribute())
			{
				return false;
			}
			else if (catAttribute.getIsRelatedAttribute()
					&& (catAttribute.getIsVisible() != null && catAttribute.getIsVisible()))
			{
				return false;
			}
		}

		return true;
	}

	/**
	 * Insert records for related attributes in each category entity.
	 * @param rootRecordId
	 * @param rootCatEntity
	 * @param records
	 * @param jdbcDAO
	 * @param identifier
	 * @throws SQLException
	 * @throws DynamicExtensionsSystemException
	 * @throws DynamicExtensionsApplicationException
	 */
	private void insertRecordsForRelatedAttributes(
			Map<BaseAbstractAttributeInterface, Object> valueMap, Long rootRecordId,
			CategoryEntityInterface rootCatEntity, Map<String, List<Long>> records,
			JDBCDAO jdbcDao, HibernateDAO hibernateDao, Long identifier) throws SQLException,
			DynamicExtensionsSystemException, DynamicExtensionsApplicationException
	{
		CategoryInterface category = rootCatEntity.getCategory();

		// Get a collection of category entities having a collection of related attributes.
		Collection<CategoryEntityInterface> catEntWithRA = category
				.getRelatedAttributeCategoryEntityCollection();

		// Call this method for root category entity's parent's related attributes.
		insertAllParentRelatedCategoryAttributesCollection(valueMap, rootCatEntity, records,
				hibernateDao, identifier);

		for (CategoryEntityInterface categoryEntity : catEntWithRA)
		{
			StringBuffer columnNames = new StringBuffer();
			StringBuffer columnValues = new StringBuffer();
			StringBuffer colNamesValues = new StringBuffer();

			Map<BaseAbstractAttribute, Object> attrVsValues = new HashMap<BaseAbstractAttribute, Object>();
			// Fetch column names and column values for related category attributes.
			getColumnNamesAndValuesForRelatedCategoryAttributes(valueMap, categoryEntity,
					columnNames, columnValues, colNamesValues, records,hibernateDao,
					identifier, attrVsValues);

			CategoryAssociationInterface catAssociation = getCategoryAssociationWithTreeParentCategoryEntity(
					categoryEntity.getTreeParentCategoryEntity(), categoryEntity);

			if (catAssociation == null && categoryEntity.equals(rootCatEntity))
			{
				// insert records for related category attributes of root category entity.
				insertRelatedAttributeRecordsForRootCategoryEntity(categoryEntity, colNamesValues,
						records,hibernateDao, attrVsValues, identifier);
			}
			else if (catAssociation != null)
			{
				insertRelatedAttributeRecordsForCategoryEntity(categoryEntity, catAssociation,
						colNamesValues, rootRecordId, records,jdbcDao,
						identifier, attrVsValues, hibernateDao);
			}
		}
	}

	/**
	 * This method clubs column names and column values for related category attributes.
	 * @param catEntity
	 * @param columnNames
	 * @param columnValues
	 * @param colNamesValues
	 * @throws DynamicExtensionsSystemException
	 * @throws SQLException
	 * @throws DynamicExtensionsApplicationException
	 */
	private void getColumnNamesAndValuesForRelatedCategoryAttributes(
			Map<BaseAbstractAttributeInterface, Object> valueMap,
			CategoryEntityInterface catEntity, StringBuffer columnNames, StringBuffer columnValues,
			StringBuffer colNamesValues, Map<String, List<Long>> records,
			HibernateDAO hibernateDao, Long userId, Map<BaseAbstractAttribute, Object> attrVsValues)
			throws DynamicExtensionsSystemException, SQLException,
			DynamicExtensionsApplicationException
	{
		Collection<CategoryAttributeInterface> catAttributes  = catEntity.getCategoryAttributeCollection();
		for (CategoryAttributeInterface catAttribute : catAttributes)
		{
			if (catAttribute.getIsRelatedAttribute() != null
					&& catAttribute.getIsRelatedAttribute())
			{
				AttributeInterface attribute = catAttribute.getAbstractAttribute().getEntity()
						.getAttributeByName(catAttribute.getAbstractAttribute().getName());

				if (catAttribute.getAbstractAttribute() instanceof AssociationInterface)
				{
					populateAndInsertRecordForRelatedMultiSelectCategoryAttribute(
							catAttribute, attribute, records, userId, hibernateDao);
				}
				else
				{
					populateColumnNamesAndValues(valueMap, attribute, catAttribute, columnNames,
							columnValues, colNamesValues, attrVsValues);
				}
			}
		}
	}

	/**
	 * This method populates and inserts records for related multiselect category attribute.
	 * @param catAttribute
	 * @param attribute
	 * @param records
	 * @param userId
	 * @param hibernateDao
	 * @param jdbcDAO
	 * @throws DynamicExtensionsSystemException
	 * @throws SQLException
	 * @throws DynamicExtensionsApplicationException
	 */
	private void populateAndInsertRecordForRelatedMultiSelectCategoryAttribute(
			CategoryAttributeInterface catAttribute, AttributeInterface attribute,
			Map<String, List<Long>> records, Long userId,HibernateDAO hibernateDao)
			throws DynamicExtensionsSystemException, SQLException,
			DynamicExtensionsApplicationException
	{
		try
		{
			AssociationInterface association = (AssociationInterface) catAttribute
					.getAbstractAttribute();
			EntityInterface targetEntity = association.getTargetEntity();

			List<Long> ids = records.get(DynamicExtensionsUtility
					.getCategoryEntityName(catAttribute.getCategoryEntity().getName()));
			for (Long id : ids)
			{
				String packageName = null;
				packageName = getPackageName(association.getEntity(), packageName);

				String sourceObjectClassName = packageName + "."
						+ association.getEntity().getName();
				String targetObjectClassName = packageName + "."
						+ association.getTargetEntity().getName();

				Object sourceObject = hibernateDao.retrieveById(sourceObjectClassName, id);

				Object targetObject = null;

				// Create a new instance.
				Class targetObjectClass = Class.forName(packageName + "." + targetEntity.getName());
				Constructor targetObjectConstructor = targetObjectClass.getConstructor();
				targetObject = targetObjectConstructor.newInstance();

				Object value = catAttribute.getDefaultValue();
				setRelatedAttributeValues(targetObjectClassName, (BaseAbstractAttribute) attribute,
						value, targetObject);

				String sourceRoleName = association.getSourceRole().getName();
				sourceRoleName = sourceRoleName.substring(0, 1).toUpperCase()
						+ sourceRoleName.substring(1, sourceRoleName.length());

				String targetRoleName = association.getTargetRole().getName();
				targetRoleName = targetRoleName.substring(0, 1).toUpperCase()
						+ targetRoleName.substring(1, targetRoleName.length());
				hibernateDao.insert(targetObject);
				Object associatedObjects = invokeGetterMethod(sourceObject.getClass(),
						targetRoleName, sourceObject);
				Collection<Object> containedObjects = null;
				if (associatedObjects == null)
				{
					containedObjects = new HashSet<Object>();
				}
				else
				{
					containedObjects = (Collection) associatedObjects;
				}
				containedObjects.add(targetObject);
				invokeSetterMethod(sourceObject.getClass(), targetRoleName, Class
						.forName("java.util.Collection"), sourceObject, containedObjects);
				hibernateDao.update(sourceObject);
				invokeSetterMethod(targetObject.getClass(), sourceRoleName, Class
						.forName(sourceObjectClassName), targetObject, sourceObject);
				hibernateDao.update(targetObject);
			}
		}
		catch (Exception e)
		{
			throw new DynamicExtensionsApplicationException(
					"Exception encountered while inserting records for related attributes!", e);
		}
	}

	/**
	 * @param attribute
	 * @param catAttribute
	 * @param columnNames
	 * @param columnValues
	 * @param colNamesValues
	 * @throws DynamicExtensionsSystemException
	 * @throws DynamicExtensionsApplicationException
	 */
	private void populateColumnNamesAndValues(Map<BaseAbstractAttributeInterface, Object> valueMap,
			AttributeInterface attribute, CategoryAttributeInterface catAttribute,
			StringBuffer columnNames, StringBuffer columnValues, StringBuffer colNamesValues,
			Map<BaseAbstractAttribute, Object> attrVsValues)
			throws DynamicExtensionsSystemException, DynamicExtensionsApplicationException
	{
		String defaultValue = null;
		String columnName = attribute.getColumnProperties().getName();
		AttributeTypeInformationInterface attrTypeInfo = attribute.getAttributeTypeInformation();

		// If attribute type information is date type whose default value is not getting set
		// as if it is read only then skip it, do not set in query.
		if (!(attrTypeInfo instanceof DateAttributeTypeInformation && catAttribute
				.getDefaultValue() == null))
		{
			if (!columnNames.equals(""))
			{
				columnNames.append(", ");
				columnValues.append(", ");
				colNamesValues.append(", ");
			}
			defaultValue = catAttribute.getDefaultValue();
			//Setting the default value to empty string since null value not updated in db for numeric and string values.
			if (defaultValue == null)
			{
				defaultValue = "";
			}
			if (catAttribute.getIsCalculated() != null && catAttribute.getIsCalculated())
			{
				FormulaCalculator formulaCalculator = new FormulaCalculator();
				String formulaResultValue = formulaCalculator.evaluateFormula(valueMap,
						catAttribute, catAttribute.getCategoryEntity().getCategory(), 1);
				if (formulaResultValue != null)
				{
					defaultValue = formulaResultValue;
				}
			}

			if (attrTypeInfo instanceof BooleanAttributeTypeInformation)
			{
				defaultValue = DynamicExtensionsUtility
						.getValueForCheckBox(DynamicExtensionsUtility
								.isCheckBoxChecked(defaultValue));
			}

			// Replace any single and double quotes value with a proper escape character.
			defaultValue = DynamicExtensionsUtility.getEscapedStringValue(defaultValue);
			columnNames.append(columnName);
			String appName = DynamicExtensionDAO.getInstance().getAppName();
			String dbType = DAOConfigFactory.getInstance().getDAOFactory(appName).getDataBaseType();
			if (dbType.equals(DEConstants.ORACLE_DATABASE)
					&& attrTypeInfo instanceof DateAttributeTypeInformation)
			{

				columnValues.append(DynamicExtensionsUtility
						.getDefaultDateForRelatedCategoryAttribute(attribute, catAttribute
								.getDefaultValue()));
				colNamesValues.append(columnName);
				colNamesValues.append(" = ");
				colNamesValues.append(DynamicExtensionsUtility
						.getDefaultDateForRelatedCategoryAttribute(attribute, catAttribute
								.getDefaultValue()));
			}
			else
			{
				columnValues.append("'" + defaultValue + "'");
				colNamesValues.append(columnName);
				colNamesValues.append(" = '");
				colNamesValues.append(defaultValue + "'");
			}
		}
		attrVsValues.put((BaseAbstractAttribute) attribute, defaultValue);
	}

	/**
	 * This method returns a category association between root category entity
	 * and category entity passed to this method.
	 * @param treeParentCatEntity
	 * @param catEntity
	 * @return category association between root category entity and category entity passed
	 */
	private CategoryAssociationInterface getCategoryAssociationWithTreeParentCategoryEntity(
			CategoryEntityInterface treeParentCatEntity, CategoryEntityInterface catEntity)
	{
		// for root category entity, its tree parent category entity will be null.
		if (treeParentCatEntity == null)
		{
			return null;
		}

		Collection<CategoryAssociationInterface> catAssociations = treeParentCatEntity
				.getCategoryAssociationCollection();

		for (CategoryAssociationInterface catAssociation : catAssociations)
		{
			if (catAssociation.getTargetCategoryEntity() != null
					&& catAssociation.getTargetCategoryEntity().equals(catEntity))
			{
				return catAssociation;
			}
		}

		return null;
	}

	/**
	 * This method inserts records for related category attributes of root category entity.
	 * @param rootCatEntity
	 * @param colNamesValues
	 * @param records
	 * @param hibernateDao
	 * @param attrVsValues
	 * @param jdbcDAO
	 * @param userId
	 * @throws SQLException
	 * @throws DynamicExtensionsSystemException
	 * @throws DynamicExtensionsApplicationException
	 */
	private void insertRelatedAttributeRecordsForRootCategoryEntity(
			CategoryEntityInterface rootCatEntity, StringBuffer colNamesValues,
			Map<String, List<Long>> records,HibernateDAO hibernateDao,
			Map<BaseAbstractAttribute, Object> attrVsValues, Long userId)
			throws DynamicExtensionsSystemException, DynamicExtensionsApplicationException
	{
		try
		{
			List<Long> recordIds = records.get(DynamicExtensionsUtility
					.getCategoryEntityName(rootCatEntity.getName()));
			if (recordIds != null && colNamesValues.length() > 0)
			{
				for (Long identifer : recordIds)
				{
					String packageName = null;
					packageName = getPackageName(rootCatEntity.getEntity(), packageName);
					// Update entity query.
					String entityClassName = packageName + "."
							+ rootCatEntity.getEntity().getName();

					Object object = hibernateDao.retrieveById(entityClassName, identifer);
					for (BaseAbstractAttribute attr : attrVsValues.keySet())
					{
						Object value = attrVsValues.get(attr);
						setRelatedAttributeValues(entityClassName, attr, value, object);
					}
					hibernateDao.update(object);
				}
			}
		}
		catch (Exception e)
		{
			throw new DynamicExtensionsApplicationException(
					"Exception encountered while inserting records for related attributes!", e);
		}
	}

	/**
	 * This method inserts records for related category attributes of category entities
	 * other than root category entity.
	 * @param catEntity
	 * @param catAssociation
	 * @param columnNames
	 * @param columnValues
	 * @param colNamesValues
	 * @param rootRecId
	 * @param records
	 * @param jdbcDAO
	 * @param userId
	 * @throws SQLException
	 * @throws DynamicExtensionsSystemException
	 */
	private void insertRelatedAttributeRecordsForCategoryEntity(CategoryEntityInterface catEntity,
			CategoryAssociationInterface catAssociation, StringBuffer colNamesValues, Long rootRecId,
			Map<String, List<Long>> records, JDBCDAO jdbcDao, Long userId,
			Map<BaseAbstractAttribute, Object> attrVsValues, HibernateDAO hibernateDao)
			throws SQLException, DynamicExtensionsSystemException,
			DynamicExtensionsApplicationException
	{
		try
		{
			String catEntTblName = catEntity.getTableProperties().getName();
			String catEntFK = catAssociation.getConstraintProperties()
					.getTgtEntityConstraintKeyProperties().getTgtForiegnKeyColumnProperties()
					.getName();

			List<Long> srcEntityId = records.get(DynamicExtensionsUtility
					.getCategoryEntityName(catAssociation.getCategoryEntity().getName()));
			String catEntityName = DynamicExtensionsUtility.getCategoryEntityName(catEntity
					.getName());
			if (records.get(catEntityName) != null)
			{
				if (colNamesValues.length() > 0)
				{
					List<Long> recordIds = records.get(catEntityName);
					for (Long id : recordIds)
					{
						String packageName = null;
						packageName = getPackageName(catEntity.getEntity(), packageName);

						String entityClassName = packageName + "."
								+ catEntity.getEntity().getName();

						Object object = hibernateDao.retrieveById(entityClassName, id);

						for (BaseAbstractAttribute attr : attrVsValues.keySet())
						{
							Object value = attrVsValues.get(attr);
							setRelatedAttributeValues(entityClassName, attr, value, object);
						}

						hibernateDao.update(object);
						String selectQuery = SELECT_KEYWORD + IDENTIFIER + FROM_KEYWORD
								+ catEntTblName + WHERE_KEYWORD + RECORD_ID + " = ?";
						LinkedList<ColumnValueBean> queryDataList = new LinkedList<ColumnValueBean>();
						queryDataList.add(new ColumnValueBean(RECORD_ID, id));
						List<Long> resultIds = getResultIDList(selectQuery, "IDENTIFIER", jdbcDao,queryDataList);
						if (resultIds.isEmpty())
						{
							Long catEntId = entityManagerUtil.getNextIdentifier(catEntTblName);

							// Insert query for category entity table.
							String insertQuery = INSERT_INTO_KEYWORD + catEntTblName
									+ " (IDENTIFIER, ACTIVITY_STATUS, " + RECORD_ID + ", "
									+ catEntFK + ") VALUES (?,?,?,?)";

							LinkedList<ColumnValueBean> colValBeanList = createColumnValueBeanListForDataEntry(catEntId,id);
							ColumnValueBean bean4 = new ColumnValueBean(catEntFK, rootRecId);
							colValBeanList.add(bean4);
							executeUpdateQuery(insertQuery, userId, jdbcDao,colValBeanList);
						}
					}
				}
			}
			// If all attributes are invisible type related attributes, then only insert explicitly,
			// in all other cases the category entity name must available in map of records.
			else if (records.get(catEntityName) == null
					&& isAllRelatedInvisibleCategoryAttributesCollection(catEntity))
			{
				PathInterface path = catEntity.getPath();

				Collection<PathAssociationRelationInterface> pathAssoRel = path
						.getSortedPathAssociationRelationCollection();
				for (PathAssociationRelationInterface par : pathAssoRel)
				{
					AssociationInterface association = par.getAssociation();

					if (association.getTargetEntity().getId() == catEntity.getEntity().getId())
					{
						for (Long sourceId : srcEntityId)
						{
							String packageName = null;
							packageName = getPackageName(catEntity.getEntity(), packageName);

							String sourceObjectClassName = packageName + "."
									+ association.getEntity().getName();
							String targetObjectClassName = packageName + "."
									+ catEntity.getEntity().getName();
							Object sourceObject = hibernateDao.retrieveById(sourceObjectClassName,
									sourceId);
							Object targetObject = null;
							// Create a new instance.
							Class targetObjectClass = Class.forName(targetObjectClassName);
							Constructor targetObjectConstructor = targetObjectClass
									.getConstructor();
							targetObject = targetObjectConstructor.newInstance();

							for (BaseAbstractAttribute attr : attrVsValues.keySet())
							{
								Object value = attrVsValues.get(attr);
								setRelatedAttributeValues(targetObjectClassName, attr, value,
										targetObject);
							}

							String targetRoleName = association.getSourceRole().getName();
							targetRoleName = targetRoleName.substring(0, 1).toUpperCase()
									+ targetRoleName.substring(1, targetRoleName.length());

							// Put the target object in a collection and
							// save it as a collection of the source object.
							Set<Object> containedObjects = new HashSet<Object>();
							containedObjects.add(targetObject);

							invokeSetterMethod(sourceObject.getClass(), targetRoleName, Class
									.forName("java.util.Collection"), sourceObject,
									containedObjects);

							hibernateDao.insert(targetObject);
							hibernateDao.update(sourceObject);

							Method method = targetObject.getClass().getMethod("getId");
							Object updatedObject = method.invoke(targetObject);
							Long entityId = Long.valueOf(updatedObject.toString());

							Long catEntId = entityManagerUtil.getNextIdentifier(catEntTblName);

							// Insert query for category entity table.
							String insQryForCatEnt = INSERT_INTO_KEYWORD + catEntTblName
									+ " (IDENTIFIER, ACTIVITY_STATUS, " + RECORD_ID + ", "
									+ catEntFK + ") VALUES (?,?,?,?)";
							LinkedList<ColumnValueBean> colValBeanList = createColumnValueBeanListForDataEntry(catEntId,entityId);
							ColumnValueBean bean4 = new ColumnValueBean(catEntFK, rootRecId);
							colValBeanList.add(bean4);
							executeUpdateQuery(insQryForCatEnt, userId, jdbcDao,colValBeanList);
						}

					}
					else
					{
						if (records.get(association.getTargetEntity().getName() + "["
								+ par.getTargetInstanceId() + "]") == null)
						{
							srcEntityId = new ArrayList<Long>();
							for (Long sourceId : srcEntityId)
							{
								String packageName = null;
								packageName = getPackageName(association.getEntity(), packageName);

								String sourceObjectClassName = packageName + "."
										+ association.getEntity().getName();
								String targetObjectClassName = packageName + "."
										+ association.getTargetEntity().getName();

								Object sourceObject = hibernateDao.retrieveById(
										sourceObjectClassName, sourceId);
								Object targetObject = null;
								// Create a new instance.
								Class targetObjectClass = Class.forName(targetObjectClassName);
								Constructor targetObjectConstructor = targetObjectClass
										.getConstructor();
								targetObject = targetObjectConstructor.newInstance();

								for (BaseAbstractAttribute attr : attrVsValues.keySet())
								{
									Object value = attrVsValues.get(attr);
									setRelatedAttributeValues(targetObjectClassName, attr, value,
											targetObject);
								}
								String targetRoleName = association.getSourceRole().getName();
								targetRoleName = targetRoleName.substring(0, 1).toUpperCase()
										+ targetRoleName.substring(1, targetRoleName.length());

								// Put the target object in a collection and
								// save it as a collection of the source object.
								Set<Object> containedObjects = new HashSet<Object>();
								containedObjects.add(targetObject);
								invokeSetterMethod(sourceObject.getClass(), targetRoleName, Class
										.forName("java.util.Collection"), sourceObject,
										containedObjects);
								hibernateDao.insert(targetObject);
								hibernateDao.update(sourceObject);
								Method method = targetObject.getClass().getMethod("getId");
								Object updatedObject = method.invoke(targetObject);
								Long entityId = Long.valueOf(updatedObject.toString());

								srcEntityId.add(entityId);
							}

							records.put(association.getTargetEntity().getName() + "["
									+ par.getTargetInstanceId() + "]", srcEntityId);
						}
						else
						{
							srcEntityId = records.get(association.getTargetEntity().getName() + "["
									+ par.getTargetInstanceId() + "]");
						}
					}
				}
			}
		}
		catch (Exception e)
		{
			throw new DynamicExtensionsApplicationException(
					"Exception encountered while inserting records for related attributes!", e);
		}
	}

	/* (non-Javadoc)
	 * @see edu.common.dynamicextensions.entitymanager.CategoryManagerInterface#editData(edu.common.dynamicextensions.domaininterface.CategoryEntityInterface, java.util.Map, java.lang.Long)
	 */
	public boolean editData(CategoryEntityInterface rootCatEntity,
			Map<BaseAbstractAttributeInterface, Object> dataValue, Long recordId, Long... userId)
			throws DynamicExtensionsApplicationException, DynamicExtensionsSystemException,
			SQLException
	{
		CategoryInterface category = rootCatEntity.getCategory();
		JDBCDAO jdbcDao = null;
		Boolean isEdited = false;
		HibernateDAO hibernateDao = null;
		Long prevEntityId = null;
		try
		{
			jdbcDao = DynamicExtensionsUtility.getJDBCDAO();
			hibernateDao = DynamicExtensionsUtility.getHibernateDAO();
			Long entityRecId = getRootCategoryEntityRecordId(rootCatEntity, recordId, jdbcDao);
			Long identifier = ((userId != null && userId.length > 0) ? userId[0] : null);
			List<Long> entityRecIds = new ArrayList<Long>();
			entityRecIds.add(entityRecId);

			Map<AbstractAttributeInterface, Object> rootCERecords = new HashMap<AbstractAttributeInterface, Object>();
			populateRootEntityRecordMap(rootCatEntity, rootCERecords, dataValue);

			CategoryEntityRecord entityRecord = new CategoryEntityRecord();
			prevEntityId = (Long) dataValue.get(entityRecord);
			// Roll back queries for category entity records.
			Stack<String> rlbkQryStack = new Stack<String>();

			// Clear all records from entity table.
			EntityManagerInterface entityManager = EntityManager.getInstance();
			isEdited = entityManager.editData(rootCatEntity.getEntity(), rootCERecords,
					entityRecId, hibernateDao, userId);

			// Clear all records from category entity table.
			clearCategoryEntityData(rootCatEntity, recordId, rlbkQryStack, identifier, jdbcDao);

			if (isEdited)
			{
				Map<String, Long> keyMap = new HashMap<String, Long>();
				Map<String, Long> fullKeyMap = new HashMap<String, Long>();
				Map<String, List<Long>> recordsMap = new HashMap<String, List<Long>>();
				String catEntityName = DynamicExtensionsUtility.getCategoryEntityName(rootCatEntity
						.getName());
				keyMap.put(catEntityName, entityRecId);
				fullKeyMap.put(catEntityName, entityRecId);
				List<Long> identifiers = new ArrayList<Long>();
				identifiers.add(entityRecId);
				recordsMap.put(catEntityName, identifiers);

				CategoryEntityInterface catEntity = rootCatEntity;

				// Add parent's record id also as parent entity tables are edited
				// in editDataForHeirarchy method.
				while (catEntity.getParentCategoryEntity() != null)
				{
					String parentCateEntName = DynamicExtensionsUtility
							.getCategoryEntityName(catEntity.getParentCategoryEntity().getName());
					keyMap.put(parentCateEntName, entityRecId);
					fullKeyMap.put(parentCateEntName, entityRecId);

					List<Long> recordIds = recordsMap.get(parentCateEntName);
					if (recordIds == null)
					{
						recordIds = new ArrayList<Long>();
					}
					recordIds.add(entityRecId);
					recordsMap.put(parentCateEntName, recordIds);

					catEntity = catEntity.getParentCategoryEntity();
				}

				for (CategoryAttributeInterface catAttribute : rootCatEntity
						.getAllCategoryAttributes())
				{
					dataValue.remove(catAttribute);
				}

				boolean areMultplRecrds = false;
				boolean isNoCatAttrPrsnt = false;

				//String entityFKColName = null;
				String catEntFKColName = null;
				Long srcCatEntityId = null;
				Long srcEntityId = null;
				isEdited = false;

				editRecordsForCategoryEntityTree(catEntFKColName, srcCatEntityId, srcEntityId,
						prevEntityId, rootCatEntity, dataValue, keyMap, fullKeyMap, recordsMap,
						areMultplRecrds, isNoCatAttrPrsnt, jdbcDao, hibernateDao, isEdited, null,
						identifier);

				Long rootCatEntId = getRootCategoryEntityRecordId(
						category.getRootCategoryElement(), fullKeyMap.get(catEntityName), jdbcDao);

				insertRecordsForRelatedAttributes(dataValue, rootCatEntId, category
						.getRootCategoryElement(), recordsMap, jdbcDao, hibernateDao, identifier);
				jdbcDao.commit();
				hibernateDao.commit();
			}
		}
		catch (Exception e)
		{
			throw (DynamicExtensionsSystemException) handleRollback(e, "Error while editing data",
					jdbcDao, true);
		}
		finally
		{
			try
			{
				DynamicExtensionsUtility.closeJDBCDAO(jdbcDao);
				if (hibernateDao != null)
				{
					try
					{
						DynamicExtensionsUtility.closeHibernateDAO(hibernateDao);
					}
					catch (DAOException e)
					{
						throw (DynamicExtensionsSystemException) handleRollback(e,
								"Error while closing", hibernateDao, true);
					}
				}
			}
			catch (DAOException e)
			{
				throw (DynamicExtensionsSystemException) handleRollback(e, "Error while closing",
						jdbcDao, true);
			}
		}

		return isEdited;
	}

	/**
	 * @param catEntFKColName
	 * @param srcCatEntityId
	 * @param srcEntityId
	 * @param previousEntityId
	 * @param categoryEnt
	 * @param dataValue
	 * @param keyMap
	 * @param fullKeyMap
	 * @param records
	 * @param areMultplRecrds
	 * @param isNoCatAttrPrsnt
	 * @param jdbcDao
	 * @param hibernateDao
	 * @param isNewRecord
	 * @param lastAsso
	 * @param userId
	 * @throws DynamicExtensionsSystemException
	 * @throws DynamicExtensionsApplicationException
	 */
	private void editRecordsForCategoryEntityTree(String catEntFKColName, Long srcCatEntityId,
			Long srcEntityId, Long previousEntityId, CategoryEntityInterface categoryEnt,
			Map dataValue, Map<String, Long> keyMap, Map<String, Long> fullKeyMap,
			Map<String, List<Long>> records, boolean areMultplRecrds, boolean isNoCatAttrPrsnt,
			JDBCDAO jdbcDao, HibernateDAO hibernateDao, Boolean isNewRecord,
			AssociationInterface lastAsso, Long userId) throws DynamicExtensionsSystemException,
			DynamicExtensionsApplicationException
	{
		try
		{
			Object value = null;

			// Variable to check if record has been inserted for category entity.
			boolean isCatEntRecIns = false;
			Map<AbstractAttributeInterface, Object> attributes = null;

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

				Long entityId = null;

				if (attribute instanceof CategoryAttributeInterface && !isCatEntRecIns)
				{
					String catEntTblName = categoryEnt.getTableProperties().getName();

					EntityManagerInterface entityManager = EntityManager.getInstance();

					if (isNoCatAttrPrsnt)
					{
						attributes = null;
					}
					else
					{
						attributes = createAttributeMap(dataValue);
					}

					if (srcEntityId == null)
					{
						// Insert data for entity.
						entityId = entityManager.insertData(categoryEnt.getEntity(), attributes,
								hibernateDao, userId);

						srcEntityId = entityId;

					}
					else
					{
						entityId = srcEntityId;

						// Edit data for entity.
						entityManager.editData(categoryEnt.getEntity(), attributes, entityId,
								hibernateDao, userId);
					}

					Long catEntId = null;

					// Check whether table is created for category entity.
					if (((CategoryEntity) categoryEnt).isCreateTable())
					{
						catEntId = entityManagerUtil.getNextIdentifier(categoryEnt
								.getTableProperties().getName());

						// Insert query for category entity table.
						String insertQuery = INSERT_INTO_KEYWORD + catEntTblName + " ("
								+ IDENTIFIER + ", ACTIVITY_STATUS, " + RECORD_ID + ") VALUES (?,?,?)";
						LinkedList<ColumnValueBean> colValBeanList = createColumnValueBeanListForDataEntry(catEntId,entityId);
						executeUpdateQuery(insertQuery, userId, jdbcDao,colValBeanList);
					}

					if (catEntFKColName != null)
					{
						if (((CategoryEntity) categoryEnt).isCreateTable())
						{
							// Update query for category entity table.
							String updateQuery = UPDATE_KEYWORD + catEntTblName + " SET "
									+ catEntFKColName + " = ? "+ WHERE_KEYWORD
									+ IDENTIFIER + " = ?" ;
							ColumnValueBean bean1 = new ColumnValueBean("catEntFKColName", srcCatEntityId);
							ColumnValueBean bean2 = new ColumnValueBean("IDENTIFIER", catEntId);
							LinkedList<ColumnValueBean> colValBeanList = new LinkedList<ColumnValueBean>();
							colValBeanList.add(bean1);
							colValBeanList.add(bean2);
							executeUpdateQuery(updateQuery, userId, jdbcDao,colValBeanList);
						}

						PathInterface paths = categoryEnt.getPath();
						List<PathAssociationRelationInterface> allPathAsso = paths
								.getSortedPathAssociationRelationCollection();
						for (PathAssociationRelationInterface pathAssociation : allPathAsso)
						{
							StringBuffer entityName = new StringBuffer(pathAssociation
									.getAssociation().getEntity().getName());
							String catEntityName = "[" + pathAssociation.getSourceInstanceId() + "]";
							entityName.append(catEntityName);

							Long sourceEntityId = fullKeyMap.get(entityName.toString());
							if (sourceEntityId != null)
							{
								previousEntityId = sourceEntityId;
							}
							AssociationInterface asso = lastAsso;
							String packageName = null;
							packageName = getPackageName(lastAsso.getEntity(), packageName);

							StringBuffer sourceObjectClassName = new StringBuffer(packageName);
							EntityInterface sourceEntity = asso.getEntity();
							sourceObjectClassName.append('.').append(sourceEntity.getName());

							StringBuffer targetObjectClassName = new StringBuffer(packageName);
							EntityInterface targetEntity = asso.getTargetEntity();
							targetObjectClassName.append('.').append(targetEntity.getName());

							Object sourceObject = hibernateDao.retrieveById(sourceObjectClassName
									.toString(), previousEntityId);

							Object targetObject = hibernateDao.retrieveById(targetObjectClassName
									.toString(), entityId);

							String sourceRoleName = lastAsso.getSourceRole().getName();
							sourceRoleName = sourceRoleName.substring(0, 1).toUpperCase()
									+ sourceRoleName.substring(1, sourceRoleName.length());

							invokeSetterMethod(targetObject.getClass(), sourceRoleName, Class
									.forName(sourceObjectClassName.toString()), targetObject,
									sourceObject);

							hibernateDao.update(targetObject);
						}
					}

					CategoryEntityInterface catEntity = categoryEnt;
					String categoryEntName = DynamicExtensionsUtility
							.getCategoryEntityName(catEntity.getName());
					keyMap.put(categoryEntName, entityId);
					fullKeyMap.put(categoryEntName, entityId);

					List<Long> recordIds = records.get(categoryEntName);
					if (recordIds == null)
					{
						recordIds = new ArrayList<Long>();
					}
					recordIds.add(entityId);

					records.put(categoryEntName, recordIds);

					while (catEntity.getParentCategoryEntity() != null)
					{
						String parentCategoryEntName = DynamicExtensionsUtility
								.getCategoryEntityName(catEntity.getParentCategoryEntity()
										.getName());
						keyMap.put(parentCategoryEntName, entityId);
						fullKeyMap.put(parentCategoryEntName, entityId);

						List<Long> recIds = records.get(parentCategoryEntName);
						if (recIds == null)
						{
							recIds = new ArrayList<Long>();
						}
						recIds.add(entityId);
						records.put(parentCategoryEntName, recIds);

						catEntity = catEntity.getParentCategoryEntity();
					}

					isCatEntRecIns = true;
				}
				else if (attribute instanceof CategoryAssociationInterface)
				{
					CategoryAssociationInterface catAssociation = (CategoryAssociationInterface) attribute;

					PathInterface path = catAssociation.getTargetCategoryEntity().getPath();
					Collection<PathAssociationRelationInterface> pathAssoRel = path
							.getSortedPathAssociationRelationCollection();

					// Foreign key column name.
					String fKeyColName = "";

					List<Map<BaseAbstractAttributeInterface, Object>> mapsOfCntaindEnt = (List) value;

					Map<CategoryAttributeInterface, Object> catAttributes = new HashMap<CategoryAttributeInterface, Object>();
					Map<CategoryAssociationInterface, Object> catAssociations = new HashMap<CategoryAssociationInterface, Object>();

					for (Map<BaseAbstractAttributeInterface, Object> valueMap : mapsOfCntaindEnt)
					{
						//isNewRecord = false;

						Long currentEntityId = null;

						Set<BaseAbstractAttributeInterface> keySet = valueMap.keySet();
						Iterator<BaseAbstractAttributeInterface> iter = keySet.iterator();
						while (iter.hasNext())
						{
							Object obj = iter.next();
							if (obj instanceof CategoryAttributeInterface)
							{
								catAttributes.put((CategoryAttributeInterface) obj, valueMap
										.get(obj));
							}
							else if (obj instanceof CategoryEntityRecord)
							{
								currentEntityId = (Long) valueMap.get(obj);
							}
							else if (obj instanceof CategoryAssociationInterface)
							{
								catAssociations.put((CategoryAssociationInterface) obj, valueMap
										.get(obj));
							}
						}

						EntityInterface entity = catAssociation.getTargetCategoryEntity()
								.getEntity();

						AssociationInterface lastAssociation = null;

						boolean areIntermediateRecords = false;
						Long intermediateEntityId = null;

						for (PathAssociationRelationInterface par : pathAssoRel)
						{
							AssociationInterface association = par.getAssociation();
							lastAssociation = par.getAssociation();

							fKeyColName = association.getConstraintProperties()
									.getTgtEntityConstraintKeyProperties()
									.getTgtForiegnKeyColumnProperties().getName();

							if (association.getTargetEntity().getId() == entity.getId())
							{
								if (intermediateEntityId == null && currentEntityId != null)
								{
									String query = SELECT_KEYWORD
											+ fKeyColName
											+ FROM_KEYWORD
											+ association.getTargetEntity().getTableProperties()
													.getName() + WHERE_KEYWORD + IDENTIFIER + " = ?";

									LinkedList<ColumnValueBean> queryDataList = new LinkedList<ColumnValueBean>();
									queryDataList.add(new ColumnValueBean(IDENTIFIER, currentEntityId));
									List<Long> ids = getResultIDList(query, fKeyColName, jdbcDao,queryDataList);
									if (ids != null && !ids.isEmpty())
									{
										intermediateEntityId = ids.get(0);
									}

									fullKeyMap
											.put(association.getEntity().getName() + "["
													+ par.getSourceInstanceId() + "]",
													intermediateEntityId);

									keyMap
											.put(association.getEntity().getName() + "["
													+ par.getSourceInstanceId() + "]",
													intermediateEntityId);
								}

							}
							else
							{
								areIntermediateRecords = true;

								// This means new record needs to be inserted.
								if (currentEntityId == null)
								{
									// Get the record identifier from the intermediate table.
									intermediateEntityId = fullKeyMap.get(association
											.getTargetEntity().getName()
											+ "[" + par.getTargetInstanceId() + "]");

									// The add more button could open up a link that contains an entity
									// in the path. In such case record will not be present in this table.
									// Therefore, the record needs to be first inserted in this intermediate
									// table and then the identifier from this table should be used as a FK
									// in usual way.
									if (intermediateEntityId == null)
									{
										String packageName = null;
										packageName = getPackageName(association.getEntity(),
												packageName);

										String sourceObjectClassName = packageName + "."
												+ association.getEntity().getName();

										String targetObjectClassName = packageName + "."
												+ association.getTargetEntity().getName();

										Long sourceObjId = fullKeyMap.get(association.getEntity()
												.getName()
												+ "[" + par.getSourceInstanceId() + "]");

										// In case of inherited association, the 'fullKeyMap' will not
										// contain the record id of the parent to whom this association
										// belongs. Hence go up the hierarchy till the parent is found
										// and use its identifier
										if (sourceObjId == null)
										{
											EntityInterface sourceEntity = catAssociation
													.getCategoryEntity().getEntity();

											while (sourceEntity.getParentEntity() != null)
											{
												EntityInterface parentEntity = sourceEntity
														.getParentEntity();
												if (parentEntity.getId() == association.getEntity()
														.getId())
												{
													sourceObjId = fullKeyMap
															.get(catAssociation.getCategoryEntity()
																	.getEntity().getName()
																	+ "["
																	+ par.getSourceInstanceId()
																	+ "]");
													break;
												}
												else
												{
													sourceEntity = parentEntity;
												}
											}

											sourceObjectClassName = packageName
													+ "."
													+ catAssociation.getCategoryEntity()
															.getEntity().getName();
										}

										Object sourceObject = hibernateDao.retrieveById(
												sourceObjectClassName, sourceObjId);

										Object targetObject = null;

										String sourceRoleName = association.getSourceRole()
												.getName();
										sourceRoleName = sourceRoleName.substring(0, 1)
												.toUpperCase()
												+ sourceRoleName.substring(1, sourceRoleName
														.length());

										String targetRoleName = association.getTargetRole()
												.getName();
										targetRoleName = targetRoleName.substring(0, 1)
												.toUpperCase()
												+ targetRoleName.substring(1, targetRoleName
														.length());

										// Create a new instance.
										Class targetObjectClass = Class
												.forName(targetObjectClassName);
										Constructor targetObjectConstructor = targetObjectClass
												.getConstructor();
										targetObject = targetObjectConstructor.newInstance();

										hibernateDao.insert(targetObject);

										// Get the associated object(s).
										Object associatedObjects = invokeGetterMethod(sourceObject
												.getClass(), targetRoleName, sourceObject);

										Cardinality targetMaxCardinality = association
												.getTargetRole().getMaximumCardinality();

										if (targetMaxCardinality != Cardinality.ONE)
										{
											Collection<Object> containedObjects = null;

											if (associatedObjects != null)
											{
												containedObjects = (Collection) associatedObjects;
											}
											else
											{
												containedObjects = new HashSet<Object>();
											}

											containedObjects.add(targetObject);

											invokeSetterMethod(sourceObject.getClass(),
													targetRoleName, Class
															.forName("java.util.Collection"),
													sourceObject, containedObjects);
										}
										else
										{
											invokeSetterMethod(sourceObject.getClass(),
													targetRoleName, targetObject.getClass(),
													sourceObject, targetObject);
										}

										hibernateDao.update(sourceObject);

										invokeSetterMethod(targetObject.getClass(), sourceRoleName,
												Class.forName(sourceObjectClassName), targetObject,
												sourceObject);

										hibernateDao.update(targetObject);

										Method method = targetObject.getClass()
												.getMethod("getId");
										Object objectId = method.invoke(targetObject);
										Long insertedObjectId = Long.valueOf(objectId.toString());

										intermediateEntityId = insertedObjectId;

										fullKeyMap.put(association.getTargetEntity().getName()
												+ "[" + par.getTargetInstanceId() + "]",
												intermediateEntityId);
									}
								}
								else
								{

									Long fKeyColData = fullKeyMap.get(association.getEntity().getName()
											+ "[" + par.getSourceInstanceId() + "]");
									String query = SELECT_KEYWORD
											+ IDENTIFIER
											+ FROM_KEYWORD
											+ association.getTargetEntity().getTableProperties()
													.getName()
											+ WHERE_KEYWORD
											+ fKeyColName
											+ " = ?";
									LinkedList<ColumnValueBean> queryDataList = new LinkedList<ColumnValueBean>();
									queryDataList.add(new ColumnValueBean(fKeyColName, fKeyColData));
									List<Long> ids = getResultIDList(query, IDENTIFIER, jdbcDao,queryDataList);
									if (ids != null && !ids.isEmpty())
									{
										if (ids.size() == 1)
										{
											intermediateEntityId = ids.get(0);
										}
										else
										{
											intermediateEntityId = fullKeyMap.get(association
													.getTargetEntity().getName()
													+ "[" + par.getTargetInstanceId() + "]");
										}
									}

									if (intermediateEntityId != null)
									{
										fullKeyMap.put(association.getTargetEntity().getName()
												+ "[" + par.getTargetInstanceId() + "]",
												intermediateEntityId);

										keyMap.put(association.getTargetEntity().getName() + "["
												+ par.getTargetInstanceId() + "]",
												intermediateEntityId);
									}
								}
							}
						}

						String selectQuery = SELECT_KEYWORD + IDENTIFIER + FROM_KEYWORD
								+ catAssociation.getCategoryEntity().getTableProperties().getName()
								+ WHERE_KEYWORD + RECORD_ID + " = ?" ;
						LinkedList<ColumnValueBean> queryDataList = new LinkedList<ColumnValueBean>();
						queryDataList.add(new ColumnValueBean(RECORD_ID, previousEntityId));
						List<Long> identifiers = getResultIDList(selectQuery, IDENTIFIER, jdbcDao,queryDataList);

						Long resultId = null;
						if (identifiers != null && !identifiers.isEmpty())
						{
							resultId = identifiers.get(0);
						}

						Long srcCategoryEntId = resultId;

						String catEntFKClmnName = catAssociation.getConstraintProperties()
								.getTgtEntityConstraintKeyProperties()
								.getTgtForiegnKeyColumnProperties().getName();

						if (mapsOfCntaindEnt.size() > 1)
						{
							areMultplRecrds = true;
						}
						else
						{
							areMultplRecrds = false;
						}

						// Insert data for category attributes. If there are no category attributes in a
						// particular instance, or all are related category attributes, then create a dummy
						// category attribute so as to link the records.
						if (catAttributes.isEmpty()
								|| isAllRelatedCategoryAttributesCollection(catAttributes))
						{
							isNoCatAttrPrsnt = true;
							CategoryAttributeInterface dummyCatAttr = DomainObjectFactory
									.getInstance().createCategoryAttribute();
							dummyCatAttr
									.setCategoryEntity(catAssociation.getTargetCategoryEntity());
							catAttributes.put(dummyCatAttr, "");

							if (areIntermediateRecords)
							{
								editRecordsForCategoryEntityTree(catEntFKClmnName,
										srcCategoryEntId, currentEntityId, intermediateEntityId,
										catAssociation.getTargetCategoryEntity(), catAttributes,
										keyMap, fullKeyMap, records, areMultplRecrds,
										isNoCatAttrPrsnt, jdbcDao, hibernateDao, isNewRecord,
										lastAssociation, userId);
							}
							else
							{
								editRecordsForCategoryEntityTree(catEntFKClmnName,
										srcCategoryEntId, currentEntityId, previousEntityId,
										catAssociation.getTargetCategoryEntity(), catAttributes,
										keyMap, fullKeyMap, records, areMultplRecrds,
										isNoCatAttrPrsnt, jdbcDao, hibernateDao, isNewRecord,
										lastAssociation, userId);
							}

							isNoCatAttrPrsnt = false;
						}
						else
						{
							if (areIntermediateRecords)
							{
								editRecordsForCategoryEntityTree(catEntFKClmnName,
										srcCategoryEntId, currentEntityId, intermediateEntityId,
										catAssociation.getTargetCategoryEntity(), catAttributes,
										keyMap, fullKeyMap, records, areMultplRecrds,
										isNoCatAttrPrsnt, jdbcDao, hibernateDao, isNewRecord,
										lastAssociation, userId);
							}
							else
							{
								editRecordsForCategoryEntityTree(catEntFKClmnName,
										srcCategoryEntId, currentEntityId, previousEntityId,
										catAssociation.getTargetCategoryEntity(), catAttributes,
										keyMap, fullKeyMap, records, areMultplRecrds,
										isNoCatAttrPrsnt, jdbcDao, hibernateDao, isNewRecord,
										lastAssociation, userId);
							}
						}

						catAttributes.clear();

						// Insert data for category associations.
						if (currentEntityId == null)
						{
							currentEntityId = fullKeyMap.get(DynamicExtensionsUtility
									.getCategoryEntityName(catAssociation.getTargetCategoryEntity()
											.getName()));
						}

						// The previous entity id (prevEntityId) will now be equal to
						// the present entity id (currentEntityId) to do the data entry
						// for category associations of current entity.
						editRecordsForCategoryEntityTree(catEntFKClmnName, srcCategoryEntId,
								currentEntityId, currentEntityId, catAssociation
										.getTargetCategoryEntity(), catAssociations, keyMap,
								fullKeyMap, records, areMultplRecrds, isNoCatAttrPrsnt, jdbcDao,
								hibernateDao, isNewRecord, lastAssociation, userId);
						catAssociations.clear();

						fullKeyMap.putAll(keyMap);
						keyMap.remove(DynamicExtensionsUtility.getCategoryEntityName(catAssociation
								.getTargetCategoryEntity().getName()));
					}
				}
			}
		}
		catch (Exception e)
		{
			throw new DynamicExtensionsApplicationException(
					"Exception encountered while inserting records!", e);
		}
	}

	/**
	 * This helper method recursively inserts records for a single category entity
	 * and all its category associations i.e. in turn for a whole category entity tree.
	 * @param entityFKColName
	 * @param catEntFKColName
	 * @param srcCatEntityId
	 * @param srcEntityId
	 * @param categoryEnt
	 * @param dataValue
	 * @param keyMap
	 * @param fullKeyMap
	 * @param records
	 * @param areMultplRecrds
	 * @param isNoCatAttrPrsnt
	 * @param jdbcDao
	 * @param userId
	 * @throws DynamicExtensionsSystemException
	 * @throws DynamicExtensionsApplicationException
	 */
	private void insertRecordsForCategoryEntityTree(String entityFKColName, String catEntFKColName,
			Long srcCatEntityId, Long srcEntityId, CategoryEntityInterface categoryEnt,
			Map dataValue, Map<String, Long> keyMap, Map<String, Long> fullKeyMap,
			Map<String, List<Long>> records, boolean areMultplRecrds, boolean isNoCatAttrPrsnt,
			JDBCDAO jdbcDao, HibernateDAO hibernateDao, boolean isEditMode,
			AssociationInterface lastAsso, Long userId) throws DynamicExtensionsSystemException,
			DynamicExtensionsApplicationException
	{
		try
		{
			Object value = null;

			// Variable to check if record has been inserted for category entity.
			boolean isCatEntRecIns = false;
			Map<AbstractAttributeInterface, Object> attributes = null;

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

				if (attribute instanceof CategoryAttributeInterface && !isCatEntRecIns)
				{
					String catEntTblName = categoryEnt.getTableProperties().getName();

					Long entityId = null;
					EntityManagerInterface entityManager = EntityManager.getInstance();

					if (isNoCatAttrPrsnt)
					{
						attributes = null;
					}
					else
					{
						attributes = createAttributeMap(dataValue);
					}
					String catEntName = DynamicExtensionsUtility
							.getCategoryEntityName(((CategoryAttribute) attribute)
									.getCategoryEntity().getName());
					if (keyMap.get(catEntName) != null && !areMultplRecrds)
					{
						entityId = keyMap.get(catEntName);

						// Edit data for entity hierarchy.
						entityManager.editData(categoryEnt.getEntity(), attributes, entityId,
								hibernateDao, userId);
					}
					else
					{
						entityId = entityManager.insertData(categoryEnt.getEntity(), attributes,
								hibernateDao, userId);
					}

					Long catEntId = null;

					// Check whether table is created for category entity.
					if (((CategoryEntity) categoryEnt).isCreateTable())
					{
						catEntId = entityManagerUtil.getNextIdentifier(categoryEnt
								.getTableProperties().getName());

						// Insert query for category entity table.
						String insertQuery = INSERT_INTO_KEYWORD + catEntTblName + " ("
								+ IDENTIFIER + ", ACTIVITY_STATUS, " + RECORD_ID + ") VALUES (?,?,?)";

						LinkedList<ColumnValueBean> colValBeanList = createColumnValueBeanListForDataEntry(catEntId,entityId);
						executeUpdateQuery(insertQuery, userId, jdbcDao,colValBeanList);
					}

					if (catEntFKColName != null && entityFKColName != null)
					{
						if (((CategoryEntity) categoryEnt).isCreateTable())
						{
							// Update query for category entity table.
							String updateQuery = UPDATE_KEYWORD + catEntTblName + " SET "
									+ catEntFKColName + " = ?" + WHERE_KEYWORD
									+ IDENTIFIER + " = ?";
							ColumnValueBean bean1=new ColumnValueBean(catEntFKColName, srcCatEntityId);
							ColumnValueBean bean2=new ColumnValueBean(IDENTIFIER, catEntId);
							LinkedList<ColumnValueBean> colValBeanList = new LinkedList<ColumnValueBean>();
							colValBeanList.add(bean1);
							colValBeanList.add(bean2);
							executeUpdateQuery(updateQuery, userId, jdbcDao,colValBeanList);
						}

						String packageName = null;
						packageName = getPackageName(lastAsso.getEntity(), packageName);
						String sourceObjectClassName = packageName + "."
								+ lastAsso.getEntity().getName();
						String targetObjectClassName = packageName + "."
								+ lastAsso.getTargetEntity().getName();
						Object sourceObject = hibernateDao.retrieveById(sourceObjectClassName,
								srcEntityId);
						Object targetObject = hibernateDao.retrieveById(targetObjectClassName,
								entityId);
						String sourceRoleName = lastAsso.getSourceRole().getName();
						sourceRoleName = sourceRoleName.substring(0, 1).toUpperCase()
								+ sourceRoleName.substring(1, sourceRoleName.length());
						invokeSetterMethod(targetObject.getClass(), sourceRoleName, Class
								.forName(sourceObjectClassName), targetObject, sourceObject);
						hibernateDao.update(targetObject);
						// Update query for entity table.
						/*String updateEntQuery = "UPDATE "
						        + ((CategoryAttribute) attribute).getCategoryEntity().getEntity()
						                .getTableProperties().getName() + " SET " + entityFKColName
						        + " = " + srcEntityId + " WHERE IDENTIFIER = " + entityId;
						executeUpdateQuery(updateEntQuery, userId, jdbcDao);*/
					}

					CategoryEntityInterface catEntity = categoryEnt;
					String categoryEntName = DynamicExtensionsUtility
							.getCategoryEntityName(catEntity.getName());
					keyMap.put(categoryEntName, entityId);
					fullKeyMap.put(categoryEntName, entityId);

					List<Long> recordIds = records.get(categoryEntName);
					if (recordIds == null)
					{
						recordIds = new ArrayList<Long>();
					}
					recordIds.add(entityId);

					records.put(categoryEntName, recordIds);

					while (catEntity.getParentCategoryEntity() != null)
					{
						String parentCategoryEntName = DynamicExtensionsUtility
								.getCategoryEntityName(catEntity.getParentCategoryEntity()
										.getName());
						keyMap.put(parentCategoryEntName, entityId);
						fullKeyMap.put(parentCategoryEntName, entityId);

						List<Long> recIds = records.get(parentCategoryEntName);
						if (recIds == null)
						{
							recIds = new ArrayList<Long>();
						}
						recIds.add(entityId);
						records.put(parentCategoryEntName, recIds);

						catEntity = catEntity.getParentCategoryEntity();
					}

					isCatEntRecIns = true;
				}
				else if (attribute instanceof CategoryAssociationInterface)
				{
					CategoryAssociationInterface catAssociation = (CategoryAssociationInterface) attribute;

					PathInterface path = catAssociation.getTargetCategoryEntity().getPath();
					Collection<PathAssociationRelationInterface> pathAssoRel = path
							.getSortedPathAssociationRelationCollection();

					Long sourceEntityId = fullKeyMap.get(DynamicExtensionsUtility
							.getCategoryEntityName(catAssociation.getCategoryEntity().getName()));

					// Foreign key column name.
					String fKeyColName = "";

					String selectQuery = SELECT_KEYWORD + IDENTIFIER + FROM_KEYWORD
							+ catAssociation.getCategoryEntity().getTableProperties().getName()
							+ WHERE_KEYWORD + RECORD_ID + " = ?" ;
					LinkedList<ColumnValueBean> queryDataList = new LinkedList<ColumnValueBean>();
					queryDataList.add(new ColumnValueBean(RECORD_ID, sourceEntityId));
					List<Long> identifiers = getResultIDList(selectQuery, IDENTIFIER, jdbcDao,queryDataList);

					Long resultId = null;
					if (identifiers != null && !identifiers.isEmpty())
					{
						resultId = identifiers.get(0);
					}

					Long srcCategoryEntId = resultId;

					String catEntFKClmnName = catAssociation.getConstraintProperties()
							.getTgtEntityConstraintKeyProperties()
							.getTgtForiegnKeyColumnProperties().getName();

					EntityInterface entity = catAssociation.getTargetCategoryEntity().getEntity();

					AssociationInterface lastAssociation = null;
					for (PathAssociationRelationInterface par : pathAssoRel)
					{
						AssociationInterface association = par.getAssociation();
						lastAssociation = par.getAssociation();

						fKeyColName = association.getConstraintProperties()
								.getTgtEntityConstraintKeyProperties()
								.getTgtForiegnKeyColumnProperties().getName();

						if (association.getTargetEntity().getId() != entity.getId())
						{
							if (fullKeyMap.get(association.getTargetEntity().getName() + "["
									+ par.getTargetInstanceId() + "]") == null)
							{
								String packageName = null;
								packageName = getPackageName(association.getEntity(), packageName);
								String sourceObjectClassName = packageName + "."
										+ association.getEntity().getName();
								String targetObjectClassName = packageName + "."
										+ association.getTargetEntity().getName();

								Object sourceObject = hibernateDao.retrieveById(
										sourceObjectClassName, sourceEntityId);
								Object targetObject = null;
								Class targetObjectClass = Class.forName(targetObjectClassName);
								Constructor targetObjectConstructor = targetObjectClass
										.getConstructor();
								targetObject = targetObjectConstructor.newInstance();
								String sourceRoleName = association.getSourceRole().getName();
								sourceRoleName = sourceRoleName.substring(0, 1).toUpperCase()
										+ sourceRoleName.substring(1, sourceRoleName.length());
								String targetRoleName = association.getTargetRole().getName();
								targetRoleName = targetRoleName.substring(0, 1).toUpperCase()
										+ targetRoleName.substring(1, targetRoleName.length());
								hibernateDao.insert(targetObject);

								// Get the associated object(s).
								Object associatedObjects = invokeGetterMethod(sourceObject
										.getClass(), targetRoleName, sourceObject);

								Cardinality targetMaxCardinality = association.getTargetRole()
										.getMaximumCardinality();
								if (targetMaxCardinality != Cardinality.ONE)
								{
									Collection<Object> containedObjects = null;
									if (associatedObjects != null)
									{
										containedObjects = (Collection) associatedObjects;
									}
									else
									{
										containedObjects = new HashSet<Object>();
									}

									containedObjects.add(targetObject);

									invokeSetterMethod(sourceObject.getClass(), targetRoleName,
											Class.forName("java.util.Collection"), sourceObject,
											containedObjects);
								}
								else
								{
									invokeSetterMethod(sourceObject.getClass(), targetRoleName,
											targetObject.getClass(), sourceObject, targetObject);
								}

								hibernateDao.update(sourceObject);

								invokeSetterMethod(targetObject.getClass(), sourceRoleName, Class
										.forName(sourceObjectClassName), targetObject, sourceObject);

								hibernateDao.update(targetObject);

								Method method = targetObject.getClass().getMethod("getId");
								Object updatedObject = method.invoke(targetObject);
								Long entityId = Long.valueOf(updatedObject.toString());

								sourceEntityId = entityId;

								fullKeyMap.put(association.getTargetEntity().getName() + "["
										+ par.getTargetInstanceId() + "]", sourceEntityId);
								keyMap.put(association.getTargetEntity().getName() + "["
										+ par.getTargetInstanceId() + "]", sourceEntityId);

								List<Long> recIds = records.get(association.getTargetEntity()
										.getName()
										+ "[" + par.getTargetInstanceId() + "]");
								if (recIds == null)
								{
									recIds = new ArrayList<Long>();
								}
								recIds.add(entityId);
								records.put(association.getTargetEntity().getName() + "["
										+ par.getTargetInstanceId() + "]", recIds);
							}
							else
							{
								sourceEntityId = fullKeyMap.get(association.getTargetEntity()
										.getName()
										+ "[" + par.getTargetInstanceId() + "]");
							}
						}
						else
						{
							sourceEntityId = fullKeyMap.get(association.getEntity().getName() + "["
									+ par.getSourceInstanceId() + "]");

							if (sourceEntityId == null)
							{
								EntityInterface sourceEntity = catAssociation.getCategoryEntity()
										.getEntity();
								while (sourceEntity.getParentEntity() != null)
								{
									EntityInterface parentEntity = sourceEntity.getParentEntity();
									if (parentEntity.getId() == association.getEntity().getId())
									{
										sourceEntityId = fullKeyMap.get(catAssociation
												.getCategoryEntity().getEntity().getName()
												+ "[" + par.getSourceInstanceId() + "]");
										break;
									}
									else
									{
										sourceEntity = parentEntity;
									}
								}
							}
						}
					}

					List<Map<BaseAbstractAttributeInterface, Object>> mapsOfCntaindEnt = (List) value;

					Map<CategoryAttributeInterface, Object> catAttributes = new HashMap<CategoryAttributeInterface, Object>();
					Map<CategoryAssociationInterface, Object> catAssociations = new HashMap<CategoryAssociationInterface, Object>();

					for (Map<BaseAbstractAttributeInterface, Object> valueMap : mapsOfCntaindEnt)
					{
						Set<BaseAbstractAttributeInterface> keySet = valueMap.keySet();
						Iterator<BaseAbstractAttributeInterface> iter = keySet.iterator();
						while (iter.hasNext())
						{
							Object obj = iter.next();
							if (obj instanceof CategoryAttributeInterface)
							{
								catAttributes.put((CategoryAttributeInterface) obj, valueMap
										.get(obj));
							}
							else
							{
								catAssociations.put((CategoryAssociationInterface) obj, valueMap
										.get(obj));
							}
						}

						if (mapsOfCntaindEnt.size() > 1)
						{
							areMultplRecrds = true;
						}
						else
						{
							areMultplRecrds = false;
						}

						// Insert data for category attributes. If there are no category attributes in a
						// particular instance, or all are related category attributes, then create a dummy
						// category attribute so as to link the records.
						if (catAttributes.isEmpty()
								|| isAllRelatedCategoryAttributesCollection(catAttributes))
						{
							isNoCatAttrPrsnt = true;
							CategoryAttributeInterface dummyCatAttr = DomainObjectFactory
									.getInstance().createCategoryAttribute();
							dummyCatAttr
									.setCategoryEntity(catAssociation.getTargetCategoryEntity());
							catAttributes.put(dummyCatAttr, "");

							insertRecordsForCategoryEntityTree(fKeyColName, catEntFKClmnName,
									srcCategoryEntId, sourceEntityId, catAssociation
											.getTargetCategoryEntity(), catAttributes, keyMap,
									fullKeyMap, records, areMultplRecrds, isNoCatAttrPrsnt,
									jdbcDao, hibernateDao, isEditMode, lastAssociation, userId);

							isNoCatAttrPrsnt = false;
						}
						else
						{
							insertRecordsForCategoryEntityTree(fKeyColName, catEntFKClmnName,
									srcCategoryEntId, sourceEntityId, catAssociation
											.getTargetCategoryEntity(), catAttributes, keyMap,
									fullKeyMap, records, areMultplRecrds, isNoCatAttrPrsnt,
									jdbcDao, hibernateDao, isEditMode, lastAssociation, userId);
						}
						catAttributes.clear();

						// Insert data for category associations.
						insertRecordsForCategoryEntityTree(fKeyColName, catEntFKClmnName,
								srcCategoryEntId, sourceEntityId, catAssociation
										.getTargetCategoryEntity(), catAssociations, keyMap,
								fullKeyMap, records, areMultplRecrds, isNoCatAttrPrsnt, jdbcDao,
								hibernateDao, isEditMode, lastAssociation, userId);
						catAssociations.clear();

						fullKeyMap.putAll(keyMap);
						keyMap.remove(DynamicExtensionsUtility.getCategoryEntityName(catAssociation
								.getTargetCategoryEntity().getName()));
					}
				}
			}
		}
		catch (Exception e)
		{
			throw new DynamicExtensionsApplicationException(
					"Exception encountered while inserting records!", e);
		}
	}

	/**
	 * This method returns the category data value map for the given root category entity.
	 * @param rootCatEntity
	 * @param recordId
	 * @return map of category entity data
	 * @throws DynamicExtensionsSystemException
	 * @throws DynamicExtensionsApplicationException
	 * @throws SQLException
	 */
	public Map<BaseAbstractAttributeInterface, Object> getRecordById(
			CategoryEntityInterface rootCatEntity, Long recordId)
			throws DynamicExtensionsSystemException, DynamicExtensionsApplicationException,
			SQLException
	{
		Map<BaseAbstractAttributeInterface, Object> dataValue = new HashMap<BaseAbstractAttributeInterface, Object>();

		JDBCDAO jdbcDAO = null;

		try
		{
			jdbcDAO = DynamicExtensionsUtility.getJDBCDAO();
			retrieveRecords(rootCatEntity, dataValue, recordId, jdbcDAO);
		}
		catch (DAOException e)
		{
			throw (DynamicExtensionsSystemException) handleRollback(e,
					"Error while retrieving data", jdbcDAO, true);
		}
		finally
		{
			try
			{
				DynamicExtensionsUtility.closeJDBCDAO(jdbcDAO);
			}
			catch (DAOException e)
			{
				throw (DynamicExtensionsSystemException) handleRollback(e, "Error while closing",
						jdbcDAO, true);
			}
		}

		Map<BaseAbstractAttributeInterface, Object> curatedRecords = new HashMap<BaseAbstractAttributeInterface, Object>();
		curateMapForRelatedAttributes(curatedRecords, dataValue);

		dataValue = curatedRecords;

		return dataValue;
	}

	/**
	 * @param rootCatEntity
	 * @param recordId
	 * @return
	 * @throws DynamicExtensionsSystemException
	 * @throws DynamicExtensionsApplicationException
	 * @throws SQLException
	 */
	public Map<String, Map<String, Object>> getRelatedAttributeValues(
			CategoryEntityInterface rootCatEntity, Long recordId)
			throws DynamicExtensionsSystemException, DynamicExtensionsApplicationException,
			SQLException
	{
		CategoryManager categoreyManager = (CategoryManager) CategoryManager.getInstance();

		Map<BaseAbstractAttributeInterface, Object> recordMap = categoreyManager.getRecordById(
				rootCatEntity, recordId);

		Map<String, Map<String, Object>> records = new HashMap<String, Map<String, Object>>();

		getMapOfRelatedAttributesValues(records, recordMap);

		return records;
	}

	/**
	 * This method keeps only the related invisible category attributes in the
	 * map.
	 *
	 * @param records
	 * @param dataValue
	 */
	private void getMapOfRelatedAttributesValues(Map<String, Map<String, Object>> records,
			Map<BaseAbstractAttributeInterface, Object> dataValue)
	{
		Iterator<BaseAbstractAttributeInterface> iter = dataValue.keySet().iterator();
		while (iter.hasNext())
		{
			Object obj = iter.next();

			if (obj instanceof CategoryAttributeInterface)
			{
				CategoryAttributeInterface catAttr = (CategoryAttributeInterface) obj;

				if (catAttr.getIsRelatedAttribute() != null && catAttr.getIsRelatedAttribute()
						&& catAttr.getIsVisible() != null && !catAttr.getIsVisible())
				{
					Map<String, Object> innerMap;

					if (records.get(catAttr.getCategoryEntity().getName()) == null)
					{
						innerMap = new HashMap<String, Object>();
						innerMap.put(catAttr.getName(), dataValue.get(obj));
					}
					else
					{
						innerMap = records.get(catAttr.getCategoryEntity().getName());
						innerMap.put(catAttr.getName(), dataValue.get(obj));
					}

					records.put(catAttr.getCategoryEntity().getName(), innerMap);
				}
			}
			else
			{
				CategoryAssociationInterface catAssociation = (CategoryAssociationInterface) obj;

				List<Map<BaseAbstractAttributeInterface, Object>> mapsOfCntdRec = (List<Map<BaseAbstractAttributeInterface, Object>>) dataValue
						.get(catAssociation);

				for (Map<BaseAbstractAttributeInterface, Object> map : mapsOfCntdRec)
				{
					getMapOfRelatedAttributesValues(records, map);
				}
			}
		}
	}

	/**
	 * This method removes related invisible category attributes from the map.
	 * @param curatedRecords
	 * @param dataValue
	 */
	private void curateMapForRelatedAttributes(
			Map<BaseAbstractAttributeInterface, Object> curatedRecords,
			Map<BaseAbstractAttributeInterface, Object> dataValue)
	{
		Iterator<Map.Entry<BaseAbstractAttributeInterface,Object>> iter = dataValue.entrySet().iterator();
		while (iter.hasNext())
		{
			Entry<BaseAbstractAttributeInterface, Object> nextEntryObject = iter.next();
			Object obj = nextEntryObject.getKey();

			if (obj instanceof CategoryAttributeInterface)
			{
				CategoryAttributeInterface catAttr = (CategoryAttributeInterface) obj;

				if (catAttr.getIsRelatedAttribute() != null && catAttr.getIsRelatedAttribute()
						&& catAttr.getIsVisible() != null && catAttr.getIsVisible())
				{
					curatedRecords.put((BaseAbstractAttributeInterface) obj, nextEntryObject.getValue());
				}
				else if (catAttr.getIsRelatedAttribute() != null
						&& !catAttr.getIsRelatedAttribute())
				{
					curatedRecords.put((BaseAbstractAttributeInterface) obj, nextEntryObject.getValue());
				}
			}
			else if (obj instanceof CategoryEntityRecord)
			{
				CategoryEntityRecord entityRecord = (CategoryEntityRecord) obj;
				curatedRecords.put(entityRecord, nextEntryObject.getValue());
			}
			else
			{
				CategoryAssociationInterface catAssociation = (CategoryAssociationInterface) obj;

				List<Map<BaseAbstractAttributeInterface, Object>> mapsOfCntdRec = (List) nextEntryObject.getValue();
				List<Map<BaseAbstractAttributeInterface, Object>> innerRecList = new ArrayList<Map<BaseAbstractAttributeInterface, Object>>();

				for (Map<BaseAbstractAttributeInterface, Object> map : mapsOfCntdRec)
				{
					Map<BaseAbstractAttributeInterface, Object> innerRecords = new HashMap<BaseAbstractAttributeInterface, Object>();
					curateMapForRelatedAttributes(innerRecords, map);
					innerRecList.add(innerRecords);
				}

				curatedRecords.put(catAssociation, innerRecList);
			}
		}
	}

	/**
	 * @param catEntity
	 * @param dataValue
	 * @param rootCatEntRecId
	 * @throws SQLException
	 * @throws DynamicExtensionsSystemException
	 * @throws DynamicExtensionsApplicationException
	 */
	private void retrieveRecords(CategoryEntityInterface catEntity,
			Map<BaseAbstractAttributeInterface, Object> dataValue, long rootCatEntRecId,
			JDBCDAO jdbcDAO) throws SQLException, DynamicExtensionsSystemException,
			DynamicExtensionsApplicationException
	{
		Long recordId = null;
		String catEntTblName = "";
		String selRecIdQuery = "";

		boolean isRecordIdNull = false;

		// Only the root category entity has the category object set in it.
		if (catEntity.getCategory() != null)
		{
			catEntTblName = catEntity.getTableProperties().getName();

			selRecIdQuery = SELECT_KEYWORD + RECORD_ID + FROM_KEYWORD + catEntTblName
					+ WHERE_KEYWORD + IDENTIFIER + " = ?";
			LinkedList<ColumnValueBean> queryDataList = new LinkedList<ColumnValueBean>();
			queryDataList.add(new ColumnValueBean(IDENTIFIER, rootCatEntRecId));
			List<Long> identifiers = getResultIDList(selRecIdQuery, RECORD_ID, jdbcDAO,queryDataList);

			if (identifiers != null && !identifiers.isEmpty())
			{
				recordId = identifiers.get(0);
			}
		}

		// If entity model is different than category model then entity data is inserted
		// according to entity model and category data is inserted according to category model.
		// In this case recordId can be NULL in category entity table.
		if (recordId == null)
		{
			isRecordIdNull = true;
			recordId = rootCatEntRecId;
		}

		Map<AbstractAttributeInterface, Object> entityRecords = new HashMap<AbstractAttributeInterface, Object>();
		entityRecords.putAll(EntityManager.getInstance().getEntityRecordById(catEntity.getEntity(),
				recordId, jdbcDAO));

		// If root category entity has parent entity, then get data from parent entity table
		// with same record id.
		CategoryEntityInterface parentCatEnt = catEntity.getParentCategoryEntity();
		while (parentCatEnt != null)
		{
			Map<AbstractAttributeInterface, Object> innerValues = EntityManager.getInstance()
					.getEntityRecordById(parentCatEnt.getEntity(), recordId, jdbcDAO);
			if (innerValues != null)
			{
				entityRecords.putAll(innerValues);
			}

			parentCatEnt = parentCatEnt.getParentCategoryEntity();
		}

		if (!isAllRelatedInvisibleCategoryAttributesCollection(catEntity))
		{
			for (CategoryAttributeInterface catAttribute : catEntity.getAllCategoryAttributes())
			{
				dataValue.put(catAttribute, entityRecords.get(catAttribute.getAbstractAttribute()));
			}
			CategoryEntityRecord catEntityRecord = new CategoryEntityRecord();
			dataValue.put(catEntityRecord, recordId);
		}

		Collection<CategoryAssociationInterface> catAssociations = new ArrayList<CategoryAssociationInterface>(
				catEntity.getCategoryAssociationCollection());
		for (CategoryAssociationInterface catAssociation : catAssociations)
		{
			CategoryEntityInterface targetCatEnt = catAssociation.getTargetCategoryEntity();
			if (targetCatEnt != null && (((CategoryEntity) targetCatEnt).isCreateTable()))
			{
				catEntTblName = targetCatEnt.getTableProperties().getName();

				if (isRecordIdNull)
				{
					String selectQuery = SELECT_KEYWORD + IDENTIFIER + FROM_KEYWORD
							+ catAssociation.getCategoryEntity().getTableProperties().getName()
							+ WHERE_KEYWORD + RECORD_ID + " = ?";
					LinkedList<ColumnValueBean> queryDataList = new LinkedList<ColumnValueBean>();
					queryDataList.add(new ColumnValueBean(RECORD_ID, recordId));

					List<Long> identifiers = getResultIDList(selectQuery, IDENTIFIER, jdbcDAO,queryDataList);

					if (identifiers != null && !identifiers.isEmpty())
					{
						rootCatEntRecId = identifiers.get(0);
					}
				}

				String tgtFkColName = catAssociation.getConstraintProperties()
						.getTgtEntityConstraintKeyProperties().getTgtForiegnKeyColumnProperties()
						.getName();
				selRecIdQuery = SELECT_KEYWORD
						+ RECORD_ID
						+ FROM_KEYWORD
						+ catEntTblName
						+ WHERE_KEYWORD
						+ tgtFkColName+ " = ?";

				LinkedList<ColumnValueBean> queryDataList = new LinkedList<ColumnValueBean>();
				queryDataList.add(new ColumnValueBean(tgtFkColName, rootCatEntRecId));

				List<Map<BaseAbstractAttributeInterface, Object>> innerRecords = new ArrayList<Map<BaseAbstractAttributeInterface, Object>>();
				dataValue.put(catAssociation, innerRecords);

				List<Long> recordIds = getResultIDList(selRecIdQuery, RECORD_ID, jdbcDAO,queryDataList);
				for (Long recId : recordIds)
				{
					Map<BaseAbstractAttributeInterface, Object> innerRecord = new HashMap<BaseAbstractAttributeInterface, Object>();
					innerRecords.add(innerRecord);

					retrieveRecords(targetCatEnt, innerRecord, recId, jdbcDAO);
				}
			}
		}
	}

	/**
	 * @param entity
	 * @param dataValue
	 * @return
	 */
	private Map<CategoryEntityInterface, Map<BaseAbstractAttributeInterface, Object>> initialiseEntityValueMap(
			Map<BaseAbstractAttributeInterface, ?> dataValue)
	{
		Map<CategoryEntityInterface, Map<BaseAbstractAttributeInterface, Object>> catEntRecords = new HashMap<CategoryEntityInterface, Map<BaseAbstractAttributeInterface, Object>>();

		for (BaseAbstractAttributeInterface baseAbstrAttr : dataValue.keySet())
		{
			CategoryEntityInterface categoryEntity = null;
			if (baseAbstrAttr instanceof CategoryAttributeInterface)
			{
				categoryEntity = ((CategoryAttributeInterface) baseAbstrAttr).getCategoryEntity();
			}
			else
			{
				categoryEntity = ((CategoryAssociationInterface) baseAbstrAttr).getCategoryEntity();
			}
			Object value = dataValue.get(baseAbstrAttr);

			Map<BaseAbstractAttributeInterface, Object> entDataValues = catEntRecords
					.get(categoryEntity);
			if (entDataValues == null)
			{
				entDataValues = new HashMap<BaseAbstractAttributeInterface, Object>();
				catEntRecords.put(categoryEntity, entDataValues);
			}
			entDataValues.put(baseAbstrAttr, value);
		}

		return catEntRecords;
	}

	/**
	 * @param catEntity
	 * @return
	 */
	private List<CategoryEntityInterface> getParentEntityList(CategoryEntityInterface catEntity)
	{
		// As here the parent category entity whose table is not created is blocked so it is not added in list.
		List<CategoryEntityInterface> catEntities = new ArrayList<CategoryEntityInterface>();
		catEntities.add(catEntity);

		// Bug # 10265 - modified as per code review comment.
		// Reviewer name - Rajesh Patil
		CategoryEntityInterface categoryEnt = catEntity.getParentCategoryEntity();
		while (categoryEnt != null && ((CategoryEntity) categoryEnt).isCreateTable())
		{
			catEntities.add(0, categoryEnt);
			categoryEnt = categoryEnt.getParentCategoryEntity();
		}

		return catEntities;
	}

	/**
	 * This method populates record map for entity which belongs to root category entity.
	 * @param rootCatEntity
	 * @param rootEntRecords
	 * @param attributeValues
	 */
	private void populateRootEntityRecordMap(CategoryEntityInterface rootCatEntity,
			Map<AbstractAttributeInterface, Object> rootEntRecords,
			Map<BaseAbstractAttributeInterface, Object> attributeValues)
	{
		// Set of category data map entries.
		Set<Entry<BaseAbstractAttributeInterface, Object>> dataMapEntry = attributeValues
				.entrySet();

		AbstractAttributeInterface abstrAttribute = null;
		Object entityValue = null;
		CategoryAttributeInterface catAttribute;
		BaseAbstractAttributeInterface baseAbstrAttr;
		Object categoryValue;

		for (Entry<BaseAbstractAttributeInterface, Object> entry : dataMapEntry)
		{
			baseAbstrAttr = entry.getKey();
			categoryValue = entry.getValue();
			if (baseAbstrAttr instanceof CategoryAttributeInterface)
			{
				catAttribute = (CategoryAttributeInterface) baseAbstrAttr;
				abstrAttribute = catAttribute.getAbstractAttribute();
				entityValue = categoryValue;

				// Add root cat entity and its parent category entity's attribute.
				for (CategoryAttributeInterface rootCECatAttr : rootCatEntity
						.getAllCategoryAttributes())
				{
					if ((catAttribute == rootCECatAttr)
							&& (catAttribute.getIsRelatedAttribute() == null || !catAttribute
									.getIsRelatedAttribute()))
					{
						rootEntRecords.put(abstrAttribute, entityValue);
					}
				}
			}
		}

		for (CategoryAssociationInterface catAssociation : rootCatEntity
				.getCategoryAssociationCollection())
		{
			// Add all root category entity's associations.
			for (AssociationInterface association : catAssociation.getCategoryEntity().getEntity()
					.getAssociationCollection())
			{
				if (!rootEntRecords.containsKey(association))
				{
					rootEntRecords.put(association, new ArrayList());
				}
			}
			// Also add any associations which are related to parent entity's associations
			// i.e. associations between root category entity's parent entity and another class
			// whose category association is created.
			EntityInterface entity = catAssociation.getCategoryEntity().getEntity();
			while (entity.getParentEntity() != null)
			{
				for (AssociationInterface association : entity.getParentEntity()
						.getAssociationCollection())
				{
					if ((association.getTargetEntity() == catAssociation.getTargetCategoryEntity()
							.getEntity())
							&& !rootEntRecords.containsKey(association))
					{
						rootEntRecords.put(association, new ArrayList());
					}
				}

				entity = entity.getParentEntity();
			}
		}
	}

	/**
	 * @param categoryEnt
	 * @param recordId
	 * @param rlbkQryStack
	 * @param userId
	 * @param jdbcDAO
	 * @throws SQLException
	 * @throws DynamicExtensionsSystemException
	 */
	private void clearCategoryEntityData(CategoryEntityInterface categoryEnt, Long recordId,
			Stack<String> rlbkQryStack, Long userId, JDBCDAO jdbcDao)
			throws DynamicExtensionsSystemException
	{
		CategoryEntityInterface catEntity = categoryEnt;

		for (CategoryAssociationInterface catAssociation : catEntity
				.getCategoryAssociationCollection())
		{
			String colName = catAssociation.getConstraintProperties()
					.getTgtEntityConstraintKeyProperties()
					.getTgtForiegnKeyColumnProperties().getName();
			if (catAssociation.getTargetCategoryEntity().getChildCategories().size() != 0)
			{
				String selectQuery = "SELECT IDENTIFIER FROM "
						+ catAssociation.getTargetCategoryEntity().getTableProperties().getName()
						+ " WHERE "
						+ colName + " = ?" ;
				LinkedList<ColumnValueBean> queryDataList = new LinkedList<ColumnValueBean>();
				queryDataList.add(new ColumnValueBean(colName, recordId));
				List<Long> recordIds = getResultIDList(selectQuery, "IDENTIFIER", jdbcDao,queryDataList);
				for (Long recId : recordIds)
				{
					clearCategoryEntityData(catAssociation.getTargetCategoryEntity(), recId,
							rlbkQryStack, userId, jdbcDao);

					String deleteQuery = "DELETE FROM "
							+ catAssociation.getTargetCategoryEntity().getTableProperties()
									.getName() + " WHERE IDENTIFIER = ?";
					ColumnValueBean bean1 = new ColumnValueBean(IDENTIFIER, recId);
					LinkedList<ColumnValueBean> colValBeanList = new LinkedList<ColumnValueBean>();
					colValBeanList.add(bean1);
					executeUpdateQuery(deleteQuery, userId, jdbcDao,colValBeanList);
					rlbkQryStack.push(deleteQuery);
				}
			}
			else
			{
				String deleteQuery = "DELETE FROM "
						+ catAssociation.getTargetCategoryEntity().getTableProperties().getName()
						+ " WHERE "
						+ colName + " = ?";
				ColumnValueBean bean1 = new ColumnValueBean(colName, recordId);
				LinkedList<ColumnValueBean> colValBeanList = new LinkedList<ColumnValueBean>();
				colValBeanList.add(bean1);
				executeUpdateQuery(deleteQuery, userId, jdbcDao,colValBeanList);
				rlbkQryStack.push(deleteQuery);
			}
		}
	}

	/**
	 *
	 */
	@Override
	protected DynamicExtensionBaseQueryBuilder getQueryBuilderInstance()
	{
		return queryBuilder;
	}

	/**
	 * @param rootCatEntity
	 * @param recordId
	 * @return the record id of the hook entity
	 * @throws SQLException
	 * @throws DynamicExtensionsSystemException
	 */
	private Long getRootCategoryEntityRecordId(CategoryEntityInterface rootCatEntity,
			Long recordId, JDBCDAO jdbcDao) throws SQLException, DynamicExtensionsSystemException
	{
		StringBuffer query = new StringBuffer();
		query.append(SELECT_KEYWORD + WHITESPACE + RECORD_ID + WHITESPACE + FROM_KEYWORD
				+ WHITESPACE + rootCatEntity.getTableProperties().getName() + WHITESPACE
				+ WHERE_KEYWORD + WHITESPACE + IDENTIFIER + EQUAL + QUESTION_MARK);

		Long rootCatEntRecId = null;
		LinkedList<ColumnValueBean> queryDataList = new LinkedList<ColumnValueBean>();
		queryDataList.add(new ColumnValueBean(IDENTIFIER, recordId));
		List<Long> results = getResultIDList(query.toString(), RECORD_ID, jdbcDao,queryDataList);
		if (!results.isEmpty())
		{
			rootCatEntRecId = results.get(0);
		}

		return rootCatEntRecId;
	}

	/* (non-Javadoc)
	 * @see edu.common.dynamicextensions.entitymanager.CategoryManagerInterface#isPermissibleValuesSubsetValid(edu.common.dynamicextensions.domaininterface.AttributeInterface, java.util.List)
	 */
	public boolean isPermissibleValuesSubsetValid(UserDefinedDEInterface userDefinedDE,
			Map<String, Collection<SemanticPropertyInterface>> desiredPVs)
	{
		boolean arePVsValid = true;

		if (userDefinedDE != null)
		{
			List<Object> attributePVs = new ArrayList<Object>();

			Collection<PermissibleValueInterface> permissibleValueCollection = userDefinedDE
					.getPermissibleValueCollection();
			for (PermissibleValueInterface pv : permissibleValueCollection)
			{
				attributePVs.add(pv.getValueAsObject());
			}

			boolean allDoubleValues = CategoryGenerationUtil
					.isAllPermissibleValuesDouble(permissibleValueCollection);

			boolean allFloatValues = CategoryGenerationUtil
					.isAllPermissibleValuesFloat(permissibleValueCollection);

			boolean allIntegerValues = CategoryGenerationUtil
					.isAllPermissibleValuesInteger(permissibleValueCollection);

			boolean allShortValues = CategoryGenerationUtil
					.isAllPermissibleValuesShort(permissibleValueCollection);

			boolean allLongValues = CategoryGenerationUtil
					.isAllPermissibleValuesLong(permissibleValueCollection);

			if (allFloatValues && desiredPVs != null)
			{
				Set<String> permissibleValues = desiredPVs.keySet();
				for (String value : permissibleValues)
				{
					if (!attributePVs.contains(Float.parseFloat(value)))
					{
						arePVsValid = false;
					}
				}
			}
			else if (allDoubleValues && desiredPVs != null)
			{
				Set<String> permissibleValues = desiredPVs.keySet();
				for (String value : permissibleValues)
				{
					if (!attributePVs.contains(Double.parseDouble(value)))
					{
						arePVsValid = false;
					}
				}
			}
			else if (allIntegerValues && desiredPVs != null)
			{
				Set<String> permissibleValues = desiredPVs.keySet();
				for (String value : permissibleValues)
				{
					if (!attributePVs.contains(Integer.parseInt(value)))
					{
						arePVsValid = false;
					}
				}
			}
			else if (allShortValues && desiredPVs != null)
			{
				Set<String> permissibleValues = desiredPVs.keySet();
				for (String value : permissibleValues)
				{
					if (!attributePVs.contains(Short.parseShort(value)))
					{
						arePVsValid = false;
					}
				}
			}
			else if (allLongValues && desiredPVs != null)
			{
				Set<String> permissibleValues = desiredPVs.keySet();
				for (String value : permissibleValues)
				{
					if (!attributePVs.contains(Long.parseLong(value)))
					{
						arePVsValid = false;
					}
				}
			}
			else if (desiredPVs != null)
			{
				Set<String> permissibleValues = desiredPVs.keySet();
				for (String value : permissibleValues)
				{
					if (!attributePVs.contains(value))
					{
						arePVsValid = false;
					}
				}
			}
		}

		return arePVsValid;
	}

	/**
	 * This method executes a SQL query and returns a list of identifier, record identifier
	 * depending upon column name passed.
	 * @param query
	 * @param columnName
	 * @param queryDataList
	 * @return a list of identifier, record identifier depending upon column name passed.
	 * @throws SQLException
	 */
	private List<Long> getResultIDList(String query, String columnName, JDBCDAO jdbcDao, LinkedList<ColumnValueBean> queryDataList)
			throws DynamicExtensionsSystemException
	{
		List<Long> recordIds = new ArrayList<Long>();
		ResultSet resultSet = null;
		Object value = null;
		try
		{
			resultSet = jdbcDao.getResultSet(query,queryDataList,null);

			while (resultSet.next())
			{
				value = resultSet.getObject(columnName);
				recordIds.add(Long.valueOf(value.toString()));
			}
		}
		catch (DAOException e)
		{
			throw new DynamicExtensionsSystemException("Error executing query ", e);
		}
		catch (NumberFormatException e)
		{
			throw new DynamicExtensionsSystemException("Error executing query ", e);
		}
		catch (SQLException e)
		{
			throw new DynamicExtensionsSystemException("Error executing query ", e);
		}
		finally
		{
			try
			{
				jdbcDao.closeStatement(resultSet);
			}
			catch (DAOException e)
			{
				throw new DynamicExtensionsSystemException("Error executing query ", e);
			}
		}
		return recordIds;
	}

	/**
	 * getEntityRecordIdByRootCategoryEntityRecordId.
	 * @throws DynamicExtensionsSystemException
	 */
	public Long getEntityRecordIdByRootCategoryEntityRecordId(Long rootCategoryEntityRecordId,
			String rootCategoryTableName) throws DynamicExtensionsSystemException
	{
		String query = "select record_Id from " + rootCategoryTableName + " where IDENTIFIER = ? ";
		LinkedList<ColumnValueBean> queryDataList = new LinkedList<ColumnValueBean>();
		queryDataList.add(new ColumnValueBean(IDENTIFIER, rootCategoryEntityRecordId));
		return getEntityRecordId(query,queryDataList);
	}

	/**
	 * getEntityRecordIdByRootCategoryEntityRecordId.
	 * @throws DynamicExtensionsSystemException
	 */
	public Long getRootCategoryEntityRecordIdByEntityRecordId(Long rootCategoryEntityRecordId,
			String rootCategoryTableName) throws DynamicExtensionsSystemException
	{
		String query = "select IDENTIFIER from " + rootCategoryTableName + " where record_Id =?";

		LinkedList<ColumnValueBean> queryDataList = new LinkedList<ColumnValueBean>();
		queryDataList.add(new ColumnValueBean(RECORD_ID, rootCategoryEntityRecordId));
		return getEntityRecordId(query,queryDataList);
	}

	/**
	 *
	 * @param query
	 * @return
	 * @throws DynamicExtensionsSystemException
	 */
	private Long getEntityRecordId(String query,LinkedList<ColumnValueBean> queryDataList) throws DynamicExtensionsSystemException
	{
		ResultSet resultSet = null;
		Long entityRecordId = null;
		JDBCDAO jdbcDAO = null;
		try
		{
			jdbcDAO = DynamicExtensionsUtility.getJDBCDAO();
			resultSet = jdbcDAO.getResultSet(query,queryDataList,null);
			resultSet.next();
			entityRecordId = resultSet.getLong(1);
		}
		catch (DAOException e)
		{
			throw new DynamicExtensionsSystemException("Exception in query execution", e);
		}
		catch (SQLException e)
		{
			throw new DynamicExtensionsSystemException("Exception in query execution", e);
		}
		finally
		{
			try
			{
				jdbcDAO.closeStatement(resultSet);
				DynamicExtensionsUtility.closeJDBCDAO(jdbcDAO);
			}
			catch (DAOException e)
			{
				throw new DynamicExtensionsSystemException("Exception in query execution", e);
			}
		}
		return entityRecordId;
	}

	private LinkedList<ColumnValueBean> createColumnValueBeanListForDataEntry(long identifier,long recordId)
	{
		ColumnValueBean bean1 = new ColumnValueBean("IDENTIFIER", identifier);
		ColumnValueBean bean2 = new ColumnValueBean("ACTIVITY_STATUS", "ACTIVE");
		ColumnValueBean bean3 = new ColumnValueBean(RECORD_ID, recordId);
		LinkedList<ColumnValueBean> colValBeanList = new LinkedList<ColumnValueBean>();
		colValBeanList.add(bean1);
		colValBeanList.add(bean2);
		colValBeanList.add(bean3);
		return colValBeanList;
	}
	/**
	 * This method executes a SQL query.
	 * @param query
	 * @param colValBeanList
	 * @throws SQLException
	 * @throws DynamicExtensionsSystemException
	 */
	private void executeUpdateQuery(String query, Long userId, JDBCDAO jdbcDao, LinkedList<ColumnValueBean> colValBeanList)
			throws DynamicExtensionsSystemException
	{
		try
		{
			LinkedList<LinkedList<ColumnValueBean>> valueBeanList = new LinkedList<LinkedList<ColumnValueBean>>();
			valueBeanList.add(colValBeanList);
			queryBuilder.auditQuery(jdbcDao, query, userId);

			jdbcDao.executeUpdate(query, valueBeanList);
		}
		catch (DAOException e)
		{
			throw new DynamicExtensionsSystemException(e.getMessage(), e);
		}
	}

	/**
	 *
	 * @param dataValue
	 * @return
	 */
	private Map<AbstractAttributeInterface, Object> createAttributeMap(
			Map<BaseAbstractAttributeInterface, Object> dataValue)
	{
		Map<AbstractAttributeInterface, Object> attributes = new HashMap<AbstractAttributeInterface, Object>();

		Iterator<Map.Entry<BaseAbstractAttributeInterface,Object>> mapEntrySetIter = dataValue.entrySet().iterator();
		while (mapEntrySetIter.hasNext())
		{
			Entry<BaseAbstractAttributeInterface, Object> nextObject = mapEntrySetIter.next();
			BaseAbstractAttributeInterface baseAbstractAttr = nextObject.getKey();
			if (baseAbstractAttr instanceof CategoryAttributeInterface)
			{
				attributes.put(((CategoryAttributeInterface) baseAbstractAttr).getAbstractAttribute(),nextObject.getValue());
			}
		}

		return attributes;
	}

	/**
	 * It will fetch all the categories present
	 * @return will return the collection of categories.
	 * @throws DynamicExtensionsSystemException
	 * @throws DynamicExtensionsApplicationException
	 */
	public Collection<CategoryInterface> getAllCategories()
			throws DynamicExtensionsSystemException, DynamicExtensionsApplicationException
	{
		return getAllObjects(CategoryInterface.class.getName());
	}

}