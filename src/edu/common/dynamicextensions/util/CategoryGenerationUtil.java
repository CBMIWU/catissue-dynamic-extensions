
package edu.common.dynamicextensions.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import edu.common.dynamicextensions.domain.CategoryEntity;
import edu.common.dynamicextensions.domaininterface.AssociationInterface;
import edu.common.dynamicextensions.domaininterface.CategoryEntityInterface;
import edu.common.dynamicextensions.domaininterface.CategoryInterface;
import edu.common.dynamicextensions.domaininterface.EntityGroupInterface;
import edu.common.dynamicextensions.domaininterface.EntityInterface;
import edu.common.dynamicextensions.domaininterface.userinterface.ContainerInterface;
import edu.common.dynamicextensions.entitymanager.EntityManager;
import edu.common.dynamicextensions.entitymanager.EntityManagerInterface;
import edu.common.dynamicextensions.exception.DynamicExtensionsApplicationException;
import edu.common.dynamicextensions.exception.DynamicExtensionsSystemException;
import edu.common.dynamicextensions.util.parser.CategoryCSVConstants;
import edu.common.dynamicextensions.validation.category.CategoryValidator;

/**
 * 
 * @author kunal_kamble
 *
 */
public class CategoryGenerationUtil
{

	/**
	 * This method returns the entity from the entity group.
	 * @param entityName
	 * @param entityGroup
	 * @return
	 */
	public static EntityInterface getEntity(String entityName, EntityGroupInterface entityGroup)
	{
		EntityInterface entityInterface = entityGroup.getEntityByName(entityName);
		return entityInterface;
	}

