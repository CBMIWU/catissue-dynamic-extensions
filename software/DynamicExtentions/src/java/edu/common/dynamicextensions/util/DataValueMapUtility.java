
package edu.common.dynamicextensions.util;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

import javax.xml.bind.JAXBException;

import net.sf.ehcache.CacheException;

import org.xml.sax.SAXException;

import edu.common.dynamicextensions.DEIntegration.DEIntegration;
import edu.common.dynamicextensions.bizlogic.BizLogicFactory;
import edu.common.dynamicextensions.bizlogic.RecordEntryBizLogic;
import edu.common.dynamicextensions.domain.Association;
import edu.common.dynamicextensions.domain.CategoryEntityRecord;
import edu.common.dynamicextensions.domain.DateAttributeTypeInformation;
import edu.common.dynamicextensions.domaininterface.AbstractAttributeInterface;
import edu.common.dynamicextensions.domaininterface.AssociationInterface;
import edu.common.dynamicextensions.domaininterface.AssociationMetadataInterface;
import edu.common.dynamicextensions.domaininterface.AttributeInterface;
import edu.common.dynamicextensions.domaininterface.AttributeTypeInformationInterface;
import edu.common.dynamicextensions.domaininterface.BaseAbstractAttributeInterface;
import edu.common.dynamicextensions.domaininterface.CategoryAssociationInterface;
import edu.common.dynamicextensions.domaininterface.CategoryAttributeInterface;
import edu.common.dynamicextensions.domaininterface.CategoryEntityInterface;
import edu.common.dynamicextensions.domaininterface.CategoryInterface;
import edu.common.dynamicextensions.domaininterface.userinterface.AbstractContainmentControlInterface;
import edu.common.dynamicextensions.domaininterface.userinterface.ContainerInterface;
import edu.common.dynamicextensions.domaininterface.userinterface.ControlInterface;
import edu.common.dynamicextensions.entitymanager.CategoryManager;
import edu.common.dynamicextensions.entitymanager.CategoryManagerInterface;
import edu.common.dynamicextensions.entitymanager.EntityManagerUtil;
import edu.common.dynamicextensions.exception.DynamicExtensionsApplicationException;
import edu.common.dynamicextensions.exception.DynamicExtensionsSystemException;
import edu.common.dynamicextensions.util.global.DEConstants;
import edu.common.dynamicextensions.xmi.exporter.XMIExporterUtility;
import edu.wustl.cab2b.server.cache.EntityCache;
import edu.wustl.dao.exception.DAOException;

/**
 * @author kunal_kamble
 * This class has utility methods on the data value map that are used for manipulating the map.
 */

/**
 * @author pathik_sheth
 *
 */
public final class DataValueMapUtility
{

	private static final String FOR_DATA_LOADING = "forDataLoading";
	private static final String FOR_DATA_STORING = "forDataStoring";

	/**
	 * Private constructor with no argument.
	 */
	private DataValueMapUtility()
	{
		//Empty constructor.
	}

	/**
	 * @param rootValueMap
	 * @param rootContainerInterface
	 * @param purpose
	 */

	private static void modifyDataValueMap(
			Map<BaseAbstractAttributeInterface, Object> rootValueMap,
			ContainerInterface rootContainerInterface, String purpose)
	{
		for (ControlInterface control : rootContainerInterface.getControlCollection())
		{
			if (control instanceof AbstractContainmentControlInterface)
			{
				modifyMapForContainmentControl(rootValueMap, purpose, control);
			}
		}

		if (rootContainerInterface.getChildContainerCollection() != null
				&& !rootContainerInterface.getChildContainerCollection().isEmpty())
		{
			modifyValueMapForChildContainers(rootValueMap, rootContainerInterface, purpose);
		}
	}

