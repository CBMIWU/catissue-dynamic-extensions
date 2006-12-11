/**
 *<p>Title: </p>
 *<p>Description:  </p>
 *<p>Copyright://TODO</p>
 *@author Vishvesh Mulay
 *@version 1.0
 */

package edu.common.dynamicextensions.entitymanager;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import edu.common.dynamicextensions.domain.Association;
import edu.common.dynamicextensions.domain.Attribute;
import edu.common.dynamicextensions.domain.DomainObjectFactory;
import edu.common.dynamicextensions.domain.Entity;
import edu.common.dynamicextensions.domaininterface.AssociationInterface;
import edu.common.dynamicextensions.domaininterface.AttributeInterface;
import edu.common.dynamicextensions.domaininterface.EntityInterface;
import edu.common.dynamicextensions.domaininterface.RoleInterface;
import edu.common.dynamicextensions.exception.BaseDynamicExtensionsException;
import edu.common.dynamicextensions.exception.DynamicExtensionsApplicationException;
import edu.common.dynamicextensions.exception.DynamicExtensionsSystemException;
import edu.common.dynamicextensions.util.DynamicExtensionsBaseTestCase;
import edu.common.dynamicextensions.util.global.Constants;
import edu.common.dynamicextensions.util.global.Variables;
import edu.common.dynamicextensions.util.global.Constants.AssociationDirection;
import edu.common.dynamicextensions.util.global.Constants.AssociationType;
import edu.common.dynamicextensions.util.global.Constants.Cardinality;
import edu.wustl.common.util.logger.Logger;

public class TestEntityManagerForAssociations extends DynamicExtensionsBaseTestCase
{

	/**
	 * 
	 */
	public TestEntityManagerForAssociations()
	{
		super();
		//TODO Auto-generated constructor stub
	}

	/**
	 * @param arg0 name
	 */
	public TestEntityManagerForAssociations(String arg0)
	{
		super(arg0);
		//TODO Auto-generated constructor stub
	}

	/**
	 * @see edu.common.dynamicextensions.util.DynamicExtensionsBaseTestCase#setUp()
	 */
	protected void setUp()
	{
		super.setUp();
	}

	/**
	 * @see edu.common.dynamicextensions.util.DynamicExtensionsBaseTestCase#tearDown()
	 */
	protected void tearDown()
	{
		super.tearDown();
	}

	/**
	 * @param associationType associationType
	 * @param name name
	 * @param minCard  minCard
	 * @param maxCard maxCard
	 * @return  RoleInterface
	 */
	private RoleInterface getRole(AssociationType associationType, String name,
			Cardinality minCard, Cardinality maxCard)
	{
		RoleInterface role = DomainObjectFactory.getInstance().createRole();
		role.setAssociationsType(associationType);
		role.setName(name);
		role.setMinimumCardinality(minCard);
		role.setMaximumCardinality(maxCard);
		return role;
	}

	/**
	 * This test case test for associating two entities with one to many association 
	 * 
	 * for oracle it should throw exception.
	 * for mysql  it works.  
	 */
	public void testCreateEntityWithOneToManyAssociation()
	{
		EntityManagerInterface entityManager = EntityManager.getInstance();
		DomainObjectFactory factory = DomainObjectFactory.getInstance();

		// create user 
		EntityInterface user = factory.createEntity();
		AttributeInterface userNameAttribute = factory.createStringAttribute();
		userNameAttribute.setName("user name");
		user.setName("user");
		user.addAbstractAttribute(userNameAttribute);

		// create study 
		EntityInterface study = factory.createEntity();
		AttributeInterface studyNameAttribute = factory.createStringAttribute();
		studyNameAttribute.setName("study name");
		study.setName("study");
		study.addAbstractAttribute(studyNameAttribute);

		// Associate user (1)------ >(*)study       
		AssociationInterface association = factory.createAssociation();

		association.setTargetEntity(study);
		association.setAssociationDirection(AssociationDirection.SRC_DESTINATION);
		association.setName("primaryInvestigator");
		association.setSourceRole(getRole(AssociationType.ASSOCIATION, "primaryInvestigator",
				Cardinality.ONE, Cardinality.ONE));
		association.setTargetRole(getRole(AssociationType.ASSOCIATION, "study", Cardinality.ZERO,
				Cardinality.MANY));

		user.addAbstractAttribute(association);

		try
		{
			//entityManager.createEntity(study);

			EntityInterface savedUser = entityManager.persistEntity(user);

			ResultSetMetaData metaData = executeQueryForMetadata("select * from "
					+ study.getTableProperties().getName());
			assertEquals(3, metaData.getColumnCount());
		}
		catch (DynamicExtensionsSystemException e)
		{
			e.printStackTrace();
			fail();
		}

		catch (DynamicExtensionsApplicationException e)
		{
			e.printStackTrace();
			fail();

		}
		catch (SQLException e)
		{
			e.printStackTrace();
			fail();

		}

	}

	/**
	 * This test case test for associating two entities with one to many association 
	 * 
	 * for oracle it should throw exception.
	 * for mysql  it works.  
	 */
	public void testCreateEntityWithManyToOneAssociation()
	{
		EntityManagerInterface entityManager = EntityManager.getInstance();
		DomainObjectFactory factory = DomainObjectFactory.getInstance();

		// create user 
		EntityInterface user = factory.createEntity();
		AttributeInterface userNameAttribute = factory.createStringAttribute();
		userNameAttribute.setName("user name");
		user.setName("user");
		user.addAbstractAttribute(userNameAttribute);

		// create study 
		EntityInterface study = factory.createEntity();
		AttributeInterface studyNameAttribute = factory.createStringAttribute();
		studyNameAttribute.setName("study name");
		study.setName("study");
		study.addAbstractAttribute(studyNameAttribute);

		// Associate user (1)------ >(*)study       
		AssociationInterface association = factory.createAssociation();

		association.setTargetEntity(study);
		association.setAssociationDirection(AssociationDirection.SRC_DESTINATION);
		association.setName("primaryInvestigator");
		association.setSourceRole(getRole(AssociationType.ASSOCIATION, "primaryInvestigator",
				Cardinality.ZERO, Cardinality.MANY));
		association.setTargetRole(getRole(AssociationType.ASSOCIATION, "study", Cardinality.ZERO,
				Cardinality.ONE));

		user.addAbstractAttribute(association);

		try
		{
			//entityManager.createEntity(study);

			EntityInterface savedUser = entityManager.persistEntity(user);

			ResultSetMetaData metaData = executeQueryForMetadata("select * from "
					+ user.getTableProperties().getName());
			assertEquals(3, metaData.getColumnCount());
		}
		catch (DynamicExtensionsSystemException e)
		{
			e.printStackTrace();
			fail();
		}

		catch (DynamicExtensionsApplicationException e)
		{
			e.printStackTrace();
			fail();

		}
		catch (SQLException e)
		{
			e.printStackTrace();
			fail();

		}

	}

	/**
	 * This test case tries to modify data type of the attribute,when data is present for that column.
	 * for oracle it should throw exception.
	 * for mysql  it works.  
	 */
	public void testCreateEntityWithAssociationWithUnsavedTargetEntity()
	{
		Entity srcEntity;

		try
		{
			srcEntity = (Entity) new MockEntityManager().initializeEntity();
			srcEntity.setName("study");
			//Entity savedSrcEntity = (Entity) EntityManager.getInstance().createEntity(srcEntity);
			Entity targetEntity = (Entity) new MockEntityManager().initializeEntity();
			targetEntity.setName("user");
			//Entity savedTargetEntity = (Entity) EntityManager.getInstance().createEntity(targetEntity);
			Association association = (Association) DomainObjectFactory.getInstance()
					.createAssociation();
			association.setEntity(srcEntity);
			association.setTargetEntity(targetEntity);
			association.setAssociationDirection(AssociationDirection.SRC_DESTINATION);
			association.setName("primaryInvestigator");
			association.setSourceRole(getRole(AssociationType.ASSOCIATION, "study",
					Cardinality.ONE, Cardinality.ONE));
			association.setTargetRole(getRole(AssociationType.ASSOCIATION, "primaryInvestigator",
					Cardinality.ONE, Cardinality.ONE));
			srcEntity.addAbstractAttribute(association);
			// association.sets

			EntityManager.getInstance().persistEntity(srcEntity);

		}
		catch (BaseDynamicExtensionsException e)
		{
			// TODO Auto-generated catch block
			fail();
			e.printStackTrace();
		}

	}

	/**
	 * This test case test for associating two entities with many to many association  and direction is src_destination.
	 * 
	 * for oracle it should throw exception.
	 * for mysql  it works.  
	 */
	public void testCreateEntityWithManyToManyAssociation()
	{
		EntityManagerInterface entityManager = EntityManager.getInstance();
		DomainObjectFactory factory = DomainObjectFactory.getInstance();

		// create user 
		EntityInterface user = factory.createEntity();
		AttributeInterface userNameAttribute = factory.createStringAttribute();
		userNameAttribute.setName("user name");
		user.setName("user");
		user.addAbstractAttribute(userNameAttribute);

		// create study 
		EntityInterface study = factory.createEntity();
		AttributeInterface studyNameAttribute = factory.createStringAttribute();
		studyNameAttribute.setName("study name");
		study.setName("study");
		study.addAbstractAttribute(studyNameAttribute);

		// Associate user (1)------ >(*)study       
		AssociationInterface association = factory.createAssociation();

		association.setTargetEntity(study);
		association.setAssociationDirection(AssociationDirection.SRC_DESTINATION);
		association.setName("primaryInvestigator");
		association.setSourceRole(getRole(AssociationType.ASSOCIATION, "primaryInvestigator",
				Cardinality.ONE, Cardinality.MANY));
		association.setTargetRole(getRole(AssociationType.ASSOCIATION, "study", Cardinality.ZERO,
				Cardinality.MANY));

		user.addAbstractAttribute(association);

		try
		{

			EntityInterface savedUser = entityManager.persistEntity(user);

			String middleTableName = association.getConstraintProperties().getName();

			assertNotNull(middleTableName);

			ResultSetMetaData metaData = executeQueryForMetadata("select * from " + middleTableName);
			assertEquals(3, metaData.getColumnCount());
		}
		catch (DynamicExtensionsSystemException e)
		{
			e.printStackTrace();
			fail();
		}

		catch (DynamicExtensionsApplicationException e)
		{
			e.printStackTrace();
			fail();

		}
		catch (SQLException e)
		{
			e.printStackTrace();
			fail();

		}

	}

