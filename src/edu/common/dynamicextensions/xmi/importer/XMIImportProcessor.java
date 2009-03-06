
package edu.common.dynamicextensions.xmi.importer;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.Map.Entry;

import org.omg.uml.UmlPackage;
import org.omg.uml.foundation.core.AssociationEnd;
import org.omg.uml.foundation.core.Attribute;
import org.omg.uml.foundation.core.Generalization;
import org.omg.uml.foundation.core.TaggedValue;
import org.omg.uml.foundation.core.UmlAssociation;
import org.omg.uml.foundation.core.UmlClass;
import org.omg.uml.foundation.datatypes.Multiplicity;
import org.omg.uml.foundation.datatypes.MultiplicityRange;
import org.omg.uml.modelmanagement.Model;
import org.omg.uml.modelmanagement.ModelClass;
import org.omg.uml.modelmanagement.ModelManagementPackage;

import edu.common.dynamicextensions.bizlogic.BizLogicFactory;
import edu.common.dynamicextensions.domain.BooleanAttributeTypeInformation;
import edu.common.dynamicextensions.domain.ByteArrayAttributeTypeInformation;
import edu.common.dynamicextensions.domain.DateAttributeTypeInformation;
import edu.common.dynamicextensions.domain.DomainObjectFactory;
import edu.common.dynamicextensions.domain.DoubleAttributeTypeInformation;
import edu.common.dynamicextensions.domain.EntityGroup;
import edu.common.dynamicextensions.domain.FileAttributeTypeInformation;
import edu.common.dynamicextensions.domain.FloatAttributeTypeInformation;
import edu.common.dynamicextensions.domain.IntegerAttributeTypeInformation;
import edu.common.dynamicextensions.domain.LongAttributeTypeInformation;
import edu.common.dynamicextensions.domain.ShortAttributeTypeInformation;
import edu.common.dynamicextensions.domain.StringAttributeTypeInformation;
import edu.common.dynamicextensions.domain.userinterface.Container;
import edu.common.dynamicextensions.domain.userinterface.ContainmentAssociationControl;
import edu.common.dynamicextensions.domain.userinterface.SelectControl;
import edu.common.dynamicextensions.domaininterface.AbstractAttributeInterface;
import edu.common.dynamicextensions.domaininterface.AbstractMetadataInterface;
import edu.common.dynamicextensions.domaininterface.AssociationDisplayAttributeInterface;
import edu.common.dynamicextensions.domaininterface.AssociationInterface;
import edu.common.dynamicextensions.domaininterface.AttributeInterface;
import edu.common.dynamicextensions.domaininterface.AttributeTypeInformationInterface;
import edu.common.dynamicextensions.domaininterface.BooleanValueInterface;
import edu.common.dynamicextensions.domaininterface.EntityGroupInterface;
import edu.common.dynamicextensions.domaininterface.EntityInterface;
import edu.common.dynamicextensions.domaininterface.RoleInterface;
import edu.common.dynamicextensions.domaininterface.SemanticPropertyInterface;
import edu.common.dynamicextensions.domaininterface.TaggedValueInterface;
import edu.common.dynamicextensions.domaininterface.UserDefinedDEInterface;
import edu.common.dynamicextensions.domaininterface.userinterface.ContainerInterface;
import edu.common.dynamicextensions.domaininterface.userinterface.ControlInterface;
import edu.common.dynamicextensions.domaininterface.userinterface.FileUploadInterface;
import edu.common.dynamicextensions.domaininterface.userinterface.ListBoxInterface;
import edu.common.dynamicextensions.domaininterface.userinterface.TextFieldInterface;
import edu.common.dynamicextensions.entitymanager.EntityGroupManager;
import edu.common.dynamicextensions.entitymanager.EntityGroupManagerInterface;
import edu.common.dynamicextensions.entitymanager.EntityManagerUtil;
import edu.common.dynamicextensions.exception.DynamicExtensionsApplicationException;
import edu.common.dynamicextensions.exception.DynamicExtensionsSystemException;
import edu.common.dynamicextensions.processor.ApplyFormControlsProcessor;
import edu.common.dynamicextensions.processor.AttributeProcessor;
import edu.common.dynamicextensions.processor.ContainerProcessor;
import edu.common.dynamicextensions.processor.ControlProcessor;
import edu.common.dynamicextensions.processor.EntityProcessor;
import edu.common.dynamicextensions.processor.LoadFormControlsProcessor;
import edu.common.dynamicextensions.processor.ProcessorConstants;
import edu.common.dynamicextensions.util.DynamicExtensionsUtility;
import edu.common.dynamicextensions.util.IdGeneratorUtil;
import edu.common.dynamicextensions.util.global.DEConstants;
import edu.common.dynamicextensions.util.global.DEConstants.AssociationDirection;
import edu.common.dynamicextensions.util.global.DEConstants.AssociationType;
import edu.common.dynamicextensions.util.global.DEConstants.Cardinality;
import edu.common.dynamicextensions.xmi.XMIConfiguration;
import edu.common.dynamicextensions.xmi.XMIConstants;
import edu.common.dynamicextensions.xmi.XMIUtilities;
import edu.common.dynamicextensions.xmi.exporter.DatatypeMappings;
import edu.common.dynamicextensions.xmi.model.ContainerModel;
import edu.common.dynamicextensions.xmi.model.ControlsModel;
import edu.wustl.common.bizlogic.DefaultBizLogic;
import edu.wustl.common.exception.BizLogicException;
import edu.wustl.common.util.global.CommonServiceLocator;
import edu.wustl.common.util.global.Constants;
import edu.wustl.common.util.logger.Logger;

/**
 *
 * @author sujay_narkar
 * @author ashish_gupta
 * @author pavan_kalantri
 */

public class XMIImportProcessor
{

	public boolean isEditedXmi = false;

	public boolean isPackagePresent = false;

	private XMIConfiguration xmiConfigurationObject;

	/**
	 * Instance of Domain object factory, which will be used to create  dynamic extension's objects.
	 */
	protected static DomainObjectFactory deFactory = DomainObjectFactory.getInstance();

	/**
	 * Map with KEY : UML id of a class(coming from domain model) VALUE : dynamic extension Entity created for this UML class.
	 */
	protected Map<String, EntityInterface> umlClassIdVsEntity;

	/**
	 * Saved entity group created by this class
	 */
	private EntityGroupInterface entityGroup;

	/**
	 * Map for storing containers corresponding to entities
	 */
	protected Map<String, List<ContainerInterface>> entityNameVsContainers = new HashMap<String, List<ContainerInterface>>();
	/**
	 * List for retrieved containers corresponding to entity group.
	 */
	private Collection<ContainerInterface> retrievedContainerList = new ArrayList<ContainerInterface>();

	/**
	 * It will store the list of primary key attribute names of the entity which are belonging to the another entity 
	 */
	private Map<EntityInterface, List<String>> entityVsPrimaryKeyNameList = new HashMap<EntityInterface, List<String>>();

	private List<ContainerInterface> mainContainerList = new ArrayList<ContainerInterface>();

	private Map<AttributeInterface, Map<String, String>> attrVsMapTagValues = new HashMap<AttributeInterface, Map<String, String>>();

	private Map<EntityInterface, Map<String, String>> entityVsMapTagValues = new HashMap<EntityInterface, Map<String, String>>();

	private Map<AssociationInterface, Map<String, String>> associationVsMapTagValues = new HashMap<AssociationInterface, Map<String, String>>();

	/**
	 * @return
	 */
	public XMIConfiguration getXmiConfigurationObject()
	{
		return xmiConfigurationObject;
	}

	/**
	 * This will set the xmiConfiguration Object to given argument
	 * @param xmiConfigurationObject configuration Object to use
	 */
	public void setXmiConfigurationObject(XMIConfiguration xmiConfigurationObject)
	{
		this.xmiConfigurationObject = xmiConfigurationObject;
	}

	/**
	 * It will import the given xmi & create the DynamicExtensions Accordingly.
	 * @param umlPackage
	 * @param entityGroupName the Name of the group which is to be Created For DynamicExtensions 
	 * @param packageName name of the package which is to be imported From EA Model
	 * @param containerNames list of the names of Entities which are to be processed
	 * @param xmiConfiguration XMIConfiguration object to indicate some configuration settings
	 * @return
	 * @throws Exception
	 */
	public List<ContainerInterface> processXmi(UmlPackage umlPackage, String entityGroupName,
			String packageName, List<String> containerNames) throws Exception
	{
		List<UmlClass> umlClassColl = new ArrayList<UmlClass>();
		List<UmlAssociation> umlAssociationColl = new ArrayList<UmlAssociation>();
		List<Generalization> umlGeneralisationColl = new ArrayList<Generalization>();
		if (xmiConfigurationObject == null)
		{
			throw new DynamicExtensionsSystemException(
					"Please set the XMIConfiguration object first ");
		}

		processModel(umlPackage, umlClassColl, umlAssociationColl, umlGeneralisationColl,
				packageName);

		validate();

		List<EntityGroupInterface> entityGroupColl = retrieveEntityGroup(entityGroupName);

		if (entityGroupColl == null || entityGroupColl.isEmpty())
		{//Add
			entityGroup = DomainObjectFactory.getInstance().createEntityGroup();
			setEntityGroupName(entityGroupName);
			entityGroup.setIsSystemGenerated(xmiConfigurationObject.isEntityGroupSystemGenerated());
		}
		else
		{//Edit
			isEditedXmi = true;
			entityGroup = entityGroupColl.get(0);
		}
		//Static models of caTissue and Clinportal are system generated entity groups
		entityGroup.setIsSystemGenerated(xmiConfigurationObject.isEntityGroupSystemGenerated());

		addTaggedValue(packageName);
		int noOfClasses = umlClassColl.size();
		umlClassIdVsEntity = new HashMap<String, EntityInterface>(noOfClasses);

		//Creating entities and entity group.
		for (UmlClass umlClass : umlClassColl)
		{
			if (xmiConfigurationObject.isEntityGroupSystemGenerated()
					&& !umlClass.getName().startsWith(XMIConstants.CATISSUE_PACKAGE))
			{
					umlClass.setName(XMIConstants.CATISSUE_PACKAGE + umlClass.getName());
			}
			EntityInterface entity = entityGroup.getEntityByName(umlClass.getName());

			if (entity == null)
			{//Add
				entity = createEntity(umlClass, umlPackage);
				entity.setEntityGroup(entityGroup);
				entityGroup.addEntity(entity);
			}
			else
			{//Edit
				addAttributes(umlClass, umlPackage, entity);
			}

			populateEntityProperties(entity, umlClass, entityVsMapTagValues);

			//			For static models
			if (xmiConfigurationObject.isEntityGroupSystemGenerated() && !entity.getName().startsWith(XMIConstants.CATISSUE_PACKAGE))
			{
					entity.setName(XMIConstants.CATISSUE_PACKAGE + entity.getName());
			}
			umlClassIdVsEntity.put(umlClass.refMofId(), entity);
		}

		Map<String, List<String>> parentIdVsChildrenIds = new HashMap<String, List<String>>();

		if (!umlGeneralisationColl.isEmpty())
		{
			parentIdVsChildrenIds = getParentVsChildrenMap(umlGeneralisationColl);
		}

		if (!umlGeneralisationColl.isEmpty())
		{
			processInheritance(parentIdVsChildrenIds);
			//			markInheritedAttributes(entityGroup);
		}
		// process composite PrimaryKey including inherited primary keys also
		for (EntityInterface entity : entityGroup.getEntityCollection())
		{
			//addPrimaryKeyOfParentToChild(entity);
			processCompositeKey(entity);
			populateMultiselectAttribute(entity);
		}
		// populate entity for generating constraint properties if it has any parent set
		for (EntityInterface entity : entityGroup.getEntityCollection())
		{
			entity.populateEntityForConstraintProperties(xmiConfigurationObject
					.isAddColumnForInherianceInChild());
		}

		if (umlAssociationColl != null)
		{
			for (UmlAssociation umlAssociation : umlAssociationColl)
			{
				addAssociation(umlAssociation);
			}
		}

		//Retrieving  all containers corresponding to the given entity group.
		if (entityGroup.getId() != null)
		{
			//retrievedContainerList populated by containerCollection of each entity
			Collection<EntityInterface> entityCollection = entityGroup.getEntityCollection();
			for (EntityInterface entity : entityCollection)
			{
				retrievedContainerList.addAll(entity.getContainerCollection());
			}
		}

		for (UmlClass umlClass : umlClassColl)
		{
			EntityInterface entity = umlClassIdVsEntity.get(umlClass.refMofId());
			//In memory operation
			createContainer(entity);
			//to retrieve primary key properties of the attribute of entity
		}
		if (!umlGeneralisationColl.isEmpty())
		{//setting base container in child container.
			postProcessInheritence(parentIdVsChildrenIds);
		}
		if (!umlAssociationColl.isEmpty())
		{//Adding container for containment control
			postProcessAssociation();
		}
		//Persist container in DB
		processPersistence(containerNames);

		return mainContainerList;
	}

	
	/**
	 * It will search the primary key attribute which is in another entity and will add it to own 
	 * composite collection
	 * @param entity whose composite key is to be processed
	 * @throws DynamicExtensionsSystemException
	 */
	private void processCompositeKey(EntityInterface entity)
			throws DynamicExtensionsSystemException
	{
		List<String> primaryKeyList = entityVsPrimaryKeyNameList.get(entity);
		if (primaryKeyList != null)
		{
			for (String primaryKeyName : primaryKeyList)
			{
				StringTokenizer tokenizer = new StringTokenizer(primaryKeyName,
						XMIConstants.DOT_SEPARATOR);
				String entityName = getNextToken(tokenizer);
				EntityInterface targetEntity = entityGroup.getEntityByName(entityName);
				if (targetEntity != null)
				{
					String attributeName = getNextToken(tokenizer);
					AttributeInterface attribute = targetEntity
							.getEntityAttributeByName(attributeName);
					if (attribute != null)
					{
						entity.addPrimaryKeyAttribute(attribute);
					}
				}
				else
				{
					throw new DynamicExtensionsSystemException("Given entity not found");
				}
			}
		}

	}

