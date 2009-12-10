
package edu.common.dynamicextensions.categoryManager;

import java.io.FileNotFoundException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import edu.common.dynamicextensions.category.CategoryCreator;
import edu.common.dynamicextensions.domain.Category;
import edu.common.dynamicextensions.domain.DomainObjectFactory;
import edu.common.dynamicextensions.domain.EntityGroup;
import edu.common.dynamicextensions.domain.PathAssociationRelationInterface;
import edu.common.dynamicextensions.domain.StringAttributeTypeInformation;
import edu.common.dynamicextensions.domain.StringValue;
import edu.common.dynamicextensions.domain.UserDefinedDE;
import edu.common.dynamicextensions.domain.userinterface.Container;
import edu.common.dynamicextensions.domaininterface.AbstractEntityInterface;
import edu.common.dynamicextensions.domaininterface.AssociationInterface;
import edu.common.dynamicextensions.domaininterface.AttributeInterface;
import edu.common.dynamicextensions.domaininterface.BaseAbstractAttributeInterface;
import edu.common.dynamicextensions.domaininterface.CategoryAssociationInterface;
import edu.common.dynamicextensions.domaininterface.CategoryAttributeInterface;
import edu.common.dynamicextensions.domaininterface.CategoryEntityInterface;
import edu.common.dynamicextensions.domaininterface.CategoryInterface;
import edu.common.dynamicextensions.domaininterface.EntityGroupInterface;
import edu.common.dynamicextensions.domaininterface.EntityInterface;
import edu.common.dynamicextensions.domaininterface.PathInterface;
import edu.common.dynamicextensions.domaininterface.PermissibleValueInterface;
import edu.common.dynamicextensions.domaininterface.RoleInterface;
import edu.common.dynamicextensions.domaininterface.UserDefinedDEInterface;
import edu.common.dynamicextensions.domaininterface.userinterface.AbstractContainmentControlInterface;
import edu.common.dynamicextensions.domaininterface.userinterface.CategoryAssociationControlInterface;
import edu.common.dynamicextensions.domaininterface.userinterface.ComboBoxInterface;
import edu.common.dynamicextensions.domaininterface.userinterface.ContainerInterface;
import edu.common.dynamicextensions.domaininterface.userinterface.TextFieldInterface;
import edu.common.dynamicextensions.domaininterface.validationrules.RuleInterface;
import edu.common.dynamicextensions.entitymanager.CategoryManager;
import edu.common.dynamicextensions.entitymanager.CategoryManagerInterface;
import edu.common.dynamicextensions.entitymanager.EntityGroupManager;
import edu.common.dynamicextensions.entitymanager.EntityGroupManagerInterface;
import edu.common.dynamicextensions.exception.DynamicExtensionsApplicationException;
import edu.common.dynamicextensions.exception.DynamicExtensionsSystemException;
import edu.common.dynamicextensions.processor.ProcessorConstants;
import edu.common.dynamicextensions.util.CiderXMIImporter;
import edu.common.dynamicextensions.util.DynamicExtensionsBaseTestCase;
import edu.common.dynamicextensions.util.DynamicExtensionsUtility;
import edu.common.dynamicextensions.util.XMIImporter;
import edu.common.dynamicextensions.util.global.CategoryConstants;
import edu.common.dynamicextensions.util.global.DEConstants.AssociationDirection;
import edu.common.dynamicextensions.util.global.DEConstants.AssociationType;
import edu.common.dynamicextensions.util.global.DEConstants.Cardinality;
import edu.common.dynamicextensions.util.parser.CategoryCSVConstants;
import edu.common.dynamicextensions.util.parser.CategoryGenerator;
import edu.common.dynamicextensions.util.parser.ImportPermissibleValues;
import edu.wustl.common.bizlogic.DefaultBizLogic;
import edu.wustl.common.exception.BizLogicException;
import edu.wustl.common.util.global.ApplicationProperties;
import edu.wustl.common.util.logger.Logger;
import edu.wustl.dao.JDBCDAO;
import edu.wustl.dao.exception.DAOException;

/**
 *
 * @author mandar_shidhore
 *
 */
public class TestCategoryManager extends DynamicExtensionsBaseTestCase
{

	/**
	 *
	 */
	public TestCategoryManager()
	{
		super();
	}

	/**
	 * @param arg0 name
	 */
	public TestCategoryManager(String arg0)
	{
		super(arg0);
	}

	/**
	 * @see edu.common.dynamicextensions.util.DynamicExtensionsBaseTestCaseUtility#setUp()
	 */
	@Override
	protected void setUp()
	{
		super.setUp();
	}

	/**
	 * @see edu.common.dynamicextensions.util.DynamicExtensionsBaseTestCaseUtility#tearDown()
	 */
	@Override
	protected void tearDown()
	{
		super.tearDown();
	}

	/**
	 *
	 *
	 */
	public void testSaveEntityGroup1()
	{
		EntityGroupManagerInterface entityGroupManager = EntityGroupManager.getInstance();

		try
		{
			EntityGroupInterface entityGroup = createEntityGroup1();
			// Save the entity group.
			entityGroupManager.persistEntityGroup(entityGroup);
		}
		catch (DynamicExtensionsSystemException e)
		{
			e.printStackTrace();
			fail();
		}
		catch (DynamicExtensionsApplicationException e)
		{
			e.printStackTrace();
			fail();
		}
	}

	/**
	 *
	 *
	 */
	public void testSaveEntityGroup2()
	{
		EntityGroupManagerInterface entityGroupManager = EntityGroupManager.getInstance();

		try
		{
			EntityGroupInterface entityGroup = createEntityGroup2();
			// Save the entity group.
			entityGroupManager.persistEntityGroup(entityGroup);
		}
		catch (DynamicExtensionsSystemException e)
		{
			e.printStackTrace();
			fail();
		}
		catch (DynamicExtensionsApplicationException e)
		{
			e.printStackTrace();
			fail();
		}
	}

	/**
	 *
	 *
	 */
	public void testSaveEntityGroup3()
	{
		EntityGroupManagerInterface entityGroupManager = EntityGroupManager.getInstance();

		try
		{
			EntityGroupInterface entityGroup = createEntityGroup3();
			// Save the entity group.
			entityGroupManager.persistEntityGroup(entityGroup);
		}
		catch (DynamicExtensionsSystemException e)
		{
			e.printStackTrace();
			fail();
		}
		catch (DynamicExtensionsApplicationException e)
		{
			e.printStackTrace();
			fail();
		}
	}

	/**
	 * Retrieve entity group by its name from database.
	 * @param name name of category
	 * @return entity group
	 */
	private EntityGroupInterface retrieveEntityGroup(String name)
	{
		DefaultBizLogic bizlogic = new DefaultBizLogic();
		Collection<EntityGroupInterface> entityGroupCollection = new HashSet<EntityGroupInterface>();
		EntityGroupInterface entityGroup = null;

		try
		{
			// Fetch the entity group from the database.
			entityGroupCollection = bizlogic.retrieve(EntityGroup.class.getName(), "shortName",
					name);

			if (entityGroupCollection != null && entityGroupCollection.size() > 0)
			{
				entityGroup = entityGroupCollection.iterator().next();
			}
		}
		catch (BizLogicException e)
		{
			e.printStackTrace();
			fail();
		}

		return entityGroup;
	}

	public void testImportInvalidPVForCategory()
	{
		ApplicationProperties.initBundle(CategoryCSVConstants.DYEXTN_ERROR_MESSAGES_FILE);

		try
		{
			String[] args1 = {XMI_FILE_PATH+"newsurgery.xmi",CSV_FILE_PATH+"surgery.csv","Surgery","  "};
			XMIImporter.main(args1);

			String[] args2 = {CSV_FILE_PATH+"cat_RULES.csv"};
			CategoryCreator categoryCreator = new CategoryCreator();
			categoryCreator.main(args2);

			fail();
		}
		catch (Exception e)
		{
			assertTrue(e.getMessage().indexOf(
					ApplicationProperties.getValue(CategoryConstants.NO_PV_FOR_ATTR)) != -1);
		}
	}

	/**
	 * Create a category from vitals entity and its attributes.
	 *
	 */
	/*public void testCreateVitalsCategory()
	{
		try
		{
			EntityGroupInterface entityGroup = retrieveEntityGroup("gcrc");

			// Get the VitalSigns entity from entity group.
			EntityInterface vitals = entityGroup.getEntityByName("VitalSigns");

			// Create category.
			CategoryHelperInterface categoryHelper = new CategoryHelper();

			CategoryInterface category = categoryHelper.getCategory("Vitals Category");

			// Create category entity from VitalSigns entity.
			ContainerInterface vitalsContainer = categoryHelper.createOrUpdateCategoryEntityAndContainer(vitals, "Vitals", category);

			// Set the root category entity.
			categoryHelper.setRootCategoryEntity(vitalsContainer, category);

			// Create category attribute(s) for VitalSigns category entity.
			categoryHelper.addOrUpdateControl(vitals, "heartRate", vitalsContainer, ControlEnum.TEXT_FIELD_CONTROL, "Pulse");
			categoryHelper.addOrUpdateControl(vitals, "diastolicBloodPressure", vitalsContainer, ControlEnum.TEXT_FIELD_CONTROL, "Diastolic BP");
			categoryHelper.addOrUpdateControl(vitals, "systolicBloodPressure", vitalsContainer, ControlEnum.TEXT_FIELD_CONTROL, "Systolic BP");
			categoryHelper.addOrUpdateControl(vitals, "weight", vitalsContainer, ControlEnum.TEXT_FIELD_CONTROL, "Weight (kg)");
			categoryHelper.addOrUpdateControl(vitals, "height", vitalsContainer, ControlEnum.TEXT_FIELD_CONTROL, "Height (cm)");
			categoryHelper.addOrUpdateControl(vitals, "respiratoryRate", vitalsContainer, ControlEnum.TEXT_FIELD_CONTROL, "Respiration");

			// Save the category.
			categoryHelper.saveCategory(category);
		}
		catch (DynamicExtensionsSystemException e)
		{
			e.printStackTrace();
			fail();
		}
		catch (DynamicExtensionsApplicationException e)
		{
			e.printStackTrace();
			fail();
		}
	}*/

