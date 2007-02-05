/**
 * 
 */

package edu.common.dynamicextensions.util;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.math.BigDecimal;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.StringTokenizer;

import edu.common.dynamicextensions.bizlogic.BizLogicFactory;
import edu.common.dynamicextensions.domain.userinterface.ContainmentAssociationControl;
import edu.common.dynamicextensions.domaininterface.AbstractAttributeInterface;
import edu.common.dynamicextensions.domaininterface.AbstractMetadataInterface;
import edu.common.dynamicextensions.domaininterface.AssociationInterface;
import edu.common.dynamicextensions.domaininterface.AttributeInterface;
import edu.common.dynamicextensions.domaininterface.AttributeTypeInformationInterface;
import edu.common.dynamicextensions.domaininterface.EntityGroupInterface;
import edu.common.dynamicextensions.domaininterface.EntityInterface;
import edu.common.dynamicextensions.domaininterface.userinterface.CheckBoxInterface;
import edu.common.dynamicextensions.domaininterface.userinterface.ComboBoxInterface;
import edu.common.dynamicextensions.domaininterface.userinterface.ContainerInterface;
import edu.common.dynamicextensions.domaininterface.userinterface.ControlInterface;
import edu.common.dynamicextensions.domaininterface.userinterface.DatePickerInterface;
import edu.common.dynamicextensions.domaininterface.userinterface.FileUploadInterface;
import edu.common.dynamicextensions.domaininterface.userinterface.ListBoxInterface;
import edu.common.dynamicextensions.domaininterface.userinterface.RadioButtonInterface;
import edu.common.dynamicextensions.domaininterface.userinterface.TextAreaInterface;
import edu.common.dynamicextensions.domaininterface.userinterface.TextFieldInterface;
import edu.common.dynamicextensions.entitymanager.EntityManagerExceptionConstantsInterface;
import edu.common.dynamicextensions.exception.DynamicExtensionsApplicationException;
import edu.common.dynamicextensions.exception.DynamicExtensionsSystemException;
import edu.common.dynamicextensions.processor.ProcessorConstants;
import edu.common.dynamicextensions.util.global.Constants;
import edu.common.dynamicextensions.util.global.Variables;
import edu.common.dynamicextensions.util.global.Constants.InheritanceStrategy;
import edu.wustl.common.beans.NameValueBean;
import edu.wustl.common.bizlogic.AbstractBizLogic;
import edu.wustl.common.util.CVSTagReader;
import edu.wustl.common.util.Utility;
import edu.wustl.common.util.dbManager.DAOException;
import edu.wustl.common.util.dbManager.DBUtil;
import edu.wustl.common.util.global.ApplicationProperties;
import edu.wustl.common.util.logger.Logger;

/**
 * @author chetan_patil
 *
 */
public class DynamicExtensionsUtility
{

	/**
	 * This method fetches the Control instance from the Database given the corresponding Control Identifier.
	 * @param controlIdentifier The Idetifier of the Control.
	 * @return the ControlInterface
	 * @throws DynamicExtensionsSystemException on System exception
	 * @throws DynamicExtensionsApplicationException on Application exception
	 */
	public static ControlInterface getControlByIdentifier(String controlIdentifier)
			throws DynamicExtensionsSystemException, DynamicExtensionsApplicationException
	{
		ControlInterface controlInterface = null;
		controlInterface = (ControlInterface) getObjectByIdentifier(ControlInterface.class
				.getName(), controlIdentifier);
		return controlInterface;
	}

	/**
	 * This method fetches the Container instance from the Database given the corresponding Container Identifier.
	 * @param containerIdentifier The Idetifier of the Container.
	 * @return the ContainerInterface
	 * @throws DynamicExtensionsSystemException on System exception
	 * @throws DynamicExtensionsApplicationException on Application exception
	 */
	public static EntityGroupInterface getEntityGroupByIdentifier(String entityGroupIdentifier)
			throws DynamicExtensionsSystemException, DynamicExtensionsApplicationException
	{
		EntityGroupInterface entityGroupInterface = null;
		entityGroupInterface = (EntityGroupInterface) getObjectByIdentifier(
				EntityGroupInterface.class.getName(), entityGroupIdentifier);
		return entityGroupInterface;
	}