	/**
	 * It will populate the UIproperties semantic properties and primary key properties
	 * @param entity
	 * @param umlClass
	 * @param entityVsMapTagValues2
	 * @throws DynamicExtensionsApplicationException
	 * @throws DynamicExtensionsSystemException 
	 */
	private void populateEntityProperties(EntityInterface entity, UmlClass umlClass,
			Map<EntityInterface, Map<String, String>> entityVsMapTagValues)
			throws DynamicExtensionsApplicationException, DynamicExtensionsSystemException
	{
		populateEntityUIProperties(entity, umlClass.getTaggedValue());
		addSemanticPropertyForEntities(entity, entityVsMapTagValues.get(entity));
		processPrimaryKey(entity, entityVsMapTagValues.get(entity));
	}

	/**
	 * It will generate constraint properties for the association which is created due to the
	 * multiselect attribute in the entity
	 * @param entity in which to search for multiselct attributes associaion
	 * @throws DynamicExtensionsSystemException
	 */
	private void populateMultiselectAttribute(EntityInterface entity)
			throws DynamicExtensionsSystemException
	{
		Collection<AssociationInterface> associationColl = entity.getAllAssociations();
		for (AssociationInterface association : associationColl)
		{
			Map<String, String> taggedValueMap = associationVsMapTagValues.get(association);
			if (isMultiselectTagValue(taggedValueMap))
			{
				association.populateAssociationForConstraintProperties();
			}
		}
	}

	/**
	 * It will check the tagValue & depending on it will make the corresponding
	 * attribute as primary key if it is in the same entity and if it is in different entity 
	 * it will save its name in the list which is stored in the entityVsPrimaryKeyNameList map
	 * which will be processed when processing the composite key for the entity
	 * @param entity whose primary key is to be processed 
	 * @param taggedValueMap map of tagKey and tagValue of the entity
	 * @throws DynamicExtensionsApplicationException
	 * @throws DynamicExtensionsSystemException 
	 */
	private void processPrimaryKey(EntityInterface entity, Map<String, String> taggedValueMap)
			throws DynamicExtensionsSystemException, DynamicExtensionsApplicationException
	{
		String primaryKey = null;
		AttributeInterface primaryAttribute = null;
		Collection<AttributeInterface> primKeyAttrColl = entity.getPrimaryKeyAttributeCollection();
		resetPrimaryKeyAttributes(primKeyAttrColl);
		primKeyAttrColl.clear();
		primaryKey = (String) taggedValueMap.get(XMIConstants.TAGGED_VALUE_PRIMARYKEY);
		List<String> primaryKeyAttributeNameList = new ArrayList<String>();
		if (primaryKey != null && !"".equals(primaryKey))
		{
			//removeIdAttributeFromEntity(entity);
			String primaryAttributeName[] = primaryKey.split(XMIConstants.COMMA);
			for (String attributeName : primaryAttributeName)
			{
				StringTokenizer tokenizer = new StringTokenizer(attributeName,
						XMIConstants.DOT_SEPARATOR);
				String entityName = getNextToken(tokenizer);
				if (entity.getName().equals(entityName))
				{
					String attribute = getNextToken(tokenizer);
					primaryAttribute = entity.getEntityAttributeByName(attribute);
					if (primaryAttribute == null)
					{
						throw new DynamicExtensionsSystemException("primary key attribute "
								+ attributeName + "not found in entity " + entityName);
					}
					else
					{
						XMIImportValidator.validateDataTypeForPrimaryKey(primaryAttribute);
						primaryAttribute.setIsPrimaryKey(true);
						entity.addPrimaryKeyAttribute(primaryAttribute);
					}
				}
				else
				{
					primaryKeyAttributeNameList.add(attributeName);
				}
			}
		}
		//This is modification because cider does not requires Id attribute at all they req. only metadata
		else if (xmiConfigurationObject.isAddIdAttribute())
		{
			EntityManagerUtil.addIdAttribute(entity);
			entity
					.addPrimaryKeyAttribute(entity
							.getAttributeByName(XMIConstants.ID_ATTRIBUTE_NAME));
		}
		entityVsPrimaryKeyNameList.put(entity, primaryKeyAttributeNameList);
	}

	/**
	 * It will retrieve the next token from the given tokenizer if present else
	 * will return empty ("") string
	 * @param tokenizer from which to retrieve next token
	 * @return next token
	 */
	private String getNextToken(StringTokenizer tokenizer)
	{
		String token = "";
		if (tokenizer.hasMoreTokens())
		{
			token = tokenizer.nextToken();
		}
		return token;
	}

	/**
	 * It will set the isPrimaryKey to false of the each attribute in the given collection
	 * @param primKeyAttrColl
	 */
	private void resetPrimaryKeyAttributes(Collection<AttributeInterface> primKeyAttrColl)
	{
		for (AttributeInterface attribute : primKeyAttrColl)
		{
			if (!attribute.getName().equals(XMIConstants.ID_ATTRIBUTE_NAME))
			{
				attribute.setIsPrimaryKey(false);
				attribute.setIsNullable(true);
			}

		}

	}

	/**
	 * @throws Exception
	 */
	private void validate() throws Exception
	{
		if (!isPackagePresent)
		{
			throw new Exception("Specified package is not present in the XMI.");
		}
	}

	/**
	 * @param entityGroupName
	 * @return
	 * @throws BizLogicException
	 */
	private List<EntityGroupInterface> retrieveEntityGroup(String entityGroupName)
			throws BizLogicException
	{
		List<EntityGroupInterface> entityGroupColl = null;
		DefaultBizLogic defaultBizLogic = BizLogicFactory.getDefaultBizLogic();

		entityGroupColl = defaultBizLogic.retrieve(EntityGroup.class.getName(),
				edu.common.dynamicextensions.ui.util.Constants.NAME, entityGroupName);

		return entityGroupColl;
	}

	/**
	 * @param entityGroupName
	 */
	private void setEntityGroupName(String entityGroupName)
	{
		entityGroup.setShortName(entityGroupName);
		entityGroup.setName(entityGroupName);
		entityGroup.setLongName(entityGroupName);
		entityGroup.setDescription(entityGroupName);
	}

	/**
	 * @param packageName
	 */
	private void addTaggedValue(String packageName)
	{
		TaggedValueInterface taggedValue = DomainObjectFactory.getInstance().createTaggedValue();
		taggedValue.setKey("PackageName");
		taggedValue.setValue(packageName);

		Collection<TaggedValueInterface> tvColl = entityGroup.getTaggedValueCollection();
		if (tvColl == null)
		{
			tvColl = new HashSet<TaggedValueInterface>();
		}
		tvColl.add(taggedValue);
		entityGroup.setTaggedValueCollection(tvColl);
	}

	/**
	 * This method checks the entity name if it matches with the data type names like Integer, String etc.
	 * @param umlClassName
	 * @return
	 */
	private boolean checkEntityWithDataTypeEntities(String umlClassName)
	{
		DatatypeMappings dataType = DatatypeMappings.get(umlClassName);

		if (dataType != null
				|| umlClassName
						.equalsIgnoreCase(edu.common.dynamicextensions.ui.util.Constants.COLLECTION)
				|| umlClassName
						.equalsIgnoreCase(edu.common.dynamicextensions.ui.util.Constants.DATE)
				|| umlClassName
						.equalsIgnoreCase(edu.common.dynamicextensions.ui.util.Constants.TIME))
		{
			return true;
		}
		return false;
	}

	/**
	 * @param umlPackage
	 * @param umlClassColl
	 * @param umlAssociationColl
	 * @param umlGeneralisationColl
	 */
	private void processModel(UmlPackage umlPackage, List<UmlClass> umlClassColl,
			List<UmlAssociation> umlAssociationColl, List<Generalization> umlGeneralisationColl,
			String packageName)
	{
		ModelManagementPackage modelManagementPackage = umlPackage.getModelManagement();
		ModelClass modelClass = modelManagementPackage.getModel();
		Collection<Model> modelColl = modelClass.refAllOfClass();

		for (Model model : modelColl)
		{
			Collection ownedElementColl = model.getOwnedElement();
			Logger.out.info("MODEL OWNED ELEMENT SIZE: " + ownedElementColl.size());
			Iterator iter = ownedElementColl.iterator();

			StringTokenizer tokens = new StringTokenizer(packageName, XMIConstants.DOT_SEPARATOR);
			String token = "";
			if (tokens.hasMoreTokens())
			{
				token = tokens.nextToken();
			}
			if (token.trim().equalsIgnoreCase(XMIConstants.DEFAULT_PACKAGE))
			{
				processPackage(model, umlClassColl, umlAssociationColl, umlGeneralisationColl);
			}
			else
			{
				StringTokenizer initializedTokens = new StringTokenizer(packageName,
						XMIConstants.DOT_SEPARATOR);
				token = "";
				if (initializedTokens.hasMoreTokens())
				{
					token = initializedTokens.nextToken();
				}
				while (iter.hasNext())
				{

					Object obj = iter.next();
					if (obj instanceof org.omg.uml.modelmanagement.UmlPackage)
					{
						org.omg.uml.modelmanagement.UmlPackage umlPackageObj = (org.omg.uml.modelmanagement.UmlPackage) obj;
						if (token.equalsIgnoreCase(umlPackageObj.getName()))
						{
							processSelectedPackage(umlPackageObj, initializedTokens, umlClassColl,
									umlAssociationColl, umlGeneralisationColl);

							//					processPackage(umlPackageObj, umlClassColl, umlAssociationColl,
							//					umlGeneralisationColl , packageName);
						}
					}
				}
			}

		}
	}

	/**
	 * @param parentPkg
	 * @param tokens
	 * @param umlClassColl
	 * @param umlAssociationColl
	 * @param umlGeneralisationColl
	 */
	private void processSelectedPackage(org.omg.uml.modelmanagement.UmlPackage parentPkg,
			StringTokenizer tokens, List<UmlClass> umlClassColl,
			List<UmlAssociation> umlAssociationColl, List<Generalization> umlGeneralisationColl)
	{
		String token = "";
		int temp = 0;
		if (tokens.hasMoreTokens())
		{
			token = tokens.nextToken();
		}

		//If no package is present in the XMI take package name as "Default"
		//		if(token.trim().equalsIgnoreCase(XMIConstants.DEFAULT_PACKAGE))
		//		{
		//			processPackage(parentPkg,umlClassColl,umlAssociationColl,umlGeneralisationColl);
		//		}
		//		else
		for (Iterator i = parentPkg.getOwnedElement().iterator(); i.hasNext();)
		{
			Object object = i.next();//
			if (object instanceof org.omg.uml.modelmanagement.UmlPackage)
			{
				org.omg.uml.modelmanagement.UmlPackage subPkg = (org.omg.uml.modelmanagement.UmlPackage) object;
				if (token.equalsIgnoreCase(subPkg.getName()))
				{
					processSelectedPackage(subPkg, tokens, umlClassColl, umlAssociationColl,
							umlGeneralisationColl);
					temp++;
				}
			}
		}
		if (temp == 0)
		{//if package name is present, import only that package.
			processPackage(parentPkg, umlClassColl, umlAssociationColl, umlGeneralisationColl);
		}

	}

	/**
	 * @param parentPkg
	 * @param pkgName
	 * @return
	 */
	private void processPackage(org.omg.uml.modelmanagement.UmlPackage parentPkg,
			List<UmlClass> umlClasses, List<UmlAssociation> associations,
			List<Generalization> generalizations)
	{
		isPackagePresent = true;
		for (Iterator i = parentPkg.getOwnedElement().iterator(); i.hasNext();)
		{
			Object object = i.next();
			/*	if (o instanceof org.omg.uml.modelmanagement.UmlPackage && !(packageName.equals(parentPkg.getName())))
			 {
			 org.omg.uml.modelmanagement.UmlPackage subPkg = (org.omg.uml.modelmanagement.UmlPackage) o;
			 processPackage(subPkg, umlClasses, associations, generalizations,packageName);
			 }
			 else*/

			if (object instanceof UmlAssociation)
			{
				associations.add((UmlAssociation) object);
			}
			else if (object instanceof Generalization)
			{
				generalizations.add((Generalization) object);
			}
			else if (object instanceof UmlClass)
			{
				UmlClass umlClass = (UmlClass) object;
				boolean isEntityADatatype = checkEntityWithDataTypeEntities(umlClass.getName());
				if (isEntityADatatype)
				{//Skipping classes having datatype names eg Integer,String etc.
					continue;
				}

				Collection<Generalization> generalizationColl = umlClass.getGeneralization();
				if (generalizationColl != null && !generalizationColl.isEmpty())
				{
					generalizations.addAll(generalizationColl);
				}
				umlClasses.add(umlClass);
			}
		}
	}

	/**
	 * Creates a Dynamic Exension Entity from given UMLClass.<br>
	 * It also assigns all the attributes of the UMLClass to the Entity as the
	 * Dynamic Extension Primitive Attributes.Then stores the input UML class,
	 * adds the Dynamic Extension's PrimitiveAttributes to the Collection.
	 * Properties which are copied from UMLAttribute to DE Attribute are
	 * name,description,semanticMetadata,permissible values
	 * @param umlClass
	 *            The UMLClass from which to form the Dynamic Extension Entity
	 * @param umlPackage 
	 * @return the unsaved entity for given UML class
	 * @throws DynamicExtensionsSystemException 
	 */
	private EntityInterface createEntity(UmlClass umlClass, UmlPackage umlPackage)
			throws DynamicExtensionsSystemException
	{
		String name = umlClass.getName();
		//EntityInterface entity = deFactory.createEntity();
		// calling createEntity of EntityProcessor as it generates id attibute for that entity
		EntityProcessor entityProcessor = EntityProcessor.getInstance();
		EntityInterface entity = entityProcessor.createEntity();
		entity.setName(name);
		entity.setDescription(entityGroup.getName() + "--" + umlClass.getName());
		entity.setAbstract(umlClass.isAbstract());
		addAttributes(umlClass, umlPackage, entity);

		
		//		setSemanticMetadata(entity, umlClass.getSemanticMetadata());
		return entity;
	}

