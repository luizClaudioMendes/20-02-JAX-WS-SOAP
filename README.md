# README #

### como começar? ###
no diretorio onde o projeto ira ficar digite:
* git clone https://LuizCMendes@bitbucket.org/LuizCMendes/02-alura_jax_ws_soap.git


### configurar o JAVA nas variaveis de ambiete windows 10 ###

Após o término da instalação do Java, vamos configurar as variáveis do ambiente, abaixo estão os passos a serem seguidos.

1. Clique com o botão direito em cima do ícone “Este Computador”;

2. Vá em “Propriedades”;

3. Selecione a aba “Configurações avançadas do sistema”, depois na aba “Avançado”;

4. Clique no botão “Variáveis de ambiente”;

5. Clique no botão “Nova” em “Variáveis do sistema”;

5.1. Nome da variável: JAVA_HOME

5.2. Valor da variável: coloque aqui o endereço de instalação do JDK “C:\Arquivos de programas\Java\jdk1.5.0_05”

5.3. Clique em OK

6. Selecione a variável PATH em “Variáveis do sistema” e clique no botão “Editar”.

6.1. Defina o valor dessa variável com o caminho da pasta Bin. No caso, pode-se utilizar a variável JAVA_HOME previamente definida.

;%JAVA_HOME%\bin




###Instalação do JBoss Wildfly###
Como mostrado no vídeo, vamos configurar o servidor Wildfly no Eclipse. Se você já configurou o Wildfly, pode pular este exercício.
1) Para baixar o servidor de aplicação JBoss Wildfly acesse a página http://wildfly.org/downloads/. Baixe a versão 8 e extraia o arquivo.
2) Com Eclipse (Java EE) aberto instale o Server Adapter (JBOSS TOOLS)  para rodar o Wildfly dentro do Eclipse. Para tal entre na View Servers e clique em New Server. 
Na janela procure o link Download additional server adapters. Na lista escolha JBoss AS Tools e confirme as próximas telas. Após a instalação, reinicie o Eclipse.
3) Com o Server Adapter instalado, configure o Wildfly como Servidor no Eclipse. Novamente, abra a View Servers e crie um New Server. Na lista escolha o Wildfly na versão 8 e 
configure o diretório de instalação do Wildfly. Após a configuração, o Wildfly deve aparecer na lista de servers:
4) Ainda na View Servers inicie o Wildfly. Fique atento ao console.
Você pode testar a instalação ao acessar http://localhost:8080/
Deve aparecer a página inicial do Wildfly:
