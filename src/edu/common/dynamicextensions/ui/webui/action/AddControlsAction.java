
package edu.common.dynamicextensions.ui.webui.action;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import edu.common.dynamicextensions.domaininterface.userinterface.ContainerInterface;
import edu.common.dynamicextensions.processor.ApplyFormControlsProcessor;
import edu.common.dynamicextensions.ui.webui.actionform.ControlsForm;
import edu.common.dynamicextensions.ui.webui.util.CacheManager;
import edu.common.dynamicextensions.util.global.Constants;

/**
 * This class is executed when user selects 'Add to Form'.
 * The exception thrown can be of 'Application' type ,in this case the same Screen will be displayed  
 * added with error messages .
 * And The exception thrown can be of 'System' type, in this case user will be directed to Error Page.
 * @author deepti_shelar
 *
 */
public class AddControlsAction extends BaseDynamicExtensionsAction
{
	/**
	 * @param mapping ActionMapping mapping
	 * @param form ActionForm form
	 * @param  request HttpServletRequest request
	 * @param response HttpServletResponse response
	 * @return ActionForward forward to next action
	 */
	public ActionForward execute(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) 
	{
		//Get controls form
		ControlsForm controlsForm = (ControlsForm) form;
		try
		{
			//Get container interface from cache
			ContainerInterface containerInterface = (ContainerInterface) CacheManager.getObjectFromCache(request, Constants.CONTAINER_INTERFACE);
			//Add control to form
			ApplyFormControlsProcessor formControlsProcessor = ApplyFormControlsProcessor.getInstance();
			formControlsProcessor.addControlToForm(containerInterface, controlsForm);
			
			//Store back container object to cache
			CacheManager.addObjectToCache(request, Constants.CONTAINER_INTERFACE, containerInterface);
			
			//Go to next page
			ActionForward actionForward = mapping.findForward(Constants.SUCCESS);
			response.sendRedirect("http://" + request.getServerName() + ":" + request.getServerPort() + request.getContextPath()
					+ actionForward.getPath());
			return null;
		}
		catch (Exception e)
		{
			String actionForwardString = catchException(e,request);
			return(mapping.findForward(actionForwardString));
		}

	}



}