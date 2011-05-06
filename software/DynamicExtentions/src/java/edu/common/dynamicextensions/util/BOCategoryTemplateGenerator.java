/**
 *
 */

package edu.common.dynamicextensions.util;

import edu.common.dynamicextensions.domain.DateAttributeTypeInformation;
import edu.common.dynamicextensions.domain.PathAssociationRelationInterface;
import edu.common.dynamicextensions.domaininterface.AssociationInterface;
import edu.common.dynamicextensions.domaininterface.AttributeTypeInformationInterface;
import edu.common.dynamicextensions.domaininterface.CategoryAssociationInterface;
import edu.common.dynamicextensions.domaininterface.CategoryAttributeInterface;
import edu.common.dynamicextensions.domaininterface.CategoryEntityInterface;
import edu.common.dynamicextensions.domaininterface.CategoryInterface;
import edu.common.dynamicextensions.util.global.DEConstants;
import edu.wustl.bulkoperator.metadata.Attribute;
import edu.wustl.bulkoperator.metadata.BulkOperationClass;
import edu.wustl.common.util.logger.Logger;
import edu.wustl.common.util.logger.LoggerConfig;

/**
 * @author shrishail_kalshetty.
 * Generates XML And CSV template for Bulk operation.
 */
public class BOCategoryTemplateGenerator extends AbstractCategoryIterator<BulkOperationClass>
{

	static
	{
		LoggerConfig.configureLogger(System.getProperty("user.dir"));
	}

	/**
	 * logger for information.
	 */
	private static final Logger LOGGER = Logger.getCommonLogger(BOCategoryTemplateGenerator.class);

	/**
	 * Bulk operation class object.
	 */
	protected BulkOperationClass bulkOperationClass;
	/**
	 * Constant for setting default max number of records.
	 */
	private static final Integer MAX_RECORD = 1;
	/**
	 * Constant for adding association between categories.
	 */
	private static final String ARROW_OPERATOR = "->";

	/**
	 * Parameterized constructor.
	 * @param categoryInterface category Interface object.
	 */
	public BOCategoryTemplateGenerator(CategoryInterface categoryInterface)
	{
		super(categoryInterface);
	}

	/**
	 * Process root category element.
	 * @param rootCategoryEntity CategoryEntityInterface object.
	 * @return BulkOperationClass object
	 */
	@Override
	protected BulkOperationClass processRootCategoryElement(
			CategoryEntityInterface rootCategoryEntity)
	{
		if (bulkOperationClass == null)
		{
			bulkOperationClass = new BulkOperationClass();
		}
		BOTemplateGeneratorUtility.setCommonAttributes(bulkOperationClass, category
				.getName());

		bulkOperationClass.setCardinality(DEConstants.Cardinality.ONE.getValue().toString());
		bulkOperationClass.setMaxNoOfRecords(MAX_RECORD);
		bulkOperationClass.setClassName(BOTemplateGeneratorUtility
				.getRootCategoryEntityName(rootCategoryEntity.getName()));

		return bulkOperationClass;
	}

	protected void processCategoryEntity(CategoryEntityInterface categoryEntity, BulkOperationClass mainObject)
	{

		for (CategoryAttributeInterface attributeInterface : categoryEntity
				.getCategoryAttributeCollection())
		{
			if (attributeInterface.getAbstractAttribute() instanceof AssociationInterface)
			{
				AssociationInterface associationInterface = (AssociationInterface) attributeInterface
						.getAbstractAttribute();
				BulkOperationClass innnerObject = processMultiSelect(associationInterface);
				processCategoryAttribute(attributeInterface, innnerObject,mainObject);
				postprocessCategoryAssociation(innnerObject, mainObject);

			}
			else
			{
				processCategoryAttribute(attributeInterface, mainObject,mainObject);
			}
		}
		for (CategoryAssociationInterface categoryAssociation : categoryEntity
				.getCategoryAssociationCollection())
		{
			BulkOperationClass innnerObject = processCategoryAssociation(categoryAssociation);
			processCategoryEntity(categoryAssociation.getTargetCategoryEntity(), innnerObject);
			postprocessCategoryAssociation(innnerObject, mainObject);
		}
	}
	/**
	 * process each category entity attributes.
	 * @param attribute category attribute.
	 * @param boObject Bulk operation class object.
	 */
	private void processCategoryAttribute(CategoryAttributeInterface attribute,
			BulkOperationClass boObject,BulkOperationClass parentObject)
	{
		if (!attribute.getIsRelatedAttribute())
		{
			Attribute bulkOperationAttribute = new Attribute();
			bulkOperationAttribute.setName(attribute.getAbstractAttribute().getName());
			bulkOperationAttribute.setBelongsTo("");
			if(attribute.getAbstractAttribute().getName().equalsIgnoreCase(boObject.getClassName()))
			{
				bulkOperationAttribute.setCsvColumnName(BOTemplateGeneratorUtility.getAttributename(parentObject.getClassName(),attribute.getAbstractAttribute().getName()));
			}
			else
			{
				bulkOperationAttribute.setCsvColumnName(BOTemplateGeneratorUtility.getAttributename(boObject.getClassName(),attribute.getAbstractAttribute().getName()));
			}
			bulkOperationAttribute.setUpdateBasedOn(false);

			final AttributeTypeInformationInterface attributeType = DynamicExtensionsUtility
					.getBaseAttributeOfcategoryAttribute(attribute).getAttributeTypeInformation();
			bulkOperationAttribute.setDataType(attributeType.getAttributeDataType().getName());
			handleDateFormat(bulkOperationAttribute, attributeType);

			boObject.getAttributeCollection().add(bulkOperationAttribute);
		}
	}

