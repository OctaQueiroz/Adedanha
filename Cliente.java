import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.net.Socket;
import javax.swing.*;

public class Cliente extends JFrame implements ActionListener, KeyListener 
{
	private static final long serialVersionUID = 1L;
	private JTextArea texto;
	private JTextField txtMsg;
	private JButton btnSend;
	private JButton btnSair;
	private JLabel lblHistorico;
	private JLabel lblMsg;
	private JPanel pnlContent;
	private Socket socket;
	private OutputStream ou ;
	private Writer ouw; 
	private BufferedWriter bfw;
	private JTextField txtIP;
	private JTextField txtPorta;
	private JTextField txtNome;

	public Cliente() throws IOException
	{                  
	    JLabel lblMessage = new JLabel("Entre com seu nome de jogador!");
	    txtNome = new JTextField("Cliente");                
	    Object[] texts = {lblMessage,txtNome };  
	    JOptionPane.showMessageDialog(null, texts);              
	     
	    pnlContent = new JPanel();
	    texto = new JTextArea(10,40);
	    texto.setEditable(false);
	    texto.setBackground(new Color(240,240,240));
	    txtMsg = new JTextField(20);
	    lblHistorico = new JLabel("Historico");
	    lblMsg = new JLabel("Palavra");
	    btnSend = new JButton("Enviar");
	    btnSend.setToolTipText("Enviar Palavra");
	    btnSend.addActionListener(this);
	    btnSend.addKeyListener(this);
	    txtMsg.addKeyListener(this);
	    
	    JScrollPane scroll = new JScrollPane(texto);

	    texto.setLineWrap(true);  
	    pnlContent.add(lblHistorico);
	    pnlContent.add(scroll);
	    pnlContent.add(lblMsg);
	    pnlContent.add(txtMsg);
	    pnlContent.add(btnSend);
	    pnlContent.setBackground(Color.LIGHT_GRAY);                                 
	                        
	    setTitle(txtNome.getText());
	    setContentPane(pnlContent);
	    setLocationRelativeTo(null);
	    setResizable(false);
	    setSize(500,320);
	    setVisible(true);
	    setDefaultCloseOperation(EXIT_ON_CLOSE);
	}

	//Metodo para realizar a conexão com o servidor
	public void conectar() throws IOException
	{                           
	  	socket = new Socket("127.0.0.1",12345);
	  	ou = socket.getOutputStream();
	  	ouw = new OutputStreamWriter(ou);
	  	bfw = new BufferedWriter(ouw);
	  	bfw.write(txtNome.getText()+"\r\n");
	  	bfw.flush();
	}

	//Metodo para enviar mensagens ao servidor
	public void enviarMensagem(String msg) throws IOException{
	                           
	   	bfw.write(msg+"\r\n");
	    texto.append(txtMsg.getText()+"\r\n");
	    

     	bfw.flush();
     	txtMsg.setText("");        
	}

	//Metodo para escutar mensagens recebidas do servidor
	public void escutar() throws IOException{
	                           
	   	InputStream in = socket.getInputStream();
	   	InputStreamReader inr = new InputStreamReader(in);
	   	BufferedReader bfr = new BufferedReader(inr);
	   	String msg = "";
	                           
	    while(true){
	    	if(bfr.ready())
	       	{
	         	msg = bfr.readLine();
	         	//Se for notificado que alguém venceu, retorna resposta para resetar a lista de palavras
		       	if(msg.equals("$%RESET%$")){
		         	enviarMensagem("$%OK%$");
		       	}
		       	else
		       	{	
		         	texto.append(msg+"\r\n");         
		       	}
	       	}
	    }
	}

	@Override
	public void actionPerformed(ActionEvent e) {
	          
	  	try {
		    if(e.getActionCommand().equals(btnSend.getActionCommand()))
		    {
		        enviarMensagem(txtMsg.getText());
		    } 
	   	} 
	   	catch (IOException e1) 
	   	{
	          // TODO Auto-generated catch block
	          e1.printStackTrace();
	    }                       
	}

	@Override
	public void keyPressed(KeyEvent e) {
	                
	    if(e.getKeyCode() == KeyEvent.VK_ENTER){
	       	try {
	          	enviarMensagem(txtMsg.getText());
	       	} 
	       	catch (IOException e1) 
	       	{
	           // TODO Auto-generated catch block
	           	e1.printStackTrace();
	       	}                                                          
	   	}                       
	}
	    
	@Override
	public void keyReleased(KeyEvent arg0) {
	  // TODO Auto-generated method stub               
	}
	    
	@Override
	public void keyTyped(KeyEvent arg0) {
	  // TODO Auto-generated method stub               
	}

	public static void main(String []args) throws IOException{       
   		Cliente app = new Cliente();
   		app.conectar();
   		app.escutar();
	}
}