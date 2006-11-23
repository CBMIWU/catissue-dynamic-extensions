<%@ taglib uri="/WEB-INF/struts-html.tld" prefix="html" %>
<%@ taglib uri="/WEB-INF/struts-bean.tld" prefix="bean" %>
<%@ taglib uri="/WEB-INF/dynamicExtensions.tld" prefix="dynamicExtensions" %>
<%@ taglib uri="/WEB-INF/c.tld" prefix="c" %>

<%@page import="edu.common.dynamicextensions.processor.ProcessorConstants"%>
<%@page import="edu.wustl.common.beans.NameValueBean"%>


<c:set var="dataTypeList" value="${controlsForm.dataTypeList}"/>
 <jsp:useBean id="dataTypeList" type="java.util.List"/>

  <c:set var="displayChoiceList" value="${controlsForm.displayChoiceList}"/>
  <jsp:useBean id="displayChoiceList" type="java.util.List"/>

<c:set var="listType" value="${controlsForm.attributeMultiSelect}"/>
<jsp:useBean id="listType" type="java.lang.String"/>

<input styleId = 'hiddenIsMultiSelect' type="hidden" name="hiddenIsMultiSelect" value="<%=listType%>">
<table summary="" cellpadding="3" cellspacing="0" align = 'center' width='100%'>
  <tr>
    <td>

	<table summary="" cellpadding="3" cellspacing="0"  align = 'center' width='100%' >

		 <tr>
		 	<td class="formRequiredNoticeWithoutBorder" width="2%">
		 				*
		 	</td>

			<td class="formRequiredLabelWithoutBorder" width="25%">
				<bean:message key="eav.att.ListBoxType"/>
			</td>
			<td class="formFieldWithoutBorder">
					<html:radio  property="attributeMultiSelect" value="<%=ProcessorConstants.LIST_TYPE_SINGLE_SELECT%>" onclick="listTypeChanged(this)" >
						<bean:message key="eav.att.ListBoxSingleTitle"/>
					</html:radio>

					<html:radio property="attributeMultiSelect" value="<%=ProcessorConstants.LIST_TYPE_MULTI_SELECT%>" onclick="listTypeChanged(this)" >
						<bean:message key="eav.att.ListBoxMultiLineTitle"/>
					</html:radio>
			</td>
		</tr>
		<tr id="rowForDisplayHeight">
					<td class="formRequiredNoticeWithoutBorder" width="2%">
								&nbsp;
					</td>
					<td class="formRequiredLabelWithoutBorder"  >
						<bean:message key="eav.att.ListBoxDisplayLines"/>
					</td>
					<td class="formFieldWithoutBorder">
						<html:text styleClass="formFieldSized5" maxlength="100" size="60" styleId = 'attributeNoOfRows'  property="attributeNoOfRows" />
					</td>

		</tr>
		<tr>
			<td class="formRequiredNoticeWithoutBorder" width="2%">
					 			&nbsp;
		 	</td>
			<td class="formRequiredLabelWithoutBorder" width="25%"><bean:message key="eav.att.ListBoxOptionTypes"></bean:message></td>
			<td  class="formFieldWithoutBorder">
				<!--<c:forEach items="${displayChoiceList}" var="choiceType">
					<jsp:useBean id="choiceType" type="edu.wustl.common.beans.NameValueBean" />
						<c:set var="choiceTypeValue" value="${choiceType.value}" />
						<jsp:useBean id="choiceTypeValue" type="java.lang.String" />
						<html:radio property="displayChoice" value="" onchange="changeSourceForValues">
							<c:out value="${choiceType.name}"/>
						</html:radio>
				</c:forEach>-->
				<html:radio property="displayChoice" value="<%=ProcessorConstants.DISPLAY_CHOICE_USER_DEFINED%>" >
					<bean:message key="eav.att.OptionsUserDefined"/>
				</html:radio>
				<html:radio  property="displayChoice" value="<%=ProcessorConstants.DISPLAY_CHOICE_CDE%>" disabled="true">
					<bean:message key="eav.att.OptionsCDE"/>
				</html:radio>
				<html:radio property="displayChoice" value="<%=ProcessorConstants.DISPLAY_CHOICE_LOOKUP%>" disabled="true">
					<bean:message key="eav.att.OptionsLookup"/>
				</html:radio>
			</td>
		</tr>


	 </table>
	</td>
 </tr>
 <tr>
 	<td valign="top">
 		<div id="optionValuesSpecificationDiv">

 		</div>
 	</td>
 <tr>
 <html:hidden styleId= 'dataType' property="dataType" value ="<%=ProcessorConstants.DATATYPE_STRING%>"/>
</table>
<jsp:include page="/pages/toolTypeHTML/ListValuesSpecification.jsp" />