	/**
	 * This method fetches the Container instance from the Database given the corresponding Container Identifier.
	 * @param containerIdentifier The Idetifier of the Container.
	 * @return the ContainerInterface
	 * @throws DynamicExtensionsSystemException on System exception
	 * @throws DynamicExtensionsApplicationException on Application exception
	 */
	public static ContainerInterface getContainerByIdentifier(String containerIdentifier)
			throws DynamicExtensionsSystemException, DynamicExtensionsApplicationException
	{
		ContainerInterface containerInterface = null;
		containerInterface = (ContainerInterface) getObjectByIdentifier(ContainerInterface.class
				.getName(), containerIdentifier);
		return containerInterface;
	}
	
	
	/**
	 * This method fetches the Container instance from the Database given the corresponding Container Identifier.
	 * @param containerIdentifier The Idetifier of the Container.
	 * @return the ContainerInterface
	 * @throws DynamicExtensionsSystemException on System exception
	 * @throws DynamicExtensionsApplicationException on Application exception
	 */
	public static AttributeInterface getAttributeByIdentifier(String attributeIdentifier)
			throws DynamicExtensionsSystemException, DynamicExtensionsApplicationException
	{
		AttributeInterface attributeInterface = null;
		attributeInterface  = (AttributeInterface) getObjectByIdentifier(AttributeInterface.class
				.getName(), attributeIdentifier);
		return attributeInterface;
	}
	
	
	

	/**
	 * This method returns object for a given class name and identifer 
	 * @param objectName  name of the class of the object
	 * @param identifier identifier of the object
	 * @return  obejct
	 * @throws DynamicExtensionsSystemException on System exception
	 * @throws DynamicExtensionsApplicationException on Application exception
	 */
	private static Object getObjectByIdentifier(String objectName, String identifier)
			throws DynamicExtensionsSystemException, DynamicExtensionsApplicationException
	{
		AbstractBizLogic bizLogic = BizLogicFactory.getDefaultBizLogic();
		Object object = null;
		if (objectName == null || identifier == null || identifier.trim().length() == 0)
		{
			return null;
		}
		try
		{
			List objectList = bizLogic.retrieve(objectName, Constants.ID, identifier);

			if (objectList == null || objectList.isEmpty())
			{
				throw new DynamicExtensionsSystemException("OBJECT_NOT_FOUND");
			}

			object = objectList.get(0);
		}
		catch (DAOException e)
		{
			throw new DynamicExtensionsSystemException(e.getMessage(), e);
		}
		return object;
	}

	/**
	 * @param controlInterface ControlInterface
	 * @return String ControlName
	 */
	public static String getControlName(ControlInterface controlInterface)
	{
		if (controlInterface != null)
		{
			if (controlInterface instanceof TextFieldInterface)
			{
				return ProcessorConstants.TEXT_CONTROL;
			}
			else if (controlInterface instanceof ComboBoxInterface)
			{
				return ProcessorConstants.COMBOBOX_CONTROL;
			}
			else if (controlInterface instanceof ListBoxInterface)
			{
				return ProcessorConstants.COMBOBOX_CONTROL;
			}
			else if (controlInterface instanceof DatePickerInterface)
			{
				return ProcessorConstants.DATEPICKER_CONTROL;
			}
			else if (controlInterface instanceof TextAreaInterface)
			{
				return ProcessorConstants.TEXT_CONTROL;
			}
			else if (controlInterface instanceof RadioButtonInterface)
			{
				return ProcessorConstants.RADIOBUTTON_CONTROL;
			}
			else if (controlInterface instanceof CheckBoxInterface)
			{
				return ProcessorConstants.CHECKBOX_CONTROL;
			}
			else if (controlInterface instanceof FileUploadInterface)
			{
				return ProcessorConstants.FILEUPLOAD_CONTROL;
			}
			if (controlInterface instanceof ContainmentAssociationControl)
			{
				return ProcessorConstants.ADD_SUBFORM_CONTROL;
			}
		}
		return null;
	}

	/**
	 * 
	 * @param controlCollectio
	 * @param sequenceNumber
	 * @return
	 */
	public static ControlInterface getControlBySequenceNumber(Collection controlCollection,
			int sequenceNumber)
	{
		ControlInterface controlInterface = null;
		if (controlCollection != null)
		{
			Iterator controlIterator = controlCollection.iterator();
			while (controlIterator.hasNext())
			{
				controlInterface = (ControlInterface) controlIterator.next();
				if (controlInterface.getSequenceNumber() != null
						&& controlInterface.getSequenceNumber() == sequenceNumber
				/*&& !controlInterface.getSequenceNumberChanged()*/)
				{
					controlInterface.setSequenceNumberChanged(true);
					return controlInterface;
				}
			}
		}
		return null;
	}

