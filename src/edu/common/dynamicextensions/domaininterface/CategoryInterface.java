package edu.common.dynamicextensions.domaininterface;

import java.util.Collection;


public interface CategoryInterface extends AbstractMetadataInterface {
    /**
     *
     * @return
     */
    CategoryEntityInterface getRootCategoryElement();
    /**
     *
     * @param rootCategoryElement
     */
    void setRootCategoryElement(CategoryEntityInterface rootCategoryElement);
    
    /**
     * @param categoryEntityName
     * @return
     */
    CategoryEntityInterface getCategoryEntityByName(String categoryEntityName);
    
    /**
     * @return
     */
    Collection<CategoryEntityInterface> getRelatedAttributeCategoryEntityCollection();
    
    /**
     * @param relatedAttributeCategoryEntityCollection
     */
    void setRelatedAttributeCategoryEntityCollection(Collection<CategoryEntityInterface> relatedAttributeCategoryEntityCollection);
    
    /**
     * @param categoryEntity
     */
    void addRelatedAttributeCategoryEntity(CategoryEntityInterface categoryEntity);
    
    void removeRelatedAttributeCategoryEntity(CategoryEntityInterface categoryEntity);
}
