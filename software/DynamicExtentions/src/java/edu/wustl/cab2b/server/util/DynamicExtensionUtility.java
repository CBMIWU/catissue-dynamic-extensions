
package edu.wustl.cab2b.server.util;

import static edu.wustl.cab2b.common.errorcodes.ErrorCodeConstants.DE_0003;
import static edu.wustl.cab2b.common.errorcodes.ErrorCodeConstants.DE_0004;
import static edu.wustl.cab2b.common.util.Constants.CAB2B_ENTITY_GROUP;
import static edu.wustl.cab2b.server.ServerConstants.LOAD_FAILED;
import static edu.wustl.cab2b.server.ServerConstants.LOAD_STATUS;
import static edu.wustl.cab2b.server.path.PathConstants.CREATE_TABLE_FOR_ENTITY;
import static edu.wustl.cab2b.server.path.PathConstants.METADATA_ENTITY_GROUP;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import edu.common.dynamicextensions.domain.Category;
import edu.common.dynamicextensions.domain.CategoryEntity;
import edu.common.dynamicextensions.domain.DomainObjectFactory;
import edu.common.dynamicextensions.domain.SemanticAnnotatableInterface;
import edu.common.dynamicextensions.domaininterface.AbstractMetadataInterface;
import edu.common.dynamicextensions.domaininterface.AssociationInterface;
import edu.common.dynamicextensions.domaininterface.AttributeInterface;
import edu.common.dynamicextensions.domaininterface.BooleanValueInterface;
import edu.common.dynamicextensions.domaininterface.CategoryInterface;
import edu.common.dynamicextensions.domaininterface.DataElementInterface;
import edu.common.dynamicextensions.domaininterface.DateValueInterface;
import edu.common.dynamicextensions.domaininterface.DoubleValueInterface;
import edu.common.dynamicextensions.domaininterface.EntityGroupInterface;
import edu.common.dynamicextensions.domaininterface.EntityInterface;
import edu.common.dynamicextensions.domaininterface.FloatValueInterface;
import edu.common.dynamicextensions.domaininterface.IntegerValueInterface;
import edu.common.dynamicextensions.domaininterface.LongValueInterface;
import edu.common.dynamicextensions.domaininterface.PermissibleValueInterface;
import edu.common.dynamicextensions.domaininterface.RoleInterface;
import edu.common.dynamicextensions.domaininterface.SemanticPropertyInterface;
import edu.common.dynamicextensions.domaininterface.StringValueInterface;
import edu.common.dynamicextensions.domaininterface.TaggedValueInterface;
import edu.common.dynamicextensions.domaininterface.UserDefinedDEInterface;
import edu.common.dynamicextensions.domaininterface.userinterface.ContainerInterface;
import edu.common.dynamicextensions.entitymanager.AbstractMetadataManager;
import edu.common.dynamicextensions.entitymanager.EntityGroupManager;
import edu.common.dynamicextensions.entitymanager.EntityGroupManagerInterface;
import edu.common.dynamicextensions.entitymanager.EntityManager;
import edu.common.dynamicextensions.entitymanager.EntityManagerInterface;
import edu.common.dynamicextensions.exception.BaseDynamicExtensionsException;
import edu.common.dynamicextensions.exception.DynamicExtensionsApplicationException;
import edu.common.dynamicextensions.exception.DynamicExtensionsCacheException;
import edu.common.dynamicextensions.exception.DynamicExtensionsSystemException;
import edu.common.dynamicextensions.skiplogic.SkipLogic;
import edu.common.dynamicextensions.util.global.DEConstants.AssociationDirection;
import edu.common.dynamicextensions.util.global.DEConstants.AssociationType;
import edu.common.dynamicextensions.util.global.DEConstants.Cardinality;
import edu.wustl.cab2b.common.errorcodes.ErrorCodeConstants;
import edu.wustl.cab2b.common.exception.RuntimeException;
import edu.wustl.cab2b.common.util.Utility;
import edu.wustl.cab2b.server.cache.EntityCache;
import edu.wustl.common.querysuite.queryobject.DataType;
import edu.wustl.common.util.logger.Logger;
import edu.wustl.dao.HibernateDAO;
import edu.wustl.dao.exception.DAOException;
import edu.wustl.dao.query.generator.DBTypes;
import edu.wustl.dao.util.NamedQueryParam;
import gov.nih.nci.cagrid.metadata.common.SemanticMetadata;

