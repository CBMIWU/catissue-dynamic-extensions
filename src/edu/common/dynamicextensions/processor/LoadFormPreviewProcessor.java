
package edu.common.dynamicextensions.processor;

import edu.common.dynamicextensions.domaininterface.userinterface.ContainerInterface;
import edu.common.dynamicextensions.entitymanager.MockEntityManager;
import edu.common.dynamicextensions.exception.DynamicExtensionsApplicationException;
import edu.common.dynamicextensions.ui.webui.actionform.PreviewForm;

/**
 * This is the Processor class for Preview Form
 * @author chetan_patil
 * @version 1.0
 */
public class LoadFormPreviewProcessor extends BaseDynamicExtensionsProcessor
{
	/**
	 * Default constructor
	 */
	protected LoadFormPreviewProcessor()
	{
		// TODO Auto-generated constructor stub
	}

	/**
	 * This method returns the instance of LoadFormPreviewProcessor
	 * @return LoadFormPreviewProcessor instance
	 */
	public static LoadFormPreviewProcessor getInstance()
	{
		return new LoadFormPreviewProcessor();
	}

	/**
	 * This method populates the data from the PrewiewForm action
	 * @param previewForm The PreviewForm action form 
	 * @throws DynamicExtensionsApplicationException if Conatainer does not exists.
	 */
	public void populatePreviewForm(PreviewForm previewForm) throws DynamicExtensionsApplicationException
	{
		ContainerInterface containerInterface = null;
		try
		{
			containerInterface = new MockEntityManager().getContainer("dummyEntity");
			if (containerInterface != null)
			{
				previewForm.setContainerInterface(containerInterface);
			}
			else
			{
				throw new DynamicExtensionsApplicationException("Container does not exists.");
			}
		}
		catch (DynamicExtensionsApplicationException dynamicExtensionsApplicationException)
		{
			throw dynamicExtensionsApplicationException;
		}
	}
}