	public static ControlInterface getControlBySequenceNumber(ControlInterface[] controlCollection,
			int sequenceNumber)
	{
		ControlInterface controlInterface = null;
		if (controlCollection != null)
		{
			int noOfControls = controlCollection.length;
			for (int i = 0; i < noOfControls; i++)
			{
				controlInterface = controlCollection[i];
				if (controlInterface.getSequenceNumber() != null
						&& controlInterface.getSequenceNumber() == sequenceNumber)
				{
					controlInterface.setSequenceNumberChanged(true);
					return controlInterface;
				}
				else
				{
					controlInterface = null;
				}
			}
		}
		return controlInterface;
	}

	/**
	 * 
	 */
	public static void initialiseApplicationVariables()
	{
		try
		{
			//			EntityManager.getInstance().getAllEntities();
			//			EntityManager.getInstance().getAllContainers();
			DBUtil.currentSession();
			DBUtil.closeSession();
		}
		catch (Exception e)
		{
			throw new RuntimeException(e);
		}

		if (Logger.out == null)
		{
			Logger.configure("");
		}

		if (!Variables.databaseName.equals(Constants.MYSQL_DATABASE))
		{
			//set string/function for oracle

			Variables.datePattern = "mm-dd-yyyy";
			Variables.timePattern = "hh-mi-ss";
			Variables.dateFormatFunction = "TO_CHAR";
			Variables.timeFormatFunction = "TO_CHAR";
			Variables.dateTostrFunction = "TO_CHAR";
			Variables.strTodateFunction = "TO_DATE";
		}
		else
		{
			Variables.datePattern = "%m-%d-%Y";
			Variables.timePattern = "%H:%i:%s";
			Variables.dateFormatFunction = "DATE_FORMAT";
			Variables.timeFormatFunction = "TIME_FORMAT";
			Variables.dateTostrFunction = "TO_CHAR";
			Variables.strTodateFunction = "STR_TO_DATE";
		}

	}

	/**
	 * 
	 */
	public static void initialiseApplicationInfo()
	{
		String fileName = Variables.dynamicExtensionsHome + System.getProperty("file.separator")
				+ ApplicationProperties.getValue("application.version.file");
		CVSTagReader cvsTagReader = new CVSTagReader();
		String cvsTag = cvsTagReader.readTag(fileName);
		Variables.applicationCvsTag = cvsTag;
		Logger.out.info("========================================================");
		Logger.out.info("Application Information");
		Logger.out.info("Name: " + Variables.applicationName);
		Logger.out.info("Version: " + Variables.applicationVersion);
		Logger.out.info("CVS TAG: " + Variables.applicationCvsTag);
		Logger.out.info("Path: " + Variables.applicationHome);
		Logger.out.info("Database Name: " + Variables.databaseName);
		Logger.out.info("========================================================");
	}

	public static AttributeTypeInformationInterface getAttributeTypeInformation(
			AbstractAttributeInterface abstractAttributeInterface)
	{
		AttributeTypeInformationInterface attributeTypeInformation = null;
		if (abstractAttributeInterface != null)
		{
			if (abstractAttributeInterface instanceof AttributeInterface)
			{
				attributeTypeInformation = ((AttributeInterface) abstractAttributeInterface)
						.getAttributeTypeInformation();
			}
		}
		return attributeTypeInformation;
	}

	/**
	 * This method converts stack trace to the string representation
	 * @param aThrowable   throwable object
	 * @return String representation  of the stack trace
	 */
	public static String getStackTrace(Throwable throwable)
	{
		final Writer result = new StringWriter();
		final PrintWriter printWriter = new PrintWriter(result);
		throwable.printStackTrace(printWriter);
		return result.toString();
	}

