//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, vJAXB 2.1.10 in JDK 6 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2011.02.18 at 11:15:04 AM CET 
//


package com.abiquo.virtualfactory.model.jobs;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for VirtualMachineAction_In complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="VirtualMachineAction_In">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="hypervisorConnection" type="{http://abiquo.com/virtualfactory/model/jobs}HypervisorConnection"/>
 *         &lt;element name="virtualMachineID" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="state" type="{http://abiquo.com/virtualfactory/model/jobs}State"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "VirtualMachineAction_In", propOrder = {
    "hypervisorConnection",
    "virtualMachineID",
    "state"
})
public class VirtualMachineActionIn {

    @XmlElement(required = true)
    protected HypervisorConnection hypervisorConnection;
    @XmlElement(required = true)
    protected String virtualMachineID;
    @XmlElement(required = true)
    protected State state;

    /**
     * Gets the value of the hypervisorConnection property.
     * 
     * @return
     *     possible object is
     *     {@link HypervisorConnection }
     *     
     */
    public HypervisorConnection getHypervisorConnection() {
        return hypervisorConnection;
    }

    /**
     * Sets the value of the hypervisorConnection property.
     * 
     * @param value
     *     allowed object is
     *     {@link HypervisorConnection }
     *     
     */
    public void setHypervisorConnection(HypervisorConnection value) {
        this.hypervisorConnection = value;
    }

    /**
     * Gets the value of the virtualMachineID property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getVirtualMachineID() {
        return virtualMachineID;
    }

    /**
     * Sets the value of the virtualMachineID property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setVirtualMachineID(String value) {
        this.virtualMachineID = value;
    }

    /**
     * Gets the value of the state property.
     * 
     * @return
     *     possible object is
     *     {@link State }
     *     
     */
    public State getState() {
        return state;
    }

    /**
     * Sets the value of the state property.
     * 
     * @param value
     *     allowed object is
     *     {@link State }
     *     
     */
    public void setState(State value) {
        this.state = value;
    }

}