	/**
	 * @param attrColl
	 * @param entity
	 * @throws DynamicExtensionsSystemException 
	 */
	private AttributeInterface createAttribute(Attribute umlAttribute, EntityInterface entity)
			throws DynamicExtensionsSystemException
	{
		//Not showing id attribute on UI if Id attribute is to be added by DE which is specified in xmiConfiguration Object
		AttributeInterface originalAttribute = null;
		if (((umlAttribute.getName().equalsIgnoreCase(DEConstants.OBJ_IDENTIFIER) || umlAttribute.getName()
				.equalsIgnoreCase(Constants.IDENTIFIER)))&& xmiConfigurationObject.isAddIdAttribute())
		{
			//If id attribute is system generated then dont create attribute for user given Id attribute
				return null;
		}
		DataType dataType = DataType.get(umlAttribute.getType().getName());
		if (dataType != null)
		{//Temporary solution for unsupported datatypes. Not adding attributes having unsupported datatypes.

			originalAttribute = entity.getAttributeByName(umlAttribute.getName());
			if (originalAttribute == null)
			{//New attribute has been created
				AttributeInterface attribute = dataType.createAttribute(umlAttribute);
				if (attribute != null)
				{ // to bypass attributes of invalid datatypes
					attribute.setName(umlAttribute.getName());
					//					attribute.setDescription(umlAttribute.getTaggedValue().getDescription());
					//					setSemanticMetadata(attribute, umlAttribute.getSemanticMetadata());
					Collection<TaggedValue> taggedValueColl = umlAttribute.getTaggedValue();
					populateAttributeUIProperties(attribute, taggedValueColl);
					Map<String, String> taggedValueMap = attrVsMapTagValues.get(attribute);
					addSemanticPropertyForAttributes(attribute, taggedValueMap);

					if (isMultiselectTagValue(taggedValueMap))
					{
						addMultiselectAttribute(attribute, umlAttribute, taggedValueMap, entity);
					}
					else
					{
						entity.addAttribute(attribute);
					}
				}
			}
			else
			{//Attribute has been edited
				Collection<TaggedValue> taggedValueColl = umlAttribute.getTaggedValue();
				populateAttributeUIProperties(originalAttribute, taggedValueColl);
				Map<String, String> taggedValueMap = attrVsMapTagValues.get(originalAttribute);
				if (isMultiselectTagValue(taggedValueMap)
						&& !entity.isMultiselectAttributePresent(umlAttribute.getName()))
				{
					AttributeInterface attribute = dataType.createAttribute(umlAttribute);
					addMultiselectAttribute(attribute, umlAttribute, taggedValueMap, entity);
				}
				addSemanticPropertyForAttributes(originalAttribute, attrVsMapTagValues
						.get(originalAttribute));

			}

		}
		return originalAttribute;
	}

	/**
	 * This method will add the attributes to the entity for the given Umlclass from XMI.
	 * boolean includeInherited - Specifies whether inherited attributes should be included or not.
	 * @param klass
	 * @param includeInherited
	 * @param umlPackage 
	 * @param entity in which to add the attributes
	 * @throws DynamicExtensionsSystemException 
	 */
	public void addAttributes(UmlClass klass, org.omg.uml.UmlPackage umlPackage,
			EntityInterface entity) throws DynamicExtensionsSystemException
	{
		Collection atts = new ArrayList();
		for (Iterator i = klass.getFeature().iterator(); i.hasNext();)
		{
			Object object = i.next();
			if (object instanceof Attribute)
			{
				atts.add(object);
				Attribute att = (Attribute) object;
				createAttribute(att, entity);
			}
		}
		if (xmiConfigurationObject.isAddInheritedAttribute())
		{
			Map attsMap = new HashMap();
			UmlClass superClass = XMIUtilities.getSuperClass(klass);
			AttributeInterface attribute;
			while (superClass != null)
			{
				for (Iterator i = superClass.getFeature().iterator(); i.hasNext();)
				{
					Object object = i.next();
					if (object instanceof Attribute)
					{
						Attribute att = (Attribute) object;
						if (attsMap.get(att.getName()) == null)
						{
							attsMap.put(att.getName(), att);
							attribute = createAttribute(att, entity);
							if (attribute != null)
							{
								DynamicExtensionsUtility.addInheritedTaggedValue(attribute);
							}
						}
					}
				}
				superClass = XMIUtilities.getSuperClass(superClass);
			}

		}

	}

	/**
	 * addMultiselectAttribute.
	 * @param attribute
	 * @param umlAttribute
	 * @throws DynamicExtensionsSystemException 
	 */
	private void addMultiselectAttribute(AttributeInterface attribute, Attribute umlAttribute,
			Map<String, String> taggedValueMap, EntityInterface entity)
			throws DynamicExtensionsSystemException
	{
		DomainObjectFactory factory = DomainObjectFactory.getInstance();
		AssociationInterface association = createAssociation();
		association.setIsCollection(Boolean.TRUE);

		EntityInterface targetEntity = factory.createEntity();
		EntityManagerUtil.addIdAttribute(targetEntity);
		targetEntity.setName(DEConstants.COLLECTIONATTRIBUTECLASS
				+ umlAttribute.getName()
				+ IdGeneratorUtil.getInstance().getNextUniqeId());
		attribute.setName(DEConstants.COLLECTIONATTRIBUTE
				+ IdGeneratorUtil.getInstance().getNextUniqeId());
		targetEntity.addAbstractAttribute(attribute);
		entityGroup.addEntity(targetEntity);
		targetEntity.setEntityGroup(entityGroup);

		entityVsPrimaryKeyNameList.put(targetEntity, new ArrayList<String>());

		if ((targetEntity != null) && (association != null))
		{
			association.setTargetEntity(targetEntity);
			association.setAssociationDirection(AssociationDirection.SRC_DESTINATION);
			association.setName(umlAttribute.getName());
			association.setSourceRole(EntityManagerUtil.getRole(
					AssociationType.ASSOCIATION,
					DEConstants.COLLECTIONATTRIBUTEROLE + entity.getName(),
					Cardinality.ONE, Cardinality.ONE));
			association.setTargetRole(EntityManagerUtil.getRole(
					AssociationType.ASSOCIATION,
					DEConstants.COLLECTIONATTRIBUTEROLE + targetEntity.getName(),
					Cardinality.ONE, Cardinality.MANY));
		}
		entity.addAbstractAttribute(association);
		//association.populateAssociationForConstraintProperties();
		Map<String, String> valueMap = new HashMap<String, String>();
		valueMap.put(XMIConstants.TAGGED_VALUE_MULTISELECT, getMultiselectTagValue(taggedValueMap));
		taggedValueMap.remove(XMIConstants.TAGGED_VALUE_MULTISELECT);
		associationVsMapTagValues.put(association, valueMap);

	}

	/**
	 * @param attribute
	 * @param taggedValueColl
	 */
	private void populateAttributeUIProperties(AttributeInterface attribute,
			Collection<TaggedValue> taggedValueColl)
	{
		Map<String, String> tagNameVsTagValue = populateTagValueMap(taggedValueColl, attribute);
		attrVsMapTagValues.put(attribute, tagNameVsTagValue);
	}

	/**
	 * @param entity
	 * @param taggedValueColl
	 */
	private void populateEntityUIProperties(EntityInterface entity,
			Collection<TaggedValue> taggedValueColl)
	{
		Map<String, String> tagNameVsTagValue = populateTagValueMap(taggedValueColl, entity);
		entityVsMapTagValues.put(entity, tagNameVsTagValue);
	}

	/**
	 * @param association
	 * @param taggedValueColl
	 */
	private void populateAssociationUIProperties(AssociationInterface association,
			Collection<TaggedValue> taggedValueColl)
	{
		Map<String, String> tagNameVsTagValue = populateTagValueMap(taggedValueColl, association);
		associationVsMapTagValues.put(association, tagNameVsTagValue);
	}

	/**
	 * @param taggedValueColl
	 * @return
	 */
	private Map<String, String> populateTagValueMap(Collection<TaggedValue> taggedValueColl,
			AbstractMetadataInterface abstrMetaDataObj)
	{
		Map<String, String> tagNameVsTagValue = new HashMap<String, String>();
		String tagName;
		DomainObjectFactory factory = DomainObjectFactory.getInstance();
		TaggedValueInterface tag;
		abstrMetaDataObj.removeAllTaggedValues();
		for (TaggedValue taggedValue : taggedValueColl)
		{
			if (taggedValue.getType() != null)
			{
				Collection<String> dataValueColl = taggedValue.getDataValue();
				tagName = taggedValue.getType().getName();
				for (String value : dataValueColl)
				{
					if (tagName.startsWith(XMIConstants.TAGGED_NAME_PREFIX))
					{
						tagName = tagName.replaceFirst(XMIConstants.TAGGED_NAME_PREFIX, "");
						tag = factory.createTaggedValue();
						tag.setKey(tagName);
						tag.setValue(value);
						abstrMetaDataObj.addTaggedValue(tag);
					}
					tagNameVsTagValue.put(tagName, value);
				}

			}
		}

		return tagNameVsTagValue;
	}

	/**
	 * @param entityInterface
	 * @param taggedValueMap
	 */
	private void addSemanticPropertyForEntities(EntityInterface entityInterface,
			Map<String, String> taggedValueMap)
	{
		Collection<SemanticPropertyInterface> semanticPropertyColl = new HashSet<SemanticPropertyInterface>();
		//		Concept codes
		String conceptCode = taggedValueMap
				.get(XMIConstants.TAGGED_VALUE_OBJECT_CLASS_CONCEPT_CODE);
		if (conceptCode != null)
		{
			String conceptDefinition = taggedValueMap
					.get(XMIConstants.TAGGED_VALUE_OBJECT_CLASS_CONCEPT_DEFINITION);
			String term = taggedValueMap
					.get(XMIConstants.TAGGED_VALUE_OBJECT_CLASS_CONCEPT_DEFINITION_SOURCE);
			String thesarausName = taggedValueMap
					.get(XMIConstants.TAGGED_VALUE_OBJECT_CLASS_CONCEPT_PREFERRED_NAME);
			SemanticPropertyInterface semanticPropertyInterface = getSemanticProperty(conceptCode,
					conceptDefinition, term, thesarausName, 0);
			semanticPropertyColl.add(semanticPropertyInterface);

			Set<String> tagNames = taggedValueMap.keySet();
			for (String tagName : tagNames)
			{
				if (tagName
						.startsWith(XMIConstants.TAGGED_VALUE_OBJECT_CLASS_QUALIFIER_CONCEPT_CODE))
				{
					int beginIndex = XMIConstants.TAGGED_VALUE_OBJECT_CLASS_QUALIFIER_CONCEPT_CODE
							.length();
					String qualifierNumber = tagName.substring(beginIndex);

					conceptCode = taggedValueMap
							.get(XMIConstants.TAGGED_VALUE_OBJECT_CLASS_QUALIFIER_CONCEPT_CODE
									+ qualifierNumber);
					conceptDefinition = taggedValueMap
							.get(XMIConstants.TAGGED_VALUE_OBJECT_CLASS_QUALIFIER_CONCEPT_DEFINITION
									+ qualifierNumber);
					term = taggedValueMap
							.get(XMIConstants.TAGGED_VALUE_OBJECT_CLASS_QUALIFIER_CONCEPT_DEFINITION_SOURCE
									+ qualifierNumber);
					thesarausName = taggedValueMap
							.get(XMIConstants.TAGGED_VALUE_OBJECT_CLASS_QUALIFIER_CONCEPT_PREFERRED_NAME
									+ qualifierNumber);

					semanticPropertyInterface = getSemanticProperty(conceptCode, conceptDefinition,
							term, thesarausName, Integer.parseInt(qualifierNumber));
					semanticPropertyColl.add(semanticPropertyInterface);
				}
			}
		}

		entityInterface.setSemanticPropertyCollection(semanticPropertyColl);
	}

	/**
	 * @param conceptCode
	 * @param conceptDefinition
	 * @param term
	 * @param thesaurasName
	 * @param seqNo
	 * @return
	 */
	private SemanticPropertyInterface getSemanticProperty(String conceptCode,
			String conceptDefinition, String term, String thesaurasName, int seqNo)
	{
		SemanticPropertyInterface semanticProperty = DomainObjectFactory.getInstance()
				.createSemanticProperty();
		semanticProperty.setConceptCode(conceptCode);
		semanticProperty.setConceptDefinition(conceptDefinition);
		semanticProperty.setTerm(term);
		semanticProperty.setThesaurasName(thesaurasName);
		semanticProperty.setSequenceNumber(seqNo);

		return semanticProperty;
	}