	/**
	 * Converts string to integer
	 * @param string
	 * @return
	 * @throws DynamicExtensionsApplicationException
	 */
	public static int convertStringToInt(String string)
			throws DynamicExtensionsApplicationException
	{
		int intValue = 0;
		if (string != null)
		{
			try
			{
				if (string.trim().equals(""))
				{
					intValue = 0; //Assume 0 for blank values
				}
				else
				{
					intValue = Integer.parseInt(string);
				}
			}
			catch (NumberFormatException e)
			{
				throw new DynamicExtensionsApplicationException(e.getMessage(), e);
			}
		}
		return intValue;
	}

	/**
	 * Checks that the input String contains only numeric digits.
	 * @param numString The string whose characters are to be checked.
	 * @return Returns false if the String contains any alphabet else returns true. 
	 * */
	public static boolean isNaturalNumber(String numString)
	{
		boolean isNaturalNumber = true;
		try
		{
			double doubleValue = Double.parseDouble(numString);
			if (doubleValue < 0)
			{
				isNaturalNumber = false;
			}
		}
		catch (NumberFormatException exp)
		{
			isNaturalNumber = false;
		}
		return isNaturalNumber;
	}

	/**
	 * 
	 * @param numString
	 * @return
	 */
	public static boolean isNumeric(String numString)
	{
		boolean isNumeric = true;
		BigDecimal bigDecimal = null;
		try
		{
			bigDecimal = new BigDecimal(numString);
			if (bigDecimal == null)
			{
				isNumeric = false;
			}
		}
		catch (NumberFormatException exp)
		{
			isNumeric = false;
		}
		return isNumeric;
	}

	public static int getCurrentDay()
	{
		return Calendar.getInstance().get(Calendar.DAY_OF_MONTH);
	}

	public static int getCurrentMonth()
	{
		return (Calendar.getInstance().get(Calendar.MONTH) + 1);
	}

	public static int getCurrentYear()
	{
		return Calendar.getInstance().get(Calendar.YEAR);
	}
	
	public static int getCurrentHours()
	{
		return Calendar.getInstance().get(Calendar.HOUR);
	}
	
	public static int getCurrentMinutes()
	{
		return Calendar.getInstance().get(Calendar.MINUTE);
	}

	/**
	 *
	 * @param originalObject Object
	 * @return Object
	 */
	public static Object cloneObject(Object originalObject)
	{
		Object clonedObject = null;
		try
		{
			ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
			ObjectOutputStream objectOutputStream = new ObjectOutputStream(outputStream);
			objectOutputStream.writeObject(originalObject);
			//retrieve back
			ByteArrayInputStream inputStream = new ByteArrayInputStream(outputStream.toByteArray());
			ObjectInputStream objectInputStream = new ObjectInputStream(inputStream);
			clonedObject = objectInputStream.readObject();
		}
		catch (IOException ioe)
		{

			ioe.printStackTrace();
		}
		catch (ClassNotFoundException cnfe)
		{
			cnfe.printStackTrace();
		}

		return clonedObject;
	}

	/**
	 * @param string : string to be checked
	 * @param list: List that is to be checked if string is contained
	 * @return check if a string is contained in the passed list and return true if yes
	 */
	public static boolean isStringInList(String string, List<String> list)
	{
		boolean isContainedInList = false;
		if ((string != null) && (list != null))
		{
			String listString = null;
			Iterator<String> iterator = list.iterator();
			while (iterator.hasNext())
			{
				listString = iterator.next();
				if (string.equals(listString))
				{
					isContainedInList = true;
					break;
				}
			}
		}
		return isContainedInList;
	}

	/**
	 *
	 * @param list list of NameValueBeanObjects
	 */
	@SuppressWarnings("unchecked")
	public static void sortNameValueBeanListByName(List<NameValueBean> list)
	{
		Collections.sort(list, new Comparator()
		{

			public int compare(Object o1, Object o2)
			{
				String s1 = "";
				String s2 = "";
				if (o1 != null)
				{
					s1 = ((NameValueBean) o1).getName();
				}
				if (o2 != null)
				{
					s2 = ((NameValueBean) o2).getName();
				}
				return s1.compareTo(s2);
			}
		});
	}

	public static EntityGroupInterface getEntityGroup(EntityInterface entity)
	{
		EntityGroupInterface entityGroup = null;
		if (entity != null)
		{
			Collection<EntityGroupInterface> entityGroupCollection = entity
					.getEntityGroupCollection();
			if (entityGroupCollection != null)
			{
				Iterator<EntityGroupInterface> entityGroupIter = entityGroupCollection.iterator();
				if (entityGroupIter.hasNext())
				{
					entityGroup = entityGroupIter.next();
				}
			}
		}
		return entityGroup;
	}

