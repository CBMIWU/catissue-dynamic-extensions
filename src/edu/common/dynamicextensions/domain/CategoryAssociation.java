
package edu.common.dynamicextensions.domain;

import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;

import edu.common.dynamicextensions.domaininterface.AssociationMetadataInterface;
import edu.common.dynamicextensions.domaininterface.CategoryAssociationInterface;
import edu.common.dynamicextensions.domaininterface.CategoryEntityInterface;
import edu.common.dynamicextensions.domaininterface.validationrules.RuleInterface;
import edu.common.dynamicextensions.util.global.Constants.AssociationType;

/**
 * @author mandar_shidhore
 * @hibernate.joined-subclass table="DYEXTN_CATEGORY_ASSOCIATION"
 * @hibernate.joined-subclass-key column="IDENTIFIER"
 */
public class CategoryAssociation extends BaseAbstractAttribute implements CategoryAssociationInterface, AssociationMetadataInterface
{

	/**
	 * 
	 */
	private static final long serialVersionUID = 12345678L;

	/**
	 * 
	 */
	protected CategoryEntity categoryEntity;

	/**
	 * 
	 */
	protected Collection<CategoryEntity> targetCategoryEntityCollection = new HashSet<CategoryEntity>();

	/**
	 * @hibernate.many-to-one column="CATEGORY_ENTIY_ID" class="edu.common.dynamicextensions.domain.CategoryEntity" constrained="true" 
	 */
	public CategoryEntityInterface getCategoryEntity()
	{
		return (CategoryEntityInterface)categoryEntity;
	}

	/**
	 * 
	 * @param categoryEntity
	 */
	public void setCategoryEntity(CategoryEntityInterface categoryEntityInterface)
	{
		this.categoryEntity = (CategoryEntity)categoryEntityInterface;
	}

	/**
	 * @hibernate.set name="targetCategoryEntityCollection" table="DYEXTN_CATEGORY_ENTITY"
	 * cascade="all-delete-orphan" inverse="false" lazy="false"
	 * @hibernate.collection-key column="CATEGORY_ASSOCIATION_ID"
	 * @hibernate.cache usage="read-write"
	 * @hibernate.collection-one-to-many class="edu.common.dynamicextensions.domain.CategoryEntity"
	 * @return the categoryAssociationCollection
	 */
	private Collection<CategoryEntity> getTargetCategoryEntityCollection()
	{
		return targetCategoryEntityCollection;
	}

	private void setTargetCategoryEntityCollection(Collection<CategoryEntity> targetCategoryEntityCollection)
	{
		this.targetCategoryEntityCollection = targetCategoryEntityCollection;
	}

	/**
	 * @return
	 */
	public CategoryEntityInterface getTargetCategoryEntity()
	{
		CategoryEntity targetCategoryEntity = null;
		if (targetCategoryEntityCollection != null && !targetCategoryEntityCollection.isEmpty())
		{
			Iterator<CategoryEntity> targetCategoryEntityCollectionIterator = targetCategoryEntityCollection.iterator();
			targetCategoryEntity = targetCategoryEntityCollectionIterator.next();
		}
		return targetCategoryEntity;
	}

	/**
	 * @param
	 */
	public void setTargetCategoryEntity(CategoryEntityInterface targetCategoryEntityInterface)
	{
		if (targetCategoryEntityCollection == null)
		{
			targetCategoryEntityCollection = new HashSet<CategoryEntity>();
		}
		else
		{
			targetCategoryEntityCollection.clear();
		}
		targetCategoryEntityCollection.add((CategoryEntity)targetCategoryEntityInterface);
	}

	/**
	 * 
	 */
	public Collection<RuleInterface> getRuleCollection()
	{
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see edu.common.dynamicextensions.domaininterface.AssociationMetadataInterface#getAssociationType()
	 */
	public AssociationType getAssociationType()
	{
		return AssociationType.CONTAINTMENT;
	}

}
