
package edu.common.dynamicextensions.validation;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import edu.common.dynamicextensions.domaininterface.AttributeInterface;
import edu.common.dynamicextensions.entitymanager.EntityManagerUtil;
import edu.common.dynamicextensions.exception.DynamicExtensionsSystemException;
import edu.common.dynamicextensions.exception.DynamicExtensionsValidationException;

/**
 * @author Rahul Ner
 *
 */
public class UniqueValidator implements ValidatorRuleInterface
{

	/**
	 * @see edu.common.dynamicextensions.validation.ValidatorRuleInterface#validate(edu.common.dynamicextensions.domaininterface.AttributeInterface, java.lang.Object, java.util.Map)
	 * @throws DynamicExtensionsValidationException
	 */
	public boolean validate(AttributeInterface attribute, Object valueObject,
			Map<String, String> parameterMap) throws DynamicExtensionsValidationException,
			DynamicExtensionsSystemException
	{
		/* Check for the validity of the number */
		NumberValidator numberValidator = new NumberValidator();
		numberValidator.validate(attribute, valueObject, parameterMap);

		if (EntityManagerUtil.isValuePresent(attribute, valueObject))
		{
			List<String> placeHolders = new ArrayList<String>();
			placeHolders.add(attribute.getName());
			placeHolders.add((String) valueObject);

			throw new DynamicExtensionsValidationException("Validation failed", null,
					"dynExtn.validation.Unique", placeHolders);
		}

		return true;

	}

}
