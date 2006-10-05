
package edu.common.dynamicextensions.ui.webui.action;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.struts.action.Action;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import edu.common.dynamicextensions.domain.Entity;
import edu.common.dynamicextensions.ui.webui.actionform.FormDefinitionForm;
import edu.common.dynamicextensions.ui.webui.util.CacheManager;
import edu.common.dynamicextensions.ui.webui.util.FormDetailsObject;
import edu.common.dynamicextensions.util.global.Constants;
import edu.wustl.common.util.dbManager.DAOException;
import edu.wustl.common.util.logger.Logger;

/**
 * @author deepti_shelar
 *
 */
public class LoadFormDefinitionAction extends Action {
	public ActionForward execute(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response) {

		FormDefinitionForm formDefinitionForm = (FormDefinitionForm) form;
		try {
			populateExistingFormsList(formDefinitionForm,request);
		} catch (DAOException daoException) {
			Logger.out.debug("excp "+daoException.getMessage());
			Logger.out.error(daoException.getMessage(), daoException);
			return mapping.findForward(new String(Constants.FAILURE));
		}
		return (mapping.findForward(Constants.SUCCESS));
	}
	/**
	 * 
	 * @param entitySelectionForm
	 */
	public void populateExistingFormsList(FormDefinitionForm formDefinitionActionForm,HttpServletRequest request) throws DAOException {

		/*DefaultBizLogic defaultBizLogic =  (DefaultBizLogic)BizLogicFactory.getBizLogic(formDefinitionForm.getFormId());   
       List existingFormsList =  defaultBizLogic.retrieve("Entity");
       if(existingFormsList == null){
    	   existingFormsList = new ArrayList();
       }*/
		Entity entity = new Entity();
		entity.setId(new Long("1"));
		entity.setName("Entity");
		Entity entity1 = new Entity();
		entity1.setId(new Long("3"));
		entity1.setName("Entity3");
		List existingFormsList = new ArrayList();
		existingFormsList.add(entity);
		existingFormsList.add(entity1);
		
		
		HttpSession session = request.getSession();
		String sessionId = session.getId();
		CacheManager cacheManager = CacheManager.getInstance();
		FormDetailsObject formDetailsObject = cacheManager.getObjectFromCache(sessionId);
		if(formDetailsObject == null) {
			formDetailsObject = new FormDetailsObject();
		}
		if(formDetailsObject.getFormDefinitionForm() != null) {
			formDefinitionActionForm.setFormName(formDetailsObject.getFormDefinitionForm().getFormName());	
			formDefinitionActionForm.setDescription((formDetailsObject.getFormDefinitionForm().getDescription()));
			formDefinitionActionForm.setCreateAs((formDetailsObject.getFormDefinitionForm().getCreateAs()));
		}		
		formDefinitionActionForm.setExistingFormsList(ActionUtil.getExistingFormsList(existingFormsList));
	}

}
