<%@ taglib uri="/WEB-INF/struts-html.tld" prefix="html" %>
<%@ taglib uri="/WEB-INF/struts-bean.tld" prefix="bean" %>
<%@ taglib uri="/WEB-INF/dynamicExtensions.tld" prefix="dynamicExtensions" %>
<%@ taglib uri="/WEB-INF/c.tld" prefix="c" %>
<%@page import="edu.common.dynamicextensions.processor.ProcessorConstants" %>


<SCRIPT>var imgsrc="images/";</SCRIPT>


<c:set var="fileFormatsList" value="${controlsForm.fileFormatsList}"/>
<jsp:useBean id="fileFormatsList" type="java.util.List"/>

<html:hidden styleId = 'dataType' property="dataType" value ="<%=ProcessorConstants.DATATYPE_FILE%>"/>
<table  summary="" cellpadding="3" cellspacing="0" align = 'center' width='100%'>
  <!--<tr>
    <td>
		<table summary="" cellpadding="3" cellspacing="0"  align = 'center' width='100%'>
				<jsp:include page="/pages/toolTypeHTML/CommonControlAttributes.jsp" />
		</table>
	</td>
 </tr>-->
  <tr>
  	<td>
  		<table summary="" cellpadding="3" cellspacing="0"  align = 'center' width='100%'>
		   <tr>
				<td class="formRequiredNoticeWithoutBorder" width="2%">
					&nbsp;
				</td>
				<td class="formRequiredLabelWithoutBorder" width="25%">
						<bean:message key="eav.att.TextFieldWidth"/>
				</td>

				<td class="formFieldWithoutBorder">
					<html:text styleClass="formDateSized"  maxlength="100" size="60"  styleId = 'attributenoOfCols' property="attributenoOfCols" />
				</td>
			</tr>
			<tr>
				<td class="formRequiredNoticeWithoutBorder" width="2%">
					&nbsp;
				</td>
				<td class="formRequiredLabelWithoutBorder" width="25%">
						<bean:message key="eav.att.FileFormatsList"/>
				</td>

				<td class="formFieldWithoutBorder">
					<html:select multiple="true" size="4" styleId = 'fileFormats'  property="fileFormats">
						<c:forEach items="${fileFormatsList}" var="fileformat">
							<jsp:useBean id="fileformat" type="java.lang.String" />
								<html:option  value='<%=fileformat%>' >
								</html:option>
						</c:forEach>
					</html:select>
				</td>
			</tr>
			<tr disabled>
				<td class="formRequiredNoticeWithoutBorder" width="2%">
					&nbsp;
				</td>
				<td class="formRequiredLabelWithoutBorder" width="25%">
					<bean:message key="eav.att.FileFormat"/>
				</td>

				<td class="formFieldWithoutBorder">
					<html:text styleClass="formDateSized"  maxlength="100" size="60" styleId = 'format' property="format" />
				</td>
			</tr>
			<tr>
				<td class="formRequiredNoticeWithoutBorder" width="2%">
					&nbsp;
				</td>
				<td class="formRequiredLabelWithoutBorder" width="25%">
					<bean:message key="eav.att.MaximumFileSize"/>
				</td>

				<td class="formFieldWithoutBorder">
					<html:text styleClass="formDateSized"  maxlength="100" size="60" styleId ='attributeSize'  property="attributeSize" />
					<bean:message key="app.maxFileSizeUnit"/>
				</td>
			</tr>
	</table>

  	</td>
  </tr>
</table>
