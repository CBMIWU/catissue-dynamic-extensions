
package edu.common.dynamicextensions.util;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

import edu.common.dynamicextensions.domain.AutoLoadXpath;
import edu.common.dynamicextensions.domain.CategoryAssociation;
import edu.common.dynamicextensions.domain.CategoryAttribute;
import edu.common.dynamicextensions.domain.CategoryEntity;
import edu.common.dynamicextensions.domain.DomainObjectFactory;
import edu.common.dynamicextensions.domain.PathAssociationRelationInterface;
import edu.common.dynamicextensions.domain.PermissibleValue;
import edu.common.dynamicextensions.domain.TaggedValue;
import edu.common.dynamicextensions.domain.UserDefinedDE;
import edu.common.dynamicextensions.domain.userinterface.ComboBox;
import edu.common.dynamicextensions.domain.userinterface.Container;
import edu.common.dynamicextensions.domain.userinterface.Label;
import edu.common.dynamicextensions.domain.userinterface.ListBox;
import edu.common.dynamicextensions.domain.userinterface.MultiSelectCheckBox;
import edu.common.dynamicextensions.domaininterface.AbstractEntityInterface;
import edu.common.dynamicextensions.domaininterface.AssociationInterface;
import edu.common.dynamicextensions.domaininterface.AttributeInterface;
import edu.common.dynamicextensions.domaininterface.AttributeTypeInformationInterface;
import edu.common.dynamicextensions.domaininterface.BaseAbstractAttributeInterface;
import edu.common.dynamicextensions.domaininterface.CategoryAssociationInterface;
import edu.common.dynamicextensions.domaininterface.CategoryAttributeInterface;
import edu.common.dynamicextensions.domaininterface.CategoryEntityInterface;
import edu.common.dynamicextensions.domaininterface.CategoryInterface;
import edu.common.dynamicextensions.domaininterface.DataElementInterface;
import edu.common.dynamicextensions.domaininterface.DynamicExtensionBaseDomainObjectInterface;
import edu.common.dynamicextensions.domaininterface.EntityGroupInterface;
import edu.common.dynamicextensions.domaininterface.EntityInterface;
import edu.common.dynamicextensions.domaininterface.FormControlNotesInterface;
import edu.common.dynamicextensions.domaininterface.PathInterface;
import edu.common.dynamicextensions.domaininterface.PermissibleValueInterface;
import edu.common.dynamicextensions.domaininterface.TaggedValueInterface;
import edu.common.dynamicextensions.domaininterface.UserDefinedDEInterface;
import edu.common.dynamicextensions.domaininterface.userinterface.AbstractContainmentControlInterface;
import edu.common.dynamicextensions.domaininterface.userinterface.CategoryAssociationControlInterface;
import edu.common.dynamicextensions.domaininterface.userinterface.CheckBoxInterface;
import edu.common.dynamicextensions.domaininterface.userinterface.ComboBoxInterface;
import edu.common.dynamicextensions.domaininterface.userinterface.ContainerInterface;
import edu.common.dynamicextensions.domaininterface.userinterface.ControlInterface;
import edu.common.dynamicextensions.domaininterface.userinterface.DatePickerInterface;
import edu.common.dynamicextensions.domaininterface.userinterface.FileUploadInterface;
import edu.common.dynamicextensions.domaininterface.userinterface.LabelInterface;
import edu.common.dynamicextensions.domaininterface.userinterface.MultiSelectCheckBoxInterface;
import edu.common.dynamicextensions.domaininterface.userinterface.RadioButtonInterface;
import edu.common.dynamicextensions.domaininterface.userinterface.SelectInterface;
import edu.common.dynamicextensions.domaininterface.userinterface.TextAreaInterface;
import edu.common.dynamicextensions.domaininterface.userinterface.TextFieldInterface;
import edu.common.dynamicextensions.domaininterface.validationrules.RuleInterface;
import edu.common.dynamicextensions.domaininterface.validationrules.RuleParameterInterface;
import edu.common.dynamicextensions.entitymanager.CategoryManager;
import edu.common.dynamicextensions.entitymanager.EntityManagerUtil;
import edu.common.dynamicextensions.exception.DynamicExtensionsApplicationException;
import edu.common.dynamicextensions.exception.DynamicExtensionsSystemException;
import edu.common.dynamicextensions.processor.ProcessorConstants;
import edu.common.dynamicextensions.util.global.CategoryConstants;
import edu.common.dynamicextensions.util.global.DEConstants;
import edu.common.dynamicextensions.util.parser.CategoryCSVConstants;
import edu.common.dynamicextensions.util.parser.CategoryFileParser;
import edu.common.dynamicextensions.validation.ValidatorUtil;
import edu.common.dynamicextensions.xmi.XMIConstants;
import edu.wustl.cab2b.server.cache.EntityCache;
import edu.wustl.common.util.global.ApplicationProperties;
import edu.wustl.common.util.logger.Logger;
import edu.wustl.metadata.util.DyExtnObjectCloner;

/**
 * @author kunal_kamble
 * @author mandar_shidhore
 *
 */
public class CategoryHelper implements CategoryHelperInterface
{

	/** The Constant LOGGER. */
	private static final Logger LOGGER = Logger.getCommonLogger(CategoryHelper.class);

	private CategoryFileParser categoryFileParser;

	/**
	 * Default constructor
	 */
	public CategoryHelper()
	{
		// TODO Auto-generated constructor

	}

	/**
	 * @param categoryFileParser
	 */
	public CategoryHelper(CategoryFileParser categoryFileParser)
	{
		super();
		this.categoryFileParser = categoryFileParser;
	}

	/**
	 * This method will retrieve the category from the cache with the given name.
	 * If the category is find then it will check weather it is already in use or not.
	 * If it is already in use it will throw exception.
	 * else will add the category to inUse set & return the category for further operations.
	 * After completing the operation user has to release the lock on category explicitly.
	 * @param name name of the category to retrieve.
	 * @return category with given name from cache else new category.
	 */
	public CategoryInterface getCategory(String name) throws DynamicExtensionsSystemException
	{
		CategoryInterface category = CategoryManager.getInstance().getCategoryByName(name);
		if (category == null)
		{
			category = DomainObjectFactory.getInstance().createCategory();
			category.setName(name);
		}
		else
		{
			synchronized (category)
			{
				if (EntityCache.getInstance().isCategoryInUse(category))
				{
					throw new DynamicExtensionsSystemException(
							"Category In Use Please Try Agian after some time");
				}
				else
				{
					EntityCache.getInstance().markCategoryInUse(category);
					DyExtnObjectCloner cloner = new DyExtnObjectCloner();
					CategoryInterface clonedCategory = cloner.clone(category);
					category = clonedCategory;
				}
			}
		}
		return category;
	}

	/**
	 * This method will release the lock on the category so that other users can use it
	 * for furthure.
	 * @param category category on which the lock is released.
	 */
	public void releaseLockOnCategory(CategoryInterface category)
	{
		if (category != null && category.getId() != null)
		{
			synchronized (category)
			{
				EntityCache.getInstance().releaseCategoryFromUse(category);
			}
		}
	}

	/* (non-Javadoc)
	 * @see edu.common.dynamicextensions.util.CategoryHelperInterface#createOrUpdateCategoryEntityAndContainer(edu.common.dynamicextensions.domaininterface.EntityInterface, java.lang.String, edu.common.dynamicextensions.domaininterface.CategoryInterface, java.lang.String[])
	 */
	public ContainerInterface createOrUpdateCategoryEntityAndContainer(EntityInterface entity,
			String containerCaptionValue, CategoryInterface category, String... categoryEntityName)
	{
		String newCategoryEntityName = categoryEntityName.length > 0 ? categoryEntityName[0] : null;
		CategoryEntityInterface categoryEntity = createOrUpdateCategoryEntity(category, entity,
				newCategoryEntityName);
		String containerCaption = containerCaptionValue;
		if (containerCaption == null)
		{
			containerCaption = entity.getName() + "_category_entity_container";

		}
		ContainerInterface container = createContainer(categoryEntity, containerCaption);

		return container;
	}

	/* (non-Javadoc)
	 * @see edu.common.dynamicextensions.util.CategoryHelperInterface#createOrUpdateCategoryEntity(edu.common.dynamicextensions.domaininterface.CategoryInterface, edu.common.dynamicextensions.domaininterface.EntityInterface, java.lang.String)
	 */
	public CategoryEntityInterface createOrUpdateCategoryEntity(CategoryInterface category,
			EntityInterface entity, String categoryEntityName)
	{
		CategoryEntityInterface categoryEntity = null;
		if (categoryEntityName != null)
		{
			categoryEntity = category.getCategoryEntityByName(categoryEntityName);
		}
		if (categoryEntity == null)
		{
			categoryEntity = DomainObjectFactory.getInstance().createCategoryEntity();
			categoryEntity.setName(categoryEntityName);
			categoryEntity.setEntity(entity);
		}

		return categoryEntity;
	}

