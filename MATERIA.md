# CURSO DE JAX-WS-SOAP
###### concluído em 04/02/2018

## WHAT I NEED?
- Objetivo: **desenvolver um projeto Java para criar um serviço SOAP**. 

- necessário:

    Java JDK 8 (pode ser JDK 7) 

    Eclipse IDE for Java EE Developers. 

    JBoss Wildfly 8.x 

    SoapUI.
    
    
## INTRODUÇÃO
A troca de informações faz parte da maioria dos sistema, sendo raro o funcionamento de um sistema isoladamente. Há sistemas de terceiros, como Google Maps ou PayPal, mas também há na mesma empresa aplicações separadas que se conectam e trocam informações. 

Há vários motivos porque sistemas precisam trocar informações, pode ser que precisamos algum dado específico que a aplicação cuida ou acessar alguma lógica. 

De qualquer forma, quando um sistema ou processo acessa o outro para trocar informações falamos de integração. 

O problema é que nem sempre é fácil fazer essa integração funcionar.

A maneira de integração mais difundida hoje em dia está no uso de **Web Services**. 

Existem várias maneiras de se implementar um Web Service, mas apesar de ser um termo genérico, existe algo muito bem especificado pela W3C.

Um dos quesitos primordiais durante a elaboração desta especificação foi que precisaríamos aproveitar toda a plataforma, arquitetura e protocolos já existentes a fim de minimizar o impacto de integrar sistemas. 

Criar um novo protocolo do zero era fora de cogitação.

Por esses motivos o **Web Service** do **W3C** é baseado em **HTTP** e **XML**, duas tecnologias onipresentes e que a maioria das linguagens sabe trabalhar muito bem.


## SOAP na JRE
Quando Java nasceu uma das principais características da plataforma era funcionar bem na rede, ou seja, na internet. 

Por isso o Java já vem com as principais classes para se conectar com recursos na rede. 

Quando os serviços Web surgiram e ganharam popularidade as primeiras bibliotecas eram exclusivamente do servidor de aplicação.

Isso significa que na época era preciso usar um servidor de aplicação para publicar o serviço web. Desde a versão **1.6** do Java isso mudou e as classes para rodar um **Web Service SOAP** foram embutidas na **JRE**. 

Dentro da plataforma Java as bibliotecas são organizadas em especificações. 