	/**
	 * Modify data value map for child containers.
	 * @param rootValueMap data value map.
	 * @param rootContainerInterface root category.
	 * @param purpose purpose of populating the data value map.
	 */
	private static void modifyValueMapForChildContainers(
			Map<BaseAbstractAttributeInterface, Object> rootValueMap,
			ContainerInterface rootContainerInterface, String purpose)
	{
		for (ContainerInterface containerInterface : rootContainerInterface
				.getChildContainerCollection())
		{
			if (rootValueMap != null)
			{

				if (FOR_DATA_LOADING.equals(purpose))
				{
					updateMap(rootContainerInterface.getAbstractEntity().getAssociation(
							containerInterface.getAbstractEntity()), rootValueMap);
				}
				else if (FOR_DATA_STORING.equals(purpose))
				{

					updateMap(rootContainerInterface.getAbstractEntity().getAssociation(
							containerInterface.getAbstractEntity()), rootValueMap,
							containerInterface);
				}
			}
		}
	}

	/**
	 * Modify data value map for containment control.
	 * @param rootValueMap root category.
	 * @param purpose purpose of populating the data value map.
	 * @param control control for which data value map is being populated.
	 */
	private static void modifyMapForContainmentControl(
			Map<BaseAbstractAttributeInterface, Object> rootValueMap, String purpose,
			ControlInterface control)
	{
		AbstractContainmentControlInterface abstractContainmentControl = (AbstractContainmentControlInterface) control;
		if (rootValueMap.get(abstractContainmentControl.getBaseAbstractAttribute()) instanceof List)
		{
			List<Map<BaseAbstractAttributeInterface, Object>> list = (List<Map<BaseAbstractAttributeInterface, Object>>) rootValueMap
					.get(abstractContainmentControl.getBaseAbstractAttribute());
			for (Map<BaseAbstractAttributeInterface, Object> map : list)
			{
				modifyDataValueMap(map, abstractContainmentControl.getContainer(), purpose);
			}
		}
		else if (rootValueMap.get(abstractContainmentControl.getBaseAbstractAttribute()) != null)
		{
			modifyDataValueMap((Map<BaseAbstractAttributeInterface, Object>) rootValueMap
					.get(abstractContainmentControl.getBaseAbstractAttribute()),
					abstractContainmentControl.getContainer(), purpose);
		}
	}

	/**
	 * This method update the value map generated by the manager classes.
	 * This transformation of map is required for the display of controls of
	 * the different container that are under the same display label.
	 * @param rootValueMap
	 * @param rootContainerInterface
	 */
	public static void updateDataValueMapDataLoading(
			Map<BaseAbstractAttributeInterface, Object> rootValueMap,
			ContainerInterface rootContainerInterface)
	{
		modifyDataValueMap(rootValueMap, rootContainerInterface, FOR_DATA_LOADING);

	}

	/**
	 * @param rootValueMap
	 * @param rootContainerInterface
	 */
	public static void updateDataValueMapForDataEntry(
			Map<BaseAbstractAttributeInterface, Object> rootValueMap,
			ContainerInterface rootContainerInterface)
	{
		modifyDataValueMap(rootValueMap, rootContainerInterface, FOR_DATA_STORING);

	}

	/**
	 * This method updates map for the attribute present within same display label for data storing
	 * @param assocation
	 * @param rootValueMap
	 * @param containerInterface
	 */
	private static void updateMap(BaseAbstractAttributeInterface assocation,
			Map<BaseAbstractAttributeInterface, Object> rootValueMap,
			ContainerInterface containerInterface)
	{
		Set<BaseAbstractAttributeInterface> set = rootValueMap.keySet();
		List<Map<BaseAbstractAttributeInterface, Object>> list = new ArrayList<Map<BaseAbstractAttributeInterface, Object>>();
		Map<BaseAbstractAttributeInterface, Object> map = new HashMap<BaseAbstractAttributeInterface, Object>();
		list.add(map);
		for (ControlInterface controlInterface : containerInterface.getControlCollection())
		{
			if (set.contains(controlInterface.getBaseAbstractAttribute()))
			{
				map.put(controlInterface.getBaseAbstractAttribute(), rootValueMap
						.get(controlInterface.getBaseAbstractAttribute()));
				rootValueMap.remove(controlInterface.getBaseAbstractAttribute());
			}

		}
		updateRecordIdInMap(set, containerInterface, rootValueMap, map);

		rootValueMap.put(assocation, list);
	}