/**
 * This class decides whether to create a storage table for entity or not based on
 * To create a table for entity set this to TRUE before calling this code else set it to false.
 * @author Chandrakant Talele
 */
public final class DynamicExtensionUtility
{

	/**
	 * Instantiates a new dynamic extension utility.
	 */
	private DynamicExtensionUtility()
	{
		// private constructor
	}

	/**
	 * The domain object factory.
	 */
	private static DomainObjectFactory domainObjectFactory = DomainObjectFactory.getInstance();

	/**
	 * Persist given entity using dynamic extension APIs.
	 * Whether to create data space (table) for this entity or not is decided by
	 * {PathConstants.CREATE_TABLE_FOR_ENTITY}
	 * @param entity Entity to persist.
	 * @return The saved entity
	 */
	public static EntityInterface persistEntity(final EntityInterface entity)
	{
		EntityManagerInterface entityManager = EntityManager.getInstance();
		EntityInterface entityToPersist;
		try
		{
			if (CREATE_TABLE_FOR_ENTITY)
			{
				entityToPersist = entityManager.persistEntity(entity);
			}
			else
			{
				entityToPersist = entityManager.persistEntityMetadata(entity);
			}
		}
		catch (BaseDynamicExtensionsException e)
		{
			throw new RuntimeException("Unable to persist Entity in Dynamic Extension", e,
					ErrorCodeConstants.DE_0002);
		}
		return entityToPersist;
	}

	/**
	 * Persist given entity Group using dynamic extension APIs. Whether to store
	 * only metadata for this entity Group is decided by
	 * {@link edu.wustl.cab2b.server.path.PathConstants.CREATE_TABLE_FOR_ENTITY}
	 *
	 * @param entityGroup
	 *            entity Group to persist.
	 * @return The saved entity Group
	 */
	public static EntityGroupInterface persistEntityGroup(final EntityGroupInterface entityGroup)
	{
		EntityGroupInterface entityGroupObject = null;
		try
		{
			entityGroupObject = persistEGroup(entityGroup);
		}
		catch (BaseDynamicExtensionsException e)
		{
			throw new RuntimeException("Unable to persist Entity Group in Dynamic Extension", e,
					ErrorCodeConstants.DE_0001);
		}
		return entityGroupObject;
	}

	/**
	 * Persist given entity Group using dynamic extension APIs. Whether to store
	 * only metadata for this entity Group is decided by
	 *
	 * @param entityGroup
	 *            entity Group to persist.
	 * @return The saved entity Group
	 * @throws DynamicExtensionsApplicationException
	 *             the dynamic extensions application exception
	 * @throws DynamicExtensionsSystemException
	 *             the dynamic extensions system exception
	 *             {@link edu.wustl.cab2b.server.path.PathConstants.CREATE_TABLE_FOR_ENTITY}
	 */
	public static EntityGroupInterface persistEGroup(final EntityGroupInterface entityGroup)
			throws DynamicExtensionsSystemException, DynamicExtensionsApplicationException
	{
		EntityGroupManagerInterface entityGroupManager = EntityGroupManager.getInstance();

		if (CREATE_TABLE_FOR_ENTITY)
		{
			entityGroupManager.persistEntityGroup(entityGroup);
		}
		else
		{
			entityGroupManager.persistEntityGroupMetadata(entityGroup);
		}

		return entityGroup;
	}

	/**
	 * Returns the Entity for given Identifier.
	 *
	 * @param identifier
	 *            Id of the entity
	 * @return Actual Entity for given id.
	 */
	public static EntityInterface getEntityById(final Long identifier)
	{
		try
		{
			return EntityManager.getInstance().getEntityByIdentifier(identifier);
		}
		catch (BaseDynamicExtensionsException e)
		{
			throw new RuntimeException("Expected Entity is not found in database", e, DE_0003);
		}
	}

