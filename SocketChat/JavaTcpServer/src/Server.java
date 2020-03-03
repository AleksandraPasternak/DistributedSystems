import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Server {

    private List<Socket> clients;

    public Server(){
        clients = new ArrayList<>();
    }

    public static void main(String[] args) throws IOException {

        Server server = new Server();
        server.clientsManaging();

    }

    public void clientsManaging() throws IOException{

        System.out.println("JAVA TCP SERVER");
        int portNumber = 12345;
        ServerSocket serverSocket = null;

        ExecutorService executorService = Executors.newCachedThreadPool();

        try{
            serverSocket = new ServerSocket(portNumber);

            while(true){

                Socket clientSocket = serverSocket.accept();
                if(!clients.stream().anyMatch(socket -> socket==clientSocket)) {
                    clients.add(clientSocket);
                    executorService.execute(new ClientThread(this, clientSocket));
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        finally{
            if (serverSocket != null){
                serverSocket.close();
            }
        }
    }

    public void sendToOther(Socket client, String message){

        clients.stream().filter(socket -> client!=socket).forEach( socket -> {
            try{
                PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
                out.println(message);
            } catch (IOException e){
                e.printStackTrace();
            }
        });

    }

}
