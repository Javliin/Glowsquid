package me.javlin.glowsquid.network.util;

import javax.crypto.*;
import javax.crypto.spec.SecretKeySpec;
import java.security.*;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.X509EncodedKeySpec;

public class UtilEncryption {
    private static final KeyPair keyPair = generateKeyPair();

    public static byte[] encryptRSA(byte[] data, byte[] key) {
        try {
            Cipher encrypt = Cipher.getInstance("RSA");
            encrypt.init(Cipher.ENCRYPT_MODE, KeyFactory.getInstance("RSA").generatePublic(new X509EncodedKeySpec(key)));
            return encrypt.doFinal(data);
        } catch (NoSuchAlgorithmException | NoSuchPaddingException | InvalidKeySpecException | InvalidKeyException | IllegalBlockSizeException | BadPaddingException exception) {
            exception.printStackTrace();

            return null;
        }
    }

    public static SecretKey decryptRSA(byte[] sharedSecret) {
        if(keyPair == null)
            return null;

        try {
            Cipher decrypt = Cipher.getInstance("RSA");
            decrypt.init(Cipher.DECRYPT_MODE, keyPair.getPrivate());
            return new SecretKeySpec(decrypt.doFinal(sharedSecret), "AES");
        }catch (NoSuchAlgorithmException | NoSuchPaddingException | InvalidKeyException | IllegalBlockSizeException | BadPaddingException exception) {
            exception.printStackTrace();

            return null;
        }
    }


    public static KeyPair getKeyPair() {
        return keyPair;
    }

    private static KeyPair generateKeyPair() {
        try {
            KeyPairGenerator keypairgenerator = KeyPairGenerator.getInstance("RSA");
            keypairgenerator.initialize(1024);
            return keypairgenerator.generateKeyPair();
        }catch (NoSuchAlgorithmException exception) {
            exception.printStackTrace();
            return null;
        }
    }
}
