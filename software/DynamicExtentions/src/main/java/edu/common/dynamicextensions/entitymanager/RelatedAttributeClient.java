package edu.common.dynamicextensions.entitymanager;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.URL;
import java.net.URLConnection;

import edu.common.dynamicextensions.client.AbstractClient;
import edu.common.dynamicextensions.exception.DynamicExtensionsSystemException;
import edu.common.dynamicextensions.utility.HTTPSConnection;

public class RelatedAttributeClient extends AbstractClient {

	@Override
	protected void initializeResources(String[] args)
			throws DynamicExtensionsSystemException, IOException {
		serverUrl = new URL(serverUrl+"RelatedAttributeHandler");

	}

	@Override
	protected void validate(String[] args)
			throws DynamicExtensionsSystemException {
		// TODO Auto-generated method stub

	}

	@Override
	protected void performAction(HTTPSConnection httpsConnection,
			URLConnection servletConnection)
			throws DynamicExtensionsSystemException, IOException {
		ObjectOutputStream outputToServlet = new ObjectOutputStream(servletConnection
				.getOutputStream());
		outputToServlet.writeObject(paramaterObjectMap);
		outputToServlet.flush();

		outputToServlet.close();
	}
	protected void processResponse(URLConnection servletConnection)
	throws DynamicExtensionsSystemException, IOException
	{

	}
}
