package br.jaxws.soap.modelo.usuario;

import javax.xml.ws.WebFault;

@WebFault(name="AutorizacaoFault")
public class AutorizacaoException extends Exception {

	public AutorizacaoException(String mensagem) {
		super(mensagem);
	}

	private static final long serialVersionUID = 1L;
	
	
	public String getFaultInfo() {
		return "Token Invalido Fault info";
	}

}
