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


## O Contrato do Serviço
Como visto na URL, vamos acessar o serviço através do protocolo **HTTP**. Também usaremos **XML**, como já falamos. O nosso serviço se chama **estoquews** na **URL** e define um método com o nome *getItens* que devolve uma lista de itens.

Então a gente poderia criar uma página **HTML** com as informações de quais métodos estão disponíveis no serviço, além de explicar alguns detalhes aos clientes. Mas será que é preciso criar uma página dessas página para cada serviço?

Não, claro que não. Deve haver alguma forma mais fácil de descrever o nosso serviço, alguma forma automática. Podemos ver a mágica usando um parâmetro especial na URL:

**http://localhost:8080/estoquews?wsdl**

**WSDL** significa **Web Service Description Language** e não é nada mais do que um **XML** que descreve o nosso serviço!

###### Nele temos todas as informações, independente do Java, que um cliente precisa para chamar o Endpoint. 

Fácil não? Bom, ainda não entendemos tudo nesse arquivo. E mesmo com nosso serviços simples tem bastante informação, mas saiba que esse arquivo não foi feito para nós humanos e sim para ferramentas interpretarem e criarem o cliente.



## Testando o serviço com SoapUI
Para testar o nosso serviço vamos criar um cliente. Ou seja, vamos usar uma ferramenta que irá interpretar o WSDL e gerar um cliente que sabe usar o nosso serviço. 

Existem várias ferramentas para tal, uma dos mais famosas no mercado é o **SoapUI**. Que é uma aplicação Java (mas não precisaria ser) que possui uma interface fácil de usar, ideal para testes.

Ao iniciar o SoapUI devemos criar um **New SOAP Project**. No Diálogo basta colocar a **URL** do **WSDL** no campo **Initial WSDL**:

**http://localhost:8080/estoquews?wsdl**

Ao confirmar é gerado os dados para enviar uma requisição SOAP. Isto é, uma requisição **HTTP POST** que envia **XML**. 

###### Esse XML é a mensagem SOAP!



## O primeiro XML SOAP
Repare que uma mensagem **SOAP** possui um **Envelope**, um **Header** (cabeçalho opcional) e um **Body** que possui um elemento com o mesmo nome do método no serviço: *getItens*.

```xml
<soapenv:Envelope xmlns:soapenv="http://schemas.xmlsoap.org/soap/envelope/" xmlns:ws="http://ws.estoque.caelum.com.br/">
   		<soapenv:Header></soapenv:Header>
   		<soapenv:Body>
     		 	<ws:getItens></ws:getItens>
  		 </soapenv:Body>
		</soapenv:Envelope>
```
Ao submeter uma requisição **SOAP** recebemos uma resposta **SOAP**. Um **XML** com a mesma estrutura, apenas o corpo da mensagem (Body) muda:
```xml
<S:Envelope xmlns:S="http://schemas.xmlsoap.org/soap/envelope/">
   		<S:Body>
     			 <ns2:getItensResponse xmlns:ns2="http://ws.estoque.caelum.com.br/">
        				 <return>
            				<codigo>GAL</codigo>
            				<nome>Galaxy Tab</nome>
            				<quantidade>3</quantidade>
            				<tipo>Tablet</tipo>
         				</return>
       				  ...
         				<return>
           				<codigo>IP4</codigo>
            				<nome>IPhone 4 C</nome>
            				<quantidade>7</quantidade>
            				<tipo>Celular</tipo>
         				</return>
      			</ns2:getItensResponse>
   		</S:Body>
	</S:Envelope>
```
Qualquer mensagem **SOAP** possui um **Envelope** e um **Body**, apenas o **Header é opcional**. A nossa mensagem SOAP não está perfeita e tem como melhorar muito. Mas agora é a hora dos exercícios!

O que você aprendeu?
- Serviços Web são utilizados para integrar sistemas
- SOAP é XML que trafega em cima do protocolo HTTP
- o JRE já vem com o JAX-WS (Metro) para usar SOAP
- o contrato do serviço é o WSDL que também é um XML
- uma mensagem SOAP possui um Envelope e um Body,
- na mensagem SOAP o Header é opcional

