/**
 *
 */

package edu.common.dynamicextensions.util;

import static edu.common.dynamicextensions.util.global.DEConstants.FALSE_VALUE_LIST;
import static edu.common.dynamicextensions.util.global.DEConstants.TRUE_VALUE_LIST;
import static edu.common.dynamicextensions.util.global.Variables.fileUploadDir;
import static edu.common.dynamicextensions.util.global.Variables.fileUploadExpiryLimit;

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
import java.sql.ResultSet;
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
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Properties;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.script.Invocable;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.servlet.http.HttpServletRequest;
import javax.xml.parsers.DocumentBuilderFactory;

import org.apache.commons.lang.StringEscapeUtils;
import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

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
import edu.common.dynamicextensions.domaininterface.BaseAbstractAttributeInterface;
import edu.common.dynamicextensions.domaininterface.CategoryAssociationInterface;
import edu.common.dynamicextensions.domaininterface.CategoryAttributeInterface;
import edu.common.dynamicextensions.domaininterface.CategoryEntityInterface;
import edu.common.dynamicextensions.domaininterface.EntityGroupInterface;
import edu.common.dynamicextensions.domaininterface.EntityInterface;
import edu.common.dynamicextensions.domaininterface.PermissibleValueInterface;
import edu.common.dynamicextensions.domaininterface.RoleInterface;
import edu.common.dynamicextensions.domaininterface.SemanticPropertyInterface;
import edu.common.dynamicextensions.domaininterface.TaggedValueInterface;
import edu.common.dynamicextensions.domaininterface.UserDefinedDEInterface;
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
import edu.common.dynamicextensions.exception.DynamicExtensionsCacheException;
import edu.common.dynamicextensions.exception.DynamicExtensionsSystemException;
import edu.common.dynamicextensions.processor.ApplyDataEntryProcessorInterface;
import edu.common.dynamicextensions.processor.InContextApplyDataEntryProcessor;
import edu.common.dynamicextensions.processor.ProcessorConstants;
import edu.common.dynamicextensions.ui.util.Constants;
import edu.common.dynamicextensions.ui.webui.actionform.DataEntryForm;
import edu.common.dynamicextensions.ui.webui.util.UserInterfaceiUtility;
import edu.common.dynamicextensions.ui.webui.util.WebUIManagerConstants;
import edu.common.dynamicextensions.util.global.CategoryConstants;
import edu.common.dynamicextensions.util.global.DEConstants;
import edu.common.dynamicextensions.util.global.DEConstants.Cardinality;
import edu.common.dynamicextensions.util.global.DEConstants.InheritanceStrategy;
import edu.common.dynamicextensions.util.global.Variables;
import edu.common.dynamicextensions.xmi.XMIConfiguration;
import edu.common.dynamicextensions.xmi.XMIConstants;
import edu.wustl.cab2b.server.cache.EntityCache;
import edu.wustl.common.beans.NameValueBean;
import edu.wustl.common.beans.SessionDataBean;
import edu.wustl.common.bizlogic.DefaultBizLogic;
import edu.wustl.common.exception.BizLogicException;
import edu.wustl.common.util.CVSTagReader;
import edu.wustl.common.util.Utility;
import edu.wustl.common.util.global.ApplicationProperties;
import edu.wustl.common.util.global.CommonServiceLocator;
import edu.wustl.common.util.global.Validator;
import edu.wustl.common.util.logger.Logger;
import edu.wustl.dao.DAO;
import edu.wustl.dao.HibernateDAO;
import edu.wustl.dao.JDBCDAO;
import edu.wustl.dao.daofactory.DAOConfigFactory;
import edu.wustl.dao.exception.DAOException;
import edu.wustl.dao.query.generator.ColumnValueBean;
import edu.wustl.metadata.util.DyExtnObjectCloner;

/**
 * @author chetan_patil
 *
 */
public class DynamicExtensionsUtility
{

	/** The Constant LOGGER. */
	private static final Logger LOGGER = Logger.getCommonLogger(DynamicExtensionsUtility.class);

	/**
	 * This method fetches the Container instance from the Database given the corresponding Container Identifier.
	 * @param containerIdentifier The identifier of the container.
	 * @param entityGroupIdentifier The entity group identifier.
	 * @return the ContainerInterface.
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

	public static EntityGroupInterface getEntityGroupByIdentifier(String entityGroupIdentifier)
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
	 * @throws NumberFormatException
	 * @throws DynamicExtensionsCacheException
	 */
	public static ContainerInterface getContainerByIdentifier(String containerIdentifier)
			throws DynamicExtensionsCacheException, NumberFormatException
	{
		ContainerInterface containerInterface = null;
		if (containerIdentifier != null && !"".equals(containerIdentifier))
		{
			containerInterface = EntityCache.getInstance().getContainerById(
					Long.valueOf(containerIdentifier.trim()));
		}
		return containerInterface;
	}
	
	/**
	 * Checks if the given string object is not null and not empty string.
	 * @param value the object name.
	 * @return true, if checks if is not empty string.
	 */
	public static boolean isNotEmptyString(final Object value)
	{
		return value != null && !"".equals(value);
	}

	/**
	 * This method returns the containerInterface object.
	 * @param containerIdentifier Container identifier.
	 * @return ContainerInterface
	 * @throws NumberFormatException
	 * @throws DynamicExtensionsCacheException
	 */
	public static ContainerInterface getClonedContainerFromCache(String containerIdentifier)
			throws DynamicExtensionsCacheException, NumberFormatException
	{
		ContainerInterface containerInterface = getContainerByIdentifier(containerIdentifier);
		DyExtnObjectCloner cloner = new DyExtnObjectCloner();
		return cloner.clone(containerInterface);
	}

