package br.jaxws.soap.modelo.exception;

import java.util.Date;

import javax.xml.ws.WebFault;

@WebFault(name="AutorizacaoFault" , messageName="AutorizacaoFaultMessageName")
public class AutorizacaoException extends Exception {

	public AutorizacaoException(String mensagem) {
		super(mensagem);
	}

	private static final long serialVersionUID = 1L;
	
	
	public InfoFault getFaultInfo() {
		return new InfoFault("token invalido info fault", new Date());
	}
	
//	public String getFaultInfo() {
//		return "Token Invalido Fault info";
//	}

}