	/**
	 * This method updates recordId for the attribute present within same display label in Map
	 * @param set
	 * @param containerInterface
	 * @param rootValueMap
	 * @param map
	 */
	private static void updateRecordIdInMap(Set<BaseAbstractAttributeInterface> set,
			ContainerInterface containerInterface,
			Map<BaseAbstractAttributeInterface, Object> rootValueMap,
			Map<BaseAbstractAttributeInterface, Object> map)
	{
		for (Iterator iterator = set.iterator(); iterator.hasNext();)
		{
			BaseAbstractAttributeInterface baseAbstractAttrIntf = (BaseAbstractAttributeInterface) iterator
					.next();
			if (baseAbstractAttrIntf.getName().equalsIgnoreCase(
					containerInterface.getAbstractEntity().getName()))
			{
				map.put(baseAbstractAttrIntf, rootValueMap.get(baseAbstractAttrIntf));
				iterator.remove();
			}

		}
	}

	/**
	 * This method updates map for the attribute present within same display label for data loading
	 * @param assocation
	 * @param rootValueMap
	 */
	private static void updateMap(BaseAbstractAttributeInterface assocation,
			Map<BaseAbstractAttributeInterface, Object> rootValueMap)
	{
		if (rootValueMap.get(assocation) != null)
		{
			if (rootValueMap.get(assocation) instanceof List)
			{
				setValueForAssosiation(assocation, rootValueMap);
			}
			else
			{
				Map<BaseAbstractAttributeInterface, Object> map = (Map<BaseAbstractAttributeInterface, Object>) rootValueMap
						.get(assocation);
				for (BaseAbstractAttributeInterface abstractAttribute : map.keySet())
				{
					rootValueMap.put(abstractAttribute, rootValueMap.get(abstractAttribute));
				}
			}
			cleanMap((List<Map<BaseAbstractAttributeInterface, Object>>) rootValueMap
					.get(assocation));
		}
	}

	private static void cleanMap(List<Map<BaseAbstractAttributeInterface, Object>> rootValueMap)
	{
		Iterator<Map<BaseAbstractAttributeInterface, Object>> valueMapIterator = rootValueMap
				.iterator();
		while (valueMapIterator.hasNext())
		{
			Map<BaseAbstractAttributeInterface, Object> valueMap = valueMapIterator.next();
			Iterator<BaseAbstractAttributeInterface> values = valueMap.keySet().iterator();
			while (values.hasNext())
			{
				BaseAbstractAttributeInterface attribute = values.next();
				if (!(attribute instanceof CategoryEntityRecord))
				{
					values.remove();
				}
			}
		}
	}

	private static void setValueForAssosiation(BaseAbstractAttributeInterface assocation,
			Map<BaseAbstractAttributeInterface, Object> rootValueMap)
	{
		if (!((List<Map<BaseAbstractAttributeInterface, Object>>) rootValueMap.get(assocation))
				.isEmpty())
		{
			List<Map<BaseAbstractAttributeInterface, Object>> list = (List<Map<BaseAbstractAttributeInterface, Object>>) rootValueMap
					.get(assocation);
			Map<BaseAbstractAttributeInterface, Object> map = list.get(0);
			for (Entry<BaseAbstractAttributeInterface, Object> entryObject : map.entrySet())
			{
				rootValueMap.put(entryObject.getKey(), entryObject.getValue());
			}
		}
	}

	/**
	 *  Returned the attribute to value map for insertion.
	 * @param category Category for which attribute id map is required.
	 * @return id To Attribute Map
	 */
	public static Map<Long, BaseAbstractAttributeInterface> retriveIdToAttributeMap(
			CategoryInterface category)
	{
		CategoryEntityInterface categoryEntity = category.getRootCategoryElement();
		Map<Long, BaseAbstractAttributeInterface> categoryIdAttributeMap = new HashMap<Long, BaseAbstractAttributeInterface>();
		populateIdToAttrMapForCategory(categoryIdAttributeMap, categoryEntity);

		return categoryIdAttributeMap;

	}

