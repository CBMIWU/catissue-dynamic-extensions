
package edu.common.dynamicextensions.util;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import edu.common.dynamicextensions.domain.CategoryEntity;
import edu.common.dynamicextensions.domain.DateAttributeTypeInformation;
import edu.common.dynamicextensions.domain.DomainObjectFactory;
import edu.common.dynamicextensions.domain.NumericAttributeTypeInformation;
import edu.common.dynamicextensions.domain.PathAssociationRelationInterface;
import edu.common.dynamicextensions.domaininterface.AssociationInterface;
import edu.common.dynamicextensions.domaininterface.AttributeMetadataInterface;
import edu.common.dynamicextensions.domaininterface.CalculatedAttributeInterface;
import edu.common.dynamicextensions.domaininterface.CategoryAssociationInterface;
import edu.common.dynamicextensions.domaininterface.CategoryAttributeInterface;
import edu.common.dynamicextensions.domaininterface.CategoryEntityInterface;
import edu.common.dynamicextensions.domaininterface.CategoryInterface;
import edu.common.dynamicextensions.domaininterface.DataElementInterface;
import edu.common.dynamicextensions.domaininterface.EntityGroupInterface;
import edu.common.dynamicextensions.domaininterface.EntityInterface;
import edu.common.dynamicextensions.domaininterface.PermissibleValueInterface;
import edu.common.dynamicextensions.domaininterface.SkipLogicAttributeInterface;
import edu.common.dynamicextensions.domaininterface.UserDefinedDEInterface;
import edu.common.dynamicextensions.domaininterface.userinterface.ContainerInterface;
import edu.common.dynamicextensions.entitymanager.CategoryManager;
import edu.common.dynamicextensions.entitymanager.EntityManager;
import edu.common.dynamicextensions.entitymanager.EntityManagerInterface;
import edu.common.dynamicextensions.exception.DynamicExtensionsApplicationException;
import edu.common.dynamicextensions.exception.DynamicExtensionsSystemException;
import edu.common.dynamicextensions.util.global.CategoryConstants;
import edu.common.dynamicextensions.util.parser.CategoryCSVConstants;
import edu.common.dynamicextensions.util.parser.CategoryFileParser;
import edu.common.dynamicextensions.util.parser.FormulaParser;
import edu.common.dynamicextensions.validation.category.CategoryValidator;
import edu.wustl.cab2b.server.cache.EntityCache;
import edu.wustl.common.util.global.ApplicationProperties;
import edu.wustl.dao.HibernateDAO;

/**
 * The Class CategoryGenerationUtil.
 *
 * @author kunal_kamble
 */
public class CategoryGenerationUtil
{

	/**
	 * This method returns the entity from the entity group.
	 *
	 * @param entityName
	 *            the entity name
	 * @param entityGroup
	 *            the entity group
	 * @return the entity
	 * @return
	 */
	public static EntityInterface getEntity(String entityName, EntityGroupInterface entityGroup)
	{
		return entityGroup.getEntityByName(entityName);
	}

	/**
	 * Returns the multiplicity in number for the give string.
	 *
	 * @param multiplicity
	 *            the multiplicity
	 * @return the multiplicity in numbers
	 * @throws DynamicExtensionsSystemException
	 *             the dynamic extensions system exception
	 * @return
	 */
	public static int getMultiplicityInNumbers(String multiplicity)
			throws DynamicExtensionsSystemException
	{
		int multiplicityI = 1; // NOPMD by gaurav_sawant on 5/6/10 2:56 PM
		if (multiplicity.equalsIgnoreCase(CategoryCSVConstants.MULTILINE))
		{
			multiplicityI = -1;
		}
		else if (multiplicity.equalsIgnoreCase(CategoryCSVConstants.SINGLE))
		{
			multiplicityI = 1;
		}
		else
		{
			throw new DynamicExtensionsSystemException(
					"ERROR: WRONG KEYWORD USED FOR MULTIPLICITY " + multiplicity
							+ ". VALID KEY WORDS ARE: 1- single 2-multiline");
		}
		return multiplicityI;
	}

