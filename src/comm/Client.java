package comm;

import java.io.*;
import java.net.Socket;

public class Client {

    public static void main(String args[]){
        try{
            //Empezando la conexion TCP
            System.out.println("Enviando solicitud...");

            //Socket es la puerta de conexion o comunicacion
            Socket socket = new Socket("127.0.0.1", 5000);

            System.out.println("Conectados");

            OutputStream os = socket.getOutputStream();
            InputStream is = socket.getInputStream();

            BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(os));
            BufferedReader br = new BufferedReader(new InputStreamReader(is));

        }catch(IOException e){
            e.printStackTrace();
        }

    }
}