As outras siglas também são referente de especificações Java EE:
- **JAX-RS**, especificação para criar **serviços web baseado no REST**
- **JAX-B**, especificação para **mapear** (binding) **XML para objetos Java**
- **JAX-RPC**, antigo padrão de serviços web, o nome antigo do **JAX-WS**
- **JAX-P**, especificação para **ler e escrever XML** (processing)

Qual é o papel SOAP no serviço web?
O **SOAP** é um padrão ou protocolo que define o **XML** que trafega entre **Cliente e Servidor** quando o serviço web é executado. Esse XML também é chamado de **mensagem SOAP**.

Ela consiste de três partes: **um Envelope que é o elemento raiz**. Dentro dele pode ter um **Header** para definir **meta-informações como tokens, senha, etc** e um **Body** que contém os **dados principais da mensagem**.

###### É importante mencionar que a mensagem SOAP normalmente trafega em cima do protocolo HTTP, no entanto não depende dele.

Ou seja, a mensagem SOAP poderia ser trafegada usando outros protocolos.



## Mãos a obra: O primeiro serviço web
Vamos preparar o projeto e as classes do Web Service!

1) Se não fez ainda, crie um novo projeto Java no Eclipse com o nome estoquews. O projeto não tem nada de especial, é um projeto Java padrão.

Nesse projeto vamos simular uma aplicação de estoque. Imagine que essa aplicação já foi criada em Java e fez bastante sucesso, tanto que outras aplicações também gostariam de acessar informações sobre o estoque. Como nem todas as aplicações foram escritas em Java é preciso pensar na integração heterogênea, ideal então para usar um Web Service!

~~2) O modelo dessa aplicação já está pronto então e possui algumas classes que usaremos durante o treinamento. Você pode baixar o modelo da aplicação aqui. Copie todo o código fonte para a pasta src do projeto.~~

3) Para este exercício inicial usaremos a classe **Item** e um **ItemDao** que você acabou de importar. O primeiro representa um item no estoque e possui os atributos *codigo*, *nome*, *tipo* e *quantidade*.

O **ItemDao** fornece os dados mas não acessa o banco de dados de verdade. Ele cria apenas alguns objetos em memória. Os métodos do DAO são *todosItens* e *cadastrar*.

4) Crie uma nova classe **EstoqueWS** dentro do pacote br.com.estoque.ws. A classe vai ter um método *getItens* que devolve a lista de itens carregada pelo DAO:

5) Use a anotação **@WebService** na classe **EstoqueWS**


## Mãos a obra: Publicar um Endpoint!
Como não estamos usando ainda um servidor de aplicação é preciso subir o serviço programaticamente. 

No mundo de serviços web, subir é chamado de **publicar o Endpoint**. 

O Endpoint é o endereço concreto do serviço e o **JAX-WS** oferece uma classe com o mesmo nome! Com ela podemos **associar uma URL com a implementação do serviço web**. 

Vamos lá, mãos a obra!

1) No projeto estoquews crie uma nova classe **PublicaEstoqueWS** dentro do pacote br.com.estoque.ws.

2) Gere o método main.

3) No método main instancie o objeto do serviço, define a URL e use a classe Endpoint:

```java
EstoqueWS implementacaoWS = new EstoqueWS();
	String URL = "http://localhost:8080/estoquews";
	System.out.println("EstoqueWS rodando: " + URL);
	//associando URL com a implementacao
	Endpoint.publish(URL, implementacaoWS);
```
4) Rode a classe **PublicaEstoqueWS** e acesse a **URL** com o sufixo **?wsdl: http://localhost:8080/estoquews?wsdl**

Segue uma vez o código completo para publicar um serviço web:

```java
public class PublicaEstoqueWS {
    public static void main(String[] args) {
        EstoqueWS implementacaoWS = new EstoqueWS();
        String URL = "http://localhost:8080/estoquews";
        System.out.println("EstoqueWS rodando: " + URL);
        //associando URL com a implementacao
        Endpoint.publish(URL, implementacaoWS);
    }
}
```


