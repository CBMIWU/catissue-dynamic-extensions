
package edu.common.dynamicextensions.dem;

import java.io.IOException;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import edu.common.dynamicextensions.domaininterface.AbstractAttributeInterface;
import edu.common.dynamicextensions.domaininterface.EntityInterface;
import edu.common.dynamicextensions.exception.DynamicExtensionsApplicationException;
import edu.common.dynamicextensions.exception.DynamicExtensionsSystemException;
import edu.wustl.dao.exception.DAOException;

public class DataEntryHandler extends AbstractHandler
{

	private DyanamicObjectProcessor dyanamicObjectProcessor;

	public DataEntryHandler() throws DAOException
	{
		 dyanamicObjectProcessor = new DyanamicObjectProcessor();
	}

	/**
	 * Do post method of the servlet
	 * @param req HttpServletRequest
	 * @param res HttpServletResponse
	 * @throws ServletException servlet exception
	 * @throws IOException io exception
	 */
	public void doPost(HttpServletRequest req, HttpServletResponse res) throws ServletException,
		 	IOException
	{

		try {
/*			*//**
			 * 1. initialize the parameter
			 *//*
			ObjectInputStream inputFromServlet = null;
			try {
				inputFromServlet = new ObjectInputStream(req.getInputStream());
				Object object = null;
				while ((object = inputFromServlet.readObject()) != null) {
					if (object instanceof AbstractEntity) {
						entity = (EntityInterface) object;
					}
					if (object instanceof Map) {
						dataValue = (Map<AbstractAttributeInterface, Object>) object;
					}

				}
			} catch (ClassNotFoundException e) {
				throw new DynamicExtensionsApplicationException(
						"Error in reading objects from request", e);
			} catch (EOFException e) {
				System.out.println("End of file.");
			} catch (IOException e) {
				throw new DynamicExtensionsApplicationException(
						"Error in reading objects from request", e);
			} finally {
				try {
					inputFromServlet.close();
				} catch (IOException e) {
					throw new DynamicExtensionsApplicationException(
							"Error in reading objects from request", e);
				}
			}
*/
			initAuditManager();
			initializeParamaterObjectMap(req);

			EntityInterface entity = (EntityInterface) paramaterObjectMap.get(ENTITY);
			Map<AbstractAttributeInterface, Object> dataValue = (Map<AbstractAttributeInterface, Object>) paramaterObjectMap.get(DATA_VALUE_MAP);

			Object object = dyanamicObjectProcessor.createObject(entity, dataValue);
			insertObject(object);
			writeObjectToResopnce(object,res);

		} catch (DynamicExtensionsApplicationException e) {
			e.printStackTrace();
		} catch (DynamicExtensionsSystemException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}



}
