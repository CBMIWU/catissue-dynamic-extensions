
package edu.common.dynamicextensions.domain;

import static edu.common.dynamicextensions.entitymanager.DynamicExtensionsQueryBuilderConstantsInterface.ASSOCIATION_COLUMN_PREFIX;
import static edu.common.dynamicextensions.entitymanager.DynamicExtensionsQueryBuilderConstantsInterface.ASSOCIATION_NAME_PREFIX;
import static edu.common.dynamicextensions.entitymanager.DynamicExtensionsQueryBuilderConstantsInterface.CATEGORY_TABLE_NAME_PREFIX;
import static edu.common.dynamicextensions.entitymanager.DynamicExtensionsQueryBuilderConstantsInterface.COLUMN_NAME_PREFIX;
import static edu.common.dynamicextensions.entitymanager.DynamicExtensionsQueryBuilderConstantsInterface.CONSTRAINT;
import static edu.common.dynamicextensions.entitymanager.DynamicExtensionsQueryBuilderConstantsInterface.TABLE_NAME_PREFIX;
import static edu.common.dynamicextensions.entitymanager.DynamicExtensionsQueryBuilderConstantsInterface.UNDERSCORE;

import java.util.Date;

import edu.common.dynamicextensions.domain.databaseproperties.ColumnProperties;
import edu.common.dynamicextensions.domain.databaseproperties.ConstraintProperties;
import edu.common.dynamicextensions.domain.databaseproperties.TableProperties;
import edu.common.dynamicextensions.domain.userinterface.CategoryAssociationControl;
import edu.common.dynamicextensions.domain.userinterface.CheckBox;
import edu.common.dynamicextensions.domain.userinterface.ComboBox;
import edu.common.dynamicextensions.domain.userinterface.Container;
import edu.common.dynamicextensions.domain.userinterface.ContainmentAssociationControl;
import edu.common.dynamicextensions.domain.userinterface.DataGrid;
import edu.common.dynamicextensions.domain.userinterface.DatePicker;
import edu.common.dynamicextensions.domain.userinterface.FileUploadControl;
import edu.common.dynamicextensions.domain.userinterface.ListBox;
import edu.common.dynamicextensions.domain.userinterface.RadioButton;
import edu.common.dynamicextensions.domain.userinterface.TextArea;
import edu.common.dynamicextensions.domain.userinterface.TextField;
import edu.common.dynamicextensions.domain.userinterface.View;
import edu.common.dynamicextensions.domain.validationrules.Rule;
import edu.common.dynamicextensions.domain.validationrules.RuleParameter;
import edu.common.dynamicextensions.domaininterface.AssociationDisplayAttributeInterface;
import edu.common.dynamicextensions.domaininterface.AssociationInterface;
import edu.common.dynamicextensions.domaininterface.AttributeInterface;
import edu.common.dynamicextensions.domaininterface.BooleanValueInterface;
import edu.common.dynamicextensions.domaininterface.ByteArrayValueInterface;
import edu.common.dynamicextensions.domaininterface.CaDSRDEInterface;
import edu.common.dynamicextensions.domaininterface.CaDSRValueDomainInfoInterface;
import edu.common.dynamicextensions.domaininterface.CategoryAssociationInterface;
import edu.common.dynamicextensions.domaininterface.CategoryAttributeInterface;
import edu.common.dynamicextensions.domaininterface.CategoryEntityInterface;
import edu.common.dynamicextensions.domaininterface.CategoryInterface;
import edu.common.dynamicextensions.domaininterface.DateValueInterface;
import edu.common.dynamicextensions.domaininterface.DoubleValueInterface;
import edu.common.dynamicextensions.domaininterface.EntityGroupInterface;
import edu.common.dynamicextensions.domaininterface.EntityInterface;
import edu.common.dynamicextensions.domaininterface.FloatValueInterface;
import edu.common.dynamicextensions.domaininterface.IdGeneratorInterface;
import edu.common.dynamicextensions.domaininterface.IntegerValueInterface;
import edu.common.dynamicextensions.domaininterface.LongValueInterface;
import edu.common.dynamicextensions.domaininterface.ObjectAttributeRecordValueInterface;
import edu.common.dynamicextensions.domaininterface.PathInterface;
import edu.common.dynamicextensions.domaininterface.RoleInterface;
import edu.common.dynamicextensions.domaininterface.SemanticPropertyInterface;
import edu.common.dynamicextensions.domaininterface.ShortValueInterface;
import edu.common.dynamicextensions.domaininterface.StringValueInterface;
import edu.common.dynamicextensions.domaininterface.TaggedValueInterface;
import edu.common.dynamicextensions.domaininterface.databaseproperties.ColumnPropertiesInterface;
import edu.common.dynamicextensions.domaininterface.databaseproperties.ConstraintPropertiesInterface;
import edu.common.dynamicextensions.domaininterface.databaseproperties.TablePropertiesInterface;
import edu.common.dynamicextensions.domaininterface.userinterface.CategoryAssociationControlInterface;
import edu.common.dynamicextensions.domaininterface.userinterface.CheckBoxInterface;
import edu.common.dynamicextensions.domaininterface.userinterface.ComboBoxInterface;
import edu.common.dynamicextensions.domaininterface.userinterface.ContainerInterface;
import edu.common.dynamicextensions.domaininterface.userinterface.ContainmentAssociationControlInterface;
import edu.common.dynamicextensions.domaininterface.userinterface.DataGridInterface;
import edu.common.dynamicextensions.domaininterface.userinterface.DatePickerInterface;
import edu.common.dynamicextensions.domaininterface.userinterface.FileUploadInterface;
import edu.common.dynamicextensions.domaininterface.userinterface.ListBoxInterface;
import edu.common.dynamicextensions.domaininterface.userinterface.RadioButtonInterface;
import edu.common.dynamicextensions.domaininterface.userinterface.TextAreaInterface;
import edu.common.dynamicextensions.domaininterface.userinterface.TextFieldInterface;
import edu.common.dynamicextensions.domaininterface.userinterface.ViewInterface;
import edu.common.dynamicextensions.domaininterface.validationrules.RuleInterface;
import edu.common.dynamicextensions.domaininterface.validationrules.RuleParameterInterface;
import edu.common.dynamicextensions.util.IdGeneratorUtil;