	/**
	 * Returns the Association for given Identifier.
	 *
	 * @param identifier
	 *            Id of the Association
	 * @return Actual Association for given id.
	 */
	public static AssociationInterface getAssociationById(final Long identifier)
	{
		try
		{
			AssociationInterface association = EntityManager.getInstance()
					.getAssociationByIdentifier(identifier);
			return association;
		}
		catch (BaseDynamicExtensionsException e)
		{
			throw new RuntimeException("Expected association is not found in database", e, DE_0003);
		}
	}

	/**
	 * Returns the entity group of given entity Id.
	 *
	 * @param identifier
	 *            Entity Id
	 * @return Returns parent Entity Group
	 */
	public static EntityGroupInterface getEntityGroupForEntityId(final Long identifier)
	{
		EntityInterface entity = getEntityById(identifier);
		return Utility.getEntityGroup(entity);
	}

	/**
	 * Returns the AttributeInterface whose name matches with given
	 * "attributeName" and whose parent name matches with "entityName".
	 *
	 * @param entityName
	 *            name of parent entity.
	 * @param attributeName
	 *            name of required attribute.
	 * @return Returns Attribute satisfying given conditions.
	 */
	public static AttributeInterface getAttribute(final String entityName, String attributeName)
	{
		try
		{
			return EntityManager.getInstance().getAttribute(entityName, attributeName);
		}
		catch (BaseDynamicExtensionsException e)
		{
			throw new RuntimeException("Expected attribute not found in database : " + entityName
					+ "-" + attributeName, e, ErrorCodeConstants.DE_0003);
		}
	}

	/**
	 * Stores the SemanticMetadata to the owner which can be class or attribute.
	 *
	 * @param owner
	 *            EntityInterface OR AttributeInterface
	 * @param semanticMetadataArr
	 *            Semantic Metadata array to set.
	 */
	public static void setSemanticMetadata(final SemanticAnnotatableInterface owner,
			final SemanticMetadata[] semanticMetadataArr)
	{
		if (semanticMetadataArr == null)
		{
			return;
		}

		for (int i = 0; i < semanticMetadataArr.length; i++)
		{
			SemanticPropertyInterface semanticProp = domainObjectFactory.createSemanticProperty();
			semanticProp.setSequenceNumber(i);
			semanticProp.setConceptCode(semanticMetadataArr[i].getConceptCode());
			semanticProp.setConceptPreferredName(semanticMetadataArr[i].getConceptName());
			semanticProp.setConceptDefinition(semanticMetadataArr[i].getConceptDefinition());
			owner.addSemanticProperty(semanticProp);
		}
	}

	/**
	 * Creates a tagged value and adds it to the owner.
	 * @param owner Owner of the tagged value
	 * @param key Key to be used for tagging
	 * @param value Actual value of the tag.
	 */
	public static void addTaggedValue(final AbstractMetadataInterface owner, final String key,
			final String value)
	{
		TaggedValueInterface taggedValue = domainObjectFactory.createTaggedValue();
		taggedValue.setKey(key);
		taggedValue.setValue(value);
		owner.addTaggedValue(taggedValue);
	}

	/**
	 * This method adds a tag to entity group to distinguish it as metadata
	 * entity group.
	 *
	 * @param entityGroup
	 *            the entity group
	 */
	public static void markMetadataEntityGroup(final EntityGroupInterface entityGroup)
	{
		TaggedValueInterface taggedValue = DomainObjectFactory.getInstance().createTaggedValue();
		taggedValue.setKey(METADATA_ENTITY_GROUP);
		taggedValue.setValue(METADATA_ENTITY_GROUP);
		entityGroup.addTaggedValue(taggedValue);
	}

