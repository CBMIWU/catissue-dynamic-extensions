
package edu.common.dynamicextensions.domain.userinterface;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import edu.common.dynamicextensions.domain.AbstractEntity;
import edu.common.dynamicextensions.domain.DynamicExtensionBaseDomainObject;
import edu.common.dynamicextensions.domaininterface.AbstractEntityInterface;
import edu.common.dynamicextensions.domaininterface.BaseAbstractAttributeInterface;
import edu.common.dynamicextensions.domaininterface.userinterface.ContainerInterface;
import edu.common.dynamicextensions.domaininterface.userinterface.ControlInterface;
import edu.common.dynamicextensions.exception.DynamicExtensionsSystemException;
import edu.common.dynamicextensions.ui.util.ControlsUtility;
import edu.common.dynamicextensions.ui.webui.util.UserInterfaceiUtility;
import edu.common.dynamicextensions.ui.webui.util.WebUIManagerConstants;
import edu.wustl.common.util.global.ApplicationProperties;

/**
 * @version 1.0
 * @created 28-Sep-2006 12:20:07 PM
 * @hibernate.class table="DYEXTN_CONTAINER"
 * @hibernate.cache  usage="read-write"
 */
public class Container extends DynamicExtensionBaseDomainObject
		implements
			Serializable,
			ContainerInterface
{

	/**
	 *
	 */
	private static final long serialVersionUID = 8092366994778601914L;

	/**
	 * @return
	 * @hibernate.id name="id" column="IDENTIFIER" type="long"
	 * length="30" unsaved-value="null" generator-class="native"
	 * @hibernate.generator-param name="sequence" value="DYEXTN_CONTAINER_SEQ"
	 */
	public Long getId()
	{
		return id;
	}

	/**
	 * css for the buttons on the container.
	 */
	protected String buttonCss;
	/**
	 * Caption to be displayed on the container.
	 */
	protected String caption;
	/**
	 * css for the main table in the container.
	 */
	protected String mainTableCss;
	/**
	 * Specifies the indicator symbol that will be used to denote a required field.
	 */
	protected String requiredFieldIndicatior;
	/**
	 * Specifies the warning mesaage to be displayed in case required fields are not entered by the user.
	 */
	protected String requiredFieldWarningMessage;
	/**
	 * css of the title in the container.
	 */
	protected String titleCss;
	/**
	 * Collection of controls that are in this container.
	 */
	protected Collection<ControlInterface> controlCollection = new HashSet<ControlInterface>();
	/**
	 *
	 */
	protected Map containerValueMap = new HashMap<BaseAbstractAttributeInterface, Object>();
	/**
	 * Entity to which this container is associated.
	 */
	protected AbstractEntity abstractEntity;
	/**
	 *
	 */
	protected String mode = WebUIManagerConstants.EDIT_MODE;
	/**
	 *
	 */
	protected Boolean showAssociationControlsAsLink = false;

	/**
	 * parent of this entity, null is no parent present.
	 */
	protected ContainerInterface baseContainer = null;

	/**
	 *
	 */
	protected ContainerInterface incontextContainer = this;

	private Boolean addCaption = true;

	private Collection<ContainerInterface> childContainerCollection = new HashSet<ContainerInterface>();

	/**
	 * Is Ajax Request
	 */
	private boolean isAjaxRequest = false;

	/**
	 * HTTP request Object
	 */
	private HttpServletRequest request;

	/**
	 * Show required field warning message
	 */
	private Boolean showRequiredFieldWarningMessage = true;
	/**
	 * @hibernate.set name="childContainerCollection" table="DYEXTN_CONTAINER"
	 * cascade="all-delete-orphan" inverse="false" lazy="false"
	 * @hibernate.collection-key column="PARENT_CONTAINER_ID"
	 * @hibernate.cache  usage="read-write"
	 * @hibernate.collection-one-to-many class="edu.common.dynamicextensions.domain.userinterface.Container"
	 * @return the childCategories
	 */
	public Collection<ContainerInterface> getChildContainerCollection()
	{
		return childContainerCollection;
	}

	public void setChildContainerCollection(Collection<ContainerInterface> childContainerCollection)
	{
		this.childContainerCollection = childContainerCollection;
	}

	/**
	 * @return
	 */
	public String getMode()
	{
		return mode;
	}

	/**
	 * @param mode
	 */
	public void setMode(String mode)
	{
		this.mode = mode;
	}

	/**
	 * @hibernate.property name="buttonCss" type="string" column="BUTTON_CSS"
	 * @return Returns the buttonCss.
	 */
	public String getButtonCss()
	{
		return buttonCss;
	}

	/**
	 * @param buttonCss The buttonCss to set.
	 */
	public void setButtonCss(String buttonCss)
	{
		this.buttonCss = buttonCss;
	}

	/**
	 * @hibernate.property name="caption" type="string" column="CAPTION" length="800"
	 * @return Returns the caption.
	 */
	public String getCaption()
	{
		return caption;
	}

	/**
	 * @param caption The caption to set.
	 */
	public void setCaption(String caption)
	{
		this.caption = caption;
	}

	/**
	 * @hibernate.set name="controlCollection" table="DYEXTN_CONTROL"
	 * cascade="all-delete-orphan" inverse="false" lazy="false"
	 * @hibernate.collection-key column="CONTAINER_ID"
	 * @hibernate.cache  usage="read-write"
	 * @hibernate.collection-one-to-many class="edu.common.dynamicextensions.domain.userinterface.Control"
	 * @return Returns the controlCollection.
	 */
	public Collection<ControlInterface> getControlCollection()
	{
		return controlCollection;
	}

	/**
	 * @param controlCollection The controlCollection to set.
	 */
	public void setControlCollection(Collection<ControlInterface> controlCollection)
	{
		this.controlCollection = controlCollection;
	}

	/**
	 * @hibernate.many-to-one column ="ABSTRACT_ENTITY_ID" class="edu.common.dynamicextensions.domain.AbstractEntity"
	 * cascade="save-update"
	 * @return Returns the entity.
	 */
	public AbstractEntityInterface getAbstractEntity()
	{
		return abstractEntity;
	}

	/**
	 * @hibernate.property name="mainTableCss" type="string" column="MAIN_TABLE_CSS"
	 * @return Returns the mainTableCss.
	 */
	public String getMainTableCss()
	{
		return mainTableCss;
	}

	/**
	 * @param mainTableCss The mainTableCss to set.
	 */
	public void setMainTableCss(String mainTableCss)
	{
		this.mainTableCss = mainTableCss;
	}

	/**
	 * @hibernate.property name="requiredFieldIndicatior" type="string" column="REQUIRED_FIELD_INDICATOR"
	 * @return Returns the requiredFieldIndicatior.
	 */
	public String getRequiredFieldIndicatior()
	{
		return requiredFieldIndicatior;
	}

	/**
	 * @param requiredFieldIndicatior The requiredFieldIndicatior to set.
	 */
	public void setRequiredFieldIndicatior(String requiredFieldIndicatior)
	{
		this.requiredFieldIndicatior = requiredFieldIndicatior;
	}

	/**
	 * @hibernate.property name="requiredFieldWarningMessage" type="string" column="REQUIRED_FIELD_WARNING_MESSAGE"
	 * @return Returns the requiredFieldWarningMessage.
	 */
	public String getRequiredFieldWarningMessage()
	{
		return requiredFieldWarningMessage;
	}

	/**
	 * @param requiredFieldWarningMessage The requiredFieldWarningMessage to set.
	 */
	public void setRequiredFieldWarningMessage(String requiredFieldWarningMessage)
	{
		this.requiredFieldWarningMessage = requiredFieldWarningMessage;
	}

	/**
	 * @hibernate.property name="titleCss" type="string" column="TITLE_CSS"
	 * @return Returns the titleCss.
	 */
	public String getTitleCss()
	{
		return titleCss;
	}

	/**
	 * @param titleCss The titleCss to set.
	 */
	public void setTitleCss(String titleCss)
	{
		this.titleCss = titleCss;
	}

	/**
	 *
	 */
	public void addControl(ControlInterface controlInterface)
	{
		if (controlCollection == null)
		{
			controlCollection = new HashSet<ControlInterface>();
		}

		controlCollection.add(controlInterface);
		controlInterface.setParentContainer(this);
	}

	/**
	 *
	 */
	public void setAbstractEntity(AbstractEntityInterface abstractEntityInterface)
	{
		abstractEntity = (AbstractEntity) abstractEntityInterface;
	}

	/**
	 *
	 * @param sequenceNumber
	 * @return
	 */
	public ControlInterface getControlInterfaceBySequenceNumber(String sequenceNumber)
	{
		boolean found = false;
		ControlInterface control = null;
		Collection<ControlInterface> controls = getControlCollection();
		if (controls != null)
		{
			Iterator<ControlInterface> controlsIter = controls.iterator();
			while (controlsIter.hasNext())
			{
				control = controlsIter.next();
				if (control.getSequenceNumber().equals(Integer.valueOf(sequenceNumber)))
				{
					found = true;
					break;
				}
			}
		}

		return found ? control : null;
	}

	/**
	 *
	 */
	public void removeControl(ControlInterface control)
	{
		if ((control != null) && (controlCollection != null)
				&& (controlCollection.contains(control)))
		{
			controlCollection.remove(control);
		}
	}

	/**
	 * remove all controls from the controls collection
	 */
	public void removeAllControls()
	{
		if (controlCollection != null)
		{
			controlCollection.clear();
		}
	}

	/**
	 * @see edu.common.dynamicextensions.domaininterface.EntityInterface#getAllAttributes()
	 */
	public List<ControlInterface> getAllControls()
	{
		List<ControlInterface> controls = new ArrayList<ControlInterface>(getControlCollection());
		Collections.sort(controls);

		List<ControlInterface> baseControls = new ArrayList<ControlInterface>();

		ContainerInterface baseContainer = this.baseContainer;
		while (baseContainer != null)
		{
			baseControls = new ArrayList(baseContainer.getControlCollection());
			Collections.sort(baseControls);
			Collections.reverse(baseControls);

			controls.addAll(baseControls);

			baseContainer.setIncontextContainer(this);
			baseContainer = baseContainer.getBaseContainer();
		}

		Collections.reverse(controls);

		return controls;
	}

	/**
	 * @return
	 */
	public List<ControlInterface> getAllControlsUnderSameDisplayLabel()
	{
		List<ControlInterface> controls = new ArrayList<ControlInterface>(getControlCollection());

		for (ContainerInterface container : childContainerCollection)
		{
			controls.addAll(container.getAllControls());
		}

		List<ControlInterface> baseControls = new ArrayList<ControlInterface>();

		ContainerInterface baseContainer = this.baseContainer;
		while (baseContainer != null)
		{
			baseControls = new ArrayList(baseContainer.getControlCollection());
			Collections.reverse(baseControls);

			controls.addAll(baseControls);

			baseContainer.setIncontextContainer(this);
			baseContainer = baseContainer.getBaseContainer();
		}

		Collections.sort(controls);
		Collections.reverse(controls);

		return controls;
	}

	/**
	 * @param control
	 */
	private void updateValueMap(ControlInterface control)
	{
		Object value = containerValueMap.get(control.getBaseAbstractAttribute());

		Map<BaseAbstractAttributeInterface, Object> displayContainerValueMap = null;
		if (value != null && value instanceof List)
		{
			if (((List) value).size() > 0)
			{
				displayContainerValueMap = ((List<Map<BaseAbstractAttributeInterface, Object>>) value)
						.get(0);
				((AbstractContainmentControl) control).isCardinalityOneToMany();
			}
		}
		else
		{
			displayContainerValueMap = (Map<BaseAbstractAttributeInterface, Object>) value;
		}
		if (displayContainerValueMap != null)
		{
			for (BaseAbstractAttributeInterface abstractAttributeInterface : displayContainerValueMap.keySet())
			{
				containerValueMap.put(abstractAttributeInterface, displayContainerValueMap
						.get(abstractAttributeInterface));
			}

			containerValueMap.remove(control.getBaseAbstractAttribute());
		}
	}

	/**
	 * @return return the HTML string for this type of a object
	 * @throws DynamicExtensionsSystemException
	 */
	public String generateContainerHTML(String caption,String dataEntryOperation) throws DynamicExtensionsSystemException
	{
		StringBuffer containerHTML = new StringBuffer();

		containerHTML
				.append("<table summary='' cellpadding='3' cellspacing='0' align='center' width='100%'>");

		if (getMode() != null
				&& getMode().equalsIgnoreCase(WebUIManagerConstants.EDIT_MODE) && (isShowRequiredFieldWarningMessage()))
		{
			containerHTML.append("<tr><td class='formMessage' colspan='3'><span class='font_red'>");
			containerHTML.append(getRequiredFieldIndicatior());
			containerHTML.append("&nbsp;</span><span class='font_gr_s'>");
			containerHTML.append(getRequiredFieldWarningMessage());
			containerHTML.append("</span></td></tr><tr><td height='5'/></tr>");

		}
		else
		{
			//Changed by : Kunal
			//Reviewed by: Sujay
			//Container hierarchy can be n level
			//So, mode of the n containers in the hierarchy need to be same.

			ContainerInterface tempContainer = baseContainer;
			while (tempContainer != null)
			{
				tempContainer.setMode(mode);
				tempContainer = tempContainer.getBaseContainer();
			}
		}

		containerHTML.append(generateControlsHTML(caption,dataEntryOperation,this));
		containerHTML.append("</table>");

		return containerHTML.toString();
	}
	/**
	 *
	 */
	public boolean isAllControlsSkipLogicTargetControlsForShowHide()
	{
		boolean isAlltargetControls = true;
		List<ControlInterface> controls = getAllControlsUnderSameDisplayLabel();
		for (ControlInterface control :controls)
		{
			if (!control.getIsSkipLogicTargetControl() && !control.getIsShowHide())
			{
				isAlltargetControls = false;
				break;
			}
		}
		return isAlltargetControls;
	}
	/**
	 * @return return the HTML string for this type of a object
	 * @throws DynamicExtensionsSystemException
	 */
	public String generateControlsHTML(String caption,String dataEntryOperation,ContainerInterface container) throws DynamicExtensionsSystemException
	{
		StringBuffer controlHTML = new StringBuffer();
		List<Object> values = new ArrayList<Object>();

		addCaption(controlHTML, caption,container,values);

		List<ControlInterface> controls = getAllControlsUnderSameDisplayLabel(); //UnderSameDisplayLabel();
		int lastRow = 0;
		int i = 0;
		for (ControlInterface control : controls)
		{
			if (control.getIsSkipLogicTargetControl())
			{
				Object value = null;
				values.clear();

				ControlsUtility
						.getAttributeValueForSkipLogicAttributesFromValueMap(
								container.getContainerValueMap(), container
										.getContainerValueMap(), control
										.getSourceSkipControl()
										.getBaseAbstractAttribute(), false,
								values, Integer.valueOf(-1), Integer
										.valueOf(-1));

				if (!values.isEmpty())
				{
					value = values.get(0);
				}
				control.getSourceSkipControl().setValue(value);
				control.getSourceSkipControl().setSkipLogicControls();
			}
			control.setDataEntryOperation(dataEntryOperation);
			Object value = containerValueMap.get(control.getBaseAbstractAttribute());
			control.setValue(value);
			if (lastRow == control.getSequenceNumber())
			{
				controlHTML.append("<div style='float:left'>&nbsp;</div>");
				//controlHTML.append("</div>");
			}
			else
			{
				if (i != 0)
				{
					controlHTML.append("</td></tr><tr><td height='7'></td></tr>");
				}
				controlHTML.append("<tr valign='center' ");
				if (control.getIsSkipLogicTargetControl())
				{
					controlHTML.append("id='"
							+ control.getHTMLComponentName() + "_row_div' name='"
							+ control.getHTMLComponentName() + "_row_div'");

				}
				if (control.getIsSkipLogicTargetControl())
				{
					if (control.getIsSkipLogicShowHideTargetControl())
					{
						controlHTML.append(" style='display:none'");
					}
				}
				else
				{
					controlHTML.append(" style='display:row'");
				}
				controlHTML.append(">");
				if (control.getIsSkipLogicTargetControl())
				{
					controlHTML.append("<input type='hidden' name='skipLogicHideControls' id='skipLogicHideControls' value = '"
						+ control.getHTMLComponentName() + "_row_div' />");
				}
			}
			controlHTML.append(control.generateHTML(container));
			i++;
			lastRow = control.getSequenceNumber();
		}
		controlHTML.append("</td></tr>");
		showAssociationControlsAsLink = false;

		return controlHTML.toString();
	}

	/**
	 * @param captionHTML
	 * @param caption in the format -- NewCaption:Main ContainerId
	 * @throws DynamicExtensionsSystemException
	 */
	private void addCaption(StringBuffer captionHTML, String caption,ContainerInterface container,List<Object> values) throws DynamicExtensionsSystemException
	{
		//check if Id in caption matches current id - if yes then it is main form, so replace caption
		if (caption == null || !caption.endsWith(id.toString()))
		{
			if (addCaption) // for subform
			{
				captionHTML.append("<tr ");
				addDisplayOptionForRow(container,values,captionHTML,"_caption");
				captionHTML.append("<td class='td_color_6e81a6' colspan='100' align='left'>");
				captionHTML.append(((AbstractEntity) getAbstractEntity())
						.getCapitalizedName(getCaption()));
				captionHTML.append("<tr ");
				addDisplayOptionForRow(container,values,captionHTML,"_emptyrow");
				captionHTML.append("<td height='5'></td></tr>");
			}
		}
		else
		{ // for Main form
			captionHTML.append("<tr><td class='td_color_6e81a6_big' colspan='100' align='left'>");
			captionHTML.append(caption
					.substring(0, (caption.length() - 1 - id.toString().length())));
			captionHTML.append("<tr><td height='5'></td></tr>");
		}

	}
	/**
	 *
	 * @param container
	 * @param values
	 * @param captionHTML
	 * @throws DynamicExtensionsSystemException
	 */
	private void addDisplayOptionForRow(ContainerInterface container,List<Object> values,StringBuffer captionHTML,String id) throws DynamicExtensionsSystemException
	{
		if (isAllControlsSkipLogicTargetControlsForShowHide())
		{
			List<ControlInterface> controls = getAllControlsUnderSameDisplayLabel();
			if (!controls.isEmpty())
			{
				ControlInterface control = controls.get(0);
				captionHTML.append("id='"
						+ control.getHTMLComponentName() + id + "_container_div' name='"
						+ control.getHTMLComponentName() + id + "_container_div'");

				Object value = null;
				values.clear();

				ControlsUtility
						.getAttributeValueForSkipLogicAttributesFromValueMap(
								container.getContainerValueMap(), container
										.getContainerValueMap(), control
										.getSourceSkipControl()
										.getBaseAbstractAttribute(), false,
								values, Integer.valueOf(-1), Integer
										.valueOf(-1));
				if (!values.isEmpty())
				{
					value = values.get(0);
				}
				control.getSourceSkipControl().setValue(value);
				control.getSourceSkipControl().setSkipLogicControls();

				if (control.getIsSkipLogicShowHideTargetControl())
				{
					captionHTML.append(" style='display:none' >");
				}
				else
				{
					captionHTML.append(" style='display:row' >");
				}
				captionHTML.append("<input type='hidden' name='skipLogicHideControls' id='skipLogicHideControls' value = '"
						+ control.getHTMLComponentName() + id + "_container_div' />");
			}
		}
		else
		{
			captionHTML.append(" style='display:row' >");
		}
	}
	/**
	 *
	 * @return
	 * @throws DynamicExtensionsSystemException
	 */
	public String generateControlsHTMLAsGrid(
			List<Map<BaseAbstractAttributeInterface, Object>> valueMaps,String dataEntryOperation,ContainerInterface container)
			throws DynamicExtensionsSystemException
	{
		return UserInterfaceiUtility.generateHTMLforGrid(this, valueMaps,dataEntryOperation,container);
	}

	/**
	 * @return
	 */
	public Map<BaseAbstractAttributeInterface, Object> getContainerValueMap()
	{
		return containerValueMap;
	}

	/**
	 * @param containerValueMap
	 */
	public void setContainerValueMap(Map<BaseAbstractAttributeInterface, Object> containerValueMap)
	{
		this.containerValueMap = containerValueMap;
	}

	/**
	 *
	 * @return
	 */
	public Boolean getShowAssociationControlsAsLink()
	{
		return showAssociationControlsAsLink;
	}

	/**
	 *
	 * @param showAssociationControlsAsLink
	 */
	public void setShowAssociationControlsAsLink(Boolean showAssociationControlsAsLink)
	{
		this.showAssociationControlsAsLink = showAssociationControlsAsLink;
	}

	/**
	 * @see edu.common.dynamicextensions.domaininterface.userinterface.ContainmentAssociationControlInterface#generateLinkHTML()
	 */
	public String generateLink(ContainerInterface container)
			throws DynamicExtensionsSystemException
	{
		String details = "";
		boolean isDataPresent = UserInterfaceiUtility.isDataPresent(container
				.getContainerValueMap());
		if (isDataPresent)
		{
			if (mode.equals(WebUIManagerConstants.EDIT_MODE))
			{
				details = ApplicationProperties.getValue("eav.att.EditDetails");
			}
			else if (mode.equals(WebUIManagerConstants.VIEW_MODE))
			{
				details = ApplicationProperties.getValue("eav.att.ViewDetails");
			}
		}
		else
		{
			if (mode.equals(WebUIManagerConstants.EDIT_MODE))
			{
				details = ApplicationProperties.getValue("eav.att.EnterDetails");
			}
			else if (mode.equals(WebUIManagerConstants.VIEW_MODE))
			{
				details = ApplicationProperties.getValue("eav.att.NoDataToView");
			}
		}

		StringBuffer linkHTML = new StringBuffer();
		linkHTML
				.append("<img src='images/de/ic_det.gif' alt='Details' width='12' height='12' hspace='3' border='0' align='absmiddle'><a href='#' style='cursor:hand' class='set1' id='lnkDetails");
		linkHTML.append(container.getId());
		linkHTML.append("' onclick='showChildContainerInsertDataPage(");
		linkHTML.append(container.getId());
		linkHTML.append(",this)'>");
		linkHTML.append(details);
		linkHTML.append("</a><tr><td></td></tr>");

		return linkHTML.toString();
	}

	/**
	 * @see edu.common.dynamicextensions.domaininterface.EntityInterface#getParentEntity()
	 * @hibernate.many-to-one column="BASE_CONTAINER_ID" class="edu.common.dynamicextensions.domain.userinterface.Container" constrained="true"
	 *                        cascade="save-update"
	 */
	public ContainerInterface getBaseContainer()
	{
		return baseContainer;
	}

	/**
	 *
	 * @param baseContainer
	 */
	public void setBaseContainer(ContainerInterface baseContainer)
	{
		this.baseContainer = baseContainer;
	}

	/**
	 * @return the incontextContainer
	 */
	public ContainerInterface getIncontextContainer()
	{
		return incontextContainer;
	}

	/**
	 * @param incontextContainer the incontextContainer to set
	 */
	public void setIncontextContainer(ContainerInterface incontextContainer)
	{
		this.incontextContainer = incontextContainer;
	}

	/**
	 * @hibernate.property name="addCaption" type="boolean" column="ADD_CAPTION"
	 * @return Returns the addCaption.
	 */
	public Boolean getAddCaption()
	{
		return addCaption;
	}

	public void setAddCaption(Boolean addCaption)
	{
		this.addCaption = addCaption;
	}

	/**
	 * @param xPosition
	 * @param yPosition
	 * @return
	 */
	public ControlInterface getControlByPosition(Integer xPosition, Integer yPosition)
	{
		ControlInterface control = null;

		for (ControlInterface cntrl : controlCollection)
		{
			if (cntrl.getSequenceNumber() != null && cntrl.getSequenceNumber().equals(xPosition)
					&& cntrl.getSequenceNumber() != null && cntrl.getYPosition().equals(yPosition))
			{
					control = cntrl;
			}
		}

		return control;
	}

	public boolean isAjaxRequest()
	{
		return isAjaxRequest;
	}

	public void setAjaxRequest(boolean isAjaxRequest)
	{
		this.isAjaxRequest = isAjaxRequest;
	}


	/**
	 * @return the request
	 */
	public HttpServletRequest getRequest()
	{
		return request;
	}


	/**
	 * @param request the request to set
	 */
	public void setRequest(HttpServletRequest request)
	{
		this.request = request;
	}

	/**
	 * @return the showRequiredFieldWarningMessage
	 */
	public Boolean isShowRequiredFieldWarningMessage()
	{
		return showRequiredFieldWarningMessage;
	}


	/**
	 * @param showRequiredFieldWarningMessage the showRequiredFieldWarningMessage to set
	 */
	public void setShowRequiredFieldWarningMessage(Boolean showRequiredFieldWarningMessage)
	{
		this.showRequiredFieldWarningMessage = showRequiredFieldWarningMessage;
	}

}