	/* (non-Javadoc)
	 * @see edu.common.dynamicextensions.categoryManager.CategoryHelperInterface#setRootCategoryEntity(edu.common.dynamicextensions.domaininterface.userinterface.ContainerInterface, edu.common.dynamicextensions.domaininterface.CategoryInterface)
	 */
	public void setRootCategoryEntity(ContainerInterface container, CategoryInterface category)
	{
		category.setRootCategoryElement((CategoryEntityInterface) container.getAbstractEntity());
		((CategoryEntityInterface) container.getAbstractEntity()).setCategory(category);
	}

	/* (non-Javadoc)
	 * @see edu.common.dynamicextensions.categoryManager.CategoryHelperInterface#addControl(edu.common.dynamicextensions.domaininterface.AttributeInterface, edu.common.dynamicextensions.domaininterface.userinterface.ContainerInterface, edu.common.dynamicextensions.categoryManager.CategoryHelperInterface.ControlEnum, java.util.List<edu.common.dynamicextensions.domaininterface.PermissibleValueInterface>[])
	 */
	public ControlInterface addOrUpdateControl(EntityInterface entity, String attributeName,
			ContainerInterface container, ControlEnum controlType, String controlCaption,
			String heading, List<FormControlNotesInterface> controlNotes,
			Map<String, Object> rulesMap, Map<String, String> controlOptions,
			Map<String, String> permValueOptions, long lineNumber,
			Set<String>... permissibleValueList) throws DynamicExtensionsSystemException
	{
		if (controlType == null)
		{
			throw new DynamicExtensionsSystemException("INVALID CONTROL TYPE FOR : "
					+ controlCaption);
		}

		CategoryAttributeInterface categoryAttribute = createOrupdateCategoryAttribute(entity,
				attributeName, container);

		//removing all dependent skip Logic Attributes.
		categoryAttribute.removeAllDependentSkipLogicAttributes();

		AttributeInterface attribute = entity.getAttributeByName(attributeName);

		applyRulesInCSVFile(categoryAttribute, attribute, rulesMap);

		Set<String> permissibleValueNameList = permissibleValueList.length == 0
				? null
				: permissibleValueList[0];

		ControlInterface control = createOrUpdateControl(controlType, controlCaption, heading,
				controlNotes, container, categoryAttribute, permValueOptions, lineNumber,
				permissibleValueNameList, controlOptions);
		return control;
	}

	/**
	 * This method applies the explicit rules mentioned in CSV file to a category attribute.
	 * @param categoryAttribute
	 * @param attribute
	 * @param rulesMap
	 * @throws DynamicExtensionsSystemException
	 */
	private void applyRulesInCSVFile(CategoryAttributeInterface categoryAttribute,
			AttributeInterface attribute, Map<String, Object> rulesMap)
			throws DynamicExtensionsSystemException
	{
		Set<RuleInterface> rules = new HashSet<RuleInterface>();

		addImplicitRules(rules, attribute.getRuleCollection());
		addExplicitRules(rules, rulesMap);
		ValidatorUtil.removeConflictingRules(rules);
		categoryAttribute.setRuleCollection(rules);
	}

	/**
	 * Add any implicit rules of attribute to a category attribute.
	 * @param implicitRules
	 * @param rules
	 * @param attributeRules
	 */
	private void addImplicitRules(Set<RuleInterface> rules, Collection<RuleInterface> attributeRules)
	{
		if (attributeRules != null)
		{
			for (RuleInterface rule : attributeRules)
			{
				String ruleName = rule.getName();
				if (rule.getIsImplicitRule() || ruleName.equals(CategoryCSVConstants.REQUIRED))
				{
					rules.add(rule);
				}
			}
		}
	}

	/**
	 * @param rules
	 * @param rulesMap
	 */
	private void addExplicitRules(Set<RuleInterface> rules, Map<String, Object> rulesMap)
	{
		if (rulesMap != null && !rulesMap.isEmpty())
		{
			for (Entry<String, Object> entryObject : rulesMap.entrySet())
			{
				String ruleName = entryObject.getKey();
				RuleInterface rule = null;

				if (ruleName.equalsIgnoreCase(CategoryCSVConstants.UNIQUE)
						|| ruleName.equalsIgnoreCase(CategoryCSVConstants.REQUIRED))
				{
					DomainObjectFactory factory = DomainObjectFactory.getInstance();
					rule = factory.createRule();
					rule.setName(ruleName);
					rule.setIsImplicitRule(false);
				}
				else if (ruleName.equalsIgnoreCase(CategoryCSVConstants.RANGE)
						|| ruleName.equalsIgnoreCase(CategoryCSVConstants.DATE_RANGE))
				{
					rule = getRulSetForDateRange(entryObject, ruleName);
				}
				else if (ruleName.equalsIgnoreCase(CategoryCSVConstants.ALLOW_FUTURE_DATE))
				{
					DomainObjectFactory factory = DomainObjectFactory.getInstance();
					rule = factory.createRule();
					rule.setName(ruleName);
					rule.setIsImplicitRule(false);
				}
				if (rule != null)
				{
					rules.add(rule);
				}
			}
		}
	}

	/**
	 * This method returns the rule for date range.
	 * @param entryObject entry object.
	 * @param ruleName name of rule
	 * @return rule RuleInterface for date range.
	 */
	private RuleInterface getRulSetForDateRange(Entry<String, Object> entryObject, String ruleName)
	{
		RuleInterface rule;
		DomainObjectFactory factory = DomainObjectFactory.getInstance();
		Map<String, Object> valuesMap = (Map<String, Object>) entryObject.getValue();

		rule = factory.createRule();
		rule.setName(ruleName);
		rule.setIsImplicitRule(false);

		RuleParameterInterface minValue = factory.createRuleParameter();
		minValue.setName(CategoryCSVConstants.MIN);
		minValue.setValue((String) valuesMap.get(CategoryCSVConstants.MIN));

		RuleParameterInterface maxValue = factory.createRuleParameter();
		maxValue.setName(CategoryCSVConstants.MAX);
		maxValue.setValue((String) valuesMap.get(CategoryCSVConstants.MAX));

		rule.getRuleParameterCollection().add(minValue);
		rule.getRuleParameterCollection().add(maxValue);
		return rule;
	}

	/**
	 * @param entity
	 * @param attributeName
	 * @param container
	 * @return
	 */
	public CategoryAttributeInterface createOrupdateCategoryAttribute(EntityInterface entity,
			String attributeName, ContainerInterface container)
	{
		CategoryAttributeInterface categoryAttribute = (CategoryAttributeInterface) getCategoryAttribute(
				attributeName, container);
		if (categoryAttribute == null)
		{
			CategoryEntity categoryEntity = (CategoryEntity) container.getAbstractEntity();
			categoryAttribute = createCategoryAttribute(entity, attributeName, categoryEntity);
		}

		return categoryAttribute;
	}

	/* (non-Javadoc)
	 * @see edu.common.dynamicextensions.util.CategoryHelperInterface#createCategoryAttribute(edu.common.dynamicextensions.domaininterface.EntityInterface, java.lang.String, edu.common.dynamicextensions.domaininterface.CategoryEntityInterface)
	 */
	public CategoryAttributeInterface createCategoryAttribute(EntityInterface entity,
			String attributeName, CategoryEntityInterface categoryEntity)
	{
		CategoryAttributeInterface categoryAttribute = DomainObjectFactory.getInstance()
				.createCategoryAttribute();
		categoryAttribute.setName(attributeName + " Category Attribute");
		categoryAttribute.setAbstractAttribute(entity.getAbstractAttributeByName(attributeName));

		categoryEntity.addCategoryAttribute(categoryAttribute);
		categoryAttribute.setCategoryEntity(categoryEntity);

		categoryEntity.addCategoryAttribute(categoryAttribute);
		categoryAttribute.setCategoryEntity(categoryEntity);

		return categoryAttribute;
	}

	/**
	 * @param entity
	 * @param attributeName
	 * @param categoryEntity
	 * @return
	 */
	public CategoryAttributeInterface getCategoryAttribute(EntityInterface entity,
			String attributeName, CategoryEntityInterface categoryEntity)
	{
		CategoryAttributeInterface categoryAttribute = categoryEntity
				.getAttributeByName(attributeName + CategoryConstants.CAT_ATTRIBUTE_NAME_POSTFIX);
		if (categoryAttribute == null)
		{
			categoryAttribute = createCategoryAttribute(entity, attributeName, categoryEntity);
		}
		return categoryAttribute;
	}