	/**
	 * Generate flattered map for category.
	 * @param map id To Attribute Map.
	 * @param categoryEntity category for which flattered map is required.
	 */
	private static void populateIdToAttrMapForCategory(
			Map<Long, BaseAbstractAttributeInterface> map, CategoryEntityInterface categoryEntity)
	{
		Collection<CategoryAttributeInterface> categoryAttrCollection = categoryEntity
				.getAllCategoryAttributes();
		for (CategoryAttributeInterface catAttr : categoryAttrCollection)
		{
			if (!catAttr.getIsRelatedAttribute())
			{
				if (catAttr.getAbstractAttribute() instanceof Association)
				{
					AssociationInterface association = (AssociationInterface) catAttr
							.getAbstractAttribute();

					map.put(association.getId(), association);

					Collection<AbstractAttributeInterface> multiselectAttrColl = EntityManagerUtil
							.filterSystemAttributes(association.getTargetEntity()
									.getAllAbstractAttributes());
					Iterator<AbstractAttributeInterface> muultiSelectAttIterator = multiselectAttrColl
							.iterator();

					while (muultiSelectAttIterator.hasNext())
					{
						AttributeInterface attribute = (AttributeInterface) muultiSelectAttIterator
								.next();
						map.put(attribute.getId(), attribute);
					}
				}
				else
				{
					map.put(catAttr.getId(), catAttr);
				}
			}
		}
		processCatgeoryAssocitaionCollection(map, categoryEntity.getCategoryAssociationCollection());

	}

	/**
	 * Process category association to generate flattered map.
	 * @param map id to attribute map.
	 * @param catAssoCollection category association collection of category.
	 */
	private static void processCatgeoryAssocitaionCollection(
			Map<Long, BaseAbstractAttributeInterface> map,
			Collection<CategoryAssociationInterface> catAssoCollection)
	{
		for (CategoryAssociationInterface categoryAssociation : catAssoCollection)
		{

			CategoryEntityInterface targetCategoryEntity = categoryAssociation
					.getTargetCategoryEntity();
			if (targetCategoryEntity != null)
			{
				map.put(categoryAssociation.getId(), categoryAssociation);
				populateIdToAttrMapForCategory(map, targetCategoryEntity);
			}
		}
	}

	/**
	 * Returns attribute to value map for the given map and CategoryInterface.
	 * @param dataValue value map.
	 * @param categoryInterface CategoryInterface
	 * @return attribute to value map.
	 * @throws DynamicExtensionsSystemException thrown while error occurred while processing entity.
	 * @throws ParseException Thrown while parsing the date.
	 */
	public static Map<BaseAbstractAttributeInterface, Object> getAttributeToValueMap(
			final Map<String, Object> dataValue, CategoryInterface categoryInterface)
			throws DynamicExtensionsApplicationException, ParseException
	{

		Map<BaseAbstractAttributeInterface, Object> attributeToValueMap = // NOPMD by gaurav_sawant
		new HashMap<BaseAbstractAttributeInterface, Object>();
		Set<java.util.Map.Entry<String, Object>> dataValueEntrySet = dataValue.entrySet();
		String associationName = null;
		for (Map.Entry<String, Object> datavalueEntry : dataValueEntrySet)
		{
			if (datavalueEntry.getValue() instanceof List)
			{
				if (datavalueEntry.getKey().contains("->"))
				{
					associationName = datavalueEntry.getKey().replace("->", "");
				}
				else
				{
					associationName = datavalueEntry.getKey() + datavalueEntry.getKey();
				}
				if (categoryInterface.getRootCategoryElement().getName().equals(associationName))
				{
					ArrayList<HashMap<Object, Object>> hashMaps = ((ArrayList<HashMap<Object, Object>>) datavalueEntry
							.getValue());
					for (HashMap<Object, Object> hashMap : hashMaps)
					{
						processCategoryEntity(attributeToValueMap, categoryInterface
								.getRootCategoryElement(), hashMap);
					}
				}
				else
				{
					throw new DynamicExtensionsApplicationException(
							"Invalid Association Name For Root Category : "
									+ datavalueEntry.getKey().toString());
				}
			}
			else
			{
				processAttributes(attributeToValueMap, categoryInterface.getRootCategoryElement(),
						datavalueEntry.getKey(), datavalueEntry.getValue());
			}
		}
		return attributeToValueMap;
	}

