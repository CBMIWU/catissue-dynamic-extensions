
package edu.common.dynamicextensions.processor;

/**
 * This processor class mainly helps the action class to call the related Object driven processors
 * to update the Actionforms by Retrieving  data form Cache.
 * @author deepti_shelar
 * @author chetan_patil
 * @version 2.0
 */
import java.util.Collection;
import java.util.Iterator;

import edu.common.dynamicextensions.domain.userinterface.ContainmentAssociationControl;
import edu.common.dynamicextensions.domaininterface.EntityGroupInterface;
import edu.common.dynamicextensions.domaininterface.EntityInterface;
import edu.common.dynamicextensions.domaininterface.userinterface.ContainerInterface;
import edu.common.dynamicextensions.domaininterface.userinterface.ControlInterface;
import edu.common.dynamicextensions.entitymanager.EntityManager;
import edu.common.dynamicextensions.entitymanager.EntityManagerInterface;
import edu.common.dynamicextensions.exception.DynamicExtensionsCacheException;
import edu.common.dynamicextensions.exception.DynamicExtensionsSystemException;
import edu.common.dynamicextensions.ui.interfaces.ContainerUIBeanInterface;
import edu.common.dynamicextensions.ui.interfaces.EntityUIBeanInterface;
import edu.common.dynamicextensions.ui.webui.actionform.FormDefinitionForm;
import edu.common.dynamicextensions.util.AssociationTreeObject;
import edu.common.dynamicextensions.util.DynamicExtensionsUtility;
import edu.common.dynamicextensions.util.global.DEConstants;

public class LoadFormDefinitionProcessor extends BaseDynamicExtensionsProcessor
{

	/**
	 * Protected constructor for LoadFormDefinitionProcessor
	 */
	protected LoadFormDefinitionProcessor()
	{
		super();
	}

	/**
	 * This method returns the new instance of the LoadFormDefinitionProcessor.
	 * @return the new instance of the LoadFormDefinitionProcessor.
	 */
	public static LoadFormDefinitionProcessor getInstance()
	{
		return new LoadFormDefinitionProcessor();
	}

	/**
	 * A call to EntityProcessor will update the actionform with the data from cacheObject.
	 * @param entityInterface : Entity Interface Domain Object
	 * @param entityUIBeanInterface : UI Bean object containing entity information added by user on UI
	 */
	private void populateEntityInformation(EntityInterface entityInterface,
			EntityUIBeanInterface entityUIBeanInterface)
	{
		if (entityInterface != null)
		{
			EntityProcessor entityProcessor = EntityProcessor.getInstance();
			entityProcessor.populateEntityUIBeanInterface(entityInterface, entityUIBeanInterface);
		}
	}

	/**
	 * A call to ContainerProcessor will update the actionform with the data from cacheObject.
	 * @param containerInterfaceObject : Container interface
	 * @param containerUIBeanInterface : container UI Bean Interface object containing information added by user
	 * @throws DynamicExtensionsSystemException
	 */
	public void populateContainerInformation(ContainerInterface containerInterfaceObject,
			ContainerUIBeanInterface containerUIBeanInterface, EntityGroupInterface entityGroup)
			throws DynamicExtensionsSystemException
	{
		ContainerInterface containerInterface = containerInterfaceObject;
		ContainerProcessor containerProcessor = ContainerProcessor.getInstance();
		if (containerInterface == null)
		{
			containerInterface = containerProcessor.createContainer();
		}
		populateEntityInformation((EntityInterface) containerInterface.getAbstractEntity(),
				((EntityUIBeanInterface) containerUIBeanInterface));
		containerProcessor.populateContainerUIBeanInterface(containerInterface,
				containerUIBeanInterface, entityGroup);
	}

	/**
	 * This method returns the populated Container instance form the database having corresponding Container identifier.
	 * @param containerIdentifier the Identifier of the Container to be fetched from database.
	 * @throws DynamicExtensionsSystemException if System level or run-time exception occurs.
	 * @return the populated Container instance.
	 * @throws NumberFormatException
	 * @throws DynamicExtensionsCacheException
	 */
	public ContainerInterface getContainerForEditing(String containerIdentifier)
			throws DynamicExtensionsSystemException, DynamicExtensionsCacheException, NumberFormatException
	{
		return DynamicExtensionsUtility.getContainerByIdentifier(containerIdentifier);
	}

