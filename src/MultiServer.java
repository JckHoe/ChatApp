import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.net.ServerSocket;
import java.net.Socket;
import java.rmi.Naming;
import java.rmi.registry.LocateRegistry;
import java.util.StringTokenizer;
import java.util.Vector;
import java.util.concurrent.ConcurrentLinkedQueue;

public class MultiServer {
	// Vector to store active clients
    static ConcurrentLinkedQueue<ClientHandler> ar = new ConcurrentLinkedQueue<>();
     
    // counter for clients
    static int i = 0;
 
    public static void main(String[] args) throws Exception 
    {
    	LocateRegistry.createRegistry(1099);
		AuthenticationInterface login = new AuthenticationImplementation();
		Naming.rebind("loginObject", login);
		System.out.println("RMI Registry server is started!");
        // server is listening on port 1234
        ServerSocket serverSocket = new ServerSocket(55555);
        
        Socket socket;
        System.out.println("Server Up...");
        
        // running infinite loop for getting
        // client request
        while (true) 
        {
            // Accept the incoming request
            socket = serverSocket.accept();
 
            System.out.println("New client request received : " + socket);
             
            // obtain input and output streams
            ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());
            ObjectInputStream in = new ObjectInputStream(socket.getInputStream());
             
            System.out.println("Creating a new handler for this client...");
 
            // Create a new handler object for handling this request.
            ClientHandler client = new ClientHandler(socket,"client " + i, in, out);
 
            // Create a new Thread with this object.
            Thread thread = new Thread(client);
             
            System.out.println("Adding this client to active client list");
 
            // add this client to active clients list
            ar.add(client);
 
            // start the thread.
            thread.start();
 
            // increment i for new client.
            i++;
 
        }
    }
}

//ClientHandler class
class ClientHandler implements Runnable {
	private String name; //unique name of this thread
	final ObjectInputStream in;
	final ObjectOutputStream out;
	Socket socket;
	boolean isloggedin; //login status
	boolean isAgent; //check whether or not it is an agent
	String myAgent; // Client's Agent Name
	int clientCounter; //for agent use
  
// constructor
	public ClientHandler(Socket socket, String name, ObjectInputStream in, ObjectOutputStream out) {
		this.in = in;
		this.out = out;
		this.name = name;
		this.socket = socket;
		this.isloggedin=false;
		this.isAgent = false;
		clientCounter = 0;
		myAgent = "NO AGENT";
	}

