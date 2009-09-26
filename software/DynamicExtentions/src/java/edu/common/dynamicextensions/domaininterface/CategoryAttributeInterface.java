
package edu.common.dynamicextensions.domaininterface;

import java.util.Collection;

import edu.common.dynamicextensions.domaininterface.databaseproperties.ColumnPropertiesInterface;
import edu.common.dynamicextensions.domaininterface.validationrules.RuleInterface;

/**
 *
 * @author mandar_shidhore
 *
 */
public interface CategoryAttributeInterface extends BaseAbstractAttributeInterface
{

	/**
	 *
	 * @return
	 */
	AbstractAttributeInterface getAbstractAttribute();

	/**
	 *
	 * @param attribute
	 */
	void setAbstractAttribute(AbstractAttributeInterface attribute);

	/**
	 *
	 * @return
	 */
	CategoryEntityInterface getCategoryEntity();

	/**
	 *
	 * @param categoryEntityInterface
	 */
	void setCategoryEntity(CategoryEntityInterface categoryEntityInterface);

	/**
	 *
	 * @return
	 */
	String getDefaultValue();

	/**
	 *
	 * @param permissibleValueInterface
	 */
	void setDefaultValue(PermissibleValueInterface permissibleValueInterface);

	/**
	 *
	 * @param dataElementInterface
	 */
	void setDataElement(DataElementInterface dataElementInterface);

	/**
	 * This method returns the ColumnProperties of the Attribute.
	 * @return the ColumnProperties of the Attribute.
	 */
	ColumnPropertiesInterface getColumnProperties();

	/**
	 * This method sets the ColumnProperties of the Attribute.
	 * @param columnProperties the ColumnProperties to be set.
	 */
	void setColumnProperties(ColumnPropertiesInterface columnProperties);

	/**
	 *
	 */
	Boolean getIsVisible();

	/**
	 *
	 */
	void setIsVisible(Boolean isVisible);

	Collection<RuleInterface> getRuleCollection();

	/**
	 * @param ruleCollection the ruleCollection to set
	 */
	void setRuleCollection(Collection<RuleInterface> ruleCollection);

	/**
	 *
	 */
	Boolean getIsRelatedAttribute();

	/**
	 *
	 */
	void setIsRelatedAttribute(Boolean isRelatedAttribute);
	
	/**
	 *
	 */
	Boolean getIsCalculated();

	/**
	 *
	 */
	void setIsCalculated(Boolean isCalculated);
	/**
	 * 
	 * @param formulaInterface
	 */
	void setFormula(FormulaInterface formulaInterface);
	/**
	 * This method return the formula.
	 * @return
	 */
	FormulaInterface getFormula();
	/**
	 * 
	 * @return
	 */
	Collection<CategoryAttributeInterface> getCalculatedCategoryAttributeCollection();
	/**
	 * 
	 * @param calculatedCategoryAttributeCollection
	 */
	void setCalculatedCategoryAttributeCollection(
			Collection<CategoryAttributeInterface> calculatedCategoryAttributeCollection);
	/**
	 * 
	 * @return
	 */
	Collection<CategoryAttributeInterface> getCalculatedDependentCategoryAttributes();
	/**
	 * 
	 * @param calculatedDependentCategoryAttributes
	 */
	void setCalculatedDependentCategoryAttributes(
			Collection<CategoryAttributeInterface> calculatedDependentCategoryAttributes);
	/**
	 *
	 */
	void addCalculatedDependentCategoryAttribute(
			CategoryAttributeInterface categoryAttributeInterface);
	/**
	 *
	 */
	void addCalculatedCategoryAttribute(
			CategoryAttributeInterface categoryAttributeInterface);
	/**
	 * 
	 * @return
	 */
	PermissibleValueInterface getDefaultValuePermissibleValue();
	/**
	 * This method removes all Calculated Category Attributes.
	 */
	void removeAllCalculatedDependentCategoryAttributes();
	/**
	 * This method removes all Calculated Category Attributes.
	 */
	void removeAllCalculatedCategoryAttributes();
	/**
	 * 
	 * @param isSkipLogic
	 */
	void setIsSkipLogic(Boolean isSkipLogic);
	/**
	 * 
	 * @return
	 */
	Boolean getIsSkipLogic();
	/**
	 * 
	 * @param permissibleValue
	 */
	void addSkipLogicPermissibleValue(PermissibleValueInterface permissibleValue);
	/**
	 * 
	 * @return
	 */
	Collection<PermissibleValueInterface> getSkipLogicPermissibleValues();
	/**
	 * 
	 * @param permissibleValue
	 * @return
	 */
	PermissibleValueInterface getSkipLogicPermissibleValue(PermissibleValueInterface permissibleValue);
}