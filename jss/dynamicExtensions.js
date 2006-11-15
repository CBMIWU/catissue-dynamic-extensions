function formSelectedAction() {
	//alert("This is a form to be copied");
}
function tagHandlerFunction(selectedTool) {
	document.getElementById('userSelectedTool').value=selectedTool;
	
}

function showBuildFormJSP() {
 	document.getElementById('operation').value='buildForm';
	var formDefinitionForm = document.getElementById('formDefinitionForm');
	formDefinitionForm.submit();
}

function controlSelectedAction()
{	
	var controlOperation = document.getElementById('controlOperation');
//	controlOperation.value = 'Add';
	
	clearForm();
	
	var controlsForm = document.getElementById('controlsForm');
	controlsForm.action="/dynamicExtensions/SelectControlAction.do";
	controlsForm.submit();
}

function formCreateAsChanged() {
}

function showHomePageFromCreateForm()
{
	var formDefinitionForm = document.getElementById('formDefinitionForm');
	formDefinitionForm.action="/dynamicExtensions/DynamicExtensionHomePage.do";
	formDefinitionForm.submit();
}
function showHomePageFromBuildForm() {
	var controlsForm = document.getElementById('controlsForm');
	controlsForm.action="/dynamicExtensions/DynamicExtensionHomePage.do";
	controlsForm.submit();
}
function addControlToFormTree() {
       document.getElementById('operation').value='controlAdded';
   	var controlsForm=document.getElementById("controlsForm");
     controlsForm.action="/dynamicExtensions/AddControlsAction.do";
//    document.getElementById('controlOperation').value='add';
	controlsForm.submit();
}
function addControlToForm() {
	
	var arg = window.dialogArguments;
	arg.document.getElementById('operation').value='controlAdded';
    	var controlsForm=arg.document.getElementById("controlsForm");
	controlsForm.action="/dynamicExtensions/AddControlsAction.do";
	controlsForm.submit();
	window.close();
}

function closeWindow() {
		window.close();
}
function showNextActionConfirmDialog()
{
	var  url="/dynamicExtensions/pages/confirmNextActionDialog.jsp";
	 var properties = "dialogHeight: 200px; dialogWidth: 350px; dialogTop: 300px; dialogLeft: 350px; edge: Sunken; center: Yes; resizable: Yes; status: No; help:no"
     window.showModalDialog(url, window, properties);
}
function showCreateFormJSP() {
	var arg = window.dialogArguments;
    	var controlsForm=arg.document.getElementById("controlsForm");
	controlsForm.action="/dynamicExtensions/LoadFormDefinitionAction.do";
	controlsForm.submit();
	window.close();
}

//Added by Preeti
function dataFldDataTypeChanged(datatypeControl)
{
	if(datatypeControl!=null)
	{
		var selectedDatatype = datatypeControl.value;
		var divForDataTypeId = selectedDatatype + "DataType";
		var divForDataType = document.getElementById(divForDataTypeId);
		
		if(divForDataType!=null)
		{
			var substitutionDiv = document.getElementById('substitutionDiv');
			substitutionDiv.innerHTML = divForDataType.innerHTML;
		}
		 insertRules(datatypeControl);
	}
}
function insertRules(datatypeControl)
{
	
		var selectedDatatype = datatypeControl.value;
		var divForDataTypeId = selectedDatatype + "Div";

		var divForDataType = document.getElementById(divForDataTypeId);
		var divForCommonRule = document.getElementById("commonsDiv");


		if(divForDataType!=null)
		{
			var substitutionDivRules = document.getElementById('substitutionDivRules');
			var tempInnerHTML  = divForCommonRule.innerHTML + divForDataType.innerHTML;
	
            while (tempInnerHTML.indexOf("tempValidationRules") != -1)
			{
			       tempInnerHTML = tempInnerHTML.replace("tempValidationRules","validationRules");
				   
			 }
		
			substitutionDivRules.innerHTML = tempInnerHTML;
		}
}


