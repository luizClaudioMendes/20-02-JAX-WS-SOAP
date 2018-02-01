package br.jaxws.soap.modelo.item;

import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * esta classe é somente um wrapper para a classe List,
 * de forma a podermos customizar o retorno no XML.
 * @author admin
 *
 */
@XmlRootElement//anotacao JAX-B -- realiza a ligacao entre o mundo java e XML
@XmlAccessorType(XmlAccessType.FIELD)//anotacao JAX-B -- realiza a configuracao do JAX-B informando-o que devera acessar pelos campos
@Deprecated
public class ListaItens {

	@XmlElement(name="item")//esta anotacao indicará o nome individual de cada item no XML
	private List<Item> itens;

	public ListaItens(List<Item> itens) {
		this.itens = itens;
	}

	ListaItens() {
	}
	
	public List<Item> getItens() {
		return itens;
	}
	
}
