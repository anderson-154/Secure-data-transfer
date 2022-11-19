package main;

import com.google.gson.Gson;

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

            //Waiting for Server RSA public key...
            System.out.println("Receiving Key...");
            String line = br.readLine();
            System.out.println("Success");

            ////Rebuilt object PublicKey
            Gson gson = new Gson();
            byte[] bytes_key = gson.fromJson(line, byte[].class);
            RSAAsymetricCrypto encrypter = new RSAAsymetricCrypto();
            encrypter.rebuiltKey(bytes_key);

            //Waiting for param...
            System.out.println("Waiting for param...");
            Scanner scan = new Scanner(System.in);
            String file_param = scan.nextLine();

            //Prepare to send file
            String file_path = "files/"+file_param;
            File file = new File(file_path);
            FileInputStream fis = new FileInputStream(file);

            //Send file
            byte[] buffer = new byte[64];

            while(fis.read(buffer)!=-1){
                byte[] bufferEncrypted = encrypter.encrypt(buffer);
                os.write(bufferEncrypted, 0, bufferEncrypted.length);
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