	/**
	 * Sets the root category entity for the category.
	 *
	 * @param category
	 *            the category
	 * @param rootContainerObject
	 *            the root container object
	 * @param containerCollection
	 *            the container collection
	 * @param paths
	 *            the paths
	 * @param absolutePath
	 *            the absolute path
	 * @param containerNameInstanceMap
	 *            the container name instance map
	 *  @param categoryHelper category helper object.
	 * @throws DynamicExtensionsSystemException
	 *             the dynamic extensions system exception
	 * @throws DynamicExtensionsApplicationException
	 *             the dynamic extensions application exception
	 */
	public static void setRootContainer(CategoryInterface category,
			ContainerInterface rootContainerObject, List<ContainerInterface> containerCollection,
			Map<String, List<AssociationInterface>> paths, Map<String, List<String>> absolutePath,
			Map<String, String> containerNameInstanceMap, CategoryHelperInterface categoryHelper)
			throws DynamicExtensionsSystemException, DynamicExtensionsApplicationException
	{
		ContainerInterface rootContainer = rootContainerObject;
		//		ContainerInterface rootContainer = null;
		//		for (ContainerInterface containerInterface : containerCollection)
		//		{
		//			CategoryEntityInterface categoryEntityInterface = (CategoryEntityInterface) containerInterface.getAbstractEntity();
		//			if (categoryEntityInterface.getTreeParentCategoryEntity() == null)
		//			{
		//				rootContainer = containerInterface;
		//				break;
		//			}
		//		}

		for (Entry<String, List<String>> entryObject : absolutePath.entrySet())
		{

			if (entryObject.getValue().size() == 1 && rootContainer != null)
			{
				CategoryEntityInterface categoryEntityInterface = (CategoryEntityInterface) rootContainer
						.getAbstractEntity();
				String entityNameForAssociationMap = CategoryGenerationUtil
						.getEntityNameForAssociationMap(containerNameInstanceMap
								.get(categoryEntityInterface.getName()));
				String entityName = entryObject.getKey();
				if (!entityName.equals(entityNameForAssociationMap))
				{
					String entName = entityName;
					EntityInterface entity = CategoryGenerationUtil
							.getEntityFromEntityAssociationMap(paths.get(entityName));
					if (entity != null)
					{
						entName = entity.getName();
					}
					ContainerInterface containerInterface = getContainerWithEntityName(
							containerCollection, entName);
					if (containerInterface == null)
					{
						EntityGroupInterface entityGroup = categoryEntityInterface.getEntity()
								.getEntityGroup();
						entity = entityGroup.getEntityByName(entName);

						ContainerInterface newRootContainer = categoryHelper
								.createOrUpdateCategoryEntityAndContainer(entity, null, category,
										entName + "[1]" + entName + "[1]");
						newRootContainer.setAddCaption(false);

						categoryHelper.associateCategoryContainers(category, entityGroup,
								newRootContainer, rootContainer, paths
										.get(entityNameForAssociationMap), 1,
								containerNameInstanceMap.get(rootContainer.getAbstractEntity()
										.getName()));

						rootContainer = newRootContainer;

					}
					else
					{
						rootContainer = containerInterface;
					}

				}
			}
		}

		for (ContainerInterface containerInterface : containerCollection)
		{
			CategoryEntityInterface categoryEntity = (CategoryEntityInterface) containerInterface
					.getAbstractEntity();
			boolean isTableCreated = ((CategoryEntity) categoryEntity).isCreateTable();
			if (rootContainer != containerInterface
					&& categoryEntity.getTreeParentCategoryEntity() == null && isTableCreated)
			{
				categoryHelper.associateCategoryContainers(category, categoryEntity.getEntity()
						.getEntityGroup(), rootContainer, containerInterface, paths
						.get(CategoryGenerationUtil
								.getEntityNameForAssociationMap(containerNameInstanceMap
										.get(categoryEntity.getName()))), 1,
						containerNameInstanceMap.get(containerInterface.getAbstractEntity()
								.getName()));
			}
		}
		//If category is edited and no attributes from the main form of the model are not selected
		//rootContainer will be null
		//keep the root container unchanged in this case. Just use the root entity of the category
		if (rootContainer == null)
		{
			EntityManagerInterface entityManagerInterface = EntityManager.getInstance();
			rootContainer = entityManagerInterface.getContainerByEntityIdentifier(category
					.getRootCategoryElement().getId());
		}
		categoryHelper.setRootCategoryEntity(rootContainer, category);

		categoryHelper.setRootCategoryEntity(rootContainer, category);
	}

	/**
	 * Returns the container having container caption as one passed to this
	 * method.
	 *
	 * @param containerCollection
	 *            the container collection
	 * @param containerCaption
	 *            the container caption
	 * @return the container
	 * @return
	 */
	public static ContainerInterface getContainer(List<ContainerInterface> containerCollection,
			String containerCaption)
	{
		ContainerInterface container = null;
		for (ContainerInterface containerInterface : containerCollection)
		{
			if (containerCaption.equals(containerInterface.getCaption()))
			{
				container = containerInterface;
				break;
			}

		}
		return container;
	}

	/**
	 * Gets the container with entity name.
	 *
	 * @param containerCollection
	 *            the container collection
	 * @param entityName
	 *            the entity name
	 * @return the container with entity name
	 * @return
	 */
	public static ContainerInterface getContainerWithEntityName(
			List<ContainerInterface> containerCollection, String entityName)
	{
		ContainerInterface container = null;
		for (ContainerInterface containerInterface : containerCollection)
		{
			CategoryEntityInterface categoryEntity = (CategoryEntityInterface) containerInterface
					.getAbstractEntity();
			if (entityName.equals(categoryEntity.getEntity().getName()))
			{
				container = containerInterface;
				break;
			}

		}
		return container;
	}

	/**
	 * Returns the container with given category entity name.
	 *
	 * @param containerCollection
	 *            the container collection
	 * @param categoryEntityName
	 *            the category entity name
	 * @return the container with category entity name
	 * @return
	 */
	public static ContainerInterface getContainerWithCategoryEntityName(
			List<ContainerInterface> containerCollection, String categoryEntityName)
	{
		ContainerInterface container = null;

		for (ContainerInterface containerInterface : containerCollection)
		{
			if (categoryEntityName.equals(containerInterface.getAbstractEntity().getName()))
			{
				container = containerInterface;
				break;
			}

		}
		return container;
	}

