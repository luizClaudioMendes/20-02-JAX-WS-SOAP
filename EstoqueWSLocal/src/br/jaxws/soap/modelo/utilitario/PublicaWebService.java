package br.jaxws.soap.modelo.utilitario;

import javax.xml.ws.Endpoint;

import br.jaxws.soap.modelo.ws.EstoqueWS;

public class PublicaWebService {

	public static void main(String[] args) {
		/*
		 * para publicar um endpoint:
		 * 1 - devemos criar uma instancia do servico
		 * 2 - devemos ter a URL do servico
		 * 3 - com a classe Endpoint e o metodo publish(), publicamos o servico passando a URL e o serviço
		 */
		EstoqueWS service = new EstoqueWS();
		String URL = "http://localhost:8080/estoquews";

		Endpoint.publish(URL, service); 
		
		System.out.println("o webservice esta funcionando em :"+ URL+"?wsdl");
	}

}
