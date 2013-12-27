
package edu.common.dynamicextensions.domain.nui;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;

public class MultiSelectCheckBox extends SelectControl implements MultiSelectControl {
	private String tableName;
	
	private int optionsPerRow = 3;
	
	private String parentKeyColumn  = "IDENTIFIER";
	
	private String foreignKeyColumn = "RECORD_ID";
	
	public MultiSelectCheckBox() {
		setDbColumnName("VALUE");
	}
	
	public int getOptionsPerRow() {
		return optionsPerRow;
	}

	public void setOptionsPerRow(int optionsPerRow) {
		this.optionsPerRow = optionsPerRow;
	}

	@Override
	public String getTableName() {
		return tableName;
	}

	@Override
	public void setTableName(String tableName) {
		this.tableName = tableName;
	}

	@Override
	public List<ColumnDef> getColumnDefs() {
		List<ColumnDef> columnDefs = new ArrayList<ColumnDef>();
		columnDefs.add(ColumnDef.get(getDbColumnName(), getDbType()));
		columnDefs.add(ColumnDef.get(foreignKeyColumn, "BIGINT"));

		return columnDefs;
	}
	
	@Override
	public String getParentKey() {
		return parentKeyColumn;
	}

	@Override
	public void setParentKey(String parentKeyColumn) {
		if (parentKeyColumn == null) {
			parentKeyColumn = "IDENTIFIER";
		}
		
		this.parentKeyColumn = parentKeyColumn;		
	}
	
	@Override
	public String getForeignKey() {
		return foreignKeyColumn;
	}

	@Override
	public void setForeignKey(String foreignKeyColumn) {
		if (foreignKeyColumn == null) {
			foreignKeyColumn = "RECORD_ID";
		}
		
		this.foreignKeyColumn = foreignKeyColumn;		
	}
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result	+ ((tableName == null) ? 0 : tableName.hashCode());
		result = prime * result + optionsPerRow;
		result = prime * result	+ ((parentKeyColumn == null) ? 0 : parentKeyColumn.hashCode());
		result = prime * result	+ ((foreignKeyColumn == null) ? 0 : foreignKeyColumn.hashCode());		
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		
		if (!super.equals(obj)) {
			return false;
		}
		
		MultiSelectCheckBox other = (MultiSelectCheckBox) obj;
		if (!StringUtils.equals(tableName, other.tableName) ||
			optionsPerRow != other.optionsPerRow ||
			!StringUtils.equals(parentKeyColumn, other.parentKeyColumn) ||
			!StringUtils.equals(foreignKeyColumn, other.foreignKeyColumn)) {
			return false;
		}

		return true;
	}
}
