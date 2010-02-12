
package edu.common.dynamicextensions.ui.webui.action;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Stack;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import edu.common.dynamicextensions.domain.DoubleAttributeTypeInformation;
import edu.common.dynamicextensions.domain.FloatAttributeTypeInformation;
import edu.common.dynamicextensions.domain.NumericAttributeTypeInformation;
import edu.common.dynamicextensions.domaininterface.AssociationInterface;
import edu.common.dynamicextensions.domaininterface.AssociationMetadataInterface;
import edu.common.dynamicextensions.domaininterface.AttributeInterface;
import edu.common.dynamicextensions.domaininterface.AttributeTypeInformationInterface;
import edu.common.dynamicextensions.domaininterface.BaseAbstractAttributeInterface;
import edu.common.dynamicextensions.domaininterface.userinterface.AbstractContainmentControlInterface;
import edu.common.dynamicextensions.domaininterface.userinterface.ContainerInterface;
import edu.common.dynamicextensions.exception.DynamicExtensionsCacheException;
import edu.common.dynamicextensions.exception.DynamicExtensionsApplicationException;
import edu.common.dynamicextensions.exception.DynamicExtensionsSystemException;
import edu.common.dynamicextensions.processor.LoadDataEntryFormProcessor;
import edu.common.dynamicextensions.ui.webui.actionform.DataEntryForm;
import edu.common.dynamicextensions.ui.webui.util.CacheManager;
import edu.common.dynamicextensions.ui.webui.util.UserInterfaceiUtility;
import edu.common.dynamicextensions.ui.webui.util.WebUIManagerConstants;
import edu.common.dynamicextensions.util.DataValueMapUtility;
import edu.common.dynamicextensions.util.DynamicExtensionsUtility;
import edu.common.dynamicextensions.util.global.DEConstants;
import edu.common.dynamicextensions.util.global.DEConstants.AssociationType;
import edu.wustl.cab2b.server.cache.EntityCache;
import edu.wustl.common.actionForm.AbstractActionForm;

/**
 * @author sujay_narkar, chetan_patil
 *
 */
public class LoadDataEntryFormAction extends BaseDynamicExtensionsAction
{

	/* (non-Javadoc)
	 * @see org.apache.struts.actions.DispatchAction#execute(org.apache.struts.action.ActionMapping, org.apache.struts.action.ActionForm, javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
	 */
	public ActionForward execute(final ActionMapping mapping, final ActionForm form,
			final HttpServletRequest request, final HttpServletResponse response)
			throws DynamicExtensionsSystemException, DynamicExtensionsApplicationException
	{
		final DataEntryForm dataEntryForm = (DataEntryForm) form;
		try
		{
			cacheCallBackURL(request);
			final ContainerInterface containerInterface = getContainerInterface(request);

			String recordId = request.getParameter("recordIdentifier");
			if ((recordId != null) && !recordId.equals(""))
			{
				CacheManager.addObjectToCache(request, "rootRecordIdentifier", recordId);
			}
			else
			{
				recordId = (String) CacheManager
						.getObjectFromCache(request, "rootRecordIdentifier");
				if (recordId == null)
				{
					recordId = "";
				}
			}

			final LoadDataEntryFormProcessor loadDataEntryFormProcessor = LoadDataEntryFormProcessor
					.getInstance();
			final Map<BaseAbstractAttributeInterface, Object> recordMap = loadDataEntryFormProcessor
					.getValueMapFromRecordId(containerInterface.getAbstractEntity(), recordId);
			Stack<ContainerInterface> containerStack = (Stack<ContainerInterface>) CacheManager
					.getObjectFromCache(request, DEConstants.CONTAINER_STACK);
			Stack<Map<BaseAbstractAttributeInterface, Object>> valueMapStack = (Stack<Map<BaseAbstractAttributeInterface, Object>>) CacheManager
					.getObjectFromCache(request, DEConstants.VALUE_MAP_STACK);

			if (containerStack != null)
			{
				dataEntryForm.setPreviousDataMap(valueMapStack.peek());

			}
			else
			{
				containerStack = new Stack<ContainerInterface>();
				CacheManager.addObjectToCache(request, DEConstants.CONTAINER_STACK, containerStack);
				valueMapStack = new Stack<Map<BaseAbstractAttributeInterface, Object>>();
				CacheManager.addObjectToCache(request, DEConstants.VALUE_MAP_STACK, valueMapStack);
				UserInterfaceiUtility.addContainerInfo(containerStack, containerInterface,
						valueMapStack, recordMap);
			}

			final Set<AttributeInterface> attributes = new HashSet<AttributeInterface>();
			addPrecisionZeroes(recordMap, attributes);
			updateStacks(dataEntryForm, containerStack, valueMapStack);

			if ((!containerStack.isEmpty()) && (!valueMapStack.isEmpty()))
			{
				String mode = request.getParameter(WebUIManagerConstants.MODE_PARAM_NAME);
				if ((mode == null) || !mode.equals(""))
				{
					mode = dataEntryForm.getMode();
				}

				loadDataEntryFormProcessor.loadDataEntryForm((AbstractActionForm) form,
						containerStack.peek(), valueMapStack.peek(), mode, recordId);
			}

			updateTopLevelEntitiyInfo(containerStack, dataEntryForm);

			if (dataEntryForm.getErrorList().isEmpty())
			{
				clearFormValues(dataEntryForm);
			}
			if ((valueMapStack.peek() != null) && !valueMapStack.peek().isEmpty())
			{
				DataValueMapUtility.updateDataValueMapDataLoading(valueMapStack.peek(),
						containerStack.peek());
			}
			return mapping.findForward("Success");
		}
		catch (DynamicExtensionsCacheException cacheException)
		{
			List<String> list = new ArrayList<String>();
			list.add(cacheException.getMessage());
			dataEntryForm.setErrorList(list);
			return mapping.findForward(WebUIManagerConstants.CACHE_ERROR);
		}

	}

