import java.net.Socket;

public class ClientThread implements Runnable{

    private Server server;
    private Socket socket;

    public ClientThread (Server server, Socket socket){
        this.server = server;
        this.socket = socket;
    }

    public void run() {

        while(!socket.isClosed()) {
           // try {
                //BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                //String msg = in.readLine();
                //System.out.println(msg);
                //server.sendToOther(socket, msg);
                System.out.println(1);
            /*}
            catch (IOException e){
                e.printStackTrace();
            }*/
        }
    }
}