	/**
	 * Method processes the category entity.
	 * @param attributeToValueMap data value map.
	 * @param entityInterface CategoryEntityInterface which needs to be processed.
	 * @param object value object.
	 * @throws DynamicExtensionsSystemException thrown while error occurred while processing entity.
	 */
	private static void processCategoryEntity(
			Map<BaseAbstractAttributeInterface, Object> attributeToValueMap,
			CategoryEntityInterface entityInterface, Object object)
			throws DynamicExtensionsApplicationException
	{
		HashMap<Object, Object> hashMap = (HashMap<Object, Object>) object;
		Set<java.util.Map.Entry<Object, Object>> dataValueEntrySet = hashMap.entrySet();
		for (Map.Entry<Object, Object> datavalueEntry : dataValueEntrySet)
		{
			if (datavalueEntry.getValue() instanceof List)
			{
				processAssociation(attributeToValueMap, entityInterface, datavalueEntry);
			}
			else
			{
				processAttributes(attributeToValueMap, entityInterface, datavalueEntry.getKey()
						.toString(), datavalueEntry.getValue());
			}
		}

	}

	/**
	 * Method processes the association.
	 * @param attributeToValueMap Data value map.
	 * @param entityInterface CategoryEntityInterface for which association is being processed.
	 * @param datavalueEntry name to value map for the association.
	 * @throws DynamicExtensionsSystemException thrown when association with
	 * specified name does not exist.
	 * @throws DynamicExtensionsApplicationException
	 */
	private static void processAssociation(
			Map<BaseAbstractAttributeInterface, Object> attributeToValueMap,
			CategoryEntityInterface entityInterface, Map.Entry<Object, Object> datavalueEntry)
			throws DynamicExtensionsApplicationException
	{
		String associationName = datavalueEntry.getKey().toString();
		CategoryAssociationInterface matchedMssociationInterface;
		Collection<CategoryAssociationInterface> associationInterfaces = entityInterface
				.getCategoryAssociationCollection();
		if (datavalueEntry.getKey().toString().contains("->"))
		{
			associationName = datavalueEntry.getKey().toString().replace("->", "");
		}
		matchedMssociationInterface = getAssociationInterface(associationInterfaces,
				associationName);
		if (matchedMssociationInterface == null)
		{
			CategoryAttributeInterface attributeInterface = entityInterface
					.getAttributeByName(associationName + " Category Attribute");
			if (attributeInterface == null || attributeInterface.getAbstractAttribute() == null
					|| !(attributeInterface.getAbstractAttribute() instanceof AssociationInterface))
			{
				throw new DynamicExtensionsApplicationException("Invalid Instance Name : "
						+ datavalueEntry.getKey().toString());
			}
			else
			{
				AbstractAttributeInterface abstractAttributeInterface = attributeInterface
						.getAbstractAttribute();
				if (abstractAttributeInterface instanceof AssociationInterface)
				{
					AssociationInterface associationInterface = (AssociationInterface) abstractAttributeInterface;
					List<Map<BaseAbstractAttributeInterface, Object>> list = new ArrayList<Map<BaseAbstractAttributeInterface, Object>>();
					ArrayList<HashMap<Object, Object>> hashMaps = ((ArrayList<HashMap<Object, Object>>) datavalueEntry
							.getValue());
					AttributeInterface multiselectAttr = (AttributeInterface) (EntityManagerUtil
							.filterSystemAttributes(associationInterface.getTargetEntity()
									.getAllAbstractAttributes())).toArray()[0];
					for (HashMap<Object, Object> hashMap : hashMaps)
					{
						Map<BaseAbstractAttributeInterface, Object> tempMap = new HashMap<BaseAbstractAttributeInterface, Object>();
						list.add(tempMap);
						tempMap.put(multiselectAttr, hashMap.get(associationName));
					}
					attributeToValueMap.put(attributeInterface, list);
				}
			}
		}
		else
		{
			List<Map<BaseAbstractAttributeInterface, Object>> list = new ArrayList<Map<BaseAbstractAttributeInterface, Object>>();
			ArrayList<HashMap<Object, Object>> hashMaps = ((ArrayList<HashMap<Object, Object>>) datavalueEntry
					.getValue());
			for (HashMap<Object, Object> hashMap : hashMaps)
			{
				Map<BaseAbstractAttributeInterface, Object> tempMap = new HashMap<BaseAbstractAttributeInterface, Object>();
				list.add(tempMap);
				processCategoryEntity(tempMap, matchedMssociationInterface
						.getTargetCategoryEntity(), hashMap);
			}
			attributeToValueMap.put(matchedMssociationInterface, list);
		}
	}

