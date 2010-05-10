/**
 *<p>Title: </p>
 *<p>Description:  </p>
 *<p>Copyright:TODO</p>
 *@author
 *@version 1.0
 */

package edu.common.dynamicextensions.DEIntegration;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import net.sf.ehcache.CacheException;
import edu.common.dynamicextensions.domaininterface.CategoryEntityInterface;
import edu.common.dynamicextensions.domaininterface.userinterface.ContainerInterface;
import edu.common.dynamicextensions.entitymanager.CategoryManager;
import edu.common.dynamicextensions.entitymanager.CategoryManagerInterface;
import edu.common.dynamicextensions.entitymanager.EntityManager;
import edu.common.dynamicextensions.entitymanager.EntityManagerUtil;
import edu.common.dynamicextensions.exception.DynamicExtensionsApplicationException;
import edu.common.dynamicextensions.exception.DynamicExtensionsSystemException;
import edu.common.dynamicextensions.util.DynamicExtensionsUtility;
import edu.common.dynamicextensions.util.global.DEConstants;
import edu.wustl.common.exception.BizLogicException;
import edu.wustl.dao.JDBCDAO;
import edu.wustl.dao.QueryWhereClause;
import edu.wustl.dao.condition.INClause;
import edu.wustl.dao.exception.DAOException;
import edu.wustl.dao.query.generator.ColumnValueBean;

/**
 *
 * @author shital_lawhale
 *
 */
public class DEIntegration implements IntegrationInterface
{

	private static Map entityMap = new HashMap();
	private static Map categoryEntityMap = new HashMap();

	/* (non-Javadoc)
	 * @see edu.common.dynamicextensions.DEIntegration.IntegrationInterface#addAssociation(java.lang.Long, java.lang.Long, boolean, boolean)
	 */
	public Long addAssociation(Long hookEntityId, Long dynamicEntityId, boolean isEntityFromXmi,
			boolean isCategory) throws DynamicExtensionsApplicationException,
			DynamicExtensionsSystemException, BizLogicException
	{
		return null;
	}

	/**
	 *This method returns the Category Record Id BasedOn HookEntityRecId
	 *Steps:
	 *1) it will search for root entity of this category entity
	 *2) and then it will return record id of category based on root entity rec id
	 *@param categoryContainerId
	 *@param staticRecId
	 *@param hookEntityId
	 *@return the record id of the category depending on hook entity record id.
	 */
	public Collection getCategoryRecIdBasedOnHookEntityRecId(Long categoryContainerId,
			Long staticRecId, Long hookEntityId) throws DynamicExtensionsSystemException,
			DAOException
	{

		List<Long> staticRecIdList = new ArrayList<Long>();
		staticRecIdList.add(staticRecId);
		Collection recIdList = getCategoryRecIdBasedOnHookEntityRecId(categoryContainerId,
				staticRecIdList, hookEntityId);
		Collection catRecIds = new HashSet();
		updateHashSet(catRecIds, recIdList);
		return catRecIds;
	}

	public Collection getCategoryRecIdBasedOnHookEntityRecId(Long categoryContainerId,
			Long staticRecId, Long hookEntityId, JDBCDAO jdbcDao)
			throws DynamicExtensionsSystemException, DAOException
	{
		String entityTableName;
		String columnName;
		Long entityContainerId;
		if (categoryEntityMap.containsKey(categoryContainerId.toString()))
		{
			entityContainerId = (Long) categoryEntityMap.get(categoryContainerId.toString());
		}
		else
		{
			entityContainerId = EntityManager.getInstance().getCategoryRootContainerId(
					categoryContainerId);
			categoryEntityMap.put(categoryContainerId.toString(), entityContainerId);
		}
		String catTableName = EntityManager.getInstance().getDynamicTableName(categoryContainerId);
		if (entityMap.containsKey(entityContainerId.toString()))
		{
			entityTableName = (String) entityMap.get(entityContainerId.toString());
			columnName = (String) entityMap.get(hookEntityId + "_" + entityContainerId);
			if (!entityMap.containsKey(categoryContainerId.toString()))
			{
				entityMap.put(categoryContainerId.toString(), catTableName);
			}
		}

		else
		{
			entityTableName = EntityManager.getInstance().getDynamicTableName(entityContainerId);
			columnName = EntityManager.getInstance().getColumnNameForAssociation(hookEntityId,
					entityContainerId);
			entityMap.put(entityContainerId.toString(), entityTableName);
			entityMap.put(hookEntityId + "_" + entityContainerId, columnName);
			entityMap.put(categoryContainerId.toString(), catTableName);

		}

		String entitySql = "select identifier from " + entityTableName + " where " + columnName
				+ " = ?";
		List<ColumnValueBean> queryDataList = new LinkedList<ColumnValueBean>();
		queryDataList.add(new ColumnValueBean(columnName, staticRecId));
		Collection catRecIds;
		try
		{
			catRecIds = getResultCollection(entitySql, queryDataList, jdbcDao);
		}
		catch (DAOException e)
		{
			throw new DynamicExtensionsSystemException("Error while fetching dynamic record id", e);
		}
		return catRecIds;
	}

