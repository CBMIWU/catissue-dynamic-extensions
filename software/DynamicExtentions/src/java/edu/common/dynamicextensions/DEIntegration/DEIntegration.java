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
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import net.sf.ehcache.CacheException;
import edu.common.dynamicextensions.entitymanager.EntityManager;
import edu.common.dynamicextensions.entitymanager.EntityManagerUtil;
import edu.common.dynamicextensions.exception.DynamicExtensionsApplicationException;
import edu.common.dynamicextensions.exception.DynamicExtensionsSystemException;
import edu.common.dynamicextensions.util.DynamicExtensionsUtility;
import edu.common.dynamicextensions.util.global.DEConstants;
import edu.wustl.common.exception.BizLogicException;
import edu.wustl.dao.JDBCDAO;
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
			SQLException, DAOException
	{

		Collection catRecIds = new HashSet();

		String entityTableName;
		String columnName;
		String catTableName;
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

		if (entityMap.containsKey(entityContainerId.toString()))
		{
			entityTableName = (String) entityMap.get(entityContainerId.toString());
			columnName = (String) entityMap.get(hookEntityId + "_" + entityContainerId);
			/*
			 * Checked for categoryContainerId in the entityMap if present then get the Category Table Name from the Map
			 * else get it from DB.
			 */
			if (!entityMap.containsKey(categoryContainerId.toString()))
			{
				catTableName = EntityManager.getInstance().getDynamicTableName(categoryContainerId);
				entityMap.put(categoryContainerId.toString(), catTableName);
			}
		}

		else
		{
			entityTableName = EntityManager.getInstance().getDynamicTableName(entityContainerId);
			columnName = EntityManager.getInstance().getColumnNameForAssociation(hookEntityId,
					entityContainerId);
			catTableName = EntityManager.getInstance().getDynamicTableName(categoryContainerId);

			entityMap.put(entityContainerId.toString(), entityTableName);
			entityMap.put(hookEntityId + "_" + entityContainerId, columnName);
			entityMap.put(categoryContainerId.toString(), catTableName);

		}
		ResultSet resultSet = null;
		JDBCDAO jdbcDao = null;
		try
		{
			jdbcDao = DynamicExtensionsUtility.getJDBCDAO();
			String entitySql = "select identifier from " + entityTableName + " where " + columnName
					+ " = ?";
			LinkedList<ColumnValueBean> queryDataList = new LinkedList<ColumnValueBean>();
			queryDataList.add(new ColumnValueBean(columnName, staticRecId));
			resultSet = jdbcDao.getResultSet(entitySql, queryDataList, null); //util.executeQuery(entitySql);
			List recIdList = new ArrayList();
			while (resultSet.next())
			{
				recIdList.add(resultSet.getLong(1));
			}
			Iterator recIt = recIdList.iterator();
			while (recIt.hasNext())
			{
				catRecIds.add(recIt.next());
			}
		}
		finally
		{
			jdbcDao.closeStatement(resultSet);
			DynamicExtensionsUtility.closeDAO(jdbcDao);
		}

		return catRecIds;
	}

	public Collection getCategoryRecIdBasedOnHookEntityRecId(Long categoryContainerId,
			Long staticRecId, Long hookEntityId, JDBCDAO jdbcDao)
			throws DynamicExtensionsSystemException, SQLException, DAOException
	{

		Collection catRecIds = new HashSet();

		String entityTableName;
		String columnName;
		String catTableName;
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

		if (entityMap.containsKey(entityContainerId.toString()))
		{
			entityTableName = (String) entityMap.get(entityContainerId.toString());
			columnName = (String) entityMap.get(hookEntityId + "_" + entityContainerId);
			/*
			 * Checked for categoryContainerId in the entityMap if present then get the Category Table Name from the Map
			 * else get it from DB.
			 */
			if (!entityMap.containsKey(categoryContainerId.toString()))
			{
				catTableName = EntityManager.getInstance().getDynamicTableName(categoryContainerId);
				entityMap.put(categoryContainerId.toString(), catTableName);
			}
		}

		else
		{
			entityTableName = EntityManager.getInstance().getDynamicTableName(entityContainerId);
			columnName = EntityManager.getInstance().getColumnNameForAssociation(hookEntityId,
					entityContainerId);
			catTableName = EntityManager.getInstance().getDynamicTableName(categoryContainerId);

			entityMap.put(entityContainerId.toString(), entityTableName);
			entityMap.put(hookEntityId + "_" + entityContainerId, columnName);
			entityMap.put(categoryContainerId.toString(), catTableName);

		}
		ResultSet resultSet = null;

		try
		{
			String entitySql = "select identifier from " + entityTableName + " where " + columnName
					+ " = ?";
			LinkedList<ColumnValueBean> queryDataList = new LinkedList<ColumnValueBean>();
			queryDataList.add(new ColumnValueBean(columnName, staticRecId));
			resultSet = jdbcDao.getResultSet(entitySql, queryDataList, null); //util.executeQuery(entitySql);
			List recIdList = new ArrayList();
			while (resultSet.next())
			{
				recIdList.add(resultSet.getLong(1));
			}
			Iterator recIt = recIdList.iterator();
			while (recIt.hasNext())
			{
				catRecIds.add(recIt.next());
			}
		}
		finally
		{
			jdbcDao.closeStatement(resultSet);
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
		Long contId = null;
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
			Long containerId, Long hookEntityId) throws DynamicExtensionsSystemException,
			SQLException
	{
		Collection recIdList;
		String tableName = "";
		String columnName = "";
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
	 * @throws SQLException
	 */
	public Collection getDynamicEntityRecordIdFromHookEntityRecordId(String hookEntityRecId,
			Long containerId, Long hookEntityId, JDBCDAO dao)
			throws DynamicExtensionsSystemException, SQLException
	{
		String tableName = "";
		String columnName = "";
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
		Collection recIdList = entityManagerUtil.getResultInList(entitySql, queryDataList, dao);

		return recIdList;
	}

	/* (non-Javadoc)
	 * @see edu.common.dynamicextensions.DEIntegration.IntegrationInterface#associateRecords(java.lang.Long, java.lang.Long, java.lang.Long, java.lang.Long)
	 */
	public void associateRecords(Long containerId, Long staticEntityRecordId,
			Long dynamicEntityRecordId, Long hookEntityId)
			throws DynamicExtensionsApplicationException, DynamicExtensionsSystemException,
			BizLogicException, DAOException
	{
		// TODO Auto-generated constructor stub
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
	 * @throws SQLException
	 * @throws DAOException
	 */
	public Collection getDynamicRecordFromStaticId(String recEntryId, Long containerId,
			String hookEntityId) throws DynamicExtensionsSystemException,
			DynamicExtensionsApplicationException, CacheException, SQLException, DAOException
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
	 * @throws SQLException
	 * @throws DAOException
	 */
	public Collection getDynamicRecordForCategoryFromStaticId(String recEntryId, Long containerId,
			String hookEntityId, JDBCDAO dao) throws DynamicExtensionsSystemException,
			DynamicExtensionsApplicationException, CacheException, SQLException, DAOException
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

		Collection recIdList = new HashSet();
		String catTableName = "";

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

		ResultSet resultSet = null;
		JDBCDAO jdbcDao = null;
		try
		{
			jdbcDao = DynamicExtensionsUtility.getJDBCDAO();
			String catSql = "select RECORD_ID from " + catTableName + " where identifier= ?";

			LinkedList<ColumnValueBean> queryDataList = new LinkedList<ColumnValueBean>();
			queryDataList.add(new ColumnValueBean(DEConstants.IDENTIFIER, dynamicEntityRecordId));
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
		catch (DAOException e)
		{
			throw new DynamicExtensionsSystemException("Error while opening session", e);
		}
		finally
		{
			try
			{
				jdbcDao.closeStatement(resultSet);
				DynamicExtensionsUtility.closeDAO(jdbcDao);
			}
			catch (DAOException e)
			{
				throw new DynamicExtensionsSystemException("Error while closing session", e);
			}
		}
		return recIdList;
	}

}