	/**
	 * Gets the association list.
	 *
	 * @param paths
	 *            the paths
	 * @param entityGroup
	 *            the entity group
	 * @return the association list
	 * @throws DynamicExtensionsSystemException
	 *             the dynamic extensions system exception
	 */
	public static Map<String, List<AssociationInterface>> getAssociationList(
			Map<String, List<String>> paths, EntityGroupInterface entityGroup)
			throws DynamicExtensionsSystemException
	{
		Map<String, List<AssociationInterface>> entityPaths = new HashMap<String, List<AssociationInterface>>();

		for (Entry<String, List<String>> entryObject : paths.entrySet())
		{
			// Path stored is from the root.
			List<String> pathsForEntity = entryObject.getValue();
			List<AssociationInterface> associations = new ArrayList<AssociationInterface>();
			Iterator<String> entNamesIter = pathsForEntity.iterator();

			String srcEntityName = entNamesIter.next();
			String associationRoleName = getAssociationRoleName(srcEntityName);

			EntityInterface sourceEntity = entityGroup
					.getEntityByName(getEntityNameExcludingAssociationRoleName(srcEntityName));
			String entityName = entryObject.getKey();
			CategoryValidator.checkForNullRefernce(sourceEntity,
					"ERROR IN DEFINING PATH FOR THE ENTITY " + entityName + ": ENTITY WITH NAME "
							+ srcEntityName + " DOES NOT EXIST");

			while (entNamesIter.hasNext())
			{
				String targetEntityName = entNamesIter.next();
				int size = associations.size();
				EntityInterface targetEntity = entityGroup
						.getEntityByName(getEntityNameExcludingAssociationRoleName(targetEntityName));
				addAssociation(sourceEntity, targetEntity, associationRoleName, associations);

				// Add all parent entity associations also to the list.
				EntityInterface parentEntity = sourceEntity.getParentEntity();
				while (parentEntity != null)
				{
					addAssociation(parentEntity, targetEntity, associationRoleName, associations);

					parentEntity = parentEntity.getParentEntity();
				}
				if (associations.size() == size)
				{
					throw new DynamicExtensionsSystemException(ApplicationProperties
							.getValue(CategoryConstants.CREATE_CAT_FAILS)
							+ "Error in defining path for the entity "
							+ entityName
							+ ": "
							+ sourceEntity.getName()
							+ " --> "
							+ targetEntityName
							+ "  Association does not exist.");
				}
				// Source entity should now be target entity.
				sourceEntity = targetEntity;
				associationRoleName = getAssociationRoleName(targetEntityName);
			}

			entityPaths.put(entityName, associations);

			if (pathsForEntity.size() > 1 && associations.isEmpty())
			{
				CategoryValidator.checkForNullRefernce(null, "ERROR: PATH DEFINED FOR THE ENTITY "
						+ entityName + " IS NOT CORRECT");
			}
		}

		return entityPaths;
	}

	/**
	 * Adds the association.
	 *
	 * @param sourceEntity
	 *            the source entity
	 * @param targetEntity
	 *            the target entity
	 * @param associationRoleName
	 *            the association role name
	 * @param associations
	 *            the associations
	 */
	private static void addAssociation(EntityInterface sourceEntity, EntityInterface targetEntity,
			String associationRoleName, List<AssociationInterface> associations)
	{
		for (AssociationInterface association : sourceEntity.getAssociationCollection())
		{
			if (association.getTargetEntity().equals(targetEntity))
			{
				if (associationRoleName != null && associationRoleName.length() > 0
						&& associationRoleName.equals(association.getTargetRole().getName()))
				{
					associations.add(association);
					break;
				}
				else if (associationRoleName != null && associationRoleName.length() == 0)
				{
					associations.add(association);
					break;
				}
			}
		}
	}

	/**
	 * Gets the entity name excluding association role name.
	 *
	 * @param entityName
	 *            the entity name
	 * @return the entity name excluding association role name
	 * @return
	 */
	public static String getEntityNameExcludingAssociationRoleName(String entityName)
	{
		String newEntityName = entityName;
		String associationRoleName = getFullAssociationRoleName(entityName);
		if (associationRoleName != null && associationRoleName.length() > 0)
		{
			newEntityName = entityName.replace(associationRoleName, "");
		}
		return newEntityName;
	}

	/**
	 * Gets the association role name.
	 *
	 * @param entityName
	 *            the entity name
	 * @return the association role name
	 * @return
	 */
	public static String getAssociationRoleName(String entityName)
	{
		String associationRoleName = "";
		if (entityName != null && entityName.indexOf('(') != -1 && entityName.indexOf(')') != -1)
		{
			associationRoleName = entityName.substring(entityName.indexOf('(') + 1, entityName
					.indexOf(')'));
		}
		return associationRoleName;
	}

	/**
	 * Gets the full association role name.
	 *
	 * @param entityName
	 *            the entity name
	 * @return the full association role name
	 * @return
	 */
	public static String getFullAssociationRoleName(String entityName)
	{
		String associationRoleName = "";
		if (entityName != null && entityName.indexOf('(') != -1 && entityName.indexOf(')') != -1)
		{
			associationRoleName = entityName.substring(entityName.indexOf('('), entityName
					.indexOf(')') + 1);
		}
		return associationRoleName;
	}

