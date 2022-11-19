package main;

import java.io.*;
import java.net.Socket;
import java.util.Scanner;

public class Client {

    public static void main(String[] args){
        try{
            //Starting TCP Connection
            System.out.println("Sending request...");
            Socket socket = new Socket("127.0.0.1", 5000);
            System.out.println("Connected");

            //Socket I/O
            OutputStream os = socket.getOutputStream();
            InputStream is = socket.getInputStream();

            //Text I/O
            BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(os));
            BufferedReader br = new BufferedReader(new InputStreamReader(is));

            //Waiting for Server RSA public key

            //Waiting for param...
            System.out.println("Waiting for param...");
            Scanner scan = new Scanner(System.in);
            String file_param = scan.nextLine();

            //Prepare to send file
            String file_path = "C:Users/Ben/Documents/Programas Java/Ciberseguridad/Secure-data-transfer-client/files/"+file_param;
            File file = new File(file_path);
            FileInputStream fis = new FileInputStream(file);

            //Send file
            byte[] buffer = new byte[128];
            int readBytes;
            while((readBytes = fis.read(buffer))!=-1){
                os.write(buffer, 0, readBytes);
            }

            //Close all
            fis.close();
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
