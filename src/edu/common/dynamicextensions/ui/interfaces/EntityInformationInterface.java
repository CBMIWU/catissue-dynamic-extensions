package edu.common.dynamicextensions.ui.interfaces;
/**
 * 
 * @author deepti_shelar
 *
 */
public interface EntityInformationInterface {
	/**
	 * @return Returns the name.
	 */
	public String getFormName();
	/**
	 * @param name The name to set.
	 */
	public void setFormName(String formName); 
	/**
	 * @return Returns the description.
	 */
	public String getDescription(); 
	/**
	 * @param description The description to set.
	 */
	public void setDescription(String description); 
	/**
	 * @param returns createAs .
	 */
	public String getCreateAs();
	/**
	 * @param createAs The createAs to set.
	 */
	public void setCreateAs(String createAs) ;
}
