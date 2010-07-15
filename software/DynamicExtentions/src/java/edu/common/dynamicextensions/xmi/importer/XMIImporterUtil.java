
package edu.common.dynamicextensions.xmi.importer;

import java.util.List;

import edu.common.dynamicextensions.domain.DomainObjectFactory;
import edu.common.dynamicextensions.domain.SemanticAnnotatableInterface;
import edu.common.dynamicextensions.domaininterface.EntityGroupInterface;
import edu.common.dynamicextensions.domaininterface.SemanticPropertyInterface;
import edu.common.dynamicextensions.exception.DynamicExtensionsSystemException;
import edu.common.dynamicextensions.util.DynamicExtensionsUtility;
import edu.common.dynamicextensions.util.global.DEConstants;
import edu.common.dynamicextensions.xmi.XMIConfiguration;
import edu.wustl.common.util.logger.Logger;
import edu.wustl.dao.JDBCDAO;
import edu.wustl.dao.exception.DAOException;
import gov.nih.nci.cagrid.metadata.common.SemanticMetadata;

public class XMIImporterUtil
{

	private static final Logger LOGGER = Logger.getCommonLogger(XMIImporterUtil.class);
	/**
	* Stores the SemanticMetadata to the owner which can be class or attribute
	* @param owner EntityInterface OR AttributeInterface
	* @param semanticMetadataArr Semantic Metadata array to set.
	*/
	public static void setSemanticMetadata(SemanticAnnotatableInterface owner,
			SemanticMetadata[] semanticMetadataArr)
	{
		if (semanticMetadataArr == null)
		{
			return;
		}
		DomainObjectFactory deFactory = DomainObjectFactory.getInstance();
		for (int i = 0; i < semanticMetadataArr.length; i++)
		{
			SemanticPropertyInterface semanticProp = deFactory.createSemanticProperty();
			semanticProp.setSequenceNumber(i);
			semanticProp.setConceptCode(semanticMetadataArr[i].getConceptCode());
			semanticProp.setTerm(semanticMetadataArr[i].getConceptName());
			owner.addSemanticProperty(semanticProp);
		}
	}

	/**
	 * @return container list
	 */
	/*
	public static List<ArrayList> getProcessedContainerList()
	{
	List<ArrayList> processedContainerListEntityList = new ArrayList<ArrayList>();
	ArrayList<ContainerInterface> processedContainerList = new ArrayList<ContainerInterface>();
	ArrayList<EntityInterface> processedEntityList = new ArrayList<EntityInterface>();
	processedContainerListEntityList.add(0, processedContainerList);
	processedContainerListEntityList.add(1, processedEntityList);
	return processedContainerListEntityList;
	}*/

	/**
	 * @param entityGroup
	 * @param xmiConfigurationObject
	 * @throws DynamicExtensionsSystemException
	 */
	public static void populateEntityForConstraintProperties(EntityGroupInterface entityGroup,
			XMIConfiguration xmiConfigurationObject) throws DynamicExtensionsSystemException
	{
		DynamicExtensionsUtility.populateEntityForConstraintProperties(entityGroup,
				xmiConfigurationObject);
	}

	/**
	 * This method is added for only using the Query which has inner query in it
	 * for inserting the data in it.
	 * @param queryList queries to be executed
	 * @throws DynamicExtensionsSystemException
	 */
	public static void executeDML(List<String> queryList) throws DynamicExtensionsSystemException
	{
		JDBCDAO jdbcDao = null;


		try
		{
			jdbcDao = DynamicExtensionsUtility.getJDBCDAO();
			for (String query : queryList)
			{
				jdbcDao.executeUpdate(query);
			}
		}

		catch (DAOException e)
		{
			throw new DynamicExtensionsSystemException("Error while retrieving the data", e);
		}
		finally
		{
			try
			{
				jdbcDao.commit();
				DynamicExtensionsUtility.closeDAO(jdbcDao);
			}
			catch (DAOException e)
			{
				throw new DynamicExtensionsSystemException("Error while retrieving the data", e);
			}
		}
	}
	/**
	 * It will create the logg statements for timing required for Hooking with the static entity.
	 * @param assoWithHEstartTime start time of the hooking
	 */
	public static void generateLogForHooking(long assoWithHEstartTime)
	{
		long assoWithHEendTime = System.currentTimeMillis();
		long assoWithHEtotalTime = assoWithHEendTime - assoWithHEstartTime;
		LOGGER.info(" ");
		LOGGER.info(" ");
		LOGGER.info("######################################################################");
		LOGGER.info("  IMPORT_XMI --> TASK : ASSOCIATE WITH HOOK ENTITY & CREATE DE TABLES");
		LOGGER.info("  ------------------------------------------------------------------");
		LOGGER.info(DEConstants.TIME_TAKEN + ((assoWithHEtotalTime / 1000) / 60) + " minutes "
				+ ((assoWithHEtotalTime / 1000) % 60) + " seconds");
		LOGGER.info("######################################################################");
		LOGGER.info(" ");
		LOGGER.info(" ");
		LOGGER.info(" ");
	}

}
