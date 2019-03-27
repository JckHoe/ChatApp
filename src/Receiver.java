import java.io.*;
import java.net.*;
import javax.sound.sampled.*;

public class Receiver {

ByteArrayOutputStream byteOutputStream;
AudioFormat format;
TargetDataLine targetDataLine;
AudioInputStream InputStream;
SourceDataLine source;

public static void main (String [] args){
	new Receiver().runREC();
}

public Receiver(){
	System.out.println("Running\n");
}

private AudioFormat getAudioFormat() {
    float sampleRate = 16000.0F;
    int sampleInbits = 16;
    int channels = 1;
    boolean signed = true;
    boolean bigEndian = false;
    return new AudioFormat(sampleRate, sampleInbits, channels, signed, bigEndian);
}

public void runREC() {
    try {
        DatagramSocket serverSocket = new DatagramSocket(9786);
        byte[] receiveData = new byte[10000];
        while (true) {
            DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);
            serverSocket.receive(receivePacket);
            try {
                byte audioData[] = receivePacket.getData();
                InputStream byteInputStream = new ByteArrayInputStream(audioData);
                AudioFormat format = getAudioFormat();
                InputStream = new AudioInputStream(byteInputStream, format, audioData.length / format.getFrameSize());
                DataLine.Info dataLineInfo = new DataLine.Info(SourceDataLine.class, format);
                source = (SourceDataLine) AudioSystem.getLine(dataLineInfo);
                source.open(format);
                source.start();
                Thread playThread = new Thread(new PlayThread());
                playThread.start();
            } catch (Exception e) {
                System.out.println(e);
                System.exit(0);
            }
        }
    } catch (Exception e) {
        e.printStackTrace();
    }
}

class PlayThread extends Thread {

    byte tempBuffer[] = new byte[10000];

    public void run() {
        try {
            int cnt;
            while ((cnt = InputStream.read(tempBuffer, 0, tempBuffer.length)) != -1) {
                if (cnt > 0) {
                    source.write(tempBuffer, 0, cnt);
                }
            }
            source.drain();
            source.close();
        } catch (Exception e) {
            System.out.println(e);
            System.exit(0);
        }
    }
}
}
