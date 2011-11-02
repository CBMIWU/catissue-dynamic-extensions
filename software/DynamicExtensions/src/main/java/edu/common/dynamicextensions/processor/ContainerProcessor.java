
package edu.common.dynamicextensions.processor;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import edu.common.dynamicextensions.domain.DomainObjectFactory;
import edu.common.dynamicextensions.domaininterface.EntityGroupInterface;
import edu.common.dynamicextensions.domaininterface.EntityInterface;
import edu.common.dynamicextensions.domaininterface.userinterface.ContainerInterface;
import edu.common.dynamicextensions.entitymanager.EntityGroupManager;
import edu.common.dynamicextensions.entitymanager.EntityManager;
import edu.common.dynamicextensions.entitymanager.EntityManagerInterface;
import edu.common.dynamicextensions.exception.DynamicExtensionsApplicationException;
import edu.common.dynamicextensions.exception.DynamicExtensionsSystemException;
import edu.common.dynamicextensions.ui.interfaces.ContainerUIBeanInterface;
import edu.common.dynamicextensions.ui.util.Constants;
import edu.common.dynamicextensions.util.DynamicExtensionsUtility;
import edu.wustl.common.beans.NameValueBean;
import edu.wustl.common.util.Utility;

/**
 *<p>Title: ContainerProcessor</p>
 *<p>Description:  This class acts as a utility class which processes tne container.
 * 1. It creates a new container.
 * 2. Populates the containerInterface (Cache) object.
 * 3. Populates the information to UIBean taking form Cache.
 * This processor class is a POJO and not a framework specific class so it can be used by
 * all types of presentation layers.  </p>
 *@author Deepti Shelar
 *@version 1.0
 */
public class ContainerProcessor extends BaseDynamicExtensionsProcessor
{

	/**
	 * This is a singleton class so we have a protected constructor , We are providing getInstance method
	 * to return the ContainerProcessor's instance.
	 */
	protected ContainerProcessor()
	{
		super();
	}

	/**
	 * this method gets the new instance of the ControlProcessor to the caller.
	 * @return ControlProcessor ControlProcessor instance
	 */
	public static ContainerProcessor getInstance()
	{
		return new ContainerProcessor();
	}

	/**
	 * This method returns empty domain object of ContainerInterface.
	 * @return ContainerInterface
	 */
	public ContainerInterface createContainer()
	{
		return DomainObjectFactory.getInstance().createContainer();
	}

	/**
	 * This method populates the given ContainerInterface using the given ContainerUIBeanInterface.
	 * @param containerInterface : Instance of containerInterface which is populated using the informationInterface.
	 * @param containerUIBeanInterface : Instance of ContainerUIBeanInterface which is used to populate the containerInterface.
	 * @throws DynamicExtensionsApplicationException
	 * @throws DynamicExtensionsSystemException
	 */
	public void populateContainerInterface(ContainerInterface containerInterface,
			ContainerUIBeanInterface containerUIBeanInterface)
			throws DynamicExtensionsSystemException, DynamicExtensionsApplicationException
	{
		if (containerInterface != null && containerUIBeanInterface != null)
		{
			containerInterface.setButtonCss(containerUIBeanInterface.getButtonCss());
			containerInterface.setCaption(containerUIBeanInterface.getFormName());
			containerInterface.setMainTableCss(containerUIBeanInterface.getMainTableCss());
			//containerInterface.setRequiredFieldIndicatior(containerUIBeanInterface.getRequiredFieldIndicatior());
			//containerInterface.setRequiredFieldWarningMessage(containerUIBeanInterface.getRequiredFieldWarningMessage());
			containerInterface.setRequiredFieldIndicatior(Constants.REQUIRED_FIELD_INDICATOR);
			containerInterface
					.setRequiredFieldWarningMessage(Constants.REQUIRED_FIELD_WARNING_MESSAGE);
			containerInterface.setTitleCss(containerUIBeanInterface.getTitleCss());
			if (containerUIBeanInterface.getParentForm() == null
					|| containerUIBeanInterface.getParentForm().equals("")
					|| containerUIBeanInterface.getParentForm().equals("0"))
			{
				containerInterface.setBaseContainer(null);
			}
			//Added for bug 6068
			else
			{
				ContainerInterface parentContainer = DynamicExtensionsUtility
						.getContainerByIdentifier(containerUIBeanInterface.getParentForm());
				containerInterface.setBaseContainer(parentContainer);

			}
		}
	}

