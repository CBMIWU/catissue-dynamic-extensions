package edu.common.dynamicextensions.query.ast;

import java.util.ArrayList;
import java.util.List;

import edu.common.dynamicextensions.domain.nui.DataType;

public class LiteralValueNode extends ExpressionNode {
	private DataType literalType;
	
	private List<Object> values = new ArrayList<Object>();
	
	public LiteralValueNode(DataType literalType) {
		this.literalType = literalType;
	}

	public List<Object> getValues() {
		return values;
	}

	public void setValues(List<Object> values) {
		this.values = values;
	}

	@Override
	public DataType getType() {
		return literalType;
	}
	
	@Override
	public LiteralValueNode copy() {
		LiteralValueNode copy = new LiteralValueNode(literalType);
		copy.getValues().addAll(getValues()); // Assume literals are immutable
		return copy;
	}	
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result	+ ((literalType == null) ? 0 : literalType.hashCode());
		result = prime * result + ((values == null) ? 0 : values.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		
		if (obj == null || getClass() != obj.getClass()) {
			return false;
		}
		
		LiteralValueNode other = (LiteralValueNode) obj;
		if (literalType != other.literalType) {
			return false;
		}
		
		if (values == null && other.values != null) {
			return false;				
		} else if (!values.equals(other.values)) {
			return false;
		}

		return true;
	}	
}