function initBuildForm()
{
	var dataTypeElt = document.getElementById("initialDataType");
	if(dataTypeElt!=null)
	{
		//Load datatype details for selected datatype
		dataFldDataTypeChanged(dataTypeElt);
	}
	
	var sourceElt =document.getElementById("displayChoice");
	if(sourceElt!=null)
	{
		//Load source details for selected sourcetype
		changeSourceForValues(sourceElt);
	}
	
	//Reinitialize counter
	var choiceListElementCnter = document.getElementById('choiceListCounter');
	if(choiceListElementCnter !=null)
	{
		choiceListElementCnter.value="1";
	}
	
	addChoicesFromListToTable();
	
	//Initilialize default value for list of options
	initializeOptionsDefaultValue();
	
	
	//If other option is selected in measurement units, enable the text box next to it
	var cboMeasurementUnits = document.getElementById('attributeMeasurementUnits');
	measurementUnitsChanged(cboMeasurementUnits);
	
}
function addChoicesFromListToTable()
{
	var choiceList = document.getElementById("choiceList");
	if(choiceList!=null)
	{
		var choiceListValue = choiceList.value;
		if((choiceListValue!=null)&&(choiceListValue!=""))
		{
			var choice_array=choiceListValue.split(",");

			if(choice_array!=null)
			{
				for(var i=0;i<choice_array.length;i++)
				{
					if((choice_array[i]!=null)&&(choice_array[i]!=""))
					{
						//Add choice to list
						var textBox = document.getElementById('optionName');
						if(textBox !=null)
						{
							textBox.value = choice_array[i];
							addChoiceToList(false);
						}	
					}
					
				}
			}
		}
	}
}
function changeSourceForValues(sourceControl)
{
	if(sourceControl!=null)
	{
		var sourceForValues = sourceControl.value;
		if(sourceForValues!=null)
		{
			var divForSourceId = sourceForValues + "Values";

			var divForSource = document.getElementById(divForSourceId);
			if(divForSource!=null)
			{
				var valueSpecnDiv = document.getElementById('optionValuesSpecificationDiv');
				if(valueSpecnDiv!=null)
				{
					valueSpecnDiv.innerHTML = divForSource.innerHTML;
				}
			}
		
		}
	}
}

//addToChoiceList : indicates whether the choice shld be added to choice list
//This will be true when called while adding choice at runtime, and false when adding at load time
function addChoiceToList(addToChoiceList)
{
	var optionName = document.getElementById('optionName');
	var optionConceptCode = document.getElementById('optionConceptCode');
	var optionDescription = document.getElementById('optionDescription');
	var choiceListElementCnter = document.getElementById('choiceListCounter');
	
	var elementNo = 0;
	if(choiceListElementCnter!=null)
	{
		elementNo = choiceListElementCnter.value;
	}

	if((optionName!=null)&&(optionName.value!=""))
	{
		newValue = optionName.value;
		var choiceListTable  = document.getElementById('choiceListTable');
	
		if(choiceListTable!=null)
		{
			var myNewRow = choiceListTable.insertRow();
			var myNewCell =  null;
			
			//Add Option to table
			myNewCell =  myNewRow.insertCell();
			myNewCell.setAttribute("id",optionName.value);
			myNewCell.setAttribute("className","formMessage");
			myNewCell.setAttribute("width","10%");
			var chkBoxId = "chkBox" + elementNo;
			myNewCell.innerHTML = "<input type='checkbox' id='" + chkBoxId +"' value='"+optionName.value + "'>"   + optionName.value;
			
			var choicelist = document.getElementById('choiceList');
			if(choicelist !=null)
			{
				//add to choicelist
				if(addToChoiceList == true)
				{
					choicelist.value = choicelist.value + "," + optionName.value;
				}
			}
			optionName.value = "";
			if(optionConceptCode!=null)
			{
				optionConceptCode.value="";
			}
			if(optionDescription!=null)
			{
				optionDescription.value="";
			}
			//increment number of elements count
			document.getElementById('choiceListCounter').value = (parseInt(elementNo) + 1) + "";
			
		}
	}
}

