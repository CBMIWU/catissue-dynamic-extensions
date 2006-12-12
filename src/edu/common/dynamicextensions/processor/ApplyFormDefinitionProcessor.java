
package edu.common.dynamicextensions.processor;

/**
 * This processor class mainly helps the action class to call the related Object driven processors 
 * to update the Actionforms by retriving data form Cache.
 * @author deepti_shelar
 */
import edu.common.dynamicextensions.domain.DomainObjectFactory;
import edu.common.dynamicextensions.domaininterface.AssociationInterface;
import edu.common.dynamicextensions.domaininterface.EntityGroupInterface;
import edu.common.dynamicextensions.domaininterface.EntityInterface;
import edu.common.dynamicextensions.domaininterface.RoleInterface;
import edu.common.dynamicextensions.domaininterface.userinterface.ContainerInterface;
import edu.common.dynamicextensions.entitymanager.EntityManager;
import edu.common.dynamicextensions.entitymanager.EntityManagerInterface;
import edu.common.dynamicextensions.exception.DynamicExtensionsApplicationException;
import edu.common.dynamicextensions.exception.DynamicExtensionsSystemException;
import edu.common.dynamicextensions.ui.interfaces.AbstractAttributeUIBeanInterface;
import edu.common.dynamicextensions.ui.webui.actionform.FormDefinitionForm;
import edu.common.dynamicextensions.util.DynamicExtensionsUtility;
import edu.common.dynamicextensions.util.global.Constants;
import edu.common.dynamicextensions.util.global.Constants.AssociationDirection;
import edu.common.dynamicextensions.util.global.Constants.AssociationType;
import edu.common.dynamicextensions.util.global.Constants.Cardinality;

public class ApplyFormDefinitionProcessor extends BaseDynamicExtensionsProcessor
{
	/**
	 * Default Constructor.
	 */
	protected ApplyFormDefinitionProcessor()
	{
	}

	/**
	 * Returns the instance of ApplyFormDefinitionProcessor.
	 * @return ApplyFormDefinitionProcessor
	 */
	public static ApplyFormDefinitionProcessor getInstance()
	{
		return new ApplyFormDefinitionProcessor();
	}

	/**
	 * This method creates a Container if not present in cache. Then it will call to ContainerProcessor will
	 * populate this Object with the data from actionform.Then EntityProcessor's methods will be called to either create and Populate
	 * or create and save the entity, Then finally this entity is added to the container. 
	 * @param containerInterface : Container object 
	 * @param actionForm : Form object
	 * @param isActionSave : flag stating whether the object is to be saved to DB
	 * @return ContainerInterface : Container object
	 * @throws DynamicExtensionsApplicationException :Exception thrown by Entity Manager 
	 * @throws DynamicExtensionsSystemException :Exception thrown by Entity Manager
	 */
	public ContainerInterface addEntityToContainer(ContainerInterface containerInterface, FormDefinitionForm actionForm,
			boolean isActionSave,EntityGroupInterface entityGroupInterface)
			throws DynamicExtensionsSystemException, DynamicExtensionsApplicationException
	{
		String operationMode = actionForm.getOperationMode();
		if((operationMode!=null)&&(operationMode.equals(Constants.ADD_SUB_FORM_OPR)))
		{
			addSubFormToEntity(containerInterface,actionForm);
			return containerInterface;
		}
		
		ContainerProcessor containerProcessor = ContainerProcessor.getInstance();
		if (containerInterface == null)
		{
			containerInterface = containerProcessor.createContainer();
		}
		
		containerProcessor.populateContainerInterface(containerInterface, actionForm);
		
		//Add entity
		EntityProcessor entityProcessor = EntityProcessor.getInstance();
		EntityInterface entityInterface = containerInterface.getEntity();
		if (entityInterface == null)
		{
			entityInterface = entityProcessor.createAndPopulateEntity(actionForm);
		}
		else
		{
			entityProcessor.populateEntity(actionForm, entityInterface);
		}
		containerInterface.setEntity(entityInterface);
		
		if(entityGroupInterface!=null)
		{
			associateEntityToGroup(entityGroupInterface,containerInterface.getEntity());
		}
		if (isActionSave)
		{
			containerProcessor.saveContainer(containerInterface);
		}
		else
		{
			containerProcessor.populateContainerInterface(containerInterface, actionForm);
		}
		return containerInterface;
	}

