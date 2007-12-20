package edu.common.dynamicextensions.domaininterface;

import edu.common.dynamicextensions.domain.CategoryEntity;

public interface CategoryInterface extends AbstractMetadataInterface {
    
    public CategoryEntity getRootCategoryElement();
    
    public void setRootCategoryElement(CategoryEntity rootCategoryElement);

}
