package main;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

public class Server {

    public static void main(String[] args){

        try {
            //Socket
            ServerSocket server = new ServerSocket(5000);

            //Thread blocked here
            System.out.println("Waiting for connection");
            Socket socket = server.accept();
            System.out.println("Connected");

            // I/O of socket
            OutputStream os = socket.getOutputStream();
            InputStream is = socket.getInputStream();

            //I/O of text
            BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(os));
            BufferedReader br = new BufferedReader(new InputStreamReader(is));

            //Prepare to receive file
            String file_param = "file1.pdf";
            String pathToSave = "C:/Users/Ben/Documents/Programas Java/Ciberseguridad/Secure-data-transfer/storage/"+file_param;
            File file = new File(pathToSave);
            FileOutputStream fos = new FileOutputStream(file);

            //Send param
            bw.write(file_param);
            bw.flush();
            System.out.println("Param sent ("+file_param+")");

            //Wait for file
            byte[] buffer = new byte[128];
            int readBytes;
            while((readBytes = is.read(buffer)) != -1){
                fos.write(buffer, 0, readBytes);
            }

            //Close all
            fos.close();
            bw.close();
            br.close();
            is.close();
            os.close();
            socket.close();

        }catch(IOException e){
            e.printStackTrace();
        }
    }
}
