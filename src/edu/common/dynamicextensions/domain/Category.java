package edu.common.dynamicextensions.domain;

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
     * rootCategoryElement.
     */
    protected CategoryEntity rootCategoryElement;

    /**
     *
     *
     */
    public Category()
    {
    	super();
    }

    /**
     * @hibernate.many-to-one column="ROOT_CATEGORY_ELEMENT" cascade="all-delete-orphan"
     * @return the rootCategoryElement.
     */
    public CategoryEntity getRootCategoryElement() {
        return rootCategoryElement;
    }

    /**
     * @param rootCategoryElement the rootCategoryElement to set
     */
    public void setRootCategoryElement(CategoryEntity rootCategoryElement) {
        this.rootCategoryElement = rootCategoryElement;
    }

}