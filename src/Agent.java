import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Calendar;

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
import javafx.geometry.Insets;
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

public class Agent extends Application{
	private TextArea msg = new TextArea();
	private TextArea msg2 = new TextArea();
	private NetworkConnection connection = createClient();
	private ArrayList<String> session = new ArrayList<>();
	private ArrayList<String> session2 = new ArrayList<>();
	private String [] kickList = new String[2];
	private GridPane popup = new GridPane();
	private GridPane popup2 = new GridPane();
	
	private GridPane createContent(Stage primaryStage) {
		TextField input = new TextField();
		PasswordField pwd = new PasswordField();
		Label error = new Label("");
		Button Submit = new Button("Submit");
		
		input.setOnAction(event->{
			error.setText("");
			boolean isServerOffline = false;
			String temp = "Agent;"+input.getText() + ";" + pwd.getText();
			input.clear();
			pwd.clear();
			try {
				connection.send(temp);
			} catch (Exception e) {
				isServerOffline = true;
			}
			if(connection.isLogin() == true){
				Scene scene = new Scene(chatPage(),1200,1200);
				scene.getStylesheets().add("Button.css");
				primaryStage.setScene(scene);
			}
			else if(isServerOffline == false){
				error.setText("Login Error");
			}
			else if(isServerOffline == true){
				error.setText("Server is Offline\nPlease Restart Client");
			}
		});
		
		pwd.setOnAction(event->{
			error.setText("");
			boolean isServerOffline = false;
			String temp = "Agent;"+input.getText() + ";" + pwd.getText();
			input.clear();
			pwd.clear();
			try {
				connection.send(temp);
			} catch (Exception e) {
				isServerOffline = true;
			}
			if(connection.isLogin() == true){
				Scene scene = new Scene(chatPage(),1200,1200);
				scene.getStylesheets().add("Button.css");
				primaryStage.setScene(scene);
			}
			else if(isServerOffline == false){
				error.setText("Login Error");
			}
			else if(isServerOffline == true){
				error.setText("Server is Offline\nPlease Restart Client");
			}
		});
		
		Submit.setOnAction(event ->{
			error.setText("");
			boolean isServerOffline = false;
			String temp = "Agent;"+input.getText() + ";" + pwd.getText();
			input.clear();
			pwd.clear();
			try {
				connection.send(temp);
			} catch (Exception e) {
				isServerOffline = true;
			}
			if(connection.isLogin() == true){
				Scene scene = new Scene(chatPage(),1200,1200);
				scene.getStylesheets().add("Button.css");
				primaryStage.setScene(scene);
			}
			else if(isServerOffline == false){
				error.setText("Login Error");
			}
			else if(isServerOffline == true){
				error.setText("Server is Offline\nPlease Restart Client");
			}
		});
		
		Submit.setId("iphone");
		GridPane.setHalignment(Submit, HPos.RIGHT);
		GridPane root = new GridPane();
		root.setVgap(10);
		root.setHgap(20);
		root.add(new Label("Username"), 0, 0);
		root.add(input, 1, 0);
		root.add(new Label("Password"), 0, 1);
		root.add(pwd, 1, 1);
		root.add(Submit, 1, 2);
		GridPane pane = new GridPane();
		pane.setAlignment(Pos.CENTER);
		pane.setVgap(15);
		Label title = new Label("Agent Login Screen");
		title.setFont(Font.font("Arial", FontWeight.BLACK, 15));
		pane.add(title, 0, 0);
		pane.add(root, 0, 1);
		pane.add(error, 0, 2);
		return pane;
	}
	
