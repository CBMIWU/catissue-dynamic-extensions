
package edu.common.dynamicextensions.domain.nui;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;

import edu.common.dynamicextensions.napi.ControlValue;
import edu.common.dynamicextensions.napi.impl.FormRenderer.ContextParameter;
import edu.common.dynamicextensions.processor.ProcessorConstants;
import edu.common.dynamicextensions.ui.util.Constants;
import edu.common.dynamicextensions.util.DynamicExtensionsUtility;
import edu.wustl.common.util.global.ApplicationProperties;

public class NumberField extends TextField {

	private static final long serialVersionUID = -5232920418590091013L;

	//
	// TODO: noOfDigits and noOfDigitsAfterDecimal needs to be pushed
	// to rule
	//
	private int noOfDigits;

	private int noOfDigitsAfterDecimal;

	private boolean calculated;

	private String formula;

	private String measurementUnits;

	public int getNoOfDigits() {
		return noOfDigits;
	}

	public void setNoOfDigits(int noOfDigits) {
		this.noOfDigits = noOfDigits;
	}

	public int getNoOfDigitsAfterDecimal() {
		return noOfDigitsAfterDecimal;
	}

	public void setNoOfDigitsAfterDecimal(int noOfDigitsAfterDecimal) {
		this.noOfDigitsAfterDecimal = noOfDigitsAfterDecimal;
	}

	public boolean isCalculated() {
		return calculated;
	}

	public void setCalculated(boolean calculated) {
		this.calculated = calculated;
	}

	public String getFormula() {
		return formula;
	}

	public void setFormula(String formula) {
		this.formula = formula;
	}

	@Override
	public boolean isDynamic() {
		return super.isDynamic() || isCalculated();
	}

	public void setMinValue(String minValue) {
		if (minValue == null) {
			return;
		}

		addValidationRule("range", Collections.singletonMap("min", minValue));
	}

	public void setMaxValue(String maxValue) {
		if (maxValue == null) {
			return;
		}

		addValidationRule("range", Collections.singletonMap("max", maxValue));
	}

	public String getMeasurementUnits() {
		return measurementUnits;
	}

	public void setMeasurementUnits(String measurementUnits) {
		this.measurementUnits = measurementUnits;
	}

	@Override
	public List<ColumnDef> getColumnDefs() {
		return Collections.singletonList(ColumnDef.get(getDbColumnName(), "NUMBER"));
	}

	@Override
	public DataType getDataType() {
		return noOfDigitsAfterDecimal == 0 ? DataType.INTEGER : DataType.FLOAT;
	}

	@SuppressWarnings("unchecked")
	@Override
	public BigDecimal fromString(String value) {
		if (value == null || value.trim().isEmpty()) {
			return null;
		}

		return new BigDecimal(value);
	}

	@Override
	protected String render(String controlName, ControlValue controlValue,
			Map<ContextParameter, String> contextParameter) {
		String value = (controlValue.getValue() == null ? DynamicExtensionsUtility.replaceHTMLSpecialCharacters(this
				.getDefaultValue()) : (String) controlValue.getValue());
		if (value == null) {
			value = StringUtils.EMPTY;
		}

		StringBuilder htmlString = new StringBuilder("");

		{
			htmlString.append("<INPUT  name='").append(controlName).append("' ").append("id='").append(controlName)
					.append("' onchange=\"").append(getOnchangeServerCall(controlName)).append(";")
					.append((this.isCalculatedSourceControl() ? "updateHTML();" : "")).append("\" value='")
					.append(DynamicExtensionsUtility.getEscapedStringValue(value) + "' ");

			int columnSize = getNoOfColumns();
			if (columnSize > 0) {
				htmlString.append("size='").append(columnSize).append("' ");
				htmlString.append("style='width:").append((columnSize + 1)).append("ex' ");
			} else {
				htmlString.append("size='").append(Constants.DEFAULT_COLUMN_SIZE).append("' ");
			}

			htmlString.append(" type='text' ");

			if (this.isCalculated() || controlValue.isReadOnly()) {
				htmlString.append(" readonly='").append(ProcessorConstants.TRUE).append("' ");
			}
			if (controlValue.getErrorMessage() != null) {
				htmlString.append(" title='").append(controlValue.getErrorMessage()).append("' ").append(" class='")
						.append("font_bl_nor_error").append("' ");
			}
			htmlString.append("/>");
			if (measurementUnits != null) {
				if (measurementUnits.equalsIgnoreCase("none")) {
					measurementUnits = "";
				}
				htmlString.append(" ").append(measurementUnits);
			}
		}

		return htmlString.toString();
	}

	@Override
	public List<String> validate(ControlValue controlValue) {
		List<String> errorList = new ArrayList<String>();
		String value = (String) controlValue.getValue();

		try {
			BigDecimal numberValue = fromString(value);

			if (numberValue == null) {
				return errorList;
			}
			int decimalPointIndex = value.indexOf('.');

			if (decimalPointIndex != -1) {
				String digitsAfterDecimalPoint = value.substring(decimalPointIndex + 1, value.length());

				if (digitsAfterDecimalPoint.length() > noOfDigitsAfterDecimal) {
					errorList.add(ApplicationProperties.getValue(
							"dynExtn.validation.Number.numberOfDigitsExceedsPrecision",
							Arrays.asList(getCaption(), String.valueOf(noOfDigitsAfterDecimal))));
				}

				String digitsbeforeDecimalPoint = value.substring(0, decimalPointIndex - 1);
				if (digitsbeforeDecimalPoint.length() > noOfDigits) {
					errorList.add(ApplicationProperties.getValue("dynExtn.validation.Number.maxNoOfDigits",
							Arrays.asList(getCaption(), String.valueOf(noOfDigits))));

				}

			} else {
				if (value.length() > noOfDigits) {
					errorList.add(ApplicationProperties.getValue("dynExtn.validation.Number.maxNoOfDigits",
							Arrays.asList(getCaption(), String.valueOf(noOfDigits))));

				}
			}

		} catch (Exception exception) {
			errorList.add(ApplicationProperties.getValue("dynExtn.validation.Number",
					Collections.singletonList(getCaption())));
		}

		return errorList;
	}

	@Override
	public String toString(Object value) {
		if (!(value instanceof BigDecimal)) {
			return null;
		}
		BigDecimal numberValue = (BigDecimal) value;
		numberValue = numberValue.setScale(noOfDigitsAfterDecimal, RoundingMode.HALF_UP).stripTrailingZeros();
		return numberValue.toPlainString();
	}

}