	/**
	 * Purpose is to test the self referencing of the entity. 
	 * Scenario - user(*)------>(1)User
	 *                   creator   
	 */
	public void testEditEntityWithSelfReferencingBiDirectionalManyToManyAssociation()
	{
		EntityManagerInterface entityManager = EntityManager.getInstance();
		DomainObjectFactory factory = DomainObjectFactory.getInstance();

		// create user 
		EntityInterface user = factory.createEntity();
		AttributeInterface userNameAttribute = factory.createStringAttribute();
		userNameAttribute.setName("user name");
		user.setName("user");
		user.addAbstractAttribute(userNameAttribute);

		try
		{
			user = entityManager.persistEntity(user);

			// Associate user (*)------ >(1)user       
			AssociationInterface association = factory.createAssociation();

			association.setTargetEntity(user);
			association.setAssociationDirection(AssociationDirection.BI_DIRECTIONAL);
			association.setName("primaryInvestigator");
			association.setSourceRole(getRole(AssociationType.ASSOCIATION, "subpi",
					Cardinality.ONE, Cardinality.ONE));
			association.setTargetRole(getRole(AssociationType.ASSOCIATION, "pi", Cardinality.ONE,
					Cardinality.MANY));

			user.addAbstractAttribute(association);

			EntityInterface savedUser = entityManager.persistEntity(user);

			String tableName = user.getTableProperties().getName();

			assertNotNull(tableName);

			ResultSetMetaData metaData = executeQueryForMetadata("select * from " + tableName);
			assertEquals(3, metaData.getColumnCount());
		}
		catch (DynamicExtensionsSystemException e)
		{
			e.printStackTrace();
			fail();
		}

		catch (DynamicExtensionsApplicationException e)
		{
			e.printStackTrace();
			fail();

		}
		catch (SQLException e)
		{
			e.printStackTrace();
			fail();

		}

	}

	/**
	 * This method test for inserting data for a multi select attribute
	 */
	public void testInsertDataForAssociationMany2Many()
	{

		EntityManagerInterface entityManagerInterface = EntityManager.getInstance();
		DomainObjectFactory factory = DomainObjectFactory.getInstance();

		try
		{

			//          create user 
			EntityInterface user = factory.createEntity();
			AttributeInterface userNameAttribute = factory.createStringAttribute();
			userNameAttribute.setName("user name");
			user.setName("user");
			user.addAbstractAttribute(userNameAttribute);

			//          create study 
			EntityInterface study = factory.createEntity();
			AttributeInterface studyNameAttribute = factory.createStringAttribute();
			studyNameAttribute.setName("study name");
			study.setName("study");
			study.addAbstractAttribute(studyNameAttribute);

			//          Associate user (1)------ >(*)study       
			AssociationInterface association = factory.createAssociation();
			association.setTargetEntity(study);
			association.setAssociationDirection(AssociationDirection.SRC_DESTINATION);
			association.setName("primaryInvestigator");
			association.setSourceRole(getRole(AssociationType.ASSOCIATION, "primaryInvestigator",
					Cardinality.ZERO, Cardinality.MANY));
			association.setTargetRole(getRole(AssociationType.ASSOCIATION, "study",
					Cardinality.ZERO, Cardinality.MANY));

			user.addAbstractAttribute(association);

			EntityInterface savedEntity = entityManagerInterface.persistEntity(user);

			Map dataValue = new HashMap();

			dataValue.put(studyNameAttribute, "study");
			entityManagerInterface.insertData(study, dataValue);
			dataValue.clear();
			dataValue.put(studyNameAttribute, "study1");
			entityManagerInterface.insertData(study, dataValue);

			dataValue.clear();
			List<Long> targetIdList = new ArrayList<Long>();
			targetIdList.add(1L);
			targetIdList.add(2L);

			dataValue.put(userNameAttribute, "rahul");
			dataValue.put(association, targetIdList);

			entityManagerInterface.insertData(savedEntity, dataValue);

			dataValue.clear();
			dataValue.put(userNameAttribute, "vishvesh");
			dataValue.put(association, targetIdList);
			entityManagerInterface.insertData(savedEntity, dataValue);

			ResultSet resultSet = executeQuery("select count(*) from "
					+ association.getConstraintProperties().getName());
			resultSet.next();
			assertEquals(4, resultSet.getInt(1));
		}
		catch (DynamicExtensionsSystemException e)
		{
			fail();
			Logger.out.debug(e.getStackTrace());
		}
		catch (DynamicExtensionsApplicationException e)
		{
			fail();
			Logger.out.debug(e.getStackTrace());
		}
		catch (Exception e)
		{
			e.printStackTrace();
			fail();

			Logger.out.debug(e.getStackTrace());
		}

	}

	/**
	 * This method test for inserting data for a multi select attribute
	 */
	public void testInsertDataForAssociationOne2Many()
	{

		EntityManagerInterface entityManagerInterface = EntityManager.getInstance();
		DomainObjectFactory factory = DomainObjectFactory.getInstance();

		try
		{

			//          create user 
			EntityInterface user = factory.createEntity();
			AttributeInterface userNameAttribute = factory.createStringAttribute();
			userNameAttribute.setName("user name");
			user.setName("user");
			user.addAbstractAttribute(userNameAttribute);

			//          create study 
			EntityInterface study = factory.createEntity();
			AttributeInterface studyNameAttribute = factory.createStringAttribute();
			studyNameAttribute.setName("study name");
			study.setName("study");
			study.addAbstractAttribute(studyNameAttribute);

			//          Associate user (1)------ >(*)study       
			AssociationInterface association = factory.createAssociation();
			association.setTargetEntity(study);
			association.setAssociationDirection(AssociationDirection.SRC_DESTINATION);
			association.setName("primaryInvestigator");
			association.setSourceRole(getRole(AssociationType.ASSOCIATION, "primaryInvestigator",
					Cardinality.ZERO, Cardinality.ONE));
			association.setTargetRole(getRole(AssociationType.ASSOCIATION, "study",
					Cardinality.ZERO, Cardinality.MANY));

			user.addAbstractAttribute(association);

			EntityInterface savedEntity = entityManagerInterface.persistEntity(user);

			Map dataValue = new HashMap();

			dataValue.put(studyNameAttribute, "study");
			entityManagerInterface.insertData(study, dataValue);

			dataValue.clear();
			dataValue.put(studyNameAttribute, "study1");
			entityManagerInterface.insertData(study, dataValue);

			dataValue.clear();
			List<Long> targetIdList = new ArrayList<Long>();
			targetIdList.add(1L);
			targetIdList.add(2L);

			dataValue.put(userNameAttribute, "rahul");
			dataValue.put(association, targetIdList);

			entityManagerInterface.insertData(savedEntity, dataValue);

			ResultSet resultSet = executeQuery("select count(*) from "
					+ study.getTableProperties().getName());
			resultSet.next();
			assertEquals(2, resultSet.getInt(1));
		}
		catch (DynamicExtensionsSystemException e)
		{
			fail();
			Logger.out.debug(e.getStackTrace());
		}
		catch (DynamicExtensionsApplicationException e)
		{
			fail();
			Logger.out.debug(e.getStackTrace());
		}
		catch (Exception e)
		{
			e.printStackTrace();
			fail();

			Logger.out.debug(e.getStackTrace());
		}

	}

	//
	/**
	 * This method test for inserting data for a multi select attribute
	 */
	public void testInsertDataForAssociationMany2One()
	{

		EntityManagerInterface entityManagerInterface = EntityManager.getInstance();
		DomainObjectFactory factory = DomainObjectFactory.getInstance();

		try
		{

			//          create user 
			EntityInterface user = factory.createEntity();
			AttributeInterface userNameAttribute = factory.createStringAttribute();
			userNameAttribute.setName("user name");
			user.setName("user");
			user.addAbstractAttribute(userNameAttribute);

			//          create study 
			EntityInterface study = factory.createEntity();
			AttributeInterface studyNameAttribute = factory.createStringAttribute();
			studyNameAttribute.setName("study name");
			study.setName("study");
			study.addAbstractAttribute(studyNameAttribute);

			//          Associate user (1)------ >(*)study       
			AssociationInterface association = factory.createAssociation();
			association.setTargetEntity(study);
			association.setAssociationDirection(AssociationDirection.SRC_DESTINATION);
			association.setName("primaryInvestigator");
			association.setSourceRole(getRole(AssociationType.ASSOCIATION, "primaryInvestigator",
					Cardinality.ZERO, Cardinality.MANY));
			association.setTargetRole(getRole(AssociationType.ASSOCIATION, "study",
					Cardinality.ZERO, Cardinality.ONE));

			user.addAbstractAttribute(association);

			EntityInterface savedEntity = entityManagerInterface.persistEntity(user);

			Map dataValue = new HashMap();
			List<Long> targetIdList = new ArrayList<Long>();
			targetIdList.add(1L);
			dataValue.put(userNameAttribute, "rahul");

			dataValue.put(association, targetIdList);

			entityManagerInterface.insertData(savedEntity, dataValue);

			ResultSet resultSet = executeQuery("select * from "
					+ savedEntity.getTableProperties().getName());
			resultSet.next();
			assertEquals(1, resultSet.getInt(1));

		}
		catch (DynamicExtensionsSystemException e)
		{
			fail();
			Logger.out.debug(e.getStackTrace());
		}
		catch (DynamicExtensionsApplicationException e)
		{
			fail();
			Logger.out.debug(e.getStackTrace());
		}
		catch (Exception e)
		{
			e.printStackTrace();
			fail();

			Logger.out.debug(e.getStackTrace());
		}

	}