	/**
	 * @param container
	 * @param formDefinitionForm
	 * @throws DynamicExtensionsSystemException
	 */
	public void initializeSubFormAttributes(FormDefinitionForm formDefinitionForm,
			EntityGroupInterface entityGroup) throws DynamicExtensionsSystemException
	{
		formDefinitionForm.setDefinedEntitiesTreeXML(getXMLForDefinedEntities(entityGroup));

		ContainerProcessor containerProcessor = ContainerProcessor.getInstance();
		formDefinitionForm.setFormList(containerProcessor.getFormsList(entityGroup.getId()));

		formDefinitionForm.setParentForm("0");
	}

	/**
	 * @return
	 * @throws DynamicExtensionsSystemException
	 */
	private String getXMLForDefinedEntities(EntityGroupInterface entityGroup)
			throws DynamicExtensionsSystemException
	{
		StringBuffer definedEntitiesXML = new StringBuffer(71);
		EntityManagerInterface entityManager = EntityManager.getInstance();
		Collection<AssociationTreeObject> associationsCollection = null;
		if (entityGroup != null && entityGroup.getId() != null)
		{
			associationsCollection = entityManager.getAssociationTree(entityGroup.getId());
		}
		definedEntitiesXML.append("<?xml version='1.0' encoding='iso-8859-1'?> <tree id='0'>");
		//Special handling for grp : assign id as "Group_ number

		if (associationsCollection != null)
		{
			Iterator<AssociationTreeObject> assocnIter = associationsCollection.iterator();
			while (assocnIter.hasNext())
			{
				AssociationTreeObject associationObj = assocnIter.next();
				if (associationObj != null)
				{
					String label = associationObj.getLabel();
					String identifier = DEConstants.GROUP_PREFIX + associationObj.getId();
					definedEntitiesXML.append(getXMLNode(identifier, label, false, false));
					definedEntitiesXML.append(getAssociationTreeXML(associationObj
							.getAssociationTreeObjectCollection()));
					definedEntitiesXML.append("</item>");
				}
			}
		}
		definedEntitiesXML.append("</tree>");
		return definedEntitiesXML.toString();
	}

	/**
	 * @param text
	 */
	private String getXMLNode(String identifier, String text, boolean showSelected,
			boolean showExpanded)
	{
		StringBuffer xmlNode = new StringBuffer();
		if (text != null)
		{
			xmlNode.append("<item text='");
			xmlNode.append(text);
			xmlNode.append("' ");
			if (identifier == null) //if id is null put name as id.
			{
				xmlNode.append(" id='");
				xmlNode.append(text);
				xmlNode.append("' ");
			}
			else
			{
				xmlNode.append(" id='");
				xmlNode.append(identifier);
				xmlNode.append("' ");
			}
			if (showExpanded)
			{
				xmlNode.append(" open='1' ");
			}
			if (showSelected)
			{
				xmlNode.append(" select='1'");
			}
			xmlNode.append('>');
		}
		return xmlNode.toString();
	}

	/**
	 * @return
	 * @throws DynamicExtensionsSystemException
	 */
	private String getAssociationTreeXML(Collection<AssociationTreeObject> associationsCollection)
			throws DynamicExtensionsSystemException
	{
		StringBuffer associationTreeXML = new StringBuffer();
		if (associationsCollection != null)
		{
			Iterator<AssociationTreeObject> iterator = associationsCollection.iterator();
			while (iterator.hasNext())
			{
				AssociationTreeObject associationObj = iterator.next();
				if (associationObj != null)
				{
					Long identifier = associationObj.getId();
					String label = associationObj.getLabel();
					if ((identifier != null) && (label != null))
					{
						associationTreeXML.append(getXMLNode(identifier.toString(), label, false,
								false));
						associationTreeXML.append(getAssociationTreeXML(associationObj
								.getAssociationTreeObjectCollection()));
						associationTreeXML.append("</item>");
					}
				}
			}
		}
		return associationTreeXML.toString();
	}