	/**
	 * Method add the attribute and value to the data value map.
	 * @param attributeToValueMap data value map.
	 * @param entityInterface CategoryEntityInterface for which attribute is being processed.
	 * @param key attribute name.
	 * @param value attribute value.
	 * @throws DynamicExtensionsSystemException thrown when attribute not found in interface.
	 */
	private static void processAttributes(
			Map<BaseAbstractAttributeInterface, Object> attributeToValueMap,
			CategoryEntityInterface entityInterface, String key, Object value)
			throws DynamicExtensionsApplicationException
	{
		CategoryAttributeInterface attributeInterface = entityInterface.getAttributeByName(key
				+ " Category Attribute");
		if (attributeInterface == null)
		{
			throw new DynamicExtensionsApplicationException("Invalid Attribute Name : " + key);
		}
		else
		{
			if (value instanceof Date)
			{
				AttributeTypeInformationInterface attributeTypeInformationInterface = ((AttributeInterface) attributeInterface
						.getAbstractAttribute()).getAttributeTypeInformation();
				String format = DynamicExtensionsUtility
						.getDateFormat(((DateAttributeTypeInformation) attributeTypeInformationInterface)
								.getFormat());
				DateFormat formatter = new SimpleDateFormat(format);
				String formatedDate = formatter.format(value);
				attributeToValueMap.put(attributeInterface, formatedDate);
			}
			else
			{
				attributeToValueMap.put(attributeInterface, value);
			}
		}
	}

	/**
	 * This method returns the association according to association name.
	 * @param associationInterfaces collection of association.
	 * @param associationName Name of the association.
	 * @return CategoryAssociationInterface.
	 */
	private static CategoryAssociationInterface getAssociationInterface(
			Collection<CategoryAssociationInterface> associationInterfaces, String associationName)
	{
		CategoryAssociationInterface matchedMssociationInterface = null;
		for (CategoryAssociationInterface associationInterface : associationInterfaces)
		{
			if (associationInterface.getTargetCategoryEntity().getName().equals(associationName))
			{
				matchedMssociationInterface = associationInterface;
				break;
			}
		}
		return matchedMssociationInterface;
	}

	/**
	 * This method can be used for update the state of the map.
	 * For every change on the UI, an ajax request is sent to server to update the value for the control
	 * in the map, so that state at the server side always remain in sync with the UI
	 *
	 * TODO Need to populate map for multiSelect attribute.
	 *  As of now, we are not populating and updating dataValueMap for multiSelect attribute
	 */
	public static void updateDataValueMap(Map<BaseAbstractAttributeInterface, Object> valueMap,
			Integer rowId, ControlInterface control, Object controlValue,
			ContainerInterface container) throws DynamicExtensionsApplicationException,
			DynamicExtensionsSystemException
	{
		for (Map.Entry<BaseAbstractAttributeInterface, Object> entry : valueMap.entrySet())
		{
			BaseAbstractAttributeInterface attribute = entry.getKey();
			if (attribute instanceof CategoryAssociationInterface
					&& isChildControl(control, attribute))
			{
				List<Map<BaseAbstractAttributeInterface, Object>> attributeValueMapList = (List<Map<BaseAbstractAttributeInterface, Object>>) entry
						.getValue();
				if (attributeValueMapList.size() < rowId + 1)
				{
					attributeValueMapList
							.add(new HashMap<BaseAbstractAttributeInterface, Object>());
				}
				updateValueMapForControl(control, controlValue, attributeValueMapList.get(rowId));
				return;

			}
		}
		updateValueMapForControl(control, controlValue, valueMap);

	}

