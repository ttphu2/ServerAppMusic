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
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author hocgioinhatlop
 */
public class ClientHandler extends Thread {

    private Socket clientSocket;
    private BufferedWriter out;
    private BufferedReader in;

    public ClientHandler(Socket socket) {
        this.clientSocket = socket;
    }

    public void run() {
        try {
            in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            out = new BufferedWriter(new OutputStreamWriter(clientSocket.getOutputStream()));
            
            String inputLine;
            while ((inputLine = in.readLine()) != null) {
                if ("get_homedata".equals(inputLine)) {
                    String data = DataHubService.getInstance().getHomeData();
                    out.write(data);
                    out.newLine();
                    out.flush();
                } else if("get_hubdetail".equals(inputLine)) {
                    String data = DataHubService.getInstance().getHubDetail();
                    out.write(data);
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
                    out.write(data);
                    out.newLine();
                    out.flush();
                }
                else if(inputLine.indexOf("SEARCH_QUERY") != -1) {
                    String[] arr = inputLine.split("_");
                    String data=DataHubService.getInstance().getSearchResultByQuery(arr[2].toLowerCase(),arr[3].toLowerCase(),arr[4].toLowerCase(),arr[5]);         
                    out.write(data);
                    out.newLine();
                    out.flush();
                }
                else if(inputLine.indexOf("ARTIRST_ALIAS") != -1) {
                    String[] arr = inputLine.split("_");
                    String data = DataHubService.getInstance().getArtistByAlias(arr[2]);
                    out.write(data);
                    out.newLine();
                    out.flush();
                }
                 else if(inputLine.indexOf("INFO_SONG") != -1) {
                    String[] arr = inputLine.split("_");
                    String data = DataHubService.getInstance().getInfoSongBySongId(arr[2]);
                    out.write(data);
                    out.newLine();
                    out.flush();
                }
                else if(inputLine.indexOf("LYRIC_ID") != -1) {
                    String[] arr = inputLine.split("_");
                    String data = DataHubService.getInstance().getLyricBySongId(arr[2]);
                    out.write(data);
                    out.newLine();
                    out.flush();
                }
                else if(inputLine.indexOf("CIPHERTEXT") != -1){
                    out.write(inputLine);
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
        }
    }
}
