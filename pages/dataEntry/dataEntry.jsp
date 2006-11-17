<%-- Jsp Summary                                                                                 	--%>
<%-- ---------------------------------------------------------------------------------------------- --%>
<%-- @author : sujay narkar, chetan patil --%>

<%-- TagLibs --%>
<%@ taglib uri="/WEB-INF/struts-bean.tld" prefix="bean" %>
<%@ taglib uri="/WEB-INF/struts-html.tld" prefix="html" %>
<%@ taglib uri="/WEB-INF/c.tld" prefix="c" %>

<%-- Imports --%>
<%@
	page language="java" contentType="text/html"
%>

<%-- Stylesheet --%>
<link rel="stylesheet" type="text/css" href="css/styleSheet.css" />
<script src="jss/calendarComponent.js"></script>
<script src="jss/script.js" type="text/javascript"></script>

<SCRIPT>var imgsrc="images/";</SCRIPT>
<LINK href="css/calanderComponent.css" type=text/css rel=stylesheet>

<script src="jss/dynamicExtensions.js" type="text/javascript"></script>
<script src="jss/overlib_mini.js" type="text/javascript"></script>
<script src="jss/calender.js" type="text/javascript"></script>
<script src="jss/calendarComponent.js"></script>

<c:set var="containerInterface" value="${dataEntryForm.containerInterface}"/>
<jsp:useBean id="containerInterface" type="edu.common.dynamicextensions.domaininterface.userinterface.ContainerInterface"/>

<c:set var="showFormPreview" value="${dataEntryForm.showFormPreview}"/>
<jsp:useBean id="showFormPreview" type="java.lang.String"/>