	/**
	 * This method test for inserting data for a multi select attribute
	 */
	public void testEditDataForAssociationMany2One()
	{

		EntityManagerInterface entityManagerInterface = EntityManager.getInstance();
		DomainObjectFactory factory = DomainObjectFactory.getInstance();

		try
		{

			//          create user 
			EntityInterface user = factory.createEntity();
			AttributeInterface userNameAttribute = factory.createStringAttribute();
			userNameAttribute.setName("user name");
			user.setName("user");
			user.addAbstractAttribute(userNameAttribute);

			//          create study 
			EntityInterface study = factory.createEntity();
			AttributeInterface studyNameAttribute = factory.createStringAttribute();
			studyNameAttribute.setName("study name");
			study.setName("study");
			study.addAbstractAttribute(studyNameAttribute);

			//          Associate user (1)------ >(*)study       
			AssociationInterface association = factory.createAssociation();
			association.setTargetEntity(study);
			association.setAssociationDirection(AssociationDirection.SRC_DESTINATION);
			association.setName("primaryInvestigator");
			association.setSourceRole(getRole(AssociationType.ASSOCIATION, "primaryInvestigator",
					Cardinality.ZERO, Cardinality.MANY));
			association.setTargetRole(getRole(AssociationType.ASSOCIATION, "study",
					Cardinality.ZERO, Cardinality.ONE));

			user.addAbstractAttribute(association);

			EntityInterface savedEntity = entityManagerInterface.persistEntity(user);

			Map dataValue = new HashMap();
			List<Long> targetIdList = new ArrayList<Long>();
			targetIdList.add(1L);
			dataValue.put(userNameAttribute, "rahul");

			dataValue.put(association, targetIdList);

			Long recordId = entityManagerInterface.insertData(savedEntity, dataValue);

			dataValue.clear();
			dataValue.put(userNameAttribute, "vishvesh");
			dataValue.put(association, targetIdList);
			Long recordId1 = entityManagerInterface.insertData(savedEntity, dataValue);

			dataValue.clear();
			dataValue = entityManagerInterface.getRecordById(savedEntity, recordId);
			List targetRecordIdList = (List) dataValue.get(association);
			System.out.println(dataValue);
			assertEquals(1, targetRecordIdList.size());
			assertEquals(1L, targetRecordIdList.get(0));

			dataValue.clear();
			dataValue = entityManagerInterface.getRecordById(savedEntity, recordId1);
			targetRecordIdList = (List) dataValue.get(association);
			System.out.println(dataValue);
			assertEquals(1, targetRecordIdList.size());
			assertEquals(1L, targetRecordIdList.get(0));

		}
		catch (DynamicExtensionsSystemException e)
		{
			e.printStackTrace();
			fail();
			Logger.out.debug(e.getStackTrace());
		}
		catch (DynamicExtensionsApplicationException e)
		{
			e.printStackTrace();
			fail();
			Logger.out.debug(e.getStackTrace());
		}
		catch (Exception e)
		{
			e.printStackTrace();
			fail();

			Logger.out.debug(e.getStackTrace());
		}

	}

	/**
	 * This method test for inserting data for a multi select attribute
	 */
	public void testGetRecordByIdAssociationMany2Many()
	{

		EntityManagerInterface entityManagerInterface = EntityManager.getInstance();
		DomainObjectFactory factory = DomainObjectFactory.getInstance();

		try
		{

			//          create user 
			EntityInterface user = factory.createEntity();
			AttributeInterface userNameAttribute = factory.createStringAttribute();
			userNameAttribute.setName("user name");
			user.setName("user");
			user.addAbstractAttribute(userNameAttribute);

			//          create study 
			EntityInterface study = factory.createEntity();
			AttributeInterface studyNameAttribute = factory.createStringAttribute();
			studyNameAttribute.setName("study name");
			study.setName("study");
			study.addAbstractAttribute(studyNameAttribute);

			//          Associate user (1)------ >(*)study       
			AssociationInterface association = factory.createAssociation();
			association.setTargetEntity(study);
			association.setAssociationDirection(AssociationDirection.SRC_DESTINATION);
			association.setName("primaryInvestigator");
			association.setSourceRole(getRole(AssociationType.ASSOCIATION, "primaryInvestigator",
					Cardinality.ZERO, Cardinality.MANY));
			association.setTargetRole(getRole(AssociationType.ASSOCIATION, "study",
					Cardinality.ZERO, Cardinality.MANY));

			user.addAbstractAttribute(association);

			EntityInterface savedEntity = entityManagerInterface.persistEntity(user);

			Map dataValue = new HashMap();

			dataValue.put(studyNameAttribute, "study");
			entityManagerInterface.insertData(study, dataValue);
			dataValue.clear();
			dataValue.put(studyNameAttribute, "study1");
			entityManagerInterface.insertData(study, dataValue);

			dataValue.clear();
			List<Long> targetIdList = new ArrayList<Long>();
			targetIdList.add(1L);
			targetIdList.add(2L);

			dataValue.put(userNameAttribute, "rahul");
			dataValue.put(association, targetIdList);

			Long recordId = entityManagerInterface.insertData(savedEntity, dataValue);

			dataValue.clear();
			dataValue.put(userNameAttribute, "vishvesh");
			dataValue.put(association, targetIdList);
			entityManagerInterface.insertData(savedEntity, dataValue);

			dataValue.clear();
			dataValue = entityManagerInterface.getRecordById(savedEntity, recordId);

			List targetRecordIdList = (List) dataValue.get(association);

			System.out.println(dataValue);

			assertEquals(2, targetRecordIdList.size());
			assertEquals(1L, targetRecordIdList.get(0));
			assertEquals(2L, targetRecordIdList.get(1));

		}
		catch (DynamicExtensionsSystemException e)
		{
			fail();
			Logger.out.debug(e.getStackTrace());
		}
		catch (DynamicExtensionsApplicationException e)
		{
			fail();
			Logger.out.debug(e.getStackTrace());
		}
		catch (Exception e)
		{
			e.printStackTrace();
			fail();

			Logger.out.debug(e.getStackTrace());
		}

	}

	/**
	 * This method test for inserting data for a multi select attribute
	 */
	public void testGetRecordByIdForOne2ManyAssociation()
	{

		EntityManagerInterface entityManagerInterface = EntityManager.getInstance();
		DomainObjectFactory factory = DomainObjectFactory.getInstance();

		try
		{

			//          create user 
			EntityInterface user = factory.createEntity();
			AttributeInterface userNameAttribute = factory.createStringAttribute();
			userNameAttribute.setName("user name");
			user.setName("user");
			user.addAbstractAttribute(userNameAttribute);

			//          create study 
			EntityInterface study = factory.createEntity();
			AttributeInterface studyNameAttribute = factory.createStringAttribute();
			studyNameAttribute.setName("study name");
			study.setName("study");
			study.addAbstractAttribute(studyNameAttribute);

			//          Associate user (1)------ >(*)study       
			AssociationInterface association = factory.createAssociation();
			association.setTargetEntity(study);
			association.setAssociationDirection(AssociationDirection.SRC_DESTINATION);
			association.setName("primaryInvestigator");
			association.setSourceRole(getRole(AssociationType.ASSOCIATION, "primaryInvestigator",
					Cardinality.ZERO, Cardinality.ONE));
			association.setTargetRole(getRole(AssociationType.ASSOCIATION, "study",
					Cardinality.ZERO, Cardinality.MANY));

			user.addAbstractAttribute(association);

			EntityInterface savedEntity = entityManagerInterface.persistEntity(user);

			Map dataValue = new HashMap();

			dataValue.put(studyNameAttribute, "study");
			entityManagerInterface.insertData(study, dataValue);

			dataValue.clear();
			dataValue.put(studyNameAttribute, "study1");
			entityManagerInterface.insertData(study, dataValue);

			dataValue.clear();
			dataValue.put(userNameAttribute, "rahul");
			List<Long> targetIdList = new ArrayList<Long>();
			targetIdList.add(1L);
			dataValue.put(association, targetIdList);

			Long recordId = entityManagerInterface.insertData(savedEntity, dataValue);
			dataValue.clear();

			dataValue = entityManagerInterface.getRecordById(savedEntity, recordId);
			List targetRecordIdList = (List) dataValue.get(association);

			System.out.println(dataValue);

			assertEquals(1, targetRecordIdList.size());
			assertEquals(1L, targetRecordIdList.get(0));

		}
		catch (DynamicExtensionsSystemException e)
		{
			fail();
			Logger.out.debug(e.getStackTrace());
		}
		catch (DynamicExtensionsApplicationException e)
		{
			fail();
			Logger.out.debug(e.getStackTrace());
		}
		catch (Exception e)
		{
			e.printStackTrace();
			fail();

			Logger.out.debug(e.getStackTrace());
		}

	}

	/**
	 * This test case is to check the constraint violation in case when the  source cardinality for target is one && maximum cardinality for target is one. 
	 * So in this test case we try to insert data such that the same target entity record is associated with the 
	 * source entity record twice. After the first insertion is successful, when the second insertion takes place
	 * at that time constraint violation should  fail
	 */
	public void testInsertDataForConstraintViolationForOneToOne()
	{

		EntityManagerInterface entityManagerInterface = EntityManager.getInstance();
		DomainObjectFactory factory = DomainObjectFactory.getInstance();
		EntityInterface savedEntity = null;
		try
		{

			//          create user 
			EntityInterface user = factory.createEntity();
			AttributeInterface userNameAttribute = factory.createStringAttribute();
			userNameAttribute.setName("user name");
			user.setName("user");
			user.addAbstractAttribute(userNameAttribute);

			//          create study 
			EntityInterface study = factory.createEntity();
			AttributeInterface studyNameAttribute = factory.createStringAttribute();
			studyNameAttribute.setName("study name");
			study.setName("study");
			study.addAbstractAttribute(studyNameAttribute);

			//          Associate user (1)------ >(*)study       
			AssociationInterface association = factory.createAssociation();
			association.setTargetEntity(study);
			association.setAssociationDirection(AssociationDirection.SRC_DESTINATION);
			association.setName("primaryInvestigator");
			association.setSourceRole(getRole(AssociationType.ASSOCIATION, "primaryInvestigator",
					Cardinality.ZERO, Cardinality.ONE));
			association.setTargetRole(getRole(AssociationType.ASSOCIATION, "study",
					Cardinality.ZERO, Cardinality.ONE));

			user.addAbstractAttribute(association);

			savedEntity = entityManagerInterface.persistEntity(user);

			Map dataValue = new HashMap();
			dataValue.clear();
			dataValue.put(studyNameAttribute, "study1");
			entityManagerInterface.insertData(study, dataValue);

			dataValue.clear();
			dataValue.put(userNameAttribute, "rahul");
			List<Long> targetIdList = new ArrayList<Long>();
			targetIdList.add(1L);
			dataValue.put(association, targetIdList);

			entityManagerInterface.insertData(savedEntity, dataValue);

			ResultSet resultSet = executeQuery("select * from "
					+ savedEntity.getTableProperties().getName());
			resultSet.next();
			assertEquals(1, resultSet.getInt(1));

			entityManagerInterface.insertData(savedEntity, dataValue);

			fail();

		}
		catch (DynamicExtensionsSystemException e)
		{
			fail();
			Logger.out.debug(e.getStackTrace());
		}
		catch (DynamicExtensionsApplicationException e)
		{

			ResultSet resultSet = executeQuery("select * from "
					+ savedEntity.getTableProperties().getName());
			try
			{
				resultSet.next();
				assertEquals(1, resultSet.getInt(1));
			}
			catch (SQLException e1)
			{
				fail();
				e1.printStackTrace();
			}

			Logger.out
					.debug("constraint validation should fail...because max target cardinality is one");
		}
		catch (Exception e)
		{
			e.printStackTrace();
			fail();

			Logger.out.debug(e.getStackTrace());
		}

	}