	/**
	 * @param controlsSeqNumbers : String of controls sequence numbers
	 * @param delimiter Delimiter used in string
	 * @return
	 */
	public static Integer[] convertToIntegerArray(String controlsSeqNumbers, String delimiter)
	{
		ArrayList<Integer> integerList = new ArrayList<Integer>();
		if (controlsSeqNumbers != null)
		{
			String str = null;
			Integer integer = null;
			StringTokenizer strTokenizer = new StringTokenizer(controlsSeqNumbers, delimiter);
			if (strTokenizer != null)
			{
				while (strTokenizer.hasMoreElements())
				{
					str = strTokenizer.nextToken();
					if (str != null)
					{
						try
						{
							integer = new Integer(str);
							integerList.add(integer);
						}
						catch (NumberFormatException e)
						{
							Logger.out.error(e);
						}
					}
				}
			}
		}
		return integerList.toArray(new Integer[integerList.size()]);
	}

	/**
	 * validate the entity for
	 * 1. Name - should not contain any special characters, should not be empty,null
	 * 2. Description - should be less than 1000 characters.
	 * 
	 * @param entity
	 * @throws DynamicExtensionsApplicationException
	 */
	public static void validateEntityForSaving(EntityInterface entity)
			throws DynamicExtensionsApplicationException
	{

		validateName(entity.getName());
		Collection<AbstractAttributeInterface> collection = entity.getAbstractAttributeCollection();
		if (collection != null && !collection.isEmpty())
		{
			Iterator iterator = collection.iterator();
			while (iterator.hasNext())
			{
				AbstractMetadataInterface abstractMetadataInterface = (AbstractMetadataInterface) iterator
						.next();
				validateName(abstractMetadataInterface.getName());
			}
		}

		if (entity.getDescription() != null && entity.getDescription().length() > 1000)
		{
			throw new DynamicExtensionsApplicationException("Entity description size exceeded ",
					null, EntityManagerExceptionConstantsInterface.DYEXTN_A_004);
		}
		validateDuplicateNamesWithinEntity(entity);
		
		if (entity.getInheritanceStrategy().equals(InheritanceStrategy.TABLE_PER_HEIRARCHY) && entity.getParentEntity() != null ) {
			if (entity.getDiscriminatorColumn() == null ||entity.getDiscriminatorColumn().equals("") ) {
				throw new DynamicExtensionsApplicationException(
						"Discriminator Column and value is required for TABLE_PER_HEIRARCHY strategy", null,
						EntityManagerExceptionConstantsInterface.DYEXTN_A_012);
				
			}

			if (entity.getDiscriminatorValue() == null ||entity.getDiscriminatorValue().equals("") ) {
				throw new DynamicExtensionsApplicationException(
						"Discriminator Column and value is required for TABLE_PER_HEIRARCHY strategy", null,
						EntityManagerExceptionConstantsInterface.DYEXTN_A_012);
			}
		}
		return;
	}

	public static void validateDuplicateNamesWithinEntity(EntityInterface entity) throws DynamicExtensionsApplicationException
	{
		Collection<AbstractAttributeInterface> collection = entity.getAbstractAttributeCollection();
		if (collection != null || !collection.isEmpty()) 
		{
		Collection<String> nameCollection = new HashSet<String>();
		for (AbstractAttributeInterface attribute : collection)
		{
			if (!nameCollection.contains(attribute.getName()))
			{
				nameCollection.add(attribute.getName());
			}
			else
			{
				throw new DynamicExtensionsApplicationException(
						"Attribute names should be unique for the entity ", null,
						EntityManagerExceptionConstantsInterface.DYEXTN_A_006, attribute.getName());
			}
		}
		}
		
	}

	/**
	 * @param name
	 * @throws DynamicExtensionsApplicationException
	 */
	public static void validateName(String name) throws DynamicExtensionsApplicationException
	{
		/**
		 * Constant representing valid names 
		 */
		final String VALIDCHARSREGEX = "[^\\\\/:*?\"<>&;|']*";

		if (name == null || name.trim().length() == 0 || !name.matches(VALIDCHARSREGEX))
		{
			throw new DynamicExtensionsApplicationException("Object name invalid", null,
					EntityManagerExceptionConstantsInterface.DYEXTN_A_003);
		}
		if (name.trim().length() > 1000)
		{
			throw new DynamicExtensionsApplicationException("Object name exceeds maximum limit",
					null, EntityManagerExceptionConstantsInterface.DYEXTN_A_007);
		}
	}

