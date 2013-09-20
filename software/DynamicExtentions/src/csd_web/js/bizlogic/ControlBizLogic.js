/**
 * 
 */

var ControlBizLogic = {

	deleteControl : function(model) {

		/*
		 * var controlName = model.get('controlName'); if
		 * (model.get('parentName') != undefined) { controlName =
		 * model.get('parentName') + "." + controlName; }
		 */
		Main.formView.getFormModel().deleteControl(model.get('editName'));

		Main.treeView.getTree().deleteItem(model.get('formTreeNodeId'), true);
	},

	getListOfCurrentNumericControls : function(isNameCaptionPair) {
		var controls = new Array();
		for ( var key in Main.formView.getFormModel().get(
				'controlObjectCollection')) {
			var control = Main.formView.getFormModel().get(
					'controlObjectCollection')[key];
			if (control.get('type') == "numericField") {
				if (isNameCaptionPair) {
					controls.push({
						'name' : control.get('controlName'),
						'caption' : control.get('caption')
					});
				} else {
					controls.push(control.get('controlName'));
				}
			}

			if (control.get('type') == "subForm") {
				for ( var subKey in control.get('subForm').get(
						'controlObjectCollection')) {
					var subControl = control.get('subForm').get(
							'controlObjectCollection')[subKey];
					var subControlName = subControl.get('controlName');
					if (subControl.get('type') == "numericField") {
						if (isNameCaptionPair) {
							controls.push({
								'name' : control.get('controlName') + "."
										+ subControlName,
								'caption' : control.get('caption')
							});
						} else {
							controls.push(control.get('controlName') + "."
									+ subControlName);
						}
					}
				}
			}
		}
		return controls;
	},

	getListOfCurrentControls : function(isNameCaptionPair) {
		var controls = new Array();
		for ( var key in Main.formView.getFormModel().get(
				'controlObjectCollection')) {
			var control = Main.formView.getFormModel().get(
					'controlObjectCollection')[key];

			if (isNameCaptionPair) {
				controls.push({
					'name' : control.get('controlName'),
					'caption' : control.get('caption') + " ("
							+ control.get('controlName') + ")"
				});
			} else {
				controls.push(control.get('controlName'));
			}

			if (control.get('type') == "subForm") {
				for ( var subKey in control.get('subForm').get(
						'controlObjectCollection')) {
					var subControl = control.get('subForm').get(
							'controlObjectCollection')[subKey];
					var subControlName = subControl.get('controlName');

					if (isNameCaptionPair) {
						controls.push({
							'name' : control.get('controlName') + "."
									+ subControlName,
							'caption' : subControl.get('caption') + " ("
									+ subControlName + ")"
						});

					} else {
						controls.push(control.get('controlName') + "."
								+ subControlName);
					}
				}
			}
		}
		return controls;
	},

	getListOfPvNameValuePairs : function(control) {
		var pvList = new Array();
		for ( var key in control.get('pvs')) {
			pvList.push({
				'name' : control.get('pvs')[key].value,
				'caption' : control.get('pvs')[key].value
			});
		}
		return pvList;
	},

	getCaptionFromControlName : function(controlName) {

		if (controlName != undefined && controlName != null)
			return ControlBizLogic.getControlFromControlName(controlName).get(
					'caption');
		else
			return null;
	},

	getControlTypeFromControlName : function(controlName) {

		if (controlName != undefined && controlName != null)
			return ControlBizLogic.getControlFromControlName(controlName).get(
					'type');
		else
			return null;
	},

	getControlFromControlName : function(controlName) {
		if (controlName != undefined && controlName != null
				&& controlName != "") {
			var controlNames = controlName.split(".");
			if (controlNames.length == 1) {
				if (Main.formView.getFormModel().get('controlObjectCollection')[controlName] != undefined) {
					return Main.formView.getFormModel().get(
							'controlObjectCollection')[controlName];
				}
			} else {
				if (Main.formView.getFormModel().get('controlObjectCollection')[controlNames[0]]
						.get('subForm').get('controlObjectCollection')[controlNames[1]] != undefined) {
					return Main.formView.getFormModel().get(
							'controlObjectCollection')[controlNames[0]].get(
							'subForm').get('controlObjectCollection')[controlNames[1]];
				}
			}
		}
	},

	createControlNode : function(name, controlName, type, model, copyControl) {

		// Check for selected control type, if its subform then add to it else
		// add to main form.
		var selectedNodeId = Main.treeView.getTree().getSelectedItemId();
		var selectedNodeControlType = Main.treeView.getTree().getUserData(
				selectedNodeId, "controlType");
		var selectedNodeControlName = Main.treeView.getTree().getUserData(
				selectedNodeId, "controlName");
		var status = "save";

		if (selectedNodeId == undefined || selectedNodeId == "") {
			// no node has been selected, hence add it to main form
			var editModel = Main.formView.getFormModel().getControl(
					model.get('controlName'));
			if (editModel == undefined) {
				var id = ControlBizLogic.createTreeNode(1, name, controlName,
						type);
				model.set({
					formTreeNodeId : id
				});
			}

			Main.formView.getFormModel().editControl(model.get('controlName'),
					model);

		} else {
			if (selectedNodeControlType == "subForm") {
				// add it to the sub form
				if (type == "subForm" || type == "pageBreak") {
					Main.currentFieldView.setErrorMessageHeader();
					$("#messagesDiv")
							.append(
									Utility.messageSpace
											+ "Cannot add a sub form or a page break within another sub form");
					status = "error";
				} else {
					var editModel = Main.formView.getFormModel().getControl(
							selectedNodeControlName + "."
									+ model.get('controlName'));
					if (editModel == undefined) {
						var id = ControlBizLogic.createTreeNode(selectedNodeId,
								name, controlName, type);
						model.set({
							formTreeNodeId : id
						});
					}
					Main.formView.getFormModel().editControl(
							selectedNodeControlName + "."
									+ model.get('controlName'), model);

				}
			} else {
				// add it to the main form
				var editModel = Main.formView.getFormModel().getControl(
						model.get('controlName'));
				if (editModel == undefined) {
					var id = ControlBizLogic.createTreeNode(1, name,
							controlName, type);
					model.set({
						formTreeNodeId : id
					});
				}

				Main.formView.getFormModel().editControl(
						model.get('controlName'), model);

			}

		}
		GlobalMemory.nodeCounter++;
		if (copyControl == true) {
			this.copySubForm(model);
		}

		return status;
	},

	createTreeNode : function(parentId, name, controlName, type) {
		var id = 10 + GlobalMemory.nodeCounter;

		Main.treeView.getTree().insertNewChild(parentId, id, name, 0, 0, 0, 0,
				"CHILD,CHECKED");

		Main.treeView.getTree().setUserData(id, "controlName", controlName);
		Main.treeView.getTree().setUserData(id, "controlType", type);
		if (type == "pageBreak") {
			Main.treeView.getTree().setItemStyle(id,
					"font-weight:bold; font-style:italic; font-color:#505050;");
		}

		return id;
	},

	copySubForm : function(model) {
		if (model.get('type') == "subForm") {

			// update controls and set their attributes
			var subForm = new Models.Form(model.get('subForm').toJSON());
			var subFrm = model.get('subForm');

			for ( var key in subFrm.get('controlObjectCollection')) {

				var subFormControl = new Models.Field(subFrm
						.get('controlObjectCollection')[key].toJSON());

				var _editName = model.get('controlName') + "."
						+ subFormControl.get('controlName');
				var _treeDisplayName = subFormControl.get('caption') + "("
						+ subFormControl.get('controlName') + ")";

				subFormControl.set({
					editName : _editName
				});

				var id = ControlBizLogic.createTreeNode(model
						.get('formTreeNodeId'), _treeDisplayName,
						subFormControl.get('controlName'), subFormControl
								.get('type'));

				subFormControl.set({
					formTreeNodeId : id
				});

				delete subForm.get('controlObjectCollection')[key];
				subForm.get('controlObjectCollection')[key] = subFormControl;
				var _subForm = subForm;
				GlobalMemory.nodeCounter++;
			}
			model.set({
				subForm : _subForm
			});
		}
	},

	/*
	 * Generate the next sequence number by retrieving the max sequence number
	 * in the model and incrementing it by 1
	 */
	getNextSequenceNumber : function() {
		var sequenceNumber = null;
		if (Main.formView.getFormModel().get('controlObjectCollection') != null) {
			sequenceNumber = 0;
			for ( var key in Main.formView.getFormModel().get(
					'controlObjectCollection')) {
				var tempSequenceNum = Main.formView.getFormModel().get(
						'controlObjectCollection')[key].get('sequenceNumber');
				if (tempSequenceNum > sequenceNumber) {
					sequenceNumber = tempSequenceNum;
				}
			}
		}

		return sequenceNumber == null ? 0 : (sequenceNumber + 1);
	},

	/*
	 * update pv file name
	 */
	addUploadedPvFileNameToCurrentModel : function(fileLocation) {
		Main.currentFieldView.getModel().set({
			pvFile : fileLocation
		});
	},

	/*
	 * Handler for form tree node click
	 */
	formTreeNodeClickHandler : function(id) {
		// De select selected control

		// selected node is a form
		if (id == 1) {
			Main.mainTabBarView.selectTab("summaryTab");
		}
		Utility.resetCarouselControlSelect();

		// Show the control tab if not selected
		Main.mainTabBarView.selectTab("controlTab");

		if (Main.currentFieldView != null) {
			// Erase the existing view
			Main.currentFieldView.destroy();
		}

		// Get the relevant model
		var fieldModel = ControlBizLogic.getSelectedModelFromTree(id);

		ControlBizLogic.populateViewWithControl(fieldModel);
		// Select the carousel's control #FFFFFF
		Main.mainTabBarView.highlightSelectedControlType();

		// enable delete button of the selected control.
		Main.currentFieldView.enableDisableButton("delete", false);
		Main.currentFieldView.enableDisableButton("copy", false);
		Main.currentFieldView.setSubmitCaptionToUpdate();

		return true;
	},

	createCopyControl : function(fieldModel) {

		if (Main.currentFieldView != null) {
			// Erase the existing view
			Main.currentFieldView.destroy();
		}
		var control = new Models.Field(fieldModel.toJSON());
		control.set({
			editName : undefined,
			formTreeNodeId : undefined,
			controlName : undefined,
			caption : undefined,
			copy : true
		});
		this.populateViewWithControl(control);
	},

	populateViewWithControl : function(fieldModel) {
		// Create a view with the relevant model
		Main.currentFieldView = Views.showControl('controlContainer',
				fieldModel);

		// Populate pv grid
		// reset the pv counter

		switch (fieldModel.get('type')) {
		case "radioButton":

		case "listBox":

		case "multiselectBox":

		case "comboBox":

		case "multiselectCheckBox":
			// show pv data in grid
			ControlBizLogic.showPvDataForCurrentControl(fieldModel);
			break;
		default:
		}
	},

	getSelectedModelFromTree : function(id) {
		var parentId = Main.treeView.getTree().getParentId(id);
		var controlName = Main.treeView.getTree()
				.getUserData(id, "controlName");

		var controlType = Main.treeView.getTree()
				.getUserData(id, "controlType");
		var parentControlType = Main.treeView.getTree().getUserData(parentId,
				"controlType");
		var parentControlName = null;
		var modifiedControlName = controlName;
		if (parentControlType == "subForm") {
			parentControlName = Main.treeView.getTree().getUserData(parentId,
					"controlName");
			modifiedControlName = parentControlName + "." + modifiedControlName;
		}
		var control = Main.formView.getFormModel().getControl(
				modifiedControlName);
		// set parent name if it is a sub form's control
		if (parentControlName != null) {
			control.set({
				parentName : parentControlName
			});
		}
		// set tree node id
		control.set({
			treeNodeId : id
		});
		return control;
	},

	showPvDataForCurrentControl : function(model) {
		GlobalMemory.pvCounter = 0;
		// iterate through the pv list
		var defaultPv = model.get('defaultPv');
		for ( var cntr in model.get('pvs')) {
			// get the i'th pv
			var pv = model.get('pvs')[cntr];
			// add the i'th pv
			var rowId = model.get('controlName') + GlobalMemory.pvCounter;
			var defaultPvRadio = "<input type='radio' name='defaultPv' value='"
					+ rowId + "'>";
			Main.currentFieldView.getPvGrid().addRow(
					rowId,
					[ pv.value, pv.numericCode, pv.definition,
							pv.definitionSource, pv.conceptCode,
							defaultPvRadio, "saved" ]);
			if (defaultPv != undefined) {
				if (pv.value == defaultPv.value) {

					$('input:radio[name="defaultPv"][value="' + rowId + '"]')
							.attr('checked', true);
				}
			}
			ControlBizLogic.initDefaultPv();
			// increment the pv counter
			GlobalMemory.pvCounter++;
		}
	},

	/*
	 * Create an empty control for editing
	 */
	createControl : function(controlType) {

		var controlModel = new Models.Field({
			type : controlType,
			pvs : {},
			sequenceNumber : ControlBizLogic.getNextSequenceNumber(),
			controlName : ""
		});
		GlobalMemory.sequenceNumCntr++;

		return controlModel;
	},

	//

	/*
	 * Handler for "onSelect" event of tab
	 */

	csdControlsTabSelectHandler : function(id) {
		if (id == "designMode") {
			Routers.populateDesignModeTab();
		} else if (id == "previewTab") {
			Main.formView.loadModelInSessionForPreview();
		}

		else if (id == "advancedControlPropertiesTab") {

			Main.advancedControlsView.clearMessage();
			Main.formView.loadModelInSession();
			AdvancedControlPropertiesBizLogic.refreshListsWithControls();
			Main.advancedControlsView.refreshFormulaField();
			$('#availableFields1').prop(
					'title',
					ControlBizLogic.getCaptionFromControlName($(
							'#availableFields1').val()));

		}
	},

	isControlNameUniqueInForm : function(control) {

		// get selected control's properties to distinguish between main form
		// controls and sub form controls
		var selectedNodeId = Main.treeView.getTree().getSelectedItemId();
		var selectedNodeControlType = Main.treeView.getTree().getUserData(
				selectedNodeId, "controlType");
		var selectedNodeControlName = Main.treeView.getTree().getUserData(
				selectedNodeId, "controlName");
		var isUnique = false;

		if (control.has('editName')) {
			// edit case
			var editName = control.get('editName').split(".");
			var controlName = control.get('controlName');
			// edit control
			if (editName.length > 1) {
				controlName = editName[0] + "." + controlName;
			}
			if (Main.formView.getFormModel().getControl(controlName) == undefined) {
				isUnique = true;
			}

		} else {
			// add case
			if ((selectedNodeControlType == undefined && selectedNodeControlName == undefined)
					|| (selectedNodeControlType == "" && selectedNodeControlName == "")) {
				if (selectedNodeControlType == "subForm") {
					// subForm control add case

					if (Main.formView.getFormModel().getControl(
							selectedNodeControlName + "."
									+ control.get('controlName')) == undefined) {
						isUnique = true;
					}

				} else {
					if (Main.formView.getFormModel().getControl(
							control.get('controlName')) == undefined) {
						isUnique = true;
					}
				}
			} else {
				if (Main.formView.getFormModel().getControl(
						control.get('controlName')) == undefined) {
					isUnique = true;
				}
			}
		}
		return isUnique;

	},
	initDefaultPv : function() {

		$('input:radio[name="defaultPv"]').change(
				function() {
					var _defaultPv = Main.currentFieldView.getModel()
							.get('pvs')[$(this).val()];
					Main.currentFieldView.getModel().set({
						defaultPv : _defaultPv
					});
				});

	}

}