	/**
	 * This test case is to check the scenario when user adds maximum cardinality less than the minimum cardinality
	 * In such case DE internally corrects these cardinalities by swapping the minimum and maximum cardinalities.
	 */
	public void testInsertDataForInvalidCardinalities()
	{

		EntityManagerInterface entityManagerInterface = EntityManager.getInstance();
		DomainObjectFactory factory = DomainObjectFactory.getInstance();
		EntityInterface savedEntity = null;
		try
		{

			//          create user 
			EntityInterface user = factory.createEntity();
			AttributeInterface userNameAttribute = factory.createStringAttribute();
			userNameAttribute.setName("user name");
			user.setName("user");
			user.addAbstractAttribute(userNameAttribute);

			//          create study 
			EntityInterface study = factory.createEntity();
			AttributeInterface studyNameAttribute = factory.createStringAttribute();
			studyNameAttribute.setName("study name");
			study.setName("study");
			study.addAbstractAttribute(studyNameAttribute);

			//          Associate user (1)------ >(*)study       
			AssociationInterface association = factory.createAssociation();
			association.setTargetEntity(study);
			association.setAssociationDirection(AssociationDirection.SRC_DESTINATION);
			association.setName("primaryInvestigator");
			association.setSourceRole(getRole(AssociationType.ASSOCIATION, "primaryInvestigator",
					Cardinality.MANY, Cardinality.ZERO));
			association.setTargetRole(getRole(AssociationType.ASSOCIATION, "study",
					Cardinality.ONE, Cardinality.ZERO));

			user.addAbstractAttribute(association);
			savedEntity = entityManagerInterface.persistEntity(user);

			Map dataValue = new HashMap();
			dataValue.put(userNameAttribute, "rahul");
			List<Long> targetIdList = new ArrayList<Long>();
			targetIdList.add(1L);

			dataValue.put(association, targetIdList);

			entityManagerInterface.insertData(savedEntity, dataValue);
			ResultSet resultSet = executeQuery("select * from "
					+ savedEntity.getTableProperties().getName());
			resultSet.next();
			assertEquals(1, resultSet.getInt(1));
		}
		catch (Exception e)
		{
			fail();
			Logger.out.debug(e.getStackTrace());
		}

	}

	/**
	 *  Purpose: To test the deletion of record for an entity which has a one to many association with other entity.
	 *  Expected behavior: the entry for this record in the target entity's data table should also be removed.
	 *  Test case flow : 1. create user and study entity. 2. create association User 1--->* Study. 3.Persist entities.
	 *  4.Insert one record 5. Check the record has been inserted or not. 6. Delete the inserted record.  
	 */

	public void testDeleteRecordWithAssociationOneToMany()
	{

		try
		{
			MockEntityManager mock = new MockEntityManager();
			Entity study = (Entity) DomainObjectFactory.getInstance().createEntity();
			study.setName("study");

			Attribute studyName = (Attribute) mock.initializeStringAttribute("studyName",
					"new Study");
			study.addAbstractAttribute(studyName);

			Entity user = (Entity) DomainObjectFactory.getInstance().createEntity();
			user.setName("user");
			Attribute userName = (Attribute) mock.initializeStringAttribute("userName", "new User");
			user.addAbstractAttribute(userName);

			//Associate  User(1) <---->(*)Study 
			AssociationInterface association = DomainObjectFactory.getInstance()
					.createAssociation();

			association.setTargetEntity(study);
			association.setAssociationDirection(AssociationDirection.SRC_DESTINATION);
			association.setName("primaryInvestigator");
			association.setSourceRole(getRole(AssociationType.ASSOCIATION, "primaryInvestigator",
					Cardinality.ONE, Cardinality.ONE));
			association.setTargetRole(getRole(AssociationType.ASSOCIATION, "study",
					Cardinality.ZERO, Cardinality.MANY));

			user.addAbstractAttribute(association);

			EntityManager.getInstance().persistEntity(user);

			Map dataValue = new HashMap();
			List list = new ArrayList();
			list.add(1L);
			dataValue.put(userName, "User1");
			dataValue.put(association, list);

			EntityManager.getInstance().insertData(user, dataValue);
			ResultSet resultSet = executeQuery("select * from "
					+ user.getTableProperties().getName());
			resultSet.next();
			assertEquals(1, resultSet.getInt(1));
			assertEquals("User1", resultSet.getString(2));
			ResultSetMetaData metadata = resultSet.getMetaData();
			assertEquals(2, metadata.getColumnCount());

			resultSet = executeQuery("select count(*) from " + user.getTableProperties().getName());
			resultSet.next();
			assertEquals(1, resultSet.getInt(1));

			EntityManager.getInstance().deleteRecord(user, 1L);
			resultSet = executeQuery("select count(*) from " + user.getTableProperties().getName());
			resultSet.next();
			assertEquals(0, resultSet.getInt(1));
		}
		catch (Exception e)
		{
			//TODO Auto-generated catch block
			Logger.out.debug(e.getMessage());
			e.printStackTrace();
			fail("Exception occured");

		}
	}

	/**
	 *  Purpose: To test the deletion of record for an entity which has a many to many association with other entity.
	 *  Expected behavior: the entry for this record in the target entity's data table should also be removed.
	 *  Test case flow : 1. create user and study entity. 2. create association User *--->* Study. 3.Persist entities.
	 *  4.Insert one record 5. Check the record has been inserted or not. 6. Delete the inserted record. 
	 *  7.Check if the record is deleted. 8. Check if there are no entries for the deleted record in the 
	 *  middle table of the many to many association.
	 */

	public void testDeleteRecordWithAssociationManyToMany()
	{

		try
		{
			MockEntityManager mock = new MockEntityManager();
			Entity study = (Entity) DomainObjectFactory.getInstance().createEntity();
			study.setName("study");

			Attribute studyName = (Attribute) mock.initializeStringAttribute("studyName",
					"new Study");
			study.addAbstractAttribute(studyName);

			Entity user = (Entity) DomainObjectFactory.getInstance().createEntity();
			user.setName("user");
			Attribute userName = (Attribute) mock.initializeStringAttribute("userName", "new User");
			user.addAbstractAttribute(userName);

			//Associate  User(*) <---->(*)Study 
			AssociationInterface association = DomainObjectFactory.getInstance()
					.createAssociation();

			association.setTargetEntity(study);
			association.setAssociationDirection(AssociationDirection.SRC_DESTINATION);
			association.setName("primaryInvestigator");
			association.setSourceRole(getRole(AssociationType.ASSOCIATION, "primaryInvestigator",
					Cardinality.ONE, Cardinality.MANY));
			association.setTargetRole(getRole(AssociationType.ASSOCIATION, "study",
					Cardinality.ZERO, Cardinality.MANY));

			user.addAbstractAttribute(association);

			EntityManager.getInstance().persistEntity(user);

			Map dataValue = new HashMap();
			List list = new ArrayList();
			list.add(1L);
			list.add(2L);
			dataValue.put(userName, "User1");
			dataValue.put(association, list);

			Long recordId = EntityManager.getInstance().insertData(user, dataValue);

			//Checking whether there is an entry added in the data table for user.
			ResultSet resultSet = executeQuery("select * from "
					+ user.getTableProperties().getName());
			resultSet.next();
			assertEquals(1, resultSet.getInt(1));
			//Checking whether the the value of the second column (i.e. the column for the user name.. first column is identifier).
			//is having the expected value or not.
			assertEquals("User1", resultSet.getString(2));
			ResultSetMetaData metadata = resultSet.getMetaData();
			assertEquals(2, metadata.getColumnCount());

			//Checking whether there are 2 entries added in the middle table for the many to many association.
			resultSet = executeQuery("select count(*) from "
					+ association.getConstraintProperties().getName());
			resultSet.next();
			assertEquals(2, resultSet.getInt(1));
			//Deleting the record
			EntityManager.getInstance().deleteRecord(user, 1L);
			//Checking there is no entry for the deleted record in the middle table.
			resultSet = executeQuery("select count(*) from "
					+ association.getConstraintProperties().getName() + " where "
					+ association.getConstraintProperties().getSourceEntityKey() + " = " + recordId);
			resultSet.next();
			assertEquals(0, resultSet.getInt(1));
		}
		catch (Exception e)
		{
			//TODO Auto-generated catch block
			Logger.out.debug(e.getMessage());
			e.printStackTrace();
			fail("Exception occured");

		}
	}

	/**
     *  Purpose: To test the deletion of record for an entity which has a many to one association with other entity.
     *  Expected behavior: the entry for this record in the target entity's data table should also be removed.
     *  Test case flow : 1. create user and study entity. 2. create association User *--->1 Study. 3.Persist entities.
     *  4.Insert one record 5. Check the record has been inserted or not. 6. Delete the inserted record.
     *  7.Check if the record is properly deleted or not.  
     */

