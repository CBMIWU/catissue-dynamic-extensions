/**
 *
 */

package edu.common.dynamicextensions.ui.webui.action;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.owasp.stinger.Stinger;
import org.owasp.stinger.rules.RuleSet;

import edu.common.dynamicextensions.exception.DynamicExtensionsApplicationException;
import edu.common.dynamicextensions.exception.DynamicExtensionsSystemException;
import edu.common.dynamicextensions.util.CategoryGenerationUtil;
import edu.common.dynamicextensions.util.DirOperationsUtility;
import edu.common.dynamicextensions.util.DownloadUtility;
import edu.common.dynamicextensions.util.parser.DynamicallyImportPermissibleValues;
import edu.common.dynamicextensions.util.parser.ImportPermissibleValues;
import edu.wustl.cab2b.server.cache.EntityCache;
import edu.wustl.common.util.global.CommonServiceLocator;
import edu.wustl.common.util.logger.Logger;
import edu.wustl.common.util.logger.LoggerConfig;

/**
 * @author gaurav_mehta
 *
 */
public class ImportPVAction extends BaseDynamicExtensionsAction
{

	static
	{
		LoggerConfig.configureLogger(System.getProperty("user.dir"));
	}
	private static final Logger LOGGER = Logger.getCommonLogger(ImportPVAction.class);

	public ActionForward execute(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
	{
		String pvDir = CommonServiceLocator.getInstance().getAppHome() + File.separator + "pvDir"
				+ EntityCache.getInstance().getNextIdForCategoryFileGeneration();
		try
		{
			DownloadUtility.downloadZipFile(request, pvDir, "pv.zip");
			LOGGER.info("Artifacts Downloaded");

			List<String> listOfFiles = getPVFileNames(request, pvDir);

			for (String file : listOfFiles)
			{
				importPVs(file, pvDir);
			}
			sendResponse(response, "PV Uploaded Successfully");
			LOGGER.info("Permissible Values uploaded successfully");
		}
		catch (Exception e)
		{
			LOGGER.info("Exception occured while importing Permissible Values", e);
			sendResponse(response, e);
		}
		finally
		{
			DirOperationsUtility.getInstance().deleteDirectory(new File(pvDir));
		}
		return null;
	}

	/**
	 * @param relativeFileName
	 * @param file
	 * @param pvDir
	 * @throws DynamicExtensionsSystemException
	 * @throws FileNotFoundException
	 * @throws DynamicExtensionsApplicationException
	 * @throws ParseException
	 */
	private void importPVs(String file, String pvDir) throws DynamicExtensionsSystemException,
			FileNotFoundException, DynamicExtensionsApplicationException, ParseException
	{
		ServletContext servletContext = servlet.getServletContext();
		String config = servletContext.getRealPath("WEB-INF") + "/stinger.xml";
		Stinger stinger = new Stinger(new RuleSet(config, servletContext), servletContext);
		ImportPermissibleValues importPVs = new ImportPermissibleValues(file, pvDir, stinger);
		importPVs.importValues();
	}

	/**
	 * This method will send the given responseObject to the caller of this servlet.
	 * @param response HttpServletResponse through which the responseObject should be send.
	 * @param responseObject object to be send.
	 */
	public void sendResponse(HttpServletResponse response, Object responseObject)
	{
		try
		{
			ObjectOutputStream outputToClient = new ObjectOutputStream(response.getOutputStream());
			outputToClient.writeObject(responseObject);
			outputToClient.flush();
			outputToClient.close();
		}
		catch (IOException e)
		{
			LOGGER.info("Exception occured while sending response back to java application");
		}
	}

	/**
	 * This method will retrieve the category names parameter provided in request
	 * and will return the List of names.
	 * @param request HttpServletRequest from which to retrieve the category names.
	 * @param tempDirName
	 * @return List of category names to be created.
	 * @throws DynamicExtensionsSystemException exception
	 */
	private List<String> getPVFileNames(HttpServletRequest request, String tempDirName)
			throws DynamicExtensionsSystemException
	{
		List<String> fileNameList;
		String fileName = request.getParameter(DynamicallyImportPermissibleValues.pvFileName);

		if (fileName == null || "".equals(fileName.trim()))
		{
			fileNameList = CategoryGenerationUtil.getPVFileListInDirectory(new File(tempDirName),
					"");
			//throw new DynamicExtensionsSystemException("Please provide names of the Category Files To be created");
		}
		else
		{
			File pvFile = new File(tempDirName + File.separator + fileName);
			if (!pvFile.exists())
			{
				throw new DynamicExtensionsSystemException("PV file " + fileName
						+ " does not exists");
			}
			fileNameList = new ArrayList<String>();
			fileNameList.add(fileName);
		}
		return fileNameList;
	}
}
