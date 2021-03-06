<%-- Jsp Summary                                                                                 	--%>
<%-- ---------------------------------------------------------------------------------------------- --%>
<%-- @author : sujay narkar, chetan patil --%>

<%-- TagLibs --%>
<%@ taglib uri="/WEB-INF/struts-bean.tld" prefix="bean" %>
<%@ taglib uri="/WEB-INF/struts-html.tld" prefix="html" %>
<%@ taglib uri="/WEB-INF/struts-logic.tld" prefix="logic" %>
<%@ taglib uri="http://java.sun.com/jstl/core" prefix="c" %>
<%@ taglib uri="/WEB-INF/dynamicExtensions.tld" prefix="dynamicExtensions" %>

<%-- Imports --%>
<%@ page language="java" contentType="text/html" %>
<%@ page import="org.apache.struts.action.ActionErrors" %>
<%@ page import="org.apache.struts.action.ActionMessages" %>

<%-- Stylesheet --%>
<link rel="stylesheet" type="text/css" href="<%=request.getContextPath()%>/stylesheets/de/styleSheet.css" />
<link rel="stylesheet" type="text/css" href="<%=request.getContextPath()%>/stylesheets/de/de_style.css" />
<link rel="stylesheet" type="text/css" href="stylesheets/de/ext-all.css" />
<link href="<%=request.getContextPath()%>/stylesheets/de/calanderComponent.css" type=text/css rel=stylesheet />
<link rel="stylesheet" type="text/css" href="stylesheets/de/xtheme-gray.css" />

<script src="<%=request.getContextPath()%>/javascripts/de/calendarComponent.js"></script>
<script src="<%=request.getContextPath()%>/javascripts/de/script.js" type="text/javascript"></script>

<script>var imgsrc="<%=request.getContextPath()%>/images/de/";</script>
<script src="<%=request.getContextPath()%>/javascripts/de/dynamicExtensions.js" type="text/javascript"></script>
<script src="<%=request.getContextPath()%>/javascripts/de/overlib_mini.js" type="text/javascript"></script>
<script src="<%=request.getContextPath()%>/javascripts/de/calender.js" type="text/javascript"></script>
<script src="<%=request.getContextPath()%>/javascripts/de/calendarComponent.js"></script>
<script src="<%=request.getContextPath()%>/javascripts/de/ajax.js"></script>

<script language="JavaScript" type="text/javascript" src="<%=request.getContextPath()%>/javascripts/de/prototype.js"></script>
<script language="JavaScript" type="text/javascript" src="<%=request.getContextPath()%>/javascripts/de/scriptaculous.js"></script>
<script language="JavaScript" type="text/javascript" src="<%=request.getContextPath()%>/javascripts/de/scr.js"></script>
<script language="JavaScript" type="text/javascript" src="<%=request.getContextPath()%>/javascripts/de/ext-base.js"></script>
<script language="JavaScript" type="text/javascript" src="<%=request.getContextPath()%>/javascripts/de/ext-all.js"></script>
<script language="JavaScript" type="text/javascript" src="<%=request.getContextPath()%>/javascripts/de/combos.js"></script>

<c:set var="containerInterface" value="${dataEntryForm.containerInterface}"/>
<jsp:useBean id="containerInterface" type="edu.common.dynamicextensions.domaininterface.userinterface.ContainerInterface"/>

<c:set var="mode" value="${dataEntryForm.mode}" />
<jsp:useBean id="mode" type="java.lang.String"/>

<html>
	<head>
		<title><bean:message key="table.heading" /></title>
	</head>

	<body onload = "setContextParameter('<%=request.getContextPath()%>')";>
		<html:form styleId="dataEntryForm" action="/ApplyFormPreviewAction" enctype="multipart/form-data" method="post">
			<div id="overDiv" style="position:absolute; visibility:hidden; z-index:1000;"></div>

	    	<table valign="top" align='center' width='100%' height="100%" cellspacing="0" cellpadding="0" class="tbBorders1" >
 				<!-- Main Page heading -->
				<tr height='6%'>
					<td class="formFieldNoBorders">
						<bean:message key="app.title.MainPageTitle" />
					</td>
				</tr>
		 
				<tr valign="top"  height='94%'>
					<td>
						<table valign="top" align='left' width='100%' height='100%' cellspacing="0" cellpadding="0" class="tbBordersAllbordersBlack" >
							<tr valign="top"  height='3%'>
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
												
							<tr valign="top">
								<td colspan="7">
									<table align='center' width='100%'>
										<tr>
											<td>
												<dynamicExtensions:dynamicUIGenerator containerInterface="<%=containerInterface%>" />
											</td>
										</tr>
									</table>
								</td>
							</tr>
							
							<tr  height='57%'>
								<td valign="top" colspan="7">
									<table cellpadding="4" cellspacing="5" border="0" align='center'>
										<tr height="5"></tr>
										<tr>
											<td align='right'>
												<input type="image" src="<%=request.getContextPath()%>/images/de/b_back.gif" width="62" height="21" align="middle" onClick="showParentContainerInsertDataPage()"/>												
											</td>
										</tr>
									</table>
								</td>
							</tr>
						</table>
					</td>
				</tr>
			</table>

			<input type="hidden" id="childContainerId" name="childContainerId" value=""/>
			<input type="hidden" id="childRowId" name="childRowId" value=""/>
			<input type="hidden" id="dataEntryOperation" name="dataEntryOperation" value=""/>
			<input type="hidden" id="showFormPreview" name="showFormPreview" value="true"/>
			<input type="hidden" id="mode" name="mode" value="<%=mode%>"/>
		</html:form>
	</body>
</html>