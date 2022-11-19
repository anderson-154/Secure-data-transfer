package main;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.security.*;
import java.security.spec.KeySpec;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;

public class RSAAsymetricCrypto {

    //Singleton
    private static RSAAsymetricCrypto instance;

    public static RSAAsymetricCrypto getInstance() {
        if(instance==null){
            instance = new RSAAsymetricCrypto();
        }
        return instance;
    }

    //Class

    //Constructor
    private  RSAAsymetricCrypto () {
        //Load keys
        try {
            publicKey = loadPublicKey("RSAKeys/publickey.dat");
            privateKey = loadPrivateKey("RSAKeys/privatekey.dat");
            cipher = Cipher.getInstance("RSA");
        } catch (Exception e) {
            generatePairKeys();
        }
    }

    //Attributes
    private PublicKey publicKey;
    private PrivateKey privateKey;
    private Cipher cipher;

    //Generate keys
    public void generatePairKeys() {
        KeyPairGenerator keyPairGenerator = null;
        try {
            keyPairGenerator = KeyPairGenerator.getInstance("RSA");
            KeyPair keyPair = keyPairGenerator.generateKeyPair();
            publicKey = keyPair.getPublic();
            privateKey = keyPair.getPrivate();

            //Save keys
            saveKey(publicKey, "RSAKeys/publickey.dat");
            saveKey(privateKey, "RSAKeys/privatekey.dat");
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    //Encrypt
    public byte[] encrypt(byte[] buffer){
        try {
            cipher.init(Cipher.ENCRYPT_MODE, publicKey);
            return cipher.doFinal(buffer);
        } catch (InvalidKeyException | IllegalBlockSizeException | BadPaddingException e) {
            throw new RuntimeException(e);
        }
    }

    //Decrypt
    public byte[] decrypt(byte[] buffer){
        try {
            cipher.init(Cipher.DECRYPT_MODE, privateKey);
            return cipher.doFinal(buffer);
        } catch (InvalidKeyException | IllegalBlockSizeException | BadPaddingException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * This method calculates the hash from the corresponding file
     * {@link <a href="https://howtodoinjava.com/java/java-security/sha-md5-file-checksum-hash/">...</a>}
     * @author Lokesh Gupta
     * @param digest the MessageDigest which will be used
     * @param file the File which we will get the hash from
     * @return string
     * @throws IOException exception thrown in case any I/O operation failed or was interrupted
     */
    public String getFileChecksum(MessageDigest digest, File file) throws IOException
    {
        //Get file input stream for reading the file content
        FileInputStream fis = new FileInputStream(file);

        //Create byte array to read data in chunks
        byte[] byteArray = new byte[1024];
        int bytesCount = 0;

        //Read file data and update in message digest
        while ((bytesCount = fis.read(byteArray)) != -1) {
            digest.update(byteArray, 0, bytesCount);
        };

        //close the stream; We don't need it now.
        fis.close();

        //Get the hash's bytes
        byte[] bytes = digest.digest();

        //This bytes[] has bytes in decimal format;
        //Convert it to hexadecimal format
        StringBuilder sb = new StringBuilder();
        for (byte aByte : bytes) {
            sb.append(Integer.toString((aByte & 0xff) + 0x100, 16).substring(1));
        }

        //return complete hash
        return sb.toString();
    }

    //Getters
    public PublicKey getPublicKey(){
        return publicKey;
    }

    private static PublicKey loadPublicKey(String fileName) throws Exception {
        FileInputStream fis = new FileInputStream(fileName);
        int numBtyes = fis.available();
        byte[] bytes = new byte[numBtyes];
        fis.read(bytes);
        fis.close();

        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        KeySpec keySpec = new X509EncodedKeySpec(bytes);
        PublicKey keyFromBytes = keyFactory.generatePublic(keySpec);
        return keyFromBytes;
    }

    private static PrivateKey loadPrivateKey(String fileName) throws Exception {
        FileInputStream fis = new FileInputStream(fileName);
        int numBtyes = fis.available();
        byte[] bytes = new byte[numBtyes];
        fis.read(bytes);
        fis.close();

        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        KeySpec keySpec = new PKCS8EncodedKeySpec(bytes);
        return keyFactory.generatePrivate(keySpec);
    }

    private static void saveKey(Key key, String fileName) throws Exception {
        byte[] publicKeyBytes = key.getEncoded();
        FileOutputStream fos = new FileOutputStream(fileName);
        fos.write(publicKeyBytes);
        fos.close();
    }
}