## Mão a obra: Usando SoapUI
Criamos o Web Service através do **JAX-WS**, chegou a hora de testar o nosso serviço. 

Testar significa criar um cliente que entende a interface WSDL e sabe enviar uma mensagem SOAP. Para os testes vamos usar o SoapUI.

Para começar a usar o SoapUI basta acessar o site e baixar a versão atual: http://www.soapui.org/

SoapUI vem com duas versões: SoapUI e SoapUI Pro. A versão Pro é paga e oferece alguns recursos a mais, como relatórios e métricas, depuração de testes entre outros. 

Após ter baixado e instalado o SoapUI podemos criar um novo projeto do tipo SOAP.

1) Ao iniciar o SoapUI crie um New SOAP Project.
2) No diálogo coloque a URL do WSDL no campo Initial WSDL: http://localhost:8080/estoquews?wsdl
3) Confirme. Automaticamente é gerado um cliente que sabe enviar uma requisição SOAP.
4) Abra o elemento **Request 1** abaixo do elemento **todosItens**.
5) Execute a requisição SOAP!

## Para saber mais: O namespace no XML do SOAP
Veja a mensagem SOAP do request:
```xml
<soapenv:Envelope xmlns:soapenv="http://schemas.xmlsoap.org/soap/envelope/" xmlns:ws="http://ws.estoque.com.br/">
   <soapenv:Header></soapenv:Header>
   <soapenv:Body>
      <ws:todosItens></ws:todosItens>
   </soapenv:Body>
</soapenv:Envelope>
```
No primeiro elemento do XML (Envelope) temos a definições de duas URLs:
**xmlns:soapenv="http://schemas.xmlsoap.org/soap/envelope/"**
**xmlns:ws="http://ws.estoque.com.br/"**

As duas definições são mais do que URLs, elas são namespaces!
xmlns:soapenv="http://schemas.xmlsoap.org/soap/envelope/"
xmlns:ws="http://ws.estoque.com.br/"

###### Um **namespace** é parecido com o **package** do mundo Java e ajuda distinguir elementos e evitar conflitos de nomes. 

O primeiro namespace define os elementos **padrão do SOAP** como Envelope e Body. 

O segundo serve para **usar os elementos do nosso modelo como o item**.

