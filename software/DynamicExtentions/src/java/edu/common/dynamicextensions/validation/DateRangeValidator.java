
package edu.common.dynamicextensions.validation;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;

import edu.common.dynamicextensions.domain.DateAttributeTypeInformation;
import edu.common.dynamicextensions.domaininterface.AttributeMetadataInterface;
import edu.common.dynamicextensions.domaininterface.AttributeTypeInformationInterface;
import edu.common.dynamicextensions.exception.DynamicExtensionsSystemException;
import edu.common.dynamicextensions.exception.DynamicExtensionsValidationException;
import edu.common.dynamicextensions.processor.ProcessorConstants;
import edu.common.dynamicextensions.util.DynamicExtensionsUtility;
import edu.wustl.common.util.Utility;

/**
 * @author chetan_patil
 *
 */
public class DateRangeValidator implements ValidatorRuleInterface
{

	/**
	 * @see edu.common.dynamicextensions.validation.ValidatorRuleInterface#validate(edu.common.dynamicextensions.domaininterface.AttributeInterface, java.lang.Object, java.util.Map)
	 * @throws DynamicExtensionsValidationException
	 * @throws DynamicExtensionsSystemException
	 */
	public boolean validate(AttributeMetadataInterface attribute, Object valueObject,
			Map<String, String> parameterMap, String controlCaption)
			throws DynamicExtensionsValidationException, DynamicExtensionsSystemException
	{
		boolean valid = true;

		/* Check for the validity of the date */
		DateValidator dateValidator = new DateValidator();
		dateValidator.validate(attribute, valueObject, parameterMap, controlCaption, true);

		AttributeTypeInformationInterface attributeTypeInformation = attribute
				.getAttributeTypeInformation();
		if (((valueObject != null) && (!(valueObject.toString()).trim().equals("")))
				&& ((attributeTypeInformation != null))
				&& (attributeTypeInformation instanceof DateAttributeTypeInformation))
		{
			DateAttributeTypeInformation dateAttributeTypeInformation = (DateAttributeTypeInformation) attributeTypeInformation;
			String dateFormat = DynamicExtensionsUtility.getDateFormat(dateAttributeTypeInformation.getFormat());
			String value = valueObject.toString();

			if (dateFormat.equals(ProcessorConstants.MONTH_YEAR_FORMAT))
			{
				value = DynamicExtensionsUtility.formatMonthAndYearDate(value, false);
				value = value.substring(0, value.length() - 4);
			}
			if (dateFormat.equals(ProcessorConstants.YEAR_ONLY_FORMAT))
			{
				value = DynamicExtensionsUtility.formatYearDate(value, false);
				value = value.substring(0, value.length() - 4);
			}

			Set<Map.Entry<String, String>> parameterSet = parameterMap.entrySet();
			for (Map.Entry<String, String> parameter : parameterSet)
			{
				String parameterName = parameter.getKey();
				String parameterValue = parameter.getValue();

				if (dateFormat.equals(ProcessorConstants.MONTH_YEAR_FORMAT))
				{
					parameterValue = DynamicExtensionsUtility.formatMonthAndYearDate(
							parameterValue, false);
					parameterValue = parameterValue.substring(0, parameterValue.length() - 4);
				}
				if (dateFormat.equals(ProcessorConstants.YEAR_ONLY_FORMAT))
				{
					parameterValue = DynamicExtensionsUtility.formatYearDate(parameterValue, false);
					parameterValue = parameterValue.substring(0, parameterValue.length() - 4);
				}

				Date parameterDate = null, valueDate = null;
				try
				{	// Fix to support different formats in DE :Pavan.
					parameterValue = parameterValue.replace('/', '-');
					parameterDate = Utility.parseDate(parameterValue, ProcessorConstants.SQL_DATE_ONLY_FORMAT);
					valueDate = Utility.parseDate(value, ProcessorConstants.DATE_ONLY_FORMAT);
				}
				catch (ParseException ParseException)
				{
					List<String> placeHolders = new ArrayList<String>();
					placeHolders.add(controlCaption);
					placeHolders.add(dateFormat);
					throw new DynamicExtensionsValidationException("Validation failed",
							ParseException, "dynExtn.validation.Date", placeHolders);
				}

				if ("min".equals(parameterName))
				{
					checkMinDate(parameterDate, valueDate, dateFormat, controlCaption);
				}
				else if ("max".equals(parameterName))
				{
					checkMaxDate(parameterDate, valueDate, dateFormat, controlCaption);
				}
			}
		}
		return valid;
	}

	/**
	 * @param parameterDate
	 * @param valueDate
	 * @param dateFormat
	 * @param controlCaption
	 * @throws DynamicExtensionsValidationException
	 */
	private void checkMinDate(Date parameterDate, Date valueDate, String dateFormat,
			String controlCaption) throws DynamicExtensionsValidationException
	{
		if (valueDate.before(parameterDate))
		{
			List<String> placeHolders = new ArrayList<String>();
			placeHolders.add(controlCaption);
			placeHolders.add(Utility.parseDateToString(parameterDate, dateFormat));
			throw new DynamicExtensionsValidationException("Validation failed", null,
					"dynExtn.validation.Date.Min", placeHolders);
		}
	}

	/**
	 * @param parameterDate
	 * @param valueDate
	 * @param dateFormat
	 * @param controlCaption
	 * @throws DynamicExtensionsValidationException
	 */
	private void checkMaxDate(Date parameterDate, Date valueDate, String dateFormat,
			String controlCaption) throws DynamicExtensionsValidationException
	{
		if (valueDate.after(parameterDate))
		{
			List<String> placeHolders = new ArrayList<String>();
			placeHolders.add(controlCaption);
			placeHolders.add(Utility.parseDateToString(parameterDate, dateFormat));
			throw new DynamicExtensionsValidationException("Validation failed", null,
					"dynExtn.validation.Date.Max", placeHolders);
		}
	}

}
