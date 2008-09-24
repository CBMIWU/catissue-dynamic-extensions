
package edu.common.dynamicextensions.util.parser;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import edu.common.dynamicextensions.domain.CategoryAssociation;
import edu.common.dynamicextensions.domain.CategoryEntity;
import edu.common.dynamicextensions.domain.UserDefinedDE;
import edu.common.dynamicextensions.domain.userinterface.CategoryAssociationControl;
import edu.common.dynamicextensions.domaininterface.AbstractAttributeInterface;
import edu.common.dynamicextensions.domaininterface.AssociationInterface;
import edu.common.dynamicextensions.domaininterface.AttributeInterface;
import edu.common.dynamicextensions.domaininterface.CategoryAttributeInterface;
import edu.common.dynamicextensions.domaininterface.CategoryEntityInterface;
import edu.common.dynamicextensions.domaininterface.CategoryInterface;
import edu.common.dynamicextensions.domaininterface.EntityGroupInterface;
import edu.common.dynamicextensions.domaininterface.EntityInterface;
import edu.common.dynamicextensions.domaininterface.SemanticPropertyInterface;
import edu.common.dynamicextensions.domaininterface.userinterface.ContainerInterface;
import edu.common.dynamicextensions.domaininterface.userinterface.ControlInterface;
import edu.common.dynamicextensions.domaininterface.userinterface.ListBoxInterface;
import edu.common.dynamicextensions.exception.DynamicExtensionsApplicationException;
import edu.common.dynamicextensions.exception.DynamicExtensionsSystemException;
import edu.common.dynamicextensions.util.CategoryGenerationUtil;
import edu.common.dynamicextensions.util.CategoryHelper;
import edu.common.dynamicextensions.util.CategoryHelperInterface;
import edu.common.dynamicextensions.util.CategoryHelperInterface.ControlEnum;
import edu.common.dynamicextensions.util.global.CategoryConstants;
import edu.common.dynamicextensions.validation.category.CategoryValidator;
import edu.wustl.common.util.global.ApplicationProperties;

/**
 * @author kunal_kamble
 * This class creates the category/categories defined in
 * the csv file.
 */
public class CategoryGenerator
{
	private CategoryFileParser categoryFileParser;
	private static final String SET = "set";

	private CategoryValidator categoryValidator;

	List<String> mainFormList = new ArrayList<String>();

	public CategoryValidator getCategoryValidator()
	{
		return categoryValidator;
	}

	public void setCategoryValidator(CategoryValidator categoryValidator)
	{
		this.categoryValidator = categoryValidator;
	}

	/**
	 * @param filePath
	 * @throws DynamicExtensionsSystemException
	 * @throws FileNotFoundException
	 */
	public CategoryGenerator(String filePath) throws DynamicExtensionsSystemException, FileNotFoundException
	{
		categoryFileParser = new CategoryCSVFileParser(filePath);
		categoryValidator = new CategoryValidator((CategoryCSVFileParser) categoryFileParser);
	}