	/**
	 * Entities : user (1)------>(*) study
	 *
	 * Category: Make 2 category entities, choosing attributes from user and study.
	 * Persist the category.
	 */
	/*public void testCreateAndSaveCategoryWithAttributesFromTwoEntities()
	 {
	 try
	 {
	 // Fetch the entity group from the database.
	 DefaultBizLogic bizlogic = new DefaultBizLogic();
	 Collection<EntityGroupInterface> entityGroupCollection = new HashSet<EntityGroupInterface>();
	 EntityGroupInterface entityGroup = null;

	 entityGroupCollection = bizlogic.retrieve(EntityGroup.class.getName(), "shortName", "User-Study EG1");

	 if (entityGroupCollection != null && entityGroupCollection.size() > 0)
	 {
	 entityGroup = (EntityGroupInterface) entityGroupCollection.iterator().next();
	 }

	 // Fetch the user and study entities
	 EntityInterface user = entityGroup.getEntityByName("User Entity");
	 EntityInterface study = entityGroup.getEntityByName("Study Entity");

	 ResultSetMetaData metaData = executeQueryForMetadata("select * from " + study.getTableProperties().getName());
	 assertEquals(metaData.getColumnCount(), noOfDefaultColumns + 4);

	 CategoryHelper categoryHelper = new CategoryHelper();

	 CategoryInterface category = categoryHelper.createCategory("Category From User and Study Entities");

	 // Create category entity from user entity.
	 ContainerInterface userCategoryContainer = categoryHelper.createCategoryEntityAndContainer(user, "User");
	 categoryHelper.setRootCategoryEntity(userCategoryContainer, category);

	 // Create category attribute(s) for user category entity.
	 //categoryHelper.addControl(user, "User Name", userCategoryContainer, ControlEnum.TEXT_FIELD_CONTROL);

	 // Permissible values.
	 List<String> pvList = new ArrayList<String>();
	 pvList.add("Permissible Value 1");
	 pvList.add("Permissible Value 2");
	 pvList.add("Permissible Value 3");

	 // Create category attribute(s) for user category entity.
	 categoryHelper.addControl(user, "User Name", userCategoryContainer, ControlEnum.LIST_BOX_CONTROL, "User Name", pvList);

	 // Create category entity from study entity.
	 ContainerInterface studyCategoryContainer = categoryHelper.createCategoryEntityAndContainer(study, "Study");
	 studyCategoryContainer.setAddCaption(false);

	 // Create category attribute(s) for user category entity.
	 categoryHelper.addControl(study, "Study Name", studyCategoryContainer, ControlEnum.TEXT_FIELD_CONTROL, "Study Name");

	 List<String> list = new ArrayList<String>();
	 list.add("primaryInvestigator");

	 categoryHelper.associateCategoryContainers(userCategoryContainer, studyCategoryContainer, list, -1);

	 // Save the category.
	 categoryHelper.saveCategory(category);
	 }
	 catch (DynamicExtensionsSystemException e)
	 {
	 e.printStackTrace();
	 fail();
	 }
	 catch (DynamicExtensionsApplicationException e)
	 {
	 e.printStackTrace();
	 fail();
	 }
	 catch (SQLException e)
	 {
	 e.printStackTrace();
	 fail();
	 }
	 catch (DAOException e)
	 {
	 e.printStackTrace();
	 fail();
	 }
	 }

	 public void testCreateCategoryFromModel()
	 {
	 try
	 {
	 // Fetch the entity group from the database.
	 DefaultBizLogic bizlogic = new DefaultBizLogic();
	 Collection<EntityGroupInterface> entityGroupCollection = new HashSet<EntityGroupInterface>();
	 EntityGroupInterface entityGroup = null;

	 entityGroupCollection = bizlogic.retrieve(EntityGroup.class.getName(), "shortName", "Needle Biopsy EG");

	 if (entityGroupCollection != null && entityGroupCollection.size() > 0)
	 {
	 entityGroup = (EntityGroupInterface) entityGroupCollection.iterator().next();
	 }

	 // Fetch the user and study entities
	 EntityInterface baseAnnotation = entityGroup.getEntityByName("BaseAnnotation");
	 EntityInterface baseTissuePathoAnno = entityGroup.getEntityByName("BaseTissuePathologyAnnotation");
	 EntityInterface prostateAnnotation = entityGroup.getEntityByName("ProstateAnnotation");
	 EntityInterface histology = entityGroup.getEntityByName("Histology");
	 EntityInterface additionalFinding = entityGroup.getEntityByName("AdditionalFinding");
	 EntityInterface invasion = entityGroup.getEntityByName("Invasion");
	 EntityInterface gleasonScore = entityGroup.getEntityByName("GleasonScore");
	 EntityInterface tumorQuant = entityGroup.getEntityByName("TumorQuantitation");
	 EntityInterface varHistoType = entityGroup.getEntityByName("VariantHistologicType");

	 // Create category.
	 CategoryHelper categoryHelper = new CategoryHelper();

	 CategoryInterface category = categoryHelper.createCategory("Needle Biopsy Pathology Annotation");

	 List<String> pvList = new ArrayList<String>();
	 pvList.add("Permissible Value 1");
	 pvList.add("Permissible Value 2");
	 pvList.add("Permissible Value 3");

	 // Create category entity from user entity.
	 ContainerInterface baseAnnotationContainer = categoryHelper.createCategoryEntityAndContainer(baseAnnotation, "Base Annotation");

	 // Create category entity from user entity.
	 ContainerInterface baseTissuePathoAnnoContainer = categoryHelper.createCategoryEntityAndContainer(baseTissuePathoAnno, "Base Tissue Patholgy Annotation");

	 // Create category entity from user entity.
	 ContainerInterface prostateAnnotationContainer = categoryHelper.createCategoryEntityAndContainer(prostateAnnotation, "Prostate Annotation");
	 categoryHelper.setRootCategoryEntity(prostateAnnotationContainer, category);

	 // Create category attribute(s) for needleBioProPathAnno category entity.
	 categoryHelper.addControl(prostateAnnotation, "specimenProcedoure", prostateAnnotationContainer, ControlEnum.LIST_BOX_CONTROL, "specimenProcedoure", pvList);
	 categoryHelper.addControl(prostateAnnotation, "procedureDate", prostateAnnotationContainer, ControlEnum.DATE_PICKER_CONTROL, "procedureDate");
	 categoryHelper.addControl(prostateAnnotation, "procedureDetailsDocument", prostateAnnotationContainer, ControlEnum.TEXT_FIELD_CONTROL, "procedureDetailsDocument");
	 categoryHelper.addControl(prostateAnnotation, "comments", prostateAnnotationContainer, ControlEnum.TEXT_AREA_CONTROL, "comments");

	 // Create category entity from  entity.
	 ContainerInterface histologyContainer = categoryHelper.createCategoryEntityAndContainer(histology, "Histology");

	 // Create category attribute(s) for histology category entity.
	 categoryHelper.addControl(histology, "type", histologyContainer, ControlEnum.LIST_BOX_CONTROL, "type", pvList);

	 // Create category entity from  entity.
	 ContainerInterface variantHistologicContainer = categoryHelper.createCategoryEntityAndContainer(varHistoType, "Variant Histology Type");

	 //  Create category attribute(s) for varHistoType category entity.
	 categoryHelper.addControl(varHistoType, "variantType", variantHistologicContainer, ControlEnum.TEXT_FIELD_CONTROL, "variantType");

	 // Create category entity from  entity.
	 ContainerInterface additionalFindingContainer = categoryHelper.createCategoryEntityAndContainer(additionalFinding, "Additional Finding");

	 // Create category attribute(s) for histology category entity.
	 categoryHelper.addControl(additionalFinding, "Detail", additionalFindingContainer, ControlEnum.LIST_BOX_CONTROL, "Detail", pvList);

	 // Create category entity from  entity.
	 ContainerInterface invasionContainer = categoryHelper.createCategoryEntityAndContainer(invasion, "Invasion");
	 // Create category attribute(s) for histology category entity.

	 List<String> lymInvValues = new ArrayList<String>();
	 lymInvValues.add("Absent");
	 lymInvValues.add("Indereminate");
	 lymInvValues.add("Not Specified");
	 lymInvValues.add("Present");
	 lymInvValues.add("Present External");
	 lymInvValues.add("Present Internal");

	 categoryHelper.addControl(invasion, "lymphaticInvasion", invasionContainer, ControlEnum.RADIO_BUTTON_CONTROL, "lymphaticInvasion", lymInvValues);

	 List<String> perInvValues = new ArrayList<String>();
	 perInvValues.add("Absent");
	 perInvValues.add("Indereminate");
	 perInvValues.add("Not Specified");
	 perInvValues.add("Present");
	 categoryHelper.addControl(invasion, "perneuralInvasion", invasionContainer, ControlEnum.RADIO_BUTTON_CONTROL, "perneuralInvasion", perInvValues);

	 // Create category entity from  entity.
	 ContainerInterface gleasonContainer = categoryHelper.createCategoryEntityAndContainer(gleasonScore, "Gleason Score");

	 // Create category attribute(s) for histology category entity.
	 categoryHelper.addControl(gleasonScore, "primaryPattern", gleasonContainer, ControlEnum.TEXT_FIELD_CONTROL, "primaryPattern");
	 categoryHelper.addControl(gleasonScore, "secondaryPattern", gleasonContainer, ControlEnum.TEXT_FIELD_CONTROL, "secondaryPattern");
	 categoryHelper.addControl(gleasonScore, "tertiaryPattern", gleasonContainer, ControlEnum.TEXT_FIELD_CONTROL, "tertiaryPattern");

	 // Create category entity from  entity.
	 ContainerInterface tumorQuantContainer = categoryHelper.createCategoryEntityAndContainer(tumorQuant, "Tumor Quantitation");

	 // Create category attribute(s) for histology category entity.
	 categoryHelper.addControl(tumorQuant, "totalLengthOfCarcinomaInMilimeters", tumorQuantContainer, ControlEnum.TEXT_FIELD_CONTROL, "totalLengthOfCarcinomaInMilimeters");
	 categoryHelper.addControl(tumorQuant, "totalLengthOfCoresInMilimeters", tumorQuantContainer, ControlEnum.TEXT_FIELD_CONTROL, "totalLengthOfCoresInMilimeters");
	 categoryHelper.addControl(tumorQuant, "totalNumberOfPositiveCores", tumorQuantContainer, ControlEnum.TEXT_FIELD_CONTROL, "totalNumberOfPositiveCores");
	 categoryHelper.addControl(tumorQuant, "totalNumberOfCores", tumorQuantContainer, ControlEnum.TEXT_FIELD_CONTROL, "totalNumberOfCores");

	 // Set appropriate child category entities.
	 categoryHelper.setParentContainer(baseAnnotationContainer, baseTissuePathoAnnoContainer);
	 categoryHelper.setParentContainer(baseTissuePathoAnnoContainer, prostateAnnotationContainer);

	 List<String> list = new ArrayList<String>();
	 list.add("histology");
	 CategoryAssociationControlInterface associationControlInterface = categoryHelper.associateCategoryContainers(baseAnnotationContainer,
	 histologyContainer, list, -1);
	 associationControlInterface.setSequenceNumber(categoryHelper.getNextSequenceNumber(prostateAnnotationContainer));

	 list = new ArrayList<String>();
	 list.add("additionalFinding");
	 CategoryAssociationControlInterface associationControlInterface2 = categoryHelper.associateCategoryContainers(baseAnnotationContainer,
	 additionalFindingContainer, list, -1);
	 associationControlInterface2.setSequenceNumber(categoryHelper.getNextSequenceNumber(prostateAnnotationContainer));

	 list = new ArrayList<String>();
	 list.add("invasion");
	 CategoryAssociationControlInterface associationControlInterface3 = categoryHelper.associateCategoryContainers(
	 baseTissuePathoAnnoContainer, invasionContainer, list, 1);
	 associationControlInterface3.setSequenceNumber(categoryHelper.getNextSequenceNumber(prostateAnnotationContainer));

	 list = new ArrayList<String>();
	 list.add("gleasonScore");
	 categoryHelper.associateCategoryContainers(prostateAnnotationContainer, gleasonContainer, list, 1);

	 list = new ArrayList<String>();
	 list.add("tumorQuantitation");
	 categoryHelper.associateCategoryContainers(prostateAnnotationContainer, tumorQuantContainer, list, 1);

	 list = new ArrayList<String>();
	 list.add("variantHistology");
	 categoryHelper.associateCategoryContainers(histologyContainer, variantHistologicContainer, list, 1);

	 // Save the category.
	 categoryHelper.saveCategory(category);
	 }
	 catch (DynamicExtensionsSystemException e)
	 {
	 e.printStackTrace();
	 fail();
	 }
	 catch (DynamicExtensionsApplicationException e)
	 {
	 e.printStackTrace();
	 fail();
	 }
	 catch (DAOException e)
	 {
	 e.printStackTrace();
	 fail();
	 }
	 }

	 public void testCreateBodyCompositionCategory()
	 {
	 try
	 {
	 // Fetch the entity group from the database.
	 DefaultBizLogic bizlogic = new DefaultBizLogic();
	 Collection<EntityGroupInterface> entityGroupCollection = new HashSet<EntityGroupInterface>();
	 EntityGroupInterface entityGroup = null;

	 entityGroupCollection = bizlogic.retrieve(EntityGroup.class.getName(), "shortName", "EG");

	 if (entityGroupCollection != null && entityGroupCollection.size() > 0)
	 {
	 entityGroup = (EntityGroupInterface) entityGroupCollection.iterator().next();
	 }

	 // Fetch the vitals entity.
	 EntityInterface visit = entityGroup.getEntityByName("visit");
	 EntityInterface bodyComposition = entityGroup.getEntityByName("BodyComposition");

	 // Create category.
	 CategoryHelper categoryHelper = new CategoryHelper();

	 CategoryInterface category = categoryHelper.createCategory("Body Composition Category");

	 // Create category entity from vitals entity.
	 ContainerInterface visitContainer = categoryHelper.createCategoryEntityAndContainer(visit, "Visit");

	 // Create category entity from bodyComposition entity.
	 ContainerInterface bodyCompositionContainer = categoryHelper.createCategoryEntityAndContainer(bodyComposition, "Body Composition");

	 List<String> permissibleValuesList1 = new ArrayList<String>();
	 permissibleValuesList1.add("BMI");
	 permissibleValuesList1.add("Body fat(%)");
	 permissibleValuesList1.add("Body fat(kg)");
	 permissibleValuesList1.add("Fat free mass(kg)");
	 permissibleValuesList1.add("Liver fat(%)");
	 permissibleValuesList1.add("Muscle fat(kg)");
	 permissibleValuesList1.add("Subcu Abdom fat(cubic cm)");
	 permissibleValuesList1.add("Intra Abdom fat(cubic cm)");

	 List<String> permissibleValuesList2 = new ArrayList<String>();
	 permissibleValuesList2.add("DEXA");
	 permissibleValuesList2.add("MRI");

	 // Create category attribute(s) for bodyComposition category entity.
	 categoryHelper.addControl(bodyComposition, "result", bodyCompositionContainer, ControlEnum.LIST_BOX_CONTROL, "result", permissibleValuesList1);
	 categoryHelper.addControl(bodyComposition, "source", bodyCompositionContainer, ControlEnum.TEXT_FIELD_CONTROL, "source");
	 categoryHelper.addControl(bodyComposition, "testName", bodyCompositionContainer, ControlEnum.LIST_BOX_CONTROL, "testName", permissibleValuesList2);

	 // Set appropriate child category entities.
	 categoryHelper.setParentContainer(visitContainer, bodyCompositionContainer);

	 List<String> list = new ArrayList<String>();
	 list.add("bodyComposition");
	 categoryHelper.associateCategoryContainers(visitContainer, bodyCompositionContainer, list, -1);

	 // Save the category.
	 categoryHelper.saveCategory(category);
	 }
	 catch (DynamicExtensionsSystemException e)
	 {
	 e.printStackTrace();
	 fail();
	 }
	 catch (DynamicExtensionsApplicationException e)
	 {
	 e.printStackTrace();
	 fail();
	 }
	 catch (DAOException e)
	 {
	 e.printStackTrace();
	 fail();
	 }
	 }

	 public void testCreateLipidCMPCategory()
	 {
	 try
	 {
	 // Fetch the entity group from the database.
	 DefaultBizLogic bizlogic = new DefaultBizLogic();
	 Collection<EntityGroupInterface> entityGroupCollection = new HashSet<EntityGroupInterface>();
	 EntityGroupInterface entityGroup = null;

	 entityGroupCollection = bizlogic.retrieve(EntityGroup.class.getName(), "shortName", "EG");

	 if (entityGroupCollection != null && entityGroupCollection.size() > 0)
	 {
	 entityGroup = (EntityGroupInterface) entityGroupCollection.iterator().next();
	 }

	 // Fetch the vitals entity.
	 EntityInterface visit = entityGroup.getEntityByName("visit");
	 EntityInterface ClinicalAnnotationLabAnnotation = entityGroup.getEntityByName("ClinicalAnnotationLabAnnotation");

	 // Create category.
	 CategoryHelper categoryHelper = new CategoryHelper();

	 CategoryInterface category = categoryHelper.createCategory("LipidCMP Category");

	 // Create category entity from ClinicalAnnotationLabAnnotation entity.
	 ContainerInterface visitContainer = categoryHelper.createCategoryEntityAndContainer(visit, "Visit");
	 ContainerInterface clinAnnoLabAnnoContainer1 = categoryHelper.createCategoryEntityAndContainer(ClinicalAnnotationLabAnnotation, "Clinical Annotation Lab Annotation");
	 ContainerInterface clinAnnoLabAnnoContainer2 = categoryHelper.createCategoryEntityAndContainer(ClinicalAnnotationLabAnnotation, "Clinical Annotation Lab Annotation");

	 List<String> permissibleValuesList1 = new ArrayList<String>();
	 permissibleValuesList1.add("Cholesterol");
	 permissibleValuesList1.add("HDL cholesterol");
	 permissibleValuesList1.add("LDL cholesterol");
	 permissibleValuesList1.add("Triglycerides");

	 List<String> permissibleValuesList2 = new ArrayList<String>();
	 permissibleValuesList2.add("Glu");
	 permissibleValuesList2.add("Na");
	 permissibleValuesList2.add("K");
	 permissibleValuesList2.add("Cl");
	 permissibleValuesList2.add("Crt");
	 permissibleValuesList2.add("BUN");
	 permissibleValuesList2.add("Tot Prot");
	 permissibleValuesList2.add("Tot Bili");
	 permissibleValuesList2.add("Alk Phos");
	 permissibleValuesList2.add("AST");
	 permissibleValuesList2.add("ALT");
	 permissibleValuesList2.add("Ca");

	 // Create category attribute(s) for bodyComposition category entity.
	 categoryHelper.addControl(ClinicalAnnotationLabAnnotation, "labTestName", clinAnnoLabAnnoContainer1, ControlEnum.LIST_BOX_CONTROL, "", permissibleValuesList1);
	 categoryHelper.addControl(ClinicalAnnotationLabAnnotation, "quantitativeResult", clinAnnoLabAnnoContainer1, ControlEnum.TEXT_FIELD_CONTROL, "");
	 categoryHelper.addControl(ClinicalAnnotationLabAnnotation, "resultUnits", clinAnnoLabAnnoContainer1, ControlEnum.TEXT_FIELD_CONTROL, "");
	 categoryHelper.addControl(ClinicalAnnotationLabAnnotation, "permissibleLowerLimit", clinAnnoLabAnnoContainer1, ControlEnum.TEXT_FIELD_CONTROL, "");
	 categoryHelper.addControl(ClinicalAnnotationLabAnnotation, "permissibleUpperLimit", clinAnnoLabAnnoContainer1, ControlEnum.TEXT_FIELD_CONTROL, "");

	 // Create category attribute(s) for bodyComposition category entity.
	 categoryHelper.addControl(ClinicalAnnotationLabAnnotation, "labTestName", clinAnnoLabAnnoContainer2, ControlEnum.LIST_BOX_CONTROL, "", permissibleValuesList2);
	 categoryHelper.addControl(ClinicalAnnotationLabAnnotation, "quantitativeResult", clinAnnoLabAnnoContainer2, ControlEnum.TEXT_FIELD_CONTROL, "");
	 categoryHelper.addControl(ClinicalAnnotationLabAnnotation, "resultUnits", clinAnnoLabAnnoContainer2, ControlEnum.TEXT_FIELD_CONTROL, "");
	 categoryHelper.addControl(ClinicalAnnotationLabAnnotation, "permissibleLowerLimit", clinAnnoLabAnnoContainer2, ControlEnum.TEXT_FIELD_CONTROL, "");
	 categoryHelper.addControl(ClinicalAnnotationLabAnnotation, "permissibleUpperLimit", clinAnnoLabAnnoContainer2, ControlEnum.TEXT_FIELD_CONTROL, "");

	 // Set appropriate child category entities.
	 categoryHelper.setParentContainer(visitContainer, clinAnnoLabAnnoContainer1);
	 categoryHelper.setParentContainer(visitContainer, clinAnnoLabAnnoContainer2);

	 List<String> list = new ArrayList<String>();
	 list.add("lipidPanel");
	 categoryHelper.associateCategoryContainers(visitContainer, clinAnnoLabAnnoContainer1, list, -1);

	 list = new ArrayList<String>();
	 list.add("CMP");
	 categoryHelper.associateCategoryContainers(visitContainer, clinAnnoLabAnnoContainer2, list, -1);

	 // Save the category.
	 categoryHelper.saveCategory(category);
	 }
	 catch (DynamicExtensionsSystemException e)
	 {
	 e.printStackTrace();
	 fail();
	 }
	 catch (DynamicExtensionsApplicationException e)
	 {
	 e.printStackTrace();
	 fail();
	 }
	 catch (DAOException e)
	 {
	 e.printStackTrace();
	 fail();
	 }
	 }

	 public void testCreateCMPCBCCategory()
	 {
	 try
	 {
	 // Fetch the entity group from the database.
	 DefaultBizLogic bizlogic = new DefaultBizLogic();
	 Collection<EntityGroupInterface> entityGroupCollection = new HashSet<EntityGroupInterface>();
	 EntityGroupInterface entityGroup = null;

	 entityGroupCollection = bizlogic.retrieve(EntityGroup.class.getName(), "shortName", "EG");

	 if (entityGroupCollection != null && entityGroupCollection.size() > 0)
	 {
	 entityGroup = (EntityGroupInterface) entityGroupCollection.iterator().next();
	 }

	 // Fetch the vitals entity.
	 EntityInterface visit = entityGroup.getEntityByName("visit");
	 EntityInterface ClinicalAnnotationLabAnnotation = entityGroup.getEntityByName("ClinicalAnnotationLabAnnotation");

	 // Create category.
	 CategoryHelper categoryHelper = new CategoryHelper();

	 CategoryInterface category = categoryHelper.createCategory("CMPCBC Category");

	 // Create category entity from ClinicalAnnotationLabAnnotation entity.
	 ContainerInterface visitContainer = categoryHelper.createCategoryEntityAndContainer(visit, "Visit");
	 ContainerInterface clinAnnoLabAnnoContainer1 = categoryHelper.createCategoryEntityAndContainer(ClinicalAnnotationLabAnnotation, "Clinical Annotation Lab Annotation");
	 ContainerInterface clinAnnoLabAnnoContainer2 = categoryHelper.createCategoryEntityAndContainer(ClinicalAnnotationLabAnnotation, "Clinical Annotation Lab Annotation");

	 List<String> permissibleValuesList1 = new ArrayList<String>();
	 permissibleValuesList1.add("Glu");
	 permissibleValuesList1.add("Na");
	 permissibleValuesList1.add("K");
	 permissibleValuesList1.add("Cl");
	 permissibleValuesList1.add("Crt");
	 permissibleValuesList1.add("BUN");
	 permissibleValuesList1.add("Tot Prot");
	 permissibleValuesList1.add("Tot Bili");
	 permissibleValuesList1.add("Alk Phos");
	 permissibleValuesList1.add("AST");
	 permissibleValuesList1.add("ALT");
	 permissibleValuesList1.add("Ca");

	 List<String> permissibleValuesList2 = new ArrayList<String>();
	 permissibleValuesList2.add("Hct");
	 permissibleValuesList2.add("Hgb");
	 permissibleValuesList2.add("MCHC");
	 permissibleValuesList2.add("MCV");
	 permissibleValuesList2.add("RBC");
	 permissibleValuesList2.add("WBC");

	 // Create category attribute(s) for bodyComposition category entity.
	 categoryHelper.addControl(ClinicalAnnotationLabAnnotation, "labTestName", clinAnnoLabAnnoContainer1, ControlEnum.LIST_BOX_CONTROL, "", permissibleValuesList1);
	 categoryHelper.addControl(ClinicalAnnotationLabAnnotation, "quantitativeResult", clinAnnoLabAnnoContainer1, ControlEnum.TEXT_FIELD_CONTROL, "");
	 categoryHelper.addControl(ClinicalAnnotationLabAnnotation, "resultUnits", clinAnnoLabAnnoContainer1, ControlEnum.TEXT_FIELD_CONTROL, "");
	 categoryHelper.addControl(ClinicalAnnotationLabAnnotation, "permissibleLowerLimit", clinAnnoLabAnnoContainer1, ControlEnum.TEXT_FIELD_CONTROL, "");
	 categoryHelper.addControl(ClinicalAnnotationLabAnnotation, "permissibleUpperLimit", clinAnnoLabAnnoContainer1, ControlEnum.TEXT_FIELD_CONTROL, "");

	 // Create category attribute(s) for bodyComposition category entity.
	 categoryHelper.addControl(ClinicalAnnotationLabAnnotation, "labTestName", clinAnnoLabAnnoContainer2, ControlEnum.LIST_BOX_CONTROL, "", permissibleValuesList2);
	 categoryHelper.addControl(ClinicalAnnotationLabAnnotation, "quantitativeResult", clinAnnoLabAnnoContainer2, ControlEnum.TEXT_FIELD_CONTROL, "");
	 categoryHelper.addControl(ClinicalAnnotationLabAnnotation, "resultUnits", clinAnnoLabAnnoContainer2, ControlEnum.TEXT_FIELD_CONTROL, "");
	 categoryHelper.addControl(ClinicalAnnotationLabAnnotation, "permissibleLowerLimit", clinAnnoLabAnnoContainer2, ControlEnum.TEXT_FIELD_CONTROL, "");
	 categoryHelper.addControl(ClinicalAnnotationLabAnnotation, "permissibleUpperLimit", clinAnnoLabAnnoContainer2, ControlEnum.TEXT_FIELD_CONTROL, "");

	 // Set appropriate child category entities.
	 categoryHelper.setParentContainer(visitContainer, clinAnnoLabAnnoContainer1);
	 categoryHelper.setParentContainer(visitContainer, clinAnnoLabAnnoContainer2);

	 List<String> list = new ArrayList<String>();
	 list.add("CMP");
	 categoryHelper.associateCategoryContainers(visitContainer, clinAnnoLabAnnoContainer1, list, -1);

	 list = new ArrayList<String>();
	 list.add("CBC");
	 categoryHelper.associateCategoryContainers(visitContainer, clinAnnoLabAnnoContainer2, list, -1);

	 // Save the category.
	 categoryHelper.saveCategory(category);
	 }
	 catch (DynamicExtensionsSystemException e)
	 {
	 e.printStackTrace();
	 fail();
	 }
	 catch (DynamicExtensionsApplicationException e)
	 {
	 e.printStackTrace();
	 fail();
	 }
	 catch (DAOException e)
	 {
	 e.printStackTrace();
	 fail();
	 }
	 }

	 public void testCreateMetabTestCategory()
	 {
	 try
	 {
	 // Fetch the entity group from the database.
	 DefaultBizLogic bizlogic = new DefaultBizLogic();
	 Collection<EntityGroupInterface> entityGroupCollection = new HashSet<EntityGroupInterface>();
	 EntityGroupInterface entityGroup = null;

	 entityGroupCollection = bizlogic.retrieve(EntityGroup.class.getName(), "shortName", "EG");

	 if (entityGroupCollection != null && entityGroupCollection.size() > 0)
	 {
	 entityGroup = (EntityGroupInterface) entityGroupCollection.iterator().next();
	 }

	 // Fetch the vitals entity.
	 EntityInterface visit = entityGroup.getEntityByName("visit");
	 EntityInterface ClinicalAnnotationLabAnnotation = entityGroup.getEntityByName("ClinicalAnnotationLabAnnotation");

	 // Create category.
	 CategoryHelper categoryHelper = new CategoryHelper();

	 CategoryInterface category = categoryHelper.createCategory("MetabTest Category");

	 // Create category entity from ClinicalAnnotationLabAnnotation entity.
	 ContainerInterface visitContainer = categoryHelper.createCategoryEntityAndContainer(visit, "Visit");
	 ContainerInterface clinAnnoLabAnnoContainer1 = categoryHelper.createCategoryEntityAndContainer(ClinicalAnnotationLabAnnotation, "Clinical Annotation Lab Annotation");
	 ContainerInterface clinAnnoLabAnnoContainer2 = categoryHelper.createCategoryEntityAndContainer(ClinicalAnnotationLabAnnotation, "Clinical Annotation Lab Annotation");

	 List<String> permissibleValuesList1 = new ArrayList<String>();
	 permissibleValuesList1.add("Glucose conc-fasting");
	 permissibleValuesList1.add("Glucose conc-2 hr fasting");
	 permissibleValuesList1.add("FFA conc-fasting");
	 permissibleValuesList1.add("Insulin conc-fasting");
	 permissibleValuesList1.add("HOMA-IR");


	 List<String> permissibleValuesList2 = new ArrayList<String>();
	 permissibleValuesList2.add("hour 0 - Baseline");
	 permissibleValuesList2.add("hour 1");
	 permissibleValuesList2.add("hour 2");
	 permissibleValuesList2.add("hour 3");
	 permissibleValuesList2.add("hour 4");

	 // Create category attribute(s) for bodyComposition category entity.
	 categoryHelper.addControl(ClinicalAnnotationLabAnnotation, "labTestName", clinAnnoLabAnnoContainer1, ControlEnum.LIST_BOX_CONTROL, "", permissibleValuesList1);
	 categoryHelper.addControl(ClinicalAnnotationLabAnnotation, "quantitativeResult", clinAnnoLabAnnoContainer1, ControlEnum.TEXT_FIELD_CONTROL, "");
	 categoryHelper.addControl(ClinicalAnnotationLabAnnotation, "resultUnits", clinAnnoLabAnnoContainer1, ControlEnum.TEXT_FIELD_CONTROL, "");
	 categoryHelper.addControl(ClinicalAnnotationLabAnnotation, "permissibleLowerLimit", clinAnnoLabAnnoContainer1, ControlEnum.TEXT_FIELD_CONTROL, "");
	 categoryHelper.addControl(ClinicalAnnotationLabAnnotation, "permissibleUpperLimit", clinAnnoLabAnnoContainer1, ControlEnum.TEXT_FIELD_CONTROL, "");

	 // Create category attribute(s) for bodyComposition category entity.
	 categoryHelper.addControl(ClinicalAnnotationLabAnnotation, "labTestName", clinAnnoLabAnnoContainer2, ControlEnum.LIST_BOX_CONTROL, "", permissibleValuesList2);
	 categoryHelper.addControl(ClinicalAnnotationLabAnnotation, "quantitativeResult", clinAnnoLabAnnoContainer2, ControlEnum.TEXT_FIELD_CONTROL, "");

	 // Set appropriate child category entities.
	 categoryHelper.setParentContainer(visitContainer, clinAnnoLabAnnoContainer1);
	 categoryHelper.setParentContainer(visitContainer, clinAnnoLabAnnoContainer2);

	 List<String> list = new ArrayList<String>();
	 list.add("CMP");
	 categoryHelper.associateCategoryContainers(visitContainer, clinAnnoLabAnnoContainer1, list, -1);

	 list = new ArrayList<String>();
	 list.add("CBC");
	 categoryHelper.associateCategoryContainers(visitContainer, clinAnnoLabAnnoContainer2, list, -1);

	 // Save the category.
	 categoryHelper.saveCategory(category);
	 }
	 catch (DynamicExtensionsSystemException e)
	 {
	 e.printStackTrace();
	 fail();
	 }
	 catch (DynamicExtensionsApplicationException e)
	 {
	 e.printStackTrace();
	 fail();
	 }
	 catch (DAOException e)
	 {
	 e.printStackTrace();
	 fail();
	 }
	 }

	 public void testCreateClinicalDXCategory()
	 {
	 try
	 {
	 // Fetch the entity group from the database.
	 DefaultBizLogic bizlogic = new DefaultBizLogic();
	 Collection<EntityGroupInterface> entityGroupCollection = new HashSet<EntityGroupInterface>();
	 EntityGroupInterface entityGroup = null;

	 entityGroupCollection = bizlogic.retrieve(EntityGroup.class.getName(), "shortName", "EG");

	 if (entityGroupCollection != null && entityGroupCollection.size() > 0)
	 {
	 entityGroup = (EntityGroupInterface) entityGroupCollection.iterator().next();
	 }

	 // Fetch the vitals entity.
	 EntityInterface visit = entityGroup.getEntityByName("visit");
	 EntityInterface clinicalDiagnosis = entityGroup.getEntityByName("ClinicalDiagnosis");

	 // Create category.
	 CategoryHelper categoryHelper = new CategoryHelper();

	 CategoryInterface category = categoryHelper.createCategory("ClinicalDX Category");

	 // Create category entity from ClinicalAnnotationLabAnnotation entity.
	 ContainerInterface visitContainer = categoryHelper.createCategoryEntityAndContainer(visit, "Visit");
	 ContainerInterface frequentDiagnosisContainer = categoryHelper.createCategoryEntityAndContainer(clinicalDiagnosis, "Clinical Diagnosis");
	 ContainerInterface otherDiagnosisContainer = categoryHelper.createCategoryEntityAndContainer(clinicalDiagnosis, "Clinical Diagnosis");

	 // Create category attribute(s) for bodyComposition category entity.
	 categoryHelper.addControl(clinicalDiagnosis, "value", frequentDiagnosisContainer, ControlEnum.CHECK_BOX_CONTROL, "");
	 categoryHelper.addControl(clinicalDiagnosis, "value", frequentDiagnosisContainer, ControlEnum.CHECK_BOX_CONTROL, "");
	 categoryHelper.addControl(clinicalDiagnosis, "value", frequentDiagnosisContainer, ControlEnum.CHECK_BOX_CONTROL, "");
	 categoryHelper.addControl(clinicalDiagnosis, "value", frequentDiagnosisContainer, ControlEnum.CHECK_BOX_CONTROL, "");
	 categoryHelper.addControl(clinicalDiagnosis, "value", frequentDiagnosisContainer, ControlEnum.CHECK_BOX_CONTROL, "");

	 // Create category attribute(s) for bodyComposition category entity.
	 categoryHelper.addControl(clinicalDiagnosis, "value", otherDiagnosisContainer, ControlEnum.TEXT_FIELD_CONTROL, "");

	 // Set appropriate child category entities.
	 categoryHelper.setParentContainer(visitContainer, frequentDiagnosisContainer);
	 categoryHelper.setParentContainer(visitContainer, otherDiagnosisContainer);

	 List<String> list = new ArrayList<String>();
	 list.add("otherDiagnosis");
	 categoryHelper.associateCategoryContainers(visitContainer, otherDiagnosisContainer, list, -1);

	 // Save the category.
	 categoryHelper.saveCategory(category);
	 }
	 catch (DynamicExtensionsSystemException e)
	 {
	 e.printStackTrace();
	 fail();
	 }
	 catch (DynamicExtensionsApplicationException e)
	 {
	 e.printStackTrace();
	 fail();
	 }
	 catch (DAOException e)
	 {
	 e.printStackTrace();
	 fail();
	 }
	 }*/

