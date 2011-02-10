package edu.common.dynamicextensions.ui.webui.action;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import edu.common.dynamicextensions.dem.AbstractHandler;
import edu.common.dynamicextensions.domaininterface.AssociationInterface;
import edu.common.dynamicextensions.domaininterface.EntityGroupInterface;
import edu.common.dynamicextensions.domaininterface.EntityInterface;
import edu.common.dynamicextensions.exception.DynamicExtensionsApplicationException;
import edu.common.dynamicextensions.exception.DynamicExtensionsSystemException;
import edu.common.dynamicextensions.ui.webui.util.WebUIManagerConstants;
import edu.common.dynamicextensions.util.DynamicExtensionsUtility;
import edu.common.dynamicextensions.util.EntityGroupManagerUtil;
import edu.wustl.cab2b.server.cache.EntityCache;
import edu.wustl.common.util.logger.Logger;
import edu.wustl.dao.HibernateDAO;
import edu.wustl.dao.exception.DAOException;

public class UpdateCache extends AbstractHandler {

	/**
	 * logger for information.
	 */
	protected static final Logger LOGGER = Logger
			.getCommonLogger(UpdateCache.class);

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		List<EntityGroupInterface> entityGroupColl = null;
		try {
			// 1: get entity group id to be updated from request
			initializeParamaterObjectMap(req);

			String operation = (String) paramaterObjectMap.get(WebUIManagerConstants.OPERATION);
			LOGGER.info("Cache operation:"+operation);
			if(WebUIManagerConstants.UPDATE_CACHE.endsWith(operation))
			{
				updateCache();

			}else if(WebUIManagerConstants.LOCK_FORMS.endsWith(operation))
			{
				lockForms();
			}else if(WebUIManagerConstants.RELEASE_FORMS.endsWith(operation))
			{
				relaseForms();
			}

		} catch (DynamicExtensionsApplicationException e) {
			LOGGER.error(e);
		} catch (DynamicExtensionsSystemException e) {
			LOGGER.error(e);
		}

	}

	private void relaseForms() throws DynamicExtensionsSystemException {
		EntityGroupInterface entityGroupInterface = (EntityGroupInterface) paramaterObjectMap.get(WebUIManagerConstants.ENTITY_GROUP);
		EntityCache.getInstance().releaseAllContainer(EntityGroupManagerUtil.getAssociatedFormId(entityGroupInterface));
		LOGGER.info("Release forms successfully");
	}

	private void lockForms() throws DynamicExtensionsSystemException {
		EntityGroupInterface entityGroupInterface = (EntityGroupInterface) paramaterObjectMap.get(WebUIManagerConstants.ENTITY_GROUP);
		EntityCache.getInstance().lockAllContainer(EntityGroupManagerUtil.getAssociatedFormId(entityGroupInterface));
		LOGGER.info("Locked forms successfully");
	}

	private void updateCache() throws DynamicExtensionsSystemException {
		// 2: refresh entity cache with latest version of the updated entity
		// group
		EntityCache cache = refreshCache( (EntityGroupInterface) paramaterObjectMap
				.get(WebUIManagerConstants.ENTITY_GROUP));

		Collection<AssociationInterface> association = (Collection<AssociationInterface>) paramaterObjectMap
				.get(WebUIManagerConstants.ASSOCIATION);
		EntityInterface hookEntity = (EntityInterface) cache
				.getEntityGroupById(1L).getEntityByName(
						(String) paramaterObjectMap
								.get(WebUIManagerConstants.ENTITY));
		// 3: add intermodel association between hook entity and the main
		// container
		// entities to the static entity group
		addIntermodelAssociation(cache, association, hookEntity);
		LOGGER.info("Updated cache successfully");
	}

	private EntityCache refreshCache(EntityGroupInterface entityGroup)
			throws DynamicExtensionsSystemException {
		HibernateDAO dao = DynamicExtensionsUtility.getHibernateDAO();
		try {
			dao.update(entityGroup);
			dao.commit();
			dao.closeSession();
		} catch (DAOException e) {
			throw new DynamicExtensionsSystemException("Error refreshing cache",e);
		}


		List<EntityGroupInterface> entityGroupColl;
		EntityCache cache = EntityCache.getInstance();
		entityGroupColl = new ArrayList<EntityGroupInterface>();
		entityGroupColl.add(entityGroup);
		cache.createCache(entityGroupColl);
		return cache;
	}

	private void addIntermodelAssociation(EntityCache cache,
			Collection<AssociationInterface> association,
			EntityInterface hookEntity) {
		for (AssociationInterface associationInterface : association) {
			associationInterface.setEntity(hookEntity);
			associationInterface.setTargetEntity(cache
					.getEntityById(associationInterface.getTargetEntity()
							.getId()));
			hookEntity.addAssociation(associationInterface);
			LOGGER.info(associationInterface.getTargetEntity().getName()
					+ " associated successfully.");
		}
	}

}
