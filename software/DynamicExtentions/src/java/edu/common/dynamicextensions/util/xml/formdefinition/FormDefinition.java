//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.0-b26-ea3
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a>
// Any modifications to this file will be lost upon recompilation of the source schema.
// Generated on: 2011.02.17 at 12:38:09 PM GMT+05:30
//


package edu.common.dynamicextensions.util.xml.formdefinition;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for FormDefinition element declaration.
 *
 * <p>The following schema fragment specifies the expected content contained within this class.
 *
 * <pre>
 * &lt;element name="FormDefinition">
 *   &lt;complexType>
 *     &lt;complexContent>
 *       &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *         &lt;sequence>
 *           &lt;element ref="{}Form" maxOccurs="unbounded"/>
 *         &lt;/sequence>
 *         &lt;attribute name="name" use="optional" type="{http://www.w3.org/2001/XMLSchema}string" />
 *         &lt;attribute name="entityGroup" use="optional" type="{http://www.w3.org/2001/XMLSchema}string" />
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
    "form"
})
@XmlRootElement(name = "FormDefinition")
public class FormDefinition {

    @XmlElement(name = "Form")
    protected List<Form> form;
    @XmlAttribute
    protected String name;
    @XmlAttribute
    protected String entityGroup;

    /**
     * Gets the value of the form property.
     *
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the form property.
     *
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getForm().add(newItem);
     * </pre>
     *
     *
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link Form }
     *
     *
     */
    public List<Form> getForm() {
        if (form == null) {
            form = new ArrayList<Form>();
        }
        return this.form;
    }

    /**
     * Gets the value of the name property.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getName() {
        return name;
    }

    /**
     * Sets the value of the name property.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setName(String value) {
        this.name = value;
    }

    /**
     * Gets the value of the entityGroup property.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getEntityGroup() {
        return entityGroup;
    }

    /**
     * Sets the value of the entityGroup property.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setEntityGroup(String entityGroup) {
        this.entityGroup = entityGroup;
    }

}
