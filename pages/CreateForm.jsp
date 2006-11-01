<%-- Jsp Summary                                                                                 	--%>
<%-- ---------------------------------------------------------------------------------------------- --%>
<%-- @author : Deepti Shelar--%>

<%@ taglib uri="/WEB-INF/struts-bean.tld" prefix="bean" %>
<%@ taglib uri="/WEB-INF/struts-html.tld" prefix="html" %>
<%@ taglib uri="/WEB-INF/c.tld" prefix="c" %>
<html>
<head>
	<title>Dynamic Extensions</title>
	<link rel="stylesheet" type="text/css" href="css/styleSheet.css" />
	<script src="jss/dynamicExtensions.js" type="text/javascript"></script>
	<script src="jss/script.js" type="text/javascript"></script>
</head>

<html:form styleId = "formDefinitionForm" action="/ApplyFormDefinitionAction" >
  <body>
     <table valign="top"  align='left' width='90%' height="90%" border='0' cellspacing="0" cellpadding="0" class="tbBorders1" >
         <!-- Main Page heading -->
         <tr><td class="formFieldSized1" ><bean:message key="app.title.MainPageTitle" /></td></tr>
	          <tr>
	     		<td valign="top" >
	     		 <table valign="top" summary="" align='left' width='100%' cellspacing="0" cellpadding="3"  >
					<tr >
					   <td height="20" class="tabMenuItem" onmouseover="changeMenuStyle(this,'tabMenuItemOver'),showCursor()" onmouseout="changeMenuStyle(this,'tabMenuItem'),hideCursor()" onclick="alert('This page is still under construction and will be available in the next release');">
						 <bean:message key="app.title.DefineGroupTabTitle" />
					   </td>

					   <td height="20" class="tabMenuItemSelected" >
						 <bean:message key="app.title.DefineFormTabTitle" />
					   </td>

					   <td height="20" class="tabMenuItem" onmouseover="changeMenuStyle(this,'tabMenuItemOver'),showCursor()" onmouseout="changeMenuStyle(this,'tabMenuItem'),hideCursor()" onClick="showBuildFormJSP()">
						 <bean:message key="app.title.BuildFormTabTitle" />
					   </td>

					   <td height="20" class="tabMenuItem" onmouseover="changeMenuStyle(this,'tabMenuItemOver'),showCursor()" onmouseout="changeMenuStyle(this,'tabMenuItem'),hideCursor()"  >
						 <bean:message key="app.title.PreviewTabTitle" />
					   </td>
					   <td width="50%" class="tabMenuSeparator" colspan="3">&nbsp;</td>
					</tr>

				   <!--  <tr>
					     <td class="formTitle" colspan="3">
								<bean:message key="eav.new.form"/>
						 </td>
				     </tr>-->
				    <!-- <tr>
						<td class="formRequiredNotice" width="5%">*</td>
						<td class="formRequiredLabel">
							<bean:message key="eav.group.title"/>
						</td>
						<td class="formField">
							<html:text styleClass="formDateSized"  maxlength="100" size="60"  property="formName" />
						</td>
					</tr>-->
					<tr><td colspan="7"><html:errors /></td></tr>
					<tr  valign="top" >
					<td colspan="7" >
						<table width="80%"  class="tbBorders1"  >
							<tr valign="top"  >
								<td width="30%" valign="top" >
									<table width="100%" >
										<tr valign="top" width="100%">
											<td  align="left">
												<input  type="button" value="Add Form" disabled >
											</td>
										</tr>
										<tr><td >Tree will appear here</td></tr>
									</table>
								</td>
								<td width="70%">
									<table cellspacing="0" cellpadding="3"  align="left" >
										<tr>
											 <td class="formFieldSizesd1" colspan="3">
												 <bean:message key="app.CreateFormTitle"/>
											 </td>
										</tr>
										<tr>
											 <td class="formMessage" colspan="3">
												 <bean:message key="app.requiredMessage"/>
											 </td>
										</tr>

										 <tr>
											<td class="formRequiredNotice" width="2%">*</td>
											<td class="formRequiredLabel"  width="20%">
												<bean:message key="eav.form.title"/>
											</td>
											<td class="formField" >
												<html:text styleClass="formDateSized"  maxlength="100" size="60"  property="formName" />
											</td>
										</tr>
										 <tr>
										 <td class="formRequiredNotice" width="2%" >*</td>
											<td class="formRequiredLabel" width="20%">
												<bean:message key="eav.form.conceptCode"/>
											</td>
											<td class="formField">
												<html:text styleClass="formDateSized"  maxlength="20" size="40"  property="conceptCode" />
											</td>
										</tr>
										<tr>
											<td class="formRequiredNotice" width="2%" >&nbsp;</td>
											<td class="formRequiredLabel" width="20%">
													<bean:message key="eav.form.description"/>
											</td>
											<td class="formField">
													<html:textarea styleClass="formDateSized"  rows = "3" cols="28"  property="formDescription" />
											</td>
										 </tr>
										  <tr>
											<td class="formRequiredNotice" width="2%">*</td>
											 <td class="formRequiredLabel" width="20%">
												<bean:message key="eav.form.createAs"/>
											 </td>
											 <td class="formField">
												<table border='0'>
													<tr class="formMessage">
														 <td >
															<html:radio property="createAs" value="NewForm">
																<bean:message key="eav.createnewentity.title"/>
															</html:radio>

															<html:radio property="createAs" value="ExistingForm">
																<bean:message key="eav.existingentity.title"/>
															</html:radio>
														</td>
													</tr>
												</table>
											</td>
										 </tr>
									</table>
								</td>
							</tr>
					</table>
				</td>
			 </tr>
		  <tr >
	 	 	<td colspan="7">
	 		 <table summary="" align = 'left' width="80%" valign="top" cellpadding="5" cellspacing="0" border="0">
	 	    	<tr >
	 				<td align="left">
	 					<html:submit styleClass="actionButton">
	 						<bean:message  key="buttons.save" />
	 					</html:submit>
	 					<html:button styleClass="actionButton" property="cancelButton" onclick="showHomePageFromCreateForm()">
							<bean:message  key="buttons.cancel" />
	 					</html:button>
	 				</td>
	 				<td >
	 					&nbsp;
	 				</td>
	 				<td align="right">
	 					<html:button styleClass="actionButton" property="nextButton" onclick="showBuildFormJSP()" >
	 						<bean:message  key="buttons.next" />
	 					</html:button>
	 				</td>
	 		</table>
	  	</td>
	</tr>
</table>
	<html:hidden property="operation" value=""/>
	<html:hidden property="entityIdentifier" value=""/>
  </body>
</html:form>
</html>
