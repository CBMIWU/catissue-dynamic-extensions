
package edu.wustl.cab2b.server.path;

import static edu.wustl.cab2b.common.errorcodes.ErrorCodeConstants.DB_0003;
import static edu.wustl.cab2b.common.errorcodes.ErrorCodeConstants.IO_0001;
import static edu.wustl.cab2b.common.util.Constants.CONNECTOR;
import static edu.wustl.cab2b.server.path.PathConstants.ASSOCIATION_FILE_NAME;
import static edu.wustl.cab2b.server.path.PathConstants.FIELD_SEPARATOR;
import static edu.wustl.cab2b.server.path.PathConstants.INTRA_MODEL_ASSO_FILE_NAME;
import static edu.wustl.cab2b.server.path.PathConstants.PATH_FILE_NAME;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.log4j.Logger;

import edu.common.dynamicextensions.domaininterface.AssociationInterface;
import edu.common.dynamicextensions.domaininterface.EntityGroupInterface;
import edu.common.dynamicextensions.domaininterface.EntityInterface;
import edu.wustl.cab2b.common.exception.RuntimeException;
import edu.wustl.cab2b.common.util.IdGenerator;
import edu.wustl.cab2b.common.util.Utility;
import edu.wustl.cab2b.server.cache.EntityCache;
import edu.wustl.cab2b.server.util.DataFileLoaderInterface;
import edu.wustl.cab2b.server.util.SQLQueryUtil;
import edu.wustl.cab2b.server.util.ServerProperties;

/**
 * This class builds all paths for a Domain Model of an application.<br>
 * This class acts as a Controller that calls different utility classes to build
 * all possible non-redundant paths for a given model. It also loads the
 * generated paths to database.<br>
 * This class decides whether to create a storage table for entity or not based
 * on {@link edu.wustl.cab2b.server.path.PathConstants#CREATE_TABLE_FOR_ENTITY}
 * To create a table for entity set this to TRUE before calling this code else
 * set it to false. <b> NOTE : </b> It does not creates PATH table. It assumes
 * that the table is already been present in database.<br>
 *
 * @author Chandrakant Talele
 * @author Munesh
 */
public final class PathBuilder
{
	/**
	 * Instantiates a new path builder.
	 */
	private PathBuilder()
	{
		// Empty constructor
	}

	/**
	 * The Constant LOGGER.
	 */
	private static final Logger LOGGER = edu.wustl.common.util.logger.Logger
			.getLogger(PathBuilder.class);

	/**
	 * The association set.
	 */
	private static Set<Long> associationSet = new HashSet<Long>();

	/**
	 * The src des vs associations.
	 */
	private static Map<String, List<AssociationInterface>> srcDesVsAssociations = new HashMap<String, List<AssociationInterface>>();

	/**
	 * The data file loader.
	 */
	private static DataFileLoaderInterface dataFileLoader;

	/**
	 * Transforms paths into list of associations.s
	 *
	 * @param connection
	 *            Database connection to use.
	 * @param entityGroup
	 *            the entity group
	 */
	protected static void transformAndLoadPaths(Connection connection,
			EntityGroupInterface entityGroup)
	{
		loadCache(entityGroup);
		try
		{
			registerIntraModelAssociations(connection, associationSet);
			transformGeneratedPaths(connection);
		}
		catch (IOException e1)
		{
			throw new RuntimeException("Error in writing to output file", e1, IO_0001);
		}
		catch (SQLException e)
		{
			throw new RuntimeException("Exception while firing Parameterized query.", e, DB_0003);
		}
	}