	/**
	 * @param abstractMetadataInterface
	 * @param taggedValueMap
	 * @param tag
	 */
	private void addSemanticPropertyForAttributes(AttributeInterface attributeInterface,
			Map<String, String> taggedValueMap)
	{
		Collection<SemanticPropertyInterface> semanticPropertyColl = new HashSet<SemanticPropertyInterface>();
		//		Concept codes
		String conceptCode = taggedValueMap.get(XMIConstants.TAGGED_VALUE_PROPERTY_CONCEPT_CODE);
		if (conceptCode != null)
		{
			String conceptDefinition = taggedValueMap
					.get(XMIConstants.TAGGED_VALUE_PROPERTY_CONCEPT_DEFINITION);
			String term = taggedValueMap
					.get(XMIConstants.TAGGED_VALUE_PROPERTY_CONCEPT_DEFINITION_SOURCE);
			String thesarausName = taggedValueMap
					.get(XMIConstants.TAGGED_VALUE_PROPERTY_CONCEPT_PREFERRED_NAME);
			SemanticPropertyInterface semanticPropertyInterface = getSemanticProperty(conceptCode,
					conceptDefinition, term, thesarausName, 0);
			semanticPropertyColl.add(semanticPropertyInterface);

			Set<String> tagNames = taggedValueMap.keySet();
			for (String tagName : tagNames)
			{
				if (tagName.startsWith(XMIConstants.TAGGED_VALUE_PROPERTY_QUALIFIER_CONCEPT_CODE))
				{
					int beginIndex = XMIConstants.TAGGED_VALUE_PROPERTY_QUALIFIER_CONCEPT_CODE
							.length();
					String qualifierNumber = tagName.substring(beginIndex);

					conceptCode = taggedValueMap
							.get(XMIConstants.TAGGED_VALUE_PROPERTY_QUALIFIER_CONCEPT_CODE
									+ qualifierNumber);
					conceptDefinition = taggedValueMap
							.get(XMIConstants.TAGGED_VALUE_PROPERTY_QUALIFIER_CONCEPT_DEFINITION
									+ qualifierNumber);
					term = taggedValueMap
							.get(XMIConstants.TAGGED_VALUE_PROPERTY_QUALIFIER_CONCEPT_DEFINITION_SOURCE
									+ qualifierNumber);
					thesarausName = taggedValueMap
							.get(XMIConstants.TAGGED_VALUE_PROPERTY_QUALIFIER_CONCEPT_PREFERRED_NAME
									+ qualifierNumber);

					semanticPropertyInterface = getSemanticProperty(conceptCode, conceptDefinition,
							term, thesarausName, Integer.parseInt(qualifierNumber));
					semanticPropertyColl.add(semanticPropertyInterface);
				}
			}
		}

		attributeInterface.setSemanticPropertyCollection(semanticPropertyColl);
	}

	/**
	 * Gives a map having parent child information.
	 * @return Map with key as UML-id of parent class and value as list of UML-id of all children classes.
	 */
	private Map<String, List<String>> getParentVsChildrenMap(
			List<Generalization> umlGeneralisationColl)
	{
		if (umlGeneralisationColl != null)
		{
			HashMap<String, List<String>> parentIdVsChildrenIds = new HashMap<String, List<String>>(
					umlGeneralisationColl.size());
			for (Generalization umlGeneralization : umlGeneralisationColl)
			{
				String childClass = umlGeneralization.getChild().refMofId();
				String parentClass = umlGeneralization.getParent().refMofId();
				List<String> children = parentIdVsChildrenIds.get(parentClass);
				if (children == null)
				{
					children = new ArrayList<String>();
					parentIdVsChildrenIds.put(parentClass, children);
				}
				children.add(childClass);
			}
			return parentIdVsChildrenIds;
		}

		return new HashMap<String, List<String>>(0);
	}

	/**
	 * Converts the UML association to dynamic Extension Association.Adds it to the entity group.
	 * It replicates this association in all children of source and all children of target class.
	 * It taggs replicated association to identify them later on and mark them inherited.
	 * Also a back pointer is added to replicated association go get original association.
	 * @param umlAssociation umlAssociation to process
	 * @throws DynamicExtensionsSystemException 
	 */
	private void addAssociation(UmlAssociation umlAssociation)
			throws DynamicExtensionsSystemException
	{
		List<AssociationEnd> associationEnds = umlAssociation.getConnection();

		AssociationEnd sourceAssociationEnd = null;
		AssociationEnd targetAssociationEnd = null;

		Collection<TaggedValue> taggedValueColl = umlAssociation.getTaggedValue();
		String direction = getTaggedValue(taggedValueColl, XMIConstants.TAGGED_NAME_ASSOC_DIRECTION);

		int nullAssoEnd = 0;
		for (AssociationEnd assoEnd : associationEnds)
		{
			if (assoEnd.getName() == null)
			{
				nullAssoEnd++;
			}
		}

		if (nullAssoEnd > 0 && nullAssoEnd < 2)
		{//Only 1 name is present hence unidirectional
			for (AssociationEnd assoEnd : associationEnds)
			{
				if (assoEnd.getName() == null)
				{
					sourceAssociationEnd = assoEnd;
				}
				else
				{
					targetAssociationEnd = assoEnd;
				}
			}
			if ("".equals(direction)
					|| direction.equalsIgnoreCase(XMIConstants.TAGGED_VALUE_ASSOC_DEST_SRC)
					|| direction.equalsIgnoreCase(XMIConstants.TAGGED_VALUE_ASSOC_SRC_DEST)
					|| direction
							.equalsIgnoreCase(XMIConstants.TAGGED_VALUE_CONTAINMENT_UNSPECIFIED))
			{
				direction = DEConstants.AssociationDirection.SRC_DESTINATION.toString();
			}
		}
		else
		{//bidirectional
			sourceAssociationEnd = associationEnds.get(0);
			targetAssociationEnd = associationEnds.get(1);
			if ("".equals(direction)
					|| direction.equalsIgnoreCase(XMIConstants.TAGGED_VALUE_ASSOC_BIDIRECTIONAL))
			{
				direction = DEConstants.AssociationDirection.BI_DIRECTIONAL.toString();
			}
			else
			{
				direction = DEConstants.AssociationDirection.SRC_DESTINATION.toString();
			}
		}
		//		getAssociationEnds(sourceAssociationEnd,targetAssociationEnd,associationEnds,direction);

		String sourceAssoTypeTV = getTaggedValue(sourceAssociationEnd.getTaggedValue(),
				XMIConstants.TAGGED_VALUE_CONTAINMENT);
		String destinationAssoTypeTV = getTaggedValue(targetAssociationEnd.getTaggedValue(),
				XMIConstants.TAGGED_VALUE_CONTAINMENT);

		String srcId = sourceAssociationEnd.getParticipant().refMofId();
		EntityInterface srcEntity = umlClassIdVsEntity.get(srcId);

		Multiplicity srcMultiplicity = sourceAssociationEnd.getMultiplicity();
		String sourceRoleName = sourceAssociationEnd.getName();
		RoleInterface sourceRole = getRole(srcMultiplicity, sourceRoleName, sourceAssoTypeTV);

		String tgtId = targetAssociationEnd.getParticipant().refMofId();
		EntityInterface tgtEntity = umlClassIdVsEntity.get(tgtId);

		Multiplicity tgtMultiplicity = targetAssociationEnd.getMultiplicity();
		String tgtRoleName = targetAssociationEnd.getName();
		RoleInterface targetRole = getRole(tgtMultiplicity, tgtRoleName, destinationAssoTypeTV);

		AssociationInterface association = null;
		Collection<AssociationInterface> existingAssociationColl = srcEntity
				.getAssociationCollection();
		if (existingAssociationColl != null && !existingAssociationColl.isEmpty())
		{//EDIT Case
			association = isAssociationPresent(umlAssociation.getName(), existingAssociationColl,
					srcEntity.getName(), tgtEntity.getName(), direction, sourceRole, targetRole);
		}

		//Adding association to entity
		if (association == null)
		{//Make new Association
			association = getAssociation(srcEntity, umlAssociation.getName());
		}

		association.setSourceRole(sourceRole);
		association.setTargetEntity(tgtEntity);
		association.setTargetRole(targetRole);
		association.populateAssociationForConstraintProperties();
		if (direction.equalsIgnoreCase(DEConstants.AssociationDirection.BI_DIRECTIONAL.toString()))
		{
			association.setAssociationDirection(DEConstants.AssociationDirection.BI_DIRECTIONAL);
		}
		else
		{
			association.setAssociationDirection(DEConstants.AssociationDirection.SRC_DESTINATION);
		}

		populateAssociationUIProperties(association, taggedValueColl);
	}

	/**
	 * Logic:
	 * 1. If association name is present and matches with an existing association name, edit that association
	 * 2. If association name is not present, check for other matching parameters like  source entity name, target entity name,
	 * 	  direction, source role and target role.
	 * 		a. If any one parameter does not match, make a new association
	 * 		b. If all parameters match, association has not been edited.
	 *
	 * @param umlAssociationName
	 * @param existingAssociationColl
	 * @param srcEntityName
	 * @param tgtEntityName
	 * @param direction
	 * @param sourceRole
	 * @param targetRole
	 * @return
	 */
	private AssociationInterface isAssociationPresent(String umlAssociationName,
			Collection<AssociationInterface> existingAssociationColl, String srcEntityName,
			String tgtEntityName, String direction, RoleInterface sourceRole,
			RoleInterface targetRole)
	{
		for (AssociationInterface existingAsso : existingAssociationColl)
		{
			if (umlAssociationName != null
					&& umlAssociationName.equalsIgnoreCase(existingAsso.getName()))
			{//Since name is present, edit this association
				return existingAsso;
			}
			//If even 1 condition does not match, goto next association
			if (!existingAsso.getEntity().getName().equalsIgnoreCase(srcEntityName))
			{
				continue;
			}
			if (!existingAsso.getTargetEntity().getName().equalsIgnoreCase(tgtEntityName))
			{
				continue;
			}
			if (!existingAsso.getAssociationDirection().toString().equalsIgnoreCase(direction))
			{
				continue;
			}
			//SourecRole
			if (!existingAsso.getSourceRole().getAssociationsType().equals(
					sourceRole.getAssociationsType()))
			{
				if (direction.equalsIgnoreCase(DEConstants.AssociationDirection.BI_DIRECTIONAL
						.toString()))
				{//For bi directional association, reversing the association ends and comparing
					if (!existingAsso.getSourceRole().getAssociationsType().equals(
							targetRole.getAssociationsType()))
					{
						continue;
					}
				}
				else
				{
					continue;
				}
			}
			if (!existingAsso.getSourceRole().getMaximumCardinality().equals(
					sourceRole.getMaximumCardinality()))
			{
				if (direction.equalsIgnoreCase(DEConstants.AssociationDirection.BI_DIRECTIONAL
						.toString()))
				{//For bi directional association, reversing the association ends and comparing
					if (!existingAsso.getSourceRole().getMaximumCardinality().equals(
							targetRole.getMaximumCardinality()))
					{
						continue;
					}
				}
				else
				{
					continue;
				}
			}
			if (!existingAsso.getSourceRole().getMinimumCardinality().equals(
					sourceRole.getMinimumCardinality()))
			{
				if (direction.equalsIgnoreCase(DEConstants.AssociationDirection.BI_DIRECTIONAL
						.toString()))
				{//For bi directional association, reversing the association ends and comparing
					if (!existingAsso.getSourceRole().getMinimumCardinality().equals(
							targetRole.getMinimumCardinality()))
					{
						continue;
					}
				}
				else
				{
					continue;
				}
			}
			if (existingAsso.getSourceRole().getName() != null
					&& sourceRole.getName() != null
					&& !existingAsso.getSourceRole().getName().equalsIgnoreCase(
							sourceRole.getName()))
			{
				if (direction.equalsIgnoreCase(DEConstants.AssociationDirection.BI_DIRECTIONAL
						.toString()))
				{//For bi directional association, reversing the association ends and comparing
					if (existingAsso.getSourceRole().getName() != null
							&& targetRole.getName() != null
							&& !existingAsso.getSourceRole().getName().equalsIgnoreCase(
									targetRole.getName()))
					{
						continue;
					}
				}
				else
				{
					continue;
				}
			}
			//			TargetRole
			if (!existingAsso.getTargetRole().getAssociationsType().equals(
					targetRole.getAssociationsType()))
			{
				if (direction.equalsIgnoreCase(DEConstants.AssociationDirection.BI_DIRECTIONAL
						.toString()))
				{//For bi directional association, reversing the association ends and comparing
					if (!existingAsso.getTargetRole().getAssociationsType().equals(
							sourceRole.getAssociationsType()))
					{
						continue;
					}
				}
				else
				{
					continue;
				}
			}
			if (!existingAsso.getTargetRole().getMaximumCardinality().equals(
					targetRole.getMaximumCardinality()))
			{
				if (direction.equalsIgnoreCase(DEConstants.AssociationDirection.BI_DIRECTIONAL
						.toString()))
				{//For bi directional association, reversing the association ends and comparing
					if (!existingAsso.getTargetRole().getMaximumCardinality().equals(
							sourceRole.getMaximumCardinality()))
					{
						continue;
					}
				}
				else
				{
					continue;
				}
			}
			if (!existingAsso.getTargetRole().getMinimumCardinality().equals(
					targetRole.getMinimumCardinality()))
			{//For bi directional association, reversing the association ends and comparing
				if (direction.equalsIgnoreCase(DEConstants.AssociationDirection.BI_DIRECTIONAL
						.toString()))
				{
					if (!existingAsso.getTargetRole().getMinimumCardinality().equals(
							sourceRole.getMinimumCardinality()))
					{
						continue;
					}
				}
				else
				{
					continue;
				}
			}
			if (existingAsso.getTargetRole().getName() != null
					&& targetRole.getName() != null
					&& !existingAsso.getTargetRole().getName().equalsIgnoreCase(
							targetRole.getName()))
			{
				if (direction.equalsIgnoreCase(DEConstants.AssociationDirection.BI_DIRECTIONAL
						.toString()))
				{//For bi directional association, reversing the association ends and comparing
					if (existingAsso.getTargetRole().getName() != null
							&& sourceRole.getName() != null
							&& !existingAsso.getTargetRole().getName().equalsIgnoreCase(
									sourceRole.getName()))
					{
						continue;
					}
				}
				else
				{
					continue;
				}
			}
			//All parameters match. Hence this Association has not been edited.
			return existingAsso;
		}
		return null;
	}

	/**
	 * @param taggedValueColl
	 * @param tagName
	 * @return
	 */
	private String getTaggedValue(Collection<TaggedValue> taggedValueColl, String tagName)
	{
		for (TaggedValue taggedValue : taggedValueColl)
		{
			if (taggedValue.getType() != null && taggedValue.getType().getName().equalsIgnoreCase(tagName))
			{
					Collection<String> dataValueColl = taggedValue.getDataValue();
					for (String value : dataValueColl)
					{
						return value;
					}
			}
		}
		return "";
	}

