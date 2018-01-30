package br.jaxws.soap.modelo.ws;

import java.util.List;

import javax.jws.WebService;

import br.jaxws.soap.modelo.item.Item;
import br.jaxws.soap.modelo.item.ItemDao;

@WebService
public class EstoqueWS {
	
	private ItemDao dao = new ItemDao();
	
	public List<Item> getItens() {
		System.out.println("chamando getItens");
		List<Item> lista = dao.todosItens();
		return lista;
	}

}
