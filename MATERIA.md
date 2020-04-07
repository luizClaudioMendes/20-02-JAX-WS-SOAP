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
<soapenv:Envelope xmlns:soapenv="http://schemas.xmlsoap.org/soap/envelope/" xmlns:ws="http://ws.estoque.com.br/">
   <soapenv:Header/>
   <soapenv:Body>
      <ws:listaItens />
   </soapenv:Body>
</soapenv:Envelope>
```
 
## Trabalhando com cabeçalhos

## Revisão
No último capítulo vimos como personalizar as mensagens SOAP a partir das anotações do JAX-B e JAX-WS. 

Vimos que os **métodos** do mundo Java** se tornam operations** e os **parâmetros** e **retornos** são **as mensagens no WSDL**. 

Essas operations fazem parte de um elemento chamado **portType**. 

De certa forma, a classe Java é traduzida para XML.


## Nova funcionalidade: Cadastrar itens
Vamos aumentar um pouco as possibilidades do nosso serviço e criar uma nova funcionalidade de cadastrar itens no sistema. 

Chamaremos o método **cadastrarItem** que recebe o Item a cadastrar como parâmetro. 

Na classe **EstoqueWS** adicionaremos:
```java
public Item cadastrarItem(Item item) {
  System.out.println("Cadastrando " + item);
  this.dao.cadastrar(item);
  return item;
}
```
Estamos retornando o mesmo item. Parece que não faz sentido, mas normalmente esse item ganha a ID do banco de dados. E ao retorná-lo estamos informando essa ID. 

Como já fizemos antes, vamos deixar o WSDL mais expressivo usando as anotações **@WebMethod, @WebParam e @WebResult**:

```java
@WebMethod(operationName="CadastrarItem") 
@WebResult(name="item")
public Item cadastrarItem(@WebParam(name="item") Item item) {
  System.out.println("Cadastrando " + item);
  this.dao.cadastrar(item);
  return item;
}
```
Já podemos publicar o serviço e testar o resultado:
http://localhost:8080/estoquews?wsdl

Repare que no elemento portType aparece mais uma operation:
```xml
<portType name="EstoqueWS">
  <operation name="TodosOsItens">
     <input wsam:Action="http://ws.estoque.caelum.com.br/EstoqueWS/TodosOsItensRequest" message="tns:TodosOsItens"/>
     <output wsam:Action="http://ws.estoque.caelum.com.br/EstoqueWS/TodosOsItensResponse" message="tns:TodosOsItensResponse"/>
  </operation>
  <operation name="CadastrarItem">
     <input wsam:Action="http://ws.estoque.caelum.com.br/EstoqueWS/CadastrarItemRequest" message="tns:CadastrarItem"/>
     <output wsam:Action="http://ws.estoque.caelum.com.br/EstoqueWS/CadastrarItemResponse" message="tns:CadastrarItemResponse"/>
  </operation>
</portType>
```

## Trabalhando com cabeçalhos
Para cadastrar um novo item no nosso sistema é preciso se autenticar. Podemos pensar que há uma auditoria automática quando alguém altera um dado no sistema e por isso devemos saber quem está solicitando a alteração. Para nosso exemplo não importa muito como a administração de usuários funciona, mas na web é muito comum que um usuário seja identificado através de um token. 

Um **token** é nada mais do que um **hash gerado para um cliente**. 

No mundo Java Web existe o **JSESSIONID** que representa um token utilizado em aplicações Web. 

O padrão de autenticação e autorização OAuth também usa um token. 

Enfim, o nosso sistema não vai reinventar a roda e também usará um token!

No nosso sistema já temos uma classe preparada para este objetivo, chamada **TokenUsuario**:
```java
public class TokenUsuario {

  private String token;
  private Date dataValidade;

  //get e set
```
Queremos receber o token do usuário na requisição SOAP que cadastra um item, mas será que faz sentido misturar os dados do item e o token do usuário? 

Normalmente não faz e o SOAP já propõe uma forma de separar esses dados. 

Já vimos que existe para tal o elemento **Header**. 

Se usarmos ele, a mensagem SOAP deve ficar parecida com a abaixo:
```xml
<soapenv:Envelope ...>
 <soapenv:Header>
   <tokenUsuario>
       <dataValidade>2015-08-30T00:00:00</dataValidade>
       <token>123131AF!@DF12334a</token>
   </tokenUsuario>
   <soapenv:Header>
   <soapenv:Body>
        <!-- body com o item omitido -->
   </soapenv:Body>
</soapenv:Envelope>
```
###### Para adicionar um elemento no Header, basta criar mais um parâmetro no método cadastrarItem e configurá-lo com a anotação @WebParam. 

A anotação possui um atributo header que indica que o parâmetro deve ser adicionado ao cabeçalho:
```java
@WebMethod(operationName="CadastrarItem") 
@WebResult(name="item")
public Item cadastrarItem(@WebParam(name="tokenUsuario", header=true) TokenUsuario token, @WebParam(name="item") Item item) {
  System.out.println("Cadastrando " + item + ", " + token);//imprimindo o token tbm
  this.dao.cadastrar(item);
  return item;
}
```
Ao alterar a classe **EstoqueWS** e rodar o serviço, não há mudanças no portType e sim no elemento **binding**. 

O binding define detalhes sobre a codificação dos dados e como se monta a mensagem SOAP. 

Nessa seção do WSDL está definido que usaremos o protocolo HTTP por baixo dos panos, entre várias outras configurações como Document e literal. 

No próximo capítulo veremos mais sobre elas. O que importa agora é o input da operation **CadastrarItem**, lá está definido que há um soap:header:
```xml
<!-- seção bindings-->
<operation name="CadastrarItem">
   <soap:operation soapAction=""/>
    <input>
       <soap:body use="literal" parts="parameters"/>
       <soap:header message="tns:CadastrarItem" part="tokenUsuario" use="literal"/>
     </input>
     <output>
       <soap:body use="literal"/>
    </output>
</operation>
```

## Testando o Header
Vamos atualizar o SoapUI e criar um novo request, nele já deve aparecer o Header. 

Abaixo um exemplo do request já com dados preenchidos:
```xml
<soapenv:Envelope xmlns:soapenv="http://schemas.xmlsoap.org/soap/envelope/" xmlns:ws="http://ws.estoque.caelum.com.br/">
   <soapenv:Header>
      <ws:tokenUsuario>
         <token>AAA</token>
         <dataValidade>2015-12-31T00:00:00</dataValidade>
      </ws:tokenUsuario>
   </soapenv:Header>
   <soapenv:Body>
      <ws:CadastrarItem>
         <item>
            <codigo>MEA</codigo>
            <nome>MEAN</nome>
            <tipo>Livro</tipo>
            <quantidade>5</quantidade>
         </item>
      </ws:CadastrarItem>
   </soapenv:Body>
</soapenv:Envelope>
```
Ao submeter, podemos ver no console do Eclipse, o token do usuário.
###### TokenUsuario [token=AAA, dataValidade=Thu Dec 31 00:00:00 BRST 2015]TokenUsuario [token=AAA, dataValidade=Thu Dec 31 00:00:00 BRST 2015]

## Verificando o token
Um vez que recebemos o token do usuário vamos verificar a validade do token. Em ambientes que são puramente baseados em serviços poderia ter um outro serviço com a responsabilidade de administrar usuários. 

Ainda não sabemos como chamar um serviço SOAP a partir do código Java, por isso preparamos no nosso sistema uma classe DAO que recebe o token e verifica a existência e validade dele. 

Vamos pensar que nossa aplicação administra os usuários e os tokens. Portanto, não é preciso chamar um serviço externo, ok?

A classe **TokenDao** possui apenas um método, *ehValido*, que recebe o token do usuário:
```java
public class TokenDao {

  public boolean ehValido(TokenUsuario usuario) {
    return //devolve true ou false
  }
```
No método *cadastrarItem* do serviço chamaremos o método *ehValido*:
 ```java
@WebMethod(operationName="CadastrarItem") 
 @WebResult(name="item")
  public Item cadastrarItem(@WebParam(name="tokenUsuario", header=true) TokenUsuario token, @WebParam(name="item") Item item) {

    System.out.println("Cadastrando " + item + ", " + token);

    //novo
    boolean valido = new TokenDao().ehValido(token); //o que faremos se o token for invalido? 

    this.dao.cadastrar(item);
    return item;
  }
```

A pergunta que não quer calar é: O que faremos se o token for inválido? 

Com certeza, não tem como continuar com a execução. Como falamos antes, devemos saber quem está acessando para cadastrar um item no estoque. Isso parece ser um momento bom para interromper o fluxo comum e jogar uma exceção.

## Trabalhando com exceções
Se alguém tenta cadastrar um item sem ter um token válido, vamos lançar um exceção. 

Chamaremos a exceção de **AutorizacaoException**:
```java
boolean valido = new TokenDao().ehValido(token);

if(!valido) {
  throw new AutorizacaoException("Token invalido");
}
```
Como a exceção não existe ainda, é preciso criá-la. 

O Eclipse ajuda nesse sentido e cria a classe automaticamente estendendo Exception :
```java
public class AutorizacaoException extends Exception {

  //esse numero eh relacionado com a serializacao do java.io mas nao importa nesse contexto
  private static final long serialVersionUID = 1L;

  public AutorizacaoException(String msg) {
    super(msg);
  }

}
```
A **AutorizacaoException** é do tipo **checked** e exige um tratamento explícito, por isso o código na classe **EstoqueWS** para de funcionar. 

Vamos adicionar o tratamento na assinatura do método:
```java
@WebService()
public class EstoqueWS {