	/**
	 * Entities : user (1)------>(*) study (1) ------>(*) experiment
	 *
	 * Category: Make 2 category entities, choosing attributes from user and experiment, do not
	 * choose attributes from study.
	 * Persist the category.
	 */
	public void testCreateAndSaveCategoryWithAttributesFromTwoOutOfThreeEntities()
	{
		DomainObjectFactory factory = DomainObjectFactory.getInstance();
		try
		{
			// Fetch the entity group from the database.
			DefaultBizLogic bizlogic = new DefaultBizLogic();
			Collection<EntityGroupInterface> entityGroupCollection = new HashSet<EntityGroupInterface>();
			EntityGroupInterface entityGroup = null;

			entityGroupCollection = bizlogic.retrieve(EntityGroup.class.getName(), "shortName",
					"User-Study-Experiment EG1");

			if (entityGroupCollection != null && entityGroupCollection.size() > 0)
			{
				entityGroup = entityGroupCollection.iterator().next();
			}

			// Fetch the user, study and experiment entities.
			EntityInterface user = entityGroup.getEntityByName("User Entity");
			EntityInterface study = entityGroup.getEntityByName("Study Entity");
			EntityInterface experiment = entityGroup.getEntityByName("Experiment Entity");

			assertEquals(getColumnCount("select * from " + study.getTableProperties().getName()),
					noOfDefaultColumns + 4);

			// Create a category from user and experiment entities.
			CategoryInterface category = factory.createCategory();
			category.setName("Category From Study and Experiment Entities");

			// Create category entity from user entity.
			CategoryEntityInterface userCategoryEntity = factory.createCategoryEntity();
			userCategoryEntity.setName("User Category Entity");
			userCategoryEntity.setNumberOfEntries(-1);
			userCategoryEntity.setEntity(user);
			userCategoryEntity.setCategory(category);

			// Set the root category element of the category.
			category.setRootCategoryElement(userCategoryEntity);

			// Create category attribute(s) for user category entity.
			CategoryAttributeInterface userCategoryAttribute = factory.createCategoryAttribute();
			userCategoryAttribute.setName("User Category Attribute");
			userCategoryAttribute.setAbstractAttribute(user.getAttributeByName("User Name"));

			userCategoryEntity.addCategoryAttribute(userCategoryAttribute);
			userCategoryAttribute.setCategoryEntity(userCategoryEntity);

			// Create category entity from experiment entity.
			CategoryEntityInterface experimentCategoryEntity = factory.createCategoryEntity();
			experimentCategoryEntity.setName("Experiment Category Entity");
			experimentCategoryEntity.setNumberOfEntries(-1);
			experimentCategoryEntity.setEntity(experiment);

			// Create category attribute(s) for experiment category entity.
			CategoryAttributeInterface experimentCategoryAttribute = factory
					.createCategoryAttribute();
			experimentCategoryAttribute.setName("Experiment Category Attribute");
			experimentCategoryAttribute.setAbstractAttribute(experiment
					.getAttributeByName("Experiment Name"));

			experimentCategoryEntity.addCategoryAttribute(experimentCategoryAttribute);
			experimentCategoryAttribute.setCategoryEntity(experimentCategoryEntity);

			// Fetch the user's associations list.
			List<AssociationInterface> userAssociationsList = new ArrayList<AssociationInterface>(
					user.getAssociationCollection());

			// Fetch the study's associations list.
			List<AssociationInterface> studyAssociationsList = new ArrayList<AssociationInterface>(
					study.getAssociationCollection());

			// Create a path between user category entity and experiment category entity.
			PathInterface path = factory.createPath();

			PathAssociationRelationInterface pathAssociationRelation1 = factory
					.createPathAssociationRelation();
			pathAssociationRelation1.setAssociation(userAssociationsList.get(0));
			pathAssociationRelation1.setPathSequenceNumber(1);

			PathAssociationRelationInterface pathAssociationRelation2 = factory
					.createPathAssociationRelation();
			pathAssociationRelation2.setAssociation(studyAssociationsList.get(0));
			pathAssociationRelation2.setPathSequenceNumber(2);

			pathAssociationRelation1.setPath(path);
			pathAssociationRelation2.setPath(path);

			path.addPathAssociationRelation(pathAssociationRelation1);
			path.addPathAssociationRelation(pathAssociationRelation2);

			// Add path information to the target category entity.
			experimentCategoryEntity.setPath(path);

			// Create a category association between user category entity and experiment category entity
			// that corresponds to association between user and experiment entities.
			CategoryAssociationInterface categoryAssociation = factory.createCategoryAssociation();
			categoryAssociation
					.setName("User Category Entity To Experiment Category Entity Category Association");
			categoryAssociation.setCategoryEntity(userCategoryEntity);
			categoryAssociation.setTargetCategoryEntity(experimentCategoryEntity);

			userCategoryEntity.getCategoryAssociationCollection().add(categoryAssociation);

			// Make experiment category entity a child of user category entity.
			userCategoryEntity.addChildCategory(experimentCategoryEntity);

			// Create containers for category entities.
			int sequenceNumber = 1;

			// Create a container for user category entity.
			ContainerInterface userContainer = createContainer(userCategoryEntity);

			// Create a control for user category attribute.
			TextFieldInterface userControl = createTextFieldControl(userCategoryAttribute,
					sequenceNumber++);
			userControl.setParentContainer((Container) userContainer);

			userContainer.addControl(userControl);

			// Create a container for experiment category entity.
			ContainerInterface experimentContainer = createContainer(experimentCategoryEntity);
			experimentContainer.setAddCaption(false);

			// Create a control for experiment category attribute.
			TextFieldInterface experimentControl = createTextFieldControl(
					experimentCategoryAttribute, sequenceNumber++);
			experimentControl.setParentContainer((Container) experimentContainer);

			experimentContainer.addControl(experimentControl);

			// Create a control for category association.
			CategoryAssociationControlInterface categoryAssociationControl = factory
					.createCategoryAssociationControl();
			categoryAssociationControl.setCaption("UserToExperimentCategoryAssociationControl");
			categoryAssociationControl.setContainer(experimentContainer);
			categoryAssociationControl.setBaseAbstractAttribute(categoryAssociation);
			categoryAssociationControl.setSequenceNumber(sequenceNumber++);
			categoryAssociationControl.setParentContainer((Container) userContainer);

			userContainer.addControl(categoryAssociationControl);

			// Save the category.
			CategoryManagerInterface categoryManager = CategoryManager.getInstance();
			categoryManager.persistCategory(category);
		}
		catch (DynamicExtensionsSystemException e)
		{
			e.printStackTrace();
			fail();
		}
		catch (DynamicExtensionsApplicationException e)
		{
			e.printStackTrace();
			fail();
		}
		catch (BizLogicException e)
		{
			e.printStackTrace();
			fail();
		}
	}

	/**
	 * Entities : user (1)------>(*) study (1) ------>(*) experiment
	 *
	 * Category: Make 3 category entities, choosing attributes from all entities.
	 * Insert data for category.
	 */
	public void testCreateAndSaveCategoryWithAttributesFromThreeOutOfThreeEntities()
	{
		DomainObjectFactory factory = DomainObjectFactory.getInstance();
		try
		{
			// Fetch the entity group from the database.
			DefaultBizLogic bizlogic = new DefaultBizLogic();
			Collection<EntityGroupInterface> entityGroupCollection = new HashSet<EntityGroupInterface>();
			EntityGroupInterface entityGroup = null;

			entityGroupCollection = bizlogic.retrieve(EntityGroup.class.getName(), "shortName",
					"User-Study-Experiment EG1");

			if (entityGroupCollection != null && entityGroupCollection.size() > 0)
			{
				entityGroup = entityGroupCollection.iterator().next();
			}

			// Fetch the user, study and experiment entities.
			EntityInterface user = entityGroup.getEntityByName("User Entity");
			EntityInterface study = entityGroup.getEntityByName("Study Entity");
			EntityInterface experiment = entityGroup.getEntityByName("Experiment Entity");
			assertEquals(getColumnCount("select * from " + study.getTableProperties().getName()),
					noOfDefaultColumns + 4);
			// Create a category from user, study and experiment entities.
			CategoryInterface category = factory.createCategory();
			category.setName("Category From User, Study and Experiment Entities");

			// Create category entity from user entity.
			CategoryEntityInterface userCategoryEntity = factory.createCategoryEntity();
			userCategoryEntity.setName("User Category Entity");
			userCategoryEntity.setNumberOfEntries(-1);
			userCategoryEntity.setEntity(user);
			userCategoryEntity.setCategory(category);

			// Set the root category element of the category.
			category.setRootCategoryElement(userCategoryEntity);

			// Create category attribute(s) for user category entity.
			CategoryAttributeInterface userCategoryAttribute = factory.createCategoryAttribute();
			userCategoryAttribute.setName("User Category Attribute");
			userCategoryAttribute.setAbstractAttribute(user.getAttributeByName("User Name"));

			userCategoryEntity.addCategoryAttribute(userCategoryAttribute);
			userCategoryAttribute.setCategoryEntity(userCategoryEntity);

			// Create category entity from study entity.
			CategoryEntityInterface studyCategoryEntity = factory.createCategoryEntity();
			studyCategoryEntity.setName("Study Category Entity");
			studyCategoryEntity.setNumberOfEntries(-1);
			studyCategoryEntity.setEntity(study);

			// Create category attribute(s) for study category entity.
			CategoryAttributeInterface studyCategoryAttribute = factory.createCategoryAttribute();
			studyCategoryAttribute.setName("Study Category Attribute");
			studyCategoryAttribute.setAbstractAttribute(study.getAttributeByName("Study Name"));

			studyCategoryEntity.addCategoryAttribute(studyCategoryAttribute);
			studyCategoryAttribute.setCategoryEntity(studyCategoryEntity);

			// Create category entity from experiment entity.
			CategoryEntityInterface experimentCategoryEntity = factory.createCategoryEntity();
			experimentCategoryEntity.setName("Experiment Category Entity");
			experimentCategoryEntity.setNumberOfEntries(-1);
			experimentCategoryEntity.setEntity(experiment);

			// Create category attribute(s) for experiment category entity.
			CategoryAttributeInterface experimentCategoryAttribute = factory
					.createCategoryAttribute();
			experimentCategoryAttribute.setName("Experiment Category Attribute");
			experimentCategoryAttribute.setAbstractAttribute(experiment
					.getAttributeByName("Experiment Name"));

			experimentCategoryEntity.addCategoryAttribute(experimentCategoryAttribute);
			experimentCategoryAttribute.setCategoryEntity(experimentCategoryEntity);

			// Fetch the user's associations list.
			List<AssociationInterface> userAssociationsList = new ArrayList<AssociationInterface>(
					user.getAssociationCollection());

			// Fetch the study's associations list.
			List<AssociationInterface> studyAssociationsList = new ArrayList<AssociationInterface>(
					study.getAssociationCollection());

			// Create a path between user category entity and study category entity.
			PathInterface path1 = factory.createPath();

			PathAssociationRelationInterface pathAssociationRelation1 = factory
					.createPathAssociationRelation();
			pathAssociationRelation1.setAssociation(userAssociationsList.get(0));
			pathAssociationRelation1.setPathSequenceNumber(1);
			pathAssociationRelation1.setPath(path1);

			// Create a path between study category entity and experiment category entity.
			PathInterface path2 = factory.createPath();

			PathAssociationRelationInterface pathAssociationRelation2 = factory
					.createPathAssociationRelation();
			pathAssociationRelation2.setAssociation(studyAssociationsList.get(0));
			pathAssociationRelation2.setPathSequenceNumber(2);
			pathAssociationRelation2.setPath(path2);

			path1.addPathAssociationRelation(pathAssociationRelation1);
			path2.addPathAssociationRelation(pathAssociationRelation2);

			// Add path information to the target category entities.
			studyCategoryEntity.setPath(path1);
			experimentCategoryEntity.setPath(path2);

			// Create a category association between user category entity and study category entity
			// that corresponds to association between user and study entities.
			CategoryAssociationInterface categoryAssociation1 = factory.createCategoryAssociation();
			categoryAssociation1
					.setName("User Category Entity To Study Category Entity Category Association");
			categoryAssociation1.setCategoryEntity(userCategoryEntity);
			categoryAssociation1.setTargetCategoryEntity(studyCategoryEntity);

			userCategoryEntity.getCategoryAssociationCollection().add(categoryAssociation1);

			// Create a category association between study category entity and experiment category entity
			// that corresponds to association between study and experiment entities.
			CategoryAssociationInterface categoryAssociation2 = factory.createCategoryAssociation();
			categoryAssociation2
					.setName("Study Category Entity To Experiment Category Entity Category Association");
			categoryAssociation2.setCategoryEntity(studyCategoryEntity);
			categoryAssociation2.setTargetCategoryEntity(experimentCategoryEntity);

			studyCategoryEntity.getCategoryAssociationCollection().add(categoryAssociation2);

			// Make study category entity a child of user category entity
			// and experiment category entity a child of study category entity.
			userCategoryEntity.addChildCategory(studyCategoryEntity);
			studyCategoryEntity.addChildCategory(experimentCategoryEntity);

			// Create containers for category entities.
			int sequenceNumber = 1;

			// Create a container for user category entity.
			ContainerInterface userContainer = createContainer(userCategoryEntity);

			// Create a control for user category attribute.
			TextFieldInterface userControl = createTextFieldControl(userCategoryAttribute,
					sequenceNumber++);
			userControl.setParentContainer((Container) userContainer);

			userContainer.addControl(userControl);

			// Create a container for study category entity.
			ContainerInterface studyContainer = createContainer(studyCategoryEntity);
			studyContainer.setAddCaption(false);

			// Create a control for study category attribute.
			TextFieldInterface studyControl = createTextFieldControl(studyCategoryAttribute,
					sequenceNumber++);
			studyControl.setParentContainer((Container) studyContainer);

			studyContainer.addControl(studyControl);

			// Create a container for experiment category entity.
			ContainerInterface experimentContainer = createContainer(experimentCategoryEntity);
			experimentContainer.setAddCaption(false);

			// Create a control for experiment category attribute.
			TextFieldInterface experimentControl = createTextFieldControl(
					experimentCategoryAttribute, sequenceNumber++);
			experimentControl.setParentContainer((Container) experimentContainer);

			experimentContainer.addControl(experimentControl);

			// Create a control for category association between user category entity and study category entity.
			CategoryAssociationControlInterface categoryAssociationControl1 = factory
					.createCategoryAssociationControl();
			categoryAssociationControl1.setCaption("UserToStudyCategoryAssociationControl");
			categoryAssociationControl1.setBaseAbstractAttribute(categoryAssociation1);
			categoryAssociationControl1.setSequenceNumber(sequenceNumber++);
			categoryAssociationControl1.setContainer(studyContainer);
			categoryAssociationControl1.setParentContainer((Container) userContainer);

			userContainer.addControl(categoryAssociationControl1);

			// Create a control for category association between study category entity and experiment category entity.
			CategoryAssociationControlInterface categoryAssociationControl2 = factory
					.createCategoryAssociationControl();
			categoryAssociationControl2.setCaption("StudyToExperimentCategoryAssociationControl");
			categoryAssociationControl2.setBaseAbstractAttribute(categoryAssociation2);
			categoryAssociationControl2.setSequenceNumber(sequenceNumber++);
			categoryAssociationControl2.setContainer(experimentContainer);
			categoryAssociationControl2.setParentContainer((Container) studyContainer);

			studyContainer.addControl(categoryAssociationControl2);

			// Save the category.
			CategoryManagerInterface categoryManager = CategoryManager.getInstance();
			categoryManager.persistCategory(category);
		}
		catch (DynamicExtensionsSystemException e)
		{
			e.printStackTrace();
			fail();
		}
		catch (DynamicExtensionsApplicationException e)
		{
			e.printStackTrace();
			fail();
		}
		catch (BizLogicException e)
		{
			e.printStackTrace();
			fail();
		}
	}

	/**
	 * Create category from SCG pathology annotation model.
	 * Refer to SCG pathology annotation model in cvs.
	 */
	public void testCreateRadicalProstatectomyCategoryFromSCGPathologyAnnotationModel()
	{
		CategoryInterface category = null;
		try
		{
			String[] args1 = {XMI_FILE_PATH +"scg.xmi",CSV_FILE_PATH+"SCG.csv","edu.wustl.catissuecore.domain.PathAnnotation_SCG"," "};
			XMIImporter.main(args1);

			String[] args2 = {CSV_FILE_PATH+ "Category_PathAnnoModel.csv"};

			CategoryCreator.main(args2);

			CategoryManager categoryManager = (CategoryManager) CategoryManager.getInstance();
			category = (CategoryInterface) categoryManager.getObjectByName(
					Category.class.getName(),
					"RadicalProstatectomyPathologyAnnotation_TestCaseCategory");

			assertNotNull(category.getId());
			assertNotNull(category.getRootCategoryElement());
			assertEquals(DynamicExtensionsUtility.getCategoryEntityName(category
					.getRootCategoryElement().getName()),
					"RadicalProstatectomyPathologyAnnotation[1]");

			CategoryEntityInterface prostatePathologyAnnotationCategoryEntity = category
					.getRootCategoryElement().getParentCategoryEntity();
			assertNotNull(prostatePathologyAnnotationCategoryEntity);
			assertEquals(DynamicExtensionsUtility
					.getCategoryEntityName(prostatePathologyAnnotationCategoryEntity.getName()),
					"ProstatePathologyAnnotation[1]");

			CategoryEntityInterface baseSolidTissuePathologyAnnotation = prostatePathologyAnnotationCategoryEntity
					.getParentCategoryEntity();
			assertNotNull(baseSolidTissuePathologyAnnotation);
			assertEquals(DynamicExtensionsUtility
					.getCategoryEntityName(baseSolidTissuePathologyAnnotation.getName()),
					"BaseSolidTissuePathologyAnnotation[1]");

			CategoryEntityInterface basePathologyAnnotation = baseSolidTissuePathologyAnnotation
					.getParentCategoryEntity();
			assertNotNull(basePathologyAnnotation);
			assertEquals(DynamicExtensionsUtility.getCategoryEntityName(basePathologyAnnotation
					.getName()), "BasePathologyAnnotation[1]");

			assertNull(basePathologyAnnotation.getParentCategoryEntity());
		}
		catch (Exception e)
		{
			e.printStackTrace();
			fail();
		}
	}

