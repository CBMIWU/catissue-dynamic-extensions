/**
 *
 */

package edu.common.dynamicextensions.processor;

import java.util.ArrayList;
import java.util.List;

import edu.common.dynamicextensions.domaininterface.userinterface.ContainerInterface;
import edu.common.dynamicextensions.entitymanager.EntityManager;
import edu.common.dynamicextensions.entitymanager.EntityManagerInterface;
import edu.common.dynamicextensions.entitymanager.EntityRecord;
import edu.common.dynamicextensions.exception.DynamicExtensionsApplicationException;
import edu.common.dynamicextensions.exception.DynamicExtensionsSystemException;
import edu.common.dynamicextensions.ui.webui.actionform.RecordListForm;

/**
 * @author chetan_patil
 *
 */
public class LoadRecordListProcessor extends BaseDynamicExtensionsProcessor
{

	/**
	 * Default Constructor.
	 */
	protected LoadRecordListProcessor()
	{
		super();
	}

	/**
	 * returns the instance of LoadFormsIndexProcessor.
	 * @return LoadFormsIndexProcessor instance of LoadFormsIndexProcessor
	 */
	public static LoadRecordListProcessor getInstance()
	{
		return new LoadRecordListProcessor();
	}

	/**
     * A call to EntityManager will return the entityList which will then added
     * to actionForm.
     *
     * @param recordListForm
     *            the record list form
     * @param container
     *            the container
     * @param mode
     *            the mode
     * @throws DynamicExtensionsApplicationException
     *             DynamicExtensionsApplicationException
     * @throws DynamicExtensionsSystemException
     *             DynamicExtensionsSystemException
     */
	public void populateRecordIndex(RecordListForm recordListForm, ContainerInterface container,
			String mode) throws DynamicExtensionsApplicationException,
			DynamicExtensionsSystemException
	{
		List<EntityRecord> entityRecordList = null;

		//Quick fix:
		//Method getall records should be invoked with common managers instance
		EntityManagerInterface entityManager = EntityManager.getInstance();
		if (container != null)
		{
			entityRecordList = entityManager.getAllRecords(container.getAbstractEntity());
			container.setMode(mode);
			recordListForm.setContainerIdentifier(container.getId().toString());
		}

		if (entityRecordList == null)
		{
			entityRecordList = new ArrayList<EntityRecord>();
		}
		recordListForm.setEntityRecordList(entityRecordList);
		recordListForm.setMode(mode);
	}

}
