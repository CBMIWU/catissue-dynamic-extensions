//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.0-b26-ea3
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a>
// Any modifications to this file will be lost upon recompilation of the source schema.
// Generated on: 2011.02.17 at 12:38:09 PM GMT+05:30
//


package edu.common.dynamicextensions.util.xml.formdefinition;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for ControllingAttribute element declaration.
 *
 * <p>The following schema fragment specifies the expected content contained within this class.
 *
 * <pre>
 * &lt;element name="ControllingAttribute">
 *   &lt;complexType>
 *     &lt;complexContent>
 *       &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *         &lt;sequence>
 *           &lt;element ref="{}Instance"/>
 *           &lt;element ref="{}Attribute"/>
 *           &lt;element ref="{}DependentAttribute"/>
 *         &lt;/sequence>
 *       &lt;/restriction>
 *     &lt;/complexContent>
 *   &lt;/complexType>
 * &lt;/element>
 * </pre>
 *
 *
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
    "instance",
    "attribute",
    "dependentAttribute"
})
@XmlRootElement(name = "ControllingAttribute")
public class ControllingAttribute {

    @XmlElement(name = "Instance")
    protected Instance instance;
    @XmlElement(name = "Attribute")
    protected Attribute attribute;
    @XmlElement(name = "DependentAttribute")
    protected DependentAttribute dependentAttribute;

    /**
     * Gets the value of the instance property.
     *
     * @return
     *     possible object is
     *     {@link Instance }
     *
     */
    public Instance getInstance() {
        return instance;
    }

    /**
     * Sets the value of the instance property.
     *
     * @param value
     *     allowed object is
     *     {@link Instance }
     *
     */
    public void setInstance(Instance value) {
        this.instance = value;
    }

    /**
     * Gets the value of the attribute property.
     *
     * @return
     *     possible object is
     *     {@link Attribute }
     *
     */
    public Attribute getAttribute() {
        return attribute;
    }

    /**
     * Sets the value of the attribute property.
     *
     * @param value
     *     allowed object is
     *     {@link Attribute }
     *
     */
    public void setAttribute(Attribute value) {
        this.attribute = value;
    }

    /**
     * Gets the value of the dependentAttribute property.
     *
     * @return
     *     possible object is
     *     {@link DependentAttribute }
     *
     */
    public DependentAttribute getDependentAttribute() {
        return dependentAttribute;
    }

    /**
     * Sets the value of the dependentAttribute property.
     *
     * @param value
     *     allowed object is
     *     {@link DependentAttribute }
     *
     */
    public void setDependentAttribute(DependentAttribute value) {
        this.dependentAttribute = value;
    }

}
