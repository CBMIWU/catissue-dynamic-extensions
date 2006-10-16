package edu.common.dynamicextensions.processor;

import edu.common.dynamicextensions.domain.DomainObjectFactory;
import edu.common.dynamicextensions.domaininterface.userinterface.ContainerInterface;
import edu.common.dynamicextensions.ui.interfaces.ContainerInformationInterface;

public class ContainerProcessor extends BaseDynamicExtensionsProcessor {
	/**
	 * Protected constructor for ControlProcessor
	 *
	 */
	protected  ContainerProcessor () {

	}
	/**
	 * this method gets the new instance of the ControlProcessor to the caller.
	 * @return ControlProcessor ControlProcessor instance
	 */
	public static ContainerProcessor getInstance () {
		return new ContainerProcessor();
	}
	public ContainerInterface createContainer() {
		return DomainObjectFactory.getInstance().createContainer();
	}
	public void populateContainerInterface(ContainerInterface containerInterface, ContainerInformationInterface containerInformationInterface) {
		containerInterface.setButtonCss(containerInformationInterface.getButtonCss());
		containerInterface.setCaption(containerInformationInterface.getFormCaption());
		containerInterface.setMainTableCss(containerInformationInterface.getMainTableCss());
		containerInterface.setRequiredFieldIndicatior(containerInformationInterface.getRequiredFieldIndicatior());
		containerInterface.setRequiredFieldWarningMessage(containerInformationInterface.getRequiredFieldWarningMessage());
		containerInterface.setTitleCss(containerInformationInterface.getTitleCss());
	}
	/**
	 * 
	 */
	public void populateContainerInformation(ContainerInterface containerInterface, ContainerInformationInterface containerInformationInterface) {
		containerInformationInterface.setButtonCss(containerInterface.getButtonCss());
		containerInformationInterface.setFormCaption(containerInterface.getCaption());
		containerInformationInterface.setMainTableCss(containerInterface.getMainTableCss());
		containerInformationInterface.setRequiredFieldIndicatior(containerInterface.getRequiredFieldIndicatior());
		containerInformationInterface.setRequiredFieldWarningMessage(containerInterface.getRequiredFieldWarningMessage());
		containerInformationInterface.setTitleCss(containerInterface.getTitleCss());
	}
	

}