	/**
	 * @param control
	 * @param controlValue
	 * @param attributeValueMapList
	 */
	private static void updateValueMapForControl(ControlInterface control, Object controlValue,
			Map<BaseAbstractAttributeInterface, Object> attributeValueMap)
	{
		if (controlValue instanceof String[] && ((String[]) controlValue).length > 0)
		{
			if (((CategoryAttributeInterface) control.getBaseAbstractAttribute())
					.getAbstractAttribute() instanceof AssociationInterface)
			{

				attributeValueMap.put(control.getBaseAbstractAttribute(), Arrays
						.asList(controlValue));
			}
			else
			{
				attributeValueMap.put(control.getBaseAbstractAttribute(),
						((String[]) controlValue)[0]);
			}

		}
	}

	/**
	 * @param control
	 * @param attribute
	 * @return
	 */
	public static boolean isChildControl(ControlInterface control,
			BaseAbstractAttributeInterface attribute)
	{
		CategoryAssociationInterface categoryAssociation = (CategoryAssociationInterface) attribute;
		ContainerInterface containerInter = (ContainerInterface) categoryAssociation
				.getTargetCategoryEntity().getContainerCollection().iterator().next();
		boolean flag = containerInter.getId().equals(control.getParentContainer().getId());
		if (!flag && !containerInter.getChildContainerCollection().isEmpty())
		{
			for (ContainerInterface containerInterface : containerInter
					.getChildContainerCollection())
			{
				if (containerInterface.getId().equals(control.getParentContainer().getId()))
				{
					flag = true;
				}
			}
		}
		return flag;
	}

	public static List<Map<String, String>> getValues(long formContextId, List<String> captionList)
			throws DynamicExtensionsSystemException, DynamicExtensionsApplicationException,
			CacheException
	{
		List<Map<String, String>> list = new ArrayList<Map<String, String>>();
		DEIntegration deItegration = new DEIntegration();
		Long dynamicRecEntryId = null;

		Long containerId;
		try
		{
			containerId = CategoryManager.getInstance().getContainerIdByFormContextId(
					formContextId, null);
			final ContainerInterface containerInterface = EntityCache.getInstance()
					.getContainerById(containerId);
			Long hookEntityId = XMIExporterUtility.getHookEntity(
					containerInterface.getAbstractEntity().getEntityGroup()).getId();

			populateFilteredMap(formContextId, captionList, list, deItegration, dynamicRecEntryId,
					containerId, containerInterface, hookEntityId);

		}
		catch (DAOException e)
		{

			throw new DynamicExtensionsSystemException("Problem loading data for formContextId"
					+ formContextId);
		}
		catch (JAXBException e)
		{

			throw new DynamicExtensionsSystemException("Problem loading data for formContextId"
					+ formContextId);
		}
		catch (SAXException e)
		{

			throw new DynamicExtensionsSystemException("Problem loading data for formContextId"
					+ formContextId);
		}

		return list;
	}

	private static void populateFilteredMap(long formContextId, List<String> captionList,
			List<Map<String, String>> list, DEIntegration deItegration, Long dynamicRecEntryId,
			Long containerId, final ContainerInterface containerInterface, Long hookEntityId)
			throws DynamicExtensionsSystemException, DynamicExtensionsApplicationException,
			CacheException
	{
		try
		{
			RecordEntryBizLogic recordEntryBizLogic = (RecordEntryBizLogic) BizLogicFactory
					.getBizLogic(RecordEntryBizLogic.class.getName());
			List<?> recordEntryIds = recordEntryBizLogic.getRecordEntryId(formContextId, null);

			for (Object recordEntryId : recordEntryIds)
			{
				ArrayList<?> object = (ArrayList<?>) recordEntryId;

				Long recordEntryIdValue = (Long.valueOf((String) object.get(0)));

				Collection<Long> map = deItegration.getDynamicRecordFromStaticId(recordEntryIdValue
						.toString(), containerId, hookEntityId.toString());
				if (!map.isEmpty())
				{
					Map<String, String> map2 = getDisplayValue(dynamicRecEntryId.toString(),
							containerInterface, captionList);
					map2.put(DEConstants.IDENTIFIER, dynamicRecEntryId.toString());
					list.add(map2);
				}
			}
		}
		catch (DAOException e)
		{
			throw new DynamicExtensionsSystemException("Problem loading data for formContextId"
					+ formContextId);
		}
		catch (JAXBException e)
		{

			throw new DynamicExtensionsSystemException("Problem loading data for formContextId"
					+ formContextId);
		}
		catch (SAXException e)
		{

			throw new DynamicExtensionsSystemException("Problem loading data for formContextId"
					+ formContextId);
		}
	}

