
package edu.common.dynamicextensions.domain.nui;

import java.util.Collections;
import java.util.List;

public class StringTextField extends TextField {
	private boolean url;

	private boolean password;

	public boolean isUrl() {
		return url;
	}

	public void setUrl(boolean url)	{
		this.url = url;
	}

	public boolean isPassword()	{
		return password;
	}

	public void setPassword(boolean password) {
		this.password = password;
	}

	public int getMinLength() {
		int minLength = 0;		
		String min = getValidationRuleParam("textLength", "min");
		if (min != null && !min.trim().isEmpty()) {
			minLength = Integer.parseInt(min);
		}
		
		return minLength;
	}
	
	public void setMinLength(int minChars) {
		addValidationRule("textLength", Collections.singletonMap("min", String.valueOf(minChars)));
	}

	public int getMaxLength() {
		int maxLength = 0;		
		String max = getValidationRuleParam("textLength", "max");
		if (max != null && !max.trim().isEmpty()) {
			maxLength = Integer.parseInt(max);
		}
		
		return maxLength;		
	}
	
	public void setMaxLength(int maxChars) {
		addValidationRule("textLength", Collections.singletonMap("max", String.valueOf(maxChars)));
	}

	@Override
	public List<ColumnDef> getColumnDefs() {
		return Collections.singletonList(ColumnDef.get(getDbColumnName(), "VARCHAR(4000)"));
	}
	
	@Override
	public DataType getDataType() {
		return DataType.STRING;
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public String fromString(String value) {
		return value;
	}
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + (url ? 1231 : 1237);
		result = prime * result + (password ? 1231 : 1237);		
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
		
		StringTextField other = (StringTextField) obj;
		if (url != other.url || password != other.password) {
			return false;
		}
		
		return true;
	}
}