	/**
	 * @param formDefinitionForm
	 * @throws DynamicExtensionsSystemException
	 */
	public void initializeFormAttributes(EntityGroupInterface entityGroup,
			ContainerInterface container, String currentContainerName,
			FormDefinitionForm formDefinitionForm) throws DynamicExtensionsSystemException
	{
		String groupName = getGroupName(entityGroup);
		formDefinitionForm.setGroupName(groupName);
		boolean addNewNode = true;
		if ((formDefinitionForm.getOperationMode() != null)
				&& (formDefinitionForm.getOperationMode().equals(DEConstants.EDIT_FORM)))
		{
			addNewNode = false;
		}
		formDefinitionForm.setCurrentEntityTreeXML(getXMLForCurrentEntity(container,
				currentContainerName, addNewNode));
		//formDefinitionForm.setTreeData(getEntityTree(request, addNewNode));
		formDefinitionForm.setCreateAs(ProcessorConstants.DEFAULT_FORM_CREATEAS);

		if ((formDefinitionForm.getViewAs() == null) || (formDefinitionForm.getViewAs().equals("")))
		{
			formDefinitionForm.setViewAs(ProcessorConstants.DEFAULT_FORM_VIEWAS);
		}
		if (formDefinitionForm.getDefinedEntitiesTreeXML() == null)
		{
			formDefinitionForm.setDefinedEntitiesTreeXML(getXMLForDefinedEntities(entityGroup));
		}
	}

	/**
	 * @param request
	 * @param addNewNode
	 * @return
	 */
	private String getXMLForCurrentEntity(ContainerInterface container,
			String currentContainerName, boolean addNewNode)
	{
		StringBuffer currentEntityXML = new StringBuffer(64);
		//ContainerInterface container = (ContainerInterface)CacheManager.getObjectFromCache(request, Constants.CONTAINER_INTERFACE);
		//String currentContainerName = (String) CacheManager.getObjectFromCache(request, Constants.CURRENT_CONTAINER_NAME);
		currentEntityXML.append("<?xml version='1.0' encoding='iso-8859-1'?> <tree id='0'>");
		currentEntityXML.append(getNodeForContainer(container, currentContainerName, addNewNode,
				true));
		currentEntityXML.append("</tree>");
		return currentEntityXML.toString();
	}

	/**
	 * @param container
	 * @param currentContainerName
	 * @param addNewNode
	 * @return
	 */
	private String getNodeForContainer(ContainerInterface container, String currentContainerName,
			boolean addNewNode, boolean showExpanded)
	{
		StringBuffer xmlNodeForContainer = new StringBuffer();
		if (container == null)
		{
			//Add new form node to main container node
			xmlNodeForContainer.append(getNewEntityNode());
		}
		else
		{
			//Entity tree will always be accessed with the container name
			String containerName = container.getCaption();

			xmlNodeForContainer.append(getXMLNode(null, containerName, false, showExpanded));

			Collection<ControlInterface> controlsCollection = container.getControlCollection();
			if (controlsCollection != null)
			{
				for (ControlInterface control : controlsCollection)
				{
					if ((control != null) && (control instanceof ContainmentAssociationControl))
					{
						xmlNodeForContainer.append(getNodeForContainer(
								((ContainmentAssociationControl) control).getContainer(),
								currentContainerName, addNewNode, showExpanded));
					}
				}
			}
			if ((containerName != null) && (containerName.equals(currentContainerName))
					&& addNewNode)
			{
				getNewEntityNode();
			}
			xmlNodeForContainer.append("</item>");
		}
		return xmlNodeForContainer.toString();
	}

	/**
	 *
	 */
	private String getNewEntityNode()
	{
		StringBuffer xmlNodeForNewEntity = new StringBuffer(
				getXMLNode(null, "New Form", true, true));
		xmlNodeForNewEntity.append("</item>");
		return xmlNodeForNewEntity.toString();
	}

	/**
	 * @param request
	 * @return
	 */
	private String getGroupName(EntityGroupInterface entityGroup)
	{
		String groupName;
		//Get group object from cache and return it
		if (entityGroup == null)
		{
			groupName = "";
		}
		else
		{
			groupName = entityGroup.getName();
		}
		return groupName;
	}

}