	/**
	 * This method searches for is there any entity group present with given name.
	 * If present it returns the entity group.If not present then it creates a entity group with given name.
	 * Saves it, and returns the saved Entity Group.
	 * @param name Name of the entity group.
	 * @return Entity group with  given name
	 */
	public static synchronized EntityGroupInterface getEntityGroupByName(final String name)
	{
		EntityGroupInterface entityGroup = null;
		try
		{
			entityGroup = EntityManager.getInstance().getEntityGroupByName(name);
		}
		catch (BaseDynamicExtensionsException e)
		{
			throw new RuntimeException("Got System exception from Dynamic Extension "
					+ "while fetching entity group", e, ErrorCodeConstants.DB_0001);
		}
		if (entityGroup == null)
		{
			entityGroup = createEntityGroup();
			entityGroup.setShortName(name);
			entityGroup.setName(name);
			entityGroup.setLongName(name);
			entityGroup.setDescription(name);
			persistEntityGroup(entityGroup);
		}
		return entityGroup;
	}

	/**
	 * creates a new entity group, and tags it to identify caB2b entity group.
	 * @return EntityGroupInterface newly created unsaved entity group.
	 */
	public static EntityGroupInterface createEntityGroup()
	{
		EntityGroupInterface entityGroup = DomainObjectFactory.getInstance().createEntityGroup();
		addTaggedValue(entityGroup, CAB2B_ENTITY_GROUP, CAB2B_ENTITY_GROUP);
		return entityGroup;
	}

	/**
	 * Copies the attribute's description, semantic metadata and associated
	 * permissible values. Sets the given name to copied attribute
	 *
	 * @param source
	 *            Attribute to copy
	 * @param name
	 *            New name to be given to copied attribute
	 * @return The copied attribute
	 */
	public static AttributeInterface getAttributeCopy(final AttributeInterface source,
			final String name)
	{
		AttributeInterface attribute = getAttributeCopy(source);
		attribute.setName(name);
		return attribute;
	}

	/**
	 * Copies the attribute's name,description, semantic metadata and associated
	 * permissible values.
	 *
	 * @param source
	 *            Attribute to copy
	 * @return The cloned attribute
	 */
	public static AttributeInterface getAttributeCopy(final AttributeInterface source)
	{
		DataType type = Utility.getDataType(source.getAttributeTypeInformation());
		DataElementInterface dataEle = source.getAttributeTypeInformation().getDataElement();
		AttributeInterface attribute = handleDataTypes(type, dataEle);
		attribute.setName(source.getName());
		attribute.setDescription(source.getDescription());
		copySemanticProperties(source, attribute);
		return attribute;
	}

	/**
	 * Handle data types.
	 * @param type
	 *            the type
	 * @param dataEle
	 *            the data ele
	 *
	 * @return the attribute interface
	 */
	private static AttributeInterface handleDataTypes(final DataType type,
			final DataElementInterface dataEle)
	{
		AttributeInterface attribute = null;
		switch (type)
		{
			case String :
				attribute = domainObjectFactory.createStringAttribute();
				handleStringType(attribute, dataEle);
				break;

			case Double :
				attribute = domainObjectFactory.createDoubleAttribute();
				handleDoubleType(attribute, dataEle);
				break;

			case Integer :
				attribute = domainObjectFactory.createIntegerAttribute();
				handleIntegerType(attribute, dataEle);
				break;

			case Date :
				attribute = domainObjectFactory.createDateAttribute();
				handleDateType(attribute, dataEle);
				break;

			case Float :
				attribute = domainObjectFactory.createFloatAttribute();
				handleFloatType(attribute, dataEle);
				break;

			case Boolean :
				attribute = domainObjectFactory.createBooleanAttribute();
				handleBooleanType(attribute, dataEle);
				break;

			case Long :
				attribute = domainObjectFactory.createLongAttribute();
				handleLongType(attribute, dataEle);
				break;

			default :
				Logger.out.info("Data type value not in specified types");

		}
		return attribute;
	}

	/**
	 * Handle long type.
	 *
	 * @param attribute
	 *            the attribute
	 * @param dataEle
	 *            the data ele
	 */
	private static void handleLongType(final AttributeInterface attribute,
			final DataElementInterface dataEle)
	{
		if (dataEle instanceof UserDefinedDEInterface)
		{
			UserDefinedDEInterface userDefinedDE = domainObjectFactory.createUserDefinedDE();
			UserDefinedDEInterface userDe = (UserDefinedDEInterface) dataEle;
			for (PermissibleValueInterface val : userDe.getPermissibleValueCollection())
			{
				LongValueInterface value = domainObjectFactory.createLongValue();
				value.setValue((Long) val.getValueAsObject());
				userDefinedDE.addPermissibleValue(value);
			}
			attribute.getAttributeTypeInformation().setDataElement(userDefinedDE);
		}
	}

