
package edu.common.dynamicextensions.util.parser;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import edu.common.dynamicextensions.domain.CategoryAttribute;
import edu.common.dynamicextensions.domain.CategoryEntity;
import edu.common.dynamicextensions.domain.DateAttributeTypeInformation;
import edu.common.dynamicextensions.domain.DomainObjectFactory;
import edu.common.dynamicextensions.domain.NumericAttributeTypeInformation;
import edu.common.dynamicextensions.domain.UserDefinedDE;
import edu.common.dynamicextensions.domain.userinterface.CheckBox;
import edu.common.dynamicextensions.domain.userinterface.TextField;
import edu.common.dynamicextensions.domaininterface.AbstractAttributeInterface;
import edu.common.dynamicextensions.domaininterface.AssociationInterface;
import edu.common.dynamicextensions.domaininterface.AttributeInterface;
import edu.common.dynamicextensions.domaininterface.AttributeMetadataInterface;
import edu.common.dynamicextensions.domaininterface.CategoryAssociationInterface;
import edu.common.dynamicextensions.domaininterface.CategoryAttributeInterface;
import edu.common.dynamicextensions.domaininterface.CategoryEntityInterface;
import edu.common.dynamicextensions.domaininterface.CategoryInterface;
import edu.common.dynamicextensions.domaininterface.DataElementInterface;
import edu.common.dynamicextensions.domaininterface.EntityGroupInterface;
import edu.common.dynamicextensions.domaininterface.EntityInterface;
import edu.common.dynamicextensions.domaininterface.FormControlNotesInterface;
import edu.common.dynamicextensions.domaininterface.PermissibleValueInterface;
import edu.common.dynamicextensions.domaininterface.SemanticPropertyInterface;
import edu.common.dynamicextensions.domaininterface.SkipLogicAttributeInterface;
import edu.common.dynamicextensions.domaininterface.UserDefinedDEInterface;
import edu.common.dynamicextensions.domaininterface.userinterface.ContainerInterface;
import edu.common.dynamicextensions.domaininterface.userinterface.ControlInterface;
import edu.common.dynamicextensions.exception.DynamicExtensionsApplicationException;
import edu.common.dynamicextensions.exception.DynamicExtensionsSystemException;
import edu.common.dynamicextensions.util.CategoryGenerationUtil;
import edu.common.dynamicextensions.util.CategoryHelper;
import edu.common.dynamicextensions.util.CategoryHelperInterface;
import edu.common.dynamicextensions.util.DynamicExtensionsUtility;
import edu.common.dynamicextensions.util.CategoryHelperInterface.ControlEnum;
import edu.common.dynamicextensions.util.global.CategoryConstants;
import edu.common.dynamicextensions.validation.category.CategoryValidator;
import edu.wustl.common.util.global.ApplicationProperties;

/**
 * @author kunal_kamble 
 * This class creates the category/categories defined in
 * the CSV file.
 */
public class CategoryGenerator
{

	private CategoryFileParser categoryFileParser;

	private CategoryValidator categoryValidator;

	private CategoryHelperInterface categoryHelper;

	private List<String> mainFormList = new ArrayList<String>();

	private CategoryInterface category;
	private EntityGroupInterface entityGroup;
	private List<ContainerInterface> containerCollection;
	private Map<String, String> categoryEntityNameInstanceMap;
	Map<String, List<AssociationInterface>> entityNameAssociationMap;

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
	public CategoryGenerator(String filePath) throws DynamicExtensionsSystemException,
			FileNotFoundException
	{
		categoryFileParser = new CategoryCSVFileParser(filePath);
		categoryValidator = new CategoryValidator((CategoryCSVFileParser) categoryFileParser);
		categoryHelper = new CategoryHelper();
	}