	/**
	 * This method returns the Container Identifier form the given request.
	 * @param request HttpServletRequest
	 * @return the Container Identifier
	 */
	private String getContainerId(final HttpServletRequest request)
	{
		String identifier = "";
		identifier = request.getParameter("containerIdentifier");
		if ((identifier == null) || identifier.equals(""))
		{
			identifier = (String) request.getAttribute("containerIdentifier");
		}
		return identifier;
	}

	/**
	 * This method flushes the values of the DataEntryForm ActionForm.
	 * @param form DataEntryForm ActionForm
	 */
	private void clearFormValues(final ActionForm form)
	{
		final DataEntryForm dataEntryForm = (DataEntryForm) form;
		dataEntryForm.setChildRowId("");
		dataEntryForm.setChildContainerId("");
	}

	/**
	 *
	 * @param request
	 */
	private void cacheCallBackURL(final HttpServletRequest request)
	{
		final String callBackURL = request.getParameter(WebUIManagerConstants.CALLBACK_URL_PARAM_NAME);
		final String userId = request.getParameter(WebUIManagerConstants.USER_ID);
		if ((callBackURL != null) && !callBackURL.equals(""))
		{
			CacheManager.clearCache(request);
			CacheManager.addObjectToCache(request, DEConstants.CALLBACK_URL, callBackURL);
			CacheManager.addObjectToCache(request, WebUIManagerConstants.USER_ID, userId);
		}
	}

	/**
	 *
	 * @param request
	 * @return
	 * @throws DynamicExtensionsSystemException
	 * @throws DynamicExtensionsApplicationException
	 */
	private ContainerInterface getContainerInterface(final HttpServletRequest request)
			throws DynamicExtensionsSystemException, DynamicExtensionsApplicationException
	{
		ContainerInterface containerInterface = (ContainerInterface) CacheManager
				.getObjectFromCache(request, DEConstants.CONTAINER_INTERFACE);
		final String containerIdentifier = getContainerId(request);
		if ((containerIdentifier != null) || (containerInterface == null))
		{
			UserInterfaceiUtility.clearContainerStack(request);

			final Long containerId =Long.valueOf(containerIdentifier);

			containerInterface = EntityCache.getInstance().getContainerById(containerId);
			if (containerInterface != null)
			{
				containerInterface.getContainerValueMap().clear();
				DynamicExtensionsUtility.cleanContainerControlsValue(containerInterface);
			}
			else
			{
				containerInterface = DynamicExtensionsUtility
						.getContainerByIdentifier(containerIdentifier);

			}

			CacheManager.addObjectToCache(request, DEConstants.CONTAINER_INTERFACE,
					containerInterface);
		}

		return containerInterface;
	}

	/**
	 *
	 * @param containerStack
	 * @param dataEntryForm
	 */
	private void updateTopLevelEntitiyInfo(final Stack<ContainerInterface> containerStack,
			final DataEntryForm dataEntryForm)
	{
		if (containerStack.size() > 1)
		{
			dataEntryForm.setIsTopLevelEntity(false);
		}
		else
		{
			dataEntryForm.setIsTopLevelEntity(true);
		}
	}