	/**
	 * This method handles date format for Date category attribute.
	 * @param bulkOperationAttribute Bulk operation class attribute.
	 * @param attributeType Type of the attribute.
	 */
	private void handleDateFormat(Attribute bulkOperationAttribute,
			final AttributeTypeInformationInterface attributeType)
	{
		if (attributeType instanceof DateAttributeTypeInformation)
		{
			final DateAttributeTypeInformation dateType = (DateAttributeTypeInformation) attributeType;
			bulkOperationAttribute.setFormat(DynamicExtensionsUtility.getDateFormat(dateType
					.getFormat()));
		}
		else
		{
			bulkOperationAttribute.setFormat("");
		}
	}

	/**
	 * process each category entity associations.
	 * @param categoryAssociation Category association.
	 * @return Bulk operation class object.
	 */
	@Override
	protected BulkOperationClass processCategoryAssociation(
			CategoryAssociationInterface categoryAssociation)
	{
		BulkOperationClass subBulkOperationClass = new BulkOperationClass();

		BOTemplateGeneratorUtility.setCommonAttributes(subBulkOperationClass, category
				.getName());

		BOTemplateGeneratorUtility.setCardinality(categoryAssociation.getTargetCategoryEntity()
				.getNumberOfEntries(), subBulkOperationClass);
		BOTemplateGeneratorUtility.setMaxNumberOfRecords(subBulkOperationClass);
		subBulkOperationClass.setClassName(updateCatgeoryEntityName(categoryAssociation));

		return subBulkOperationClass;

	}
	@Override
	protected BulkOperationClass processMultiSelect(AssociationInterface association)
	{
		BulkOperationClass subBulkOperationClass = new BulkOperationClass();
		BOTemplateGeneratorUtility.setCommonAttributes(subBulkOperationClass, category
				.getName());
		if(association.getTargetRole().getMaximumCardinality().getValue()==100)
		{
			subBulkOperationClass.setCardinality(BOTemplateGeneratorUtility.MANY);
		}
		else
		{
			subBulkOperationClass.setCardinality(DEConstants.Cardinality.ONE.getValue().toString());
		}
		BOTemplateGeneratorUtility.setMaxNumberOfRecords(subBulkOperationClass);
		subBulkOperationClass.setClassName(association.getName());

		return subBulkOperationClass;

	}

	/**
	 * post process for each category association.
	 * @param innerObject BulkOperationClass object.
	 * @param mainObject BulkOperationClass object.
	 */
	@Override
	protected void postprocessCategoryAssociation(BulkOperationClass innerObject,
			BulkOperationClass mainObject) // NOPMD by Kunal_Kamble on 12/30/10 9:22 PM
	{
		mainObject.getContainmentAssociationCollection().add(innerObject);
		mainObject = innerObject;
	}

	/**
	 * This method updates the category association target entity name.
	 * @param catAssociation object of the category association.
	 * @param className buffer to store the updated category name.
	 */
	private String updateCatgeoryEntityName(CategoryAssociationInterface catAssociation)
	{
		StringBuffer className = new StringBuffer();
		PathAssociationRelationInterface assocRelation = (PathAssociationRelationInterface) catAssociation
				.getTargetCategoryEntity().getPath().getSortedPathAssociationRelationCollection()
				.toArray()[0];
		className.append(assocRelation.getAssociation().getEntity().getName()).append(
				DEConstants.OPENING_SQUARE_BRACKET).append(assocRelation.getSourceInstanceId())
				.append(DEConstants.CLOSING_SQUARE_BRACKET).append(ARROW_OPERATOR);

		for (PathAssociationRelationInterface pathAssociationRelation : catAssociation
				.getTargetCategoryEntity().getPath().getSortedPathAssociationRelationCollection())
		{
			className.append(pathAssociationRelation.getAssociation().getTargetEntity().getName())
					.append(DEConstants.OPENING_SQUARE_BRACKET).append(
							pathAssociationRelation.getTargetInstanceId()).append(
							DEConstants.CLOSING_SQUARE_BRACKET).append(ARROW_OPERATOR);
		}
		BOTemplateGeneratorUtility.replaceLastDelimiter(className, ARROW_OPERATOR);
		return className.toString();
	}

	/**
	 * @return the bulkOperationClass.
	 */
	public BulkOperationClass getBulkOperationClass()
	{
		return bulkOperationClass;
	}

	/**
	 * @param bulkOperationClass the bulkOperationClass to set.
	 */
	public void setBulkOperationClass(BulkOperationClass bulkOperationClass)
	{
		this.bulkOperationClass = bulkOperationClass;
	}

	@Override
	protected void processCategoryAttribute(CategoryAttributeInterface attribute,
			BulkOperationClass object)
	{
		// TODO Auto-generated method stub

	}

}
