
package edu.common.dynamicextensions.interfaceactions;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.Collection;
import java.util.Iterator;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import edu.common.dynamicextensions.domaininterface.userinterface.ContainerInterface;
import edu.common.dynamicextensions.entitymanager.EntityManager;
import edu.common.dynamicextensions.entitymanager.EntityManagerInterface;
import edu.common.dynamicextensions.exception.DynamicExtensionsApplicationException;
import edu.common.dynamicextensions.exception.DynamicExtensionsSystemException;
import edu.common.dynamicextensions.ui.webui.util.WebUIManagerConstants;
import edu.wustl.common.util.logger.Logger;

public class DynamicExtensionsInterfaceAction extends HttpServlet implements WebUIManagerConstants
{

	/**
	 * Do get method calls do post method
	 * @param req HttpServletRequest
	 * @param res HttpServletResponse
	 * @throws ServletException servlet exception
	 * @throws IOException io exception
	 */
	public void doGet(HttpServletRequest req, HttpServletResponse res) throws ServletException,
			IOException
	{
		doPost(req, res);
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

		BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(req
				.getInputStream()));
		Object json = null;
		JSONArray entityInterfaceJSONArray = new JSONArray();
		try
		{
			json = bufferedReader.readLine();
			bufferedReader.close();
		}
		catch (IOException e)
		{
			Logger.out.error(e.getMessage());
			generateOutput(res, entityInterfaceJSONArray);
		}

		//check which operation needs to be performed
		//JSONObject requestObject = null;
		String operation = null;
		operation = getOperation(json, operation);

		//depending upon the operation execute the appropriate steps
		if (GET_ALL_CONTAINERS.equalsIgnoreCase(operation))
		{

			EntityManagerInterface entityManager = EntityManager.getInstance();
			Collection containerCollection = null;
			try
			{
				containerCollection = entityManager.getAllContainers();
			}
			catch (DynamicExtensionsSystemException e)
			{
				Logger.out.error(e.getMessage());
				generateOutput(res, entityInterfaceJSONArray);

			}
			catch (DynamicExtensionsApplicationException e)
			{
				Logger.out.error(e.getMessage());
				generateOutput(res, entityInterfaceJSONArray);
			}

			if (containerCollection != null)
			{
				Iterator entityIterator = containerCollection.iterator();
				ContainerInterface containerInterface;

				while (entityIterator.hasNext())
				{
					JSONObject containerInterfaceJSONObject = new JSONObject();
					containerInterface = (ContainerInterface) entityIterator.next();
					try
					{
						containerInterfaceJSONObject.put(CONTAINER_NAME, containerInterface
								.getCaption());
					}
					catch (JSONException e)
					{
						Logger.out.error(e.getMessage());
					}
					try
					{
						containerInterfaceJSONObject.put(CONTAINER_IDENTIFIER, containerInterface
								.getId());
					}
					catch (JSONException e)
					{
						Logger.out.error(e.getMessage());
					}

					entityInterfaceJSONArray.put(containerInterfaceJSONObject);
				}
			}

			generateOutput(res, entityInterfaceJSONArray);
		}

	}

	/**
	 * Gets the operation.
	 *
	 * @param json the json
	 * @param operation the operation
	 * @return the operation
	 */
	private String getOperation(Object json, String operation)
	{
		JSONObject requestObject;
		try
		{
			if (json != null)
			{
				requestObject = new JSONObject(json.toString());
				operation = requestObject.getString("operation");
			}
		}
		catch (JSONException e1)
		{
			Logger.out.error(e1.getMessage());
		}
		return operation;
	}

	/**
	 *
	 * @param res HttpServletResponse
	 * @param entityInterfaceJSONArray JSONArray
	 * @throws IOException IOException
	 */
	private void generateOutput(HttpServletResponse res, JSONArray entityInterfaceJSONArray)
			throws IOException
	{
		res.setContentType("text/javascript");
		PrintWriter out = res.getWriter();
		out.write(entityInterfaceJSONArray.toString());
	}
}