	/**
	 * @param formDefinitionForm 
	 * @param containerInterface 
	 * @throws DynamicExtensionsApplicationException 
	 * @throws DynamicExtensionsSystemException 
	 * 
	 */
	private void addSubFormToEntity(ContainerInterface containerInterface, FormDefinitionForm formDefinitionForm) throws DynamicExtensionsSystemException, DynamicExtensionsApplicationException
	{
		if((formDefinitionForm!=null)&&(containerInterface!=null))
		{
			String createAs = formDefinitionForm.getCreateAs();
			EntityInterface targetEntity = null;
			
			if((createAs!=null)&&(createAs.equals(ProcessorConstants.CREATE_FROM_EXISTING)))
			{
				targetEntity = getTargetEntity(formDefinitionForm.getSelectedObjectId());
			}
			EntityInterface sourceEntity = containerInterface.getEntity();
			if((sourceEntity!=null)&&(targetEntity!=null))
			{
				String viewAs = formDefinitionForm.getViewAs();
				
				if((viewAs!=null)&&(viewAs.equals(ProcessorConstants.VIEW_AS_FORM)))
				{
					associateEntity(AssociationType.CONTAINTMENT,sourceEntity,targetEntity,Cardinality.ONE,Cardinality.ONE);	
				}
				else if((viewAs!=null)&&(viewAs.equals(ProcessorConstants.VIEW_AS_SPREADSHEET)))
				{
					associateEntity(AssociationType.CONTAINTMENT,sourceEntity,targetEntity,Cardinality.ONE,Cardinality.MANY);				
				}
			}
		}
		
	}
	/**
	 * @param containtment
	 * @param sourceEntity
	 * @param targetEntity
	 * @param one
	 * @param one2
	 */
	private void associateEntity(AssociationType associationType, EntityInterface sourceEntity, EntityInterface targetEntity, Cardinality sourceCardinality, Cardinality targetCardinality)
	{
		AssociationInterface association = DomainObjectFactory.getInstance().createAssociation();
		association.setTargetEntity(targetEntity);
		association.setAssociationDirection(AssociationDirection.SRC_DESTINATION);
		association.setName(targetEntity.getName());
		association.setSourceRole(getRole(associationType,null,
				Cardinality.ONE, sourceCardinality));
		association.setTargetRole(getRole(associationType,targetEntity.getName() , Cardinality.ONE,
					targetCardinality));
		sourceEntity.addAssociation(association);
	}

	/**
	 * @param selectedObjectId
	 * @return
	 * @throws DynamicExtensionsApplicationException 
	 * @throws DynamicExtensionsSystemException 
	 */
	private EntityInterface getTargetEntity(String targetContainerId) throws DynamicExtensionsSystemException, DynamicExtensionsApplicationException
	{
		EntityInterface targetEntity = null;
		if((targetContainerId!=null)&&(!targetContainerId.trim().equals("")))
		{
			//EntityManagerInterface entityManager = EntityManager.getInstance();
			ContainerInterface targetContainer = DynamicExtensionsUtility.getContainerByIdentifier(targetContainerId);
			if(targetContainer!=null)
			{
				targetEntity = targetContainer.getEntity();
			}
		}
		return targetEntity;
	}

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
	 * @param entityGroup : Entity Group containing entity
	 * @param entity : Entity to be associated
	 */
	public void associateEntityToGroup(EntityGroupInterface entityGroup, EntityInterface entity)
	{
		if((entityGroup!=null)&&(entity!=null))
		{
			entity.removeAllEntityGroups();
			
			entityGroup.addEntity(entity);
			entity.addEntityGroupInterface(entityGroup);
		}
	}
}