	/**
	 * @return
	 * @throws DynamicExtensionsSystemException
	 * @throws DynamicExtensionsApplicationException
	 * @throws ParseException
	 */
	public List<CategoryInterface> getCategoryList() throws DynamicExtensionsSystemException, DynamicExtensionsApplicationException, ParseException
	{
		CategoryHelperInterface categoryHelper = new CategoryHelper();
		List<CategoryInterface> categoryList = new ArrayList<CategoryInterface>();
		ApplicationProperties.initBundle(CategoryCSVConstants.DYNAMIC_EXTENSIONS_ERROR_MESSAGES_FILE);

		try
		{
			while (categoryFileParser.readNext())
			{
				// First line in the category file is Category_Definition.
				if (categoryFileParser.hasFormDefination())
				{
					continue;
				}

				// Category definition in the file starts.
				// 1: Read the category name
				CategoryInterface category = categoryHelper.getCategory(categoryFileParser.getCategoryName());

				// 2: Read the entity group.
				categoryFileParser.readNext();
				EntityGroupInterface entityGroup = CategoryGenerationUtil.getEntityGroup(category, categoryFileParser.getEntityGroupName());

				CategoryValidator.checkForNullRefernce(entityGroup, ApplicationProperties.getValue(CategoryConstants.LINE_NUMBER)
						+ categoryFileParser.getLineNumber() + ApplicationProperties.getValue("noEntityGroup")
						+ categoryFileParser.getEntityGroupName());

				categoryFileParser.getCategoryValidator().setEntityGroup(entityGroup);
				populateMainFormList(entityGroup);

				// 3: Get the path represented by ordered entity names.
				categoryFileParser.readNext();
				Map<String, List<String>> paths = categoryFileParser.getPaths();

				// 4: Get the association names list.
				Map<String, List<AssociationInterface>> entityNameAssociationMap = CategoryGenerationUtil.getAssociationList(paths, entityGroup);

				List<ContainerInterface> containerCollection = new ArrayList<ContainerInterface>();

				ContainerInterface containerInterface = null;
				EntityInterface entityInterface = null;

				// 5: Get the selected attributes and create the controls for them.
				String displayLabel = null;
				List<String> categoryEntityName = null;
				int sequenceNumber = 1;
				ControlInterface lastControl = null;
				Map<String, String> categoryEntityNameInstanceMap = new HashMap<String, String>();
				boolean hasRelatedAttributes = false;

				String previousEntityName = new String();
				boolean firstTimeinDisplayLabel = false;
				HashMap<String, List> sequenceMap = new HashMap<String, List>();

				while (categoryFileParser.readNext())
				{
					if (categoryFileParser.hasFormDefination())
					{
						break;
					}

					if (categoryFileParser.hasRelatedAttributes())
					{
						hasRelatedAttributes = true;
						break;
					}

					if (categoryFileParser.hasDisplayLable())
					{
						displayLabel = categoryFileParser.getDisplyLable();
						categoryEntityName = createForm(entityGroup, containerCollection, entityNameAssociationMap, category,
								categoryEntityNameInstanceMap);

						firstTimeinDisplayLabel = true;
						previousEntityName = null;
						categoryFileParser.readNext();
					}

					if (categoryFileParser.hasSubcategory())
					{
						//Set this flag when subcategory is just after display label
						if (firstTimeinDisplayLabel)
						{
							firstTimeinDisplayLabel = false;
						}
						ContainerInterface sourceContainer = null;
						if (entityInterface != null)
						{
							// Always add sub-category to the container.
							sourceContainer = CategoryGenerationUtil.getContainerWithCategoryEntityName(containerCollection, categoryEntityName
									.get(0));
						}
						else
						{
							sourceContainer = CategoryGenerationUtil.getContainer(containerCollection, displayLabel);
						}

						String targetContainerCaption = categoryFileParser.getTargetContainerCaption();
						ContainerInterface targetContainer = CategoryGenerationUtil.getContainer(containerCollection, targetContainerCaption);

						CategoryValidator
								.checkForNullRefernce(targetContainer, ApplicationProperties.getValue(CategoryConstants.LINE_NUMBER)
										+ categoryFileParser.getLineNumber() + ApplicationProperties.getValue("subcategoryNotFound")
										+ targetContainerCaption);

						String multiplicity = categoryFileParser.getMultiplicity();

						((CategoryEntityInterface) CategoryGenerationUtil.getContainer(containerCollection, targetContainerCaption)
								.getAbstractEntity()).getEntity();
						String entityName = ((CategoryEntityInterface) CategoryGenerationUtil.getContainer(containerCollection,
								targetContainerCaption).getAbstractEntity()).getEntity().getName();
						List<AssociationInterface> associationNameList = entityNameAssociationMap.get(entityName);
						CategoryValidator.checkForNullRefernce(associationNameList, ApplicationProperties.getValue(CategoryConstants.LINE_NUMBER)
								+ categoryFileParser.getLineNumber() + ApplicationProperties.getValue("pathNotFound") + targetContainerCaption);

						lastControl = categoryHelper.associateCategoryContainers(category, entityGroup, sourceContainer, targetContainer,
								associationNameList, CategoryGenerationUtil.getMultiplicityInNumbers(multiplicity), categoryEntityNameInstanceMap
										.get(targetContainer.getAbstractEntity().getName()));
					}
					else
					{
						// Add control to the container.
						Map<String, Collection<SemanticPropertyInterface>> permissibleValues = categoryFileParser.getPermissibleValues();

						String attributeName = categoryFileParser.getAttributeName();

						entityInterface = entityGroup.getEntityByName(categoryFileParser.getEntityName());

						// Added for category inheritance, check if a given attribute is parent category attribute.
						boolean isAttributePresent = entityInterface.isAttributePresent(attributeName);

						boolean isAttributeCategoryMatched = false;
						CategoryValidator.checkForNullRefernce(getcategoryEntityName(categoryEntityName, categoryFileParser.getEntityName()),
								ApplicationProperties.getValue(CategoryConstants.LINE_NUMBER) + categoryFileParser.getLineNumber()
										+ categoryEntityName + ApplicationProperties.getValue("incorrectInstanceInformation"));
						containerInterface = CategoryGenerationUtil.getContainerWithCategoryEntityName(containerCollection, getcategoryEntityName(
								categoryEntityName, categoryFileParser.getEntityName()));

						//Set this flag when attribute is just after display label,and set previous entity name ,so that we can verify whether all attributes are of same entity or other entities also.
						if (firstTimeinDisplayLabel)
						{
							firstTimeinDisplayLabel = false;
							previousEntityName = ((CategoryEntityInterface) containerInterface.getAbstractEntity()).getName();
						}
						if ((previousEntityName != null && !previousEntityName.equals(containerInterface.getAbstractEntity().getName()) && !firstTimeinDisplayLabel))
						{
							List listofEntities = sequenceMap.get(containerInterface.getAbstractEntity().getName());
							if (listofEntities == null)
							{
								listofEntities = new ArrayList<String>();
							}
							listofEntities.add(containerInterface.getAbstractEntity().getName());
							sequenceMap.put(previousEntityName, listofEntities);
						}
						AttributeInterface attribute = entityInterface.getAttributeByName(attributeName);

						if (attribute != null && permissibleValues != null)
						{
							UserDefinedDE userDefinedDE = (UserDefinedDE) attribute.getAttributeTypeInformation().getDataElement();
							if (userDefinedDE == null)
							{
								throw new DynamicExtensionsSystemException(ApplicationProperties.getValue(CategoryConstants.CREATE_CAT_FAILS)
										+ ApplicationProperties.getValue("noPVForAttribute") + attributeName);
							}
						}

						CategoryValidator.checkForNullRefernce(attribute, ApplicationProperties.getValue(CategoryConstants.LINE_NUMBER)
								+ categoryFileParser.getLineNumber() + ApplicationProperties.getValue("attribute") + " " + attributeName
								+ ApplicationProperties.getValue("attributeNotPresent") + entityInterface.getName());

						if (previousEntityName != null && !previousEntityName.equals(containerInterface.getAbstractEntity().getName()))
						{
							previousEntityName = containerInterface.getAbstractEntity().getName();
						}
						// If this is a parent attribute and currently the parent category entity is not created
						// for given category entity, create parent category hierarchy up to where attribute is found.
						if (!isAttributePresent)
						{
							EntityInterface parentEntity = entityInterface.getParentEntity();
							EntityInterface childEntity = entityInterface;
							CategoryEntityInterface childCategoryEntity = (CategoryEntityInterface) containerInterface.getAbstractEntity();
							CategoryEntityInterface parentCategoryEntity = childCategoryEntity.getParentCategoryEntity();

							while (childEntity.getParentEntity() != null)
							{
								parentEntity = childEntity.getParentEntity();

								// Check whether the given category entity's parent category entity is created,
								// if not created, create it.
								if (parentCategoryEntity == null)
								{
									ContainerInterface parentContainer = createParentCategoryEntity(childCategoryEntity, parentEntity, entityGroup,
											containerCollection);
									parentCategoryEntity = (CategoryEntityInterface) parentContainer.getAbstractEntity();
									ContainerInterface childcontainerInterface = CategoryGenerationUtil.getContainerWithCategoryEntityName(
											containerCollection, childCategoryEntity.getName());
									childcontainerInterface.setBaseContainer(parentContainer);
								}

								// Iterate over parent entity's attribute, check whether it is present in parent entity.
								Iterator<AbstractAttributeInterface> parentattrIterator = parentEntity.getAbstractAttributeCollection().iterator();
								while (parentattrIterator.hasNext())
								{
									AbstractAttributeInterface objParentAttribute = parentattrIterator.next();
									if (attributeName.equals(objParentAttribute.getName()))
									{
										isAttributeCategoryMatched = true;
										break;
									}
								}

								childEntity = childEntity.getParentEntity();
								childCategoryEntity = parentCategoryEntity;
								parentCategoryEntity = parentCategoryEntity.getParentCategoryEntity();

								// If attribute is found in parent category entity, break out of loop.
								if (isAttributeCategoryMatched)
									break;
							}

							entityInterface = childEntity;
							//containerInterface = (ContainerInterface) (childCategoryEntity.getContainerCollection()).iterator().next();
						}

						Map<String, Object> rules = categoryFileParser.getRules(attribute.getName());
						if (rules != null)
						{
							CategoryValidator.checkRangeAgainstAttributeValueRange(attribute, rules);
							CategoryValidator.checkRequiredRule(attribute, rules);
						}

						String controlType = categoryFileParser.getControlType();
						getCategoryValidator().isTextAreaForNumeric(controlType, attribute);
						lastControl = categoryHelper.addOrUpdateControl(entityInterface, attributeName, containerInterface, ControlEnum
								.get(controlType), categoryFileParser.getControlCaption(), rules, permissibleValues);

						// Set default value for attribute's IsRelatedAttribute and IsVisible property.
						// This is required in case of edit of category entity.
						((CategoryAttributeInterface) lastControl.getAttibuteMetadataInterface()).setIsRelatedAttribute(false);
						((CategoryAttributeInterface) lastControl.getAttibuteMetadataInterface()).setIsVisible(true);

						// Set default value for control's option.
						// This is required in case of edit of category entity.
						categoryHelper.setDefaultControlsOptions(lastControl, ControlEnum.get(categoryFileParser.getControlType()));

						// Clear category entity from related attribute collection of root category entity.
						category.removeRelatedAttributeCategoryEntity((CategoryEntityInterface) containerInterface.getAbstractEntity());

						setControlsOptions(lastControl);
						setDefaultValue(lastControl);

						if (lastControl != null && lastControl instanceof ListBoxInterface)
						{
							CategoryValidator.checkIsMultiSelectValid(entityInterface, attributeName, lastControl);
						}

						// Check for isReadOnly option.
						if (lastControl.getIsReadOnly())
						{
							((CategoryAttributeInterface) lastControl.getAttibuteMetadataInterface()).setIsRelatedAttribute(true);
							((CategoryAttributeInterface) lastControl.getAttibuteMetadataInterface()).setIsVisible(true);
							category.addRelatedAttributeCategoryEntity((CategoryEntityInterface) containerInterface.getAbstractEntity());
						}
					}
					lastControl.setSequenceNumber(sequenceNumber++);
				}

				CategoryGenerationUtil
						.setRootContainer(category, containerCollection, entityNameAssociationMap, paths, categoryEntityNameInstanceMap);

				if (hasRelatedAttributes)
				{
					handleRelatedAttributes(entityGroup, category, entityNameAssociationMap, containerCollection);
				}
				rearrangeControlSequence((ContainerInterface) category.getRootCategoryElement().getContainerCollection().iterator().next(),
						sequenceMap);
				categoryList.add(category);

			}

		}
		catch (FileNotFoundException e)
		{
			throw new DynamicExtensionsSystemException(ApplicationProperties.getValue(CategoryConstants.CREATE_CAT_FAILS)
					+ ApplicationProperties.getValue("fileNotFound"), e);
		}
		catch (IOException e)
		{
			throw new DynamicExtensionsSystemException(ApplicationProperties.getValue(CategoryConstants.CREATE_CAT_FAILS)
					+ ApplicationProperties.getValue(CategoryConstants.LINE_NUMBER) + categoryFileParser.getLineNumber() + " "
					+ ApplicationProperties.getValue("readingFile") + categoryFileParser.getFilePath(), e);
		}
		catch (Exception e)
		{
			if (!(e instanceof DynamicExtensionsSystemException))
			{
				throw new DynamicExtensionsSystemException(ApplicationProperties.getValue(CategoryConstants.CREATE_CAT_FAILS)
						+ ApplicationProperties.getValue(CategoryConstants.LINE_NUMBER) + categoryFileParser.getLineNumber() + " "
						+ ApplicationProperties.getValue("readingFile") + categoryFileParser.getFilePath(), e);
			}

			throw new DynamicExtensionsSystemException("", e);
		}
		return categoryList;
	}