	/**
	 * This method registered all the associations present in dynamic extension.
	 * It creates new data files at {@link PathConstants#ASSOCIATION_FILE_NAME}
	 * and {@link PathConstants#INTRA_MODEL_ASSO_FILE_NAME} with data.
	 * Then uses these files to load data in tables ASSOCIATION and
	 * INTRA_MODEL_ASSOCIATION.
	 *
	 * @param connection -
	 *            Database connection to use to fire SQLs.
	 * @param associationIdSet
	 *            Set of all association identifiers which are to be registered.
	 * @throws IOException
	 *             If file operation fails.
	 */
	protected static synchronized void registerIntraModelAssociations(Connection connection,
			Set<Long> associationIdSet) throws IOException
	{
		LOGGER.debug("Registering all the associations present in DE as IntraModelAssociations");
		BufferedWriter associationFile = new BufferedWriter(new FileWriter(new File(
				ASSOCIATION_FILE_NAME)));
		BufferedWriter intraModelAssociationFile = new BufferedWriter(new FileWriter(new File(
				INTRA_MODEL_ASSO_FILE_NAME)));
		try
		{
			long nextId = getNextAssociationId(associationIdSet.size(), connection).longValue(); // NOPMD by gaurav_sawant on 5/6/10 6:46 PM
			for (Long associationId : associationIdSet)
			{
				associationFile.write(Long.toString(nextId));
				associationFile.write(FIELD_SEPARATOR);
				associationFile.write(Integer.toString(AssociationType.INTRA_MODEL_ASSOCIATION
						.getValue()));
				associationFile.write("\n");
				associationFile.flush();

				intraModelAssociationFile.write(Long.toString(nextId));
				intraModelAssociationFile.write(FIELD_SEPARATOR);
				intraModelAssociationFile.write(Long.toString(associationId));
				intraModelAssociationFile.write("\n");
				intraModelAssociationFile.flush();
				nextId++; // NOPMD by gaurav_sawant on 5/6/10 6:46 PM
			}
			String columns = "(ASSOCIATION_ID,ASSOCIATION_TYPE)";
			loadDataFromFile(connection, ASSOCIATION_FILE_NAME, columns, "ASSOCIATION",
					new Class[]{Long.class, Integer.class});

			columns = "(ASSOCIATION_ID,DE_ASSOCIATION_ID)";
			loadDataFromFile(connection, INTRA_MODEL_ASSO_FILE_NAME, columns,
					"INTRA_MODEL_ASSOCIATION", new Class[]{Long.class, Long.class});
			LOGGER.debug("All the associations are registered");
		}
		finally
		{
			associationFile.close();
			intraModelAssociationFile.close();
		}

	}

	/**
	 * Converts paths present in file {@link PathConstants#PATH_FILE_NAME} from
	 * list of entity identifiers to list of association identifiers. Then overwrites that file
	 * by converted paths.
	 *
	 * @param connection -
	 *            Database connection to use to fire SQLs.
	 * @throws IOException
	 *             If file operation fails.
	 * @throws SQLException
	 *             If database query fails.
	 */
	protected static void transformGeneratedPaths(Connection connection) throws IOException,
			SQLException
	{
		LOGGER.info("Path transformation is started");
		PreparedStatement prepareStatement = connection
				.prepareStatement("select ASSOCIATION_ID from intra_model_association where DE_ASSOCIATION_ID = ?");

		List<String> pathList = readFullFile();
		BufferedWriter pathFile = new BufferedWriter(new FileWriter(new File(PATH_FILE_NAME)));
		IdGenerator idGenerator = new IdGenerator(getNextPathId(connection)); // NOPMD by gaurav_sawant on 5/6/10 6:47 PM
		int totalPaths = pathList.size();
		LOGGER.info("Transforming " + totalPaths + " paths...");
		for (int i = 0; i < totalPaths; i++)
		{
			log(totalPaths, i);
			String[] columnValues = pathList.get(i).split(FIELD_SEPARATOR);
			long firstEntityId = Long.parseLong(columnValues[0]);
			long lastEntityId = Long.parseLong(columnValues[2]);

			Long[] allEntitiesInPath = getEntityIdSequence(firstEntityId, columnValues[1],
					lastEntityId);

			for (String iPath : getIntraModelPaths(allEntitiesInPath, prepareStatement))
			{
				pathFile.write(Long.toString(idGenerator.getNextId()));
				pathFile.write(FIELD_SEPARATOR);
				pathFile.write(Long.toString(firstEntityId));
				pathFile.write(FIELD_SEPARATOR);
				pathFile.write(iPath);
				pathFile.write(FIELD_SEPARATOR);
				pathFile.write(Long.toString(lastEntityId));
				pathFile.write("\n");
				pathFile.flush();
			}
		}
		pathFile.close();
		prepareStatement.close();
		String pathColumns = "(PATH_ID,FIRST_ENTITY_ID,INTERMEDIATE_PATH,LAST_ENTITY_ID)";
		LOGGER.info("Generated the paths to file : " + PATH_FILE_NAME);
		loadDataFromFile(connection, PATH_FILE_NAME, pathColumns, "PATH", new Class[]{Long.class,
				Long.class, String.class, Long.class});
	}

