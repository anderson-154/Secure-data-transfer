package main;

import com.google.gson.Gson;

import java.io.*;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
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
            String file_name = scan.nextLine();
            String file_path = "files/"+file_name;
            File file = new File(file_path);
            byte[] bytes_file = Files.readAllBytes(Paths.get(file_path));

            //Calculate Sha-256
            MessageDigest shaDigest = MessageDigest.getInstance("SHA-256");
            //SHA-1 checksum
            String shaChecksum = encrypter.getFileChecksum(shaDigest, file);
            System.out.println("SHA-256: "+shaChecksum);

            //SendInfo
            bw.write(file_name+"\n");
            bw.write(bytes_file.length+ " Bytes\n");
            bw.write(shaChecksum+"\n");

            //Encrypt file
            byte[] encrypted_bytes = encrypter.encrypt(bytes_file);
            String fileJson = gson.toJson(encrypted_bytes);

            //Send EncryptedFile
            bw.write(fileJson+"\n");
            bw.flush();

            //Close all
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