	/**
	 * Create category where attributes from a particular class are not chosen,
	 * i.e. not selecting attributes from GleasonScore entity.
	 * Refer to pathology annotation model.
	 */
	public void testCreateNeedleBiopsyCategoryFromSCGPathologyAnnotationModel()
	{
		CategoryInterface category = null;
		try
		{
			String[] args1 = {XMI_FILE_PATH+"scg.xmi",CSV_FILE_PATH+"SCG.csv", "edu.wustl.catissuecore.domain.PathAnnotation_SCG","  "};
			XMIImporter.main(args1);

			String[] args2 = {CSV_FILE_PATH + "Category_NeedleBiopsy.csv"};

			CategoryCreator.main(args2);

			CategoryManager categoryManager = (CategoryManager) CategoryManager.getInstance();
			category = (CategoryInterface) categoryManager.getObjectByName(
					Category.class.getName(), "NeedleBiopsy_TestCaseCategory");

			assertNotNull(category.getId());
			assertNotNull(category.getRootCategoryElement());
			assertEquals(category.getName(), "NeedleBiopsy_TestCaseCategory");
			assertEquals(DynamicExtensionsUtility.getCategoryEntityName(category
					.getRootCategoryElement().getName()),
					"NeedleBiopsyProstatePathologyAnnotation[1]");

			CategoryEntityInterface prostatePathologyAnnotationCategoryEntity = category
					.getRootCategoryElement().getParentCategoryEntity();
			assertNotNull(prostatePathologyAnnotationCategoryEntity);
			assertEquals(DynamicExtensionsUtility
					.getCategoryEntityName(prostatePathologyAnnotationCategoryEntity.getName()),
					"ProstatePathologyAnnotation[1]");

			CategoryEntityInterface baseSolidTissuePathologyAnnotation = prostatePathologyAnnotationCategoryEntity
					.getParentCategoryEntity();
			assertNotNull(baseSolidTissuePathologyAnnotation);
			assertEquals(DynamicExtensionsUtility
					.getCategoryEntityName(baseSolidTissuePathologyAnnotation.getName()),
					"BaseSolidTissuePathologyAnnotation[1]");

			CategoryEntityInterface basePathologyAnnotation = baseSolidTissuePathologyAnnotation
					.getParentCategoryEntity();
			assertNotNull(basePathologyAnnotation);
			assertEquals(DynamicExtensionsUtility.getCategoryEntityName(basePathologyAnnotation
					.getName()), "BasePathologyAnnotation[1]");

			assertNull(basePathologyAnnotation.getParentCategoryEntity());
		}
		catch (Exception e)
		{
			e.printStackTrace();
			fail();
		}
	}

	/**
	 * Create category where attributes from a particular class are not chosen,
	 * i.e. not selecting attributes from GleasonScore entity.
	 * Refer to pathology annotation model.
	 */
	public void testCIDERModel()
	{
		CategoryInterface category = null;
		try
		{



			String[] args = {XMI_FILE_PATH+"cider.xmi", CSV_FILE_PATH+"cider.csv","edu.wustl.cider.domain","none"};
			CiderXMIImporter.main(args);

			String[] args2 = {CSV_FILE_PATH+"Lab_category.csv", "true"};

			CategoryCreator.main(args2);

			CategoryManager categoryManager = (CategoryManager) CategoryManager.getInstance();
			category = (CategoryInterface) categoryManager.getObjectByName(
					Category.class.getName(), "CIDER Test Category");

			assertNotNull(category.getId());
			assertNotNull(category.getRootCategoryElement());
			assertEquals(category.getName(), "CIDER Test Category");

		}
		catch (Exception e)
		{
			e.printStackTrace();
			fail();
		}
	}

	/**
	 * Create category where attributes from a particular class are not chosen,
	 * i.e. not selecting attributes from GleasonScore entity.
	 * Refer to pathology annotation model.
	 */
	public void testBMIASCalculatedAttribute()
	{
		CategoryInterface category = null;
		try
		{
			String[] args = {XMI_FILE_PATH+"BMI.xmi", CSV_FILE_PATH+"BMIMainContainer.csv","BMI","  "};
			XMIImporter.main(args);

			String[] args2 = {CSV_FILE_PATH + "BMI.csv"};
			CategoryCreator.main(args2);

			CategoryManager categoryManager = (CategoryManager) CategoryManager.getInstance();
			category = (CategoryInterface) categoryManager.getObjectByName(
					Category.class.getName(), "BMI Calculation");

			assertNotNull(category.getId());
			assertNotNull(category.getRootCategoryElement());
			assertEquals(category.getName(), "BMI Calculation");

		}
		catch (Exception e)
		{
			e.printStackTrace();
			fail();
		}
	}

	/**
	 * Create category where attributes from a particular class are not chosen,
	 * i.e. not selecting attributes from GleasonScore entity.
	 * Refer to pathology annotation model.
	 */
	public void testBMIASRACalculatedAttribute()
	{
		CategoryInterface category = null;
		try
		{
			String[] args = {XMI_FILE_PATH + "BMI.xmi", CSV_FILE_PATH+"BMIMainContainer.csv", "BMI"," "};
			XMIImporter.main(args);

			String[] args2 = {CSV_FILE_PATH+"BMIRelatedAttribute.csv"};

			CategoryCreator.main(args2);

			CategoryManager categoryManager = (CategoryManager) CategoryManager.getInstance();
			category = (CategoryInterface) categoryManager.getObjectByName(
					Category.class.getName(), "BMI RA Calculation");

			assertNotNull(category.getId());
			assertNotNull(category.getRootCategoryElement());
			assertEquals(category.getName(), "BMI RA Calculation");

		}
		catch (Exception e)
		{
			e.printStackTrace();
			fail();
		}
	}

	/**
	 * Create category where attributes from a particular class are not chosen,
	 * i.e. not selecting attributes from GleasonScore entity.
	 * Refer to pathology annotation model.
	 */
	public void testBMIInDifferentClassInstanceAsCalculatedAttribute()
	{
		CategoryInterface category = null;
		try
		{
			String[] args = {XMI_FILE_PATH+"BMI.xmi", CSV_FILE_PATH+"BMIMainContainer.csv","BMI" , "  "};
			XMIImporter.main(args);

			String[] args2 = {CSV_FILE_PATH + "BMI_DifferentClass.csv"};

			CategoryCreator.main(args2);

			CategoryManager categoryManager = (CategoryManager) CategoryManager.getInstance();
			category = (CategoryInterface) categoryManager.getObjectByName(
					Category.class.getName(), "BMI Different Class Calculation");

			assertNotNull(category.getId());
			assertNotNull(category.getRootCategoryElement());
			assertEquals(category.getName(), "BMI Different Class Calculation");

		}
		catch (Exception e)
		{
			e.printStackTrace();
			fail();
		}
	}

