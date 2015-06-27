package code;

import java.io.*;
import java.net.*;
import java.util.*;

public class User 
{
    int port, Njogos;
    String IP, nick, pass;
    String inmessage, outmessage;
    boolean interpreted = false;
    boolean broadCast = false;
    boolean received = false;
    boolean started = false;
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
    public void Listen() {
        new Thread(new Receive("jogo")).start();
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
        
        public void interpret(String inmessage) {
            if (operation.equals("login")) {
                for (i = 0; i < inmessage.length(); i++)
                    if (inmessage.charAt(i) == '@')
                        break;
                nick = inmessage.substring(0, i);
                pass = inmessage.substring(i + 1, inmessage.length());
            } else if (operation.equals("chat"))
                broadCast = true;
        }
        
        @Override
        public void run() {
            interpreted = received = broadCast = false;
            while (!received) {
                try {
                    Scanner entrada = new Scanner(cliente.getInputStream());
                    inmessage = entrada.nextLine();
                    interpret(inmessage);
                    received = true;
                } catch (Exception e) {
                    try {
                        Thread.sleep(3);
                    } catch (InterruptedException ex) { }
                }
            }
        }
    }

    class Send implements Runnable 
    {
        Send(String msg) {
            outmessage = msg;
        }

        @Override
        public void run() {
            try {
                PrintStream saida = new PrintStream(cliente.getOutputStream());
                saida.println(outmessage);
                sent = true;
            } catch (IOException ex) {
            }
        }
    }
}