	/**
	 * Processes inheritance relation ship present in domain model
	 * @param parentIdVsChildrenIds Map with key as UML-id of parent class and value as list of UML-id of all children classes.
	 * @throws DynamicExtensionsSystemException 
	 */
	private void processInheritance(Map<String, List<String>> parentIdVsChildrenIds)
			throws DynamicExtensionsSystemException
	{
		for (Entry<String, List<String>> entry : parentIdVsChildrenIds.entrySet())
		{
			EntityInterface parent = umlClassIdVsEntity.get(entry.getKey());
			for (String childId : entry.getValue())
			{
				EntityInterface child = umlClassIdVsEntity.get(childId);
				child.setParentEntity(parent);
			}
		}
	}

	/**
	 * @param sourceEntity Entity to which a association is to be attached
	 * @return A association attached to given entity.
	 */
	private AssociationInterface getAssociation(EntityInterface sourceEntity, String associationName)
	{
		AssociationInterface association = deFactory.createAssociation();
		//remove it after getting DE fix,association name should not be compulsory
		if (associationName == null || associationName.equals(""))
		{
			association.setName("AssociationName_"
					+ (sourceEntity.getAssociationCollection().size() + 1));
		}
		else
		{
			association.setName(associationName);
		}
		association.setEntity(sourceEntity);
		sourceEntity.addAssociation(association);
		return association;
	}

	/**
	 * Creates Role for the input UMLAssociationEdge
	 * @param edge UML Association Edge to process
	 * @return the Role for given UML Association Edge
	 */
	private RoleInterface getRole(Multiplicity srcMultiplicity, String sourceRoleName,
			String associationType)
	{
		Collection<MultiplicityRange> rangeColl = srcMultiplicity.getRange();
		int minCardinality = 0;
		int maxCardinality = 0;
		for (MultiplicityRange range : rangeColl)
		{
			minCardinality = range.getLower();
			maxCardinality = range.getUpper();
		}

		RoleInterface role = deFactory.createRole();
		if (associationType != null
				&& (associationType
						.equalsIgnoreCase(XMIConstants.TAGGED_VALUE_CONTAINMENT_UNSPECIFIED) || associationType
						.equalsIgnoreCase(XMIConstants.TAGGED_VALUE_CONTAINMENT_NOTSPECIFIED)))
		{
			role.setAssociationsType(DEConstants.AssociationType.ASSOCIATION);
		}
		else
		{
			role.setAssociationsType(DEConstants.AssociationType.CONTAINTMENT);
		}
		role.setName(sourceRoleName);
		role.setMaximumCardinality(getCardinality(maxCardinality));
		role.setMinimumCardinality(getCardinality(minCardinality));
		return role;
	}

	/**
	 * Gets dynamic extension's Cardinality enumration for passed integer value.
	 * @param cardinality intger value of cardinality.
	 * @return Dynamic Extension's Cardinality enumration
	 */
	private DEConstants.Cardinality getCardinality(int cardinality)
	{
		if (cardinality == 0)
		{
			return DEConstants.Cardinality.ZERO;
		}
		if (cardinality == 1)
		{
			return DEConstants.Cardinality.ONE;
		}
		return DEConstants.Cardinality.MANY;
	}

	/**
	 * @param entityInterface
	 * @param controlModel
	 * @return
	 * @throws DynamicExtensionsSystemException
	 * @throws DynamicExtensionsApplicationException
	 */
	private ContainerInterface createNewContainer(EntityInterface entityInterface,
			ControlsModel controlModel) throws DynamicExtensionsSystemException,
			DynamicExtensionsApplicationException
	{
		ContainerInterface containerInterface = deFactory.createContainer();
		containerInterface.setCaption(entityInterface.getName());
		containerInterface.setAbstractEntity(entityInterface);

		//Adding Required field indicator
		containerInterface.setRequiredFieldIndicatior("*");
		containerInterface.setRequiredFieldWarningMessage("indicates required fields.");

		Collection<AbstractAttributeInterface> abstractAttributeCollection = entityInterface
				.getAbstractAttributeCollection();
		Integer sequenceNumber = Integer.valueOf(0);
		ControlInterface controlInterface;
		for (AbstractAttributeInterface abstractAttributeInterface : abstractAttributeCollection)
		{
			controlInterface = getControlForAttribute(abstractAttributeInterface, controlModel);
			if (controlInterface != null) //no control created for id attribute
			{
				sequenceNumber++;
				controlInterface.setSequenceNumber(sequenceNumber);
				containerInterface.addControl(controlInterface);
				controlInterface.setParentContainer((Container) containerInterface);
			}
		}
		return containerInterface;
	}

	/**
	 * @param entityName
	 * @return
	 */
	private ContainerInterface getContainer(String entityName)
	{
		if (retrievedContainerList != null && !retrievedContainerList.isEmpty())
		{
			for (ContainerInterface container : retrievedContainerList)
			{
				if (container.getCaption().equals(entityName))
				{
					return container;
				}
			}
		}
		return null;
	}

	/**
	 * @param entityName
	 * @return
	 */
	private EntityInterface getEntity(String entityName)
	{
		if (retrievedContainerList != null && !retrievedContainerList.isEmpty())
		{
			for (ContainerInterface container : retrievedContainerList)
			{
				if (container.getAbstractEntity().getName().equals(entityName))
				{
					return (EntityInterface) container.getAbstractEntity();
				}
			}
		}
		return null;
	}

	/**
	 * This method creates a container object.
	 * @param entityInterface
	 * @return
	 */
	protected void createContainer(EntityInterface entityInterface) throws Exception
	{
		ContainerInterface containerInterface = getContainer(entityInterface.getName());

		/*DynamicExtensionsUtility.getContainerByCaption(entityInterface.getName()); */
		if (containerInterface == null)//Add
		{
			ControlsModel controlModel = new ControlsModel();
			containerInterface = createNewContainer(entityInterface, controlModel);
		}
		else
		{//Edit
			editEntityAndContainer(containerInterface, entityInterface);

			//Populating Attributes and Controls
			//			Collection<AbstractAttributeInterface> editedAttributeColl = entityInterface
			//				.getAbstractAttributeCollection();
			//			Collection<AbstractAttributeInterface> originalAttributeColl = ((EntityInterface) containerInterface
			//					.getAbstractEntity()).getAbstractAttributeCollection();

			/* Bug Id: 7209
			 * editedAttributeColl - contains new attribute that needs to be added
			 * originalAttributeColl - contains only original attributes from database
			 * As both these objects point to the same entity object hence both collections have same attributes.
			 * Hence a new object for editedAttributeColl is created and the
			 * new attribute is removed from the originalAttributeColl on the basis of objects having id as null
			 */
			Collection<AbstractAttributeInterface> savedAssociation = new ArrayList<AbstractAttributeInterface>();
			Collection<AbstractAttributeInterface> editedAttributeColl = new ArrayList<AbstractAttributeInterface>();
			editedAttributeColl.addAll(entityInterface.getAbstractAttributeCollection());

			Collection<AbstractAttributeInterface> originalAttributeColl = ((EntityInterface) containerInterface
					.getAbstractEntity()).getAbstractAttributeCollection();

			Iterator<AbstractAttributeInterface> abstrAttrIter = originalAttributeColl.iterator();
			while (abstrAttrIter.hasNext())
			{
				AbstractAttributeInterface originalAttr = abstrAttrIter.next();
				if (originalAttr.getId() == null && originalAttr instanceof AssociationInterface)
				{
					/*
					 * Bug Id:7316
					 * Here the new associations were also getting deleted from the entity object
					 * Hence, new association objects are saved in a list and then removed from entity
					 * so that they can be added again to the entity object
					 */
						savedAssociation.add(originalAttr);
				}
			}
			Collection<AbstractAttributeInterface> attributesToRemove = new HashSet<AbstractAttributeInterface>();
			for (AbstractAttributeInterface editedAttribute : editedAttributeColl)
			{
				if (editedAttribute.getName().equalsIgnoreCase(Constants.SYSTEM_IDENTIFIER)
						&& xmiConfigurationObject.isAddIdAttribute())
				{
					// We dont edit "id" attribute as it is the system identifier.
					continue;
				}
				if (editedAttribute instanceof AssociationInterface)
				{
					//When association direction is changed from bi-directional to src-destination, this method removes
					//the redundant association.
					removeRedundantAssociation(editedAttribute, attributesToRemove);
				}
				ControlsModel controlModel = new ControlsModel();
				boolean isAttrPresent = getAttrToEdit(originalAttributeColl, editedAttribute);

				if (isAttrPresent)
				{//Edit
					editAttributeAndControl(controlModel, editedAttribute, containerInterface);
				}
				else
				{//Add Attribute
					addAttributeAndControl(controlModel, editedAttribute, containerInterface);
					//					Duplicate attributes have been created since we have created attribute in the method create attributes also
					//Do not create attributes above but create them here.
					if (!(editedAttribute instanceof AssociationInterface))
					{
						attributesToRemove.add(editedAttribute);
					}
				}
				controlModel.setCaption(editedAttribute.getName());
				controlModel.setName(editedAttribute.getName());
				setTaggedValue(controlModel, editedAttribute);
				//Not for Containment Association Control
				if (!(editedAttribute instanceof AssociationInterface))
				{
					AttributeInterface attribute = (AttributeInterface) editedAttribute;
					controlModel.setIsPrimaryKey(attribute.getIsPrimaryKey());
					controlModel.setIsNullable(attribute.getIsNullable());
					controlModel.setColumnName(attribute.getColumnProperties().getName());

					ApplyFormControlsProcessor applyFormControlsProcessor = ApplyFormControlsProcessor
							.getInstance();
					applyFormControlsProcessor.addControlToForm(containerInterface, controlModel,
							controlModel, entityInterface.getEntityGroup());
					populateAttributeForPrimaryKey(entityInterface.getAttributeByName(controlModel
							.getName()), controlModel);

				}
			}
			/*Bug id:7316
			 * new associations are added again to the entity
			 */
			for (AbstractAttributeInterface savedAssoc : savedAssociation)
			{
				entityInterface.addAbstractAttribute(savedAssoc);
			}
			//Since we are creating attributes in createAttributes method and also in applyFormControlsProcessor.addControlToForm method
			//duplicate attributes have been created. Hence removing the duplicate attributes.
			editedAttributeColl.removeAll(attributesToRemove);
		}
		List<ContainerInterface> containerList = new ArrayList<ContainerInterface>();
		containerList.add(containerInterface);
		entityNameVsContainers.put(entityInterface.getName(), containerList);
	}

	/**
	 * 
	 * @param attribute
	 * @param controlModel
	 */
	private void populateAttributeForPrimaryKey(AttributeInterface attribute,
			ControlsModel controlModel)
	{
		if (attribute != null)
		{
			attribute.setIsPrimaryKey(controlModel.getIsPrimaryKey());
			attribute.setIsNullable(controlModel.getIsNullable());
			attribute.getColumnProperties().setName(controlModel.getColumnName());
		}

	}

	/**
	 * @param editedAttribute
	 * @param attributesToRemove
	 */
	private void removeRedundantAssociation(AbstractAttributeInterface editedAttribute,
			Collection<AbstractAttributeInterface> attributesToRemove)
	{
		AssociationInterface association = (AssociationInterface) editedAttribute;
		Collection<AssociationInterface> targetEntityAssociationColl = association
				.getTargetEntity().getAssociationCollection();

		EntityInterface originalTargetEntity = getEntity(association.getTargetEntity().getName());
		if (originalTargetEntity != null)
		{
			//Removing redundant association
			for (AssociationInterface targetAsso : targetEntityAssociationColl)
			{
				if (targetAsso.getTargetEntity().getName().equalsIgnoreCase(
						association.getEntity().getName()))
				{
					AssociationInterface originalTargetAssociation = null;
					for (AssociationInterface originalTgtAsso : originalTargetEntity
							.getAssociationCollection())
					{
						if (targetAsso.getName().equalsIgnoreCase(originalTgtAsso.getName()))
						{
							originalTargetAssociation = originalTgtAsso;
							break;
						}
					}

					if (targetAsso.getAssociationDirection().equals(
							DEConstants.AssociationDirection.SRC_DESTINATION)
							&& originalTargetAssociation != null
							&& originalTargetAssociation.getAssociationDirection().equals(
									DEConstants.AssociationDirection.BI_DIRECTIONAL))
					{//We need to remove system generated association if direction has been changed from bi directional to source destination
						attributesToRemove.add(editedAttribute);
					}
				}
			}
		}
	}

	/**
	 * @param controlModel
	 * @param editedAttribute
	 * @param containerInterface
	 * @throws DynamicExtensionsSystemException
	 * @throws DynamicExtensionsApplicationException
	 */
	private void addAttributeAndControl(ControlsModel controlModel,
			AbstractAttributeInterface editedAttribute, ContainerInterface containerInterface)
			throws DynamicExtensionsSystemException, DynamicExtensionsApplicationException
	{
		controlModel.setControlOperation(ProcessorConstants.OPERATION_ADD);
		ControlInterface newcontrol = getControlForAttribute(editedAttribute, controlModel);
		if (newcontrol != null)
		{ //no control created for id attribute
			int sequenceNumber = containerInterface.getControlCollection().size() + 1;
			newcontrol.setSequenceNumber(sequenceNumber);
			//containerInterface.addControl(newcontrol);
			newcontrol.setParentContainer((Container) containerInterface);

			String userSelectedTool = DynamicExtensionsUtility.getControlName(newcontrol);
			controlModel.setUserSelectedTool(userSelectedTool);
			//For Text Control
			if (newcontrol instanceof TextFieldInterface)
			{
				controlModel.setColumns(Integer.valueOf(0));
			}
			//For creating Association or Attribute
			populateControlModel(controlModel, editedAttribute);

			//if(controlModel.getUserSelectedTool().equalsIgnoreCase(ProcessorConstants.ADD_SUBFORM_CONTROL))
			if (editedAttribute instanceof AssociationInterface)
			{
				containerInterface.addControl(newcontrol);
			}
		}
	}

