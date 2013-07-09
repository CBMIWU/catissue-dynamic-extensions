
package edu.wustl.dynamicextensions.formdesigner.resource;

import java.io.InputStream;
import java.io.StringWriter;
import java.io.Writer;
import java.util.Date;
import java.util.HashMap;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;

import org.json.JSONException;
import org.json.JSONObject;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sun.jersey.core.header.FormDataContentDisposition;
import com.sun.jersey.multipart.FormDataParam;
import edu.common.dynamicextensions.exception.DynamicExtensionsSystemException;
import edu.common.dynamicextensions.util.DynamicExtensionsUtility;
import edu.wustl.dao.HibernateDAO;
import edu.wustl.dao.JDBCDAO;
import edu.wustl.dao.exception.DAOException;
import edu.wustl.dynamicextensions.formdesigner.mapper.Properties;
import edu.wustl.dynamicextensions.formdesigner.resource.facade.ContainerFacade;
import edu.wustl.dynamicextensions.formdesigner.utility.CSDConstants;
import edu.wustl.dynamicextensions.formdesigner.utility.Utility;

@Path("/form")
public class Form
{

	private static final String CONTAINER_SESSION_ATTR = "sessionContainer";

	private JDBCDAO jdbcDao;

	private HibernateDAO hibernateDao;

	/**
	 * Create a new Form
	 * @param formJson
	 * @param request
	 * @param response
	 * @return
	 * @throws JSONException
	 */
	@SuppressWarnings("unchecked")
	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public String createForm(String formJson, final @Context HttpServletRequest request,
			@Context HttpServletResponse response) throws JSONException
	{

		JSONObject formJSON = new JSONObject();
		String jsonResponseString = "";

		try
		{
			Properties formProps = new Properties(new ObjectMapper().readValue(formJson,
					HashMap.class));
			ContainerFacade containerFacade = ContainerFacade.createContainer(formProps);
			request.getSession().removeAttribute(CONTAINER_SESSION_ATTR);
			request.getSession().setAttribute(CONTAINER_SESSION_ATTR, containerFacade);
			String save = formProps.getString("save");

			if (save.equalsIgnoreCase("yes"))
			{
				intializeDao();
				containerFacade.persistContainer();
				commitDao();
			}

			formProps = containerFacade.getProperties();
			formProps.setProperty(CSDConstants.STATUS, CSDConstants.STATUS_SAVED);
			Writer strWriter = new StringWriter();
			new ObjectMapper().writeValue(strWriter, formProps.getAllProperties());
			jsonResponseString = strWriter.toString();

		}
		catch (Exception ex)
		{
			ex.printStackTrace();
			formJSON.put("status", "error");
			jsonResponseString = formJSON.toString();
		}
		finally
		{
			closeDao();
		}

		return jsonResponseString;

	}

	/**
	 * Retrieve a Form
	 * @param id
	 * @param request
	 * @param response
	 * @return
	 * @throws JSONException
	 */
	@Path("/{id}")
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public String getForm(@PathParam("id") String id, final @Context HttpServletRequest request,
			@Context HttpServletResponse response) throws JSONException
	{
		System.out.println(id);
		try
		{
			intializeDao();
			ContainerFacade container = ContainerFacade.loadContainer(Long.valueOf(id));

			request.getSession().setAttribute(CONTAINER_SESSION_ATTR, container);
			Properties containerProps = container.getProperties();
			containerProps.setProperty(CSDConstants.STATUS, CSDConstants.STATUS_SAVED);
			Writer strWriter = new StringWriter();
			new ObjectMapper().writeValue(strWriter, containerProps.getAllProperties());
			return strWriter.toString();
		}
		catch (Exception ex)
		{
			ex.printStackTrace();
			return "{'status' : 'error'}";
		}
		finally
		{
			closeDao();
		}

	}

