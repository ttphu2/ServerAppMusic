/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package service;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JTextArea;

/**
 *
 * @author hocgioinhatlop
 */
public class Server {

    private ServerSocket serverSocket;
    private static Server instance;
    private JTextArea textArea;
    private final int PORT_NUMBER = 9999;
    private static int numberClient = 0;
    public static Server getInstance(JTextArea textArea) {
        if (instance == null) {
            instance = new Server(textArea);
        }
        return instance;
    }
    private Server(JTextArea textArea) {
        this.textArea = textArea;
    }
    public void start() {
        try {
            serverSocket = new ServerSocket(PORT_NUMBER);
            while (!serverSocket.isClosed()) {
                Socket soc = serverSocket.accept();
                textArea.append("Server has Start on port : " + PORT_NUMBER + "\n");
                numberClient++;
                System.out.println("1 client connected");
                ClientHandler handler = new ClientHandler(soc);
                handler.start();
            }
       //     new EchoClientHandler(serverSocket.accept()).start();
        } catch (IOException ex) {
            Logger.getLogger(Server.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void stop() {
        try {
            serverSocket.close();
        } catch (IOException ex) {
            Logger.getLogger(Server.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

}
