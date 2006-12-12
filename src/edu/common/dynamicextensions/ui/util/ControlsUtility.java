
package edu.common.dynamicextensions.ui.util;

/**
 * This class defines miscellaneous methods that are commonly used by many Control objects. * 
 * @author chetan_patil
 */
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.Set;
import java.util.Vector;

import edu.common.dynamicextensions.domaininterface.AbstractAttributeInterface;
import edu.common.dynamicextensions.domaininterface.AssociationInterface;
import edu.common.dynamicextensions.domaininterface.AttributeInterface;
import edu.common.dynamicextensions.domaininterface.AttributeTypeInformationInterface;
import edu.common.dynamicextensions.domaininterface.BooleanTypeInformationInterface;
import edu.common.dynamicextensions.domaininterface.BooleanValueInterface;
import edu.common.dynamicextensions.domaininterface.DataElementInterface;
import edu.common.dynamicextensions.domaininterface.DateTypeInformationInterface;
import edu.common.dynamicextensions.domaininterface.DateValueInterface;
import edu.common.dynamicextensions.domaininterface.DoubleTypeInformationInterface;
import edu.common.dynamicextensions.domaininterface.DoubleValueInterface;
import edu.common.dynamicextensions.domaininterface.EntityInterface;
import edu.common.dynamicextensions.domaininterface.FloatTypeInformationInterface;
import edu.common.dynamicextensions.domaininterface.FloatValueInterface;
import edu.common.dynamicextensions.domaininterface.IntegerTypeInformationInterface;
import edu.common.dynamicextensions.domaininterface.IntegerValueInterface;
import edu.common.dynamicextensions.domaininterface.LongTypeInformationInterface;
import edu.common.dynamicextensions.domaininterface.LongValueInterface;
import edu.common.dynamicextensions.domaininterface.PermissibleValueInterface;
import edu.common.dynamicextensions.domaininterface.ShortTypeInformationInterface;
import edu.common.dynamicextensions.domaininterface.ShortValueInterface;
import edu.common.dynamicextensions.domaininterface.StringTypeInformationInterface;
import edu.common.dynamicextensions.domaininterface.StringValueInterface;
import edu.common.dynamicextensions.domaininterface.UserDefinedDEInterface;
import edu.common.dynamicextensions.domaininterface.userinterface.AssociationControlInterface;
import edu.common.dynamicextensions.domaininterface.userinterface.ContainerInterface;
import edu.common.dynamicextensions.domaininterface.userinterface.ControlInterface;
import edu.common.dynamicextensions.entitymanager.EntityManager;
import edu.common.dynamicextensions.entitymanager.EntityManagerInterface;
import edu.common.dynamicextensions.exception.DynamicExtensionsApplicationException;
import edu.common.dynamicextensions.exception.DynamicExtensionsSystemException;
import edu.common.dynamicextensions.ui.webui.util.ControlInformationObject;
import edu.common.dynamicextensions.util.DynamicExtensionsUtility;
import edu.wustl.common.beans.NameValueBean;

