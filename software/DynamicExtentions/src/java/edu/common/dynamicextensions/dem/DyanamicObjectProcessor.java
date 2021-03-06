
package edu.common.dynamicextensions.dem;

import java.util.Map;

import edu.common.dynamicextensions.dao.impl.DynamicExtensionDAO;
import edu.common.dynamicextensions.domain.Association;
import edu.common.dynamicextensions.domaininterface.BaseAbstractAttributeInterface;
import edu.common.dynamicextensions.domaininterface.userinterface.ContainerInterface;
import edu.common.dynamicextensions.entitymanager.AbstractBaseMetadataManager;
import edu.common.dynamicextensions.entitymanager.EntityManager;
import edu.common.dynamicextensions.entitymanager.EntityManagerInterface;
import edu.common.dynamicextensions.exception.DynamicExtensionsApplicationException;
import edu.common.dynamicextensions.exception.DynamicExtensionsSystemException;
import edu.common.dynamicextensions.processor.InContextApplyDataEntryProcessor;
import edu.common.dynamicextensions.ui.webui.util.WebUIManagerConstants;
import edu.common.dynamicextensions.util.DynamicExtensionsUtility;
import edu.wustl.common.beans.SessionDataBean;
import edu.wustl.dao.HibernateDAO;
import edu.wustl.dao.daofactory.DAOConfigFactory;
import edu.wustl.dao.exception.DAOException;
import edu.wustl.metadata.util.DyExtnObjectCloner;

/**
 * @author pathik_sheth
 *
 */
public class DyanamicObjectProcessor extends AbstractBaseMetadataManager
{

	private final DyExtnObjectCloner cloner = new DyExtnObjectCloner();
	private InContextApplyDataEntryProcessor entryProcessor = new InContextApplyDataEntryProcessor();

	public DyanamicObjectProcessor() throws DAOException
	{
		DynamicExtensionDAO.getInstance();
	}

	/**
	 * This method will pass the changed (modified) values entered into the controls to EntityManager to update them in Database.
	 * @param paramaterObjectMap object received from request.
	 * @return whether object is edited or not.
	 * @throws DynamicExtensionsApplicationException
	 * @throws DynamicExtensionsSystemException
	 * @throws DAOException
	 */
	public Object editObject(Map<String, Object> paramaterObjectMap)
			throws DynamicExtensionsApplicationException, DynamicExtensionsSystemException,
			DAOException
	{
		Map<BaseAbstractAttributeInterface, Object> attributeValueMap = (Map<BaseAbstractAttributeInterface, Object>) paramaterObjectMap
				.get(WebUIManagerConstants.DATA_VALUE_MAP);
		Long recordIdentifier = (Long) paramaterObjectMap.get(WebUIManagerConstants.RECORD_ID);
		SessionDataBean sessionDataBean = (SessionDataBean) paramaterObjectMap
				.get(WebUIManagerConstants.SESSION_DATA_BEAN);
		Long userId = (Long) paramaterObjectMap.get(WebUIManagerConstants.USER_ID);
		ContainerInterface container = (ContainerInterface) paramaterObjectMap
				.get(WebUIManagerConstants.CONTAINER);

		/*boolean isEdited;

		if (container.getAbstractEntity() instanceof EntityInterface)
		{
			EntityManagerInterface entityManager = EntityManager.getInstance();
			EntityInterface entity = (Entity) container.getAbstractEntity();
			//Correct this:
			Map map = attributeValueMap;

			isEdited = entityManager.editData(entity, map, recordIdentifier, null,
					new ArrayList<FileQueryBean>(), sessionDataBean, userId);
		}
		else
		{
			CategoryInterface categoryInterface = ((CategoryEntityInterface) container
					.getAbstractEntity()).getCategory();
			CategoryManagerInterface categoryManager = CategoryManager.getInstance();
			Long categoryRecordId = categoryManager.getRootCategoryEntityRecordIdByEntityRecordId(
					recordIdentifier, categoryInterface.getRootCategoryElement()
							.getTableProperties().getName());
			isEdited = CategoryManager.getInstance().editData(
					(CategoryEntityInterface) container.getAbstractEntity(), attributeValueMap,
					categoryRecordId, sessionDataBean, userId);
		}*/
		return entryProcessor.editDataEntryForm(container, attributeValueMap, recordIdentifier, sessionDataBean);
	}