	/**
	 * Handle boolean type.
	 *
	 * @param attribute
	 *            the attribute
	 * @param dataEle
	 *            the data ele
	 */
	private static void handleBooleanType(final AttributeInterface attribute,
			final DataElementInterface dataEle)
	{
		if (dataEle instanceof UserDefinedDEInterface)
		{
			UserDefinedDEInterface userDefinedDE = domainObjectFactory.createUserDefinedDE();
			UserDefinedDEInterface userDe = (UserDefinedDEInterface) dataEle;
			for (PermissibleValueInterface val : userDe.getPermissibleValueCollection())
			{
				BooleanValueInterface value = domainObjectFactory.createBooleanValue();
				value.setValue((Boolean) val.getValueAsObject());
				userDefinedDE.addPermissibleValue(value);
			}
			attribute.getAttributeTypeInformation().setDataElement(userDefinedDE);
		}
	}

	/**
	 * Handle float type.
	 *
	 * @param attribute
	 *            the attribute
	 * @param dataEle
	 *            the data ele
	 */
	private static void handleFloatType(final AttributeInterface attribute,
			final DataElementInterface dataEle)
	{
		if (dataEle instanceof UserDefinedDEInterface)
		{
			UserDefinedDEInterface userDefinedDE = domainObjectFactory.createUserDefinedDE();
			UserDefinedDEInterface userDe = (UserDefinedDEInterface) dataEle;
			for (PermissibleValueInterface val : userDe.getPermissibleValueCollection())
			{
				FloatValueInterface value = domainObjectFactory.createFloatValue();
				value.setValue((Float) val.getValueAsObject());
				userDefinedDE.addPermissibleValue(value);
			}
			attribute.getAttributeTypeInformation().setDataElement(userDefinedDE);
		}
	}

	/**
	 * Handle date type.
	 *
	 * @param attribute
	 *            the attribute
	 * @param dataEle
	 *            the data ele
	 */
	private static void handleDateType(final AttributeInterface attribute,
			final DataElementInterface dataEle)
	{
		if (dataEle instanceof UserDefinedDEInterface)
		{
			UserDefinedDEInterface userDefinedDE = domainObjectFactory.createUserDefinedDE();
			UserDefinedDEInterface userDe = (UserDefinedDEInterface) dataEle;
			for (PermissibleValueInterface val : userDe.getPermissibleValueCollection())
			{
				DateValueInterface value = domainObjectFactory.createDateValue();
				value.setValue((Date) val.getValueAsObject());
				userDefinedDE.addPermissibleValue(value);
			}
			attribute.getAttributeTypeInformation().setDataElement(userDefinedDE);
		}
	}

	/**
	 * Handle integer type.
	 *
	 * @param attribute
	 *            the attribute
	 * @param dataEle
	 *            the data ele
	 */
	private static void handleIntegerType(final AttributeInterface attribute,
			final DataElementInterface dataEle)
	{
		if (dataEle instanceof UserDefinedDEInterface)
		{
			UserDefinedDEInterface userDefinedDE = domainObjectFactory.createUserDefinedDE();
			UserDefinedDEInterface userDe = (UserDefinedDEInterface) dataEle;
			for (PermissibleValueInterface val : userDe.getPermissibleValueCollection())
			{
				IntegerValueInterface value = domainObjectFactory.createIntegerValue();
				value.setValue((Integer) val.getValueAsObject());
				userDefinedDE.addPermissibleValue(value);
			}
			attribute.getAttributeTypeInformation().setDataElement(userDefinedDE);
		}
	}

