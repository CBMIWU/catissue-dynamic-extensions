/**
 * <p>Title: Constants Class>
 * <p>Description:  This class stores the constants used in the operations in the application.</p>
 * Copyright:    Copyright (c) year
 * Company: Washington University, School of Medicine, St. Louis.
 * @author Gautam Shetty
 * @version 1.00
 * Created on Mar 16, 2005
 */

package edu.common.dynamicextensions.util.global;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;

/**
 * This class stores the constants used in the operations in the application.
 * @author Sujay Narkar
 * @author Rahul Ner
 */

public class DEConstants
{

	public static final int ENTITY_FORM_ID = 301;
	public static final int ATTRIBUTE_FORM_ID = 302;
	public static final int ENTITY_INFORMATION_FORM_ID = 303;
	public static final int ENTITY_DATA_FORM_ID = 304;
	public static final int ENTITY_DATA_INFO_FORM_IDI = 305;
	public static final int ENTITY_SELECTION_FORM_ID = 306;
	public static final String DEPRECATED = "deprecated_";
	public static final String FORM_DEFINITION_FORM = "formDefinitionForm";
	public static final String CONTROLS_FORM = "controlsForm";
	public static final String CACHE_MAP = "cacheMap";
	public static final String ADD_CONTROLS_TO_FORM = "addControlsToForm";
	public static final String SHOW_BUILD_FORM_JSP = "showBuildFormJSP";
	public static final String EDIT_SUB_FORM_PAGE = "editSubForm";
	public static final String LOAD_FORM_PREVIEW_ACTION = "loadFormPreviewAction";
	public static final String SHOW_FORM_PREVIEW_JSP = "showFormPreviewJSP";
	public static final String BUILD_FORM = "buildForm";
	public static final String SAVE_FORM = "saveForm";
	public static final String SHOW_CREATE_FORM_JSP = "showCreateFormView";
	public static final String CONTROL_SELECTED_ACTION = "controlSelectedAction";
	public static final String SYSTEM_EXCEPTION = "systemException";
	public static final String APPLICATION_EXCEPTION = "applicationException";
	public static final String CONTAINER_INTERFACE = "containerInterface";
	public static final String CONTAINER_STACK = "containerStack";
	public static final String VALUE_MAP_STACK = "valueMapStack";
	public static final String SCROLL_TOP_STACK = "scrollTopStack";
	public static final String SCROLL_TOP = "scrollTop";
	public static final String SCROLL_POSITION = "scrollPostion";

	public static final String ENTITYGROUP_INTERFACE = "entityGroupInterface";
	public static final String CURRENT_CONTAINER_NAME = "currentContainerName";
	public static final String ERRORS_LIST = "errorList";
	public static final String SHOW_DYEXTN_HOMEPAGE = "showDynamicExtensionsHomePage";
	public static final String SHOW_EDIT_RECORDS_PAGE = "showRecordListPage";
	public static final String SUCCESS = "success";
	public static final String ADD_SUB_FORM = "addSubForm";

	public static final String CONTROL_OPERATION = "controlOperation";
	public static final String SELECTED_CONTROL_ID = "selectedControlId";
	public static final String USER_SELECTED_TOOL = "userSelectedTool";
	public static final String ADD_NEW_FORM = "AddNewForm";
	public static final String EDIT_FORM = "EditForm";
	public static final String ADD_SUB_FORM_OPR = "AddSubForm";
	public static final String EDIT_SUB_FORM_OPR = "EditSubForm";
	public static final String INSERT_DATA = "insertData";
	public static final String CALLBACK_URL = "callbackURL";
	public static final String JANUARY = "Jan";
	public static final String FEBRUARY = "Feb";
	public static final String MARCH = "Mar";
	public static final String APRIL = "Apr";
	public static final String MAY = "May";
	public static final String JUNE = "Jun";
	public static final String JULY = "Jul";
	public static final String AUGUST = "Aug";
	public static final String SEPTEMBER = "Sep";
	public static final String OCTOBER = "Oct";
	public static final String NOVEMBER = "Nov";
	public static final String DECEMBER = "Dec";

