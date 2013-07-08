/**
 * 
 */
package edu.common.dynamicextensions.domain.nui;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import edu.common.dynamicextensions.napi.ControlValue;

public abstract class SelectControl extends Control {
	
	private static final long serialVersionUID = 2286060335229542875L;
	
	private PvDataSource pvDataSource;
	
	public PvDataSource getPvDataSource() {
		return pvDataSource;
	}

	public void setPvDataSource(PvDataSource pvDataSource) {
		this.pvDataSource = pvDataSource;
	}
	
	public List<PermissibleValue> getPvs() {
		return getPvs(Calendar.getInstance().getTime());
	}
	
	public List<PermissibleValue> getPvs(Date activationDate) {
		return pvDataSource != null ? pvDataSource.getPermissibleValues(activationDate) : null;
	}
	
	public PermissibleValue getDefaultValue() {
		return getDefaultValue(Calendar.getInstance().getTime());
	}
	
	public PermissibleValue getDefaultValue(Date activationDate) {
		return pvDataSource != null ? pvDataSource.getDefaultValue(activationDate) : null;
	}

	@Override
	public List<ColumnDef> getColumnDefs() {
		return Collections.singletonList(ColumnDef.get(getDbColumnName(), getDbType()));
	}
	
	@Override
	public DataType getDataType() {
		return pvDataSource.getDataType();
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public <T> T fromString(String value) {
		if (value == null) {
			return null;
		}
		
		Object result = null;
		DataType pvType = pvDataSource.getDataType();
		if (pvType == DataType.STRING) {
			result = value;
		} else if (pvType == DataType.INTEGER || pvType == DataType.FLOAT) {
			result = new BigDecimal(value);
		} else if (pvType == DataType.BOOLEAN) {
			if (value.equals("true")) {
				result = 1;
			} else {
				result = 0;
			}
		} else if (pvType == DataType.DATE) {
			try {
				result = new SimpleDateFormat(pvDataSource.getDateFormat()).parse(value);
			} catch (Exception e) {
				throw new RuntimeException("Error parsing date string: " + value, e);
			}			
		}
		
		return (T)result;
	}
	
	@Override
	public String toString(Object value) {
		if (value == null) {
			return null;
		}
		
		String result = value.toString();
		DataType pvType = pvDataSource.getDataType();
		if (pvType == DataType.BOOLEAN && value instanceof Number) {
			Number number = (Number)value;
			if (number.intValue() == 1) {
				result = "true";
			} else {
				result = "false";
			}
		} else if (pvType == DataType.DATE && value instanceof Date) {
			try {
				result = new SimpleDateFormat(pvDataSource.getDateFormat()).format((Date)value);
			} catch (Exception e) {
				throw new RuntimeException("Error converting date to string: " + value, e);				
			}
		}
		
		return result;
	}
	
	protected String getDbType() {
		String dbType;
		switch (pvDataSource.getDataType()) {
			case STRING:
				dbType = "VARCHAR2(4000)";
				break;
				
			case INTEGER:
			case FLOAT:
			case BOOLEAN:
				dbType = "NUMBER";
				break;
				
			case DATE:
				dbType = "DATE";
				break;
				
			default:
				dbType = null;
				break;
		}
		
		return dbType;
	}
	
	
	//////////////////////////////////////////////////////////////////////////
	//
	// Used by UI
	//
	//////////////////////////////////////////////////////////////////////////
	
	protected String getDisplayValue(ControlValue controlValue, Date activationDate) {
		String value = (String) controlValue.getValue();
		if (value == null) {
			PermissibleValue defaultValue = getDefaultValue(activationDate); 
			value = defaultValue != null ? defaultValue.getValue() : "";
		}		
		return value;
	}

	public List<PermissibleValue> getPVList(Date encounterDate, ControlValue controlValue) {
		List<PermissibleValue> permissibleValues = controlValue.getPermissibleValues();
		if (permissibleValues == null || permissibleValues.isEmpty()) {
			permissibleValues = getPvDataSource().getPermissibleValues(encounterDate);
		}
		return permissibleValues;
	}
}
