/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package service;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import util.AESUtil;
import util.RSAUtil;

/**
 *
 * @author hocgioinhatlop
 */
public class ClientHandler extends Thread {

    private Socket clientSocket;
    private BufferedWriter out;
    private BufferedReader in;
    private String secretKey;
    private AESUtil aesUtil;

    public ClientHandler(Socket socket) {
        this.clientSocket = socket;
    }

    public void run() {
        try {
            in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            out = new BufferedWriter(new OutputStreamWriter(clientSocket.getOutputStream()));
            aesUtil = new AESUtil();
            String inputLine;
            while ((inputLine = in.readLine()) != null) {
                if ("get_homedata".equals(inputLine)) {
                    String data = DataHubService.getInstance().getHomeData();
                    out.write(aesUtil.encrypt(data));
                    out.newLine();
                    out.flush();
                } else if("get_hubdetail".equals(inputLine)) {
                    String data = DataHubService.getInstance().getHubDetail();
                    out.write(aesUtil.encrypt(data));
                    out.newLine();
                    out.flush();
                }
                else if(inputLine.indexOf("GET_KEYWORD") != -1) {
                    String[] arr = inputLine.split("_");
                    String data="";
                    if(arr.length >= 3)
                    {
                      data = DataHubService.getInstance().getKeywordByQuery(arr[2]);   
                    }else{
                       data = DataHubService.getInstance().getKeywordByQuery(""); 
                    }          
                    out.write(aesUtil.encrypt(data));
                    out.newLine();
                    out.flush();
                }
                else if(inputLine.indexOf("SEARCH_QUERY") != -1) {
                    String[] arr = inputLine.split("_");
                    String data=DataHubService.getInstance().getSearchResultByQuery(arr[2].toLowerCase(),arr[3].toLowerCase(),arr[4].toLowerCase(),arr[5]);         
                    out.write(aesUtil.encrypt(data));
                    out.newLine();
                    out.flush();
                }
                else if(inputLine.indexOf("ARTIRST_ALIAS") != -1) {
                    String[] arr = inputLine.split("_");
                    String data = DataHubService.getInstance().getArtistByAlias(arr[2]);
                    out.write(aesUtil.encrypt(data));
                    out.newLine();
                    out.flush();
                }
                 else if(inputLine.indexOf("INFO_SONG") != -1) {
                    String[] arr = inputLine.split("_");
                    String data = DataHubService.getInstance().getInfoSongBySongId(arr[2]);
                    out.write(aesUtil.encrypt(data));
                    out.newLine();
                    out.flush();
                }
                else if(inputLine.indexOf("LYRIC_ID") != -1) {
                    String[] arr = inputLine.split("_");
                    String data = DataHubService.getInstance().getLyricBySongId(arr[2]);
                    out.write(aesUtil.encrypt(data));
                    out.newLine();
                    out.flush();
                }
                else if(inputLine.indexOf("SECRETKEY") != -1){
                    String[] arr = inputLine.split("_");
                    String secretKey= !arr[1].equals("") ? RSAUtil.decrypt(arr[1], RSAUtil.privateKey) : "";
                    System.out.println("Secret Key client is: "+secretKey);
                    aesUtil.setKey(secretKey);
                    out.write("success");
                    out.newLine();
                    out.flush(); 
                }
                else{
                    out.write(inputLine);
                    out.newLine();
                    out.flush(); 
                }
                
            }

            in.close();
            out.close();
            clientSocket.close();
        } catch (IOException ex) {
            Logger.getLogger(ClientHandler.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IllegalBlockSizeException ex) {
            Logger.getLogger(ClientHandler.class.getName()).log(Level.SEVERE, null, ex);
        } catch (InvalidKeyException ex) {
            Logger.getLogger(ClientHandler.class.getName()).log(Level.SEVERE, null, ex);
        } catch (BadPaddingException ex) {
            Logger.getLogger(ClientHandler.class.getName()).log(Level.SEVERE, null, ex);
        } catch (NoSuchAlgorithmException ex) {
            Logger.getLogger(ClientHandler.class.getName()).log(Level.SEVERE, null, ex);
        } catch (NoSuchPaddingException ex) {
            Logger.getLogger(ClientHandler.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