	/** The Constant COMBOBOX_IDENTIFER. */
	public static final String COMBOBOX_IDENTIFER = "comboBoxId";

	//Constant for groupName prefix

	public static final String GROUP_PREFIX = "Group_";

	//public static final int DATA_TABLE_STATE_CREATED = 1;
	public static final int DATA_TABLE_STATE_CREATED = 1;
	public static final int DATA_TABLE_STATE_NOT_CREATED = 2;
	public static final int DATA_TABLE_STATE_ALREADY_PRESENT = 3;

	public static final String OBJ_IDENTIFIER = "id";
	public static final String IDENTIFIER = "identifier";
	public static final String LIST_OF_CONTAINER = "listofContainer";
	public static final String ISCATEGORY = "_IsCategory";
	public static final String COLLECTIONATTRIBUTECLASS = "CollectionAttributeClass";
	public static final String COLLECTIONATTRIBUTE = "collectionAttribute";
	public static final String COLLECTIONATTRIBUTEROLE = "collectionAttributeRole";
	public static final String COLLECTIONATTRIBUTE_OLD = "CollectionAttribute";
	public static final String TRUE = "true";
	public static final String FALSE = "false";

	public static final String MYSQL_DATABASE = "MYSQL";
	public static final String DB2_DATABASE = "DB2";
	public static final String ORACLE_DATABASE = "ORACLE";
	public static final String POSTGRESQL_DATABASE = "DATABASE";
	public static final String MSSQLSERVER_DATABASE = "MSSQLSERVER";

	public static final String AUDIT_TABLE_NAME = "DYEXTN_SQL_AUDIT";
	public static final String AUDIT_DATE_COLUMN = "AUDIT_DATE";
	public static final String AUDIT_QUERY_COLUMN = "QUERY_EXECUTED";
	public static final String AUDIT_USER_ID_COLUMN = "USER_ID";
	public static final String OPENING_SQUARE_BRACKET = "[";
	public static final String CLOSING_SQUARE_BRACKET = "]";
	public static final String CATEGORY_ENTITY_ID = "CATEGORY_ENTITY_ID";
	public static final String PATH_TABLE_NAME = "DYEXTN_PATH";
	public static final String ABSTRACT_METADATA_TABLE_NAME = "DYEXTN_ABSTRACT_METADATA";
	public static final String NAME = "NAME";
	public static final String APPLICATION_RESOURCES = "ApplicationResources";
	public static final int TWO = 2;
	public static final String DATE = "date";
	public static final String DATE_RANGE = "dateRange";
	public static final String ALLOW_FUTURE_DATE = "allowfuturedate";
	public static final String RANGE = "range";
	public static final String CANCEL = "cancel";
	public static final String IS_DIRTY = "isDirty";
	public static final String BREAD_CRUMB_POSITION = "breadCrumbPosition";
	public static final String HTML_SPACE = "&nbsp;";
	public static final String PRINTCALANDER = "printCalendar";
	public static final String PROCESS_AJAX_CAL_SCRIPT = "processAjaxCalScript";
	public static final String PRINT_TIME_CAL_FOR_AJAX = "printTimeCalendarForAjax";
	public static final String PRINT_MON_YEAR_CAL_FOR_AJAX = "printMonthYearCalendarForAjax";
	public static final String PRINT_YEAR_CAL_FOR_AJAX = "printYearCalendarForAjax";
	public static final String CLIP_BOARD_DATA = "clipboradData";
	public static final String HTML_SCRIPT_TAG = "</SCRIPT>";
	public static final String SINGLE_QUOTE = "'";
	public static final String OPEN_ROUND_BRACE = "(";
	public static final String CLOSED_ROUND_BRACE = ")";
	public static final String INDEX = "index";
	public static final String COMMA = ",";
	public static final String CATEGORY = "category";
	public static final String CONTAINER = "container";
	public static final String SURVEY_MODE = "surveymode";
	public static final String LAYOUT = "layout";
	public static final String PAGE_ID = "pageId";
	public static final String CATEGORY_ID = "categoryId";
	public static final String CONTAINER_ID = "containerId";
	public static final int CONTROL_DEFAULT_VALUE = 10;
	public static final String ATTRIBUTE_IDENTIFIER = "attributeIdentifier";
	public static final String CONTAINER_IDENTIFIER = "containerIdentifier";
	public static final String RECORD_IDENTIFIER = "recordIdentifier";
	public static final String RECORD_IDENTIFIER_URL_PARAM = "&recordIdentifier=";
	
