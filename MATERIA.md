# CURSO DE JAX-WS-SOAP
###### concluído em 04/02/2018


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
