<%-- Jsp Summary                                                                                 	--%>
<%-- ---------------------------------------------------------------------------------------------- --%>
<%-- @author : Deepti Shelar--%>
<%-- @author : Vishvesh Mulay :)--%>
<%-- @author : Preeti Munot--%>

<%@ taglib uri="/WEB-INF/struts-bean.tld" prefix="bean" %>
<%@ taglib uri="/WEB-INF/struts-html.tld" prefix="html" %>
<%@ taglib uri="/WEB-INF/c.tld" prefix="c" %>
<%@ taglib uri="/WEB-INF/dynamicExtensions.tld" prefix="dynamicExtensions" %>

<html>
	<c:set var="currentEntityXML" value="${formDefinitionForm.currentEntityTreeXML}"/>
	<jsp:useBean id="currentEntityXML" type="java.lang.String"/>

	<c:set var="definedEntitiesTreeXML" value="${formDefinitionForm.definedEntitiesTreeXML}"/>
	<jsp:useBean id="definedEntitiesTreeXML" type="java.lang.String"/>

	<c:set var="isDataEntered" value="${formDefinitionForm.dataEntered}"/>
	<jsp:useBean id="isDataEntered" type="java.lang.Boolean"/>

	<head>
		<title>Dynamic Extensions</title>
		<link rel="stylesheet" type="text/css" href="<%=request.getContextPath()%>/stylesheets/de/styleSheet.css"/>
		<link rel="STYLESHEET" type="text/css" href="<%=request.getContextPath()%>/dhtml/stylesheets/de/dhtmlXTree.css"/>

		<script src="<%=request.getContextPath()%>/javascripts/de/dynamicExtensions.js" type="text/javascript"></script>
		<script src="<%=request.getContextPath()%>/javascripts/de/script.js" type="text/javascript"></script>
		<script src="<%=request.getContextPath()%>/javascripts/de/ajax.js" type="text/javascript"></script>
		<script src="<%=request.getContextPath()%>/dhtml/javascripts/de/dhtmlXCommon.js"></script>
		<script src="<%=request.getContextPath()%>/dhtml/javascripts/de/dhtmlXTree.js"></script>

		<script>
			resetTimeoutCounter();
			var currentEntityTree = null,definedEntitiesTree = null;
			function loadDefineFormPage()
			{
				contextParam = "<%=request.getContextPath()%>";
				loadCurrentEntityTree();
				loadDefinedEntitiesTree();
			}
			function preLoadImages()
			{
				var imSrcAr = new Array("line1.gif","line2.gif","line3.gif","line4.gif","minus2.gif","minus3.gif","minus4.gif","plus2.gif","plus3.gif","plus4.gif");
				var imAr = new Array(0);
				for(var i=0;i<imSrcAr.length;i++)
				{
					imAr[imAr.length] = new Image();
					imAr[imAr.length-1].src = "/dhtml/de/imgs/"+imSrcAr[i]
				}
			}
			function loadCurrentEntityTree()
			{
				preLoadImages();
				currentEntityTree=new dhtmlXTreeObject(document.getElementById('currentEntityTreeDiv'),"100%","100%",0);
				currentEntityTree.setImagePath("<%=request.getContextPath()%>/dhtml/de/imgs/");
				currentEntityTree.enableTreeImages(0);
				currentEntityTree.setOnClickHandler(treeNodeSelected);
				currentEntityTree.loadXMLString("<%=currentEntityXML%>");
			}
			function loadDefinedEntitiesTree()
			{
				preLoadImages();
				definedEntitiesTree=new dhtmlXTreeObject(document.getElementById('definedEntitiesTreeDiv'),"100%","100%",0);
				definedEntitiesTree.setImagePath("<%=request.getContextPath()%>/dhtml/de/imgs/");
				definedEntitiesTree.enableTreeImages(0);
				definedEntitiesTree.setOnClickHandler(definedEntitySelected);
				definedEntitiesTree.loadXMLString("<%=definedEntitiesTreeXML%>");
			}
		</script>
	</head>
	<body onload="loadDefineFormPage()">
		<html:form styleId="formDefinitionForm" action="/ApplyFormDefinitionAction">
			<c:set var="groupName" value="${formDefinitionForm.groupName}"/>
			<jsp:useBean id="groupName" type="java.lang.String"/>

			<c:set var="formList" value="${formDefinitionForm.formList}"/>
			<jsp:useBean id="formList" type="java.util.List"/>

			<table border='1' valign="top" align='right' width='100%' height="100%" cellspacing="0" cellpadding="0">
			<!-- Main Page heading -->
				<tr style = "border-bottom:0px" height="3%">
					<td style="border-left:0px;border-bottom:0px" class="formFieldNoBorders">
						<bean:message key="app.title.MainPageTitle" />
					</td>
				</tr>
				<tr height="97%">
					<td style="border-left:0px;border-top:0px;border-bottom:0px" valign="top">
						<table valign="top" summary="" align='left' width='100%' height='100%' cellspacing="0" cellpadding="3" class="tbBordersAllbordersBlack">
							<tr>
								<td height="20" class="tabMenuItem">
									<bean:message key="app.title.DefineGroupTabTitle" />
								</td>
								<td height="20" class="tabMenuItemSelected" >
									<bean:message key="app.title.DefineFormTabTitle" />
								</td>
								<td height="20" class="tabMenuItem" >
								 <bean:message key="app.title.BuildFormTabTitle" />
								</td>

							   <td height="20" class="tabMenuItem"  >
								 <bean:message key="app.title.PreviewTabTitle" />
							   </td>
							   <td width="50%" class="tabMenuSeparator" colspan="3">&nbsp;</td>
							</tr>
							<tr valign="top" >
								<td colspan="7" class="tbBordersAllbordersNone">
									<table align = "top" width="100%" height='100%' cellspacing="0">
										<tr valign="top" height='95%'>
											<!--  Tree display -->
											<td width="20%" valign="top" height='100%'>
												<table width="100%" height='100%' class="tbBordersAllbordersBlack">
													<tr valign="top" width="100%" height='5%'>
														<td align="left" class="formLabelLeftBorderless">
															<input  type="button" value="Add Form" disabled>
														</td>
													</tr>
													<tr height='15%'>
														<td>
															<label class="formMessage"><%=groupName%></label>
															<div style="border:solid 0px; padding:1px; width:250px; height:100%; overflow:auto;" id="currentEntityTreeDiv" />
														</td>
													</tr>
													<tr height='80%'><td>&nbsp;</td></tr>
												</table>
											</td>
											<td width="80%" height='100%'>
												<table cellspacing="0" cellpadding="3" align="left" width="100%" height='100%' class="tbBordersAllbordersBlack">
													<tr valign="top" height='3%'>
														<c:choose>
															<c:when test='${formDefinitionForm.operationMode == "AddSubForm"}'>
																<td class="formFieldWithNoTopBorder" colspan="3">
																	<bean:message key="app.CreateSubFormTitle"/>
																</td>
															</c:when>
															<c:otherwise>
																<td class="formFieldWithNoTopBorder" colspan="3">
																	<bean:message key="app.CreateFormTitle"/>
																</td>
															</c:otherwise>
														</c:choose>
													</tr>
													<tr valign="top" height='3%'>
														<td class="formMessage" colspan="3">
															<bean:message key="app.requiredMessage"/>
														</td>
													</tr>
													<tr valign="top" height='4%'>
														<td class="formMessage" colspan="3">&nbsp;</td>
													</tr>
													<tr valign="top" height='2%'>
														<td class="formMessage" colspan="3">
															<font color="red" ><html:errors/></font>
														</td>
													</tr>
													<tr valign="top" height='4%'>
														<td class="formRequiredNoticeWithoutBorder" width="2%">*</td>
														<td class="formRequiredLabelWithoutBorder" width="20%">
															<bean:message key="eav.form.title"/> :
														</td>
														<td class="formFieldWithoutBorder" >
															<html:text styleClass="formDateSized"  maxlength="100" size="60"  styleId= 'formName' property="formName" />
														</td>
													</tr>
													<tr valign="top" height='4%'>
														<td class="formRequiredNoticeWithoutBorder" width="2%" >&nbsp;</td>
														<td class="formRequiredLabelWithoutBorder" width="20%">
															<bean:message key="eav.form.conceptCode"/> :
														</td>
														<td class="formFieldWithoutBorder">
															<html:text styleClass="formDateSized" size="40" styleId='conceptCode' property="conceptCode" />
														</td>
													</tr>
													<tr valign="top" height='10%'>
														<td class="formRequiredNoticeWithoutBorder" width="2%">&nbsp;</td>
														<td class="formRequiredLabelWithoutBorder" width="20%">
															<bean:message key="eav.form.description"/> :
														</td>
														<td>
															<html:textarea styleClass="formFieldSmallSized"  rows = "3" cols="28"  styleId = 'formDescription' property="formDescription" />
														</td>
													</tr>
													<tr valign="top" height='4%'>
														<td class="formRequiredNoticeWithoutBorder" width="2%">&nbsp;</td>
														<td width="20%"></td>
														<td class="formMessage"  valign="top">
															<html:checkbox property="isAbstract" styleId="isAbstract" value="true"/>
															<bean:message key="eav.form.abstract"/>
														</td>
													</tr>
													<tr valign="top" height='4%'>
														<td class="formRequiredNoticeWithoutBorder" width="2%">&nbsp;</td>
														<td class="formRequiredLabelWithoutBorder" width="20%">
															<bean:message key="eav.form.inheritform"/>
														</td>
														<td>
															<html:select styleId="parentForm" styleClass="formFieldSmallSized" property="parentForm" disabled="<%=isDataEntered%>">
																<html:options collection="formList" labelProperty="name" property="value" />
															</html:select>
														</td>
													</tr>


													<c:if test='${formDefinitionForm.operationMode != "EditSubForm"}'>
														<tr valign = "top" height='5%'>
															<td class="formRequiredNoticeWithoutBorder" width="2%">*</td>
															<td class="formRequiredLabelWithoutBorder" width="20%">
																<bean:message key="eav.form.createAs"/> :
															</td>
															<td class="formFieldWithoutBorder">
																<table border='0'>
																	<tr class="formMessage">
																		<td>

																<c:choose>
																		<c:when test='${formDefinitionForm.operationMode=="EditForm"}'>
																			<html:radio styleId= 'createAsNew' property="createAs"  value="NewForm" disabled="true" onclick="createFormAsChanged()">
																				<bean:message key="eav.createnewentity.title"/>
																			</html:radio>
																		</c:when>
																		<c:otherwise>
																			<html:radio styleId= 'createAsNew' property="createAs" value="NewForm" onclick="createFormAsChanged()">
																				<bean:message key="eav.createnewentity.title"/>
																			</html:radio>
																		</c:otherwise>
																</c:choose>



																			<c:choose>
																				<c:when test='${formDefinitionForm.operationMode=="AddSubForm"}'>
																					<html:radio styleId = 'createAsExisting' property="createAs" value="ExistingForm" onclick="createFormAsChanged()" >
																						<bean:message key="eav.existingentity.title"/>
																					</html:radio>
																				</c:when>
																				<c:otherwise>
																					<html:radio styleId = 'createAsExisting' property="createAs" value="ExistingForm" disabled="true" onclick="createFormAsChanged()">
																						<bean:message key="eav.existingentity.title"/>
																					</html:radio>
																				</c:otherwise>
																			</c:choose>
																		</td>
																	</tr>
																</table>
															</td>
														</tr>
													</c:if>
													<tr valign = "top" id="rowForExistingFormDetails" style="display:none;">
														<td class="formRequiredNoticeWithoutBorder" width="2%" >&nbsp;</td>
														<td class="formRequiredLabelWithoutBorder" width="20%">&nbsp;</td>
														<td>
															<div style="border:solid 1px; padding:1px; width:250px; height:100px; overflow:auto;" id="definedEntitiesTreeDiv"/>
														</td>
													</tr>
													<c:choose>
														<c:when test='${formDefinitionForm.operationMode == "EditSubForm" || formDefinitionForm.operationMode == "AddSubForm"}'>
															<tr valign = "top" height="5%" >
																<td class="formRequiredNoticeWithoutBorder" width="2%" >&nbsp;</td>
																<td class="formRequiredLabelWithoutBorder"  width="20%">
																		<bean:message key="eav.form.viewAs"/> :
																</td>
																<td class="formFieldWithoutBorder">
																	<html:radio styleId='viewAs'property="viewAs" value="Form" >
																		<bean:message key="eav.viewAs.formTitle"/>
																	</html:radio>
																	<html:radio styleId='viewAs' property="viewAs" value="SpreadSheet" >
																		<bean:message key="eav.viewAs.spreadsheetTitle"/>
																	</html:radio>
																</td>
															</tr>
														</c:when>
													</c:choose>
													<tr height='50%' valign="top">
														<td colspan="3">&nbsp;</td>
													</tr>
												</table>
											</td>
										</tr>
										<tr height="5%" valign="top">
											<td align="left" >
												<html:button styleClass="actionButton" property="saveButton" onclick="saveFormDetails()" onkeydown="saveFormDetailsOnKeyDown(event)">
													<bean:message key="buttons.save" />
												</html:button>
												<html:button styleClass="actionButton" property="cancelButton" onclick="showHomePageFromCreateForm()">
													<bean:message  key="buttons.cancel" />
												</html:button>
											</td>

											<td align="right">
												<html:button styleClass="actionButton" property="nextButton" onclick="showBuildFormJSP()" >
													<bean:message  key="buttons.next" />
												</html:button>
											</td>
										</tr>
									</table>
								</td>
							 </tr>
						</table>
					</td>
				</tr>
			</table>

			<html:hidden styleId='selectedParentForm' property="selectedParentForm" value=""/>
			<html:hidden styleId='operation' property="operation" value=""/>
			<html:hidden styleId='operationMode' property="operationMode"/>
			<html:hidden styleId='entityIdentifier' property="entityIdentifier" value=""/>
			<html:hidden styleId='selectedObjectId' property="selectedObjectId" value=""/>
			<html:hidden styleId='currentContainerName' property="currentContainerName"/>
			<html:hidden styleId='viewAs' property="viewAs"/>
		</html:form>
	</body>
</html>