	/**
	 * Returns the entity group used for careting this category.
	 *
	 * @param category
	 *            the category
	 * @param entityGroupName
	 *            the entity group name
	 * @return the entity group
	 * @return
	 */
	public static EntityGroupInterface getEntityGroup(CategoryInterface category,
			String entityGroupName)
	{
		EntityGroupInterface entityGroup;
		if (category.getRootCategoryElement() != null)
		{
			entityGroup = category.getRootCategoryElement().getEntity().getEntityGroup();
		}

		entityGroup = EntityCache.getInstance().getEntityGroupByName(entityGroupName);
		return entityGroup;

	}

	/**
	 * category names in CSV are of format <entity_name>[instance_Number].
	 *
	 * @param categoryNameInCSV
	 *            the category name in csv
	 * @return the entity name
	 * @return
	 */
	public static String getEntityName(String categoryNameInCSV)
	{
		return getEntityNameExcludingAssociationRoleName(categoryNameInCSV.substring(0,
				categoryNameInCSV.indexOf('[')));
	}

	/**
	 * category names in CSV are of format <entity_name>[instance_Number].
	 *
	 * @param categoryEntityInstancePath
	 *            the category entity instance path
	 * @return the entity name for association map
	 * @return
	 */
	public static String getEntityNameForAssociationMap(String categoryEntityInstancePath)
	{
		StringBuffer entityNameForAssociationMap = new StringBuffer();
		String[] categoryEntityPathArray = categoryEntityInstancePath.split("->");

		for (String instancePath : categoryEntityPathArray)
		{
			entityNameForAssociationMap
					.append(instancePath.substring(0, instancePath.indexOf("[")));
		}
		return entityNameForAssociationMap.toString();
	}

	/**
	 * The path which are used for Root Cat Entity path should always be
	 * NAME[1]->Name[1] but user can only specify Name[1] & thus this method modifies the instance
	 * information if only NAME[1] is given to NAME[1]->NAME[1].
	 * @param categoryEntityPath paths.
	 * @return list of modified category entity paths.
	 */
	public static List<String> getCategoryEntityPath(String[] categoryEntityPath)
	{
		List<String> categoryEntityPathList = new ArrayList<String>();
		for (String categoryPath : categoryEntityPath)
		{
			if (!categoryPath.contains("->"))
			{
				categoryPath = categoryPath.concat("->" + categoryPath);
			}
			categoryEntityPathList.add(categoryPath);
		}
		return categoryEntityPathList;
	}

	/**
	 * category names in CSV are of format <entity_name>[instance_Number].
	 *
	 * @param categoryEntityInstancePath
	 *            the category entity instance path
	 * @return the entity name from category entity instance path
	 * @return
	 */
	public static String getEntityNameFromCategoryEntityInstancePath(
			String categoryEntityInstancePath)
	{
		String entityName = "";
		if (categoryEntityInstancePath != null)
		{
			String[] categoryEntitiesInPath = categoryEntityInstancePath.split("->");
			String newCatEntityName = categoryEntitiesInPath[categoryEntitiesInPath.length - 1];
			entityName = getEntityName(newCatEntityName);
		}
		return entityName;
	}

	/**
	 * category names in CSV are of format <entity_name>[instance_Number].
	 *
	 * @param assoList
	 *            the asso list
	 * @return the entity from entity association map
	 * @return
	 */
	public static EntityInterface getEntityFromEntityAssociationMap(
			List<AssociationInterface> assoList)
	{
		EntityInterface entityInterface = null;
		if (assoList != null && !assoList.isEmpty())
		{
			AssociationInterface association = assoList.get(assoList.size() - 1);
			entityInterface = association.getTargetEntity();
		}
		return entityInterface;
	}

	/**
	 * Sets the default value for calculated attributes.
	 *
	 * @param category
	 *            the category
	 * @param rootCatEntity
	 *            the root cat entity
	 * @param lineNumber
	 *            the line number
	 * @throws DynamicExtensionsApplicationException
	 *             the dynamic extensions application exception
	 * @throws DynamicExtensionsSystemException
	 *             the dynamic extensions system exception
	 */
	public static void setDefaultValueForCalculatedAttributes(CategoryInterface category,
			CategoryEntityInterface rootCatEntity, Long lineNumber)
			throws DynamicExtensionsApplicationException, DynamicExtensionsSystemException
	{

		for (CategoryAttributeInterface categoryAttributeInterface : rootCatEntity
				.getAllCategoryAttributes())
		{
			Boolean isCalcAttr = categoryAttributeInterface.getIsCalculated();
			if (isCalcAttr != null && isCalcAttr)
			{
				setDefaultValue(categoryAttributeInterface, category);
				FormulaCalculator formulaCalculator = new FormulaCalculator();
				String message = formulaCalculator.setDefaultValueForCalculatedAttributes(
						categoryAttributeInterface, rootCatEntity.getCategory());
				if (message != null && message.length() > 0)
				{
					throw new DynamicExtensionsSystemException(ApplicationProperties
							.getValue(CategoryConstants.CREATE_CAT_FAILS)
							+ ApplicationProperties.getValue(CategoryConstants.LINE_NUMBER)
							+ lineNumber + " " + message);
				}
			}
		}
		for (CategoryAssociationInterface categoryAssociationInterface : rootCatEntity
				.getCategoryAssociationCollection())
		{
			setDefaultValueForCalculatedAttributes(category, categoryAssociationInterface
					.getTargetCategoryEntity(), lineNumber);
		}
	}