	/**
	 * @param controlType
	 * @param controlCaption
	 * @param container
	 * @param categoryAttribute
	 * @param permValueNames
	 * @return
	 * @throws DynamicExtensionsApplicationException
	 * @throws DynamicExtensionsSystemException
	 */
	private ControlInterface createOrUpdateControl(ControlEnum controlType, String controlCaption,
			String heading, List<FormControlNotesInterface> controlNotes,
			ContainerInterface container, CategoryAttributeInterface categoryAttribute,
			Map<String, String> permValueOptions, long lineNumber,
			Set<String> permissibleValueNameList, Map<String, String> controlOptions)
			throws DynamicExtensionsSystemException
	{
		ControlInterface control = null;
		EntityInterface entity = categoryAttribute.getAbstractAttribute().getEntity();
		String attributeName = categoryAttribute.getAbstractAttribute().getName();
		switch (controlType)
		{
			case TEXT_FIELD_CONTROL :
				control = createOrUpdateTextFieldControl(container, categoryAttribute);
				break;
			case LIST_BOX_CONTROL :
				control = createOrUpdateSelectControl(
						container,
						categoryAttribute,

						createPermissibleValuesList(entity, attributeName, lineNumber,
								permissibleValueNameList,controlOptions), controlType, permValueOptions,
						lineNumber, controlOptions);
				break;
			case COMBO_BOX_CONTROL :
				control = createOrUpdateSelectControl(
						container,
						categoryAttribute,
						createPermissibleValuesList(entity, attributeName, lineNumber,
								permissibleValueNameList,controlOptions), controlType, permValueOptions,
						lineNumber, controlOptions);
				break;
			case DATE_PICKER_CONTROL :
				control = createOrUpdateDatePickerControl(container, categoryAttribute);
				break;
			case FILE_UPLOAD_CONTROL :
				control = createOrUpdateFileUploadControl(container, categoryAttribute);
				break;
			case TEXT_AREA_CONTROL :
				control = createOrUpdateTextAreaControl(container, categoryAttribute);
				break;
			case RADIO_BUTTON_CONTROL :
				control = createOrUpdateRadioButtonControl(
						container,
						categoryAttribute,
						createPermissibleValuesList(entity, attributeName, lineNumber,
								permissibleValueNameList,controlOptions), permValueOptions, lineNumber);
				break;
			case CHECK_BOX_CONTROL :
				control = createOrUpdateCheckBoxControl(container, categoryAttribute);
				break;
			case LABEL_CONTROL :
				break;
			case MULTISELECT_CHECKBOX_CONTROL :
				control = createOrUpdateSelectControl(
						container,
						categoryAttribute,
						createPermissibleValuesList(entity, attributeName, lineNumber,
								permissibleValueNameList,controlOptions), controlType, permValueOptions,
						lineNumber, controlOptions);
				break;
			default :
				throw new DynamicExtensionsSystemException("ERROR: INCORRECT CONTROL TYPE");
		}
		updateContainerAndControl(container, control, categoryAttribute, controlCaption);

		if (heading.length() != 0)
		{
			control.setHeading(heading);
		}
		if (!controlNotes.isEmpty())
		{
			control.setFormNotes(controlNotes);
		}

		return control;
	}

	/**
	 * @param attributeName
	 * @param container
	 * @return
	 */
	private BaseAbstractAttributeInterface getCategoryAttribute(String attributeName,
			ContainerInterface container)
	{
		BaseAbstractAttributeInterface categoryAttribute = null;
		for (ControlInterface control : container.getControlCollection())
		{
			if (control.getBaseAbstractAttribute() == null)
			{
				continue;
			}
			if (!(control.getBaseAbstractAttribute() instanceof CategoryAssociation)
					&& ((CategoryAttributeInterface) control.getBaseAbstractAttribute())
							.getAbstractAttribute().getName().equals(attributeName))
			{
				categoryAttribute = control.getBaseAbstractAttribute();
				break;
			}
			else if (!(control.getBaseAbstractAttribute() instanceof CategoryAssociation)
					&& ((CategoryAttributeInterface) control.getBaseAbstractAttribute())
							.getAbstractAttribute().getName().startsWith(DEConstants.DEPRECATED))
			{
				categoryAttribute = control.getBaseAbstractAttribute();
				((CategoryAttributeInterface) categoryAttribute)
						.setAbstractAttribute(((CategoryEntityInterface) container
								.getAbstractEntity()).getEntity().getAbstractAttributeByName(
								attributeName));
				break;
			}
		}

		return categoryAttribute;
	}

	/* (non-Javadoc)
	 * @see edu.wustl.catissuecore.test.CategoryHelperInterface#associateCategoryContainers(edu.common.dynamicextensions.domaininterface.userinterface.ContainerInterface, edu.common.dynamicextensions.domaininterface.userinterface.ContainerInterface, java.util.List, int)
	 */
	public CategoryAssociationControlInterface associateCategoryContainers(
			CategoryInterface category, EntityGroupInterface entityGroup,
			ContainerInterface sourceContainer, ContainerInterface targetContainer,
			List<AssociationInterface> associationList, int noOfEntries, String instance)
			throws DynamicExtensionsSystemException, DynamicExtensionsApplicationException
	{
		ContainerInterface rootContainer = null;

		if (category.getRootCategoryElement() != null)
		{
			rootContainer = new ArrayList<ContainerInterface>(category.getRootCategoryElement()
					.getContainerCollection()).get(0);
		}

		CategoryAssociationControlInterface associationControl = (CategoryAssociationControlInterface) getAssociationControl(
				rootContainer, targetContainer.getId());

		if (associationControl != null)
		{
			if (associationControl.getParentContainer().equals(sourceContainer))
			{
				// This sets the value for Paste Button as defined in CSV file
				associationControl.setIsPasteEnable(categoryFileParser
						.isPasteButtonEnabled(noOfEntries));
				return associationControl;
			}
			else
			{
				removeControl(associationControl.getParentContainer(), associationControl);
				CategoryAssociationInterface oldAssociation = (CategoryAssociationInterface) associationControl
						.getBaseAbstractAttribute();
				removeCategoryAssociation(oldAssociation);
				associationControl.setBaseAbstractAttribute(null);

			}
			// This sets the value for Paste Button as defined in CSV file
			associationControl.setIsPasteEnable(categoryFileParser
					.isPasteButtonEnabled(noOfEntries));
		}

		CategoryAssociationControlInterface categoryAssociationControl = getControlInterface(
				sourceContainer, targetContainer);
		if (categoryAssociationControl == null)
		{
			CategoryEntityInterface sourceCategoryEntity = (CategoryEntityInterface) sourceContainer
					.getAbstractEntity();
			CategoryEntityInterface targetCategoryEntity = (CategoryEntityInterface) targetContainer
					.getAbstractEntity();

			CategoryAssociationInterface categoryAssociation = associateCategoryEntities(
					sourceCategoryEntity, targetCategoryEntity, sourceCategoryEntity.getName()
							+ " to " + targetCategoryEntity.getName() + " category association",
					noOfEntries, entityGroup, associationList, instance);
			categoryAssociationControl = createCategoryAssociationControl(sourceContainer,
					targetContainer, categoryAssociation, targetContainer.getCaption());
		}
		categoryAssociationControl.setCaption(targetContainer.getCaption());
		if (categoryFileParser != null)
		{
			// This sets the value for Paste Button as defined in CSV file
			categoryAssociationControl.setIsPasteEnable(categoryFileParser
					.isPasteButtonEnabled(noOfEntries));
		}
		return categoryAssociationControl;
	}

	/**
	 * @param path
	 * @param associationList
	 * @throws DynamicExtensionsSystemException
	 * @throws DynamicExtensionsApplicationException
	 */
	private void updatePath(PathInterface path, List<AssociationInterface> associationList)
			throws DynamicExtensionsSystemException, DynamicExtensionsApplicationException
	{
		DomainObjectFactory factory = DomainObjectFactory.getInstance();

		//clear old path
		path.setPathAssociationRelationCollection(null);
		int pathSequenceNumber = 1;
		for (AssociationInterface association : associationList)
		{
			PathAssociationRelationInterface pathAssociationRelation = factory
					.createPathAssociationRelation();
			pathAssociationRelation.setPathSequenceNumber(pathSequenceNumber++);
			pathAssociationRelation.setAssociation(association);

			pathAssociationRelation.setPath(path);
			path.addPathAssociationRelation(pathAssociationRelation);
		}
	}

	/**
	 * @param path
	 * @param instance
	 * @throws DynamicExtensionsSystemException
	 */
	public void addInstanceInformationToPath(PathInterface path, String instance)
			throws DynamicExtensionsSystemException
	{

		int counter = 0;
		if (path.getPathAssociationRelationCollection() != null)
		{
			for (PathAssociationRelationInterface associationRelation : path
					.getSortedPathAssociationRelationCollection())
			{
				String[] entityArray = instance.split("->");
				String sourceEntity = entityArray[counter];

				if (sourceEntity.indexOf('[') == -1 || sourceEntity.indexOf(']') == -1)
				{
					throw new DynamicExtensionsSystemException(
							"ERROR: INSTANCE INFORMATION IS NOT IN THE CORRECT FORMAT " + instance);

				}
				String targetEntity = entityArray[counter + 1];
				associationRelation.setSourceInstanceId(getInsatnce(sourceEntity));
				associationRelation.setTargetInstanceId(getInsatnce(targetEntity));
				counter++;
			}
		}
	}

	/**
	 * Separates the instance information form the string of the format entityName[instance]
	 * @param categoryEntityName
	 * @return
	 */
	public Long getInsatnce(String categoryEntityName)
	{
		return Long.parseLong(categoryEntityName.substring(categoryEntityName.indexOf('[') + 1,
				categoryEntityName.indexOf(']')));
	}

	/* (non-Javadoc)
	 * @see edu.wustl.catissuecore.test.CategoryHelperInterface#getNextSequenceNumber(edu.common.dynamicextensions.domaininterface.userinterface.ContainerInterface)
	 */
	public int getNextSequenceNumber(ContainerInterface container)
	{
		return DynamicExtensionsUtility.getSequenceNumberForNextControl(container);
	}