Além disso, o** primeiro namespace** indica que estamos usando **SOAP na versão 1.1** (não muito explícito mas se fosse SOAP 1.2 seria o namespace http://www.w3.org/2003/05/soap-envelope - acreditem!).

Repare também que cada namespace define um **prefixo**. 

O primeiro usa **soapenv**, o segundo **ws**. 

**Os prefixo são úteis para referenciar o namespace dentro da mensagem XML.** Ou seja, cada vez que encontramos ws: na verdade queremos dizer http://ws.estoque.com.br/.

Você deve estar um pouco inseguro com o mundo XML. O mundo XML pode ser complexo mas com tempo e prática as coisas vão se encaixar melhor.

## Para saber mais: Estilos de integração
Você que está fazendo este curso, certamente sabe que é raríssimo um sistema funcionar de forma isolada. Sendo sempre necessário fazer integração com outros sistemas que não foram desenhadas com esse propósito. 

Para resolver esse problema, podemos usar os mais diversos estilos de integração. Dentre eles:
**Troca de arquivos**
**Banco de dados compartilhado**
**RPC**
**Mensageria**

A questão é que cada estilo tem suas vantagens e desvantagens. É por isso que é papel do arquiteto pensar em algumas questões antes de decidir qual estilo aplicar:

- Vamos trocar funcionalidades ou apenas dados?
- Quais dados trocaremos?
- Qual protocolos utilizaremos?
- A comunicação será síncrona ou assíncrona?
- Quais ferramentas/frameworks utilizaremos?
Entre outros …

Nesse treinamento focaremos no estilo **RPC** (**Remote Procedure Call**). 

Objetivo desse estilo de integração é chamar um procedimento remotamente (via rede usamos **HTTP com SOAP**). 

No mundo Java este procedimento é um método.

RPC segue o modelo cliente-server (síncrono) que já testamos neste capítulo. 

O SoapUI foi o cliente e rodamos o server através do JRE!


## Revisão
No capítulo anterior, escrevemos e publicamos o primeiro serviço SOAP. Apesar de ser a forma mais simples possível, já foi o bastante para conhecermos alguns artefatos importantes. 

**Qualquer serviço define um contrato, o WSDL**. 

**Os dados que trafegam entre cliente e servidor são apresentados através das mensagens SOAP. **

Vimos que no nosso serviço temos uma mensagem de ida e uma de volta. 

Publicamos o nosso serviço usando **JAX-WS**. Especificação Java EE que é especialista neste assunto. 

Por fim criamos um cliente do nosso primeiro serviço. Nesse capítulo, vamos ajustar a mensagem e o WSDL.


## Entendendo as operations
Vamos subir o serviço pelo Eclipse e chamar o WSDL pelo navegador.

No SoapUI temos a mensagem SOAP gerada. Repare que no Body temos um elemento com o nome **getItens**:
```xml
<soapenv:Envelope xmlns:soapenv="http://schemas.xmlsoap.org/soap/envelope/" xmlns:ws="http://ws.estoque.caelum.com.br/">
   <soapenv:Header/>
   <soapenv:Body>
      <ws:getItens/>
   </soapenv:Body>
</soapenv:Envelope>
```
###### O elemento possui o mesmo nome que o método na classe Java. 

Como criamos a implementação do serviço primeiro, **foi utilizado a nomenclatura dela para criar o contrato WSDL** que é a base para o SOAP.

Podemos ver esse elemento operation que faz parte do portType definido no WSDL:
```xml
<definitions ...>
    <types>
        <!-- definições dos tipos xsd-->
    </types>
    <message name="getItens">
        <part name="parameters" element="tns:getItens" />
    </message>
    <message name="getItensResponse">
        <part name="parameters" element="tns:getItensResponse" />
    </message>
    <portType name="EstoqueWS">
        <operation name="getItens">
            <input  message="tns:getItens" />
            <output  message="tns:getItensResponse" />
        </operation>
    </portType>
    <!-- bindings e endereços omitidos-->
</definitions>
```
Repare que o elemento **portType** está parecido com uma **interface Java**: declara um nome (**EstoqueWS**) e** define as operações com cada entrada e saída**.

A mensagem SOAP se baseia no input ou output das operações do portType.

## Melhorando o serviço
Nosso serviço é simples, mas podemos (e iremos) melhorar muito. 

Aquele elemento **getItens** faz sentido para quem é desenvolvedor Java. Mas será que é um bom nome para desenvolvedores de outras plataformas como Ruby e Python? Precisamos lembrar que nosso serviço é independente de plataforma.

**Não há uma nomenclatura padrão de serviços SOAP**, por isso devemos deixar nosso contrato o mais expressivo possível. Já que ferramentas o usarão para gerar clientes do nosso serviço.

Observe, por exemplo, a resposta SOAP. Lá temos um elemento return, que novamente aparenta ser um nome não muito expressivo, não acha?

Precisamos alterar a classe **EstoqueWS** para definir nomes melhores, portanto nosso primeiro passo será **escolher um outro nome para o método getItens**. 

Poderíamos então dar um rename e fugir da nomenclatura padrão de Java. Porém dessa forma estariamos prejudicando nosso código Java, abrindo mão das convenções de nomenclatura, por conta do WSDL.

**@WebMethod** e **@WebResult**
Para evitar esse problema, o JAX-WS nos oferece uma alternativa: **a anotação @WebMethod**. 

Com ela, podemos redefinir o nome da operation no WSDL e assim também manipular a mensagem SOAP.

```java
@WebMethod(operationName="todosOsItens")
public List<Item> getItens() {
    System.out.println("Chamando getItens()");
    return dao.todosItens();
}
```
Essa mudança simples faz com que a requisição SOAP tenha agora um elemento com o nome da operação:
```xml
<soapenv:Envelope ...>
   <soapenv:Header/>
   <soapenv:Body>
      <ws:todosOsItens/>
   </soapenv:Body>
</soapenv:Envelope>
```
Segundo passo é melhorar a resposta SOAP, já que aquele return não deve existir. 

Isso acontece porque o JAX-B (que veremos mais a frente) que é o responsável por gerar o XML, não conhece a interface List. 

Portanto, ele substitui por um nome genérico (return).

Repare que na resposta SOAP aparece para cada item um **elemento return**. 

Uma forma fácil de resolver isso é usar a anotação **@WebResult**:
```java
@WebMethod(operationName="todosOsItens")
@WebResult(name="item")
public List<Item> getItens() {
    System.out.println("Chamando getItens()");
    return dao.todosItens();
}
```
A resposta SOAP já melhorou bastante, veja o XML:
```xml
<S:Envelope xmlns:S="http://schemas.xmlsoap.org/soap/envelope/">
   <S:Body>
      <ns2:todosOsItensResponse xmlns:ns2="http://ws.estoque.caelum.com.br/">
         <item>
            <codigo>GAL</codigo>
            <nome>Galaxy Tab</nome>
            <quantidade>3</quantidade>
            <tipo>Tablet</tipo>
         </item>
         <item>
            <codigo>MOX</codigo>
            <nome>Moto X</nome>
            <quantidade>6</quantidade>
            <tipo>Celular</tipo>
         </item>
         <!-- outros itens -->
      </ns2:todosOsItensResponse>
   </S:Body>
</S:Envelope>
```
## Mapeamento com JAX-B
Houve uma mudança significativa do XML SOAP. 

No entanto há mais para melhorar: é um tanto estranho chamar o **@WebResult** de **item** já que estamos trabalhando com uma **lista de itens**, não acha?

Faz mais sentido usar um elemento **itens** que possui vários filhos **item**, algo assim:
```xml
<itens>
         <item>
                  ....
         </item>
         <item>
                  ....
         </item>
</itens>
```
Precisamos resolver isso, mas nosso método **getItens** possui um pequeno problema: ele retorna um List e já vimos que esta interface não é conhecida do JAX-B. Por isso foi preciso usar a anotação **@WebResult** para mapear cada um dos elementos da lista como item no XML. Porém dessa forma, eles ficam órfãos (sem uma tag pai).

Então, para resolvermos esse problema, precisaremos criar uma classe separada para esse propósito. Vamos chamar a classe de **ListaItens** que vai existir para embrulhar a lista original:
```java
public class ListaItens {

    private List<Item> itens;

    public ListaItens(List<Item> itens) {
        this.itens = itens;
    }

    //esse construtor também é necessário
    ListaItens() {
    }    
}
```
Para o JAX-B funcionar corretamente devemos colocar a anotação **@XmlRootElement** e, para não criar um getter e setter, vamos definir o acesso pelo atributo pela anotação **@XmlAccessorType(XmlAccessType.FIELD)**. Repare também que definimos que cada elemento na lista é um item:
```java
@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class ListaItens {

    @XmlElement(name="item")
    private List<Item> itens;

    public ListaItens(List<Item> itens) {
        this.itens = itens;
    }

    //esse construtor também é necessário
    ListaItens() {
    }    
}
```
Pronto, no método do serviço colocaremos como retorno **ListaItens**:
```java
@WebService()
public class EstoqueWS {

    private ItemDao dao = new ItemDao(); 

    @WebMethod(operationName="todosOsItens")
    @WebResult(name="itens")
    public ListaItens getItens() {
        System.out.println("Chamando getItens()");
        return new ListaItens(dao.todosItens()); //criando uma ListaItens
    }

}
```
Tudo pronto para testar de novo! Vamos republicar o serviço e verificar o WSDL. Acesse novamente a URL:
http://localhost:8080/estoquews?wsdl
Vá ao SoapUI e atualize o cliente (aperte F5 no EstoqueWSPortBinding). Abra o request e execute a requisição SOAP. Na resposta deve haver um elemento itens seguido pelos elementos item:
```xml
<S:Envelope xmlns:S="http://schemas.xmlsoap.org/soap/envelope/">
   <S:Body>
      <ns2:todosOsItensResponse xmlns:ns2="http://ws.estoque.caelum.com.br/">
         <itens>
            <item>
               <codigo>SEO</codigo>
               <nome>SEO na Prática</nome>
               <quantidade>4</quantidade>
               <tipo>Livro</tipo>
            </item>

            <!-- outros itens omitidos -->
         </itens>
      </ns2:todosOsItensResponse>
   </S:Body>
</S:Envelope>
```
## O que você aprendeu neste capítulo?
- os métodos Java se tornam operations no WSDL
- as operations fazem parte do portType
- as anotações do JAX-WS servem para personalizar o WSDL
- a especificação JAX-B gera o XML por baixo dos panos

## Mãos a obra: @WebMethod
Como visto no vídeo anote o método **getItens** com a anotação @**WebMethod** para alterar o nome do **operation**. Chame a operation de **todosOsItens**.

Lembrando também, depois da cada alteração é preciso republicar o serviço. Como alteramos o nome da operation vai ter um impacto no WSDL e no SOAP gerando. É preciso atualizar o cliente SoapUI!

###### A anotação @**WebMethod** é opcional, por padrão é utilizado o nome do método para a operation no WSDL. 

Caso precise usar um outro nome no WSDL (e consequentemente no SOAP) podemos configurá-lo através da anotação @WebMethod.

**A anotação também serve para excluir um método do contrato WSDL pois por padrão todos os métodos públicos serão utilizados no WSDL.
@WebMethod(exclude=true)**
 
## Mãos a obra: @WebResult
Agora, use a anotação **@WebResult(name="itens")** no método **getItens** para personalizar a resposta gerada.

Além disso, altere o retorno do método para **ListaItens**. 

A classe **ListaItens** já existe e deve utilizar as **anotações do JAX-B**:


## Mãos a obra: Filtrando resultados
Na classe **EstoqueWS**, no método **getItens**() coloque um parâmetro para filtrar os dados. 

No projeto já tem um classe **Filtro** e uma outra **Filtros** preparadas para receber os dados da requisição SOAP. 

O DAO também já está preparado para a pesquisa usando os dados do filtro. O método **getItens** fica assim:
   ```java
@WebMethod(operationName="todosOsItens")
    @WebResult(name="itens")
    public ListaItens getItens(Filtros filtros) { //cuidado, plural
        System.out.println("Chamando getItens()");
        List<Filtro> lista = filtros.getLista();
        List<Item> itensResultado = dao.todosItens(lista);
        return new ListaItens(itensResultado);
    }
```
Um filtro define um tipo do item (Livro, Celular ou Tablet) e o nome do item. A classe já criada é bem simples:
```java
public class Filtro {

    private TipoItem tipo;
    private String nome;

    //get e sets
```
A classe **filtro** é um pequeno wrapper para embrulhar um filtro e deixar o XML mais expressivo:
```java
@XmlRootElement()
@XmlAccessorType(XmlAccessType.FIELD)
public class Filtros {

    @XmlElement(name="filtro")
    private List<Filtro> filtros;

    //construtores

    public List<Filtro> getLista() {
        return filtros;
    }

}
```
Republique o serviço. Atualize o cliente no SoapUI. Se for preciso criar um novo Request abaixo da operação todosOsItens.

Ao atualizar o cliente e criar um novo request o SoapUI gera o XML seguinte. 

Dentro do elemento **ws:todosOsItens** encontramos agora um novo com o pouco expressivo de **arg0**. 

Esse **arg0** são os filtros e pode ter outros elementos:
```xml
<soapenv:Envelope xmlns:soapenv="http://schemas.xmlsoap.org/soap/envelope/" xmlns:ws="http://ws.estoque.caelum.com.br/">
   <soapenv:Header/>
   <soapenv:Body>
      <ws:todosOsItens>
         <!--Optional:-->
         <arg0>
            <!--Zero or more repetitions:-->
            <filtro>
               <tipo>?</tipo>
               <nome>?</nome>
            </filtro>
         </arg0>
      </ws:todosOsItens>
   </soapenv:Body>
</soapenv:Envelope>
```
Basta colocar um valor no filtro para pesquisar pelo nome e tipo, por exemplo:
```xml
<arg0>
         <filtro>
            <!--Optional:-->
            <tipo>Celular</tipo>
            <!--Optional:-->
            <nome>Moto</nome>
         </filtro>
</arg0>
```
Você também pode repetir o elemento filtro, por exemplo:
```xml
<arg0>
         <filtro>
            <!--Optional:-->
            <tipo>Celular</tipo>
            <!--Optional:-->
            <nome>Moto</nome>
         </filtro>
         <filtro>
            <!--Optional:-->
            <tipo>Celular</tipo>
            <!--Optional:-->
            <nome>IP</nome>
         </filtro>
 </arg0>
```
Agora, o que você acha de um elemento no XML que se chama arg0?

Chamar algum elemento de **arg0** com certeza vai confundir os nossos clientes! No próximo exercício vamos melhorar o nome para deixar o XML mais expressivo sempre pensando na melhor interface (WSDL) possível.
 
## Mãos a obra: Filtrando resultados 2
Na classe **EstoqueWS**, no método **getItens**() coloque** um parâmetro para filtrar os dados**. 

No projeto já tem um classe **Filtro** e uma outra **Filtros** preparadas para receber os dados da requisição SOAP. 
O DAO também já está preparado para a pesquisa usando os dados do filtro. O método **getItens** fica assim:
 ```java
  @WebMethod(operationName="todosOsItens")
    @WebResult(name="itens")
    public ListaItens getItens(Filtros filtros) { //cuidado, plural
        System.out.println("Chamando getItens()");
        List<Filtro> lista = filtros.getLista();
        List<Item> itensResultado = dao.todosItens(lista);
        return new ListaItens(itensResultado);
    }
```
Um filtro define um tipo do item (Livro, Celular ou Tablet) e o nome do item. A classe já criada é bem simples:
```java
public class Filtro {

    private TipoItem tipo;
    private String nome;

    //get e sets
```
A classe **filtro** é um pequeno wrapper para embrulhar um filtro e deixar o XML mais expressivo:
```java
@XmlRootElement()
@XmlAccessorType(XmlAccessType.FIELD)
public class Filtros {

    @XmlElement(name="filtro")
    private List<Filtro> filtros;

    //construtores

    public List<Filtro> getLista() {
        return filtros;
    }

}
```
## Mãos a obra: @WebParam no método getItens
Vamos deixar o WSDL e SOAP mais expressivo. Use a anotação **@WebParam** no método **getItens** que recebe o nome do parâmetro:
  ```java
 @WebMethod(operationName="todosOsItens") 
    @WebResult(name="itens")
    public ListaItens getItens(@WebParam(name="filtros") Filtros filtros) {
        System.out.println("Chamando getItens()");
        List<Filtro> lista = filtros.getLista();
        List<Item> itensResultado = dao.todosItens(lista);
        return new ListaItens(itensResultado);
    }
```
Republique o serviço e atualize o SoapUI. Gere um novo request, **repare que o XML SOAP não possui mais o elemento arg0**:
```xml
<soapenv:Envelope xmlns:soapenv="http://schemas.xmlsoap.org/soap/envelope/" xmlns:ws="http://ws.estoque.caelum.com.br/">
   <soapenv:Header/>
   <soapenv:Body>
      <ws:todosOsItens>
         <!--Optional:-->
         <filtros>
            <!--Zero or more repetitions:-->
            <filtro>
               <tipo>?</tipo>
               <nome>?</nome>
            </filtro>
         </filtros>
      </ws:todosOsItens>
   </soapenv:Body>
</soapenv:Envelope>
```
Como já falamos, sempre coloque-se no posição do seu cliente. Provavelmente ele não tem a classe Java da implementação do serviço, então não sabe dos detalhes da implementação. Mostrando variáveis sem sentido ou abreviadas vão dificultar o desenvolvimento e a integração.
 
## ResponseWrapper
Antes de conhecermos JAX-B, nosso método **getItens** devolvia uma **List<Item>**. 

O problema dessa abordagem é que cada **item** da lista era representado por um elemento **return** que deixava o XML da resposta SOAP pouco expressivo. 

Resolvemos então esse problema usando a classe **ListaItens**. Dessa forma, para que quando realizarmos uma requisição SOAP em busca dos itens, recebemos como resposta algo como:
```xml
<S:Envelope xmlns:S="http://schemas.xmlsoap.org/soap/envelope/">
   <S:Body>
      <ns2:todosOsItensResponse xmlns:ns2="http://ws.estoque.com.br/">
         <itens>
            <item>
               <codigo>MEA</codigo>
               <nome>MEAN</nome>
               <quantidade>5</quantidade>
               <tipo>Livro</tipo>
            </item>
            <!-- outros itens omitidos -->
         </itens>
      </ns2:todosOsItensResponse>
   </S:Body>
</S:Envelope>
```

Podemos ver no XML que há ainda uma outra tag que está envolvendo nossa lista de itens: 

    <ns2:todosOsItensResponse>
Isso ocorre porque nosso SOAP usa **o padrão Wrapped** (embrulhado) que estudaremos mais a frente. 

Por enquanto, basta sabermos que essa TAG é usada para indicar de **qual método veio essa resposta**. 

No nosso caso, do método **todosItens** anotado com **@WebMethod(operationName="todosOsItens")**.

Podemos aproveitar essa tag para envolver nossos itens, em vez de usarmos a classe **ListaItem**. Basta customizarmos o nome da tag usando **@ResponseWrapper** passando no atributo **localName** o novo nome da tag (itens).

Como ficaria o método getItens usando **@ResponseWrapper **?
O primeiro passo é usar a anotação **@ResponseWrapper(localName="itens")** no método **getItens** para redefinir o nome do elemento que embrulha a mensagem:
```xml
@WebMethod(operationName="todosOsItens")
@ResponseWrapper(localName="itens")
@WebResult(name="itens")
public ListaItens getItens() { 

    System.out.println("Chamando getItens()");
    return dao.todosItens();

}
```
Segundo, **não precisamos mais devolver uma instância de ListaItens**. 

Portanto, voltaremos a devolver uma **List<Item>** onde cada item deverá ser representado pela tag <item> usando
```java
@WebResult(name="item"):
@WebMethod(operationName="todosOsItens")
@ResponseWrapper(localName="itens")
@WebResult(name="item")
public List<Item> getItens() { 

    System.out.println("Chamando getItens()");
    return dao.todosItens();

}
```
 
## RequestWrapper
Aquele Wrapper visto no exercício anterior não só existe na resposta como também na requisição! Experimente a anotação **@RequestWrapper(localName="listaItens")** no método **getItens**.

O nosso método já é um belo festival de anotações:

```java
@WebMethod(operationName="todosOsItens")
@ResponseWrapper(localName="itens")
@WebResult(name="item")
@RequestWrapper(localName="listaItens")
public ListaItens getItens(@WebParam(name="filtros") Filtros filtros){

        List<Filtro> lista = filtros.getLista();
        List<Item> result = dao.todosItens(lista);
        return new ListaItens(result);
}
```
E o SOAP gerado:
```xml
<soapenv:Envelope xmlns:soapenv="http://schemas.xmlsoap.org/soap/envelope/" xmlns:ws="http://ws.estoque.caelum.com.br/">
   <soapenv:Header/>
   <soapenv:Body>
      <ws:listaItens />
   </soapenv:Body>
</soapenv:Envelope>
```
 























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
