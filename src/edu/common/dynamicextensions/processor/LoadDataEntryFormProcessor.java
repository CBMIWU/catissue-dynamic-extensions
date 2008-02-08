
package edu.common.dynamicextensions.processor;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import edu.common.dynamicextensions.domaininterface.AbstractEntityInterface;
import edu.common.dynamicextensions.domaininterface.BaseAbstractAttributeInterface;
import edu.common.dynamicextensions.domaininterface.CategoryEntityInterface;
import edu.common.dynamicextensions.domaininterface.EntityInterface;
import edu.common.dynamicextensions.domaininterface.userinterface.ContainerInterface;
import edu.common.dynamicextensions.entitymanager.CategoryManager;
import edu.common.dynamicextensions.entitymanager.CategoryManagerInterface;
import edu.common.dynamicextensions.entitymanager.EntityManager;
import edu.common.dynamicextensions.entitymanager.EntityManagerInterface;
import edu.common.dynamicextensions.exception.DynamicExtensionsApplicationException;
import edu.common.dynamicextensions.exception.DynamicExtensionsSystemException;
import edu.common.dynamicextensions.ui.webui.actionform.DataEntryForm;
import edu.common.dynamicextensions.ui.webui.util.WebUIManagerConstants;
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
	public ContainerInterface loadDataEntryForm(AbstractActionForm actionForm,
			ContainerInterface containerInterface,
			Map<BaseAbstractAttributeInterface, Object> valueMap, String mode, String recordIdentifier)
			throws DynamicExtensionsSystemException, DynamicExtensionsApplicationException
	{
		DataEntryForm dataEntryForm = (DataEntryForm) actionForm;

		if (mode != null && mode.equalsIgnoreCase(WebUIManagerConstants.VIEW_MODE))
		{
			containerInterface.setMode(mode);
		}

		if (valueMap != null && !valueMap.isEmpty())
		{
			containerInterface.setContainerValueMap(valueMap);
		}

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
		if (recordIdentifier != null)
		{
			dataEntryForm.setRecordIdentifier(recordIdentifier);
		}
		else
		{
			dataEntryForm.setRecordIdentifier("");
		}
		return containerInterface;
	}

	/**
	 * 
	 * @param entityInterface
	 * @param recordIdentifier
	 * @return
	 * @throws NumberFormatException
	 * @throws DynamicExtensionsSystemException
	 * @throws DynamicExtensionsApplicationException
	 * @throws SQLException 
	 */
	public Map<BaseAbstractAttributeInterface, Object> getValueMapFromRecordId(
			AbstractEntityInterface entityInterface, String recordIdentifier) throws NumberFormatException,
			DynamicExtensionsSystemException, DynamicExtensionsApplicationException, SQLException
	{
		Map<BaseAbstractAttributeInterface, Object> recordMap = new HashMap<BaseAbstractAttributeInterface, Object>();
		if (recordIdentifier != null && !recordIdentifier.equals(""))
		{
			//Quic fix:
			if(entityInterface instanceof EntityInterface){
				EntityManagerInterface entityManagerInterface = EntityManager.getInstance();
				Map map= entityManagerInterface.getRecordById((EntityInterface)entityInterface, Long.valueOf(recordIdentifier));
				recordMap = map;
			}else{
			CategoryManagerInterface categoryManagerInterface = CategoryManager.getInstance();
			recordMap = categoryManagerInterface.getRecordById((CategoryEntityInterface)entityInterface, Long.valueOf(recordIdentifier));
			}

		}
		return recordMap;
	}
}