/**
 * This is a singleton class which provides methods for generating domain
 * objects. For each domain object a create method is provided.
 *
 * @author sujay_narkar
 *
 */
public class DomainObjectFactory
{

	/**
	 * Domain Object Factory Instance
	 */
	private static DomainObjectFactory domainObjectFactory;

	/**
	 * Empty Constructor
	 */
	protected DomainObjectFactory()
	{
	}

	/**
	 * This method returns the instance of SegmentationDomainElementFactory.
	 *
	 * @return the instance of SegmentationDomainElementFactory.
	 */
	public static synchronized DomainObjectFactory getInstance()
	{
		if (domainObjectFactory == null)
		{
			domainObjectFactory = new DomainObjectFactory();
		}
		return domainObjectFactory;
	}

	/**
	 * Mock entity manager can be placed in the entity manager using this
	 * method.
	 *
	 * @param entityManager
	 */
	public void setInstance(DomainObjectFactory domainObjectFactory)
	{
		DomainObjectFactory.domainObjectFactory = domainObjectFactory;

	}

	/**
	 * This method creates an object of Entity.
	 *
	 * @return an instance of Entity.
	 */
	public EntityInterface createEntity()
	{
		Entity entity = new Entity();
		entity.setCreatedDate(new Date());
		entity.setLastUpdated(entity.getCreatedDate());
		entity.setTableProperties(createTableProperties());
		return entity;
	}

	/**
	 * This method creates an object of EntityGroup
	 *
	 * @return an instance of EntityGroup.
	 */
	public EntityGroupInterface createEntityGroup()
	{
		EntityGroup entityGroup = new EntityGroup();
		return entityGroup;
	}

	/**
	 * This method creates an object of Association
	 *
	 * @return an instance of Association.
	 */
	public AssociationInterface createAssociation()
	{
		Association association = new Association();
		association.setConstraintProperties(createConstraintProperties());
		return association;
	}

	/**
	 * This method creates an object of Role
	 *
	 * @return an instance of Role.
	 */
	public RoleInterface createRole()
	{
		Role role = new Role();
		return role;
	}

	/**
	 * This method creates an object of SemanticProperty.
	 *
	 * @return an instance of SemanticProperty.
	 */
	public SemanticPropertyInterface createSemanticProperty()
	{
		SemanticProperty semanticProperty = new SemanticProperty();
		return semanticProperty;
	}

	/**
	 * This method creates an object of ColunmProperties.
	 *
	 * @return an instance of ColumnProperties.
	 */
	public ColumnPropertiesInterface createColumnProperties()
	{
		ColumnProperties columnProperties = new ColumnProperties();
		columnProperties.setName(COLUMN_NAME_PREFIX + UNDERSCORE + IdGeneratorUtil.getNextUniqeId());
		return columnProperties;
	}

