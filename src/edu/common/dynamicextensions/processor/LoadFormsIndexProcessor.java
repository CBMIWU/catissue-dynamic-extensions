
package edu.common.dynamicextensions.processor;

import java.util.Collection;
import java.util.HashSet;

import edu.common.dynamicextensions.domaininterface.userinterface.ContainerInterface;
import edu.common.dynamicextensions.entitymanager.EntityManager;
import edu.common.dynamicextensions.exception.DynamicExtensionsApplicationException;
import edu.common.dynamicextensions.exception.DynamicExtensionsSystemException;
import edu.common.dynamicextensions.ui.webui.actionform.FormsIndexForm;

/**
 * Populates  the actonForm with required data eg. entityList. 
 * @author deepti_shelar
 *
 */
public class LoadFormsIndexProcessor extends BaseDynamicExtensionsProcessor
{
	/**
	 * Default Constructor.
	 */
	protected LoadFormsIndexProcessor()
	{
	}

	/**
	 * returns the instance of LoadFormsIndexProcessor.
	 */
	public static LoadFormsIndexProcessor getInstance()
	{
		return new LoadFormsIndexProcessor();
	}

	/**
	 * A call to EntityManager will return the entityList which will then added to actionForm.
	 * @param loadFormIndexForm
	 * @throws DynamicExtensionsApplicationException 
	 * @throws DynamicExtensionsSystemException
	 */
	public void populateFormsIndex(FormsIndexForm loadFormIndexForm) throws DynamicExtensionsApplicationException, DynamicExtensionsSystemException
	{
		Collection<ContainerInterface> containerCollection = null;
		try
		{
			EntityManager entityManager = EntityManager.getInstance();
			containerCollection = entityManager.getAllContainers();

			/*for(ContainerInterface container: containerCollection)
			 {
			 EntityInterface entity = container.getEntity();
			 Date date = entity.getCreatedDate();
			 }*/

			if (containerCollection == null)
			{
				containerCollection = new HashSet<ContainerInterface>();
			}
			loadFormIndexForm.setContainerCollection(containerCollection);
		}
		catch (DynamicExtensionsApplicationException e)
		{
			throw e;
		}
		catch (DynamicExtensionsSystemException e)
		{
			throw e;
		}
	}
}
