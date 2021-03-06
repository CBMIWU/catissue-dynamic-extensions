<%@ taglib uri="/WEB-INF/struts-html.tld" prefix="html" %>
<%@ taglib uri="/WEB-INF/struts-bean.tld" prefix="bean" %>
<%@ taglib uri="/WEB-INF/dynamicExtensions.tld" prefix="dynamicExtensions" %>
<%@ taglib uri="/WEB-INF/c.tld" prefix="c" %>
<%@page import="edu.common.dynamicextensions.processor.ProcessorConstants" %>
<%@page import="edu.common.dynamicextensions.util.DynamicExtensionsUtility"%>

<script>var imgsrc="<%=request.getContextPath()%>/images/de/";</script>

<html:hidden styleId = 'dataType'  property="dataType" value="<%=ProcessorConstants.DATATYPE_BOOLEAN%>"/>

<table  summary="" align = 'center' width='100%' height="100%">
	<tr height="4%">
  		<td>
  			<table summary=""  align = 'center' width='100%'>
			   	<tr>
					<td class="formRequiredNoticeWithoutBorder" width="2%">*</td>
					<td class="formRequiredLabelWithoutBorder" width="30%">
						<bean:message key="eav.att.DefaultValue"/> :
					</td>
					<td class="formFieldWithoutBorder">
						<html:radio styleId = 'attributeDefaultValue' property="attributeDefaultValue" value="true">
							<bean:message key="eav.att.CheckedAttributeTitle"/>
						</html:radio>
						<html:radio styleId= 'attributeDefaultValue' property="attributeDefaultValue" value="false">
							<bean:message key="eav.att.UnCheckedAttributeTitle"/>
						</html:radio>
					</td>
				</tr>
			</table>
  		</td>
	</tr>
	<tr width="100%" height="2%">
		<td width="100%">
			<hr/>
			<div id="substitutionDivRules"></div>
		</td>
	</tr>
	<tr height="94%">
	</tr>
</table>
<jsp:include page="/pages/de/ValidationRules.jsp" />
