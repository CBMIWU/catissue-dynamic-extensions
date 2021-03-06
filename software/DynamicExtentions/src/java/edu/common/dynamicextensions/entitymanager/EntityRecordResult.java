
package edu.common.dynamicextensions.entitymanager;

import java.io.Serializable;
import java.util.List;

/**
 * This is an interface to operate on the entity record result object.
 * @author rahul_ner
 * @author vishvesh_mulay
 *
 */
public class EntityRecordResult implements EntityRecordResultInterface, Serializable
{

	/**
	 * Serial Version Unique Identifier
	 */
	private static final long serialVersionUID = -552600540977483821L;

	/**
	 * List of entity records
	 */
	protected List<EntityRecordInterface> entityRecordList;

	/**
	 * Metadata for the result
	 */
	protected EntityRecordMetadata entityRecordMetadata;

	/**
	 * @see edu.common.dynamicextensions.entitymanager.EntityRecordResultInterface#getEntityRecordList()
	 */
	public List<EntityRecordInterface> getEntityRecordList()
	{
		return entityRecordList;
	}

	/**
	 * @see edu.common.dynamicextensions.entitymanager.EntityRecordResultInterface#setEntityRecordList(java.util.List)
	 */
	public void setEntityRecordList(List<EntityRecordInterface> entityRecordList)
	{
		this.entityRecordList = entityRecordList;
	}

	/**
	 * @see edu.common.dynamicextensions.entitymanager.EntityRecordResultInterface#getEntityRecordMetadata()
	 */
	public EntityRecordMetadata getEntityRecordMetadata()
	{
		return entityRecordMetadata;
	}

	/**
	 * @see edu.common.dynamicextensions.entitymanager.EntityRecordResultInterface#setEntityRecordMetadata(edu.common.dynamicextensions.entitymanager.EntityRecordMetadata)
	 */
	public void setEntityRecordMetadata(EntityRecordMetadata entityRecordMetadata)
	{
		this.entityRecordMetadata = entityRecordMetadata;
	}

	/**
	 * @see java.lang.Object#toString()
	 */
	public String toString()
	{
		StringBuffer str = new StringBuffer();
		str.append(entityRecordMetadata.getAttributeList());
		str.append(entityRecordList);

		return str.toString();
	}
}