	/**
	 * This method will pass the values entered into the controls to EntityManager to insert them in Database.
	 * @param paramaterObjectMap object received from request.
	 * @return identifier of the inserted object.
	 * @throws DynamicExtensionsApplicationException
	 * @throws DynamicExtensionsSystemException
	 */
	public Long insertDataEntryForm(Map<String, Object> paramaterObjectMap)
			throws DynamicExtensionsApplicationException, DynamicExtensionsSystemException
	{
		Map<BaseAbstractAttributeInterface, Object> attributeValueMap = (Map<BaseAbstractAttributeInterface, Object>) paramaterObjectMap
				.get(WebUIManagerConstants.DATA_VALUE_MAP);
		SessionDataBean sessionDataBean = (SessionDataBean) paramaterObjectMap
				.get(WebUIManagerConstants.SESSION_DATA_BEAN);
		Long userId = (Long) paramaterObjectMap.get(WebUIManagerConstants.USER_ID);
		ContainerInterface container = (ContainerInterface) paramaterObjectMap
				.get(WebUIManagerConstants.CONTAINER);

		/*Long recordIdentifier = null;

		if (container.getAbstractEntity() instanceof CategoryEntityInterface)
		{
			CategoryInterface categoryInterface = ((CategoryEntityInterface) container
					.getAbstractEntity()).getCategory();
			CategoryManagerInterface categoryManager = CategoryManager.getInstance();
			Long categoryRecordId = categoryManager.insertData(categoryInterface,
					attributeValueMap, sessionDataBean, userId);
			recordIdentifier = categoryManager.getEntityRecordIdByRootCategoryEntityRecordId(
					categoryRecordId, categoryInterface.getRootCategoryElement()
							.getTableProperties().getName());
		}
		else
		{
			Map map = attributeValueMap;
			EntityManagerInterface entityManagerInterface = EntityManager.getInstance();
			recordIdentifier = entityManagerInterface.insertData((EntityInterface) container
					.getAbstractEntity(), map, null, new ArrayList<FileQueryBean>(),
					sessionDataBean, userId);
		}
*/
		return Long.valueOf(entryProcessor.insertDataEntryForm(container, attributeValueMap, sessionDataBean));
	}

	/**
	 * Associate dynamic record with static entity.
	 * @param paramaterObjectMap object received from request.
	 * @throws DynamicExtensionsSystemException
	 * @throws DynamicExtensionsApplicationException
	 */
	public void associateObjects(Map<String, Object> paramaterObjectMap)
			throws DynamicExtensionsSystemException, DynamicExtensionsApplicationException
	{
		Long staticEntityId = (Long) paramaterObjectMap.get(WebUIManagerConstants.STATIC_OBJECT_ID);
		Long dynamicEntityId = (Long) paramaterObjectMap
				.get(WebUIManagerConstants.DYNAMIC_OBJECT_ID);
		Association association = (Association) paramaterObjectMap
				.get(WebUIManagerConstants.ASSOCIATION);
		SessionDataBean sessionDataBean = (SessionDataBean) paramaterObjectMap
				.get(WebUIManagerConstants.SESSION_DATA_BEAN);
		String tmpPackageName = (String) paramaterObjectMap.get(WebUIManagerConstants.PACKAGE_NAME);
		HibernateDAO hibernateDao = null;
		try
		{
			String applicationName = DynamicExtensionDAO.getInstance().getAppName();
			hibernateDao = (HibernateDAO) DAOConfigFactory.getInstance()
					.getDAOFactory(applicationName).getDAO();
			hibernateDao.openSession(sessionDataBean);
			EntityManagerInterface entityManagerInterface = EntityManager.getInstance();
			entityManagerInterface.associateData(staticEntityId, dynamicEntityId, association, tmpPackageName,
					hibernateDao);

		}
		catch (DAOException e)
		{
			throw new DynamicExtensionsSystemException("Error in associating objects", e);
		}

		finally
		{
			DynamicExtensionsUtility.closeDAO(hibernateDao);
		}
	}

	
}
