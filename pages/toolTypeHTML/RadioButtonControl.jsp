<%@ taglib uri="/WEB-INF/struts-html.tld" prefix="html" %>
<%@ taglib uri="/WEB-INF/struts-bean.tld" prefix="bean" %>
<%@ taglib uri="/WEB-INF/dynamicExtensions.tld" prefix="dynamicExtensions" %>
<%@ taglib uri="/WEB-INF/c.tld" prefix="c" %>

<%@page import="edu.common.dynamicextensions.processor.ProcessorConstants"%>
<%@page import="edu.wustl.common.beans.NameValueBean"%>

<c:set var="displayChoiceList" value="${controlsForm.displayChoiceList}"/>
<jsp:useBean id="displayChoiceList" type="java.util.List"/>

<table summary="" cellpadding="3" cellspacing="0" align = 'center' width='100%'>
	<tr>
    	<td>
			<table summary="" cellpadding="3" cellspacing="0"  align = 'center' width='100%' >
				<tr valign="top">
					<td class="formRequiredNoticeWithoutBorder" width="2%">&nbsp;</td>
					<td class="formRequiredLabelWithoutBorder" width="25%" >
						<bean:message key="eav.att.RadioButtonOptionTypes"></bean:message> :
					</td>
					<td class="formFieldWithoutBorder">
						<html:radio styleId = 'displayChoice' property="displayChoice" value="<%=ProcessorConstants.DISPLAY_CHOICE_USER_DEFINED%>" >
							<bean:message key="eav.att.OptionsUserDefined"/>
						</html:radio>
						<html:radio styleId='displayChoice' property="displayChoice" value="<%=ProcessorConstants.DISPLAY_CHOICE_CDE%>" disabled="true" >
							<bean:message key="eav.att.OptionsCDE"/>
						</html:radio>
					</td>
				</tr>
	 		</table>
		</td>
 	</tr>
	<tr>
		<td valign="top">
			<div id="optionValuesSpecificationDiv" />
		</td>
 	</tr>
 	<tr width="100%">
		<td width="100%">
			<hr/>
			<div id="substitutionDivRules"></div>
		</td>
	</tr>
</table>
<html:hidden styleId = 'dataType' property="dataType" value ="<%=ProcessorConstants.DATATYPE_STRING%>"/>
<jsp:include page="/pages/toolTypeHTML/ListValuesSpecification.jsp" />
<jsp:include page="/pages/ValidationRules.jsp" />