	/**
	 * @param sourceCategoryEntity source category entity
	 * @param targetCategoryEntity target category entity
	 * @param path path information between the category entities
	 */
	private PathInterface addPathBetweenCategoryEntities(
			CategoryEntityInterface sourceCategoryEntity,
			CategoryEntityInterface targetCategoryEntity)
	{
		PathInterface path = DomainObjectFactory.getInstance().createPath();
		targetCategoryEntity.setPath(path);
		targetCategoryEntity.setTreeParentCategoryEntity(sourceCategoryEntity);

		return path;
	}

	/**
	 * Method associates the source and the target category entity
	 * @param sourceCategoryEntity source category entity
	 * @param targetCategoryEntity target category entity
	 * @param name name of the category association
	 * @return CategoryAssociationInterface category association object
	 * @throws DynamicExtensionsApplicationException
	 * @throws DynamicExtensionsSystemException
	 */
	public CategoryAssociationInterface associateCategoryEntities(
			CategoryEntityInterface sourceCategoryEntity,
			CategoryEntityInterface targetCategoryEntity, String name, int numberOfentries,
			EntityGroupInterface entityGroup, List<AssociationInterface> associationList,
			String instance) throws DynamicExtensionsSystemException,
			DynamicExtensionsApplicationException
	{

		PathInterface path = addPathBetweenCategoryEntities(sourceCategoryEntity,
				targetCategoryEntity);
		updatePath(path, associationList);
		targetCategoryEntity.setNumberOfEntries(numberOfentries);

		CategoryAssociationInterface categoryAssociation = DomainObjectFactory.getInstance()
				.createCategoryAssociation();
		categoryAssociation.setName(name);

		sourceCategoryEntity.addChildCategory(targetCategoryEntity);

		categoryAssociation.setCategoryEntity(sourceCategoryEntity);
		categoryAssociation.setTargetCategoryEntity(targetCategoryEntity);

		sourceCategoryEntity.getCategoryAssociationCollection().add(categoryAssociation);

		addInstanceInformationToPath(path, instance);

		return categoryAssociation;
	}

	/**
	 * Method creates the association between the given parent and the target container
	 * @param parentContainer main form
	 * @param targetContainer sub form
	 * @param categoryAssociation association between category entities
	 * @param caption name to be displayed on UI
	 * @return CategoryAssociationControlInterface category association control object
	 */
	private CategoryAssociationControlInterface createCategoryAssociationControl(
			ContainerInterface parentContainer, ContainerInterface targetContainer,
			CategoryAssociationInterface categoryAssociation, String caption)
	{

		CategoryAssociationControlInterface categoryAssociationControl = DomainObjectFactory
				.getInstance().createCategoryAssociationControl();
		categoryAssociationControl.setCaption(caption);
		categoryAssociationControl.setContainer(targetContainer);
		categoryAssociationControl.setBaseAbstractAttribute(categoryAssociation);

		int sequenceNumber = getNextSequenceNumber(parentContainer);
		categoryAssociationControl.setSequenceNumber(sequenceNumber);

		parentContainer.addControl(categoryAssociationControl);
		categoryAssociationControl.setParentContainer((Container) parentContainer);

		return categoryAssociationControl;
	}

	/**
	 * @param captionValue
	 * @param abstractEntity category entity
	 * @return container object for category entity
	 */
	private ContainerInterface createContainer(AbstractEntityInterface abstractEntity,
			String captionValue)
	{
		ContainerInterface container = null;
		if (abstractEntity.getContainerCollection().size() > 0)
		{
			container = new ArrayList<ContainerInterface>(abstractEntity.getContainerCollection())
					.get(0);
		}
		if (container == null)
		{
			container = DomainObjectFactory.getInstance().createContainer();
			container.setMainTableCss("formRequiredLabel");
			container.setRequiredFieldIndicatior("*");
			container.setRequiredFieldWarningMessage("indicates mandatory fields.");
			container.setAbstractEntity(abstractEntity);
			abstractEntity.addContainer(container);
		}
		String caption = captionValue;
		if (caption == null)
		{
			caption = abstractEntity.getName() + " category container";
		}

		container.setCaption(caption);

		return container;
	}

	/**
	 *
	 * @param container category entity container
	 * @param baseAbstractAttribute category attribute
	 * @return text field object
	 */
	private TextFieldInterface createOrUpdateTextFieldControl(ContainerInterface container,
			BaseAbstractAttributeInterface baseAbstractAttribute)
	{
		ControlInterface control = getControl(container, baseAbstractAttribute);

		TextFieldInterface textField = null;
		if (control == null || control instanceof TextFieldInterface)
		{
			textField = (TextFieldInterface) control;
		}
		else
		{
			removeControl(container, control);
		}

		if (textField == null)
		{
			textField = DomainObjectFactory.getInstance().createTextField();
			textField.setColumns(50);
		}

		return textField;
	}

	/**
	 * @param container
	 * @param baseAbstractAttribute
	 * @return
	 */
	public static ControlInterface getControl(ContainerInterface container,
			BaseAbstractAttributeInterface baseAbstractAttribute)
	{
		ControlInterface controlInterface = null;
		for (ControlInterface control : container.getControlCollection())
		{
			if (baseAbstractAttribute.equals(control.getBaseAbstractAttribute()))
			{
				controlInterface = control;
			}
		}

		return controlInterface;
	}

	/**
	 * @param container category entity container
	 * @param baseAbstractAttribute category attribute
	 * @param permissibleValues list of permissible values
	 * @return list box object
	 * @throws DynamicExtensionsSystemException
	 */
	private SelectInterface createOrUpdateSelectControl(ContainerInterface container,
			BaseAbstractAttributeInterface baseAbstractAttribute,
			Set<PermissibleValueInterface> permissibleValues, ControlEnum controlType,
			Map<String, String> permValueOptions, long lineNumber,
			Map<String, String> controlOptions) throws DynamicExtensionsSystemException
	{
		CategoryAttribute categoryAttribute = (CategoryAttribute) baseAbstractAttribute;
		ControlInterface control = getControl(container, baseAbstractAttribute);
		SelectInterface selectControl = null;
		if (control == null || control instanceof SelectInterface)
		{
			if (control instanceof ComboBox && controlType.equals(controlType.COMBO_BOX_CONTROL)
					|| control instanceof ListBox
					&& controlType.equals(controlType.LIST_BOX_CONTROL)
					|| control instanceof MultiSelectCheckBox
					&& controlType.equals(controlType.MULTISELECT_CHECKBOX_CONTROL))

			{
				selectControl = (SelectInterface) control;
			}
			else if (control != null)
			{
				removeControl(container, control);
			}
		}
		else
		{
			removeControl(container, control);
		}

		if (selectControl == null)
		{
			if (controlType.equals(controlType.LIST_BOX_CONTROL))
			{
				if (categoryAttribute.getAbstractAttribute() instanceof AssociationInterface)
				{
					selectControl = DomainObjectFactory.getInstance().createListBox();
				}
				else
				{
					selectControl = DomainObjectFactory.getInstance().createComboBox();
				}
			}
			else if (controlType.equals(controlType.MULTISELECT_CHECKBOX_CONTROL)
					&& categoryAttribute.getAbstractAttribute() instanceof AssociationInterface)
			{
				selectControl = DomainObjectFactory.getInstance().createMultiSelectCheckBox();
			}
			else if (controlType.equals(controlType.COMBO_BOX_CONTROL))
			{
				selectControl = DomainObjectFactory.getInstance().createComboBox();
			}
		}

		if (!controlOptions.containsKey(DEConstants.PV_TYPE))
		{
			populatePermissibleValues(permissibleValues, permValueOptions, lineNumber,
					categoryAttribute);
		}

		return selectControl;
	}

	private void populatePermissibleValues(Set<PermissibleValueInterface> permissibleValues,
			Map<String, String> permValueOptions, long lineNumber,
			CategoryAttribute categoryAttribute) throws DynamicExtensionsSystemException
	{
		if (categoryAttribute.getId() != null
				&& isDataEntered(categoryAttribute.getCategoryEntity())
				&& checkForRemovedPermissibleValues(categoryAttribute, permissibleValues))
		{
			throw new DynamicExtensionsSystemException(
					ApplicationProperties.getValue(CategoryConstants.CREATE_CAT_FAILS)
							+ ApplicationProperties.getValue(CategoryConstants.PV_EDIT));
		}

		// Set permissible values
		UserDefinedDEInterface userDefinedDE = setPermissibleValuesForCatAttribute(
				categoryAttribute, permissibleValues);
		setOptions(userDefinedDE, permValueOptions, lineNumber);

		AttributeInterface attribute = categoryAttribute.getAbstractAttribute().getEntity()
				.getAttributeByName(categoryAttribute.getAbstractAttribute().getName());
		AttributeTypeInformationInterface attributeTypeInformation = attribute
				.getAttributeTypeInformation();

		if (attributeTypeInformation.getDefaultValue() != null)
		{
			//categoryAttribute.setDefaultValue(attributeTypeInformation.getDefaultValue());
			setDefaultValue(categoryAttribute, attributeTypeInformation.getDefaultValue(),
					false);
		}
	}

