/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package util;

import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;
import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;

import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import org.apache.commons.lang3.RandomStringUtils;


/**
 *
 * @author hocgioinhatlop
 */
public class AESUtil {

    public String key;
    private static String initVector="encryptionIntVec";


    public AESUtil() {
       key=RandomStringUtils.randomAlphanumeric(16);
    }
    public String encrypt(String value) {
        try {
            IvParameterSpec iv = new IvParameterSpec(initVector.getBytes("UTF-8"));
           
            SecretKeySpec skeySpec = new SecretKeySpec(key.getBytes("UTF-8"), "AES");
            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5PADDING");
            cipher.init(Cipher.ENCRYPT_MODE, skeySpec, iv);

            byte[] encrypted = cipher.doFinal(value.getBytes());
            return Base64.getEncoder().encodeToString(encrypted);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return null;
    }

    public String decrypt(String encrypted) {
        try {
            IvParameterSpec iv = new IvParameterSpec(initVector.getBytes("UTF-8"));
           
            SecretKeySpec skeySpec = new SecretKeySpec(key.getBytes("UTF-8"), "AES");

            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5PADDING");
            cipher.init(Cipher.DECRYPT_MODE, skeySpec, iv);
            byte[] original = cipher.doFinal(Base64.getDecoder()
                    .decode(encrypted));

            return new String(original);
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        return null;
    }
    public static void main(String[] args) throws IllegalBlockSizeException, InvalidKeyException, NoSuchPaddingException, BadPaddingException, NoSuchAlgorithmException {
//        init();
//        String cipherText = Base64.getEncoder().encodeToString(RSAUtil.encrypt(key, RSAUtil.publicKey));
//        System.out.println("Key decrypt" + cipherText + "send to server");
//        String secretKey = RSAUtil.decrypt(cipherText, RSAUtil.privateKey);
//        System.out.println("sk"+secretKey);
//        
//        String originalString = "message";
//	System.out.println("Original String to encrypt - " + originalString);
//	String encryptedString = encrypt(originalString);
//	System.out.println("Encrypted String - " + encryptedString);
//	String decryptedString = decrypt(encryptedString);
//	System.out.println("After decryption - " + decryptedString);

    }
}
