
package edu.common.dynamicextensions.bizlogic;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.JAXBException;

import net.sf.ehcache.CacheException;

import org.xml.sax.SAXException;

import edu.common.dynamicextensions.exception.DynamicExtensionsApplicationException;
import edu.common.dynamicextensions.exception.DynamicExtensionsSystemException;
import edu.common.dynamicextensions.ui.util.Constants;
import edu.common.dynamicextensions.util.SQLQueryManager;
import edu.common.dynamicextensions.util.global.DEConstants;
import edu.wustl.common.beans.SessionDataBean;
import edu.wustl.common.bizlogic.DefaultBizLogic;
import edu.wustl.dao.exception.DAOException;
import edu.wustl.dao.query.generator.ColumnValueBean;

public class RecordEntryBizLogic extends DefaultBizLogic
{

	public List<?> getRecordEntryId(Long formContextId, SessionDataBean sessionDataBean)
			throws DynamicExtensionsSystemException, DAOException, JAXBException, SAXException,
			DynamicExtensionsApplicationException, CacheException
	{

		List<ColumnValueBean> columnValueBeans = new ArrayList<ColumnValueBean>();
		columnValueBeans.add(new ColumnValueBean(formContextId));
		columnValueBeans.add(new ColumnValueBean(Constants.DISABLED));
		List<?> recordEntryIds = SQLQueryManager.executeQuery(
				DEConstants.RECORD_ID_FROM_FORM_CONTEXT_ID, columnValueBeans, sessionDataBean);

		return recordEntryIds;
	}

	public List<?> getRecordEntryId(Long formContextId, SessionDataBean sessionDataBean,
			Long hookObjectRecordId) throws DynamicExtensionsSystemException, DAOException,
			JAXBException, SAXException
	{

		List<ColumnValueBean> columnValueBeans = new ArrayList<ColumnValueBean>();
		columnValueBeans.add(new ColumnValueBean(hookObjectRecordId));
		columnValueBeans.add(new ColumnValueBean(formContextId));
		columnValueBeans.add(new ColumnValueBean(Constants.DISABLED));
		List<?> recordEntryIds = SQLQueryManager.executeQuery(
				DEConstants.RECORD_ID_FOR_PARTICIPANT_FROM_FORM_CONTEXT_ID, columnValueBeans,
				sessionDataBean);

		return recordEntryIds;
	}

}
