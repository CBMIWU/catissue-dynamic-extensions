/**
 *
 */

package edu.common.dynamicextensions.util;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.script.Invocable;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

import edu.common.dynamicextensions.bizlogic.BizLogicFactory;
import edu.common.dynamicextensions.dao.impl.DynamicExtensionDAO;
import edu.common.dynamicextensions.dao.impl.DynamicExtensionDBFactory;
import edu.common.dynamicextensions.dao.impl.IDEDBUtility;
import edu.common.dynamicextensions.domain.Association;
import edu.common.dynamicextensions.domain.CategoryEntity;
import edu.common.dynamicextensions.domain.DateAttributeTypeInformation;
import edu.common.dynamicextensions.domain.DomainObjectFactory;
import edu.common.dynamicextensions.domain.Entity;
import edu.common.dynamicextensions.domain.EntityGroup;
import edu.common.dynamicextensions.domain.userinterface.AbstractContainmentControl;
import edu.common.dynamicextensions.domain.userinterface.Container;
import edu.common.dynamicextensions.domain.userinterface.ContainmentAssociationControl;
import edu.common.dynamicextensions.domain.userinterface.Control;
import edu.common.dynamicextensions.domain.userinterface.ListBox;
import edu.common.dynamicextensions.domaininterface.AbstractAttributeInterface;
import edu.common.dynamicextensions.domaininterface.AbstractEntityInterface;
import edu.common.dynamicextensions.domaininterface.AbstractMetadataInterface;
import edu.common.dynamicextensions.domaininterface.AssociationInterface;
import edu.common.dynamicextensions.domaininterface.AttributeInterface;
import edu.common.dynamicextensions.domaininterface.AttributeMetadataInterface;
import edu.common.dynamicextensions.domaininterface.AttributeTypeInformationInterface;
import edu.common.dynamicextensions.domaininterface.CategoryAssociationInterface;
import edu.common.dynamicextensions.domaininterface.CategoryEntityInterface;
import edu.common.dynamicextensions.domaininterface.EntityGroupInterface;
import edu.common.dynamicextensions.domaininterface.EntityInterface;
import edu.common.dynamicextensions.domaininterface.RoleInterface;
import edu.common.dynamicextensions.domaininterface.TaggedValueInterface;
import edu.common.dynamicextensions.domaininterface.databaseproperties.ConstraintKeyPropertiesInterface;
import edu.common.dynamicextensions.domaininterface.databaseproperties.ConstraintPropertiesInterface;
import edu.common.dynamicextensions.domaininterface.userinterface.AbstractContainmentControlInterface;
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
import edu.common.dynamicextensions.entitymanager.EntityManagerUtil;
import edu.common.dynamicextensions.exception.DynamicExtensionsApplicationException;
import edu.common.dynamicextensions.exception.DynamicExtensionsSystemException;
import edu.common.dynamicextensions.processor.ProcessorConstants;
import edu.common.dynamicextensions.ui.webui.util.UserInterfaceiUtility;
import edu.common.dynamicextensions.util.global.CategoryConstants;
import edu.common.dynamicextensions.util.global.Variables;
import edu.common.dynamicextensions.util.global.DEConstants.Cardinality;
import edu.common.dynamicextensions.util.global.DEConstants.InheritanceStrategy;
import edu.common.dynamicextensions.xmi.XMIConfiguration;
import edu.common.dynamicextensions.xmi.XMIConstants;
import edu.wustl.cab2b.server.cache.EntityCache;
import edu.wustl.common.beans.NameValueBean;
import edu.wustl.common.bizlogic.DefaultBizLogic;
import edu.wustl.common.exception.BizLogicException;
import edu.wustl.common.util.CVSTagReader;
import edu.wustl.common.util.Utility;
import edu.wustl.common.util.global.ApplicationProperties;
import edu.wustl.common.util.global.CommonServiceLocator;
import edu.wustl.common.util.logger.Logger;
import edu.wustl.common.util.logger.LoggerConfig;
import edu.wustl.dao.HibernateDAO;
import edu.wustl.dao.JDBCDAO;
import edu.wustl.dao.daofactory.DAOConfigFactory;
import edu.wustl.dao.exception.DAOException;

/**
 * @author chetan_patil
 *
 */
public class DynamicExtensionsUtility
{

	/**
	 * This method fetches the Control instance from the Database given the corresponding Control Identifier.
	 * @param controlIdentifier The identifier of the Control.
	 * @return the ControlInterface
	 * @throws DynamicExtensionsSystemException on System exception
	 * @throws DynamicExtensionsApplicationException on Application exception
	 */
	public static ControlInterface getControlByIdentifier(String controlIdentifier)
			throws DynamicExtensionsSystemException, DynamicExtensionsApplicationException
	{
		ControlInterface controlInterface = null;
		if (controlIdentifier != null && !"".equals(controlIdentifier))
		{
			controlInterface = EntityCache.getInstance().getControlById(
					Long.valueOf(controlIdentifier));
		}
		return controlInterface;
	}

	/**
	 * This method fetches the Container instance from the Database given the corresponding Container Identifier.
	 * @param containerIdentifier The identifier of the container.
	 * @return the ContainerInterface
	 * @throws DynamicExtensionsSystemException on System exception
	 * @throws DynamicExtensionsApplicationException on Application exception
	 */
	public static EntityGroupInterface getEntityGroupByIdentifier(String entityGroupIdentifier)
			throws DynamicExtensionsSystemException, DynamicExtensionsApplicationException
	{
		EntityGroupInterface entityGroupInterface = null;
		if (entityGroupIdentifier != null && !"".equals(entityGroupIdentifier))
		{
			entityGroupInterface = EntityCache.getInstance().getEntityGroupById(
					Long.valueOf(entityGroupIdentifier));
		}
		return entityGroupInterface;
	}

	/**
	 * This method fetches the Container instance from the Database given the corresponding Container Identifier.
	 * @param containerIdentifier The Identifier of the Container.
	 * @return the ContainerInterface
	 * @throws DynamicExtensionsSystemException on System exception
	 * @throws DynamicExtensionsApplicationException on Application exception
	 */
	public static ContainerInterface getContainerByIdentifier(String containerIdentifier)
			throws DynamicExtensionsSystemException, DynamicExtensionsApplicationException
	{
		ContainerInterface containerInterface = null;
		if (containerIdentifier != null && !"".equals(containerIdentifier))
		{
			containerInterface = EntityCache.getInstance().getContainerById(
					Long.valueOf(containerIdentifier));
		}
		return containerInterface;
	}

	/**
	 * This method fetches the Container instance from the Database given the corresponding Container Identifier.
	 * @param containerIdentifier The Identifier of the Container.
	 * @return the ContainerInterface
	 * @throws DynamicExtensionsSystemException on System exception
	 * @throws DynamicExtensionsApplicationException on Application exception
	 */
	public static ContainerInterface getContainerByIdentifier(String containerIdentifier,
			ContainerInterface mainContainer) throws DynamicExtensionsSystemException,
			DynamicExtensionsApplicationException
	{
		ContainerInterface containerInterface = null;
		if (containerIdentifier != null && mainContainer.getId() != null)
		{
			if (containerIdentifier.equals(mainContainer.getId().toString()))
			{
				containerInterface = mainContainer;
			}
			else
			{
				AbstractContainmentControlInterface containmentControl = UserInterfaceiUtility
						.getAssociationControl(mainContainer, containerIdentifier);
				containerInterface = containmentControl.getContainer();
			}
		}
		return containerInterface;
	}

	/**
	 * This method fetches the Container instance from the Database given the corresponding Container Identifier.
	 * @param containerIdentifier The Identifier of the Container.
	 * @return the ContainerInterface
	 * @throws DynamicExtensionsSystemException on System exception
	 * @throws DynamicExtensionsApplicationException on Application exception
	 */
	public static AttributeInterface getAttributeByIdentifier(String attributeIdentifier)
			throws DynamicExtensionsSystemException, DynamicExtensionsApplicationException
	{
		AttributeInterface attributeInterface = null;
		if (attributeIdentifier != null && !"".equals(attributeIdentifier))
		{
			attributeInterface = EntityCache.getInstance().getAttributeById(
					Long.valueOf(attributeIdentifier));
		}
		return attributeInterface;
	}

	/**
	 * It will verify weather the Inherited Tag is present on the given attribute parameter or not & will return boolean accordingly
	 * @param attribute to check for taggedValue
	 * @return true if "Inherited" tagged value present.
	 */
	public static boolean isInheritedTaggPresent(AttributeInterface attribute)
	{
		Collection<TaggedValueInterface> taggValueColl = attribute.getTaggedValueCollection();
		boolean isPresent = false;
		for (TaggedValueInterface taggedValue : taggValueColl)
		{
			if (taggedValue.getKey().equals(XMIConstants.TAGGED_VALUE_INHERITED))
			{
				isPresent = true;
				break;
			}
		}
		return isPresent;
	}

