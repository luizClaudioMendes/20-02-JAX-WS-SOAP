package br.jaxws.soap.modelo.ws;

import java.util.List;

import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebResult;
import javax.jws.WebService;
import javax.xml.ws.RequestWrapper;
import javax.xml.ws.ResponseWrapper;

import br.jaxws.soap.modelo.item.Filtros;
import br.jaxws.soap.modelo.item.Item;
import br.jaxws.soap.modelo.item.ItemDao;
import br.jaxws.soap.modelo.usuario.AutorizacaoException;
import br.jaxws.soap.modelo.usuario.TokenDao;
import br.jaxws.soap.modelo.usuario.TokenUsuario;

/*
 * a anotaçao @WebService diz ao servidor que esta classe é um web service.
 */
@WebService
public class EstoqueWS {
	
	private ItemDao dao = new ItemDao();
	
	/*
	 * a anotaçao @WebMethod Customizes a method that is 
	 * exposed as a Web Service operation. The associated 
	 * method must be public and its parameters return value.
	 * neste caso, a operation no wsdl tera o name="todosOsItens"
	 * 
	 * a anotaçao @WebResult Customizes the mapping of the return 
	 * value to a WSDL part and XML element.
	 * ou seja, no xml de retorno o item individual se chamara
	 * "item"
	 * 
	 */
//	@WebMethod(operationName="todosOsItens")
//	@ResponseWrapper(localName="itens")//anotacao que altera o nome do retorno
//	@RequestWrapper(localName="listaItens")//anotacao que altera o nome da solicitacao
//	@WebResult(name="itens")//esta anotacao indicara a lista de itens retornados
//	public ListaItens  getItens(@WebParam(name="filtros") Filtros filtro) { //como colocamos a anotacao @ResponseWrapper(localName="itens") já nao precisamos usar a classe wrapper ListaItens
//		return new ListaItens(lista);
//	}
	@WebMethod(operationName="todosOsItens")
	@ResponseWrapper(localName="itens")//anotacao que altera o nome do retorno
	@RequestWrapper(localName="listaItens")//anotacao que altera o nome da solicitacao
	@WebResult(name="itens")//esta anotacao indicara a lista de itens retornados
	public List<Item>  getItens(@WebParam(name="filtros") Filtros filtro) { //a anotacao @WebParam serve para dar nomes aos parametros
		System.out.println("chamando getItens");
		List<Item> lista = dao.todosItens(filtro.getLista());		
		return lista;
	}
	
	@WebMethod(operationName="cadastraritem")
//	@ResponseWrapper(localName="itens")
//	@RequestWrapper(localName="listaItens")
	@WebResult(name="itemCadastrado")
	public Item cadastrarItem(@WebParam(name="token", header=true) TokenUsuario token, @WebParam(name="item") Item item) throws AutorizacaoException {
		System.out.println("chamando cadastrarItem: "+item+" / "+token);
		
		boolean valido = new TokenDao().ehValido(token);
		
		if(!valido) {
			throw new AutorizacaoException("token invalido fault String.");
		}
		
		
		
		this.dao.cadastrar(item);
		return item;
		
	}

}