public class ControlsUtility
{
	/**
	 * This method returns the default value of the PrimitiveAttribute for displaying in corresponding controls on UI. 
	 * @param abstractAttribute the PrimitiveAttribute
	 * @return the Default Value of the PrimitiveAttribute
	 */
	public static String getDefaultValue(AbstractAttributeInterface abstractAttribute)
	{
		String defaultValue = null;
		AttributeTypeInformationInterface abstractAttributeType = null;

		if (abstractAttribute != null)
		{
			if(abstractAttribute instanceof AttributeInterface)
			{
				abstractAttributeType = ((AttributeInterface) abstractAttribute).getAttributeTypeInformation();
			}
		}
		if (abstractAttributeType != null)
		{
			if (abstractAttributeType instanceof StringTypeInformationInterface)
			{
				StringTypeInformationInterface stringAttribute = (StringTypeInformationInterface) abstractAttributeType;
				if (stringAttribute != null)
				{
					defaultValue = getDefaultString(stringAttribute);
				}
			}
			else if (abstractAttributeType instanceof BooleanTypeInformationInterface)
			{
				BooleanTypeInformationInterface booleanAttribute = (BooleanTypeInformationInterface) abstractAttributeType;
				if (booleanAttribute != null)
				{
					defaultValue = getDefaultBoolean(booleanAttribute);
				}
			}
			else if (abstractAttributeType instanceof IntegerTypeInformationInterface)
			{
				IntegerTypeInformationInterface integerAttribute = (IntegerTypeInformationInterface) abstractAttributeType;
				if (integerAttribute != null)
				{
					defaultValue = getDefaultInteger(integerAttribute);
				}
			}
			else if (abstractAttributeType instanceof LongTypeInformationInterface)
			{
				LongTypeInformationInterface longAttribute = (LongTypeInformationInterface) abstractAttributeType;
				if (longAttribute != null)
				{
					defaultValue = getDefaultLong(longAttribute);
				}
			}
			else if (abstractAttributeType instanceof DoubleTypeInformationInterface)
			{
				DoubleTypeInformationInterface doubleAttribute = (DoubleTypeInformationInterface) abstractAttributeType;
				if (doubleAttribute != null)
				{
					defaultValue = getDefaultDouble(doubleAttribute);
				}
			}
			else if (abstractAttributeType instanceof FloatTypeInformationInterface)
			{
				FloatTypeInformationInterface floatAttribute = (FloatTypeInformationInterface) abstractAttributeType;
				if (floatAttribute != null)
				{
					defaultValue = getDefaultFloat(floatAttribute);
				}
			}
			else if (abstractAttributeType instanceof ShortTypeInformationInterface)
			{
				ShortTypeInformationInterface shortAttribute = (ShortTypeInformationInterface) abstractAttributeType;
				if (shortAttribute != null)
				{
					defaultValue = getDefaultShort(shortAttribute);
				}
			}
			else if (abstractAttributeType instanceof DateTypeInformationInterface)
			{
				DateTypeInformationInterface dateAttribute = (DateTypeInformationInterface) abstractAttributeType;
				if (dateAttribute != null)
				{
					defaultValue = getDefaultDate(dateAttribute);
				}
			}
		}
		return defaultValue;
	}

	private static String getDefaultString(StringTypeInformationInterface stringAttribute)
	{
		String defaultValue = null;
		StringValueInterface stringValue = (StringValueInterface) stringAttribute.getDefaultValue();
		if (stringValue != null)
		{
			defaultValue = stringValue.getValue();
		}
		return defaultValue;
	}

	private static String getDefaultBoolean(BooleanTypeInformationInterface booleanAttribute)
	{
		String defaultValue = null;
		BooleanValueInterface booleanValue = (BooleanValueInterface) booleanAttribute.getDefaultValue();
		if (booleanValue != null)
		{
			Boolean defaultBoolean = booleanValue.getValue();
			if (defaultBoolean != null)
			{
				defaultValue = defaultBoolean.toString();
			}
		}
		return defaultValue;
	}

	private static String getDefaultInteger(IntegerTypeInformationInterface integerAttribute)
	{
		String defaultValue = null;
		IntegerValueInterface integerValue = (IntegerValueInterface) integerAttribute.getDefaultValue();
		if (integerValue != null)
		{
			Integer defaultInteger = integerValue.getValue();
			if (defaultInteger != null)
			{
				defaultValue = defaultInteger.toString();
			}
		}
		return defaultValue;
	}

	private static String getDefaultLong(LongTypeInformationInterface longAttribute)
	{
		String defaultValue = null;
		LongValueInterface longValue = (LongValueInterface) longAttribute.getDefaultValue();
		if (longValue != null)
		{
			Long defaultLong = longValue.getValue();
			if (defaultLong != null)
			{
				defaultValue = defaultLong.toString();
			}
		}
		return defaultValue;
	}

