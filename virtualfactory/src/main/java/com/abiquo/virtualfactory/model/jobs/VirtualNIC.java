//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, vJAXB 2.1.10 in JDK 6 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2011.02.18 at 12:55:00 PM CET 
//


package com.abiquo.virtualfactory.model.jobs;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for VirtualNIC complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="VirtualNIC">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="vSwitchName" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="macAddress" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="networkName" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="vlanTag" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="sequence" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "VirtualNIC", propOrder = {
    "vSwitchName",
    "macAddress",
    "networkName",
    "vlanTag",
    "sequence"
})
public class VirtualNIC {

    @XmlElement(required = true)
    protected String vSwitchName;
    @XmlElement(required = true)
    protected String macAddress;
    @XmlElement(required = true)
    protected String networkName;
    @XmlElement(required = true)
    protected String vlanTag;
    @XmlElement(required = true)
    protected String sequence;

    /**
     * Gets the value of the vSwitchName property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getVSwitchName() {
        return vSwitchName;
    }

    /**
     * Sets the value of the vSwitchName property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setVSwitchName(String value) {
        this.vSwitchName = value;
    }

    /**
     * Gets the value of the macAddress property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getMacAddress() {
        return macAddress;
    }

    /**
     * Sets the value of the macAddress property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setMacAddress(String value) {
        this.macAddress = value;
    }

    /**
     * Gets the value of the networkName property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getNetworkName() {
        return networkName;
    }

    /**
     * Sets the value of the networkName property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setNetworkName(String value) {
        this.networkName = value;
    }

    /**
     * Gets the value of the vlanTag property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getVlanTag() {
        return vlanTag;
    }

    /**
     * Sets the value of the vlanTag property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setVlanTag(String value) {
        this.vlanTag = value;
    }

    /**
     * Gets the value of the sequence property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getSequence() {
        return sequence;
    }

    /**
     * Sets the value of the sequence property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setSequence(String value) {
        this.sequence = value;
    }

}
