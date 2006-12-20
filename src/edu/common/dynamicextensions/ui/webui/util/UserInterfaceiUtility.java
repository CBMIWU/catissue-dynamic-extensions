/**
 * 
 */

package edu.common.dynamicextensions.ui.webui.util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Stack;

import edu.common.dynamicextensions.domaininterface.AbstractAttributeInterface;
import edu.common.dynamicextensions.domaininterface.userinterface.ContainerInterface;
import edu.common.dynamicextensions.domaininterface.userinterface.ContainmentAssociationControlInterface;
import edu.common.dynamicextensions.domaininterface.userinterface.ControlInterface;
import edu.common.dynamicextensions.domaininterface.validationrules.RuleInterface;
import edu.common.dynamicextensions.exception.DynamicExtensionsSystemException;
import edu.wustl.common.util.global.ApplicationProperties;

/**
 * @author chetan_patil
 *
 */
public class UserInterfaceiUtility
{

	/**
	 * 
	 * @param stringBuffer
	 * @param controlInterface
	 * @param containerInterface
	 * @throws DynamicExtensionsSystemException
	 */
	@SuppressWarnings("unchecked")
	public static String generateHTMLforGrid(ContainerInterface subContainer)
			throws DynamicExtensionsSystemException
	{
		StringBuffer stringBuffer = new StringBuffer();
		List<ControlInterface> controlsList = new ArrayList<ControlInterface>(subContainer
				.getControlCollection());
		Collections.sort(controlsList);

		stringBuffer.append("<tr width='100%'><td colspan='3' class='formFieldContainer'>");
		stringBuffer.append("<div style='display:none' id='" + subContainer.getId()
				+ "_substitutionDiv'>");
		stringBuffer.append("<table>");
		stringBuffer.append("<tr width='100%'>");
		stringBuffer.append("<td class='formRequiredNotice' width='1%'>");
		stringBuffer.append("<input type='checkbox' name='deleteRow' value=''/>");
		stringBuffer.append("</td>");
		for (ControlInterface control : controlsList)
		{
			String controlHTML = "";
			control.setIsSubControl(true);

			if (control instanceof ContainmentAssociationControlInterface)
			{
				controlHTML = ((ContainmentAssociationControlInterface) control).generateLinkHTML();
			}
			else
			{
				controlHTML = control.generateHTML();
			}

			stringBuffer.append("<td class='formField'>");
			stringBuffer.append(controlHTML);
			stringBuffer.append("</td>");
		}
		stringBuffer.append("</tr>");
		stringBuffer.append("</table>");
		stringBuffer.append("</div>");

		stringBuffer.append("<input type='hidden' name='" + subContainer.getId()
				+ "_rowCount' id= '" + subContainer.getId() + "_rowCount' value='0'/> ");
		stringBuffer.append("</td></tr>");

		stringBuffer.append("<tr width='100%'>");
		stringBuffer.append("<td class='formFieldContainer' colspan='3' align='center'>");
		stringBuffer.append("<table cellpadding='3' cellspacing='0' align='center' width='100%'>");

		stringBuffer.append("<tr width='100%'>");
		stringBuffer.append("<td class='formTitle' colspan='3' align='left'>");
		stringBuffer.append(subContainer.getCaption());
		stringBuffer.append("</td>");
		stringBuffer.append("</tr>");

		stringBuffer.append("<tr width='100%'>");
		stringBuffer.append("<td class='formFieldContainer' colspan='3'>");
		stringBuffer.append("<table id='" + subContainer.getId()
				+ "_table' cellpadding='3' cellspacing='0' align='center' width='100%'>");

		stringBuffer.append("<tr width='100%'>");
		stringBuffer.append("<th class='formRequiredNotice' width='1%'>&nbsp;</th>");
		for (ControlInterface control : controlsList)
		{
			boolean isControlRequired = isControlRequired(control);
			if (isControlRequired)
			{
				stringBuffer.append("<th class='formRequiredLabel'>");
				stringBuffer.append(subContainer.getRequiredFieldIndicatior() + "&nbsp;"
						+ control.getCaption());
			}
			else
			{
				stringBuffer.append("<th class='formLabel'>");
				stringBuffer.append("&nbsp;" + control.getCaption());
			}
			stringBuffer.append("</th>");
		}

		stringBuffer.append("</tr>");
		stringBuffer.append("</table>");

		stringBuffer
				.append("<table cellpadding='3' cellspacing='0' align='center' width='100%'><tr>");

		stringBuffer.append("<td align='left'>");
		stringBuffer
				.append("<button type='button' class='actionButton' id='removeRow' onclick=\"removeCheckedRow('"
						+ subContainer.getId() + "')\">");
		stringBuffer.append(ApplicationProperties.getValue("buttons.delete"));
		stringBuffer.append("</button>");
		stringBuffer.append("</td>");

		stringBuffer.append("<td align='right'>");
		stringBuffer
				.append("<button type='button' class='actionButton' id='addMore' onclick=\"addRow('"
						+ subContainer.getId() + "')\">");
		stringBuffer.append(ApplicationProperties.getValue("eav.button.AddRow"));
		stringBuffer.append("</button>");
		stringBuffer.append("</td>");

		stringBuffer.append("</tr></table>");

		stringBuffer.append("</td>");
		stringBuffer.append("</tr>");

		stringBuffer.append("</table></td></tr>");

		return stringBuffer.toString();
	}

	/**
	 * 
	 * @param controlInterface
	 * @return
	 */
	public static boolean isControlRequired(ControlInterface controlInterface)
	{
		AbstractAttributeInterface abstractAttribute = controlInterface.getAbstractAttribute();
		Collection<RuleInterface> ruleCollection = abstractAttribute.getRuleCollection();
		boolean required = false;
		if (ruleCollection != null && !ruleCollection.isEmpty())
		{
			for (RuleInterface attributeRule : ruleCollection)
			{
				if (attributeRule.getName().equals("required"))
				{
					required = true;
					break;
				}
			}
		}
		return required;
	}

	/**
	 * 
	 * @param containerStack
	 * @param containerInterface
	 * @param valueMapStack
	 * @param valueMap
	 */
	public static void addContainerInfo(Stack<ContainerInterface> containerStack,
			ContainerInterface containerInterface, Stack<Map> valueMapStack, Map valueMap)
	{
		containerStack.push(containerInterface);
		valueMapStack.push(valueMap);
	}
	
	/**
	 * 
	 * @param containerStack
	 * @param containerInterface
	 * @param valueMapStack
	 * @param valueMap
	 */
	public static void removeContainerInfo(Stack<ContainerInterface> containerStack,
			ContainerInterface containerInterface, Stack<Map> valueMapStack, Map valueMap)
	{
		containerStack.pop();
		valueMapStack.pop();
	}

}
