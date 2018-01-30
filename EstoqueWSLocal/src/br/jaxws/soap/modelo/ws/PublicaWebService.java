package br.jaxws.soap.modelo.ws;

import javax.xml.ws.Endpoint;

public class PublicaWebService {

	public static void main(String[] args) {
//		publicar o servico
		EstoqueWS service = new EstoqueWS();
		String URL = "http://localhost:8080/estoquews";

		Endpoint.publish(URL, service); 
	}

}