	/**
	 * This method creates an object of TableProperties.
	 *
	 * @return an instance of TableProperties.
	 */
	public TablePropertiesInterface createTableProperties()
	{
		TableProperties tableProperties = new TableProperties();

		tableProperties.setName(TABLE_NAME_PREFIX + UNDERSCORE + IdGeneratorUtil.getNextUniqeId());

		tableProperties.setConstraintName(CONSTRAINT + UNDERSCORE + IdGeneratorUtil.getNextUniqeId());

		return tableProperties;
	}

	/**
	 * This method creates an object of ConstraintProperties
	 *
	 * @return an instance of ConstraintProperties.
	 */
	public ConstraintPropertiesInterface createConstraintProperties()
	{
		ConstraintProperties constraintProperties = new ConstraintProperties();

		constraintProperties.setName(ASSOCIATION_NAME_PREFIX + UNDERSCORE + IdGeneratorUtil.getNextUniqeId());

		constraintProperties.setSourceEntityKey(ASSOCIATION_COLUMN_PREFIX + UNDERSCORE + "S" + UNDERSCORE + IdGeneratorUtil.getNextUniqeId());

		constraintProperties.setTargetEntityKey(ASSOCIATION_COLUMN_PREFIX + UNDERSCORE + "T" + UNDERSCORE + IdGeneratorUtil.getNextUniqeId());

		constraintProperties.setSourceEntityKeyConstraintName(CONSTRAINT + UNDERSCORE + IdGeneratorUtil.getNextUniqeId());

		constraintProperties.setTargetEntityKeyConstraintName(CONSTRAINT + UNDERSCORE + IdGeneratorUtil.getNextUniqeId());

		return constraintProperties;
	}

	/**
	 *
	 * @return instance of BooleanAttributeTypeInformation.
	 */
	public AttributeInterface createBooleanAttribute()
	{
		Attribute booleanAttribute = new Attribute();
		booleanAttribute.setAttributeTypeInformation(new BooleanAttributeTypeInformation());
		booleanAttribute.setColumnProperties(createColumnProperties());
		return booleanAttribute;
	}

	/**
	 *
	 * @return instance of ByteArrayAttributeTypeInformation.
	 */
	public AttributeInterface createByteArrayAttribute()
	{
		Attribute byteArrayAttribute = new Attribute();
		byteArrayAttribute.setAttributeTypeInformation(new ByteArrayAttributeTypeInformation());
		byteArrayAttribute.setColumnProperties(createColumnProperties());
		return byteArrayAttribute;
	}

	/**
	 *
	 * @return instance of ByteArrayAttributeTypeInformation.
	 */
	public ByteArrayValueInterface createByteArrayValue()
	{
		ByteArrayValue byteArrayValue = new ByteArrayValue();
		return byteArrayValue;
	}

	/**
	 *
	 * @return instance of DateAttributeTypeInformation.
	 */
	public AttributeInterface createDateAttribute()
	{
		Attribute dateAttribute = new Attribute();
		dateAttribute.setAttributeTypeInformation(new DateAttributeTypeInformation());
		dateAttribute.setColumnProperties(createColumnProperties());
		return dateAttribute;
	}

	/**
	 *
	 * @return instance of DoubleAttributeTypeInformation.
	 */
	public AttributeInterface createDoubleAttribute()
	{
		Attribute doubleAttribute = new Attribute();
		doubleAttribute.setAttributeTypeInformation(new DoubleAttributeTypeInformation());
		doubleAttribute.setColumnProperties(createColumnProperties());
		return doubleAttribute;
	}

	/**
	 *
	 * @return instance of FloatAttributeTypeInformation.
	 */
	public AttributeInterface createFloatAttribute()
	{
		Attribute floatAttribute = new Attribute();
		floatAttribute.setAttributeTypeInformation(new FloatAttributeTypeInformation());
		floatAttribute.setColumnProperties(createColumnProperties());
		return floatAttribute;
	}

	/**
	 *
	 * @return instance of IntegerAttributeTypeInformation.
	 */
	public AttributeInterface createIntegerAttribute()
	{
		Attribute integerAttribute = new Attribute();
		integerAttribute.setAttributeTypeInformation(new IntegerAttributeTypeInformation());
		integerAttribute.setColumnProperties(createColumnProperties());
		return integerAttribute;
	}