	/**
	 * This function rearranges the sequences ,so that display is as per CSV file
	 * @param containerObject
	 * @param sequenceMap
	 */
	private void rearrangeControlSequence(ContainerInterface containerObject, HashMap<String, List> sequenceMap)
	{
		//The sequencemap contains the keyentityname and list of categoryentity which requires to set after this entity on UI
		//See for eg. root categoryentity is having 4 controls(category association + othercontrol) ,its sequence number is assigned as  per their parsing
		//But in one association is specified as with other association under one display label itself ,so only one subcategory name is under rootcategory in CSV
		//eg. DL :XXX
		//    class A:A1
		//    class B:B1
		//    DL: root
		//    subcategory:XXX
		//  So here both attributs will be shown under same display label XXX ,so we need to change category association B's sequence number such that its after class A on UI
		//
		List<ControlInterface> listControl = containerObject.getAllControls();
		rearrangeSequenceMap(sequenceMap);
		Iterator<String> iterator = sequenceMap.keySet().iterator();
		while (iterator.hasNext())
		{
			String keyName = (String) iterator.next();
			List<String> entityNameList = sequenceMap.get(keyName);
			int newSequenceNumber = 0;
			for (String entityName : entityNameList)
			{
				int previousSequenceNumber = 0;
				for (ControlInterface objControl : listControl)
				{
					if (objControl instanceof CategoryAssociationControl)
					{
						CategoryAssociationControl obj = ((CategoryAssociationControl) objControl);
						String categoryName = ((CategoryAssociation) obj.getBaseAbstractAttribute()).getTargetCategoryEntity().getName();
						if (categoryName.equals(keyName))
						{
							previousSequenceNumber = objControl.getSequenceNumber();
							break;
						}

					}
				}

				boolean modified = false;
				for (int index = 0; index < listControl.size(); index++)
				{
					ControlInterface objControl = (ControlInterface) listControl.get(index);
					if (objControl instanceof CategoryAssociationControl)
					{
						CategoryAssociationControl obj = ((CategoryAssociationControl) objControl);
						String categoryName = ((CategoryAssociation) obj.getBaseAbstractAttribute()).getTargetCategoryEntity().getName();
						if (categoryName.equals(entityName))
						{

							if (newSequenceNumber == 0)
								newSequenceNumber = ++previousSequenceNumber;
							else
								newSequenceNumber++;
							objControl.setSequenceNumber(newSequenceNumber);

							modified = true;

						}
						if (modified)
						{
							for (int j = 0; j < listControl.size(); j++)
							{
								ControlInterface controlObj = listControl.get(j);
								if (controlObj instanceof CategoryAssociationControl)
								{

									String targetcategoryName = ((CategoryAssociation) controlObj.getBaseAbstractAttribute())
											.getTargetCategoryEntity().getName();
									if (targetcategoryName.equals(categoryName))
									{
										//skip its the same control ,whose sequence is modified.
									}

									else if (controlObj.getSequenceNumber() == newSequenceNumber)
									{
										controlObj.setSequenceNumber(++newSequenceNumber);

									}
									else if (controlObj.getSequenceNumber() >= newSequenceNumber)
									{
										//set all other control who has higher sequence number,increment it by 1
										controlObj.setSequenceNumber(controlObj.getSequenceNumber() + 1);
									}
								}
							}
							break;

						}
					}
				}

			}

		}

	}

