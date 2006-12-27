/**
 * 
 */

package edu.common.dynamicextensions.processor;

import java.util.ArrayList;
import java.util.List;

import edu.common.dynamicextensions.domaininterface.EntityInterface;
import edu.common.dynamicextensions.domaininterface.userinterface.ContainerInterface;
import edu.common.dynamicextensions.entitymanager.EntityManager;
import edu.common.dynamicextensions.entitymanager.EntityManagerInterface;
import edu.common.dynamicextensions.entitymanager.EntityRecord;
import edu.common.dynamicextensions.exception.DynamicExtensionsApplicationException;
import edu.common.dynamicextensions.exception.DynamicExtensionsSystemException;
import edu.common.dynamicextensions.ui.webui.actionform.EditRecordsForm;

/**
 * @author chetan_patil
 *
 */
public class LoadEditRecordsProcessor extends BaseDynamicExtensionsProcessor
{

	/**
	 * Default Constructor.
	 */
	protected LoadEditRecordsProcessor()
	{
	}

	/**
	 * returns the instance of LoadFormsIndexProcessor.
	 * @return LoadFormsIndexProcessor instance of LoadFormsIndexProcessor
	 */
	public static LoadEditRecordsProcessor getInstance()
	{
		return new LoadEditRecordsProcessor();
	}

	/**
	 * A call to EntityManager will return the entityList which will then added to actionForm.
	 * @param loadFormIndexForm FormsIndexForm
	 * @throws DynamicExtensionsApplicationException  DynamicExtensionsApplicationException
	 * @throws DynamicExtensionsSystemException DynamicExtensionsSystemException
	 */
	public void populateRecordIndex(EditRecordsForm editRecordsForm, ContainerInterface container)
			throws DynamicExtensionsApplicationException, DynamicExtensionsSystemException
	{
		List<EntityRecord> entityRecordList = null;

		EntityManagerInterface entityManager = EntityManager.getInstance();
		if (container != null)
		{
			EntityInterface entity = container.getEntity();
			entityRecordList = entityManager.getAllRecords(entity);
		}

		if (entityRecordList == null)
		{
			entityRecordList = new ArrayList<EntityRecord>();
		}
		editRecordsForm.setContainerIdentifier(container.getId().toString());
		editRecordsForm.setEntityRecordList(entityRecordList);
	}
}
