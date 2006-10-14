<%-- Jsp Summary                                                                                 	--%>
<%-- ---------------------------------------------------------------------------------------------- --%>
<%-- @author : chetan_patil--%>

<%-- TagLibs --%>
<%@ taglib uri="/WEB-INF/struts-bean.tld" prefix="bean" %>
<%@ taglib uri="/WEB-INF/struts-html.tld" prefix="html" %>
<%@ taglib uri="/WEB-INF/c.tld" prefix="c" %>

<%-- Imports --%>
<%@	
	page language="java" contentType="text/html" 
    import="java.util.List"
    import="java.util.Collection"
	import="edu.common.dynamicextensions.domain.Entity"
	import="java.util.Iterator"
	import="java.text.SimpleDateFormat"
  
%>

<%-- Stylesheet --%>
<link rel="stylesheet" type="text/css" href="css/stylesheet.css" />

<html>
	<head>
		<h3><bean:message key="table.heading" /><h3>
	</head>
	

<html:form styleId='formsIndexForm' action='/ApplyFormsIndexAction'>
	<body>
		
		<c:set var="entityList" value="${formsIndexForm.entityList}"/>
 		<jsp:useBean id="entityList" type="java.util.Collection"/>
							
		<table border='0'>
			<tr>
				<td align='left'>
					<html:submit styleClass="actionButton" property="buildForm" >
						<bean:message  key="buttons.build.form" />
					</html:submit>
				</td>
			</tr>
			
			<tr>
				<table table width='100%' cellpadding="0"	cellspacing="0" border='1'>				
					<tr>
						<th align='left'>
							<input type='checkbox' />
						</th>
						<th align='left'>
							<bean:message key="table.title" />
						</th>
						
						<th align='left'>
							<bean:message key="table.date" />
						</th>
						
						<th align='left'>
							<bean:message key="table.createdBy" />
						</th>
						
						<th align='left'>
							<bean:message key="table.status" />
						</th>		
					</tr>

					<tr>
						<%
							int i = 0;
							Iterator entityIterator = entityList.iterator();
							Entity entity = null;
							String name = null;
							String createdDate = null;

							while(entityIterator.hasNext())
							{
								entity = (Entity)entityIterator.next();
								name = entity.getName();
								createdDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(entity.getCreatedDate());
						%>
					</tr>
					<tr>
						<td>
							<input type='checkbox' />
						</td>
												
						<td>
							<%= name%>
						</td>

						<td>
							<%= createdDate%>
						</td>

						<td> Robert Lloyd </td>
						
						<td> In Progress </td>						
					</tr>
					<tr>
			   		    <%
								i++;
			   		    	}
						%>
					</tr>
				</table>
			</tr>
		
</table>
<table>
			
			<tr>
				<td align='left'>
					<html:button styleClass="actionButton" property="delete" disabled='true' >
						<bean:message  key="buttons.delete"/>
					</html:button>
				</td>
			</tr>
			
			
		</table>
	</body>
	</html:form>
</html>