  private ItemDao dao = new ItemDao(); 

  @WebMethod(operationName="CadastrarItem") 
  @WebResult(name="item")
  public Item cadastrarItem(
    @WebParam(name="tokenUsuario", header=true) TokenUsuario token, 
    @WebParam(name="item") Item item) throws AutorizacaoException {

  //código omitido
```
Repare o throws **AutorizacaoException**, deixamos explícito que pode acontecer uma AutorizacaoException.

## Fault no WSDL
###### No mundo SOAP não existem exceções e sim Faults. 
Uma exceção no mundo Java é traduzido para um Fault. Ao publicar o serviço podemos ver no WSDL que há uma nova mensagem com o nome da exceção:
```xml
<message name="AutorizacaoException">
  <part name="fault" element="tns:AutorizacaoException"/>
</message>
Essa mensagem é utilizada no elemento portType do WSDL. Além do input e output temos um elemento :
<portType name="EstoqueWS">

  <!-- operacao TodosOsItens omitida -->

  <operation name="CadastrarItem" parameterOrder="parameters tokenUsuario">
    <input message="tns:CadastrarItem"/> 
    <output message="tns:CadastrarItemResponse"/>
    <fault message="tns:AutorizacaoException" name="AutorizacaoException" >
  </operation>
</portType>
```

## Estrutura de um Fault
No WSDL um **SoapFault** é também uma mensagem que é associada no **portType** através do elemento fault. 

Vamos testar o serviço e enviar uma mensagem com token errado para causar uma exceção no lado servidor. 

Vamos colocar no elemento token do Header um valor inválido, por exemplo:
```xml
<soapenv:Envelope xmlns:soapenv="http://schemas.xmlsoap.org/soap/envelope/" xmlns:ws="http://ws.estoque.caelum.com.br/">
   <soapenv:Header>
      <ws:tokenUsuario soapenv:mustUnderstand="1">
         <token>errado</token>
         <dataValidade>2015-12-31T00:00:00</dataValidade>
      </ws:tokenUsuario>
   </soapenv:Header>
  <!-- body omitido -->
</soapenv:Envelope>
Ao submeter recebemos a resposta com o Fault, isto é, dentro do Body tem agora um Fault:
<S:Envelope xmlns:S="http://schemas.xmlsoap.org/soap/envelope/">
   <S:Body>
      <S:Fault xmlns:ns4="http://www.w3.org/2003/05/soap-envelope">
         <faultcode>S:Server</faultcode>
         <faultstring>Token invalido</faultstring>
         <detail>
            <ns2:AutorizacaoException xmlns:ns2="http://ws.estoque.caelum.com.br/">
               <message>Token invalido</message>
            </ns2:AutorizacaoException>
         </detail>
      </S:Fault>
   </S:Body>
</S:Envelope>
```
Um **Fault** possui um **faultcode** que indica se o problema foi do **servidor ou do cliente**, o **faultstring** com uma mensagem mais amigável e um **detail** que é a instância da exceção serializada em XML.

## Personalizando Fault
Como tudo no JAX-WS podemos e devemos personalizar o Fault que tem ainda uma cara de exceção Java, que pode ser algo confuso para outras plataformas. 

A anotação responsável pelo Fault se chama **@WebFault** e deve ser usado no nível da classe. 

Vamos chamar a exceção de **AutorizacaoFault**. Além disso, podemos adicionar um método chamado *getFaultInfo* na nossa classe que será usado pelo JAX-B para definir o conteúdo do elemento detail do Fault:
```java
@WebFault(name="AutorizacaoFault")
public class AutorizacaoException extends Exception {

  private static final long serialVersionUID = 1L;

  public AutorizacaoException(String msg) {
    super(msg);
  }

  public String getFaultInfo() {
    return "Token invalido";
  }
}
```
Deixamos o getter com uma mensagem fixa, mas poderíamos ter algum atributo que define os detalhes do Fault. 

Ao testar o serviço aparece na resposta SOAP o nome AutorizacaoFault com a informação Token inválido:
```xml
<S:Envelope xmlns:S="http://schemas.xmlsoap.org/soap/envelope/">
   <S:Body>
      <S:Fault xmlns:ns4="http://www.w3.org/2003/05/soap-envelope">
         <faultcode>S:Server</faultcode>
         <faultstring>Autorizacao falhou</faultstring>
         <detail>
            <ns2:AutorizacaoFault xmlns:ns2="http://ws.estoque.com.br/">Token invalido</ns2:AutorizacaoFault>
         </detail>
      </S:Fault>
   </S:Body>
</S:Envelope>
```
Ainda está um pouco inseguro com os Faults do mundo SOAP? 

Isso então é uma boa hora de praticar! Nos exercícios vamos consolidar o aprendizado e veremos com mais detalhes os cabeçalhos e Faults. Mãos a obra!

## O que você aprendeu nesse capítulo?
- Cabeçalhos servem para guardar informações dados da aplicação
- O elemento Header vem antes do Body
- A anotação @WebParam serve para definir o Header
- Exceptions são mapeadas para Faults

Através do **Header** e **Body** podemos separar as **meta-informações** dos **dados principais**, o que é muito comum em protocolos de comunicação. 

No Body ficam os dados principais da mensagem SOAP. 

Já no Header colocaremos informações de **autenticação/autorização**, **validade da mensagem**, **tempos mínimo de resposta** ou **dados sobre a transação** entre várias outras possibilidades.

Em ambiente SOAP é bem comum trabalharmos com alguns intermediários entre cliente e server que validam os Headers e até os manipulam. Por exemplo, poderíamos ter um intermediário que verifica os dados de autenticação/autorização antes da mensagem chegar no servidor final. Um outro poderia fazer uma auditoria para logar informações importantes do que está sendo feito. Esses intermediários tem até um nome específico no mundo SOAP: nós o chamamos de Node (nó)

## Tipo de Faults
Quando uma mensagem SOAP está sendo processada e se for encontrado um erro, é preciso comunicar o problema ao cliente.

Como estes podem ser escritos em várias plataformas e linguagens diferentes, deve existir um mecanismo independente de plataforma para comunicação de erros. 

Como vimos, a especificação SOAP define uma maneira padrão e independente de plataforma de descrever o erro dentro da mensagem SOAP usando o elemento Fault.

###### No mundo Java as exceções são mapeadas para Faults. 

O JAX-WS define duas categorias ou tipos de exceções:
**Modeled** (Modelado) - Para mapear uma exceção explicitamente a partir da lógica de negócios no código Java. As definições desse Fault estão no arquivo WSDL, as falhas SOAP são previstas no WSDL.

**Unmodeled** (Não modelado) - Para mapear uma exceção (normalmente do tipo java.lang.**RuntimeException**) que acontecerá em tempo de execução se alguma lógica falha. Neste caso, as exceções Java são representados como falha SOAP genérico.

## Mãos a obra: Autorização pelo Token
No exercício anterior criamos um Header através do parâmetro do método *cadastrarItem*(..). Agora vamos validar o token do usuário. 

Para tal já preparamos um DAO que simula a verificação (tudo em memória) e possui alguns tokens pré-cadastrados.

Agora, dentro do método *cadastrarItem*(..) faça um if para testar a validade. Se o token for inválido, lance uma exceção do tipo AutorizacaoException. 

Por exemplo:
```java
if(!new TokenDao().ehValido(token)) {
    throw new AutorizacaoException("Autorizacao falhou");
}
```
Você será obrigado a fazer um tratamento da exceção. Faça um throws da exceção no método *cadastrarItem*(..).

Salve a classe e republique o serviço. 

Atualize o cliente no SoapUI e gere um novo request. 

Na mensagem SOAP deve aparecer o Header com o elemento **tokenUsuario**. Preencha a mensagem, mas com um token inválido:
```xml
<soapenv:Envelope xmlns:soapenv="http://schemas.xmlsoap.org/soap/envelope/" xmlns:ws="http://ws.estoque.caelum.com.br/">
   <soapenv:Header>
      <ws:tokenUsuario>
         <!--Optional:-->
         <token>Nao existe</token>
         <!--Optional:-->
         <dataValidade>2015-12-31T00:00:00</dataValidade>
      </ws:tokenUsuario>
   </soapenv:Header>
   <soapenv:Body>
      <ws:CadastrarItem>
         <!--Optional:-->
         <item>
            <!--Optional:-->
            <codigo>MEA</codigo>
            <!--Optional:-->
            <nome>MEAN</nome>
            <!--Optional:-->
            <tipo>Livro</tipo>
            <quantidade>4</quantidade>
         </item>
      </ws:CadastrarItem>
   </soapenv:Body>
</soapenv:Envelope>
```
Submetendo a mensagem SOAP você deve receber o Fault! Tente também com um token válido (por exemplo AAA).


## Como personalizar este Fault?
Segue uma vez a implementação do método *cadastrarItem*(..):
```java
@WebMethod(operationName="CadastrarItem") 
public Item cadastrarItem(@WebParam(name="tokenUsuario", header=true) TokenUsuario token, @WebParam(name="item") Item item) throws AutorizacaoException{

    System.out.println("Cadastrando " + item + ", " + token);

    if(!new TokenDao().ehValido(token)) {
        throw new AutorizacaoException("Autorizacao falhou");
    }

    this.dao.cadastrar(item);
    return item;
}
```
Para personalizar o Fault definido no WSDL devemos usar a anotação **@WebFault**. Com ela podemos definir o **nome da mensagem** (os dados do Fault são representados através de uma mensagem no WSDL) e** nome do Fault em si**:
```java
@WebFault(name="AutorizacaoFault", messageName="AutorizacaoFault")
public class AutorizacaoException extends Exception {

    private static final long serialVersionUID = 1L;

    public AutorizacaoException(String msg) {
        super(msg);
    }
}
```
Submetendo uma mensagem SOAP com token inválido você deve receber um Fault parecido com o abaixo:
```xml
<S:Envelope xmlns:S="http://schemas.xmlsoap.org/soap/envelope/">
   <S:Body>
      <S:Fault xmlns:ns4="http://www.w3.org/2003/05/soap-envelope">
         <faultcode>S:Server</faultcode>
         <faultstring>Autorizacao falhou</faultstring>
         <detail>
            <ns2:AutorizacaoFault xmlns:ns2="http://ws.estoque.com.br/">
               <message>Autorizacao falhou</message>
            </ns2:AutorizacaoFault>
         </detail>
      </S:Fault>
   </S:Body>
</S:Envelope>
```
 
 
## Mãos a obra: Usando o FaultInfo da exceção
Cada Fault deve ter no mínimo um < faultcode> e < faultstring> mas vimos no vídeo que existe a possibilidade de definir mais detalhes:
```xml
<S:Fault xmlns:ns4="http://www.w3.org/2003/05/soap-envelope">
 <faultcode>S:Server</faultcode>
 <faultstring>Autorizacao falhou</faultstring>
 <detail>
    <ns2:AutorizacaoFault xmlns:ns2="http://ws.estoque.caelum.com.br/">Token invalido</ns2:AutorizacaoFault>
 </detail>
</S:Fault>
```
Para alterar o conteúdo do elemento < detail> é preciso mexer na exceção. Abre a classe **AutorizacaoException** e crie um novo método* getFaultInfo()*:
```java
@WebFault(name="AutorizacaoFault", messageName="AutorizacaoFault")
public class AutorizacaoException extends Exception {

    private static final long serialVersionUID = 1L;

    public AutorizacaoException(String msg) {
        super(msg);
    }

    //novo
    public String getFaultInfo() {
        return "Token invalido";
    }
}
```
Publique o serviço e faça o teste através do SoapUI! Envie um requisição SOAP com um token inválido. Você deve receber um Fault parecido com o acima.

Os nossos clientes vão agradecer se receberem mais detalhes sobre o problema ocorrido no servidor. Vamos agradar os clientes e personalizar ainda mais o elemento < detail> do Fault, ok?

Através do método *getFaultInfo* podemos devolver um objeto que encapsula esses informações, por exemplo:
```java
public InfoFault getFaultInfo() {
    return new InfoFault("Token invalido" , new Date());
}
```
Essa classe precisa ser criada e possui, além dos construtores, uma anotação do JAX-B:
```java
@XmlAccessorType(XmlAccessType.FIELD)
public class InfoFault {

    private Date dataErro;
    private String mensagem;

    public InfoFault(String mensagem, Date dataErro) {
        this.mensagem = mensagem;
        this.dataErro = dataErro;
    }

    //JAX-B precisa
    InfoFault() {
    }

}
```
Isso causa que o elemento < detail> do Fault possui as informações dos atributos da classe InfoFault. Veja o XML:
```xml
<S:Fault xmlns:ns4="http://www.w3.org/2003/05/soap-envelope">
         <faultcode>S:Server</faultcode>
         <faultstring>Autorizacao falhou</faultstring>
         <detail>
            <ns2:AutorizacaoFault xmlns:ns2="http://ws.estoque.com.br/">
               <dataErro>2015-07-01T17:03:47.594-03:00</dataErro>
               <mensagem>Token invalido</mensagem>
            </ns2:AutorizacaoFault>
         </detail>
</S:Fault>
```
 
## O elemento Fault
Na mensagem SOAP, onde deve aparecer o Fault?

###### O Fault deve aparecer logo abaixo do Body, 
por exemplo:
```xml
<S:Envelope xmlns:S="http://schemas.xmlsoap.org/soap/envelope/">
   <S:Body>
      <S:Fault xmlns:ns4="http://www.w3.org/2003/05/soap-envelope">
         <faultcode>S:Server</faultcode>
         <faultstring>Autorizacao falhou</faultstring>
         <detail>
            <ns2:AutorizacaoFault xmlns:ns2="http://ws.estoque.caelum.com.br/">
               <message>Autorizacao falhou</message>
            </ns2:AutorizacaoFault>
         </detail>
      </S:Fault>
   </S:Body>
</S:Envelope>
```
Segue também uma breve descrição dos** elementos principais de um Fault**:
- < faultcode> - **Server** ou **Client** para indicar onde ocorreu o problema, mas existem outros como **VersionMissmatch**
- < faultstring> - uma **explicação do Fault** legível para humanos
- < detail> - mais informações sobre o Fault, normalmente** específicas da aplicação**
###### O < faultcode> e < faultstring> são obrigatórios.
 
## Mãos a obra: exceções unchecked
 
Continuando falando sobre exceções e Faults. Vamos testar uma exceção do tipo **unchecked** (ou seja unmodeled).

No projeto já preparamos um validador do item (a classe **ItemValidador**). Use este validador no método *cadastrarItem* da classe **EstoqueWS**:
   ```java
@WebMethod(operationName="CadastrarItem") 
    public Item cadastrarItem(@WebParam(name="tokenUsuario", header=true) TokenUsuario token, @WebParam(name="item") Item item) throws AutorizacaoException {

        System.out.println("Cadastrando " + item + ", " + token);

        if(! new TokenDao().ehValido(token)) {
            throw new AutorizacaoException("Autorizacao falhou");
        }

        //novo
        new ItemValidador(item).validate();

        this.dao.cadastrar(item);
        return item;
    }
```
A única linha nova é: **new ItemValidador(item).validate()**. Ao chamar o validador lança uma **ItemValidadorException** caso o item esteja inválido. 

Essa exceção é **unchecked**, ou seja, **não faz parte do WSDL**. Faça o teste, envie uma mensagem SOAP com um token válido mas o item inválido, por exemplo:
```xml
<soapenv:Envelope xmlns:soapenv="http://schemas.xmlsoap.org/soap/envelope/" xmlns:ws="http://ws.estoque.caelum.com.br/">
   <soapenv:Header>
      <ws:tokenUsuario soapenv:mustUnderstand="1">
         <token>AAA</token>
         <dataValidade>2015-12-31T00:00:00</dataValidade>
      </ws:tokenUsuario>
   </soapenv:Header>
   <soapenv:Body>
      <ws:CadastrarItem>
         <!--Optional:-->
         <item>
            <codigo>M</codigo>
            <nome>MEAN</nome>
            <tipo>Livro</tipo>
            <quantidade>5</quantidade>
         </item>
      </ws:CadastrarItem>
   </soapenv:Body>
</soapenv:Envelope>
```
Nessa mensagem SOAP o código está errado pois possui apenas 1 char. Teste agora :)

Repare que recebemos também um Fault como resposta:
```xml
<S:Envelope xmlns:S="http://schemas.xmlsoap.org/soap/envelope/">
   <S:Body>
      <S:Fault xmlns:ns4="http://www.w3.org/2003/05/soap-envelope">
         <faultcode>S:Server</faultcode>
         <faultstring>[Codigo invalido]</faultstring>
      </S:Fault>
   </S:Body>
</S:Envelope>
```
O Fault é mais genérico, não possui um elemento < detail>. 
A exceção não faz parte do WSDL, **nem adianta colocar @WebFault**.

Uma pergunta que poderia surgir: será que não tem como definir, de maneira explícita, que o código de um item deve ter 3 chars? 
Tem! 

E o lugar certo para tal regras é o **XSD**! Assunto que abordaremos mais a frente :)
 
 
## Mãos a obra: Dados obrigatórios
Ao atualizar e criar um novo request no SoapUI, o XML SOAP mostra alguns comentários indicando que os elementos do Item e TokenUsuario são opcionais! Veja só:
```xml
<soapenv:Envelope xmlns:soapenv="http://schemas.xmlsoap.org/soap/envelope/" xmlns:ws="http://ws.estoque.caelum.com.br/">
   <soapenv:Header>
      <ws:tokenUsuario>
         <!--Optional:-->
         <token>?</token>
         <!--Optional:-->
         <dataValidade>?</dataValidade>
      </ws:tokenUsuario>
   </soapenv:Header>
   <soapenv:Body>
      <ws:CadastrarItem>
         <!--Optional:-->
         <item>
            <!--Optional:-->
            <codigo>?</codigo>
            <!--Optional:-->
            <nome>?</nome>
            <!--Optional:-->
            <tipo>?</tipo>
            <quantidade>?</quantidade>
         </item>
      </ws:CadastrarItem>
   </soapenv:Body>
</soapenv:Envelope>
```
Entretanto, para nossa aplicação funcionar, **o cliente deve enviar todos os dados sobre o token e item**. 

Essa confusão com certeza vai atrapalhar os clientes do nosso serviço web! Não podemos sinalizar os dados como opcionais que na verdade são obrigatórios.

Para resolver essa confusão é preciso mexer nas classes **TokenUsuario** e **Item**. 

Por padrão, 
###### qualquer dado do nosso modelo é opcional a não ser quando configurado como obrigatório.

Abra a classe **TokenUsuario** e faça que o** token e data se tornem obrigatórios**. 

Use a anotação **@XmlElement** em cada atributo da classe. 

Além disso, para simplificar, use a anotação **@XmlAccessorType** para definir o acesso aos atributo invés de usar os Getter/Setter:

```java
@XmlAccessorType(XmlAccessType.FIELD)
public class TokenUsuario {

    @XmlElement(required=true)
    private String token;
	
    @XmlElement(required=true)
    private Date dataValidade;

    //JAX-B precisa desse construtor
    TokenUsuario() {
    }

    public TokenUsuario(String token, Date dataValidade) {
        this.token = token;
        this.dataValidade = dataValidade;
    }
    //outros métodos omitidos
}
```
Essas anotações são da especificação JAX-B que é utilizado pelo JAX-WS para gerar e ler o XSD/XML.

Agora faça o mesmo na classe **Item**:

```java
@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement
public class Item {

    @XmlElement(required=true)
    private String codigo;

    @XmlElement(required=true)
    private String nome;

    @XmlElement(required=true)
    private String tipo;

    @XmlElement(required=true)
    private int quantidade;

    //construtores e métodos omitidos
}
```
Republique o serviço web no Eclipse e depois atualize o SoapUI e gere um novo request. O SoapUI ainda mostra um elemento opcional?

Essa mudança não resolveu ainda todo o problema, o SoapUI ainda indica que o **item** em si é opcional que não é correto. 

Vamos resolver esse problema mais para frente, ok?
```xml
<soapenv:Envelope xmlns:soapenv="http://schemas.xmlsoap.org/soap/envelope/" xmlns:ws="http://ws.estoque.caelum.com.br/">
   <soapenv:Header>
      <ws:tokenUsuario>
         <token>?</token>
         <dataValidade>?</dataValidade>
      </ws:tokenUsuario>
   </soapenv:Header>
   <soapenv:Body>
      <ws:CadastrarItem>
         <!--Optional:-->
         <item>
            <codigo>?</codigo>
            <nome>?</nome>
            <tipo>?</tipo>
            <quantidade>?</quantidade>
         </item>
      </ws:CadastrarItem>
   </soapenv:Body>
</soapenv:Envelope>
```

Em geral, já sabemos, **quando se desenvolve um serviço web é fundamental observar como os clientes vão utilizar o serviço**. 

O contrato, ou seja **o WSDL, é muito mais importante do que a implementação** (a classe). 

Os clientes não sabem nada da nossa classe e só enxergam o WSDL. 

Todas as más e boas práticas no contrato vão se espalhar nos clientes!

O desenvolvedor mais experiente já ouviu falar da frase: **Program to Interface, not an Implementation**. 

Isso também vale, ou vale ainda mais para serviços web! É preciso verificar com muito detalhe o que estamos publicando pois isso o cliente vai enxergar. 

A nossa classe **EstoqueWS** é apenas a implementação que atende as requisições, nada mais.

Nos próximos capítulos conversaremos mais sobre essa questão fundamental de design de um serviço web .
 
 
## (Desafio) XmlAdapter
Todas as mensagens SOAP enviadas para o serviço de cadastro de ítens, agora precisam possuir o Token para autenticação. Esse token, que é enviado no Header da mensagem, possui um código além de uma data de validade. Por exemplo:
```xml
<soapenv:Header>
      <ws:tokenUsuario soapenv:mustUnderstand="1">
         <token>AAA</token>
         <dataValidade>2015-12-31T00:00:00</dataValidade>
      </ws:tokenUsuario>
   </soapenv:Header>
```
No entanto, estamos enviando a data no formato UTC que é pouco amigável para humanos. Como podemos melhorar este formato, fazendo com que o JAX-B consiga entender uma data no estilo dd/MM/yyyy ?
```xml
<soapenv:Header>
      <ws:tokenUsuario soapenv:mustUnderstand="1">
         <token>AAA</token>
         <dataValidade>31/12/2015</dataValidade>
      </ws:tokenUsuario>
   </soapenv:Header>
```
Precisamos ensinar ao JAX-B que quando ele encontrar o **valor do atributo dataValidade no formato dd/MM/yyyy, este valor deve ser convertido em um date (unmarshal).**

Ou seja, vamos criar uma classe que fará essa adaptação para nós.
```java
public class DateAdapter {

    private String pattern = "dd/MM/yyyy";

    public Date unmarshal(String dateString) throws Exception {
        return new SimpleDateFormat(pattern).parse(dateString);
    }

}
```
De forma inversa, devemos **ensinar o JAX-B a converter um Date em uma String do tipo dd/MM/yyyy (marshal)**.

```java
public String marshal(Date date) throws Exception {
   return new SimpleDateFormat(pattern).format(date);
}
```
Nossa classe DateAdapter deverá ficar algo como:
public class DateAdapter {

```java
   private String pattern = "dd/MM/yyyy";

   public Date unmarshal(String dateString) throws Exception {
      return new SimpleDateFormat(pattern).parse(dateString);
   }

   public String marshal(Date date) throws Exception {
      return new SimpleDateFormat(pattern).format(date);
   }
}
```
Devemos agora dizer na nossa classe **TokenUsuario** que gostariamos de usar o **Adapter** que criamos para o **atributo dataValidade**. 

Para isso, usaremos a anotação @**XmlJavaTypeAdapter**:
```java
@XmlAccessorType(XmlAccessType.FIELD)
public class TokenUsuario {

