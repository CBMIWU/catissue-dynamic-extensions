
package edu.common.dynamicextensions.util.parser;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import edu.common.dynamicextensions.domaininterface.CategoryInterface;
import edu.common.dynamicextensions.domaininterface.EntityGroupInterface;
import edu.common.dynamicextensions.domaininterface.EntityInterface;
import edu.common.dynamicextensions.domaininterface.userinterface.ContainerInterface;
import edu.common.dynamicextensions.domaininterface.userinterface.ControlInterface;
import edu.common.dynamicextensions.exception.DynamicExtensionsApplicationException;
import edu.common.dynamicextensions.exception.DynamicExtensionsSystemException;
import edu.common.dynamicextensions.util.CategoryGenerationUtil;
import edu.common.dynamicextensions.util.CategoryHelper;
import edu.common.dynamicextensions.util.CategoryHelperInterface;
import edu.common.dynamicextensions.util.CategoryHelperInterface.ControlEnum;

/**
 * @author kunal_kamble
 * This class creates the catgory/categories defined in 
 * the csv file.
 */
public class CategoryGenerator
{
	private CategoryFileParser categoryFileParser;

	private static final String SET = "set";

	/**
	 * @param filePath
	 * @throws DynamicExtensionsSystemException
	 * @throws FileNotFoundException 
	 */
	public CategoryGenerator(String filePath) throws DynamicExtensionsSystemException, FileNotFoundException
	{
		categoryFileParser = new CategoryCSVFileParser(filePath);
	}

	/**
	 * @return
	 * @throws DynamicExtensionsSystemException
	 * @throws DynamicExtensionsApplicationException
	 */
	public List<CategoryInterface> getCategoryList() throws DynamicExtensionsSystemException, DynamicExtensionsApplicationException
	{
		CategoryHelperInterface categoryHelper = new CategoryHelper();
		List<CategoryInterface> categoryList = new ArrayList<CategoryInterface>();
		try
		{
			while (categoryFileParser.readNext())
			{
				//first line in the categopry file is Category_Definition
				if (categoryFileParser.hasFormDefination())
				{
					continue;
				}

				//category defination in the file starts
				//1: read the category name
				CategoryInterface category = categoryHelper.getCategory(categoryFileParser.getCategoryName());

				//2:read the entity group
				categoryFileParser.readNext();
				EntityGroupInterface entityGroup = CategoryGenerationUtil.getEntityGroup(category, categoryFileParser.getEntityGroupName());

				checkForNullRefernce(entityGroup, "Entity group with name " + categoryFileParser.getEntityGroupName() + " at line number "
						+ categoryFileParser.getLineNumber() + " does not exist");

				//3:get the path represneted by ordered entity names
				categoryFileParser.readNext();
				Map<String, List<String>> paths = categoryFileParser.getPaths();

				//4:get the association names list
				Map<String, List<String>> associationNamesMap = CategoryGenerationUtil.getAssociationList(paths, entityGroup);

				List<ContainerInterface> containerCollection = new ArrayList<ContainerInterface>();

				ContainerInterface containerInterface = null;
				EntityInterface entityInterface = null;

				//5: get the selected attributes and create the controls for them 
				String displayLabel = null;
				String categoryEntityName = null;
				int sequenceNumber = 1;
				ControlInterface lastControl = null;

				while (categoryFileParser.readNext())
				{
					if (categoryFileParser.hasFormDefination())
					{
						break;
					}
					if (categoryFileParser.hasDisplayLable())
					{
						displayLabel = categoryFileParser.getDisplyLable();
						categoryEntityName = createForm(entityGroup, containerCollection, associationNamesMap, category);
						categoryFileParser.readNext();
					}

					if (categoryFileParser.hasSubcategory())
					{

						ContainerInterface sourceContainer = null;
						if (entityInterface != null)
						{
							sourceContainer = CategoryGenerationUtil.getContainerWithCategoryEntityName(containerCollection, categoryEntityName);
						}
						else
						{
							sourceContainer = CategoryGenerationUtil.getContainer(containerCollection, displayLabel);
						}

						String targetContainerCaption = categoryFileParser.getTargetContainerCaption();
						ContainerInterface targetContainer = CategoryGenerationUtil.getContainer(containerCollection, targetContainerCaption);

						checkForNullRefernce(targetContainer, "Error at Error at line No:" + categoryFileParser.getLineNumber()
								+ " Does not found subcategory with name  " + targetContainerCaption);

						String multiplicity = categoryFileParser.getMultiplicity();

						List<String> associationNameList = CategoryGenerationUtil.getAssociationNameList(associationNamesMap, CategoryGenerationUtil
								.getContainer(containerCollection, targetContainerCaption));
						checkForNullRefernce(associationNameList, "Error at line No:" + categoryFileParser.getLineNumber()
								+ " Does not found entities in the path " + "for the category entity " + targetContainerCaption);

						lastControl = categoryHelper.associateCategoryContainers(category, sourceContainer, targetContainer, associationNameList,
								CategoryGenerationUtil.getMultiplicityInNumbers(multiplicity));

					}
					else
					{

						//add control to the container
						List<String> permissibleValues = categoryFileParser.getPermissibleValues();

						String attributeName = categoryFileParser.getAttributeName();

						entityInterface = entityGroup.getEntityByName(categoryFileParser.getEntityName());
						containerInterface = CategoryGenerationUtil.getContainerWithEntityName(containerCollection, categoryFileParser
								.getEntityName());

						checkForNullRefernce(entityInterface.getAttributeByName(attributeName), "Line Number:" + categoryFileParser.getLineNumber()
								+ " attribute with name " + attributeName + " in the entity " + entityInterface.getName() + " does not exist!");

						if (permissibleValues != null)
						{
							lastControl = categoryHelper.addOrUpdateControl(entityInterface, attributeName, containerInterface, ControlEnum
									.get(categoryFileParser.getControlType()), categoryFileParser.getControlCaption(), permissibleValues);
						}
						else
						{
							checkForNullRefernce(ControlEnum.get(categoryFileParser.getControlType()), "Error at line No:"
									+ categoryFileParser.getLineNumber() + " Illegal control type " + categoryFileParser.getControlType());
							lastControl = categoryHelper.addOrUpdateControl(entityInterface, attributeName, containerInterface, ControlEnum
									.get(categoryFileParser.getControlType()), categoryFileParser.getControlCaption());
						}

						setControlsOptions(lastControl);

					}

					lastControl.setSequenceNumber(sequenceNumber++);
				}

				CategoryGenerationUtil.setRootContainer(category, containerCollection, associationNamesMap, paths);
				categoryList.add(category);
			}

		}

		catch (FileNotFoundException e)
		{
			throw new DynamicExtensionsSystemException("Error while openig CSV file ", e);
		}
		catch (IOException e)
		{
			throw new DynamicExtensionsSystemException("Error redaring CSV file " + categoryFileParser.getFilePath() + " at line "
					+ categoryFileParser.getLineNumber(), e);
		}
		return categoryList;

	}

