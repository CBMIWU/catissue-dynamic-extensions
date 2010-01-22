
package edu.common.dynamicextensions.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import edu.common.dynamicextensions.domaininterface.BaseAbstractAttributeInterface;
import edu.common.dynamicextensions.domaininterface.userinterface.AbstractContainmentControlInterface;
import edu.common.dynamicextensions.domaininterface.userinterface.ContainerInterface;
import edu.common.dynamicextensions.domaininterface.userinterface.ControlInterface;

/**
 * @author kunal_kamble
 * This class has utility methods on the data value map that are used for manipulating the map.
 */
public class DataValueMapUtility
{

	private static final String FOR_DATA_LOADING = "forDataLoading";
	private static final String FOR_DATA_STORING = "forDataStoring";

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
				AbstractContainmentControlInterface abstractContainmentControl = (AbstractContainmentControlInterface) control;
				if (rootValueMap.get(abstractContainmentControl.getBaseAbstractAttribute()) != null
						&& rootValueMap.get(abstractContainmentControl.getBaseAbstractAttribute()) instanceof List)
				{
					List<Map<BaseAbstractAttributeInterface, Object>> list = (List<Map<BaseAbstractAttributeInterface, Object>>) rootValueMap
							.get(abstractContainmentControl.getBaseAbstractAttribute());
					for (Map<BaseAbstractAttributeInterface, Object> map : list)
					{
						modifyDataValueMap(map, abstractContainmentControl.getContainer(), purpose);
					}
				}
				else
				{
					if (rootValueMap.get(abstractContainmentControl.getBaseAbstractAttribute()) != null)
					{
						modifyDataValueMap(
								(Map<BaseAbstractAttributeInterface, Object>) rootValueMap
										.get(abstractContainmentControl.getBaseAbstractAttribute()),
								abstractContainmentControl.getContainer(), purpose);
					}
				}
			}
		}

		if (rootContainerInterface.getChildContainerCollection() != null
				&& !rootContainerInterface.getChildContainerCollection().isEmpty())
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
	}

	/**
	 * This method update the value map generated by the manager classes. This transformation of map is required
	 * for the display of controls of the different container that are under the same display label
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
		for (Iterator iterator = set.iterator(); iterator.hasNext();)
		{
			BaseAbstractAttributeInterface baseAbstractAttrIntf = (BaseAbstractAttributeInterface) iterator
					.next();
			if(baseAbstractAttrIntf.getName().equalsIgnoreCase(containerInterface.getAbstractEntity().getName()))
			{
				map.put(baseAbstractAttrIntf, rootValueMap.get(baseAbstractAttrIntf));
				iterator.remove();
			}

		}
		rootValueMap.put(assocation, list);
	}

	/**
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
				if (!((List<Map<BaseAbstractAttributeInterface, Object>>) rootValueMap
						.get(assocation)).isEmpty())
				{
					List<Map<BaseAbstractAttributeInterface, Object>> list = (List<Map<BaseAbstractAttributeInterface, Object>>) rootValueMap
							.get(assocation);
					Map<BaseAbstractAttributeInterface, Object> map = (Map<BaseAbstractAttributeInterface, Object>) list
							.get(0);
					for (BaseAbstractAttributeInterface abstractAttribute : map.keySet())
					{
						rootValueMap.put(abstractAttribute, map.get(abstractAttribute));
					}
				}
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
			rootValueMap.remove(assocation);
		}

	}

	/**
	 * @param sourceContainer
	 * @param targetContainer
	 * @return
	 */
	/*
		private static CategoryAssociationInterface getAssocation(ContainerInterface sourceContainer,
				ContainerInterface targetContainer)
		{
			CategoryEntityInterface sourceCategory = (CategoryEntityInterface) sourceContainer
					.getAbstractEntity();
			CategoryAssociationInterface association = null;
			for (CategoryAssociationInterface associationInterface : sourceCategory
					.getCategoryAssociationCollection())
			{
				if (associationInterface.getTargetCategoryEntity().getName().equals(
						targetContainer.getAbstractEntity().getName()))
				{
					association = associationInterface;
				}
			}
			return association;

		}*/
}
