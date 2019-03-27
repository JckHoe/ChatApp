import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.net.Socket;
import java.util.function.Consumer;

public abstract class NetworkConnection{

	private ConnectionThread connThread = new ConnectionThread();
	private Consumer<Serializable> onReceiveCallBack;
	private boolean Login;
	private String agent; // Target Agent's Name
	private String clientID; // client LoginID
	protected String [] client = new String[2];
	protected int clientCounter;
	protected boolean [] isSlotFree = {true,true};
	
	public NetworkConnection(Consumer<Serializable> onReceiveCallBack){
		this.onReceiveCallBack = onReceiveCallBack;
		connThread.setDaemon(true);
		Login = false;
		agent = "NO AGENT";
		clientID = "";
		clientCounter = 0;
	}
	
	public String getAgent(){
		return agent;
	}
	
	public String getClientID(){
		return clientID;
	}
	
	public boolean isLogin(){
		return Login;
	}
	
	public void startConnection() throws Exception {
		connThread.start();
	}
	
	public void send(Serializable data)throws Exception {
		connThread.out.writeObject(data);
		connThread.sleep(300);
	}
	
	public void closeConnection() throws Exception {
		connThread.socket.close();
	}
	
	protected abstract boolean isServer();
	protected abstract String getIP();
	protected abstract int getPort();
	
	private class ConnectionThread extends Thread {
		private Socket socket;
		private ObjectOutputStream out;
		
		
		public void run(){
			try (
				Socket socket = new Socket(getIP(), getPort());
				ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());
				ObjectInputStream in = new ObjectInputStream(socket.getInputStream());
			){
				this.socket = socket;
				this.out = out;
				socket.setTcpNoDelay(true);
				String [] temp, temp2; //temp2 is only for agent use, for organizing Message
				while (true){
					Serializable data = (Serializable) in.readObject();
					if (Login == false){
						temp = data.toString().split(";");
						if(temp[2].equals("Agent Login")){
							agent = "I AM AGENT";
							Login = true;
							clientID = temp[1];
						}else if (temp[2].equals("Login Successful")){
							Login = true;
							clientID = temp[1];
							agent = temp[0];
						}
					}
					if (Login == true && agent.equals("I AM AGENT")){
						//Agent Actions
						temp = data.toString().split(";");
						if(temp[0].equals("Add") && clientCounter<=2){
							for(int i=0;i<2;i++){
								if(isSlotFree[i] == true){
									isSlotFree[i] = false;
									client[i] = temp[1];
									onReceiveCallBack.accept((Serializable)(temp[1]+": =====Joined====="));
									clientCounter++;
									break;
								}
							}		
						}else if (temp[0].equals("MSG")){
							temp2 = temp[1].split(":");
							if(temp2[1].contains("offers by CVT")){
								send((Serializable)("\nTwo Packages: \n1.)Prepaid  \n2.)PostPaid\n"+"#"+temp2[0]));
								onReceiveCallBack.accept((Serializable)("AUTO:"+temp2[0]));
							}else {
								onReceiveCallBack.accept((Serializable)temp[1]);
							}
						}else if (temp[0].equals("Exit")){
							for(int i=0;i<2;i++){
								if(temp[1].equals(client[i])){
									onReceiveCallBack.accept((Serializable)(temp[1]+": =====Disconnected====="));
									break;
								}
							}
						}
					}else if (Login == true){
						//Client Actions
						temp = data.toString().split(";");
						if(temp[0].equals("MSG")){
							onReceiveCallBack.accept((Serializable)temp[1]);
						}
						if(temp[0].equals("Exit")){
							onReceiveCallBack.accept((Serializable)("FORCE EXIT"));
							agent = "NO AGENT";
						}
					}
				}
			}
			catch (Exception e){
				onReceiveCallBack.accept("Connection closed\nPlease Restart Client");
			}
		}
	}
}