    public void testDeleteRecordWithAssociationManyToOne()
    {

        try
        {
        	Variables.databaseName = Constants.ORACLE_DATABASE;
        	
        	MockEntityManager mock = new MockEntityManager();
            Entity study = (Entity) DomainObjectFactory.getInstance().createEntity();
            study.setName("study");
            
            Attribute studyName = (Attribute) mock.initializeStringAttribute("studyName","new Study");
            study.addAbstractAttribute(studyName);
            
            Entity user = (Entity) DomainObjectFactory.getInstance().createEntity();
            user.setName("user");
            Attribute userName = (Attribute) mock.initializeStringAttribute("userName","new User");
            user.addAbstractAttribute(userName);
            
            //Associate  User(*) <---->(1)Study 
    		AssociationInterface association = DomainObjectFactory.getInstance().createAssociation();

    		association.setTargetEntity(study);
    		association.setAssociationDirection(AssociationDirection.SRC_DESTINATION);
    		association.setName("primaryInvestigator");
    		association.setSourceRole(getRole(AssociationType.ASSOCIATION, "primaryInvestigator", Cardinality.ONE, Cardinality.MANY));
    		association.setTargetRole(getRole(AssociationType.ASSOCIATION, "study", Cardinality.ZERO, Cardinality.ONE));

    		user.addAbstractAttribute(association);
    		
    		EntityManager.getInstance().persistEntity(user);
    		
    		Map dataValue = new HashMap();
    		List list = new ArrayList();
    		list.add(1L);
            dataValue.put(userName, "User1");
            dataValue.put(association, list);
            
            Long recordId = EntityManager.getInstance().insertData(user,dataValue);
            
            //Checking whether there is an entry added in the data table for user.
            ResultSet resultSet = executeQuery("select * from "
                    + user.getTableProperties().getName());
            resultSet.next();
            assertEquals(1, resultSet.getInt(1));
            //Checking whether the the value of the second column (i.e. the column for the user name.. first column is identifier).
            //is having the expected value or not.
            assertEquals("User1", resultSet.getString(2));
            //Checking if the extra column for many to one assiciation is added or not
            ResultSetMetaData metadata = resultSet.getMetaData();
            assertEquals(3, metadata.getColumnCount());

            //Deleting the record
            EntityManager.getInstance().deleteRecord(user,1L);
            
            resultSet = executeQuery("select count(*) from "
                    + user.getTableProperties().getName());
            resultSet.next();
            assertEquals(0, resultSet.getInt(1));
        }
        catch (Exception e)
        {
            //TODO Auto-generated catch block
            Logger.out.debug(e.getMessage());
            e.printStackTrace();
            fail("Exception occured");
           
        }
}

	/**
	 * @param targetEntity
	 * @param associationDirection
	 * @param assoName
	 * @param sourceRole
	 * @param targetRole
	 * @return
	 */
	private AssociationInterface getAssociation(EntityInterface targetEntity,
			AssociationDirection associationDirection, String assoName, RoleInterface sourceRole,
			RoleInterface targetRole)
	{
		AssociationInterface association = DomainObjectFactory.getInstance().createAssociation();
		association.setTargetEntity(targetEntity);
		association.setAssociationDirection(associationDirection);
		association.setName(assoName);
		association.setSourceRole(sourceRole);
		association.setTargetRole(targetRole);
		return association;
	}
	
	/**
	 * This test case test for associating three entities with  many to one to one 
	 * 
	 * User(*) ---- >(1)Study(1) ------>(1)institute
	 */
	public void testCreateEntityWithCascadeManyToOneAssociation()
	{
		EntityManagerInterface entityManager = EntityManager.getInstance();
		DomainObjectFactory factory = DomainObjectFactory.getInstance();

		// create user 
		EntityInterface user = factory.createEntity();
		AttributeInterface userNameAttribute = factory.createStringAttribute();
		userNameAttribute.setName("user name");
		user.setName("user");
		user.addAbstractAttribute(userNameAttribute);

		// create study 
		EntityInterface study = factory.createEntity();
		AttributeInterface studyNameAttribute = factory.createStringAttribute();
		studyNameAttribute.setName("study name");
		study.setName("study");
		study.addAbstractAttribute(studyNameAttribute);

		// create institution
		EntityInterface institution = factory.createEntity();
		AttributeInterface institutionNameAttribute = factory.createStringAttribute();
		institutionNameAttribute.setName("institution name");
		institution.setName("institution");
		institution.addAbstractAttribute(institutionNameAttribute);

		// Associate user (*)------ >(1)study       
		AssociationInterface association = factory.createAssociation();

		association.setTargetEntity(study);
		association.setAssociationDirection(AssociationDirection.SRC_DESTINATION);
		association.setName("primaryInvestigator");
		association.setSourceRole(getRole(AssociationType.ASSOCIATION, "primaryInvestigator",
				Cardinality.ZERO, Cardinality.MANY));
		association.setTargetRole(getRole(AssociationType.ASSOCIATION, "study", Cardinality.ZERO,
				Cardinality.ONE));

		user.addAbstractAttribute(association);

		// Associate study(1) ------> (1) institution       
		AssociationInterface studInstAssociation = factory.createAssociation();

		studInstAssociation.setTargetEntity(institution);
		studInstAssociation.setAssociationDirection(AssociationDirection.SRC_DESTINATION);
		studInstAssociation.setName("studyLocation");
		studInstAssociation.setSourceRole(getRole(AssociationType.ASSOCIATION, "studyPerformed",
				Cardinality.ZERO, Cardinality.ONE));
		studInstAssociation.setTargetRole(getRole(AssociationType.ASSOCIATION, "studyLocation",
				Cardinality.ZERO, Cardinality.ONE));

		study.addAbstractAttribute(studInstAssociation);

		try
		{
			//entityManager.createEntity(study);

			EntityInterface savedUser = entityManager.persistEntity(user);

			ResultSetMetaData metaData = executeQueryForMetadata("select * from "
					+ user.getTableProperties().getName());
			assertEquals(3, metaData.getColumnCount());

			metaData = executeQueryForMetadata("select * from "
					+ study.getTableProperties().getName());
			assertEquals(2, metaData.getColumnCount());

			metaData = executeQueryForMetadata("select * from "
					+ institution.getTableProperties().getName());
			assertEquals(3, metaData.getColumnCount());

		}
		catch (DynamicExtensionsSystemException e)
		{
			e.printStackTrace();
			fail();
		}

		catch (DynamicExtensionsApplicationException e)
		{
			e.printStackTrace();
			fail();

		}
		catch (SQLException e)
		{
			e.printStackTrace();
			fail();

		}

	}

	/**
	 * This test case test for associating three entities with  many to one to many to many
	 * 
	 *        User(*) ---- >(1)Study(1) ------>(1)institute(*)-- (*)User
	 *        
	 */
	public void testCreateEntityWithCyclicCascadeManyToOneAssociation()
	{
		EntityManagerInterface entityManager = EntityManager.getInstance();
		DomainObjectFactory factory = DomainObjectFactory.getInstance();

		// create user 
		EntityInterface user = factory.createEntity();
		AttributeInterface userNameAttribute = factory.createStringAttribute();
		userNameAttribute.setName("user name");
		user.setName("user");
		user.addAbstractAttribute(userNameAttribute);

		// create study 
		EntityInterface study = factory.createEntity();
		AttributeInterface studyNameAttribute = factory.createStringAttribute();
		studyNameAttribute.setName("study name");
		study.setName("study");
		study.addAbstractAttribute(studyNameAttribute);

		// create institution
		EntityInterface institution = factory.createEntity();
		AttributeInterface institutionNameAttribute = factory.createStringAttribute();
		institutionNameAttribute.setName("institution name");
		institution.setName("institution");
		institution.addAbstractAttribute(institutionNameAttribute);

		// Associate user (*)------ >(1)study       
		AssociationInterface association = factory.createAssociation();

		association.setTargetEntity(study);
		association.setAssociationDirection(AssociationDirection.SRC_DESTINATION);
		association.setName("primaryInvestigator");
		association.setSourceRole(getRole(AssociationType.ASSOCIATION, "primaryInvestigator",
				Cardinality.ZERO, Cardinality.MANY));
		association.setTargetRole(getRole(AssociationType.ASSOCIATION, "study", Cardinality.ZERO,
				Cardinality.ONE));

		user.addAbstractAttribute(association);

		// Associate study(1) ------> (1) institution       
		AssociationInterface studInstAssociation = factory.createAssociation();

		studInstAssociation.setTargetEntity(institution);
		studInstAssociation.setAssociationDirection(AssociationDirection.SRC_DESTINATION);
		studInstAssociation.setName("studyLocation");
		studInstAssociation.setSourceRole(getRole(AssociationType.ASSOCIATION, "studyPerformed",
				Cardinality.ZERO, Cardinality.ONE));
		studInstAssociation.setTargetRole(getRole(AssociationType.ASSOCIATION, "studyLocation",
				Cardinality.ZERO, Cardinality.ONE));

		study.addAbstractAttribute(studInstAssociation);

		// Associate institution (*)----->(*) user      
		AssociationInterface instUserAssociation = factory.createAssociation();

		instUserAssociation.setTargetEntity(user);
		instUserAssociation.setAssociationDirection(AssociationDirection.SRC_DESTINATION);
		instUserAssociation.setName("lecturers");
		instUserAssociation.setSourceRole(getRole(AssociationType.ASSOCIATION, "university",
				Cardinality.ONE, Cardinality.MANY));
		instUserAssociation.setTargetRole(getRole(AssociationType.ASSOCIATION, "lecturer",
				Cardinality.ONE, Cardinality.MANY));

		institution.addAbstractAttribute(instUserAssociation);

		try
		{

			EntityInterface savedUser = entityManager.persistEntity(user);

			ResultSetMetaData metaData = executeQueryForMetadata("select * from "
					+ user.getTableProperties().getName());
			assertEquals(3, metaData.getColumnCount());

			metaData = executeQueryForMetadata("select * from "
					+ study.getTableProperties().getName());
			assertEquals(2, metaData.getColumnCount());

			metaData = executeQueryForMetadata("select * from "
					+ institution.getTableProperties().getName());
			assertEquals(3, metaData.getColumnCount());

			assertNotNull(instUserAssociation.getConstraintProperties().getName());

			metaData = executeQueryForMetadata("select * from "
					+ instUserAssociation.getConstraintProperties().getName());
			assertEquals(3, metaData.getColumnCount());

		}
		catch (DynamicExtensionsSystemException e)
		{
			e.printStackTrace();
			fail();
		}

		catch (DynamicExtensionsApplicationException e)
		{
			e.printStackTrace();
			fail();

		}
		catch (SQLException e)
		{
			e.printStackTrace();
			fail();

		}

	}

