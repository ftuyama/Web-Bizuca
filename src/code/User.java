package code;

import java.io.*;
import java.net.*;
import java.util.*;

public class User 
{
    int port;
    String IP, nick, pass, message;
    boolean broadCast = false;
    boolean received = false;
    boolean sent = false;
    ServerSocket servidor;
    Socket cliente;
    
    User(int port, Socket cliente)
    {
        this.cliente = cliente;
        new Thread(new Send(""+port)).start();
    }
    
    User(String IP, int port, Socket cliente, ServerSocket servidor)
    {
        this.IP = IP;
        this.port = port;
        this.cliente = cliente;
        this.servidor = servidor;
        new Thread(new Send("Digite seu Nick")).start();
        new Thread(new Receive("login")).start();
    }
    
    public void chatListen() {
        new Thread(new Receive("chat")).start();
    }
    public void Send(String msg) {
        new Thread(new Send(msg)).start();
    }
    
    class Receive implements Runnable 
    {
        int i;
        String operation;
        
        Receive(String operation) {
            this.operation = operation;
        }
        
        public void interpret(String message) {
            if (operation.equals("login")) {
                for (i = 0; i < message.length(); i++)
                    if (message.charAt(i) == '@')
                        break;
                nick = message.substring(0, i);
                pass = message.substring(i + 1, message.length());
            } else if (operation.equals("chat"))
                broadCast = true;
        }
        
        @Override
        public void run() {
            received = broadCast = false;
            while (!received) {
                try {
                    Scanner entrada = new Scanner(cliente.getInputStream());
                    message = entrada.nextLine();
                    interpret(message);
                    received = true;
                    cliente = servidor.accept();
                } catch (Exception e) {
                    try {
                        Thread.sleep(50);
                    } catch (InterruptedException ex) { }
                }
            }
        }
    }

    class Send implements Runnable 
    {
        Send(String msg) {
            message = msg;
        }

        @Override
        public void run() {
            try {
                PrintStream saida = new PrintStream(cliente.getOutputStream());
                saida.println(message);
                sent = true;
            } catch (IOException ex) {
            }
        }
    }
}