	public GridPane chatPage (){
		GridPane pane = new GridPane();
		pane.setHgap(20);
		pane.setAlignment(Pos.CENTER);
		popup.setAlignment(Pos.CENTER);
		popup2.setAlignment(Pos.CENTER);
		msg.appendText("-----THIS IS CHAT ROOM 1-----\n");
		msg.setPrefHeight(300);
		msg.setWrapText(true);
		GridPane root = new GridPane();
		root.setAlignment(Pos.CENTER);
		msg.setEditable(false);
		root.add(msg, 0, 0);
		TextField txt = new TextField();
		root.add(txt, 0, 1);
		Button btn = new Button("Send");
		btn.setId("iphone");
		
		
		txt.setOnAction(event->{
			if(connection.isSlotFree[0] == false && txt.getText().length()>0){
				String temp = txt.getText()+"#"+connection.client[0];
				try {
					connection.send(temp);
				} catch (Exception e) {
					
				}
				msg.appendText(connection.getClientID()+" : "+txt.getText()+"\n");
				session.add(connection.getClientID()+" : "+txt.getText());
				txt.clear();
			}else {
				msg.appendText("Error, User has already Disconnected\n");
				txt.clear();
			}
		});
		
		btn.setOnAction(event->{
			if(connection.isSlotFree[0] == false){
				String temp = txt.getText()+"#"+connection.client[0];
				try {
					connection.send(temp);
				} catch (Exception e) {
					
				}
				msg.appendText(connection.getClientID()+" : "+txt.getText()+"\n");
				session.add(connection.getClientID()+" : "+txt.getText());
				txt.clear();
			}else {
				msg.appendText("Error, User has already Disconnected\n");
				txt.clear();
			}
		});
		
		popup.setVgap(15);
		popup.setVisible(false);
		popup.add(new Label("User Has Left"), 0, 0);
		popup.add(new Label("Save Chat?"), 0, 1);
		popup.setStyle("-fx-background-color:yellow");
		Button clear = new Button("Save");
		clear.setOnAction(event->{
			if(connection.isSlotFree[0] == true){
				saveFile1();
			}
			msg.clear();
			msg.appendText("-----THIS IS CHAT ROOM 1-----\n");
			popup.setVisible(false);
			txt.setVisible(true);
		});
		popup.add(clear, 0, 2);
		clear.setId("shinyOrange");
		clear.setAlignment(Pos.CENTER);
		Button cancel = new Button("Cancel");
		cancel.setOnAction(event->{
			msg.clear();
			msg.appendText("-----THIS IS CHAT ROOM 1-----\n");
			popup.setVisible(false);
			txt.setVisible(true);
		});
		cancel.setId("shinyOrange");
		cancel.setAlignment(Pos.CENTER);
		popup.add(cancel, 0, 3);
		root.add(popup, 0, 0);
		
		Button kick = new Button("Remove Room1 User");
		kick.setOnAction(event->{
			if(connection.isSlotFree[0] == false && !connection.client[0].equals(kickList[0])){
				String temp = "KICK"+"#"+connection.client[0];
				try {
					connection.send(temp);
				} catch (Exception e) {
					
				}
				msg.appendText("User Has Been Kicked\n");
				kickList[0] = connection.client[0];
				txt.setVisible(false);
			}
			connection.isSlotFree[0] = true;
			connection.clientCounter--;
			popup.setVisible(true);
		});
		kick.setId("shinyOrange");
		
		Button exit = new Button("Close Program");
		exit.setOnAction(event->{
			String temp = "Exit";
			try {
				connection.send(temp);
			} catch (Exception e) {
				
			}
			System.exit(1);
		});
		exit.setAlignment(Pos.BOTTOM_RIGHT);
		exit.setId("shinyOrange");
		
		GridPane btnPane = new GridPane();
		btnPane.setPadding(new Insets(20, 20, 20, 20));
		btnPane.setVgap(20);
		btnPane.setHgap(60);
		
		Button start = new Button("Start Voice 1");
		start.setId("iphone");
		start.setOnAction(event ->{
			Label talk = new Label("Talking...");
			start.setVisible(false);
			btnPane.add(talk, 0,2);
			UDPSender send = new UDPSender();
		});
		
		btnPane.add(btn, 0, 0);
		btnPane.add(exit, 1, 1);
		btnPane.add(kick, 0, 1);
		btnPane.add(start, 0, 2);
		root.add(btnPane, 0, 2);
		
		msg2.appendText("-----THIS IS CHAT ROOM 2-----\n");
		msg2.setPrefHeight(300);
		msg2.setWrapText(true);
		GridPane root2 = new GridPane();
		root2.setAlignment(Pos.CENTER);
		msg2.setEditable(false);
		root2.add(msg2, 0, 0);
		TextField txt2 = new TextField();
		root2.add(txt2, 0, 1);
		Button btn2 = new Button("Send");
		btn2.setId("iphone");
		
		txt2.setOnAction(event->{
			if(connection.isSlotFree[1] == false && txt2.getText().length()>0){
				String temp = txt2.getText()+"#"+connection.client[1];
				try {
					connection.send(temp);
				} catch (Exception e) {
					
				}
				msg2.appendText(connection.getClientID()+" : "+txt2.getText()+"\n");
				session2.add(connection.getClientID()+" : "+txt2.getText());
				txt2.clear();
			}else {
				msg2.appendText("Error, User has already Disconnected\n");
				txt2.clear();
			}
		});
		
		btn2.setOnAction(event->{
			if(connection.isSlotFree[1] == false){
				String temp = txt2.getText()+"#"+connection.client[1];
				try {
					connection.send(temp);
				} catch (Exception e) {
					
				}
				msg2.appendText(connection.getClientID()+" : "+txt2.getText()+"\n");
				session2.add(connection.getClientID()+" : "+txt2.getText());
				txt2.clear();
			}else {
				msg2.appendText("Error, User has already Disconnected\n");
				txt2.clear();
			}
		});
		
		popup2.setVgap(15);
		popup2.setVisible(false);
		popup2.add(new Label("User Has Left"), 0, 0);
		popup2.add(new Label("Save Chat?"), 0, 1);
		popup2.setStyle("-fx-background-color:yellow");
		Button clear2 = new Button("Save");
		clear2.setOnAction(event->{
			if(connection.isSlotFree[1] == true){
				saveFile2();
			}
			msg2.clear();
			msg2.appendText("-----THIS IS CHAT ROOM 2-----\n");
			popup2.setVisible(false);
			txt2.setVisible(true);
		});
		popup2.add(clear2, 0, 2);
		clear2.setId("shinyOrange");
		clear2.setAlignment(Pos.CENTER);
		Button cancel2 = new Button("Cancel");
		cancel2.setOnAction(event->{
			msg2.clear();
			msg2.appendText("-----THIS IS CHAT ROOM 2-----\n");
			popup2.setVisible(false);
			txt2.setVisible(true);
			connection.isSlotFree[1] = true;
			connection.clientCounter--;
		});
		cancel2.setId("shinyOrange");
		cancel2.setAlignment(Pos.CENTER);
		popup2.add(cancel2, 0, 3);
		root2.add(popup2, 0, 0);
		
		Button kick2 = new Button("Remove Room2 User");
		kick2.setOnAction(event->{
			if(connection.isSlotFree[1] == false && !connection.client[1].equals(kickList[1])){
				String temp = "KICK"+"#"+connection.client[1];
				try {
					connection.send(temp);
				} catch (Exception e) {
					
				}
				msg2.appendText("User Has Been Kicked\n");
				kickList[1] = connection.client[1];
			}
			txt2.setVisible(false);
			connection.isSlotFree[1] = true;
			connection.clientCounter--;
			popup2.setVisible(true);
		});
		kick2.setId("shinyOrange");
		
		GridPane btnPane2 = new GridPane();
		btnPane2.setPadding(new Insets(20, 20, 20, 20));
		btnPane2.setVgap(20);
		btnPane2.setHgap(60);

		Button start2 = new Button("Start Voice 1");
		start2.setId("iphone");
		start2.setOnAction(event ->{
			Label talk = new Label("Talking...");
			start2.setVisible(false);
			btnPane2.add(talk,0,2);
			UDPSender send = new UDPSender();
		});
	
		btnPane2.add(btn2, 0, 0);
		btnPane2.add(kick2, 0, 1);
		btnPane2.add(start2, 0, 2);
		root2.add(btnPane2, 0, 2);
		
		Label title = new Label("WELCOME "+connection.getClientID());
		title.setFont(Font.font("Times New Roman", FontWeight.BOLD, 20));
		pane.add(title, 0, 0);
		pane.add(root, 0, 1);
		pane.add(root2, 1, 1);
		return pane;
	}
	