	/**
	 * Handle double type.
	 *
	 * @param attribute
	 *            the attribute
	 * @param dataEle
	 *            the data ele
	 */
	private static void handleDoubleType(final AttributeInterface attribute,
			final DataElementInterface dataEle)
	{
		if (dataEle instanceof UserDefinedDEInterface)
		{
			UserDefinedDEInterface userDefinedDE = domainObjectFactory.createUserDefinedDE();
			UserDefinedDEInterface userDe = (UserDefinedDEInterface) dataEle;
			for (PermissibleValueInterface val : userDe.getPermissibleValueCollection())
			{
				DoubleValueInterface value = domainObjectFactory.createDoubleValue();
				value.setValue((Double) val.getValueAsObject());
				userDefinedDE.addPermissibleValue(value);
			}
			attribute.getAttributeTypeInformation().setDataElement(userDefinedDE);
		}
	}

	/**
	 * Handle string type.
	 *
	 * @param attribute
	 *            the attribute
	 * @param dataEle
	 *            the data ele
	 */
	private static void handleStringType(final AttributeInterface attribute,
			final DataElementInterface dataEle)
	{
		if (dataEle instanceof UserDefinedDEInterface)
		{
			UserDefinedDEInterface userDefinedDE = domainObjectFactory.createUserDefinedDE();
			UserDefinedDEInterface userDe = (UserDefinedDEInterface) dataEle;
			for (PermissibleValueInterface val : userDe.getPermissibleValueCollection())
			{
				StringValueInterface value = domainObjectFactory.createStringValue();
				value.setValue((String) val.getValueAsObject());
				userDefinedDE.addPermissibleValue(value);
			}
			attribute.getAttributeTypeInformation().setDataElement(userDefinedDE);
		}
	}

	/**
	 * Stores the SemanticMetadata to the owner which can be class or attribute.
	 *
	 * @param from
	 *            the from
	 * @param copyTo
	 *            the copy to
	 */
	private static void copySemanticProperties(final AbstractMetadataInterface from,
			final AbstractMetadataInterface copyTo)
	{
		for (SemanticPropertyInterface p : from.getSemanticPropertyCollection())
		{
			SemanticPropertyInterface semanticProp = domainObjectFactory.createSemanticProperty();
			semanticProp.setConceptPreferredName(p.getConceptPreferredName());
			semanticProp.setConceptCode(p.getConceptCode());
			copyTo.addSemanticProperty(semanticProp);
		}
	}

	/**
	 * Creates and returns a new one to many association between source target
	 * entities.
	 *
	 * @param srcEntity
	 *            source entity of the new association
	 * @param tarEntity
	 *            target entity of the new association
	 * @return new association
	 * @throws DynamicExtensionsSystemException
	 *             the dynamic extensions system exception
	 */
	public static AssociationInterface createNewOneToManyAsso(final EntityInterface srcEntity,
			final EntityInterface tarEntity) throws DynamicExtensionsSystemException
	{
		AssociationInterface association = domainObjectFactory.createAssociation();
		String associationName = "AssociationName_"
				+ (srcEntity.getAssociationCollection().size() + 1);
		association.setName(associationName);
		association.setEntity(srcEntity);
		association.setTargetEntity(tarEntity);
		association.setAssociationDirection(AssociationDirection.SRC_DESTINATION);
		association.setSourceRole(getNewRole(AssociationType.CONTAINTMENT, "source_role_"
				+ associationName, Cardinality.ONE, Cardinality.ONE));
		association.setTargetRole(getNewRole(AssociationType.CONTAINTMENT, "target_role_"
				+ associationName, Cardinality.ZERO, Cardinality.MANY));

		srcEntity.addAssociation(association);

		return association;
	}

	/**
	 * Creates and returns new Role for an association.
	 *
	 * @param associationType
	 *            associationType
	 * @param name
	 *            name
	 * @param minCard
	 *            minCard
	 * @param maxCard
	 *            maxCard
	 * @return RoleInterface
	 */
	public static RoleInterface getNewRole(final AssociationType associationType,
			final String name, final Cardinality minCard, final Cardinality maxCard)
	{
		RoleInterface role = domainObjectFactory.createRole();
		role.setAssociationsType(associationType);
		role.setName(name);
		role.setMinimumCardinality(minCard);
		role.setMaximumCardinality(maxCard);
		return role;
	}

