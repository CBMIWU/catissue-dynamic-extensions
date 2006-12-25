
package edu.common.dynamicextensions.entitymanager;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Date;

import edu.common.dynamicextensions.domain.AbstractAttribute;
import edu.common.dynamicextensions.domain.Attribute;
import edu.common.dynamicextensions.domain.DateAttributeTypeInformation;
import edu.common.dynamicextensions.domain.StringAttributeTypeInformation;
import edu.common.dynamicextensions.domaininterface.AttributeTypeInformationInterface;
import edu.common.dynamicextensions.exception.DynamicExtensionsSystemException;
import edu.common.dynamicextensions.util.global.Constants;
import edu.common.dynamicextensions.util.global.Variables;
import edu.wustl.common.util.Utility;
import edu.wustl.common.util.dbManager.DAOException;
import edu.wustl.common.util.dbManager.DBUtil;
import edu.wustl.common.util.logger.Logger;

public class EntityManagerUtil
{

	/**
	 * 
	 * @param attribute
	 * @param value
	 * @return
	 */
	public static String getFormattedValue(AbstractAttribute attribute, Object value)
	{
		String formattedvalue = null;
		AttributeTypeInformationInterface attributeInformation = ((Attribute) attribute)
				.getAttributeTypeInformation();
		if (attribute == null)
		{
			formattedvalue = null;
		}

		else if (attributeInformation instanceof StringAttributeTypeInformation)
		{
			formattedvalue = "'" + value + "'";
		}
		else if (attributeInformation instanceof DateAttributeTypeInformation)
		{
			String format = ((DateAttributeTypeInformation) attributeInformation).getFormat();
			if (format == null)
			{
				format = Constants.DATE_PATTERN_MM_DD_YYYY;
			}
			String str = null;
			if (value instanceof Date)
			{
				str = Utility.parseDateToString(((Date) value), format);
			}
			else
			{
				str = (String) value;
			}

			formattedvalue = Variables.strTodateFunction + "('" + str + "','"
					+ Variables.datePattern + "')";
		}
		else
		{
			formattedvalue = value.toString();
		}
		Logger.out.debug("getFormattedValue The formatted value for attribute "
				+ attribute.getName() + "is " + formattedvalue);
		return formattedvalue;

	}

	/**
	 * @param query query to be executed
	 * @return 
	 * @throws DynamicExtensionsSystemException 
	 */
	public ResultSet executeQuery(String query) throws DynamicExtensionsSystemException
	{

		Connection conn = null;
		try
		{
			conn = DBUtil.getConnection();
			Statement statement = null;
			statement = conn.createStatement();
			ResultSet resultSet = statement.executeQuery(query);
			resultSet.next();
			return resultSet;
		}

		catch (Exception e)
		{
			try
			{
				conn.rollback();
			}
			catch (SQLException e1)
			{
				throw new DynamicExtensionsSystemException(e.getMessage(), e);
			}
			throw new DynamicExtensionsSystemException(e.getMessage(), e);
		}

	}

	/**
	 * @param query query to be executed
	 * @return 
	 * @throws DynamicExtensionsSystemException 
	 */
	public int executeDML(String query) throws DynamicExtensionsSystemException
	{

		Connection conn = null;
		try
		{
			conn = DBUtil.getConnection();
			Statement statement = null;
			statement = conn.createStatement();
			return statement.executeUpdate(query);
		}
		catch (Exception e)
		{
			try
			{
				conn.rollback();
			}
			catch (SQLException e1)
			{
				throw new DynamicExtensionsSystemException(e.getMessage(), e);
			}
			throw new DynamicExtensionsSystemException(e.getMessage(), e);
		}

	}

	/**
	 * Method generates the next identifier for the table that stores the value of the passes entity.
	 * @param entity
	 * @return
	 * @throws DAOException
	 * @throws ClassNotFoundException
	 */
	synchronized public Long getNextIdentifier(String entityTableName)
			throws DynamicExtensionsSystemException
	{

		StringBuffer queryToGetNextIdentifier = new StringBuffer("SELECT MAX(IDENTIFIER) FROM "
				+ entityTableName);
		try
		{
			ResultSet resultSet = executeQuery(queryToGetNextIdentifier.toString());
			Long identifier = resultSet.getLong(1);
			return identifier + 1;
		}
		catch (SQLException e)
		{
			throw new DynamicExtensionsSystemException(
					"Could not fetch the next identifier for table " + entityTableName);
		}
	}
}
