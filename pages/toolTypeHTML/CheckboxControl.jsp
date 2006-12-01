<%@ taglib uri="/WEB-INF/struts-html.tld" prefix="html" %>
<%@ taglib uri="/WEB-INF/struts-bean.tld" prefix="bean" %>
<%@ taglib uri="/WEB-INF/dynamicExtensions.tld" prefix="dynamicExtensions" %>
<%@ taglib uri="/WEB-INF/c.tld" prefix="c" %>
<%@page import="edu.common.dynamicextensions.processor.ProcessorConstants" %>

<script>var imgsrc="images/";</script>

<html:hidden styleId = 'dataType'  property="dataType" value="<%=ProcessorConstants.DATATYPE_STRING%>"/>

<table  summary="" align = 'center' width='100%'>
	<tr>
  		<td>
  			<table summary=""  align = 'center' width='100%'>
			   	<tr>
					<td class="formRequiredNoticeWithoutBorder" width="2%">*</td>
					<td class="formRequiredLabelWithoutBorder" width="25%">
						<bean:message key="eav.att.DefaultValue"/> :
					</td>
					<td class="formFieldWithoutBorder">
						<html:radio styleId = 'attributeDefaultValue' property="attributeDefaultValue" value="checked" >
							<bean:message key="eav.att.CheckedAttributeTitle"/>
						</html:radio>
						<html:radio styleId= 'attributeDefaultValue' property="attributeDefaultValue" value="unchecked">
							<bean:message key="eav.att.UnCheckedAttributeTitle"/>
						</html:radio>
					</td>
				</tr>
			</table>
  		</td>
	</tr>
	<tr width="100%">
		<td width="100%">
			<hr/>
			<div id="substitutionDivRules"></div>
		</td>
	</tr>
</table>
<jsp:include page="/pages/ValidationRules.jsp" />
