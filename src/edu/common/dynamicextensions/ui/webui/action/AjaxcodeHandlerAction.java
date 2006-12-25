/*
 * Created on Dec 19, 2006
 * @author
 *
 */
package edu.common.dynamicextensions.ui.webui.action;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;
import java.util.Map;
import java.util.Stack;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import edu.common.dynamicextensions.domain.userinterface.ContainmentAssociationControl;
import edu.common.dynamicextensions.domaininterface.AbstractAttributeInterface;
import edu.common.dynamicextensions.domaininterface.AssociationInterface;
import edu.common.dynamicextensions.domaininterface.EntityGroupInterface;
import edu.common.dynamicextensions.domaininterface.EntityInterface;
import edu.common.dynamicextensions.domaininterface.userinterface.ContainerInterface;
import edu.common.dynamicextensions.exception.DynamicExtensionsApplicationException;
import edu.common.dynamicextensions.exception.DynamicExtensionsSystemException;
import edu.common.dynamicextensions.processor.GroupProcessor;
import edu.common.dynamicextensions.ui.util.SemanticPropertyBuilderUtil;
import edu.common.dynamicextensions.ui.webui.util.CacheManager;
import edu.common.dynamicextensions.ui.webui.util.UserInterfaceiUtility;
import edu.common.dynamicextensions.util.global.Constants;