	/**
	 *
	 * @param hookEntityId
	 * @return the container Id of the DE entities that are associated with given static hook entity
	 */
	public Collection getDynamicEntitiesContainerIdFromHookEntity(Long hookEntityId)
			throws DynamicExtensionsSystemException
	{
		/**
		 * get all associated de entities with static entity and get its container id
		 */
		Collection dynamicList = EntityManager.getInstance()
				.getDynamicEntitiesContainerIdFromHookEntity(hookEntityId);
		return dynamicList;
	}

	/**
	 *
	 * @param containerId
	 * @return whether this entity is simple DE form /category.
	 */
	public boolean isCategory(Long containerId) throws DynamicExtensionsSystemException
	{
		boolean isCategory = false;
		Long contId;
		if (categoryEntityMap.containsKey((containerId + DEConstants.ISCATEGORY)))
		{
			contId = (Long) categoryEntityMap.get(containerId + DEConstants.ISCATEGORY);
		}
		else
		// if(contId == null)
		{
			contId = EntityManager.getInstance().isCategory(containerId);
			categoryEntityMap.put(containerId + DEConstants.ISCATEGORY, contId);
		}
		if (contId != null)
		{
			isCategory = true;
		}
		return isCategory;
	}

	/**
	 *This method returns the entry Record Id BasedOn HookEntityRecId
	 * @param categoryContainerId
	 * @param staticRecId
	 * @param hookEntityId
	 * @return the record id of the category depending on hook entity record id.
	 */
	public Collection getDynamicEntityRecordIdFromHookEntityRecordId(String hookEntityRecId,
			Long containerId, Long hookEntityId) throws DynamicExtensionsSystemException
	{
		Collection recIdList;
		String tableName;
		String columnName;
		if (entityMap.containsKey(containerId.toString()))
		{
			tableName = (String) entityMap.get(containerId.toString());
			columnName = (String) entityMap.get(hookEntityId + "_" + containerId);
		}
		else
		// if(tableName ==null)
		{
			tableName = EntityManager.getInstance().getDynamicTableName(containerId);
			columnName = EntityManager.getInstance().getColumnNameForAssociation(hookEntityId,
					containerId);

			entityMap.put(containerId.toString(), tableName);
			entityMap.put(hookEntityId + "_" + containerId, columnName);
		}
		EntityManagerUtil entityManagerUtil = new EntityManagerUtil();
		String entitySql = "select identifier from " + tableName + " where " + columnName + "=?";

		List<ColumnValueBean> queryDataList = new LinkedList<ColumnValueBean>();
		queryDataList.add(new ColumnValueBean(columnName, Long.valueOf(hookEntityRecId)));
		recIdList = entityManagerUtil.getResultInList(entitySql, queryDataList);

		return recIdList;
	}

	/**
	 * @param hookEntityRecId
	 * @param containerId
	 * @param hookEntityId
	 * @param dao
	 * @return
	 * @throws DynamicExtensionsSystemException
	 * @throws DAOException
	 */
	public Collection getDynamicEntityRecordIdFromHookEntityRecordId(String hookEntityRecId,
			Long containerId, Long hookEntityId, JDBCDAO dao)
			throws DynamicExtensionsSystemException, DAOException
	{
		List<Long> hookEntityRecIdList = new ArrayList<Long>();

		Collection recIdList = getDynamicEntityRecordIdFromHookEntityRecordId(hookEntityRecIdList,
				containerId, hookEntityId, dao);
		Collection dynRecIds = new HashSet();
		updateHashSet(dynRecIds, recIdList);
		return dynRecIds;
	}

