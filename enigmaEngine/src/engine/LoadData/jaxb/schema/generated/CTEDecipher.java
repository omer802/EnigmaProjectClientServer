//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.2.8-b130911.1802 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2022.10.09 at 06:51:14 PM IDT 
//


package engine.LoadData.jaxb.schema.generated;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for anonymous complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType>
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element ref="{}CTE-Dictionary"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
    "cteDictionary"
})
@XmlRootElement(name = "CTE-Decipher")
public class CTEDecipher {

    @XmlElement(name = "CTE-Dictionary", required = true)
    protected CTEDictionary cteDictionary;

    /**
     * Gets the value of the cteDictionary property.
     * 
     * @return
     *     possible object is
     *     {@link CTEDictionary }
     *     
     */
    public CTEDictionary getCTEDictionary() {
        return cteDictionary;
    }

    /**
     * Sets the value of the cteDictionary property.
     * 
     * @param value
     *     allowed object is
     *     {@link CTEDictionary }
     *     
     */
    public void setCTEDictionary(CTEDictionary value) {
        this.cteDictionary = value;
    }

}
