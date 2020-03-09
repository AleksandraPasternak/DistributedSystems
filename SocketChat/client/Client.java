import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.concurrent.*;

public class Client {

    private final static int PORT = 12345;
    private final static int MULTIPORT = 6789;
    private final static String HOSTNAME = "localhost";
    private final static String MULTI_IP = "224.0.0.2";
    private static final Path PATH_TO_ASCII = Paths.get(".\\resources\\asciiart.txt");

    private Socket TCPSocket;
    private DatagramSocket UDPSocket;
    private MulticastSocket multicastSocket;
    private InetAddress inetAddress;
    private InetAddress multiAddress;

    private ExecutorService executorService;
    private Semaphore terminalWritingAccess;
    private String username;

    public Client(){
        this.terminalWritingAccess = new Semaphore(1);
        this.executorService = Executors.newFixedThreadPool(4);
        try{
            this.inetAddress = InetAddress.getByName(HOSTNAME);
            this.multiAddress = InetAddress.getByName(MULTI_IP);
            this.TCPSocket = new Socket(HOSTNAME, PORT);
            this.UDPSocket = new DatagramSocket();
            this.multicastSocket = new MulticastSocket(MULTIPORT);
        } catch (IOException e){
            e.printStackTrace();
        }
    }

    public void chatManaging(){
            executorService.execute(() -> {
                try {
                    String userMessage;
                    PrintWriter out = new PrintWriter(TCPSocket.getOutputStream(), true);
                    BufferedReader terminalStream = new BufferedReader(new InputStreamReader(System.in));
                    username = terminalStream.readLine();
                    out.println(username);
                    UDPsend(username.getBytes());
                    multicastSocket.joinGroup(multiAddress);

                    while (true) {
                        userMessage = terminalStream.readLine();
                        if (!userMessage.isEmpty()) {
                            if (userMessage.equals("U")) {
                                String usernameToSend = username + " : \n";
                                byte[] usernameBytes = usernameToSend.getBytes();
                                byte[] asciiArt = Files.readAllBytes(PATH_TO_ASCII);
                                int length = usernameBytes.length + asciiArt.length;
                                byte[] sendBuffer = new byte[length];
                                System.arraycopy(usernameBytes, 0, sendBuffer, 0, usernameBytes.length);
                                System.arraycopy(asciiArt, 0, sendBuffer, usernameBytes.length, asciiArt.length);

                                UDPsend(sendBuffer);
                                printOnTerminal("You : \n" + new String(asciiArt));
                            } else if (userMessage.equals("M")) {
                                String multicastMsg = terminalStream.readLine();
                                printOnTerminal("You : " + multicastMsg);
                                multicastMsg = username + " : " + multicastMsg;
                                DatagramPacket packet = new DatagramPacket(multicastMsg.getBytes(), multicastMsg.length(),
                                        multiAddress, MULTIPORT);
                                multicastSocket.send(packet);
                            } else if (userMessage.equals("exit")){
                                UDPsend(userMessage.getBytes());
                                out.println(userMessage);
                                throw new IOException("exit client");
                            }
                            else {
                                out.println(userMessage);
                                printOnTerminal("You : " + userMessage);
                            }
                        }
                    }
                } catch (IOException e) {
                    //e.printStackTrace();
                } finally {
                    try{
                        UDPSocket.close();
                        TCPSocket.close();
                        multicastSocket.leaveGroup(multiAddress);
                        multicastSocket.close();
                        executorService.shutdown();
                        if (!executorService.awaitTermination(100, TimeUnit.MILLISECONDS)) {
                            executorService.shutdownNow();
                        }
                    } catch (InterruptedException | IOException e) {
                        executorService.shutdownNow();
                    }
                }
            });

            executorService.execute(() -> {
                try {
                    byte[] receiveBuffer = new byte[1024];

                    while (true) {
                        Arrays.fill(receiveBuffer, (byte) 0);
                        DatagramPacket receivePacket = new DatagramPacket(receiveBuffer, receiveBuffer.length);
                        UDPSocket.receive(receivePacket);
                        byte[] data = receivePacket.getData();
                        String msg = new String(data);
                        if (!msg.startsWith(username)) {
                            printOnTerminal(new String(data));
                        }
                    }
                } catch (IOException e) {
                    //e.printStackTrace();
                }
            });

            executorService.execute(() -> {
                try {
                    byte[] receiveBuffer = new byte[1024];

                    while (true) {
                        Arrays.fill(receiveBuffer, (byte) 0);
                        DatagramPacket receivePacket = new DatagramPacket(receiveBuffer, receiveBuffer.length);
                        multicastSocket.receive(receivePacket);
                        byte[] data = receivePacket.getData();
                        String msg = new String(data);
                        if (!msg.startsWith(username)) {
                            printOnTerminal(new String(data));
                        }
                    }
                } catch (IOException e) {
                    //e.printStackTrace();
                }
            });

            executorService.execute(() -> {
                try {
                    String line;
                    BufferedReader in = new BufferedReader(new InputStreamReader(TCPSocket.getInputStream()));

                    while ( (line = in.readLine()) != null) {
                        printOnTerminal(line);
                        String msg = in.readLine();
                        printOnTerminal(msg);
                    }
                } catch (IOException e) {
                    //e.printStackTrace();
                }
            });
    }

    public void UDPsend(byte[] usernameBytes){
        try{
            DatagramPacket sendPacket = new DatagramPacket(usernameBytes, usernameBytes.length, inetAddress, PORT);
            UDPSocket.send(sendPacket);
        } catch (IOException e){
            e.printStackTrace();
        }
    }

    public void printOnTerminal(String msg){
        try {
            terminalWritingAccess.acquire();
            System.out.println(msg);
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            terminalWritingAccess.release();
        }
    }

    public static void main(String[] args){
        new Client().chatManaging();
    }
}