	/**
	 * Log.
	 *
	 * @param totalPaths
	 *            the total paths
	 * @param transformedPaths
	 *            the transformed paths
	 */
	private static void log(int totalPaths, int transformedPaths)
	{
		if (transformedPaths % 200 == 0)
		{
			Float percentage = (float) transformedPaths / totalPaths * 100;
			String percentageAsString = percentage.toString();
			int index = percentageAsString.indexOf('.');
			if (percentageAsString.length() >= index + 3)
			{
				percentageAsString = percentageAsString.substring(0, index + 3);
			}
			LOGGER.info(percentageAsString + " %");
		}
	}

	/**
	 * Reads full file {@link PathConstants#PATH_FILE_NAME} and returns it as
	 * array of Strings.
	 *
	 * @return Array of strings where one element is one line from file.
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 */
	protected static List<String> readFullFile() throws IOException
	{
		BufferedReader bufferedReader = null; // NOPMD by gaurav_sawant on 5/6/10 6:44 PM
		try
		{
			bufferedReader = new BufferedReader(new FileReader(PATH_FILE_NAME)); // NOPMD by gaurav_sawant on 5/6/10 6:55 PM
		}
		catch (FileNotFoundException e)
		{
			throw new RuntimeException("File not found :" + PATH_FILE_NAME, e, IO_0001);
		}
		ArrayList<String> inputFile = new ArrayList<String>(1000);
		//trying to get max efficiency by declaring initial size
		String onePath = bufferedReader.readLine();
		while (onePath != null)
		{
			inputFile.add(onePath);
			onePath = bufferedReader.readLine();
		}
		bufferedReader.close();
		return inputFile;
	}

	/**
	 * Returns All Possible Paths present in given list of entities. The order
	 * of entity IDs is important. Paths will be list of association IDs.
	 *
	 * @param entityIds
	 *            Array of entities IDs present in a path.
	 * @param prepareStatement
	 *            the prepare statement
	 * @return the List of possible intra model paths.
	 * @throws SQLException
	 *             the SQL exception
	 */
	protected static List<String> getIntraModelPaths(Long[] entityIds,
			PreparedStatement prepareStatement) throws SQLException
	{
		List<String> allPossiblePaths = new ArrayList<String>();
		allPossiblePaths.add("");
		for (int i = 0; i < entityIds.length - 1; i++)
		{
			List<Long> associationIdList = getIntraModelAssociations(entityIds[i],
					entityIds[i + 1], prepareStatement);

			List<String> newPathList = new ArrayList<String>();
			for (Long associationId : associationIdList)
			{
				List<String> pathList = new ArrayList<String>(allPossiblePaths.size());
				for (String path : allPossiblePaths)
				{
					StringBuffer buff = new StringBuffer();
					if (path.length() != 0)
					{
						buff.append(path);
						buff.append(CONNECTOR);
					}
					buff.append(associationId.toString());
					pathList.add(buff.toString());
				}
				newPathList.addAll(pathList);
			}
			allPossiblePaths = newPathList; // NOPMD by gaurav_sawant on 5/6/10 6:55 PM
		}
		return allPossiblePaths;
	}

