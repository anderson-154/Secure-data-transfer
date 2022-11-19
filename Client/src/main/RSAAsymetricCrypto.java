package main;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.security.*;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.KeySpec;
import java.security.spec.X509EncodedKeySpec;

import com.google.gson.Gson;

/**
 * Ejemplo sencillo de encriptado/desencriptado con algoritmo RSA. Se comenta
 * tambien como guardar las claves en fichero y recuperarlas después.
 *
 * @author Chuidiang
 */
public class RSAAsymetricCrypto {

    //Constructor
    public RSAAsymetricCrypto() {
        try {
            cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
        } catch (NoSuchAlgorithmException | NoSuchPaddingException e) {
            throw new RuntimeException(e);
        }
    }

    //Attributes
    private PublicKey publicKey;
    private Cipher cipher;

    public void setPublicKey(PublicKey key){
        publicKey = key;
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

    public void rebuiltKey(byte[] bytes_key) {
        try {
            KeyFactory keyFactory = KeyFactory.getInstance("RSA");
            X509EncodedKeySpec publicKeySpec = new X509EncodedKeySpec(bytes_key);
            publicKey = keyFactory.generatePublic(publicKeySpec);
        } catch (InvalidKeySpecException | NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }
}