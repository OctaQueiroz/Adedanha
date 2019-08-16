import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Servidor extends Thread 
{
	private static ArrayList<BufferedWriter>clientes;           
	private static ServerSocket server; 
	private String nome;
	public String nomeCliente;
	public List<String> palavras;
	private static Socket con;
	private InputStream inputStream;  
	private InputStreamReader streamReader;  
	private BufferedReader bufferedreader;
	public static List<Thread> listaClientes;
	private static Random gerador = new Random();
	private static char letra;
	

	public Servidor(Socket con)
	{
	   	this.con = con;
	   	try
	   	{
	   		nomeCliente = "";
	   		//Aramzena as palavras  enviadas pelo cliente
	   		palavras = new ArrayList<String>();
	   		//Metodos para leitura e escrita  do cliente
	        inputStream  = con.getInputStream();
	        streamReader = new InputStreamReader(inputStream);
	        bufferedreader = new BufferedReader(streamReader);
	   	}
	   	catch (IOException e) 
	   	{
	        e.printStackTrace();
	   	}                          
	}

	public void run()
	{                       
	  	try
	  	{                                  
		    String msg;
		    
		    OutputStream outputStream =  this.con.getOutputStream();
		    Writer writer = new OutputStreamWriter(outputStream);
		    BufferedWriter escritor = new BufferedWriter(writer);
		    //A primeira palavra lida na execução é o nome de usuário passado na interface inicial
		    nomeCliente = nome = msg = bufferedreader.readLine();
		    
		    while(msg != null)
		    {           
		       	msg = bufferedreader.readLine();
		       	//Verifica se a palavra começa com a letra sorteada e se ela ja foi inserida na rodada
		       	if(!msg.equals("")){
		       		//Verifica se a primeira letra é meiúscula, se não for, passa a ser
		       		//Char[] temp = msg.toCharArray();
		       		if(!Character.isUpperCase(msg.toCharArray()[0])){
		       			//System.out.println("ENTROU");
		       			msg = Character.toUpperCase(msg.charAt(0)) + msg.substring(1);
		       		}
		       		if(msg.charAt(0) == letra){
			       		boolean copia = false;
			       		for(int i = 0; i<palavras.size(); i++){
			       			if(palavras.get(i).equals(msg)){
			       				copia = true;
			       				break;
			       			}
			       		}
			       		if(!copia){
			       			palavras.add(msg);
			       		}
		       		}
		       	}
		       	
		       	//Assim que for notificado que houve um vencedor, a lista de palavras  é apagada
		       	if(msg.equals("$%OK%$")){
		       		System.out.println(msg);
		       		palavras = new ArrayList<String>();
		       	}
		       	//sendToAll(escritor, msg);
		       	//System.out.println(msg);
		       	int vencedor;
		       	if(palavras.size()==5){
		       		sendToAll(escritor, nomeCliente + " venceu com as seguintes palavras:\n");
		       		for(int i = 0; i < 5; i++){
		       			sendToAll(escritor, palavras.get(i) + "\n");
		       		}
		       		//Envia uma notificação para todas as threads sinalizando o código de que houve um vencedor
		       		sendToAll(escritor, "$%RESET%$");
		       		//Gera um novo caracter aleatório e busca seu valor na tabla ascII
		       		int rand = gerador.nextInt(26)+65;
		       		letra = (char)rand;
		       		sendToAll(escritor," Agora escrevam 5 palavras que se iniciam com a letra:" + letra +".\n");
		       		
		       	}                                           
		    }			                                      
		}
		catch (Exception e)
		{
	    	e.printStackTrace();
	   	}                       
	}

	public static void sendToAll(BufferedWriter bwSaida, String msg) throws  IOException 
	{
		BufferedWriter bwS;
		
		//Manda a mensagem para cada thread por meio do buffered writer pessoal de cada uma
		for(BufferedWriter bw : clientes)
		{
		   	bwS = (BufferedWriter)bw;
		    bw.write(msg+"\r\n");
		    bw.flush(); 
		}          
	}

	/***
	 * Método main
	 * @param args
	 */
	public static void main(String []args) 
	{    
	  	try{
		    server = new ServerSocket(12345);
		    clientes = new ArrayList<BufferedWriter>();
		    
	    	listaClientes = new ArrayList<Thread>();

	    	//Gera um novo caracter aleatório e busca seu valor na tabla ascII
			int rand = gerador.nextInt(26)+65;
		    letra = (char)rand;
	     	
		    int i = 0;

	     	while(i < 3){
	       		System.out.println("Aguardando conexão...");
		       	Socket con = server.accept();
		       	System.out.println("Cliente conectado...");
		     	Thread t = new Servidor(con);
		     	//Mantém um histórico de quem está conectado
	        	t.start();
	        	
	        	//Cria um buffered writer para a nova thread criada
	        	OutputStream outputStream =  con.getOutputStream();
		    	Writer writer = new OutputStreamWriter(outputStream);
		    	BufferedWriter escritor = new BufferedWriter(writer); 
		    	//Guarda o buffered pessoal da thread para ser utilizado mais tarde
		    	clientes.add(escritor);
	        	
	        	sendToAll(escritor," Aguardando mais "+(3-i)+" jogador(es).\n");
	        	
	        	i++;
	        	if(i == 3){
	        		sendToAll(escritor," Agora escrevam 5 palavras que se iniciam com a letra: " + letra +".\n");
	        	}   
	    	}
	    	
	                              
	  	}catch (Exception e) {
	    
	    	e.printStackTrace();
	  	}                       
	}// Fim do método main                     
}//Fim da classe