	@Override
	public void run() {
	
		String received;
		Serializable data;
		
		while (true) 
		{
			try
			{
				// receive the string
				data = (Serializable)in.readObject();
				received =data.toString();
				
				//Client Chat controller
				if (!received.equals("Exit") && isloggedin == true && isAgent == false){
					StringTokenizer st = new StringTokenizer(received, "#");
					String MsgToSend = st.nextToken();
					String recipient = st.nextToken();
					
					for (ClientHandler mc : MultiServer.ar) 
					{
						if (mc.name.equals(recipient) && mc.isloggedin==true) 
						{
							received = this.name+": "+MsgToSend;
							mc.out.writeObject("MSG;"+(Serializable)received);
							break;
						}
					}
				}
				
				if(!received.equals("Exit") && received.contains("UPDATE")){
					String [] array = received.split(";");
					for (ClientHandler mc : MultiServer.ar) 
					{
						if(mc.name.equals(array[1])){
							mc.clientCounter = mc.clientCounter -1;
						}
					}
				}
				//Agent Chat controller
				if (!received.equals("Exit") && isloggedin == true && isAgent == true){
					StringTokenizer st2 = new StringTokenizer(received, "#");
					String MsgToSend2 = st2.nextToken();
					String recipient2 = st2.nextToken();
					
					for (ClientHandler mc : MultiServer.ar) 
					{
						if (MsgToSend2.equals("KICK") && mc.name.equals(recipient2) && mc.isloggedin==true) 
						{
							mc.out.writeObject((Serializable)("Exit;"));
							break;
						}else if (mc.name.equals(recipient2) && mc.isloggedin==true){
							received = this.name+": "+MsgToSend2;
							mc.out.writeObject("MSG;"+(Serializable)received);
							break;
						}
					}
				}
				
				if(!received.equals("Exit") && isloggedin == false){
					String [] input = received.split(";");
					boolean checker = false;
					//Client Login Authentication
					if (input[0].equals("Client") && input[1].equals("User1") && input[2].equals("1234")){
						checker = false;
						for (ClientHandler mc : MultiServer.ar) {
							if(mc.name.equals("User1")){
								checker=true;
							}
						}
						for (ClientHandler mc : MultiServer.ar) {
							if(checker==false && mc.isAgent == true && mc.clientCounter<2){
								received = mc.name+";User1;Login Successful";
			                    out.writeObject((Serializable)received);
			                    isloggedin = true;
			                    name = "User1";
			                    myAgent = mc.name;
			                    mc.out.writeObject("Add;"+(Serializable)name);
			                    mc.clientCounter++;
			                    break;
							}
						}
						if(isloggedin == false){
							received = " ; ;No Agent Available";
		                    out.writeObject((Serializable)received);
						}
	                }
					else if (!received.equals("Exit") && input[0].equals("Client") && input[1].equals("User2") && input[2].equals("1234")){
						checker = false;
						for (ClientHandler mc : MultiServer.ar) {
							if(mc.name.equals("User2")){
								checker=true;
							}
						}
						for (ClientHandler mc : MultiServer.ar) {
							if(checker==false && mc.isAgent == true && mc.clientCounter<2){
								received = mc.name+";User2;Login Successful";
			                    out.writeObject((Serializable)received);
			                    isloggedin = true;
			                    name = "User2";
			                    myAgent = mc.name;
			                    mc.out.writeObject("Add;"+(Serializable)name);
			                    mc.clientCounter++;
			                    break;
							}
						}
						if(isloggedin == false){
							received = " ; ;No Agent Available";
		                    out.writeObject((Serializable)received);
						}
	                }
					else if (!received.equals("Exit") && input[0].equals("Client") && input[1].equals("Jack") && input[2].equals("1234")){
						checker = false;
						for (ClientHandler mc : MultiServer.ar) {
							if(mc.name.equals("Jack")){
								checker=true;
							}
						}
						for (ClientHandler mc : MultiServer.ar) {
							if(checker==false && mc.isAgent == true && mc.clientCounter<2){
								received = mc.name+";Jack;Login Successful";
			                    out.writeObject((Serializable)received);
			                    isloggedin = true;
			                    name = "Jack";
			                    myAgent = mc.name;
			                    mc.out.writeObject("Add;"+(Serializable)name);
			                    mc.clientCounter++;
			                    break;
							}
						}
						if(isloggedin == false){
							received = " ; ;No Agent Available";
		                    out.writeObject((Serializable)received);
						}
	                }
					else if (!received.equals("Exit") && input[0].equals("Client") && input[1].equals("Tristan") && input[2].equals("1234")){
						checker = false;
						for (ClientHandler mc : MultiServer.ar) {
							if(mc.name.equals("Tristan")){
								checker=true;
							}
						}
						for (ClientHandler mc : MultiServer.ar) {
							if(checker==false && mc.isAgent == true && mc.clientCounter<2){
								received = mc.name+";Tristan;Login Successful";
			                    out.writeObject((Serializable)received);
			                    isloggedin = true;
			                    name = "Tristan";
			                    myAgent = mc.name;
			                    mc.out.writeObject("Add;"+(Serializable)name);
			                    mc.clientCounter++;
			                    break;
							}
						}
						if(isloggedin == false){
							received = " ; ;No Agent Available";
		                    out.writeObject((Serializable)received);
						}
	                }
					else if (!received.equals("Exit") && input[0].equals("Agent") && input[1].equals("Agent007") && input[2].equals("1234")){
						checker = false;
						for (ClientHandler mc : MultiServer.ar) {
							if(mc.name.equals("Agent007")){
								checker=true;
							}
						}
						if(checker == false){
							received = " ;Agent007;Agent Login";
		                    out.writeObject((Serializable)received);
		                    isloggedin = true;
		                    name = "Agent007";
		                    isAgent = true;
						}
	                }
					else if (!received.equals("Exit") && input[0].equals("Agent") && input[1].equals("AgentIrwin") && input[2].equals("1234")){
						checker = false;
						for (ClientHandler mc : MultiServer.ar) {
							if(mc.name.equals("AgentIrwin")){
								checker=true;
							}
						}
						if(checker == false){
							received = " ;AgentIrwin;Agent Login";
		                    out.writeObject((Serializable)received);
		                    isloggedin = true;
		                    name = "AgentIrwin";
		                    isAgent = true;
						}
						
	                }
				}
				
				if(received.equals("Exit")){
					if (isAgent == false){
						//Client Log out
						for (ClientHandler mc : MultiServer.ar) 
						{
							if (mc.name.equals(myAgent)) 
							{
								mc.clientCounter =  mc.clientCounter-1;
								try {
									mc.out.writeObject((Serializable)("Exit;"+name));
								} catch (IOException e1) {
									break;
								}
								break;
							}
						}
						for (ClientHandler mc : MultiServer.ar) 
						{
							if(mc.name.equals(name)){
								MultiServer.ar.remove(mc);
							}
						}
					}
					else {
						//Agent Logout
						for (ClientHandler mc : MultiServer.ar) 
						{
							if (mc.myAgent.equals(name)) 
							{
								try {
									mc.out.writeObject((Serializable)("Exit;"));
								} catch (IOException e1) {
									
								}
							}
						}
						for (ClientHandler mc : MultiServer.ar) 
						{
							if(mc.name.equals(name)){
								MultiServer.ar.remove(mc);
							}
						}
					}
					break;
				}
			} catch (Exception e) {
				if (isAgent == false){
					//Client Log out
					for (ClientHandler mc : MultiServer.ar) 
					{
						mc.clientCounter =  mc.clientCounter-1;
						if (mc.name.equals(myAgent)) 
						{
							try {
								mc.out.writeObject((Serializable)("Exit;"+name));
							} catch (IOException e1) {
								break;
							}
							break;
						}
					}
					for (ClientHandler mc : MultiServer.ar) 
					{
						if(mc.name.equals(name)){
							MultiServer.ar.remove(mc);
						}
					}
				}
				else {
					//Agent Logout
					for (ClientHandler mc : MultiServer.ar) 
					{
						if (mc.myAgent.equals(name)) 
						{
							try {
								mc.out.writeObject((Serializable)("Exit;"));
							} catch (IOException e1) {
								
							}
						}
					}
					for (ClientHandler mc : MultiServer.ar) 
					{
						if(mc.name.equals(name)){
							MultiServer.ar.remove(mc);
						}
					}
				}
				break;
				
			}
				      
		}
		
		try
		{
		    // closing resources
	        this.in.close();
	        this.out.close();
	        socket.close();
		    
		}catch(IOException e){
			e.printStackTrace();
		}
	}
}