	/**
	 * This method calls Dynamic extension's API to get all the Associations
	 * present between passed Source and Target Entities. Then it gets IDs of
	 * those and finds corresponding mapping IDs from table
	 * INTRA_MODEL_ASSOCIATION. and returns list of all those mapping
	 * identifiers.
	 *
	 * @param source
	 *            Source end of the association.
	 * @param target
	 *            Target end of the association.
	 * @param prepareStatement
	 *            the prepare statement
	 * @return List of association IDs from INTRA_MODEL_ASSOCIATION table
	 * @throws SQLException
	 *             the SQL exception
	 */
	protected static List<Long> getIntraModelAssociations(Long source, Long target,
			PreparedStatement prepareStatement) throws SQLException
	{
		List<AssociationInterface> associations = srcDesVsAssociations.get(source + CONNECTOR
				+ target);
		checkEmptyAssociations(source, target, associations);
		ArrayList<Long> list = new ArrayList<Long>(associations.size()); // NOPMD by gaurav_sawant on 5/6/10 6:55 PM
		for (AssociationInterface association : associations)
		{
			prepareStatement.setLong(1, association.getId());
			String[][] res = SQLQueryUtil.executeQuery(prepareStatement);
			if (res.length != 1 || res[0].length != 1)
			{
				throw new RuntimeException(
						"More than one OR Zero rows found in INTRA_MODEL_ASSOCIATION for DE_ASSOCIATION_ID : "
								+ association.getId());
			}
			list.add(Long.parseLong(res[0][0]));
		}
		return list;
	}

	/**
	 * Check empty associations.
	 *
	 * @param source
	 *            the source
	 * @param target
	 *            the target
	 * @param associations
	 *            the associations
	 */
	private static void checkEmptyAssociations(Long source, Long target,
			List<AssociationInterface> associations)
	{
		if (associations == null || associations.isEmpty())
		{
			throw new RuntimeException("No association present in entity : " + source
					+ " and entity: " + target);
		}
	}

	/**
	 * This method parses the intermediate path and returns all identifiers
	 * present in it.<br>
	 * It parses intermediate path string based on
	 *
	 * @param firstEntityId
	 *            the first entity id
	 * @param intermediatePath
	 *            the intermediate path
	 * @param lastEntityId
	 *            the last entity id
	 * @return the Long[] identifiers of all entities present in the path in
	 *         sequential order. {@link PathConstants#CONNECTOR} and then
	 *         converts it to entity identifiers. Then adds first entity id at
	 *         the start and last entity id to the end and returns the list of
	 *         Long.
	 */
	protected static Long[] getEntityIdSequence(Long firstEntityId, String intermediatePath,
			Long lastEntityId)
	{
		ArrayList<Long> entitySequence = new ArrayList<Long>();
		entitySequence.add(firstEntityId);

		if (intermediatePath.length() != 0)
		{
			String[] intermediateEntityIds = intermediatePath.split(CONNECTOR);
			for (String intermediateEntityId : intermediateEntityIds)
			{
				entitySequence.add(Long.decode(intermediateEntityId));
			}
		}
		entitySequence.add(lastEntityId);
		return entitySequence.toArray(new Long[entitySequence.size()]);
	}

	/**
	 * Returns the next available ID from CAB2B_ID_TABLE which can be used to
	 * insert records in ASSOCIATION table. It also updates the CAB2B_ID_TABLE
	 * to mark next available ID as old id + no of associations.
	 *
	 * @param noOfAssociations
	 *            No of associations you want to store.
	 * @param connection
	 *            database connection to use for firing SQLs.
	 * @return Next available ID to insert records in ASSOCIATION table.
	 */
	protected static synchronized Long getNextAssociationId(int noOfAssociations,
			Connection connection)
	{
		String[][] result = SQLQueryUtil.executeQuery(
				"select NEXT_ASSOCIATION_ID from CAB2B_ID_TABLE", connection);
		if (result.length != 1)
		{
			throw new RuntimeException("Zero or more than one rows found in CAB2B_ID_TABLE");
		}
		Long nextId = Long.parseLong(result[0][0]);
		String updateNextIdSql = "update CAB2B_ID_TABLE set NEXT_ASSOCIATION_ID = "
				+ (nextId + noOfAssociations);

		SQLQueryUtil.executeUpdate(updateNextIdSql, connection);
		return nextId;
	}

