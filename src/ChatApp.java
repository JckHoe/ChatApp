import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.rmi.AccessException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.SourceDataLine;
import javax.sound.sampled.TargetDataLine;
import javax.swing.JButton;
import javax.swing.JFrame;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.HPos;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;

public class ChatApp extends Application{
	private TextArea msg = new TextArea();
	private NetworkConnection connection = createClient();
	
	ByteArrayOutputStream byteOutputStream;
	AudioFormat format;
	TargetDataLine targetDataLine;
	AudioInputStream InputStream;
	SourceDataLine source;
	
	private GridPane createContent(Stage primaryStage) throws AccessException, RemoteException, NotBoundException{
		Registry registry = null;
		try {
			registry = LocateRegistry.getRegistry("127.0.0.1");
		} catch (Exception e1) {
			e1.printStackTrace();
		}
		AuthenticationInterface login =(AuthenticationInterface)
				registry.lookup("loginObject");
		TextField input = new TextField();
		PasswordField pwd = new PasswordField();
		Label error = new Label("");
		Button Submit = new Button("Submit");
		Submit.setId("iphone");
		input.setOnAction(event->{
			error.setText("");
			boolean isServerDown = false;
			try {
				if(login.userAuth(input.getText(), pwd.getText()) == true){
					String temp = "Client;"+input.getText() + ";" + pwd.getText();
					input.clear();
					pwd.clear();
					try {
						connection.send(temp);
					} catch (Exception e) {
						isServerDown = true;
					}
					if(connection.isLogin() == true){
						Scene scene = new Scene(chatPage(),600,600);
						scene.getStylesheets().add("Button.css");
						primaryStage.setScene(scene);
					}
					else if(isServerDown == false){
						error.setText("Login Error or All Agents are Busy");
					}
					else if(isServerDown == true){
						error.setText("Server Down\nRestart Client");
					}
				}
			}catch (Exception e){
				e.printStackTrace();
			}
		});
		
		pwd.setOnAction(event->{
			error.setText("");
			boolean isServerDown = false;
			String temp = "Client;"+input.getText() + ";" + pwd.getText();
			input.clear();
			pwd.clear();
			try {
				connection.send(temp);
			} catch (Exception e) {
				isServerDown = true;
			}
			if(connection.isLogin() == true){
				Scene scene = new Scene(chatPage(),600,600);
				scene.getStylesheets().add("Button.css");
				primaryStage.setScene(scene);
			}
			else if(isServerDown == false){
				error.setText("Login Error or All Agents are Busy");
			}
			else if(isServerDown == true){
				error.setText("Server Down\nRestart Client");
			}
		});
		
		Submit.setOnAction(event ->{
			error.setText("");
			boolean isServerDown = false;
			String temp = "Client;"+input.getText() + ";" + pwd.getText();
			input.clear();
			pwd.clear();
			try {
				connection.send(temp);
			} catch (Exception e) {
				isServerDown = true;
			}
			if(connection.isLogin() == true){
				Scene scene = new Scene(chatPage(),600,600);
				scene.getStylesheets().add("Button.css");
				primaryStage.setScene(scene);
			}
			else if(isServerDown == false){
				error.setText("Login Error or All Agents are Busy");
			}
			else if(isServerDown == true){
				error.setText("Server Down\nRestart Client");
			}
		});
		
		GridPane root = new GridPane();
		root.setVgap(10);
		root.setHgap(15);
		root.add(new Label("Username"), 0, 0);
		root.add(input, 1, 0);
		root.add(new Label("Password"), 0, 1);
		root.add(pwd, 1, 1);
		root.add(Submit, 1, 2);
		GridPane.setHalignment(Submit, HPos.RIGHT);
		GridPane pane = new GridPane();
		pane.setAlignment(Pos.CENTER);
		pane.setVgap(20);
		Label title = new Label("User Login Page");
		title.setFont(Font.font("Arial", FontWeight.BOLD, 20));
		pane.add(title, 0, 0);
		pane.add(root, 0, 1);
		pane.add(error, 0, 2);
		return pane;
	}
	
	public GridPane chatPage (){
		msg.appendText("Welcome "+connection.getClientID()+" To CVT Customer Service!\nYou are connected to "+connection.getAgent()+"\n");
		msg.setPrefHeight(300);
		msg.setWrapText(true);
		GridPane root = new GridPane();
		root.setVgap(10);
		root.setAlignment(Pos.CENTER);
		msg.setEditable(false);
		root.add(msg, 0, 0);
		TextField txt = new TextField();
		root.add(txt, 0, 1);
		Button btn = new Button("Send");
		root.add(btn, 0, 2);
		btn.setId("iphone");
		Button start = new Button("Start Voice");
		root.add(start, 0, 3);
		start.setId("iphone");
		Button exit = new Button("Exit Program");
		exit.setId("iphone");
		root.add(exit, 0, 4);
		
		start.setOnAction(event ->{
			Label talk = new Label("Talking...");
			start.setVisible(false);
			root.add(talk, 0,3);
			UDPSend send = new UDPSend();
		});
		
		exit.setOnAction(event->{
			String temp = "Exit";
			try {
				connection.send(temp);
			} catch (Exception e) {
				
			}
			System.exit(1);
		});
		
		txt.setOnAction(event->{
			if(connection.getAgent().equals("NO AGENT")){
				msg.clear();
				msg.appendText("Error, Agent Already Disconnected!\nPlease Relog if you seek further Assistance\n");
				txt.clear();
			}
			else {
				String temp = txt.getText()+"#"+connection.getAgent();
				try {
					connection.send(temp);
				} catch (Exception e) {
					
				}
				msg.appendText(connection.getClientID()+" : "+txt.getText()+"\n");
				txt.clear();
			}
		});
		
		btn.setOnAction(event->{
			if(connection.getAgent().equals("NO AGENT")){
				msg.clear();
				msg.appendText("Error, Agent Already Disconnected!\nPlease Relog if you seek further Assistance\n");
				txt.clear();
			}
			else {
				String temp = txt.getText()+"#"+connection.getAgent();
				try {
					connection.send(temp);
				} catch (Exception e) {
					
				}
				msg.appendText(connection.getClientID()+" : "+txt.getText()+"\n");
				txt.clear();
			}
		});
		
		return root;
	}
	
