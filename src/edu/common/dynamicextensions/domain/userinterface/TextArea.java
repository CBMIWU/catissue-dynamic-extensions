package edu.common.dynamicextensions.domain.userinterface;

import java.util.Map;

/**
 * @version 1.0
 * @created 28-Sep-2006 12:20:09 PM
 * @hibernate.joined-subclass table="DYEXTN_TEXT_AREA" 
 * @hibernate.joined-subclass-key column="IDENTIFIER" 
 */
public class TextArea extends Control {

	/**
	 * Number of columns in the text area.
	 */
	protected Integer columns;
	/**
	 * Number of rows in the text area.
	 */
	protected Integer rows;

	public TextArea(){

	}

	public void finalize() throws Throwable {
		super.finalize();
	}


	/**
	 * @hibernate.property name="columns" type="integer" column="COLUMNS" 
	 * @return Returns the columns.
	 */
	public Integer getColumns() {
		return columns;
	}
	/**
	 * @param columns The columns to set.
	 */
	public void setColumns(Integer columns) {
		this.columns = columns;
	}
	/**
	 * @hibernate.property name="rows" type="integer" column="ROWS" 
	 * @return Returns the rows.
	 */
	public Integer getRows() {
		return rows;
	}
	/**
	 * @param rows The rows to set.
	 */
	public void setRows(Integer rows) {
		this.rows = rows;
	}

	public void populateAttribute(Map propertiesMap) {
		super.populateAttribute(propertiesMap);
		if(propertiesMap!=null)
		{
			try {
				rows = new Integer((String)propertiesMap.get(UIConfigurationConstants.NO_OF_ROWS_ATTRIBUTE));
			} catch (NumberFormatException e) {
				System.out.println("Error while retrieving no Of rows");
				rows = new Integer(UIConfigurationConstants.DEFAULT_NO_OF_ROWS_TEXT);
			}
			try {
				columns = new Integer((String)propertiesMap.get(UIConfigurationConstants.NO_OF_COLS_ATTRIBUTE));
			} catch (NumberFormatException e) {
				System.out.println("Error while retrieving no Of columns");
				columns  = new Integer(UIConfigurationConstants.DEFAULT_NO_OF_COLS_TEXT);
			}
		}
		else
		{
			rows = new Integer(UIConfigurationConstants.DEFAULT_NO_OF_ROWS_TEXT);
			columns  = new Integer(UIConfigurationConstants.DEFAULT_NO_OF_COLS_TEXT);
		}
	}

	public String generateHTML()
	{
		String htmlString = "<textarea " +
		"class = '" + cssClass + "' " +
		"name = '" + name + "' " +
		"id = '" + name + "' " +
		"cols = '" + columns.intValue() + "' " +
		"rows = '" + rows.intValue() + "' " +
		"title = '" + tooltip + "' " 
		+">" ;
		//htmlString = htmlString + defaultvalue
		htmlString = htmlString  + "</textarea>";
		System.out.println("Returning " + htmlString);
		return htmlString;
	}
}