/**
 *
 */

package edu.common.dynamicextensions.domain;

import java.util.Collection;

import edu.common.dynamicextensions.domaininterface.StaticCategoryInterface;

/**
* @author suhas_khot
* @hibernate.joined-subclass table="DYEXTN_STATIC_CATEGORY"
* @hibernate.joined-subclass-key column="IDENTIFIER"
*/
public class StaticCategory extends AbstractCategory implements StaticCategoryInterface
{

	/**
	 * Serial Version UID
	 */
	private static final long serialVersionUID = 1L;

	/** The form url. */
	private String formURL;

	/** The grid view column list. */
	private Collection<GridViewColumn> gridViewColumnList;
	
	/** The data query. */
	private String dataQuery;

	/**
	 * This method returns the form URL.
	 * @hibernate.property name="formURL" type="string" column="FORM_URL" length="800"
	 * @return the formURL
	 */
	public String getFormURL()
	{
		return formURL;
	}

	/**
	 * @param formURL the formURL to set
	 */
	public void setFormURL(String formURL)
	{
		this.formURL = formURL;
	}

	
	/* (non-Javadoc)
	 * @see edu.common.dynamicextensions.domaininterface.StaticCategoryInterface#getGridViewColumnList()
	 */
	public Collection<GridViewColumn> getGridViewColumnList()
	{
		return gridViewColumnList;
	}

	
	/* (non-Javadoc)
	 * @see edu.common.dynamicextensions.domaininterface.StaticCategoryInterface#setGridViewColumnList(java.util.Collection)
	 */
	public void setGridViewColumnList(Collection<GridViewColumn> gridViewColumnList)
	{
		this.gridViewColumnList = gridViewColumnList;
	}

	
	/**
	 * Gets the data query.
	 * 
	 * @return the data query
	 */
	public String getDataQuery()
	{
		return dataQuery;
	}

	
	/**
	 * Sets the data query.
	 * 
	 * @param dataQuery the new data query
	 */
	public void setDataQuery(String dataQuery)
	{
		this.dataQuery = dataQuery;
	}
}