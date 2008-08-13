
package edu.common.dynamicextensions.domaininterface;

import java.util.Set;

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
	public AttributeInterface getAttribute();

	/**
	 *
	 * @param attribute
	 */
	public void setAttribute(AttributeInterface attribute);

	/**
	 *
	 * @return
	 */
	public CategoryEntityInterface getCategoryEntity();

	/**
	 *
	 * @param categoryEntityInterface
	 */
	public void setCategoryEntity(CategoryEntityInterface categoryEntityInterface);

	/**
	 *
	 * @return
	 */
	public String getDefaultValue();

	/**
	 *
	 * @param permissibleValueInterface
	 */
	public void setDefaultValue(PermissibleValueInterface permissibleValueInterface);

	/**
	 *
	 * @param dataElementInterface
	 */
	public void setDataElement(DataElementInterface dataElementInterface);
	/**
	 * This method returns the ColumnProperties of the Attribute.
	 * @return the ColumnProperties of the Attribute.
	 */
	public ColumnPropertiesInterface getColumnProperties();
	/**
	 * This method sets the ColumnProperties of the Attribute.
	 * @param columnProperties the ColumnProperties to be set.
	 */
	public void setColumnProperties(ColumnPropertiesInterface columnProperties);
	
	/**
	 * 
	 */
	public Boolean getIsVisible();
	
	/**
	 * 
	 */
	public void setIsVisible(Boolean isVisible);
	
	public Set<RuleInterface> getRuleCollection();

	/**
	 * @param ruleCollection the ruleCollection to set
	 */
	public void setRuleCollection(Set<RuleInterface> ruleCollection);
	
	/**
	 * 
	 */
	public Boolean getIsRelatedAttribute();
	
	/**
	 * 
	 */
	public void setIsRelatedAttribute(Boolean isRelatedAttribute);
	
}