
package edu.common.dynamicextensions.entitymanager;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Stack;

import edu.common.dynamicextensions.dao.impl.DynamicExtensionDAO;
import edu.common.dynamicextensions.dao.impl.DynamicExtensionDBFactory;
import edu.common.dynamicextensions.dao.impl.IDEDBUtility;
import edu.common.dynamicextensions.domain.AbstractAttribute;
import edu.common.dynamicextensions.domain.Association;
import edu.common.dynamicextensions.domain.Attribute;
import edu.common.dynamicextensions.domain.BooleanAttributeTypeInformation;
import edu.common.dynamicextensions.domain.CategoryAssociation;
import edu.common.dynamicextensions.domain.CategoryAttribute;
import edu.common.dynamicextensions.domain.CategoryEntity;
import edu.common.dynamicextensions.domain.DateAttributeTypeInformation;
import edu.common.dynamicextensions.domain.DomainObjectFactory;
import edu.common.dynamicextensions.domain.DoubleAttributeTypeInformation;
import edu.common.dynamicextensions.domain.Entity;
import edu.common.dynamicextensions.domain.FileAttributeTypeInformation;
import edu.common.dynamicextensions.domain.FloatAttributeTypeInformation;
import edu.common.dynamicextensions.domain.IntegerAttributeTypeInformation;
import edu.common.dynamicextensions.domain.LongAttributeTypeInformation;
import edu.common.dynamicextensions.domain.NumericAttributeTypeInformation;
import edu.common.dynamicextensions.domain.ObjectAttributeTypeInformation;
import edu.common.dynamicextensions.domain.ShortAttributeTypeInformation;
import edu.common.dynamicextensions.domain.StringAttributeTypeInformation;
import edu.common.dynamicextensions.domaininterface.AbstractAttributeInterface;
import edu.common.dynamicextensions.domaininterface.AbstractEntityInterface;
import edu.common.dynamicextensions.domaininterface.AssociationInterface;
import edu.common.dynamicextensions.domaininterface.AttributeInterface;
import edu.common.dynamicextensions.domaininterface.AttributeTypeInformationInterface;
import edu.common.dynamicextensions.domaininterface.BaseAbstractAttributeInterface;
import edu.common.dynamicextensions.domaininterface.CategoryAssociationInterface;
import edu.common.dynamicextensions.domaininterface.CategoryAttributeInterface;
import edu.common.dynamicextensions.domaininterface.CategoryEntityInterface;
import edu.common.dynamicextensions.domaininterface.EntityInterface;
import edu.common.dynamicextensions.domaininterface.RoleInterface;
import edu.common.dynamicextensions.domaininterface.databaseproperties.ConstraintKeyPropertiesInterface;
import edu.common.dynamicextensions.domaininterface.databaseproperties.ConstraintPropertiesInterface;
import edu.common.dynamicextensions.exception.DataTypeFactoryInitializationException;
import edu.common.dynamicextensions.exception.DynamicExtensionsApplicationException;
import edu.common.dynamicextensions.exception.DynamicExtensionsSystemException;
import edu.common.dynamicextensions.processor.ProcessorConstants;
import edu.common.dynamicextensions.util.ConstraintKeyPropertiesComparator;
import edu.common.dynamicextensions.util.DynamicExtensionsUtility;
import edu.common.dynamicextensions.util.global.ErrorConstants;
import edu.common.dynamicextensions.util.global.DEConstants.AssociationType;
import edu.common.dynamicextensions.util.global.DEConstants.Cardinality;
import edu.wustl.common.util.Utility;
import edu.wustl.common.util.global.Constants;
import edu.wustl.common.util.global.Status;
import edu.wustl.common.util.logger.Logger;
import edu.wustl.dao.HibernateDAO;
import edu.wustl.dao.JDBCDAO;
import edu.wustl.dao.daofactory.DAOConfigFactory;
import edu.wustl.dao.exception.DAOException;
import edu.wustl.dao.query.generator.ColumnValueBean;

/**
 * This class provides the methods that builds the queries that are required for
 * creation and updating of the tables of the entities.These queries are as per SQL-99 standard.
 * Theses methods can be over-ridden  in the database specific query builder class to
 * provide any database-specific implementation.
 *
 * @author rahul_ner,pavan_kalantri
 */