	/**
	 * Purpose is to test the self referencing of the entity. 
	 * Scenario - user(*)------>(1)User
	 *                   creator   
	 */
	public void testCreateEntityWithSelfReferencingManyToManyAssociation()
	{
		EntityManagerInterface entityManager = EntityManager.getInstance();
		DomainObjectFactory factory = DomainObjectFactory.getInstance();

		// create user 
		EntityInterface user = factory.createEntity();
		AttributeInterface userNameAttribute = factory.createStringAttribute();
		userNameAttribute.setName("user name");
		user.setName("user");
		user.addAbstractAttribute(userNameAttribute);

		// Associate user (*)------ >(1)user       
		AssociationInterface association = factory.createAssociation();

		association.setTargetEntity(user);
		association.setAssociationDirection(AssociationDirection.SRC_DESTINATION);
		association.setName("primaryInvestigator");
		association.setSourceRole(getRole(AssociationType.ASSOCIATION, "subpi", Cardinality.ONE,
				Cardinality.MANY));
		association.setTargetRole(getRole(AssociationType.ASSOCIATION, "pi", Cardinality.ONE,
				Cardinality.MANY));

		user.addAbstractAttribute(association);

		try
		{

			EntityInterface savedUser = entityManager.persistEntity(user);

			String tableName = user.getTableProperties().getName();

			assertNotNull(tableName);

			ResultSetMetaData metaData = executeQueryForMetadata("select * from " + tableName);
			assertEquals(2, metaData.getColumnCount());

			metaData = executeQueryForMetadata("select * from "
					+ association.getConstraintProperties().getName());
			assertEquals(3, metaData.getColumnCount());
		}
		catch (DynamicExtensionsSystemException e)
		{
			e.printStackTrace();
			fail();
		}

		catch (DynamicExtensionsApplicationException e)
		{
			e.printStackTrace();
			fail();

		}
		catch (SQLException e)
		{
			e.printStackTrace();
			fail();

		}

	}

	/**
	 * Purpose is to test the multiple self referencing of the entity. 
	 * Scenario - 
	 * user(*)------>(*)User
	 *       childUsers
	 * user(*)------>(1)User
	 *        creator   
	 */
	public void testCreateEntityWithSelfReferencingMultipleAssociations()
	{
		EntityManagerInterface entityManager = EntityManager.getInstance();
		DomainObjectFactory factory = DomainObjectFactory.getInstance();

		// create user 
		EntityInterface user = factory.createEntity();
		AttributeInterface userNameAttribute = factory.createStringAttribute();
		userNameAttribute.setName("user name");
		user.setName("user");
		user.addAbstractAttribute(userNameAttribute);

		// Associate user (*)------ >(*)user       
		AssociationInterface association = factory.createAssociation();

		association.setTargetEntity(user);
		association.setAssociationDirection(AssociationDirection.SRC_DESTINATION);
		association.setName("children");
		association.setSourceRole(getRole(AssociationType.ASSOCIATION, "subpi", Cardinality.ONE,
				Cardinality.MANY));
		association.setTargetRole(getRole(AssociationType.ASSOCIATION, "pi", Cardinality.ONE,
				Cardinality.MANY));

		user.addAbstractAttribute(association);

		// Associate user (*)------ >(1)user       
		AssociationInterface creatorAssociation = factory.createAssociation();

		creatorAssociation.setTargetEntity(user);
		creatorAssociation.setAssociationDirection(AssociationDirection.SRC_DESTINATION);
		creatorAssociation.setName("creator");
		creatorAssociation.setSourceRole(getRole(AssociationType.ASSOCIATION, "user",
				Cardinality.ONE, Cardinality.MANY));
		creatorAssociation.setTargetRole(getRole(AssociationType.ASSOCIATION, "creator",
				Cardinality.ONE, Cardinality.ONE));

		user.addAbstractAttribute(creatorAssociation);

		try
		{

			EntityInterface savedUser = entityManager.persistEntity(user);

			String tableName = user.getTableProperties().getName();

			assertNotNull(tableName);

			ResultSetMetaData metaData = executeQueryForMetadata("select * from " + tableName);
			assertEquals(3, metaData.getColumnCount());

			metaData = executeQueryForMetadata("select * from "
					+ association.getConstraintProperties().getName());
			assertEquals(3, metaData.getColumnCount());
		}
		catch (DynamicExtensionsSystemException e)
		{
			e.printStackTrace();
			fail();
		}

		catch (DynamicExtensionsApplicationException e)
		{
			e.printStackTrace();
			fail();

		}
		catch (SQLException e)
		{
			e.printStackTrace();
			fail();

		}

	}

	/**
	 * Purpose is to test the multiple self referencing of the entity. 
	 * Scenario - 
	 * user(*)------>(*)User
	 *       childUsers
	 * user(*)------>(*)User
	 *        creators  
	 */
	public void testCreateEntityWithSelfReferencingMultipleManyToManyAssociations()
	{
		EntityManagerInterface entityManager = EntityManager.getInstance();
		DomainObjectFactory factory = DomainObjectFactory.getInstance();

		// create user 
		EntityInterface user = factory.createEntity();
		AttributeInterface userNameAttribute = factory.createStringAttribute();
		userNameAttribute.setName("user name");
		user.setName("user");
		user.addAbstractAttribute(userNameAttribute);

		// Associate user (*)------ >(*)user       
		AssociationInterface association = factory.createAssociation();

		association.setTargetEntity(user);
		association.setAssociationDirection(AssociationDirection.SRC_DESTINATION);
		association.setName("children");
		association.setSourceRole(getRole(AssociationType.ASSOCIATION, "subpi", Cardinality.ONE,
				Cardinality.MANY));
		association.setTargetRole(getRole(AssociationType.ASSOCIATION, "pi", Cardinality.ONE,
				Cardinality.MANY));

		user.addAbstractAttribute(association);

		// Associate user (*)------ >(*)user       
		AssociationInterface creatorAssociation = factory.createAssociation();

		creatorAssociation.setTargetEntity(user);
		creatorAssociation.setAssociationDirection(AssociationDirection.SRC_DESTINATION);
		creatorAssociation.setName("parentUSers");
		creatorAssociation.setSourceRole(getRole(AssociationType.ASSOCIATION, "user",
				Cardinality.ONE, Cardinality.MANY));
		creatorAssociation.setTargetRole(getRole(AssociationType.ASSOCIATION, "creator",
				Cardinality.ONE, Cardinality.MANY));

		user.addAbstractAttribute(creatorAssociation);

		try
		{

			EntityInterface savedUser = entityManager.persistEntity(user);

			String tableName = user.getTableProperties().getName();

			assertNotNull(tableName);

			ResultSetMetaData metaData = executeQueryForMetadata("select * from " + tableName);
			assertEquals(2, metaData.getColumnCount());

			metaData = executeQueryForMetadata("select * from "
					+ association.getConstraintProperties().getName());
			assertEquals(3, metaData.getColumnCount());

			metaData = executeQueryForMetadata("select * from "
					+ creatorAssociation.getConstraintProperties().getName());
			assertEquals(3, metaData.getColumnCount());

		}
		catch (DynamicExtensionsSystemException e)
		{
			e.printStackTrace();
			fail();
		}

		catch (DynamicExtensionsApplicationException e)
		{
			e.printStackTrace();
			fail();

		}
		catch (SQLException e)
		{
			e.printStackTrace();
			fail();

		}

	}

	/**
	 * This test case test for associating two entities with one to many association  and bidirectional
	 * It tests for internal system generated association for the bidirectional.
	 * User(1) <---->(*)Study
	 */
	public void testCreateEntityBidirectionalOneToManyAssociation()
	{
		EntityManagerInterface entityManager = EntityManager.getInstance();
		DomainObjectFactory factory = DomainObjectFactory.getInstance();

		// create user 
		EntityInterface user = factory.createEntity();
		AttributeInterface userNameAttribute = factory.createStringAttribute();
		userNameAttribute.setName("user name");
		user.setName("user");
		user.addAbstractAttribute(userNameAttribute);

		// create study 
		EntityInterface study = factory.createEntity();
		study.setName("Study");

		// Associate  User(1) <---->(*)Study 
		AssociationInterface association = factory.createAssociation();

		association.setTargetEntity(study);
		association.setAssociationDirection(AssociationDirection.BI_DIRECTIONAL);
		association.setName("primaryInvestigator");
		association.setSourceRole(getRole(AssociationType.ASSOCIATION, "primaryInvestigator",
				Cardinality.ONE, Cardinality.ONE));
		association.setTargetRole(getRole(AssociationType.ASSOCIATION, "study", Cardinality.ZERO,
				Cardinality.MANY));

		user.addAbstractAttribute(association);

		try
		{

			EntityInterface savedUser = entityManager.persistEntity(user);

			ResultSetMetaData metaData = executeQueryForMetadata("select * from "
					+ study.getTableProperties().getName());
			assertEquals(2, metaData.getColumnCount());

			assertEquals(1, study.getAbstractAttributeCollection().size());

			Association systemGeneratedAssociation = (Association) study
					.getAbstractAttributeCollection().toArray()[0];

			assertTrue(systemGeneratedAssociation.getIsSystemGenerated());

			assertEquals(user, systemGeneratedAssociation.getTargetEntity());

		}
		catch (DynamicExtensionsSystemException e)
		{
			e.printStackTrace();
			fail();
		}

		catch (DynamicExtensionsApplicationException e)
		{
			e.printStackTrace();
			fail();

		}
		catch (SQLException e)
		{
			e.printStackTrace();
			fail();

		}

	}

