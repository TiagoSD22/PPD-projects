import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.Socket;

public class Server extends Thread {
        private Socket source, destination;
        private InputStream inputS;
        private InputStreamReader inputSR;
        private BufferedReader bufferedR;

        public Server(Socket source, Socket destination){
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

        public void run(){
            try{

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
}
