import java.io.*;
import java.net.Socket;

public class ServerClientHandler extends Thread {
    private Socket source, destination;
    private InputStream inputS;
    private InputStreamReader inputSR;
    private BufferedReader bufferedR;

    public ServerClientHandler(Socket source, Socket destination) {
        this.source = source;
        this.destination = destination;
        try {
            inputS = this.source.getInputStream();
            inputSR = new InputStreamReader(inputS);
            bufferedR = new BufferedReader(inputSR);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void sendMessageToClient(BufferedWriter bfw, String msg){
        try {
            bfw.write(msg);
            bfw.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void run() {
        try {
            OutputStream out = this.destination.getOutputStream();
            Writer outWriter = new OutputStreamWriter(out);
            BufferedWriter bfw = new BufferedWriter(outWriter);
            String msg = this.bufferedR.readLine();

            while (!("Desisto".equalsIgnoreCase(msg)) && (msg != null)) {
                msg = this.bufferedR.readLine();
                System.out.println("Mensagem recebida por " +
                        this.source.getInetAddress().getHostAddress() +
                        ": " + msg);
                sendMessageToClient(bfw, msg);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
