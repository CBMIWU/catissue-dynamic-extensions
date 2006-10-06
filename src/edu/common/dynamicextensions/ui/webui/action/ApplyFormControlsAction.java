package edu.common.dynamicextensions.ui.webui.action;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.Action;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import edu.common.dynamicextensions.domain.userinterface.UIControlsConfigurationFactory;
import edu.common.dynamicextensions.ui.webui.actionform.ControlsForm;
import edu.common.dynamicextensions.ui.webui.util.CacheManager;
import edu.common.dynamicextensions.util.global.Constants;


/**
 * 
 * @author deepti_shelar
 *
 */
public class ApplyFormControlsAction extends Action {
	/**
	 * 
	 */
	public ActionForward execute(ActionMapping mapping, ActionForm form,HttpServletRequest request,
			HttpServletResponse response) {
		ControlsForm actionForm = (ControlsForm) form;
		if(actionForm.getOperation().equalsIgnoreCase("controlSelectedAction")) {
			CacheManager.addObjectToCache(request,Constants.CONTROLS_FORM , actionForm);
			/*UIControlsConfigurationFactory uiControlsConfigurationFactory = UIControlsConfigurationFactory.getInstance();
			List controlAttributesList = uiControlsConfigurationFactory.getConrolAttributesList(actionForm.getSelectedTool());
			actionForm.setSelectedControlAttributesList(controlAttributesList);
			actionForm.setToolsList(uiControlsConfigurationFactory.getControlNames());
			*/return mapping.findForward("SelectControlAction");
		}
		if(actionForm.getOperation().equalsIgnoreCase(Constants.SHOW_CREATE_FORM_JSP)) {
			ControlsForm cacheForm = (ControlsForm)CacheManager.getObjectFromCache(request,Constants.CONTROLS_FORM);
			actionForm.setUserSelectedTool(cacheForm.getUserSelectedTool());
			actionForm.setSelectedControlAttributesList(cacheForm.getSelectedControlAttributesList());
			CacheManager.addObjectToCache(request,Constants.CONTROLS_FORM , actionForm);
			return mapping.findForward(Constants.SHOW_CREATE_FORM_JSP);
		}
		return mapping.findForward(Constants.SUCCESS);
	}  
	/**
	 * 
	 * @param request
	 * @return
	 *//*
	protected SessionDataBean getSessionData(HttpServletRequest request) {
		Object obj = request.getSession().getAttribute(Constants.SESSION_DATA);
		if(obj!=null)
		{
			SessionDataBean sessionData = (SessionDataBean) obj;
			return  sessionData;
		}
		return null;
		//return (String) request.getSession().getAttribute(Constants.SESSION_DATA);
	}*/


}