<html>
	<head>
		<title><bean:message key="table.heading" /></title>
	</head>

	<body onload="loadPreviewForm()">
		<html:form styleId = "dataEntryForm" action="/ApplyDataEntryFormAction" enctype="multipart/form-data" >
			<html:errors />
			<html:hidden property="entitySaved" />
			<div id="overDiv" style="position:absolute; visibility:hidden; z-index:1000;"></div>
		
			<!-- Table Tag -->
			<c:choose>
			    <c:when test='${showFormPreview  == "true"}'>
			    	<table valign="top" style="border-right:1px" border=1 align='center' width='90%' height="90%" border='0' cellspacing="0" cellpadding="0" class="tbBorders1" >
				</c:when>
				
				<c:otherwise>
					<table valign="top" align='center' width='90%' height="90%" border='0' cellspacing="0" cellpadding="0"  >
				</c:otherwise>
			</c:choose>					
				
 				<!-- Main Page heading -->
				<tr>
					<td class="formFieldSized1">
						<c:choose>
							<c:when test='${showFormPreview  == "true"}'> 
								<bean:message key="app.title.MainPageTitle" />
								</c:when>
						</c:choose>
					</td>
				</tr>
	 
	  			<tr valign="top">
					<td>
						<table valign="top" summary="" align='center' width='100%' cellspacing="0" cellpadding="3">
						
							<c:choose>
							    <c:when test='${showFormPreview  == "true"}'> 
									<tr valign="top">
									   <td height="20" class="tabMenuItem" onmouseover="changeMenuStyle(this,'tabMenuItemOver'),showCursor()" onmouseout="changeMenuStyle(this,'tabMenuItem'),hideCursor()" onclick="alert('This page is still under construction and will be available in the next release');">
										 <bean:message key="app.title.DefineGroupTabTitle" />
									   </td>
						
									   <td height="20" class="tabMenuItem" onmouseover="changeMenuStyle(this,'tabMenuItemOver'),showCursor()" onmouseout="changeMenuStyle(this,'tabMenuItem'),hideCursor()" onclick="showFormDefinitionPage()">
										 <bean:message key="app.title.DefineFormTabTitle" />
									   </td>
						
									   <td height="20" class="tabMenuItem" onmouseover="changeMenuStyle(this,'tabMenuItemOver'),showCursor()" onmouseout="changeMenuStyle(this,'tabMenuItem'),hideCursor()" onclick="backToControlForm()">
										 <bean:message key="app.title.BuildFormTabTitle" />
									   </td>
						
									   <td height="20" class="tabMenuItemSelected"  >
										 <bean:message key="app.title.PreviewTabTitle" />
									   </td>
									   <td width="50%" class="tabMenuSeparator" colspan="3">&nbsp;</td>
									</tr>
								</c:when>	
							</c:choose>
							
							<tr valign="top">
								<td colspan="7">
									<table align='center' width='80%'  >
										<tr >
											<td align="center" class="formTitle">
												<logic:messagesPresent message="true">
															<html:messages message="true" id="msg">
																<bean:write name="msg" ignore="true"/>
															</html:messages>
												</logic:messagesPresent>
											</td>
										</tr>
										<tr><td>&nbsp;</td></tr>
										<tr>
											<td >
												<table summary="" cellpadding="3" cellspacing="0"  align='center' width = '100%'>
													<tr>
														<td class="formMessage" colspan="3">
															<c:out value="${containerInterface.requiredFieldIndicatior}" escapeXml="false" />&nbsp;
															<c:out value="${containerInterface.requiredFieldWarningMessage}" escapeXml="false" />
														</td>
													</tr>
													<tr>
														<td class='formTitle' colspan="3" align='left'>
															<c:set var="entityInterface" value="${containerInterface.entity}" />
															<jsp:useBean id="entityInterface" type="edu.common.dynamicextensions.domaininterface.EntityInterface" />
				
															Insert data for &nbsp;<c:out value="${entityInterface.name}" escapeXml="false" />
														</td>
													</tr>

													<c:set var="dummyControlCollection" value="${containerInterface.controlCollection}" />
													<jsp:useBean id="dummyControlCollection" type="java.util.Collection" />
													<% 
														for(int sequenceNumber = 1; sequenceNumber <= dummyControlCollection.size(); sequenceNumber++)
														{
													%>
															<c:forEach items="${containerInterface.controlCollection}" var="control">
															<jsp:useBean id="control" type="edu.common.dynamicextensions.domaininterface.userinterface.ControlInterface" />
														
															<% 
																if(sequenceNumber == control.getSequenceNumber().intValue())
																{
															%>
																	<tr>
																		<td class="formRequiredNotice" width="2%">
																			&nbsp;
																		</td>
																		<td class="formRequiredLabel" width="20%">
																			<label for="<c:out value="control_${control.sequenceNumber}"/>"><c:out value="${control.caption}"/></label>
																		</td>
																		<td class="formField">
																			<% String generateHTMLStr = control.generateHTML(); %>
																			<% pageContext.setAttribute("generateHTMLStr", generateHTMLStr); %>
																			<c:out value="${generateHTMLStr}" escapeXml="false" />
																		</td>
																	</tr>
															<%
																}
															%>
															</c:forEach>
													<%
														}
													%>
												</table>
											</td>
										</tr>
									</table>
           						</td>
           					</tr>
							<tr>
								<td valign="top" colspan="7">
									<table cellpadding="4" cellspacing="5" border="0"  align='center'>
										<tr height="5"></tr>
										<tr>
											<td align='left'>
												<c:if test='${showFormPreview  == "true"}'>
													<html:button property="backToPrevious"  styleClass="actionButton" onclick="backToControlForm()">
														<bean:message  key="buttons.backToPrevious" />
													</html:button>
												</c:if>
											</td>
						
											<td align='right'>						
												<c:choose>
						  	    					<c:when test='${showFormPreview  == "true"}'>
														<html:submit styleClass="actionButton"  onclick="addDynamicData()" disabled="true">
															<bean:message  key="buttons.submit" />
														</html:submit>
													</c:when>
						 							<c:otherwise>	
												 		<html:submit styleClass="actionButton" onclick="addDynamicData()">
													 		<bean:message  key="buttons.submit" />
														</html:submit>
					 								</c:otherwise>
			     								</c:choose>								
											</td>
										</tr>
									</table>
								</td>
							</tr>
						</table>
					</td>
				</tr>
			</table>
		</html:form>
	</body>
</html>