   @XmlElement(required=true)
   private String token;

   @XmlJavaTypeAdapter(DateAdapter.class)
   @XmlElement(required=true)
   private Date dataValidade;

   // código omitido
```
Para finalizar, precisamos dizer que essa classe **é um adapter do JAX-B**. 

Para isso, iremos** extender a classe abstrata XmlAdapter**.

Como ficará a classe DateAdapter?

```java
public class DateAdapter extends XmlAdapter<String, Date> {

    private String pattern = "MM/dd/yyyy";

    public String marshal(Date date) throws Exception {
        return new SimpleDateFormat(pattern).format(date);
    }

    public Date unmarshal(String dateString) throws Exception {
        System.out.println(dateString);
        return new SimpleDateFormat(pattern).parse(dateString);
    }
}
```
Esse foi um desafio interessante. Se você já tem uma certa experiência com Java, provavelmente já percebeu que o que fizemos é bem parecido com os conversores do JSF ou do SpringMVC. É bem provável que você use bastante esse tipo de adaptação em projetos reais que envolvam outros tipos.
Te vejo no próximo capítulo (:


## WSDL abstrato e concreto
## Revisão
No último capítulo vimos como trabalhar com cabeçalhos e exceções na mensagem SOAP. 

Personalizamos ambos usando as anotações disponíveis no JAX-WS. 

Para criar um cabeçalho da mensagem SOAP basta adicionar um parâmetro no método e declará-lo com **@WebParam(header=true)**. 

Vimos que os cabeçalhos são úteis para declarar meta informações como auditoria, dados da transação e autenticação e autorização. 

Muitas vezes os dados nos cabeçalhos são processados pelos intermediários entre cliente e servidor. Pode ter vários desses intermediários ou SOAP Nodes que realmente usam esses cabeçalhos.

Também vimos como o SOAP trabalha com exceções. Para ser correto os SOAP chama as exceções do mundo Java de Fault. Então é preciso traduzir as exceções para os Faults do mundo SOAP. As exceções checked automaticamente fazem parte do contrato, ou seja, fazem parte do WSDL. As exceções unchecked serão traduzidos para um Fault padrão.

Neste capítulo veremos mais detalhes sobre o contrato e vamos falar sobre as seções **types**, **message** e **portType** do WSDL.

## Definição dos tipos
Já falamos bastante e sobre o WSDL, vamos dar uma olhada com mais detalhes nas seções do WSDL. 

A primeira seção são os **tipos usados no contrato (< types>)**. 

São aqueles definições do XSD. Este define como, por exemplo, se compor um item ou como se define aquele token do usuário. 

O XSD define também as regras de validação e é bastante rico nesse sentido. Esse arquivo até pode ser utilizado separadamente para validar um item ou o payload (os dados principais) da mensagem SOAP! 

Exemplo do item no XSD:
```xml
<xs:simpleType name="tipoItem">
    <xs:restriction base="xs:string">
        <xs:enumeration value="Livro"></xs:enumeration>
        <xs:enumeration value="Celular"></xs:enumeration>
        <xs:enumeration value="Tablet"></xs:enumeration>
    </xs:restriction>
</xs:simpleType>
```
Podemos dizer, que tudo que está trafegando dentro de uma mensagem SOAP deve estar declarado de alguma forma no XSD. 

Em geral, qualquer serviço expõe um modelo, e no mundo SOAP este modelo está definido o XSD!


## Mensagens no WSDL
A segunda parte do WSDL são as mensagens. 

As mensagens se baseiam no XSD e cada uma representa uma entrada ou saída do serviço. 

Repare o elemento CadastrarItem:
```xml
<message name="CadastrarItem">
    <part name="parameters" element="tns:CadastrarItem"/>
    <part name="tokenUsuario" element="tns:tokenUsuario"/>
</message>
```
Ele possui duas partes, a primeira é o que vem no corpo da mensagem (no Body), a segunda parte é do cabeçalho (Header). Repare também que nosso Fault faz parte de uma mensagem:
```xml
<message name="AutorizacaoFault">
    <part name="fault" element="tns:AutorizacaoFault"/>
</message>
```
Isso faz sentido já que o Fault também é uma saída.


## A interface: o elemento PortType
Logo após as mensagem, encontramos a seção portType que associa as mensagem a uma operação. 

Repare a operação **TodosOsItens**, ela possui uma entrada e saída, cada uma representada por uma mensagem:
```xml
<operation name="TodosOsItens">
   <input wsam:Action="http://ws.estoque.com.br/EstoqueWS/TodosOsItensRequest" message="tns:TodosOsItens"/>
  <output wsam:Action="http://ws.estoque.com.br/EstoqueWS/TodosOsItensResponse" message="tns:TodosOsItensResponse"/>
</operation>
```
###### O que importa aqui é o atributo message. 

Nele temos uma referência a mensagem, por exemplo **tns:TodosOsItens**.

O atributo **wsam:Action** é relacionado com o **WS-Addressing** que pode ser útil para chamadas assíncronas, quando queremos devolver a mensagem de resposta para algum outro endereço. O JAX-WS define uma anotação **@Action** para manipular estes valores.

## WSDL abstrato e concreto
Até agora, nesses elementos do WSDL **não definimos o protocolo concreto a ser utilizado**. Em nenhum momento está escrito que realmente queremos usar o SOAP! Também não tem nenhuma informação sobre o endereço de serviço. Essas definições mais concretas vem na segunda parte do WSDL.

De certa forma o contrato é dividido em duas partes: 
- A **primeira parte** que já vimos, com as operações, mensagens e tipos que chamamos de **WSDL abstrato**. 
- A **segunda parte** que terá **definições sobre o protocolo, endereço e codificação das mensagens** chamamos de **WSDL concreto.**

## Visualizando o WSDL
Para simplificar vamos visualizar o WSDL no Eclipse, mas antes iremos gerar o WSDL a partir da classe.

1- Entre na pasta do projeto e execute:
**wsgen -wsdl -inlineSchemas -cp bin br.com.estoque.ws.EstoqueWS**
explicação:
wsgen [gera um wsdl] [gera no mesmo arquivo os schemas] [classpath - informa onde estao o .class da classe do WS] [pasta onde esta o .class] [nome completo da classe] 
###### wsgen [gera um wsdl] [gera no mesmo arquivo os schemas] [classpath - informa onde estao o .class da classe do WS] [pasta onde esta o .class] [nome completo da classe] 
###### para mais informações, digite somente wsgen e aperte ‘enter’.para mais informações, digite somente wsgen e aperte ‘enter’.

Repare que no comando usamos a configuração **inlineSchemas** para criar um arquivo apenas com **XSD** E **WSDL**. 

2- Agora abra o arquivo gerado no Eclipse (JEE)  que possui um editor dedicado ao WSDL:

Podemos ver no lado esquerdo a parte concreta do WSDL e no lado direito a parte abstrata. Entre as duas parte tem uma ligação (binding). 

Repare que a parte abstrata é apresentado como se fosse uma interface Java (usa-se o mesmo símbolo). Claro que não é Java e sim XML, mas trata-se do contrato do serviço.

O editor possui algumas funções para editar e refatorar os dados do serviço, no entanto devemos ter em mente que o WSDL publicado pelo serviço não é criado ao vivo, na hora de rodar o serviço.

No próximo capítulo vamos focar no WSDL concreto mas agora é a hora dos exercícios.


## O que você aprendeu neste capítulo?
- existe uma parte abstrata e concreto no WSDL
- a parte abstrata é parecido com uma interface
- a parte concreta é para definir o protocolo e endereço
- a parte abstrata define os tipos, mensagens e operações
- a interface no WSDL se chama portType
- podemos usar wsgen para gerar o arquivo WSDL
 
Qual é o papel do XSD?
Vimos que dentro do WSDL tem um elemento < type> que contém algo que se chama de XSD.

Qual é o papel do XSD na definição do serviço?
O XSD ou XML Schema ou apenas Schema descreve a estrutura de um documento XML. O XSD define como se compor uma mensagem SOAP, o que pode aparecer no XML, quantas vezes, quais tipos, nomenclatura etc. 

Segue um exemplo do token do usuário no XSD:
     

    <xs:complexType name="tokenUsuario">
            <xs:sequence>
              <xs:element name="token" type="xs:string"></xs:element>
              <xs:element name="dataValidade" type="xs:dateTime"></xs:element>
            </xs:sequence>
          </xs:complexType>
		  
Repare que o elemento **token** é uma **string** e o elemento **dataValidade** é um **dateTime**. Ambos elementos definem uma sequência, o tokenUsuario.

O interessante é que o XSD também é um XML :)

O que as mensagens no WSDL representam?
O elemento < **message**> descreve os dados a serem trocados entre cliente e servidor. Ou seja, cada mensagem representa uma entrada ou saída.
Dentro de um elemento < **message**> vem os **part** que associam um tipo concreto do XSD. 

Por exemplo:


    <message name="AutorizacaoFault">
        <part name="fault" element="tns:AutorizacaoFault"/>
    </message>
	
Quais elementos fazem parte do WSDL?
Os elementos que definem o WSDL abstrato são: < types>, < message> e < portType>. 
Os elementos que definem o WSDL concreto são: < binding> e < service>.

###### Concreto significa que há informações sobre o encoding (veremos no próximo capítulo), sobre o protocolo e o endereço do serviço.


Baseado no conteúdo desse capítulo, qual é a responsabilidade do elemento < portType>?
**O < portType> é parecido com uma interface Java** e define as operações com entrada e saída.
Veja o exemplo:


    <portType name="EstoqueWS">
      <operation name="TodosOsItens">
        <input  message="tns:TodosOsItens"/>
        <output message="tns:TodosOsItensResponse"/>
      </operation>
    </portType>
Um < portType> pode ter várias operations e para ser correto, nem sempre uma operação precisa ter entrada E saída. Por exemplo o < portType> abaixo é válido:


    <portType>
      <operation name="CadastrarItem" parameterOrder="parameters tokenUsuario">
         <input wsam:Action="CadastrarItem" message="tns:CadastrarItem"/>
      </operation>
    </portType>
Nesse caso o serviço SOAP vai ter apenas uma mensagem de ida, sem retorno. O retorno será apenas no nível do protocolo HTTP, sem ter uma mensagem SOAP.
 
## Mãos a obra: Gerando o WSDL
Vamos gerar o arquivo WSDL a partir da nossa implementação. Para este exercício você precisa ter o JDK instalado.
###### 1) Abra um terminal e vá para a pasta do seu projeto, algo: cd C:\workspace\estoquews
###### 2) Execute o comando wsgen:
**wsgen -wsdl -cp bin br.com.estoque.ws.EstoqueWS**
O **-wsdl** é para gerar o arquivo WSDL e o **-cp** para definir o local das classes compiladas.
###### 3) Atualize o projeto estoquews no Eclipse e abra o arquivo pelo Eclipse (j2ee). 
Repare que temos o elemento service no lado esquerdo e o portType no lado direito. O elemento binding é a ligação entre os dois.
###### 4) Também teste a opção -inlineSchemas do comando wsgen:
**wsgen -wsdl -inlineSchemas -cp bin br.com.caelum.estoque.ws.EstoqueWS**
Através do editor WSDL no Eclipse podemos alterar os elementos, adicionar mais documentação, criar novos elementos como service ou binding entre várias outras possibilidades. 

No entanto, faz sentido alterar o WSDL que é gerado a partir de uma classe?


## (Opcional) Gerando XML com JAX-B
Com JAX-B podemos facilmente ler e escrever um XML. Você pode testar isso com o nosso Item. A única configuração obrigatória é a anotação **@XmlRootElement:**


    @XmlRootElement
    @XmlAccessorType(XmlAccessType.FIELD) //essa anotação já existia
    public class Item {
       //resto da classe omitido

A partir daí pode escrever (marshal) o XML:


    public class TesteItemParaXML {
    
        public static void main(String[] args) throws JAXBException {
            Item item = new Item.Builder().comCodigo("MEA").comNome("MEAN").comQuantidade(4).comTipo("Livro").build();
    
            JAXBContext context = JAXBContext.newInstance(Item.class);
            Marshaller marshaller = context.createMarshaller();
            marshaller.marshal(item, new File("item.xml")); //ou marshaller.marshal(item, System.out);        
        }
    
    }
###### Para ler um XML basta usar um Unmarshaller.

Ainda não foi suficiente sobre o JAX-B? Temos um artigo no blog da Caelum que mostra como gerar as classes a partir do XSD:
http://blog.caelum.com.br/jaxb-xml-e-java-de-maos-dadas/
Boa leitura :)
> ## JAXB – XML e Java de mãos dadas
Você já participou de um projeto que precisou ler um arquivo de configuração em xml? Já precisou consumir um xml e transformá-lo em objeto? O que você usou? Quem já trabalhou com xml sabe da dificuldade que podemos encontrar pelo caminho, e é esse tipo de dificuldade que a especificação **Java Architecture for XML Binding** ou simplesmente **JAXB** tenta resolver.

> Imagine a seguinte situação: Precisarmos enviar os dados contidos em um objeto para um outro servidor. Temos muitas opções para fazer o envio, como por exemplo colocar essas informações em um arquivo de texto seguindo uma máscara pré-definida. Porém apenas as aplicações que conhecessem essa máscara entenderiam os dados, e perdemos portabilidade. 
###### Usando xml a situação já é outra: qualquer aplicação, independende de linguagem, entenderá os dados contidos no arquivo xml.

> Antes de falarmos sobre o JAXB vamos primeiro conferir alguns conceitos:

> ## XML
**XML** é uma linguagem de marcação que **serve para guardar dados de uma forma estruturada**. Essa estrutura é definida pelo próprio usuário ou por um schema. Um xml é um arquivo de texto puro, portanto independente de plataforma, por isso é muito utilizado para transmitir dados entre diferentes aplicações e sistemas. 
Exemplo:
carro.xml

    <?xml version="1.0" encoding="UTF-8"?>
    <carro>
      <nome>Fusca</nome>
      <portas>2</portas>
      <motoristas>
        <motorista>
          <nome>Guilherme</nome>
        </motorista>
        <motorista>
          <nome>Leonardo</nome>
        </motorista>
      </motoristas>
    </carro>

> ## XSD
**XSD** é o **schema** citado na seção anterior, ele **define quais são as regras que a estrutura do xml** deve seguir, possibilitando a validação desse xml. 

Exemplo:

    <?xml version="1.0" encoding="UTF-8"?>
    <xsd:schema xmlns:xsd="http://www.w3.org/2001/XMLSchema">
      <xsd:element name="carro" type="Carro" />
      <xsd:complexType name="Carro">
        <xsd:sequence>
          <xsd:element name="nome" type="xsd:string" minOccurs="1"
            maxOccurs="1" nillable="false"/>
          <xsd:element name="portas" type="xsd:int" minOccurs="1"
            maxOccurs="1" nillable="false"/>
          <xsd:element name="motoristas" type="Motorista" minOccurs="0"
            maxOccurs="unbounded"/>
        </xsd:sequence>
      </xsd:complexType>
      <xsd:complexType name="Motorista">
        <xsd:sequence>
          <xsd:element name="nome" minOccurs="1" maxOccurs="1"
            type="xsd:string" nillable="false"/>
        </xsd:sequence>
      </xsd:complexType>
    </xsd:schema>
> O primeiro ponto da especificação apresenta uma ferramenta chamada **Binding Compiler**, cuja função é** transformar um xsd em um conjunto de classes que tenham uma estrutura compatível com a estrutura do xml que esse xsd define**.

> No XSD de exemplo definimos a seguinte estrutura: Um elemento carro deve ter um elemento nome e um elemento motoristas (do tipo Motorista), seguindo essa ordem, primeiro nome e depois motoristas. Depois definimos o tipo Motorista que deve conter apenas um nome.

> O **Binding Compiler** é independente da implementação do JAXB, ou seja, quem define como ele será executado é quem implementa a especificação, porém a maioria e inclusive a própria RI(**Reference Implementation**) cria um comando que pode ser chamado pela linha de comando do Sistema Operacional, o **xjc**. 
Por exemplo no Linux:

> ###### xjc carro.xsd -d src -p br.com.caelum

> Se você já está usando o **Java 6**, o **JAXB já vêm junto com o JDK**.

> Com esse comando o Binding Compiler gera três classes: **Carro.java**, **Motorista.java** e a **ObjectFactory.java**. As classes Carro e Motorista seguem a estrutura do xsd.

> ## Gerando e Lendo XML
A segunda parte da especificação define **o que temos que fazer para transformar objetos em xml e vice-versa**. A API do JAXB é quem se responsabiliza por essas transformações.

> ## Transformando objetos em xml
O processo de transformar um objeto em xml é chamado de **Marshal**. Com o JAXB para transformar um objeto em xml precisamos de um **JAXBContext**, esse context é quem fornecerá o **Marshaller**. 

> O Marshaller é quem finalmente transforma um objeto (**JAXBElement**) em xml. O **JAXBElement** contém o objeto de verdade a ser serializado e algumas propriedades do xml. É aqui que entra a importância do ObjectFactory criado pelo Binding Compiler, ele é responsável por criar uma instância do JAXBElement apropriada para o tipo de objeto a ser serializado.

    JAXBContext context = JAXBContext.newInstance("br.com.caelum");
    Marshaller marshaller = context.createMarshaller();
    JAXBElement<Carro> element = new ObjectFactory().createCarro(carro);
    marshaller.marshal(element, System.out);
	
> ## Parseando xml em objetos java
Para fazer o caminho contrário, ou seja popular um objeto java com dados de um xml também precisamos de um **JAXBContext**, porém agora temos que pegar um **Unmarshaller**. 

> ###### O Unmarshaller recebe um arquivo xml e devolve um JAXBElement contendo um objeto populado.



    JAXBContext context = JAXBContext.newInstance("br.com.caelum");
    Unmarshaller unmarshaller = context.createUnmarshaller();
    JAXBElement<Carro> element = (JAXBElement<Carro>) unmarshaller.unmarshal(new File("resources/carro.xml"));
    Carro carro = element.getValue();
	
	
> ## Conclusão
###### O JAXB facilita muito a vida dos programadores java, fazendo o consumo e criação de xml menos trabalhosos. Essa API também fornece outros recursos como, validação, geração de schema (a partir de classes java, cria um xsd), opções para trabalhar com Namespace e etc. Comente nesse post outras oções do JAXB e outras bibliotecas que você usa no seu dia-a-dia.O JAXB facilita muito a vida dos programadores java, fazendo o consumo e criação de xml menos trabalhosos. Essa API também fornece outros recursos como, validação, geração de schema (a partir de classes java, cria um xsd), opções para trabalhar com Namespace e etc. Comente nesse post outras oções do JAXB e outras bibliotecas que você usa no seu dia-a-dia.

## (Para saber mais) Serviços com @Oneway
Como já vimos, ao criar um serviço SOAP estamos seguindo o padrão de requisição e resposta. Ou seja, o serviço recebe a requisição e o cliente aguarda pacientemente o seu processamento para receber uma resposta.

O problema é que em alguns casos não precisamos receber nenhuma resposta do serviço, **apenas queremos enviar alguma informação** para um serviço de auditoria ou pedir um relatório para um gerador de PDF ou fazer alguma notificação por e-mail.

Nesses casos, esperar pelo processamento da requisição torna-se inviável (pelo fato de que outros serviços externos, também estão envolvidos) e desnecessário.

Neste caso, o que queremos é criar um serviço de "mão única". 

Deixando claro aos clientes que esse serviço não terá resposta e que ele não precisará esperar pelo processamento da requisição.

###### Fazemos isso anotando o método (de retorno void) com @Oneway.

Faça um teste e verifique o que mudou no WSDL após inserir essa anotação.

Vamos usar por exemplo o serviço abaixo:
```java
@WebService
public class RelatorioService {