	/**
	 * @param originalAttributeColl
	 * @param controlModel
	 * @param editedAttribute
	 * @param containerInterface
	 * @param loadFormControlsProcessor
	 * @throws DynamicExtensionsSystemException
	 * @throws DynamicExtensionsApplicationException
	 */
	private void editAttributeAndControl(ControlsModel controlModel,
			AbstractAttributeInterface editedAttribute, ContainerInterface containerInterface)
			throws DynamicExtensionsSystemException, DynamicExtensionsApplicationException
	{
		LoadFormControlsProcessor loadFormControlsProcessor = LoadFormControlsProcessor
				.getInstance();
		controlModel.setControlOperation(ProcessorConstants.OPERATION_EDIT);

		Collection<ControlInterface> originalControlColl = containerInterface
				.getControlCollection();
		ControlInterface originalControlObj = null;
		for (ControlInterface originalcontrol : originalControlColl)
		{
			if (originalcontrol.getBaseAbstractAttribute().getName().equalsIgnoreCase(
					editedAttribute.getName()))
			{
				originalControlObj = originalcontrol;
				break;
			}
		}

		if (originalControlObj != null)
		{
			originalControlObj.setBaseAbstractAttribute(editedAttribute);
			//This method wil give us populated ControlUIBean and AttributeUIBean with original control object corresponding to edited attribute
			if (!(editedAttribute instanceof AssociationInterface))
			{
				loadFormControlsProcessor.editControl(originalControlObj, controlModel,
						controlModel);

				controlModel
						.setSelectedControlId(originalControlObj.getSequenceNumber().toString());

			}
			//controlModel.setCaption(originalControlObj.getCaption());
		}
	}

	/**
	 * Method to set tagged values(max length, precision, date format) in control model
	 * @throws DynamicExtensionsSystemException
	 */
	private void setTaggedValue(ControlsModel controlModel,
			AbstractAttributeInterface editedAttribute) throws DynamicExtensionsSystemException
	{
		if (!(editedAttribute instanceof AssociationInterface))
		{
			Map<String, String> taggedValueMap = attrVsMapTagValues.get(editedAttribute);
			if (taggedValueMap != null)
			{
				// max length of string from tagged value
				String maxLen = getMaxLengthTagValue(taggedValueMap);
				controlModel.setAttributeSize(maxLen);

				//date format tagged value
				String format = getDateFormatTagValue(taggedValueMap);
				controlModel.setFormat(format);
				String dateFormat = DynamicExtensionsUtility.getDateFormat(format);
				controlModel.setDateValueType(dateFormat);

				// precision tagged value
				Integer precision = getPrecisionTagValue(taggedValueMap,
						((AttributeInterface) editedAttribute).getAttributeTypeInformation());
				controlModel.setAttributeDecimalPlaces(precision.toString());

				//Added by Prashant
				//password tagged value
				String password = getPasswordTagValue(taggedValueMap);
				controlModel.setIsPassword(Boolean.parseBoolean(password));

				//Single/Multiline(Number of Lines) tagged Value
				setMultilineTaggedValue(taggedValueMap, controlModel);

				//NoOfColumns tagged Value
				String width = getDisplayWidthTagValue(taggedValueMap);
				controlModel.setColumns(Integer.parseInt(width.toString()));

				//defaultValue tagged Value
				String defaultValue = getDefaultValueTagValue(taggedValueMap);
				controlModel.setAttributeDefaultValue(defaultValue);

				if (defaultValue != null && !defaultValue.equalsIgnoreCase(""))
				{
					//dateValueType  value
					controlModel.setDateValueType(ProcessorConstants.DATE_VALUE_SELECT);
				}
				//URL tagged value
				String url = getUrlTagValue(taggedValueMap);
				controlModel.setIsUrl(Boolean.parseBoolean(url));

				//PHI Attribute tagged value
				String PHIAttribute = getPHIAttributeTagValue(taggedValueMap);
				controlModel.setAttributeIdentified(PHIAttribute);

				//FileFormats tagged value
				String[] fileFormats = getFileFormatsTagValue(taggedValueMap);
				controlModel.setFileFormats(fileFormats);

				//For list box or combo box
				setMultiselectTaggedValue(taggedValueMap, controlModel);

				//For combo box,separator tagged value
				String separator = getSeparatorTagValue(taggedValueMap);
				controlModel.setSeparator(separator);

				//set Explicit validation Rules
				setExplicitValidationRules(taggedValueMap, controlModel);
			}

		}
	}

	/**
	 *
	 * @param taggedValueMap
	 * @param controlModel
	 */
	private void setMultiselectTaggedValue(Map<String, String> taggedValueMap,
			ControlsModel controlModel)
	{
		if (taggedValueMap.containsKey(XMIConstants.TAGGED_VALUE_MULTISELECT))
		{
			controlModel.setIsMultiSelect(true);
			String listBoxHeight = getListBoxHeightTagValue(taggedValueMap);
			controlModel.setAttributeNoOfRows(listBoxHeight);
		}

	}

	/**
	 *
	 * @param taggedValueMap
	 * @return
	 */
	private String getListBoxHeightTagValue(Map<String, String> taggedValueMap)
	{
		String listBoxHeight = taggedValueMap.get(XMIConstants.TAGGED_VALUE_MULTISELECT);
		if (listBoxHeight == null || listBoxHeight.trim().equals(""))
		{
			listBoxHeight = Integer.toString(edu.common.dynamicextensions.ui.util.Constants.DEFAULT_ROW_SIZE);
		}
		return listBoxHeight;
	}

	/**
	 *
	 * @param taggedValueMap
	 * @return
	 */
	private String getSeparatorTagValue(Map<String, String> taggedValueMap)
	{
		String separator = taggedValueMap.get(XMIConstants.TAGGED_VALUE_SEPARATOR);
		if (separator == null || separator.trim().equals(""))
		{
			return null;
		}
		return separator;
	}

	/**
	 *
	 * @param taggedValueMap
	 * @param controlModel
	 */
	private void setMultilineTaggedValue(Map<String, String> taggedValueMap,
			ControlsModel controlModel)
	{
		if (taggedValueMap.containsKey(XMIConstants.TAGGED_VALUE_MULTILINE))
		{
			controlModel.setLinesType(XMIConstants.MULTILINE);
			String noOFLines = getNoOfRowsTagValue(taggedValueMap);
			controlModel.setRows(Integer.parseInt(noOFLines.toString()));
		}
	}

	/**
	 *
	 * @param taggedValueMap
	 * @return
	 */
	private String[] getFileFormatsTagValue(Map<String, String> taggedValueMap)
	{
		String fileFormat = taggedValueMap.get(XMIConstants.TAGGED_VALUE_FILE_FORMATS);
		if (fileFormat != null)
		{
			StringTokenizer stringTokenizer = new StringTokenizer(fileFormat, ",");
			int size = stringTokenizer.countTokens();

			String[] fileformats = new String[size];

			fileformats = fileFormat.split(",");

			return fileformats;

		}
		return null;
	}

	/**
	 * @param taggedValueMap
	 * @return
	 */
	private boolean isMultiselectTagValue(Map<String, String> taggedValueMap)
	{
		if (taggedValueMap != null && taggedValueMap.containsKey(XMIConstants.TAGGED_VALUE_MULTISELECT))
		{
				return true;
		}
		return false;
	}

	/**
	 * @param taggedValueMap
	 * @return
	 */
	private String getMultiselectTagValue(Map<String, String> taggedValueMap)
	{
		if (taggedValueMap != null && taggedValueMap.containsKey(XMIConstants.TAGGED_VALUE_MULTISELECT))
		{
				return taggedValueMap.get(XMIConstants.TAGGED_VALUE_MULTISELECT);
		}
		return null;
	}

	/**
	 *
	 * @param taggedValueMap
	 * @return
	 */
	private String getPHIAttributeTagValue(Map<String, String> taggedValueMap)
	{
		String PHIAttribute = taggedValueMap.get(XMIConstants.TAGGED_VALUE_PHI_ATTRIBUTE);
		if (PHIAttribute == null || PHIAttribute.trim().equals(""))
		{
			return "false";
		}
		return PHIAttribute;
	}

	/**
	 *
	 * @param taggedValueMap
	 * @return
	 */
	private String getDisplayWidthTagValue(Map<String, String> taggedValueMap)
	{
		String width = taggedValueMap.get(XMIConstants.TAGGED_VALUE_DISPLAY_WIDTH);
		if (width == null || width.trim().equals(""))
		{
			width = Integer.toString(edu.common.dynamicextensions.ui.util.Constants.DEFAULT_COLUMN_SIZE);
		}
		if (width != null && (Integer.parseInt(width)) > 999)
		{
			width = XMIConstants.MAX_LENGTH_LIMIT;
		}
		return width;
	}

	/**
	 * @param taggedValueMap
	 * @return
	 */
	private String getMaxLengthTagValue(Map<String, String> taggedValueMap)
	{
		String maxLen = taggedValueMap.get(XMIConstants.TAGGED_VALUE_MAX_LENGTH);
		if (maxLen == null || maxLen.trim().equals(""))
		{
			maxLen = XMIConstants.DEFAULT_TEXT_FIELD_MAX_LENGTH;
		}
		if (maxLen != null && (Integer.parseInt(maxLen)) > 999)
		{
			maxLen = XMIConstants.MAX_LENGTH_LIMIT;
		}
		return maxLen;
	}

	/**
	 * @param taggedValueMap
	 * @return
	 */
	private String getDateFormatTagValue(Map<String, String> taggedValueMap)
	{
		String format = taggedValueMap.get(XMIConstants.TAGGED_VALUE_DATE_FORMAT);
		if (format == null || format.trim().equals(""))
		{
			format = CommonServiceLocator.getInstance().getDatePattern();
		}

		return format;
	}

	/**
	 *
	 * @param taggedValueMap
	 * @return
	 */
	private String getNoOfRowsTagValue(Map<String, String> taggedValueMap)
	{
		String noOFRows = taggedValueMap.get(XMIConstants.TAGGED_VALUE_MULTILINE);
		if (noOFRows == null || noOFRows.trim().equals(""))
		{
			noOFRows = Integer.toString(edu.common.dynamicextensions.ui.util.Constants.DEFAULT_ROW_SIZE);
		}
		return noOFRows;
	}

	/**
	 *
	 * @param taggedValueMap
	 * @return
	 */
	private String getDefaultValueTagValue(Map<String, String> taggedValueMap)
	{
		String defaultValue = taggedValueMap.get(XMIConstants.TAGGED_VALUE_DEFAULT_VALUE);
		if (defaultValue == null || defaultValue.trim().equals(""))
		{
			return "";
		}
		return defaultValue;
	}

	/**
	 * @param attributeInterface 
	 * @param taggedValueMap
	 * @return
	 * @throws DynamicExtensionsSystemException 
	 * @throws DynamicExtensionsApplicationException 
	 */
	private String getDefaultValueForBooleanTagValue(
			AbstractAttributeInterface abstractAttributeInterface,
			Map<String, String> taggedValueMap) throws DynamicExtensionsApplicationException,
			DynamicExtensionsSystemException
	{

		String defaultValue = taggedValueMap.get(XMIConstants.TAGGED_VALUE_DEFAULT_VALUE);
		String boolValue = ProcessorConstants.FALSE;
		if (defaultValue != null && !defaultValue.trim().equals(""))
		{
			XMIImportValidator.validateDefBooleanValue(abstractAttributeInterface, defaultValue);
			boolValue = defaultValue;
		}
		return boolValue;
	}

	/**
	 * By Prashant
	 * @param taggedValueMap
	 * @param attributeTypeInformation
	 * @return
	 */
	private String getPasswordTagValue(Map<String, String> taggedValueMap)
	{
		String password = taggedValueMap.get(XMIConstants.TAGGED_VALUE_PASSWORD);
		if (password == null || password.trim().equals(""))
		{
			return "false";
		}
		return password;
	}

	/**
	 *
	 * @param taggedValueMap
	 * @return
	 */
	private String getUrlTagValue(Map<String, String> taggedValueMap)
	{
		String url = taggedValueMap.get(XMIConstants.TAGGED_VALUE_URL);
		if (url == null || url.trim().equals(""))
		{
			return "false";
		}
		return url;
	}

	/**
	 * @param precision
	 * @param editedAttribute
	 * @throws DynamicExtensionsSystemException
	 */
	private Integer getPrecisionTagValue(Map<String, String> taggedValueMap,
			AttributeTypeInformationInterface attrTypeInfo) throws DynamicExtensionsSystemException
	{
		String precision = taggedValueMap.get(XMIConstants.TAGGED_VALUE_PRECISION);
		Integer precisionDigits = null;

		if (precision == null || precision.trim().equals(""))
		{
			if (attrTypeInfo instanceof FloatAttributeTypeInformation)
			{
				precisionDigits = Integer.valueOf(
						edu.common.dynamicextensions.ui.util.Constants.FLOAT_PRECISION);
			}
			else if (attrTypeInfo instanceof DoubleAttributeTypeInformation)
			{
				precisionDigits = Integer.valueOf(
						edu.common.dynamicextensions.ui.util.Constants.DOUBLE_PRECISION);
			}
			else
			{
				precisionDigits = Integer.valueOf(edu.common.dynamicextensions.ui.util.Constants.ZERO);
			}
		}
		else
		{
			precisionDigits = Integer.parseInt(precision);
			if (precisionDigits.intValue() > edu.common.dynamicextensions.ui.util.Constants.DOUBLE_PRECISION)
			{
				throw new DynamicExtensionsSystemException(
						"Precision can at maximum be 15 owing to database constraints.");
			}
		}

		return precisionDigits;
	}

	/**
	 * @param containerInterface
	 * @param entityInterface
	 * @throws DynamicExtensionsSystemException
	 * @throws DynamicExtensionsApplicationException
	 */
	private void editEntityAndContainer(ContainerInterface containerInterface,
			EntityInterface entityInterface) throws DynamicExtensionsSystemException,
			DynamicExtensionsApplicationException
	{
		ContainerProcessor containerProcessor = ContainerProcessor.getInstance();
		EntityProcessor entityProcessor = EntityProcessor.getInstance();
		ContainerModel containerModel = new ContainerModel();

		//Setting Edited entity name as caption for container.
		//Also not setting parentform to avoid unnecessary DB call as base container is already present in the container object
		containerModel.setFormName(entityInterface.getName());
		if (entityInterface.isAbstract())
		{
			containerModel.setIsAbstract("true");
		}
		//Container Object is now populated
		containerProcessor.populateContainerInterface(containerInterface, containerModel);

		containerModel.setFormDescription(entityInterface.getDescription());
		//Entity Object is now populated
		entityProcessor.addEntity(containerModel, (EntityInterface) containerInterface
				.getAbstractEntity());
	}

	/**
	 * @param originalAttributeColl
	 * @param editedAttribute
	 * @return
	 */
	private boolean getAttrToEdit(Collection<AbstractAttributeInterface> originalAttributeColl,
			AbstractAttributeInterface editedAttribute)
	{
		for (AbstractAttributeInterface originalAttribute : originalAttributeColl)
		{
			if (editedAttribute.getName().equalsIgnoreCase(originalAttribute.getName()))
			{
				return true;
			}
		}
		return false;
	}

	/**
	 * @param controlModel
	 * @param editedAttribute
	 */
	private void populateControlModel(ControlsModel controlModel,
			AbstractAttributeInterface editedAttribute)
	{
		if (editedAttribute instanceof AssociationInterface)
		{
			controlModel.setDisplayChoice(ProcessorConstants.DISPLAY_CHOICE_LOOKUP);
		}
		else
		{
			AttributeInterface attribute = (AttributeInterface) editedAttribute;

			AttributeTypeInformationInterface attributeTypeInfo = attribute
					.getAttributeTypeInformation();
			if (attributeTypeInfo instanceof DateAttributeTypeInformation)
			{
				controlModel.setDataType(ProcessorConstants.DATATYPE_DATE);
			}
			else if (attributeTypeInfo instanceof StringAttributeTypeInformation)
			{
				controlModel.setDataType(ProcessorConstants.DATATYPE_STRING);
			}
			else if (attributeTypeInfo instanceof ByteArrayAttributeTypeInformation)
			{
				controlModel.setDataType(ProcessorConstants.DATATYPE_BYTEARRAY);
			}
			else if (attributeTypeInfo instanceof BooleanAttributeTypeInformation)
			{
				controlModel.setDataType(ProcessorConstants.DATATYPE_BOOLEAN);
			}
			else if (attributeTypeInfo instanceof ShortAttributeTypeInformation)
			{
				controlModel.setDataType(ProcessorConstants.DATATYPE_SHORT);
			}
			else if (attributeTypeInfo instanceof IntegerAttributeTypeInformation)
			{
				controlModel.setDataType(ProcessorConstants.DATATYPE_INTEGER);
			}
			else if (attributeTypeInfo instanceof LongAttributeTypeInformation)
			{
				controlModel.setDataType(ProcessorConstants.DATATYPE_LONG);
			}
			else if (attributeTypeInfo instanceof FloatAttributeTypeInformation)
			{
				controlModel.setDataType(ProcessorConstants.DATATYPE_FLOAT);
			}
			else if (attributeTypeInfo instanceof DoubleAttributeTypeInformation)
			{
				controlModel.setDataType(ProcessorConstants.DATATYPE_DOUBLE);
			}
		}
	}

	/**
	 * @param parentIdVsChildrenIds
	 * This method add the parent container to the child container for Generalization.
	 */
	protected void postProcessInheritence(Map<String, List<String>> parentIdVsChildrenIds)
			throws Exception
	{
		for (Entry<String, List<String>> entry : parentIdVsChildrenIds.entrySet())
		{
			EntityInterface parent = umlClassIdVsEntity.get(entry.getKey());

			List parentContainerList = (ArrayList) entityNameVsContainers.get(parent.getName());
			ContainerInterface parentContainer = null;
			if (parentContainerList == null || parentContainerList.isEmpty())
			{
				parentContainer = getContainer(parent.getName());
			}
			else
			{
				parentContainer = (ContainerInterface) parentContainerList.get(0);
			}
			for (String childId : entry.getValue())
			{
				EntityInterface child = umlClassIdVsEntity.get(childId);

				List childContainerList = (ArrayList) entityNameVsContainers.get(child.getName());
				ContainerInterface childContainer = null;
				if (childContainerList == null || childContainerList.isEmpty())
				{
					childContainer = getContainer(child.getName());
				}
				else
				{
					childContainer = (ContainerInterface) childContainerList.get(0);
				}

				childContainer.setBaseContainer(parentContainer);
			}
		}
	}

	/**
	 * This method adds the target container to the containment association control
	 */
	protected void addControlsForAssociation() throws Exception
	{
		Set<String> entityIdKeySet = entityNameVsContainers.keySet();
		for (String entityId : entityIdKeySet)
		{
			List containerList = (ArrayList) entityNameVsContainers.get(entityId);
			ContainerInterface containerInterface = (ContainerInterface) containerList.get(0);
			Collection<ControlInterface> controlCollection = containerInterface
					.getControlCollection();

			for (ControlInterface controlInterface : controlCollection)
			{
				if (controlInterface instanceof ContainmentAssociationControl)
				{
					ContainmentAssociationControl containmentAssociationControl = (ContainmentAssociationControl) controlInterface;
					AssociationInterface associationInterface = (AssociationInterface) controlInterface
							.getBaseAbstractAttribute();

					String targetEntityId = associationInterface.getTargetEntity().getName();

					List targetContainerInterfaceList = (ArrayList) entityNameVsContainers
							.get(targetEntityId.toString());

					//					TODO remove this condition to delete association with deleted or renamed entities.
					//getting container corresponding to renamed or deleted entity which is associated with some association from the retrieved entity group
					ContainerInterface targetContainerInterface = null;
					if (targetContainerInterfaceList == null
							|| targetContainerInterfaceList.isEmpty())
					{
						targetContainerInterface = getContainer(targetEntityId);
					}
					else
					{
						targetContainerInterface = (ContainerInterface) targetContainerInterfaceList
								.get(0);
					}
					containmentAssociationControl.setContainer(targetContainerInterface);
				}
			}
		}
	}

	/**
	 *
	 * @param abstractAttributeInterface
	 * @param controlModel
	 * @return
	 * This method creates a control for the attribute.
	 * @throws DynamicExtensionsApplicationException
	 */
	private ControlInterface getControlForAttribute(
			AbstractAttributeInterface abstractAttributeInterface, ControlsModel controlModel)
			throws DynamicExtensionsSystemException, DynamicExtensionsApplicationException
	{
		ControlInterface controlInterface = null;
		AttributeProcessor attributeProcessor = AttributeProcessor.getInstance();
		ControlProcessor controlProcessor = ControlProcessor.getInstance();

		if (abstractAttributeInterface instanceof AssociationInterface)
		{

			AssociationInterface associationInterface = (AssociationInterface) abstractAttributeInterface;
			Map<String, String> taggedValueMap = associationVsMapTagValues
					.get(associationInterface);
			//If association is system generated,its taggedvaluemap is null ,so return null instead of processing further.
			//This scenario occurs while importing static model's xmi,like catissuesuite through DE
			if (associationInterface.getIsSystemGenerated())
			{
				return null;

			}			
			if (associationInterface.getSourceRole().getAssociationsType().compareTo(
					AssociationType.CONTAINTMENT) == 0)
			{
				// This line is for containment association.

				controlInterface = deFactory.createContainmentAssociationControl();
				associationInterface.getSourceRole().setAssociationsType(
						AssociationType.CONTAINTMENT);
				associationInterface.getTargetRole().setAssociationsType(
						AssociationType.CONTAINTMENT);
			}
			else
			{
				// this is for Linking Association
				// if source maxcardinality or target  maxcardinality or both == -1, then control is listbox.
				// int  sourceMaxCardinality = associationInterface.getSourceRole().getMaximumCardinality().getValue().intValue();
				controlModel.setDisplayChoice(ProcessorConstants.DISPLAY_CHOICE_LOOKUP);
				String userSelectedControlName = null;
				//				int targetMaxCardinality = 0;
				//				if (associationInterface.getTargetRole() != null && associationInterface.getTargetRole().getMaximumCardinality() != null)
				//				{
				//					targetMaxCardinality = associationInterface.getTargetRole().getMaximumCardinality().getValue().intValue();
				//				}
				//if (targetMaxCardinality == -1)

				if (isMultiselectTagValue(taggedValueMap))
				{//List box for 1 to many or many to many relationship
					userSelectedControlName = ProcessorConstants.LISTBOX_CONTROL;
					controlInterface = deFactory.createListBox();
					setMultiselectTaggedValue(taggedValueMap, controlModel);
					((ListBoxInterface) controlInterface).setIsMultiSelect(controlModel
							.getIsMultiSelect());
					((ListBoxInterface) controlInterface).setNoOfRows(controlModel.getRows());
				}
				else
				{//Combo box for the rest
					userSelectedControlName = ProcessorConstants.COMBOBOX_CONTROL;
					controlInterface = deFactory.createComboBox();
				}
				String separator = getSeparatorTagValue(taggedValueMap);
				((SelectControl) controlInterface).setSeparator(separator);
				addAssociationDisplayAttributes(associationInterface, taggedValueMap,
						controlInterface);
				//Set Explicit Validation Rules
				setExplicitValidationRules(taggedValueMap, controlModel);
				//populate rules
				attributeProcessor.populateRules(userSelectedControlName, associationInterface,
						controlModel);
				String[] ruleNamesString = new String[0];
				controlModel.setValidationRules(ruleNamesString);
				controlModel.setTempValidationRules(ruleNamesString);
			}
		}
		else
		{
			AttributeInterface attributeInterface = (AttributeInterface) abstractAttributeInterface;
			AttributeTypeInformationInterface attributeTypeInformation = attributeInterface
					.getAttributeTypeInformation();
			UserDefinedDEInterface userDefinedDEInterface = (UserDefinedDEInterface) attributeTypeInformation
					.getDataElement();
			if (!attributeInterface.getIsPrimaryKey())
			{
				Map<String, String> taggedValueMap = attrVsMapTagValues.get(attributeInterface);
				if (userDefinedDEInterface != null
						&& userDefinedDEInterface.getPermissibleValueCollection() != null
						&& userDefinedDEInterface.getPermissibleValueCollection().size() > 0)
				{
					String userSelectedControlName = null;
					controlModel.setDataType(ProcessorConstants.DATATYPE_STRING);
					// multiselect for permisible values
					//attributeInterface.setIsCollection(new Boolean(true));
					if (isMultiselectTagValue(taggedValueMap))
					{
						userSelectedControlName = ProcessorConstants.LISTBOX_CONTROL;
						controlInterface = deFactory.createListBox();
						setMultiselectTaggedValue(taggedValueMap, controlModel);
					}
					else
					{//Combo box for the rest
						userSelectedControlName = ProcessorConstants.COMBOBOX_CONTROL;
						controlInterface = deFactory.createComboBox();
					}
					if ((controlModel.getIsMultiSelect() != null)
							&& (controlModel.getIsMultiSelect().booleanValue()))
					{
						controlInterface = controlProcessor.getListBoxControl(controlInterface,
								controlModel);
					}
					else
					{
						controlInterface = controlProcessor.getComboBoxControl(controlInterface,
								controlModel, entityGroup);
					}
					//Set Explicit Validation Rules
					setExplicitValidationRules(taggedValueMap, controlModel);
					//populate rules
					attributeProcessor.populateRules(userSelectedControlName, attributeInterface,
							controlModel);
					String[] ruleNamesString = new String[0];
					controlModel.setValidationRules(ruleNamesString);
					controlModel.setTempValidationRules(ruleNamesString);
				}
				else if (attributeTypeInformation instanceof DateAttributeTypeInformation)
				{
					controlModel.setDataType(ProcessorConstants.DATATYPE_DATE);
					String userSelectedControlName = ProcessorConstants.DATEPICKER_CONTROL;
					String format = getDateFormatTagValue(taggedValueMap);
					controlModel.setFormat(format);
					controlInterface = deFactory.createDatePicker();
					String defaultValue = getDefaultValueTagValue(taggedValueMap);
					controlModel.setAttributeDefaultValue(defaultValue);
					if (defaultValue != null && !defaultValue.equalsIgnoreCase(""))
					{
						controlModel.setDateValueType(ProcessorConstants.DATE_VALUE_SELECT);
					}

					//PHI
					String strIsIdentified = getPHIAttributeTagValue(taggedValueMap);
					if (strIsIdentified != null && !strIsIdentified.equalsIgnoreCase(""))
					{
						attributeProcessor.populateIsIdentifiedInfo(attributeInterface,
								strIsIdentified);
					}

					//Set Explicit Validation Rules
					setExplicitValidationRules(taggedValueMap, controlModel);

					attributeProcessor.populateDateAttributeInterface(attributeInterface,
							(DateAttributeTypeInformation) attributeTypeInformation, controlModel);
					//					//populate rules
					attributeProcessor.populateRules(userSelectedControlName, attributeInterface,
							controlModel);
					String[] ruleNamesString = new String[0];
					controlModel.setValidationRules(ruleNamesString);
					controlModel.setTempValidationRules(ruleNamesString);

				}
				//Creating check box for boolean attributes
				else if (attributeTypeInformation instanceof BooleanAttributeTypeInformation)
				{
					String userSelectedControlName = ProcessorConstants.CHECKBOX_CONTROL;
					controlModel.setDataType(ProcessorConstants.DATATYPE_BOOLEAN);
					controlInterface = deFactory.createCheckBox();
					String defaultValue = getDefaultValueForBooleanTagValue(attributeInterface,
							taggedValueMap);

					BooleanValueInterface booleanValue = DomainObjectFactory.getInstance()
							.createBooleanValue();
					booleanValue.setValue(Boolean.valueOf(defaultValue));

					((BooleanAttributeTypeInformation) attributeTypeInformation)
							.setDefaultValue(booleanValue);
					//Set Explicit Validation Rules
					setExplicitValidationRules(taggedValueMap, controlModel);
					//populate rules
					attributeProcessor.populateRules(userSelectedControlName, attributeInterface,
							controlModel);
					String[] ruleNamesString = new String[0];
					controlModel.setValidationRules(ruleNamesString);
					controlModel.setTempValidationRules(ruleNamesString);
				}
				//Creating File upload for byte array attributes
				else if (attributeTypeInformation instanceof FileAttributeTypeInformation)
				{
					controlModel.setDataType(ProcessorConstants.DATATYPE_FILE);
					String userSelectedControlName = ProcessorConstants.FILEUPLOAD_CONTROL;
					controlInterface = deFactory.createFileUploadControl();
					((FileUploadInterface) controlInterface).setColumns(10);
					//Setting MaxLength
					String maxLen = getMaxLengthTagValue(taggedValueMap);
					controlModel.setAttributeSize(maxLen);
					//Setting fileformats
					String[] fileFormats = getFileFormatsTagValue(taggedValueMap);
					controlModel.setFileFormats(fileFormats);
					attributeProcessor.populateFileAttributeInterface(
							(FileAttributeTypeInformation) attributeTypeInformation, controlModel);

					//PHI
					String strIsIdentified = getPHIAttributeTagValue(taggedValueMap);
					if (strIsIdentified != null && !strIsIdentified.equalsIgnoreCase(""))
					{
						attributeProcessor.populateIsIdentifiedInfo(attributeInterface,
								strIsIdentified);
					}
					//Set Explicite validation Rules
					setExplicitValidationRules(taggedValueMap, controlModel);
					//populate rules
					attributeProcessor.populateRules(userSelectedControlName, attributeInterface,
							controlModel);
					String[] ruleNamesString = new String[0];
					controlModel.setValidationRules(ruleNamesString);
					controlModel.setTempValidationRules(ruleNamesString);
				}
				else
				{
					String userSelectedControlName = ProcessorConstants.DEFAULT_SELECTED_CONTROL;
					controlInterface = deFactory.createTextField();
					((TextFieldInterface) controlInterface).setColumns(10);
					//Creating Text Control
					if (attributeTypeInformation instanceof StringAttributeTypeInformation)
					{
						controlModel.setDataType(ProcessorConstants.DATATYPE_STRING);
						String defaultValue = getDefaultValueTagValue(taggedValueMap);
						controlModel.setAttributeDefaultValue(defaultValue);
						String maxLen = getMaxLengthTagValue(taggedValueMap);
						controlModel.setAttributeSize(maxLen);
						attributeProcessor.populateStringAttributeInterface(
								(StringAttributeTypeInformation) attributeTypeInformation,
								controlModel);
						//Password
						String password = getPasswordTagValue(taggedValueMap);
						controlModel.setIsPassword(Boolean.parseBoolean(password));
						//URL
						String url = getUrlTagValue(taggedValueMap);
						controlModel.setIsUrl(Boolean.parseBoolean(url));

						//NoOfColumns
						String width = getDisplayWidthTagValue(taggedValueMap);
						controlModel.setColumns(Integer.parseInt(width.toString()));

						// Single/Multiline(Number of Lines) tagged Value
						setMultilineTaggedValue(taggedValueMap, controlModel);
						//PHI
						String strIsIdentified = getPHIAttributeTagValue(taggedValueMap);
						attributeProcessor.populateIsIdentifiedInfo(attributeInterface,
								strIsIdentified);

						if (controlModel.getLinesType() != null
								&& controlModel.getLinesType().equalsIgnoreCase(
										XMIConstants.MULTILINE))
						{
							controlInterface = controlProcessor.getMultiLineControl(
									controlInterface, controlModel);
						}
						else
						{
							controlInterface = controlProcessor.getTextControl(controlInterface,
									controlModel);
						}
						//Set Explicit validation Rules
						setExplicitValidationRules(taggedValueMap, controlModel);
						//populate rules
						attributeProcessor.populateRules(userSelectedControlName,
								attributeInterface, controlModel);
						String[] ruleNamesString = new String[0];
						controlModel.setValidationRules(ruleNamesString);
						controlModel.setTempValidationRules(ruleNamesString);
					}
					//Number Attribute
					else
					{
						controlModel.setDataType(ProcessorConstants.DATATYPE_NUMBER);

						String defaultValue = getDefaultValueTagValue(taggedValueMap);
						controlModel.setAttributeDefaultValue(defaultValue);

						Integer precision = getPrecisionTagValue(taggedValueMap,
								attributeTypeInformation);
						controlModel.setAttributeDecimalPlaces(precision.toString());

						//Set Explicit validation Rules.
						setExplicitValidationRules(taggedValueMap, controlModel);

						if (attributeTypeInformation instanceof LongAttributeTypeInformation)
						{
							attributeProcessor.populateLongAttributeInterface(attributeInterface,
									(LongAttributeTypeInformation) attributeTypeInformation,
									controlModel);
						}
						else if (attributeTypeInformation instanceof IntegerAttributeTypeInformation)
						{
							attributeProcessor.populateIntegerAttributeInterface(
									attributeInterface,
									(IntegerAttributeTypeInformation) attributeTypeInformation,
									controlModel);
						}
						else if (attributeTypeInformation instanceof FloatAttributeTypeInformation)
						{
							attributeProcessor.populateFloatAttributeInterface(attributeInterface,
									(FloatAttributeTypeInformation) attributeTypeInformation,
									controlModel);
						}
						else if (attributeTypeInformation instanceof DoubleAttributeTypeInformation)
						{
							attributeProcessor.populateDoubleAttributeInterface(attributeInterface,
									(DoubleAttributeTypeInformation) attributeTypeInformation,
									controlModel);
						}
						else if (attributeTypeInformation instanceof ShortAttributeTypeInformation)
						{
							attributeProcessor.populateShortAttributeInterface(attributeInterface,
									(ShortAttributeTypeInformation) attributeTypeInformation,
									controlModel);
						}

						//populate rules
						attributeProcessor.populateRules(userSelectedControlName,
								attributeInterface, controlModel);
						String[] ruleNamesString = new String[0];
						controlModel.setValidationRules(ruleNamesString);
						controlModel.setTempValidationRules(ruleNamesString);
					}
				}
			}
			else
			{
				return null;
			}
		}
		controlInterface.setName(abstractAttributeInterface.getName());
		controlInterface.setCaption(abstractAttributeInterface.getName());
		controlInterface.setBaseAbstractAttribute(abstractAttributeInterface);
		return controlInterface;
	}

	/**
	 * @param associationInterface
	 * @param controlInterface
	 * In case of linking association, this method adds the association display attributes.
	 */
	private void addAssociationDisplayAttributes(AssociationInterface associationInterface,
			Map<String, String> taggedValueMap, ControlInterface controlInterface)
	{
		EntityInterface targetEntity = associationInterface.getTargetEntity();
		DomainObjectFactory domainObjectFactory = DomainObjectFactory.getInstance();
		//		This method returns all attributes and not associations
		Collection<AttributeInterface> targetEntityAttrColl = targetEntity.getAttributeCollection();
		int seqNo = 1;
		String attributesInAssociationDropDownTagValue = taggedValueMap
				.get(XMIConstants.TAGGED_VALUE_ATTRIBUTES_IN_ASSOCIATION_DROP_DOWN);
		if (attributesInAssociationDropDownTagValue != null)
		{
			StringTokenizer stringTokenizer = new StringTokenizer(attributesInAssociationDropDownTagValue, ",");
			while (stringTokenizer.hasMoreTokens())
			{
				String attributeName = stringTokenizer.nextToken();
				for (AttributeInterface attr : targetEntityAttrColl)
				{
					if (attributeName.equals(attr.getName()))
					{
						AssociationDisplayAttributeInterface associationDisplayAttribute = domainObjectFactory
								.createAssociationDisplayAttribute();
						associationDisplayAttribute.setSequenceNumber(seqNo);
						associationDisplayAttribute.setAttribute(attr);
						//This method adds to the associationDisplayAttributeCollection
						((SelectControl) controlInterface)
								.addAssociationDisplayAttribute(associationDisplayAttribute);
						seqNo++;
					}
				}
			}
		}
	}

	/**
	 *
	 * @param taggedValueMap
	 * @param attributeInterface
	 * @param controlModel
	 * @throws DynamicExtensionsSystemException
	 */
	private void setExplicitValidationRules(Map<String, String> taggedValueMap,
			ControlsModel controlModel) throws DynamicExtensionsSystemException
	{
		Map<String, String> taggedValueRuleMap = new HashMap<String, String>();
		if (taggedValueMap != null)
		{
			Set<String> keySetForTaggedValue = taggedValueMap.keySet();
			//Grouping Rule Tagged Values
			for (String key : keySetForTaggedValue)
			{
				if (key.startsWith(XMIConstants.TAGGED_VALUE_RULE + XMIConstants.SEPARATOR))
				{
					taggedValueRuleMap.put(key, taggedValueMap.get(key));
				}
			}
			//Seting Rule Tagged Values to ControlsModel
			if (taggedValueRuleMap != null && !(taggedValueRuleMap.isEmpty()))
			{
				populateValidationRule(taggedValueRuleMap, controlModel);
			}
		}
	}

	/**
	 *
	 * @param taggedValueMapRule
	 * @param attributeInterface
	 * @param controlModel
	 * @throws DynamicExtensionsSystemException
	 */
	private void populateValidationRule(Map<String, String> taggedValueRuleMap,
			ControlsModel controlModel) throws DynamicExtensionsSystemException
	{
		ArrayList<String> ruleNames = new ArrayList<String>();
		String ruleName = "";
		int counter = 0;

		Set<String> keySetForRuleTaggedValueMap = taggedValueRuleMap.keySet();
		for (String key : keySetForRuleTaggedValueMap)
		{
			StringTokenizer stringTokenizer = new StringTokenizer(key, XMIConstants.SEPARATOR);
			int tokenNumber = 0;
			int count = stringTokenizer.countTokens();

			// Seting Rule Tagged Values and parameter values to ControlsModel.
			if (count <= 3)
			{
				while (stringTokenizer.hasMoreTokens())
				{
					tokenNumber++;
					String tokenName = stringTokenizer.nextToken();
					// Finding Rule name.
					if (tokenNumber == 2)
					{
						ruleName = tokenName;
					}
					// Setting Parameter values.
					if (tokenNumber == 3)
					{
						if (tokenName.equalsIgnoreCase("min"))
						{
							controlModel.setMin(taggedValueRuleMap.get(key));
							controlModel.setMinTemp(taggedValueRuleMap.get(key));
						}
						else if (tokenName.equalsIgnoreCase("max"))
						{
							controlModel.setMax(taggedValueRuleMap.get(key));
							controlModel.setMaxTemp(taggedValueRuleMap.get(key));
						}
					}
					if (!(ruleNames.contains(ruleName)))
					{
						ruleNames.add(ruleName);
					}
				}
			}
		}

		String[] ruleNamesString = new String[ruleNames.size()];
		for (String ruleStringName : ruleNames)
		{
			ruleNamesString[counter++] = ruleStringName;
		}

		controlModel.setValidationRules(ruleNamesString);
		controlModel.setTempValidationRules(ruleNamesString);
	}

	/**
	 * @param entity
	 * This method removes inherited attributes.
	 */

	protected void removeInheritedAttributes(EntityInterface entity,
			List duplicateAttributeCollection)
	{
		if (duplicateAttributeCollection != null)
		{
			entity.getAbstractAttributeCollection().removeAll(duplicateAttributeCollection);
		}
	}

	/**
	 * @param umlClasses
	 * This method creates all containers.
	 */
	protected void processPersistence(List<String> containerNames) throws Exception
	{
		//Collection<ContainerInterface> containerColl = new HashSet<ContainerInterface>();

		//		Set<String> entityIdKeySet = entityNameVsContainers.keySet();

		for (String containerName : containerNames)
		{
			//		For static models
			String temp = "";
			if (xmiConfigurationObject.isEntityGroupSystemGenerated() && !containerName.startsWith(XMIConstants.CATISSUE_PACKAGE))
			{
					temp = containerName;
					containerName = "";
					containerName = XMIConstants.CATISSUE_PACKAGE + temp;
			}
			List containerList = (ArrayList) entityNameVsContainers.get(containerName);
			if (containerList == null || containerList.size() < 1)
			{
				throw new DynamicExtensionsApplicationException("The container name "
						+ containerName + " does "
						+ "not match with the container name in the Model.");
			}
			ContainerInterface containerInterface = (ContainerInterface) containerList.get(0);
			mainContainerList.add(containerInterface);
		}

		EntityGroupManagerInterface entityManagerInterface = EntityGroupManager.getInstance();
		try
		{
			//	entityManagerInterface.persistEntityGroupWithAllContainers(entityGroup, mainContainerList);
			for (ContainerInterface container : mainContainerList)
			{
				entityGroup.addMainContainer(container);
			}
			if (xmiConfigurationObject.isEntityGroupSystemGenerated()
					|| !xmiConfigurationObject.isCreateTable())
			{//Static Model. Hence saving only metadata
				entityManagerInterface.persistEntityGroupMetadata(entityGroup);
			}
			else
			{//Dynamic model
				entityManagerInterface.persistEntityGroup(entityGroup);
			}
		}
		catch (DynamicExtensionsApplicationException e)
		{
			throw new DynamicExtensionsApplicationException(e.getMessage(), e);
		}
		catch (DynamicExtensionsSystemException e)
		{
			throw new DynamicExtensionsSystemException(e.getMessage(), e);
		}
	}

	/**
	 * @return
	 */
	public AssociationInterface createAssociation()
	{
		AssociationInterface associationInterface = DomainObjectFactory.getInstance()
				.createAssociation();
		return associationInterface;
	}

	/**
	 * @throws Exception
	 */
	protected void postProcessAssociation() throws Exception
	{
		addControlsForAssociation();
	}

}
