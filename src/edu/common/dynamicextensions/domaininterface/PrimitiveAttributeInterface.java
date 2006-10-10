package edu.common.dynamicextensions.domaininterface;



/**
 * @author geetika_bangard
 */
public interface PrimitiveAttributeInterface extends AttributeInterface {

   
	/**
	 * @return Returns the isCollection.
	 */
	public Boolean getIsCollection() ;
	/**
	 * @param isCollection The isCollection to set.
	 */
	public void setIsCollection(Boolean isCollection);
	/**
	 * @return Returns the isIdentified.
	 */
	public Boolean getIsIdentified();
	/**
	 * @param isIdentified The isIdentified to set.
	 */
	public void setIsIdentified(Boolean isIdentified);
	/**
	 * @return Returns the isPrimaryKey.
	 */
	public Boolean getIsPrimaryKey();
	/**
	 * @param isPrimaryKey The isPrimaryKey to set.
	 */
	public void setIsPrimaryKey(Boolean isPrimaryKey);
    
     /**
      * @return
     */
    public DataElementInterface getDataElement();
     
    /**
     * @param sourceEntity
     */
    public void setDataElement(DataElementInterface dataElementInterface);
  
}