	/**
	 *
	 * @param categoryAttribute category attribute
	 * @param permissibleValues permissible values
	 * @return the UserDefinedDE object with PVs set
	 */
	private UserDefinedDEInterface setPermissibleValuesForCatAttribute(
			CategoryAttribute categoryAttribute, Set<PermissibleValueInterface> permissibleValues)
	{
		UserDefinedDEInterface userDefinedDE = null;

		if (categoryAttribute.getId() == null)
		{
			userDefinedDE = DomainObjectFactory.getInstance().createUserDefinedDE();
			//add new permissible value set to categoryAttribute
			categoryAttribute.setDataElement(userDefinedDE);
		}
		else
		{
			userDefinedDE = (UserDefinedDEInterface) categoryAttribute.getDataElement(null);
			userDefinedDE.clearPermissibleValues();
		}
		for (PermissibleValueInterface pv : permissibleValues)
		{
			//remove all dependent skip Logic Attributes.
			pv.removeAllDependentSkipLogicAttributes();
			userDefinedDE.addPermissibleValue(pv);
		}

		return userDefinedDE;
	}

	/**
	 * @param categoryEntity
	 * @return true if the data is entered for the category
	 * @throws DynamicExtensionsSystemException
	 */
	public static boolean isDataEntered(AbstractEntityInterface abstractEntity)
			throws DynamicExtensionsSystemException
	{

		boolean isDataEntered = false;
		String rootTableName = abstractEntity.getTableProperties().getName();
		if (EntityManagerUtil.getNoOfRecordInTable(rootTableName) > 0)
		{
			isDataEntered = true;
		}

		return isDataEntered;
	}

	/**
	 * This method returns true if the existing permissible value is not present
	 * in the incoming permissible value set
	 * @param categoryAttribute
	 * @param permissibleValues
	 * @return
	 * @throws DynamicExtensionsSystemException
	 */
	private boolean checkForRemovedPermissibleValues(CategoryAttribute categoryAttribute,
			Set<PermissibleValueInterface> permissibleValues)
			throws DynamicExtensionsSystemException
	{
		boolean isPermissibleValueRemoved = false;
		Collection<PermissibleValueInterface> existingPv = null;
		if (categoryAttribute.getDataElement(null) != null)
		{
			existingPv = ((UserDefinedDEInterface) categoryAttribute.getDataElement(null))
					.getPermissibleValueCollection();
			for (PermissibleValueInterface pv : existingPv)
			{
				if (!permissibleValues.contains(pv))
				{
					isPermissibleValueRemoved = true;
					break;
				}

			}
		}

		if (isPermissibleValueRemoved)
		{
			throw new DynamicExtensionsSystemException(
					ApplicationProperties.getValue(CategoryConstants.CREATE_CAT_FAILS)
							+ ApplicationProperties.getValue(CategoryConstants.LINE_NUMBER)
							+ categoryFileParser.getLineNumber()
							+ ApplicationProperties.getValue(CategoryConstants.PV_EDIT)
							+ "\nORIGINAL PV SET FOR CATEGORY ATTRIBUE "
							+ categoryAttribute.getName() + ":" + getExistingPvList(existingPv));
		}
		return isPermissibleValueRemoved;
	}

	/**
	 * This method will return the String formed from the given existingPv parameter.
	 * @param existingPv colection of permissible values.
	 * @return string formed using the above parameter.
	 */
	private String getExistingPvList(Collection<PermissibleValueInterface> existingPv)
	{
		StringBuffer existingPvList = new StringBuffer();
		for (PermissibleValueInterface permissibleValue : existingPv)
		{
			existingPvList.append(permissibleValue.getValueAsObject());
			existingPvList.append(DEConstants.COMMA);
		}
		return existingPvList.toString();
	}

	/**
	 * @param control
	 * @param nextLine
	 * @throws DynamicExtensionsSystemException
	 */
	public void setControlOptions(ControlInterface dyextnBaseDomainObject,
			Map<String, String> options, long lineNumber) throws DynamicExtensionsSystemException
	{
		try
		{
			if (options.isEmpty())
			{
				return;
			}
				Iterator<TaggedValueInterface> tagValueIterator = dyextnBaseDomainObject.getBaseAbstractAttribute()
						.getTaggedValueCollection().iterator();
				
				while(tagValueIterator.hasNext())
				{
					tagValueIterator.next();
					tagValueIterator.remove();
				}
			for (Entry<String, String> entryObject : options.entrySet())
			{
				String taggedValue = entryObject.getKey().toLowerCase();

				if (CategoryConstants.ATRRIBUTE_TAG_VALUES.contains(taggedValue))
				{
					((ControlInterface) dyextnBaseDomainObject).getBaseAbstractAttribute()
					.getTaggedValueCollection()
					.add(new TaggedValue(taggedValue, entryObject.getValue()));
				}else
				{
					setControlProperty(dyextnBaseDomainObject, lineNumber,
							entryObject);
				}


			}
		}
		catch (SecurityException e)
		{
			throw new DynamicExtensionsSystemException(
					ApplicationProperties.getValue(CategoryConstants.CREATE_CAT_FAILS)
							+ ApplicationProperties.getValue(CategoryConstants.CONTACT_ADMIN), e);
		}
		
		catch (IllegalArgumentException e)
		{
			throw new DynamicExtensionsSystemException(
					ApplicationProperties.getValue(CategoryConstants.CREATE_CAT_FAILS)
							+ ApplicationProperties.getValue(CategoryConstants.CONTACT_ADMIN), e);
		}
		
	}
	
	
	
	
	/**
	 * @param dyextnBaseDomainObject
	 * @param options
	 * @param lineNumber
	 * @throws DynamicExtensionsSystemException
	 */
	public void setOptions(DynamicExtensionBaseDomainObjectInterface dyextnBaseDomainObject,
			Map<String, String> options, long lineNumber) throws DynamicExtensionsSystemException
	{
		if (options.isEmpty())
		{
			return;
		}
		for (Entry<String, String> entryObject : options.entrySet())
		{
			setControlProperty(dyextnBaseDomainObject, lineNumber, entryObject);
		}

	}