	/**
	 * Create category where attributes from a particular class are not chosen,
	 * i.e. not selecting attributes from GleasonScore entity.
	 * Refer to pathology annotation model.
	 */
	public void testCalculatedAttributeForDates()
	{
		CategoryInterface category = null;
		try
		{
			String[] args = {XMI_FILE_PATH + "BMIDate.xmi",CSV_FILE_PATH + "BMIMainContainer.csv", "BMI", " "};
			XMIImporter.main(args);

			String[] args2 = {CSV_FILE_PATH + "BMI_Date.csv"};

			CategoryCreator.main(args2);

			CategoryManager categoryManager = (CategoryManager) CategoryManager.getInstance();
			category = (CategoryInterface) categoryManager.getObjectByName(
					Category.class.getName(), "Date Calculation");

			assertNotNull(category.getId());
			assertNotNull(category.getRootCategoryElement());
			assertEquals(category.getName(), "Date Calculation");

		}
		catch (Exception e)
		{
			e.printStackTrace();
			fail();
		}
	}
	/**
	 * Create category where attributes from a particular class are not chosen,
	 * i.e. not selecting attributes from GleasonScore entity.
	 * Refer to pathology annotation model.
	 */
	public void testSkipLogicAttributes()
	{
		CategoryInterface category = null;
		try
		{
			String[] args = {XMI_FILE_PATH + "test.xmi",CSV_FILE_PATH + "Skip_Logic_Main.csv", "annotations", "  "};
			XMIImporter.main(args);

			ImportPermissibleValues importPermissibleValues = new ImportPermissibleValues(CSV_FILE_PATH + "TestModel_pv.csv");
			importPermissibleValues.importValues();

			String[] args2 = {CSV_FILE_PATH + "SkipLogic.csv"};

			CategoryCreator.main(args2);

			CategoryManager categoryManager = (CategoryManager) CategoryManager.getInstance();

			category = (CategoryInterface) categoryManager.getObjectByName(
					Category.class.getName(), "SkipLogic_Test");

			assertNotNull(category.getId());
			assertNotNull(category.getRootCategoryElement());
			assertEquals(category.getName(), "SkipLogic_Test");

		}
		catch (Exception e)
		{
			e.printStackTrace();
			fail();
		}
	}
	/**
	 * Create entity group from pathology annotation model.
	 * Check if a correct subset of values for tumourTissueSiteCategoryAttribute is displayed.
	 */
	public void testSelectivePermissibleValuesForCategory()
	{
		CategoryManagerInterface categoryManager = CategoryManager.getInstance();
		DomainObjectFactory factory = DomainObjectFactory.getInstance();

		try
		{
			// Create and fetch the entity group.
			EntityGroupInterface entityGroup = createEntityGroup4();

			// Fetch the entities.
			EntityInterface baseSolidTissuePathologyAnnotation = entityGroup
					.getEntityByName("BaseSolidTissuePathologyAnnotation");
			EntityInterface prostatePathologyAnnotation = entityGroup
					.getEntityByName("ProstatePathologyAnnotation");
			EntityInterface gleasonScore = entityGroup.getEntityByName("GleasonScore");
			EntityInterface radicalProstatectomyPathologyAnnotation = entityGroup
					.getEntityByName("RadicalProstatectomyPathologyAnnotation");
			EntityInterface melanomaMargin = entityGroup.getEntityByName("melanomaMargin");
			EntityInterface radicalProstatectomyMargin = entityGroup
					.getEntityByName("RadicalProstatectomyMargin");

			// Create a category.
			CategoryInterface category = factory.createCategory();
			category.setName("Category");

			// Create category entity from baseSolidTissuePathologyAnnotation entity.
			CategoryEntityInterface baseSolidTissuePathologyAnnotationCategoryEntity = factory
					.createCategoryEntity();
			baseSolidTissuePathologyAnnotationCategoryEntity
					.setName("baseSolidTissuePathologyAnnotationCategoryEntity");
			baseSolidTissuePathologyAnnotationCategoryEntity
					.setEntity(baseSolidTissuePathologyAnnotation);
			baseSolidTissuePathologyAnnotationCategoryEntity.setNumberOfEntries(-1);

			// Fetch the baseSolidTissuePathologyAnnotation's attributes' list.
			List<AttributeInterface> attributesList1 = new ArrayList<AttributeInterface>(
					baseSolidTissuePathologyAnnotation.getAttributeCollection());

			// Create category attribute(s) for baseSolidTissuePathologyAnnotation category entity.
			CategoryAttributeInterface tissueSlideCategoryAttribute = factory
					.createCategoryAttribute();
			tissueSlideCategoryAttribute.setName("tissueSlideCategoryAttribute");
			tissueSlideCategoryAttribute.setAbstractAttribute(attributesList1.get(0));
			baseSolidTissuePathologyAnnotationCategoryEntity
					.addCategoryAttribute(tissueSlideCategoryAttribute);
			tissueSlideCategoryAttribute
					.setCategoryEntity(baseSolidTissuePathologyAnnotationCategoryEntity);

			CategoryAttributeInterface tumourTissueSiteCategoryAttribute = factory
					.createCategoryAttribute();
			tumourTissueSiteCategoryAttribute.setName("tumourTissueSiteCategoryAttribute");
			tumourTissueSiteCategoryAttribute.setAbstractAttribute(attributesList1.get(1));
			baseSolidTissuePathologyAnnotationCategoryEntity
					.addCategoryAttribute(tumourTissueSiteCategoryAttribute);
			tumourTissueSiteCategoryAttribute
					.setCategoryEntity(baseSolidTissuePathologyAnnotationCategoryEntity);

			// Create User defined DE for tumourTissueSiteCategoryAttribute.
			UserDefinedDEInterface tumourTissueSiteCategoryAttributeUserDefinedDE = factory
					.createUserDefinedDE();

			UserDefinedDEInterface attributeUserDefinedDE = (UserDefinedDE) attributesList1.get(2)
					.getAttributeTypeInformation().getDataElement();
			List<PermissibleValueInterface> permissibleValueCollection = new ArrayList<PermissibleValueInterface>(
					attributeUserDefinedDE.getPermissibleValueCollection());

			tumourTissueSiteCategoryAttributeUserDefinedDE
					.addPermissibleValue(permissibleValueCollection.get(0));
			tumourTissueSiteCategoryAttributeUserDefinedDE
					.addPermissibleValue(permissibleValueCollection.get(1));
			tumourTissueSiteCategoryAttributeUserDefinedDE
					.addPermissibleValue(permissibleValueCollection.get(2));

			tumourTissueSiteCategoryAttribute
					.setDataElement(tumourTissueSiteCategoryAttributeUserDefinedDE);
			tumourTissueSiteCategoryAttribute.setDefaultValue(permissibleValueCollection.get(1));

			// Create category entity from prostatePathologyAnnotation entity.
			CategoryEntityInterface prostatePathologyAnnotationCategoryEntity = factory
					.createCategoryEntity();
			prostatePathologyAnnotationCategoryEntity
					.setName("prostatePathologyAnnotationCategoryEntity");
			prostatePathologyAnnotationCategoryEntity.setEntity(prostatePathologyAnnotation);
			prostatePathologyAnnotationCategoryEntity.setNumberOfEntries(-1);
			prostatePathologyAnnotationCategoryEntity
					.setParentCategoryEntity(baseSolidTissuePathologyAnnotationCategoryEntity);

			// Fetch the prostatePathologyAnnotation attributes' list.
			List<AttributeInterface> attributesList2 = new ArrayList<AttributeInterface>(
					prostatePathologyAnnotation.getAttributeCollection());

			// Create category attribute(s) for prostatePathologyAnnotationCategoryEntity category entity.
			CategoryAttributeInterface seminalVesicleInvasionCategoryAttribute = factory
					.createCategoryAttribute();
			seminalVesicleInvasionCategoryAttribute
					.setName("seminalVesicleInvasionCategoryAttribute");
			seminalVesicleInvasionCategoryAttribute.setAbstractAttribute(attributesList2.get(0));
			prostatePathologyAnnotationCategoryEntity
					.addCategoryAttribute(seminalVesicleInvasionCategoryAttribute);
			seminalVesicleInvasionCategoryAttribute
					.setCategoryEntity(prostatePathologyAnnotationCategoryEntity);

			CategoryAttributeInterface periprostaticFatInvasionCategoryAttribute = factory
					.createCategoryAttribute();
			periprostaticFatInvasionCategoryAttribute
					.setName("periprostaticFatInvasionCategoryAttribute");
			periprostaticFatInvasionCategoryAttribute.setAbstractAttribute(attributesList2.get(1));
			prostatePathologyAnnotationCategoryEntity
					.addCategoryAttribute(periprostaticFatInvasionCategoryAttribute);
			periprostaticFatInvasionCategoryAttribute
					.setCategoryEntity(prostatePathologyAnnotationCategoryEntity);

			// Create category entity from gleasonScore entity.
			CategoryEntityInterface gleasonScoreCategoryEntity = factory.createCategoryEntity();
			gleasonScoreCategoryEntity.setName("gleasonScoreCategoryEntity");
			gleasonScoreCategoryEntity.setEntity(gleasonScore);
			gleasonScoreCategoryEntity.setNumberOfEntries(-1);

			// Fetch the gleasonScore's attributes' list.
			List<AttributeInterface> attributesList3 = new ArrayList<AttributeInterface>(
					gleasonScore.getAttributeCollection());

			// Create category attribute(s) for gleasonScoreCategoryEntity.
			CategoryAttributeInterface primaryPatternCategoryAttribute = factory
					.createCategoryAttribute();
			primaryPatternCategoryAttribute.setName("primaryPatternCategoryAttribute");
			primaryPatternCategoryAttribute.setAbstractAttribute(attributesList3.get(0));
			gleasonScoreCategoryEntity.addCategoryAttribute(primaryPatternCategoryAttribute);
			primaryPatternCategoryAttribute.setCategoryEntity(gleasonScoreCategoryEntity);

			CategoryAttributeInterface secondaryPatternCategoryAttribute = factory
					.createCategoryAttribute();
			secondaryPatternCategoryAttribute.setName("secondaryPatternCategoryAttribute");
			secondaryPatternCategoryAttribute.setAbstractAttribute(attributesList3.get(1));
			gleasonScoreCategoryEntity.addCategoryAttribute(secondaryPatternCategoryAttribute);
			secondaryPatternCategoryAttribute.setCategoryEntity(gleasonScoreCategoryEntity);

			// Fetch the prostatePathologyAnnotation's associations' list.
			List<AssociationInterface> associationsList1 = new ArrayList<AssociationInterface>(
					prostatePathologyAnnotation.getAssociationCollection());

			// Create a path between prostate pathology annotation category entity and gleason score category entity.
			PathInterface path1 = factory.createPath();

			PathAssociationRelationInterface pathAssociationRelationForPath1 = factory
					.createPathAssociationRelation();
			pathAssociationRelationForPath1.setAssociation(associationsList1.get(0));
			pathAssociationRelationForPath1.setPathSequenceNumber(1);
			pathAssociationRelationForPath1.setPath(path1);
			path1.addPathAssociationRelation(pathAssociationRelationForPath1);

			// Add path information to the target category entity.
			gleasonScoreCategoryEntity.setPath(path1);

			// Create a category association between prostate pathology annotation category entity
			// and gleason score category entity that corresponds to association between prostate pathology annotation
			// and gleason score.
			CategoryAssociationInterface prostateGleasonCategoryAssociation = factory
					.createCategoryAssociation();
			prostateGleasonCategoryAssociation
					.setName("prostateGleasonAssociationCategoryAssociation");
			prostateGleasonCategoryAssociation
					.setCategoryEntity(prostatePathologyAnnotationCategoryEntity);
			prostateGleasonCategoryAssociation.setTargetCategoryEntity(gleasonScoreCategoryEntity);

			prostatePathologyAnnotationCategoryEntity.getCategoryAssociationCollection().add(
					prostateGleasonCategoryAssociation);

			// Make gleason score category entity a child of prostate pathology annotation category entity.
			prostatePathologyAnnotationCategoryEntity.addChildCategory(gleasonScoreCategoryEntity);

			// Create category entity from radicalProstatectomyPathologyAnnotation entity.
			CategoryEntityInterface radicalProstatectomyPathologyAnnotationCategoryEntity = factory
					.createCategoryEntity();
			radicalProstatectomyPathologyAnnotationCategoryEntity
					.setName("radicalProstatectomyPathologyAnnotationCategoryEntity");
			radicalProstatectomyPathologyAnnotationCategoryEntity
					.setEntity(radicalProstatectomyPathologyAnnotation);
			radicalProstatectomyPathologyAnnotationCategoryEntity.setNumberOfEntries(-1);
			radicalProstatectomyPathologyAnnotationCategoryEntity
					.setParentCategoryEntity(prostatePathologyAnnotationCategoryEntity);

			// Fetch the radicalProstatectomyPathologyAnnotation's attributes' list.
			List<AttributeInterface> attributesList4 = new ArrayList<AttributeInterface>(
					radicalProstatectomyPathologyAnnotation.getAttributeCollection());

			// Create category attribute(s) for radicalProstatectomyPathologyAnnotationCategoryEntity.
			CategoryAttributeInterface radicalProstateNameCategoryAttribute = factory
					.createCategoryAttribute();
			radicalProstateNameCategoryAttribute.setName("radicalProstateNameCategoryAttribute");
			radicalProstateNameCategoryAttribute.setAbstractAttribute(attributesList4.get(0));
			radicalProstatectomyPathologyAnnotationCategoryEntity
					.addCategoryAttribute(radicalProstateNameCategoryAttribute);
			radicalProstateNameCategoryAttribute
					.setCategoryEntity(radicalProstatectomyPathologyAnnotationCategoryEntity);

			CategoryAttributeInterface radicalProstateTypeCategoryAttribute = factory
					.createCategoryAttribute();
			radicalProstateTypeCategoryAttribute.setName("radicalProstateTypeCategoryAttribute");
			radicalProstateTypeCategoryAttribute.setAbstractAttribute(attributesList4.get(1));
			radicalProstatectomyPathologyAnnotationCategoryEntity
					.addCategoryAttribute(radicalProstateTypeCategoryAttribute);
			radicalProstateTypeCategoryAttribute
					.setCategoryEntity(radicalProstatectomyPathologyAnnotationCategoryEntity);

			// Create category entity from melanomaMargin entity.
			CategoryEntityInterface melanomaMarginCategoryEntity = factory.createCategoryEntity();
			melanomaMarginCategoryEntity.setName("melanomaMarginCategoryEntity");
			melanomaMarginCategoryEntity.setEntity(melanomaMargin);
			melanomaMarginCategoryEntity.setNumberOfEntries(-1);

			// Fetch the melanomaMargin's attributes' list.
			List<AttributeInterface> attributesList5 = new ArrayList<AttributeInterface>(
					melanomaMargin.getAttributeCollection());

			// Create category attribute(s) for melanomaMarginCategoryEntity.
			CategoryAttributeInterface melanomaMarginNameCategoryAttribute = factory
					.createCategoryAttribute();
			melanomaMarginNameCategoryAttribute.setName("melanomaMarginNameCategoryAttribute");
			melanomaMarginNameCategoryAttribute.setAbstractAttribute(attributesList5.get(0));
			melanomaMarginCategoryEntity.addCategoryAttribute(melanomaMarginNameCategoryAttribute);
			melanomaMarginNameCategoryAttribute.setCategoryEntity(melanomaMarginCategoryEntity);

			CategoryAttributeInterface melanomaMarginTypeCategoryAttribute = factory
					.createCategoryAttribute();
			melanomaMarginTypeCategoryAttribute.setName("melanomaMarginTypeCategoryAttribute");
			melanomaMarginTypeCategoryAttribute.setAbstractAttribute(attributesList5.get(1));
			melanomaMarginCategoryEntity.addCategoryAttribute(melanomaMarginTypeCategoryAttribute);
			melanomaMarginTypeCategoryAttribute.setCategoryEntity(melanomaMarginCategoryEntity);

			// Make melanoma margin category entity a child of radical prostatectomy pathology annotation category entity.
			radicalProstatectomyPathologyAnnotationCategoryEntity
					.addChildCategory(melanomaMarginCategoryEntity);

			// Create a category association between radical prostatectomy pathology annotation category entity
			// and melanoma margin category entity that corresponds to association between radical prostatectomy pathology annotation
			// and melanoma margin.
			CategoryAssociationInterface prostatePathologyMelanomaMarginCategoryAssociation = factory
					.createCategoryAssociation();
			prostatePathologyMelanomaMarginCategoryAssociation
					.setName("prostatePathologymelanomaMarginCategoryAssociation");
			prostatePathologyMelanomaMarginCategoryAssociation
					.setCategoryEntity(radicalProstatectomyPathologyAnnotationCategoryEntity);
			prostatePathologyMelanomaMarginCategoryAssociation
					.setTargetCategoryEntity(melanomaMarginCategoryEntity);
			radicalProstatectomyPathologyAnnotationCategoryEntity
					.getCategoryAssociationCollection().add(
							prostatePathologyMelanomaMarginCategoryAssociation);

			// Fetch the radicalProstatectomyPathologyAnnotation's associations' list.
			List<AssociationInterface> associationsList2 = new ArrayList<AssociationInterface>(
					radicalProstatectomyPathologyAnnotation.getAssociationCollection());

			// Fetch the radicalProstatectomyMargin's associations' list.
			List<AssociationInterface> associationsList3 = new ArrayList<AssociationInterface>(
					radicalProstatectomyMargin.getAssociationCollection());

			// Create a path between radical prostatectomy pathology annotation category entity and melanoma margin category entity.
			PathInterface path2 = factory.createPath();

			PathAssociationRelationInterface pathAssociationRelation1ForPath2 = factory
					.createPathAssociationRelation();
			pathAssociationRelation1ForPath2.setAssociation(associationsList2.get(0));
			pathAssociationRelation1ForPath2.setPathSequenceNumber(1);

			PathAssociationRelationInterface pathAssociationRelation2ForPath2 = factory
					.createPathAssociationRelation();
			pathAssociationRelation2ForPath2.setAssociation(associationsList3.get(0));
			pathAssociationRelation2ForPath2.setPathSequenceNumber(2);

			pathAssociationRelation1ForPath2.setPath(path2);
			pathAssociationRelation2ForPath2.setPath(path2);

			path2.addPathAssociationRelation(pathAssociationRelation1ForPath2);
			path2.addPathAssociationRelation(pathAssociationRelation2ForPath2);

			// Add path information to the target category entity.
			melanomaMarginCategoryEntity.setPath(path2);

			// Set the root category element of the category.
			category.setRootCategoryElement(radicalProstatectomyPathologyAnnotationCategoryEntity);
			radicalProstatectomyPathologyAnnotationCategoryEntity.setCategory(category);

			// Create containers for category entities.
			int sequenceNumber = 1;

			// Create a container for baseSolidTissuePathologyAnnotationCategoryEntity.
			ContainerInterface baseSolidTissuePathologyAnnotationContainer = createContainer(baseSolidTissuePathologyAnnotationCategoryEntity);
			baseSolidTissuePathologyAnnotationContainer.setCaption("Base Solid Tissue Pathology");

			// Create a control for tissueSlideCategoryAttribute.
			TextFieldInterface tissueSlideControl = createTextFieldControl(
					tissueSlideCategoryAttribute, sequenceNumber++);
			// Create a control for tumourTissueSiteCategoryAttribute.
			ComboBoxInterface tumourTissueSiteControl = createComboBoxControl(
					tumourTissueSiteCategoryAttribute, sequenceNumber++);

			tissueSlideControl
					.setParentContainer((Container) baseSolidTissuePathologyAnnotationContainer);
			baseSolidTissuePathologyAnnotationContainer.addControl(tissueSlideControl);

			tumourTissueSiteControl
					.setParentContainer((Container) baseSolidTissuePathologyAnnotationContainer);
			baseSolidTissuePathologyAnnotationContainer.addControl(tumourTissueSiteControl);

			// Create a container for prostatePathologyAnnotationCategoryContainer.
			ContainerInterface prostatePathologyAnnotationCategoryContainer = createContainer(prostatePathologyAnnotationCategoryEntity);
			prostatePathologyAnnotationCategoryContainer
					.setCaption("Prostate Pathology Annotation");

			// Create a control for seminalVesicleInvasionCategoryAttribute.
			TextFieldInterface seminalVesicleInvasionControl = createTextFieldControl(
					seminalVesicleInvasionCategoryAttribute, sequenceNumber++);
			// Create a control for periprostaticFatInvasionCategoryAttribute.
			TextFieldInterface periprostaticFatInvasionControl = createTextFieldControl(
					periprostaticFatInvasionCategoryAttribute, sequenceNumber++);

			seminalVesicleInvasionControl
					.setParentContainer((Container) prostatePathologyAnnotationCategoryContainer);
			prostatePathologyAnnotationCategoryContainer.addControl(seminalVesicleInvasionControl);

			periprostaticFatInvasionControl
					.setParentContainer((Container) prostatePathologyAnnotationCategoryContainer);
			prostatePathologyAnnotationCategoryContainer
					.addControl(periprostaticFatInvasionControl);

			// Create a container for gleasonScoreCategoryEntity.
			ContainerInterface gleasonScoreContainer = createContainer(gleasonScoreCategoryEntity);
			gleasonScoreContainer.setCaption("Gleason Score");

			// Create a control for primaryPatternCategoryAttribute.
			TextFieldInterface primaryPatternControl = createTextFieldControl(
					primaryPatternCategoryAttribute, sequenceNumber++);
			// Create a control for secondaryPatternCategoryAttribute.
			TextFieldInterface secondaryPatternControl = createTextFieldControl(
					secondaryPatternCategoryAttribute, sequenceNumber++);

			primaryPatternControl.setParentContainer((Container) gleasonScoreContainer);
			gleasonScoreContainer.addControl(primaryPatternControl);

			secondaryPatternControl.setParentContainer((Container) gleasonScoreContainer);
			gleasonScoreContainer.addControl(secondaryPatternControl);

			AbstractContainmentControlInterface prostateGleasonCategoryContainmentControl = factory
					.createCategoryAssociationControl();
			prostateGleasonCategoryContainmentControl
					.setCaption("prostateGleasonCategory association");
			prostateGleasonCategoryContainmentControl
					.setBaseAbstractAttribute(prostateGleasonCategoryAssociation);
			prostateGleasonCategoryContainmentControl.setSequenceNumber(sequenceNumber++);
			prostateGleasonCategoryContainmentControl.setContainer(gleasonScoreContainer);
			prostateGleasonCategoryContainmentControl
					.setParentContainer((Container) prostatePathologyAnnotationCategoryContainer);

			prostatePathologyAnnotationCategoryContainer
					.addControl(prostateGleasonCategoryContainmentControl);

			// Create a container for radicalProstatectomyPathologyAnnotationCategoryEntity.
			ContainerInterface radicalProstatectomyPathologyAnnotationContainer = createContainer(radicalProstatectomyPathologyAnnotationCategoryEntity);
			radicalProstatectomyPathologyAnnotationContainer
					.setCaption("Radical Prostatectomy Pathology Annotation");

			// Create a control for radicalProstateNameCategoryAttribute.
			TextFieldInterface radicalProstateNameControl = createTextFieldControl(
					radicalProstateNameCategoryAttribute, sequenceNumber++);
			// Create a control for radicalProstateTypeCategoryAttribute.
			TextFieldInterface radicalProstateTypeControl = createTextFieldControl(
					radicalProstateTypeCategoryAttribute, sequenceNumber++);

			radicalProstateNameControl
					.setParentContainer((Container) radicalProstatectomyPathologyAnnotationContainer);
			radicalProstatectomyPathologyAnnotationContainer.addControl(radicalProstateNameControl);

			radicalProstateTypeControl
					.setParentContainer((Container) radicalProstatectomyPathologyAnnotationContainer);
			radicalProstatectomyPathologyAnnotationContainer.addControl(radicalProstateTypeControl);

			// Create a container for melanomaMarginCategoryEntity.
			ContainerInterface melanomaMarginContainer = createContainer(melanomaMarginCategoryEntity);
			melanomaMarginContainer.setCaption("Melanoma Margin");

			// Create a control for melanomaMarginNameCategoryAttribute.
			TextFieldInterface melanomaMarginNameControl = createTextFieldControl(
					melanomaMarginNameCategoryAttribute, sequenceNumber++);
			// Create a control for melanomaMarginTypeCategoryAttribute.
			TextFieldInterface melanomaMarginTypeControl = createTextFieldControl(
					melanomaMarginTypeCategoryAttribute, sequenceNumber++);

			melanomaMarginNameControl.setParentContainer((Container) melanomaMarginContainer);
			melanomaMarginContainer.addControl(melanomaMarginNameControl);

			melanomaMarginTypeControl.setParentContainer((Container) melanomaMarginContainer);
			melanomaMarginContainer.addControl(melanomaMarginTypeControl);

			// Create a containment control.
			AbstractContainmentControlInterface prostatePathologyMelanomaMarginCategoryContainmentControl = factory
					.createCategoryAssociationControl();
			prostatePathologyMelanomaMarginCategoryContainmentControl
					.setBaseAbstractAttribute(prostatePathologyMelanomaMarginCategoryAssociation);
			prostatePathologyMelanomaMarginCategoryContainmentControl
					.setSequenceNumber(sequenceNumber++);
			prostatePathologyMelanomaMarginCategoryContainmentControl
					.setCaption("prostatePathologyMelanomaMargin association");
			prostatePathologyMelanomaMarginCategoryContainmentControl
					.setContainer(melanomaMarginContainer);
			prostatePathologyMelanomaMarginCategoryContainmentControl
					.setParentContainer((Container) radicalProstatectomyPathologyAnnotationContainer);

			radicalProstatectomyPathologyAnnotationContainer
					.addControl(prostatePathologyMelanomaMarginCategoryContainmentControl);

			// Link containers.
			prostatePathologyAnnotationCategoryContainer
					.setBaseContainer(baseSolidTissuePathologyAnnotationContainer);
			radicalProstatectomyPathologyAnnotationContainer
					.setBaseContainer(prostatePathologyAnnotationCategoryContainer);

			// Save category.
			categoryManager.persistCategory(category);

			// Create data insertion map for category
			//						Map<BaseAbstractAttributeInterface, Object> radicalProstateDataCategoryMap = new HashMap<BaseAbstractAttributeInterface, Object>();
			//
			//						radicalProstateDataCategoryMap.put(tissueSlideCategoryAttribute, "tissueSlideCategoryAttribute");
			//						radicalProstateDataCategoryMap.put(tumourTissueSiteCategoryAttribute, "tumourTissueSiteCategoryAttribute");
			//						radicalProstateDataCategoryMap.put(seminalVesicleInvasionCategoryAttribute, "seminalVesicleInvasionCategoryAttribute");
			//						radicalProstateDataCategoryMap.put(periprostaticFatInvasionCategoryAttribute, "periprostaticFatInvasionCategoryAttribute");
			//
			//						List<Map<BaseAbstractAttributeInterface, Object>> prostateGleasonCategoryAssociationDataList = new ArrayList<Map<BaseAbstractAttributeInterface, Object>>();
			//
			//						Map<BaseAbstractAttributeInterface, Object> prostateGleasonAssociationCategoryAssociationDataMap = new HashMap<BaseAbstractAttributeInterface, Object>();
			//						prostateGleasonAssociationCategoryAssociationDataMap.put(primaryPatternCategoryAttribute, "primaryPatternCategoryAttribute");
			//						prostateGleasonAssociationCategoryAssociationDataMap.put(secondaryPatternCategoryAttribute, "secondaryPatternCategoryAttribute");
			//						prostateGleasonCategoryAssociationDataList.add(prostateGleasonAssociationCategoryAssociationDataMap);
			//
			//						radicalProstateDataCategoryMap.put(prostateGleasonCategoryAssociation, prostateGleasonCategoryAssociationDataList);
			//
			//						radicalProstateDataCategoryMap.put(radicalProstateNameCategoryAttribute, "radicalProstateNameCategoryAttribute");
			//						radicalProstateDataCategoryMap.put(radicalProstateTypeCategoryAttribute, "radicalProstateTypeCategoryAttribute");
			//
			//						List<Map<BaseAbstractAttributeInterface, Object>> prostatePathologyMelanomaMarginCategoryAssociationDataList = new ArrayList<Map<BaseAbstractAttributeInterface, Object>>();
			//						Map<BaseAbstractAttributeInterface, Object> prostatePathologyMelanomaMarginCategoryAssociationDataMap = new HashMap<BaseAbstractAttributeInterface, Object>();
			//						prostatePathologyMelanomaMarginCategoryAssociationDataMap.put(melanomaMarginNameCategoryAttribute, "melanomaMarginNameCategoryAttribute");
			//						prostatePathologyMelanomaMarginCategoryAssociationDataMap.put(melanomaMarginTypeCategoryAttribute, "melanomaMarginTypeCategoryAttribute");
			//						prostatePathologyMelanomaMarginCategoryAssociationDataList.add(prostatePathologyMelanomaMarginCategoryAssociationDataMap);
			//
			//						radicalProstateDataCategoryMap.put(prostatePathologyMelanomaMarginCategoryAssociation,
			//								prostatePathologyMelanomaMarginCategoryAssociationDataList);
			//
			//						categoryManager.insertData(category, radicalProstateDataCategoryMap);
		}
		catch (Exception e)
		{
			e.printStackTrace();
			Logger.out.debug(DynamicExtensionsUtility.getStackTrace(e));
			fail();
		}
	}

	/**
	 * Create a non-linear category tree from non-linear entity tree.
	 * Save the category.
	 */
	public void testCreateCategoryFromNonLinearEntityTree()
	{
		MockCategoryManager mockCategoryManager = new MockCategoryManager();
		CategoryManagerInterface categoryManager = CategoryManager.getInstance();
		CategoryInterface category = mockCategoryManager.createCategoryFromModel1();
		try
		{
			// Save category.
			categoryManager.persistCategory(category);
		}
		catch (DynamicExtensionsSystemException e)
		{
			e.printStackTrace();
			fail();
		}
		catch (DynamicExtensionsApplicationException e)
		{
			e.printStackTrace();
			fail();
		}
	}

	/**
	 * Add a category entity to a category.
	 * Save the category. This should not result in new tables creation,
	 * just one more category entity should be added to category.
	 */
	public void testAddCategoryEntityToCategoryFromNonLinearEntityTree()
	{
		MockCategoryManager mockCategoryManager = new MockCategoryManager();
		CategoryManagerInterface categoryManager = CategoryManager.getInstance();
		CategoryInterface category = mockCategoryManager.createCategoryFromModel1();

		try
		{
			// Save the category.
			categoryManager.persistCategory(category);

			// Add another category entity to present category.
			category = mockCategoryManager.addNewCategoryEntityToExistingCategory(category);

			// Again save the category.
			categoryManager.persistCategory(category);
		}
		catch (DynamicExtensionsSystemException e)
		{
			e.printStackTrace();
			fail();
		}
		catch (DynamicExtensionsApplicationException e)
		{
			e.printStackTrace();
			fail();
		}
	}

	/**
	 * Edit metadata for a category entity in a category.
	 * Save the category. This should not result in new tables creation,
	 * just the category entity metadata information should get updated.
	 */
	public void testEditCategoryEntityMetadataFromCategoryFromNonLinearEntityTree()
	{
		MockCategoryManager mockCategoryManager = new MockCategoryManager();
		CategoryManagerInterface categoryManager = CategoryManager.getInstance();
		CategoryInterface category = mockCategoryManager.createCategoryFromModel1();

		try
		{
			// Save the category.
			categoryManager.persistCategory(category);

			// Edit category entity metadata.
			category.getRootCategoryElement().setName(
					"chemotherapyTrialsCategoryEntity name changed");

			// Again save the category. This should not result in new tables creation,
			// just the category entity metadata information should get updated.
			categoryManager.persistCategory(category);
		}
		catch (DynamicExtensionsSystemException e)
		{
			e.printStackTrace();
			fail();
		}
		catch (DynamicExtensionsApplicationException e)
		{
			e.printStackTrace();
			fail();
		}
	}

	/**
	 * Add a category attribute to a category entity.
	 * Save the category. This should not result in new tables creation,
	 * just one more category attribute should be added to category entity.
	 */
	public void testAddCategoryAttributeToCategoryFromNonLinearEntityTree()
	{
		MockCategoryManager mockCategoryManager = new MockCategoryManager();
		CategoryManagerInterface categoryManager = CategoryManager.getInstance();
		CategoryInterface category = mockCategoryManager.createCategoryFromModel1();

		try
		{
			// Save the category.
			categoryManager.persistCategory(category);

			// Add category attribute to category entity.
			category = mockCategoryManager.addCategoryAttributetyToCategoryEntity(category);

			// Again save the category. This should not result in new tables creation,
			// just one more category attribute should be added to category entity.
			categoryManager.persistCategory(category);
		}
		catch (DynamicExtensionsSystemException e)
		{
			e.printStackTrace();
			fail();
		}
		catch (DynamicExtensionsApplicationException e)
		{
			e.printStackTrace();
			fail();
		}
	}

	/**
	 * Edit metadata for a category attribute in a category entity.
	 * Save the category. This should not result in new tables creation,
	 * just the category attribute metadata information should get updated.
	 */
	public void testEditCategoryAttributeMetadataFromCategoryFromNonLinearEntityTree()
	{
		MockCategoryManager mockCategoryManager = new MockCategoryManager();
		CategoryManagerInterface categoryManager = CategoryManager.getInstance();
		CategoryInterface category = mockCategoryManager.createCategoryFromModel1();

		try
		{
			// Save the category.
			categoryManager.persistCategory(category);

			// Edit category attribute metadata.
			CategoryEntityInterface rootCategoryEntity = category.getRootCategoryElement();

			for (CategoryAttributeInterface ca : rootCategoryEntity
					.getCategoryAttributeCollection())
			{
				ca.setName(ca.getName() + String.valueOf(new Double(Math.random()).intValue()));
			}

			// Again save the category. This should not result in new tables creation,
			// just the category attribute metadata information should get updated.
			categoryManager.persistCategory(category);
		}
		catch (DynamicExtensionsSystemException e)
		{
			e.printStackTrace();
			fail();
		}
		catch (DynamicExtensionsApplicationException e)
		{
			e.printStackTrace();
			fail();
		}
	}