	/**
	 * @param association
	 * @param entitySet
	 */
	public static void updateEntityReferences(AbstractAttributeInterface abstractAttribute)
	{

		if (abstractAttribute instanceof AttributeInterface)
		{
			return;
		}
		Set<EntityInterface> entitySet = new HashSet<EntityInterface>();
		entitySet.add(abstractAttribute.getEntity());
		getAssociatedEntities(abstractAttribute.getEntity(), entitySet);
		List<EntityInterface> entityList = new ArrayList<EntityInterface>(entitySet);

		AssociationInterface association = (AssociationInterface) abstractAttribute;
		EntityInterface targetEntity = association.getTargetEntity();
		if (entityList.contains(targetEntity))
		{
			association.setTargetEntity((EntityInterface) entityList.get(entityList
					.indexOf(targetEntity)));
			return;
		}
		for (AssociationInterface tagretEntityAssociation : targetEntity.getAssociationCollection())
		{
			EntityInterface entity = tagretEntityAssociation.getTargetEntity();
			if (entityList.contains(entity))
			{
				tagretEntityAssociation.setTargetEntity((EntityInterface) entityList.get(entityList
						.indexOf(entity)));
			}
		}
	}

	/**
	 * @param entity
	 * @param entitySet
	 */
	public static void getAssociatedEntities(EntityInterface entity, Set<EntityInterface> entitySet)
	{
		Collection<AssociationInterface> associationCollection = entity.getAssociationCollection();
		for (AssociationInterface associationInterface : associationCollection)
		{
			EntityInterface targetEntity = associationInterface.getTargetEntity();
			if (!entitySet.contains(targetEntity))
			{
				entitySet.add(targetEntity);
				getAssociatedEntities(targetEntity, entitySet);
			}
		}
	}
	
	/**
	 * This method checks if the date string is as per the given format or not. 
	 * @param dateFormat Format of the date (e.g. dd/mm/yyyy)
	 * @param strDate Date value in String.
	 * @return true if date is valid, false otherwise
	 */
	public static boolean isDateValid(String dateFormat, String strDate)
	{
		boolean isDateValid = false;
		Date date = null;

		try
		{
			date = Utility.parseDate(strDate, dateFormat);
			if (date != null)
			{
				isDateValid = true;
			}
		}
		catch (ParseException parseException)
		{
			isDateValid = false;
		}

		return isDateValid;
	}

	/**
	 * This method returns the format of the date depending upon the the type of the format selected on UI.
	 * @param format Selected format
	 * @return date format
	 */
	public static String getDateFormat(String format)
	{
		String dateFormat = null;
		if (format != null)
		{
			if (format.equals(ProcessorConstants.DATE_FORMAT_OPTION_DATEONLY))
			{
				dateFormat = ProcessorConstants.DATE_ONLY_FORMAT;
			}
			else
			{
				dateFormat = ProcessorConstants.DATE_TIME_FORMAT;
			}
		}
		else
		{
			dateFormat = ProcessorConstants.DATE_ONLY_FORMAT;
		}

		return dateFormat;
	}
	
	/**
	 * This method returns the sql format of the date depending upon the the type of the format of the Date Attribute.
	 * @param dateFormat format of the Date Attribute
	 * @return SQL date format
	 */
	public static String getSQLDateFormat(String dateFormat)
	{
		String sqlDateFormat = null;
		if (dateFormat != null)
		{
			if (dateFormat.equals(ProcessorConstants.DATE_ONLY_FORMAT))
			{
				sqlDateFormat = ProcessorConstants.SQL_DATE_ONLY_FORMAT;
			}
			else if (dateFormat.equals(ProcessorConstants.DATE_TIME_FORMAT))
			{
				sqlDateFormat = ProcessorConstants.SQL_DATE_TIME_FORMAT;
			}
		}
		else
		{
			sqlDateFormat = ProcessorConstants.SQL_DATE_ONLY_FORMAT;
		}

		return sqlDateFormat;
	}

}