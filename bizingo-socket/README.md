# Bizingo Socket


Este projeto foi criado para a disciplina de Programacao Paralela e Distribuida(PPD) do Instituto Federal do Ceara(IFCE)
.
---

# Estrutura do projeto

O projeto está dividido em uma arquitetura cliente-servidor. Ambas as partes foram escritas em java 8, utilizando a biblioteca de sockets da java.net para envio e recebimento de mensagens. A GUI do cliente foi desenvolvida com o framework JavaFX e algumas bibliotecas de componentes estilo Material Design.

---

## Dependências utilizadas

Para a aplicação em Python foram usadas, principalmente, as dependências:

+ Apache Commons Collections
+ Apache Commons IO
+ VAVR
+ JFOENIX

---

## Execução da aplicação

Caso queira subir a aplicacao do servidor na sua maquina, execute o .jar do projeto servidor, ele ira subir a aplicacao na porta 5005, suba 2 clientes a partir do arquivo cliente_local.jar para que o jogo seja iniciado. Caso queira rodar clientes em maquinas diferentes ou ate mesmo fora da rede local, suba o cliente a partir do arquivo cliente_remoto.jar, esse executavel esta configurado para se conectar com o servidor em nuvem rodando em uma instancia ec2 da aws com um proxy de acesso configurado via Ngrok, o ip e porta cofigurados no cliente remoto foram setados de acordo com as configuracoes do proxy.


---

## Conhecimentos adquiridos e dificuldades encontradas

* Configurar um servidor com ServerSocket java
* Configurar um cliente usando Socket em java
* Sincronizar mensagens entre threads

---

#### Screenshots da aplicação

![Tela do menu](https://i.imgur.com/a8tV7XW.jpg)
  *Tela do menu da aplicacao, em que o usuario pode especificar seu nickname e avatar*

---  
  
![Tela principal do jogo](https://i.imgur.com/qYdPb43.jpg)
  *Tela do jogo, no centro o tabuleiro do bizingo, na lateral esquerda a tela de chat em que os jogadores podem trocar mensagens*
  
 ---
 
 ![Chat com mensagens](https://i.imgur.com/1APzKs7.jpg)
  *Exibindo funcionalidade de troca de mensagens na tela de chat*
  
  ---
  
  ![Pecas sendo movimentadas](https://i.imgur.com/443RHA0.jpg)
  *Exibindo funcionalidade de movimentacao de pecas e controle de turno*
  