	/**
	 * @return
	 * @throws DynamicExtensionsSystemException
	 * @throws DynamicExtensionsApplicationException
	 * @throws ParseException
	 */
	public List<CategoryInterface> getCategoryList() throws DynamicExtensionsSystemException,
			DynamicExtensionsApplicationException, ParseException
	{
		CategoryHelperInterface categoryHelper = new CategoryHelper();
		List<CategoryInterface> categoryList = new ArrayList<CategoryInterface>();
		ApplicationProperties.initBundle(CategoryCSVConstants.DYEXTN_ERROR_MESSAGES_FILE);

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
				category = categoryHelper.getCategory(categoryFileParser.getCategoryName());

				// 2: Read the entity group.
				categoryFileParser.readNext();
				entityGroup = CategoryGenerationUtil.getEntityGroup(category, categoryFileParser
						.getEntityGroupName());

				CategoryValidator.checkForNullRefernce(entityGroup, ApplicationProperties
						.getValue(CategoryConstants.LINE_NUMBER)
						+ categoryFileParser.getLineNumber()
						+ ApplicationProperties.getValue("noEntityGroup")
						+ categoryFileParser.getEntityGroupName());

				categoryFileParser.getCategoryValidator().setEntityGroupId(entityGroup.getId());
				populateMainFormList(entityGroup);

				// 3: Get the path represented by ordered entity names.
				categoryFileParser.readNext();
				Map<String, List<String>> paths = categoryFileParser.getPaths();

				// 4: Get the association names list.
				entityNameAssociationMap = CategoryGenerationUtil.getAssociationList(paths,
						entityGroup);

				containerCollection = new ArrayList<ContainerInterface>();

				ContainerInterface container = null;
				EntityInterface entityInterface = null;

				// 5: Get the selected attributes and create the controls for
				// them.
				List<String> categoryEntityName = null;
				ControlInterface lastControl = null;
				categoryEntityNameInstanceMap = new HashMap<String, String>();
				String previousEntityName = "";
				boolean firstTimeinDisplayLabel = false;
				// HashMap<String, List> sequenceMap = new HashMap<String,
				// List>();
				int controlXPosition = 1;
				int controlYPosition = 0;

				Map<String, String> commonControlOptions = null;

				while (categoryFileParser.readNext())
				{
					if (categoryFileParser.hasFormDefination())
					{
						break;
					}

					if (categoryFileParser.hasRelatedAttributes()
							|| categoryFileParser.hasSkipLogicAttributes())
					{
						break;
					}
					if (categoryFileParser.hasDisplayLable())
					{
						categoryEntityName = processDisplayLabel();

						firstTimeinDisplayLabel = true;
						previousEntityName = null;
						categoryFileParser.readNext();
						controlXPosition = 1;
						controlYPosition = 0;
					}

					if (categoryFileParser.hasSubcategory())
					{
						lastControl = processSubcategory(firstTimeinDisplayLabel, entityInterface,
								categoryEntityName);
					}
					else
					{
						// Add control to the container.
						if (categoryFileParser.isSingleLineDisplayStarted())
						{
							controlYPosition++;
						}
						if (categoryFileParser.isSingleLineDisplayEnd())
						{
							controlYPosition = 0;
							controlXPosition++;
							commonControlOptions = null;
							continue;
						}
						if (categoryFileParser.hasSeparator())
						{
							categoryHelper.addOrUpdateLabelControl(entityInterface, container,
									categoryFileParser.getSeparator(), categoryFileParser
											.getLineNumber(), controlXPosition, controlYPosition);

							continue;
						}
						if (categoryFileParser.hasCommonControlOptions())
						{
							commonControlOptions = categoryFileParser.getCommonControlOptions();
							categoryFileParser.readNext();
						}
						String heading = categoryFileParser.getHeading();

						List<FormControlNotesInterface> controlNotes = new LinkedList<FormControlNotesInterface>();
						categoryFileParser.getFormControlNotes(controlNotes);

						Map<String, Collection<SemanticPropertyInterface>> permissibleValues = categoryFileParser
								.getPermissibleValues();

						Map<String, String> permissibleValueOptions = categoryFileParser
								.getPermissibleValueOptions();

						String attributeName = categoryFileParser.getAttributeName();

						entityInterface = entityGroup.getEntityByName(categoryFileParser
								.getEntityName());

						// Added for category inheritance, check if a given
						// attribute is parent category attribute.
						boolean isAttributePresent = entityInterface
								.isAttributePresent(attributeName);

						CategoryValidator.checkForNullRefernce(getcategoryEntityName(
								categoryEntityNameInstanceMap, categoryFileParser.getEntityName()),
								ApplicationProperties.getValue(CategoryConstants.LINE_NUMBER)
										+ categoryFileParser.getLineNumber()
										+ categoryEntityName
										+ ApplicationProperties
												.getValue("incorrectInstanceInformation"));

						container = CategoryGenerationUtil.getContainerWithCategoryEntityName(
								containerCollection, getCategoryEntityName(categoryEntityName,
										categoryFileParser.getEntityName()));

						AttributeInterface attribute = entityInterface
								.getAttributeByName(attributeName);

						if (attribute != null && permissibleValues != null)
						{
							UserDefinedDE userDefinedDE = (UserDefinedDE) attribute
									.getAttributeTypeInformation().getDataElement();
							if (userDefinedDE == null)
							{
								throw new DynamicExtensionsSystemException(ApplicationProperties
										.getValue(CategoryConstants.CREATE_CAT_FAILS)
										+ ApplicationProperties
												.getValue(CategoryConstants.NO_PV_FOR_ATTR)
										+ attributeName);
							}
						}

						CategoryValidator.checkForNullRefernce(attribute, ApplicationProperties
								.getValue(CategoryConstants.LINE_NUMBER)
								+ categoryFileParser.getLineNumber()
								+ " "
								+ ApplicationProperties.getValue(CategoryConstants.ATTR)
								+ attributeName
								+ " "
								+ ApplicationProperties
										.getValue(CategoryConstants.ATTR_NOT_PRESENT)
								+ entityInterface.getName());

						if (previousEntityName != null
								&& !previousEntityName.equals(container.getAbstractEntity()
										.getName()))
						{
							previousEntityName = container.getAbstractEntity().getName();
						}
						CategoryEntityInterface categoryEntity = (CategoryEntityInterface) container
								.getAbstractEntity();
						// If this is a parent attribute and currently the
						// parent category entity is not created
						// for given category entity, create parent category
						// hierarchy up to where attribute is found.
						if (!isAttributePresent)
						{
							EntityInterface parentEntity = entityInterface.getParentEntity();
							EntityInterface childEntity = entityInterface;
							CategoryEntityInterface parentCategoryEntity = categoryEntity
									.getParentCategoryEntity();

							categoryEntity = processInheritance(parentEntity, childEntity,
									parentCategoryEntity, categoryEntity, attributeName,
									containerCollection);

							entityInterface = categoryEntity.getEntity();
						}

						Map<String, Object> rules = categoryFileParser
								.getRules(attribute.getName());
						if (rules != null)
						{
							CategoryValidator
									.checkRangeAgainstAttributeValueRange(attribute, rules);
							CategoryValidator.checkRequiredRule(attribute, rules);
						}
						CategoryValidator.checkIfFutureDateRuleSpecified(attribute, rules);
						CategoryValidator.validateCSVFutureDateValue(attribute, rules,
								categoryFileParser.getDefaultValue());

						String controlType = categoryFileParser.getControlType();
						getCategoryValidator().isTextAreaForNumeric(controlType, attribute);
						/*if (categoryFileParser.isSingleLineDisplayStarted())
						{
							getCategoryValidator().validateControlInSingleLine(controlType,
									controlXPosition, container);
						}*/
						lastControl = categoryHelper.addOrUpdateControl(entityInterface,
								attributeName, container, ControlEnum.get(controlType),
								categoryFileParser.getControlCaption(), heading, controlNotes,
								rules, permissibleValueOptions, categoryFileParser.getLineNumber(),
								permissibleValues);

						// Set default value for attribute's IsRelatedAttribute
						// and IsVisible property.
						// This is required in case of edit of category entity.
						((CategoryAttributeInterface) lastControl.getAttibuteMetadataInterface())
								.setIsRelatedAttribute(false);
						((CategoryAttributeInterface) lastControl.getAttibuteMetadataInterface())
								.setIsVisible(true);

						// Set default value for control's option.
						// This is required in case of edit of category entity.
						categoryHelper.setDefaultControlsOptions(lastControl, ControlEnum
								.get(categoryFileParser.getControlType()));

						Map<String, String> controlOptions = categoryFileParser.getControlOptions();

						if (commonControlOptions != null)
						{
							categoryHelper.setOptions(lastControl, commonControlOptions,
									categoryFileParser.getLineNumber());
						}
						categoryHelper.setOptions(lastControl, controlOptions, categoryFileParser
								.getLineNumber());

						setDefaultValue(lastControl);

						CategoryValidator.checkIsMultiSelectValid(entityInterface, attributeName,
								lastControl);

						// Clear category entity from related attribute
						// collection of root category entity
						// only if no category attribute in this category entity
						// is a related category attribute.
						if (DynamicExtensionsUtility
								.areNoRelatedCategoryAttributesPresent(container))
						{
							category
									.removeRelatedAttributeCategoryEntity((CategoryEntityInterface) container
											.getAbstractEntity());
						}

						// Check for isReadOnly option.
						if (lastControl.getIsReadOnly())
						{
							((CategoryAttributeInterface) lastControl
									.getAttibuteMetadataInterface()).setIsRelatedAttribute(true);
							((CategoryAttributeInterface) lastControl
									.getAttibuteMetadataInterface()).setIsVisible(true);
							category
									.addRelatedAttributeCategoryEntity((CategoryEntityInterface) container
											.getAbstractEntity());
						}
					}

					lastControl.setControlPosition(controlXPosition, controlYPosition);
					if (!categoryFileParser.isSingleLineDisplayStarted())
					{
						controlXPosition++;
					}

				}

				CategoryGenerationUtil.setRootContainer(category, container, containerCollection,
						entityNameAssociationMap, paths, categoryEntityNameInstanceMap);

				categoryValidator.isRootEntityUsedTwice(category.getRootCategoryElement(), category
						.getRootCategoryElement().getEntity());
				if (categoryFileParser.hasRelatedAttributes())
				{
					handleRelatedAttributes(entityGroup, category, entityNameAssociationMap,
							containerCollection);
					if (categoryFileParser.hasSkipLogicAttributes())
					{
						handleSkipLogicAttributes(entityGroup, category, entityNameAssociationMap,
								containerCollection);
					}
				}
				if (categoryFileParser.hasSkipLogicAttributes())
				{
					handleSkipLogicAttributes(entityGroup, category, entityNameAssociationMap,
							containerCollection);
					if (categoryFileParser.hasRelatedAttributes())
					{
						handleRelatedAttributes(entityGroup, category, entityNameAssociationMap,
								containerCollection);
					}
				}
				CategoryGenerationUtil.setDefaultValueForCalculatedAttributes(category,category
						.getRootCategoryElement(), categoryFileParser.getLineNumber());

				// Commented this code since the method is error prone
				// TODO change this logic to reset the sequnces.
				// rearrangeControlSequence((ContainerInterface)
				// category.getRootCategoryElement()
				// .getContainerCollection().iterator().next(), sequenceMap);
				categoryList.add(category);

			}

		}
		catch (FileNotFoundException e)
		{
			throw new DynamicExtensionsSystemException(ApplicationProperties
					.getValue(CategoryConstants.CREATE_CAT_FAILS)
					+ ApplicationProperties.getValue("fileNotFound"), e);
		}
		catch (IOException e)
		{
			throw new DynamicExtensionsSystemException(ApplicationProperties
					.getValue(CategoryConstants.CREATE_CAT_FAILS)
					+ ApplicationProperties.getValue(CategoryConstants.LINE_NUMBER)
					+ categoryFileParser.getLineNumber()
					+ " "
					+ ApplicationProperties.getValue("readingFile")
					+ categoryFileParser.getFilePath(), e);
		}
		catch (Exception e)
		{
			if (!(e instanceof DynamicExtensionsSystemException))
			{
				throw new DynamicExtensionsSystemException(ApplicationProperties
						.getValue(CategoryConstants.CREATE_CAT_FAILS)
						+ ApplicationProperties.getValue(CategoryConstants.LINE_NUMBER)
						+ categoryFileParser.getLineNumber()
						+ " "
						+ ApplicationProperties.getValue("readingFile")
						+ categoryFileParser.getFilePath(), e);
			}

			throw new DynamicExtensionsSystemException("", e);
		}
		return categoryList;
	}

	private ControlInterface processSubcategory(boolean firstTimeinDisplayLabel,
			EntityInterface entityInterface, List<String> categoryEntityName)
			throws DynamicExtensionsSystemException, DynamicExtensionsApplicationException
	{
		// Set this flag when sub category is just after display label
		if (firstTimeinDisplayLabel)
		{
			firstTimeinDisplayLabel = false;
		}
		ContainerInterface sourceContainer = null;
		if (entityInterface != null)
		{
			// Always add sub-category to the container.
			sourceContainer = CategoryGenerationUtil.getContainerWithCategoryEntityName(
					containerCollection, categoryEntityName.get(0));
		}
		else
		{
			sourceContainer = CategoryGenerationUtil.getContainer(containerCollection,
					categoryFileParser.getDisplyLable());
		}

		String targetContainerCaption = categoryFileParser.getTargetContainerCaption();
		ContainerInterface targetContainer = CategoryGenerationUtil.getContainer(
				containerCollection, targetContainerCaption);

		CategoryValidator.checkForNullRefernce(targetContainer, ApplicationProperties
				.getValue(CategoryConstants.LINE_NUMBER)
				+ categoryFileParser.getLineNumber()
				+ ApplicationProperties.getValue("subcategoryNotFound") + targetContainerCaption);

		String multiplicity = categoryFileParser.getMultiplicity();

		String categoryEntName = ((CategoryEntityInterface) CategoryGenerationUtil.getContainer(
				containerCollection, targetContainerCaption).getAbstractEntity()).getName();

		List<AssociationInterface> associationNameList = entityNameAssociationMap
				.get(CategoryGenerationUtil
						.getEntityNameForAssociationMap(categoryEntityNameInstanceMap
								.get(categoryEntName)));

		CategoryValidator.checkForNullRefernce(associationNameList, ApplicationProperties
				.getValue(CategoryConstants.LINE_NUMBER)
				+ categoryFileParser.getLineNumber()
				+ ApplicationProperties.getValue("pathNotFound") + targetContainerCaption);

		CategoryHelper categoryHelper = new CategoryHelper();
		ControlInterface lastControl = categoryHelper.associateCategoryContainers(category,
				entityGroup, sourceContainer, targetContainer, associationNameList,
				CategoryGenerationUtil.getMultiplicityInNumbers(multiplicity),
				categoryEntityNameInstanceMap.get(targetContainer.getAbstractEntity().getName()));

		return lastControl;

	}

	/**
	 * @param categoryEntityNameList
	 * @param entityName
	 * @return
	 */
	private String getcategoryEntityName(Map<String, String> categoryEntityNameInstanceMap,
			String entityName)
	{
		String categoryEntityName = null;
		for (String name : categoryEntityNameInstanceMap.keySet())
		{
			if (entityName.equals(CategoryGenerationUtil
					.getEntityNameFromCategoryEntityInstancePath(categoryEntityNameInstanceMap
							.get(name))))
			{
				categoryEntityName = name;
			}
		}
		return categoryEntityName;
	}

	/**
	 * @param childCategoryEntity
	 * @param parentEntity
	 * @param entityGroup
	 * @param containerCollection
	 * @return
	 * @throws DynamicExtensionsSystemException
	 */
	private ContainerInterface createParentCategoryEntity(
			CategoryEntityInterface childCategoryEntity, EntityInterface parentEntity,
			EntityGroupInterface entityGroup, Collection<ContainerInterface> containerCollection)
			throws DynamicExtensionsSystemException
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

		ContainerInterface parentContainer = createCategoryEntityAndContainer(entityGroup
				.getEntityByName(parentEntity.getName()), newCategoryEntityName,
				newCategoryEntityName, false, containerCollection, parentCategory);
		((CategoryEntityInterface) childCategoryEntity)
				.setParentCategoryEntity((CategoryEntityInterface) parentContainer
						.getAbstractEntity());
		CategoryEntity parentCEntity = (CategoryEntity) parentContainer.getAbstractEntity();
		parentCEntity.addChildCategory(childCategoryEntity);

		CategoryEntity parentCategoryEntity = (CategoryEntity) ((CategoryEntityInterface) childCategoryEntity)
				.getParentCategoryEntity();
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
	private void handleRelatedAttributes(EntityGroupInterface entityGroup,
			CategoryInterface category,
			Map<String, List<AssociationInterface>> entityNameAssociationMap,
			List<ContainerInterface> containerCollection) throws IOException, ParseException,
			DynamicExtensionsSystemException, DynamicExtensionsApplicationException
	{
		while (categoryFileParser.readNext() && !categoryFileParser.hasSkipLogicAttributes())
		{
			String[] categoryPaths = categoryFileParser.getCategoryPaths();
			String categoryEntityName = CategoryGenerationUtil
					.getCategoryEntityName(categoryPaths[0],entityNameAssociationMap);

			String entityName = CategoryGenerationUtil
					.getEntityNameFromCategoryEntityInstancePath(categoryPaths[0]);

			String entityNameForEntityAssociationMap = CategoryGenerationUtil
					.getEntityNameForAssociationMap(categoryPaths[0]);

			EntityInterface entity = entityGroup.getEntityByName(CategoryGenerationUtil
					.getEntityNameExcludingAssociationRoleName(entityName));

			categoryFileParser.readNext();
			String attributeName = categoryFileParser.getRelatedAttributeName();
			CategoryHelperInterface categoryHelper = new CategoryHelper();
			boolean newCategoryCreated = false;
			if (category.getCategoryEntityByName(categoryEntityName) == null)
			{
				newCategoryCreated = true;
			}
			CategoryEntityInterface categoryEntity = categoryHelper.createOrUpdateCategoryEntity(
					category, entity, categoryEntityName);

			if (newCategoryCreated)
			{
				String associationName = category.getRootCategoryElement() + " to "
						+ categoryEntity.getName() + " association";
				categoryHelper.associateCategoryEntities(category.getRootCategoryElement(),
						categoryEntity, associationName, 1, entityGroup, entityNameAssociationMap
								.get(entityNameForEntityAssociationMap), categoryPaths[0]);
			}
			CategoryValidator.checkForNullRefernce(entity.getAttributeByName(attributeName),
					ApplicationProperties.getValue(CategoryConstants.LINE_NUMBER)
							+ categoryFileParser.getLineNumber() + " "
							+ ApplicationProperties.getValue(CategoryConstants.ATTR)
							+ attributeName + " "
							+ ApplicationProperties.getValue(CategoryConstants.ATTR_NOT_PRESENT)
							+ entity.getName());

			// Added for category inheritance.
			boolean isAttributePresent = entity.isAttributePresent(attributeName);

			// If this is the parent attribute and currently the parent category
			// entity is not created
			// for given category entity, create parent category hierarchy up to
			// where attribute is found.
			if (!isAttributePresent)
			{
				EntityInterface parentEntity = entity.getParentEntity();
				EntityInterface childEntity = entity;
				CategoryEntityInterface parentCategoryEntity = categoryEntity
						.getParentCategoryEntity();

				categoryEntity = processInheritance(parentEntity, childEntity,
						parentCategoryEntity, categoryEntity, attributeName, containerCollection);
				entity = categoryEntity.getEntity();

			}

			CategoryAttributeInterface categoryAttribute = categoryHelper.getCategoryAttribute(
					entity, attributeName, categoryEntity);
			Map<String, String> controlOptions = categoryFileParser.getControlOptions();

			categoryHelper.setOptions(categoryAttribute, controlOptions, categoryFileParser
					.getLineNumber());

			String defaultValue = categoryFileParser.getDefaultValueForRelatedAttribute();

			if (categoryAttribute.getIsCalculated() != null && categoryAttribute.getIsCalculated())
			{
				setFormula(categoryAttribute, defaultValue);
			}
			else
			{
				categoryAttribute.setDefaultValue(entity.getAttributeByName(attributeName)
						.getAttributeTypeInformation().getPermissibleValueForString(
								DynamicExtensionsUtility.getEscapedStringValue(defaultValue)));
			}
			categoryAttribute.setIsVisible(false);
			categoryAttribute.setIsRelatedAttribute(true);
			category.addRelatedAttributeCategoryEntity(categoryEntity);

		}
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
	private void handleSkipLogicAttributes(EntityGroupInterface entityGroup,
			CategoryInterface category,
			Map<String, List<AssociationInterface>> entityNameAssociationMap,
			List<ContainerInterface> containerCollection) throws IOException, ParseException,
			DynamicExtensionsSystemException, DynamicExtensionsApplicationException
	{
		while (categoryFileParser.readNext() && !categoryFileParser.hasRelatedAttributes())
		{
			String[] categoryPaths = categoryFileParser.getCategoryPaths();
			
			categoryFileParser.readNext();
			
			String sourceAttributeClassName = categoryFileParser
					.getSkipLogicSourceAttributeClassName();

			String sourceInstancePath = getInstancePath(categoryPaths,
					sourceAttributeClassName);

			String sourceCategoryEntityName = CategoryGenerationUtil
					.getCategoryEntityName(sourceInstancePath,
							entityNameAssociationMap);

			String sourceEntityName = CategoryGenerationUtil
					.getEntityNameFromCategoryEntityInstancePath(sourceInstancePath);

			EntityInterface sourceEntity = entityGroup
					.getEntityByName(CategoryGenerationUtil
							.getEntityNameExcludingAssociationRoleName(sourceEntityName));

			CategoryEntityInterface sourceCategoryEntity = categoryHelper
					.createOrUpdateCategoryEntity(category, sourceEntity,
							sourceCategoryEntityName);
	
			String targetAttributeClassName = categoryFileParser
					.getSkipLogicTargetAttributeClassName();

			String targetInstancePath = getInstancePath(categoryPaths,
					targetAttributeClassName);

			String targetCategoryEntityName = CategoryGenerationUtil
					.getCategoryEntityName(targetInstancePath,
							entityNameAssociationMap);

			String targetEntityName = CategoryGenerationUtil
					.getEntityNameFromCategoryEntityInstancePath(targetInstancePath);

			EntityInterface targetEntity = entityGroup
					.getEntityByName(CategoryGenerationUtil
							.getEntityNameExcludingAssociationRoleName(targetEntityName));
			
			CategoryEntityInterface targetCategoryEntity = categoryHelper
			.createOrUpdateCategoryEntity(category, targetEntity,
					targetCategoryEntityName);
			
			String sourceAttributeName = categoryFileParser.getSkipLogicSourceAttributeName();
			
			CategoryValidator.checkForNullRefernce(sourceEntity.getAttributeByName(sourceAttributeName),
					ApplicationProperties.getValue(CategoryConstants.LINE_NUMBER)
							+ categoryFileParser.getLineNumber() + " "
							+ ApplicationProperties.getValue(CategoryConstants.ATTR)
							+ sourceAttributeName + " "
							+ ApplicationProperties.getValue(CategoryConstants.ATTR_NOT_PRESENT)
							+ sourceEntity.getName());
			boolean isAttributePresent = sourceEntity.isAttributePresent(sourceAttributeName);

			// If this is the parent attribute and currently the parent category
			// entity is not created
			// for given category entity, create parent category hierarchy up to
			// where attribute is found.
			if (!isAttributePresent)
			{
				EntityInterface parentEntity = sourceEntity.getParentEntity();
				EntityInterface childEntity = sourceEntity;
				CategoryEntityInterface parentCategoryEntity = sourceCategoryEntity
						.getParentCategoryEntity();

				sourceCategoryEntity = processInheritance(parentEntity, childEntity,
						parentCategoryEntity, sourceCategoryEntity, sourceAttributeName, containerCollection);
				sourceEntity = sourceCategoryEntity.getEntity();

			}

			CategoryAttributeInterface sourceCategoryAttribute = categoryHelper.getCategoryAttribute(
					sourceEntity, sourceAttributeName, sourceCategoryEntity);
			
			ContainerInterface sourceContainerInterface = DynamicExtensionsUtility
			.getContainerForAbstractEntity(sourceCategoryAttribute
					.getCategoryEntity());
			
			ControlInterface sourceControl = DynamicExtensionsUtility
					.getControlForAbstractAttribute(
							(AttributeMetadataInterface) sourceCategoryAttribute,
							sourceContainerInterface,sourceCategoryEntityName);
			
			boolean isEnumeratedSourceControl = true;
			if (sourceControl instanceof CheckBox)
			{
				isEnumeratedSourceControl = false;
			}
			sourceControl.setIsSkipLogic(Boolean.valueOf(true));
			sourceCategoryAttribute.setIsSkipLogic(Boolean.valueOf(true));
			PermissibleValueInterface permissibleValueInterface = null;
			if (isEnumeratedSourceControl)
			{
				String permissibleValue = categoryFileParser.getSkipLogicPermissibleValueName();
				DataElementInterface dataElementInterface = ((AttributeMetadataInterface)sourceCategoryAttribute).getDataElement();
				UserDefinedDEInterface userDefinedDEInterface = (UserDefinedDEInterface) dataElementInterface;
				
				permissibleValueInterface = categoryHelper
						.getPermissibleValue(userDefinedDEInterface
								.getPermissibleValueCollection(), permissibleValue);
				
				sourceCategoryAttribute.addSkipLogicPermissibleValue(permissibleValueInterface);
				
				CategoryValidator.checkForNullRefernce(permissibleValueInterface,
						ApplicationProperties.getValue(CategoryConstants.LINE_NUMBER)
								+ categoryFileParser.getLineNumber() + " "
								+ ApplicationProperties.getValue(CategoryConstants.ATTR)
								+ permissibleValue + " "
								+ ApplicationProperties.getValue(CategoryConstants.PV_ATTR_NOT_PRESENT)
								+ sourceAttributeName);
			}
			ContainerInterface targetContainerInterface = DynamicExtensionsUtility
					.getContainerForAbstractEntity(targetCategoryEntity);
			
			String targetAttributeName = categoryFileParser.getSkipLogicTargetAttributeName();
			
			if (targetAttributeName != null  && targetAttributeName.equals(CategoryConstants.ALL))
			{
				//Setting control options
				Map<String, String> controlOptions = categoryFileParser.getControlOptions();
				
				setSkipLogicForAllSubFormAttributes(targetCategoryEntity,
						sourceControl, sourceCategoryAttribute,
						permissibleValueInterface, isEnumeratedSourceControl,
						controlOptions);
			}
			else
			{
				CategoryValidator.checkForNullRefernce(targetEntity.getAttributeByName(targetAttributeName),
						ApplicationProperties.getValue(CategoryConstants.LINE_NUMBER)
								+ categoryFileParser.getLineNumber() + " "
								+ ApplicationProperties.getValue(CategoryConstants.ATTR)
								+ targetAttributeName + " "
								+ ApplicationProperties.getValue(CategoryConstants.ATTR_NOT_PRESENT)
								+ targetEntity.getName());
				
				isAttributePresent = targetEntity.isAttributePresent(targetAttributeName);
	
				// If this is the parent attribute and currently the parent category
				// entity is not created
				// for given category entity, create parent category hierarchy up to
				// where attribute is found.
				if (!isAttributePresent)
				{
					EntityInterface parentEntity = targetEntity.getParentEntity();
					EntityInterface childEntity = targetEntity;
					CategoryEntityInterface parentCategoryEntity = targetCategoryEntity
							.getParentCategoryEntity();
	
					targetCategoryEntity = processInheritance(parentEntity, childEntity,
							parentCategoryEntity, targetCategoryEntity, targetAttributeName, containerCollection);
					targetEntity = targetCategoryEntity.getEntity();
	
				}
	
				CategoryAttributeInterface targetCategoryAttribute = categoryHelper.getCategoryAttribute(
						targetEntity, targetAttributeName, targetCategoryEntity);
				
				ControlInterface targetControl = DynamicExtensionsUtility
						.getControlForAbstractAttribute(
								(AttributeMetadataInterface) targetCategoryAttribute,
								targetContainerInterface,targetCategoryEntityName);
				
				targetControl.setIsSkipLogicTargetControl(Boolean.valueOf(true));
				targetControl.setSourceSkipControl(sourceControl);

				SkipLogicAttributeInterface skipLogicAttributeInterface = DomainObjectFactory.getInstance().createSkipLogicAttribute();
				skipLogicAttributeInterface.setSourceSkipLogicAttribute(sourceCategoryAttribute);
				skipLogicAttributeInterface.setTargetSkipLogicAttribute(targetCategoryAttribute);
				
				String defaultValue = categoryFileParser.getDefaultValue();
				if (defaultValue == null)
				{
					// Validation-If category attribute is of type Read-only its default
					// value must be specified
					if ((targetControl.getIsReadOnly() != null && targetControl.getIsReadOnly())
							|| (targetControl.getIsCalculated() != null && targetControl.getIsCalculated()))
					{
						throw new DynamicExtensionsSystemException(ApplicationProperties
								.getValue(CategoryConstants.CREATE_CAT_FAILS)
								+ ApplicationProperties.getValue(CategoryConstants.LINE_NUMBER)
								+ categoryFileParser.getLineNumber()
								+ " "
								+ ApplicationProperties.getValue("mandatoryDValueForRO") + targetAttributeName);
					}
				}
				else
				{
					skipLogicAttributeInterface
						.setDefaultValue(((AttributeMetadataInterface) targetCategoryAttribute)
								.getAttributeTypeInformation()
								.getPermissibleValueForString(
										DynamicExtensionsUtility
												.getEscapedStringValue(defaultValue)));
				}
				//Setting control options
				Map<String, String> controlOptions = categoryFileParser.getControlOptions();
				
				categoryHelper.setOptions(targetControl, controlOptions, categoryFileParser.getLineNumber());
				
				if (isEnumeratedSourceControl)
				{
					permissibleValueInterface.addDependentSkipLogicAttribute(skipLogicAttributeInterface);
				}
				else
				{
					sourceCategoryAttribute.addDependentSkipLogicAttribute(skipLogicAttributeInterface);
				}
				Map<String, Collection<SemanticPropertyInterface>> permissibleValues = categoryFileParser
						.getPermissibleValues();
	
				Map<String, String> permissibleValueOptions = categoryFileParser
						.getPermissibleValueOptions();
				
				List<PermissibleValueInterface> permissibleValuesList = categoryHelper
						.createPermissibleValuesList(targetAttributeName, targetAttributeClassName,
								skipLogicAttributeInterface, categoryFileParser
										.getLineNumber(), permissibleValues);
				
				skipLogicAttributeInterface.clearDataElementCollection();
				if (permissibleValuesList != null && !permissibleValuesList.isEmpty())
				{
					UserDefinedDEInterface userDefinedDE = DomainObjectFactory.getInstance()
							.createUserDefinedDE();
					for (PermissibleValueInterface pv : permissibleValuesList)
					{
						userDefinedDE.addPermissibleValue(pv);
					}
		
					//add new permissible values
					skipLogicAttributeInterface.setDataElement(userDefinedDE);
		
					categoryHelper.setOptions(userDefinedDE, permissibleValueOptions, categoryFileParser.getLineNumber());
				}
			}
		}
	}
	private String getInstancePath(String[] categoryPaths,String className)
	{
		String instancePathInformation = "";
		for (String instancePath :categoryPaths)
		{
			if (instancePath.contains(className))
			{
				instancePathInformation = instancePath;
				break;
			}
		}
		return instancePathInformation;
	}
	/**
	 * 
	 * @param targetCategoryEntity
	 * @param targetContainerInterface
	 * @param sourceControl
	 * @param sourceCategoryAttribute
	 * @param permissibleValueInterface
	 * @throws DynamicExtensionsSystemException 
	 */
	private void setSkipLogicForAllSubFormAttributes(
			CategoryEntityInterface targetCategoryEntity,
			ControlInterface sourceControl,
			CategoryAttributeInterface sourceCategoryAttribute,
			PermissibleValueInterface permissibleValueInterface,
			boolean isEnumeratedSourceControl,
			Map<String, String> controlOptions)
			throws DynamicExtensionsSystemException
	{
		ContainerInterface targetContainerInterface = DynamicExtensionsUtility
		.getContainerForAbstractEntity(targetCategoryEntity);
		
		for (CategoryAttributeInterface targetCategoryAttribute : targetCategoryEntity.getAllCategoryAttributes())
		{
			ControlInterface targetControl = DynamicExtensionsUtility
			.getControlForAbstractAttribute(
					(AttributeMetadataInterface) targetCategoryAttribute,
					targetContainerInterface,targetCategoryEntity.getName());
			
			targetControl.setIsSkipLogicTargetControl(Boolean.valueOf(true));
			targetControl.setSourceSkipControl(sourceControl);
			SkipLogicAttributeInterface skipLogicAttributeInterface = DomainObjectFactory.getInstance().createSkipLogicAttribute();
			skipLogicAttributeInterface.setSourceSkipLogicAttribute(sourceCategoryAttribute);
			skipLogicAttributeInterface.setTargetSkipLogicAttribute(targetCategoryAttribute);
			if (isEnumeratedSourceControl)
			{
				permissibleValueInterface.addDependentSkipLogicAttribute(skipLogicAttributeInterface);
			}
			else
			{
				sourceCategoryAttribute.addDependentSkipLogicAttribute(skipLogicAttributeInterface);
			}
			categoryHelper.setOptions(targetControl, controlOptions, categoryFileParser.getLineNumber());
		}
		for (CategoryAssociationInterface targetCategoryAssociation : targetCategoryEntity.getCategoryAssociationCollection())
		{
			setSkipLogicForAllSubFormAttributes(targetCategoryAssociation
					.getTargetCategoryEntity(), sourceControl,
					sourceCategoryAttribute, permissibleValueInterface,
					isEnumeratedSourceControl,controlOptions);
		}
	}
	/**
	 * 
	 * @param categoryAttribute
	 * @param defaultValue
	 * @throws DynamicExtensionsApplicationException
	 */
	private void setFormula(CategoryAttributeInterface categoryAttribute, String defaultValue)
			throws DynamicExtensionsApplicationException
	{
		boolean isValidFormula = false;
		FormulaParser formulaParser = new FormulaParser();
		try
		{
			isValidFormula = formulaParser.validateExpression(defaultValue);
		}
		catch (DynamicExtensionsSystemException ex)
		{
			throw new DynamicExtensionsApplicationException(ApplicationProperties
					.getValue(CategoryConstants.CREATE_CAT_FAILS)
					+ ApplicationProperties.getValue(CategoryConstants.LINE_NUMBER)
					+ categoryFileParser.getLineNumber()
					+ categoryAttribute.getAbstractAttribute().getName()
					+ ApplicationProperties.getValue("incorrectFormulaCalculatedAttribute")
					+ defaultValue, ex);
		}
		if (isValidFormula)
		{
			categoryAttribute.setIsCalculated(true);
			if (categoryAttribute.getFormula() == null)
			{
				categoryAttribute.setFormula(DomainObjectFactory.getInstance().createFormula(
						defaultValue));
			}
			else
			{
				categoryAttribute.getFormula().setExpression(defaultValue);
			}
		}
	}

	/**
	 * @param parentEntity
	 * @param childEntity
	 * @param parentCategoryEntity
	 * @param childCategoryEntity
	 * @param attributeName
	 * @param containerCollection
	 * @throws DynamicExtensionsSystemException
	 */
	private CategoryEntityInterface processInheritance(EntityInterface parentEntity,
			EntityInterface childEntity, CategoryEntityInterface parentCategoryEntity,
			CategoryEntityInterface childCategoryEntity, String attributeName,
			List<ContainerInterface> containerCollection) throws DynamicExtensionsSystemException
	{
		boolean isAttributeCategoryMatched = false;
		while (childEntity.getParentEntity() != null)
		{
			parentEntity = childEntity.getParentEntity();

			// Check whether the given cat.entity's parent category entity is
			// created.
			// If not created, then create it.
			if (parentCategoryEntity == null)
			{
				ContainerInterface parentContainer = createParentCategoryEntity(
						childCategoryEntity, parentEntity, childEntity.getEntityGroup(),
						containerCollection);
				parentCategoryEntity = (CategoryEntityInterface) parentContainer
						.getAbstractEntity();

				ContainerInterface childcontainerInterface = CategoryGenerationUtil
						.getContainerWithCategoryEntityName(containerCollection,
								childCategoryEntity.getName());
				childcontainerInterface.setBaseContainer(parentContainer);
			}

			// Iterate over parent entity's attribute, check whether its present
			// in parent entity.
			Iterator<AbstractAttributeInterface> parentattrIterator = parentEntity
					.getAbstractAttributeCollection().iterator();

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
			{
				break;
			}
			addContainerInfo(childCategoryEntity);
		}
		return childCategoryEntity;
	}

	/**
	 * Adds containers to containerCollection
	 * @param childCategoryEntity
	 */
	private void addContainerInfo(CategoryEntityInterface childCategoryEntity)
	{
		boolean isContainerPresent = false;
		if(childCategoryEntity.getContainerCollection()!=null)
		{
			ContainerInterface container = (ContainerInterface)childCategoryEntity.getContainerCollection().iterator().next();
			for (ContainerInterface containerInterface : containerCollection)
			{
				if (container.getAbstractEntity().getName().equals(containerInterface.getAbstractEntity().getName()))
				{
					isContainerPresent = true;
					break;
				}
			}
			if(!isContainerPresent)
			{
				containerCollection.add(container);
			}
		}
	}
	
	/**
	 * @param categoryEntityNameList
	 * @param entityName
	 * @return
	 */
	private String getCategoryEntityName(List<String> categoryEntityNameList, String entityName)
	{
		String categoryEntityName = null;
		for (String categoryEntName : categoryEntityNameList)
		{
			String catEntityName = DynamicExtensionsUtility.getCategoryEntityName(categoryEntName);
			if (entityName.equals(catEntityName.substring(0, catEntityName.indexOf('['))))
			{
				categoryEntityName = categoryEntName;
				break;
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
	private ContainerInterface createCategoryEntityAndContainer(EntityInterface entityInterface,
			String categoryEntityName, String displayLable, Boolean showCaption,
			Collection<ContainerInterface> containerCollection, CategoryInterface category)
			throws DynamicExtensionsSystemException
	{
		ContainerInterface containerInterface = null;
		CategoryHelperInterface categoryHelper = new CategoryHelper();

		containerInterface = categoryHelper.createOrUpdateCategoryEntityAndContainer(
				entityInterface, displayLable, category, categoryEntityName);

		containerInterface.setAddCaption(showCaption);

		containerCollection.add(containerInterface);

		return containerInterface;
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
	private List<String> processDisplayLabel() throws DynamicExtensionsSystemException,
			DynamicExtensionsApplicationException
	{

		Boolean showCaption = categoryFileParser.isShowCaption();
		ContainerInterface mainContainer = null;
		String displayLable = categoryFileParser.getDisplyLable();
		List<String> categoryEntityName = new ArrayList<String>();
		String categoryEntName = null;

		try
		{
			categoryFileParser.readNext();
			String[] categoryPaths = categoryFileParser.getCategoryPaths();
			//categoryValidator.validateContainersUnderSameDisplayLabel(categoryPaths, showCaption);
			ContainerInterface temp = null;
			for (String categoryPath : categoryPaths)
			{
				categoryEntName = CategoryGenerationUtil.getCategoryEntityName(categoryPath,entityNameAssociationMap);
				temp = createForm(displayLable, categoryPath, categoryEntityName, categoryEntName,
						categoryPaths, showCaption);
				categoryHelper.removeAllSeprators(temp);
				if (mainContainer == null)
				{
					mainContainer = temp;
				}
				else
				{
					List<AssociationInterface> associationNameList = entityNameAssociationMap
							.get(CategoryGenerationUtil
									.getEntityNameForAssociationMap(categoryEntityNameInstanceMap
											.get(categoryEntName)));

					new CategoryHelper().addChildContainers(category, entityGroup, mainContainer,
							temp, associationNameList, 1, categoryEntityNameInstanceMap.get(temp
									.getAbstractEntity().getName()));
				}

				showCaption = false;
			}
		}
		catch (IOException exception)
		{
			throw new DynamicExtensionsSystemException(ApplicationProperties
					.getValue(CategoryConstants.CREATE_CAT_FAILS)
					+ ApplicationProperties.getValue(CategoryConstants.LINE_NUMBER)
					+ categoryFileParser.getLineNumber()
					+ ApplicationProperties.getValue("errorReadingCategoryEntityPath"), exception);
		}

		return categoryEntityName;
	}

	private ContainerInterface createForm(String displayLable, String categoryPath,
			List<String> categoryEntityNameList, String categoryEntityName, String[] categoryPaths,
			boolean showCaption) throws DynamicExtensionsSystemException
	{
		String entityName = null;
		String[] categoryEntitiesInPath;

		categoryEntitiesInPath = categoryPath.split("->");
		String newCategoryEntityName = categoryEntitiesInPath[categoryEntitiesInPath.length - 1];
		entityName = CategoryGenerationUtil.getEntityName(newCategoryEntityName);

		// Check if instance information is wrong, i.e. entity mentioned
		// in
		// the instance information exists in the entity group.
		for (int i = 0; i < categoryEntitiesInPath.length; i++)
		{
			String entName = CategoryGenerationUtil
					.getEntityNameExcludingAssociationRoleName(categoryEntitiesInPath[i].substring(
							0, categoryEntitiesInPath[i].indexOf('[')));

			if (i + 1 <= categoryEntitiesInPath.length - 1)
			{
				try
				{
					String prevInstance = categoryEntitiesInPath[i].substring(
							categoryEntitiesInPath[i].indexOf('[') + 1, categoryEntitiesInPath[i]
									.indexOf(']'));
					String nextInstance = categoryEntitiesInPath[i + 1].substring(
							categoryEntitiesInPath[i + 1].indexOf('[') + 1,
							categoryEntitiesInPath[i + 1].indexOf(']'));

					Integer prevInstanceNo = Integer.valueOf(prevInstance);
					Integer nextInstanceNo = Integer.valueOf(nextInstance);

					if (prevInstanceNo.compareTo(nextInstanceNo) > 0)
					{
						handleInstanceException(categoryEntitiesInPath[i],
								categoryEntitiesInPath[i + 1]);
					}
				}
				catch (NumberFormatException eNumberFormatException)
				{
					handleInstanceException(categoryEntitiesInPath[i],
							categoryEntitiesInPath[i + 1]);
				}
				catch (StringIndexOutOfBoundsException eStringIndexOutOfBoundsException)
				{
					handleInstanceException(categoryEntitiesInPath[i],
							categoryEntitiesInPath[i + 1]);
				}
			}
			if (entityGroup.getEntityByName(entName) == null)
			{
				throw new DynamicExtensionsSystemException(ApplicationProperties
						.getValue(CategoryConstants.CREATE_CAT_FAILS)
						+ ApplicationProperties.getValue(CategoryConstants.LINE_NUMBER)
						+ categoryFileParser.getLineNumber()
						+ " "
						+ ApplicationProperties.getValue(CategoryConstants.WRONG_INST_INFO)
						+ entName);
			}
		}
		ContainerInterface container = null;
		if (!categoryEntityNameList.contains(categoryEntityName))
		{

			container = SearchExistingCategoryEntityAndContainer(categoryEntityName,
					containerCollection);
			if (container == null)
			{
				container = createCategoryEntityAndContainer(entityGroup
						.getEntityByName(entityName), categoryEntityName, displayLable,
						showCaption, containerCollection, category);
			}

			categoryEntityNameInstanceMap.put(container.getAbstractEntity().getName(),
					getCategoryPath(categoryPaths, newCategoryEntityName));

			categoryEntityNameList.add(categoryEntityName);

		}
		return container;
	}

	/**
	 * handleInstanceException.
	 * 
	 * @param sourceInstance
	 * @param targetInstance
	 * @throws DynamicExtensionsSystemException
	 */
	private void handleInstanceException(String sourceInstance, String targetInstance)
			throws DynamicExtensionsSystemException
	{
		throw new DynamicExtensionsSystemException(ApplicationProperties
				.getValue(CategoryConstants.CREATE_CAT_FAILS)
				+ ApplicationProperties.getValue(CategoryConstants.LINE_NUMBER)
				+ categoryFileParser.getLineNumber()
				+ " "
				+ ApplicationProperties.getValue(CategoryConstants.INCORRECT_INST_INFO)
				+ sourceInstance + "->" + targetInstance);
	}

	/**
	 * @param categoryEntityName
	 * @param containerCollection
	 * @return
	 */
	private ContainerInterface SearchExistingCategoryEntityAndContainer(String categoryEntityName,
			List<ContainerInterface> containerCollection)
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
			{
				categoryPath = string;
			}
		}
		return categoryPath;
	}

	/**
	 * @param control
	 * @throws ParseException
	 * @throws DynamicExtensionsApplicationException 
	 */
	private void setDefaultValue(ControlInterface control) throws ParseException,
			DynamicExtensionsSystemException, DynamicExtensionsApplicationException
	{
		String defaultValue = categoryFileParser.getDefaultValue();
		String attributeName = ((CategoryAttributeInterface) control.getAttibuteMetadataInterface())
				.getAbstractAttribute().getName();
		if (defaultValue == null)
		{
			// Validation-If category attribute is of type Read-only its default
			// value must be specified
			if ((control.getIsReadOnly() != null && control.getIsReadOnly())
					|| (control.getIsCalculated() != null && control.getIsCalculated()))
			{
				throw new DynamicExtensionsSystemException(ApplicationProperties
						.getValue(CategoryConstants.CREATE_CAT_FAILS)
						+ ApplicationProperties.getValue(CategoryConstants.LINE_NUMBER)
						+ categoryFileParser.getLineNumber()
						+ " "
						+ ApplicationProperties.getValue("mandatoryDValueForRO") + attributeName);
			}
			return;
		}

		CategoryAttributeInterface categoryAttribute = (CategoryAttributeInterface) control
				.getAttibuteMetadataInterface();
		if (!defaultValue.equals(categoryAttribute.getDefaultValue()))
		{
			AttributeInterface attributeInterface = categoryAttribute.getAbstractAttribute()
					.getEntity().getAttributeByName(
							categoryAttribute.getAbstractAttribute().getName());
			if (control.getIsCalculated() != null && control.getIsCalculated())
			{
				if (control instanceof TextField)
				{
					if (((AttributeMetadataInterface) categoryAttribute)
							.getAttributeTypeInformation() instanceof NumericAttributeTypeInformation
							|| ((AttributeMetadataInterface) categoryAttribute)
									.getAttributeTypeInformation() instanceof DateAttributeTypeInformation)
					{
						setFormula(categoryAttribute, defaultValue);
					}
					else
					{
						throw new DynamicExtensionsSystemException(ApplicationProperties
								.getValue(CategoryConstants.CREATE_CAT_FAILS)
								+ " "
								+ ApplicationProperties
										.getValue("incorrectDataTypeCalculatedAttribute")
								+ attributeName);
					}
				}
				else
				{
					throw new DynamicExtensionsSystemException(ApplicationProperties
							.getValue(CategoryConstants.CREATE_CAT_FAILS)
							+ ApplicationProperties.getValue(CategoryConstants.LINE_NUMBER)
							+ categoryFileParser.getLineNumber()
							+ " "
							+ ApplicationProperties
									.getValue("incorrectControlTypeCalculatedAttribute")
							+ attributeName);
				}
			}
			else
			{
				categoryAttribute.setDefaultValue(attributeInterface.getAttributeTypeInformation()
						.getPermissibleValueForString(
								DynamicExtensionsUtility.getEscapedStringValue(defaultValue)));
			}
		}
	}

	/**
	 * This method populate the main form list for the given entity group
	 * 
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