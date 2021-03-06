
package edu.common.dynamicextensions.client;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

import edu.common.dynamicextensions.exception.DynamicExtensionsSystemException;
import edu.common.dynamicextensions.ui.webui.util.WebUIManagerConstants;
import edu.common.dynamicextensions.util.DirOperationsUtility;
import edu.common.dynamicextensions.util.ZipUtility;
import edu.common.dynamicextensions.utility.HTTPSConnection;

/**
 * This class is used to create category by using ant task, which will not require to
 * restart the Jboss server to view those categories.
 * This class will upload all the necessary data to the jboss server & then will
 * trigger the category creation on server side itself.
 * @author pavan_kalantri
 *
 */
public class CategoryClient extends AbstractClient
{

	/**
	 * Prefix for template file path.
	 */
	private static final String MAPPING_PREFIX = "Mapping_";
	/**
	 * Prefix for XML file path.
	 */
	private static final String TEMPLATE_PREFIX = "Template_";
	/**
	 * Specifies weather the Category should be saved in metadata only.
	 */
	private static boolean isMetadataOnly = false;

	/**
	 * This method will internally call the category creation on server.
	 * @param args The list of arguments this method expects is as follows.
	 * args[0]=Zip file Path, args[1] =file path which contains categories to be created,
	 * args[2] =Application Url on which category creation should be triggered
	 * args[3] = is persist metadata only.
	 */
	public static void main(String[] args)
	{
		CategoryClient refresher = new CategoryClient();
		refresher.execute(args);
	}

	/**
	 * It will validate all the necessary parameters are provided & are valid.
	 * If all are valid it will also intialize all the instance variables also
	 * @param args the arguments which are to be validated & initialize instance varibles from
	 * these arguments
	 * @throws IOException exception
	 * @throws Exception exception
	 */
	protected void initializeResources(String[] args) throws DynamicExtensionsSystemException,
			IOException
	{
		try
		{
			copyBOTemplateFiles(args);
			//validate size of the folder is less than 500MB
			DirOperationsUtility.validateFolderSizeForUpload(args[0], 500000000L);
			this.zipFile = ZipUtility.zipFolder(args[0], "tempCategoryDir.zip");
			String url = HTTPSConnection.getCorrectedApplicationURL(args[1])
					+ WebUIManagerConstants.CREATE_CATEGORY_ACTION+"?";
			String catFilename = "";
			if (args.length > 2 && !"".equals(args[2].trim()))
			{
				catFilename = getCategoryFilenameString(args[2]);
			}
			String importBulkTemplate = "";
			if (args.length == 7)
			{
				importBulkTemplate = args[args.length - 1].toLowerCase();
			}
			persistMetadataOnly(args);
			this.serverUrl = new URL(url + CategoryCreatorConstants.METADATA_ONLY + "="
					+ isMetadataOnly + "&" + CategoryCreatorConstants.CATEGORY_NAMES_FILE + "="
					+ catFilename + "&" + "importBulkTemplate=" + importBulkTemplate);
		}
		catch (MalformedURLException e)
		{
			throw new DynamicExtensionsSystemException("Please provide correct ApplicationURL", e);
		}
	}

	/**
	 * @param args
	 * @throws IOException
	 */
	private void copyBOTemplateFiles(String[] args) throws IOException
	{
		if (args.length > 4)
		{
			DirOperationsUtility.getInstance().copyFileToDestDir(args[4], args[0], TEMPLATE_PREFIX);
			DirOperationsUtility.getInstance().copyFileToDestDir(args[5], args[0], MAPPING_PREFIX);
		}
	}

	/**
	 * @param args
	 */
	private void persistMetadataOnly(String[] args)
	{
		if (args.length > 3 && args[3].equalsIgnoreCase("true"))
		{
			isMetadataOnly = true;
		}
	}

	/**
	 * It will validate weather the correct number of arguments are passed or not & then throw
	 * exception accordingly.
	 * @param args arguments
	 * @throws DynamicExtensionsSystemException exception
	 */
	protected void validate(String[] args) throws DynamicExtensionsSystemException
	{
		if (args.length == 0)
		{
			throw new DynamicExtensionsSystemException("Please specify category files folder path.");
		}
		if (args.length < 2)
		{
			throw new DynamicExtensionsSystemException("Please specify the AppplicationURL.");
		}
		boolean isPathUnvailable = args[0] != null && args[0].trim().length() == 0;
		if (isPathUnvailable)
		{
			throw new DynamicExtensionsSystemException("Please specify category files folder path.");
		}
		boolean isUrlUnavailable = args[1] != null && args[1].trim().length() == 0;
		if (isUrlUnavailable)
		{
			throw new DynamicExtensionsSystemException("Please specify the  AppplicationURL.");
		}
	}

	/**
	 * This is the overridden version of trust manager which will trust all the https connections
	 * @author pavan_kalantri
	 *
	 */
	public static class DerivedTrustManager
			implements
				javax.net.ssl.TrustManager,
				javax.net.ssl.X509TrustManager
	{

		/* (non-Javadoc)
		 * @see javax.net.ssl.X509TrustManager#getAcceptedIssuers()
		 */
		public java.security.cert.X509Certificate[] getAcceptedIssuers()
		{
			return new java.security.cert.X509Certificate[0];
		}

		/**
		 * This method will always return true so that any url is considered to be trusted.
		 * & no certification validation is done.
		 * @param certs certificates.
		 * @return true if certificate is trusted
		 */
		public boolean isServerTrusted(java.security.cert.X509Certificate[] certs)
		{
			return true;
		}

		/**
		 * This method will always return true so that any url is considered to be trusted.
		 * & no certification validation is done.
		 * @param certs certificates.
		 * @return true if certificate is trusted
		 */
		public boolean isClientTrusted(java.security.cert.X509Certificate[] certs)
		{
			return true;
		}

		/* (non-Javadoc)
		 * @see javax.net.ssl.X509TrustManager#checkServerTrusted(java.security.cert.X509Certificate[], java.lang.String)
		 */
		public void checkServerTrusted(java.security.cert.X509Certificate[] certs, String authType)
				throws java.security.cert.CertificateException
		{
			// TODO Auto-generated catch block
		}

		/* (non-Javadoc)
		 * @see javax.net.ssl.X509TrustManager#checkClientTrusted(java.security.cert.X509Certificate[], java.lang.String)
		 */
		public void checkClientTrusted(java.security.cert.X509Certificate[] certs, String authType)
				throws java.security.cert.CertificateException
		{
			// TODO Auto-generated catch block
		}
	}
}