A especificação que trata de **SOAP** se chama **JAX-WS** (Java API for XML - Web Service) e a sua implementação referencial, o **Metro** (https://jax-ws.java.net/), está embutida na **JRE**.

Nas primeiras seções vamos utilizar apenas a JRE para criar serviços web para simplificar o ambiente de execução. Então não preciso usar o servidor mais sofisticado para Web Services? Claro que não pois dentro de uma aplicação há várias outras preocupações, além da integração na web. 

Mais para frente veremos como usar um serviço **SOAP** dentro de servidor de aplicação.


## Ambiente de execução
No nosso ambiente de desenvolvimento usaremos **JRE** na versão **1.8** (mas poderia ser 1.7) e **Eclipse Luna** na versão Java EE. 

As duas ferramentas já estão instalados então podemos começar criar um novo** projeto Java padrão**.

A ideia do projeto é simular uma **aplicação de estoque**. 

Vamos imaginar que essa aplicação foi criada em Java e fez bastante sucesso, tanto que outras aplicações também gostariam de acessar informações sobre o estoque. Como não todas as aplicações foram escritos em Java é preciso pensar na integração heterogênea, ideal então para usar um Web Service!

O modelo dessa aplicação já está pronto e possui duas classes. Temos uma classe **Item** e um **ItemDao**. 

O primeiro representa um item no estoque e possui os atributos codigo, nome, tipo e quantidade:


    public class Item {
       		private String codigo;
       		private String nome;
        		private String tipo;
        		private int quantidade;
    
        		//construtores  e gets/sets omitidos

O ItemDao ainda não acessa o banco de dados e cria apenas alguns objetos em memória. Os métodos do DAO são encontrar e cadastra itens:


    public class ItemDao {
       		 private static Map<String, Item> ITENS = new HashMap<>();
    
       		 public ItemDao() {
            			// populando alguns itens no estoque
           			 ITENS.put("MEA", new Item("MEA", "MEAN", "Livro", 5));
           			 ITENS.put("SEO", new Item("SEO", "SEO na Prática", "Livro", 4));
           			 ITENS.put("IP4", new Item("IP4", "IPhone 4 C", "Celular", 7));
           			 ITENS.put("GAL", new Item("GAL", "Galaxy Tab", "Tablet", 3));
           			 ITENS.put("MOX", new Item("MOX", "Moto X", "Celular" , 6));
        		}
    
        		//métodos para cadastrar e procurar Item


## Criação do serviço web
Vamos chamar a classe que realmente representa a implementação do serviço web de **EstoqueWS**. 

Ela não tem nada especial e possui por enquanto um método apenas que chamaremos de *getItens()*. 

O método devolve uma lista de itens que buscaremos do DAO:


    public class EstoqueWS {
        		private ItemDao dao = new ItemDao();
       		public List<Item> getItens() {
            		System.out.println("Chamando getItens()");
            		return dao.todosItens();
        }
    }

Para realmente indicar que queremos criar o Web Service devemos usar a anotação **@WebService**. Ou seja, a nossa intenção é chamar aquele método usando HTTP e XML:


    @WebService
    	public class EstoqueWS {
       		 //...
    	}
Pronto, é a forma mais simples possível de criar um serviços web!


## Publicando (programaticamente) o primeiro Endpoint
Ok, mas ainda falta uma coisa. Como não estamos usando um servidor formal é preciso publicar o serviço programaticamente. 

No mundo de serviços web isso é chamado de publicar o Endpoint. 

O Endpoint é o endereço concreto do serviço. 

A classe Endpoint possui o papel de associar a nossa implementação EstoqueWS com uma URL:


    public class PublicaEstoqueWS {
        		public static void main(String[] args) {
            			EstoqueWS implementacaoWS = new EstoqueWS();
            			String URL = "http://localhost:8080/estoquews";
            			System.out.println("EstoqueWS rodando: " + URL);
            			//associando URL com implementacao
           			 Endpoint.publish(URL, implementacaoWS);
       		 }
    	}























## KEYWORDS

- **Web Service** é baseado em **HTTP** e **XML**

- Dentro da plataforma Java as bibliotecas são organizadas em especificações. A especificação que trata de **SOAP** se chama **JAX-WS** (**Java API for XML - Web Service**)

- Para realmente indicar que queremos criar o Web Service devemos usar a anotação **@WebService**.

- publicar o serviço é o mesmo que publicar o Endpoint.

- **WSDL** significa **Web Service Description Language** e não é nada mais do que um **XML** que **descreve o serviço**

- ** mensagem SOAP** possui um **Envelope**, um **Header** (cabeçalho opcional) e um **Body** que possui um elemento com o mesmo nome do método no serviço

- Ao submeter uma requisição SOAP recebemos uma resposta SOAP

- O **Endpoint** é o **endereço concreto do serviço**

- Um namespace é parecido com o package do mundo Java e ajuda distinguir elementos e evitar conflitos de nomes (xmlns:soapenv="http://schemas.xmlsoap.org/soap/envelope/").

- **RPC** (**Remote Procedure Call**). Objetivo desse estilo de integração é chamar um procedimento remotamente (via rede usamos **HTTP** com **SOAP**).

- A mensagem SOAP se baseia no **input** ou **output** das operações do **portType**.

- com **@WebMethod**, podemos redefinir o nome da operation no **WSDL** e assim também manipular a mensagem **SOAP**. ex: **@WebMethod(operationName="todosOsItens")**

- com **@WebResult(name="item")** podemos alterar o nome apresentado para cada item

- **@XmlRootElement** When a top level class or an enum type is annotated with the @XmlRootElement annotation, then its value is represented as XML element in an XML document.

- para não criar um getter e setter, vamos definir o acesso pelo atributo pela anotação **@XmlAccessorType(XmlAccessType.FIELD)**

- **@WebMethod(exclude=true)** exclui um metodo publico do contrato do wsdl

- **@WebParam** serve para nomear os parametros que o metodo recebe pelo SOAP

- O **binding** define detalhes sobre a codificação dos dados e como se monta a mensagem SOAP

- Uma exceção (Exceptions) no mundo Java é traduzido para um Fault.

- Cabeçalhos servem para guardar informações e dados da aplicação

- A anotação **@WebParam** serve para definir o **Header**

- **Tipo de Faults** 
-- **Modeled** (Modelado) - As definições desse Fault estão no arquivo **WSDL**, as falhas **SOAP** são previstas no **WSDL**.
-- **Unmodeled** (Não modelado) - Para mapear uma exceção (normalmente do tipo java.lang.**RuntimeException**) que acontecerá em **tempo de execução**.

- O Fault deve aparecer logo abaixo do Body

- Segue também uma breve descrição dos elementos principais de um Fault:
-- **< faultcode>** - **Server** ou **Client** para indicar onde ocorreu o problema, mas existem outros como **VersionMissmatch**
-- **< faultstring>** - uma **explicação do Fault legível para humanos**
-- **< detail>** - mais informações sobre o Fault, normalmente específicas da aplicação

- **O < faultcode> e < faultstring> são obrigatórios.**

- A primeira parte do **WSDL** é a declaracao do **XSD**. **Tudo que está trafegando dentro de uma mensagem SOAP deve estar declarado de alguma forma no XSD**

- A segunda parte do **WSDL** são as **mensagens**. As mensagens se baseiam no XSD e cada uma representa uma entrada ou saída do serviço.

- Logo após as mensagem, encontramos a seção **portType** que associa as **mensagem** a uma **operação**.

- o contrato é dividido em duas partes: A primeira parte que já vimos, com as **operações**, **mensagens** e **tipos** que chamamos de **WSDL abstrato**. A segunda parte que terá definições sobre o **protocolo**, **endereço** e **codificação** das mensagens chamamos de **WSDL concreto**.

- O **XSD** ou **XML Schema** ou apenas **Schema** descreve a estrutura de um documento XML.

- Quais elementos fazem parte do WSDL?
-- Os elementos que definem o **WSDL abstrato** são: **< types>**, **< message>** e **< portType>**. 
-- Os elementos que definem o **WSDL concreto** são: **< binding>** e **< service>**.

- O **< portType>** é parecido com uma interface Java e define as operações com entrada e saída.

- O protocolo **HTTP** é utilizado por baixo dos panos como um **protocolo de transporte (/soap/http)**.

- O cliente envia uma requisição **SOAP** para executar o método ou procedimento no servidor. Para atender essa forma de chamada foi criado o estilo **RPC** que significa **Remote Procedure Call** (Chamada remota de um procedimento) um estilo de integração muito antigo que foi criado muito antes do mundo SOAP.  
Para usar **RPC** com **SOAP** devemos enviar primeiro o nome do método ou procedimento e, logo abaixo, os parâmetros.

- **Document/Wrapped**
Para não gerar problemas de compatibilidade, a grande maioria dos serviços usa hoje em dia o estilo **Document**. O grande problema do Document é que não havia uma forma padrão para fazer RPC! Isso mudou, como vocês já viram podemos usar o estilo **Document** para fazer uma chamada remota de um método. Basta embrulhar o documento em um elemento XML como mesmo nome do método! Esse forma se chama de **Document/Wrapped**. Ou seja, usamos o tempo todo Document/Wrapped para fazer RPC.

- Produzir um serviço a partir de um WSDL é chamado de Contract first.

- Gerar o **WSDL** a partir de uma classe Java, ou seja implementar primeiro o serviço (e o resto é gerado) é chamado de **Contract last**.

- O **Metro** possui uma ferramenta, o **wsimport** (e justamente essa ferramenta **só vem com o JDK**), que **consegue gerar as classes que acessam o serviço de uma maneira transparente**.

- O **Port** é nada mais do que o **objeto que se comunica com o serviço**! Ele abstrai todos os detalhes como estabelecer a conexão **HTTP** e gerar a mensagem **SOAP**. Em alguns casos ele também é chamado de stub. De qualquer forma, no mundo de padrões de projeto esse objeto também é chamado de proxy ou remote proxy.

- O **Port** é o objeto que se comunica com o serviço remotamente. Ele abstrai todos os detalhes como estabelecer a conexão HTTP e gerar a mensagem SOAP.

- **wsimport** e **wsdl2java** são ferramentas de linha de comando para **gerar as classes Java** a partir do **WSDL**.



## AULA 1
