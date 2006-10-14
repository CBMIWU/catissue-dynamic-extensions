/**
 * 
 */
package edu.common.dynamicextensions.ui.webui.action;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import edu.common.dynamicextensions.processor.LoadFormsIndexProcessor;
import edu.common.dynamicextensions.ui.webui.actionform.FormsIndexForm;
import edu.common.dynamicextensions.util.global.Constants;
/**
 * @author chetan_patil
 *
 */
public class LoadFormsIndexAction extends BaseDynamicExtensionsAction
{
	/**
	 * This method overrides the execute method of the Action class.
	 * It forwards the action to the DynamicExtension Home page.
	 * This is the first action called when the dynamicExceptions application is started ,
	 * So it initialises the cache with a new cacheMap. 
	 * @param mapping ActionMapping
	 * @param form ActionForm
	 * @param request HttpServletRequest
	 * @param response HttpServletResponse
	 * @throws Exception on exception
	 * @return ActionForward
	 */
	public ActionForward execute(ActionMapping mapping, ActionForm form, 
				HttpServletRequest request, HttpServletResponse response) throws Exception 
	{
		FormsIndexForm loadFormIndexForm = (FormsIndexForm)form;
		LoadFormsIndexProcessor loadFormsIndexProcessor = LoadFormsIndexProcessor.getInstance();
		loadFormsIndexProcessor.populateFormsIndex(loadFormIndexForm);
		return mapping.findForward(Constants.SHOW_DYNAMIC_EXTENSIONS_HOMEPAGE);
	}
	
}