	/**
	 *
	 * @return instance of LongAttributeTypeInformation.
	 */
	public AttributeInterface createLongAttribute()
	{
		Attribute longAttribute = new Attribute();
		longAttribute.setAttributeTypeInformation(new LongAttributeTypeInformation());
		longAttribute.setColumnProperties(createColumnProperties());
		return longAttribute;
	}

	/**
	 *
	 * @return instance of ShortAttributeTypeInformation.
	 */
	public AttributeInterface createShortAttribute()
	{
		Attribute shortAttribute = new Attribute();
		shortAttribute.setAttributeTypeInformation(new ShortAttributeTypeInformation());
		shortAttribute.setColumnProperties(createColumnProperties());
		return shortAttribute;
	}

	/**
	 *
	 * @return instance of StringAttributeTypeInformation.
	 */
	public AttributeInterface createStringAttribute()
	{
		Attribute stringAttribute = new Attribute();
		stringAttribute.setAttributeTypeInformation(new StringAttributeTypeInformation());
		stringAttribute.setColumnProperties(createColumnProperties());
		return stringAttribute;
	}

	/**
	 *
	 * @return instance of BooleanAttributeTypeInformation.
	 */
	public AttributeTypeInformation createBooleanAttributeTypeInformation()
	{
		return new BooleanAttributeTypeInformation();
	}

	/**
	 *
	 * @return instance of ByteArrayAttributeTypeInformation.
	 */
	public AttributeTypeInformation createByteArrayAttributeTypeInformation()
	{
		return new ByteArrayAttributeTypeInformation();
	}

	/**
	 *
	 * @return instance of DateAttributeTypeInformation.
	 */
	public AttributeTypeInformation createDateAttributeTypeInformation()
	{
		return new DateAttributeTypeInformation();
	}

	/**
	 *
	 * @return instance of DoubleAttributeTypeInformation.
	 */
	public AttributeTypeInformation createDoubleAttributeTypeInformation()
	{
		return new DoubleAttributeTypeInformation();
	}

	/**
	 *
	 * @return instance of FloatAttributeTypeInformation.
	 */
	public AttributeTypeInformation createFloatAttributeTypeInformation()
	{
		return new FloatAttributeTypeInformation();
	}

	/**
	 *
	 * @return instance of IntegerAttributeTypeInformation.
	 */
	public AttributeTypeInformation createIntegerAttributeTypeInformation()
	{
		return new IntegerAttributeTypeInformation();
	}

	/**
	 *
	 * @return instance of LongAttributeTypeInformation.
	 */
	public AttributeTypeInformation createLongAttributeTypeInformation()
	{
		return new LongAttributeTypeInformation();
	}

	/**
	 *
	 * @return instance of ShortAttributeTypeInformation.
	 */
	public AttributeTypeInformation createShortAttributeTypeInformation()
	{
		return new ShortAttributeTypeInformation();
	}

	/**
	 *
	 * @return instance of StringAttributeTypeInformation.
	 */
	public AttributeTypeInformation createStringAttributeTypeInformation()
	{
		return new StringAttributeTypeInformation();
	}

	/**
	 * This method creates an object of BooleanValue.
	 *
	 * @return an instance of BooleanValue.
	 */
	public BooleanValueInterface createBooleanValue()
	{
		BooleanValue booleanValue = new BooleanValue();
		return booleanValue;
	}

	/**
	 * This method creates an object of DateValue.
	 *
	 * @return an instance of DateValue.
	 */
	public DateValueInterface createDateValue()
	{
		DateValue dateValue = new DateValue();
		return dateValue;
	}

	/**
	 * This method creates an object of DoubleValue.
	 *
	 * @return an instance of DoubleValue.
	 */
	public DoubleValueInterface createDoubleValue()
	{
		DoubleValue doubleValue = new DoubleValue();
		return doubleValue;
	}

	/**
	 * This This method creates an object of FloatValue.
	 *
	 * @return an instance of FloatValue.
	 */
	public FloatValueInterface createFloatValue()
	{
		FloatValue floatValue = new FloatValue();
		return floatValue;
	}

	/**
	 * This method creates an object of IntegerValue.
	 *
	 * @return an instance of IntegerValue.
	 */
	public IntegerValueInterface createIntegerValue()
	{
		IntegerValue integerValue = new IntegerValue();
		return integerValue;
	}

	/**
	 * This method creates an object of LongValue.
	 *
	 * @return an instance of LongValue.
	 */
	public LongValueInterface createLongValue()
	{
		LongValue longValue = new LongValue();
		return longValue;
	}