	/**
	 * @param entityInterface
	 * @param categoryEntityName
	 * @param displayLable
	 * @param showCaption
	 * @param containerCollection
	 * @return
	 * @throws DynamicExtensionsSystemException
	 */
	private ContainerInterface createCategoryEntityAndContainer(EntityInterface entityInterface, String categoryEntityName, String displayLable,
			Boolean showCaption, Collection<ContainerInterface> containerCollection, CategoryInterface category)
			throws DynamicExtensionsSystemException
	{
		ContainerInterface containerInterface = null;
		CategoryHelperInterface categoryHelper = new CategoryHelper();

		containerInterface = categoryHelper.createOrUpdateCategoryEntityAndContainer(entityInterface, displayLable, category, categoryEntityName);

		containerInterface.setAddCaption(showCaption);

		containerCollection.add(containerInterface);

		return containerInterface;
	}

	/**
	 * @param object
	 * @param message
	 * @throws DynamicExtensionsSystemException
	 */
	private void checkForNullRefernce(Object object, String message) throws DynamicExtensionsSystemException
	{
		if (object == null)
		{
			throw new DynamicExtensionsSystemException(message);
		}
	}

	/**
	 * @param control
	 * @param nextLine
	 * @throws DynamicExtensionsSystemException
	 */
	private void setControlsOptions(ControlInterface control) throws DynamicExtensionsSystemException
	{
		try
		{
			Map<String, String> controlOptions = categoryFileParser.getControlOptions();

			if (controlOptions.isEmpty())
			{
				return;
			}
			for (String optionString : controlOptions.keySet())
			{
				String methodName = SET + optionString;

				Class[] types = getParameterType(methodName, control);
				List<Object> values = new ArrayList<Object>();
				values.add(getFormattedValues(types[0], controlOptions.get(optionString)));

				Method method;

				method = control.getClass().getMethod(methodName, types);

				method.invoke(control, values.toArray());

			}
		}
		catch (SecurityException e)
		{
			throw new DynamicExtensionsSystemException("Please conatct administartor", e);
		}
		catch (NoSuchMethodException e)
		{
			throw new DynamicExtensionsSystemException("Line number:" + categoryFileParser.getLineNumber() + "Incorrect option", e);
		}
		catch (IllegalArgumentException e)
		{
			throw new DynamicExtensionsSystemException("Please conatct administartor", e);
		}
		catch (IllegalAccessException e)
		{
			throw new DynamicExtensionsSystemException("Please conatct administartor", e);
		}
		catch (InvocationTargetException e)
		{
			throw new DynamicExtensionsSystemException("Please conatct administartor", e);
		}
		catch (InstantiationException e)
		{
			throw new DynamicExtensionsSystemException("Please conatct administartor", e);
		}

	}

