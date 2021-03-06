//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.0-b26-ea3
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a>
// Any modifications to this file will be lost upon recompilation of the source schema.
// Generated on: 2010.07.16 at 05:58:21 PM IST
//

package edu.common.dynamicextensions.util.xml;

import java.util.Collection;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlType;

import edu.common.dynamicextensions.domaininterface.SemanticPropertyInterface;

/**
 * <p>Java class for qualifierType complex type.
 *
 * <p>The following schema fragment specifies the expected content contained within this class.
 *
 * <pre>
 * &lt;complexType name="qualifierType">
 *   &lt;complexContent>
 *     &lt;extension base="{}primaryDefinitionType">
 *       &lt;attribute name="number" use="required" type="{http://www.w3.org/2001/XMLSchema}long" />
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 *
 *
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "qualifierType", propOrder = {
    "qualifierConceptDefinitionSource",
    "qualifierConceptCode",
    "qualifierConceptPreferredName",
    "qualifierConceptDefinition"
})


public class QualifierType implements SemanticPropertyInterface
{

	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = -9112022290977002587L;

	/** The concept definition source. */
	protected String qualifierConceptDefinitionSource;

    /** The concept code. */
    protected String qualifierConceptCode;

    /** The concept preferred name. */
    protected String qualifierConceptPreferredName;

    /** The concept definition. */
    protected String qualifierConceptDefinition;

	@XmlAttribute(name = "number", required = true)
	protected long sequenceNumber;


	/**
	 * @return the qualifierConceptDefinitionSource
	 */
	public String getQualifierConceptDefinitionSource()
	{
		return qualifierConceptDefinitionSource;
	}


	/**
	 * @param qualifierConceptDefinitionSource the qualifierConceptDefinitionSource to set
	 */
	public void setQualifierConceptDefinitionSource(String qualifierConceptDefinitionSource)
	{
		this.qualifierConceptDefinitionSource = qualifierConceptDefinitionSource;
	}


	/**
	 * @return the qualifierConceptCode
	 */
	public String getQualifierConceptCode()
	{
		return qualifierConceptCode;
	}


	/**
	 * @param qualifierConceptCode the qualifierConceptCode to set
	 */
	public void setQualifierConceptCode(String qualifierConceptCode)
	{
		this.qualifierConceptCode = qualifierConceptCode;
	}


	/**
	 * @return the qualifierConceptPreferredName
	 */
	public String getQualifierConceptPreferredName()
	{
		return qualifierConceptPreferredName;
	}


	/**
	 * @param qualifierConceptPreferredName the qualifierConceptPreferredName to set
	 */
	public void setQualifierConceptPreferredName(String qualifierConceptPreferredName)
	{
		this.qualifierConceptPreferredName = qualifierConceptPreferredName;
	}


	/**
	 * @return the qualifierConceptDefinition
	 */
	public String getQualifierConceptDefinition()
	{
		return qualifierConceptDefinition;
	}


	/**
	 * @param qualifierConceptDefinition the qualifierConceptDefinition to set
	 */
	public void setQualifierConceptDefinition(String qualifierConceptDefinition)
	{
		this.qualifierConceptDefinition = qualifierConceptDefinition;
	}

	/**
     * Gets the value of the conceptDefinitionSource property.
     * @return the concept definition source
     * possible object is
     * {@link String }
     */
    public String getConceptDefinitionSource() {
        return getQualifierConceptDefinitionSource();
    }

    /**
     * Sets the value of the conceptDefinitionSource property.
     * @param value allowed object is
     * {@link String }
     */
    public void setConceptDefinitionSource(String value) {
        setQualifierConceptDefinitionSource(value);
    }

    /**
     * Gets the value of the conceptCode property.
     * @return the concept code
     * possible object is
     * {@link String }
     */
    public String getConceptCode() {
        return getQualifierConceptCode();
    }

    /**
     * Sets the value of the conceptCode property.
     * @param value allowed object is
     * {@link String }
     */
    public void setConceptCode(String value) {
        setQualifierConceptCode(value);
    }

    /**
     * Gets the value of the conceptPreferredName property.
     * @return the concept preferred name
     * possible object is
     * {@link String }
     */
    public String getConceptPreferredName() {
        return getQualifierConceptPreferredName();
    }

    /**
     * Sets the value of the conceptPreferredName property.
     * @param value allowed object is
     * {@link String }
     */
    public void setConceptPreferredName(String value) {
        setQualifierConceptPreferredName(value);
    }

    /**
     * Gets the value of the conceptDefinition property.
     * @return the concept definition
     * possible object is
     * {@link String }
     */
    public String getConceptDefinition() {
        return getQualifierConceptDefinition();
    }

    /**
     * Sets the value of the conceptDefinition property.
     * @param value allowed object is
     * {@link String }
     */
    public void setConceptDefinition(String value) {
        setQualifierConceptDefinition(value);
    }

    /**
     * Gets the Qualifier number.
     * @return the sequence number
     * @see edu.common.dynamicextensions.domaininterface.SemanticPropertyInterface#getSequenceNumber()
     */
    public long getSequenceNumber()
    {
    	return sequenceNumber;
    }

    /**
     * Sets the Qualifier number.
     * @param sequenceNumber the sequence number
     * @see edu.common.dynamicextensions.domaininterface.SemanticPropertyInterface#setSequenceNumber(long)
     */
    public void setSequenceNumber(long sequenceNumber)
    {
    	this.sequenceNumber = sequenceNumber;
    }

	/**
	 * Gets the collection of Qualifier.
	 * @return the list of qualifier
	 * @see edu.common.dynamicextensions.domaininterface.SemanticPropertyInterface#getListOfQualifier()
	 */
	public Collection<SemanticPropertyInterface> getListOfQualifier()
	{
		return null;
	}


	/**
	 * Sets the collection of Qualifier.
	 * @param listOfQualifier the list of qualifier
	 * @see edu.common.dynamicextensions.domaininterface.SemanticPropertyInterface#setListOfQualifier(java.util.Collection)
	 */
	public void setListOfQualifier(Collection<SemanticPropertyInterface> listOfQualifier)
	{

	}

	/**
	 * Compares two objects.
	 * @param o the o
	 * @return the int
	 * @see java.lang.Comparable#compareTo(java.lang.Object)
	 */
	public int compareTo(Object o)
	{
		return 0;
	}

}