	/**
	 * This method creates an object of ShortValue.
	 *
	 * @return an instance of ShortValue.
	 */
	public ShortValueInterface createShortValue()
	{
		ShortValue shortValue = new ShortValue();
		return shortValue;
	}

	/**
	 * This method creates an object of StringValue.
	 *
	 * @return an instance of StringValue.
	 */
	public StringValueInterface createStringValue()
	{
		StringValue stringValue = new StringValue();
		return stringValue;
	}

	/**
	 * This method creates an object of CheckBox.
	 *
	 * @return an instance of CheckBox.
	 */
	public CheckBoxInterface createCheckBox()
	{
		CheckBox checkBox = new CheckBox();
		return checkBox;
	}

	/**
	 * This method creates an object of ComboBox
	 *
	 * @return an instance of ComboBox.
	 */
	public ComboBoxInterface createComboBox()
	{
		ComboBox comboBox = new ComboBox();
		return comboBox;
	}

	/**
	 * This method creates an object of Container.
	 *
	 * @return an instance of Container.
	 */
	public ContainerInterface createContainer()
	{
		Container container = new Container();
		return container;
	}

	/**
	 * This method creates an object of DataGrid.
	 *
	 * @return an instance of DataGrid.
	 */
	public DataGridInterface createDataGrid()
	{
		DataGrid dataGrid = new DataGrid();
		return dataGrid;
	}

	/**
	 * This method creates an object of DatePicker.
	 *
	 * @return an instance of DatePicker.
	 */
	public DatePickerInterface createDatePicker()
	{
		DatePicker datePicker = new DatePicker();
		return datePicker;
	}

	/**
	 * This method creates an object of ListBox.
	 *
	 * @return an instance of ListBox.
	 */
	public ListBoxInterface createListBox()
	{
		ListBox listBox = new ListBox();
		return listBox;
	}

	/**
	 * This method creates an object of RadioButton.
	 *
	 * @return an instance of RadioButton.
	 */
	public RadioButtonInterface createRadioButton()
	{
		RadioButton radioButton = new RadioButton();
		return radioButton;
	}

	/**
	 * This method creates an object of TextArea.
	 *
	 * @return an instance of TextArea.
	 */
	public TextAreaInterface createTextArea()
	{
		TextArea textArea = new TextArea();
		return textArea;
	}

	/**
	 * This method creates an object of TextField.
	 *
	 * @return an instance of TextField.
	 */
	public TextFieldInterface createTextField()
	{
		TextField textField = new TextField();
		return textField;
	}

	/**
	 * This method creates an object of File Upload Control.
	 *
	 * @return an instance of TextField.
	 */
	public FileUploadInterface createFileUploadControl()
	{
		FileUploadControl fileUpload = new FileUploadControl();
		return fileUpload;
	}

	/**
	 * This method creates an object of View.
	 *
	 * @return an instance of View.
	 */
	public ViewInterface createView()
	{
		View view = new View();
		return view;
	}

	/**
	 * This method creates an object of Rule.
	 *
	 * @return an instance of Rule.
	 */
	public RuleInterface createRule()
	{
		Rule rule = new Rule();
		return rule;
	}

	/**
	 * This method creates an object of RuleParameter.
	 *
	 * @return an instance of RuleParameter.
	 */
	public RuleParameterInterface createRuleParameter()
	{
		RuleParameter ruleParameter = new RuleParameter();
		return ruleParameter;
	}

	/**
	 * This method creates an object of CaDSRDE.
	 *
	 * @return an instance of CaDSRDE.
	 */
	public CaDSRDEInterface createCaDSRDE()
	{
		CaDSRDE caDSRDE = new CaDSRDE();
		return caDSRDE;
	}

	/**
	 * This method creates an object of UserDefinedDE.
	 *
	 * @return an instance of UserDefinedDE.
	 */
	public UserDefinedDE createUserDefinedDE()
	{
		UserDefinedDE userDefinedDE = new UserDefinedDE();
		return userDefinedDE;
	}

	/**
	 * This method creates an object of UserDefinedDE.
	 *
	 * @return an instance of UserDefinedDE.
	 */
	public TaggedValueInterface createTaggedValue()
	{
		TaggedValueInterface taggedValueInterface = new TaggedValue();
		return taggedValueInterface;
	}

	/**
	 *
	 * @return fileAttribute.
	 */
	public AttributeInterface createFileAttribute()
	{
		Attribute fileAttribute = new Attribute();
		fileAttribute.setAttributeTypeInformation(new FileAttributeTypeInformation());
		fileAttribute.setColumnProperties(createColumnProperties());
		return fileAttribute;
	}