    @WebMethod(operationName="GerarRelatorio")
    public void gerarRelatorio() { 
        // código omitido
    }
}
```
Como já vimos, as mensagens usadas no serviço são declaradas no WSDL pela seção < message>. 

E por padrão, há no mínimo duas mensagens: uma para requisição e outra para resposta.


    <message name="GerarRelatorio">
        <part name="parameters" element="tns:GerarRelatorio"/>
    </message>
    <message name="GerarRelatorioResponse">
        <part name="parameters" element="tns:GerarRelatorioResponse"/>
    </message>
E na seção < operation>, dizemos quais mensagens serão usadas e se elas são de entrada e saída.


    <operation name="GerarRelatorio">
        <input wsam:Action="http://ws.caelum.com.br/RelatorioService/GerarRelatorioRequest" message="tns:GerarRelatorio"/>
        <output wsam:Action="http://ws.caelum.com.br/RelatorioService/GerarRelatorioResponse" message="tns:GerarRelatorioResponse"/>
    </operation>
Ao anotarmos o método com **@Oneway** não teremos mais uma mensagem de saída, já que o serviço deixa de ter resposta:
```java
@Oneway
@WebMethod(operationName="GerarRelatorio")
public void gerarRelatorio() { 
    // código omitido
}
```
```xml
<message name="GerarRelatorio">
    <part name="parameters" element="tns:GerarRelatorio"/>
</message>
<portType name="RelatorioService">
    <operation name="GerarRelatorio">
        <input wsam:Action="http://ws.caelum.com.br/RelatorioService/GerarRelatorio" message="tns:GerarRelatorio"/>
    </operation>
</portType>
```

## Entendendo os estilos Document e RPC
## Revisão
Vimos no capítulo anterior que o WSDL está dividido em duas partes, uma abstrata, outra concreta. 

A **abstrata** é parecida com uma **interface, define os tipos, mensagens e operações** que se compõem no elemento **portType**. 

A parte **concreta** é para **definir o protocolo e endereço** que é o grande foco desse capítulo.

A parte concreta é importante para o servidor subir o serviço corretamente pois ele precisa saber o protocolo, encoding etc. Mas para fazer a implementação do serviço basta a parte abstrata.

## O elemento binding
Vamos abrir o WSDL no Eclipse para visualizar os elementos. 

Repare da ligação entre as duas partes do WSDL. 

###### Não por acaso esse elemento se chama de binding pois ele referencia o < portType>.

Vamos mudar a visualização para ver o XML. 

Logo após do elemento < binding> podemos ver uma configuração importante:

###### < soap:binding transport="http://schemas.xmlsoap.org/soap/http" style="document"/>
**Aqui temos a configuração que realmente usamos SOAP com HTTP**, pois isso fica declarado na URI:

http://schemas.xmlsoap.org/soap/http

O protocolo HTTP é utilizado por baixo dos panos como um protocolo de transporte (/soap/http). Isso parece estranho pois deve ser o padrão usar HTTP quando falamos de um Webservice, porém o SOAP não depende do HTTP e poderia ser transportado através de outros protocolos.

O outro atributo nessa mesma linha define o estilo da mensagem. Aqui o estilo se chama de **Document** mas existe também o RPC.

## RPC e Document
Serviços web podem ser utilizados de maneira diferente. No nosso caso publicamos o serviço para o cliente chamar alguns métodos remotamente. O cliente envia uma requisição SOAP para executar o método ou procedimento no servidor. Para atender essa forma de chamada foi criado o estilo **RPC** que significa **Remote Procedure Call** (Chamada remota de um procedimento) um estilo de integração muito antigo que foi criado muito antes do mundo SOAP. 

Para usar RPC com SOAP devemos **enviar primeiro o nome do método ou procedimento e, logo abaixo, os parâmetros**. 
Algo assim:


    <soapenv:Envelope ...>
       <soapenv:Body>
          <ws:CadastrarItem>
             <item>
                <codigo>MEA</codigo>
                <nome>MEAN</nome>
                <tipo>Livro</tipo>
                <quantidade>5</quantidade>
             </item>
          </ws:CadastrarItem>
       </soapenv:Body>
    </soapenv:Envelope>

Você pode testar o estilo RPC, basta anotar a classe **EstoqueWS** com a anotação **@SOAPBinding**:
```java
@WebService
@SOAPBinding(style=Style.RPC)
public class EstoqueWS {
```
Para ver a diferença é preciso republicar o serviço. 

O WSDL mudou um pouco já que agora estamos usando o estilo RPC:
```xml
<soap:binding transport="http://schemas.xmlsoap.org/soap/http" style="rpc"/>
```
Também é preciso atualizar o SoapUI e gerar o novo request. 

Logo abaixo do elemento **Body** fica um **elemento com o nome do método/operation**:
```xml
<soapenv:Envelope xmlns:soapenv="http://schemas.xmlsoap.org/soap/envelope/" xmlns:ws="http://ws.estoque.caelum.com.br/">
   <soapenv:Body>
      <ws:TodosOsItens>
         <filtros>
            <!--Zero or more repetitions:-->
            <filtro>
               <!--Optional:-->
               <nome>?</nome>
               <!--Optional:-->
               <tipo>?</tipo>
            </filtro>
         </filtros>
      </ws:TodosOsItens>
   </soapenv:Body>
</soapenv:Envelope>
```
Isso parece muito familiar, não? Repare que isso é a mesma coisa que fizemos com o estilo Document! Então para que existe o estilo Document?

## O estilo Document
Novamente, existem formas diferentes de se comunicar no mundo de serviços web. Por exemplo: quando uma loja recebe uma compra de um produto é gerado um pedido. Imagine que a partir desse pedido devemos notificar um sistema de notas fiscais. Queremos apenas entregar o pedido e o que esse sistema de notas fiscais realmente fará com esse pedido não interessa para o loja. Ou seja, não estamos interessados em chamar algum método ou procedimento do outro sistema. Apenas queremos notificar e entregar o pedido. Nesse caso faz sentido enviar apenas os dados do pedido na mensagem SOAP, por exemplo:
```xml
<soapenv:Envelope xmlns:soapenv="http://schemas.xmlsoap.org/soap/envelope/" xmlns:ws="http://ws.estoque.caelum.com.br/">
   <soapenv:Body>
            <pedido>
               <numero>123</numero>
               <data>22/07/2015</data>
               <!-- outras infos omitidas -->
            </pedido>
   </soapenv:Body>
</soapenv:Envelope>
```
A mensagem SOAP representa apenas um documento! Mas porque estávamos usando Document invés de RPC já que estamos chamando um método?

## Problemas do RPC
http://mangstacular.blogspot.com.br/2011/05/wsdl-soap-bindings-confusion-rpc-vs.html
> ## WSDL SOAP bindings confusion - RPC vs document
What is the difference between **RPC** and **document** styles in SOAP web services?

> I was asked this question at a job interview and was embarrassed to discover that I didn't know the answer (considering that I listed "SOAP web services" on my resume). I've heard the terms before, but couldn't remember what they meant. The main difference lies in what the body of the SOAP message looks like.

> ## RPC vs document styles

> The body of an **RPC** (remote procedure call) style SOAP message is constructed in a specific way, which is defined in the **SOAP standard**. 
###### It is built around the assumption that you want to call the web service just like you would call a normal function or method that is part of your application code. 
The message body contains an XML element for each "parameter" of the method. These parameter elements are wrapped in an XML element which contains the name of the method that is being called. The response returns a single value (encoded in XML), just like a programmatic method. The WSDL code for a RPC-style web service is less complex than that of a document-style web service, but this isn't a big deal since WSDLs aren't meant to be handled by humans.

> A RPC-style request:
```xml
<soap:envelope>
  <soap:body>
    <multiply>    <!-- web method name -->
      <a>2.0</a>  <!-- first parameter -->
      <b>7</b>    <!-- second parameter -->
    </multiply>
  </soap:body>
</soap:envelope>
```

> A document style web service, on the other hand, contains no restrictions for how the SOAP body must be constructed. It allows you to include whatever XML data you want and also to include a schema for this XML. This means that the client's and server's application code must do the marshalling and unmarshalling work. This contrasts with RPC in which the marshalling/unmarshalling process is part of the standard, so presumably should be handled by whatever SOAP library you are using. The WSDL code for a document-style web service is much more complex than that of a RPC-style web service, but this isn't a big deal since WSDLs aren't meant to be handled by humans.

> A document-style request:
```xml
<soap:envelope>
  <soap:body>
    <!-- arbitrary XML -->
    <movies xmlns="http://www.myfavoritemovies.com">
      <movie>
        <title>2001: A Space Odyssey</title>
        <released>1968</released>
      </movie>
      <movie>
        <title>Donnie Darko</title>
        <released>2001</released>
      </movie>
    </movies>
  </soap:body>
</soap:envelope>
```

> The** main downside of the RPC style is that it is tightly coupled to the application code** (that is, if you decide you want to call these web methods like normal methods--this is not a requirement, but this is what the RPC style was designed for). This means that if you want to change the order of the parmeters or change the types of those parameters, this change will affect the definition of the web service itself (just as it would affect the definition of a normal function or method).

> **Document style services do not have this issue because they are loosely coupled with the application code**--the **application must handle the marshalling and unmarshalling of the XML data separately**. For example, with a document style service, it doesn't matter if the programmer decides to use a "float" instead of an "int" to represent a particular parameter because it's all converted to XML text in the end.

> The **main downside of the document style is that there is no standard way of determining which method of the web service the request is for**. It's easy to get around this limitation, but, however it's done, it must be done manually by the application code. 
###### (Note: The "document/literal wrapped" style removes this limitation; read on for more details.)

> Another point to note about the document style is that there are no rules for how the SOAP body must be formatted. This can either be seen as a downside or a strength, depending on your perspective. It's a strength if you are looking for the freedom to handle the message the way you want, but a downside if you don't want to have to do the extra marshalling/unmarshalling work that it requires.

> ## Encoded vs literal encodings

> In addition to the RPC and document styles, there are two types of encodings: "**encoded**" and "**literal**".

> **Literal** means that the SOAP body follows an XML schema, which is included in the web service's WSDL document. As long as the client has access to the WSDL, it knows exactly how each message is formatted.

> **Encoded**, on the other hand, means that the SOAP body does not follow a schema, but still follows a specific format which the client is expected to already know.** It is not endorsed by the WS-I standard** because there can be slight differences in the way in which various programming languages and web service frameworks interpret these formatting rules, leading to incompatabilities.

> This makes for 4 different style/encoding combinations:

> - **RPC/encoded** - RPC-style message that formats its body according to the rules defined in the SOAP standard (which are not always exact and can lead to incompatabilities).
- **RPC/literal** - RPC-style message that formats its body according to a schema that reflects the rules defined in the SOAP standard. This schema is included in the WSDL.
- **document/encoded** - Document-style message that does not include a schema (nobody uses this in practice).
document/literal - Document-style message that formats its body according to a schema. This schema is included in the WSDL.

> There's also a 5th type. It isn't an official standard but it is used a lot in practice. It came into being to compensate for document/literal's main shortcoming of not having a standard way of specifying the web method name:

> - **document/literal** wrapped - The same as document/literal, but wraps the contents of the body in an element with the same name as the web service method (just like RPC-style messages). This is what web services implemented in Java use by default.

> Is my understanding of all this accurate? Which approach do you think is the best? Let me know in the comments.


------------










Realmente o nosso serviço usa o estilo de integração RPC. 

No entanto, ao expor serviços dessa maneira muitas vezes o XML fica muito amarrado a aplicação. 

###### Isso pode gerar uma acoplamento forte e criar problemas de compatibilidade que dificulta a integração heterogênea. 

Na realidade, isso significava que muitas vezes um cliente não conseguia se comunicar com um serviço por causa do **RPC**.

## Document/Wrapped
Para não gerar problemas de compatibilidade, a grande maioria dos serviços usa hoje em dia o estilo **Document**. 

O grande problema do **Document** é que **não havia uma forma padrão para fazer RPC**! Felizmente isso mudou, como vocês já viram podemos usar o estilo **Document** para fazer uma chamada remota de um método. 

###### Basta embrulhar o documento em um elemento XML como mesmo nome do método! 

Esse forma se chama de **Document/Wrapped**. 

Ou seja, 
###### usamos o tempo todo Document/Wrapped para fazer RPC, ok?
Podemos deixar essa configuração explícita, usando a mesma anotação **@SOAPBinding** mas não é necessário já que é o padrão:
```java
@WebService
@SOAPBinding(style=Style.DOCUMENT,parameterStyle=ParameterStyle.WRAPPED)
public class EstoqueWS {
```
No SOAP temos um elemento Wrapped (com o nome do método) como vimos antes:
```xml
<soapenv:Envelope ...>
   <!-- header omitido -->
   <soapenv:Body>
      <ws:CadastrarItem><!-- document/wrapped -->
         <item>
            <codigo>?</codigo>
            <nome>?</nome>
            <tipo>?</tipo>
            <quantidade>?</quantidade>
         </item>
      </ws:CadastrarItem>
   </soapenv:Body>
</soapenv:Envelope>
```
## Document/Bare
Será que existem serviço do tipo document que não são wrapped? 

Existem, claro! E já discutimos isso, quando queremos entregar apenas o item sem ter conhecimento de qual método/procedimento é chamado no lado do servidor. 

Podemos testar isso facilmente usando a mesma anotação **@SOAPBinding**
```java
@WebService
@SOAPBinding(style=Style.DOCUMENT,parameterStyle=ParameterStyle.BARE)
public class EstoqueWS {
```
Como sempre, devemos re-publicar e atualizar o cliente.

Como resultado disso vemos que, a mensagem SOAP gerada não possui mais o elemento wrapped, apenas o item:
```xml
<soapenv:Envelope ...>
    <!-- header omitido -->
   <soapenv:Body>
      <ws:item>
         <codigo>MEA</codigo>
         <nome>MEAN</nome>
         <tipo>Livro</tipo>
         <quantidade>5</quantidade>
      </ws:item>
   </soapenv:Body>
</soapenv:Envelope>
```
Será que ainda podemos chamar o nosso serviço? Teste e você vai ver que funciona! 

Mas como o JAX-WS sabe resolver isso já que não tem o nome do método no SOAP? 

Bom nesse caso foi fácil. Pois só há um método que recebe um item. 

Mas se houvessem mais um método o JAX-WS já reclamaria na hora de subir o serviço (mas sobe), no entanto ao executar a mesma mensagem SOAP recebemos como resposta:
```xml
<S:Envelope xmlns:S="http://schemas.xmlsoap.org/soap/envelope/">
   <S:Body>
      <S:Fault xmlns:ns4="http://www.w3.org/2003/05/soap-envelope">
         <faultcode>S:Client</faultcode>
         <faultstring>Não é possível localizar o método de despacho para Request=[SOAPAction="",Payload={http://ws.estoque.caelum.com.br/}item]</faultstring>
      </S:Fault>
   </S:Body>
</S:Envelope>
```

###### Ou seja, se a assinatura das operation não for clara, o JAX-WS vai gerar um fault.

## Usando SOAPAction
Repare que na resposta aparece um elemento **SOAPAction** sendo uma String vazia. Esse **SOAPAction** foi criado para mensagens do tipo **Document** que querem definir o método a ser chamado fora do XML. 

A configuração do **SOAPAction** fica no **WSDL** que se baseia na nossa classe **EstoqueWS**. Nela podemos aproveitar a anotação **@WebMethod** para definir a action:

```java
@WebService()
@SOAPBinding(style=Style.DOCUMENT,parameterStyle=ParameterStyle.BARE)
public class EstoqueWS {

   //novidade atributo action
   @WebMethod(action="CadastrarItem", operationName="CadastrarItem") 
   @WebResult(name="item")
   public Item cadstrarItem(@WebParam(name="tokenUsuario", header=true) TokenUsuario token, @WebParam(name="item") Item item) throws ItemValidadorException, AutorizacaoException {
```

Republicando percebemos uma pequena mudança no WSDL:
```xml
<operation name="CadastrarItem">
   <soap:operation soapAction="CadastrarItem"/>
</operation>
```
###### O soapAction já existia antes, mas agora está preenchido com o valor da anotação **@WebMethod**. 

Ao atualizar o cliente e recriar o request não há nenhuma diferença na mensagem SOAP. A diferença está no protocolo HTTP que ganhou um novo cabeçalho:

**SOAPAction: "CadastrarItem"**

Através desse cabeçalho o JAX-WS sabe resolver o método correto e podemos executar a requisição sem problemas.


## Literal e encoded
Continuando na nossa viagem pelo WSDL temos em cada operation um input, output e um possível fault. 

Aqui podemos ver qual mensagem aparece onde. Ela pode ser um **input** ou **output**, e fazer parte do **body**, **header** ou **fault**:
```xml
<operation name="CadastrarItem">
   <soap:operation soapAction="CadastrarItem"/>
   <input>
      <soap:body use="literal" parts="item"/>
      <soap:header message="tns:CadastrarItem" part="tokenUsuario" use="literal"/>
   </input>
   <output>
      <soap:body use="literal"/>
   </output>
   <fault name="AutorizacaoException">
      <soap:fault name="AutorizacaoException" use="literal"/>
   </fault>
</operation>
```
Além disso, tem uma configuração importante, **use="literal"**! 

Ela faz parte da codificação da mensagem e significa que **na mensagem SOAP apenas dados (literais trafegam, sem nenhuma informação tipos ou regras de validação**. 

Isso faz sentido pois o lugar correto para os tipos e as regras de validação é o XSD.

Infelizmente, essa boa separação dos dados e tipos nem sempre foi assim, pois existe uma outra forma de codificação: o **encoded** que significa que na mensagem SOAP **os tipos são enviados junto aos dados literais**. 

Por isso, não há XSD! 

Lembra do estilo RPC e os problemas de compatibilidade? 

Pois é, era muito comum usar RPC/encoded. 

A mensagem fica parecida com a mensagem a seguir onde também estamos usando RPC/encoded:
```xml
<soapenv:Envelope ...>
   <soapenv:Body>
    <ws:CadastrarItem>
      <ws:item>
         <codigo type="xsd:string">MEA</codigo>
         <nome type="xsd:string">MEAN</nome>
         <tipo type="xsd:string">Livro</tipo>
         <quantidade type="xs:int">5</quantidade>
      </ws:item>
    </ws:CadastrarItem>
   </soapenv:Body>
</soapenv:Envelope>
```
Os dados na mensagem já vem com os tipos. Torna-se mais fácil para nós humanos entendermos, mas gera uma dor de cabeça terrível para validadores de XML. 

Enfim, não adianta nem discutir muito pois é algo legado, não é aderente à especificação de compatibilidade de serviços SOAP do W3C e deve ser evitado, ok?

Essa forma de codificação fez bastante sucesso (Encoded) e ainda existem serviços web legados que usam isso, mas saiba que o JAX-WS nem dá mais suporte a isso.

## O elemento Service - o endereço
Por fim temos o elemento **service** que é muito mais simples que define duas coisas. 

###### O binding utilizado e o endereço concreto para chamar serviço SOAP. 

Nesse caso é
```xml
<service name="EstoqueWSService">
   <port name="EstoqueWSPort" binding="tns:EstoqueWSPortBinding">
      <soap:address location="http://localhost:8080/estoquews"/>
   </port>
</service>
```
Todo esse tópico merecem uma boa revisão, não? Bora fazer os exercícios :)

## O que você aprendeu neste capítulo?
- Os estilos Document e RPC
- A codificação da mensagem SOAP: literal e encoded
- Usamos Document/literal/Wrapped para fazer RPC
- Encoded é algo legado e JAX-WS não suporta mais
- O elemento service define o endereço
 
### EXERCÍCIOS
Segue abaixo os dados de uma requisição SOAP:
```xml
POST /pedido HTTP/1.1
Host: http://www.caelum.com.br
Content-Type: application/soap+xml; charset=utf-8

<?xml version="1.0"?>
<soap:Envelope
xmlns:soap="http://www.w3.org/2001/12/soap-envelope"
soap:encodingStyle="http://www.w3.org/2001/12/soap-encoding">

<soap:Body xmlns:m="http://www.caelum.com.br/pedido">
  <m:pedido>
    <m:codigo>1742</m:codigo>
    <m:valor>319.12</m:valor>
    <m:data>01/12/2015</m:data>
  </m:pedido>
</soap:Body>

</soap:Envelope>
```
Qual estilo (style) foi utilizado no WSDL?
Na mensagem SOAP apareçam apenas informações sobre os dados que indica que foi utilizado o estilo Document. Se fosse RPC aparecia o nome do método na mensagem SOAP.
 
## Mãos a obra: Experimentando @SoapBinding

Teste a anotação **@SoapBinding** no Web Service. 

Use as configurações RPC e DOCUMENT.

Por exemplo, use o estilo RPC:
```java
@WebService
@SOAPBinding(style=Style.RPC)
public class EstoqueWS {
```
Também tente colocar DOCUMENT com os parâmetros nos estilo BARE e WRAPPED, por exemplo:
```java
@WebService
@SOAPBinding(style=Style.DOCUMENT, parameterStyle=ParameterStyle.BARE)
public class EstoqueWS {
```
Cada alteração do Web Service exige que você republique o serviço. Você também precisa atualizar o SoapUI.

###### DocumentComo vimos, o estilo Document é usado para simplesmente passar uma informação enquanto RPC foi pensado para chamar uma operação remotamente. Uma alternativa ao RPC é o padrão Document/Wrapped.
 
Sobre o elemento < binding> podemos afirmar:
###### O elemento < binding> realiza uma ligação entre o elemento < service> (que define o endereço e o protocolo do serviço) e o elemento < portType> (que define a interface), além de definir detalhes sobre a mensagem SOAP (no elemento < operation>).

Em WSDL abstrato somente definimos a interface, o que é suficiente para implementação do serviço. Deixamos detalhes a respeito do formato da mensagem e de como ela deve ser entregue a cargo do WSDL concreto.




















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