	/**
	 * @param sequenceMap
	 */
	private void rearrangeSequenceMap(HashMap<String, List> sequenceMap)
	{

		List<String> toberemoveKey = new ArrayList<String>();
		Iterator<String> iterator = sequenceMap.keySet().iterator();
		while (iterator.hasNext())
		{
			String keyName = (String) iterator.next();
			List<String> listofEntities = sequenceMap.get(keyName);
			List<String> elist = new ArrayList<String>();
			elist.addAll(listofEntities);
			findEntities(sequenceMap, listofEntities, toberemoveKey, elist);
			sequenceMap.put(keyName, elist);
		}
		for (String key : toberemoveKey)
		{
			sequenceMap.remove(key);
		}

	}

	/**
	 * @param sequenceMap
	 * @param listofEntities
	 * @param toberemoveKey
	 * @param elist
	 */
	private void findEntities(HashMap<String, List> sequenceMap, List<String> listofEntities, List<String> toberemoveKey, List<String> elist)
	{

		for (String ename : listofEntities)
		{
			if (sequenceMap.keySet().contains(ename))
			{
				List<String> additionalList = sequenceMap.get(ename);
				elist.addAll(additionalList);
				findEntities(sequenceMap, additionalList, toberemoveKey, elist);
				toberemoveKey.add(ename);
			}
		}

	}

	/**
	 * @param childCategoryEntity
	 * @param parentEntity
	 * @param entityGroup
	 * @param containerCollection
	 * @return
	 * @throws DynamicExtensionsSystemException
	 */
	private ContainerInterface createParentCategoryEntity(CategoryEntityInterface childCategoryEntity, EntityInterface parentEntity,
			EntityGroupInterface entityGroup, Collection<ContainerInterface> containerCollection) throws DynamicExtensionsSystemException
	{
		String newCategoryEntityName = parentEntity.getName() + "[1]";
		for (ContainerInterface objContainer : containerCollection)
		{
			if (objContainer.getCaption().equals(newCategoryEntityName))
			{
				return objContainer;
			}

		}

		CategoryHelper categoryHelper = new CategoryHelper();
		CategoryInterface parentCategory = categoryHelper.getCategory(newCategoryEntityName);

		ContainerInterface parentContainer = createCategoryEntityAndContainer(entityGroup.getEntityByName(parentEntity.getName()),
				newCategoryEntityName, newCategoryEntityName, false, containerCollection, parentCategory);
		((CategoryEntityInterface) childCategoryEntity).setParentCategoryEntity((CategoryEntityInterface) parentContainer.getAbstractEntity());
		CategoryEntity parentCEntity = (CategoryEntity) parentContainer.getAbstractEntity();
		parentCEntity.addChildCategory(childCategoryEntity);

		CategoryEntity parentCategoryEntity = (CategoryEntity) ((CategoryEntityInterface) childCategoryEntity).getParentCategoryEntity();
		parentCategoryEntity.setCreateTable(false);
		return parentContainer;
	}

	/**
	 * @param entityGroup
	 * @param category
	 * @param entityNameAssociationMap
	 * @throws IOException
	 * @throws ParseException
	 * @throws DynamicExtensionsSystemException
	 * @throws DynamicExtensionsApplicationException
	 */
	private void handleRelatedAttributes(EntityGroupInterface entityGroup, CategoryInterface category,
			Map<String, List<AssociationInterface>> entityNameAssociationMap, List<ContainerInterface> containerCollection) throws IOException,
			ParseException, DynamicExtensionsSystemException, DynamicExtensionsApplicationException
	{
		while (categoryFileParser.readNext())
		{
			String[] categoryPaths = categoryFileParser.getCategoryPaths();
			String[] categoryEntitysInPath = categoryPaths[0].split("->");
			String categoryEntityName = categoryEntitysInPath[categoryEntitysInPath.length - 1];

			String entityName = categoryEntityName.substring(0, categoryEntityName.indexOf("["));
			EntityInterface entity = entityGroup.getEntityByName(entityName);
			categoryFileParser.readNext();
			String attributeName = categoryFileParser.getRelatedAttributeName();
			CategoryHelperInterface categoryHelper = new CategoryHelper();
			boolean newCategoryCreated = false;
			if (category.getCategoryEntityByName(categoryEntityName) == null)
			{
				newCategoryCreated = true;
			}
			CategoryEntityInterface categoryEntity = categoryHelper.createOrUpdateCategoryEntity(category, entity, categoryEntityName);

			if (newCategoryCreated)
			{
				String associationName = category.getRootCategoryElement() + " to " + categoryEntity.getName() + " association";
				categoryHelper.associateCategoryEntities(category.getRootCategoryElement(), categoryEntity, associationName, 1, entityGroup,
						entityNameAssociationMap.get(entityName), categoryPaths[0]);
			}
			CategoryValidator.checkForNullRefernce(entity.getAttributeByName(attributeName), ApplicationProperties
					.getValue(CategoryConstants.LINE_NUMBER)
					+ categoryFileParser.getLineNumber()
					+ ApplicationProperties.getValue("attribute")
					+ attributeName
					+ ApplicationProperties.getValue("attributeNotPresent") + entity.getName());

			// Added for category inheritance.
			boolean isAttributePresent = entity.isAttributePresent(attributeName);
			boolean isAttributeCategoryMatched = false;

			// If this is the parent attribute and currently the parent category entity is not created
			// for given category entity, create parent category hierarchy up to where attribute is found.
			if (!isAttributePresent)
			{
				EntityInterface parentEntity = entity.getParentEntity();
				EntityInterface childEntity = entity;
				CategoryEntityInterface childCategoryEntity = categoryEntity;
				CategoryEntityInterface parentCategoryEntity = childCategoryEntity.getParentCategoryEntity();

				while (childEntity.getParentEntity() != null)
				{
					parentEntity = childEntity.getParentEntity();

					// Check whether the given cat.entity's parent category entity is created.
					// If not created, then create it.
					if (parentCategoryEntity == null)
					{
						ContainerInterface parentContainer = createParentCategoryEntity(childCategoryEntity, parentEntity, entityGroup,
								containerCollection);
						parentCategoryEntity = (CategoryEntityInterface) parentContainer.getAbstractEntity();

						ContainerInterface childcontainerInterface = CategoryGenerationUtil.getContainerWithCategoryEntityName(containerCollection,
								childCategoryEntity.getName());
						childcontainerInterface.setBaseContainer(parentContainer);
					}

					// Iterate over parent entity's attribute, check whether its present in parent entity.
					Iterator<AbstractAttributeInterface> parentattrIterator = parentEntity.getAbstractAttributeCollection().iterator();

					while (parentattrIterator.hasNext())
					{
						AbstractAttributeInterface objParentAttribute = parentattrIterator.next();
						if (attributeName.equals(objParentAttribute.getName()))
						{
							isAttributeCategoryMatched = true;
							break;
						}
					}

					childEntity = childEntity.getParentEntity();
					childCategoryEntity = parentCategoryEntity;
					parentCategoryEntity = parentCategoryEntity.getParentCategoryEntity();

					// If attribute found in parent category entity, break out of loop.
					if (isAttributeCategoryMatched)
						break;
				}

				entity = childEntity;
				categoryEntity = childCategoryEntity;
			}

			CategoryAttributeInterface categoryAttribute = categoryHelper.createCategoryAttribute(entity, attributeName, categoryEntity);

			String defaultValue = categoryFileParser.getDefaultValueForRelatedAttribute();
			categoryAttribute.setDefaultValue(entity.getAttributeByName(attributeName).getAttributeTypeInformation().getPermissibleValueForString(
					defaultValue));
			categoryAttribute.setIsVisible(false);
			categoryAttribute.setIsRelatedAttribute(true);
			category.addRelatedAttributeCategoryEntity(categoryEntity);

			if (newCategoryCreated)
			{
				String associationName = category.getRootCategoryElement() + " to " + categoryEntity.getName() + " association";
				categoryHelper.associateCategoryEntities(category.getRootCategoryElement(), categoryEntity, associationName, 1, entityGroup,
						entityNameAssociationMap.get(entityName), categoryPaths[0]);
			}
		}
	}

	/**
	 * @param categoryEntityNameList
	 * @param entityName
	 * @return
	 */
	private String getcategoryEntityName(List<String> categoryEntityNameList, String entityName)
	{
		String categoryEntityName = null;
		for (String string : categoryEntityNameList)
		{
			if (entityName.equals(string.substring(0, string.indexOf("["))))
			{
				categoryEntityName = string;
			}
		}
		return categoryEntityName;
	}

	/**
	 * @param entityInterface
	 * @param categoryEntityName
	 * @param displayLable
	 * @param showCaption
	 * @param containerCollection
	 * @return
	 * @throws DynamicExtensionsSystemException
	 */
	private ContainerInterface createCategoryEntityAndContainer(EntityInterface entityInterface, String categoryEntityName, String displayLable,
			Boolean showCaption, Collection<ContainerInterface> containerCollection, CategoryInterface category)
			throws DynamicExtensionsSystemException
	{
		ContainerInterface containerInterface = null;
		CategoryHelperInterface categoryHelper = new CategoryHelper();

		containerInterface = categoryHelper.createOrUpdateCategoryEntityAndContainer(entityInterface, displayLable, category, categoryEntityName);

		containerInterface.setAddCaption(showCaption);

		containerCollection.add(containerInterface);

		return containerInterface;
	}

	/**
	 * @param control
	 * @param nextLine
	 * @throws DynamicExtensionsSystemException
	 */
	private void setControlsOptions(ControlInterface control) throws DynamicExtensionsSystemException
	{
		try
		{
			Map<String, String> controlOptions = categoryFileParser.getControlOptions();

			if (controlOptions.isEmpty())
			{
				return;
			}
			for (String optionString : controlOptions.keySet())
			{
				String methodName = SET + optionString;

				Class[] types = getParameterType(methodName, control);
				if (types.length < 1)
				{
					throw new DynamicExtensionsSystemException(ApplicationProperties.getValue(CategoryConstants.CREATE_CAT_FAILS)
							+ ApplicationProperties.getValue(CategoryConstants.LINE_NUMBER) + categoryFileParser.getLineNumber()
							+ ApplicationProperties.getValue("incorrectControlOption") + optionString);
				}
				List<Object> values = new ArrayList<Object>();
				values.add(getFormattedValues(types[0], controlOptions.get(optionString)));

				Method method;

				method = control.getClass().getMethod(methodName, types);

				method.invoke(control, values.toArray());
			}
		}
		catch (SecurityException e)
		{
			throw new DynamicExtensionsSystemException(ApplicationProperties.getValue(CategoryConstants.CREATE_CAT_FAILS)
					+ ApplicationProperties.getValue(CategoryConstants.CONTACT_ADMIN), e);
		}
		catch (NoSuchMethodException e)
		{
			throw new DynamicExtensionsSystemException(ApplicationProperties.getValue(CategoryConstants.CREATE_CAT_FAILS)
					+ ApplicationProperties.getValue(CategoryConstants.LINE_NUMBER) + categoryFileParser.getLineNumber()
					+ ApplicationProperties.getValue("incorrectOption"), e);
		}
		catch (IllegalArgumentException e)
		{
			throw new DynamicExtensionsSystemException(ApplicationProperties.getValue(CategoryConstants.CREATE_CAT_FAILS)
					+ ApplicationProperties.getValue(CategoryConstants.CONTACT_ADMIN), e);
		}
		catch (IllegalAccessException e)
		{
			throw new DynamicExtensionsSystemException(ApplicationProperties.getValue(CategoryConstants.CREATE_CAT_FAILS)
					+ ApplicationProperties.getValue(CategoryConstants.CONTACT_ADMIN), e);
		}
		catch (InvocationTargetException e)
		{
			throw new DynamicExtensionsSystemException(ApplicationProperties.getValue(CategoryConstants.CREATE_CAT_FAILS)
					+ ApplicationProperties.getValue(CategoryConstants.CONTACT_ADMIN), e);
		}
		catch (InstantiationException e)
		{
			throw new DynamicExtensionsSystemException(ApplicationProperties.getValue(CategoryConstants.CREATE_CAT_FAILS)
					+ ApplicationProperties.getValue(CategoryConstants.CONTACT_ADMIN), e);
		}
	}

	/**
	 * This meth
	 * @param methodName
	 * @param object
	 * @return
	 */
	private Class[] getParameterType(String methodName, Object object)
	{
		Class[] parameterTypes = new Class[0];
		for (Method method : object.getClass().getMethods())
		{
			if (methodName.equals(method.getName()))
			{
				parameterTypes = method.getParameterTypes();
			}
		}

		return parameterTypes;
	}

	/**
	 * @param type
	 * @param string
	 * @return
	 * @throws IllegalAccessException
	 * @throws InstantiationException
	 * @throws NoSuchMethodException
	 * @throws SecurityException
	 * @throws InvocationTargetException
	 * @throws IllegalArgumentException
	 */
	private Object getFormattedValues(Class type, String string) throws SecurityException, NoSuchMethodException, InstantiationException,
			IllegalAccessException, IllegalArgumentException, InvocationTargetException
	{
		Method method = type.getMethod("valueOf", new Class[]{String.class});
		return method.invoke(type, new Object[]{string});
	}

