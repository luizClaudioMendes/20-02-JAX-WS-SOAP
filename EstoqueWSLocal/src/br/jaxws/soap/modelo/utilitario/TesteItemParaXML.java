package br.jaxws.soap.modelo.utilitario;

import java.io.File;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;

import br.jaxws.soap.modelo.item.Item;

public class TesteItemParaXML {

	public static void main(String[] args) throws JAXBException {
		
		Item item = new Item.Builder().comCodigo("MEA").comNome("MEAN").comQuantidade(4).comTipo("Livro").build();

        JAXBContext context = JAXBContext.newInstance(Item.class);
        Marshaller marshaller = context.createMarshaller();
        marshaller.marshal(item, new File("itemTeste.xml")); //ou marshaller.marshal(item, System.out);  
	}

}
