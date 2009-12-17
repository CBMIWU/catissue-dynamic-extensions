
package edu.common.dynamicextensions.domain;

import java.util.Collection;
import java.util.HashSet;

import edu.common.dynamicextensions.domaininterface.CategoryEntityInterface;
import edu.common.dynamicextensions.domaininterface.CategoryInterface;

/**
 *
 * @author mandar_shidhore
 * @hibernate.joined-subclass table="DYEXTN_CATEGORY"
 * @hibernate.joined-subclass-key column="IDENTIFIER"
 */
public class Category extends AbstractMetadata implements CategoryInterface
{

	/**
	 * Serial Version UID
	 */
	private static final long serialVersionUID = 4234527890L;

	/**
	 * User id
	 */
	protected long userId;
	
	/**
	 * rootCategoryElement.
	 */
	protected CategoryEntity rootCategoryElement;

	/**
	 *
	 */
	protected Collection<CategoryEntityInterface> relatedAttributeCategoryEntityCollection = new HashSet<CategoryEntityInterface>();

	/**
     * 
     * @return
     * @hibernate.property name="userId" type="long" length="19" column="USER_ID" 
     */
	public long getUserId() {
        return userId;
    }

    /**
     * 
     * @param userId
     */
	public void setUserId(long userId) {
        this.userId = userId;
    }
	
	/**
	 * @hibernate.many-to-one column="ROOT_CATEGORY_ELEMENT" cascade="all" class="edu.common.dynamicextensions.domain.CategoryEntity"
	 * @return the rootCategoryElement.
	 */
	public CategoryEntityInterface getRootCategoryElement()
	{
		return rootCategoryElement;
	}

	/**
	 * @param rootCategoryElement the rootCategoryElement to set
	 */
	public void setRootCategoryElement(CategoryEntityInterface rootCategoryElement)
	{
		this.rootCategoryElement = (CategoryEntity) rootCategoryElement;
	}

	/**
	 * @param categoryEntityName
	 * @return
	 */
	public CategoryEntityInterface getCategoryEntityByName(String categoryEntityName)
	{
		return getCategoryEntity(getRootCategoryElement(), categoryEntityName);
	}

	/**
	 * @param categoryEntity
	 * @param categoryEntityName
	 * @return
	 */
	private CategoryEntityInterface getCategoryEntity(CategoryEntityInterface categoryEntity,
			String categoryEntityName)
	{
		CategoryEntityInterface searchedCategoryEntity = null;
		if (categoryEntity == null)
		{
			return searchedCategoryEntity;
		}
		if (categoryEntity.getName().equals(categoryEntityName))
		{
			return categoryEntity;
		}
		for (CategoryEntityInterface categoryEntityInterface : categoryEntity.getChildCategories())
		{
			if (categoryEntityInterface.getName().equals(categoryEntityName))
			{
				searchedCategoryEntity = categoryEntityInterface;
				break;
			}

			if (categoryEntityInterface.getChildCategories().size() > 0)
			{
				searchedCategoryEntity = getCategoryEntity(categoryEntityInterface,
						categoryEntityName);
				if (searchedCategoryEntity != null)
				{
					break;
				}

			}

		}

		return searchedCategoryEntity;
	}

	/**
	 * @hibernate.set name="relAttrCategoryEntity" table="DYEXTN_CATEGORY_ENTITY"
	 * cascade="all" inverse="false" lazy="false"
	 * @hibernate.collection-key column="REL_ATTR_CAT_ENTITY_ID"
	 * @hibernate.cache usage="read-write"
	 * @hibernate.collection-one-to-many class="edu.common.dynamicextensions.domain.CategoryEntity"
	 * @return the relatedAttributeCategoryEntityCollection
	 */
	public Collection<CategoryEntityInterface> getRelatedAttributeCategoryEntityCollection()
	{
		return relatedAttributeCategoryEntityCollection;
	}

	/**
	 * @param relatedAttributeCategoryEntityCollection the relatedAttributeCategoryEntityCollection to set
	 */
	public void setRelatedAttributeCategoryEntityCollection(
			Collection<CategoryEntityInterface> relatedAttributeCategoryEntityCollection)
	{
		this.relatedAttributeCategoryEntityCollection = relatedAttributeCategoryEntityCollection;
	}

	/**
	 * @param categoryEntity
	 */
	public void addRelatedAttributeCategoryEntity(CategoryEntityInterface categoryEntity)
	{
		relatedAttributeCategoryEntityCollection.add(categoryEntity);
	}

	/**
	 * @param categoryEntity
	 */
	public void removeRelatedAttributeCategoryEntity(CategoryEntityInterface categoryEntity)
	{
		relatedAttributeCategoryEntityCollection.remove(categoryEntity);
	}

}