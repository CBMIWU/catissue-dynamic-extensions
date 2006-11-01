
package edu.common.dynamicextensions.processor;

import java.util.Collection;
import java.util.Map;
import java.util.Set;

import edu.common.dynamicextensions.domaininterface.AbstractAttributeInterface;
import edu.common.dynamicextensions.domaininterface.EntityInterface;
import edu.common.dynamicextensions.domaininterface.userinterface.ContainerInterface;
import edu.common.dynamicextensions.domaininterface.userinterface.ControlInterface;
import edu.common.dynamicextensions.entitymanager.EntityManager;
import edu.common.dynamicextensions.entitymanager.EntityManagerInterface;
import edu.common.dynamicextensions.exception.DynamicExtensionsApplicationException;
import edu.common.dynamicextensions.exception.DynamicExtensionsSystemException;
import edu.common.dynamicextensions.ui.webui.actionform.DataEntryForm;
import edu.common.dynamicextensions.util.DynamicExtensionsUtility;
import edu.wustl.common.actionForm.AbstractActionForm;

/**
 * @author sujay_narkar
 *
 */
public class LoadDataEntryFormProcessor 
{

	/**
	 * Protected constructor for entity processor
	 *
	 */
	protected LoadDataEntryFormProcessor() 
	{

	}

	/**
	 * this method gets the new instance of the entity processor to the caller.
	 * @return EntityProcessor EntityProcessor instance
	 */
	public static LoadDataEntryFormProcessor getInstance () 
	{
		return new LoadDataEntryFormProcessor();

	}

	/**
	 * 
	 * @param actionForm
	 * @param containerInterface
	 * @throws DynamicExtensionsSystemException 
	 * @throws DynamicExtensionsApplicationException 
	 */
	@SuppressWarnings("unchecked")
	public void loadDataEntryForm(AbstractActionForm actionForm,ContainerInterface containerInterface,String containerIdentifier,
			String recordId) throws DynamicExtensionsSystemException, DynamicExtensionsApplicationException  
	{
		try
		{
			DataEntryForm dataEntryForm = (DataEntryForm) actionForm;
			Map recordMap = null;
			
			if(containerInterface == null)
			{
				containerInterface = DynamicExtensionsUtility.getContainerByIdentifier(containerIdentifier);
			}

			if (recordId != null)
			{
				//Get corresponding Entity of the Container
				EntityInterface entity = containerInterface.getEntity();
				// Get corresponding Control Collection of the Container
				Collection<ControlInterface> controlCollection = containerInterface.getControlCollection();

				//Get Records of the corresponding Entity
				EntityManagerInterface entityManager = EntityManager.getInstance();
				recordMap = entityManager.getRecordById(entity, Long.valueOf(recordId));

				Set<Map.Entry> recordSet = recordMap.entrySet();
				for (Map.Entry recordNode : recordSet)
				{
					String recordAttributeName = (String) recordNode.getKey();
					String recordAttributeValue = (String) recordNode.getValue();
					
					for (ControlInterface control : controlCollection)
					{
						AbstractAttributeInterface controlAbstractAttribute = control.getAbstractAttribute();
						if (controlAbstractAttribute.getName().equals(recordAttributeName))
						{
							if(recordAttributeValue != null)
							{
								control.setValue(recordAttributeValue);
							}
						}
					}
				}
			}
			dataEntryForm.setContainerInterface(containerInterface);
		}
		catch (DynamicExtensionsSystemException dynamicExtensionsSystemException)
		{
			throw dynamicExtensionsSystemException;
		}
		catch (DynamicExtensionsApplicationException dynamicExtensionsApplicationException)
		{
			throw dynamicExtensionsApplicationException;
		}
	}

}
