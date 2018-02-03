package br.jaxws.soap.modelo.validador;

import java.text.SimpleDateFormat;
import java.util.Date;

import javax.xml.bind.annotation.adapters.XmlAdapter;

public class DateAdapter extends XmlAdapter<String, Date>{
	
	private String pattern = "dd/MM/yyyy";

	/**
	 * converte uma data em string em uma Date
	 * @param dateString
	 * @return
	 * @throws Exception
	 */
    public Date unmarshal(String dateString) throws Exception {
    	System.out.println("chamando o unmarshal : "+dateString);
    	
    	Date retorno = new SimpleDateFormat(pattern).parse(dateString);
    	
    	System.out.println("Date formatada: " + retorno);
        return retorno;
    }

    /**
     * comverte uma date em uma String
     * @param date
     * @return
     * @throws Exception
     */
    public String marshal(Date date) throws Exception {
    	System.out.println("chamando o marshal : "+date);
    	   return new SimpleDateFormat(pattern).format(date);
    	}
}
