package comm;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

public class Server {

    public static void main(String args[]){

        try {
            ServerSocket server = new ServerSocket(5000);
            System.out.println("Esperando conexion");
            Socket socket = server.accept();

            OutputStream os = socket.getOutputStream();
            InputStream is = socket.getInputStream();

            BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(os));
            BufferedReader br = new BufferedReader(new InputStreamReader(is));

            System.out.println("Conectado");
        }catch(IOException e){
            e.printStackTrace();
        }
    }
}