	/**
	 *
	 * @param hookEntityId
	 * @return the record id of the category depending on hook entity record id.
	 */
	public Collection getCategoriesContainerIdFromHookEntity(Long hookEntityId)
			throws DynamicExtensionsSystemException
	{
		Collection dynamicList = EntityManager.getInstance()
				.getCategoriesContainerIdFromHookEntity(hookEntityId);
		return dynamicList;
	}

	/**
	 * this method returns DynamicRecord From association id
	 * @param recEntryId
	 * @param containerId
	 * @param hookEntityId
	 * @return
	 * @throws DynamicExtensionsApplicationException
	 * @throws DynamicExtensionsSystemException
	 * @throws CacheException
	 * @throws DAOException
	 */
	public Collection getDynamicRecordFromStaticId(String recEntryId, Long containerId,
			String hookEntityId) throws DynamicExtensionsSystemException,
			DynamicExtensionsApplicationException, CacheException, DAOException
	{
		Collection recIdList;
		if (isCategory(containerId))
		{
			recIdList = getCategoryRecIdBasedOnHookEntityRecId(containerId, Long
					.valueOf(recEntryId), Long.valueOf(hookEntityId));
		}
		else
		{
			recIdList = getDynamicEntityRecordIdFromHookEntityRecordId(recEntryId, containerId,
					Long.valueOf(hookEntityId));
		}
		return recIdList;
	}

	/**
	 * @param recEntryId
	 * @param containerId
	 * @param hookEntityId
	 * @param dao
	 * @return
	 * @throws DynamicExtensionsSystemException
	 * @throws DynamicExtensionsApplicationException
	 * @throws CacheException
	 * @throws DAOException
	 */
	public Collection getDynamicRecordForCategoryFromStaticId(String recEntryId, Long containerId,
			String hookEntityId, JDBCDAO dao) throws DynamicExtensionsSystemException,
			DynamicExtensionsApplicationException, CacheException, DAOException
	{
		Collection recIdList;
		if (isCategory(containerId))
		{
			recIdList = getCategoryRecIdBasedOnHookEntityRecId(containerId, Long
					.valueOf(recEntryId), Long.valueOf(hookEntityId), dao);
		}
		else
		{
			recIdList = getDynamicEntityRecordIdFromHookEntityRecordId(recEntryId, containerId,
					Long.valueOf(hookEntityId), dao);
		}
		return recIdList;
	}

	/**
	 *This method returns the Category Record_Id ie id of its root entity
	 *BasedOn categoryContainerId and dynamicEntityRecordId
	 * @param categoryContainerId
	 * @param dynamicEntityRecordId
	 * @return
	 * @throws DynamicExtensionsSystemException
	 */
	public Collection<Long> getCategoryRecordIdBasedOnCategoryId(Long categoryContainerId,
			Long dynamicEntityRecordId) throws DynamicExtensionsSystemException
	{

		String catTableName;

		/*
		 * Checked for categoryContainerId in the entityMap if present then get the Category Table Name from the Map
		 * else get it from DB.
		 */
		if (entityMap.containsKey(categoryContainerId.toString()))
		{
			catTableName = (String) entityMap.get(categoryContainerId.toString());
		}
		else
		{
			catTableName = EntityManager.getInstance().getDynamicTableName(categoryContainerId);
			entityMap.put(categoryContainerId.toString(), catTableName);
		}
		String catSql = "select RECORD_ID from " + catTableName + " where identifier= ?";
		List<ColumnValueBean> queryDataList = new LinkedList<ColumnValueBean>();
		queryDataList.add(new ColumnValueBean(DEConstants.IDENTIFIER, dynamicEntityRecordId));
		Collection recIdList;
		try
		{
			recIdList = getResultCollection(catSql, queryDataList, null);
		}
		catch (DAOException e)
		{
			throw new DynamicExtensionsSystemException("Error occured in DAO operations", e);
		}
		return recIdList;
	}