	/**
	 * 
	 *
	 */
	public void testGetAssociation()
	{
		try
		{
			EntityManagerInterface entityManager = EntityManager.getInstance();
			DomainObjectFactory factory = DomainObjectFactory.getInstance();

			// create user 
			EntityInterface user = factory.createEntity();
			AttributeInterface userNameAttribute = factory.createStringAttribute();
			userNameAttribute.setName("user name");
			user.setName("user");
			user.addAbstractAttribute(userNameAttribute);

			// create study 
			EntityInterface study = factory.createEntity();
			AttributeInterface studyNameAttribute = factory.createStringAttribute();
			studyNameAttribute.setName("study name");
			study.setName("study");
			study.addAbstractAttribute(studyNameAttribute);

			// create institution
			EntityInterface institution = factory.createEntity();
			AttributeInterface institutionNameAttribute = factory.createStringAttribute();
			institutionNameAttribute.setName("institution name");
			institution.setName("institution");
			institution.addAbstractAttribute(institutionNameAttribute);

			// Associate user (*)------ >(1)study       
			AssociationInterface association = factory.createAssociation();

			association.setTargetEntity(study);
			association.setAssociationDirection(AssociationDirection.SRC_DESTINATION);
			association.setName("primaryInvestigator");
			association.setSourceRole(getRole(AssociationType.ASSOCIATION, "primaryInvestigator",
					Cardinality.ZERO, Cardinality.MANY));
			association.setTargetRole(getRole(AssociationType.ASSOCIATION, "study",
					Cardinality.ZERO, Cardinality.ONE));

			user.addAbstractAttribute(association);

			// Associate study(1) ------> (1) institution       
			AssociationInterface studInstAssociation = factory.createAssociation();

			studInstAssociation.setTargetEntity(institution);
			studInstAssociation.setAssociationDirection(AssociationDirection.SRC_DESTINATION);
			studInstAssociation.setName("studyLocation");
			studInstAssociation.setSourceRole(getRole(AssociationType.ASSOCIATION,
					"studyPerformed", Cardinality.ZERO, Cardinality.ONE));
			studInstAssociation.setTargetRole(getRole(AssociationType.ASSOCIATION, "studyLocation",
					Cardinality.ZERO, Cardinality.ONE));

			study.addAbstractAttribute(studInstAssociation);

			user = (Entity) entityManager.persistEntity(user);
			AssociationInterface associationInterface = (AssociationInterface) entityManager
					.getAssociation("user", "primaryInvestigator");
			assertTrue(associationInterface != null);

		}

		catch (Exception e)
		{
			//TODO Auto-generated catch block
			e.printStackTrace();
			Logger.out.debug(e.getMessage());
			fail("Exception occured");
		}

	}

	public void testGetAssociations()
	{
		EntityManagerInterface entityManager = EntityManager.getInstance();
		DomainObjectFactory factory = DomainObjectFactory.getInstance();

		// create user 
		EntityInterface user = factory.createEntity();
		AttributeInterface userNameAttribute = factory.createStringAttribute();
		userNameAttribute.setName("user name");
		user.setName("user");
		user.addAbstractAttribute(userNameAttribute);

		// create study 
		EntityInterface study = factory.createEntity();
		study.setName("study name");

		// Associate  User(1) <---->(*)Study 
		AssociationInterface association = factory.createAssociation();

		association.setTargetEntity(study);
		association.setAssociationDirection(AssociationDirection.BI_DIRECTIONAL);
		association.setName("primaryInvestigator");
		association.setSourceRole(getRole(AssociationType.ASSOCIATION, "primaryInvestigator",
				Cardinality.ONE, Cardinality.ONE));
		association.setTargetRole(getRole(AssociationType.ASSOCIATION, "study", Cardinality.ZERO,
				Cardinality.MANY));

		user.addAbstractAttribute(association);

		try
		{
			EntityInterface savedUser = entityManager.persistEntity(user);
			Collection associationCollection = entityManager.getAssociations(savedUser.getId(),
					study.getId());
			assertTrue(associationCollection != null && associationCollection.size() > 0);

		}
		catch (DynamicExtensionsSystemException e)
		{
			e.printStackTrace();
			fail();
		}
		catch (DynamicExtensionsApplicationException e)
		{
			e.printStackTrace();
			fail();
		}

	}

	/**
	 * Purpose is to test the self referencing of the entity. 
	 * Scenario - user(*)------>(1)User
	 *                   creator   
	 */
	public void testCreateEntityWithSelfReferencingBidirectionManyToManyAssociation()
	{
		EntityManagerInterface entityManager = EntityManager.getInstance();
		DomainObjectFactory factory = DomainObjectFactory.getInstance();

		// create user 
		EntityInterface user = factory.createEntity();
		AttributeInterface userNameAttribute = factory.createStringAttribute();
		userNameAttribute.setName("user name");
		user.setName("user");
		user.addAbstractAttribute(userNameAttribute);

		// Associate user (*)------ >(1)user       
		AssociationInterface association = factory.createAssociation();

		association.setTargetEntity(user);
		association.setAssociationDirection(AssociationDirection.BI_DIRECTIONAL);
		association.setName("primaryInvestigator");
		association.setSourceRole(getRole(AssociationType.ASSOCIATION, "subpi", Cardinality.ONE,
				Cardinality.MANY));
		association.setTargetRole(getRole(AssociationType.ASSOCIATION, "pi", Cardinality.ONE,
				Cardinality.MANY));

		user.addAbstractAttribute(association);

		try
		{

			EntityInterface savedUser = entityManager.persistEntity(user);

			String tableName = user.getTableProperties().getName();

			assertNotNull(tableName);

			ResultSetMetaData metaData = executeQueryForMetadata("select * from " + tableName);
			assertEquals(2, metaData.getColumnCount());

			metaData = executeQueryForMetadata("select * from "
					+ association.getConstraintProperties().getName());
			assertEquals(3, metaData.getColumnCount());
		}
		catch (DynamicExtensionsSystemException e)
		{
			e.printStackTrace();
			fail();
		}

		catch (DynamicExtensionsApplicationException e)
		{
			e.printStackTrace();
			fail();

		}
		catch (SQLException e)
		{
			e.printStackTrace();
			fail();

		}

	}

	/**
	 * This test case test for adding a new associatiion bet 2 entities 
	 * 
	 * for oracle it should throw exception.
	 * for mysql  it works.  
	 */
	public void testAddAssociationAfterSave()
	{
		EntityManagerInterface entityManager = EntityManager.getInstance();
		DomainObjectFactory factory = DomainObjectFactory.getInstance();

		// create user 
		EntityInterface user = factory.createEntity();
		AttributeInterface userNameAttribute = factory.createStringAttribute();
		userNameAttribute.setName("user name");
		user.setName("user");
		user.addAbstractAttribute(userNameAttribute);

		try
		{
			user = entityManager.persistEntity(user);

			// create study 
			EntityInterface study = factory.createEntity();
			AttributeInterface studyNameAttribute = factory.createStringAttribute();
			studyNameAttribute.setName("study name");
			study.setName("study");
			study.addAbstractAttribute(studyNameAttribute);

			// Associate user (1)------ >(*)study       
			AssociationInterface association = factory.createAssociation();

			association.setTargetEntity(study);
			association.setAssociationDirection(AssociationDirection.SRC_DESTINATION);
			association.setName("primaryInvestigator");
			association.setSourceRole(getRole(AssociationType.ASSOCIATION, "primaryInvestigator",
					Cardinality.ZERO, Cardinality.MANY));
			association.setTargetRole(getRole(AssociationType.ASSOCIATION, "study",
					Cardinality.ZERO, Cardinality.ONE));

			user.addAbstractAttribute(association);

			EntityInterface savedUser = entityManager.persistEntity(user);

			ResultSetMetaData metaData = executeQueryForMetadata("select * from "
					+ user.getTableProperties().getName());
			assertEquals(3, metaData.getColumnCount());

			//			 Associate user (1) <------>(*)study       
			AssociationInterface association1 = factory.createAssociation();

			association1.setTargetEntity(study);
			association1.setAssociationDirection(AssociationDirection.BI_DIRECTIONAL);
			association1.setName("primaryInvestigator");
			association1.setSourceRole(getRole(AssociationType.ASSOCIATION, "association1",
					Cardinality.ZERO, Cardinality.ONE));
			association1.setTargetRole(getRole(AssociationType.ASSOCIATION, "association1",
					Cardinality.ZERO, Cardinality.MANY));

			savedUser.addAbstractAttribute(association1);

			savedUser = entityManager.persistEntity(savedUser);

			metaData = executeQueryForMetadata("select * from "
					+ study.getTableProperties().getName());
			assertEquals(3, metaData.getColumnCount());
		}
		catch (DynamicExtensionsSystemException e)
		{
			e.printStackTrace();
			fail();
		}

		catch (DynamicExtensionsApplicationException e)
		{
			e.printStackTrace();
			fail();

		}
		catch (SQLException e)
		{
			e.printStackTrace();
			fail();

		}

	}

	/**
	 * This test case test for removing an association bet 2 existing entities 
	 */
	public void testRemoveAssociationAfterSave()
	{
		EntityManagerInterface entityManager = EntityManager.getInstance();
		DomainObjectFactory factory = DomainObjectFactory.getInstance();

		// create user 
		EntityInterface user = factory.createEntity();
		AttributeInterface userNameAttribute = factory.createStringAttribute();
		userNameAttribute.setName("user name");
		user.setName("user");
		user.addAbstractAttribute(userNameAttribute);

		try
		{
			user = entityManager.persistEntity(user);

			// create study 
			EntityInterface study = factory.createEntity();
			AttributeInterface studyNameAttribute = factory.createStringAttribute();
			studyNameAttribute.setName("study name");
			study.setName("study");
			study.addAbstractAttribute(studyNameAttribute);

			// Associate user (1)------ >(*)study   
			RoleInterface sourceRole = getRole(AssociationType.ASSOCIATION, "association1",
					Cardinality.ZERO, Cardinality.MANY);
			RoleInterface targetRole = getRole(AssociationType.ASSOCIATION, "association1",
					Cardinality.ZERO, Cardinality.ONE);
			AssociationInterface association = getAssociation(user,
					AssociationDirection.SRC_DESTINATION, "prim", sourceRole, targetRole);

			user.addAbstractAttribute(association);
			user = entityManager.persistEntity(user);
			ResultSetMetaData metaData = executeQueryForMetadata("select * from "
					+ user.getTableProperties().getName());
			assertEquals(3, metaData.getColumnCount());

			user.removeAssociation(association);
			user = entityManager.persistEntity(user);

			metaData = executeQueryForMetadata("select * from "
					+ user.getTableProperties().getName());
			assertEquals(2, metaData.getColumnCount());

			sourceRole = getRole(AssociationType.ASSOCIATION, "association1", Cardinality.ZERO,
					Cardinality.MANY);
			targetRole = getRole(AssociationType.ASSOCIATION, "association1", Cardinality.ZERO,
					Cardinality.MANY);
			association = getAssociation(user, AssociationDirection.SRC_DESTINATION, "prim",
					sourceRole, targetRole);

			user.addAbstractAttribute(association);
			user = entityManager.persistEntity(user);

			assertTrue(isTablePresent(association.getConstraintProperties().getName()));
			metaData = executeQueryForMetadata("select * from "
					+ association.getConstraintProperties().getName());
			assertEquals(3, metaData.getColumnCount());

			user.removeAssociation(association);
			user = entityManager.persistEntity(user);

			assertFalse(isTablePresent(association.getConstraintProperties().getName()));
		}
		catch (DynamicExtensionsSystemException e)
		{
			e.printStackTrace();
			fail();
		}

		catch (DynamicExtensionsApplicationException e)
		{
			e.printStackTrace();
			fail();

		}
		catch (SQLException e)
		{
			e.printStackTrace();
			fail();

		}

	}

