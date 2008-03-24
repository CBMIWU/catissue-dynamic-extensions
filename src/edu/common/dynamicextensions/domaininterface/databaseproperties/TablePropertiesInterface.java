
package edu.common.dynamicextensions.domaininterface.databaseproperties;

/**
 * These are the database properties for the entity.
 * @author geetika_bangard
 */
public interface TablePropertiesInterface extends DatabasePropertiesInterface
{
	/**
	 *
	 * @return
	 */
	String getConstraintName();
	/**
	 *
	 * @param constraintName
	 */
	void setConstraintName(String constraintName);
}