	public static final String JAVA_UTIL_COLLECTION_CLASS = "java.util.Collection";
	public static final String GET_ID = "getId";
	public static final String INVALID_CONTROL_VALUE = "errors.invalidInputForControl";
	public static final String VIOLATING_PROPERTY_NAMES = "propertyNamesList";

	public static final String DATA_INSERTION_ERROR_MESSAGE = "Error while inserting data";

	public static final String APPLICATION_ERROR_MSGS = "ApplicationErrorMsgs";

	public static final Collection<String> TRUE_VALUE_LIST;

	public static final Collection<String> FALSE_VALUE_LIST;

	static
	{
		Collection<String> trueValues = new HashSet<String>();
		trueValues.add("TRUE");
		trueValues.add("T");
		trueValues.add("1");
		trueValues.add("YES");
		TRUE_VALUE_LIST = Collections.unmodifiableCollection(trueValues);

		Collection<String> falseValues = new HashSet<String>();
		falseValues.add("FALSE");
		falseValues.add("F");
		falseValues.add("0");
		falseValues.add("NO");
		FALSE_VALUE_LIST = Collections.unmodifiableCollection(falseValues);

	}

	public enum Cardinality {
		ZERO(0), ONE(1), MANY(100);

		Integer value;

		Cardinality(Integer value)
		{
			this.value = value;
		}

		public Integer getValue()
		{
			return this.value;
		}

		public static Cardinality get(Integer value)
		{
			Cardinality[] cardinalities = Cardinality.values();

			for (Cardinality cardinality : cardinalities)
			{
				if (cardinality.getValue().equals(value))
				{
					return cardinality;
				}
			}
			return null;
		}
	};

	public enum AssociationDirection {
		SRC_DESTINATION("SRC_DESTINATION"), BI_DIRECTIONAL("BI_DIRECTIONAL");

		String value;

		AssociationDirection(String value)
		{
			this.value = value;
		}

		public String getValue()
		{
			return this.value;
		}

		public static AssociationDirection get(String value)
		{
			AssociationDirection[] associations = AssociationDirection.values();

			for (AssociationDirection association : associations)
			{
				if (association.getValue().equalsIgnoreCase(value))
				{
					return association;
				}
			}
			return null;
		}
	};

	public enum AssociationType {
		ASSOCIATION("ASSOCIATION"), CONTAINTMENT("CONTAINTMENT");

		String value;

		AssociationType(String value)
		{
			this.value = value;
		}

		public String getValue()
		{
			return this.value;
		}

		public static AssociationType get(String value)
		{
			AssociationType[] associationTypes = AssociationType.values();

			for (AssociationType associationType : associationTypes)
			{
				if (associationType.getValue().equalsIgnoreCase(value))
				{
					return associationType;
				}
			}
			return null;
		}
	}

	/**
	 * enum to define strategies of the inheritance
	 */
	public enum InheritanceStrategy {
		TABLE_PER_CONCRETE_CLASS(1), TABLE_PER_HEIRARCHY(2), TABLE_PER_SUB_CLASS(3);

		int value;

		InheritanceStrategy(int value)
		{
			this.value = value;
		}

		public int getValue()
		{
			return this.value;
		}

		public static InheritanceStrategy get(int value)
		{
			InheritanceStrategy[] inheritanceStrategies = InheritanceStrategy.values();

			for (InheritanceStrategy inheritanceStrategy : inheritanceStrategies)
			{
				if (inheritanceStrategy.getValue() == value)
				{
					return inheritanceStrategy;
				}
			}
			return null;
		}
	}

	public enum ValueDomainType {
		ENUMERATED("ENUMERATED"), NON_ENUMERATED("NON_ENUMERATED");