	/**
	 * This meth
	 * @param methodName
	 * @param object
	 * @return
	 */
	private Class[] getParameterType(String methodName, Object object)
	{
		Class[] parameterTypes = new Class[0];
		for (Method method : object.getClass().getMethods())
		{
			if (methodName.equals(method.getName()))
			{
				parameterTypes = method.getParameterTypes();
			}
		}

		return parameterTypes;
	}

	/**
	 * @param type
	 * @param string
	 * @return
	 * @throws IllegalAccessException 
	 * @throws InstantiationException 
	 * @throws NoSuchMethodException 
	 * @throws SecurityException 
	 * @throws InvocationTargetException 
	 * @throws IllegalArgumentException 
	 */
	private Object getFormattedValues(Class type, String string) throws SecurityException, NoSuchMethodException, InstantiationException,
			IllegalAccessException, IllegalArgumentException, InvocationTargetException
	{
		Method method = type.getMethod("valueOf", new Class[]{String.class});
		return method.invoke(type, new Object[]{string});
	}

	private String createForm(EntityGroupInterface entityGroup, List<ContainerInterface> containerCollection,
			Map<String, List<String>> associationNamesMap, CategoryInterface category) throws DynamicExtensionsSystemException,
			DynamicExtensionsApplicationException
	{
		String displayLable = categoryFileParser.getDisplyLable();
		Boolean showCaption = categoryFileParser.isShowCaption();

		String categoryEntityName = null;
		String entityName = null;
		String[] categoryEntityNames;

		try
		{
			categoryFileParser.readNext();
			String[] categoryPaths = categoryFileParser.getCategoryPaths();

			if (categoryPaths.length == 1)
			{
				categoryEntityNames = categoryPaths[0].split("->");
				categoryEntityName = categoryEntityNames[categoryEntityNames.length - 1];
				entityName = categoryEntityName.substring(0, categoryEntityName.indexOf("["));

				EntityInterface entity = entityGroup.getEntityByName(entityName);
				ContainerInterface container = createCategoryEntityAndContainer(entity, categoryEntityName, displayLable, showCaption,
						containerCollection, category);

			}
			else
			{
				categoryEntityName = CategoryGenerationUtil.getMainCategoryEntityName(categoryPaths);
				ContainerInterface mainContainer = CategoryGenerationUtil.getContainerWithCategoryEntityName(containerCollection, categoryEntityName);

				entityName = categoryEntityName.substring(0, categoryEntityName.indexOf("["));
				if (mainContainer == null)
				{
					mainContainer = createCategoryEntityAndContainer(entityGroup.getEntityByName(entityName), categoryEntityName, displayLable,
							showCaption, containerCollection, category);
				}

				CategoryHelperInterface categoryHelper = new CategoryHelper();
				for (String categoryPath : categoryPaths)
				{
					categoryEntityNames = categoryPath.split("->");
					categoryEntityName = categoryEntityNames[categoryEntityNames.length - 1];
					entityName = categoryEntityName.substring(0, categoryEntityName.indexOf("["));

					ContainerInterface container = createCategoryEntityAndContainer(entityGroup.getEntityByName(entityName), categoryEntityName,
							null, false, containerCollection, category);

					categoryHelper.associateCategoryContainers(category, mainContainer, container, associationNamesMap.get(entityName), 1);

				}

			}
		}
		catch (IOException exception)
		{
			throw new DynamicExtensionsSystemException("Line number:" + categoryFileParser.getLineNumber() + ": Error reading category entity path");
		}

		return categoryEntityName;
	}

}