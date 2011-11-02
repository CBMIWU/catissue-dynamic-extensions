package edu.common.dynamicextensions.bizlogic;

import java.util.ArrayList;
import java.util.List;

import edu.common.dynamicextensions.domaininterface.userinterface.ContainerInterface;
import edu.common.dynamicextensions.exception.DynamicExtensionsSystemException;
import edu.common.dynamicextensions.skiplogic.SkipLogic;
import edu.common.dynamicextensions.util.DynamicExtensionsUtility;
import edu.wustl.dao.HibernateDAO;
import edu.wustl.dao.exception.DAOException;
import edu.wustl.dao.query.generator.ColumnValueBean;


/**
 * @author kunal_kamble
 * This class has all the necessary methods related to the container.
 */
public class ContainerBizlogic
{

	/**
	 * This method returns the skip logic object for a given container object
	 * @param container
	 * @return
	 * @throws DynamicExtensionsSystemException
	 */
	public SkipLogic getSkipLogic(ContainerInterface container) throws DynamicExtensionsSystemException
	{
		HibernateDAO hibernateDAO = DynamicExtensionsUtility.getHibernateDAO();
		List<ColumnValueBean> valBeanList = new ArrayList<ColumnValueBean>();
		ColumnValueBean colValueBean = new ColumnValueBean("containerIdentifier",
				container.getId());
		valBeanList.add(colValueBean);

		List objects = null;
		try
		{
			objects = hibernateDAO.retrieve(SkipLogic.class.getName(), colValueBean);
			DynamicExtensionsUtility.closeDAO(hibernateDAO);
		}
		catch (DAOException e)
		{
			throw new DynamicExtensionsSystemException("Error while retiring skip logic for container ",e);
		}
		SkipLogic skipLogic = null;
		if (objects != null && !objects.isEmpty())
		{
			skipLogic = (SkipLogic) objects.get(0);
		}
		return skipLogic;
	}


}