	/**
	 * Gets the category attribute.
	 *
	 * @param entityName
	 *            the entity name
	 * @param instanceNumber
	 *            the instance number
	 * @param attributeName
	 *            the attribute name
	 * @param rootCatEntity
	 *            the root cat entity
	 * @param attributes
	 *            the attributes
	 * @return the category attribute
	 * @return
	 */
	public static void getCategoryAttribute(String entityName, Long instanceNumber,
			String attributeName, CategoryEntityInterface rootCatEntity,
			List<CategoryAttributeInterface> attributes)
	{
		if (rootCatEntity != null)
		{
			Long instanceId = getInstanceIdOfCategoryEntity(rootCatEntity);
			if (rootCatEntity.getEntity().getName().equals(entityName)
					&& instanceId.equals(instanceNumber))
			{
				for (CategoryAttributeInterface categoryAttributeInterface : rootCatEntity
						.getCategoryAttributeCollection())
				{
					if (categoryAttributeInterface.getAbstractAttribute().getName().equals(
							attributeName))
					{
						attributes.add(categoryAttributeInterface);
						return;
					}
				}
			}
			else
			{
				for (CategoryAssociationInterface categoryAssociationInterface : rootCatEntity
						.getCategoryAssociationCollection())
				{
					getCategoryAttribute(entityName, instanceNumber, attributeName,
							categoryAssociationInterface.getTargetCategoryEntity(), attributes);
				}
			}
		}
	}

	/**
	 * setDefaultValueforCalculatedAttributes.
	 *
	 * @param categoryAttribute
	 *            the category attribute
	 * @param categoryInterface
	 *            the category interface
	 * @throws DynamicExtensionsApplicationException
	 *             the dynamic extensions application exception
	 * @throws DynamicExtensionsSystemException
	 *             the dynamic extensions system exception
	 */
	private static void setDefaultValue(CategoryAttributeInterface categoryAttribute,
			CategoryInterface categoryInterface) throws DynamicExtensionsApplicationException,
			DynamicExtensionsSystemException
	{
		FormulaParser formulaParser = new FormulaParser();
		if (formulaParser.validateExpression(categoryAttribute.getFormula().getExpression()))
		{
			categoryAttribute.removeAllCalculatedCategoryAttributes();
			List<String> symbols = formulaParser.getSymobols();
			List<CategoryAttributeInterface> attributes = new ArrayList<CategoryAttributeInterface>();
			for (String symbol : symbols)
			{
				String[] names = symbol.split("_");
				if (names.length == 3)
				{
					attributes.clear();
					try
					{
						Long.parseLong(names[1]);
					}
					catch (NumberFormatException e)
					{
						throw new DynamicExtensionsSystemException(ApplicationProperties
								.getValue(CategoryConstants.CREATE_CAT_FAILS)
								+ " "
								+ Arrays.toString(names)
								+ ApplicationProperties
										.getValue("incorrectFormulaSyntaxCalculatedAttribute")
								+ categoryAttribute.getFormula().getExpression(), e);

					}
					CategoryGenerationUtil.getCategoryAttribute(names[0], Long.valueOf(names[1]),
							names[2], categoryInterface.getRootCategoryElement(), attributes);
					if (attributes.isEmpty())
					{
						throw new DynamicExtensionsSystemException(ApplicationProperties
								.getValue(CategoryConstants.CREATE_CAT_FAILS)
								+ " "
								+ Arrays.toString(names)
								+ ApplicationProperties
										.getValue("incorrectFormulaSyntaxCalculatedAttribute")
								+ categoryAttribute.getFormula().getExpression());
					}
					else
					{
						if (((AttributeMetadataInterface) attributes.get(0))
								.getAttributeTypeInformation() instanceof NumericAttributeTypeInformation
								|| ((AttributeMetadataInterface) attributes.get(0))
										.getAttributeTypeInformation() instanceof DateAttributeTypeInformation)
						{
							CalculatedAttributeInterface calculatedAttribute = DomainObjectFactory
									.getInstance().createCalculatedAttribute();
							calculatedAttribute.setCalculatedAttribute(categoryAttribute);
							calculatedAttribute.setSourceForCalculatedAttribute(attributes.get(0));
							calculatedAttribute.getSourceForCalculatedAttribute()
									.setIsSourceForCalculatedAttribute(Boolean.TRUE);
							categoryAttribute.addCalculatedCategoryAttribute(calculatedAttribute);
						}
						else
						{
							throw new DynamicExtensionsSystemException(ApplicationProperties
									.getValue(CategoryConstants.CREATE_CAT_FAILS)
									+ " "
									+ Arrays.toString(names)
									+ ApplicationProperties
											.getValue("incorrectDataTypeForCalculatedAttribute")
									+ categoryAttribute.getFormula().getExpression());
						}
					}
				}
				else
				{
					throw new DynamicExtensionsSystemException(ApplicationProperties
							.getValue(CategoryConstants.CREATE_CAT_FAILS)
							+ " "
							+ Arrays.toString(names)
							+ ApplicationProperties
									.getValue("incorrectFormulaSyntaxCalculatedAttribute")
							+ categoryAttribute.getFormula().getExpression());
				}
			}
		}
	}