/**
 * @author preeti_munot
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public class AjaxcodeHandlerAction extends BaseDynamicExtensionsAction
{
	/**
	 * @param mapping ActionMapping mapping
	 * @param form ActionForm form
	 * @param  request HttpServletRequest request
	 * @param response HttpServletResponse response
	 * @return ActionForward forward to next action
	 * @throws DynamicExtensionsApplicationException 
	 */
	public ActionForward execute(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response)
	throws DynamicExtensionsApplicationException
	{
		String returnXML = null;
		try
		{
			String operation = request.getParameter("ajaxOperation");
			if(operation!=null)
			{
				if(operation.trim().equals("selectFormNameFromTree"))
				{
					String selectedFormName = request.getParameter("selectedFormName");
					if (selectedFormName != null) 
					{
						returnXML = getSelectedFormDetails(request,selectedFormName);
					}
				}
				else if(operation.trim().equals("selectGroup"))
				{
					String selectedGroupName = request.getParameter("selectedGroupName");
					if (selectedGroupName != null) 
					{
						returnXML = getSelectedGroupDetails(request,selectedGroupName);
					}
				} 
				else if (operation.trim().equals("deleteRowsForContainment")) {
					String deletedRowIds  = request.getParameter("deletedRowIds");
					String containerId  = request.getParameter("containerId");
					returnXML = deleteRowsForContainment(request,deletedRowIds,containerId);
				}
				
			}
			sendResponse(returnXML, response);
			return null;
		}
		catch (Exception e)
		{
			String actionForwardString = catchException(e, request);
			if((actionForwardString==null)||(actionForwardString.equals("")))
			{
				return mapping.getInputForward(); 
			}
			return (mapping.findForward(actionForwardString));
		}
	}

	/**
	 * @param request
	 * @param deletedRowIds
	 * @param childContainerId
	 * @return
	 */
	private String deleteRowsForContainment(HttpServletRequest request, String deletedRowIds, String childContainerId)
	{
		Stack containerStack = (Stack) CacheManager.getObjectFromCache(request,
				Constants.CONTAINER_STACK);
		Stack valueMapStack = (Stack) CacheManager.getObjectFromCache(request,
				Constants.VALUE_MAP_STACK);
		
		Map<AbstractAttributeInterface, Object> valueMap = (Map<AbstractAttributeInterface, Object>) valueMapStack.peek();
		ContainerInterface containerInterface = (ContainerInterface) containerStack.peek();

		ContainmentAssociationControl associationControl = UserInterfaceiUtility.getAssociationControl(
				containerInterface, childContainerId);
		
		AssociationInterface association = (AssociationInterface) associationControl.getAbstractAttribute();
		
		List<Map<AbstractAttributeInterface, Object>> associationValueMapList = (List<Map<AbstractAttributeInterface, Object>>) valueMap.get(association);

		String[] deletedRows = deletedRowIds.split(",");
		
		for(int i=0;i<deletedRows.length; i++) {
			int removeIndex = Integer.valueOf(deletedRows[i]) - 1;

			if (associationValueMapList.size() > removeIndex) {
				associationValueMapList.remove(removeIndex);
			}
			
		}
		
		return "";
	}
	
	 

	/**
	 * @param request
	 * @param selectedGroupName
	 * @return
	 * @throws DynamicExtensionsApplicationException 
	 * @throws DynamicExtensionsSystemException 
	 */
	private String getSelectedGroupDetails(HttpServletRequest request, String selectedGroupName) throws DynamicExtensionsSystemException, DynamicExtensionsApplicationException
	{
		EntityGroupInterface entityGroup = null;
		if((selectedGroupName!=null)&&(!selectedGroupName.trim().equals("")))
		{
			GroupProcessor groupProcessor = GroupProcessor.getInstance();
			entityGroup = groupProcessor.getEntityGroupByIdentifier(selectedGroupName);
		}
		String groupDetailsXML = getGroupDetailsXML(request,entityGroup);
		return groupDetailsXML;
	}

	/**
	 * @param request
	 * @param entityGroup
	 * @return
	 */
	private String getGroupDetailsXML(HttpServletRequest request, EntityGroupInterface entityGroup)
	{
		String groupDescription = null;
		if(entityGroup!=null)
		{
			groupDescription = entityGroup.getDescription();
		}
		if(groupDescription==null)
		{
			groupDescription="";
		}
		String groupDetailsXML = createGroupDetailsXML(groupDescription);
		if(groupDetailsXML==null)
		{
			groupDetailsXML = "";
		}
		return groupDetailsXML;
	}

	/**
	 * @param groupDescription
	 * @return
	 */
	private String createGroupDetailsXML(String groupDescription)
	{
		StringBuffer responseXML = new StringBuffer();
		responseXML.append("<group>");
		responseXML.append("<group-description>" + groupDescription +"</group-description>");
		responseXML.append("</group>");
		return responseXML.toString();
	
	}

	/**
	 * @param request
	 * @param selectedFormName
	 */
	private String getSelectedFormDetails(HttpServletRequest request, String selectedFormName)
	{
		ContainerInterface containerForSelectedForm = null;
		if((request!=null)&&(selectedFormName!=null))
		{
			containerForSelectedForm = (ContainerInterface)CacheManager.getObjectFromCache(request, selectedFormName);
			if(containerForSelectedForm!=null)
			{
				updateCacheRefernces(request,selectedFormName,containerForSelectedForm);
			}
		}
		String formDetailsXML = getFormDetailsXML(request,selectedFormName,containerForSelectedForm);
		return formDetailsXML;
	}

	/**
	 * @param containerForSelectedForm
	 * @return
	 */
	private String getFormDetailsXML(HttpServletRequest request ,String selectedFormName,ContainerInterface containerForSelectedForm)
	{
		String formName = selectedFormName;
		String formDescription = "";
		String formConceptCode = "";
		String operationMode = Constants.ADD_SUB_FORM_OPR;
		if(containerForSelectedForm!=null)
		{
			formName=containerForSelectedForm.getCaption();
			EntityInterface entity = containerForSelectedForm.getEntity();
			if(entity!=null)
			{
				formDescription=entity.getDescription();
				formConceptCode = SemanticPropertyBuilderUtil.getConceptCodeString(entity);
			}
			operationMode = Constants.EDIT_FORM;
		}
		//If selected form container is null and cache container interface is also null,
		// it means that there is no container in cache and a new form is to be created.
	
		if(containerForSelectedForm==null)
		{
			ContainerInterface mainContainerInterface = (ContainerInterface)CacheManager.getObjectFromCache(request, Constants.CONTAINER_INTERFACE);
			if(mainContainerInterface==null)
			{
				operationMode = Constants.ADD_NEW_FORM;
			}
		}
		String formDetailsXML = createFormDetailsXML(formName, formDescription,formConceptCode,operationMode);
		if(formDetailsXML==null)
		{
			formDetailsXML = "";
		}
		return formDetailsXML;
	}

	/**
	 * @param formName
	 * @param formDescription
	 * @param formConceptCode
	 * @return
	 */
	private String createFormDetailsXML(String formName, String formDescription, String formConceptCode,String operationMode)
	{
		StringBuffer responseXML = new StringBuffer();
		responseXML.append("<form>");
		responseXML.append("<form-name>" + formName+"</form-name>");
		responseXML.append("<form-description>" + formDescription+"</form-description>");
		responseXML.append("<form-conceptcode>" + formConceptCode+"</form-conceptcode>");
		responseXML.append("<operationMode>" + operationMode+"</operationMode>");
		responseXML.append("</form>");
		return responseXML.toString();
	}

	/**
	 * @param selectedFormName
	 */
	private void updateCacheRefernces(HttpServletRequest request,String selectedFormName,ContainerInterface containerForSelectedForm)
	{
		CacheManager.addObjectToCache(request,Constants.CURRENT_CONTAINER_NAME,selectedFormName);
		CacheManager.addObjectToCache(request,selectedFormName,containerForSelectedForm);
	}
	/**
	 * @throws IOException 
	 * 
	 */
	private void sendResponse(String responseXML, HttpServletResponse response) throws IOException
	{
		PrintWriter out = response.getWriter();
		response.setContentType("text/xml");
		out.write(responseXML);
	}
}