	public void saveFile1(){
		msg.clear();
		msg.appendText("-----THIS IS CHAT ROOM 1-----\n");
		String temp;
		Calendar cal = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("hh;mm;ssa");
        LocalDate date = LocalDate.now();
        temp = sdf.format(cal.getTime()).toString();
		File file = new File(temp+"_"+date+"_"+connection.client[0]+".txt");
		PrintWriter output = null;
		try {
			output = new PrintWriter(file);
			for(int i=0;i<session.size();i++){
				output.println(session.get(i));
			}
		} catch (FileNotFoundException e) {
			
		}
		output.close();
		session.clear();
	}
	
	public void saveFile2(){
		msg2.clear();
		msg2.appendText("-----THIS IS CHAT ROOM 2-----\n");
		String temp;
		Calendar cal = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("hh;mm;ssa");
        LocalDate date = LocalDate.now();
        temp = sdf.format(cal.getTime()).toString();
		File file = new File(temp+"_"+date+"_"+connection.client[1]+".txt");
		PrintWriter output = null;
		try {
			output = new PrintWriter(file);
			for(int i=0;i<session2.size();i++){
				output.println(session2.get(i));
			}
		} catch (FileNotFoundException e) {
			
		}
		output.close();
		session2.clear();
	}
	
	public GridPane menu (Stage primaryStage){
		GridPane root = new GridPane();
		root.setAlignment(Pos.CENTER);
		root.setVgap(20);
		Label title = new Label("Welcome to Agent Client Program for CVT!");
		title.setFont(Font.font("Arial", FontWeight.BOLD, 20));
		root.add(title, 0, 0);
		Button btn = new Button("Login");
		btn.setOnAction(event -> {
			Scene scene = new Scene(createContent(primaryStage),600,600);
			scene.getStylesheets().add("Button.css");
			primaryStage.setScene(scene);
		});
		GridPane.setHalignment(btn, HPos.CENTER);
		btn.setId("iphone");
		root.add(btn, 0, 1);
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
				String [] temp;
				temp = data.toString().split(":");
				if(temp[0].equals(connection.client[0])){
					if(temp[1].equals(" =====Disconnected=====")){
						popup.setVisible(true);
					}
					msg.appendText(data.toString() + "\n");
					session.add(data.toString());
				}
				if(temp[0].equals(connection.client[1])){
					if(temp[1].equals(" =====Disconnected=====")){
						popup2.setVisible(true);
					}
					msg2.appendText(data.toString() + "\n");
					session2.add(data.toString());
				}
				if(temp[0].equals("AUTO") && temp[1].equals(connection.client[0])){
					msg.appendText("An Automatic Message Has Been Sent\n");
				}
				if(temp[0].equals("AUTO") && temp[1].equals(connection.client[1])){
					msg2.appendText("An Automatic Message Has Been Sent\n");
				}
			});
		});
	}
	
	public static void main (String [] args){
		launch(args);
	}
}

@SuppressWarnings("serial")
class UDPSender extends JFrame {

	boolean stopaudioCapture = false;
	ByteArrayOutputStream byteOutputStream;
	AudioFormat adFormat;
	TargetDataLine targetDataLine;
	AudioInputStream InputStream;
	SourceDataLine sourceLine;
	Graphics g;

	public UDPSender() {
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
	            DatagramSocket clientSocket = new DatagramSocket(8786);
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