	/**
	 * It will add the taggedValue to the given attribute in parameter with key "derived" and its value also"derived"
	 * @param attribute
	 */
	public static void addInheritedTaggedValue(AttributeInterface attribute)
	{
		if (!DynamicExtensionsUtility.isInheritedTaggPresent(attribute))
		{
			TaggedValueInterface taggedValue = new edu.common.dynamicextensions.domain.TaggedValue();
			taggedValue.setKey(XMIConstants.TAGGED_VALUE_INHERITED);
			taggedValue.setValue(XMIConstants.TAGGED_VALUE_INHERITED);
			attribute.addTaggedValue(taggedValue);
		}
	}

	/**
	 * This method clears data value for all controls within container
	 * @param baseContainerObject Container Object
	 */
	public static void cleanContainerControlsValue(ContainerInterface baseContainerObject)
	{
		ContainerInterface baseContainer = baseContainerObject;
		while (baseContainer != null)
		{
			Collection controls = baseContainer.getControlCollection();

			for (Iterator iterator = controls.iterator(); iterator.hasNext();)
			{
				Control objControl = (Control) iterator.next();
				if (objControl instanceof AbstractContainmentControl)
				{
					ContainerInterface subContainer = ((AbstractContainmentControl) objControl)
							.getContainer();
					if (subContainer != null)
					{
						subContainer.getContainerValueMap().clear();
						cleanContainerControlsValue(subContainer);
					}

				}
				objControl.setValue(null);
			}

			baseContainer = baseContainer.getBaseContainer();
		}

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
			if (controlInterface instanceof AbstractContainmentControl)
			{
				return ProcessorConstants.ADD_SUBFORM_CONTROL;
			}
		}
		return null;
	}

	/**
	 *
	 * @param controlCollection
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

	/**
	 *
	 * @param abstractAttribute
	 * @param containerInterface
	 * @return
	 */
	public static ControlInterface getControlForAbstractAttribute(
			AttributeMetadataInterface attributeMetadataInterface,
			ContainerInterface containerInterface)
	{
		ControlInterface control = null;
		Collection<ControlInterface> controlCollection = containerInterface.getAllControls();
		for (ControlInterface controlInterface : controlCollection)
		{
			if (controlInterface instanceof AbstractContainmentControlInterface)
			{
				control = getControlForAbstractAttribute(attributeMetadataInterface,
						((AbstractContainmentControlInterface) controlInterface).getContainer());
				if (control != null)
				{
					return control;
				}
			}
			else if (controlInterface.getBaseAbstractAttribute() != null
						&& controlInterface.getBaseAbstractAttribute().getId() != null
						&& controlInterface.getBaseAbstractAttribute().equals(attributeMetadataInterface))
			{
						control = controlInterface;
						break;
			}
		}
		return control;

	}
	/**
	 *
	 * @param abstractAttribute
	 * @param containerInterface
	 * @return
	 */
	public static ControlInterface getControlForAbstractAttribute(
			AttributeMetadataInterface attributeMetadataInterface,
			ContainerInterface containerInterface,String categoryEntityName)
	{
		ControlInterface control = null;
		Collection<ControlInterface> controlCollection = containerInterface.getAllControls();
		for (ControlInterface controlInterface : controlCollection)
		{
			if (controlInterface instanceof AbstractContainmentControlInterface)
			{
				control = getControlForAbstractAttribute(attributeMetadataInterface,
						((AbstractContainmentControlInterface) controlInterface).getContainer());
				if (control != null)
				{
					return control;
				}
			}
			else if (controlInterface.getBaseAbstractAttribute() != null)
			{
				if (controlInterface.getBaseAbstractAttribute().getId() != null)
				{
					if (controlInterface.getBaseAbstractAttribute().equals(
							attributeMetadataInterface))
					{
						control = controlInterface;
						break;
					}
				}
				else
				{
					if (controlInterface.getParentContainer()
							.getAbstractEntity().getName() != null
							&& controlInterface.getParentContainer()
									.getAbstractEntity().getName().equals(
											categoryEntityName)
							&& controlInterface.getBaseAbstractAttribute()
									.getName().equals(
											attributeMetadataInterface
													.getName()))
					{
						control = controlInterface;
						break;
					}
				}
			}
		}
		return control;

	}

	/**
	 *
	 * @param controlIdentifier
	 * @param containerInterface
	 * @return
	 */
	public static ControlInterface getControlByIdentifier(String controlIdentifier,
			ContainerInterface containerInterface)
	{
		ControlInterface control = null;
		Collection<ControlInterface> controlCollection = containerInterface.getAllControls();
		for (ControlInterface controlInterface : controlCollection)
		{
			if (controlInterface instanceof AbstractContainmentControlInterface)
			{
				control = getControlByIdentifier(controlIdentifier,
						((AbstractContainmentControlInterface) controlInterface).getContainer());
				if (control != null)
				{
					return control;
				}
			}
			else if (controlInterface.getId() != null
					&& controlInterface.getId().toString().equals(controlIdentifier))
			{
				control = controlInterface;
				break;

			}
		}
		return control;

	}

	/**
	 *
	 * @return
	 */
	public static ContainerInterface getContainerForAbstractEntity(
			AbstractEntityInterface abstractEntityInterface)
	{
		ContainerInterface containerInterface = null;
		List<ContainerInterface> containerList = new ArrayList<ContainerInterface>();
		containerList.addAll(abstractEntityInterface.getContainerCollection());
		if (!containerList.isEmpty())
		{
			containerInterface = containerList.get(0);
		}
		return containerInterface;
	}

	/**
	 * @param controlCollection collection of controls
	 * @param sequenceNumber sequence number
	 * @return control
	 */
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
	 * initialize application variables
	 */
	public static void initialiseApplicationVariables()
	{
		if (Logger.out == null)
		{
			LoggerConfig.configureLogger("");
		}
	}

	/**
	 *
	 */
	public static void initialiseApplicationInfo()
	{

		CommonServiceLocator serviceLocator = CommonServiceLocator.getInstance();
		String fileName = Variables.dynamicExtensionsHome + System.getProperty("file.separator")
				+ ApplicationProperties.getValue("application.version.file");
		CVSTagReader cvsTagReader = new CVSTagReader();
		String cvsTag = cvsTagReader.readTag(fileName);
		Variables.applicationCvsTag = cvsTag;
		Logger.out.info("========================================================");
		Logger.out.info("Application Information");
		Logger.out.info("-----------------------");
		Logger.out.info("Name : " + DynamicExtensionDAO.getInstance().getAppName());
		Logger.out.info("CVS TAG : " + Variables.applicationCvsTag);
		Logger.out.info("Path : " + serviceLocator.getAppHome());
		Logger.out.info("========================================================");

		try
		{
			Logger.out.info(" ");
			Logger.out.info("Preloading the DE metadata....this may take a few minutes!!");
			Logger.out.info(" ");
		}
		catch (Exception e)
		{
			throw new RuntimeException(e);
		}
	}

	/**
	 * @param abstractAttributeInterface
	 * @return attributeTypeInformation
	 */
	public static AttributeTypeInformationInterface getAttributeTypeInformation(
			AbstractAttributeInterface abstractAttributeInterface)
	{
		AttributeTypeInformationInterface attributeTypeInformation = null;
		if (abstractAttributeInterface instanceof AttributeInterface)
		{
			attributeTypeInformation = ((AttributeInterface) abstractAttributeInterface)
					.getAttributeTypeInformation();
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
	 * @param numString Number as string.
	 * @return boolean true if numeric else false.
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

	/**
	 * @return day form Calendar.
	 */
	public static int getCurrentDay()
	{
		return Calendar.getInstance().get(Calendar.DAY_OF_MONTH);
	}

	/**
	 * @return current month.
	 */
	public static int getCurrentMonth()
	{
		return (Calendar.getInstance().get(Calendar.MONTH) + 1);
	}

	/**
	 * @return current year.
	 */
	public static int getCurrentYear()
	{
		return Calendar.getInstance().get(Calendar.YEAR);
	}

	/**
	 * @return current Hour.
	 */
	public static int getCurrentHours()
	{
		return Calendar.getInstance().get(Calendar.HOUR);
	}

	/**
	 * @return current minutes.
	 */
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
		catch (IOException e)
		{
			Logger.out.error(e);
		}
		catch (ClassNotFoundException e)
		{
			Logger.out.error(e);
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
	public static void sortNameValueBeanListByName(List<NameValueBean> list)
	{
		Collections.sort(list, new Comparator()
		{

			public int compare(Object object1, Object object2)
			{
				String obj1Name = "";
				String obj2Name = "";
				if (object1 != null)
				{
					obj1Name = ((NameValueBean) object1).getName();
				}
				if (object2 != null)
				{
					obj2Name = ((NameValueBean) object2).getName();
				}
				return obj1Name.compareTo(obj2Name);
			}
		});
	}

	/**
	 * @param entity Entity Object
	 * @return entityGroup of a entity
	 */
	public static EntityGroupInterface getEntityGroup(EntityInterface entity)
	{
		EntityGroupInterface entityGroup = null;
		if (entity != null)
		{
			//			Collection<EntityGroupInterface> entityGroupCollection = entity
			//					.getEntityGroupCollection();
			//			if (entityGroupCollection != null)
			//			{
			//				Iterator<EntityGroupInterface> entityGroupIter = entityGroupCollection.iterator();
			//				if (entityGroupIter.hasNext())
			//				{
			entityGroup = entity.getEntityGroup();
			//				}
			//			}
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
							integer = Integer.valueOf(str);
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
		//This validation is already in place in ApplyFormControlsProcessor
		//(entity, entity.getName());

		if (entity.getInheritanceStrategy().equals(InheritanceStrategy.TABLE_PER_HEIRARCHY)
				&& entity.getParentEntity() != null)
		{
			if (entity.getDiscriminatorColumn() == null
					|| entity.getDiscriminatorColumn().equals(""))
			{
				throw new DynamicExtensionsApplicationException(
						"Discriminator Column and value is required for TABLE_PER_HEIRARCHY strategy",
						null, EntityManagerExceptionConstantsInterface.DYEXTN_A_012);

			}

			if (entity.getDiscriminatorValue() == null || entity.getDiscriminatorValue().equals(""))
			{
				throw new DynamicExtensionsApplicationException(
						"Discriminator Column and value is required for TABLE_PER_HEIRARCHY strategy",
						null, EntityManagerExceptionConstantsInterface.DYEXTN_A_012);
			}
		}
	}

	/**
	 * @param entity Entity Object
	 * @param attributeName attribute name
	 * @throws DynamicExtensionsApplicationException
	 */
	public static void validateDuplicateNamesWithinEntity(EntityInterface entity,
			String attributeName) throws DynamicExtensionsApplicationException
	{
		Collection<AbstractAttributeInterface> collection = entity.getAbstractAttributeCollection();
		if (collection != null || !collection.isEmpty())
		{
			for (AbstractAttributeInterface attribute : collection)
			{
				if (attribute.getName().equals(attributeName))
				{
					throw new DynamicExtensionsApplicationException(
							"Attribute names should be unique for the entity ", null,
							EntityManagerExceptionConstantsInterface.DYEXTN_A_006);

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
	 * @param abstractAttribute abstract attribute
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
			association.setTargetEntity(entityList.get(entityList.indexOf(targetEntity)));
			return;
		}
		for (AssociationInterface tagretEntityAssociation : targetEntity.getAssociationCollection())
		{
			EntityInterface entity = tagretEntityAssociation.getTargetEntity();
			if (entityList.contains(entity))
			{
				tagretEntityAssociation.setTargetEntity(entityList.get(entityList.indexOf(entity)));
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
	 * @param entityGroup
	 * @return entity collection
	 */
	public static List<EntityInterface> getUnsavedEntities(EntityGroupInterface entityGroup)
	{
		List<EntityInterface> entities = new ArrayList<EntityInterface>();

		Collection<EntityInterface> entitiesInGrp = entityGroup.getEntityCollection();
		for (EntityInterface entity : entitiesInGrp)
		{
			if (entity.getId() == null)
			{
				entities.add(entity);
			}
		}

		return entities;
	}

	/**
	 * @param entityGroup
	 * @param entitySet
	 */
	public static List<EntityInterface> getSavedEntities(EntityGroupInterface entityGroup)
	{
		List<EntityInterface> entities = new ArrayList<EntityInterface>();

		Collection<EntityInterface> entitiesInGrp = entityGroup.getEntityCollection();
		for (EntityInterface entity : entitiesInGrp)
		{
			if (entity.getId() != null)
			{
				entities.add(entity);
			}
		}

		return entities;
	}

	/**
	 * This method checks if the date string is as per the given format or not.
	 * @param dateFormat Format of the date (e.g. MM-DD-YYYY)
	 * @param strAsDate Date value in String.
	 * @return true if date is valid, false otherwise
	 */
	public static boolean isDateValid(String dateFormat, String strAsDate)
	{
		boolean isDateValid = false;
		Date date = null;
		String strDate = strAsDate;
		if (dateFormat.equals(ProcessorConstants.MONTH_YEAR_FORMAT))
		{
			strDate = formatMonthAndYearDate(strDate, false);
			//09-12-2007 0:0
		}
		if (dateFormat.equals(ProcessorConstants.YEAR_ONLY_FORMAT))
		{
			strDate = formatYearDate(strDate, false);
			//09-12-2007 0:0
		}

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
	 * @param strDate start date
	 * @param removeTime
	 * @return formated date
	 */
	public static String formatMonthAndYearDate(String strDate, boolean removeTime)
	{
		String appName = DynamicExtensionDAO.getInstance().getAppName();
		String dbType = DAOConfigFactory.getInstance().getDAOFactory(appName).getDataBaseType();
		IDEDBUtility dbUtility = DynamicExtensionDBFactory.getInstance().getDbUtility(dbType);
		return dbUtility.formatMonthAndYearDate(strDate, removeTime);
	}

	/**
	 * @param strDate
	 * @param removeTime
	 * @return formated date
	 */
	public static String formatYearDate(String strDate, boolean removeTime)
	{
		String appName = DynamicExtensionDAO.getInstance().getAppName();
		String dbType = DAOConfigFactory.getInstance().getDAOFactory(appName).getDataBaseType();
		return DynamicExtensionDBFactory.getInstance().getDbUtility(dbType).formatYearDate(strDate,
				removeTime);
	}

	/**
	 * This method determines whether the checkbox is to be checked or not.
	 * @param value the value particular to database
	 * for e.g. oracle - "1" or "0"
	 * mysql "true" or "false"
	 * @return true if checked , false otherwise
	 */
	public static boolean isCheckBoxChecked(String value)
	{
		boolean isChecked = false;
		if ((value != null && value.trim().length() > 0)
				&& (value.equals("1") || value.equals("true")))
		{
			isChecked = true;
		}
		return isChecked;
	}

	/**
	 * This method returns the value for checkbox depending on database.
	 * for e.g. oracle - "1" or "0"
	 * mysql "true" or "false"
	 * @param ischecked
	 * @return string value which is assigned to checkbox value
	 */
	public static String getValueForCheckBox(boolean ischecked)
	{
		String appName = DynamicExtensionDAO.getInstance().getAppName();
		String dbType = DAOConfigFactory.getInstance().getDAOFactory(appName).getDataBaseType();
		return DynamicExtensionDBFactory.getInstance().getDbUtility(dbType).getValueForCheckBox(
				ischecked);
	}

	/**
	 * This method returns the html keyword checked for checkbox selection
	 * @param ischecked
	 * @return 'checked' string or empty string
	 */
	public static String getCheckboxSelectionValue(String value)
	{
		String checkboxValue = "";
		if (value != null && value.trim().length() > 0
				&& value.equalsIgnoreCase(getValueForCheckBox(true)))
		{
			checkboxValue = "checked";
		}
		return checkboxValue;
	}

	/**
	 * This method compares the two date strings.
	 * @param strDate1 the first date value.
	 * @param strDate2 the second date value.
	 * @param dateFormat the format of both date.
	 * @return -1 if date1 is lesser than date2
	 * 			0 if date1 is equals to date2
	 * 			1 if date1 is greater than date2.
	 */
	public static int compareDates(String strDate1, String strDate2, String dateFormat)
	{
		int result = 0;
		String date1 = strDate1;
		String date2 = strDate2;
		if (areBothDatesOfSameFormat(date1, date2))
		{
			result = 1;
			return result;
		}

		if (dateFormat.equals(ProcessorConstants.MONTH_YEAR_FORMAT))
		{
			if (Integer.parseInt(date1.substring(3, date1.length()).trim()) > Integer
					.parseInt(date2.substring(3, date2.length()).trim()))
			{
				result = 1;
				return result;
			}
			date1 = formatMonthAndYearDate(date1, false);
			date2 = formatMonthAndYearDate(date2, false);
			//09-12-2007 0:0
		}

		if (dateFormat.equals(ProcessorConstants.YEAR_ONLY_FORMAT)
				&& Integer.parseInt(date1) > Integer.parseInt(date2))
		{
			//date1 = formatYearDate(date1);
			//date2 = formatYearDate(date2);
			result = 1;
			return result;
			//09-12-2007 0:0
		}

		try
		{
			// Fix to support different formats in DE :Pavan.
			date1 = date1.replace('/', '-');
			date2 = date2.replace('/', '-');
			Date firstDate = Utility.parseDate(date1, "MM-dd-yyyy");
			Date secondDate = Utility.parseDate(date2, "MM-dd-yyyy");
			if (firstDate.after(secondDate))
			{
				result = 1;
			}
			else if (firstDate.before(secondDate))
			{
				result = -1;
			}
		}
		catch (ParseException parseException)
		{
			result = -2;
		}

		return result;
	}

	/**
	 * @param date1 date in string format
	 * @param date2 date in string format
	 * @return
	 */
	public static boolean areBothDatesOfSameFormat(String date1, String date2)
	{
		boolean returnValue = false;
		if (date1.length() != date2.length())
		{
			returnValue = true;
		}
		return returnValue;
	}

	/**
	 * This method returns the format of the date depending upon the the type of the format selected on UI.
	 * @param format Selected format
	 * @return date format
	 */
	public static String getDateFormat(String format)
	{
		String dateFormat = ProcessorConstants.DATE_ONLY_FORMAT;
		if (format != null && format.equals(ProcessorConstants.DATE_FORMAT_OPTION_DATEANDTIME))
		{
			dateFormat = ProcessorConstants.DATE_TIME_FORMAT;
		}
		if (format != null && format.equals(ProcessorConstants.DATE_FORMAT_OPTION_MONTHANDYEAR))
		{
			dateFormat = ProcessorConstants.MONTH_YEAR_FORMAT;
		}
		if (format != null && format.equals(ProcessorConstants.DATE_FORMAT_OPTION_YEARONLY))
		{
			dateFormat = ProcessorConstants.YEAR_ONLY_FORMAT;
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
		CommonServiceLocator locator = CommonServiceLocator.getInstance();
		StringBuffer sqlDateFormat = new StringBuffer(locator.getDatePattern());
		if (dateFormat != null && dateFormat.equals(ProcessorConstants.DATE_TIME_FORMAT))
		{
			sqlDateFormat.append(' ').append(locator.getTimePattern());
		}
		return sqlDateFormat.toString();
	}

	/**
	 * @param caption
	 * @return
	 * @throws DynamicExtensionsSystemException
	 */
	public static ContainerInterface getContainerByCaption(String caption)
			throws DynamicExtensionsSystemException
	{
		DefaultBizLogic defaultBizLogic = BizLogicFactory.getDefaultBizLogic();
		defaultBizLogic.setAppName(DynamicExtensionDAO.getInstance().getAppName());
		List objectList = new ArrayList();
		ContainerInterface containerInterface = null;
		if (caption == null || caption.trim().length() == 0)
		{
			return null;
		}
		try
		{
			objectList = defaultBizLogic.retrieve(Container.class.getName(), "caption", caption);
		}
		catch (BizLogicException e)
		{
			throw new DynamicExtensionsSystemException(e.getMessage(), e);
		}

		if (!objectList.isEmpty())
		{
			containerInterface = (ContainerInterface) objectList.get(0);
		}

		return containerInterface;
	}

	/**
	 * @param containerColl
	 * @return
	 */
	public static List<String> getMainContainerNamesList(
			Collection<ContainerInterface> containerColl)
	{
		List<String> mainContainerNames = new ArrayList<String>();
		for (ContainerInterface container : containerColl)
		{
			mainContainerNames.add(container.getCaption());
		}
		return mainContainerNames;
	}

	/**
	 * This method corrects cardinalities such that max cardinality  < minimum cardinality ,otherwise it throws exception
	 * @param entity
	 */
	private static void correctCardinalities(EntityInterface entity)
			throws DynamicExtensionsApplicationException
	{
		Collection associationCollection = entity.getAssociationCollection();
		if (associationCollection != null && !associationCollection.isEmpty())
		{
			Iterator iterator = associationCollection.iterator();
			while (iterator.hasNext())
			{
				Association association = (Association) iterator.next();
				swapCardinality(association.getSourceRole());
				swapCardinality(association.getTargetRole());
			}
		}
	}

	/**
	 * @param role
	 * @throws DynamicExtensionsApplicationException
	 */
	private static void swapCardinality(RoleInterface role)
			throws DynamicExtensionsApplicationException
	{
		// make Min cardinality < Max cardinality
		if (role.getMinimumCardinality().equals(Cardinality.MANY)
				|| role.getMaximumCardinality().equals(Cardinality.ZERO))
		{
			Cardinality cardinality = role.getMinimumCardinality();
			role.setMinimumCardinality(role.getMaximumCardinality());
			role.setMaximumCardinality(cardinality);
		}

		if (role.getMaximumCardinality().equals(Cardinality.ZERO))
		{
			throw new DynamicExtensionsApplicationException("Cardinality constraint violated",
					null, EntityManagerExceptionConstantsInterface.DYEXTN_A_005);
		}
	}

	/**
	 * This method processes entity before saving it to database.
	 * <li> It validates entity for duplicate name of entity,attributes and association
	 * <li> It sets created and updated date-time.
	 *
	 * @param entity entity
	 */
	public static void validateEntity(EntityInterface entity)
			throws DynamicExtensionsApplicationException
	{
		validateEntityForSaving(entity);// chk if entity is valid or not.

		correctCardinalities(entity); // correct the cardinality if max cardinality  < min cardinality

		//        if (entity.getId() != null)
		//        {
		//            entity.setLastUpdated(new Date());
		//        }
		//        else
		//        {
		//            entity.setCreatedDate(new Date());
		//            entity.setLastUpdated(entity.getCreatedDate());
		//        }
	}

	/**
	 * @param association
	 * @return table name
	 */
	public static String getTableName(AssociationInterface association)
	{
		String tableName = "";
		RoleInterface sourceRole = association.getSourceRole();
		RoleInterface targetRole = association.getTargetRole();
		Cardinality sourceMaxCardinality = sourceRole.getMaximumCardinality();
		Cardinality targetMaxCardinality = targetRole.getMaximumCardinality();
		if (sourceMaxCardinality == Cardinality.MANY && targetMaxCardinality == Cardinality.MANY)
		{
			tableName = association.getConstraintProperties().getName();
		}
		else if (sourceMaxCardinality == Cardinality.MANY
				&& targetMaxCardinality == Cardinality.ONE)
		{
			tableName = association.getEntity().getTableProperties().getName();
		}
		else
		{
			tableName = association.getTargetEntity().getTableProperties().getName();
		}
		return tableName;
	}

	/**
	 * Retrieve entity group by its name from database.
	 * @param name name of category
	 * @return entity group
	 */
	public static EntityGroupInterface retrieveEntityGroup(String name)
	{
		//DefaultBizLogic bizlogic = new DefaultBizLogic();
		Collection<EntityGroupInterface> entityGroupCollection = new HashSet<EntityGroupInterface>();
		EntityGroupInterface entityGroup = null;
		DefaultBizLogic bizlogic = BizLogicFactory.getDefaultBizLogic();
		bizlogic.setAppName(DynamicExtensionDAO.getInstance().getAppName());
		Logger.out.info("-retrieveEntityGroup APP NAME-----"
				+ DynamicExtensionDAO.getInstance().getAppName());
		bizlogic.setAppName(DynamicExtensionDAO.getInstance().getAppName());
		//

		try
		{
			// Fetch the entity group from the database.
			entityGroupCollection = bizlogic.retrieve(EntityGroup.class.getName(), "shortName",
					name);

			if (entityGroupCollection != null && !entityGroupCollection.isEmpty())
			{
				entityGroup = entityGroupCollection.iterator().next();
			}
		}
		catch (BizLogicException e)
		{
			Logger.out.debug(e.getMessage(), e);

		}

		return entityGroup;
	}

	/**
	 * Method to check if data type is numeric i.e long,integer,short,float,double
	 * @param dataType
	 * @return true if data type is numeric
	 */
	public static boolean isDataTypeNumeric(String dataType)
	{
		boolean isDataTypeNumber = false;
		if (dataType.equals(ProcessorConstants.DATATYPE_SHORT)
				|| dataType.equals(ProcessorConstants.DATATYPE_INTEGER)
				|| dataType.equals(ProcessorConstants.DATATYPE_LONG)
				|| dataType.equals(ProcessorConstants.DATATYPE_FLOAT)
				|| dataType.equals(ProcessorConstants.DATATYPE_DOUBLE)
				|| dataType.equals(ProcessorConstants.DATATYPE_NUMBER))
		{
			isDataTypeNumber = true;
		}

		return isDataTypeNumber;
	}

	/**
	 * @param categoryEntity
	 * @param objCategoryMap
	 * @throws DynamicExtensionsSystemException
	 */
	public static void getUnsavedCategoryEntityList(CategoryEntityInterface categoryEntity,
			HashMap<String, CategoryEntityInterface> objCategoryMap)
			throws DynamicExtensionsSystemException
	{
		if (categoryEntity != null)
		{
			if (objCategoryMap.containsKey(categoryEntity.getName()))
			{
				return;
			}
			CategoryEntity objCategoryEntity = (CategoryEntity) categoryEntity;
			if (objCategoryEntity.getParentCategoryEntity() != null
					&& !objCategoryMap.containsKey(objCategoryEntity.getParentCategoryEntity()
							.getName())
					&& ((CategoryEntity) objCategoryEntity.getParentCategoryEntity())
							.isCreateTable())
			{
				getUnsavedCategoryEntityList(objCategoryEntity.getParentCategoryEntity(),
						objCategoryMap);
			}
			if (!objCategoryMap.containsKey(categoryEntity.getName())
					&& objCategoryEntity.isCreateTable())
			{
				if (objCategoryEntity.getId() == null && objCategoryEntity.isCreateTable())
				{
					//Only includes those category entity for which table is required to be created
					objCategoryMap.put(categoryEntity.getName(), objCategoryEntity);
				}
			}
			else
			{
				return;
			}
			for (CategoryAssociationInterface categoryAssociationInterface : categoryEntity
					.getCategoryAssociationCollection())
			{
				CategoryEntity objCEntity = (CategoryEntity) categoryAssociationInterface
						.getTargetCategoryEntity();
				if (objCEntity != null
						&& objCEntity.isCreateTable()
						&& !objCategoryMap.containsKey(categoryAssociationInterface
								.getTargetCategoryEntity().getName()))
				{
					getUnsavedCategoryEntityList(categoryAssociationInterface
							.getTargetCategoryEntity(), objCategoryMap);
				}
			}
		}
	}

	/**
	 * @param categoryEntity
	 * @param objCategoryMap
	 * @throws DynamicExtensionsSystemException
	 */
	public static void getSavedCategoryEntityList(CategoryEntityInterface categoryEntity,
			HashMap<String, CategoryEntityInterface> objCategoryMap)
			throws DynamicExtensionsSystemException
	{
		if (categoryEntity != null)
		{
			if (objCategoryMap.containsKey(categoryEntity.getName()))
			{
				return;
			}
			CategoryEntity objCategoryEntity = (CategoryEntity) categoryEntity;
			if (categoryEntity.getParentCategoryEntity() != null
					&& !objCategoryMap.containsKey(categoryEntity.getParentCategoryEntity()
							.getName())
					&& ((CategoryEntity) objCategoryEntity.getParentCategoryEntity())
							.isCreateTable())
			{
				getSavedCategoryEntityList(categoryEntity.getParentCategoryEntity(), objCategoryMap);
			}
			if (!objCategoryMap.containsKey(categoryEntity.getName())
					&& objCategoryEntity.getId() != null && objCategoryEntity.isCreateTable())
			{
				objCategoryMap.put(categoryEntity.getName(), categoryEntity);
			}
			else
			{
				return;
			}
			for (CategoryAssociationInterface categoryAssociationInterface : categoryEntity
					.getCategoryAssociationCollection())
			{
				CategoryEntity objCEntity = (CategoryEntity) categoryAssociationInterface
						.getTargetCategoryEntity();
				if (objCEntity != null
						&& objCEntity.isCreateTable()
						&& objCEntity.getId() != null
						&& !objCategoryMap.containsKey(categoryAssociationInterface
								.getTargetCategoryEntity().getName()))
				{
					getSavedCategoryEntityList(categoryAssociationInterface
							.getTargetCategoryEntity(), objCategoryMap);
				}
			}
		}
	}

	/**
	 * This method populates the constraint properties for the childEntity
	 * @param childEntity whose constraint properties is to be updated
	 * @param isAddColumnForInheritance
	 * @throws DynamicExtensionsSystemException
	 */
	public static void getConstraintKeyPropertiesForInheritance(EntityInterface childEntity,
			boolean isAddColumnForInheritance) throws DynamicExtensionsSystemException
	{
		EntityInterface parentEntity = childEntity.getParentEntity();
		Long identifier = childEntity.getId();
		Entity dbaseCopy;
		if (identifier == null && parentEntity != null)
		{
			getConstraintKeyProperties(childEntity, parentEntity, isAddColumnForInheritance);
		}
		else if (identifier != null)
		{
			try
			{
				dbaseCopy = (Entity) DynamicExtensionsUtility.getCleanObject(Entity.class
						.getCanonicalName(), identifier);
			}
			catch (DAOException e)
			{
				throw new DynamicExtensionsSystemException(e.getMessage(), e);
			}
			if (EntityManagerUtil.isParentChanged((Entity) childEntity, dbaseCopy)
					|| EntityManagerUtil.isPrimaryKeyChanged(parentEntity))
			{
				getConstraintKeyProperties(childEntity, parentEntity, isAddColumnForInheritance);
			}
		}

	}

	/**
	 * This method populates the constraint properties of the child depending on the parentEntity primary key
	 * @param childEntity whose constraintProperties is to be populated
	 * @param parentEntity
	 * @param isAddColumnForInheritance
	 * @throws DynamicExtensionsSystemException
	 */
	private static void getConstraintKeyProperties(EntityInterface childEntity,
			EntityInterface parentEntity, boolean isAddColumnForInheritance)
			throws DynamicExtensionsSystemException
	{
		DomainObjectFactory factory = DomainObjectFactory.getInstance();
		Collection<ConstraintKeyPropertiesInterface> cnstrKeyProp = childEntity
				.getConstraintProperties().getSrcEntityConstraintKeyPropertiesCollection();
		ConstraintKeyPropertiesInterface primaryCnstrKeyProp = null;
		cnstrKeyProp.clear();
		if (parentEntity == null)
		{
			Logger.out.info("");
			//do nothing as parent entity is null
		}
		else if ((EntityManagerUtil.isIdAttributePresent(parentEntity) && EntityManagerUtil
				.isIdAttributePresent(childEntity))
				&& !isAddColumnForInheritance)
		{
			AttributeInterface parentIdAtt = parentEntity.getAttributeByName("id");
			primaryCnstrKeyProp = factory.createConstraintKeyProperties(parentIdAtt
					.getColumnProperties().getName());
			primaryCnstrKeyProp.setSrcPrimaryKeyAttribute(parentIdAtt);
			cnstrKeyProp.add(primaryCnstrKeyProp);
		}
		else
		{
			/*Collection<AttributeInterface> parentPrmAttrColl = parentEntity
					.getPrimaryKeyAttributeCollection();
			if (isPrimaryKeyAttributeCollectionEmpty(parentPrmAttrColl))
			{
				throw new DynamicExtensionsSystemException("Parent entity "
						+ parentEntity.getName()
						+ " does not contain any primary key child entity is "
						+ childEntity.getName());
			}*/
			for (AttributeInterface attribute : parentEntity.getPrimaryKeyAttributeCollection())
			{
				primaryCnstrKeyProp = factory.createConstraintKeyProperties();
				primaryCnstrKeyProp.setSrcPrimaryKeyAttribute(attribute);
				cnstrKeyProp.add(primaryCnstrKeyProp);
			}

		}

	}

	/**
	 * This method sets the constraintProperties of the association depending
	 * on weather it is edited one or not
	 *
	 * @param association
	 * @return ConstraintPropertiesInterface
	 * @throws DynamicExtensionsSystemException
	 */
	public static ConstraintPropertiesInterface getConstraintPropertiesForAssociation(
			AssociationInterface association) throws DynamicExtensionsSystemException
	{
		Long identifier = association.getId();
		ConstraintPropertiesInterface constraintProperties = association.getConstraintProperties();
		if (identifier == null)
		{
			constraintProperties = getConstraintKeyPropertiesForAssociation(association);

		}
		else
		{
			AssociationInterface dbaseCopy;
			try
			{
				dbaseCopy = (AssociationInterface) DynamicExtensionsUtility.getCleanObject(
						Association.class.getCanonicalName(), identifier);
			}
			catch (DAOException e)
			{
				throw new DynamicExtensionsSystemException(e.getMessage(), e);
			}

			if (EntityManagerUtil.isCardinalityChanged(association, dbaseCopy)
					|| EntityManagerUtil.isPrimaryKeyChanged(association.getEntity())
					|| EntityManagerUtil.isPrimaryKeyChanged(association.getTargetEntity()))
			{
				association.getConstraintProperties()
						.getSrcEntityConstraintKeyPropertiesCollection().clear();
				association.getConstraintProperties()
						.getTgtEntityConstraintKeyPropertiesCollection().clear();
				constraintProperties = getConstraintKeyPropertiesForAssociation(association);
			}
		}

		return constraintProperties;
	}

	/**
	 * It will verify weather the attributeColl is not empty
	 * @param attributeColl collection of attribute to check
	 * @throws DynamicExtensionsSystemException if attributeColl is Empty
	 */
	private static boolean isPrimaryKeyAttributeCollectionEmpty(
			Collection<AttributeInterface> attributeColl) throws DynamicExtensionsSystemException
	{
		boolean isEmpty = false;
		if (attributeColl.isEmpty())
		{
			isEmpty = true;
		}
		return isEmpty;
	}

	/**
	 * This method sets the constraintProperties of the association depending on
	 * whether the association is one-to-one, one-to-many or many-to-one.
	 * @param association
	 * @return
	 * @throws DynamicExtensionsSystemException
	 */
	private static ConstraintPropertiesInterface getConstraintKeyPropertiesForAssociation(
			AssociationInterface association) throws DynamicExtensionsSystemException
	{
		ConstraintPropertiesInterface constraintProperties = association.getConstraintProperties();
		EntityInterface srcEntity = association.getEntity();
		EntityInterface tgtEntity = association.getTargetEntity();
		DomainObjectFactory factory = DomainObjectFactory.getInstance();
		Collection<ConstraintKeyPropertiesInterface> srcCnstrKeyPropColl;
		Collection<ConstraintKeyPropertiesInterface> tgtCnstrKeyPropColl;
		ConstraintKeyPropertiesInterface srcCnstrKeyProp = null;
		ConstraintKeyPropertiesInterface tgtCnstrKeyProp = null;
		Collection<AttributeInterface> tgtPrmKeyAttrColl = tgtEntity
				.getPrimaryKeyAttributeCollection();
		Collection<AttributeInterface> srcPrmKeyAttrColl = srcEntity
				.getPrimaryKeyAttributeCollection();
		srcCnstrKeyPropColl = constraintProperties.getSrcEntityConstraintKeyPropertiesCollection();
		tgtCnstrKeyPropColl = constraintProperties.getTgtEntityConstraintKeyPropertiesCollection();
		srcCnstrKeyPropColl.clear();
		tgtCnstrKeyPropColl.clear();
		try
		{
			if (association.getSourceRole().getMaximumCardinality() == Cardinality.MANY
					&& association.getTargetRole().getMaximumCardinality() == Cardinality.MANY
					&& !isPrimaryKeyAttributeCollectionEmpty(srcPrmKeyAttrColl)
					&& !isPrimaryKeyAttributeCollectionEmpty(tgtPrmKeyAttrColl))
			{
				for (AttributeInterface tgtAttribute : tgtPrmKeyAttrColl)
				{
					srcCnstrKeyProp = factory
							.createConstraintKeyProperties(ProcessorConstants.ASSOCIATION_COLUMN_PREFIX
									+ ProcessorConstants.UNDERSCORE
									+ "S"
									+ ProcessorConstants.UNDERSCORE
									+ IdGeneratorUtil.getNextUniqeId());
					srcCnstrKeyProp.setSrcPrimaryKeyAttribute(tgtAttribute);
					srcCnstrKeyPropColl.add(srcCnstrKeyProp);
				}
				for (AttributeInterface srcAttribute : srcPrmKeyAttrColl)
				{
					tgtCnstrKeyProp = factory
							.createConstraintKeyProperties(ProcessorConstants.ASSOCIATION_COLUMN_PREFIX
									+ ProcessorConstants.UNDERSCORE
									+ "T"
									+ ProcessorConstants.UNDERSCORE
									+ IdGeneratorUtil.getNextUniqeId());
					tgtCnstrKeyProp.setSrcPrimaryKeyAttribute(srcAttribute);
					tgtCnstrKeyPropColl.add(tgtCnstrKeyProp);
				}
			}

			else if (association.getSourceRole().getMaximumCardinality() == Cardinality.MANY
					&& association.getTargetRole().getMaximumCardinality() == Cardinality.ONE
					&& !isPrimaryKeyAttributeCollectionEmpty(tgtPrmKeyAttrColl))
			{

				for (AttributeInterface tgtAttribute : tgtPrmKeyAttrColl)
				{
					srcCnstrKeyProp = factory
							.createConstraintKeyProperties(ProcessorConstants.ASSOCIATION_COLUMN_PREFIX
									+ ProcessorConstants.UNDERSCORE
									+ "S"
									+ ProcessorConstants.UNDERSCORE
									+ IdGeneratorUtil.getNextUniqeId());
					srcCnstrKeyProp.setSrcPrimaryKeyAttribute(tgtAttribute);
					srcCnstrKeyPropColl.add(srcCnstrKeyProp);

				}

			}
			else if (!isPrimaryKeyAttributeCollectionEmpty(srcPrmKeyAttrColl))
			{
				for (AttributeInterface srcAttribute : srcPrmKeyAttrColl)
				{
					tgtCnstrKeyProp = factory
							.createConstraintKeyProperties(ProcessorConstants.ASSOCIATION_COLUMN_PREFIX
									+ ProcessorConstants.UNDERSCORE
									+ "T"
									+ ProcessorConstants.UNDERSCORE
									+ IdGeneratorUtil.getNextUniqeId());
					tgtCnstrKeyProp.setSrcPrimaryKeyAttribute(srcAttribute);
					tgtCnstrKeyPropColl.add(tgtCnstrKeyProp);

				}

			}
		}
		catch (NullPointerException e)
		{
			throw new DynamicExtensionsSystemException(
					"Please set source & target entity of the association", e);
		}
		catch (NoSuchElementException e)
		{
			throw new DynamicExtensionsSystemException(
					"Please set source & target entity of the association", e);
		}
		return constraintProperties;
	}

	/**
	 *
	 * @param controlCollection
	 * @param sequenceNumber
	 * @return
	 */
	public static List<Long> getDeletedAssociationIds(ControlInterface[] controlCollection,
			Integer[] sequenceNumbers)
	{
		List<Long> listOfIds = new ArrayList<Long>();
		boolean isPresent = false;
		if (controlCollection != null)
		{
			for (ControlInterface control : controlCollection)
			{
				isPresent = false;
				if ((control instanceof ContainmentAssociationControl || control instanceof ListBox)
						&& sequenceNumbers != null)
				{
					for (Integer sequenceNumber : sequenceNumbers)
					{
						if (control.getSequenceNumber() != null
								&& control.getSequenceNumber().equals(sequenceNumber))
						{
							isPresent = true;
						}
					}
					if (!isPresent)
					{
						listOfIds.add(control.getBaseAbstractAttribute().getId());
					}
				}
			}
		}
		return listOfIds;
	}

	/**
	 * This method used to replace escape characters such as single and double quote
	 * @param str
	 * @param one
	 * @param another
	 * @return
	 */
	public static String replaceUtil(String str, String one, String another)
	{
		if (str == null)
		{
			return str;
		}
		//    	 In a string replace one substring with another
		if ("".equals(str))
		{
			return "";
		}
		String res = "";
		int index = str.indexOf(one, 0);
		int lastpos = 0;
		while (index != -1)
		{
			res += str.substring(lastpos, index) + another;
			lastpos = index + one.length();
			index = str.indexOf(one, lastpos);
		}
		res += str.substring(lastpos);
		return res;
	}

	/**
	 * @param attr
	 * @param value
	 * @return
	 * @throws DynamicExtensionsSystemException
	 * @throws ParseException
	 */
	public static String getDefaultDateForRelatedCategoryAttribute(AttributeInterface attr,
			Object value) throws DynamicExtensionsSystemException
	{
		String formattedvalue = null;
		Date date = null;

		String dateFormat = ((DateAttributeTypeInformation) attr.getAttributeTypeInformation())
				.getFormat();
		String datePatten = DynamicExtensionsUtility.getDateFormat(dateFormat);

		String str = null;
		if (value instanceof Date)
		{
			str = Utility.parseDateToString(((Date) value), datePatten);
		}
		else
		{
			str = (String) value;
		}
		Locale locale = CommonServiceLocator.getInstance().getDefaultLocale();
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat(
				ProcessorConstants.SDF_ORCL_CAT_REL_ATTR, locale);
		try
		{
			date = simpleDateFormat.parse(str);
		}
		catch (ParseException e)
		{
			throw new DynamicExtensionsSystemException("Unable to parse given date.", e);
		}

		String appName = DynamicExtensionDAO.getInstance().getAppName();
		JDBCDAO jdbcDao = null;
		try
		{
			jdbcDao = DAOConfigFactory.getInstance().getDAOFactory(appName).getJDBCDAO();
		}
		catch (DAOException e)
		{
			throw new DynamicExtensionsSystemException("Unable to JDBCDAO object.", e);
		}
		formattedvalue = jdbcDao.getStrTodateFunction() + "('" + simpleDateFormat.format(date)
				+ "','" + ProcessorConstants.ORCL_CAT_REL_ATTR_FORMAT + "')";

		return formattedvalue;
	}

	/**
	 *
	 * @param containerInterface
	 * @param inContextContainerInterface
	 * @param processedContainersList
	 */
	public static void setAllInContextContainers(ContainerInterface containerInterface,
			List<ContainerInterface> processedContainersList)
	{
		if (processedContainersList.contains(containerInterface))
		{
			return;
		}
		else
		{
			processedContainersList.add(containerInterface);
			containerInterface.setIncontextContainer(containerInterface);

			if (containerInterface.getBaseContainer() != null)
			{
				setAllInContextContainers(containerInterface.getBaseContainer(),
						processedContainersList);
			}
			for (ControlInterface controlInterface : containerInterface.getControlCollection())
			{
				if (controlInterface instanceof AbstractContainmentControlInterface)
				{
					AbstractContainmentControlInterface containmentAssociationControl = (AbstractContainmentControlInterface) controlInterface;
					setAllInContextContainers(containmentAssociationControl.getContainer(),
							processedContainersList);
				}
			}
		}
	}

	/**
	 * This method checks if an entity with the same name exists in the entity group.
	 * @param entityGroup
	 * @param container
	 * @param formDefinitionForm
	 * @throws DynamicExtensionsApplicationException
	 */
	public static void checkIfEntityPreExists(EntityGroupInterface entityGroup,
			ContainerInterface container, String formName, ContainerInterface... mainFormContainer)
			throws DynamicExtensionsApplicationException
	{
		if (entityGroup == null)
		{
			throw new DynamicExtensionsApplicationException("Null entity group!", null,
					"Entity group is null!");
		}

		if (container == null || container.getId() == null)
		{
			if (container == null)
			{
				checkIfEntityPreExists(entityGroup, formName);
			}
			else if (container != null)
			{
				if (mainFormContainer != null && mainFormContainer.length > 0)
				{
					ContainerInterface mainContainer = mainFormContainer[0];
					checkIfEntityPreExists(entityGroup, container.getCaption(), mainContainer);
				}
				else
				{
					checkIfEntityPreExists(entityGroup, container.getCaption(), formName);
				}
			}
		}
		else if (container.getId() != null)
		{
			checkIfEntityPreExists(entityGroup, container.getCaption(), formName);
		}
	}

	/**
	 * If a sub-form is selected using the XML tree, and its name is changed,
	 * and if an entity already exists with the same name, then throw an exception.
	 * e.g. If a new entity group by the name 'TestGroup' is added and a new form
	 * by the name 'FA' is added, and a sub form by the name 'FB' is added to 'FA',
	 * and previous is clicked, 'FB' is selected from XML tree and name of 'FB'
	 * is changed to 'FA', then this block is executed.
	 * also if a new entity group by the name 'TestGroup' is added and a new form by the
	 * name 'FA' is added and a sub form 'FB' is added to 'FA' and 'FA' is saved. Then in
	 * edit mode, 'FB' is opened again and its name is changed to 'FA', then this block
	 * is executed. Call delegation from editSubForm method of ApplyFormDefinitionAction or
	 * call delegation from addSubForm method of ApplyFormDefinitionAction.
	 * @param entityGroup
	 * @param caption
	 * @param formName
	 * @throws DynamicExtensionsApplicationException
	 */
	private static void checkIfEntityPreExists(EntityGroupInterface entityGroup, String caption,
			String formName) throws DynamicExtensionsApplicationException
	{
		if (caption != null && !caption.equals(formName)
				&& entityGroup.getEntityByName(formName) != null)
		{
			reportDuplicateEntityName();
		}
	}

	/**
	 * If a new sub-form is being added, and if an entity with the same name
	 * already exists in the entity group, then throw an exception.
	 * e.g. If a new entity group by the name 'TestGroup' is added and a new form
	 * by the name 'FA' is added, and a sub-form by the same name 'FA' is added to
	 * main form 'FA', then this block is executed. Call delegation from addSubForm
	 * method of ApplyFormDefinitionAction.
	 * e.g. If a new entity group by the name 'TestGroup' is added and a new form
	 * by the name 'FA' is added, and a sub form by the name 'FB' is added to
	 * main form 'FA', then this block is executed. Call delegation from addSubForm
	 * method of ApplyFormDefinitionAction.
	 * @param entityGroup
	 * @param caption
	 * @param mainContainer
	 * @throws DynamicExtensionsApplicationException
	 */
	private static void checkIfEntityPreExists(EntityGroupInterface entityGroup, String caption,
			ContainerInterface mainContainer) throws DynamicExtensionsApplicationException
	{
		if (mainContainer != null && mainContainer.getCaption().equals(caption))
		{
			reportDuplicateEntityName();
		}

		EntityInterface existingEntity = entityGroup.getEntityByName(caption);
		if (existingEntity != null)
		{
			reportDuplicateEntityName();
		}
	}

	/**
	 * If a entity whose parent is a persistent object is selected using the XML tree
	 * and its name is not changed, then check if the entity already exists
	 * and if it exists, then throw an exception.
	 * e.g. If a new entity group by the name 'TestGroup' is added and a new form by the
	 * name 'FA' is added, or if 'FA' is previously saved and a sub form 'FB' is added
	 * to 'FA', then this block is executed. Call delegation from applyFormDefinition
	 * method of ApplyFormDefinitionAction
	 * @param entityGroup
	 * @param formName
	 * @throws DynamicExtensionsApplicationException
	 */
	private static void checkIfEntityPreExists(EntityGroupInterface entityGroup, String formName)
			throws DynamicExtensionsApplicationException
	{
		EntityInterface entity = entityGroup.getEntityByName(formName);
		if (entity != null && entity.getId() != null)
		{
			reportDuplicateEntityName();
		}
	}

	/**
	 * @throws DynamicExtensionsApplicationException
	 */
	private static void reportDuplicateEntityName() throws DynamicExtensionsApplicationException
	{
		throw new DynamicExtensionsApplicationException(
				"Duplicate form name within same entity group!", null,
				EntityManagerExceptionConstantsInterface.DYEXTN_A_019);
	}

	/**
	 * @param hql Query and returns the results.
	 * @return String query
	 * @throws DAOException Generic DAO Exception
	 * @throws ClassNotFoundException
	 */
	public static List executeQuery(String hql) throws DAOException, ClassNotFoundException
	{
		HibernateDAO dao = DynamicExtensionsUtility.getHibernateDAO();
		List list = dao.executeQuery(hql);
		DynamicExtensionsUtility.closeHibernateDAO(dao);
		return list;
	}

	/**
	 * Replace any single and double quotes value with proper escape character	in HTML
	 * @param value
	 * @return String value
	 */
	public static String getEscapedStringValue(String value)
	{
		String replacedValue = value;
		replacedValue = replaceUtil(replacedValue, "'", "&#39");
		replacedValue = replaceUtil(replacedValue, "\"", "&#34");
		if (replacedValue != null)
		{
			replacedValue = replacedValue.trim();
		}
		return replacedValue;
	}

	/**
	 * @param container
	 * @return
	 * @throws DynamicExtensionsSystemException
	 */
	public static boolean areNoRelatedCategoryAttributesPresent(ContainerInterface container)
			throws DynamicExtensionsSystemException
	{
		if (container == null)
		{
			throw new DynamicExtensionsSystemException(ApplicationProperties
					.getValue(CategoryConstants.CREATE_CAT_FAILS)
					+ "Container is null");
		}

		Collection<ControlInterface> controls = container.getAllControls();

		if (controls != null)
		{
			for (ControlInterface control : controls)
			{
				if (control.getIsReadOnly() != null && control.getIsReadOnly())
				{
					return false;
				}
			}
		}

		return true;
	}

	/**
	 * @param categoryEntityName
	 * @return categoryEntityName
	 */
	public static String getCategoryEntityName(String categoryEntityName)
	{
		Pattern pattern = Pattern.compile("[]]");
		String entityName = categoryEntityName;
		if (entityName != null && entityName.length() > 0)
		{
			Matcher matcher = pattern.matcher(entityName);
			StringBuffer stringBuff = new StringBuffer();
			boolean result = matcher.find();
			// Loop through and create a new String
			// with the replacements
			while (result)
			{
				matcher.appendReplacement(stringBuff, entityName.subSequence(matcher.start(),
						matcher.end())
						+ " ");
				result = matcher.find();
			}
			//Add the last segment of input to
			//the new String
			matcher.appendTail(stringBuff);

			String[] categoryEntityNameArray = stringBuff.toString().trim().split(" ");
			entityName = categoryEntityNameArray[categoryEntityNameArray.length - 1];
		}
		return entityName;
	}

	/**
	 * this method returns object from database not from current session.
	 * @param sourceObjectName class name.
	 * @param identifier id for object.
	 * @return object from database
	 * @throws DAOException generic DAO exception.
	 */
	public static Object getCleanObject(String sourceObjectName, Long identifier)
			throws DAOException
	{
		HibernateDAO hibernateDao = null;
		try
		{
			String appName = DynamicExtensionDAO.getInstance().getAppName();
			hibernateDao = (HibernateDAO) DAOConfigFactory.getInstance().getDAOFactory(appName)
					.getDAO();
			hibernateDao.openSession(null);
			return hibernateDao.retrieveById(sourceObjectName, identifier);
		}
		finally
		{
			hibernateDao.closeSession();
		}
	}

	/**
	 * @return jdbcDao
	 * @throws DAOException generic DAO exception
	 */
	public static JDBCDAO getJDBCDAO() throws DAOException
	{

		String appName = DynamicExtensionDAO.getInstance().getAppName();
		JDBCDAO jdbcDao = DAOConfigFactory.getInstance().getDAOFactory(appName).getJDBCDAO();
		jdbcDao.openSession(null);
		return jdbcDao;
	}

	/**
	 * @return hibernateDao
	 * @throws DAOException generic DAO exception
	 */
	public static HibernateDAO getHibernateDAO() throws DAOException
	{
		String appName = DynamicExtensionDAO.getInstance().getAppName();
		HibernateDAO hibernateDao = (HibernateDAO) DAOConfigFactory.getInstance().getDAOFactory(
				appName).getDAO();
		hibernateDao.openSession(null);
		return hibernateDao;
	}

	/**
	 * @param jdbcDao DAO object
	 * @throws DAOException generic DAO exception
	 */
	public static void closeJDBCDAO(JDBCDAO jdbcDao) throws DAOException
	{
		jdbcDao.closeSession();
	}

	/**
	 * @param hibernateDao DAO object
	 * @throws DAOException generic DAO exception
	 */
	public static void closeHibernateDAO(HibernateDAO hibernateDao) throws DAOException
	{
		hibernateDao.closeSession();
	}

	/**
	 * @param entityGroup entityGroup object
	 * @param xmiConfigurationObject xml configuration
	 * @throws DynamicExtensionsSystemException fails to populate entity
	 */
	public static void populateEntityForConstraintProperties(EntityGroupInterface entityGroup,
			XMIConfiguration xmiConfigurationObject) throws DynamicExtensionsSystemException
	{
		HibernateDAO hibernateDao = null;
		try
		{
			String appName = DynamicExtensionDAO.getInstance().getAppName();
			hibernateDao = (HibernateDAO) DAOConfigFactory.getInstance().getDAOFactory(appName)
					.getDAO();
			hibernateDao.openSession(null);

			// populate entity for generating constraint properties if it has any parent set
			for (EntityInterface entity : entityGroup.getEntityCollection())
			{
				populateEntityForConstraintPropertiesInheritance(entity, xmiConfigurationObject
						.isAddColumnForInherianceInChild(), hibernateDao);
			}
		}
		catch (DAOException e)
		{
			throw new DynamicExtensionsSystemException(
					"Exception encountered while populating constraint properties for entity.", e);
		}
		finally
		{
			try
			{
				hibernateDao.closeSession();
			}
			catch (DAOException e)
			{
				throw new DynamicExtensionsSystemException(
						"Exception encountered while closing session.", e);
			}
		}
	}

	/**
	 * @param childEntity child entity object
	 * @param isAddColumnForInheritance if true then add column for inheritance
	 * @param hibernateDao DAO object
	 * @throws DynamicExtensionsSystemException
	 */
	private static void populateEntityForConstraintPropertiesInheritance(
			EntityInterface childEntity, boolean isAddColumnForInheritance,
			HibernateDAO hibernateDao) throws DynamicExtensionsSystemException
	{
		EntityInterface parentEntity = childEntity.getParentEntity();
		Long identifier = childEntity.getId();
		Entity dbaseCopy;
		if (identifier == null && parentEntity != null)
		{
			getConstraintKeyProperties(childEntity, parentEntity, isAddColumnForInheritance);
		}
		else if (identifier != null)
		{
			try
			{
				dbaseCopy = (Entity) hibernateDao.retrieveById(Entity.class.getCanonicalName(),
						identifier);
			}
			catch (DAOException e)
			{
				throw new DynamicExtensionsSystemException(e.getMessage(), e);
			}
			if (EntityManagerUtil.isParentChanged((Entity) childEntity, dbaseCopy)
					|| EntityManagerUtil.isPrimaryKeyChanged(parentEntity, hibernateDao))
			{
				getConstraintKeyProperties(childEntity, parentEntity, isAddColumnForInheritance);
			}
		}

	}

	/**
	 * @param association association object
	 * @param hibernateDao DAO object
	 * @return ConstraintProperties
	 * @throws DynamicExtensionsSystemException fails to set Constraint Key Properties
	 */
	public static ConstraintPropertiesInterface populateConstraintPropertiesForAssociation(
			AssociationInterface association, HibernateDAO hibernateDao)
			throws DynamicExtensionsSystemException
	{
		Long identifier = association.getId();
		ConstraintPropertiesInterface constraintProperties = association.getConstraintProperties();
		if (identifier == null)
		{
			constraintProperties = getConstraintKeyPropertiesForAssociation(association);
		}
		else
		{
			AssociationInterface dbaseCopy;
			try
			{
				dbaseCopy = (AssociationInterface) hibernateDao.retrieveById(Association.class
						.getCanonicalName(), identifier);
			}
			catch (DAOException e)
			{
				throw new DynamicExtensionsSystemException(e.getMessage(), e);
			}

			if (EntityManagerUtil.isCardinalityChanged(association, dbaseCopy)
					|| EntityManagerUtil.isPrimaryKeyChanged(association.getEntity(), hibernateDao)
					|| EntityManagerUtil.isPrimaryKeyChanged(association.getTargetEntity(),
							hibernateDao))
			{
				association.getConstraintProperties()
						.getSrcEntityConstraintKeyPropertiesCollection().clear();
				association.getConstraintProperties()
						.getTgtEntityConstraintKeyPropertiesCollection().clear();
				constraintProperties = getConstraintKeyPropertiesForAssociation(association);
			}
		}

		return constraintProperties;
	}

	/**
	 * @param filePath
	 * @param jsFunctionName
	 * @param jsFunctParameters
	 * @return
	 */
	public static String executeJavaScriptFunc(String filePath, String jsFunctionName,
			Object[] jsFunctParameters)
	{
		String output = null;
		FileReader reader = null;
		try
		{
			ScriptEngineManager manager = new ScriptEngineManager();
			if (manager != null)
			{
				ScriptEngine engine = manager.getEngineByName("javascript");
				reader = new FileReader(filePath);
				if (reader != null && engine != null)
				{
					engine.eval(reader);
					Invocable invokeEngine = (Invocable) engine;
					output = invokeEngine.invokeFunction(jsFunctionName, jsFunctParameters)
							.toString();
				}
			}
		}
		catch (Exception e)
		{
			Logger.out.error(e.getMessage());
		}
		finally
		{
			try
			{
				if (reader != null)
				{
					reader.close();
				}
			}
			catch (IOException e)
			{
				Logger.out.error(e.getMessage());
			}
		}
		return output;
	}

	/**
	 * This function will return the next sequence number for control by incrementing the maximum sequence number within current control collection.
	 * @param container
	 * @return
	 */
	public static int getSequenceNumberForNextControl(ContainerInterface container)
	{
		int nextSeqNumber = 0;
		if (container.getControlCollection() != null)
		{
			List<ControlInterface> controls = new ArrayList<ControlInterface>(container
					.getControlCollection());

			for (ControlInterface control : controls)
			{
				nextSeqNumber = nextSeqNumber > control.getSequenceNumber()
						? nextSeqNumber
						: control.getSequenceNumber();
			}
		}
		return nextSeqNumber + 1;
	}
	/**
	 * It will read the ValidDatePatterns.XML & verify that the date patterns
	 * specified in the ApplicationResources.properties Files is in the given patterns.
	 * else will throw the Exception.
	 */
	public static void validateGivenDatePatterns()
	{
		DynamicExtensionsUtility utility = new DynamicExtensionsUtility();
		List<String> validDatePatternList = utility.getAllValidDatePatterns();

			if(!(validDatePatternList.contains(ProcessorConstants.DATE_ONLY_FORMAT)
					&& validDatePatternList.contains(ProcessorConstants.DATE_TIME_FORMAT)
					&& validDatePatternList.contains(ProcessorConstants.MONTH_YEAR_FORMAT)
					&& validDatePatternList.contains(ProcessorConstants.YEAR_ONLY_FORMAT)))
			{
				throw new RuntimeException("Invalid date pattern specified in the Application resource file");
			}

	}
	/**
	 * It will return the List of all  valid Patterns specified in the ValidDatePatterns.xml
	 * @return List of datePatterns.
	 */
	public List<String> getAllValidDatePatterns()
	{
		List<String> validDatePatternList = new ArrayList<String>();
		SAXReader saxReader = new SAXReader();

		InputStream inputStream =this.getClass().getClassLoader().getResourceAsStream("ValidDatePatterns.xml");

		Document document = null;

		try
		{
			document = saxReader.read(inputStream);
			Element pattern = null;

			Element datePatternsElement = document.getRootElement();
			Iterator patternIterator = datePatternsElement
					.elementIterator("pattern");

			Element primitiveAttributeElement = null;

			while (patternIterator.hasNext())
			{
				primitiveAttributeElement = (Element) patternIterator.next();
				validDatePatternList.add(primitiveAttributeElement.getText());

			}
		}
		catch (DocumentException documentException)
		{

		}
		return validDatePatternList;
	}

}