package main;

import com.google.gson.Gson;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
public class Server {

    public static void main(String[] args){

        try {
            //Socket
            ServerSocket server = new ServerSocket(5000);

            //TCP Connection
            System.out.println("Waiting for connection");
            Socket socket = server.accept();
            System.out.println("Connected");

            // I/O of socket
            OutputStream os = socket.getOutputStream();
            InputStream is = socket.getInputStream();

            //I/O of text
            BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(os));
            BufferedReader br = new BufferedReader(new InputStreamReader(is));

            //Initialize cypher and keys
            RSAAsymetricCrypto encrypter = RSAAsymetricCrypto.getInstance();

            //Send Public Key
            System.out.println("Sending public key...");
            Gson gson = new Gson();
            String key_bytes = gson.toJson(encrypter.getPublicKey().getEncoded());
            bw.write(key_bytes+"\n");
            bw.flush();
            System.out.println("Key sent");

            //Prepare to receive file
            String file_param = "file1.pdf";
            String pathToSave = "storage/"+file_param;
            File file = new File(pathToSave);
            FileOutputStream fos = new FileOutputStream(file);

            //Wait for file...
            byte[] buffer = new byte[256];
            int decryptedReadBytes;
            while(is.read(buffer) != -1){
                //Decrypt
                byte[] decrypted = encrypter.decrypt(buffer);
                decryptedReadBytes = decrypted.length;

                //Write
                fos.write(buffer, 0, decryptedReadBytes);
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
