package br.jaxws.soap.modelo.ws;

import java.util.List;

import javax.jws.Oneway;
import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebResult;
import javax.jws.WebService;
import javax.jws.soap.SOAPBinding;
import javax.jws.soap.SOAPBinding.ParameterStyle;
import javax.jws.soap.SOAPBinding.Style;
import javax.jws.soap.SOAPBinding.Use;
import javax.xml.ws.RequestWrapper;
import javax.xml.ws.ResponseWrapper;

import br.jaxws.soap.modelo.dao.ItemDao;
import br.jaxws.soap.modelo.dao.TokenDao;
import br.jaxws.soap.modelo.exception.AutorizacaoException;
import br.jaxws.soap.modelo.item.Filtros;
import br.jaxws.soap.modelo.item.Item;
import br.jaxws.soap.modelo.item.ItemValidador;
import br.jaxws.soap.modelo.usuario.TokenUsuario;

/*
 * a anotaçao @WebService diz ao servidor que esta classe é um web service.
 * 
 * @SOAPBinding(style=Style.RPC, use=Use.ENCODED, parameterStyle=ParameterStyle.BARE)//alternativa para utilizacao do soap (mais antiga)
 * nao reflete o uso. somente expondo as configuraçoes alternativas.
 */
@WebService
@SOAPBinding(style=Style.DOCUMENT, use=Use.LITERAL, parameterStyle=ParameterStyle.WRAPPED)//padrao para utilizacao moderna do soap
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
		
		new ItemValidador(item).validate();;
		
		
		this.dao.cadastrar(item);
		return item;
		
	}
	
	@WebMethod(operationName="gerarRelatorioTeste")
	@Oneway //anotacao que informa que a operation tera somente um input, nao sendo necessario esperar por um output
	public void gerarRelatorioTeste () {
		System.out.println("teste relatorio oneway");
	}

}