	/**
	 * This method fetches the Container instance from the Database given the corresponding Container Identifier.
	 * @param containerIdentifier The Identifier of the Container.
	 * @param mainContainer Container Interface object.
	 * @return the ContainerInterface
	 * @throws DynamicExtensionsSystemException on System exception
	 * @throws DynamicExtensionsApplicationException on Application exception
	 */
	public static ContainerInterface getContainerByIdentifier(String containerIdentifier,
			ContainerInterface mainContainer) throws DynamicExtensionsSystemException
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
	 * @param attributeIdentifier The Identifier of the Container.
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
	 * It will add the taggedValue to the given attribute in parameter.
	 * with key "derived" and its value also "derived".
	 * @param attribute AttributeInterface object.
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
	 * This method clears data value for all controls within container.
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
	 * This method returns the control name.
	 * @param controlInterface ControlInterface
	 * @return String ControlName
	 */
	public static String getControlName(ControlInterface controlInterface)
	{
		String typeOfControl = null;
		if (controlInterface != null)
		{
			if (controlInterface instanceof TextFieldInterface)
			{
				typeOfControl = ProcessorConstants.TEXT_CONTROL;
			}
			else if (controlInterface instanceof ComboBoxInterface)
			{
				typeOfControl = ProcessorConstants.COMBOBOX_CONTROL;
			}
			else if (controlInterface instanceof ListBoxInterface)
			{
				typeOfControl = ProcessorConstants.COMBOBOX_CONTROL;
			}
			else if (controlInterface instanceof DatePickerInterface)
			{
				typeOfControl = ProcessorConstants.DATEPICKER_CONTROL;
			}
			else if (controlInterface instanceof TextAreaInterface)
			{
				typeOfControl = ProcessorConstants.TEXT_CONTROL;
			}
			else if (controlInterface instanceof RadioButtonInterface)
			{
				typeOfControl = ProcessorConstants.RADIOBUTTON_CONTROL;
			}
			else if (controlInterface instanceof CheckBoxInterface)
			{
				typeOfControl = ProcessorConstants.CHECKBOX_CONTROL;
			}
			else if (controlInterface instanceof FileUploadInterface)
			{
				typeOfControl = ProcessorConstants.FILEUPLOAD_CONTROL;
			}
			if (controlInterface instanceof AbstractContainmentControl)
			{
				typeOfControl = ProcessorConstants.ADD_SUBFORM_CONTROL;
			}
		}
		return typeOfControl;
	}

	/**
	 * This method returns controlInterface object by sequence number.
	 * @param controlCollection
	 * @param sequenceNumber
	 * @return ControlInterface object.
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
					break;
				}
			}
		}
		return controlInterface;
	}

	/**
	 * This method returns controlInterface object.
	 * @param attributeMetadataInterface
	 * @param containerInterface
	 * @return ControlInterface object.
	 */
	public static ControlInterface getControlForAbstractAttribute(
			AttributeMetadataInterface attributeMetadataInterface,
			ContainerInterface containerInterface)
	{
		ControlInterface control = null;
		Collection<ControlInterface> controlCollection = containerInterface
				.getAllControlsUnderSameDisplayLabel();
		for (ControlInterface controlInterface : controlCollection)
		{
			if (controlInterface instanceof AbstractContainmentControlInterface)
			{
				control = getControlForAbstractAttribute(attributeMetadataInterface,
						((AbstractContainmentControlInterface) controlInterface).getContainer());
				if (control != null)
				{
					break;
				}
			}
			else if (controlInterface.getBaseAbstractAttribute() != null
					&& controlInterface.getBaseAbstractAttribute().getId() != null
					&& controlInterface.getBaseAbstractAttribute().equals(
							attributeMetadataInterface)
					|| attributeMetadataInterface.equals(controlInterface
							.getBaseAbstractAttribute()))
			{
				control = controlInterface;
				break;
			}
		}
		return control;
	}