	private Collection getResultCollection(String catSql, List<ColumnValueBean> queryDataList,
			JDBCDAO dao) throws DynamicExtensionsSystemException, DAOException
	{
		ResultSet resultSet = null;
		Collection recIdList = new HashSet();
		JDBCDAO jdbcDao = dao;
		try
		{
			if (dao == null)
			{
				jdbcDao = DynamicExtensionsUtility.getJDBCDAO();
			}
			resultSet = jdbcDao.getResultSet(catSql, queryDataList, null); //util.executeQuery(entitySql);
			while (resultSet.next())
			{
				recIdList.add(resultSet.getLong(1));
			}
		}
		catch (SQLException e)
		{
			throw new DynamicExtensionsSystemException("Error while opening session", e);
		}
		finally
		{

			jdbcDao.closeStatement(resultSet);
			if (dao == null)
			{
				DynamicExtensionsUtility.closeDAO(jdbcDao);
			}
		}
		return recIdList;
	}

	/**
	 * @param hookEntityId
	 * @param catRecIds
	 * @param recIdList
	 * @throws DynamicExtensionsSystemException
	 * @throws DAOException
	 */
	private void updateHashSet(Collection catRecIds, Collection recIdList)
			throws DynamicExtensionsSystemException, DAOException
	{
		if (recIdList != null && recIdList.toArray().length > 0
				&& ((List) recIdList.toArray()[0]).size() > 0)
		{
			catRecIds.add(Long.valueOf(((List) recIdList.toArray()[0]).get(0).toString()));

		}
	}

	/**
	 * @param staticRecIdList
	 * @param entityTableName
	 * @param columnName
	 * @param jdbcDao
	 * @return
	 * @throws DAOException
	 */
	private Collection<Long> getRecordIdList(List<Long> staticRecIdList, String entityTableName,
			String columnName, JDBCDAO jdbcDao) throws DAOException
	{
		List<ColumnValueBean> colValueBean = new ArrayList<ColumnValueBean>();
		List<Object> columnValues = new ArrayList<Object>();

		String[] columnNames = {"identifier"};

		for (Long colValue : staticRecIdList)
		{
			colValueBean.add(new ColumnValueBean(colValue));
			columnValues.add('?');
		}
		QueryWhereClause queryWhereClause = new QueryWhereClause(entityTableName);
		queryWhereClause.addCondition(new INClause(columnName, columnValues.toArray()));
		//jdbcDao.retrieve(entityTableName, columnNames, queryWhereClause, false);
		return jdbcDao
				.retrieve(entityTableName, columnNames, queryWhereClause, colValueBean, false);

	}

	/**
	 *	This method returns the Category Record Id collection BasedOn HookEntityRecId
	 * @param hookEntityRecIdList
	 * @param containerId
	 * @param hookEntityId
	 * @param dao
	 * @return
	 * @throws DynamicExtensionsSystemException
	 * @throws DAOException
	 */
	public Collection getDynamicEntityRecordIdFromHookEntityRecordId(
			List<Long> hookEntityRecIdList, Long containerId, Long hookEntityId, JDBCDAO dao)
			throws DynamicExtensionsSystemException, DAOException
	{
		String tableName;
		String columnName;
		if (entityMap.containsKey(containerId.toString()))
		{
			tableName = (String) entityMap.get(containerId.toString());
			columnName = (String) entityMap.get(hookEntityId + "_" + containerId);
		}
		else
		{
			tableName = EntityManager.getInstance().getDynamicTableName(containerId);
			columnName = EntityManager.getInstance().getColumnNameForAssociation(hookEntityId,
					containerId);

			entityMap.put(containerId.toString(), tableName);
			entityMap.put(hookEntityId + "_" + containerId, columnName);
		}
		return getRecordIdList(hookEntityRecIdList, tableName, columnName, dao);
	}

