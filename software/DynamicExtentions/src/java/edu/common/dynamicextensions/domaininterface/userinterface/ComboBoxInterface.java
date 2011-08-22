
package edu.common.dynamicextensions.domaininterface.userinterface;

import java.util.List;

/**
 * ComboBoxInterface stores necessary information for generating ComboBox control on
 * dynamically generated user interface.
 * @author geetika_bangard
 */
public interface ComboBoxInterface extends SelectInterface
{

	/**
	 * This method returns the list of values that are displayed as choices.
	 * @return the list of values that are displayed as choices.
	 */
	List getChoiceList();

	/**
	 * This method sets the list of values that are displayed as choices.
	 * @param choiceList the List of values that is to set as ChoiceList.
	 */
	void setChoiceList(List choiceList);

	/**
	 * @hibernate.property name="columns" type="integer" column="NO_OF_COLUMNS"
	 * @return Returns the columns.
	 */
	Integer getColumns();

	/**
	 * @param columns The columns to set.
	 */
	void setColumns(Integer columns);

	public boolean getIsLazy();

	/**
	 * @param columns The columns to set.
	 */
	public void setIsLazy(boolean  isLazy);
}