	/**
	 * Test data entry for a category entity with related attributes.
	 */
	public void testDataEntryForCategoryEntityWithRelatedAttributes()
	{
		String filePath = CSV_FILE_PATH + "catMedicalHistoryRadiologicalDiagnosis.csv";

		try
		{
			CategoryManagerInterface categoryManager = CategoryManager.getInstance();
			CategoryGenerator categoryGenerator = new CategoryGenerator(filePath,"");

			CategoryInterface category = categoryGenerator.generateCategory();

			// Save the category.
			category = categoryManager.persistCategory(category);

			// populate the values map.
			Map<BaseAbstractAttributeInterface, Object> attributeValues = populateMap(category);

			JDBCDAO dao = DynamicExtensionsUtility.getJDBCDAO();

			// retrieve category entity of the category entity 'Annotations[1]LabAnnotation[2]'
			CategoryEntityInterface catEnt2 = category
					.getCategoryEntityByName("Annotations[1]LabAnnotation[2]");

			// insert values for the category.
			categoryManager.insertData(category, attributeValues);

			String result = dao.executeQuery(
					"select MAX(identifier) from "
							+ catEnt2.getEntity().getTableProperties().getName()).get(0).toString();
			int maxId = Integer.parseInt(result.substring(1, result.length() - 1));

			// verify that the records for related category attribute 'labTestName' have been inserted.
			CategoryAttributeInterface catAttrLabTestName = catEnt2
					.getAttributeByName("labTestName Category Attribute");
			AttributeInterface attrLabTestName = (AttributeInterface) catAttrLabTestName
					.getAbstractAttribute();

			// get the column name for lab test name attribute.
			String colNameForAttrLabTestName = attrLabTestName.getColumnProperties().getName();
			// get the table name for LabAnnotation entity.
			String tblNameForlabAnnotation = catEnt2.getEntity().getTableProperties().getName();

			// verify that for all records inserted, the value for attribute 'labTestName' is 'Bone Density'
			for (int j = maxId; j > 0; j--)
			{
				String boneDensity = dao.executeQuery(
						"select " + colNameForAttrLabTestName + " from " + tblNameForlabAnnotation
								+ " where identifier = " + maxId).get(0).toString();
				boneDensity = boneDensity.substring(1, boneDensity.length() - 1);
				assertEquals("Bone Density", boneDensity);
			}

			DynamicExtensionsUtility.closeJDBCDAO(dao);
		}
		catch (DynamicExtensionsSystemException e)
		{
			e.printStackTrace();
			fail();
		}
		catch (DynamicExtensionsApplicationException e)
		{
			e.printStackTrace();
			fail();
		}
		catch (FileNotFoundException e)
		{
			e.printStackTrace();
			fail();
		}
		catch (ParseException e)
		{
			e.printStackTrace();
			fail();
		}
		catch (DAOException e)
		{
			e.printStackTrace();
			fail();
		}
	}

	/**
	 * Create category where attributes from a particular class are not chosen,
	 * i.e. not selecting attributes from GleasonScore entity.
	 * Refer to pathology annotation model.
	 */
	public void testCreateCategoryWithMultiselectCheckBox()
	{
		CategoryInterface category = null;
		try
		{
			String[] args1 = {XMI_FILE_PATH + "test.xmi",CSV_FILE_PATH + "Skip_Logic_Main.csv", "annotations", "  "};
			XMIImporter.main(args1);


			String[] args2 = {CSV_FILE_PATH + "category_multiselectCheckBox.csv"};
			CategoryCreator.main(args2);

			CategoryManager categoryManager = (CategoryManager) CategoryManager.getInstance();
			category = (CategoryInterface) categoryManager.getObjectByName(
					Category.class.getName(), "Test_Cat_Rad_therapy_multiselectCheckbox");

			assertNotNull(category.getId());
			assertNotNull(category.getRootCategoryElement());
			assertEquals(category.getName(), "Test_Cat_Rad_therapy_multiselectCheckbox");

		}
		catch (Exception e)
		{
			e.printStackTrace();
			fail();
		}
	}
	/**
	 * Make a entity group from following entities:
	 * Entities : user (1)------>(*) study
	 * @return EntityGroupInterface
	 * @throws DynamicExtensionsSystemException
	 */
	private EntityGroupInterface createEntityGroup1() throws DynamicExtensionsSystemException
	{
		DomainObjectFactory factory = DomainObjectFactory.getInstance();

		// Create entity group.
		EntityGroupInterface entityGroup = factory.createEntityGroup();
		entityGroup.setName("Entity Group " + new Double(Math.random()).toString());
		entityGroup.setShortName("User-Study EG1");

		// Create user entity.
		EntityInterface user = createAndPopulateEntity();
		user.setName("User Entity");

		// Create attribute(s) for user entity.
		AttributeInterface userName = factory.createStringAttribute();
		userName.setName("User Name");
		((StringAttributeTypeInformation) userName.getAttributeTypeInformation()).setSize(40);

		AttributeInterface userGender = factory.createStringAttribute();
		userGender.setName("User Gender");
		((StringAttributeTypeInformation) userGender.getAttributeTypeInformation()).setSize(40);

		AttributeInterface userAddress = factory.createStringAttribute();
		userAddress.setName("User Address");
		((StringAttributeTypeInformation) userAddress.getAttributeTypeInformation()).setSize(40);

		// Add attribute(s) to user entity.
		user.addAbstractAttribute(userName);
		user.addAbstractAttribute(userGender);
		user.addAbstractAttribute(userAddress);

		// Create study entity.
		EntityInterface study = createAndPopulateEntity();
		study.setName("Study Entity");

		// Create attribute(s) for study entity.
		AttributeInterface studyName = factory.createStringAttribute();
		studyName.setName("Study Name");
		((StringAttributeTypeInformation) studyName.getAttributeTypeInformation()).setSize(40);

		AttributeInterface studyType = factory.createStringAttribute();
		studyType.setName("Study Type");
		((StringAttributeTypeInformation) studyType.getAttributeTypeInformation()).setSize(40);

		AttributeInterface studyDescription = factory.createStringAttribute();
		studyDescription.setName("Study Description");
		((StringAttributeTypeInformation) studyDescription.getAttributeTypeInformation())
				.setSize(40);

		// Add attribute(s) to study entity.
		study.addAbstractAttribute(studyName);
		study.addAbstractAttribute(studyType);
		study.addAbstractAttribute(studyDescription);

		// Associate user entity with study entity : user (1)------ >(*) study
		AssociationInterface userToStudyAssociation = factory.createAssociation();
		userToStudyAssociation.setName("User To Study Association");
		userToStudyAssociation.setTargetEntity(study);
		userToStudyAssociation.setAssociationDirection(AssociationDirection.SRC_DESTINATION);

		// Create source role for user to study association.
		RoleInterface sourceRole = getRole(AssociationType.ASSOCIATION, "primaryInvestigator",
				Cardinality.ONE, Cardinality.ONE);
		sourceRole.setAssociationsType(AssociationType.CONTAINTMENT);

		userToStudyAssociation.setSourceRole(sourceRole);
		userToStudyAssociation.setTargetRole(getRole(AssociationType.ASSOCIATION, "study",
				Cardinality.ZERO, Cardinality.MANY));

		user.addAbstractAttribute(userToStudyAssociation);
		DynamicExtensionsUtility.getConstraintPropertiesForAssociation(userToStudyAssociation);

		// Create containers for user and study entities.
		int sequenceNumber = 0;

		ContainerInterface userEntityContainer = createContainerForEntity(user);
		ContainerInterface studyEntityContainer = createContainerForEntity(study);

		// Create text field controls for attributes of user and study entities.
		createTextFieldControlForEntity(userEntityContainer, user, sequenceNumber);
		sequenceNumber = userEntityContainer.getControlCollection().size();
		createTextFieldControlForEntity(studyEntityContainer, study, sequenceNumber);
		sequenceNumber = sequenceNumber + studyEntityContainer.getControlCollection().size();

		// Create a contaiment control.
		AbstractContainmentControlInterface containmentControl = factory
				.createContainmentAssociationControl();
		containmentControl.setCaption("UserToStudyAbstractContainmentControl");
		containmentControl.setContainer(studyEntityContainer);
		containmentControl.setBaseAbstractAttribute(userToStudyAssociation);
		containmentControl.setSequenceNumber(++sequenceNumber);

		containmentControl.setParentContainer((Container) userEntityContainer);
		userEntityContainer.addControl(containmentControl);

		entityGroup.addEntity(user);
		user.setEntityGroup(entityGroup);
		entityGroup.addEntity(study);
		study.setEntityGroup(entityGroup);

		return entityGroup;
	}

	/**
	 * Make a entity group from following entities:
	 * Entities : user (1)------>(*) study (1) ------>(*) experiment
	 * @return EntityGroupInterface
	 * @throws DynamicExtensionsSystemException
	 */
	private EntityGroupInterface createEntityGroup2() throws DynamicExtensionsSystemException
	{
		DomainObjectFactory factory = DomainObjectFactory.getInstance();

		// Create entity group.
		EntityGroupInterface entityGroup = factory.createEntityGroup();
		entityGroup.setName("Entity Group " + new Double(Math.random()).toString());
		entityGroup.setShortName("User-Study-Experiment EG1");

		// Create user entity.
		EntityInterface user = createAndPopulateEntity();
		user.setName("User Entity");

		// Create attribute(s) for user entity.
		AttributeInterface userName = factory.createStringAttribute();
		userName.setName("User Name");
		((StringAttributeTypeInformation) userName.getAttributeTypeInformation()).setSize(40);

		AttributeInterface userGender = factory.createStringAttribute();
		userGender.setName("User Gender");
		((StringAttributeTypeInformation) userGender.getAttributeTypeInformation()).setSize(40);

		AttributeInterface userAddress = factory.createStringAttribute();
		userAddress.setName("User Address");
		((StringAttributeTypeInformation) userAddress.getAttributeTypeInformation()).setSize(40);

		// Add attribute(s) to user entity.
		user.addAbstractAttribute(userName);
		user.addAbstractAttribute(userGender);
		user.addAbstractAttribute(userAddress);

		// Create study entity.
		EntityInterface study = createAndPopulateEntity();
		study.setName("Study Entity");

		// Create attribute(s) for study entity.
		AttributeInterface studyName = factory.createStringAttribute();
		studyName.setName("Study Name");
		((StringAttributeTypeInformation) studyName.getAttributeTypeInformation()).setSize(40);

		AttributeInterface studyType = factory.createStringAttribute();
		studyType.setName("Study Type");
		((StringAttributeTypeInformation) studyType.getAttributeTypeInformation()).setSize(40);

		AttributeInterface studyDescription = factory.createStringAttribute();
		studyDescription.setName("Study Description");
		((StringAttributeTypeInformation) studyDescription.getAttributeTypeInformation())
				.setSize(40);

		// Add attribute(s) to study entity.
		study.addAbstractAttribute(studyName);
		study.addAbstractAttribute(studyType);
		study.addAbstractAttribute(studyDescription);

		// Create experiment entity.
		EntityInterface experiment = createAndPopulateEntity();
		experiment.setName("Experiment Entity");

		AttributeInterface experimentName = factory.createStringAttribute();
		experimentName.setName("Experiment Name");
		((StringAttributeTypeInformation) experimentName.getAttributeTypeInformation()).setSize(40);

		AttributeInterface experimentType = factory.createStringAttribute();
		experimentType.setName("Experiment Type");
		((StringAttributeTypeInformation) experimentType.getAttributeTypeInformation()).setSize(40);

		AttributeInterface experimentDescription = factory.createStringAttribute();
		experimentDescription.setName("Experiment Description");
		((StringAttributeTypeInformation) experimentDescription.getAttributeTypeInformation())
				.setSize(40);

		// Add attribute to experiment entity.
		experiment.addAbstractAttribute(experimentName);
		experiment.addAbstractAttribute(experimentType);
		experiment.addAbstractAttribute(experimentDescription);

		// Associate user entity with study entity : user (1)------ >(*) study
		AssociationInterface userToStudyAssociation = factory.createAssociation();
		userToStudyAssociation.setName("User To Study Association");
		userToStudyAssociation.setTargetEntity(study);
		userToStudyAssociation.setAssociationDirection(AssociationDirection.SRC_DESTINATION);

		// Create source role for user to study association.
		RoleInterface sourceRole1 = getRole(AssociationType.ASSOCIATION,
				"User To Study Association Source Role", Cardinality.ONE, Cardinality.ONE);
		sourceRole1.setAssociationsType(AssociationType.CONTAINTMENT);

		userToStudyAssociation.setSourceRole(sourceRole1);
		userToStudyAssociation.setTargetRole(getRole(AssociationType.ASSOCIATION,
				"User To Study Association Target Role", Cardinality.ZERO, Cardinality.MANY));

		user.addAbstractAttribute(userToStudyAssociation);
		DynamicExtensionsUtility.getConstraintPropertiesForAssociation(userToStudyAssociation);

		// Associate study entity with experiment entity : study (1)------ >(*) experiment
		AssociationInterface studyToExperimentAssociation = factory.createAssociation();
		studyToExperimentAssociation.setName("Study To Experiment Association");
		studyToExperimentAssociation.setTargetEntity(experiment);
		studyToExperimentAssociation.setAssociationDirection(AssociationDirection.SRC_DESTINATION);

		// Create source role for study to experiment association.
		RoleInterface sourceRole2 = getRole(AssociationType.ASSOCIATION,
				"Study To Experiment Association Source Role", Cardinality.ONE, Cardinality.ONE);
		sourceRole2.setAssociationsType(AssociationType.CONTAINTMENT);

		studyToExperimentAssociation.setSourceRole(sourceRole2);
		studyToExperimentAssociation.setTargetRole(getRole(AssociationType.ASSOCIATION,
				"Study To Experiment Association Target Role", Cardinality.ZERO, Cardinality.MANY));

		study.addAbstractAttribute(studyToExperimentAssociation);
		DynamicExtensionsUtility
				.getConstraintPropertiesForAssociation(studyToExperimentAssociation);

		// Create containers for user, study and experiment entities.
		int sequenceNumber = 0;

		ContainerInterface userEntityContainer = createContainerForEntity(user);
		ContainerInterface studyEntityContainer = createContainerForEntity(study);
		ContainerInterface experimentEntityContainer = createContainerForEntity(experiment);

		// Create text field controls for attributes of user, study and experiment entities.
		createTextFieldControlForEntity(userEntityContainer, user, sequenceNumber);
		sequenceNumber = userEntityContainer.getControlCollection().size();
		createTextFieldControlForEntity(studyEntityContainer, study, sequenceNumber);
		sequenceNumber = sequenceNumber + studyEntityContainer.getControlCollection().size();
		createTextFieldControlForEntity(experimentEntityContainer, experiment, sequenceNumber);
		sequenceNumber = sequenceNumber + experimentEntityContainer.getControlCollection().size();

		// Create contaiment controls.
		AbstractContainmentControlInterface containmentControl1 = factory
				.createContainmentAssociationControl();
		containmentControl1.setCaption("UserToStudyContainmentControl");
		containmentControl1.setContainer(studyEntityContainer);
		containmentControl1.setBaseAbstractAttribute(userToStudyAssociation);
		containmentControl1.setSequenceNumber(++sequenceNumber);

		containmentControl1.setParentContainer((Container) userEntityContainer);
		userEntityContainer.addControl(containmentControl1);

		AbstractContainmentControlInterface containmentControl2 = factory
				.createContainmentAssociationControl();
		containmentControl2.setCaption("StudyToExperimentContainmentControl");
		containmentControl2.setContainer(experimentEntityContainer);
		containmentControl2.setBaseAbstractAttribute(studyToExperimentAssociation);
		containmentControl2.setSequenceNumber(++sequenceNumber);

		containmentControl2.setParentContainer((Container) studyEntityContainer);
		studyEntityContainer.addControl(containmentControl2);

		entityGroup.addEntity(user);
		user.setEntityGroup(entityGroup);
		entityGroup.addEntity(study);
		study.setEntityGroup(entityGroup);
		entityGroup.addEntity(experiment);
		experiment.setEntityGroup(entityGroup);

		return entityGroup;
	}