	/**
	 * category names in CSV are of format <entity_name>[instance_Number] This
	 * method will generate category entity names according to the path.
	 *
	 * @param categoryEntityInstancePath
	 *            the category entity instance path
	 * @param entityNameAssociationMap
	 *            the entity name association map
	 * @return the category entity name
	 * @throws DynamicExtensionsSystemException
	 *             the dynamic extensions system exception
	 * @return
	 */
	public static String getCategoryEntityName(String categoryEntityInstancePath,
			Map<String, List<AssociationInterface>> entityNameAssociationMap)
			throws DynamicExtensionsSystemException
	{
		StringBuffer categoryEntityName = new StringBuffer();
		String entityNameForEntityAssocMap = getEntityNameForAssociationMap(categoryEntityInstancePath);

		//e.g Annotations[1]->PhysicalExam[2]
		String[] pathWithInstance = categoryEntityInstancePath.split("->");

		int counter = 0;
		if (entityNameAssociationMap.get(entityNameForEntityAssocMap) != null)
		{
			CategoryHelperInterface categoryHelper = new CategoryHelper();
			for (AssociationInterface association : entityNameAssociationMap
					.get(entityNameForEntityAssocMap))
			{
				String sourceEntityName = pathWithInstance[counter];
				if (sourceEntityName.indexOf('[') == -1 || sourceEntityName.indexOf(']') == -1)
				{
					throw new DynamicExtensionsSystemException(
							"ERROR: INSTANCE INFORMATION IS NOT IN THE CORRECT FORMAT "
									+ association);

				}
				if (counter == 0)
				{
					categoryEntityName.append(association.getEntity().getName());
					categoryEntityName.append(formatInstance(categoryHelper
							.getInsatnce(sourceEntityName)));
				}

				String targetEntityName = pathWithInstance[counter + 1];
				categoryEntityName.append(association.getTargetEntity().getName());
				categoryEntityName.append(formatInstance(categoryHelper
						.getInsatnce(targetEntityName)));

				counter++;
			}
		}
		if (categoryEntityName.length() == 0)
		{
			categoryEntityName.append(pathWithInstance[0]);
			categoryEntityName.append(pathWithInstance[0]);
		}
		return categoryEntityName.toString();
	}

	/**
	 * Format instance.
	 *
	 * @param insatnce
	 *            the insatnce
	 * @return the object
	 * @return
	 */
	private static Object formatInstance(Long insatnce)
	{
		StringBuffer categoryEntityName = new StringBuffer();
		categoryEntityName.append('[');
		categoryEntityName.append(insatnce);
		categoryEntityName.append(']');
		return categoryEntityName;
	}

	/**
	 * This method will verify weather all the values in the given collection are
	 * of Integer type or not.
	 * @param permissibleValueColl permisibleValueCollection
	 * @return true if all values are Integer else false
	 */
	public static boolean isAllPermissibleValuesInteger(
			Collection<PermissibleValueInterface> permissibleValueColl)
	{
		boolean allIntegerValues = false;
		Iterator<PermissibleValueInterface> itrPVInteger = permissibleValueColl.iterator();
		while (itrPVInteger.hasNext())
		{
			if (itrPVInteger.next() instanceof edu.common.dynamicextensions.domain.IntegerValue)
			{
				allIntegerValues = true;
			}
			else
			{
				allIntegerValues = false;
			}
		}
		return allIntegerValues;
	}

	/**
	 * This method will verify weather all the values in the given collection are
	 * of Double type or not.
	 * @param permissibleValueColl permisibleValueCollection
	 * @return true if all values are Double else false
	 */
	public static boolean isAllPermissibleValuesDouble(
			Collection<PermissibleValueInterface> permissibleValueColl)
	{
		boolean allDoubleValues = false; // NOPMD by gaurav_sawant on 5/6/10 2:57 PM
		Iterator<PermissibleValueInterface> itrPVInteger = permissibleValueColl.iterator();
		while (itrPVInteger.hasNext())
		{
			if (itrPVInteger.next() instanceof edu.common.dynamicextensions.domain.DoubleValue)
			{
				allDoubleValues = true;
			}
			else
			{
				allDoubleValues = false;
			}
		}
		return allDoubleValues;
	}

	/**
	 * This method will verify weather all the values in the given collection are
	 * of Float type or not.
	 * @param permissibleValueColl permisibleValueCollection
	 * @return true if all values are Float else false
	 */
	public static boolean isAllPermissibleValuesFloat(
			Collection<PermissibleValueInterface> permissibleValueColl)
	{
		boolean allFloatValues = false;
		Iterator<PermissibleValueInterface> itrPVInteger = permissibleValueColl.iterator();
		while (itrPVInteger.hasNext())
		{
			if (itrPVInteger.next() instanceof edu.common.dynamicextensions.domain.FloatValue)
			{
				allFloatValues = true;
			}
			else
			{
				allFloatValues = false;
			}
		}
		return allFloatValues;
	}

