package main;

import com.google.gson.Gson;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

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
            System.out.println("Key sent\n");

            //Receive info
            String file_name = br.readLine();
            String file_size = br.readLine();
            String hash = br.readLine();

            //Prepare to receive file
            String pathToSave = "storage/"+file_name;
            File file = new File(pathToSave);
            FileOutputStream fos = new FileOutputStream(file);

            //Receive file and decrypt
            System.out.println("Receiving an decrypting file...");
            byte[] buffer = new byte[256];
            int readBytes;
            while((readBytes = is.read(buffer))!=-1){
                byte[] bytes_file = encrypter.decrypt(buffer,readBytes);
                System.out.println(bytes_file.length);
                fos.write(bytes_file);
            }
            System.out.println("Success\n");

            System.out.println("Calculating hash...\n");
            //Calculate Sha-256
            MessageDigest shaDigest = MessageDigest.getInstance("SHA-256");
            //SHA-1 checksum
            String currentHash = encrypter.getFileChecksum(shaDigest, file);
            String state = currentHash.equals(hash) ?  "CORRECT!" : "CORRUPT!";

            System.out.println("FILE INFO RECEIVED:\n" +
                    "Name: "+file_name+"\n" +
                    "Size: "+file_size+"\n" +
                    "Path: "+pathToSave+"\n"+
                    "Hash algorithm: SHA-256\n" +
                    "Current Hash: "+currentHash +"\n" +
                    "Expected Hash: "+hash+"\n" +
                    "STATE: "+state);

            //Close all
            fos.close();
            bw.close();
            br.close();
            is.close();
            os.close();
            socket.close();

        }catch(IOException e){
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }
}