	/**
	 * Returns Cab2b Entity Groups.
	 *
	 * @param hibernateDAO
	 *            the hibernate dao
	 * @return Cab2b Entity Groups
	 * @throws DAOException
	 *             the DAO exception
	 */
	@SuppressWarnings("unchecked")
	public static Collection<EntityGroupInterface> getSystemGeneratedEntityGroups(
			final HibernateDAO hibernateDAO) throws DAOException
	{
		List<EntityGroupInterface> entityGroups = new ArrayList<EntityGroupInterface>();
		Collection<EntityGroupInterface> allEntityGroups = hibernateDAO
				.retrieve(EntityGroupInterface.class.getName());
		for (EntityGroupInterface entityGroup : allEntityGroups)
		{
			if (isEntityGroupMetadata(entityGroup))
			{
				entityGroups.add(entityGroup);
			}
		}
		return entityGroups;
	}

	@SuppressWarnings("unchecked")
	public static Collection<SkipLogic> getSkipLogicsDefinedForCategories(
			final HibernateDAO hibernateDAO) throws DAOException
	{
		return hibernateDAO.retrieve(SkipLogic.class.getName());
	}

	/**
	 * It will retrieve all the categories present in the dataBase.
	 *
	 * @param hibernateDAO
	 *            the hibernate dao
	 * @return List Of the categories present in the DB.
	 * @throws DAOException
	 *             the DAO exception
	 */
	@SuppressWarnings("unchecked")
	public static List<CategoryInterface> getAllCategories(final HibernateDAO hibernateDAO)
			throws DAOException
	{
		Logger.out.info("EntityCache in before GetAll Categories ");
		List<CategoryInterface> categoryList = hibernateDAO.retrieve(CategoryInterface.class
				.getName());
		Logger.out.info("EntityCache in after getAllCategories ");
		return categoryList;
	}

	/**
	 * It will retrieve all the cacheable categories present in the dataBase.
	 * @param hibernateDAO hibernateDAO
	 * @return List Of the categories present in the DB.
	 * @throws DAOException exception.
	 */
	public static List<CategoryInterface> getAllCacheableCategories(HibernateDAO hibernateDAO)
			throws DAOException
	{
		Logger.out.info("Loading cacheable categories ");
		List<CategoryInterface> categoryList = hibernateDAO.retrieve(CategoryInterface.class
				.getName(), "isCacheable", Boolean.TRUE);
		Logger.out.info("Loading cacheable categories finished");
		return categoryList;
	}

	/**
	 * It will retrieve Identifier of all the categories present in the
	 * dataBase.
	 *
	 * @param hibernateDAO
	 *            the hibernate dao
	 * @return List Of the categories present in the DB.
	 * @throws DAOException
	 *             the DAO exception
	 */
	@SuppressWarnings({"unchecked"})
	public static List<Long> getAllCategoryIds(final HibernateDAO hibernateDAO) throws DAOException
	{
		String[] columnName = {"id"};
		List<Long> categoryList = hibernateDAO.retrieve(CategoryInterface.class.getName(),
				columnName);
		return categoryList;
	}

	/**
	 * It will retrieve Identifier of all the categories present in the
	 * dataBase.
	 *
	 * @param hibernateDAO
	 *            the hibernate dao
	 * @return List Of the categories present in the DB.
	 * @throws DAOException
	 *             the DAO exception
	 */
	@SuppressWarnings({"unchecked"})
	public static List<Long> getAllEntityGroupIds(final HibernateDAO hibernateDAO)
			throws DAOException
	{
		String[] columnName = {"id"};
		List<Long> entityGroupList = hibernateDAO.retrieve(EntityGroupInterface.class.getName(),
				columnName);
		return entityGroupList;
	}