	/**
	 *
	 * @param form
	 * @param containerStack
	 * @param valueMapStack
	 */
	private void updateStacks(final DataEntryForm dataEntryForm,
			final Stack<ContainerInterface> containerStack,
			final Stack<Map<BaseAbstractAttributeInterface, Object>> valueMapStack)
	{
		final String dataEntryOperation = dataEntryForm.getDataEntryOperation();

		if ((dataEntryOperation != null) && dataEntryOperation.equalsIgnoreCase("insertChildData")
				&& (dataEntryForm.getErrorList().isEmpty()))
		{
			final String childContainerId = dataEntryForm.getChildContainerId();
			final AbstractContainmentControlInterface associationControl = UserInterfaceiUtility
					.getAssociationControl(containerStack.peek(),
							childContainerId);

			final Map<BaseAbstractAttributeInterface, Object> containerValueMap = valueMapStack.peek();
			final AssociationMetadataInterface association = (AssociationMetadataInterface) associationControl
					.getBaseAbstractAttribute();
			final List<Map<BaseAbstractAttributeInterface, Object>> childContainerValueMapList = (List<Map<BaseAbstractAttributeInterface, Object>>) containerValueMap
					.get(association);

			Map<BaseAbstractAttributeInterface, Object> childContainerValueMap = null;
			if (associationControl.isCardinalityOneToMany())
			{
				childContainerValueMap = childContainerValueMapList.get(Integer
						.parseInt(dataEntryForm.getChildRowId()) - 1);
			}
			else
			{
				childContainerValueMap = childContainerValueMapList.get(0);
			}

			final ContainerInterface childContainer = associationControl.getContainer();
			UserInterfaceiUtility.addContainerInfo(containerStack, childContainer, valueMapStack,
					childContainerValueMap);
		}
		else if ((dataEntryOperation != null)
				&& dataEntryOperation.equalsIgnoreCase("insertParentData"))
		{
			final List<String> errorList = dataEntryForm.getErrorList();
			if (((errorList != null) && (errorList.isEmpty()))
					&& (((containerStack != null) && !(containerStack.isEmpty())) && ((valueMapStack != null) && !(valueMapStack
							.isEmpty()))))
			{
				UserInterfaceiUtility.removeContainerInfo(containerStack, valueMapStack);
			}
		}
	}

	/**
	 * Append number of zeroes to the output depending on precision entered while creating the attribute of double type.
	 * @param recordMap
	 * @param processedAttributes
	 */
	private void addPrecisionZeroes(final Map<BaseAbstractAttributeInterface, Object> recordMap,
			final Set<AttributeInterface> processedAttributes)
	{
		// If the value is 1.48 and precision entered for it is 3, make it appear as 1.480
		final Set<BaseAbstractAttributeInterface> recordMapKeySet = recordMap.keySet();
		final Iterator<BaseAbstractAttributeInterface> iter = recordMapKeySet.iterator();

		while (iter.hasNext())
		{
			final Object object = iter.next();

			if (object instanceof AttributeInterface)
			{
				final AttributeInterface currentAttribute = (AttributeInterface) object;

				final AttributeTypeInformationInterface attributeTypeInformation = (currentAttribute)
						.getAttributeTypeInformation();

				if (attributeTypeInformation instanceof NumericAttributeTypeInformation)
				{
					if (processedAttributes.contains(currentAttribute))
					{
						return;
					}
					else
					{
						processedAttributes.add(currentAttribute);
					}
					final int decimalPlaces = ((NumericAttributeTypeInformation) attributeTypeInformation)
							.getDecimalPlaces();
					String value = (String) recordMap.get(currentAttribute);
					if (value.contains(".") && !value.contains("E"))
					{
						final int placesAfterDecimal = value.length() - (value.indexOf('.') + 1);

						if (placesAfterDecimal != decimalPlaces)
						{
							for (int j = decimalPlaces; j > placesAfterDecimal; j--)
							{
								value = value + "0";
							}
							recordMap.put(currentAttribute, value);
						}
					}
					else
					{
						if (((attributeTypeInformation instanceof DoubleAttributeTypeInformation)
								|| (attributeTypeInformation instanceof FloatAttributeTypeInformation))&&
								((value.length() != 0) && !value.contains("E")))
						{
								if (decimalPlaces != 0)
								{
									value = value + ".";
								}

								for (int i = 0; i < decimalPlaces; i++)
								{
									value = value + "0";
								}
								recordMap.put(currentAttribute, value);
						}
					}
				}
			}
			else if (object instanceof AssociationInterface)
			{
				final AssociationMetadataInterface association = (AssociationMetadataInterface) object;
				if (association.getAssociationType() != null)
				{
					final String associationType = association.getAssociationType().getValue();
					if ((associationType != null) && (recordMap.get(object) != null)
							&& associationType.equals(AssociationType.CONTAINTMENT.getValue()))
					{
							final List<Map<BaseAbstractAttributeInterface, Object>> innerRecordsList = (List<Map<BaseAbstractAttributeInterface, Object>>) recordMap
									.get(object);
							for (final Map<BaseAbstractAttributeInterface, Object> innerMap : innerRecordsList)
							{
								addPrecisionZeroes(innerMap, processedAttributes);
							}
					}
				}
			}
		}
	}

}
