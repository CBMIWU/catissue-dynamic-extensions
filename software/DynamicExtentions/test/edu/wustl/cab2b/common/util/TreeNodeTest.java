
package edu.wustl.cab2b.common.util;

import edu.common.dynamicextensions.util.DynamicExtensionsBaseTestCase;

/**
 * @author chandrakant_talele
 */
public class TreeNodeTest extends DynamicExtensionsBaseTestCase
{

	public void testSetGetValue()
	{
		TreeNode<String> node = new TreeNode<String>("rootNode");
		assertEquals("rootNode", node.getValue());
		assertEquals(0, node.getChildren().size());
	}

	public void testAddChildValue()
	{
		TreeNode<String> parent = new TreeNode<String>("rootNode");
		parent.addChildValue("child");
		assertEquals(1, parent.getChildren().size());
		assertEquals("child", parent.getChildren().get(0).getValue());
	}

	public void testAddChild()
	{
		TreeNode<String> parent = new TreeNode<String>("rootNode");
		TreeNode<String> child = new TreeNode<String>("child");
		parent.addChild(child);
		assertEquals(1, parent.getChildren().size());
		assertEquals("child", parent.getChildren().get(0).getValue());
		// TODO need to see this assertEquals(parent, child.getParent());
	}

	public void testIsLeaf()
	{
		TreeNode<String> parent = new TreeNode<String>("rootNode");
		TreeNode<String> child = new TreeNode<String>("child");
		parent.addChild(child);
		assertTrue(child.isLeaf());
		assertFalse(parent.isLeaf());
		assertTrue(parent.isRoot());
	}

	public void testCompareNode()
	{
		TreeNode<String> parent = new TreeNode<String>("rootNode");
		try
		{
			parent.toString();
			//parent.equals(parent);
			parent.hashCode();
			assertTrue(true);
		}
		catch (Exception ex)
		{
		    ex.printStackTrace();
			assertTrue(false);
		}
	}
}