	/**
	 * It will retrieve Identifier of all the categories present in the
	 * dataBase.
	 *
	 * @param hibernateDAO
	 *            the hibernate dao
	 * @param entityGroupID
	 *            the entity group id
	 * @return List Of the categories present in the DB.
	 * @throws DAOException
	 *             the DAO exception
	 * @throws DynamicExtensionsSystemException
	 *             the dynamic extensions system exception
	 */
	@SuppressWarnings({"unchecked", "deprecation"})
	public static List<Long> getAllCategoryIdsForEntityGroupId(final HibernateDAO hibernateDAO,
			final Long entityGroupID) throws DAOException, DynamicExtensionsSystemException
	{
		Map<String, NamedQueryParam> substParams = new HashMap<String, NamedQueryParam>();
		substParams.put("0", new NamedQueryParam(DBTypes.LONG, entityGroupID));

		// Following method is called to execute the stored HQL, the name of
		// which is given as
		// the first parameter. The second parameter is the map which contains
		// the actual values
		// that are replaced for the place holders.
		List<Long> categoryList = (List<Long>) AbstractMetadataManager.executeHQL(
				"getAllCategoryIdsForEntityGroupId", substParams);

		return categoryList;
	}

	/**
	 * This method checks if the given entity group is a metadata entity group
	 * or not.
	 *
	 * @param entityGroup
	 *            the entity group
	 * @return true, if is entity group metadata
	 */
	public static boolean isEntityGroupMetadata(final EntityGroupInterface entityGroup)
	{
		boolean hasMetadataTag = false;
		Collection<TaggedValueInterface> tags = entityGroup.getTaggedValueCollection();
		for (TaggedValueInterface tag : tags)
		{
			if (METADATA_ENTITY_GROUP.equals(tag.getKey())
					&& METADATA_ENTITY_GROUP.equals(tag.getValue()))
			{
				hasMetadataTag = true;
				break;
			}
		}
		return hasMetadataTag && isSuccessfullyLoaded(tags);
	}

	/**
	 * Checks if is successfully loaded.
	 *
	 * @param taggedValues
	 *            the tagged values
	 * @return true, if is successfully loaded
	 */
	private static boolean isSuccessfullyLoaded(final Collection<TaggedValueInterface> taggedValues)
	{
		boolean isSuccessfullyLoaded = true;
		for (TaggedValueInterface taggedValue : taggedValues)
		{
			String taggedKey = taggedValue.getKey();
			String tagggedValue = taggedValue.getValue();
			if (LOAD_STATUS.equals(taggedKey) && LOAD_FAILED.equals(tagggedValue))
			{
				isSuccessfullyLoaded = false;
			}
		}
		return isSuccessfullyLoaded;
	}

	/**
	 * Gets the incoming intramodel associations.
	 *
	 * @param entityId
	 *            Entity Id
	 * @return associations with given entity as the target entity.
	 * @throws DynamicExtensionsCacheException
	 */
	public static Collection<AssociationInterface> getIncomingIntramodelAssociations(
			final Long entityId) throws DynamicExtensionsCacheException
	{
		EntityInterface entity = EntityCache.getInstance().getEntityById(entityId);
		try
		{
			return EntityManager.getInstance().getIncomingAssociations(entity);
		}
		catch (DynamicExtensionsSystemException e)
		{
			throw new RuntimeException(
					"Unable to get incoming associations from Dynamic Extension", e, DE_0004);
		}
	}

	/**
	 * @param role Role to clone
	 * @return the clone of the Role
	 */
	public static RoleInterface cloneRole(final RoleInterface role)
	{
		RoleInterface clone = domainObjectFactory.createRole();
		clone.setAssociationsType(role.getAssociationsType());
		clone.setName(role.getName());
		clone.setMaximumCardinality(role.getMaximumCardinality());
		clone.setMinimumCardinality(role.getMinimumCardinality());
		return clone;
	}

	/**
	 * @param containerId
	 * @return
	 * @throws DynamicExtensionsCacheException
	 */
	public static Category getCategoryByContainerId(String containerId)
			throws DynamicExtensionsCacheException
	{
		ContainerInterface container = EntityCache.getInstance().getContainerById(Long.valueOf(containerId));
		CategoryEntity categoryEntity = (CategoryEntity) container.getAbstractEntity();
		return (Category) categoryEntity.getCategory();
	}

}
