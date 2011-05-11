/**
 *
 */

package edu.common.dynamicextensions.util.templategenerator;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Collection;

import edu.common.dynamicextensions.exception.DynamicExtensionsSystemException;
import edu.common.dynamicextensions.util.global.DEConstants;
import edu.wustl.bulkoperator.metadata.Attribute;
import edu.wustl.bulkoperator.metadata.BulkOperationClass;
import edu.wustl.bulkoperator.metadata.BulkOperationMetaData;

/**
 * @author shrishail_kalshetty
 *
 */
public class BOTemplateGeneratorUtility
{

	public static final String MANY = "*";
	/**
	 * Constant for adding association between categories.
	 */
	private static final String ARROW_OPERATOR = "->";
	/**
	 * Constant for specifying attributes belongs to which containment association.
	 */
	private static final String CONTAINMENT_SEPARATOR = "#1";
	/**
	 * Constant for setting batch size.
	 */
	private static final Integer MAX_RECORD = 5;
	/**
	 * Constant for setting default max number of records.
	 */
	private static final Integer MIN_RECORD = 1;
	/**
	 * Constant for adding value to bulk operation class attributes.
	 */
	private static final String BLANK_SPACE = "";
	/**
	 * Constant for setting batch size.
	 */
	private static final Integer BATCH_SIZE = 5;

	/**
	 * constant string
	 */
	private static final String CONTAINMENT = "containment";

	/**
	 * This method sets the common attribute properties.
	 * @param bOClass BulkOperationClass object.
	 */
	public static void setCommonAttributes(BulkOperationClass bOClass, String templateName)
	{
		bOClass.setRoleName(BLANK_SPACE);
		bOClass.setBatchSize(BATCH_SIZE);
		bOClass.setParentRoleName(BLANK_SPACE);

		bOClass.setRelationShipType(CONTAINMENT);
		bOClass.setTemplateName(templateName);
	}

	/**
	 * This method removes the last extra arrow operator.
	 * @param buffer buffer to store resultant string.
	 * @param delimiter delimiter to remove from buffer.
	 */
	public static void replaceLastDelimiter(StringBuffer buffer, String delimiter)
	{
		if (buffer.toString().endsWith(delimiter))
		{
			buffer.replace(0, buffer.length(), buffer.substring(0, buffer.lastIndexOf(delimiter)));
		}
	}

	/**
	 * This method sets the max number of records for category association.
	 * @param bulkOperationClass BulkOperationClass object.
	 */
	public static void setMaxNumberOfRecords(BulkOperationClass bulkOperationClass)
	{
		if (DEConstants.Cardinality.ONE.getValue().toString().equals(
				bulkOperationClass.getCardinality()))
		{
			bulkOperationClass.setMaxNoOfRecords(MIN_RECORD);
		}
		else
		{
			bulkOperationClass.setMaxNoOfRecords(MAX_RECORD);
		}
	}

	/**
	 * Adds the category name to bulk operation class attribute template name and class name.
	 * @param categoryName name of the category.
	 * @return name of the category.
	 */
	public static String getRootCategoryEntityName(String categoryName)
	{
		String[] name = categoryName.split(DEConstants.CLOSING_SQUARE_BRACKET);
		StringBuffer buffer = new StringBuffer();
		for (String string : name)
		{
			buffer.append(string).append(DEConstants.CLOSING_SQUARE_BRACKET).append(ARROW_OPERATOR);
		}
		replaceLastDelimiter(buffer, ARROW_OPERATOR);
		return buffer.toString();
	}

	/**
	 * This method sets the cardinality between source and target category entity.
	 * @param numberOfEntries Number of Entries.
	 * @param bulkOperationClass Set the cardinality of this object.
	 */
	public static void setCardinality(Integer numberOfEntries, BulkOperationClass bulkOperationClass)
	{
		//to check if cardinality is one to many or not
		if (numberOfEntries < 0 || numberOfEntries>99)
		{
			bulkOperationClass.setCardinality(MANY);
		}
		else
		{
			bulkOperationClass.setCardinality(DEConstants.Cardinality.ONE.getValue().toString());
		}
	}



	/**
	 * Process the containment association for adding attribute names in CSV file.
	 * @param csvBuffer buffer to append CSV column names.
	 * @param containmentAssoCollection Collection of Containment association.
	 * @param count Count required to get depth of containment association.
	 */
	public static void processContainmentAssociation(StringBuffer csvBuffer,
			Collection<BulkOperationClass> containmentAssoCollection, String suffix )
	{
		for (BulkOperationClass boClass : containmentAssoCollection)
		{
			appendAttributeCSVNames(csvBuffer, suffix, boClass);
		}
	}

	/**
	 * This method appends the category attribute CSV names to buffer.
	 * @param csvBuffer buffer to append category attribute CSV names.
	 * @param count number of times to append CA separator.
	 * @param boClass get attribute collection from this object.
	 */
	private static void appendAttributeCSVNames(StringBuffer csvBuffer, String suffix,
			BulkOperationClass boClass)
	{
		for (int index = 1; index <= boClass.getMaxNoOfRecords(); index++)
		{
			for (Attribute attribute : boClass.getAttributeCollection())
			{
				csvBuffer.append(attribute.getCsvColumnName());
				csvBuffer.append(suffix + "#" + index);

				csvBuffer.append(DEConstants.COMMA);
			}
			final Collection<BulkOperationClass> caCollection = boClass
			.getContainmentAssociationCollection();
			if (!caCollection.isEmpty())
			{
				processContainmentAssociation(csvBuffer, caCollection, suffix+"#" + index);
			}
		}
	}




	/**
	 * This method saves the XML file copy in Template directory.
	 * @param baseDir Base directory in which template directory to be created.
	 * @param mappingXML Mapping XML file path.
	 * @param bulkMetaData BulkOperationMetaData object.
	 * @return File object.
	 * @throws DynamicExtensionsSystemException throws DynamicExtensionsSystemException.
	 */
	public static void saveXMLTemplateCopy(File fileName, String mappingXML,
			final BulkOperationMetaData bulkMetaData)
			throws DynamicExtensionsSystemException
	{
		try
		{
			MarshalUtility.marshalObject(mappingXML, bulkMetaData, new FileWriter(
					fileName));
		}
		catch (IOException exception)
		{
			throw new DynamicExtensionsSystemException(
					"Error while creating XML template for Bulk operation.", exception);
		}
	}

	/**
	 * @param file File to save.
	 * @param csvString CSV string to write in a file.
	 * @throws DynamicExtensionsSystemException throw DynamicExtensionsSystemException
	 */
	public static void saveCSVTemplateCopy(File file, String csvString)
			throws DynamicExtensionsSystemException
	{
		try
		{
			BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(file));
			bufferedWriter.write(csvString);
			bufferedWriter.close();
		}
		catch (IOException exception)
		{
			throw new DynamicExtensionsSystemException(
					"Error while creating CSV template for bulk operation.", exception);
		}
	}
}