function deleteElementsFromChoiceList()
{
	var valuestable = document.getElementById('choiceListTable');
	if(valuestable!=null)
	{
		//Clear choice list
		var choicelist = document.getElementById('choiceList');
		if(choicelist !=null)
		{
			choicelist.value = "";
		}
		var choiceListElementCnter = document.getElementById('choiceListCounter');
		var noOfElements = 0;
		if(choiceListElementCnter!=null)
		{
			noOfElements = parseInt(choiceListElementCnter.value);
		}
		
		var chkBoxId = "";
		var chkBox;
		
		for(var i=0;i<noOfElements;i++)
		{
			
			chkBoxId = "chkBox" + i;
			
			chkBox = document.getElementById(chkBoxId);
			
			if(chkBox!=null)
			{
				var rowofCheckBox = chkBox.parentElement.parentElement;
				if(chkBox.checked == true)
				{
					if(rowofCheckBox!=null)
					{
						var rowIndexOfChkBox = rowofCheckBox.rowIndex;
			
						if(rowIndexOfChkBox!=null)
						{
							valuestable.deleteRow(rowIndexOfChkBox);
						}
					}
				}
				else
				{
					//Add to choice list if not selected
					choicelist.value = choicelist.value + ","  + chkBox.value;
				}
				
			}
		}
	}
}
function initializeOptionsDefaultValue()
{
	var valuestable = document.getElementById('choiceListTable');
	var defaultValue = document.getElementById('attributeDefaultValue');
	if((defaultValue!=null)&&(valuestable!=null))
	{
		var rowForDefaultValue = document.getElementById(defaultValue.value+"");
		if(rowForDefaultValue!=null)
		{
			rowForDefaultValue.style.fontWeight='bold';
		}
	}
}
function setDefaultValue()
{
	var defaultValue = document.getElementById('attributeDefaultValue');
	defaultValue.value = "";
	var defaultValueSelected = false;
	var valuestable = document.getElementById('choiceListTable');
	if(valuestable!=null)
	{
		
		var choiceListElementCnter = document.getElementById('choiceListCounter');
		var noOfElements = 0;
		if(choiceListElementCnter!=null)
		{
			noOfElements = parseInt(choiceListElementCnter.value);
		}

		var chkBoxId = "";
		var chkBox;

		for(var i=0;i<noOfElements;i++)
		{

			chkBoxId = "chkBox" + i;

			chkBox = document.getElementById(chkBoxId);

			if(chkBox!=null)
			{
				var rowofCheckBox = chkBox.parentElement.parentElement;
				if((chkBox.checked == true)&&(defaultValueSelected==false))
				{
					defaultValue.value = chkBox.value; 
					chkBox.checked = false;
					rowofCheckBox.style.fontWeight='bold';
					defaultValueSelected = true;
				}
				else
				{
					chkBox.checked = false;	
					rowofCheckBox.style.fontWeight='normal';
				}
			}
		}
 	}
}

//Added by sujay

function showFormPreview() 
{
	var entitySaved = document.getElementById('entitySaved');

	if(entitySaved!=null)
	{
		entitySaved.value="";
	}	
	var controlsForm = document.getElementById('controlsForm');
	controlsForm.action="/dynamicExtensions/LoadDataEntryFormAction.do?showFormPreview=true";
	controlsForm.submit();
}

function addFormAction()
{
	document.getElementById('mode').value = 'AddNewForm';
	//document.getElementById('formsIndexForm').submit;
}

function radioButtonClicked(obj)
{
if(obj.value == 'SingleLine')
	{
		document.getElementById('attributeNoOfRows').value="";
		document.getElementById('attributeNoOfRows').disabled=true;
		document.getElementById('noOfLines').disabled=true;
	}
	if(obj.value == 'MultiLine')
	{

		document.getElementById('attributeNoOfRows').disabled=false;
		document.getElementById('noOfLines').disabled=false;
	}
}

// Added by Chetan
function backToControlForm() 
{
	var dataEntryForm = document.getElementById('dataEntryForm');
	if(dataEntryForm != null)
	{
		dataEntryForm.action="/dynamicExtensions/LoadFormControlsAction.do";
		dataEntryForm.submit();
	}
}

function clearForm()
{
var controlsForm = document.getElementById('controlsForm');

	/*if(controlsForm.name != null)
	{
	controlsForm.name.value = "";
	}
	if(controlsForm.caption != null)
	{
	controlsForm.caption.value = "";
	}
	if(controlsForm.description != null)
	{
	controlsForm.description.value = "";
	}*/
	if(controlsForm.cssClass != null)
	{
	controlsForm.cssClass.value = "";
	}
	if(controlsForm.tooltip != null)
	{
	controlsForm.tooltip.value = "";
	}
	
	
	if(document.getElementById('attributeSize') != null)
	{
	document.getElementById('attributeSize').value = "";
	}
	if(document.getElementById('attributeDefaultValue') != null)
	{
	document.getElementById('attributeDefaultValue').value = "";
	}
	if(document.getElementById('attributeDigits') != null)
	{
	document.getElementById('attributeDigits').value = "";
	}
	if(document.getElementById('attributeDecimalPlaces') != null)
	{
	document.getElementById('attributeDecimalPlaces').value = "";
	}
	
	if(document.getElementById('attributeMeasurementUnits') != null)
	{
	document.getElementById('attributeMeasurementUnits').value = "";
	}
	if(document.getElementById('measurementUnitOther') != null)
	{
	document.getElementById('measurementUnitOther').value = "";
	}
	if(document.getElementById('format') != null)
	{
	document.getElementById('format').value = "";
	}
	if(controlsForm.attributeNoOfRows != null)
	{
	controlsForm.attributeNoOfRows.value = "";
	}
	if(controlsForm.attributenoOfCols != null)
	{
	controlsForm.attributenoOfCols.value = "";
	}
	/*if(controlsForm.attributeConceptCode != null)
	{
	controlsForm.attributeConceptCode.value = "";
	}*/
	if(controlsForm.choiceList != null)
	{
	controlsForm.choiceList.value = "";
	}
	if(document.getElementById('attributeIsPassword') != null)
	{
		document.getElementById('attributeIsPassword').value = "";
	}
	/*if(document.getElementById('attributeIdentified') != null)
	{
		document.getElementById('attributeIdentified').value = "";
	}*/
	if(document.getElementById('choiceList') != null)
	{
		document.getElementById('choiceList').value = "";
	}
	
}