	public GridPane menu (Stage primaryStage){
		GridPane root = new GridPane();
		root.setAlignment(Pos.CENTER);
		root.setVgap(60);
		Label title = new Label("Welcome to CVT Customer Service");
		title.setFont(Font.font("Arial", FontWeight.BOLD, 18));
		Button btn = new Button("Login");
		btn.setOnAction(event -> {
			try {
				Scene scene = new Scene(createContent(primaryStage),600,600);
				scene.getStylesheets().add("Button.css");
				primaryStage.setScene(scene);
			}catch (Exception e){
				e.printStackTrace();
			}
		});
		GridPane.setHalignment(btn, HPos.CENTER);
		root.add(title, 0, 0);
		root.add(btn, 0, 1);
		btn.setId("shinyOrange");
		return root;
	}
	
	public void init() throws Exception {
		connection.startConnection();
	}
	
	public void start(Stage primaryStage) throws Exception{
		Scene scene = new Scene(menu(primaryStage),600,600);
		scene.getStylesheets().add("Button.css");
		primaryStage.setScene(scene);
		primaryStage.show();
	}
	
	public void stop() throws Exception {
		connection.closeConnection();
	}
	
	private Client createClient(){
		return new Client(55555, "127.0.0.1", data->{
			Platform.runLater(()-> {
				if(data.toString().equals("FORCE EXIT")){
					String temp = "Exit";
					try {
						connection.send(temp);
					} catch (Exception e) {
						
					}
					System.exit(1);
				}
				msg.appendText(data.toString() + "\n");
			});
		});
	}
	
	public static void main (String [] args){
		launch(args);
	}
}

@SuppressWarnings("serial")
class UDPSend extends JFrame {

	boolean stopaudioCapture = false;
	ByteArrayOutputStream byteOutputStream;
	AudioFormat adFormat;
	TargetDataLine targetDataLine;
	AudioInputStream InputStream;
	SourceDataLine sourceLine;
	Graphics g;

	public UDPSend() {
	    final JButton capture = new JButton("Capture");
	    final JButton stop = new JButton("Stop");

	    capture.setEnabled(true);
	    stop.setEnabled(false);

	    capture.addActionListener(new ActionListener() {
	        public void actionPerformed(ActionEvent e) {
	            capture.setEnabled(false);
	            stop.setEnabled(true);
	            captureAudio();
	        }
	    });
	    getContentPane().add(capture);

	    stop.addActionListener(new ActionListener() {
	        public void actionPerformed(ActionEvent e) {
	            stop.setEnabled(false);
	            stopaudioCapture = true;
	            targetDataLine.close();
	            
	        }
	    });
	    getContentPane().add(stop);


	    getContentPane().setLayout(new FlowLayout());
	    setTitle("Voice Chat");
	    setDefaultCloseOperation(EXIT_ON_CLOSE);
	    setSize(400, 100);
	    getContentPane().setBackground(Color.white);
	    setVisible(true);

	    g = (Graphics) this.getGraphics();
	}

	private void captureAudio() {
	    try {
	        adFormat = getAudioFormat();
	        DataLine.Info dataLineInfo = new DataLine.Info(TargetDataLine.class, adFormat);
	        targetDataLine = (TargetDataLine) AudioSystem.getLine(dataLineInfo);
	        targetDataLine.open(adFormat);
	        targetDataLine.start();

	        Thread captureThread = new Thread(new CaptureThread());
	        captureThread.start();
	    } catch (Exception e) {
	        StackTraceElement stackEle[] = e.getStackTrace();
	        for (StackTraceElement val : stackEle) {
	            System.out.println(val);
	        }
	        System.exit(0);
	    }
	}

	private AudioFormat getAudioFormat() {
	    float sampleRate = 16000.0F;
	    int sampleInbits = 16;
	    int channels = 1;
	    boolean signed = true;
	    boolean bigEndian = false;
	    return new AudioFormat(sampleRate, sampleInbits, channels, signed, bigEndian);
	}

	class CaptureThread extends Thread {

	    byte tempBuffer[] = new byte[10000];

	    public void run() {

	        byteOutputStream = new ByteArrayOutputStream();
	        stopaudioCapture = false;
	        try {
	            DatagramSocket clientSocket = new DatagramSocket(8788);
	            InetAddress IPAddress = InetAddress.getByName("127.0.0.1");
	            while (!stopaudioCapture) {
	                int cnt = targetDataLine.read(tempBuffer, 0, tempBuffer.length);
	                if (cnt > 0) {
	                    DatagramPacket sendPacket = new DatagramPacket(tempBuffer, tempBuffer.length, IPAddress, 9786);
	                    clientSocket.send(sendPacket);
	                    byteOutputStream.write(tempBuffer, 0, cnt);
	                }
	            }
	            byteOutputStream.close();
	        } catch (Exception e) {
	            System.out.println("CaptureThread::run()" + e);
	            System.exit(0);
	        }
	    }
	}

}
