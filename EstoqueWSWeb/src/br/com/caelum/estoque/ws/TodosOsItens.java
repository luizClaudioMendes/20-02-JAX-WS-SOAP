
package br.com.caelum.estoque.ws;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Classe Java de TodosOsItens complex type.
 * 
 * <p>O seguinte fragmento do esquema especifica o conteúdo esperado contido dentro desta classe.
 * 
 * <pre>
 * &lt;complexType name="TodosOsItens"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element ref="{http://ws.estoque.caelum.com.br/}filtros" minOccurs="0"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "TodosOsItens", propOrder = {
    "filtros"
})
public class TodosOsItens {

    @XmlElement(namespace = "http://ws.estoque.caelum.com.br/")
    protected Filtros filtros;

    /**
     * Obtém o valor da propriedade filtros.
     * 
     * @return
     *     possible object is
     *     {@link Filtros }
     *     
     */
    public Filtros getFiltros() {
        return filtros;
    }

    /**
     * Define o valor da propriedade filtros.
     * 
     * @param value
     *     allowed object is
     *     {@link Filtros }
     *     
     */
    public void setFiltros(Filtros value) {
        this.filtros = value;
    }

}