	/**
	 * This method will verify weather all the values in the given collection are
	 * of Short type or not.
	 * @param permissibleValueColl permisibleValueCollection
	 * @return true if all values are Short else false
	 */
	public static boolean isAllPermissibleValuesShort(
			Collection<PermissibleValueInterface> permissibleValueColl)
	{
		boolean allShortValues = false;
		Iterator<PermissibleValueInterface> itrPVInteger = permissibleValueColl.iterator();
		while (itrPVInteger.hasNext())
		{
			if (itrPVInteger.next() instanceof edu.common.dynamicextensions.domain.ShortValue)
			{
				allShortValues = true;
			}
			else
			{
				allShortValues = false;
			}
		}
		return allShortValues;
	}

	/**
	 * This method will verify weather all the values in the given collection are
	 * of Long type or not.
	 * @param permissibleValueColl permisibleValueCollection
	 * @return true if all values are Long else false
	 */
	public static boolean isAllPermissibleValuesLong(
			Collection<PermissibleValueInterface> permissibleValueColl)
	{
		boolean allLongValues = false;
		Iterator<PermissibleValueInterface> itrPVInteger = permissibleValueColl.iterator();
		while (itrPVInteger.hasNext())
		{
			if (itrPVInteger.next() instanceof edu.common.dynamicextensions.domain.LongValue)
			{
				allLongValues = true;
			}
			else
			{
				allLongValues = false;
			}
		}
		return allLongValues;
	}

	/**
	 * It will search in the given base directory & will find out all the category
	 * files present in the given directory.
	 * @param baseDirectory directory in which to search for the files.
	 * @param relativePath path used to reach the category files.
	 * @return list of the file names relative to the given base directory.
	 * @throws DynamicExtensionsSystemException exception.
	 */
	public static List<String> getCategoryFileListInDirectory(File baseDirectory,
			String relativePath) throws DynamicExtensionsSystemException
	{
		List<String> fileNameList = new ArrayList<String>();
		try
		{
			for (File file : baseDirectory.listFiles())
			{
				if (file.isDirectory())
				{
					String childDirPath = relativePath + file.getName() + "/";
					fileNameList.addAll(getCategoryFileListInDirectory(file, childDirPath));
				}
				else
				{
					if (file.getAbsolutePath().endsWith(".csv")
							|| file.getAbsolutePath().endsWith(".CSV"))
					{
						CategoryFileParser categoryFileParser = DomainObjectFactory.getInstance()
								.createCategoryFileParser(file.getAbsolutePath(), "", null);
						if (categoryFileParser != null)
						{
							if (categoryFileParser.isCategoryFile())
							{
								fileNameList.add(relativePath + file.getName());
							}
							categoryFileParser.closeResources();
						}
					}
				}
			}
		}
		catch (IOException e)
		{
			throw new DynamicExtensionsSystemException(
					"Exception occured while reading the category file names ", e);

		}
		return fileNameList;
	}

	/**
	 * It will search in the given base directory & will find out all the Permissible Value
	 * files present in the given directory.
	 * @param baseDirectory directory in which to search for the files.
	 * @param relativePath path used to reach the category files.
	 * @return list of the file names relative to the given base directory.
	 * @throws DynamicExtensionsSystemException exception.
	 */
	public static List<String> getPVFileListInDirectory(File baseDirectory, String relativePath)
			throws DynamicExtensionsSystemException
	{
		List<String> fileNameList = new ArrayList<String>();
		try
		{
			for (File file : baseDirectory.listFiles())
			{
				if (file.isDirectory())
				{
					String childDirPath = relativePath + file.getName() + "/";
					fileNameList.addAll(getPVFileListInDirectory(file, childDirPath));
				}
				else
				{
					if (file.getAbsolutePath().endsWith(".csv")
							|| file.getAbsolutePath().endsWith(".CSV"))
					{
						CategoryFileParser categoryFileParser = DomainObjectFactory.getInstance()
								.createCategoryFileParser(file.getAbsolutePath(), "", null);
						if (categoryFileParser != null && categoryFileParser.isPVFile())
						{
							fileNameList.add(relativePath + file.getName());
						}
						if (categoryFileParser != null)
						{
							categoryFileParser.closeResources();
						}
					}

				}
			}
		}
		catch (IOException e)
		{
			throw new DynamicExtensionsSystemException(
					"Exception occured while reading the Permissible Value file", e);
		}
		return fileNameList;
	}

	/**
	 * This method will return the Instance no of the entity used by this category Entity.
	 * @param catEntity category Entity
	 * @return Instance Id.
	 */
	public static long getInstanceIdOfCategoryEntity(CategoryEntityInterface catEntity)
	{

		long instanceId = 1;
		if (catEntity.getPath() != null)
		{
			List<PathAssociationRelationInterface> pathAssociationColl = catEntity.getPath()
					.getSortedPathAssociationRelationCollection();
			PathAssociationRelationInterface pathAssociation = pathAssociationColl
					.get(pathAssociationColl.size() - 1);
			instanceId = pathAssociation.getTargetInstanceId();
		}
		return instanceId;

	}

