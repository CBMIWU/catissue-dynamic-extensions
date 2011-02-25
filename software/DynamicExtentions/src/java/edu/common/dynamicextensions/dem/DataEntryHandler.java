
package edu.common.dynamicextensions.dem;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import edu.common.dynamicextensions.domaininterface.AbstractAttributeInterface;
import edu.common.dynamicextensions.domaininterface.EntityInterface;
import edu.common.dynamicextensions.entitymanager.AbstractBaseMetadataManager;
import edu.common.dynamicextensions.entitymanager.FileQueryBean;
import edu.common.dynamicextensions.exception.DynamicExtensionsApplicationException;
import edu.common.dynamicextensions.exception.DynamicExtensionsSystemException;
import edu.common.dynamicextensions.util.global.DEConstants;
import edu.wustl.dao.exception.DAOException;

public class DataEntryHandler extends AbstractHandler
{

	private final DyanamicObjectProcessor dyanamicObjectProcessor;

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

		try
		{
			/*			*//**
					* 1. initialize the parameter
					*/
			/*
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
			Map<AbstractAttributeInterface, Object> dataValue = (Map<AbstractAttributeInterface, Object>) paramaterObjectMap
					.get(DATA_VALUE_MAP);

			Object object = dyanamicObjectProcessor.createObject(entity, dataValue);
			insertObject(object);
			List<FileQueryBean> queryListForFile = dyanamicObjectProcessor
					.getQueryListForFileAttributes(dataValue, entity, object);
			dyanamicObjectProcessor.executeQuery(queryListForFile,
					(List<FileQueryBean>) paramaterObjectMap.get(FILE_RECORD_QUERY_LIST));
			Map<String, Object> map = new HashMap<String, Object>();
			map.put(DEConstants.IDENTIFIER, AbstractBaseMetadataManager.getObjectId(object));
			map.put(FILE_RECORD_QUERY_LIST, paramaterObjectMap.get(FILE_RECORD_QUERY_LIST));

			writeObjectToResopnce(map, res);

		}
		catch (DynamicExtensionsApplicationException e)
		{
			e.printStackTrace();
		}
		catch (DynamicExtensionsSystemException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		catch (DAOException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		catch (NoSuchMethodException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		catch (IllegalAccessException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		catch (InvocationTargetException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	protected void writeObjectToResopnce(Object object, HttpServletResponse res)
			throws DynamicExtensionsApplicationException
	{
		ObjectOutputStream objectOutputStream = null;
		try
		{
			objectOutputStream = new ObjectOutputStream(res.getOutputStream());
			objectOutputStream.writeObject(object);
		}
		catch (IOException e)
		{
			throw new DynamicExtensionsApplicationException(
					"Error in writing object to the responce", e);
		}
		finally
		{
			try
			{
				objectOutputStream.flush();
				objectOutputStream.close();
			}
			catch (IOException e)
			{
				throw new DynamicExtensionsApplicationException(
						"Error in writing object to the responce", e);
			}

		}
	}
}