	private void setControlProperty(
			DynamicExtensionBaseDomainObjectInterface dyextnBaseDomainObject, long lineNumber,
			Entry<String, String> entryObject) throws DynamicExtensionsSystemException
	{
		try
		{

			String optionString = entryObject.getKey();
			String methodName = CategoryConstants.SET + optionString;

			Class[] types = getParameterType(methodName, dyextnBaseDomainObject);
			if (types.length < 1)
			{
				throw new DynamicExtensionsSystemException(ApplicationProperties
						.getValue(CategoryConstants.CREATE_CAT_FAILS)
						+ ApplicationProperties.getValue(CategoryConstants.LINE_NUMBER)
						+ lineNumber
						+ ApplicationProperties.getValue("incorrectControlOption")
						+ optionString);
			}
			List<Object> values = new ArrayList<Object>();
			values.add(getFormattedValues(types[0], entryObject.getValue()));

			Method method;
			method = dyextnBaseDomainObject.getClass().getMethod(methodName, types);
			method.invoke(dyextnBaseDomainObject, values.toArray());

		}
		catch (SecurityException e)
		{
			throw new DynamicExtensionsSystemException(ApplicationProperties
					.getValue(CategoryConstants.CREATE_CAT_FAILS)
					+ ApplicationProperties.getValue(CategoryConstants.CONTACT_ADMIN), e);
		}
		catch (NoSuchMethodException e)
		{
			throw new DynamicExtensionsSystemException(ApplicationProperties
					.getValue(CategoryConstants.CREATE_CAT_FAILS)
					+ ApplicationProperties.getValue(CategoryConstants.LINE_NUMBER)
					+ lineNumber
					+ ApplicationProperties.getValue("incorrectOption"), e);
		}
		catch (IllegalArgumentException e)
		{
			throw new DynamicExtensionsSystemException(ApplicationProperties
					.getValue(CategoryConstants.CREATE_CAT_FAILS)
					+ ApplicationProperties.getValue(CategoryConstants.CONTACT_ADMIN), e);
		}
		catch (IllegalAccessException e)
		{
			throw new DynamicExtensionsSystemException(ApplicationProperties
					.getValue(CategoryConstants.CREATE_CAT_FAILS)
					+ ApplicationProperties.getValue(CategoryConstants.CONTACT_ADMIN), e);
		}
		catch (InvocationTargetException e)
		{
			throw new DynamicExtensionsSystemException(ApplicationProperties
					.getValue(CategoryConstants.CREATE_CAT_FAILS)
					+ ApplicationProperties.getValue(CategoryConstants.CONTACT_ADMIN), e);
		}
		catch (InstantiationException e)
		{
			throw new DynamicExtensionsSystemException(ApplicationProperties
					.getValue(CategoryConstants.CREATE_CAT_FAILS)
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
	 * @throws InvocationTargetException
	 */
	private Object getFormattedValues(Class type, String string) throws NoSuchMethodException,
			InstantiationException, IllegalAccessException, InvocationTargetException
	{
		if ("java.util.Date".equals(type.getName()))
		{
			SimpleDateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy", Locale.getDefault());
			Date date = null;
			try
			{
				date = dateFormat.parse(string);
			}
			catch (ParseException e)
			{
				LOGGER.error("Exception occured while parsing Date", e);
			}
			return date;
		}
		else
		{
			Method method = type.getMethod("valueOf", new Class[]{String.class});
			return method.invoke(type, new Object[]{string});
		}
	}

	/**
	 * @param container
	 * @param control
	 */
	private void removeControl(ContainerInterface container, ControlInterface control)
	{
		control.setParentContainer(null);
		container.getControlCollection().remove(control);
	}

	/**
	 * @param categoryAssociation
	 */
	private void removeCategoryAssociation(CategoryAssociationInterface categoryAssociation)
	{
		CategoryEntityInterface sourceCategoryEntity = categoryAssociation.getCategoryEntity();
		CategoryEntityInterface targetCategoryEntity = categoryAssociation
				.getTargetCategoryEntity();

		targetCategoryEntity.setPath(null);
		sourceCategoryEntity.getChildCategories().remove(targetCategoryEntity);
		sourceCategoryEntity.getCategoryAssociationCollection().remove(categoryAssociation);
		categoryAssociation.setCategoryEntity(null);

		targetCategoryEntity.setParentCategoryEntity(null);
	}

	/**
	 * @param container
	 * @param control
	 * @param baseAbstractAttribute
	 * @param controlCaption
	 */
	private void updateContainerAndControl(ContainerInterface container, ControlInterface control,
			BaseAbstractAttributeInterface baseAbstractAttribute, String controlCaption)
	{
		if (control != null)
		{
			container.addControl(control);
			control.setParentContainer((Container) container);
			control.setBaseAbstractAttribute(baseAbstractAttribute);
			control.setCaption(controlCaption);
		}
	}

	/**
	 * @param container category entity container
	 * @param baseAbstractAttribute category attribute
	 * @return date picker object
	 */
	private DatePickerInterface createOrUpdateDatePickerControl(ContainerInterface container,
			BaseAbstractAttributeInterface baseAbstractAttribute)
	{
		ControlInterface control = getControl(container, baseAbstractAttribute);
		DatePickerInterface datePicker = null;
		if (control == null || control instanceof DatePickerInterface)
		{
			datePicker = (DatePickerInterface) control;
		}
		else
		{
			removeControl(container, control);
		}

		if (datePicker == null)
		{
			datePicker = DomainObjectFactory.getInstance().createDatePicker();

		}

		datePicker.setDateValueType(ProcessorConstants.DATE_ONLY_FORMAT);

		return datePicker;
	}

	/**
	 * @param container category entity container
	 * @param baseAbstractAttribute category attribute
	 * @return file upload object
	 */
	private FileUploadInterface createOrUpdateFileUploadControl(ContainerInterface container,
			BaseAbstractAttributeInterface baseAbstractAttribute)
	{
		ControlInterface control = getControl(container, baseAbstractAttribute);
		FileUploadInterface fileUpload = null;
		if (control == null || control instanceof FileUploadInterface)
		{
			fileUpload = (FileUploadInterface) control;
		}
		else
		{
			removeControl(container, control);
		}

		if (fileUpload == null)
		{
			fileUpload = DomainObjectFactory.getInstance().createFileUploadControl();
		}

		return fileUpload;
	}

	/**
	 * @param container category entity container
	 * @param baseAbstractAttribute category attribute
	 * @return text area object
	 */
	private TextAreaInterface createOrUpdateTextAreaControl(ContainerInterface container,
			BaseAbstractAttributeInterface baseAbstractAttribute)
	{
		ControlInterface control = getControl(container, baseAbstractAttribute);
		TextAreaInterface textArea = null;
		if (control == null || control instanceof TextAreaInterface)
		{
			textArea = (TextAreaInterface) control;
		}
		else
		{
			removeControl(container, control);
		}

		if (textArea == null)
		{
			textArea = DomainObjectFactory.getInstance().createTextArea();
			textArea.setColumns(50);
			textArea.setRows(5);
		}

		return textArea;
	}

	/**
	 * @param container category entity container
	 * @param baseAbstractAttribute category attribute
	 * @param permissibleValues list of permissible values
	 * @return RadioButtonInterface radio button object
	 * @throws DynamicExtensionsSystemException
	 */
	private RadioButtonInterface createOrUpdateRadioButtonControl(ContainerInterface container,
			BaseAbstractAttributeInterface baseAbstractAttribute,
			Set<PermissibleValueInterface> permissibleValues, Map<String, String> permValueOptions,
			long lineNumber) throws DynamicExtensionsSystemException
	{
		ControlInterface control = getControl(container, baseAbstractAttribute);
		RadioButtonInterface radioButton = null;
		if (control == null || control instanceof RadioButtonInterface)
		{
			radioButton = (RadioButtonInterface) control;
		}
		else
		{
			removeControl(container, control);
		}

		if (radioButton == null)
		{
			radioButton = DomainObjectFactory.getInstance().createRadioButton();
		}

		if (baseAbstractAttribute.getId() != null
				&& isDataEntered(((CategoryAttribute) baseAbstractAttribute).getCategoryEntity())
				&& checkForRemovedPermissibleValues(((CategoryAttribute) baseAbstractAttribute),
						permissibleValues))
		{
			throw new DynamicExtensionsSystemException(
					ApplicationProperties.getValue(CategoryConstants.CREATE_CAT_FAILS)
							+ ApplicationProperties.getValue(CategoryConstants.PV_EDIT));
		}

		UserDefinedDEInterface userDefinedDE = (UserDefinedDEInterface) ((CategoryAttribute) baseAbstractAttribute)
				.getDataElement(null);
		if (userDefinedDE == null)
		{
			userDefinedDE = DomainObjectFactory.getInstance().createUserDefinedDE();
			((CategoryAttribute) baseAbstractAttribute).setDataElement(userDefinedDE);
		}
		userDefinedDE.clearPermissibleValues();
		for (PermissibleValueInterface pv : permissibleValues)
		{
			//remove all dependent skip Logic Attributes.
			pv.removeAllDependentSkipLogicAttributes();
			userDefinedDE.addPermissibleValue(pv);
		}

		((CategoryAttribute) baseAbstractAttribute).setDataElement(userDefinedDE);

		setOptions(userDefinedDE, permValueOptions, lineNumber);
		return radioButton;
	}

	/**
	 * @param container category entity container
	 * @param baseAbstractAttribute category attribute
	 * @return check box object
	 */
	private CheckBoxInterface createOrUpdateCheckBoxControl(ContainerInterface container,
			BaseAbstractAttributeInterface baseAbstractAttribute)
	{
		ControlInterface control = getControl(container, baseAbstractAttribute);
		CheckBoxInterface checkBox = null;
		if (control == null || control instanceof CheckBoxInterface)
		{
			checkBox = (CheckBoxInterface) control;
		}
		else
		{
			removeControl(container, control);
		}

		if (checkBox == null)
		{
			checkBox = DomainObjectFactory.getInstance().createCheckBox();
		}

		return checkBox;
	}

	/**
	 * This method creates a list of permissible values for a category attribute
	 * @param entity entity which contains attribute by the given name
	 * @param attributeName name of the attribute
	 * @param desiredPermissibleValues subset of permissible values for this category attribute
	 * @return list of permissible values for category attribute
	 * @throws DynamicExtensionsApplicationException
	 * @throws DynamicExtensionsSystemException
	 */
	public Set<PermissibleValueInterface> createPermissibleValuesList(EntityInterface entity,
			String attributeName, Long lineNo, Set<String> desiredPermissibleValues,Map<String, String> controlOptions)
			throws DynamicExtensionsSystemException
	{

		AttributeInterface attribute = entity.getAttributeByName(attributeName);
		AttributeTypeInformationInterface attributeTypeInformation = attribute
				.getAttributeTypeInformation();
		UserDefinedDEInterface userDefinedDE = (UserDefinedDE) attributeTypeInformation
				.getDataElement();
		Set<PermissibleValueInterface> permissibleValues = new HashSet<PermissibleValueInterface>();

		if (!controlOptions.containsKey(DEConstants.PV_TYPE))
		{
			if (userDefinedDE == null)
			{
				LOGGER.error("Permissible values not defined for " + attribute.getName()
						+ " at model level.");
				throw new DynamicExtensionsSystemException("Permissible values not defined for "
						+ attribute.getName() + " at model level.");
			}

			if (desiredPermissibleValues == null)
			{
				permissibleValues.addAll(userDefinedDE.getPermissibleValueCollection());
			}
			else
			{
				Collection<PermissibleValueInterface> allPermissibleValues = userDefinedDE
						.getPermissibleValueCollection();
				for (PermissibleValueInterface pv : allPermissibleValues)
				{
					if (desiredPermissibleValues.contains(DynamicExtensionsUtility
							.getUnEscapedStringValue(pv.getValueAsObject().toString())))
					{
						permissibleValues.add(pv);
						desiredPermissibleValues.remove(DynamicExtensionsUtility
								.getUnEscapedStringValue(pv.getValueAsObject().toString()));
					}
				}
			}
			StringBuffer missingPVList = new StringBuffer();
			if (desiredPermissibleValues != null && !desiredPermissibleValues.isEmpty())
			{
				for (String pv : desiredPermissibleValues)
				{
					missingPVList.append(pv).append(',');
				}
			}
			if (missingPVList.length() > 0)
			{
				String pvList = missingPVList.substring(0, (missingPVList.length() - 1));
				LOGGER.error("Permissible values subset defined for attribute " + attributeName
						+ " is not correct.");
				LOGGER.error("Following Permissible values are not present at attribute level. Add them to attribute and then try again");
				LOGGER.error(pvList);
				throw new DynamicExtensionsSystemException(
						"Permissible values subset defined for attribute " + attributeName
								+ " is not correct. Please add " + pvList
								+ " to model level Permissible values");
			}

		}

		return permissibleValues;
	}

	/**
	 *This method will return the list of permissible values for given attribute type info
	 * from the file.
	 * @param attributeTypeInformation attribute type info.
	 * @param desiredPermissibleValues desired permissible values
	 * @return list of permissible Values
	 * @throws ParseException
	 */
	public List<PermissibleValueInterface> getPermissibleValueList(
			AttributeTypeInformationInterface attributeTypeInformation,
			Set<String> desiredPermissibleValues) throws ParseException
	{
		List<PermissibleValueInterface> permissibleValues = new ArrayList<PermissibleValueInterface>();
		if (desiredPermissibleValues != null)
		{
			for (String entryObject : desiredPermissibleValues)
			{
				PermissibleValue permissibleValueInterface = (PermissibleValue) attributeTypeInformation
						.getPermissibleValueForString(DynamicExtensionsUtility
								.getEscapedStringValue(entryObject));

				permissibleValues.add(permissibleValueInterface);
			}
		}

		return permissibleValues;
	}

	/**
	 * @param rootContainer
	 * @param associationName
	 * @return
	 */
	private AbstractContainmentControlInterface getAssociationControl(
			ContainerInterface rootContainer, Long associationContainerId)
	{
		AbstractContainmentControlInterface associationControl = null;
		if (rootContainer != null && associationContainerId != null)
		{
			for (ControlInterface controlInterface : rootContainer.getControlCollection())
			{
				if (controlInterface instanceof AbstractContainmentControlInterface)
				{
					if (associationContainerId
							.equals(((AbstractContainmentControlInterface) controlInterface)
									.getContainer().getId()))
					{
						associationControl = (AbstractContainmentControlInterface) controlInterface;
						break;
					}

					associationControl = getAssociationControl(
							((AbstractContainmentControlInterface) controlInterface).getContainer(),
							associationContainerId);
					if (associationControl != null)
					{
						break;
					}
				}
			}

		}
		return associationControl;
	}

	/**
	 * @param control
	 * @param controlType
	 * @throws DynamicExtensionsSystemException
	 */
	public void setDefaultControlsOptions(ControlInterface control, ControlEnum controlType,
			boolean isLazyPvLoading) throws DynamicExtensionsSystemException
	{
		try
		{
			switch (controlType)
			{
				case TEXT_FIELD_CONTROL :
					TextFieldInterface textField = (TextFieldInterface) control;
					textField.setIsHidden(false);
					textField.setIsPassword(false);
					textField.setIsReadOnly(false);
					textField.setIsUrl(false);
					break;
				case LIST_BOX_CONTROL :
					SelectInterface selectControl = (SelectInterface) control;
					selectControl.setIsHidden(false);
					selectControl.setIsReadOnly(false);
					break;
				case COMBO_BOX_CONTROL :
					ComboBoxInterface comboControl = (ComboBoxInterface) control;
					comboControl.setIsHidden(false);
					comboControl.setIsReadOnly(false);
					comboControl.setIsLazy(isLazyPvLoading);
					break;
				case DATE_PICKER_CONTROL :
					DatePickerInterface datePickerControl = (DatePickerInterface) control;
					datePickerControl.setIsHidden(false);
					datePickerControl.setIsReadOnly(false);
					datePickerControl.setDateValueType(null);
					break;
				case FILE_UPLOAD_CONTROL :
					FileUploadInterface fileUploadControl = (FileUploadInterface) control;
					fileUploadControl.setIsHidden(false);
					fileUploadControl.setIsReadOnly(false);
					break;
				case TEXT_AREA_CONTROL :
					TextAreaInterface textAreaControl = (TextAreaInterface) control;
					textAreaControl.setIsHidden(false);
					textAreaControl.setIsReadOnly(false);
					break;
				case RADIO_BUTTON_CONTROL :
					RadioButtonInterface radioButtonControl = (RadioButtonInterface) control;
					radioButtonControl.setIsReadOnly(false);
					radioButtonControl.setIsHidden(false);
					break;
				case CHECK_BOX_CONTROL :
					CheckBoxInterface checkboxControl = (CheckBoxInterface) control;
					checkboxControl.setIsReadOnly(false);
					checkboxControl.setIsHidden(false);
					break;
				case LABEL_CONTROL :
					break;
				case MULTISELECT_CHECKBOX_CONTROL :
					MultiSelectCheckBoxInterface multiSelectControl = (MultiSelectCheckBoxInterface) control;
					multiSelectControl.setIsHidden(false);
					multiSelectControl.setIsReadOnly(false);
					break;
				default :
					throw new DynamicExtensionsSystemException("ERROR: INCORRECT CONTROL TYPE");
			}
		}
		catch (Exception e)
		{
			throw new DynamicExtensionsSystemException("PLEASE CONTACT ADMINISTRATOR", e);
		}
	}

	public ControlInterface addOrUpdateLabelControl(EntityInterface entity,
			ContainerInterface container, String controlCaption, long lineNumber, int xPosition,
			int yPosition)
	{
		ControlInterface controlInterface = container.getControlByPosition(xPosition, yPosition);
		if (controlInterface == null || !(controlInterface instanceof LabelInterface))
		{
			controlInterface = DomainObjectFactory.getInstance().createLabelControl();
		}
		controlInterface.setControlPosition(xPosition, yPosition);
		updateContainerAndControl(container, controlInterface, null, controlCaption);
		return controlInterface;
	}

	/**
	 * @param category
	 * @param entityGroup
	 * @param sourceContainer
	 * @param targetContainer
	 * @param associationList
	 * @param noOfEntries
	 * @param instance
	 * @throws DynamicExtensionsSystemException
	 * @throws DynamicExtensionsApplicationException
	 */
	public void addChildContainers(CategoryInterface category, EntityGroupInterface entityGroup,
			ContainerInterface sourceContainer, ContainerInterface targetContainer,
			List<AssociationInterface> associationList, int noOfEntries, String instance)
			throws DynamicExtensionsSystemException, DynamicExtensionsApplicationException
	{

		if (!sourceContainer.getChildContainerCollection().contains(targetContainer))
		{
			CategoryEntityInterface sourceCategoryEntity = (CategoryEntityInterface) sourceContainer
					.getAbstractEntity();
			CategoryEntityInterface targetCategoryEntity = (CategoryEntityInterface) targetContainer
					.getAbstractEntity();

			associateCategoryEntities(sourceCategoryEntity, targetCategoryEntity,
					sourceCategoryEntity.getName() + " to " + targetCategoryEntity.getName()
							+ " category association", 1, entityGroup, associationList, instance);

			sourceContainer.getChildContainerCollection().add(targetContainer);
		}
	}

	/* (non-Javadoc)
	 * @see edu.common.dynamicextensions.util.CategoryHelperInterface#removeAllSeprators(edu.common.dynamicextensions.domaininterface.userinterface.ContainerInterface)
	 */
	public void removeAllSeprators(ContainerInterface temp)
	{
		Collection<ControlInterface> collection = temp.getAllControls();
		Iterator<ControlInterface> iterator = collection.iterator();
		while (iterator.hasNext())
		{
			ControlInterface controlInterface = iterator.next();
			if (controlInterface instanceof Label)
			{
				controlInterface.setParentContainer(null);
				temp.getControlCollection().remove(controlInterface);
			}
		}
	}

	/**
	 * This method will return CategoryAssociationControlInterface with the given Container interface.
	 * @param sourceContainerInterface Source container interface.
	 * @param targetContainerInterface Target container identifier.
	 * @return associationControlInterface association control interface which has container
	 * same as target container.
	 */
	private CategoryAssociationControlInterface getControlInterface(
			ContainerInterface sourceContainerInterface, ContainerInterface targetContainerInterface)
	{
		CategoryAssociationControlInterface associationControlInterface = null;
		List<ControlInterface> allControlsUnderContianer = sourceContainerInterface
				.getAllControls();
		for (ControlInterface controlInterfaceUnderControl : allControlsUnderContianer)
		{
			if (controlInterfaceUnderControl instanceof AbstractContainmentControlInterface)
			{
				ContainerInterface containerOfControl = ((AbstractContainmentControlInterface) controlInterfaceUnderControl)
						.getContainer();
				if (containerOfControl != null
						&& containerOfControl.equals(targetContainerInterface))
				{
					associationControlInterface = (CategoryAssociationControlInterface) controlInterfaceUnderControl;
					break;
				}
			}
		}
		return associationControlInterface;
	}

	/**
	 * This method will return the categoryFileParser.
	 * @return categoryFileParser
	 */
	public CategoryFileParser getCategoryFileParser()
	{
		return categoryFileParser;
	}

	/**
	 * This method will set the categoryFileParser.
	 * @param categoryFileParser parser
	 */
	public void setCategoryFileParser(CategoryFileParser categoryFileParser)
	{
		this.categoryFileParser = categoryFileParser;
	}

	/**
	 * This method will verify weather this category attribute is marked as populateFromXml.
	 * If the attribute is marked then its properties will be set accordingly & category is also
	 * marked as populateFromXml & its autoloadXpath collection will be modified according to the
	 * category attribute.
	 * If only related attribute is marked as populateFromXml then validation will be thrown.
	 * If Attribute is marked populateFromXml & it does not have XPath tag then validation will be thrown.
	 * @param catAttribute category attribute which is to be checked for populateFromXml tag.
	 * @param category it's underlying category.
	 * @throws DynamicExtensionsSystemException thrown if any of the above mentioned criteria's failed.
	 *
	 */
	public void updateCategoryAttributeForXmlPopulation(CategoryAttributeInterface catAttribute,
			CategoryInterface category) throws DynamicExtensionsSystemException
	{
		boolean isAutoPopulate = categoryFileParser.isPopulateFromXMLAttribute();
		catAttribute.setIsPopulateFromXml(isAutoPopulate);
		if (isAutoPopulate)
		{
			validateCategoryAttributeForXpath(catAttribute);

			if (catAttribute.getIsRelatedAttribute() && !category.getIsPopulateFromXml())
			{
				throw new DynamicExtensionsSystemException(
						ApplicationProperties.getValue(CategoryConstants.CREATE_CAT_FAILS)
								+ ApplicationProperties.getValue(CategoryConstants.LINE_NUMBER)
								+ " "
								+ categoryFileParser.getLineNumber()
								+ " "
								+ ApplicationProperties.getValue("readingFile")
								+ categoryFileParser.getRelativeFilePath()
								+ ". "
								+ ApplicationProperties
										.getValue("dyExtn.category.relatedAttribute.PopulateFromXML"));
			}
			String xpath = catAttribute.getAbstractAttribute().getTaggedValue(
					XMIConstants.TAGGED_NAME_IDENTIFYING_XPATH);
			if (xpath != null)
			{
				AutoLoadXpath autoLoadXpath = category.getAutoLoadXpath(xpath);
				if (autoLoadXpath == null)
				{
					autoLoadXpath = new AutoLoadXpath();
					autoLoadXpath.setXpath(xpath);
					category.getAutoLoadXpathCollection().add(autoLoadXpath);
				}
				if (catAttribute.getIsRelatedAttribute())
				{
					// related cat attribute marked as autoPopulate , get the concept code of the default
					//value of the attribute from underlying attribute

					autoLoadXpath.addConceptCode(DynamicExtensionsUtility.getConceptCodeForValue(
							catAttribute, catAttribute.getDefaultValue(null)));

				}
				else
				{
					//normal category attribute marked as auto-populate

					//take the autoloadxpath for the coming xpath & add the concept codes to that
					//auto load xapth object.
					autoLoadXpath.getConceptCodeCollection().addAll(
							getAssociatedConceptCodeCollection(catAttribute));

				}
			}
			category.setIsPopulateFromXml(isAutoPopulate);

		}

	}

	/**
	 * This method will validate weather the value_XPath tag is present on the underlying
	 * attribute of the catAttribute, if not present will throw exception accordingly.
	 * @param catAttribute category attribute for which to validate the Tag.
	 * @throws DynamicExtensionsSystemException if XPath tag is not present on the
	 * 		underlying catAttribute.
	 */
	private void validateCategoryAttributeForXpath(CategoryAttributeInterface catAttribute)
			throws DynamicExtensionsSystemException
	{
		String taggedValue = catAttribute.getAbstractAttribute().getTaggedValue(
				XMIConstants.TAGGED_NAME_VALUE_XPATH);
		if (taggedValue == null || "".equals(taggedValue.trim()))
		{
			throw new DynamicExtensionsSystemException(
					ApplicationProperties.getValue(CategoryConstants.CREATE_CAT_FAILS)
							+ ApplicationProperties.getValue(CategoryConstants.LINE_NUMBER) + " "
							+ categoryFileParser.getLineNumber() + " "
							+ ApplicationProperties.getValue("readingFile")
							+ categoryFileParser.getRelativeFilePath() + ". "
							+ ApplicationProperties.getValue("dyExtn.category.noXPathPresent"));
		}

	}

	/**
	 * This method will check the tag "CONCEPT_CODE_LOCATION" on the base abstract attribute of catAttribute.
	 * if value is permissible value then it will take the concept codes of permissible value or
	 * if value is attribute then will take the concept codes from the attribute.
	 * If value is none then no concept code is taken.
	 * @param catAttribute category attribute whose concept code collection is needed.
	 * @return collection of concept code found from its pv's or its attribute.
	 * @throws DynamicExtensionsSystemException exception
	 */
	private Collection<String> getAssociatedConceptCodeCollection(
			CategoryAttributeInterface catAttribute) throws DynamicExtensionsSystemException
	{
		Collection<String> conceptCodeColl = new HashSet<String>();
		String conceptLocation = catAttribute.getAbstractAttribute().getTaggedValue(
				XMIConstants.TAGGED_NAME_CONCEPT_LOCATION);
		if (XMIConstants.CONCEPT_CODE_LOC_ATTRIBUTE.equalsIgnoreCase(conceptLocation))
		{
			// get the concept code on the attribute for comparing at the message
			conceptCodeColl.addAll(DynamicExtensionsUtility.getConceptCodes(catAttribute
					.getAbstractAttribute().getSemanticPropertyCollection()));
		}
		else if (XMIConstants.CONCEPT_CODE_LOC_PV.equalsIgnoreCase(conceptLocation))
		{
			// add the concept codes on the permissible values for comparing
			Collection<PermissibleValueInterface> permissibleValueColl = catAttribute
					.getAllPermissibleValues();
			for (PermissibleValueInterface permValue : permissibleValueColl)
			{
				conceptCodeColl.addAll(DynamicExtensionsUtility.getConceptCodes(permValue
						.getSemanticPropertyCollection()));

			}
		}
		return conceptCodeColl;
	}
	
	public static void convertPVTagsToLowerCase(Map<String,String> options)
	{
		String pvType="pvType";
		String pvProcessor="pvProcessor";
		if(options.containsKey(pvType))
		{	
			String value=options.get(pvType);
			options.remove(pvType);
			options.put(DEConstants.PV_TYPE, value);
		}
		if(options.containsKey(pvProcessor))
		{
			String value=options.get(pvProcessor);
			options.remove(pvProcessor);
			options.put(DEConstants.PV_PROCESSOR, value);
		}
		return;
	}

	/**
	* @param categoryAttribute
	* @param permissibleValue
	* @param hibernateDAO
	* @throws DynamicExtensionsSystemException
	*/
	public void setDefaultValue(CategoryAttributeInterface categoryAttribute,
			PermissibleValueInterface permissibleValue, boolean isNotAttributeTypeInfo)
			throws DynamicExtensionsSystemException
	{
		if (permissibleValue != null && permissibleValue.getValueAsObject() != null)
		{
			if (categoryAttribute.getDataElementCollection() != null
					&& !categoryAttribute.getDataElementCollection().isEmpty())
			{
				if (isNotAttributeTypeInfo)
				{
					// check if present in category attribute set
					UserDefinedDEInterface userDefinedDE = (UserDefinedDEInterface) ((CategoryAttribute) categoryAttribute)
							.getDataElement(null);

					final Collection<PermissibleValueInterface> permissibleValueCollection = userDefinedDE
							.getPermissibleValueCollection();
					Map<Object, PermissibleValueInterface> pvMap = new HashMap<Object, PermissibleValueInterface>();
					for (PermissibleValueInterface pValueInterface : permissibleValueCollection)
					{
						pvMap.put(pValueInterface.getValueAsObject(), pValueInterface);
					}

					PermissibleValueInterface valueInterface = pvMap.get(permissibleValue
							.getValueAsObject());

					if (valueInterface == null)
					{
						throw new DynamicExtensionsSystemException(
								ApplicationProperties.getValue(CategoryConstants.CREATE_CAT_FAILS)
										+ ApplicationProperties
												.getValue(CategoryConstants.LINE_NUMBER)
										+ categoryFileParser.getLineNumber()
										+ ApplicationProperties
												.getValue(CategoryConstants.PV_INVALID_DEFAULT_VALUE));
					}
					else
					{
						Collection<PermissibleValueInterface> defaultPermissibleValues = new LinkedHashSet<PermissibleValueInterface>();
						defaultPermissibleValues.add(valueInterface);
						DataElementInterface dataElementInterface = ((CategoryAttribute) categoryAttribute)
								.getDataElement(null);
						((UserDefinedDEInterface) dataElementInterface)
								.setDefaultPermissibleValues(defaultPermissibleValues);
					}
				}
			}
			else
			{
				categoryAttribute.setDefaultValue(permissibleValue);
			}
		}
	}

	public static String getControlCaption(
			BaseAbstractAttributeInterface baseAbstractAttributeInterface)
	{
		String controlCaption = CategoryHelper
		.getControl(
				((ContainerInterface) ((CategoryAttributeInterface) baseAbstractAttributeInterface)
						.getCategoryEntity().getContainerCollection().iterator()
						.next()), baseAbstractAttributeInterface).getCaption();
		return controlCaption;
	}
}