	/**
	 * Returns the multiplicity in number for the give string
	 * @param multiplicity
	 * @return
	 * @throws DynamicExtensionsSystemException 
	 */
	public static int getMultiplicityInNumbers(String multiplicity) throws DynamicExtensionsSystemException
	{
		int multiplicityI = 1;
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
			throw new DynamicExtensionsSystemException("ERROR: WRONG KEYWORD USED FOR MULTIPLICITY " + multiplicity
					+ ". VALID KEY WORDS ARE: 1- single 2-multiline");
		}
		return multiplicityI;
	}

	/**
	 * Sets the root category entity for the category.
	 * @param containerCollection
	 * @param paths
	 * @param absolutePath
	 * @return
	 * @throws DynamicExtensionsSystemException
	 * @throws DynamicExtensionsApplicationException
	 */
	public static void setRootContainer(CategoryInterface category, List<ContainerInterface> containerCollection,
			Map<String, List<AssociationInterface>> paths, Map<String, List<String>> absolutePath, Map<String, String> containerNameInstanceMap)
			throws DynamicExtensionsSystemException, DynamicExtensionsApplicationException
	{

		ContainerInterface rootContainer = null;
		for (ContainerInterface containerInterface : containerCollection)
		{
			CategoryEntityInterface categoryEntityInterface = (CategoryEntityInterface) containerInterface.getAbstractEntity();
			if (categoryEntityInterface.getTreeParentCategoryEntity() == null)
			{
				rootContainer = containerInterface;
				break;
			}
		}

		CategoryHelperInterface categoryHelper = new CategoryHelper();

		for (String entityName : absolutePath.keySet())
		{

			if (absolutePath.get(entityName).size() == 1 && rootContainer != null)
			{
				CategoryEntityInterface categoryEntityInterface = (CategoryEntityInterface) rootContainer.getAbstractEntity();
				if (!entityName.equals(categoryEntityInterface.getEntity().getName()))
				{
					ContainerInterface containerInterface = getContainerWithEntityName(containerCollection, entityName);
					if (containerInterface == null)
					{
						EntityGroupInterface entityGroup = categoryEntityInterface.getEntity().getEntityGroup();
						EntityInterface entity = entityGroup.getEntityByName(entityName);

						ContainerInterface newRootContainer = categoryHelper.createOrUpdateCategoryEntityAndContainer(entity, null, category,
								entityName + "[1]");
						newRootContainer.setAddCaption(false);

						categoryHelper.associateCategoryContainers(category, entityGroup, newRootContainer, rootContainer, paths
								.get(categoryEntityInterface.getEntity().getName()), 1, containerNameInstanceMap.get(rootContainer
								.getAbstractEntity().getName()));

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
			CategoryEntityInterface categoryEntity = ((CategoryEntityInterface) containerInterface.getAbstractEntity());
			boolean isTableCreated = ((CategoryEntity) categoryEntity).isCreateTable();
			if (rootContainer != containerInterface && categoryEntity.getTreeParentCategoryEntity() == null && isTableCreated)
			{
				categoryHelper.associateCategoryContainers(category, categoryEntity.getEntity().getEntityGroup(), rootContainer, containerInterface,
						paths.get(categoryEntity.getEntity().getName()), 1, containerNameInstanceMap.get(containerInterface.getAbstractEntity()
								.getName()));
			}
		}
		//If category is edited and no attributes from the main form of the model are not selected
		//rootContainer will be null
		//keep the root container unchanged in this case. Just use the root entity of the catgeory
		if (rootContainer == null)
		{
			EntityManagerInterface entityManagerInterface = EntityManager.getInstance();
			rootContainer = entityManagerInterface.getContainerByEntityIdentifier(category.getRootCategoryElement().getId());
		}
		categoryHelper.setRootCategoryEntity(rootContainer, category);

		categoryHelper.setRootCategoryEntity(rootContainer, category);
	}

	/**
	 * Retunrs the container having container caption as one passed to this method.
	 * @param containerCollection
	 * @param containerCaption
	 * @return
	 */
	public static ContainerInterface getContainer(List<ContainerInterface> containerCollection, String containerCaption)
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
	 * @param containerCollection
	 * @param entityName
	 * @return
	 */
	public static ContainerInterface getContainerWithEntityName(List<ContainerInterface> containerCollection, String entityName)
	{
		ContainerInterface container = null;
		for (ContainerInterface containerInterface : containerCollection)
		{
			CategoryEntityInterface categoryEntity = (CategoryEntityInterface) containerInterface.getAbstractEntity();
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
	 * @param containerCollection
	 * @param categoryEntityName
	 * @return
	 */
	public static ContainerInterface getContainerWithCategoryEntityName(List<ContainerInterface> containerCollection, String categoryEntityName)
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
	 * @param paths
	 * @param entityGroup
	 * @return
	 * @throws DynamicExtensionsSystemException 
	 */
	public static Map<String, List<AssociationInterface>> getAssociationList(Map<String, List<String>> paths, EntityGroupInterface entityGroup)
			throws DynamicExtensionsSystemException
	{
		Map<String, List<AssociationInterface>> listOfPath = new HashMap<String, List<AssociationInterface>>();

		Set<String> entityNames = paths.keySet();
		List<AssociationInterface> assocaitionList = new ArrayList<AssociationInterface>();
		
		for (String entityName : entityNames)
		{

			//Path stored is from the root. 
			List<String> list = paths.get(entityName);

			Iterator<String> namesIterator = list.iterator();
			assocaitionList.clear();
			String sourceEntityName = namesIterator.next();
			EntityInterface sourceEntity = entityGroup.getEntityByName(sourceEntityName);
			CategoryValidator.checkForNullRefernce(sourceEntity, "ERROR IN DEFINING PATH FOR THE ENTITY " + entityName + ": ENTITY WITH NAME "
					+ sourceEntityName + " DOES NOT EXIST");
			while (namesIterator.hasNext())
			{
				EntityInterface targetEntity = entityGroup.getEntityByName(namesIterator.next());
				for (AssociationInterface associationInterface : sourceEntity.getAssociationCollection())
				{
					if (associationInterface.getTargetEntity() == targetEntity)
					{
						assocaitionList.add(associationInterface);
					}
				}
				//Add all parententity association also to the list

				EntityInterface parentEntity = sourceEntity.getParentEntity();
				while (parentEntity != null)
				{
					for (AssociationInterface associationInterface : parentEntity.getAssociationCollection())
					{
						if (associationInterface.getTargetEntity() == targetEntity)
						{
							assocaitionList.add(associationInterface);
						}

					}
					parentEntity = parentEntity.getParentEntity();
				}
				//end
				sourceEntity = targetEntity;

			}
			listOfPath.put(entityName, assocaitionList);
			if (list.size() > 1 && assocaitionList.size() == 0)
			{
				CategoryValidator.checkForNullRefernce(null, "ERROR: PATH DEFINED FOR THE ENTITY " + entityName + " IS NOT CORRECT");
			}
		}

		return listOfPath;
	}

	/**
	 * This method gets the relative path.
	 * @param entityNameList ordered entities names in the path
	 * @param pathMap 
	 * @return
	 */
	public static List<String> getRelativePath(List<String> entityNameList, Map<String, List<String>> pathMap)
	{
		List<String> newEntityNameList = new ArrayList<String>();
		String lastEntityName = null;
		for (String entityName : entityNameList)
		{
			if (pathMap.get(entityName) == null)
			{
				newEntityNameList.add(entityName);
			}
			else
			{
				lastEntityName = entityName;
			}
		}
		if (lastEntityName != null && !newEntityNameList.contains(lastEntityName))
		{
			newEntityNameList.add(0, lastEntityName);
		}

		return newEntityNameList;
	}

	/**
	 * Returns the entity group used for careting this category 
	 * @param category
	 * @param entityGroupName
	 * @return
	 */
	public static EntityGroupInterface getEntityGroup(CategoryInterface category, String entityGroupName)
	{
		if (category.getRootCategoryElement() != null)
		{
			return category.getRootCategoryElement().getEntity().getEntityGroup();
		}

		return DynamicExtensionsUtility.retrieveEntityGroup(entityGroupName);

	}

	/**
	 * This method finds the main category entity.
	 * @param categoryPaths
	 * @return
	 */
	public static String getMainCategoryEntityName(String[] categoryPaths)
	{
		int minimumNumberOfCategoryEntityNames = categoryPaths[0].split("->").length;
		for (String string : categoryPaths)
		{
			if (minimumNumberOfCategoryEntityNames > string.split("->").length)
			{
				minimumNumberOfCategoryEntityNames = string.split("->").length;
			}
		}
		String categoryEntityName = categoryPaths[0].split("->")[0];

		a : for (int i = 0; i < minimumNumberOfCategoryEntityNames; i++)
		{
			String temp = categoryPaths[0].split("->")[i];
			for (String string : categoryPaths)
			{
				if (!string.split("->")[i].equals(temp))
				{
					break a;
				}
			}
			categoryEntityName = categoryPaths[0].split("->")[i];
		}

		return categoryEntityName;
	}

	/**
	 * category names in CSV are of format <entity_name>[instance_Nnmber]
	 * @param catgeoryNameInCSV
	 * @return
	 */
	public static String getEntityName(String catgeoryNameInCSV)
	{
		return catgeoryNameInCSV.substring(0, catgeoryNameInCSV.indexOf("["));
	}
}