		String value;

		ValueDomainType(String value)
		{
			this.value = value;
		}

		public String getValue()
		{
			return this.value;
		}

		public static ValueDomainType get(String value)
		{
			ValueDomainType[] valueDomainTypes = ValueDomainType.values();

			for (ValueDomainType valueDomainType : valueDomainTypes)
			{
				if (valueDomainType.getValue().equalsIgnoreCase(value))
				{
					return valueDomainType;
				}
			}
			return null;
		}
	}

	public static final String XML_PACKAGE_NAME = "edu.common.dynamicextensions.util.xml";

	/**
	 * Constant for directory.
	 */
	public static final String TEMPLATE_DIR = "XMLAndCSVTemplate";
	/**
	 * Constant for CSV extension.
	 */
	public static final String CSV_SUFFIX = ".csv";
	/**
	 * Constant for XML extension.
	 */
	public static final String XML_SUFFIX = ".xml";

	public static final String ON_FORM_LOAD = "onFormLoad";

	public static final String FILE_NAME= "fileName";

	public static final String SUFFIX_FILENAME= "_FILE_NAME";

	public static final String CONTENT_TYPE = "contentType";

	public static final String SUFFIX_CONTENT_TYPE = "_CONTENT_TYPE";

	public static final String ENTITY_ID = "entityId";

	public static final String ENTITY_ID_COL_NAME = "ENTITY_ID";

	public static final String IDENTIFIER_COL_NAME = "IDENTIFIER";

	public static final String RECORD_ID = "recordId";
	
	public static final String RECORD_ID_URL_PARAM = "&recordId=";
	
	public static final String AUDIT_INSERT = "INSERT";

	public static final String AUDIT_UPDATE = "UPDATE";

	public static final String AUDIT_TABLENAME = "tableName";

	public static final String AUDIT_COL_NAME_MAP = "attributeColumnNameMap";
	
	public static final String REQUIRED = "required";

	public static final String SESSION_DATA = "sessionDataBean";
	
	public static final String FORM_CONTEXT_ID = "formContextId";
	
	public static final String RECORD_ENTRY_ENTITY_ID = "recEntryEntityId";
	
	public static final String FORM_URL = "formUrl"; 
	
	public static final String DE_URL = "deUrl";
	public static final String HOOK_OBJECT_RECORD_ID = "hookObjectRecordId";
	
	public static final String RECORD_ID_FROM_FORM_CONTEXT_ID = "record.id.from.form.context.id";
	public static final String RECORD_ID_FOR_PARTICIPANT_FROM_FORM_CONTEXT_ID = "record.id.for.participant.from.form.context.id";
	public static final String CONTAINER_ID_FROM_FORM_CONTEXT_ID = "container.id.from.form.context.id";
	
	public static final String DESQL_XSD_FILENAME = "DESql.xsd";
	
	public static final String DESQL_XML_FILENAME = "DESql.xml";
	
	public static final String SQL_PVS = "sqlPvs";
	
	public static final String PV_TYPE = "pvtype";
	
	public static final String PV_PROCESSOR = "pvprocessor";
	
	public static final String PV_POSTFIX = "_pv";
	
	public static final String EQUALS = "Equals";
	public static final String NOT_EQUALS ="Not Equals";
	public static final String BETWEEN = "Between";
	public static final String LESS_THAN = "Less than";
	public static final String LESS_THAN_OR_EQUAL_TO ="Less than or Equal to";
	public static final String GREATER_THAN = "Greater than";
	public static final String GREATER_THAN_OR_EQUAL_TO = "Greater than or Equal to";
	public static final String IS_PRESENT = "Is Present";
	public static final String IS_NOT_PRESENT = "Is Not Present";
	public static final String STARTS_WITH = "Starts With";
	public static final String ENDS_WITH = "Ends With";
	public static final String CONTAINS = "Contains";
	public static final String CONTEXT_PATH = "contextPath";
	public static final String UPDATE_RESPONSE = "updateResponse";
	public static final String DISPLAY_UNANSWERED_QUESTION = "firstUnansweredQuestion";
	
}