	private static String getDefaultDouble(DoubleTypeInformationInterface doubleAttribute)
	{
		String defaultValue = null;
		DoubleValueInterface doubleValue = (DoubleValueInterface) doubleAttribute.getDefaultValue();
		if (doubleValue != null)
		{
			Double defaultDouble = doubleValue.getValue();
			if (defaultDouble != null)
			{
				defaultValue = defaultDouble.toString();
			}
		}
		return defaultValue;
	}

	private static String getDefaultFloat(FloatTypeInformationInterface floatAttribute)
	{
		String defaultValue = null;
		FloatValueInterface floatValue = (FloatValueInterface) floatAttribute.getDefaultValue();
		if (floatValue != null)
		{
			Float defaultFloat = floatValue.getValue();
			if (defaultFloat != null)
			{
				defaultValue = defaultFloat.toString();
			}
		}
		return defaultValue;
	}

	private static String getDefaultShort(ShortTypeInformationInterface shortAttribute)
	{
		String defaultValue = null;
		ShortValueInterface shortValue = (ShortValueInterface) shortAttribute.getDefaultValue();
		if (shortValue != null)
		{
			Short defaultShort = shortValue.getValue();
			if (defaultShort != null)
			{
				defaultValue = defaultShort.toString();
			}
		}
		return defaultValue;
	}

	private static String getDefaultDate(DateTypeInformationInterface dateAttribute)
	{
		String defaultValue = null;
		DateValueInterface dateValue = (DateValueInterface) dateAttribute.getDefaultValue();
		if (dateValue != null)
		{
			Date defaultDate = dateValue.getValue();
			if (defaultDate != null)
			{
				defaultValue = new SimpleDateFormat(getDateFormat(dateAttribute)).format(defaultDate);
			}
		}
		return defaultValue;
	}

	/**
	 * This method returns the prescribed date format for the given DateAttributeTypeInformation
	 * @param attribute the DateAttributeTypeInformation
	 * @return the date format String
	 */
	public static String getDateFormat(AttributeTypeInformationInterface dateAttribute)
	{
		String dateFormat = ((DateTypeInformationInterface) dateAttribute).getFormat();
		if (dateFormat == null)
		{
			dateFormat = "";
		}
		return dateFormat;
	}

	/**
	 * This method populates the List of Values of the ListBox in the NameValueBean Collection.
	 * @return List of pair of Name and its corresponding Value.
	 */
	public static List<NameValueBean> populateListOfValues(ControlInterface control)
	{
		List<NameValueBean> nameValueBeanList = null;
		try
		{
			AbstractAttributeInterface abstractAttribute = control.getAbstractAttribute();
			if (abstractAttribute != null)
			{
				if (abstractAttribute instanceof AttributeInterface)
				{
					nameValueBeanList = getListOfPermissibleValues((AttributeInterface) abstractAttribute);
				}
				else if (abstractAttribute instanceof AssociationInterface)
				{
					EntityManagerInterface entityManager = EntityManager.getInstance();

					AssociationControlInterface associationControl = (AssociationControlInterface) control;
					Map<Long, List<String>> displayAttributeMap = null;

					String sepatator = associationControl.getSeparator();

					displayAttributeMap = entityManager.getRecordsForAssociationControl(associationControl);

					nameValueBeanList = getTargetEntityDisplayAttributeList(displayAttributeMap, sepatator);
				}
			}
		}
		catch (Exception exception)
		{
			throw new RuntimeException(exception);
		}
		return nameValueBeanList;
	}

