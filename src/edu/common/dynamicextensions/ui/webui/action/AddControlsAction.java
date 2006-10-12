package edu.common.dynamicextensions.ui.webui.action;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import edu.common.dynamicextensions.domaininterface.AttributeInterface;
import edu.common.dynamicextensions.domaininterface.EntityInterface;
import edu.common.dynamicextensions.domaininterface.userinterface.ControlInterface;
import edu.common.dynamicextensions.processor.AttributeProcessor;
import edu.common.dynamicextensions.processor.ControlProcessor;
import edu.common.dynamicextensions.ui.webui.actionform.ControlsForm;
import edu.common.dynamicextensions.ui.webui.util.CacheManager;
import edu.common.dynamicextensions.util.global.Constants;


/*This class is executed when user selects 'Add to Form'.
 * The exception thrown can be of 'Application' type ,in this case the same Screen will be displayed  
 * added with error messages .
 * And The exception thrown can be of 'System' type, in this case user will be directed to Error Page.
 * @author deepti_shelar
 *
 */
public class AddControlsAction extends BaseDynamicExtensionsAction {
	/**
	 * 
	 */
	public ActionForward execute(ActionMapping mapping, ActionForm form,HttpServletRequest request,
			HttpServletResponse response) {
		ControlsForm actionForm = (ControlsForm)form;
		EntityInterface entityInterface = (EntityInterface)CacheManager.getObjectFromCache(request, Constants.ENTITY_INTERFACE);
		ControlProcessor controlProcessor = ControlProcessor.getInstance();
		AttributeProcessor attributeProcessor = AttributeProcessor.getInstance();
		try {
			AttributeInterface attributeInterface = attributeProcessor.createAttribute(actionForm.getDataType());
			attributeProcessor.populateAttribute(actionForm,attributeInterface);
			actionForm.setAttribute(attributeInterface);
			ControlInterface controlInterface = controlProcessor.createControl(actionForm.getUserSelectedTool());
			controlProcessor.populateControl(actionForm, controlInterface);
			System.out.println("");
			entityInterface.addAttribute(attributeInterface);
			CacheManager.addObjectToCache(request, Constants.CONTROL_INTERFACE, controlInterface);
			CacheManager.addObjectToCache(request, Constants.ENTITY_INTERFACE, entityInterface);
		}catch (Exception e) {
			e.printStackTrace();
		}
		return mapping.findForward(Constants.SUCCESS);
	}
}
