/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package util;

import constant.Constants;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.math.BigInteger;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.Timestamp;
import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;

/**
 *
 * @author hocgioinhatlop
 */
public class HashUtil {

    public static BufferedImage convertToBufferImage(String urlStr) {
        try {
            URL url = new URL(urlStr);
            BufferedImage image = ImageIO.read(url);
            return image;
        } catch (IOException ex) {
            Logger.getLogger(HashUtil.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }
    public static String encrypt_256_String(String input) {
        try {

            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] messageDigest = md.digest(input.getBytes());

            BigInteger no = new BigInteger(1, messageDigest);

            String hashtext = no.toString(16);

            while (hashtext.length() < 32) {
                hashtext = "0" + hashtext;
            }

            return hashtext;
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }

    public static String encrypt_512_String(String input) {
        try {

            MessageDigest md = MessageDigest.getInstance("SHA-512");
            byte[] messageDigest = md.digest(input.getBytes());

            BigInteger no = new BigInteger(1, messageDigest);

            String hashtext = no.toString(16);

            while (hashtext.length() < 32) {
                hashtext = "0" + hashtext;
            }

            return hashtext;
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }

    private static String toHexString(byte[] bytes) {
        Formatter formatter = new Formatter();
        for (byte b : bytes) {
            formatter.format("%02x", b);
        }
        return formatter.toString();
    }

    public static String bytesToHex(byte[] hash) {
        StringBuilder hexString = new StringBuilder(2 * hash.length);
        for (int i = 0; i < hash.length; i++) {
            String hex = Integer.toHexString(0xff & hash[i]);
            if (hex.length() == 1) {
                hexString.append('0');
            }
            hexString.append(hex);
        }
        return hexString.toString();
    }

    public static String genarateSignature(String ctime, String endpoint, String musicId,String page,String type,String count) {
        try {
            StringBuilder s=new StringBuilder("");
            if(count != null){
               s.append("count=" + count);
            }
            if(ctime != null){
               s.append("ctime=" + ctime);
            }
            if (musicId != null) {
                s.append("id=" + musicId);
            }
            if(page != null){
                s.append("page=" + page);
            }
            if(type != null){
                s.append("type=" + type);
            }
            s.append("version="+constant.Constants.ZING_VERSION);
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] encodedhash = digest.digest(s.toString().getBytes(StandardCharsets.UTF_8));
            String data = endpoint + bytesToHex(encodedhash);
            SecretKeySpec secretKeySpec = new SecretKeySpec(Constants.ZING_CLIENTKEY.getBytes(), Constants.HMAC_SHA512);
            Mac mac = Mac.getInstance(Constants.HMAC_SHA512);
            mac.init(secretKeySpec);
            return toHexString(mac.doFinal(data.getBytes()));
        } catch (InvalidKeyException ex) {
            Logger.getLogger(HashUtil.class.getName()).log(Level.SEVERE, null, ex);
        } catch (NoSuchAlgorithmException ex) {
            Logger.getLogger(HashUtil.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }
}
