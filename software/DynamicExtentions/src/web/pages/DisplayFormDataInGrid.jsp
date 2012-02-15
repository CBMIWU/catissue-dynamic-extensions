<%@ page language="java" isELIgnored="false"%>
<!-- configure isELIgnored in web.xml  -->
<html>
<head>
<title>Dynamic Extension</title>
<script type="text/javascript" src="dhtmlx_suite/js/dhtmlxcommon.js"></script>
<script type="text/javascript" src="dhtmlx_suite/js/dhtmlxgrid.js"></script>
<script type="text/javascript" src="dhtmlx_suite/js/dhtmlxgridcell.js"></script>
<script type="text/javascript" src="dhtmlx_suite/gridexcells/dhtmlxgrid_excell_grid.js"></script>
<script type="text/javascript" src="dhtmlx_suite/ext/dhtmlxgrid_mcol.js"></script>
<script type="text/javascript" src="dhtmlx_suite/ext/dhtmlxgrid_filter.js"></script>
<script src="<%=request.getContextPath()%>/javascripts/de/dynamicExtensions.js" type="text/javascript"></script>

<link rel="STYLESHEET" type="text/css"	href="dhtmlx_suite/css/dhtmlxgrid.css">
<link rel="STYLESHEET" type="text/css"	href="dhtmlx_suite/skins/dhtmlxgrid_dhx_skyblue.css">

<script>
var formDataGrid;

		function addColumnsToGrid()
		{
			var gridHeaders = '${requestScope.gridHeaders}';
			var arr = gridHeaders.substring(1,gridHeaders.length-1).split(",");
			var numberOfCol = formDataGrid.getColumnsNum();
			for(i=0;i<arr.length;i++)
			{
				formDataGrid.insertColumn(numberOfCol+i,arr[i],"ro",10,"na","left","top",null,null);
			}
			formUrl = encodeURIComponent('${param.formUrl}');
			deUrl = encodeURIComponent('${param.deUrl}');
			formDataGrid.loadXML("LoadDisplayFormDataInGrid.do?formContextId=${param.formContextId}&recEntryEntityId=${param.recEntryEntityId}&formUrl="+formUrl+"&deUrl="+deUrl);
		}

		function doOnLoad()
		{
			formDataGrid = new dhtmlXGridObject('displayFormDataGrid');
			formDataGrid.setImagePath("<%=request.getContextPath()%>/dhtmlx_suite/imgs/");
			formDataGrid.setHeader("SELECT,SR.NO.,OPERATION,deUrl");
			formDataGrid.setInitWidthsP("5,5,10,0");
			formDataGrid.setColAlign("center,center,center,center");
			formDataGrid.setColTypes("ch,ro,ro,ro");
			formDataGrid.attachHeader("#master_checkbox,,,");
			formDataGrid.enableMultiselect(true);
			formDataGrid.setSkin("dhx_skyblue");
			formDataGrid.init();
			addColumnsToGrid();
		}
	</script>
</head>

<form name="formGrid">

<body onload="doOnLoad();">
<table border="0" height="100%" width="100%">
	<tr>
		<td>
			<input type="button" value="Add Record" onclick="javascript:document.location.href = '${requestScope.formUrl}';"/>
		</td>
		<td width="95%">
			<input type="button" align="left" value="Print" onclick="printForm();"/>
		</td>
	</tr>
	<tr>
		<td style="vertical-align: top;" colspan="2">
		<div id="displayFormDataGrid" style="height: 380; width: 100%;"></div>
		</td>
	</tr>
	
</table>
<input type="hidden" id="formUrl" name="formUrl"/>
</body>

</form>
</html>