function saveEntity()
{
	var entitySaved = document.getElementById('entitySaved');
	if(entitySaved!=null)
	{	
		entitySaved.value='true';
	}
	var controlsForm = document.getElementById('controlsForm');
	if(controlsForm!=null)
	{
		controlsForm.action="/dynamicExtensions/SaveEntityAction.do";
		//controlsForm.submit();	
	}
}

function loadPreviewForm()
{
	var entitySaved = document.getElementById('entitySaved');
	if(entitySaved!=null)
	{
		if(entitySaved.value=='true')
		{
			var backBtn = document.getElementById('backToPrevious');
			if(backBtn!=null)
			{
				backBtn.disabled='true';
			}
		}
	}
}

function listTypeChanged(obj)
{
	if(obj.value == 'SingleSelect')
	{
		if(document.getElementById('attributeNoOfRows')!=null)
		{
			document.getElementById('attributeNoOfRows').value="";
			document.getElementById('attributeNoOfRows').disabled=true;
		}
		if(document.getElementById('lblNumberOfRows')!=null)
		{
			document.getElementById('lblNumberOfRows').disabled=true;
		}
	}
	if(obj.value == 'MultiSelect')
	{
		if(document.getElementById('attributeNoOfRows')!=null)
		{
			document.getElementById('attributeNoOfRows').disabled=false;
		}
		if(document.getElementById('lblNumberOfRows')!=null)
		{
			document.getElementById('lblNumberOfRows').disabled=false;
		}
	}
}
function listDataTypeChanged(listControl)
{
	if(listControl!=null)
	{
		var selectedDataType = listControl.value;
		if(selectedDataType=="Yes/No")
		{
			deleteAllElementsFromChoiceTable();
			//Clear choice list
			var choiceList = document.getElementById('choiceList');
			if(choiceList!=null)
			{
				choiceList.value = "true,false";	//True and false values for yes/no flds
				addChoicesFromListToTable();
			}
			
			//Disable add btn
			var addBtn = document.getElementById('addChoiceValue');
			if(addBtn!=null)
			{
				addBtn.disabled=true;
			}
			
			//Disable delete button
			var deleteBtn = document.getElementById('deleteChoiceValue');
			if(deleteBtn!=null)
			{
				deleteBtn.disabled=true;
			}
		}
		else
		{
		
			//Enable add btn
			var addBtn = document.getElementById('addChoiceValue');
			if(addBtn!=null)
			{
				addBtn.disabled=false;
			}

			//Enable delete button
			var deleteBtn = document.getElementById('deleteChoiceValue');
			if(deleteBtn!=null)
			{
				deleteBtn.disabled=false;
			}
		}
	}
}

function deleteAllElementsFromChoiceTable()
{
	var valuestable = document.getElementById('choiceListTable');
	
	if(valuestable!=null)
	{
		var choiceListElementCnter = document.getElementById('choiceListCounter');
		var noOfElements = 0;
		if(choiceListElementCnter!=null)
		{
			noOfElements = parseInt(choiceListElementCnter.value);
		}
		
		for(i=0; i<noOfElements; i++)
		{
			var chkBoxId = "chkBox" + i;
			chkBox = document.getElementById(chkBoxId);
			if(chkBox!=null)
			{
				chkBox.checked = true;
			}
		}
		deleteElementsFromChoiceList();
	}
}

//When Date type is changed :  Disable default value txt box for None and Todays date option
function changeDateType(dateType)
{	
	if(dateType!=null)
	{
		dateTypeValue =dateType.value;
	}
	var defValueTxtBox = document.getElementById('attributeDefaultValue');
	if((dateTypeValue == "None")||(dateTypeValue == "Today"))
	{
		defValueTxtBox.disabled=true;
	}
	else
	{
		defValueTxtBox.disabled=false;
	}
}