	private static List<NameValueBean> getListOfPermissibleValues(AttributeInterface attribute) throws DynamicExtensionsSystemException,
			DynamicExtensionsApplicationException
	{
		List<NameValueBean> nameValueBeanList = null;

		AttributeTypeInformationInterface attributeTypeInformation = attribute.getAttributeTypeInformation();
		if (attributeTypeInformation != null)
		{
			DataElementInterface dataElement = attributeTypeInformation.getDataElement();
			if (dataElement != null)
			{
				if (dataElement instanceof UserDefinedDEInterface)
				{
					Collection<PermissibleValueInterface> permissibleValueCollection = ((UserDefinedDEInterface) dataElement)
							.getPermissibleValueCollection();
					if (permissibleValueCollection != null)
					{
						nameValueBeanList = new Vector<NameValueBean>();
						NameValueBean nameValueBean = null;
						for (PermissibleValueInterface permissibleValue : permissibleValueCollection)
						{
							if (permissibleValue instanceof StringValueInterface)
							{
								nameValueBean = getPermissibleStringValue(permissibleValue);
							}
							else if (permissibleValue instanceof DateValueInterface)
							{
								DateTypeInformationInterface dateAttribute = (DateTypeInformationInterface) attribute;
								nameValueBean = getPermissibleDateValue(permissibleValue, dateAttribute);
							}
							else if (permissibleValue instanceof DoubleValueInterface)
							{
								nameValueBean = getPermissibleDoubleValue(permissibleValue);
							}
							else if (permissibleValue instanceof FloatValueInterface)
							{
								nameValueBean = getPermissibleFloatValue(permissibleValue);
							}
							else if (permissibleValue instanceof LongValueInterface)
							{
								nameValueBean = getPermissibleLongValue(permissibleValue);
							}
							else if (permissibleValue instanceof IntegerValueInterface)
							{
								nameValueBean = getPermissibleIntegerValue(permissibleValue);
							}
							else if (permissibleValue instanceof ShortValueInterface)
							{
								nameValueBean = getPermissibleShortValue(permissibleValue);
							}
							else if (permissibleValue instanceof BooleanValueInterface)
							{
								nameValueBean = getPermissibleBooleanValue(permissibleValue);
							}
							nameValueBeanList.add(nameValueBean);
						}
					}
				}
			}
		}
		return nameValueBeanList;
	}

	private static List<NameValueBean> getTargetEntityDisplayAttributeList(Map<Long, List<String>> displayAttributeMap, String separator)
			throws DynamicExtensionsSystemException, DynamicExtensionsApplicationException
	{
		List<NameValueBean> displayAttributeList = new Vector<NameValueBean>();

		Set<Map.Entry<Long, List<String>>> displayAttributeSet = displayAttributeMap.entrySet();
		for (Map.Entry<Long, List<String>> displayAttributeEntry : displayAttributeSet)
		{
			Long recordIdentifier = displayAttributeEntry.getKey();
			List<String> attributeList = displayAttributeEntry.getValue();

			NameValueBean nameValueBean = new NameValueBean();
			nameValueBean.setValue(recordIdentifier.toString());
			
			StringBuffer value = new StringBuffer();
			for (String attributeValue : attributeList)
			{
				value.append(attributeValue + separator);
			}
			value.delete(value.lastIndexOf(separator), value.length());
			
			nameValueBean.setName(value.toString());
			displayAttributeList.add(nameValueBean);
		}

		return displayAttributeList;
	}

	private static NameValueBean getPermissibleDateValue(PermissibleValueInterface permissibleValue, DateTypeInformationInterface dateAttribute)
	{
		DateValueInterface dateValue = (DateValueInterface) permissibleValue;
		NameValueBean nameValueBean = null;

		if (dateValue != null && dateValue.getValue() != null)
		{
			nameValueBean = new NameValueBean();
			String date = new SimpleDateFormat(getDateFormat(dateAttribute)).format(dateValue.getValue());
			nameValueBean.setName(date);
			nameValueBean.setValue(date);
		}
		return nameValueBean;
	}

	private static NameValueBean getPermissibleDoubleValue(PermissibleValueInterface permissibleValue)
	{
		DoubleValueInterface doubleValue = (DoubleValueInterface) permissibleValue;
		NameValueBean nameValueBean = null;

		if (doubleValue != null && doubleValue.getValue() != null)
		{
			nameValueBean = new NameValueBean();
			nameValueBean.setName(doubleValue.getValue().doubleValue());
			nameValueBean.setValue(doubleValue.getValue().doubleValue());
		}
		return nameValueBean;
	}

