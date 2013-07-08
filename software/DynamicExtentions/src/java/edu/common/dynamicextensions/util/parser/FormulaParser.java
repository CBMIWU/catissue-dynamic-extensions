package edu.common.dynamicextensions.util.parser;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

import org.nfunk.jep.JEP;
import org.nfunk.jep.SymbolTable;

import edu.common.dynamicextensions.exception.DynamicExtensionsSystemException;


/**
 *
 * @author rajesh_patil
 *
 */
public class FormulaParser
{
	/**
	 *
	 */
	private JEP parser = null;
	/**
	 *
	 */
	public FormulaParser()
	{
		setParser(new JEP());
		getParser().setAllowUndeclared(true);
		getParser().setAllowAssignment(true);
		addStandardFunctionsAndConstants();
	}
	/**
	 *
	 * @return
	 */
	public JEP getParser()
	{
		return parser;
	}
	/**
	 *
	 * @param parser
	 */
	public void setParser(JEP parser)
	{
		this.parser = parser;
	}
	/**
	 *
	 * @param formulaExpression
	 * @return
	 * @throws DynamicExtensionsSystemException
	 */
	public boolean parseExpression(String formulaExpression) throws DynamicExtensionsSystemException
	{
		parser.parseExpression(formulaExpression); // Parse the expression
		if (parser.hasError())
		{
			throw new DynamicExtensionsSystemException(parser.getErrorInfo());
		}
		return true;
	}
	/**
	 * setVariableValue.
	 * @param operand
	 * @param value
	 */
	public void setVariableValue(String operand,Object value)
	{
		parser.setVarValue(operand,value);
	}
	/**
	 *
	 * @param formulaExpression
	 * @return
	 */
	public BigDecimal evaluateExpression()
	{
		return new BigDecimal(parser.getValue()).stripTrailingZeros();
	}
	/**
	 *
	 * @param formulaExpression
	 * @return
	 * @throws DynamicExtensionsSystemException
	 */
	public boolean validateExpression(String formulaExpression) throws DynamicExtensionsSystemException
	{
		boolean isValid = false;
		isValid = parseExpression(formulaExpression);
		return isValid;
	}
	/**
	 *
	 * @return
	 */
	public List<String> getSymobols()
	{
		List<String> symbols = new ArrayList <String>();
		SymbolTable symbolTable = parser.getSymbolTable();// Parse the expression
		Enumeration enumeration = symbolTable.keys();
		while (enumeration.hasMoreElements())
		{
			String element = (String) enumeration.nextElement();
			if (!("pi".equals(element)) && !("e".equals(element)))
			{
				symbols.add(element);
			}
		}
		return symbols;
	}
	/**
	 *
	 * @param formulaParser
	 */
	private  void addStandardFunctionsAndConstants()
	{
		getParser().addStandardFunctions();
		getParser().addStandardConstants();
	}
}