	/**
	 * @param entityGroup
	 * @param containerCollection
	 * @param associationNamesMap
	 * @param category
	 * @param categoryEntityNameInstanceMap
	 * @return
	 * @throws DynamicExtensionsSystemException
	 * @throws DynamicExtensionsApplicationException
	 */
	private List<String> createForm(EntityGroupInterface entityGroup, List<ContainerInterface> containerCollection,
			Map<String, List<AssociationInterface>> associationNamesMap, CategoryInterface category, Map<String, String> categoryEntityNameInstanceMap)
			throws DynamicExtensionsSystemException, DynamicExtensionsApplicationException
	{
		String displayLable = categoryFileParser.getDisplyLable();
		Boolean showCaption = categoryFileParser.isShowCaption();

		List<String> categoryEntityName = new ArrayList<String>();
		String entityName = null;
		String[] categoryEntitiesInPath;

		try
		{
			categoryFileParser.readNext();
			String[] categoryPaths = categoryFileParser.getCategoryPaths();

			for (String categoryPath : categoryPaths)
			{
				categoryEntitiesInPath = categoryPath.split("->");
				String newCategoryEntityName = categoryEntitiesInPath[categoryEntitiesInPath.length - 1];
				entityName = newCategoryEntityName.substring(0, newCategoryEntityName.indexOf("["));

				// Check if instance information is wrong, i.e. entity mentioned in
				// the instance information exists in the entity group.
				for (String catEntName : categoryEntitiesInPath)
				{
					String entName = catEntName.substring(0, catEntName.indexOf("["));

					if (entityGroup.getEntityByName(entName) == null)
					{
						throw new DynamicExtensionsSystemException(ApplicationProperties.getValue(CategoryConstants.CREATE_CAT_FAILS)
								+ ApplicationProperties.getValue(CategoryConstants.LINE_NUMBER) + categoryFileParser.getLineNumber() + " "
								+ ApplicationProperties.getValue(CategoryConstants.WRONG_INST_INFO) + entName);
					}
				}

				categoryValidator.isRootEntityUsedTwice(entityName, mainFormList, categoryEntityNameInstanceMap.keySet());
				if (!categoryEntityName.contains(newCategoryEntityName))
				{

					ContainerInterface container = null;
					container = SearchExistingCategoryEntityAndContainer(newCategoryEntityName, containerCollection);
					if (container == null)
					{
						container = createCategoryEntityAndContainer(entityGroup.getEntityByName(entityName), newCategoryEntityName, displayLable,
								showCaption, containerCollection, category);
					}

					categoryEntityNameInstanceMap.put(container.getAbstractEntity().getName(), getCategoryPath(categoryPaths, newCategoryEntityName));

					categoryEntityName.add(newCategoryEntityName);
					showCaption = false;
				}
			}
		}
		catch (IOException exception)
		{
			throw new DynamicExtensionsSystemException(ApplicationProperties.getValue(CategoryConstants.CREATE_CAT_FAILS)
					+ ApplicationProperties.getValue(CategoryConstants.LINE_NUMBER) + categoryFileParser.getLineNumber()
					+ ApplicationProperties.getValue("errorReadingCategoryEntityPath"));
		}

		return categoryEntityName;
	}

	/**
	 * @param categoryEntityName
	 * @param containerCollection
	 * @return
	 */
	private ContainerInterface SearchExistingCategoryEntityAndContainer(String categoryEntityName, List<ContainerInterface> containerCollection)
	{
		// Check whether the container is already created for category entity
		// and return it if it exists.
		Iterator<ContainerInterface> containerIterator = containerCollection.iterator();
		while (containerIterator.hasNext())
		{
			ContainerInterface container = containerIterator.next();
			if (container.getAbstractEntity().getName().equals(categoryEntityName))
			{
				return container;
			}
		}

		return null;
	}

	/**
	 * @param categoryPaths
	 * @param newCategoryEntityName
	 * @return
	 */
	private String getCategoryPath(String[] categoryPaths, String newCategoryEntityName)
	{
		String categoryPath = null;
		for (String string : categoryPaths)
		{
			if (string.endsWith(newCategoryEntityName))
				categoryPath = string;
		}
		return categoryPath;
	}

	/**
	 * @param control
	 * @throws ParseException
	 */
	private void setDefaultValue(ControlInterface control) throws ParseException
	{
		if (categoryFileParser.getDefaultValue() == null)
		{
			return;
		}

		CategoryAttributeInterface categoryAttribute = (CategoryAttributeInterface) control.getAttibuteMetadataInterface();
		if (!categoryFileParser.getDefaultValue().equals(categoryAttribute.getDefaultValue()))
		{
			AttributeInterface attributeInterface = categoryAttribute.getAbstractAttribute().getEntity().getAttributeByName(
					categoryAttribute.getAbstractAttribute().getName());
			categoryAttribute.setDefaultValue(attributeInterface.getAttributeTypeInformation().getPermissibleValueForString(
					categoryFileParser.getDefaultValue()));
		}
	}

	/**
	 * This method popoulate the main form list for the given entity group
	 * @param entityGroup
	 */
	private void populateMainFormList(EntityGroupInterface entityGroup)
	{
		for (ContainerInterface containerInterface : entityGroup.getMainContainerCollection())
		{
			mainFormList.add(containerInterface.getAbstractEntity().getName());
		}
	}

}