	private static NameValueBean getPermissibleFloatValue(PermissibleValueInterface permissibleValue)
	{
		FloatValueInterface floatValue = (FloatValueInterface) permissibleValue;
		NameValueBean nameValueBean = null;

		if (floatValue != null & floatValue.getValue() != null)
		{
			nameValueBean = new NameValueBean();
			nameValueBean.setName(floatValue.getValue().floatValue());
			nameValueBean.setValue(floatValue.getValue().floatValue());
		}
		return nameValueBean;
	}

	private static NameValueBean getPermissibleLongValue(PermissibleValueInterface permissibleValue)
	{
		LongValueInterface longValue = (LongValueInterface) permissibleValue;
		NameValueBean nameValueBean = null;

		if (longValue != null && longValue.getValue() != null)
		{
			nameValueBean = new NameValueBean();
			nameValueBean.setName(longValue.getValue().longValue());
			nameValueBean.setValue(longValue.getValue().longValue());
		}
		return nameValueBean;
	}

	private static NameValueBean getPermissibleIntegerValue(PermissibleValueInterface permissibleValue)
	{
		IntegerValueInterface integerValue = (IntegerValueInterface) permissibleValue;
		NameValueBean nameValueBean = null;

		if (integerValue != null && integerValue.getValue() != null)
		{
			nameValueBean = new NameValueBean();
			nameValueBean.setName(integerValue.getValue().intValue());
			nameValueBean.setValue(integerValue.getValue().intValue());
		}
		return nameValueBean;
	}

	private static NameValueBean getPermissibleShortValue(PermissibleValueInterface permissibleValue)
	{
		ShortValueInterface shortValue = (ShortValueInterface) permissibleValue;
		NameValueBean nameValueBean = null;

		if (shortValue != null && shortValue.getValue() != null)
		{
			nameValueBean = new NameValueBean();
			nameValueBean.setName(shortValue.getValue().shortValue());
			nameValueBean.setValue(shortValue.getValue().shortValue());
		}
		return nameValueBean;
	}

	private static NameValueBean getPermissibleBooleanValue(PermissibleValueInterface permissibleValue)
	{
		BooleanValueInterface booleanValue = (BooleanValueInterface) permissibleValue;
		NameValueBean nameValueBean = null;

		if (booleanValue != null && booleanValue.getValue() != null)
		{
			nameValueBean = new NameValueBean();
			nameValueBean.setName(booleanValue.getValue().booleanValue());
			nameValueBean.setValue(booleanValue.getValue().booleanValue());
		}
		return nameValueBean;
	}

	private static NameValueBean getPermissibleStringValue(PermissibleValueInterface permissibleValue)
	{
		StringValueInterface stringValue = (StringValueInterface) permissibleValue;
		NameValueBean nameValueBean = null;

		if (stringValue != null && stringValue.getValue() != null)
		{
			nameValueBean = new NameValueBean();
			nameValueBean.setName(stringValue.getValue().trim());
			nameValueBean.setValue(stringValue.getValue().trim());
		}
		return nameValueBean;
	}

	/**
	 * 
	 * @param entityInterface
	 * @param sequenceNumbers
	 */
	public static void applySequenceNumbers(ContainerInterface containerInterface, String[] sequenceNumbers)
	{
		Collection controlCollection = containerInterface.getControlCollection();
		ControlInterface controlInterface;
		int sequenceNumber;
		if (controlCollection != null && controlCollection.size() > 0 && sequenceNumbers != null && sequenceNumbers.length > 0)
		{
			for (int counter = 0; counter < sequenceNumbers.length - 1; counter++)
			{
				sequenceNumber = new Integer(sequenceNumbers[counter]).intValue();
				controlInterface = DynamicExtensionsUtility.getControlBySequenceNumber(controlCollection, sequenceNumber);
				controlInterface.setSequenceNumber(new Integer(counter + 1));
			}
			deleteControls(containerInterface, sequenceNumbers.length);
			DynamicExtensionsUtility.resetSequenceNumberChanged(controlCollection);
		}
	}