function addDynamicData()
{
	var previewForm = document.getElementById('previewForm');
	if(previewForm!=null)
	{
		previewForm.action="/dynamicExtensions/ApplyDataEntryFormAction.do";
	}
}
function showFormDefinitionPage()
{
	var previewForm = document.getElementById('previewForm');
	previewForm.action="/dynamicExtensions/LoadFormDefinitionAction.do";
	previewForm.submit();
}
function showTooltip(text,obj,message) {
	var tooltip = "";
	var w1 = obj.scrollWidth;
	var w2 = obj.offsetWidth;
	var difference = w1-w2;
	if(difference > 0) {
		tooltip = text;
		obj.title = tooltip;
	} else {
		if(message != null)
		{ 
		  tooltip = message;
		  obj.title = tooltip;
		} else {
			if(obj.tagName != "IMG") {
			  obj.title = "";
			}  
		}
	}
}

function hideTooltip() {
     //alert('ashwini');
      el = document.getElementById("akvtooltip");
	  //alert(el.innerHTML);
	  if( el != null) 
	   el.style.visibility="hidden";
	//  el.removeNode();
}

function controlSelected(ths) {
	
	var prevRow = document.getElementById('previousControl').value;
	if (prevRow != null && prevRow != '' && prevRow != undefined) 
	{
		document.getElementById(prevRow).className = "tableItemUnselected";
	}

	document.getElementById('previousControl').value = ths.id;
	ths.className = "tableItemSelected";
	
	//Added by Preeti
	document.getElementById('controlOperation').value='Edit';
	document.getElementById('selectedControlId').value=ths.id;
	
	var controlsForm=document.getElementById('controlsForm');
    controlsForm.action='/dynamicExtensions/LoadFormControlsAction.do';
    controlsForm.submit();
}

function measurementUnitsChanged(cboMeasuremtUnits)
{
	if(cboMeasuremtUnits!=null)
	{
		var txtMeasurementUnitOther = document.getElementById('measurementUnitOther');
		if(txtMeasurementUnitOther!=null)
		{
			if(cboMeasuremtUnits.value =="other")
			{
				txtMeasurementUnitOther.disabled=false;
			}
			else
			{
				txtMeasurementUnitOther.disabled=true;
			}
		}
	}
}
function ruleSelected(ruleObject)
{
	if(ruleObject.value == 'range') 
	{
		if(ruleObject.checked ==false)
		{
			document.getElementById('min').value='';
			document.getElementById('max').value='';
		}

	}
}

function deleteControl()
{
	checkAttribute = document.controlsForm.checkAttribute;
	
	for(i = checkAttribute.length-1; i >= 0; i--)
	{
		//alert(i);
		if(checkAttribute[i].checked)
		{
			//alert(checkAttribute[i].value);
			//alert((document.getElementById(checkAttribute[i].value + "rowNum")).value);
			var startPoint = (document.getElementById(checkAttribute[i].value + "rowNum")).value;
			deleteRow('controlList', startPoint)

		}
	}

	 resetRowNum(checkAttribute);
}


function resetRowNum(checkAttribute)
{
	for(i = 0; i < checkAttribute.length; i++)
	{
		(document.getElementById(checkAttribute[i].value + "rowNum")).value = i + 1 ;
	}
}

function deleteRow(tableId, startPoint)
{
	var tab = document.getElementById(tableId);
	tab.deleteRow(startPoint);
}


function decreaseSequencenumber()
{
	checkAttribute = document.controlsForm.checkAttribute;
	for(i = 0; i < checkAttribute.length; i++)
	{
		if(checkAttribute[i].checked)
		{
			var startPoint = (document.getElementById(checkAttribute[i].value + "rowNum")).value;
			moveRowsUp('controlList',startPoint,1);
		}
	}
	resetRowNum(checkAttribute);
}



function moveRowsUp (tableId, startPoint, counter)
{
	var tab = document.getElementById(tableId);
	for (var i = 0 ; i < counter; i++) 
	{
		if (startPoint == 1)
		{
			break;
		}
		tab.moveRow(startPoint,startPoint-1);
		startPoint +=1;
	}
}


function increaseSequencenumber()
{
  	checkAttribute = document.controlsForm.checkAttribute;
	for(i = checkAttribute.length-1; i >= 0; i--)
	{
		if(checkAttribute[i].checked)
		{
			var startPoint = (document.getElementById(checkAttribute[i].value + "rowNum")).value;
			moveRowsDown('controlList',startPoint,1);
		}
	}
	resetRowNum(checkAttribute);
}

function moveRowsDown(tableId, startPoint, counter)
{
	var tab = document.getElementById(tableId);
	for (var i = 0 ; i < counter; i++) 
	{
		tab.moveRow(startPoint,parseInt(startPoint)+1);
		startPoint -=1;
	}
}

