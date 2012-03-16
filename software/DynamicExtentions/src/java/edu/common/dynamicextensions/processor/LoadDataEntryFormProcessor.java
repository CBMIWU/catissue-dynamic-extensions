
package edu.common.dynamicextensions.processor;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import edu.common.dynamicextensions.domaininterface.AbstractEntityInterface;
import edu.common.dynamicextensions.domaininterface.BaseAbstractAttributeInterface;
import edu.common.dynamicextensions.domaininterface.CategoryEntityInterface;
import edu.common.dynamicextensions.domaininterface.EntityInterface;
import edu.common.dynamicextensions.domaininterface.userinterface.AbstractContainmentControlInterface;
import edu.common.dynamicextensions.domaininterface.userinterface.ContainerInterface;
import edu.common.dynamicextensions.domaininterface.userinterface.ControlInterface;
import edu.common.dynamicextensions.entitymanager.CategoryManager;
import edu.common.dynamicextensions.entitymanager.CategoryManagerInterface;
import edu.common.dynamicextensions.entitymanager.EntityManager;
import edu.common.dynamicextensions.entitymanager.EntityManagerInterface;
import edu.common.dynamicextensions.exception.DynamicExtensionsApplicationException;
import edu.common.dynamicextensions.exception.DynamicExtensionsSystemException;
import edu.common.dynamicextensions.ui.webui.actionform.DataEntryForm;
import edu.common.dynamicextensions.util.DynamicExtensionsUtility;
import edu.wustl.common.actionForm.AbstractActionForm;

/**
 * @author sujay_narkar
 * @author chetan_patil
 *
 */
public class LoadDataEntryFormProcessor
{

	/**
	 * Empty Constructor
	 */
	protected LoadDataEntryFormProcessor()
	{
		// TODO Auto-generated constructor stub
	}

	/**
	 * This method gets the new instance of the entity processor to the caller.
	 * @return EntityProcessor EntityProcessor instance
	 */
	public static LoadDataEntryFormProcessor getInstance()
	{
		return new LoadDataEntryFormProcessor();
	}

	/**
	 *
	 * @param actionForm
	 * @param containerInterface
	 * @param recordIdentifier
	 * @throws DynamicExtensionsSystemException DynamicExtensionsSystemException
	 * @throws DynamicExtensionsApplicationException DynamicExtensionsApplicationException
	 */
	public ContainerInterface updateFormBean(AbstractActionForm actionForm,
			ContainerInterface containerInterface,
			Map<BaseAbstractAttributeInterface, Object> valueMap,
			String recordIdentifier) throws DynamicExtensionsSystemException,
			DynamicExtensionsApplicationException
	{
		DataEntryForm dataEntryForm = (DataEntryForm) actionForm;

		containerInterface.setMode(dataEntryForm.getMode());
		if (valueMap != null && !valueMap.isEmpty())
		{
			cleanContainer(containerInterface);
			containerInterface.setContainerValueMap(valueMap);
		}
		List processedContainersList = new ArrayList<ContainerInterface>();
		DynamicExtensionsUtility.setAllInContextContainers(containerInterface,
				processedContainersList);

		dataEntryForm.setContainerInterface(containerInterface);

		if (dataEntryForm.getErrorList() == null)
		{
			List<String> errorList = new ArrayList<String>();
			dataEntryForm.setErrorList(errorList);
		}
		if (dataEntryForm.getShowFormPreview() == null)
		{
			dataEntryForm.setShowFormPreview("");
		}
		if (recordIdentifier == null)
		{
			dataEntryForm.setRecordIdentifier("");
		}
		else
		{
			dataEntryForm.setRecordIdentifier(recordIdentifier);
		}
		return containerInterface;
	}

	/**
	 * This method clears the value maps attached to the child containers
	 * @param containerInterface container object
	 */
	private void cleanContainer(ContainerInterface containerInterface)
	{
		for (ControlInterface controlInterface : containerInterface.getControlCollection())
		{
			if (controlInterface instanceof AbstractContainmentControlInterface)
			{
				((AbstractContainmentControlInterface) controlInterface)
						.getContainer()
						.setContainerValueMap(new HashMap<BaseAbstractAttributeInterface, Object>());
			}
		}
	}

	/**
	 *
	 * @param entityInterface
	 * @param recordIdentifier
	 * @return
	 * @throws NumberFormatException
	 * @throws DynamicExtensionsSystemException
	 * @throws DynamicExtensionsApplicationException
	 */
	public static Map<BaseAbstractAttributeInterface, Object> getValueMapFromRecordId(
			AbstractEntityInterface entityInterface, String recordIdentifier)
			throws DynamicExtensionsSystemException, DynamicExtensionsApplicationException
	{
		Map<BaseAbstractAttributeInterface, Object> recordMap = new HashMap<BaseAbstractAttributeInterface, Object>();
		if (recordIdentifier != null && !recordIdentifier.equals(""))
		{
			//Quick fix:
			if (entityInterface instanceof EntityInterface)
			{
				EntityManagerInterface entityManagerInterface = EntityManager.getInstance();
				Map map = entityManagerInterface.getRecordById((EntityInterface) entityInterface,
						Long.valueOf(recordIdentifier));
				recordMap = map;
			}
			else
			{
				CategoryManagerInterface categoryManagerInterface = CategoryManager.getInstance();
				CategoryEntityInterface categoryEntityInterface = (CategoryEntityInterface) entityInterface;
				Long recordId = categoryManagerInterface
						.getRootCategoryEntityRecordIdByEntityRecordId(Long
								.valueOf(recordIdentifier), categoryEntityInterface
								.getTableProperties().getName());
				recordMap = categoryManagerInterface.getRecordById(categoryEntityInterface,
						recordId);
			}
		}else
		{
			for(ControlInterface controlInterface:
				((ContainerInterface)entityInterface.getContainerCollection().toArray()[0]).getAllControls())
			{
				if(controlInterface instanceof AbstractContainmentControlInterface)
				{
					recordMap.put(controlInterface.getBaseAbstractAttribute(),
							new ArrayList<Map<BaseAbstractAttributeInterface, Object>>());
				}
			}
		}

		return recordMap;
	}
}