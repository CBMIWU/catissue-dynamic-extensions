package edu.common.dynamicextensions.ui.webui.action;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.struts.action.Action;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import edu.common.dynamicextensions.util.global.Constants;


/**
 * @author deepti_shelar
 *
 * This class is used to redirect the user to the Home / SignIn Page after session is timedOut.
 */
public class ForwardAction extends Action {
   
	public ActionForward execute(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response) {
		HttpSession session = request.getSession();
		Map cacheMap = new HashMap();
		session.setAttribute(Constants.CACHE_MAP,cacheMap );
      return (mapping.findForward(Constants.SUCCESS));
	}

}