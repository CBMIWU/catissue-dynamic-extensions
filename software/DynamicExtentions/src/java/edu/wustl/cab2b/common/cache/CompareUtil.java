
package edu.wustl.cab2b.common.cache;

import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import edu.common.dynamicextensions.domaininterface.AttributeInterface;
import edu.common.dynamicextensions.domaininterface.EntityInterface;
import edu.common.dynamicextensions.domaininterface.PermissibleValueInterface;
import edu.common.dynamicextensions.domaininterface.SemanticPropertyInterface;
import edu.wustl.cab2b.common.beans.MatchedClassEntry;
import edu.wustl.cab2b.common.beans.MatchedClassEntry.MatchCause;
import edu.wustl.cab2b.common.util.Utility;

/**
 * @author Chandrakant Talele
 * @author Rahul Ner
 */
public class CompareUtil
{

	private static final Map<MatchCause, MetadataSearchComparator> causeVsComparator;

	static
	{
		StringComparator strComparator = new StringComparator();
		SemanticPropertyCollectionComparator semPropCmp = new SemanticPropertyCollectionComparator();

		causeVsComparator = new HashMap<MatchCause, MetadataSearchComparator>();
		causeVsComparator.put(MatchCause.EntityName, strComparator);
		causeVsComparator.put(MatchCause.EntityDescription, strComparator);
		causeVsComparator.put(MatchCause.EntitySemanticProperty, semPropCmp);
		causeVsComparator.put(MatchCause.AttributeName, strComparator);
		causeVsComparator.put(MatchCause.AttributeDescription, strComparator);
		causeVsComparator.put(MatchCause.AttributeSemanticProperty, semPropCmp);
		causeVsComparator.put(MatchCause.PermissibleValueName, strComparator);
		causeVsComparator.put(MatchCause.PermissibleSemanticProperty, semPropCmp);
	}

	/**
	 * Compares given pattern entity with the cachedEntity in following order
	 *   1. Name
	 *   2. Description
	 *   3. SemanticProperty
	 * If any of the above field matches then, {@link MatchedClassEntry} is created for that entity.
	 * The position where the match was found is set into the  {@link MatchedClassEntry}. It is used to
	 * sort the entire search result
	 * @param cachedEntity
	 * @param patternEntity
	 * @return The MatchedClassEntry
	 */
	public static MatchedClassEntry compare(EntityInterface cachedEntity,
			EntityInterface patternEntity)
	{
		LinkedHashMap<MatchCause, OrderedPair> map = new LinkedHashMap<MatchCause, OrderedPair>();
		map.put(MatchCause.EntityName, new OrderedPair(patternEntity.getName(),
				getOnlyClassName(cachedEntity)));
		map.put(MatchCause.EntityDescription, new OrderedPair(patternEntity.getDescription(),
				cachedEntity.getDescription()));
		map.put(MatchCause.EntitySemanticProperty, new OrderedPair(patternEntity
				.getSemanticPropertyCollection(), cachedEntity.getSemanticPropertyCollection()));
		return findMatch(map, cachedEntity);
	}

	/**
	 * Compares given pattern attribute with the cached attribute in following order
	 *   1. Name
	 *   2. Description
	 *   3. SemanticProperty
	 * If any of the above field matches then, {@link MatchedClassEntry} is created for the entity of that attribute.
	 * The position where the match was found is set into the  {@link MatchedClassEntry}. It is used to
	 * sort the entire search result
	 *
	 * @param cachedAttr cached Attribute
	 * @param patternAttr pattern Attribute
	 * @return  The MatchedClassEntry
	 */
	public static MatchedClassEntry compare(AttributeInterface cachedAttr,
			AttributeInterface patternAttr)
	{
		LinkedHashMap<MatchCause, OrderedPair> map = new LinkedHashMap<MatchCause, OrderedPair>();
		map.put(MatchCause.AttributeName, new OrderedPair(patternAttr.getName(), cachedAttr
				.getName()));
		map.put(MatchCause.AttributeDescription, new OrderedPair(patternAttr.getDescription(),
				cachedAttr.getDescription()));
		map.put(MatchCause.AttributeSemanticProperty, new OrderedPair(patternAttr
				.getSemanticPropertyCollection(), cachedAttr.getSemanticPropertyCollection()));
		return findMatch(map, cachedAttr.getEntity());
	}