	/**
	 * 
	 * @param controlCollection
	 */
	public static void deleteControls(ContainerInterface containerInterface, int sequenceNumbersCount)
	{
		Collection controlCollection = containerInterface.getControlCollection();
		if (sequenceNumbersCount == 1)
		{
			containerInterface.getControlCollection().retainAll(new HashSet());
		}
		else
		{
			Iterator controlIterator = controlCollection.iterator();
			ControlInterface controlInterface;
			while (controlIterator.hasNext())
			{
				controlInterface = (ControlInterface) controlIterator.next();
				if (!controlInterface.getSequenceNumberChanged())
				{
					EntityInterface entityInterface = containerInterface.getEntity();
					entityInterface.removeAbstractAttribute(controlInterface.getAbstractAttribute());
					controlIterator.remove();
				}
			}
		}
	}

//	/**
//	 * 
//	 * @param file
//	 * @return
//	 * @throws IOException
//	 */
//	public static byte[] getBytesFromFile(File file) throws IOException
//	{
//		InputStream inputStream = new FileInputStream(file);
//
//		// Get the size of the file
//		long length = file.length();
//
//		// You cannot create an array using a long type.
//		// It needs to be an int type.
//		// Before converting to an int type, check
//		// to ensure that file is not larger than Integer.MAX_VALUE.
//		if (length > Integer.MAX_VALUE)
//		{
//			// File is too large
//		}
//
//		// Create the byte array to hold the data
//		byte[] fileInBytes = new byte[(int) length];
//
//		// Read in the bytes
//		int offset = 0;
//		int numRead = 0;
//		while (offset < fileInBytes.length && (numRead = inputStream.read(fileInBytes, offset, fileInBytes.length - offset)) >= 0)
//		{
//			offset += numRead;
//		}
//
//		// Ensure all the bytes have been read in
//		if (offset < fileInBytes.length)
//		{
//			throw new IOException("Could not completely read file " + file.getName());
//		}
//
//		// Close the input stream and return bytes
//		inputStream.close();
//		return fileInBytes;
//	}

	/**
	 *  
	 * @param containerInterface containerInterface
	 * @return List ChildList
	 * @throws DynamicExtensionsSystemException DynamicExtensionsSystemException
	 */
	public static List getChildList(ContainerInterface containerInterface) throws DynamicExtensionsSystemException
	{
		List<ControlInformationObject> childList = new ArrayList<ControlInformationObject>();
		Collection controlCollection = containerInterface.getControlCollection();

		ControlInterface controlInterface = null;
		ControlInformationObject controlInformationObject = null;
		String controlCaption = null, controlDatatype = null, controlSequenceNumber = null, controlName = null;
		ControlConfigurationsFactory controlConfigurationsFactory = ControlConfigurationsFactory.getInstance();
		if (controlCollection != null)
		{
			for (int counter = 1; counter <= controlCollection.size(); counter++)
			{
				controlInterface = DynamicExtensionsUtility.getControlBySequenceNumber(controlCollection, counter);
				if (controlInterface.getCaption() != null && !controlInterface.getCaption().equals(""))
				{
					controlCaption = controlInterface.getCaption();
					controlSequenceNumber = controlInterface.getSequenceNumber() + "";
					controlName = DynamicExtensionsUtility.getControlName(controlInterface);
					controlDatatype = getControlCaption(controlConfigurationsFactory.getControlDisplayLabel(controlName));
					controlInformationObject = new ControlInformationObject(controlCaption, controlDatatype, controlSequenceNumber);
					childList.add(controlInformationObject);
				}

			}
			DynamicExtensionsUtility.resetSequenceNumberChanged(controlCollection);
		}
		return childList;
	}

	/**
	 * 
	 * @param captionKey String captionKey
	 * @return String ControlCaption
	 */
	public static String getControlCaption(String captionKey)
	{
		if (captionKey != null)
		{
			ResourceBundle resourceBundle = ResourceBundle.getBundle("ApplicationResources");
			if (resourceBundle != null)
			{
				return resourceBundle.getString(captionKey);
			}
		}
		return null;
	}

}