	/**
	 * Loads data from given file into given table. <b> NOTE : </b> This method
	 * will not create table in database. It assumes that table is already
	 * present
	 *
	 * @param connection
	 *            Connection to use to fire SQL
	 * @param fileName
	 *            Full path of data file.
	 * @param columns
	 *            Data columns in table. They should be in format
	 *            "(column1,column2,...)"
	 * @param tableName
	 *            Name of the table in which data to load.
	 * @param dataTypes
	 *            the data types
	 */
	protected static void loadDataFromFile(Connection connection, String fileName, String columns,
			String tableName, Class<?>[] dataTypes)
	{
		synchronized (PathBuilder.class)
		{
			if (dataFileLoader == null)
			{
				initDataLoader();
			}
		}
		dataFileLoader.loadDataFromFile(connection, fileName, columns, tableName, dataTypes,
				PathConstants.FIELD_SEPARATOR);
	}

	/**
	 * Inits the data loader.
	 */
	private static void initDataLoader()
	{
		String className = null;
		try
		{
			className = ServerProperties.getDatabaseLoader();
			dataFileLoader = (DataFileLoaderInterface) Class.forName(className).newInstance();
		}
		catch (InstantiationException e)
		{
			LOGGER.error("Unable to instantiation " + className);
			LOGGER.error(Utility.getStackTrace(e));
		}
		catch (IllegalAccessException e)
		{
			LOGGER.error("Unable to access public default constructor of " + className);
			LOGGER.error(Utility.getStackTrace(e));
		}
		catch (ClassNotFoundException e)
		{
			LOGGER.error("Class " + className + " not found. Please put it in classpath");
			LOGGER.error(Utility.getStackTrace(e));
		}
		catch (ClassCastException e)
		{
			LOGGER.error("Class " + className + " must implement DataFileLoaderInterface");
			LOGGER.error(Utility.getStackTrace(e));
		}
	}

	/**
	 * Initializes the cache for building paths.
	 *
	 * @param entGroup
	 *            the ent group
	 */
	private static void loadCache(EntityGroupInterface entGroup)
	{
		EntityCache.getInstance().refreshCache();
		for (EntityGroupInterface entityGroup : EntityCache.getInstance().getEntityGroups())
		{
			Collection<EntityInterface> allEntities = entityGroup.getEntityCollection();

			fetchEntityAssociations(allEntities);
		}
		for (EntityInterface entity : entGroup.getEntityCollection())
		{
			for (AssociationInterface association : entity.getAssociationCollection())
			{
				associationSet.add(association.getId());
			}
		}
	}

	/**
	 * Fetch entity associations.
	 *
	 * @param allEntities
	 *            the all entities
	 */
	private static void fetchEntityAssociations(Collection<EntityInterface> allEntities)
	{
		for (EntityInterface entity : allEntities)
		{
			for (AssociationInterface association : entity.getAssociationCollection())
			{
				String key = entity.getId() + CONNECTOR + association.getTargetEntity().getId();
				List<AssociationInterface> associations = srcDesVsAssociations.get(key);
				if (associations == null)
				{
					associations = new ArrayList<AssociationInterface>();
					srcDesVsAssociations.put(key, associations);
				}
				associations.add(association);
			}
		}
	}

	/**
	 * Returns next path Id.
	 *
	 * @param connection
	 *            the connection
	 * @return Next path Id.
	 */
	public static long getNextPathId(Connection connection)
	{
		String[][] result = SQLQueryUtil.executeQuery("select MAX(PATH_ID) from PATH", connection);
		Long maxId;
		if (result[0][0] == null)
		{
			maxId = 0L;
		}
		else
		{
			maxId = Long.parseLong(result[0][0]);
		}
		return maxId + 1;
	}

}