	/**
	 * Create entity group from pathology annotation model.
	 * @return EntityGroupInterface
	 * @throws DynamicExtensionsSystemException
	 */
	private EntityGroupInterface createEntityGroup3() throws DynamicExtensionsSystemException
	{
		DomainObjectFactory factory = DomainObjectFactory.getInstance();

		EntityGroupInterface entityGroup = factory.createEntityGroup();
		entityGroup.setName("Entity Group " + new Double(Math.random()).toString());
		entityGroup.setShortName("PathologyModel EG1");

		// Create a baseSolidTissuePathologyAnnotation entity.
		EntityInterface baseSolidTissuePathologyAnnotation = createAndPopulateEntity();
		baseSolidTissuePathologyAnnotation.setName("BaseSolidTissuePathologyAnnotation");
		baseSolidTissuePathologyAnnotation.setAbstract(true);

		// Create attribute(s) for baseSolidTissuePathologyAnnotation entity.
		AttributeInterface tissueSlide = factory.createStringAttribute();
		tissueSlide.setName("tissueSlide");
		((StringAttributeTypeInformation) tissueSlide.getAttributeTypeInformation()).setSize(40);

		AttributeInterface tumourTissueSite = factory.createStringAttribute();
		tumourTissueSite.setName("tumourTissueSite");
		((StringAttributeTypeInformation) tumourTissueSite.getAttributeTypeInformation())
				.setSize(40);

		baseSolidTissuePathologyAnnotation.addAbstractAttribute(tissueSlide);
		baseSolidTissuePathologyAnnotation.addAbstractAttribute(tumourTissueSite);

		// Create a prostatePathologyAnnotation entity.
		EntityInterface prostatePathologyAnnotation = createAndPopulateEntity();
		prostatePathologyAnnotation.setName("ProstatePathologyAnnotation");
		prostatePathologyAnnotation.setParentEntity(baseSolidTissuePathologyAnnotation);
		DynamicExtensionsUtility.getConstraintKeyPropertiesForInheritance(
				prostatePathologyAnnotation, false);

		// Create attribute(s) for prostatePathologyAnnotation entity.
		AttributeInterface seminalVesicleInvasion = factory.createStringAttribute();
		seminalVesicleInvasion.setName("seminalVesicleInvasion");
		((StringAttributeTypeInformation) seminalVesicleInvasion.getAttributeTypeInformation())
				.setSize(40);

		AttributeInterface periprostaticFatInvasion = factory.createStringAttribute();
		periprostaticFatInvasion.setName("periprostaticFatInvasion");
		((StringAttributeTypeInformation) periprostaticFatInvasion.getAttributeTypeInformation())
				.setSize(40);

		prostatePathologyAnnotation.addAbstractAttribute(seminalVesicleInvasion);
		prostatePathologyAnnotation.addAbstractAttribute(periprostaticFatInvasion);

		// Create a gleasonScore entity.
		EntityInterface gleasonScore = createAndPopulateEntity();
		gleasonScore.setName("GleasonScore");

		// Create attribute(s) for gleasonScore entity.
		AttributeInterface primaryPattern = factory.createStringAttribute();
		primaryPattern.setName("primaryPattern");
		((StringAttributeTypeInformation) primaryPattern.getAttributeTypeInformation()).setSize(40);

		AttributeInterface secondaryPattern = factory.createStringAttribute();
		secondaryPattern.setName("secondaryPattern");
		((StringAttributeTypeInformation) secondaryPattern.getAttributeTypeInformation())
				.setSize(40);

		gleasonScore.addAbstractAttribute(primaryPattern);
		gleasonScore.addAbstractAttribute(secondaryPattern);

		// Associate prostatePathologyAnnotation entity with gleasonScore entity : prostatePathologyAnnotation (1)------ >(*) gleasonScore
		AssociationInterface association1 = factory.createAssociation();
		association1.setName("prostatePathologyAnnotationToGleasonScoreAssociation");
		association1.setTargetEntity(gleasonScore);
		association1.setAssociationDirection(AssociationDirection.SRC_DESTINATION);

		// Create source role for prostatePathologyAnnotation to gleasonScore association.
		RoleInterface sourceRole1 = getRole(AssociationType.ASSOCIATION,
				"prostatePathologyAnnotation", Cardinality.ONE, Cardinality.ONE);
		sourceRole1.setAssociationsType(AssociationType.CONTAINTMENT);

		association1.setSourceRole(sourceRole1);
		association1.setTargetRole(getRole(AssociationType.ASSOCIATION, "gleasonScore",
				Cardinality.ZERO, Cardinality.MANY));

		prostatePathologyAnnotation.addAbstractAttribute(association1);
		DynamicExtensionsUtility.getConstraintPropertiesForAssociation(association1);

		// Create a radicalProstatectomyPathologyAnnotation entity.
		EntityInterface radicalProstatectomyPathologyAnnotation = createAndPopulateEntity();
		radicalProstatectomyPathologyAnnotation.setName("RadicalProstatectomyPathologyAnnotation");
		radicalProstatectomyPathologyAnnotation.setParentEntity(prostatePathologyAnnotation);
		DynamicExtensionsUtility.getConstraintKeyPropertiesForInheritance(
				radicalProstatectomyPathologyAnnotation, false);

		// Create attribute(s) for radicalProstatectomyPathologyAnnotation entity.
		AttributeInterface radicalProstateName = factory.createStringAttribute();
		radicalProstateName.setName("radicalProstateName");
		((StringAttributeTypeInformation) radicalProstateName.getAttributeTypeInformation())
				.setSize(40);

		AttributeInterface radicalProstateType = factory.createStringAttribute();
		radicalProstateType.setName("radicalProstateType");
		((StringAttributeTypeInformation) radicalProstateType.getAttributeTypeInformation())
				.setSize(40);

		radicalProstatectomyPathologyAnnotation.addAttribute(radicalProstateName);
		radicalProstatectomyPathologyAnnotation.addAttribute(radicalProstateType);

		// Create a radicalProstatectomyMargin entity.
		EntityInterface radicalProstatectomyMargin = createAndPopulateEntity();
		radicalProstatectomyMargin.setName("RadicalProstatectomyMargin");
		radicalProstatectomyMargin.setAbstract(true);

		// Create attribute(s) for radicalProstatectomyMargin entity.
		AttributeInterface focality = factory.createStringAttribute();
		focality.setName("focality");
		((StringAttributeTypeInformation) focality.getAttributeTypeInformation()).setSize(40);

		AttributeInterface marginalStatus = factory.createStringAttribute();
		marginalStatus.setName("marginalStatus");
		((StringAttributeTypeInformation) marginalStatus.getAttributeTypeInformation()).setSize(40);

		radicalProstatectomyMargin.addAbstractAttribute(focality);
		radicalProstatectomyMargin.addAbstractAttribute(marginalStatus);

		// Associate radicalProstatectomyPathologyAnnotation entity with radicalProstatectomyMargin entity : radicalProstatectomyPathologyAnnotation (1)------ >(*) radicalProstatectomyMargin
		AssociationInterface association2 = factory.createAssociation();
		association2
				.setName("radicalProstatectomyPathologyAnnotationToRadicalProstatectomyMarginAssociation");
		association2.setTargetEntity(radicalProstatectomyMargin);
		association2.setAssociationDirection(AssociationDirection.SRC_DESTINATION);

		RoleInterface sourceRole2 = getRole(AssociationType.ASSOCIATION,
				"radicalProstatectomyPathologyAnnotation", Cardinality.ONE, Cardinality.ONE);
		sourceRole2.setAssociationsType(AssociationType.CONTAINTMENT);

		association2.setSourceRole(sourceRole2);
		association2.setTargetRole(getRole(AssociationType.ASSOCIATION,
				"radicalProstatectomyMargin", Cardinality.ZERO, Cardinality.MANY));

		radicalProstatectomyPathologyAnnotation.addAbstractAttribute(association2);
		DynamicExtensionsUtility.getConstraintPropertiesForAssociation(association2);

		// Create a melanomaMargin entity.
		EntityInterface melanomaMargin = createAndPopulateEntity();
		melanomaMargin.setName("MelanomaMargin");

		// Create attribute(s) for melanomaMargin entity.
		AttributeInterface melanomaMarginName = factory.createStringAttribute();
		melanomaMarginName.setName("melanomaMarginName");
		((StringAttributeTypeInformation) melanomaMarginName.getAttributeTypeInformation())
				.setSize(40);

		AttributeInterface melanomaMarginType = factory.createStringAttribute();
		melanomaMarginType.setName("melanomaMarginType");
		((StringAttributeTypeInformation) melanomaMarginType.getAttributeTypeInformation())
				.setSize(40);

		melanomaMargin.addAbstractAttribute(melanomaMarginName);
		melanomaMargin.addAbstractAttribute(melanomaMarginType);

		// Associate radicalProstatectomyMargin entity with melanomaMargin entity : radicalProstatectomyMargin (1)------ >(*) melanomaMargin
		AssociationInterface association3 = factory.createAssociation();
		association3.setName("radicalProstatectomyMarginToMelanomaMarginAssociation");
		association3.setTargetEntity(melanomaMargin);
		association3.setAssociationDirection(AssociationDirection.SRC_DESTINATION);

		RoleInterface sourceRole3 = getRole(AssociationType.ASSOCIATION,
				"radicalProstatectomyMargin", Cardinality.ONE, Cardinality.ONE);
		sourceRole3.setAssociationsType(AssociationType.CONTAINTMENT);

		association3.setSourceRole(sourceRole3);
		association3.setTargetRole(getRole(AssociationType.ASSOCIATION, "melanomaMargin",
				Cardinality.ZERO, Cardinality.MANY));

		radicalProstatectomyMargin.addAbstractAttribute(association3);
		DynamicExtensionsUtility.getConstraintPropertiesForAssociation(association3);

		// Create containers for baseSolidTissuePathologyAnnotation, prostatePathologyAnnotation,
		// gleasonScore, radicalProstatectomyPathologyAnnotation, radicalProstatectomyMargin
		//and melanomaMargin entities.
		int sequenceNumber = 0;

		ContainerInterface baseSolidTissuePathologyAnnotationEntityContainer = createContainerForEntity(baseSolidTissuePathologyAnnotation);
		ContainerInterface prostatePathologyAnnotationEntityContainer = createContainerForEntity(prostatePathologyAnnotation);
		ContainerInterface gleasonScoreEntityContainer = createContainerForEntity(gleasonScore);
		ContainerInterface radicalProstatectomyPathologyAnnotationrEntityContainer = createContainerForEntity(radicalProstatectomyPathologyAnnotation);
		ContainerInterface radicalProstatectomyMarginEntityContainer = createContainerForEntity(radicalProstatectomyMargin);
		ContainerInterface melanomaMarginEntityContainer = createContainerForEntity(melanomaMargin);

		// Create text field controls for attributes baseSolidTissuePathologyAnnotation,
		// prostatePathologyAnnotation, gleasonScore, radicalProstatectomyPathologyAnnotation,
		// radicalProstatectomyMargin and melanomaMargin entities.
		createTextFieldControlForEntity(baseSolidTissuePathologyAnnotationEntityContainer,
				baseSolidTissuePathologyAnnotation, sequenceNumber);
		sequenceNumber = baseSolidTissuePathologyAnnotationEntityContainer.getControlCollection()
				.size();
		createTextFieldControlForEntity(prostatePathologyAnnotationEntityContainer,
				prostatePathologyAnnotation, sequenceNumber);
		sequenceNumber = sequenceNumber
				+ prostatePathologyAnnotationEntityContainer.getControlCollection().size();
		createTextFieldControlForEntity(gleasonScoreEntityContainer, gleasonScore, sequenceNumber);
		sequenceNumber = sequenceNumber + gleasonScoreEntityContainer.getControlCollection().size();
		createTextFieldControlForEntity(radicalProstatectomyPathologyAnnotationrEntityContainer,
				radicalProstatectomyPathologyAnnotation, sequenceNumber);
		sequenceNumber = sequenceNumber
				+ radicalProstatectomyPathologyAnnotationrEntityContainer.getControlCollection()
						.size();
		createTextFieldControlForEntity(radicalProstatectomyMarginEntityContainer,
				radicalProstatectomyMargin, sequenceNumber);
		sequenceNumber = sequenceNumber
				+ radicalProstatectomyMarginEntityContainer.getControlCollection().size();
		createTextFieldControlForEntity(melanomaMarginEntityContainer, melanomaMargin,
				sequenceNumber);
		sequenceNumber = sequenceNumber
				+ melanomaMarginEntityContainer.getControlCollection().size();

		// Create contaiment controls.
		AbstractContainmentControlInterface containmentControl1 = factory
				.createContainmentAssociationControl();
		containmentControl1
				.setCaption("ProstatePathologyAnnotationToGleasonScoreContainmentControl");
		containmentControl1.setContainer(gleasonScoreEntityContainer);
		containmentControl1.setBaseAbstractAttribute(association1);
		containmentControl1.setSequenceNumber(++sequenceNumber);

		containmentControl1
				.setParentContainer((Container) prostatePathologyAnnotationEntityContainer);
		prostatePathologyAnnotationEntityContainer.addControl(containmentControl1);

		AbstractContainmentControlInterface containmentControl2 = factory
				.createContainmentAssociationControl();
		containmentControl2
				.setCaption("RadicalProstatectomyPathologyAnnotationToRadicalProstatectomyMarginContainmentControl");
		containmentControl2.setContainer(radicalProstatectomyMarginEntityContainer);
		containmentControl2.setBaseAbstractAttribute(association2);
		containmentControl2.setSequenceNumber(++sequenceNumber);

		containmentControl2
				.setParentContainer((Container) radicalProstatectomyPathologyAnnotationrEntityContainer);
		radicalProstatectomyPathologyAnnotationrEntityContainer.addControl(containmentControl2);

		AbstractContainmentControlInterface containmentControl3 = factory
				.createContainmentAssociationControl();
		containmentControl3
				.setCaption("RadicalProstatectomyMarginToMelanomaMarginAssociationContainmentControl");
		containmentControl3.setContainer(melanomaMarginEntityContainer);
		containmentControl3.setBaseAbstractAttribute(association3);
		containmentControl3.setSequenceNumber(++sequenceNumber);

		containmentControl3
				.setParentContainer((Container) radicalProstatectomyMarginEntityContainer);
		radicalProstatectomyMarginEntityContainer.addControl(containmentControl3);

		entityGroup.addEntity(baseSolidTissuePathologyAnnotation);
		baseSolidTissuePathologyAnnotation.setEntityGroup(entityGroup);
		entityGroup.addEntity(prostatePathologyAnnotation);
		prostatePathologyAnnotation.setEntityGroup(entityGroup);
		entityGroup.addEntity(gleasonScore);
		gleasonScore.setEntityGroup(entityGroup);
		entityGroup.addEntity(radicalProstatectomyPathologyAnnotation);
		radicalProstatectomyPathologyAnnotation.setEntityGroup(entityGroup);
		entityGroup.addEntity(radicalProstatectomyMargin);
		radicalProstatectomyMargin.setEntityGroup(entityGroup);
		entityGroup.addEntity(melanomaMargin);
		melanomaMargin.setEntityGroup(entityGroup);

		return entityGroup;
	}

	/**
	 * Create entity group from pathology annotation model.
	 * Add permissible values to tumourTissueSite attribute.
	 * @return EntityGroupInterface
	 * @throws DynamicExtensionsSystemException
	 */
	private EntityGroupInterface createEntityGroup4() throws DynamicExtensionsSystemException
	{
		EntityGroupManagerInterface entityGroupManager = EntityGroupManager.getInstance();
		DomainObjectFactory factory = DomainObjectFactory.getInstance();

		EntityGroupInterface entityGroup = factory.createEntityGroup();
		entityGroup.setName("Entity Group " + new Double(Math.random()).toString());

		// Create a baseSolidTissuePathologyAnnotation entity.
		EntityInterface baseSolidTissuePathologyAnnotation = createAndPopulateEntity();
		baseSolidTissuePathologyAnnotation.setName("BaseSolidTissuePathologyAnnotation");
		baseSolidTissuePathologyAnnotation.setAbstract(true);

		// Create attribute(s) for baseSolidTissuePathologyAnnotation entity.
		AttributeInterface tissueSlide = factory.createStringAttribute();
		tissueSlide.setName("tissueSlide");
		((StringAttributeTypeInformation) tissueSlide.getAttributeTypeInformation()).setSize(40);

		AttributeInterface tumourTissueSite = factory.createStringAttribute();
		tumourTissueSite.setName("tumourTissueSite");
		((StringAttributeTypeInformation) tumourTissueSite.getAttributeTypeInformation())
				.setSize(40);

		baseSolidTissuePathologyAnnotation.addAbstractAttribute(tissueSlide);
		baseSolidTissuePathologyAnnotation.addAbstractAttribute(tumourTissueSite);

		// Create permissible values for tumourTissueSite.
		UserDefinedDEInterface userDefinedDE = factory.createUserDefinedDE();

		PermissibleValueInterface permissibleValue1 = factory.createStringValue();
		((StringValue) permissibleValue1).setValue("Permissible Value 1");

		PermissibleValueInterface permissibleValue2 = factory.createStringValue();
		((StringValue) permissibleValue2).setValue("Permissible Value 2");

		PermissibleValueInterface permissibleValue3 = factory.createStringValue();
		((StringValue) permissibleValue3).setValue("Permissible Value 3");

		PermissibleValueInterface permissibleValue4 = factory.createStringValue();
		((StringValue) permissibleValue4).setValue("Permissible Value 4");

		PermissibleValueInterface permissibleValue5 = factory.createStringValue();
		((StringValue) permissibleValue5).setValue("Permissible Value 5");

		userDefinedDE.addPermissibleValue(permissibleValue1);
		userDefinedDE.addPermissibleValue(permissibleValue2);
		userDefinedDE.addPermissibleValue(permissibleValue3);
		userDefinedDE.addPermissibleValue(permissibleValue4);
		userDefinedDE.addPermissibleValue(permissibleValue5);

		StringAttributeTypeInformation tumourTissueSiteTypeInfo = (StringAttributeTypeInformation) tumourTissueSite
				.getAttributeTypeInformation();

		tumourTissueSiteTypeInfo.setDataElement(userDefinedDE);

		// Create a prostatePathologyAnnotation entity.
		EntityInterface prostatePathologyAnnotation = createAndPopulateEntity();
		prostatePathologyAnnotation.setParentEntity(baseSolidTissuePathologyAnnotation);
		DynamicExtensionsUtility.getConstraintKeyPropertiesForInheritance(
				prostatePathologyAnnotation, false);
		prostatePathologyAnnotation.setName("ProstatePathologyAnnotation");

		// Create attribute(s) for prostatePathologyAnnotation entity.
		AttributeInterface seminalVesicleInvasion = factory.createStringAttribute();
		seminalVesicleInvasion.setName("seminalVesicleInvasion");
		((StringAttributeTypeInformation) seminalVesicleInvasion.getAttributeTypeInformation())
				.setSize(40);

		AttributeInterface periprostaticFatInvasion = factory.createStringAttribute();
		periprostaticFatInvasion.setName("periprostaticFatInvasion");
		((StringAttributeTypeInformation) periprostaticFatInvasion.getAttributeTypeInformation())
				.setSize(40);

		prostatePathologyAnnotation.addAbstractAttribute(seminalVesicleInvasion);
		prostatePathologyAnnotation.addAbstractAttribute(periprostaticFatInvasion);

		// Create a gleasonScore entity.
		EntityInterface gleasonScore = createAndPopulateEntity();
		gleasonScore.setName("GleasonScore");

		// Create attribute(s) for gleasonScore entity.
		AttributeInterface primaryPattern = factory.createStringAttribute();
		primaryPattern.setName("primaryPattern");
		((StringAttributeTypeInformation) primaryPattern.getAttributeTypeInformation()).setSize(40);

		AttributeInterface secondaryPattern = factory.createStringAttribute();
		secondaryPattern.setName("secondaryPattern");
		((StringAttributeTypeInformation) secondaryPattern.getAttributeTypeInformation())
				.setSize(40);

		gleasonScore.addAbstractAttribute(primaryPattern);
		gleasonScore.addAbstractAttribute(secondaryPattern);

		// Associate prostatePathologyAnnotation entity with gleasonScore entity : prostatePathologyAnnotation (1)------ >(*) gleasonScore
		AssociationInterface association1 = factory.createAssociation();
		association1.setName("prostatePathologyAnnotationToGleasonScoreAssociation");
		association1.setTargetEntity(gleasonScore);
		association1.setAssociationDirection(AssociationDirection.SRC_DESTINATION);

		RoleInterface sourceRole1 = getRole(AssociationType.ASSOCIATION,
				"prostatePathologyAnnotation", Cardinality.ONE, Cardinality.ONE);
		sourceRole1.setAssociationsType(AssociationType.CONTAINTMENT);

		association1.setSourceRole(sourceRole1);
		association1.setTargetRole(getRole(AssociationType.ASSOCIATION, "gleasonScore",
				Cardinality.ZERO, Cardinality.MANY));

		prostatePathologyAnnotation.addAbstractAttribute(association1);
		DynamicExtensionsUtility.getConstraintPropertiesForAssociation(association1);

		// Create a radicalProstatectomyPathologyAnnotation entity.
		EntityInterface radicalProstatectomyPathologyAnnotation = createAndPopulateEntity();
		radicalProstatectomyPathologyAnnotation.setName("RadicalProstatectomyPathologyAnnotation");
		radicalProstatectomyPathologyAnnotation.setParentEntity(prostatePathologyAnnotation);
		DynamicExtensionsUtility.getConstraintKeyPropertiesForInheritance(
				radicalProstatectomyPathologyAnnotation, false);

		// Create attribute(s) for radicalProstatectomyPathologyAnnotation entity.
		AttributeInterface radicalProstateName = factory.createStringAttribute();
		radicalProstateName.setName("radicalProstateName");
		((StringAttributeTypeInformation) radicalProstateName.getAttributeTypeInformation())
				.setSize(40);

		AttributeInterface radicalProstateType = factory.createStringAttribute();
		radicalProstateType.setName("radicalProstateType");
		((StringAttributeTypeInformation) radicalProstateType.getAttributeTypeInformation())
				.setSize(40);

		radicalProstatectomyPathologyAnnotation.addAttribute(radicalProstateName);
		radicalProstatectomyPathologyAnnotation.addAttribute(radicalProstateType);

		// Create a radicalProstatectomyMargin entity.
		EntityInterface radicalProstatectomyMargin = createAndPopulateEntity();
		radicalProstatectomyMargin.setName("RadicalProstatectomyMargin");
		radicalProstatectomyMargin.setAbstract(true);

		// Create attribute(s) for radicalProstatectomyMargin entity.
		AttributeInterface focality = factory.createStringAttribute();
		focality.setName("focality");
		((StringAttributeTypeInformation) focality.getAttributeTypeInformation()).setSize(40);

		AttributeInterface marginalStatus = factory.createStringAttribute();
		marginalStatus.setName("marginalStatus");
		((StringAttributeTypeInformation) marginalStatus.getAttributeTypeInformation()).setSize(40);

		radicalProstatectomyMargin.addAbstractAttribute(focality);
		radicalProstatectomyMargin.addAbstractAttribute(marginalStatus);

		// Associate radicalProstatectomyPathologyAnnotation entity with radicalProstatectomyMargin entity : radicalProstatectomyPathologyAnnotation (1)------ >(*) radicalProstatectomyMargin
		AssociationInterface association2 = factory.createAssociation();
		association2
				.setName("radicalProstatectomyPathologyAnnotationToRadicalProstatectomyMarginAssociation");
		association2.setTargetEntity(radicalProstatectomyMargin);
		association2.setAssociationDirection(AssociationDirection.SRC_DESTINATION);

		// Create source role for radicalProstatectomyPathologyAnnotation to radicalProstatectomyMargin association.
		RoleInterface sourceRole2 = getRole(AssociationType.ASSOCIATION,
				"radicalProstatectomyPathologyAnnotation", Cardinality.ONE, Cardinality.ONE);
		sourceRole2.setAssociationsType(AssociationType.CONTAINTMENT);

		association2.setSourceRole(sourceRole2);
		association2.setTargetRole(getRole(AssociationType.ASSOCIATION,
				"radicalProstatectomyMargin", Cardinality.ZERO, Cardinality.MANY));

		radicalProstatectomyPathologyAnnotation.addAbstractAttribute(association2);
		DynamicExtensionsUtility.getConstraintPropertiesForAssociation(association2);

		// Create a melanomaMargin entity.
		EntityInterface melanomaMargin = createAndPopulateEntity();
		melanomaMargin.setName("MelanomaMargin");

		// Create attribute(s) for melanomaMargin entity.
		AttributeInterface melanomaMarginName = factory.createStringAttribute();
		melanomaMarginName.setName("melanomaMarginName");
		((StringAttributeTypeInformation) melanomaMarginName.getAttributeTypeInformation())
				.setSize(40);

		AttributeInterface melanomaMarginType = factory.createStringAttribute();
		melanomaMarginType.setName("melanomaMarginType");
		((StringAttributeTypeInformation) melanomaMarginType.getAttributeTypeInformation())
				.setSize(40);

		melanomaMargin.addAbstractAttribute(melanomaMarginName);
		melanomaMargin.addAbstractAttribute(melanomaMarginType);

		// Associate radicalProstatectomyMargin entity with melanomaMargin entity : radicalProstatectomyMargin (1)------ >(*) melanomaMargin
		AssociationInterface association3 = factory.createAssociation();
		association3.setName("radicalProstatectomyMarginToMelanomaMarginAssociation");
		association3.setTargetEntity(melanomaMargin);
		association3.setAssociationDirection(AssociationDirection.SRC_DESTINATION);

		RoleInterface sourceRole3 = getRole(AssociationType.ASSOCIATION,
				"radicalProstatectomyMargin", Cardinality.ONE, Cardinality.ONE);
		sourceRole3.setAssociationsType(AssociationType.CONTAINTMENT);

		association3.setSourceRole(sourceRole3);
		association3.setTargetRole(getRole(AssociationType.ASSOCIATION, "melanomaMargin",
				Cardinality.ZERO, Cardinality.MANY));

		radicalProstatectomyMargin.addAbstractAttribute(association3);
		DynamicExtensionsUtility.getConstraintPropertiesForAssociation(association3);

		entityGroup.addEntity(baseSolidTissuePathologyAnnotation);
		baseSolidTissuePathologyAnnotation.setEntityGroup(entityGroup);
		entityGroup.addEntity(prostatePathologyAnnotation);
		prostatePathologyAnnotation.setEntityGroup(entityGroup);
		entityGroup.addEntity(gleasonScore);
		gleasonScore.setEntityGroup(entityGroup);
		entityGroup.addEntity(radicalProstatectomyPathologyAnnotation);
		radicalProstatectomyPathologyAnnotation.setEntityGroup(entityGroup);
		entityGroup.addEntity(radicalProstatectomyMargin);
		radicalProstatectomyMargin.setEntityGroup(entityGroup);
		entityGroup.addEntity(melanomaMargin);
		melanomaMargin.setEntityGroup(entityGroup);

		try
		{
			// Save the entity group.
			entityGroupManager.persistEntityGroup(entityGroup);
		}
		catch (DynamicExtensionsSystemException e)
		{
			e.printStackTrace();
			fail();
		}
		catch (DynamicExtensionsApplicationException e)
		{
			e.printStackTrace();
			fail();
		}

		return entityGroup;
	}