public class DynamicExtensionBaseQueryBuilder
		implements
			EntityManagerConstantsInterface,
			EntityManagerExceptionConstantsInterface,
			DynamicExtensionsQueryBuilderConstantsInterface
{

	protected EntityManagerUtil entityManagerUtil = new EntityManagerUtil();

	/**
	 * This method builds the list of all the queries that need to be executed in order to
	 * create the data table for the entity and its associations.
	 * @param entity entity for which the queries are formed.
	 * @param revQueries for every data table query this method creates a reverse query.
	 * @return List<String> list of queries
	 * @throws DynamicExtensionsSystemException
	 * @throws DynamicExtensionsApplicationException
	 */
	public List<String> getCreateEntityQueryList(Entity entity, List<String> revQueries)
			throws DynamicExtensionsSystemException, DynamicExtensionsApplicationException
	{
		List<String> queries = new ArrayList<String>();

		// Get query to create main table with primitive attributes.
		queries.addAll(getCreateMainTableQuery(entity, revQueries));

		return queries;
	}

	/**
	 * This method builds the list of all the queries that need to be executed in order to
	 * create the data table for the entity and its associations.
	 * @param entity entity for which the queries are formed.
	 * @param revQueries for every data table query this method creates a reverse query.
	 * @return List<String> list of queries
	 * @throws DynamicExtensionsSystemException
	 * @throws DynamicExtensionsApplicationException
	 */
	public List<String> getUpdateEntityQueryList(Entity entity, List<String> revQueries)
			throws DynamicExtensionsSystemException, DynamicExtensionsApplicationException
	{
		List<String> queries = new ArrayList<String>();

		queries.addAll(getForeignKeyConstraintQuery(entity, revQueries));

		// Get query to create associations, it involves altering source/target
		// table or creating middle table depending upon the cardinalities.
		queries.addAll(getCreateAssociationsQueryList(entity, revQueries));

		return queries;
	}

	/**
	 * This method builds the list of all the queries that need to be executed in order to
	 * create the data table for the entity and its associations.
	 * @param catEntity category entity for which the queries are formed.
	 * @param revQueries for every data table query this method creates a reverse query.
	 * @return List<String> list of queries
	 * @throws DynamicExtensionsSystemException
	 * @throws DynamicExtensionsApplicationException
	 */
	public List<String> getUpdateCategoryEntityQueryList(CategoryEntityInterface catEntity,
			List<String> revQueries) throws DynamicExtensionsSystemException,
			DynamicExtensionsApplicationException
	{
		List<String> queries = new ArrayList<String>();

		// Get queries for foreign key constraint for inheritance and to create associations.
		// It involves altering source/target table or creating middle table depending upon the cardinalities.
		queries.addAll(getForeignKeyConstraintQuery(catEntity, revQueries));
		queries.addAll(getCreateAssociationsQueryList(catEntity, revQueries));

		return queries;
	}

	/**
	 * This method is used to execute the data table queries for entity in case of editing the entity.
	 * This method takes each attribute of the entity and then scans for any changes and builds the
	 * alter query for each attribute for the entity.
	 * @param entity entity for which the queries are formed.
	 * @param dbaseCopy database copy of the entity.
	 * @param attrRlbkQries roll back queries list.
	 * @return List<String> list of queries.
	 * @throws DynamicExtensionsSystemException
	 * @throws DynamicExtensionsApplicationException
	 */
	public List<String> getUpdateEntityQueryList(Entity entity, Entity dbaseCopy,
			List<String> attrRlbkQries, HibernateDAO hibernateDAO)
			throws DynamicExtensionsSystemException, DynamicExtensionsApplicationException
	{
		Logger.out.debug("getUpdateEntityQueryList : Entering method");
		List<String> updateQries = new ArrayList<String>();
		// Get the query for any attribute that is modified.
		List<String> updAttrQries = getUpdateAttributeQueryList(entity, dbaseCopy, attrRlbkQries);
		// Get the query part for if primary keys are changed
		List<String> prmCnstrQueries = getPrimaryKeyQueryList(entity, dbaseCopy, attrRlbkQries);
		//Get the query part inheritance changed
		List<String> entInheQries = getInheritanceQueryList(entity, dbaseCopy, attrRlbkQries);

		// Get the query for any association that is modified.
		List<String> updAssoQries = getUpdateAssociationsQueryList(entity, dbaseCopy,
				attrRlbkQries, hibernateDAO);

		updateQries.addAll(updAttrQries);
		updateQries.addAll(prmCnstrQueries);
		updateQries.addAll(entInheQries);
		updateQries.addAll(updAssoQries);
		// process all removed attributes
		processRemovedAttributes(entity, dbaseCopy, updateQries, attrRlbkQries);

		Logger.out.debug("getUpdateEntityQueryList Exiting method");

		return updateQries;
	}

	/**
	 * This method checks weather the primary key of the entity is changed & if changed
	 * it searches for all child entities of that entity remove all foreign key constraints
	 * then modify primary key of the entity and again apply the foreign key constraint of its child
	 * @param entity for which to check weather primary key is changed
	 * @param dbaseCopy database copy of the entity
	 * @param attrRlbkQries rollback query list
	 * @return List<String> which are queries to be executed
	 * @throws DynamicExtensionsSystemException
	 * @throws DynamicExtensionsApplicationException
	 */
	private List<String> getPrimaryKeyQueryList(EntityInterface entity, EntityInterface dbaseCopy,
			List<String> attrRlbkQries) throws DynamicExtensionsSystemException,
			DynamicExtensionsApplicationException
	{
		List<String> queries = new ArrayList<String>();
		StringBuffer query = new StringBuffer();
		query.append(SELECT_KEYWORD).append(WHITESPACE).append(COUNT_KEYWORD).append(
				OPENING_BRACKET).append(ASTERIX).append(CLOSING_BRACKET).append(WHITESPACE).append(
				FROM_KEYWORD).append(entity.getTableProperties().getName());
		if (EntityManagerUtil.isPrimaryKeyChanged(entity, dbaseCopy))
		{
			int noOfRecords = EntityManagerUtil.getNoOfRecord(query.toString(), null);
			if (noOfRecords != 0)
			{
				throw new DynamicExtensionsApplicationException(
						"primary key can not be changed data present in the entity"
								+ entity.getName());
			}
			//remove all foreignKey constraint in other entities which depends on this entity
			queries.addAll(getForeignConstraintRemoveQueryForChangedPrimaryKey(entity,
					attrRlbkQries));

			//Change primary key constraint of the entity
			List<String> prmKeyRemoveQuery = getPrimaryKeyRemoveCnstrQuery(dbaseCopy, attrRlbkQries);
			queries.addAll(prmKeyRemoveQuery);
			List<String> prmKeyQuery = getPrimaryKeyQuery(entity, attrRlbkQries);
			queries.addAll(prmKeyQuery);

			//Add all foreign key constraints in other entities which depends on this entity & references new primary key
			queries.addAll(getForeignConstraintQueryForChangedPrimaryKey(entity, attrRlbkQries));

		}

		return queries;
	}

	/**
	 * This method prepares queries for applying the foreign key constraint
	 * of all the child entity of the current parent whose primary key is changed
	 * @param entity entity whose primary key is changed
	 * @param attrRlbkQries rollback query list
	 * @return list of queries
	 * @throws DynamicExtensionsSystemException
	 */
	private List<String> getForeignConstraintQueryForChangedPrimaryKey(EntityInterface entity,
			List<String> attrRlbkQries) throws DynamicExtensionsSystemException
	{
		List<String> queries = new ArrayList<String>();
		Collection<EntityInterface> entityColl = entity.getEntityGroup().getEntityCollection();
		DynamicExtensionBaseQueryBuilder queryBuilder = QueryBuilderFactory.getQueryBuilder();
		EntityInterface depEntityParent;
		String frnCnstrQry;
		String frnCnstrRemQry;
		for (EntityInterface depEntity : entityColl)
		{
			depEntityParent = depEntity.getParentEntity();
			if (depEntityParent != null && depEntityParent.equals(entity))
			{
				//this is child of the current entity so drop foreign key constraint
				queries.addAll(queryBuilder.getAddColumnQueryForForeignKeyConstraint(depEntity,
						depEntityParent, attrRlbkQries, true));
				frnCnstrQry = queryBuilder.getForeignKeyConstraintQueryForInheritance(depEntity,
						depEntityParent);
				queries.add(frnCnstrQry);

				frnCnstrRemQry = queryBuilder.getForeignKeyRemoveConstraintQueryForInheritance(
						depEntity, depEntityParent);
				attrRlbkQries.add(frnCnstrRemQry);
			}
		}
		return queries;
	}

	/**
	 * This method prepares queries for removing the foreign key constraint
	 * of all the child entity of the current parent whose primary key is changed
	 * It searches for child entities in its own entityGroup
	 * @param entity whose primary key is changed
	 * @param attrRlbkQries rollback query list
	 * @return list of queries
	 * @throws DynamicExtensionsSystemException
	 */
	private List<String> getForeignConstraintRemoveQueryForChangedPrimaryKey(
			EntityInterface entity, List<String> attrRlbkQries)
			throws DynamicExtensionsSystemException
	{
		List<String> queries = new ArrayList<String>();
		Collection<EntityInterface> entityColl = entity.getEntityGroup().getEntityCollection();
		EntityInterface dbDepEntity = null;
		DynamicExtensionBaseQueryBuilder queryBuilder = QueryBuilderFactory.getQueryBuilder();
		EntityInterface dbDepEntityParent;
		String frnCnstrRlbkQry;
		String frnCnstrRemQry;
		for (EntityInterface depEntity : entityColl)
		{
			if (depEntity.getParentEntity() != null && depEntity.getParentEntity().equals(entity))
			{

				dbDepEntity = (EntityInterface) DynamicExtensionsUtility.getCleanObject(
						Entity.class.getCanonicalName(), depEntity.getId());
				dbDepEntityParent = dbDepEntity.getParentEntity();
				frnCnstrRemQry = queryBuilder.getForeignKeyRemoveConstraintQueryForInheritance(
						dbDepEntity, dbDepEntityParent);
				queries.add(frnCnstrRemQry);

				frnCnstrRlbkQry = getForeignKeyConstraintQueryForInheritance(dbDepEntity,
						dbDepEntityParent);
				attrRlbkQries.add(frnCnstrRlbkQry);

				queries.addAll(queryBuilder.getAddColumnQueryForForeignKeyConstraint(dbDepEntity,
						dbDepEntityParent, attrRlbkQries, false));

			}
		}

		return queries;
	}

	/**
	 * It prepares the queries for applying the primary key constraint on the entity
	 * @param entity on which primary key constraint is to be applied
	 * @param attrRlbkQries rollback query list
	 * @return List<String> queries
	 * @throws DynamicExtensionsSystemException
	 */
	protected List<String> getPrimaryKeyQuery(EntityInterface entity, List<String> attrRlbkQries)
			throws DynamicExtensionsSystemException
	{
		Collection<AttributeInterface> prmKeyAttrColl = entity
				.getPrimarykeyAttributeCollectionInSameEntity();
		StringBuffer prmKeyQuery = new StringBuffer();
		List<String> primaryKeyName = new ArrayList<String>();
		List<String> queries = new ArrayList<String>();
		String tableName = entity.getTableProperties().getName();

		//add all the not null constraints
		getNotNullQeries(prmKeyAttrColl, queries, attrRlbkQries);

		prmKeyQuery.append(ALTER_TABLE).append(WHITESPACE).append(tableName).append(WHITESPACE)
				.append(ADD_KEYWORD).append(WHITESPACE).append(PRIMARY_KEY);
		for (AttributeInterface attribute : prmKeyAttrColl)
		{
			primaryKeyName.add(attribute.getColumnProperties().getName());
		}
		//sorting the primary key name cause sequence matters when applying the foreign key
		//constraint on the child entities
		Collections.sort(primaryKeyName);
		prmKeyQuery.append(OPENING_BRACKET);
		Iterator nameIterator = primaryKeyName.iterator();
		while (nameIterator.hasNext())
		{
			prmKeyQuery.append(nameIterator.next());
			if (nameIterator.hasNext())
			{
				prmKeyQuery.append(COMMA);
			}
		}
		prmKeyQuery.append(CLOSING_BRACKET);
		queries.add(prmKeyQuery.toString());
		StringBuffer prmKeyRemoveQuery = new StringBuffer();
		prmKeyRemoveQuery.append(ALTER_TABLE).append(WHITESPACE).append(tableName).append(
				WHITESPACE).append(DROP_KEYWORD).append(WHITESPACE).append(PRIMARY_KEY);
		attrRlbkQries.add(prmKeyRemoveQuery.toString());
		return queries;
	}

	/**
	 * This method will create the Query for applying the not null
	 * constraint on the primary key attributes column
	 * @param prmKeyAttrColl collection of Attributes on which not null is to be applied
	 * @param queries List of queries
	 * @param attrRlbkQries roll back query list
	 * @throws DynamicExtensionsSystemException
	 */
	private void getNotNullQeries(Collection<AttributeInterface> prmKeyAttrColl,
			List<String> queries, List<String> attrRlbkQries)
			throws DynamicExtensionsSystemException
	{

		String notNullQuery;
		String rollBackQuery;
		DynamicExtensionBaseQueryBuilder queryBuilder = QueryBuilderFactory.getQueryBuilder();
		for (AttributeInterface attribute : prmKeyAttrColl)
		{
			//create queries for applying not null constraint on the primary key attribute columns
			//and there roll back queries
			notNullQuery = queryBuilder.addNotNullConstraintQuery(attribute);
			rollBackQuery = queryBuilder.dropNotNullConstraintQuery(attribute);
			if (notNullQuery != null && rollBackQuery != null)
			{
				queries.add(notNullQuery);
				attrRlbkQries.add(rollBackQuery);
			}
		}

	}

	/**
	 * This method will create the Query for dropping the not null
	 * constraint on the primary key attributes column collection
	 * @param prmKeyAttrColl collection of Attributes on which not null is to be dropped
	 * @param queries List of query
	 * @param attrRlbkQries roll back query list
	 * @throws DynamicExtensionsSystemException
	 */
	private void getDropNotNullQeries(Collection<AttributeInterface> prmKeyAttrColl,
			List<String> queries, List<String> attrRlbkQries)
			throws DynamicExtensionsSystemException
	{
		String notNullQuery;
		String rlbackQuery;
		DynamicExtensionBaseQueryBuilder queryBuilder = QueryBuilderFactory.getQueryBuilder();
		for (AttributeInterface attribute : prmKeyAttrColl)
		{
			// after removing the primary key on attribute it should also be nullable so
			//removing not null constraint on it
			notNullQuery = queryBuilder.dropNotNullConstraintQuery(attribute);
			rlbackQuery = queryBuilder.addNotNullConstraintQuery(attribute);
			if (notNullQuery != null && rlbackQuery != null)
			{
				queries.add(notNullQuery);
				attrRlbkQries.add(rlbackQuery);
			}
		}
	}

	/**
	 * It prepares he queries for removing the primary key constraint on the entity
	 * @param entity whose primary key constraint is to be removed
	 * @param attrRlbkQries rollback Query List
	 * @return List<String> queries to be executed
	 * @throws DynamicExtensionsSystemException
	 */
	protected List<String> getPrimaryKeyRemoveCnstrQuery(EntityInterface entity,
			List<String> attrRlbkQries) throws DynamicExtensionsSystemException
	{
		StringBuffer prmKeyRemoveQuery = new StringBuffer();
		List<String> queries = new ArrayList<String>();
		String tableName = entity.getTableProperties().getName();
		Collection<AttributeInterface> prmKeyAttrColl = entity
				.getPrimarykeyAttributeCollectionInSameEntity();
		prmKeyRemoveQuery.append(ALTER_TABLE).append(WHITESPACE).append(tableName).append(
				WHITESPACE).append(DROP_KEYWORD).append(WHITESPACE).append(PRIMARY_KEY);
		queries.add(prmKeyRemoveQuery.toString());
		StringBuffer query = new StringBuffer();
		StringBuffer primaryKeyName = new StringBuffer();

		query.append(ALTER_TABLE).append(WHITESPACE).append(tableName).append(WHITESPACE).append(
				ADD_KEYWORD).append(WHITESPACE).append(PRIMARY_KEY);
		for (AttributeInterface attribute : prmKeyAttrColl)
		{
			if (primaryKeyName.toString().trim().length() != 0)
			{
				primaryKeyName.append(COMMA);
			}
			primaryKeyName.append(attribute.getColumnProperties().getName());

		}
		query.append(OPENING_BRACKET).append(primaryKeyName).append(CLOSING_BRACKET);
		attrRlbkQries.add(query.toString());

		//drop all the not null constraints
		getDropNotNullQeries(prmKeyAttrColl, queries, attrRlbkQries);

		return queries;

	}

	/**
	 * It prepares queries for adding the NOT NULL constraint on the given primay key attributes
	 * @param attribute on which the not null constraint is to be applied
	 * @return query
	 * @throws DynamicExtensionsSystemException
	 */
	protected String addNotNullConstraintQuery(AttributeInterface attribute)
			throws DynamicExtensionsSystemException
	{
		StringBuffer query = new StringBuffer();
		String tableName = attribute.getEntity().getTableProperties().getName();
		String columnName = attribute.getColumnProperties().getName();
		query.append(ALTER_TABLE).append(WHITESPACE).append(tableName).append(WHITESPACE).append(
				CHANGE_KEYWORD).append(WHITESPACE).append(COLUMN_KEYWORD).append(columnName)
				.append(WHITESPACE).append(columnName).append(WHITESPACE).append(
						getDatabaseTypeAndSize(attribute)).append(WHITESPACE).append(NOT_KEYWORD)
				.append(WHITESPACE).append(NULL_KEYWORD);
		return query.toString();
	}

	/**
	 * It prepares queries for removing the NOT NULL constraint and setting it to null for given attibute
	 * @param attribute on which NULL constraint is to be applied
	 * @return query
	 * @throws DynamicExtensionsSystemException
	 */
	protected String dropNotNullConstraintQuery(AttributeInterface attribute)
			throws DynamicExtensionsSystemException
	{
		StringBuffer query = new StringBuffer();
		String columnName = attribute.getColumnProperties().getName();
		query.append(ALTER_TABLE).append(WHITESPACE).append(
				attribute.getEntity().getTableProperties().getName()).append(WHITESPACE).append(
				CHANGE_KEYWORD).append(WHITESPACE).append(COLUMN_KEYWORD).append(columnName)
				.append(WHITESPACE).append(columnName).append(WHITESPACE).append(
						getDatabaseTypeAndSize(attribute)).append(WHITESPACE).append(NULL_KEYWORD);
		return query.toString();
	}

	/**
	 * This method is used to execute the data table queries for entity in case of editing the entity.
	 * This method takes each attribute of the entity and then scans for any changes and builds the
	 * alter query for each attribute for the entity.
	 * @param catEntity category entity for which the queries are formed.
	 * @param dbaseCopy database copy of the entity.
	 * @param attrRlbkQries roll back queries list.
	 * @return List<String> list of queries.
	 * @throws DynamicExtensionsSystemException
	 * @throws DynamicExtensionsApplicationException
	 */
	public List<String> getUpdateEntityQueryList(CategoryEntity catEntity,
			CategoryEntity dbaseCopy, List<String> attrRlbkQries)
			throws DynamicExtensionsSystemException, DynamicExtensionsApplicationException
	{
		Logger.out.debug("getUpdateEntityQueryList : Entering method");
		List<String> updateQries = new ArrayList<String>();

		List<String> entInheQries = getInheritanceQueryList(catEntity, dbaseCopy, attrRlbkQries);

		// Get the query for any attribute that is modified.
		List<String> updAttrQries = getUpdateAttributeQueryList(catEntity, dbaseCopy, attrRlbkQries);

		// Get the query for any association that is modified.
		List<String> updAssoQries = getUpdateAssociationsQueryList(catEntity, dbaseCopy,
				attrRlbkQries);

		updateQries.addAll(entInheQries);
		updateQries.addAll(updAttrQries);
		updateQries.addAll(updAssoQries);

		Logger.out.debug("getUpdateEntityQueryList Exiting method");

		return updateQries;
	}

	/**
	 * This method is used to create the queries in case of editing the entity.
	 * This method creates the queries related to inheritance i.e. foreign key constraint queries
	 * @param entity on which queries are formed
	 * @param dbaseCopy saved state of the entity
	 * @param attrRlbkQries rollback query list
	 * @return Query list
	 * @throws DynamicExtensionsSystemException
	 */
	private List<String> getInheritanceQueryList(Entity entity, Entity dbaseCopy,
			List<String> attrRlbkQries) throws DynamicExtensionsSystemException
	{
		List<String> queries = new ArrayList<String>();
		if (entity.getTableProperties() != null
				&& EntityManagerUtil.isParentChanged(entity, dbaseCopy))
		{
			if (entityManagerUtil.isDataPresent(entity.getTableProperties().getName()))
			{
				throw new DynamicExtensionsSystemException("Can not change the parent of "
						+ entity.getName() + ". Data is already entered for it.");
			}
			DynamicExtensionBaseQueryBuilder queryBuilder = QueryBuilderFactory.getQueryBuilder();
			EntityInterface dbaseCopyParent = dbaseCopy.getParentEntity();
			EntityInterface parentEntity = entity.getParentEntity();
			if (dbaseCopyParent != null)
			{
				// first of all remove all column added for foreign keys & then remove constraint
				String frnCnstrRemQry = queryBuilder
						.getForeignKeyRemoveConstraintQueryForInheritance(dbaseCopy,
								dbaseCopyParent);
				queries.add(frnCnstrRemQry);
				attrRlbkQries.add(getForeignKeyConstraintQueryForInheritance(dbaseCopy,
						dbaseCopyParent));

				queries.addAll(getAddColumnQueryForForeignKeyConstraint(dbaseCopy, dbaseCopyParent,
						attrRlbkQries, false));

			}
			if (entity.getParentEntity() != null)
			{
				queries.addAll(getAddColumnQueryForForeignKeyConstraint(entity, parentEntity,
						attrRlbkQries, true));
				queries.add(getForeignKeyConstraintQueryForInheritance(entity, parentEntity));
				attrRlbkQries.add(getForeignKeyRemoveConstraintQueryForInheritance(entity, entity
						.getParentEntity()));
			}

		}

		return queries;
	}

	/**
	 * This method is used to create the queries in case of editing the CategoryEntity.
	 * This method creates the queries related to inheritance i.e. foreign key constraint queries
	 * @param catEntity on which queries are formed
	 * @param dbaseCopy saved state of the catEnity
	 * @param attrRlbkQries rollback query List
	 * @return query list
	 */
	private List<String> getInheritanceQueryList(CategoryEntity catEntity,
			CategoryEntity dbaseCopy, List<String> attrRlbkQries)
	{
		List<String> queries = new ArrayList<String>();
		if (catEntity.getTableProperties() != null
				&& EntityManagerUtil.isParentChanged(catEntity, dbaseCopy))
		{
			String frnCnstrRlbkQry = "";
			if (dbaseCopy.getParentCategoryEntity() != null
					&& dbaseCopy.getParentCategoryEntity().isCreateTable())
			{
				frnCnstrRlbkQry = getForeignKeyConstraintQueryForInheritance(dbaseCopy, dbaseCopy
						.getParentCategoryEntity());
				queries.add(getForeignKeyRemoveConstraintQueryForInheritance(dbaseCopy, dbaseCopy
						.getParentCategoryEntity()));
				attrRlbkQries.add(frnCnstrRlbkQry);
			}
			if (catEntity.getParentCategoryEntity() != null
					&& catEntity.getParentCategoryEntity().isCreateTable())
			{
				String frnCnstrntAdQry = getForeignKeyConstraintQueryForInheritance(catEntity,
						catEntity.getParentCategoryEntity());
				queries.add(frnCnstrntAdQry);
				attrRlbkQries.add(getForeignKeyRemoveConstraintQueryForInheritance(catEntity,
						catEntity.getParentCategoryEntity()));
			}
		}

		return queries;
	}

	/**
	 * This method returns association values for the entity's given record.
	 * e.g if user1 is associated with study1 and study2, this method returns the
	 * list of record identifiers of study1 and study2 as the return value for the
	 * association between user and study.
	 * @param entity
	 * @param recordId
	 * @return
	 * @throws DynamicExtensionsSystemException
	 * @throws DynamicExtensionsApplicationException
	 */
	public Map<Association, List<?>> getAssociationGetRecordQueryList(EntityInterface entity,
			Long recordId, JDBCDAO... dao) throws DynamicExtensionsSystemException,
			DynamicExtensionsApplicationException
	{
		Collection<AssociationInterface> associations = entity.getAssociationCollection();
		Iterator<AssociationInterface> assocIter = associations.iterator();
		StringBuffer mnyToOneAssQry = new StringBuffer(SELECT_KEYWORD);
		mnyToOneAssQry.append(WHITESPACE);
		List<Association> manyToOneAssocns = new ArrayList<Association>();

		Map<Association, List<?>> assocValues = new HashMap<Association, List<?>>();

		while (assocIter.hasNext())
		{
			Association association = (Association) assocIter.next();

			String tableName = DynamicExtensionsUtility.getTableName(association);
			String sourceKey;
			String targetKey;
			StringBuffer query = new StringBuffer();

			RoleInterface sourceRole = association.getSourceRole();
			RoleInterface targetRole = association.getTargetRole();
			Cardinality srcMaxCard = sourceRole.getMaximumCardinality();
			Cardinality tgtMaxCard = targetRole.getMaximumCardinality();
			if (srcMaxCard == Cardinality.MANY && tgtMaxCard == Cardinality.MANY)
			{
				sourceKey = association.getConstraintProperties()
						.getSrcEntityConstraintKeyProperties().getTgtForiegnKeyColumnProperties()
						.getName();
				targetKey = association.getConstraintProperties()
						.getTgtEntityConstraintKeyProperties().getTgtForiegnKeyColumnProperties()
						.getName();
				// For many to many, get values from the middle table.
				query.append(SELECT_KEYWORD);
				query.append(WHITESPACE);
				query.append(targetKey);
				query.append(WHITESPACE);
				query.append(FROM_KEYWORD);
				query.append(WHITESPACE);
				query.append(tableName);
				query.append(WHITESPACE);
				query.append(WHITESPACE);
				query.append(WHERE_KEYWORD);
				query.append(WHITESPACE);
				query.append(sourceKey);
				query.append(EQUAL);
				query.append(WHITESPACE);
				query.append(QUESTION_MARK);
				LinkedList<ColumnValueBean> queryDataList = new LinkedList<ColumnValueBean>();
				queryDataList.add(new ColumnValueBean(sourceKey, recordId));
				if (dao != null && dao.length > 0)
				{
					assocValues.put(association, getAssociationRecordValues(query.toString(),
							dao[0], queryDataList));
				}
				else
				{
					assocValues.put(association, getAssociationRecordValues(query.toString(),
							queryDataList));
				}
			}
			else if (srcMaxCard == Cardinality.MANY && tgtMaxCard == Cardinality.ONE)
			{
				// For all many to one associations of a single entity,
				// create a single query to get values for the target records.
				sourceKey = association.getConstraintProperties()
						.getSrcEntityConstraintKeyProperties().getTgtForiegnKeyColumnProperties()
						.getName();
				if (!manyToOneAssocns.isEmpty())
				{
					mnyToOneAssQry.append(COMMA);
				}
				mnyToOneAssQry.append(WHITESPACE);
				mnyToOneAssQry.append(sourceKey);
				mnyToOneAssQry.append(WHITESPACE);
				manyToOneAssocns.add(association);
			}
			else
			{
				// For one to many or one to one association, get target record values
				// from the target entity table.
				targetKey = association.getConstraintProperties()
						.getTgtEntityConstraintKeyProperties().getTgtForiegnKeyColumnProperties()
						.getName();
				query.append(SELECT_KEYWORD);
				query.append(WHITESPACE);
				query.append(IDENTIFIER);
				query.append(WHITESPACE);
				query.append(FROM_KEYWORD);
				query.append(WHITESPACE);
				query.append(tableName);
				query.append(WHITESPACE);
				query.append(WHITESPACE);
				query.append(WHERE_KEYWORD);
				query.append(WHITESPACE);
				query.append(targetKey);
				query.append(EQUAL);
				query.append(WHITESPACE);
				query.append(QUESTION_MARK);
				LinkedList<ColumnValueBean> queryDataList = new LinkedList<ColumnValueBean>();
				queryDataList.add(new ColumnValueBean(targetKey, recordId));
				List<Long> recordIds = null;

				if (dao != null && dao.length > 0)
				{
					recordIds = getAssociationRecordValues(query.toString(), dao[0], queryDataList);
				}
				else
				{
					recordIds = getAssociationRecordValues(query.toString(), queryDataList);
				}

				if (association.getSourceRole().getAssociationsType().equals(
						AssociationType.CONTAINTMENT)
						|| (association.getSourceRole().getAssociationsType().equals(
								AssociationType.ASSOCIATION) && association.getIsCollection()))
				{
					List<Map<AbstractAttributeInterface, Object>> cntnmntRecords = new ArrayList<Map<AbstractAttributeInterface, Object>>();

					for (Long cntnmntRecId : recordIds)
					{
						Map<AbstractAttributeInterface, Object> recordMap = EntityManager
								.getInstance().getRecordForSingleEntity(
										association.getTargetEntity(), cntnmntRecId);
						cntnmntRecords.add(recordMap);
					}
					assocValues.put(association, cntnmntRecords);
				}
				else
				{
					assocValues.put(association, recordIds);
				}
			}
		}

		mnyToOneAssQry.append(WHITESPACE);
		mnyToOneAssQry.append(FROM_KEYWORD);
		mnyToOneAssQry.append(WHITESPACE);
		mnyToOneAssQry.append(entity.getTableProperties().getName());
		mnyToOneAssQry.append(WHITESPACE);
		mnyToOneAssQry.append(WHITESPACE );
		mnyToOneAssQry.append(WHERE_KEYWORD );
		mnyToOneAssQry.append(WHITESPACE );
		mnyToOneAssQry.append(IDENTIFIER );
		mnyToOneAssQry.append(EQUAL);
		mnyToOneAssQry.append(QUESTION_MARK);

		LinkedList<ColumnValueBean> queryDataList = new LinkedList<ColumnValueBean>();
		queryDataList.add(new ColumnValueBean(IDENTIFIER, recordId));
		int noOfMany2OneAsso = manyToOneAssocns.size();
		if (noOfMany2OneAsso != 0)
		{
			ResultSet resultSet = null;
			JDBCDAO jdbcDao = null;
			try
			{
				if (dao != null && dao.length > 0)
				{
					jdbcDao = dao[0];
				}
				else
				{
					jdbcDao = DynamicExtensionsUtility.getJDBCDAO();
				}

				resultSet = jdbcDao.getResultSet(mnyToOneAssQry.toString(), queryDataList, null);

				resultSet.next();
				for (int i = 0; i < noOfMany2OneAsso; i++)
				{
					Long tgtRecId = resultSet.getLong(i + 1);
					List<Long> values = new ArrayList<Long>();
					values.add(tgtRecId);
					assocValues.put(manyToOneAssocns.get(i), values);
				}
			}
			catch (SQLException e)
			{
				throw new DynamicExtensionsSystemException(ErrorConstants.EXCEPTION_IN_EXEC_QUERY,
						e);
			}
			catch (DAOException e)
			{
				throw new DynamicExtensionsSystemException(ErrorConstants.EXCEPTION_IN_EXEC_QUERY,
						e);
			}
			finally
			{
				try
				{
					jdbcDao.closeStatement(resultSet);

					if (dao != null && dao.length > 0)
					{
						Logger.out.info("Dao passed by calling method....");
					}
					else
					{
						DynamicExtensionsUtility.closeDAO(jdbcDao);
					}
				}
				catch (DAOException e)
				{
					throw new DynamicExtensionsApplicationException(e.getMessage(), e);
				}
			}
		}

		return assocValues;
	}

	/**
	 * This method returns the queries to remove the the association.
	 * @param association
	 * @param recId
	 * @return
	 */
	public String getAssociationRemoveDataQuery(Association association, Long recId)
	{
		String tableName = DynamicExtensionsUtility.getTableName(association);
		String sourceKey = association.getConstraintProperties()
				.getSrcEntityConstraintKeyProperties().getTgtForiegnKeyColumnProperties().getName();
		String targetKey = association.getConstraintProperties()
				.getTgtEntityConstraintKeyProperties().getTgtForiegnKeyColumnProperties().getName();

		StringBuffer query = new StringBuffer();

		RoleInterface sourceRole = association.getSourceRole();
		RoleInterface targetRole = association.getTargetRole();

		Cardinality srcMaxCard = sourceRole.getMaximumCardinality();
		Cardinality tgtMaxCard = targetRole.getMaximumCardinality();
		if (srcMaxCard == Cardinality.MANY && tgtMaxCard == Cardinality.MANY)
		{
			// For many to many, delete all the records referred to by this recordId.
			query.append(DELETE_KEYWORD);
			query.append(WHITESPACE);
			query.append(tableName);
			query.append(WHITESPACE);
			query.append(WHERE_KEYWORD);
			query.append(WHITESPACE);
			query.append(sourceKey);
			query.append(WHITESPACE);
			query.append(EQUAL);
			query.append(recId.toString());
		}
		else if (srcMaxCard == Cardinality.MANY && tgtMaxCard == Cardinality.ONE)
		{
			query.append(UPDATE_KEYWORD);
			query.append(WHITESPACE);
			query.append(tableName);
			query.append(WHITESPACE);
			query.append(SET_KEYWORD);
			query.append(WHITESPACE);
			query.append(sourceKey);
			query.append(EQUAL);
			query.append(WHITESPACE);
			query.append("null");
			query.append(WHITESPACE);
			query.append(WHERE_KEYWORD);
			query.append(WHITESPACE);
			query.append(sourceKey);
			query.append(EQUAL);
			query.append(recId);
		}
		else
		{
			// For one to many and one to one, update target entity's records
			// (set value in target column key = null) that are referring to
			// this record by setting it to null.
			query.append(UPDATE_KEYWORD);
			query.append(WHITESPACE);
			query.append(tableName);
			query.append(WHITESPACE);
			query.append(SET_KEYWORD);
			query.append(WHITESPACE);
			query.append(targetKey);
			query.append(EQUAL);
			query.append(WHITESPACE);
			query.append("null");
			query.append(WHITESPACE);
			query.append(WHERE_KEYWORD);
			query.append(WHITESPACE );
			query.append(targetKey );
			query.append(EQUAL );
			query.append(recId);
		}

		return query.toString();
	}

	/**
	 * This method returns the main data table CREATE query that is associated with the entity.
	 * @param entity
	 * @param revQueries
	 * @return
	 * @throws DynamicExtensionsSystemException
	 */
	protected List<String> getCreateMainTableQuery(Entity entity, List<String> revQueries)
			throws DynamicExtensionsSystemException
	{
		List<String> queries = new ArrayList<String>();
		if (entity.getTableProperties() != null)
		{
			String actStatus = Constants.ACTIVITY_STATUS_COLUMN + WHITESPACE
					+ getDataTypeForStatus();

			String tableName = entity.getTableProperties().getName();
			StringBuffer query = new StringBuffer(CREATE_TABLE + " " + tableName + " "
					+ OPENING_BRACKET + " " + actStatus);
			StringBuffer primaryKeyName = new StringBuffer();
			Collection<AttributeInterface> attributes = entity.getAttributeCollection();

			if (attributes != null && !attributes.isEmpty())
			{
				Iterator<AttributeInterface> attrIter = attributes.iterator();
				while (attrIter.hasNext())
				{
					Attribute attribute = (Attribute) attrIter.next();

					if (isAttributeColumnToBeExcluded(attribute))
					{
						// Column is not created if it is multi-select, file type etc.
						continue;
					}
					if (attribute.getAttributeTypeInformation() instanceof FileAttributeTypeInformation)
					{
						query = query.append(COMMA
								+ extraColumnQueryStringForFileAttribute(attribute));
					}

					String type = "";

					// Get column info for attribute.
					String attrQueryPart = getQueryPartForAttribute(attribute, type, true);
					query = query.append(COMMA);
					query = query.append(attrQueryPart);

					if (!attribute.getIsNullable() && attribute.getIsPrimaryKey())
					{
						if (primaryKeyName.toString().trim().length() != 0)
						{
							primaryKeyName.append(COMMA);
						}
						primaryKeyName.append(attribute.getColumnProperties().getName());
					}
				}
			}
			if (primaryKeyName.length() != 0)
			{
				query = query.append(COMMA).append(PRIMARY_KEY).append(OPENING_BRACKET).append(
						primaryKeyName).append(CLOSING_BRACKET);
			}
			query = query.append(CLOSING_BRACKET);

			// Add create query.
			queries.add(query.toString());
			String reverseQuery = getReverseQueryForAbstractEntityTable(entity.getTableProperties()
					.getName());
			revQueries.add(reverseQuery);
		}

		return queries;
	}

	/**
	 * This method builds the list of all the queries that need to be executed in order to
	 * create the data table for the entity and its associations.
	 * @param catEntity
	 * @param revQueries
	 * @param hibernateDAO
	 * @return
	 * @throws DynamicExtensionsSystemException
	 * @throws DynamicExtensionsApplicationException
	 */
	public List<String> getCreateCategoryQueryList(CategoryEntityInterface catEntity,
			List<String> revQueries) throws DynamicExtensionsSystemException,
			DynamicExtensionsApplicationException
	{
		return getCreateCategoryEntityTableQuery(catEntity, revQueries);
	}

	/**
	 * This method is used to create a table for each category entity.
	 * @param catEntity
	 * @param revQueries
	 * @return
	 * @throws DynamicExtensionsSystemException
	 */
	protected List<String> getCreateCategoryEntityTableQuery(CategoryEntityInterface catEntity,
			List<String> revQueries) throws DynamicExtensionsSystemException
	{
		List<String> queries = new ArrayList<String>();

		if (catEntity.getTableProperties() != null)
		{
			String actStatus = Constants.ACTIVITY_STATUS_COLUMN + WHITESPACE
					+ getDataTypeForStatus();

			String tableName = catEntity.getTableProperties().getName();
			StringBuffer query = new StringBuffer(CREATE_TABLE + " " + tableName + " "
					+ OPENING_BRACKET + " " + actStatus + COMMA);
			query.append(IDENTIFIER).append(WHITESPACE).append(getDataTypeForIdentifier()).append(
					WHITESPACE).append(NOT_KEYWORD).append(WHITESPACE).append(NULL_KEYWORD).append(
					COMMA);
			query = query.append("record_Id" + WHITESPACE + getDataTypeForIdentifier() + WHITESPACE
					+ "NOT NULL" + COMMA);
			query = query.append(PRIMARY_KEY_CONSTRAINT + ")"); //identifier set as primary key
			queries.add(query.toString());

			String reverseQuery = getReverseQueryForAbstractEntityTable(catEntity
					.getTableProperties().getName());
			revQueries.add(reverseQuery);
		}

		return queries;
	}

	/**
	 * getForeignKeyConstraintQuery for the given entity .
	 * @param entity on which the query is to be applied
	 * @param revQueries rollback query list
	 * @return list of queries
	 * @throws DynamicExtensionsSystemException
	 */
	private List<String> getForeignKeyConstraintQuery(Entity entity, List<String> revQueries)
			throws DynamicExtensionsSystemException
	{
		List<String> queries = new ArrayList<String>();
		EntityInterface parentEntity = entity.getParentEntity();

		// Add foreign key query for inheritance.
		if (parentEntity != null)
		{
			queries.addAll(getAddColumnQueryForForeignKeyConstraint(entity, parentEntity,
					revQueries, true));

			queries.add(getForeignKeyConstraintQueryForInheritance(entity, parentEntity));

			revQueries.add(getForeignKeyRemoveConstraintQueryForInheritance(entity, parentEntity));
		}

		return queries;
	}

	/**
	 * @param catEntity
	 * @param revQueries
	 * @return
	 */
	private List<String> getForeignKeyConstraintQuery(CategoryEntityInterface catEntity,
			List<String> revQueries)
	{
		List<String> queries = new ArrayList<String>();
		CategoryEntity parentCatEntity = (CategoryEntity) catEntity.getParentCategoryEntity();

		// Add foreign key query for inheritance.
		if (parentCatEntity != null && parentCatEntity.isCreateTable())
		{
			catEntity.getAttributeByName(IDENTIFIER);
			String frnKeyCnstrQry = getForeignKeyConstraintQueryForInheritance(catEntity,
					parentCatEntity);
			queries.add(frnKeyCnstrQry);

			revQueries.add(getForeignKeyRemoveConstraintQueryForInheritance(catEntity,
					parentCatEntity));
		}

		return queries;
	}

	/**
	 * @param entity
	 * @param parentEntity
	 * @return
	 */
	protected String getForeignKeyConstraintQueryForInheritance(AbstractEntityInterface entity,
			AbstractEntityInterface parentEntity)
	{
		StringBuffer frnKeyCnstrQry = new StringBuffer();
		String frnCnstrName = entity.getConstraintProperties().getConstraintName() + UNDERSCORE
				+ parentEntity.getConstraintProperties().getConstraintName();

		frnKeyCnstrQry.append(ALTER_TABLE).append(WHITESPACE).append(
				entity.getTableProperties().getName()).append(WHITESPACE).append(ADD_KEYWORD)
				.append(WHITESPACE).append(CONSTRAINT_KEYWORD).append(WHITESPACE).append(
						frnCnstrName).append(FOREIGN_KEY_KEYWORD).append(OPENING_BRACKET).append(
						IDENTIFIER).append(CLOSING_BRACKET).append(WHITESPACE).append(
						REFERENCES_KEYWORD).append(WHITESPACE).append(
						parentEntity.getTableProperties().getName()).append(OPENING_BRACKET)
				.append(IDENTIFIER).append(CLOSING_BRACKET);

		return frnKeyCnstrQry.toString();
	}

	/**
	 * It actually prepares the query for inheritance on the entity whose parent is parent entity
	 * @param entity child entity
	 * @param parentEntity parent of the entity
	 * @return query
	 * @throws DynamicExtensionsSystemException
	 */
	protected String getForeignKeyConstraintQueryForInheritance(EntityInterface entity,
			EntityInterface parentEntity) throws DynamicExtensionsSystemException
	{
		StringBuffer frnKeyCnstrQry = new StringBuffer();
		String tableName = entity.getTableProperties().getName();
		String frnCnstrName = entity.getConstraintProperties().getConstraintName() + UNDERSCORE
				+ parentEntity.getConstraintProperties().getConstraintName();

		frnKeyCnstrQry.append(ALTER_TABLE).append(WHITESPACE).append(tableName).append(WHITESPACE);

		Collection<ConstraintKeyPropertiesInterface> frnCnstKeyPrpties = entity
				.getConstraintProperties().getSrcEntityConstraintKeyPropertiesCollection();

		String frgnCnstrName = "";
		String prmKeyName = "";
		frnCnstKeyPrpties = sortOnPrimaryAttribute(frnCnstKeyPrpties);
		String columnName = "";
		// add the constraint in child for each primary key in parent except IDENTIFIER
		for (ConstraintKeyPropertiesInterface cnstrKeyProp : frnCnstKeyPrpties)
		{
			columnName = cnstrKeyProp.getTgtForiegnKeyColumnProperties().getName();

			if (!"".equals(frgnCnstrName.trim()))
			{
				frgnCnstrName = frgnCnstrName.concat(COMMA);
				prmKeyName = prmKeyName.concat(COMMA);
			}

			frgnCnstrName = frgnCnstrName.concat(columnName);
			prmKeyName = prmKeyName.concat(cnstrKeyProp.getSrcPrimaryKeyAttribute()
					.getColumnProperties().getName());

		}
		if ("".equals(frgnCnstrName) || "".equals(prmKeyName))
		{
			throw new DynamicExtensionsSystemException("Generate Constraint properties for entity "
					+ entity.getName());
		}
		frnKeyCnstrQry.append(ADD_KEYWORD).append(WHITESPACE).append(CONSTRAINT_KEYWORD).append(
				WHITESPACE).append(frnCnstrName).append(FOREIGN_KEY_KEYWORD)
				.append(OPENING_BRACKET).append(frgnCnstrName).append(CLOSING_BRACKET).append(
						WHITESPACE).append(REFERENCES_KEYWORD).append(WHITESPACE).append(
						parentEntity.getTableProperties().getName()).append(OPENING_BRACKET)
				.append(prmKeyName).append(CLOSING_BRACKET);

		return frnKeyCnstrQry.toString();
	}

	protected List<String> getAddColumnQueryForForeignKeyConstraint(EntityInterface entity,
			EntityInterface parentEntity, List<String> revQueries, boolean isAddColumn)
			throws DynamicExtensionsSystemException
	{
		Collection<ConstraintKeyPropertiesInterface> frnCnstKeyPrpties = entity
				.getConstraintProperties().getSrcEntityConstraintKeyPropertiesCollection();
		String tableName = entity.getTableProperties().getName();
		String columnName = "";
		List<String> queries = new ArrayList<String>();
		String query = "";
		if (frnCnstKeyPrpties == null)
		{
			throw new DynamicExtensionsSystemException("Generate Constraint properties for enity"
					+ entity.getName());
		}
		for (ConstraintKeyPropertiesInterface cnstrKeyProp : frnCnstKeyPrpties)
		{
			columnName = cnstrKeyProp.getTgtForiegnKeyColumnProperties().getName();
			// dont add the column if the both child and parent contains id attribute
			if (!columnName.equals(IDENTIFIER))
			{
				query = getAddAttributeQuery(tableName, columnName,
						getDatabaseTypeAndSize(cnstrKeyProp.getSrcPrimaryKeyAttribute()),
						revQueries, isAddColumn);
				queries.add(query);
			}
		}
		return queries;
	}

	/**
	 * This method is used to sort the frgnCnstrKeyPropColl in the natural order of the srcPrimaryKeyAttributes column name
	 * @param frnCnstrKeyProps which is to be sorted
	 * @return sorted collection of frgnCnstrKeyPropColl
	 */
	private Collection<ConstraintKeyPropertiesInterface> sortOnPrimaryAttribute(
			Collection<ConstraintKeyPropertiesInterface> frnCnstrKeyProps)
	{
		List<ConstraintKeyPropertiesInterface> frgnKeyCnstrList = new ArrayList<ConstraintKeyPropertiesInterface>();
		for (ConstraintKeyPropertiesInterface cnstrKeyProp : frnCnstrKeyProps)
		{
			frgnKeyCnstrList.add(cnstrKeyProp);
		}
		ConstraintKeyPropertiesComparator comparator = new ConstraintKeyPropertiesComparator();
		Collections.sort(frgnKeyCnstrList, comparator);
		return frgnKeyCnstrList;
	}

	/**
	 * This method returns the queries to remove foreign key constraint in the
	 * given child entity that refers to identifier column of the parent.
	 * @param entity whose foreign key constraint is to be removed
	 * @param parentEntity on which it refers
	 * @return query
	 */
	protected String getForeignKeyRemoveConstraintQueryForInheritance(
			AbstractEntityInterface entity, AbstractEntityInterface parentEntity)
	{
		StringBuffer frnKeyCnstrQry = new StringBuffer();

		String tableName = entity.getTableProperties().getName();
		String frnCnstrName = entity.getConstraintProperties().getConstraintName() + UNDERSCORE
				+ parentEntity.getConstraintProperties().getConstraintName();

		frnKeyCnstrQry.append(ALTER_TABLE).append(WHITESPACE).append(tableName).append(WHITESPACE)
				.append(DROP_KEYWORD).append(WHITESPACE).append(CONSTRAINT_KEYWORD).append(
						WHITESPACE).append(frnCnstrName);

		return frnKeyCnstrQry.toString();
	}

	/**
	 * This method returns the database type for identifier.
	 * @return String database type for the identifier.
	 * @throws DynamicExtensionsSystemException exception is thrown if factory is not instantiated properly.
	 */
	protected String getDataTypeForIdentifier() throws DynamicExtensionsSystemException
	{
		DataTypeFactory dataTypeFactory = DataTypeFactory.getInstance();
		return dataTypeFactory.getDatabaseDataType("Integer");
	}

	/**
	 * This method returns the database type for identifier.
	 * @return String database type for the identifier.
	 * @throws DynamicExtensionsSystemException exception is thrown if factory is not instantiated properly.
	 */
	protected String getDataTypeForStatus() throws DynamicExtensionsSystemException
	{
		DataTypeFactory dataTypeFactory = DataTypeFactory.getInstance();
		return dataTypeFactory.getDatabaseDataType("String");
	}

	/**
	 * This method returns true if a column in not to be created for the attribute.
	 * @param attribute
	 * @return false
	 */
	protected boolean isAttributeColumnToBeExcluded(AttributeInterface attribute)
	{
		// This method is to be deleted.
		return false;
	}

	/**
	 * This method builds the query part for the primitive attribute
	 * @param attribute
	 * @param type
	 * @param procCnstrn
	 * @return
	 * @throws DynamicExtensionsSystemException
	 */
	protected String getQueryPartForAttribute(Attribute attribute, String type, boolean procCnstrn)
			throws DynamicExtensionsSystemException
	{
		String attributeQry = null;
		if (attribute != null)
		{
			String columnName = attribute.getColumnProperties().getName();
			String nullConstraint = "";
			if (procCnstrn)
			{
				nullConstraint = "NULL";

				if (!attribute.getIsNullable())
				{
					nullConstraint = "NOT NULL";
				}
			}

			attributeQry = columnName + WHITESPACE + type + WHITESPACE
					+ getDatabaseTypeAndSize(attribute) //+ WHITESPACE + defaultConstraint
					+ WHITESPACE + nullConstraint;
		}

		return attributeQry;
	}

	/**
	 * This method builds the query part for the primitive attribute
	 * @param attribute
	 * @param type
	 * @param procCnstrn
	 * @return
	 * @throws DynamicExtensionsSystemException
	 */
	protected String getQueryPartForCategoryAttribute(CategoryAttributeInterface attribute,
			String type, boolean procCnstrn) throws DynamicExtensionsSystemException
	{
		String attributeQry = null;
		if (attribute != null)
		{
			String columnName = attribute.getColumnProperties().getName();
			String nullConstraint = "";
			if (procCnstrn)
			{
				nullConstraint = "NULL";
			}

			attributeQry = columnName + WHITESPACE + type + WHITESPACE + getDataTypeForIdentifier()
					+ WHITESPACE + nullConstraint;
		}

		return attributeQry;
	}

	/**
	 * This method returns the database type and size of the attribute
	 * passed to it, which becomes the part of the query for that attribute.
	 * @param attribute
	 * @return
	 * @throws DynamicExtensionsSystemException
	 */
	protected String getDatabaseTypeAndSize(AttributeInterface attribute)
			throws DynamicExtensionsSystemException
	{
		String dataType = null;
		try
		{
			DataTypeFactory dataTypeFactory = DataTypeFactory.getInstance();
			AttributeTypeInformationInterface attrTypeInfo = attribute
					.getAttributeTypeInformation();
			if (attrTypeInfo instanceof StringAttributeTypeInformation)
			{
				dataType = dataTypeFactory.getDatabaseDataType("String");
			}
			else if (attrTypeInfo instanceof IntegerAttributeTypeInformation)
			{
				dataType = dataTypeFactory.getDatabaseDataType("Integer");
			}
			else if (attrTypeInfo instanceof DateAttributeTypeInformation)
			{
				DateAttributeTypeInformation dateAttrTypInfo = (DateAttributeTypeInformation) attrTypeInfo;

				String format = DynamicExtensionsUtility.getDateFormat(dateAttrTypInfo.getFormat());

				if (format != null && format.equalsIgnoreCase(ProcessorConstants.DATE_TIME_FORMAT))
				{
					dataType = dataTypeFactory.getDatabaseDataType("DateTime");
				}
				else
				{
					dataType = dataTypeFactory.getDatabaseDataType("Date");
				}
			}
			else if (attrTypeInfo instanceof FloatAttributeTypeInformation)
			{
				dataType = dataTypeFactory.getDatabaseDataType("Float");
			}
			else if (attrTypeInfo instanceof BooleanAttributeTypeInformation)
			{
				dataType = dataTypeFactory.getDatabaseDataType("Boolean");
			}
			else if (attrTypeInfo instanceof DoubleAttributeTypeInformation)
			{
				dataType = dataTypeFactory.getDatabaseDataType("Double");
			}
			else if (attrTypeInfo instanceof LongAttributeTypeInformation)
			{
				dataType = dataTypeFactory.getDatabaseDataType("Long");
			}
			else if (attrTypeInfo instanceof ShortAttributeTypeInformation)
			{
				dataType = dataTypeFactory.getDatabaseDataType("Short");
			}
			else if (attrTypeInfo instanceof FileAttributeTypeInformation)
			{
				dataType = dataTypeFactory.getDatabaseDataType("File");
			}
			else if (attrTypeInfo instanceof ObjectAttributeTypeInformation)
			{
				dataType = dataTypeFactory.getDatabaseDataType("Object");
			}
		}
		catch (DataTypeFactoryInitializationException e)
		{
			throw new DynamicExtensionsSystemException("Could Not get data type attribute", e);
		}

		return dataType;
	}

	/**
	 * This method gives the opposite query of "CREATE TABLE" query for the data table.
	 * @param tableName
	 * @return
	 */
	protected String getReverseQueryForAbstractEntityTable(String tableName)
	{
		String query = null;
		if (tableName != null && tableName.length() > 0)
		{
			query = "Drop table" + " " + tableName;
		}

		return query;
	}

	/**
	 * This method returns all the CREATE table entries for associations present in the entity.
	 * @param entity whose associations are to be processed
	 * @param revQueries rollback query list
	 * @return list of queries
	 * @throws DynamicExtensionsApplicationException
	 * @throws DynamicExtensionsSystemException
	 */
	protected List<String> getCreateAssociationsQueryList(Entity entity, List<String> revQueries)
			throws DynamicExtensionsApplicationException, DynamicExtensionsSystemException
	{
		Collection<AssociationInterface> associations = entity.getAssociationCollection();

		List<String> assoQueries = new ArrayList<String>();
		if (associations != null && !associations.isEmpty())
		{
			Iterator<AssociationInterface> assoIter = associations.iterator();
			while (assoIter.hasNext())
			{
				AssociationInterface association = assoIter.next();
				if (((Association) association).getIsSystemGenerated())
				{
					// No need to process system generated associations.
					continue;
				}

				boolean isAddAssoQry = true;
				assoQueries
						.addAll(getQueryPartForAssociation(association, revQueries, isAddAssoQry));
			}
		}

		return assoQueries;
	}

	/**
	 * This method returns all the CREATE table entries for associations present in the entity.
	 * @param entity whose all category associations are to be processed
	 * @param revQueries rollback query list
	 * @return list of queries
	 * @throws DynamicExtensionsApplicationException
	 * @throws DynamicExtensionsSystemException
	 */
	protected List<String> getCreateAssociationsQueryList(CategoryEntityInterface entity,
			List<String> revQueries) throws DynamicExtensionsApplicationException,
			DynamicExtensionsSystemException
	{
		Collection<CategoryAssociationInterface> associations = entity
				.getCategoryAssociationCollection();

		List<String> assoQueries = new ArrayList<String>();
		if (associations != null && !associations.isEmpty())
		{
			for (CategoryAssociationInterface catAsso : associations)
			{
				boolean isAddAssoQry = true;
				String assoQuery = getQueryPartForAssociation(catAsso, revQueries, isAddAssoQry);
				assoQueries.add(assoQuery);
			}
		}

		return assoQueries;
	}

	/**
	 * This method builds the query part for the association.
	 * @param association whose query is to be prepared
	 * @param revQueries rollback query list
	 * @param isAddAssoQuery boolean indicating weather add or remove association query
	 * @return
	 * @throws DynamicExtensionsSystemException
	 */
	public List<String> getQueryPartForAssociation(AssociationInterface association,
			List<String> revQueries, boolean isAddAssoQuery)
			throws DynamicExtensionsSystemException
	{
		Logger.out.debug("Entering getQueryPartForAssociation method");
		List<String> queries = new ArrayList<String>();
		if (!association.getIsSystemGenerated())
		{
			EntityInterface srcEntity = association.getEntity();
			EntityInterface tgtEntity = association.getTargetEntity();
			RoleInterface sourceRole = association.getSourceRole();
			RoleInterface targetRole = association.getTargetRole();

			Cardinality srcMaxCard = sourceRole.getMaximumCardinality();
			Cardinality tgtMaxCard = targetRole.getMaximumCardinality();

			ConstraintPropertiesInterface cnstrnPrprties = association.getConstraintProperties();
			String tableName = "";

			Collection<ConstraintKeyPropertiesInterface> srcCnstrKeyProps = cnstrnPrprties
					.getSrcEntityConstraintKeyPropertiesCollection();
			Collection<ConstraintKeyPropertiesInterface> tgtCnstrKeyProps = cnstrnPrprties
					.getTgtEntityConstraintKeyPropertiesCollection();
			String dataType = getDataTypeForIdentifier();

			StringBuffer query = new StringBuffer();
			if (srcMaxCard == Cardinality.MANY && tgtMaxCard == Cardinality.MANY)
			{
				// For many to many, a middle table is created.
				tableName = cnstrnPrprties.getName();
				query.append(CREATE_TABLE);
				query.append( WHITESPACE );
				query.append(tableName );
				query.append(WHITESPACE );
				query.append(OPENING_BRACKET);
				query.append(WHITESPACE );
				query.append(IDENTIFIER );
				query.append(WHITESPACE );
				query.append(dataType );
				query.append(WHITESPACE);
				query.append( NOT_KEYWORD );
				query.append(WHITESPACE );
				query.append(NULL_KEYWORD );
				query.append(COMMA);
				for (ConstraintKeyPropertiesInterface cnstrKeyProp : srcCnstrKeyProps)
				{
					query.append(cnstrKeyProp.getTgtForiegnKeyColumnProperties().getName());
					query.append(WHITESPACE);
					query.append(getDatabaseTypeAndSize(cnstrKeyProp.getSrcPrimaryKeyAttribute()));
					query.append(COMMA);
				}
				for (ConstraintKeyPropertiesInterface cnstrKeyProp : tgtCnstrKeyProps)
				{
					query.append(cnstrKeyProp.getTgtForiegnKeyColumnProperties().getName());
					query.append(WHITESPACE);
					query.append(getDatabaseTypeAndSize(cnstrKeyProp.getSrcPrimaryKeyAttribute()));
					query.append(COMMA);
				}
				query.append(PRIMARY_KEY_CONSTRAINT);
				query.append(CLOSING_BRACKET);
				String rollbackQuery = DROP_KEYWORD + WHITESPACE + TABLE_KEYWORD + WHITESPACE
						+ tableName;

				if (isAddAssoQuery)
				{
					revQueries.add(rollbackQuery);
				}
				else
				{
					revQueries.add(query.toString());
					query = new StringBuffer(rollbackQuery);

				}
				queries.add(query.toString());
			}
			else if (srcMaxCard == Cardinality.MANY && tgtMaxCard == Cardinality.ONE)
			{
				// For many to one, a column is added into source entity table.
				tableName = srcEntity.getTableProperties().getName();
				for (ConstraintKeyPropertiesInterface cnstrKeyProp : srcCnstrKeyProps)
				{
					queries.add(getAddAttributeQuery(tableName, cnstrKeyProp
							.getTgtForiegnKeyColumnProperties().getName(),
							getDatabaseTypeAndSize(cnstrKeyProp.getSrcPrimaryKeyAttribute()),
							revQueries, isAddAssoQuery));
				}

			}
			else
			{
				// For one to one and one to many, a column is added into target entity table.
				tableName = tgtEntity.getTableProperties().getName();
				for (ConstraintKeyPropertiesInterface cnstrKeyProp : tgtCnstrKeyProps)
				{
					queries.add(getAddAttributeQuery(tableName, cnstrKeyProp
							.getTgtForiegnKeyColumnProperties().getName(),
							getDatabaseTypeAndSize(cnstrKeyProp.getSrcPrimaryKeyAttribute()),
							revQueries, isAddAssoQuery));
				}
			}
			Logger.out.debug("Exiting getQueryPartForAssociation method");
		}
		return queries;
	}

	/**
	 * This method builds the query part for the association.
	 * @param catAsso
	 * @param revQueries
	 * @param isAddAssoQuery
	 * @return
	 * @throws DynamicExtensionsSystemException
	 */
	public String getQueryPartForAssociation(CategoryAssociationInterface catAsso,
			List<String> revQueries, boolean isAddAssoQuery)
			throws DynamicExtensionsSystemException
	{
		Logger.out.debug("Entering getQueryPartForAssociation method");

		StringBuffer query = new StringBuffer();

		CategoryEntityInterface tgtCatEntity = catAsso.getTargetCategoryEntity();

		// For one to one and one to many, a column is added into target entity table.
		String tableName = tgtCatEntity.getTableProperties().getName();
		String columnName = catAsso.getConstraintProperties().getTgtEntityConstraintKeyProperties()
				.getTgtForiegnKeyColumnProperties().getName();
		query.append(getAddAttributeQuery(tableName, columnName, getDataTypeForIdentifier(),
				revQueries, isAddAssoQuery));

		Logger.out.debug("Exiting getQueryPartForAssociation  method");

		return query.toString();
	}

	/**
	 * @param tableName
	 * @param columnName
	 * @param dataType
	 * @param revQueries
	 * @param isAddAssoQry
	 * @return
	 */
	protected String getAddAttributeQuery(String tableName, String columnName, String dataType,
			List<String> revQueries, boolean isAddAssoQry)
	{
		String queryToReturn;
		StringBuffer query = new StringBuffer(ALTER_TABLE);
		query.append(WHITESPACE);
		query.append(tableName);
		query.append(WHITESPACE);
		query.append(ADD_KEYWORD);
		query.append(WHITESPACE);
		query.append(columnName);
		query.append(WHITESPACE);
		query.append(dataType);
		query.append(WHITESPACE);

		StringBuffer rollbackQuery = new StringBuffer(ALTER_TABLE);
		rollbackQuery.append(WHITESPACE);
		rollbackQuery.append(tableName);
		rollbackQuery.append(WHITESPACE);
		rollbackQuery.append(DROP_KEYWORD);
		rollbackQuery.append(WHITESPACE);
		rollbackQuery.append(COLUMN_KEYWORD);
		rollbackQuery.append(WHITESPACE);
		rollbackQuery.append(columnName);

		if (isAddAssoQry)
		{
			revQueries.add(rollbackQuery.toString());
			queryToReturn = query.toString();
		}
		else
		{
			revQueries.add(query.toString());
			queryToReturn = rollbackQuery.toString();
		}
		return queryToReturn;
	}

	/**
	 * This method returns queries for any attribute that is modified. except removed attributes
	 * @param entity
	 * @param dbaseCopy
	 * @param attrRlbkQries
	 * @return
	 * @throws DynamicExtensionsSystemException
	 * @throws DynamicExtensionsApplicationException
	 */
	protected List<String> getUpdateAttributeQueryList(Entity entity, Entity dbaseCopy,
			List<String> attrRlbkQries) throws DynamicExtensionsSystemException,
			DynamicExtensionsApplicationException
	{
		Logger.out.debug("Entering getUpdateAttributeQueryList method");

		Collection<AttributeInterface> attributes = entity.getAttributeCollection();

		List<String> attrQueries = new ArrayList<String>();
		if (attributes != null && !attributes.isEmpty())
		{
			Iterator<AttributeInterface> attrIter = attributes.iterator();

			while (attrIter.hasNext())
			{
				Attribute attribute = (Attribute) attrIter.next();
				Attribute savedAttribute = (Attribute) dbaseCopy.getAttributeByIdentifier(attribute
						.getId());

				if (isAttributeColumnToBeAdded(attribute, savedAttribute))
				{
					// Either the attribute is newly added or previously excluded(file type/multi select)
					// attribute.

					attrQueries.add(processAddAttribute(attribute, attrRlbkQries));
				}
				else
				{
					// Check for other modification in the attributes such a unique constraint change.
					List<String> modAttrQueries = processModifyAttribute(attribute, savedAttribute,
							attrRlbkQries);
					attrQueries.addAll(modAttrQueries);
				}
			}
		}

		Logger.out.debug("Exiting getUpdateAttributeQueryList method");

		return attrQueries;
	}

	/**
	 * This method returns queries for any attribute that is modified.
	 * @param catEntity
	 * @param dbaseCopy
	 * @param attrRlbkQries
	 * @return
	 * @throws DynamicExtensionsSystemException
	 * @throws DynamicExtensionsApplicationException
	 */
	protected List<String> getUpdateAttributeQueryList(CategoryEntity catEntity,
			CategoryEntity dbaseCopy, List<String> attrRlbkQries)
			throws DynamicExtensionsSystemException, DynamicExtensionsApplicationException
	{
		Logger.out.debug("Entering getUpdateAttributeQueryList method");

		Collection<CategoryAttributeInterface> attributes = catEntity
				.getCategoryAttributeCollection();

		List<String> attrQueries = new ArrayList<String>();
		if (attributes != null)
		{
			for (CategoryAttributeInterface catAttribute : attributes)
			{
				CategoryAttribute attribute = (CategoryAttribute) catAttribute;
				CategoryAttribute savedAttribute = (CategoryAttribute) dbaseCopy
						.getAttributeByIdentifier(attribute.getId());

				if (isAbstarctAttributeColumnToBeAdded(attribute, savedAttribute))
				{
					// Either the attribute is newly added or previously excluded(file type/multi select)
					// attribute.
					String attributeQuery = processAddAttribute(attribute, attrRlbkQries);
					attrQueries.add(attributeQuery);
				}
			}
		}

		processRemovedAttributes(catEntity, dbaseCopy, attrQueries, attrRlbkQries);

		Logger.out.debug("Exiting getUpdateAttributeQueryList method");

		return attrQueries;
	}

	/**
	 * It will create the queries for association in case of edit
	 * @param entity which is edited
	 * @param dbaseCopy
	 * @param attrRlbkQries rollback query list
	 * @return list of queries
	 * @throws DynamicExtensionsSystemException
	 */
	protected List<String> getUpdateAssociationsQueryList(Entity entity, Entity dbaseCopy,
			List<String> attrRlbkQries, HibernateDAO hibernateDAO)
			throws DynamicExtensionsSystemException
	{
		Logger.out.debug("Entering getUpdateAssociationsQueryList method");

		List<String> assoQueries = new ArrayList<String>();
		boolean isAddAssoQuery = true;

		Collection<AssociationInterface> associations = entity.getAssociationCollection();

		if (associations != null && !associations.isEmpty())
		{
			Iterator<AssociationInterface> assoIter = associations.iterator();

			while (assoIter.hasNext())
			{
				Association association = (Association) assoIter.next();
				Association assoDbaseCopy = (Association) dbaseCopy
						.getAssociationByIdentifier(association.getId());

				if (association.getIsSystemGenerated())
				{
					continue;
				}
				if (assoDbaseCopy == null)
				{
					isAddAssoQuery = true;
					assoQueries.addAll(getQueryPartForAssociation(association, attrRlbkQries,
							isAddAssoQuery));
				}
				else
				{
					if (EntityManagerUtil.isCardinalityChanged(association, assoDbaseCopy)
							|| EntityManagerUtil.isPrimaryKeyChanged(entity, hibernateDAO)
							|| EntityManagerUtil.isPrimaryKeyChanged(association.getTargetEntity(),
									hibernateDAO))
					{
						isAddAssoQuery = false;
						assoQueries.addAll(getQueryPartForAssociation(assoDbaseCopy, attrRlbkQries,
								isAddAssoQuery));

						isAddAssoQuery = true;
						assoQueries.addAll(getQueryPartForAssociation(association, attrRlbkQries,
								isAddAssoQuery));
					}
				}
			}
		}

		processRemovedAssociation(entity, dbaseCopy, assoQueries, attrRlbkQries);

		Logger.out.debug("Exiting getUpdateAssociationsQueryList method");

		return assoQueries;
	}

	/**
	 * @param entity
	 * @param dbaseCopy
	 * @param attrRlbkQries
	 * @return
	 */
	protected List<String> getUpdateAssociationsQueryList(CategoryEntity catEntity,
			CategoryEntity dbaseCopy, List<String> attrRlbkQries)
			throws DynamicExtensionsSystemException
	{
		Logger.out.debug("Entering getUpdateAssociationsQueryList method");

		List<String> assoQueries = new ArrayList<String>();
		boolean isAddAssoQry = true;

		Collection<CategoryAssociationInterface> associations = catEntity
				.getCategoryAssociationCollection();

		if (associations != null)
		{
			for (CategoryAssociationInterface catAsso : associations)
			{
				CategoryAssociation assoDbaseCopy = (CategoryAssociation) dbaseCopy
						.getAssociationByIdentifier(catAsso.getId());

				if (isAbstarctAttributeColumnToBeAdded(catAsso, assoDbaseCopy))
				{
					isAddAssoQry = true;
					String newAssoQuery = getQueryPartForAssociation(catAsso, attrRlbkQries,
							isAddAssoQry);
					assoQueries.add(newAssoQuery);
				}
			}
		}

		processRemovedAssociation(catEntity, dbaseCopy, assoQueries, attrRlbkQries);

		Logger.out.debug("Exiting getUpdateAssociationsQueryList method");

		return assoQueries;
	}

	/**
	 * This method processes all the attributes that were previously saved but now removed by editing.
	 * @param entity
	 * @param dbaseCopy
	 * @param attrQueries
	 * @param attrRlbkQries
	 * @throws DynamicExtensionsSystemException
	 * @throws DynamicExtensionsApplicationException
	 */
	protected void processRemovedAttributes(Entity entity, Entity dbaseCopy,
			List<String> attrQueries, List<String> attrRlbkQries)
			throws DynamicExtensionsSystemException, DynamicExtensionsApplicationException
	{
		Collection<AttributeInterface> savedAttr = dbaseCopy.getAttributeCollection();

		if (entity.getTableProperties() != null)
		{
			String tableName = entity.getTableProperties().getName();

			if (savedAttr != null && !savedAttr.isEmpty())
			{
				Iterator<AttributeInterface> savedAttrIter = savedAttr.iterator();
				while (savedAttrIter.hasNext())
				{
					Attribute savedAttribute = (Attribute) savedAttrIter.next();
					Attribute attribute = (Attribute) entity
							.getAttributeByIdentifier(savedAttribute.getId());

					if (attribute == null && isDataPresent(tableName, savedAttribute))
					{
						throw new DynamicExtensionsApplicationException(
								"data is present ,attribute can not be deleted", null, DYEXTN_A_013);
					}

					// Attribute is removed or modified, so its column needs to be removed.
					if (isAttributeColumnToBeRemoved(attribute, savedAttribute))
					{
						List<String> columnName = new ArrayList<String>();
						columnName.add(savedAttribute.getColumnProperties().getName());

						String type = "";
						StringBuffer remAttrRlbkQry = new StringBuffer(ALTER_TABLE);
						remAttrRlbkQry.append(WHITESPACE);
						remAttrRlbkQry.append(tableName);
						remAttrRlbkQry.append(WHITESPACE);
						remAttrRlbkQry.append(ADD_KEYWORD);
						remAttrRlbkQry.append(WHITESPACE);
						remAttrRlbkQry.append(OPENING_BRACKET);
						remAttrRlbkQry.append(getQueryPartForAttribute(savedAttribute, type, true));

						if (savedAttribute.getAttributeTypeInformation() instanceof FileAttributeTypeInformation)
						{
							columnName.add(savedAttribute.getColumnProperties().getName()
									+ UNDERSCORE + FILE_NAME);
							columnName.add(savedAttribute.getColumnProperties().getName()
									+ UNDERSCORE + CONTENT_TYPE);
							remAttrRlbkQry.append(COMMA);
							remAttrRlbkQry
									.append(extraColumnQueryStringForFileAttribute(savedAttribute));
						}

						remAttrRlbkQry.append(CLOSING_BRACKET);

						String remAttrQuery = getDropColumnQuery(tableName, columnName);

						attrQueries.add(remAttrQuery);
						attrRlbkQries.add(remAttrRlbkQry.toString());
					}
				}
			}
		}
	}

	/**
	 * This method processes all the attributes that were previously saved but now removed by editing.
	 * @param catEntity
	 * @param dbaseCopy
	 * @param attrQueries
	 * @param attrRlbkQries
	 * @throws DynamicExtensionsSystemException
	 * @throws DynamicExtensionsApplicationException
	 */
	protected void processRemovedAttributes(CategoryEntity catEntity, CategoryEntity dbaseCopy,
			List<String> attrQueries, List<String> attrRlbkQries)
			throws DynamicExtensionsSystemException, DynamicExtensionsApplicationException
	{
		Collection<CategoryAttributeInterface> savedAttr = dbaseCopy
				.getCategoryAttributeCollection();

		if (catEntity.getTableProperties() != null)
		{
			String tableName = catEntity.getTableProperties().getName();

			if (savedAttr != null)
			{
				for (CategoryAttributeInterface catAttr : savedAttr)
				{
					CategoryAttribute categoryAttr = (CategoryAttribute) catEntity
							.getAttributeByIdentifier(catAttr.getId());

					if (categoryAttr == null && isDataPresent(tableName, catAttr))
					{
						throw new DynamicExtensionsApplicationException(
								"data is present ,attribute can not be deleted", null, DYEXTN_A_013);
					}

					// Attribute is removed or modified, so its column needs to be removed.
					if (isAttributeColumnToBeRemoved(categoryAttr, catAttr))
					{
						String columnName = catAttr.getColumnProperties().getName();

						String remAttrQuery = ALTER_TABLE + WHITESPACE + tableName + WHITESPACE
								+ DROP_KEYWORD + WHITESPACE + COLUMN_KEYWORD + WHITESPACE
								+ columnName;
						String type = "";

						String remAttrRlbkQry = ALTER_TABLE + WHITESPACE + tableName + WHITESPACE
								+ ADD_KEYWORD + WHITESPACE
								+ getQueryPartForCategoryAttribute(catAttr, type, true);

						attrQueries.add(remAttrQuery);
						attrRlbkQries.add(remAttrRlbkQry);
					}
				}
			}
		}
	}

	/**
	 * This method returns true if an attribute is changed whether its column needs to be removed.
	 * @param attribute attribute
	 * @param dbaseCopy dataBaseCopy of the  attribute
	 * @return true if its column needs to be removed
	 */
	protected boolean isAttributeColumnToBeRemoved(AttributeInterface attribute,
			AttributeInterface dbaseCopy)
	{
		boolean isColRemoved = false;

		if (attribute == null)
		{
			// Attribute removed now.
			isColRemoved = true;

			if (isAttributeColumnToBeExcluded(dbaseCopy))
			{
				isColRemoved = false;
			}
		}
		else
		{
			// Previously not excluded, but now needs to excluded.
			if (!attribute.getColumnProperties().getName().equalsIgnoreCase(IDENTIFIER)
					&& (!isAttributeColumnToBeExcluded(dbaseCopy) && isAttributeColumnToBeExcluded(attribute)))
			{
				isColRemoved = true;
			}
		}

		return isColRemoved;
	}

	/**
	 * This method returns true if a category attribute is changed whether its column needs to be removed.
	 * @param catAttr attribute
	 * @param dbaseCopy dataBaseCopy of the  attribute
	 * @return true if its column to be removed
	 */
	protected boolean isAttributeColumnToBeRemoved(CategoryAttributeInterface catAttr,
			CategoryAttributeInterface dbaseCopy)
	{
		boolean isColRemoved = false;

		if (catAttr == null && dbaseCopy != null)
		{
			// Attribute removed now.
			isColRemoved = true;
		}

		return isColRemoved;
	}

	/**
	 * This method processes any associations that are deleted from the entity.
	 * @param entity
	 * @param dbaseCopy
	 * @param assoQueries
	 * @param attrRlbkQries
	 * @throws DynamicExtensionsSystemException
	 */
	protected void processRemovedAssociation(Entity entity, Entity dbaseCopy,
			List<String> assoQueries, List<String> attrRlbkQries)
			throws DynamicExtensionsSystemException
	{
		Logger.out.debug("Entering processRemovedAssociation method");

		Collection<AssociationInterface> savedAsso = dbaseCopy.getAssociationCollection();

		if (entity.getTableProperties() != null && (savedAsso != null && !savedAsso.isEmpty()))
		{

			Iterator<AssociationInterface> savedAssoIter = savedAsso.iterator();
			while (savedAssoIter.hasNext())
			{
				Association savedAssociation = (Association) savedAssoIter.next();
				Association association = (Association) entity
						.getAssociationByIdentifier(savedAssociation.getId());

				// Removed.
				if (association == null)
				{
					boolean isAddAssoQuery = false;

					assoQueries.addAll(getQueryPartForAssociation(savedAssociation, attrRlbkQries,
							isAddAssoQuery));
				}
			}

		}

		Logger.out.debug("Exiting processRemovedAssociation method");
	}

	/**
	 * This method processes any associations that are deleted from the entity.
	 * @param entity
	 * @param dbaseCopy
	 * @param assoQueries
	 * @param attrRlbkQries
	 * @throws DynamicExtensionsSystemException
	 */
	protected void processRemovedAssociation(CategoryEntity catEntity, CategoryEntity dbaseCopy,
			List<String> assoQueries, List<String> attrRlbkQries)
			throws DynamicExtensionsSystemException
	{
		Logger.out.debug("Entering processRemovedAssociation method");

		Collection<CategoryAssociationInterface> savedAsso = dbaseCopy
				.getCategoryAssociationCollection();

		if (catEntity.getTableProperties() != null && savedAsso != null)
		{
			for (CategoryAssociationInterface savedCatAsso : savedAsso)
			{
				CategoryAssociation association = (CategoryAssociation) catEntity
						.getAssociationByIdentifier(savedCatAsso.getId());

				// Removed.
				if (association == null)
				{
					boolean isAddAssoQuery = false;
					String remAssoQuery = getQueryPartForAssociation(savedCatAsso, attrRlbkQries,
							isAddAssoQuery);
					assoQueries.add(remAssoQuery);
				}
			}

		}

		Logger.out.debug("Exiting processRemovedAssociation method");
	}

	/**
	 * This method returns true if a category attribute is changed whether its column needs to be added.
	 * @param attribute
	 * @param dbaseCopy
	 * @return
	 */
	protected boolean isAttributeColumnToBeAdded(AttributeInterface attribute,
			AttributeInterface dbaseCopy)
	{
		boolean isColAdded = false;

		if (dbaseCopy == null)
		{
			// Newly added.
			if (!isAttributeColumnToBeExcluded(attribute))
			{
				isColAdded = true;
			}
		}
		else
		{
			// Previously excluded and now need to be added.
			if (isAttributeColumnToBeExcluded(dbaseCopy)
					&& !isAttributeColumnToBeExcluded(attribute))
			{
				isColAdded = true;
			}
		}

		return isColAdded;
	}

	/**
	 * This method returns true if a category attribute is changed whether its column needs to be added.
	 * @param attribute
	 * @param dbaseCopy
	 * @return
	 */
	protected boolean isAbstarctAttributeColumnToBeAdded(BaseAbstractAttributeInterface attribute,
			BaseAbstractAttributeInterface dbaseCopy)
	{
		boolean isColAdded = false;

		if (dbaseCopy == null && attribute != null)
		{
			// Newly added.
			isColAdded = true;
		}

		return isColAdded;
	}

	/**
	 * This method takes the edited attribute and its database copy and then looks for
	 * any changes. Changes could be in terms of data table query viz. change in the
	 * constraint NOT NULL AND UNIQUE.
	 * @param attribute
	 * @param savedAttr
	 * @param attrRlbkQries
	 * @return
	 * @throws DynamicExtensionsSystemException
	 * @throws DynamicExtensionsApplicationException
	 */
	protected List<String> processModifyAttribute(Attribute attribute, Attribute savedAttr,
			List<String> attrRlbkQries) throws DynamicExtensionsSystemException,
			DynamicExtensionsApplicationException
	{
		List<String> mdfyAttrQries = new ArrayList<String>();

		if (!isAttributeColumnToBeExcluded(attribute))
		{
			String newTypeClass = attribute.getAttributeTypeInformation().getClass().getName();
			String oldTypeClass = savedAttr.getAttributeTypeInformation().getClass().getName();

			if (!newTypeClass.equals(oldTypeClass))
			{
				checkIfDataTypeChangeAllowable(attribute);
				mdfyAttrQries = getAttributeDataTypeChangedQuery(attribute, savedAttr,
						attrRlbkQries);
			}
		}
		return mdfyAttrQries;
	}

	/**
	 * @param attribute
	 * @throws DynamicExtensionsApplicationException
	 * @throws DynamicExtensionsSystemException
	 */
	private void checkIfDataTypeChangeAllowable(Attribute attribute)
			throws DynamicExtensionsApplicationException, DynamicExtensionsSystemException
	{
		EntityInterface entity = attribute.getEntity();
		String tableName = entity.getTableProperties().getName();
		if (isDataPresent(tableName))
		{
			throw new DynamicExtensionsApplicationException(
					"Can not change the data type of the attribute", null, DYEXTN_A_009);
		}
	}

	/**
	 * @param tableName
	 * @param savedAttr
	 * @return
	 * @throws DynamicExtensionsSystemException
	 */
	public boolean isDataPresent(String tableName, Attribute savedAttr)
			throws DynamicExtensionsSystemException
	{
		boolean isDataPresent = false;

		StringBuffer query = new StringBuffer();
		LinkedList<ColumnValueBean> queryDataList = new LinkedList<ColumnValueBean>();
		if (isAttributeColumnToBeExcluded(savedAttr))
		{
			Collection<Integer> records = EntityManager.getInstance().getAttributeRecordsCount(
					savedAttr.getEntity().getId(), savedAttr.getId());

			if (records != null && !records.isEmpty())
			{
				Integer count = records.iterator().next();
				if (count > 0)
				{
					isDataPresent = true;
				}
			}
		}
		else
		{
			query.append(SELECT_KEYWORD).append(WHITESPACE).append("COUNT").append(OPENING_BRACKET)
					.append(ASTERIX).append(CLOSING_BRACKET).append(WHITESPACE)
					.append(FROM_KEYWORD).append(WHITESPACE).append(tableName).append(WHITESPACE)
					.append(WHERE_KEYWORD).append(WHITESPACE).append(
							savedAttr.getColumnProperties().getName()).append(WHITESPACE).append(
							"IS").append(WHITESPACE).append(NOT_KEYWORD).append(WHITESPACE).append(
							NULL_KEYWORD).append(WHITESPACE);

			if (!(savedAttr.getAttributeTypeInformation() instanceof FileAttributeTypeInformation)
					&& !(savedAttr.getAttributeTypeInformation() instanceof ObjectAttributeTypeInformation))
			{
				query.append(AND_KEYWORD).append(WHITESPACE).append(
						savedAttr.getColumnProperties().getName()).append(WHITESPACE).append(
						NOT_KEYWORD).append(WHITESPACE).append(LIKE_KEYWORD).append(WHITESPACE)
						.append(QUESTION_MARK);
				queryDataList
						.add(new ColumnValueBean(savedAttr.getColumnProperties().getName(), ""));
			}

			ResultSet resultSet = null;
			JDBCDAO jdbcDao = null;
			try
			{
				jdbcDao = DynamicExtensionsUtility.getJDBCDAO();
				resultSet = jdbcDao.getResultSet(query.toString(), queryDataList, null);
				resultSet.next();
				Long count = resultSet.getLong(1);
				if (count > 0)
				{
					isDataPresent = true;
				}
				jdbcDao.commit();
			}
			catch (DAOException e)
			{
				throw new DynamicExtensionsSystemException(
						ErrorConstants.CANNOT_CHECK_AVAIL_OF_DATA, e);
			}
			catch (SQLException e)
			{
				throw new DynamicExtensionsSystemException(
						ErrorConstants.CANNOT_CHECK_AVAIL_OF_DATA, e);
			}
			finally
			{
				if (resultSet != null)
				{
					try
					{
						jdbcDao.closeStatement(resultSet);
						DynamicExtensionsUtility.closeDAO(jdbcDao);
					}
					catch (DAOException e)
					{
						throw new DynamicExtensionsSystemException(e.getMessage(), e);
					}
				}
			}
		}

		return isDataPresent;
	}

	/**
	 * @param tableName
	 * @param savedAttr
	 * @return
	 * @throws DynamicExtensionsSystemException
	 */
	public boolean isDataPresent(String tableName, CategoryAttributeInterface savedAttr)
			throws DynamicExtensionsSystemException
	{
		boolean isDataPresent = false;

		StringBuffer query = new StringBuffer();
		LinkedList<ColumnValueBean> queryDataList = new LinkedList<ColumnValueBean>();

		if (savedAttr != null)
		{
			query.append(SELECT_KEYWORD).append(WHITESPACE).append("COUNT").append(OPENING_BRACKET)
					.append(ASTERIX).append(CLOSING_BRACKET).append(WHITESPACE)
					.append(FROM_KEYWORD).append(WHITESPACE).append(tableName).append(WHITESPACE)
					.append(WHERE_KEYWORD).append(WHITESPACE).append(
							savedAttr.getColumnProperties().getName()).append(WHITESPACE).append(
							"IS").append(WHITESPACE).append(NOT_KEYWORD).append(WHITESPACE).append(
							NULL_KEYWORD).append(WHITESPACE).append(AND_KEYWORD).append(WHITESPACE)
					.append(savedAttr.getColumnProperties().getName()).append(WHITESPACE).append(
							NOT_KEYWORD).append(WHITESPACE).append(LIKE_KEYWORD).append(WHITESPACE)
					.append(QUESTION_MARK);

			queryDataList.add(new ColumnValueBean(savedAttr.getColumnProperties().getName(), ""));
			ResultSet resultSet = null;
			JDBCDAO jdbcDao = null;
			try
			{
				jdbcDao = DynamicExtensionsUtility.getJDBCDAO();
				resultSet = jdbcDao.getResultSet(query.toString(), queryDataList, null);
				resultSet.next();
				Long count = resultSet.getLong(1);
				if (count > 0)
				{
					isDataPresent = true;
				}

			}
			catch (DAOException e)
			{
				throw new DynamicExtensionsSystemException(
						ErrorConstants.CANNOT_CHECK_AVAIL_OF_DATA, e);
			}
			catch (SQLException e)
			{
				throw new DynamicExtensionsSystemException(
						ErrorConstants.CANNOT_CHECK_AVAIL_OF_DATA, e);
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
					throw new DynamicExtensionsSystemException(e.getMessage(), e);
				}
			}
		}

		return isDataPresent;
	}

	/**
	 * @param tableName
	 * @return
	 * @throws DynamicExtensionsSystemException
	 */
	public boolean isDataPresent(String tableName) throws DynamicExtensionsSystemException
	{
		boolean isDataPresent = false;
		StringBuffer query = new StringBuffer();
		query.append(SELECT_KEYWORD).append(WHITESPACE).append("COUNT").append(OPENING_BRACKET)
				.append(ASTERIX).append(CLOSING_BRACKET).append(WHITESPACE).append(FROM_KEYWORD)
				.append(WHITESPACE).append(tableName);

		ResultSet resultSet = null;
		JDBCDAO jdbcDao = null;
		try
		{
			jdbcDao = DynamicExtensionsUtility.getJDBCDAO();
			resultSet = jdbcDao.getResultSet(query.toString(), null, null);
			resultSet.next();
			Long count = resultSet.getLong(1);
			if (count > 0)
			{
				isDataPresent = true;
			}
			return isDataPresent;
		}
		catch (DAOException e)
		{
			throw new DynamicExtensionsSystemException(ErrorConstants.CANNOT_CHECK_AVAIL_OF_DATA, e);
		}
		catch (SQLException e)
		{
			throw new DynamicExtensionsSystemException(ErrorConstants.CANNOT_CHECK_AVAIL_OF_DATA, e);
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
				throw new DynamicExtensionsSystemException(e.getMessage(), e);
			}

		}
	}

	/**
	 * This method returns the query for the attribute to modify its data type.
	 * @param attribute
	 * @param savedAttr
	 * @param modifyAttributeRollbackQuery
	 * @param tableName
	 * @return
	 * @throws DynamicExtensionsSystemException
	 */
	protected List<String> getAttributeDataTypeChangedQuery(Attribute attribute,
			Attribute savedAttr, List<String> mdfyAttrRlbkQries)
			throws DynamicExtensionsSystemException
	{
		String tableName = attribute.getEntity().getTableProperties().getName();
		String type = "";
		String mdfyAttrRlbkQry = "";

		String mdfyAttrQuery = getQueryPartForAttribute(attribute, type, false);
		mdfyAttrQuery = ALTER_TABLE + tableName + ADD_KEYWORD + OPENING_BRACKET + mdfyAttrQuery;

		mdfyAttrRlbkQry = getQueryPartForAttribute(savedAttr, type, false);
		mdfyAttrRlbkQry = ALTER_TABLE + WHITESPACE + tableName + WHITESPACE + MODIFY_KEYWORD
				+ WHITESPACE + mdfyAttrRlbkQry;

		String nullQueryKeyword = "";
		String nullQueryRlbkKeyword = "";
		List<String> mdfyAttrQries = new ArrayList<String>();
		mdfyAttrQries.add(ALTER_TABLE + tableName + DROP_KEYWORD + COLUMN_KEYWORD
				+ savedAttr.getColumnProperties().getName());

		if (attribute.getIsNullable() && !savedAttr.getIsNullable())
		{
			nullQueryKeyword = WHITESPACE + NULL_KEYWORD + WHITESPACE;
			nullQueryRlbkKeyword = WHITESPACE + NOT_KEYWORD + WHITESPACE + NULL_KEYWORD
					+ WHITESPACE;
		}
		else if (!attribute.getIsNullable() && savedAttr.getIsNullable())
		{
			nullQueryKeyword = WHITESPACE + NOT_KEYWORD + WHITESPACE + NULL_KEYWORD + WHITESPACE;
			nullQueryRlbkKeyword = WHITESPACE + NULL_KEYWORD + WHITESPACE;

		}

		// Added by: Kunal : Two more extra columns file name and content type
		// need to be added to the table.
		if (attribute.getAttributeTypeInformation() instanceof FileAttributeTypeInformation)
		{
			mdfyAttrQuery = mdfyAttrQuery + COMMA
					+ extraColumnQueryStringForFileAttribute(attribute);
			mdfyAttrRlbkQry = mdfyAttrRlbkQry + COMMA
					+ dropExtraColumnQueryStringForFileAttribute(attribute);
		}

		mdfyAttrQuery = mdfyAttrQuery + nullQueryKeyword;
		mdfyAttrRlbkQry = mdfyAttrRlbkQry + nullQueryRlbkKeyword;
		mdfyAttrRlbkQries.add(mdfyAttrRlbkQry);

		mdfyAttrQuery += CLOSING_BRACKET;
		mdfyAttrQries.add(mdfyAttrQuery);

		return mdfyAttrQries;
	}

	/**
	 * This method builds the query part for the newly added attribute.
	 * @param attribute Newly added attribute in the entity.
	 * @param attrRlbkQries This list is updated with the roll back queries for the actual queries.
	 * @return String The actual query part for the new attribute.
	 * @throws DynamicExtensionsSystemException
	 * @throws DynamicExtensionsApplicationException
	 */
	protected String processAddAttribute(Attribute attribute, List<String> attrRlbkQries)
			throws DynamicExtensionsSystemException, DynamicExtensionsApplicationException
	{
		String columnName = attribute.getColumnProperties().getName();
		String tableName = attribute.getEntity().getTableProperties().getName();
		String type = "";
		String newAttrQuery = ALTER_TABLE + WHITESPACE + tableName + WHITESPACE + ADD_KEYWORD
				+ WHITESPACE + OPENING_BRACKET + getQueryPartForAttribute(attribute, type, true);

		String newAttrRlbkQry = ALTER_TABLE + WHITESPACE + tableName + WHITESPACE + DROP_KEYWORD
				+ WHITESPACE + COLUMN_KEYWORD + WHITESPACE + OPENING_BRACKET + columnName;
		if (attribute.getAttributeTypeInformation() instanceof FileAttributeTypeInformation)
		{
			newAttrQuery += COMMA + extraColumnQueryStringForFileAttribute(attribute);
			newAttrRlbkQry += COMMA + dropExtraColumnQueryStringForFileAttribute(attribute);
		}

		newAttrQuery += CLOSING_BRACKET;
		newAttrRlbkQry += CLOSING_BRACKET;
		attrRlbkQries.add(newAttrRlbkQry);

		return newAttrQuery;
	}

	/**
	 * This method builds the query part for the newly added attribute.
	 * @param attribute Newly added attribute in the entity.
	 * @param attrRlbkQries This list is updated with the roll back queries for the actual queries.
	 * @return String The actual query part for the new attribute.
	 * @throws DynamicExtensionsSystemException
	 * @throws DynamicExtensionsApplicationException
	 */
	protected String processAddAttribute(CategoryAttribute attribute, List<String> attrRlbkQries)
			throws DynamicExtensionsSystemException, DynamicExtensionsApplicationException
	{
		String columnName = attribute.getColumnProperties().getName();
		String tableName = attribute.getCategoryEntity().getTableProperties().getName();
		String type = "";
		String newAttrQuery = ALTER_TABLE + WHITESPACE + tableName + WHITESPACE + ADD_KEYWORD
				+ WHITESPACE + getQueryPartForCategoryAttribute(attribute, type, true);

		String newAttrRlbkQry = ALTER_TABLE + WHITESPACE + tableName + WHITESPACE + DROP_KEYWORD
				+ WHITESPACE + COLUMN_KEYWORD + WHITESPACE + columnName;

		attrRlbkQries.add(newAttrRlbkQry);

		return newAttrQuery;
	}

	/**
	 * This method constructs the query part for adding two
	 * extra columns when an attribute of type file is created.
	 * @param attribute FileAttribute
	 * @return queryString
	 * @throws DynamicExtensionsSystemException
	 */
	protected String extraColumnQueryStringForFileAttribute(Attribute attribute)
			throws DynamicExtensionsSystemException
	{
		Attribute stringAttr = (Attribute) DomainObjectFactory.getInstance()
				.createStringAttribute();

		String query = attribute.getColumnProperties().getName() + UNDERSCORE + FILE_NAME
				+ WHITESPACE + getDatabaseTypeAndSize(stringAttr) + COMMA + WHITESPACE
				+ attribute.getColumnProperties().getName() + UNDERSCORE + CONTENT_TYPE
				+ WHITESPACE + getDatabaseTypeAndSize(stringAttr);

		return query;
	}

	/**
	 * This method constructs the query part for dropping
	 * the extra columns created while creating an attribute of type file.
	 * @param attribute FileAttribute
	 * @return query string
	 * @throws DynamicExtensionsSystemException
	 */
	protected String dropExtraColumnQueryStringForFileAttribute(Attribute attribute)
			throws DynamicExtensionsSystemException
	{
		String query = attribute.getColumnProperties().getName() + UNDERSCORE + FILE_NAME + COMMA
				+ WHITESPACE + attribute.getColumnProperties().getName() + UNDERSCORE
				+ CONTENT_TYPE;

		return query;
	}

	/**
	 * This method executes the queries which generate and or manipulate the data table associated with the entity.
	 * @param entity Entity for which the data table queries are to be executed.
	 * @param rlbkQryStack
	 * @param queries
	 * @param revQueries
	 * @throws DynamicExtensionsSystemException Whenever there is any exception , this exception is thrown with proper message and the exception is
	 * wrapped inside this exception.
	 */
	public Stack<String> executeQueries(List<String> queries, List<String> revQueries,
			Stack<String> rlbkQryStack) throws DynamicExtensionsSystemException
	{

		JDBCDAO jdbcDao = null;
		try
		{
			jdbcDao = DynamicExtensionsUtility.getJDBCDAO();
			Iterator<String> revQryIter = revQueries.iterator();

			if (queries != null && !queries.isEmpty())
			{
				Iterator<String> queryIter = queries.iterator();
				while (queryIter.hasNext())
				{
					String query = queryIter.next();
					Logger.out.info("Query: " + query);
					jdbcDao.executeUpdate(query);
					if (revQryIter.hasNext())
					{
						rlbkQryStack.push(revQryIter.next());
					}
				}
			}
			jdbcDao.commit();
		}
		catch (DAOException e)
		{
			throw new DynamicExtensionsSystemException(
					"Exception occured while forming the data tables for entity", e, DYEXTN_S_002);
		}
		finally
		{
			DynamicExtensionsUtility.closeDAO(jdbcDao);
		}

		return rlbkQryStack;
	}

	/**
	 * This method executes the query that selects record identifiers of the target entity
	 * that are associated to the source entity for a given association.
	 * @param query
	 * @return List of record identifiers of the target entity .
	 * @throws DynamicExtensionsSystemException
	 */
	protected List<Long> getAssociationRecordValues(String query,
			List<ColumnValueBean> queryDataList) throws DynamicExtensionsSystemException
	{
		List<Long> assoRecords = new ArrayList<Long>();
		ResultSet resultSet = null;
		JDBCDAO jdbcDao = null;
		try
		{
			jdbcDao = DynamicExtensionsUtility.getJDBCDAO();
			resultSet = jdbcDao.getResultSet(query, queryDataList, null);

			while (resultSet.next())
			{
				Long recordId = resultSet.getLong(1);
				assoRecords.add(recordId);
			}
		}
		catch (DAOException e)
		{
			throw new DynamicExtensionsSystemException(ErrorConstants.EXCEPTION_IN_EXEC_QUERY, e);
		}
		catch (SQLException e)
		{
			throw new DynamicExtensionsSystemException(ErrorConstants.EXCEPTION_IN_EXEC_QUERY, e);
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
				throw new DynamicExtensionsSystemException("can not close result set", e);
			}
		}

		return assoRecords;
	}

	/**
	 * This method executes the query that selects record identifiers of the target entity
	 * that are associated to the source entity for a given association.
	 * @param query
	 * @return List of record identifiers of the target entity .
	 * @throws DynamicExtensionsSystemException
	 */
	protected List<Long> getAssociationRecordValues(String query, JDBCDAO jdbcDAO,
			List<ColumnValueBean> queryDataList) throws DynamicExtensionsSystemException
	{
		List<Long> assoRecords = new ArrayList<Long>();
		ResultSet resultSet = null;
		JDBCDAO jdbcDao = jdbcDAO;
		try
		{
			resultSet = jdbcDao.getResultSet(query, queryDataList, null);

			while (resultSet.next())
			{
				Long recordId = resultSet.getLong(1);
				assoRecords.add(recordId);
			}
		}
		catch (DAOException e)
		{
			throw new DynamicExtensionsSystemException(ErrorConstants.EXCEPTION_IN_EXEC_QUERY, e);
		}
		catch (SQLException e)
		{
			throw new DynamicExtensionsSystemException(ErrorConstants.EXCEPTION_IN_EXEC_QUERY, e);
		}
		finally
		{
			try
			{
				jdbcDao.closeStatement(resultSet);
			}
			catch (DAOException e)
			{
				throw new DynamicExtensionsSystemException("can not close result set", e);
			}
		}

		return assoRecords;
	}

	/**
	 * This method make sure the cardinality constraints are properly followed.
	 * e.g for one to one association, it checks if target entity's record id is
	 * not associated to any other record.
	 * source entity.
	 * @param asso
	 * @param srcRecId
	 * @throws DynamicExtensionsApplicationException
	 * @throws DynamicExtensionsSystemException
	 */
	protected void verifyCardinalityConstraints(JDBCDAO jdbcDao, AssociationInterface asso,
			Long srcRecId) throws DynamicExtensionsApplicationException,
			DynamicExtensionsSystemException
	{
		EntityInterface tgtEnt = asso.getTargetEntity();
		Cardinality srcMaxCard = asso.getSourceRole().getMaximumCardinality();
		Cardinality tgtMaxCard = asso.getTargetRole().getMaximumCardinality();

		String columnName = "";
		String tableName = "";

		if (tgtMaxCard == Cardinality.ONE && srcMaxCard == Cardinality.ONE)
		{
			tableName = tgtEnt.getTableProperties().getName();
			columnName = asso.getConstraintProperties().getTgtEntityConstraintKeyProperties()
					.getTgtForiegnKeyColumnProperties().getName();

			String query = SELECT_KEYWORD + WHITESPACE + COUNT_KEYWORD + OPENING_BRACKET + "*"
					+ CLOSING_BRACKET + WHITESPACE + FROM_KEYWORD + WHITESPACE + tableName
					+ WHITESPACE + WHERE_KEYWORD + WHITESPACE + columnName + WHITESPACE + EQUAL
					+ WHITESPACE + QUESTION_MARK;
			LinkedList<ColumnValueBean> queryDataList = new LinkedList<ColumnValueBean>();
			queryDataList.add(new ColumnValueBean(columnName, srcRecId));
			ResultSet resultSet = null;
			try
			{
				resultSet = jdbcDao.getResultSet(query, queryDataList, null);
				resultSet.next();

				// If another source record is already using target record, throw exception.
				if (resultSet.getInt(1) != 0)
				{
					throw new DynamicExtensionsApplicationException(
							"Cardinality constraint violated", null, DYEXTN_A_005);
				}
			}
			catch (DAOException e)
			{
				throw new DynamicExtensionsSystemException(e.getMessage(), e);
			}
			catch (SQLException e)
			{
				throw new DynamicExtensionsSystemException(e.getMessage(), e);
			}
			finally
			{
				try
				{
					jdbcDao.closeStatement(resultSet);
				}
				catch (DAOException e)
				{
					throw new DynamicExtensionsSystemException(e.getMessage(), e);
				}
			}
		}
	}

	/**
	 * This method make sure the cardinality constraints are properly followed.
	 * e.g for one to one association, it checks if target entity's record id is
	 * not associated to any other record.
	 * source entity.
	 * @param catAsso
	 * @param srcRecId
	 * @throws DynamicExtensionsApplicationException
	 * @throws DynamicExtensionsSystemException
	 */
	protected void verifyCardinalityConstraints(CategoryAssociationInterface catAsso, Long srcRecId)
			throws DynamicExtensionsApplicationException, DynamicExtensionsSystemException
	{
		CategoryEntityInterface catEntity = catAsso.getTargetCategoryEntity();
		String columnName = "";
		String tableName = "";

		if (catEntity.getNumberOfEntries() == 1)
		{
			tableName = catEntity.getTableProperties().getName();
			columnName = catAsso.getConstraintProperties().getTgtEntityConstraintKeyProperties()
					.getTgtForiegnKeyColumnProperties().getName();

			String query = SELECT_KEYWORD + WHITESPACE + COUNT_KEYWORD + OPENING_BRACKET + "*"
					+ CLOSING_BRACKET + WHITESPACE + FROM_KEYWORD + WHITESPACE + tableName
					+ WHITESPACE + WHERE_KEYWORD + WHITESPACE + columnName + WHITESPACE + EQUAL
					+ WHITESPACE + QUESTION_MARK;
			LinkedList<ColumnValueBean> queryDataList = new LinkedList<ColumnValueBean>();
			queryDataList.add(new ColumnValueBean(columnName, srcRecId));
			ResultSet resultSet = null;
			JDBCDAO jdbcDao = null;
			try
			{
				jdbcDao = DynamicExtensionsUtility.getJDBCDAO();
				resultSet = jdbcDao.getResultSet(query, queryDataList, null);
				resultSet.next();
				// If another source record is already using target record, throw exception.
				if (resultSet.getInt(1) != 0)
				{
					throw new DynamicExtensionsApplicationException(
							"Cardinality constraint violated", null, DYEXTN_A_005);
				}
			}
			catch (DAOException e)
			{
				throw new DynamicExtensionsSystemException(e.getMessage(), e);
			}
			catch (SQLException e)
			{
				throw new DynamicExtensionsSystemException(e.getMessage(), e);
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
					throw new DynamicExtensionsSystemException(e.getMessage(), e);
				}

			}
		}
	}

	/**
	 *
	 * @param attribute
	 * @param value
	 * @return
	 * @throws DynamicExtensionsSystemException
	 */
	public Object getFormattedValue(AbstractAttribute attribute, Object value)
			throws DynamicExtensionsSystemException
	{
		String appName = DynamicExtensionDAO.getInstance().getAppName();
		String dbType = DAOConfigFactory.getInstance().getDAOFactory(appName).getDataBaseType();
		IDEDBUtility dbUtility = DynamicExtensionDBFactory.getInstance().getDbUtility(dbType);

		String frmtedValue = null;
		if (attribute == null)
		{
			throw new DynamicExtensionsSystemException("Attribute is null");
		}

		AttributeTypeInformationInterface attrTypInfo = ((Attribute) attribute)
				.getAttributeTypeInformation();

		if (attrTypInfo instanceof StringAttributeTypeInformation)
		{
			// Quick fix.
			if (value instanceof List)
			{
				if (((List) value).size() > 0)
				{
					frmtedValue = DynamicExtensionsUtility
							.getEscapedStringValue((String) ((List) value).get(0));
				}
			}
			else
			{
				frmtedValue = DynamicExtensionsUtility.getEscapedStringValue((String) value);

			}
		}
		else if (attrTypInfo instanceof DateAttributeTypeInformation)
		{
			String dateFormat = ((DateAttributeTypeInformation) attrTypInfo).getFormat();
			String datePattern = DynamicExtensionsUtility.getDateFormat(dateFormat);

			String str = null;
			if (value instanceof Date)
			{
				str = Utility.parseDateToString(((Date) value), datePattern);
			}
			else
			{
				str = (String) value;
			}

			if (datePattern.equals(ProcessorConstants.MONTH_YEAR_FORMAT) && str.length() != 0)
			{
				str = dbUtility.formatMonthAndYearDate(str, true);
			}

			if (datePattern.equals(ProcessorConstants.YEAR_ONLY_FORMAT) && str.length() != 0)
			{
				str = dbUtility.formatMonthAndYearDate(str, true);
			}
			// For MySQL5 if user does not enter any value for date field, it gets saved as 00-00-0000,
			// which is throwing exception so to avoid it store null value in database.
			if ("".equals(str.trim()))
			{
				frmtedValue = null;
			}
			else
			{
				JDBCDAO jdbcDao = null;
				try
				{
					jdbcDao = DynamicExtensionsUtility.getJDBCDAO();
					frmtedValue = jdbcDao.getStrTodateFunction() + "('" + str.trim() + "','"
							+ DynamicExtensionsUtility.getSQLDateFormat(datePattern) + "')";
				}
				finally
				{
					DynamicExtensionsUtility.closeDAO(jdbcDao);
				}
			}
		}
		else
		{
			// Quick fix.
			if (value instanceof List && ((List) value).size() > 0)
			{
				frmtedValue = ((List) value).get(0).toString();

			}
			else
			{
				frmtedValue = value.toString();
			}

			// In case of MySQL5, if the column data type is one of double, float or integer,
			// then it is not possible to pass '' as  a value in insert-update query so pass null as value.
			if (attrTypInfo instanceof NumericAttributeTypeInformation
					&& "".equals(frmtedValue.trim()))
			{
				frmtedValue = null;
			}
			if (attrTypInfo instanceof BooleanAttributeTypeInformation)
			{
				dbUtility.getValueForCheckBox(Boolean.parseBoolean(frmtedValue));
			}
			if (frmtedValue != null)
			{
				frmtedValue = frmtedValue;
			}
		}

		Logger.out.debug("getFormattedValue The formatted value for attribute "
				+ attribute.getName() + "is " + frmtedValue);

		return frmtedValue;
	}

	/**
	 * @param attribute
	 * @param value
	 * @return
	 * @throws DynamicExtensionsSystemException
	 */
	public boolean isValuePresent(AttributeInterface attribute, Object value)
			throws DynamicExtensionsSystemException
	{
		boolean isPresent = false;

		String tableName = attribute.getEntity().getTableProperties().getName();
		String columnName = attribute.getColumnProperties().getName();
		Object frmtedValue = QueryBuilderFactory.getQueryBuilder().getFormattedValue(
				(AbstractAttribute) attribute, value);

		StringBuffer query = new StringBuffer();
		query.append(SELECT_KEYWORD).append(WHITESPACE).append(COUNT_KEYWORD).append(
				OPENING_BRACKET).append(ASTERIX).append(CLOSING_BRACKET).append(WHITESPACE).append(
				FROM_KEYWORD).append(WHITESPACE).append(tableName).append(WHITESPACE).append(
				WHERE_KEYWORD).append(WHITESPACE).append(columnName).append(EQUAL).append(
				QUESTION_MARK).append(" and " + Constants.ACTIVITY_STATUS_COLUMN + " <> ?");

		LinkedList<ColumnValueBean> queryDataList = new LinkedList<ColumnValueBean>();
		queryDataList.add(new ColumnValueBean(columnName, frmtedValue));
		queryDataList.add(new ColumnValueBean(Constants.ACTIVITY_STATUS_COLUMN,
				Status.ACTIVITY_STATUS_DISABLED.toString()));
		ResultSet resultSet = null;
		JDBCDAO jdbcDao = null;
		try
		{
			jdbcDao = DynamicExtensionsUtility.getJDBCDAO();
			resultSet = jdbcDao.getResultSet(query.toString(), queryDataList, null);
			resultSet.next();
			Long count = resultSet.getLong(1);
			if (count > 0)
			{
				isPresent = true;
			}
		}
		catch (DAOException e)
		{
			throw new DynamicExtensionsSystemException("Can not check the availability of value", e);
		}
		catch (SQLException e)
		{
			throw new DynamicExtensionsSystemException("Can not check the availability of value", e);
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
				throw new DynamicExtensionsSystemException(e.getMessage(), e);
			}
		}
		return isPresent;
	}

	/**
	 * @param tableName
	 * @return
	 */
	public static String getRemoveDisbledRecordsQuery(String tableName)
	{
		String prefix = "";
		if (tableName != null && !tableName.equals(""))
		{
			prefix = tableName + ".";
		}

		return " " + prefix + Constants.ACTIVITY_STATUS_COLUMN + " <> '"
				+ Status.ACTIVITY_STATUS_DISABLED.toString() + "' ";
	}

	/**
	 * @param asso
	 * @param srcEntRecId
	 * @param tgtEntRecId
	 * @throws DynamicExtensionsSystemException
	 */
	public static void associateRecords(AssociationInterface asso, Long srcEntRecId,
			Long tgtEntRecId) throws DynamicExtensionsSystemException
	{
		RoleInterface srcRole = asso.getSourceRole();
		RoleInterface tgtRole = asso.getTargetRole();

		Cardinality srcMaxCard = srcRole.getMaximumCardinality();
		Cardinality tgtMaxCard = tgtRole.getMaximumCardinality();

		ConstraintPropertiesInterface constraint = asso.getConstraintProperties();

		String tableName = DynamicExtensionsUtility.getTableName(asso);

		StringBuffer query = new StringBuffer();
		query.append(UPDATE_KEYWORD).append(tableName).append(SET_KEYWORD);
		LinkedList<ColumnValueBean> colValBeanList = new LinkedList<ColumnValueBean>();

		if (srcMaxCard == Cardinality.MANY && tgtMaxCard == Cardinality.MANY)
		{
			query = new StringBuffer();
			String tgtFkColName = constraint.getSrcEntityConstraintKeyProperties()
					.getTgtForiegnKeyColumnProperties().getName();
			String srcFkColname = constraint.getTgtEntityConstraintKeyProperties()
					.getTgtForiegnKeyColumnProperties().getName();
			query.append(INSERT_INTO_KEYWORD).append(tableName).append(OPENING_BRACKET).append(
					tgtFkColName).append(COMMA).append(srcFkColname).append(CLOSING_BRACKET)
					.append("values").append(OPENING_BRACKET).append(QUESTION_MARK).append(COMMA)
					.append(QUESTION_MARK).append(CLOSING_BRACKET);
			colValBeanList.add(new ColumnValueBean(tgtFkColName, srcEntRecId));
			colValBeanList.add(new ColumnValueBean(srcFkColname, tgtEntRecId));

		}
		else if (srcMaxCard == Cardinality.MANY && tgtMaxCard == Cardinality.ONE)
		{
			String tgtFkColName = constraint.getSrcEntityConstraintKeyProperties()
					.getTgtForiegnKeyColumnProperties().getName();
			query.append(tgtFkColName).append(EQUAL).append(QUESTION_MARK).append(WHERE_KEYWORD)
					.append(IDENTIFIER).append(EQUAL).append(QUESTION_MARK);
			colValBeanList.add(new ColumnValueBean(tgtFkColName, tgtEntRecId));
			colValBeanList.add(new ColumnValueBean(IDENTIFIER, srcEntRecId));
		}
		else
		{
			String srcFkColname = constraint.getTgtEntityConstraintKeyProperties()
					.getTgtForiegnKeyColumnProperties().getName();
			query.append(srcFkColname).append(EQUAL).append(QUESTION_MARK).append(WHERE_KEYWORD)
					.append(IDENTIFIER).append(EQUAL).append(QUESTION_MARK);
			colValBeanList.add(new ColumnValueBean(srcFkColname, srcEntRecId));
			colValBeanList.add(new ColumnValueBean(IDENTIFIER, tgtEntRecId));
		}

		JDBCDAO jdbcDao = null;
		try
		{
			jdbcDao = DynamicExtensionsUtility.getJDBCDAO();
			LinkedList<LinkedList<ColumnValueBean>> columnValLinkedList = new LinkedList<LinkedList<ColumnValueBean>>();
			columnValLinkedList.add(colValBeanList);
			jdbcDao.executeUpdate(query.toString(), columnValLinkedList);
			jdbcDao.commit();
		}
		catch (DAOException e)
		{
			connectionRollBack(jdbcDao);
		}
		finally
		{
			DynamicExtensionsUtility.closeDAO(jdbcDao);
		}
	}

	/**
	 * This method rolls back the connection.
	 * @param connection
	 * @throws DynamicExtensionsSystemException
	 */
	private static void connectionRollBack(JDBCDAO jdbcDao) throws DynamicExtensionsSystemException
	{
		try
		{
			jdbcDao.rollback();
		}
		catch (DAOException e)
		{
			Logger.out.debug(e.getMessage());
			throw new DynamicExtensionsSystemException("Can not rollback query", e);
		}

		throw new DynamicExtensionsSystemException("Can not execute query");
	}

	/**
	 * This method generate the alter table query to drop columns
	 * @param tableName
	 * @param columnName
	 * @return alter query
	 */
	protected String getDropColumnQuery(String tableName, List<String> columnName)
	{
		StringBuffer alterTableQuery = new StringBuffer();

		alterTableQuery.append(ALTER_TABLE);
		alterTableQuery.append(tableName);
		alterTableQuery.append(WHITESPACE);

		for (int i = 0; i < columnName.size(); i++)
		{
			alterTableQuery.append(DROP_KEYWORD);
			alterTableQuery.append(COLUMN_KEYWORD);
			alterTableQuery.append(columnName.get(i));
			if (i != columnName.size() - 1)
			{
				alterTableQuery.append(COMMA);
			}
		}

		return alterTableQuery.toString();
	}

	/**
	 * @param valueObj
	 * @return
	 * @throws DynamicExtensionsSystemException
	 */
	protected Object convertValueToObject(Object valueObj) throws DynamicExtensionsSystemException
	{
		return "";
	}

}