	/**
	 * This test case test for removing an association bet 2 existing entities
	 * Before- SRC-DESTINATION
	 * After - BIDIRECTIONAL 
	 */
	public void testEditAssociationDirection1AfterSave()
	{
		EntityManagerInterface entityManager = EntityManager.getInstance();
		DomainObjectFactory factory = DomainObjectFactory.getInstance();

		// create user 
		EntityInterface user = factory.createEntity();
		AttributeInterface userNameAttribute = factory.createStringAttribute();
		userNameAttribute.setName("user name");
		user.setName("user");
		user.addAbstractAttribute(userNameAttribute);

		// create study 
		EntityInterface study = factory.createEntity();
		AttributeInterface studyNameAttribute = factory.createStringAttribute();
		studyNameAttribute.setName("study name");
		study.setName("study");
		study.addAbstractAttribute(studyNameAttribute);

		// Associate user (1)------ >(*)study   
		RoleInterface sourceRole = getRole(AssociationType.ASSOCIATION, "association1",
				Cardinality.ZERO, Cardinality.MANY);
		RoleInterface targetRole = getRole(AssociationType.ASSOCIATION, "association1",
				Cardinality.ZERO, Cardinality.ONE);
		AssociationInterface association = getAssociation(study,
				AssociationDirection.SRC_DESTINATION, "prim", sourceRole, targetRole);

		user.addAbstractAttribute(association);

		try
		{
			user = entityManager.persistEntity(user);

			ResultSetMetaData metaData = executeQueryForMetadata("select * from "
					+ user.getTableProperties().getName());
			assertEquals(3, metaData.getColumnCount());
			metaData = executeQueryForMetadata("select * from "
					+ study.getTableProperties().getName());
			assertEquals(2, metaData.getColumnCount());

			association.getSourceRole().setMaximumCardinality(Cardinality.ONE);
			association.getTargetRole().setMaximumCardinality(Cardinality.MANY);
			association.setAssociationDirection(AssociationDirection.BI_DIRECTIONAL);

			user = entityManager.persistEntity(user);

			metaData = executeQueryForMetadata("select * from "
					+ user.getTableProperties().getName());
			assertEquals(2, metaData.getColumnCount());
			metaData = executeQueryForMetadata("select * from "
					+ study.getTableProperties().getName());
			assertEquals(3, metaData.getColumnCount());

			EntityInterface savedUser = entityManager
					.getEntityByIdentifier(user.getId().toString());
			assertEquals(1, savedUser.getAssociationCollection().size());

			EntityInterface savedStudy = entityManager.getEntityByIdentifier(study.getId()
					.toString());
			assertEquals(1, savedStudy.getAssociationCollection().size());
		}
		catch (DynamicExtensionsSystemException e)
		{
			e.printStackTrace();
			fail();
		}

		catch (DynamicExtensionsApplicationException e)
		{
			e.printStackTrace();
			fail();

		}
		catch (SQLException e)
		{
			e.printStackTrace();
			fail();

		}

	}

	/**
	 * This test case test for removing an association bet 2 existing entities
	 * Before-  BIDIRECTIONAL
	 * After -  SRC-DESTINATION
	 * system generated association should get removed.
	 */
	public void testEditAssociationDirectionAfterSave()
	{
		EntityManagerInterface entityManager = EntityManager.getInstance();
		DomainObjectFactory factory = DomainObjectFactory.getInstance();

		// create user 
		EntityInterface user = factory.createEntity();
		AttributeInterface userNameAttribute = factory.createStringAttribute();
		userNameAttribute.setName("user name");
		user.setName("user");
		user.addAbstractAttribute(userNameAttribute);

		// create study 
		EntityInterface study = factory.createEntity();
		AttributeInterface studyNameAttribute = factory.createStringAttribute();
		studyNameAttribute.setName("study name");
		study.setName("study");
		study.addAbstractAttribute(studyNameAttribute);

		// Associate user (1)------ >(*)study   
		RoleInterface sourceRole = getRole(AssociationType.ASSOCIATION, "association1",
				Cardinality.ZERO, Cardinality.MANY);
		RoleInterface targetRole = getRole(AssociationType.ASSOCIATION, "association1",
				Cardinality.ZERO, Cardinality.ONE);
		AssociationInterface association = getAssociation(study,
				AssociationDirection.BI_DIRECTIONAL, "prim", sourceRole, targetRole);

		user.addAbstractAttribute(association);

		try
		{
			user = entityManager.persistEntity(user);

			ResultSetMetaData metaData = executeQueryForMetadata("select * from "
					+ user.getTableProperties().getName());
			assertEquals(3, metaData.getColumnCount());
			metaData = executeQueryForMetadata("select * from "
					+ study.getTableProperties().getName());
			assertEquals(2, metaData.getColumnCount());

			association.getSourceRole().setMaximumCardinality(Cardinality.ONE);
			association.getTargetRole().setMaximumCardinality(Cardinality.MANY);
			association.setAssociationDirection(AssociationDirection.SRC_DESTINATION);

			user = entityManager.persistEntity(user);

			metaData = executeQueryForMetadata("select * from "
					+ user.getTableProperties().getName());
			assertEquals(2, metaData.getColumnCount());
			metaData = executeQueryForMetadata("select * from "
					+ study.getTableProperties().getName());
			assertEquals(3, metaData.getColumnCount());

			EntityInterface savedUser = entityManager
					.getEntityByIdentifier(user.getId().toString());
			assertEquals(1, savedUser.getAssociationCollection().size());

			EntityInterface savedStudy = entityManager.getEntityByIdentifier(study.getId()
					.toString());
			assertEquals(0, savedStudy.getAssociationCollection().size());
		}
		catch (DynamicExtensionsSystemException e)
		{
			e.printStackTrace();
			fail();
		}

		catch (DynamicExtensionsApplicationException e)
		{
			e.printStackTrace();
			fail();

		}
		catch (SQLException e)
		{
			e.printStackTrace();
			fail();

		}

	}


	/**
	 *  Purpose: This method test for inserting data for a containtment relationship between two entities
	 *  Expected Behaviour: Data should be persisted for the target entity in its own table and that record should 
	 *                      get associated with the source entity's record.
	 *  Test case flow: 1. create User
	 *                  2. Create Address                       
	 *                  3. Add Association with      User(1) ------->(1) Address containtment association
	 *                  4. persist entities.
	 *                  5. Insert Data
	 *                  6. Check for it.
	 */
	public void testInsertDataForContaintment()
	{

		EntityManagerInterface entityManagerInterface = EntityManager.getInstance();
		DomainObjectFactory factory = DomainObjectFactory.getInstance();

		try
		{

			// Step 1  
			EntityInterface user = factory.createEntity();
			AttributeInterface userNameAttribute = factory.createStringAttribute();
			userNameAttribute.setName("user name");
			user.setName("user");
			user.addAbstractAttribute(userNameAttribute);

			
			
			// Step 2  
			EntityInterface address = factory.createEntity();
			address.setName("address");

			AttributeInterface streetAttribute = factory.createStringAttribute();
			streetAttribute.setName("street name");
			address.addAbstractAttribute(streetAttribute);

			AttributeInterface cityAttribute = factory.createStringAttribute();
			cityAttribute.setName("city name");
			address.addAbstractAttribute(cityAttribute);
			
			
			// Step 3
			AssociationInterface association = factory.createAssociation();
			association.setTargetEntity(address);
			association.setAssociationDirection(AssociationDirection.SRC_DESTINATION);
			association.setName("UserAddress");
			association.setSourceRole(getRole(AssociationType.CONTAINTMENT, "User",
					Cardinality.ZERO, Cardinality.ONE));
			association.setTargetRole(getRole(AssociationType.ASSOCIATION, "address",
					Cardinality.ZERO, Cardinality.ONE));

			user.addAbstractAttribute(association);

			// Step 4
			EntityInterface savedEntity = entityManagerInterface.persistEntity(user);

			Map dataValue = new HashMap();
			Map addressDataValue = new HashMap();
			addressDataValue.put(streetAttribute,"Laxmi Road");
			addressDataValue.put(cityAttribute,"Pune");
			

			dataValue.put(userNameAttribute, "rahul");
			dataValue.put(association, addressDataValue);

			// Step 5
			entityManagerInterface.insertData(savedEntity, dataValue);

			// Step 6
			ResultSet resultSet = executeQuery("select count(*) from "
					+ address.getTableProperties().getName());
			resultSet.next();
			assertEquals(1, resultSet.getInt(1));
			
			resultSet = executeQuery("select count(*) from "
					+ user.getTableProperties().getName());
			resultSet.next();
			assertEquals(1, resultSet.getInt(1));
		}
		catch (DynamicExtensionsSystemException e)
		{
			fail();
			Logger.out.debug(e.getStackTrace());
		}
		catch (DynamicExtensionsApplicationException e)
		{
			fail();
			Logger.out.debug(e.getStackTrace());
		}
		catch (Exception e)
		{
			e.printStackTrace();
			fail();

			Logger.out.debug(e.getStackTrace());
		}

	}

}
