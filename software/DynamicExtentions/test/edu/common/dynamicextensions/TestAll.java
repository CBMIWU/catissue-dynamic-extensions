
package edu.common.dynamicextensions;

/**
 *
 */

import junit.framework.Test;
import junit.framework.TestSuite;
import edu.common.dynamicextensions.category.TestXMLToCSVConverter;
import edu.common.dynamicextensions.categoryManager.TestCategoryManager;
import edu.common.dynamicextensions.entitymanager.TestEntityManager;
import edu.common.dynamicextensions.entitymanager.TestEntityManagerForAssociations;
import edu.common.dynamicextensions.entitymanager.TestEntityManagerForInheritance;
import edu.common.dynamicextensions.entitymanager.TestEntityManagerWithPrimaryKey;
import edu.common.dynamicextensions.entitymanager.TestEntityMangerForXMIImportExport;
import edu.common.dynamicextensions.entitymanager.TestImportPermissibleValues;
import edu.common.dynamicextensions.host.csd.util.TestCSDUtility;
import edu.common.dynamicextensions.util.DynamicExtensionsBaseTestCase;

/**
 * Test Suite for testing all DE  related classes.
 */
public class TestAll extends DynamicExtensionsBaseTestCase
{

	/**
	 * @param args arg
	 */
	public static void main(String[] args)
	{
		junit.swingui.TestRunner.run(TestAll.class);
	}

	/**
	 * @return test suite
	 */
	public static Test suite()
	{
		TestSuite suite = new TestSuite("Test suite for Query Interface Classes");
		suite.addTestSuite(TestEntityManagerWithPrimaryKey.class);
		suite.addTestSuite(TestEntityManager.class);
		suite.addTestSuite(TestEntityManagerForAssociations.class);
		suite.addTestSuite(TestEntityManagerForInheritance.class);
		suite.addTestSuite(TestEntityMangerForXMIImportExport.class);
		suite.addTestSuite(TestImportPermissibleValues.class);
		suite.addTestSuite(TestCategoryManager.class);
		suite.addTestSuite(TestXMLToCSVConverter.class);
		suite.addTestSuite(TestCSDUtility.class);
		return suite;
	}
}