	public void populateContainer(ContainerInterface container,
			ContainerUIBeanInterface containerUIBean, EntityGroupInterface entityGroup)
			throws DynamicExtensionsSystemException, DynamicExtensionsApplicationException
	{
		if (container != null && containerUIBean != null)
		{
			container.setButtonCss(containerUIBean.getButtonCss());
			container.setCaption(containerUIBean.getFormName());
			container.setMainTableCss(containerUIBean.getMainTableCss());
			container.setRequiredFieldIndicatior(Constants.REQUIRED_FIELD_INDICATOR);
			container.setRequiredFieldWarningMessage(Constants.REQUIRED_FIELD_WARNING_MESSAGE);
			container.setTitleCss(containerUIBean.getTitleCss());

			if (containerUIBean.getParentForm() == null
					|| containerUIBean.getParentForm().equals("")
					|| containerUIBean.getParentForm().equals("0"))
			{
				container.setBaseContainer(null);
			}
			//Added for bug 6068
			else
			{
				ContainerInterface parentContainer = null;

				for (EntityInterface entity : entityGroup.getEntityCollection())
				{
					Collection<ContainerInterface> containerCollection = entity
							.getContainerCollection();
					for (ContainerInterface currentContainer : containerCollection)
					{
						if (currentContainer.getId() != null
								&& currentContainer.getId().toString().equals(
										containerUIBean.getParentForm()))
						{
							parentContainer = currentContainer;
						}
					}
				}
				container.setBaseContainer(parentContainer);
			}
		}
	}

	/**
	 * /**
	 * This method will populate the containerInformationInterface using the containerInterface so that the
	 * information of the Container can be shown on the user page using the EntityUIBeanInterface.
	 * @param containerInterface Instance of containerInterface from which to populate the informationInterface.
	 * @param containerUIBeanInterface Instance of containerInformationInterface which will be populated using
	 * the first parameter that is ContainerInterface.
	 * @throws DynamicExtensionsSystemException
	 */

	public void populateContainerUIBeanInterface(ContainerInterface containerInterface,
			ContainerUIBeanInterface containerUIBeanInterface, EntityGroupInterface entityGroup)
			throws DynamicExtensionsSystemException
	{
		NameValueBean formTobeRemoved = null;
		if (containerInterface != null && containerUIBeanInterface != null)
		{
			containerUIBeanInterface.setButtonCss(Utility.toString(containerInterface
					.getButtonCss()));
			//containerUIBeanInterface.setFormCaption(Utility.toString(containerInterface.getCaption()));
			containerUIBeanInterface.setFormName(containerInterface.getCaption());
			containerUIBeanInterface.setMainTableCss(Utility.toString(containerInterface
					.getMainTableCss()));
			containerUIBeanInterface.setRequiredFieldIndicatior(Utility.toString(containerInterface
					.getRequiredFieldIndicatior()));
			containerUIBeanInterface.setRequiredFieldWarningMessage(Utility
					.toString(containerInterface.getRequiredFieldWarningMessage()));
			containerUIBeanInterface
					.setTitleCss(Utility.toString(containerInterface.getTitleCss()));

			if (entityGroup.getId() == null)
			{
				containerUIBeanInterface.setFormList(new ArrayList<NameValueBean>());
			}
			else
			{
				// Restrict Inherit call from same form.

				List<NameValueBean> formList = getFormsList(entityGroup.getId());

				for (NameValueBean form : formList)
				{
					if (form.getName().equals(containerUIBeanInterface.getFormName()))
					{
						formTobeRemoved = form;
					}
				}
				formList.remove(formTobeRemoved);

				containerUIBeanInterface.setFormList(formList);

			}

			if (containerInterface.getBaseContainer() == null)
			{
				containerUIBeanInterface.setParentForm("0");
			}
			else
			{
				containerUIBeanInterface.setParentForm(containerInterface.getBaseContainer()
						.getId().toString());
			}
		}
	}

	/**
	 *
	 * @return
	 * @throws DynamicExtensionsSystemException
	 */
	public List<NameValueBean> getFormsList(Long groupId) throws DynamicExtensionsSystemException

	{
		EntityManagerInterface entityManager = EntityManager.getInstance();
		List<NameValueBean> containerList;
		if (groupId == null)
		{
			containerList = new ArrayList<NameValueBean>();
		}
		else
		{
			containerList = entityManager.getAllContainerBeansByEntityGroupId(groupId);
		}

		List<NameValueBean> formsList = new ArrayList<NameValueBean>();
		formsList.add(new NameValueBean("--select--", "0"));
		if (containerList != null && !containerList.isEmpty())
		{
			Collections.sort(containerList);
			formsList.addAll(containerList);
		}

		return formsList;
	}

	/**
	 *
	 * @param containerInterface : Container object
	 * @throws DynamicExtensionsApplicationException : Exception thrown by Entity Manager
	 * @throws DynamicExtensionsSystemException :  Exception thrown by Entity Manager
	 */
	public ContainerInterface saveContainer(ContainerInterface containerInterface)
			throws DynamicExtensionsApplicationException, DynamicExtensionsSystemException
	{
		//metadata creation needs to run incase of entity only
		EntityGroupInterface entityGroup = ((EntityInterface) containerInterface
				.getAbstractEntity()).getEntityGroup();
		entityGroup.addMainContainer(containerInterface);
		EntityGroupManager.getInstance().persistEntityGroup(entityGroup);
		return containerInterface;
	}
}
