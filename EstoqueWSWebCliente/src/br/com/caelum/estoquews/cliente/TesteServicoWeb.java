package br.com.caelum.estoquews.cliente;

public class TesteServicoWeb {

	public static void main(String[] args) {
		EstoqueWS cliente = new EstoqueWS_Service().getEstoqueWPort();
		
		
		Filtro filtro = new Filtro();
		filtro.setNome("Iphone");
		filtro.setTipo("Celular");
		
		Filtros filtros = new Filtros();
		filtros.getFiltro().add(filtro);
		
		ListaItens lista = cliente.todosOsItens(filtros);
		
		for (Item item : lista.getItem()) {
			System.out.println(item);
		}
		

	}

}