	/**
	 *
	 * @param dataElementCollection dataElementCollection
	 * @param hibernateDAO hibernateDAO
	 * @throws DynamicExtensionsSystemException exception.
	 */
	public static void populateDataElementCollectionFromDB(
			Collection<DataElementInterface> dataElementCollection, HibernateDAO hibernateDAO)
			throws DynamicExtensionsSystemException
	{
		CategoryManager.getInstance();
		for (DataElementInterface dataElement : dataElementCollection)
		{
			Collection<PermissibleValueInterface> pvCollection = ((UserDefinedDEInterface) dataElement)
					.getPermissibleValueCollection();
			Collection<PermissibleValueInterface> defaultPermissibleValues = ((UserDefinedDEInterface) dataElement)
					.getDefaultPermissibleValues();
			populatePVCollectionFromDB(defaultPermissibleValues, hibernateDAO);
			populatePVCollectionFromDB(pvCollection, hibernateDAO);
		}
	}

	/**
	 *
	 * @param pvCollection pvCollection
	 * @param hibernateDAO hibernateDAO
	 * @throws DynamicExtensionsSystemException exception.
	 */
	private static void populatePVCollectionFromDB(
			Collection<PermissibleValueInterface> pvCollection, HibernateDAO hibernateDAO)
			throws DynamicExtensionsSystemException
	{
		Collection<PermissibleValueInterface> newPvCollection = new HashSet<PermissibleValueInterface>();
		for (PermissibleValueInterface pValue : pvCollection)
		{
			if (pValue.getId() != null)
			{
				PermissibleValueInterface permissibleValueInterface = (PermissibleValueInterface) CategoryManager
						.getObjectByIdentifier(PermissibleValueInterface.class.getName(), pValue
								.getId().toString(), hibernateDAO);
				newPvCollection.add(permissibleValueInterface);
				populateSkipLogicPVs(pValue, hibernateDAO);
			}
		}
		if (!newPvCollection.isEmpty())
		{
			pvCollection.clear();
			pvCollection.addAll(newPvCollection);
		}
	}

	/**
	 * It will update entity with DB PV objects.
	 * @param categoryEntity entity.
	 * @param categoryHelper Categoryhelper.
	 * @param hibernateDAO HibernateDao.
	 * @throws DynamicExtensionsSystemException exception.
	 */
	public static void updateaEntityWithDBPVs(CategoryEntityInterface categoryEntity,
			CategoryHelperInterface categoryHelper, HibernateDAO hibernateDAO)
			throws DynamicExtensionsSystemException
	{
		updateAllEntityAttributeWithDBPVs(categoryEntity, categoryHelper, hibernateDAO);
		Collection<CategoryAssociationInterface> categoryAssociationInterfaces = categoryEntity
				.getCategoryAssociationCollection();

		for (CategoryAssociationInterface categoryAssociationInterface : categoryAssociationInterfaces)
		{
			CategoryEntityInterface tarCategoryEntityInterface = categoryAssociationInterface
					.getTargetCategoryEntity();
			updateaEntityWithDBPVs(tarCategoryEntityInterface, categoryHelper, hibernateDAO);
		}
	}

	/**
	 * It will update all attribute of entity with DB PV objects.
	 * @param categoryEntity entity.
	 * @param categoryHelper Category helper.
	 * @param hibernateDAO HibernateDao.
	 * @throws DynamicExtensionsSystemException exception.
	 */
	public static void updateAllEntityAttributeWithDBPVs(CategoryEntityInterface categoryEntity,
			CategoryHelperInterface categoryHelper, HibernateDAO hibernateDAO)
			throws DynamicExtensionsSystemException
	{
		Collection<CategoryAttributeInterface> attributes = categoryEntity
				.getAllCategoryAttributes();
		for (CategoryAttributeInterface categoryAttributeInterface : attributes)
		{
			populateDataElementCollectionFromDB(categoryAttributeInterface
					.getDataElementCollection(), hibernateDAO);
			populatePVCollectionFromDB(categoryAttributeInterface.getSkipLogicPermissibleValues(),
					hibernateDAO);
		}
	}

	/**
	 * This method will populate all the skip logic PVs and replace it with DB
	 * retrieved PV.
	 * @param pValue PermissibleValueInterface which skip logic attribute needs
	 * to be populate.
	 * @param hibernateDAO HibernateDAO.
	 * @throws DynamicExtensionsSystemException  DynamicExtensionsSystemException.
	 */
	private static void populateSkipLogicPVs(PermissibleValueInterface pValue,
			HibernateDAO hibernateDAO) throws DynamicExtensionsSystemException
	{
		if (pValue.getDependentSkipLogicAttributes() != null)
		{
			Collection<SkipLogicAttributeInterface> skipLogicAttributes = pValue
					.getDependentSkipLogicAttributes();
			for (SkipLogicAttributeInterface skipLogicAttributeInterface : skipLogicAttributes)
			{
				if (skipLogicAttributeInterface.getDataElement() != null)
				{
					Collection<PermissibleValueInterface> permissibleValueInterfaces = ((UserDefinedDEInterface) skipLogicAttributeInterface
							.getDataElement()).getPermissibleValueCollection();
					populatePVCollectionFromDB(permissibleValueInterfaces, hibernateDAO);
				}
			}
		}
	}

}