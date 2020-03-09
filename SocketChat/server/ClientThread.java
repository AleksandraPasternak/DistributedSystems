import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class ClientThread implements Runnable{

    private Server server;
    private Socket TCPSocket;
    private DatagramSocket UDPSocket;
    private String username;

    public ClientThread (Server server, Socket TCPSocket) throws SocketException {
        this.server = server;
        this.TCPSocket = TCPSocket;
        this.UDPSocket = new DatagramSocket();
    }

    public void run() {

        ExecutorService socketsExecutors = Executors.newFixedThreadPool(1);

        try{
            PrintWriter out = new PrintWriter(TCPSocket.getOutputStream(), true);
            BufferedReader in = new BufferedReader(new InputStreamReader(TCPSocket.getInputStream()));
            out.println("\tWhat's your nick?");
            username = in.readLine();
            while(!server.registerUser(username, TCPSocket)){
                out.println("\tNick is already used. Choose different one.");
                username = in.readLine();
            }
            out.println("\tWelcome to chat " + username);
            server.sendToOtherTCP(TCPSocket, "\t" + username + " entered the chat");

            socketsExecutors.execute(() -> {
                try {
                    while (true) {
                        String msg = in.readLine();
                        if(!msg.equals("exit")) {
                            String msgWithSender = username + " : " + msg;
                            server.sendToOtherTCP(TCPSocket, msgWithSender);
                        } else {
                            break;
                        }
                    }
                } catch (IOException e){
                    e.printStackTrace();
                } finally {
                    try {
                        server.unregisterUser(username, TCPSocket);
                        TCPSocket.close();
                        UDPSocket.close();
                    } catch (IOException e){
                        e.printStackTrace();
                    }
                }
            });
        } catch (IOException e){
            e.printStackTrace();
        } finally {
            try {
                socketsExecutors.shutdown();
                if (!socketsExecutors.awaitTermination(10, TimeUnit.MILLISECONDS)) {
                    socketsExecutors.shutdownNow();
                }
            } catch (InterruptedException e){
                e.printStackTrace();
            }
        }
    }
}
