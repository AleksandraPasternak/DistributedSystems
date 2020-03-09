import java.io.IOException;
import java.io.PrintWriter;
import java.net.*;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Server {

    private List<Socket> TCPClients;
    private Map<Integer, InetAddress> UDPClients;
    private List<String> usernames;
    private final static int PORT_NUMBER = 12345;

    public Server(){
        TCPClients = new ArrayList<>();
        UDPClients = new HashMap<>();
        usernames = new ArrayList<>();
    }

    public void startChat() throws IOException{
        ExecutorService executorService = Executors.newCachedThreadPool();
        ServerSocket TCPServerSocket = null;

        try{
            TCPServerSocket = new ServerSocket(PORT_NUMBER);
            DatagramSocket UDPSocket = new DatagramSocket(PORT_NUMBER);

            Executors.newSingleThreadExecutor().submit(() -> {
                byte[] receiveBuffer = new byte[1024];

                while (true) {
                    Arrays.fill(receiveBuffer, (byte) 0);
                    DatagramPacket receivePacket = new DatagramPacket(receiveBuffer, receiveBuffer.length);
                    UDPSocket.receive(receivePacket);
                    String msg = new String(receivePacket.getData());
                    if(!UDPClients.keySet().contains(receivePacket.getPort())){
                        UDPClients.put(receivePacket.getPort(), receivePacket.getAddress());
                    } else if (msg.startsWith("exit")){
                        UDPClients.remove(receivePacket.getPort());
                    } else {
                        sendToOtherUDP(receivePacket);
                    }
                }
            });

            while(true){
                Socket TCPSocket = TCPServerSocket.accept();
                executorService.execute(new ClientThread(this, TCPSocket));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        finally{
            if (TCPServerSocket != null){
                TCPServerSocket.close();
            }
        }
    }

    public void sendToOtherTCP(Socket client, String message){
        TCPClients.stream().filter(socket -> socket != client).forEach( socket -> {
            try {
                PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
                out.println(message);
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }

    public void sendToOtherUDP(DatagramPacket packet) throws IOException{
        String msg = new String(packet.getData());
        byte[] usernameBytes = msg.getBytes();
        DatagramSocket sendingSocket = new DatagramSocket();
        UDPClients.keySet().stream().filter( port -> port != packet.getPort()).forEach( port -> {
            DatagramPacket sendPacket = new DatagramPacket(usernameBytes,
                    usernameBytes.length, packet.getAddress(), port);
            try {
                sendingSocket.send(sendPacket);
            } catch (IOException e){
                e.printStackTrace();
            }
        });
    }

    public boolean registerUser(String username, Socket TCPSocket) {
        if (!usernames.contains(username)) {
            usernames.add(username);
            TCPClients.add(TCPSocket);
            return true;
        } else {
            return false;
        }
    }

    public void unregisterUser(String username, Socket socket){
        TCPClients.remove(socket);
        usernames.remove(username);
        sendToOtherTCP(socket, "\t" + username + " left the chat");
    }

    public static void main(String [] args) throws IOException{
        System.out.println("JAVA TCP/UDP SERVER");
        new Server().startChat();
    }
}