	/**
	 * @param recEntryId
	 * @param containerId
	 * @param hookEntityId
	 * @param dao
	 * @return
	 * @throws DynamicExtensionsSystemException
	 * @throws DynamicExtensionsApplicationException
	 * @throws CacheException
	 * @throws DAOException
	 */
	public Collection<Long> getDynamicRecordForCategoryFromStaticId(List<Long> recEntryId,
			Long containerId, String hookEntityId, JDBCDAO dao)
			throws DynamicExtensionsSystemException, DynamicExtensionsApplicationException,
			CacheException, DAOException
	{
		Collection<Long> recIdList;
		if (isCategory(containerId))
		{
			recIdList = getCategoryRecIdBasedOnHookEntityRecId(containerId, recEntryId, Long
					.valueOf(hookEntityId));
		}
		else
		{
			recIdList = getDynamicEntityRecordIdFromHookEntityRecordId(recEntryId, containerId,
					Long.valueOf(hookEntityId), dao);
		}
		return recIdList;
	}

	/**
	 *This method returns the Category Record Id BasedOn HookEntityRecId
	 *Steps:
	 *1) it will search for root entity of this category entity
	 *2) and then it will return record id of category based on root entity rec id
	 *@param categoryContainerId
	 *@param staticRecId
	 *@param hookEntityId
	 *@return the record id of the category depending on hook entity record id.
	 */
	public Collection<Long> getCategoryRecIdBasedOnHookEntityRecId(Long categoryContainerId,
			List<Long> staticRecId, Long hookEntityId) throws DynamicExtensionsSystemException,
			DAOException
	{
		Collection<Long> catRecIds;
		String entityTableName;
		String columnName;
		Long entityContainerId;

		if (categoryEntityMap.containsKey(categoryContainerId.toString()))
		{
			entityContainerId = (Long) categoryEntityMap.get(categoryContainerId.toString());
		}
		else
		{
			entityContainerId = EntityManager.getInstance().getCategoryRootContainerId(
					categoryContainerId);
			categoryEntityMap.put(categoryContainerId.toString(), entityContainerId);
		}
		String catTableName = EntityManager.getInstance().getDynamicTableName(categoryContainerId);
		if (entityMap.containsKey(entityContainerId.toString()))
		{
			entityTableName = (String) entityMap.get(entityContainerId.toString());
			columnName = (String) entityMap.get(hookEntityId + "_" + entityContainerId);
			if (!entityMap.containsKey(categoryContainerId.toString()))
			{
				entityMap.put(categoryContainerId.toString(), catTableName);
			}
		}

		else
		{
			entityTableName = EntityManager.getInstance().getDynamicTableName(entityContainerId);
			columnName = EntityManager.getInstance().getColumnNameForAssociation(hookEntityId,
					entityContainerId);
			entityMap.put(entityContainerId.toString(), entityTableName);
			entityMap.put(hookEntityId + "_" + entityContainerId, columnName);
			entityMap.put(categoryContainerId.toString(), catTableName);

		}
		JDBCDAO jdbcDao = null;
		try
		{
			jdbcDao = DynamicExtensionsUtility.getJDBCDAO();
			catRecIds = getRecordIdList(staticRecId, entityTableName, columnName, jdbcDao);
		}
		finally
		{
			DynamicExtensionsUtility.closeDAO(jdbcDao);
		}
		return catRecIds;
	}

	/**
	 * @param container
	 * @param recordEntryId
	 * @param recEntryEntityId
	 * @return
	 * @throws DynamicExtensionsSystemException
	 * @throws DAOException
	 * @throws SQLException
	 */
	public boolean isDataHooked(ContainerInterface container, Long recordEntryId,
			Long recEntryEntityId) throws DynamicExtensionsSystemException, DAOException,
			SQLException
	{
		boolean isDataHooked = false;
		Collection entityRecordList = getCategoryRecIdBasedOnHookEntityRecId(container.getId(),
				recordEntryId, Long.valueOf(recEntryEntityId));
		CategoryManagerInterface categoryManager = CategoryManager.getInstance();
		if (entityRecordList != null && !entityRecordList.isEmpty())
		{
			Long dynamicRecId = (Long) entityRecordList.iterator().next();
			CategoryEntityInterface rootCatEntity = (CategoryEntityInterface) container
					.getAbstractEntity();

			Long categoryRecordId = categoryManager.getRootCategoryEntityRecordIdByEntityRecordId(
					dynamicRecId, rootCatEntity.getTableProperties().getName());
			if (categoryRecordId != null && !"".equalsIgnoreCase(categoryRecordId.toString()))
			{
				isDataHooked = true;
			}
		}
		return isDataHooked;
	}
}