	/**
	 * This method returns controlInterface object.
	 * @param attributeMetadataInterface AttributeMetadataInterface object.
	 * @param containerInterface ContainerInterface object
	 * @param categoryEntityName category entity name.
	 * @return ControlInterface object.
	 */
	public static ControlInterface getControlForAbstractAttribute(
			AttributeMetadataInterface attributeMetadataInterface,
			ContainerInterface containerInterface, String categoryEntityName)
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
					break;
				}
			}
			else if (controlInterface.getBaseAbstractAttribute() != null)
			{
				if (controlInterface.getBaseAbstractAttribute().getId() == null)
				{
					if (controlInterface.getParentContainer().getAbstractEntity().getName() != null
							&& controlInterface.getParentContainer().getAbstractEntity().getName()
									.equals(categoryEntityName)
							&& controlInterface.getBaseAbstractAttribute().getName().equals(
									attributeMetadataInterface.getName()))
					{
						control = controlInterface;
						break;
					}
				}
				else
				{
					if (controlInterface.getBaseAbstractAttribute().equals(
							attributeMetadataInterface))
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
	 * This method returns controlInterface object.
	 * @param controlIdentifier
	 * @param containerInterface
	 * @return ControlInterface object.
	 */
	public static ControlInterface getControlByIdentifier(String controlIdentifier,
			ContainerInterface containerInterface)
	{
		ControlInterface control = null;
		Collection<ControlInterface> controlCollection = containerInterface
				.getAllControlsUnderSameDisplayLabel();
		for (ControlInterface controlInterface : controlCollection)
		{
			if (controlInterface instanceof AbstractContainmentControlInterface)
			{
				control = getControlByIdentifier(controlIdentifier,
						((AbstractContainmentControlInterface) controlInterface).getContainer());
				if (control != null)
				{
					break;
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
	 * This method returns ContainerInterface object.
	 * @param abstractEntityInterface AbstractEntityInterface object.
	 * @return ContainerInterface object.
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

		}
	}

	/**
	 * Initialize Application Information.
	 */
	public static void initialiseApplicationInfo()
	{

		CommonServiceLocator serviceLocator = CommonServiceLocator.getInstance();
		String fileName = Variables.dynamicExtensionsHome + System.getProperty("file.separator")
				+ ApplicationProperties.getValue("application.version.file");
		CVSTagReader cvsTagReader = new CVSTagReader();
		String cvsTag = cvsTagReader.readTag(fileName);
		Variables.applicationCvsTag = cvsTag;
		LOGGER.info("========================================================");
		LOGGER.info("Application Information");
		LOGGER.info("-----------------------");
		LOGGER.info("Name : " + DynamicExtensionDAO.getInstance().getAppName());
		LOGGER.info("CVS TAG : " + Variables.applicationCvsTag);
		LOGGER.info("Path : " + serviceLocator.getAppHome());
		LOGGER.info("========================================================");

		LOGGER.info(" ");
		LOGGER.info("Preloading the DE metadata....this may take a few minutes!!");
		LOGGER.info(" ");
	}

	/**
	 * This method returns AttributeTypeInformationInterface object.
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
	 * This method converts stack trace to the string representation.
	 * @param throwable throwable object
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
	 * Converts string to integer.
	 * @param string
	 * @return int vale.
	 * @throws DynamicExtensionsApplicationException.
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
			if (Double.parseDouble(numString) < 0)
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
	 * Checks that the input String contains only numeric digits also allowed zero as valid value.
	 *
	 * @param numString The string whose characters are to be checked.
	 *
	 * @return false if the String contains any alphabet else returns true.
	 */
	public static boolean isNumericDigit(String numString)
	{
		boolean isNumeric = false;
		try
		{
			long longValue = Long.parseLong(numString);
			if (longValue >= 0)
			{
				isNumeric ^= true;
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
		return Calendar.getInstance().get(Calendar.MONTH) + 1;
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
	 * @param string : string to be checked.
	 * @param list: List that is to be checked if string is contained
	 * @return check if a string is contained in the passed list and return true if yes
	 */
	public static boolean isStringInList(String string, List<String> list)
	{
		boolean isContainedInList = false;
		if (string != null && list != null)
		{
			//String listString = null;
			Iterator<String> iterator = list.iterator();
			while (iterator.hasNext())
			{
				String listString = iterator.next();
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
	 * @return Integer objecta array.
	 */
	public static Integer[] convertToIntegerArray(String controlsSeqNumbers, String delimiter)
	{
		ArrayList<Integer> integerList = new ArrayList<Integer>();
		if (controlsSeqNumbers != null)
		{
			StringTokenizer strTokenizer = new StringTokenizer(controlsSeqNumbers, delimiter);
			if (strTokenizer != null)
			{
				//String str = null;
				//Integer integer = null;
				while (strTokenizer.hasMoreElements())
				{
					String str = strTokenizer.nextToken();
					if (str != null)
					{
						try
						{
							Integer integer = Integer.valueOf(str);
							integerList.add(integer);
						}
						catch (NumberFormatException e)
						{
							LOGGER.error(e);
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
	 * @param entity EntityInterface object.
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
		if (collection != null && !collection.isEmpty())
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
	 * @param name Name.
	 * @throws DynamicExtensionsApplicationException
	 */
	public static void validateName(String name) throws DynamicExtensionsApplicationException
	{
		/**
		 * Constant representing valid names
		 */
		final String VALIDCHARSREGEX = "[^\\\\/:*?\"<>&;|']*";

		if (name == null || "".equals(name.trim()) || !name.matches(VALIDCHARSREGEX))
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
	 * @return EntityInterface list.
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
			Date date = Utility.parseDate(strDate, dateFormat);
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
		if (value != null && !"".equals(value.trim())
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
	 * @param value String value.
	 * @return 'checked' string or empty string
	 */
	public static String getCheckboxSelectionValue(String value)
	{
		String checkboxValue = "";
		if (value != null && !"".equals(value.trim())
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
	private static boolean areBothDatesOfSameFormat(String date1, String date2)
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
		try
		{
			ContainerInterface containerInterface = null;
			if (caption != null && !"".equals(caption.trim()))
			{
				List objectList;
				objectList = defaultBizLogic
						.retrieve(Container.class.getName(), "caption", caption);
				if (!objectList.isEmpty())
				{
					containerInterface = (ContainerInterface) objectList.get(0);
				}
			}
			return containerInterface;
		}
		catch (BizLogicException e)
		{
			throw new DynamicExtensionsSystemException(e.getMessage(), e);
		}
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
		Collection<EntityGroupInterface> entityGroupCollection;
		EntityGroupInterface entityGroup = null;
		DefaultBizLogic bizlogic = BizLogicFactory.getDefaultBizLogic();
		bizlogic.setAppName(DynamicExtensionDAO.getInstance().getAppName());
		LOGGER.info("-retrieveEntityGroup APP NAME-----"
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
			LOGGER.debug(e.getMessage(), e);

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
		if (dataType.equals(ProcessorConstants.DATATYPE_INTEGER)
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
			Map<String, CategoryEntityInterface> objCategoryMap)
			throws DynamicExtensionsSystemException
	{
		if (categoryEntity != null && !objCategoryMap.containsKey(categoryEntity.getName()))
		{
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
			if (objCategoryEntity.isCreateTable())
			{
				if (objCategoryEntity.getId() == null && objCategoryEntity.isCreateTable())
				{
					//Only includes those category entity for which table is required to be created
					objCategoryMap.put(categoryEntity.getName(), objCategoryEntity);
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
	}

	/**
	 * @param categoryEntity
	 * @param objCategoryMap
	 * @throws DynamicExtensionsSystemException
	 */
	public static void getSavedCategoryEntityList(CategoryEntityInterface categoryEntity,
			Map<String, CategoryEntityInterface> objCategoryMap)
			throws DynamicExtensionsSystemException
	{
		if (categoryEntity != null && !objCategoryMap.containsKey(categoryEntity.getName()))
		{
			CategoryEntity objCategoryEntity = (CategoryEntity) categoryEntity;
			if (categoryEntity.getParentCategoryEntity() != null
					&& !objCategoryMap.containsKey(categoryEntity.getParentCategoryEntity()
							.getName())
					&& ((CategoryEntity) objCategoryEntity.getParentCategoryEntity())
							.isCreateTable())
			{
				getSavedCategoryEntityList(categoryEntity.getParentCategoryEntity(), objCategoryMap);
			}
			if (objCategoryEntity.getId() != null && objCategoryEntity.isCreateTable())
			{
				objCategoryMap.put(categoryEntity.getName(), categoryEntity);

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
			dbaseCopy = (Entity) DynamicExtensionsUtility.getCleanObject(Entity.class
					.getCanonicalName(), identifier);

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
		ConstraintPropertiesInterface constraintProperties = childEntity.getConstraintProperties();
		if (constraintProperties == null)
		{
			constraintProperties = factory.createConstraintPropertiesForInheritance();
			childEntity.setConsraintProperties(constraintProperties);
		}
		Collection<ConstraintKeyPropertiesInterface> cnstrKeyProp = constraintProperties
				.getSrcEntityConstraintKeyPropertiesCollection();
		ConstraintKeyPropertiesInterface primaryCnstrKeyProp = null;
		cnstrKeyProp.clear();
		if (EntityManagerUtil.isAttributePresent(parentEntity, "id")
				&& EntityManagerUtil.isAttributePresent(childEntity, "id")
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
	 * This method sets the constraintProperties of the association depending.
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
			dbaseCopy = (AssociationInterface) DynamicExtensionsUtility.getCleanObject(
					Association.class.getCanonicalName(), identifier);
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
	 * It will verify weather the attributeColl is not empty.
	 * @param attributeColl collection of attribute to check
	 * @return boolean value.
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
		if (srcEntity == null || tgtEntity == null)
		{
			throw new DynamicExtensionsSystemException(
					"Please set source & target entity of the association");
		}
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
	 * @param sequenceNumbers
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
	 * This method used to replace escape characters such as single and double
	 * quote.
	 *
	 * @param str
	 *            the str
	 * @param one
	 *            the one
	 * @param another
	 *            the another
	 * @return the string
	 */
	public static String replaceUtil(String str, String one, String another)
	{
		String string = str;
		if (str != null && !"".equals(str))
		{
			StringBuilder res = new StringBuilder();
			int index = str.indexOf(one, 0);
			int lastpos = 0;
			while (index != -1)
			{
				res.append(str.substring(lastpos, index) + another);
				lastpos = index + one.length();
				index = str.indexOf(one, lastpos);
			}
			res.append(str.substring(lastpos));
			string = res.toString();
		}
		return string;
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
		//String formattedvalue = null;
		Date date = null;
		String str = null;
		if (value instanceof Date)
		{
			String dateFormat = ((DateAttributeTypeInformation) attr.getAttributeTypeInformation())
					.getFormat();
			String datePatten = DynamicExtensionsUtility.getDateFormat(dateFormat);
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

		JDBCDAO jdbcDao = getJDBCDAO();

		String formattedvalue = jdbcDao.getStrTodateFunction() + "('"
				+ simpleDateFormat.format(date) + "','"
				+ ProcessorConstants.ORCL_CAT_REL_ATTR_FORMAT + "')";

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
	 * @param formName
	 * @param mainFormContainer
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
	public static void checkIfEntityPreExists(EntityGroupInterface entityGroup, String caption,
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
	 * @throws DynamicExtensionsSystemException
	 */
	public static List executeQuery(String hql) throws DAOException,
			DynamicExtensionsSystemException
	{
		HibernateDAO dao = DynamicExtensionsUtility.getHibernateDAO();
		List list = dao.executeQuery(hql);
		DynamicExtensionsUtility.closeDAO(dao);
		return list;
	}

	/**
	 * This method executes a SQL query.
	 * @param query
	 * @param userId
	 * @param jdbcDao
	 * @param colValBeanList
	 * @throws DynamicExtensionsSystemException
	 */
	public static void executeUpdateQuery(final String query, final Long userId,
			final JDBCDAO jdbcDao, final LinkedList<ColumnValueBean> colValBeanList)
			throws DynamicExtensionsSystemException
	{
		
		JDBCDAO dao = jdbcDao;
		try
		{
			if (dao == null)
			{
				dao = getJDBCDAO();
			}
			final LinkedList<LinkedList<ColumnValueBean>> valueBeanList = new LinkedList<LinkedList<ColumnValueBean>>();
			valueBeanList.add(colValBeanList);
			dao.executeUpdate(query, valueBeanList);
		}
		catch (final DAOException e)
		{
			throw new DynamicExtensionsSystemException(e.getMessage(), e);
		}
		finally
		{
			if (jdbcDao == null)
			{
				closeDAO(dao);
			}
		}
	}

	/**
	 * Replace any single and double quotes value with proper escape character	in HTML
	 * @param value
	 * @return String value
	 */
	public static String getEscapedStringValue(String value)
	{
		String originalString = null;
		if (value != null)
		{
			originalString = replaceUtil(value, "'", "&#39");
			originalString = replaceUtil(originalString, "\"", "&#34");
			originalString = originalString.trim();
		}
		return originalString;
	}

	/**
	 * Replace any single and double quotes value with proper escape character	in HTML
	 * @param value
	 * @return String value
	 */
	public static String getUnEscapedStringValue(String value)
	{
		String originalString = null;
		if (value != null)
		{
			originalString = replaceUtil(value, "&#39", "'");
			originalString = replaceUtil(value, "&#34", "\"");
			originalString = originalString.trim();
			
		}
		return originalString;
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

		boolean catAttributeNotRelated = true;
		if (controls != null)
		{
			for (ControlInterface control : controls)
			{
				if (control.getIsReadOnly() != null && control.getIsReadOnly())
				{
					catAttributeNotRelated = false;
					break;
				}
			}
		}
		return catAttributeNotRelated;
	}

	/**
	 * @param categoryEntityName
	 * @return categoryEntityName
	 */
	public static String getCategoryEntityName(String categoryEntityName)
	{

		String entityName = categoryEntityName;
		if (entityName != null && entityName.length() > 0)
		{
			Pattern pattern = Pattern.compile("[]]");
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
			throws DynamicExtensionsSystemException
	{
		HibernateDAO hibernateDao = null;
		try
		{

			hibernateDao = DynamicExtensionsUtility.getHibernateDAO();
			return hibernateDao.retrieveById(sourceObjectName, identifier);
		}
		catch (DAOException e)
		{
			throw new DynamicExtensionsSystemException("Error while retrieving the Clean Object", e);
		}
		finally
		{
			DynamicExtensionsUtility.closeDAO(hibernateDao);
		}
	}

	/**
	 * @return jdbcDao
	 * @throws DynamicExtensionsSystemException
	 * @deprecated Use {@link #getJDBCDAO(SessionDataBean)} instead
	 */
	public static JDBCDAO getJDBCDAO() throws DynamicExtensionsSystemException
	{
		return getJDBCDAO(null);
	}

	/**
	 * @param sessionDataBean TODO
	 * @return jdbcDao
	 * @throws DynamicExtensionsSystemException
	 */
	public static JDBCDAO getJDBCDAO(SessionDataBean sessionDataBean)
			throws DynamicExtensionsSystemException
	{
		
		String appName = DynamicExtensionDAO.getInstance().getAppName();
		JDBCDAO jdbcDao = null;
		try
		{
			jdbcDao = DAOConfigFactory.getInstance().getDAOFactory(appName).getJDBCDAO();
			jdbcDao.openSession(sessionDataBean);
		}
		catch (DAOException e)
		{
			throw new DynamicExtensionsSystemException(
					"Error occured while opening the DAO session", e);
		}
		return jdbcDao;
	}

	/**
	 * @return hibernateDao
	 * @throws DynamicExtensionsSystemException
	 * @deprecated Use {@link #getHibernateDAO(SessionDataBean)} instead
	 */
	public static HibernateDAO getHibernateDAO() throws DynamicExtensionsSystemException
	{
		return getHibernateDAO(null);
	}

	/**
	 * @param sessionDataBean
	 * @return hibernateDao
	 * @throws DynamicExtensionsSystemException
	 */
	public static HibernateDAO getHibernateDAO(SessionDataBean sessionDataBean)
			throws DynamicExtensionsSystemException
	{
		String appName = DynamicExtensionDAO.getInstance().getAppName();
		HibernateDAO hibernateDao = null;
		try
		{
			hibernateDao = (HibernateDAO) DAOConfigFactory.getInstance().getDAOFactory(appName)
					.getDAO();
			hibernateDao.openSession(sessionDataBean);
		}
		catch (DAOException e)
		{
			throw new DynamicExtensionsSystemException(
					"Error occured while opening the DAO session", e);
		}
		return hibernateDao;
	}

	/**
	 * @param jdbcDao DAO object
	 * @throws DynamicExtensionsSystemException
	 * @throws DAOException generic DAO exception
	 */
	public static void closeDAO(DAO dao)
	{
		closeDAO(dao, true);
	}
	
	public static void closeDAO(DAO dao, boolean throwException) 
	{
		if (dao != null)
		{
			try
			{
				dao.closeSession();
			}
			catch (DAOException e)
			{
				if (throwException) {
					throw new RuntimeException("Error closing DAO", e);
				}
			}
		}
	}
	
	public static void closeResultSet(ResultSet rs) {
		if (rs != null) {
			try {
				rs.close();
			} catch (Exception e) {
				
			}
		}
	}

	/**
	 * Roll Back DAO.
	 * @param hibernateDAO DAO
	 * @throws DynamicExtensionsSystemException DynamicExtensionsSystemException
	 */
	public static void rollBackDAO(DAO hibernateDAO) throws DynamicExtensionsSystemException
	{
		try
		{
			hibernateDAO.rollback();
		}
		catch (DAOException daoExp)
		{
			throw new DynamicExtensionsSystemException(DEConstants.DATA_INSERTION_ERROR_MESSAGE,
					daoExp);
		}
	}

	public static void executeDML(List<Map<String, LinkedList<ColumnValueBean>>> queryList)
			throws DynamicExtensionsSystemException
	{
		JDBCDAO jdbcDao = null;
		try
		{
			jdbcDao = DynamicExtensionsUtility.getJDBCDAO();
			for (Map<String, LinkedList<ColumnValueBean>> query : queryList)
			{
				for (Map.Entry<String, LinkedList<ColumnValueBean>> queryRecord : query.entrySet())
				{
					LinkedList<LinkedList<ColumnValueBean>> colValBeanList = new LinkedList<LinkedList<ColumnValueBean>>();
					colValBeanList.add(queryRecord.getValue());
					jdbcDao.executeUpdate(queryRecord.getKey(), colValBeanList);
				}
			}
		}

		catch (DAOException e)
		{
			throw new DynamicExtensionsSystemException("Error while inserting the data", e);
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
				throw new DynamicExtensionsSystemException("Error while inserting the data", e);
			}
		}
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
			hibernateDao = DynamicExtensionsUtility.getHibernateDAO();

			// populate entity for generating constraint properties if it has any parent set
			for (EntityInterface entity : entityGroup.getEntityCollection())
			{
				populateEntityForConstraintPropertiesInheritance(entity, xmiConfigurationObject
						.isAddColumnForInherianceInChild(), hibernateDao);
			}
		}
		finally
		{
//			DynamicExtensionsUtility.closeDAO(hibernateDao);
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
		Entity cacheCopy;
		if (identifier == null && parentEntity != null)
		{
			getConstraintKeyProperties(childEntity, parentEntity, isAddColumnForInheritance);
		}
		else if (identifier != null)
		{
			try {
				cacheCopy = (Entity) EntityCache.getInstance().getEntityById(identifier);
				if (EntityManagerUtil.isParentChanged((Entity) childEntity, cacheCopy)
						|| EntityManagerUtil.isParentPrimaryKeyChanged(parentEntity))
				{
					getConstraintKeyProperties(childEntity, parentEntity, isAddColumnForInheritance);
				}
			} catch (DynamicExtensionsCacheException e) {
				throw new DynamicExtensionsSystemException(e.getMessage(), e);
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
			AssociationInterface association)
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
			AssociationInterface cacheCopy;

			cacheCopy = EntityCache.getInstance().getAssociationById(identifier);

			if (EntityManagerUtil.isCardinalityChanged(association, cacheCopy)
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
			LOGGER.error(e.getMessage());
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
				LOGGER.error(e.getMessage());
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
	 * It will return the List of all  valid Patterns specified in the ValidDatePatterns.xml
	 * @return List of datePatterns.
	 * @throws DynamicExtensionsSystemException
	 */
	public static Map<String, String> getAllValidDatePatterns()
			throws DynamicExtensionsSystemException
	{
		Map<String, String> validDatePatternMap = new HashMap<String, String>();

		InputStream inputStream = Thread.currentThread().getContextClassLoader()
				.getResourceAsStream("ValidDatePatterns.xml");
		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		try
		{
			Document doc = dbf.newDocumentBuilder().parse(inputStream);
			NodeList nodeList = doc.getElementsByTagName("pattern");
			for (int s = 0; s < nodeList.getLength(); s++)
			{
				Node firstNode = nodeList.item(s);

				if (firstNode.getNodeType() == Node.ELEMENT_NODE)
				{
					NamedNodeMap attributeMap = firstNode.getAttributes();
					Node attributeNode = attributeMap.getNamedItem("regex");
					String regex = attributeNode.getNodeValue();
					NodeList fstNm = firstNode.getChildNodes();
					validDatePatternMap.put(fstNm.item(0).getNodeValue(), regex);
				}
			}
		}
		catch (Exception exception)
		{
			throw new DynamicExtensionsSystemException(exception.getMessage(), exception);
		}
		return validDatePatternMap;
	}

	/**
	 * Replace any single and double quotes value with proper escape character	in HTML
	 * @param value
	 * @return String value
	 */
	public static String replaceHTMLSpecialCharacters(String value)
	{
		/*String escapeStringValue = value;
		escapeStringValue = getUnEscapedStringValue(escapeStringValue);*/
		String escapeStringValue = getUnEscapedStringValue(value);
		if (escapeStringValue != null && escapeStringValue.length() > 0)
		{
			escapeStringValue = StringEscapeUtils.escapeHtml(escapeStringValue);
		}
		return escapeStringValue;
	}

	/**
	 * This method will populate all the category entities associated with
	 * the categoryEntity in the given paramater categoryEntityList.
	 * @param categoryEntityList list of categroy entities to be populated in.
	 * @param categoryEntity category entity who's all associated categroy entities to be added in the list.
	 */
	public static void populateAllCategoryEntityList(
			List<CategoryEntityInterface> categoryEntityList, CategoryEntityInterface categoryEntity)
	{
		if (categoryEntity != null)
		{
			categoryEntityList.add(categoryEntity);
			for (CategoryEntityInterface categoryEntityInterface : categoryEntity
					.getChildCategories())
			{
				populateAllCategoryEntityList(categoryEntityList, categoryEntityInterface);
			}
		}
	}

	/**
	 * Set context parameters to all the child container of the main container.
	 * @param containerInterface main  container.
	 * @param contextParameter Context parameters.
	 */
	public static void setEncounterDateToChildContainer(ContainerInterface containerInterface,
			Map<String, Object> contextParameter)
	{
		Collection<ContainerInterface> childContainerCollection = containerInterface
				.getChildContainerCollection();
		for (ContainerInterface childcContainerInterface : childContainerCollection)
		{
			childcContainerInterface.setContextParameter(contextParameter);
			setEncounterDateToChildContainer(childcContainerInterface, contextParameter);
		}

		Collection<ControlInterface> controls = containerInterface.getControlCollection();
		for (ControlInterface controlInterface : controls)
		{
			if (controlInterface instanceof AbstractContainmentControl)
			{
				ContainerInterface abstCtnmentCtrlContainer = ((AbstractContainmentControl) controlInterface)
						.getContainer();
				abstCtnmentCtrlContainer.setContextParameter(contextParameter);
				setEncounterDateToChildContainer(abstCtnmentCtrlContainer, contextParameter);
			}
		}
	}

	/**
	 * Validate boolean value.
	 *
	 * @param booleanValue the boolean value
	 *
	 * @return true, if successful
	 * @throws DynamicExtensionsSystemException
	 */
	public static boolean validateAndReturnBooleanValue(String booleanValue)
			throws DynamicExtensionsSystemException
	{
		String upperCaseBooleanValue = booleanValue.toUpperCase(Locale.getDefault());
		boolean returnValue = false;
		if (TRUE_VALUE_LIST.contains(upperCaseBooleanValue))
		{
			returnValue = true;
		}
		else if (FALSE_VALUE_LIST.contains(upperCaseBooleanValue))
		{
			returnValue = false;
		}
		else
		{
			throw new DynamicExtensionsSystemException(
					"Incorrect spelling for Paste Button Configuration value");
		}
		return returnValue;
	}

	/**
	 * This method will search the ConceptCode from the given semanticPropCollection
	 * and return them.
	 * @param semanticPropCollection semantic property collection.
	 * @return the collection of concept codes retrieved from semanticPropCollection.
	 */
	public static Collection<String> getConceptCodes(
			Collection<SemanticPropertyInterface> semanticPropCollection)
	{
		Collection<String> conceptCodeColl = new HashSet<String>();
		// get the concpt code fom the semantic properties
		for (SemanticPropertyInterface semanticProp : semanticPropCollection)
		{
			String conceptCode = semanticProp.getConceptCode().trim();
			if (conceptCode != null && !"".equals(conceptCode))
			{
				conceptCodeColl.add(conceptCode);
			}
		}
		return conceptCodeColl;
	}

	/**
	 * This method will return the concept code associated with the given defaultValue from the
	 * permissible value collection of the catAttribute.
	 * @param catAttribute category attribute from whose pv collection to search the concept code.
	 * @param defaultValue value for which concept code is to be searched.
	 * @return concept code associated with the default value.
	 * @throws DynamicExtensionsSystemException exception.
	 */
	public static String getConceptCodeForValue(CategoryAttributeInterface catAttribute,
			String defaultValue) throws DynamicExtensionsSystemException
	{
		String conceptCode = null;
		AttributeInterface attribute = getBaseAttributeOfcategoryAttribute(catAttribute);
		UserDefinedDEInterface userdefinedDe = (UserDefinedDEInterface) attribute
				.getAttributeTypeInformation().getDataElement();
		if (userdefinedDe != null)
		{
			for (PermissibleValueInterface permValue : userdefinedDe
					.getPermissibleValueCollection())
			{
				if (defaultValue.equals(permValue.getValueAsObject().toString()))
				{
					for (SemanticPropertyInterface semanticProp : permValue
							.getSemanticPropertyCollection())
					{
						conceptCode = semanticProp.getConceptCode();
					}
					break;
				}
			}
		}
		if (conceptCode == null)
		{
			throw new DynamicExtensionsSystemException("No concept code found for Value "
					+ defaultValue);
		}
		return conceptCode;
	}

	/**
	 * This method will return the baseAttribute of the given category Attribute,
	 * if the base attribute is association i.e. multiselect case then it will search the original
	 * attribute created for it & return that.
	 * @param catAttribute category attribute whose base attribute is needed.
	 * @return attribute from which the category attribute is derived.
	 */
	public static AttributeInterface getBaseAttributeOfcategoryAttribute(
			CategoryAttributeInterface catAttribute)
	{
		AttributeInterface attribute;
		if (catAttribute.getAbstractAttribute() instanceof AssociationInterface)
		{
			attribute = (AttributeInterface) EntityManagerUtil.filterSystemAttributes(
					((AssociationInterface) catAttribute.getAbstractAttribute()).getTargetEntity()
							.getAbstractAttributeCollection()).iterator().next();
		}
		else
		{
			attribute = (AttributeInterface) catAttribute.getAbstractAttribute();
		}
		return attribute;
	}

	public static void initializeVariables(Properties props)
	{
		StringBuffer buffer = new StringBuffer();
		Variables.serverUrl = props.getProperty("Application.url");
		int lastIndex = props.getProperty("Application.url").lastIndexOf("/");
		buffer.append(props.getProperty("Application.url").substring(0, lastIndex + 1)).append(
				WebUIManagerConstants.DYNAMIC_EXTENSIONS);
		if (props.getProperty("default.prifix.war") != null)
		{
			buffer.append(props.getProperty("default.prifix.war").trim());
		}
		Variables.jbossUrl = buffer.toString();
		fileUploadDir = props.getProperty("de.file.upload.dir");
		if (fileUploadDir.trim().isEmpty() || "${de.file.upload.dir}".equals(fileUploadDir)) {
			fileUploadDir = "./DEFileUpload";
		}

		String expiry = props.getProperty("de.file.upload.expiry.limit");
		//if (expiry.trim().isEmpty() || "${de.file.upload.expiry.limit}".equals(expiry.trim())) {
			fileUploadExpiryLimit = 7;
		//} else {
		//	fileUploadExpiryLimit = Integer.valueOf(expiry);
		//}

		LOGGER.info(Variables.jbossUrl);
	}
	public static Long insertDataUtility(Long recordIdentifier, ContainerInterface containerInterface,
			Map<BaseAbstractAttributeInterface, Object> attributeToValueMap)
			throws DynamicExtensionsApplicationException
	{
		return insertDataUtility(recordIdentifier, containerInterface, attributeToValueMap, null);
	}

	/**
	 * @param recordIdentifier
	 * @param containerInterface
	 * @param attributeToValueMap
	 * @return
	 * @throws DynamicExtensionsApplicationException
	 */
	public static Long insertDataUtility(Long recordIdentifier,
			ContainerInterface containerInterface,
			Map<BaseAbstractAttributeInterface, Object> attributeToValueMap, SessionDataBean sessionDataBean)
			throws DynamicExtensionsApplicationException
	{
		ApplyDataEntryProcessorInterface applyDataEntryFormProcessor = new InContextApplyDataEntryProcessor();
		try
		{
			recordIdentifier = Long.valueOf(applyDataEntryFormProcessor.insertDataEntryForm(containerInterface, attributeToValueMap, sessionDataBean));
		}
		catch (NumberFormatException e)
		{
			throw new DynamicExtensionsApplicationException(e.getMessage()); 
		}
		catch (DynamicExtensionsSystemException e)
		{
			throw new DynamicExtensionsApplicationException(e.getMessage());
		}
		return recordIdentifier;
	}
	public static boolean editDataUtility(Long recordIdentifier, ContainerInterface containerInterface,
			Map<BaseAbstractAttributeInterface, Object> attributeToValueMap,SessionDataBean sessionDataBean,Long userid) throws DynamicExtensionsSystemException, DynamicExtensionsApplicationException
	{
		ApplyDataEntryProcessorInterface applyDataEntryFormProcessor = new InContextApplyDataEntryProcessor();
		boolean edited = true;
		try
		{
			edited = applyDataEntryFormProcessor.editDataEntryForm(containerInterface, attributeToValueMap,recordIdentifier, sessionDataBean);
		}
		catch (NumberFormatException e)
		{
			throw new DynamicExtensionsSystemException(e.getMessage()); 
		}
		catch (DynamicExtensionsSystemException e)
		{
			throw new DynamicExtensionsSystemException(e.getMessage());
		}
		return edited;
	}
	
	/**
	 * @param sessionDataBean
	 * @return hibernateDao
	 * @throws DynamicExtensionsSystemException
	 */
	public static HibernateDAO getHostAppHibernateDAO(SessionDataBean sessionDataBean)
			throws DynamicExtensionsSystemException
	{
		String appName = DynamicExtensionDAO.getInstance().getHostAppName();
		HibernateDAO hibernateDao = null;
		try
		{
			if(Validator.isEmpty(appName))
			{
				hibernateDao = (HibernateDAO) DAOConfigFactory.getInstance().getDAOFactory()
				.getDAO();
			}
			else
			{
				hibernateDao = (HibernateDAO) DAOConfigFactory.getInstance().getDAOFactory(appName)
					.getDAO();
			}
			hibernateDao.openSession(sessionDataBean);
		}
		catch (DAOException e)
		{
			throw new DynamicExtensionsSystemException(
					"Error occured while opening the DAO session", e);
		}
		return hibernateDao;
	}
	
	public static DataEntryForm poulateDataEntryForm(HttpServletRequest request)
	{
		DataEntryForm dataEntryForm = new DataEntryForm();
		dataEntryForm.setContainerId(request.getParameter(DEConstants.CONTAINER_ID));
		dataEntryForm.setRecordIdentifier(request.getParameter(DEConstants.RECORD_IDENTIFIER));
		dataEntryForm.setMode(request.getParameter(WebUIManagerConstants.MODE_PARAM_NAME));
		dataEntryForm.setDataEntryOperation(request.getParameter(Constants.DATA_ENTRY_OPERATION));
		return dataEntryForm;
	}
	
	public static DELayoutEnum getLayout(Long containerId) throws DynamicExtensionsCacheException
	{
		DELayoutEnum layout = DELayoutEnum.DEFAULT;
		ContainerInterface containerInterface = EntityCache.getInstance().getContainerById(containerId);
		if (containerInterface.getAbstractEntity() instanceof CategoryEntityInterface)
		{
			CategoryEntity categoryEntity = (CategoryEntity) containerInterface.getAbstractEntity();
			if (categoryEntity.getCategory().getLayout() != null)
			{
				layout = DELayoutEnum.SURVEY;
			}

		}
		return layout;
	}
	
	/**
	 * @param taggedValueColl
	 * @param key
	 * @return
	 */
	public static TaggedValueInterface updatePackageName(
			final Collection<TaggedValueInterface> taggedValueColl, final String key)
	{
		TaggedValueInterface taggedValue = null;
		for (final TaggedValueInterface tag : taggedValueColl)
		{
			if (tag.getKey().equals(key))
			{
				taggedValue = tag;
				break;
			}
		}
		return taggedValue;
	}

}