	/**
	 * Compares given pattern PermissibleValue with the cached PermissibleValue in following order
	 *   1. Name
	 *   2. SemanticProperty
	 * If any of the above field matches then, {@link MatchedClassEntry} is created for the entity of that PermissibleValue.
	 * The position where the match was found is set into the  {@link MatchedClassEntry}. It is used to
	 * sort the entire search result
	 * @param cachedPv cachedPermissibleValue
	 * @param patternPv patternPermissibleValue
	 * @param cachedEntity
	 * @return The MatchedClassEntry
	 */
	public static MatchedClassEntry compare(PermissibleValueInterface cachedPv,
			PermissibleValueInterface patternPv, EntityInterface cachedEntity)
	{
		LinkedHashMap<MatchCause, OrderedPair> map = new LinkedHashMap<MatchCause, OrderedPair>();
		map.put(MatchCause.PermissibleValueName, new OrderedPair(getString(patternPv),
				getString(cachedPv)));
		map.put(MatchCause.PermissibleSemanticProperty, new OrderedPair(patternPv
				.getSemanticPropertyCollection(), cachedPv.getSemanticPropertyCollection()));
		return findMatch(map, cachedEntity);
	}

	private static MatchedClassEntry findMatch(LinkedHashMap<MatchCause, OrderedPair> map,
			EntityInterface cachedEntity)
	{
		for (Map.Entry<MatchCause, OrderedPair> entry : map.entrySet())
		{
			MatchCause cause = entry.getKey();
			OrderedPair pair = entry.getValue();
			int index = causeVsComparator.get(cause).compare(pair.object1, pair.object2);
			if (index != -1)
			{
				MatchedClassEntry matchedClassEntry = new MatchedClassEntry(cachedEntity);
				matchedClassEntry.setPositionOf(cause, index);
				return matchedClassEntry;
			}
		}
		return null;
	}

	private static String getOnlyClassName(EntityInterface entity)
	{
		String className = entity.getName();
		if (className == null)
		{
			return null;
		}
		return className.substring(className.lastIndexOf('.') + 1, className.length());
	}

	private static String getString(PermissibleValueInterface permissibleValue)
	{
		if (permissibleValue.getValueAsObject() == null)
		{
			return null;
		}
		return permissibleValue.getValueAsObject().toString();
	}

	private interface MetadataSearchComparator
	{

		int compare(Object object1, Object object2);
	}

	private static class OrderedPair
	{

		private final Object object1;

		private final Object object2;

		public OrderedPair(Object object1, Object object2)
		{
			this.object1 = object1;
			this.object2 = object2;
		}

	}

	private static class StringComparator implements MetadataSearchComparator
	{

		public int compare(Object object1, Object object2)
		{
			if (object1 == null || object2 == null)
			{
				return -1;
			}
			return Utility.indexOfRegEx((String) object1, (String) object2);
		}
	}

	static class SemanticPropertyCollectionComparator implements MetadataSearchComparator
	{

		@SuppressWarnings("unchecked")
		public int compare(Object object1, Object object2)
		{
			if (object1 == null || object2 == null)
			{
				return -1;
			}
			return compare((Collection<SemanticPropertyInterface>) object1,
					(Collection<SemanticPropertyInterface>) object2);
		}

		/**
		 * Compares given pattern SemanticProperties with the cached SemanticProperties.
		 * returns the position of first matched concept code.
		 * @param cachedProperties
		 * @param patternProperties
		 * @return
		 */
		public static int compare(Collection<SemanticPropertyInterface> patternProperties,
				Collection<SemanticPropertyInterface> cachedProperties)
		{
			int index = -1;

			if (patternProperties != null && cachedProperties != null)
			{

				for (SemanticPropertyInterface patternSemanticProperty : patternProperties)
				{
					for (SemanticPropertyInterface cachedSemanticProperty : cachedProperties)
					{
						index = compare(patternSemanticProperty, cachedSemanticProperty);
						if (index != -1)
						{
							break;
						}
					}
				}
			}
			return index;
		}

		/**
		 * Compares given pattern SemanticProperty with the Cached SemanticProperty for the concept code
		 * If it matched, return the position where the match was found.
		 * @param cachedSemanticProperty
		 * @param patternSemanticProperty
		 * @return
		 */
		public static int compare(SemanticPropertyInterface patternSemanticProperty,
				SemanticPropertyInterface cachedSemanticProperty)
		{
			int index = -1;
			if (cachedSemanticProperty.getConceptCode() != null
					&& patternSemanticProperty.getConceptCode() != null)
			{
				// TODO this null check is because caTissue
				// has some permissible values without
				// concept codes. This will never be the case with models from cDSR
				String patternConceptCode = patternSemanticProperty.getConceptCode();

				index = Utility.indexOfRegEx(patternConceptCode, cachedSemanticProperty
						.getConceptCode());
			}
			return index;
		}
	}
}