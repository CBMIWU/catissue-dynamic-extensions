
package edu.common.dynamicextensions.ui.webui.util;

import java.util.Collection;

import javax.servlet.http.HttpServletRequest;

import edu.common.dynamicextensions.domaininterface.userinterface.ContainerInterface;
import edu.common.dynamicextensions.domaininterface.userinterface.ControlInterface;
import edu.common.dynamicextensions.util.global.DEConstants;

/**
 * This class acts as an interface to the external systems.The dynamic extensions can be delivered
 * as a separate war.
 * This class provides URLs for various action classes as well as parameter names.Those parameters can be 
 * send along with query String.  
 * @author sujay_narkar
 *
 */

public class WebUIManager implements WebUIManagerConstants
{

	/**
	 * The URL for action class which creates container.
	 * @return
	 */
	public static String getCreateContainerURL()
	{
		return CREATE_CONTAINER_URL;
	}

	/**
	 * The parameter to be set in the request which specifies the callback URL.
	 * @return
	 */
	public static String getCallbackURLParamName()
	{
		return CALLBACK_URL_PARAM_NAME;
	}

	/**
	 * The URL for action class which returns the different objects in the JESSON format 
	 * depending on operation.
	 * @return
	 */
	public static String getDynamicExtensionsInterfaceActionURL()
	{
		return DYNAMIC_EXTENSIONS_INTERFACE_ACTION_URL;
	}

	/**
	 * The parameter to be set in the request which specifies the operation for DynamicExtensionsInterfaceAction
	 * @return
	 */
	public static String getDynamicExtenionsInterfaceActionParamName()
	{
		return DYNAMIC_EXTENSIONS_INTERFACE_ACTION_PARAM_NAME;
	}

	/**
	 * 
	 *The URL for action class which displays the UI for record insertion.
	 */
	public static String getLoadDataEntryFormActionURL()
	{
		return LOAD_DATA_ENTRY_FORM_ACTION_URL;
	}

	/**
	 * The parameter to be set in the request which specifies the containerIdentifier for LoadDataEntryFormAction. 
	 * @return
	 */
	public static String getContainerIdentifierParameterName()
	{
		return CONATINER_IDENTIFIER_PARAMETER_NAME;
	}

	/**
	 * The parameter to be set in the request which specifies the record id for LoadDataEntryFormAction.
	 *
	 */
	public static String getRecordIdentifierParameterName()
	{
		return RECORD_IDENTIFIER_PARAMETER_NAME;
	}

	/**
	 * The parameter to be set in the request which specifies status of the operation 
	 *
	 */
	public static String getOperationStatusParameterName()
	{
		return OPERATION_STATUS_PARAMETER_NAME;
	}

	/**
	 * The parameter to be set in the request which specifies user id
	 *
	 */
	public static String getUserIdParameterName()
	{
		return USER_ID;
	}

	/** 
	 * returns the current container whose data/controls are to be displayed
	 *  
	 * @param controlsForm
	 * @return
	 */
	public static ContainerInterface getCurrentContainer(HttpServletRequest request)
	{
		//If the current container name is not null, get the container for corresponding name from cache
		//if null, return default container from cache.
		ContainerInterface currentContainer = null;
		String currentContainerName = (String) CacheManager.getObjectFromCache(request,
				DEConstants.CURRENT_CONTAINER_NAME);
		if ((currentContainerName != null) && (!currentContainerName.trim().equals("")))
		{
			//container for current container name
			currentContainer = (ContainerInterface) CacheManager.getObjectFromCache(request,
					currentContainerName);
		}
		else
		{
			//return default container
			currentContainer = (ContainerInterface) CacheManager.getObjectFromCache(request,
					DEConstants.CONTAINER_INTERFACE);
		}
		return currentContainer;
	}

	/**
	 * @param container
	 * @return
	 */
	public static Integer getSequenceNumberForNextControl(ContainerInterface container)
	{
		Integer nextSequenceNumber = null;
		Collection<ControlInterface> controlCollection = container.getControlCollection();
		if (controlCollection != null)
		{
			int noOfControls = controlCollection.size();
			nextSequenceNumber = Integer.valueOf(noOfControls + 1);
		}
		return nextSequenceNumber;
	}

	
	/**
	 * @param url DE form URL
	 * @param paramName used by DE
	 * @param value 
	 * @return
	 */
	public static String appendParameter(String url,String paramName,String value)
	{
		url = url +"&%s=%s";
		return String.format(url, paramName,value);
	}
	
	
}