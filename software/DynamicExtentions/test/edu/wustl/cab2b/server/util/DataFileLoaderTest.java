
package edu.wustl.cab2b.server.util;

import static edu.wustl.cab2b.server.path.PathConstants.FIELD_SEPARATOR;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.Connection;

import edu.common.dynamicextensions.util.DynamicExtensionsBaseTestCase;
import edu.wustl.cab2b.server.path.PathConstants;

/**
 * @author chandrakant_talele
 */
public class DataFileLoaderTest extends DynamicExtensionsBaseTestCase
{

	private static Connection con = TestConnectionUtil.getConnection();
	private static String tableName = "T_" + System.currentTimeMillis();
	private File file = null;

	@Override
	protected void setUp()
	{
		String createTableSQL = "create table " + tableName
				+ " (ID BIGINT(38) NOT NULL, NAME VARCHAR(10) NULL,PRIMARY KEY (ID))";
		SQLQueryUtil.executeUpdate(createTableSQL, con);
	}

	public void testLoadDataFromFile()
	{
		assertTrue(true);
		String str = "S";
		String home = System.getProperty("user.home");
		file = new File(home, "GeneratedFromDataFileLoaderTest.txt");
		String fileName = file.getAbsolutePath();
		file.delete();
		try
		{
			file.createNewFile();
			BufferedWriter fileWriter = new BufferedWriter(new FileWriter(file));
			for (int i = 1; i < 101; i++)
			{
				fileWriter.write(Long.toString(i));
				fileWriter.write(FIELD_SEPARATOR);
				fileWriter.write(str + i);
				fileWriter.write("\n");
				fileWriter.flush();
			}
		}
		catch (IOException e)
		{
			e.printStackTrace();
			assertTrue("Unable to write to file " + fileName, false);
		}
		String columns = "(ID,NAME)";

		Class<?>[] dataTypes = {Long.class, String.class};
		new DataFileLoader().loadDataFromFile(con, fileName, columns, tableName, dataTypes,
				PathConstants.FIELD_SEPARATOR);

		String selectSQL = "SELECT ID,NAME FROM " + tableName;
		int recordCount = 0;
		String[][] rs = null;

		rs = SQLQueryUtil.executeQuery(selectSQL, con);

		for (int i = 0; i < rs.length; i++)
		{
			recordCount++;
			Long id = Long.parseLong(rs[i][0]);
			String name = rs[i][1];
			assertTrue(id > 0 && id < 101);
			assertTrue(name.startsWith(str));
		}
		assertEquals(100, recordCount);
		file.delete();
	}

	/**
	 * @see junit.framework.TestCase#tearDown()
	 */
	@Override
	protected void tearDown()
	{
		if (file != null)
		{
			file.delete();
		}
		SQLQueryUtil.executeUpdate("DROP table " + tableName, con);
	}

	@Override
	protected void finalize() throws Throwable
	{
		super.finalize();
		TestConnectionUtil.close(con);
	}
}