	/**
	 * Renders preview
	 * @param request
	 * @param response
	 * @return
	 * @throws JSONException
	 */
	@Path("/preview")
	@GET
	@Produces(MediaType.TEXT_HTML)
	public String getPreview(final @Context HttpServletRequest request,
			@Context HttpServletResponse response) throws JSONException
	{
		try
		{
			ContainerFacade containerFacade = (ContainerFacade) request.getSession().getAttribute(
					CONTAINER_SESSION_ATTR);
			return containerFacade.getHTML();
		}
		catch (Exception ex)
		{
			ex.printStackTrace();
			return "Error";
		}
		finally
		{
			//request.getSession().removeAttribute(CONTAINER_SESSION_ATTR);
		}
	}

	/**
	 * Edit a Form
	 * @param formJson
	 * @param request
	 * @param response
	 * @return
	 */
	@SuppressWarnings("unchecked")
	@PUT
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public String editForm(String formJson, final @Context HttpServletRequest request,
			@Context HttpServletResponse response)
	{
		try
		{
			Properties formProps = new Properties(new ObjectMapper().readValue(formJson,
					HashMap.class));
			String save = formProps.getString("save");
			((ContainerFacade) request.getSession().getAttribute(CONTAINER_SESSION_ATTR))
					.updateContainer(formProps);
			if (save.equalsIgnoreCase("yes"))
			{
				intializeDao();
				((ContainerFacade) request.getSession().getAttribute(CONTAINER_SESSION_ATTR))
						.persistContainer();
				commitDao();
			}
			Writer strWriter = new StringWriter();
			new ObjectMapper().writeValue(strWriter, ((ContainerFacade) request.getSession()
					.getAttribute(CONTAINER_SESSION_ATTR)).getProperties().getAllProperties());
			return strWriter.toString();
		}
		catch (Exception ex)
		{
			ex.printStackTrace();
			return "{'status' : 'error'}";
		}
		finally
		{
			closeDao();
		}

	}

	/**
	 * Delete a control in a form
	 * @param name
	 * @param request
	 * @param response
	 * @return
	 */
	@Path("/control/{name}")
	@Produces(MediaType.APPLICATION_JSON)
	@DELETE
	public String deleteControl(@PathParam("name") String name,
			final @Context HttpServletRequest request, @Context HttpServletResponse response)
	{
		((ContainerFacade) request.getSession().getAttribute(CONTAINER_SESSION_ATTR))
				.deleteControl(name);
		return "{'status' : 'deleted'}";
	}

	/**
	 * upload permissible values
	 * @param uploadedInputStream
	 * @param fileDetail
	 * @return
	 */
	@POST
	@Path("/permissibleValues")
	@Consumes(MediaType.MULTIPART_FORM_DATA)
	@Produces(MediaType.APPLICATION_JSON)
	public String uploadFile(@FormDataParam("file") InputStream uploadedInputStream,
			@FormDataParam("file") FormDataContentDisposition fileDetail)
	{
		String output = "{\"status\" : \"error\"}";
		try
		{
			// get temp location programatically.
			if (fileDetail.getFileName() != null)
			{
				String uploadedFileLocation = "/tmp/" + new Date().getTime()
						+ fileDetail.getFileName();
				Utility.saveStreamToFileInTemp(uploadedInputStream, uploadedFileLocation);
				output = "{\"status\": \"saved\", \"file\" : \"" + uploadedFileLocation + "\"}";
			}
		}
		catch (Exception ex)
		{
			return output;
		}

		return output;

	}

	/**
	 * @throws DAOException
	 */
	private void commitDao() throws DAOException
	{
		try
		{
			jdbcDao.commit();
			hibernateDao.commit();
		}
		finally
		{
			closeDao();
		}
	}

	/**
	 * 
	 */
	private void closeDao()
	{
		DynamicExtensionsUtility.closeDAO(jdbcDao, false);
		DynamicExtensionsUtility.closeDAO(hibernateDao, false);
	}

	/**
	 * @throws DynamicExtensionsSystemException
	 */
	@SuppressWarnings("deprecation")
	private void intializeDao() throws DynamicExtensionsSystemException
	{
		jdbcDao = DynamicExtensionsUtility.getJDBCDAO();
		hibernateDao = DynamicExtensionsUtility.getHibernateDAO();
	}
}