	/**
	 *
	 * @param categoryEntity
	 * @return ContainerInterface
	 */
	private ContainerInterface createContainer(AbstractEntityInterface abstractEntity)
	{
		ContainerInterface container = DomainObjectFactory.getInstance().createContainer();
		container.setCaption(abstractEntity.getName() + "_container");
		container.setAbstractEntity(abstractEntity);
		container.setMainTableCss("formRequiredLabel");
		container.setRequiredFieldIndicatior("*");
		container.setRequiredFieldWarningMessage("indicates mandatory fields.");
		abstractEntity.addContainer(container);

		return container;
	}

	/**
	 *
	 * @param categoryAttribute
	 * @param sequenceNumber
	 * @return
	 */
	private TextFieldInterface createTextFieldControl(
			BaseAbstractAttributeInterface baseAbstractAttribute, int sequenceNumber)
	{
		TextFieldInterface textFieldInterface = DomainObjectFactory.getInstance().createTextField();
		textFieldInterface.setCaption(baseAbstractAttribute.getName());
		textFieldInterface.setBaseAbstractAttribute(baseAbstractAttribute);
		textFieldInterface.setColumns(50);
		textFieldInterface.setSequenceNumber(sequenceNumber);

		return textFieldInterface;
	}

	/**
	 *
	 * @param categoryAttribute
	 * @param sequenceNumber
	 * @return ComboBoxInterface
	 */
	private ComboBoxInterface createComboBoxControl(CategoryAttributeInterface categoryAttribute,
			int sequenceNumber)
	{
		ComboBoxInterface comboBox = DomainObjectFactory.getInstance().createComboBox();
		comboBox.setCaption(categoryAttribute.getName());
		comboBox.setBaseAbstractAttribute(categoryAttribute);
		comboBox.setTooltip(categoryAttribute.getName());
		return comboBox;
	}

	/**
	 *
	 * @param entity
	 * @param sequenceNumber
	 * @return
	 */
	private ContainerInterface createContainerForEntity(EntityInterface entity)
	{
		ContainerInterface container = createContainer(entity);
		return container;
	}

	/**
	 *
	 * @param container
	 * @param entity
	 * @param sequenceNumber
	 */
	private void createTextFieldControlForEntity(ContainerInterface container,
			EntityInterface entity, int sequenceNumber)
	{
		for (AttributeInterface attribute : entity.getAttributeCollection())
		{
			TextFieldInterface textField = createTextFieldControl(attribute, ++sequenceNumber);
			textField.setParentContainer((Container) container);
			container.addControl(textField);
		}
	}

	/**
	 * use case: Display of controls in a single line for the attributes of the same class
	 */
	public void testSingleLineDisplay1()
	{
		CategoryInterface category = null;
		try
		{
			importModel(XMI_FILE_PATH + "scg.xmi", CSV_FILE_PATH + "SCG.csv",
					"edu.wustl.catissuecore.domain.PathAnnotation_SCG");

			createCaegory(CSV_FILE_PATH + "singleLineDsiplaySameClassl.csv");

			CategoryManager categoryManager = (CategoryManager) CategoryManager.getInstance();
			category = (CategoryInterface) categoryManager.getObjectByName(
					Category.class.getName(), "singleLineDisplaySameClass1");

			assertNotNull(category.getId());

		}
		catch (Exception e)
		{
			e.printStackTrace();
			fail();
		}
	}

	/**
	 * use case: Single line display controls validations
	 * Two controls on the sinlge line should of the same type
	 */
	public void testSingleLineDisplay2()
	{
		CategoryInterface category = null;
		try
		{
			importModel(XMI_FILE_PATH + "scg.xmi", CSV_FILE_PATH + "SCG.csv",
					"edu.wustl.catissuecore.domain.PathAnnotation_SCG");

			createCaegory(CSV_FILE_PATH + "singleLineDsiplaySameClass2.csv");

			CategoryManager categoryManager = (CategoryManager) CategoryManager.getInstance();
			category = (CategoryInterface) categoryManager.getObjectByName(
					Category.class.getName(), "singleLineDisplaySameClass2");
			assertNotNull(category.getId());

		}
		catch (Exception e)
		{
			fail();
			e.printStackTrace();

		}
	}

	/**
	 * use case: Single line display controls validations
	 * Below category has follwing combinations of controls in single line
	 * 1. combo/text
	 * 2. text/text
	 * 3. combo/combo
	 * Two controls on the sinlge line should of the same type
	 */
	public void testSingleLineDisplayValidControlTypes1()
	{
		CategoryInterface category = null;
		try
		{
			importModel(XMI_FILE_PATH + "scg.xmi", CSV_FILE_PATH + "SCG.csv",
					"edu.wustl.catissuecore.domain.PathAnnotation_SCG");

			createCaegory(CSV_FILE_PATH + "singleLineDsiplaySameClass4.csv");

			CategoryManager categoryManager = (CategoryManager) CategoryManager.getInstance();
			category = (CategoryInterface) categoryManager.getObjectByName(
					Category.class.getName(), "singleLineDisplaySameClass4");
			assertNotNull(category.getId());

		}
		catch (Exception e)
		{
			fail();
			e.printStackTrace();

		}
	}

	/**
	 * use case: Single line display controls validations
	 * Below category has follwing combinations of controls in single line
	 * 1. list/combo
	 * 2. list/list
	 * 3. list/text
	 * Two controls on the sinlge line should of the same type
	 */
	public void testSingleLineDisplayValidControlTypes2()
	{
		CategoryInterface category = null;
		try
		{
			importModel(XMI_FILE_PATH + "scg.xmi", CSV_FILE_PATH + "SCG.csv",
					"edu.wustl.catissuecore.domain.PathAnnotation_SCG");

			createCaegory(CSV_FILE_PATH + "singleLineDsiplaySameClass5.csv");

			CategoryManager categoryManager = (CategoryManager) CategoryManager.getInstance();
			category = (CategoryInterface) categoryManager.getObjectByName(
					Category.class.getName(), "singleLineDisplaySameClass5");
			assertNotNull(category.getId());

		}
		catch (Exception e)
		{
			fail();
			e.printStackTrace();

		}
	}

	/**
	 * use case: Single line display controls validations
	 * Two controls on the sinlge line should of the same type
	 * (Now it is supported) so changed the test case & no error will be thrown.
	 */
	public void testSingleLineDisplay3()
	{
		CategoryInterface category = null;
		try
		{
			importModel(XMI_FILE_PATH + "scg.xmi", CSV_FILE_PATH + "SCG.csv",
					"edu.wustl.catissuecore.domain.PathAnnotation_SCG");

			createCaegory(CSV_FILE_PATH + "singleLineDsiplaySameClass3.csv");

			CategoryManager categoryManager = (CategoryManager) CategoryManager.getInstance();
			category = (CategoryInterface) categoryManager.getObjectByName(
					Category.class.getName(), "singleLineDisplaySameClass3");


		}
		catch (Exception e)
		{
			fail();
			e.printStackTrace();
		}
	}

	/**
	 * Use case: Single line display for the attibutes of the differnt class
	 */
	public void testSingleLineDisplayDiffrentClass1()
	{
		CategoryInterface category = null;
		try
		{
			importModel(XMI_FILE_PATH + "scg.xmi", CSV_FILE_PATH + "SCG.csv",
					"edu.wustl.catissuecore.domain.PathAnnotation_SCG");

			createCaegory(CSV_FILE_PATH + "singleLineDsiplayDifferentClassl.csv");

			CategoryManager categoryManager = (CategoryManager) CategoryManager.getInstance();
			category = (CategoryInterface) categoryManager.getObjectByName(
					Category.class.getName(), "singleLineDisplayDifferentClass1");
			assertNotNull(category.getId());

		}
		catch (Exception e)
		{
			e.printStackTrace();
			fail();
		}
	}

	/**
	 * Negative Use case: Single line display for the attibutes of the differnt class
	 * subcategory used has multiple entities under same diplay label and
	 * show=false
	 */
	public void testSingleLineDisplayDiffrentClass2()
	{
		CategoryInterface category = null;
		try
		{
			importModel(XMI_FILE_PATH + "scg.xmi", CSV_FILE_PATH + "SCG.csv",
					"edu.wustl.catissuecore.domain.PathAnnotation_SCG");

			createCaegory(CSV_FILE_PATH + "singleLineDsiplayDifferentClass2.csv");

			CategoryManager categoryManager = (CategoryManager) CategoryManager.getInstance();
			category = (CategoryInterface) categoryManager.getObjectByName(
					Category.class.getName(), "singleLineDisplayDifferentClass2");

		}
		catch (Exception e)
		{
			e.printStackTrace();
			fail();

		}
	}

	/**
	 * Populate the map with values for data entry.
	 * @param category
	 * @return
	 */
	private Map<BaseAbstractAttributeInterface, Object> populateMap(CategoryInterface category)
	{
		// category association : Annotations[1] to PastMedicalHistory[1]
		String catAssociationMain = "Annotations[1]Annotations[1] to Annotations[1]PastMedicalHistory[1] category association";

		// category association : PastMedicalHistory[1] to LabAnnotation[1] category association
		String catAssociation1 = "Annotations[1]PastMedicalHistory[1] to Annotations[1]LabAnnotation[1] category association";

		// category association : PastMedicalHistory[1] to LabAnnotation[2] category association
		String catAssociation2 = "Annotations[1]PastMedicalHistory[1] to Annotations[1]LabAnnotation[2] category association";

		// category association : PastMedicalHistory[1] to LabAnnotation[3] category association
		String catAssociation3 = "Annotations[1]PastMedicalHistory[1] to Annotations[1]LabAnnotation[3] category association";

		// category association : PastMedicalHistory[1] to LabAnnotation[3] category association
		String catAssociation4 = "Annotations[1]PastMedicalHistory[1] to Annotations[1]HealthExaminationAnnotation[1] category association";

		// category entity Annotations[1]Annotations[1]
		String categoryEntAnnotation = "Annotations[1]Annotations[1]";

		// category entity Annotations[1]PastMedicalHistory[1]
		String categoryEntMain = "Annotations[1]PastMedicalHistory[1]";

		// category entity Annotations[1]LabAnnotation[1]
		String categoryEnt1 = "Annotations[1]LabAnnotation[1]";

		// category entity Annotations[1]LabAnnotation[2]
		String categoryEnt2 = "Annotations[1]LabAnnotation[2]";

		// category entity Annotations[1]LabAnnotation[3]
		String categoryEnt3 = "Annotations[1]LabAnnotation[3]";

		// category entity Annotations[1]HealthExaminationAnnotation[1]
		String categoryEnt4 = "Annotations[1]HealthExaminationAnnotation[1]";

		Map<BaseAbstractAttributeInterface, Object> attributeValues = new HashMap<BaseAbstractAttributeInterface, Object>();
		List<Map<BaseAbstractAttributeInterface, Object>> mainValueMaps = new ArrayList<Map<BaseAbstractAttributeInterface, Object>>();

		// retrieve category entity of the category entity 'Annotations[1]LabAnnotation[1]'
		CategoryEntityInterface catEntAnnotation = category
				.getCategoryEntityByName(categoryEntAnnotation);

		// retrieve category entity of the category entity 'Annotations[1]LabAnnotation[1]'
		CategoryEntityInterface catEntMain = category.getCategoryEntityByName(categoryEntMain);
		CategoryAssociationInterface catAssoMain = catEntAnnotation
				.getAssociationByName(catAssociationMain);
		CategoryAttributeInterface catAttr1 = catEntMain
				.getAttributeByName("comment Category Attribute");
		CategoryAttributeInterface catAttr2 = catEntMain
				.getAttributeByName("dateApproximate Category Attribute");
		CategoryAttributeInterface catAttr3 = catEntMain
				.getAttributeByName("dateOfDiagnosis Category Attribute");
		CategoryAttributeInterface catAttr4 = catEntMain
				.getAttributeByName("laterality Category Attribute");
		CategoryAttributeInterface catAttr5 = catEntMain
				.getAttributeByName("otherDiagnosis Category Attribute");
		CategoryAttributeInterface catAttr6 = catEntMain
				.getAttributeByName("diagnosis Category Attribute");

		// retrieve category entity of the category entity 'Annotations[1]LabAnnotation[1]'
		CategoryEntityInterface catEnt1 = category.getCategoryEntityByName(categoryEnt1);
		CategoryAssociationInterface catAsso1 = catEntMain.getAssociationByName(catAssociation1);
		CategoryAttributeInterface catAttr7 = catEnt1
				.getAttributeByName("quantitativeResult Category Attribute");

		// retrieve category entity of the category entity 'Annotations[1]LabAnnotation[2]'
		CategoryEntityInterface catEnt2 = category.getCategoryEntityByName(categoryEnt2);
		CategoryAssociationInterface catAsso2 = catEntMain.getAssociationByName(catAssociation2);
		CategoryAttributeInterface catAttr8 = catEnt2
				.getAttributeByName("quantitativeResult Category Attribute");

		// retrieve category entity of the category entity 'Annotations[1]LabAnnotation[2]'
		CategoryEntityInterface catEnt3 = category.getCategoryEntityByName(categoryEnt3);
		CategoryAssociationInterface catAsso3 = catEntMain.getAssociationByName(catAssociation3);
		CategoryAttributeInterface catAttr9 = catEnt3
				.getAttributeByName("quantitativeResult Category Attribute");

		// retrieve category entity of the category entity 'Annotations[1]LabAnnotation[2]'
		CategoryEntityInterface catEnt4 = category.getCategoryEntityByName(categoryEnt4);
		CategoryAssociationInterface catAsso4 = catEntMain.getAssociationByName(catAssociation4);
		CategoryAttributeInterface catAttr10 = catEnt4
				.getAttributeByName("nameOfProcedure Category Attribute");
		CategoryAttributeInterface catAttr11 = catEnt4
				.getAttributeByName("otherProcedure Category Attribute");

		Map<BaseAbstractAttributeInterface, Object> valueMap1 = new HashMap<BaseAbstractAttributeInterface, Object>();
		valueMap1.put(catAttr7, "22");
		List<Map<BaseAbstractAttributeInterface, Object>> list1 = new ArrayList<Map<BaseAbstractAttributeInterface, Object>>();
		list1.add(valueMap1);

		Map<BaseAbstractAttributeInterface, Object> valueMap2 = new HashMap<BaseAbstractAttributeInterface, Object>();
		valueMap2.put(catAttr8, "11");
		List<Map<BaseAbstractAttributeInterface, Object>> list2 = new ArrayList<Map<BaseAbstractAttributeInterface, Object>>();
		list2.add(valueMap2);

		Map<BaseAbstractAttributeInterface, Object> valueMap3 = new HashMap<BaseAbstractAttributeInterface, Object>();
		valueMap3.put(catAttr9, "33");
		List<Map<BaseAbstractAttributeInterface, Object>> list3 = new ArrayList<Map<BaseAbstractAttributeInterface, Object>>();
		list3.add(valueMap3);

		Map<BaseAbstractAttributeInterface, Object> valueMap4 = new HashMap<BaseAbstractAttributeInterface, Object>();
		valueMap4.put(catAttr10, "nameOfProcedureTC");
		valueMap4.put(catAttr11, "otherProcedureTC");
		List<Map<BaseAbstractAttributeInterface, Object>> list4 = new ArrayList<Map<BaseAbstractAttributeInterface, Object>>();
		list4.add(valueMap4);

		Map<BaseAbstractAttributeInterface, Object> valueMapMain = new HashMap<BaseAbstractAttributeInterface, Object>();

		// put all category attributes of the category entity 'PastMedicalHistory[1]' in data value map
		valueMapMain.put(catAttr1, "CommentTC");
		valueMapMain.put(catAttr2, "1");
		valueMapMain.put(catAttr3, "07"+ProcessorConstants.DATE_SEPARATOR+"01"+ProcessorConstants.DATE_SEPARATOR+"2009");
		valueMapMain.put(catAttr4, "LeftTC");
		valueMapMain.put(catAttr5, "123");
		valueMapMain.put(catAttr6, "HydronephrosisTC");

		valueMapMain.put(catAsso1, list1);
		valueMapMain.put(catAsso2, list2);
		valueMapMain.put(catAsso3, list3);
		valueMapMain.put(catAsso4, list4);

		mainValueMaps.add(valueMapMain);

		// put all values in the main map
		attributeValues.put(catAssoMain, mainValueMaps);

		return attributeValues;
	}

	/**
	 * Create category with one of the attribute specified with allowfuturedate rule.
	 * Check whether this rule is present for the created category attribute
	 */
	public void testFutureDateRule()
	{
		CategoryInterface category = null;
		try
		{
			importModel(XMI_FILE_PATH + "test_date.xmi", CSV_FILE_PATH + "test_date.csv", "TestAnnotations");

			createCaegory(CSV_FILE_PATH + "categoryFutureDate.csv");

			CategoryManager categoryManager = (CategoryManager) CategoryManager.getInstance();
			category = (CategoryInterface) categoryManager.getObjectByName(
					Category.class.getName(), "futuredate_cat");

			assertNotNull(category.getId());
			CategoryEntityInterface rootCategoryEntity = category.getRootCategoryElement();

			CategoryAttributeInterface categoryAttribute = rootCategoryEntity
					.getAttributeByName("visitDate Category Attribute");
			Collection<RuleInterface> ruleCollection = categoryAttribute.getRuleCollection();
			if (ruleCollection.isEmpty())
			{
				fail();
			}
			boolean isFutureDateRulePresent = false;
			for (RuleInterface rule : ruleCollection)
			{
				if ("allowfuturedate".equalsIgnoreCase(rule.getName()))
				{
					isFutureDateRulePresent = true;
				}

			}
			assertTrue(isFutureDateRulePresent);

		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}

	/**
	 * Negative usecase :-Create category having date type attribute specified with "allowfuturedate" and "daterange" rules.
	 * Error message must thrown for these conflicting rules
	 */
	public void testForConflictingDateRule()
	{
		CategoryInterface category = null;
		try
		{
			CategoryManager categoryManager = (CategoryManager) CategoryManager.getInstance();
			try
			{
				importModel(XMI_FILE_PATH + "test_date.xmi", CSV_FILE_PATH + "test_date.csv", "TestAnnotations");
				createCaegory(CSV_FILE_PATH + "categoryFutureDate1.csv");
				category = (CategoryInterface) categoryManager.getObjectByName(Category.class
						.getName(), "Test category - future_date");
			}
			catch (Exception e)
			{
				Logger.out.info("Could not create category due to conficting rules....");
			}

			assertNull(category);
		}
		catch (Exception e)
		{
			e.printStackTrace();
			fail();
		}
	}

	/**
	 * Negative usecase :-Create category having date type attribute having allowfuturedate at model level.
	 * Error message must thrown for these conflicting rules
	 */
	public void testForOverridingDateRule()
	{
		CategoryInterface category = null;
		try
		{
			CategoryManager categoryManager = (CategoryManager) CategoryManager.getInstance();
			try
			{
				importModel(XMI_FILE_PATH + "test_date.xmi", CSV_FILE_PATH + "test_date.csv", "TestAnnotations");
				createCaegory(CSV_FILE_PATH + "categoryFutureDate3.csv");
				category = (CategoryInterface) categoryManager.getObjectByName(Category.class
						.getName(), "Category_Lab Information");
			}
			catch (Exception e1)
			{
				Logger.out
						.info("Could not create category due to overriding allowfuturedate rule....");
			}

			assertNull(category);
		}
		catch (Exception e)
		{
			e.printStackTrace();
			fail();
		}
	}
}