	/**
	 *
	 * @return instance of StringAttributeTypeInformation.
	 */
	public AttributeTypeInformation createFileAttributeTypeInformation()
	{
		return new FileAttributeTypeInformation();
	}

	/**
	 *
	 * @return
	 */
	public FileAttributeRecordValue createFileAttributeRecordValue()
	{
		return new FileAttributeRecordValue();
	}

	/**
	 * @return
	 */
	public AssociationDisplayAttributeInterface createAssociationDisplayAttribute()
	{
		return new AssociationDisplayAttribute();
	}

	/**
	 * This method creates an object of ColunmProperties.
	 *
	 * @return an instance of ColumnProperties.
	 */
	public ColumnPropertiesInterface createColumnProperties(String columnName)
	{
		ColumnPropertiesInterface columnProperties = createColumnProperties();
		columnProperties.setName(columnName);
		return columnProperties;
	}

	/**
	 * This method creates an object of TableProperties.
	 *
	 * @return an instance of TableProperties.
	 */
	public TablePropertiesInterface createTableProperties(String tableName)
	{
		TablePropertiesInterface tableProperties = createTableProperties();
		tableProperties.setName(tableName);
		return tableProperties;
	}

	/**
	 * This method creates an object of ConstraintProperties
	 *
	 * @return an instance of ConstraintProperties.
	 */
	public ConstraintPropertiesInterface createConstraintProperties(String middleTableName)
	{
		ConstraintPropertiesInterface constraintProperties = createConstraintProperties();
		constraintProperties.setName(middleTableName);
		return constraintProperties;
	}

	/**
	 * @return
	 */
	public ContainmentAssociationControlInterface createContainmentAssociationControl()
	{
		ContainmentAssociationControlInterface containmentAssociationControlInterface = new ContainmentAssociationControl();
		return containmentAssociationControlInterface;
	}

	/**
	 *
	 * @return
	 */
	public CaDSRValueDomainInfoInterface createCaDSRValueDomainInfo()
	{
		CaDSRValueDomainInfoInterface caDSRValueDomainInfoInterface = new CaDSRValueDomainInfo();
		return caDSRValueDomainInfoInterface;
	}

	/**
	 * @return
	 */
	public AttributeInterface createObjectAttribute()
	{
		Attribute ObjectAttribute = new Attribute();
		ObjectAttribute.setAttributeTypeInformation(new ObjectAttributeTypeInformation());
		ObjectAttribute.setColumnProperties(createColumnProperties());
		return ObjectAttribute;
	}

	/**
	 *
	 * @return instance of IntegerAttributeTypeInformation.
	 */
	public AttributeTypeInformation createObjectAttributeTypeInformation()
	{
		return new ObjectAttributeTypeInformation();
	}

	public ObjectAttributeRecordValueInterface createObjectAttributeRecordValue()
	{
		return new ObjectAttributeRecordValue();
	}

	public CategoryInterface createCategory()
	{
		return new Category();
	}

	public CategoryEntityInterface createCategoryEntity()
	{
		CategoryEntity categoryEntity = new CategoryEntity();
		categoryEntity.setCreatedDate(new Date());
		categoryEntity.setLastUpdated(categoryEntity.getCreatedDate());
		categoryEntity.setTableProperties(createTableProperties(CATEGORY_TABLE_NAME_PREFIX + UNDERSCORE + IdGeneratorUtil.getNextUniqeId()));
		return categoryEntity;
	}

	public CategoryAttributeInterface createCategoryAttribute()
	{
		CategoryAttribute categoryAttribute = new CategoryAttribute();
		categoryAttribute.setColumnProperties(createColumnProperties());
		return categoryAttribute;
	}

	/**
	 * @return IdGeneratorInterface
	 */
	public IdGeneratorInterface createIdGenerator()
	{
		return new IdGenerator();
	}

	public PathInterface createPath()
	{
		return new Path();
	}

	public PathAssociationRelationInterface createPathAssociationRelation()
	{
		return new PathAssociationRelation();
	}

	public CategoryAssociationInterface createCategoryAssociation()
	{
		CategoryAssociation association = new CategoryAssociation();
		association.setConstraintProperties(createConstraintProperties());
		return association;
	}

	/**
	 * @return IdGeneratorInterface
	 */
	public CategoryAssociationControlInterface createCategoryAssociationControl()
	{
		return new CategoryAssociationControl();
	}

}