	/**
	 * This method returns
	 * @param recordIdentifier
	 * @param containerInterface
	 * @param captionList 
	 * @return
	 * @throws DynamicExtensionsSystemException
	 * @throws DynamicExtensionsApplicationException
	 */
	public static Map<String, String> getDisplayValue(String recordIdentifier,
			ContainerInterface containerInterface, List<String> captionList)
			throws DynamicExtensionsSystemException, DynamicExtensionsApplicationException
	{
		CategoryEntityInterface categoryEntityInterface = (CategoryEntityInterface) containerInterface
				.getAbstractEntity();
		CategoryManagerInterface categoryManagerInterface = CategoryManager.getInstance();
		Long recordId = categoryManagerInterface.getRootCategoryEntityRecordIdByEntityRecordId(Long
				.valueOf(recordIdentifier), categoryEntityInterface.getTableProperties().getName());
		Map<BaseAbstractAttributeInterface, Object> recordMap = categoryManagerInterface
				.getRecordById(categoryEntityInterface, recordId);
		return populateDisplayValue(recordMap, captionList);
	}

	/**
	 * populates the map with respective attribute name and it's value. Map<AttributeName,value>
	 * @param recordMap
	 * @param captionList 
	 * @return
	 */

	@SuppressWarnings("unchecked")
	private static Map<String, String> populateDisplayValue(
			Map<BaseAbstractAttributeInterface, Object> recordMap, List<String> captionList)
	{
		Map<String, String> showInGridValues = new HashMap<String, String>();

		for (BaseAbstractAttributeInterface baseAbstractAttributeInterface : recordMap.keySet())
		{

			String controlCaption = CategoryHelper
					.getControlCaption(baseAbstractAttributeInterface);

			if (baseAbstractAttributeInterface instanceof AssociationMetadataInterface)
			{
				CategoryAssociationInterface categoryAssociationInterface = (CategoryAssociationInterface) baseAbstractAttributeInterface;
				if (categoryAssociationInterface.getTargetCategoryEntity().getNumberOfEntries() == 1)
				{
					for (Map<BaseAbstractAttributeInterface, Object> record : (List<Map<BaseAbstractAttributeInterface, Object>>) recordMap
							.get(baseAbstractAttributeInterface))
					{
						showInGridValues.putAll(populateDisplayValue(record, captionList));
					}
				}
			}
			else if (captionList.contains(controlCaption))
			{

				addValueAsString(recordMap, showInGridValues, baseAbstractAttributeInterface,
						controlCaption);
			}
		}
		return showInGridValues;
	}

	private static void addValueAsString(Map<BaseAbstractAttributeInterface, Object> recordMap,
			Map<String, String> showInGridValues,
			BaseAbstractAttributeInterface baseAbstractAttributeInterface, String controlCaption)
	{
		if (recordMap.get(baseAbstractAttributeInterface) instanceof ArrayList<?>)
		{
			Map<Object, Object> map = ((Map<Object, Object>) ((ArrayList) recordMap
					.get(baseAbstractAttributeInterface)).get(0));
			Object key = map.keySet().iterator().next();
			showInGridValues.put(controlCaption, map.get(key).toString());
		}
		else
		{
			showInGridValues.put(controlCaption, recordMap.get(baseAbstractAttributeInterface)
					.toString());
		}
	}

}
