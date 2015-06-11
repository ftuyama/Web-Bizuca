package code;

import java.awt.*;
import javax.swing.*;
import java.util.*;

public class Game extends JPanel
{
    Config cfg = Config.getInstance();
    
    Random gerador = new Random();
    static boolean AllAI = false, AllPlayers = false;
    private ArrayList<Thread> AIvector = new ArrayList<>();
    private Thread Jogo, AI;
    
    float Sin, Cos, Theta;
    int AbsX, AbsY, Xp, Yp, xVet, yVet;
    static int distance, tempo = 0;
    
    Fase fase = new Fase();
    Image fasedesign = new ImageIcon(cfg.fasedesign_).getImage();
    Image status = new ImageIcon(cfg.status_).getImage();
    ArrayList<Figura> Balas = new ArrayList<>();
    ArrayList<Figura> RemBalas = new ArrayList<>();
    ArrayList<Figura> Tiros = new ArrayList<>();
    ArrayList<Figura> RemTiros = new ArrayList<>();
    ArrayList<Figura> Players = new ArrayList<>();
    ArrayList<Figura> RemPlayers = new ArrayList<>();
    ArrayList<Figura> Bots = new ArrayList<>();

    // XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX
    // XXXXXXXXXXXXXXXXX Inicialização XXXXXXXXXXXXXXXXXX
    // XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX
    
    
    public Game()
    {
        setFocusable(true);
        setBounds(0, 0, cfg.HTela, cfg.VTela);
        removeAll(); validate();
        setLayout(null);
        Inicializar();
        initialMap();
        startGame();
    }
    
    // XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX
    // XXXXXXXXXXXXXX Conectar Jogadores XXXXXXXXXXXXXXXX
    // XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX
    
    public void initialMap(){
        new Thread(new sendMap()).start();
    }
    public void exchangeMap() {
        for (User U : cfg.Users)
            new Thread(new receiveMap(U)).start();
        new Thread(new sendMap()).start();
    }
    
    public class sendMap implements Runnable
    {
        public void run() 
        {
            String message = "@"+Players.size()+","+Bots.size()+","+"0";
            for (Figura P : Players)
                message += ","+P.ID+","+P.getX()+","+P.getY()+","+P.HP+","+P.getAng();
            for (Figura B : Bots)
                message += ","+B.ID+","+B.getX()+","+B.getY()+","+B.HP+","+B.getAng2();
            for (Figura T : Tiros)
                message += ","+"0"+","+T.getX()+","+T.getY()+","+T.HP+","+T.getAng2();
            for (User W : cfg.Users) {
                for (Figura P : Players)
                    if (P.ID == W.port)
                        message+=","+P.Bullets;
                message+=",@";
                W.Send(message);
            }
        }
    }
    
    public class receiveMap implements Runnable
    {
        User U;
        receiveMap(User U){
            this.U = U;
        }
        public void run() 
        {
            try 
            {
                U.Listen();
                while(!U.received)
                    Thread.sleep(cfg.SleepTime);
                interpret(U.message, U.port);
                U.interpreted = true;
            } catch (InterruptedException ex) { }
        }
    }
    public void interpret (String message, int id)
    {
        String info;
        int i, ind = 0, it = 0;
        for (Figura P: Players)
            if(P.ID == id)
                ind = Players.indexOf(P);
        System.out.print("interpreting "+message);
        
        for (i = 0; i<message.length(); i++)
            if (++it <= 3)
            {
                info = "";
                while(message.charAt(i)!=',')
                    info+=message.charAt(i++);
                if (it == 1)
                    Players.get(ind).setAng(Integer.parseInt(info));
                else if (it == 2) Players.get(ind).Keyboard = info;
                else Players.get(ind).Mouse = info;
            }
        System.out.println(" "+Players.get(ind).Keyboard+" ok!");
    }
    
    // XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX
    // XXXXXXXXXXXXXXXXXXXXX Menu XXXXXXXXXXXXXXXXXXXXXXX
    // XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX
    
    public void Inicializar()
    {
        for (User U : cfg.Users)
            U.sent = U.received = false;
        for (int n = 0; n<cfg.Users.size(); n++)
        {
            Xp = gerador.nextInt(fase.HFase);
            Yp = gerador.nextInt(fase.LFase);
            Figura P = new Figura(new ImageIcon(cfg.player_).getImage(),
                    Xp, Yp,cfg.Users.get(n).port, 0, 0, true);
            while(colisao2(P,Xp/4,Yp/4))  
            {
                Xp = gerador.nextInt(fase.HFase);
                Yp = gerador.nextInt(fase.LFase);
                P.setXY(Xp, Yp);
            }
            P.nick = cfg.Users.get(n).nick;
            Players.add(P);
        }
        for (int n = 0; n<cfg.Nbots; n++) 
        {
            Xp = gerador.nextInt(fase.HFase);
            Yp = gerador.nextInt(fase.LFase);
            float Sin = 2 * gerador.nextFloat() - 1;
            Figura novoBot = new Figura(new ImageIcon(cfg.bot_).getImage(),
                    Xp, Yp, n, Sin, (float) Math.sqrt(1 - Sin * Sin), false);
            while (colisao2(novoBot, Xp / 4, Yp / 4)) {
                Xp = gerador.nextInt(fase.HFase);
                Yp = gerador.nextInt(fase.LFase);
                novoBot.setXY(Xp, Yp);
            }
            Bots.add(novoBot);
        }
        Balas.clear();
    }
    
    // XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX
    // XXXXXXXXXXXXXXXX Loop Principal XXXXXXXXXXXXXXXXXX
    // XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX

    public void startGame()
    {
        Jogo = new Thread() 
        {
            public void run() 
            {
                setUpAI();
                while (GameOn()) 
                {
                    System.out.print("Loop");
                    timeEvents();
                    physics();
                    controle(); 
                    System.out.print(" Maps");
                    exchangeMap(); 
                    System.out.println(" Waiting player");
                    waitPlayers();
                    repaint();
                    waitAI();
                }
                repaint();
            }
        };
        Jogo.start();
    }
    
    // Verifica o término do jogo
    public boolean GameOn(){
        for (Figura P : Players)
            if (!P.alive())
                RemPlayers.add(P);
        for (Figura P : RemPlayers)
            Players.remove(P);
        return (Players.size()+Bots.size()>2) && tempo<cfg.Tmax;
    }

    public void waitPlayers()
    {
        AllPlayers = false;
        while (!AllPlayers)
        {
            AllPlayers = true;
            for (User U : cfg.Users)
                if (!U.interpreted || !U.sent)
                    AllPlayers = false;
            try {
                Thread.sleep(cfg.SleepTime);
            } catch (InterruptedException ex) {}
        }
        for (User U : cfg.Users)
            U.received = U.interpreted = U.sent = false;
    }
        
    // XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX
    // XXXXXXXXXXXXXXXXXXX Thread da AI XXXXXXXXXXXXXXXXXXXXXXX
    // XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX 

    public synchronized void waitAI() 
    {
        while (!Allmoved())
            try {
                wait();
            } catch (InterruptedException e) { }
        for (Figura bot : Bots)
            bot.moved = false;
        notifyAll();
    }
    public boolean Allmoved(){
        AllAI = true;
        for (Figura bot : Bots){
            if (AllAI) AllAI = bot.moved && AllAI;
            else break;
        }
        return AllAI;
    }
    public synchronized void botWait(Figura bot) {
        bot.moved = true;
        while (bot.moved) {
            try {
                notifyAll();
                wait();
            } catch (InterruptedException e) {}
        }
    }
    public void setUpAI(){
        for(Figura bot: Bots)
            AIvector.add(AI = new Thread(){
                public void run(){
                    while (bot.alive()) {
                        botLook(bot);
                        botDecide(bot);
                        botMove(bot);
                        botWait(bot);
                    }
                    Bots.remove(bot);
                    tempo-=100;
                }
            });
        for (Thread AIbot: AIvector)
            AIbot.start();
    }
    
    // XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX
    // XXXXXXXXXXXXXXXXXX Loop do Thread XXXXXXXXXXXXXXXXXXXXXX
    // XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX
   
    public boolean insideBounds(int X, int Y){
        return (X>0&&Y>0&&X<fase.LFase&&Y<fase.HFase);
    }
    public void botLook(Figura bot)
    {
        bot.olhar.clear();
        bot.atirou = bot.parede = false; distance = 0;
        int xOrig = bot.getXc(), yOrig = bot.getYc();
        int StepX = (int) (5*bot.Cos), StepY = (int) (5*bot.Sin);
        
        if (Math.abs(bot.Cos)<0.1) { StepX = 0; StepY = 5*(int) Math.signum(bot.Sin);}
        else if (Math.abs(bot.Sin)<0.1) { StepX = 5*(int) Math.signum(bot.Cos); StepY = 0;}
        for (int i = 5; i<10; i++)
            if (Math.abs(bot.Cos)*i>1 && Math.abs(bot.Sin)*i>1){
                StepX = (int) (i*bot.Cos); StepY = (int) (i*bot.Sin);
                break;
            }
        
        for (xVet = xOrig, yVet = yOrig; insideBounds(xVet,yVet)&&
                distance<bot.Rvisao/4; xVet+=StepX, yVet+=StepY)
        {   
            distance = distance(xOrig,yOrig,xVet,yVet);
            //bot.olhar.add(new Point(xVet,yVet));
            if (distance>1 && distance<bot.Rparede)
                 if (!fase.fase[xVet/4][yVet/4] ) {
                     bot.parede = true; bot.dparede = distance; }
            for (Figura P : Players)
                if (P.contains(xVet, yVet))
                    bot.atirou = true; 
            if(bot.atirou||bot.parede) break;
        }
        bot.xVet = xVet; bot.yVet = yVet;
    }
    
    
    public void botDecide(Figura bot){
        if (bot.atirou){
            if (bot.recharge==0)
            {
                if (bot.atirar())
                    bot.vBullet = new Figura(new ImageIcon(cfg.tiro_).getImage(),
                            bot.getX(), bot.getY(), bot.Sin, bot.Cos, false);
                botTurnAround(bot);
                bot.recharge = cfg.Trecharge;
            }
            else bot.recharge--;
        }
    }
    
    public void botMove(Figura bot)
    {
        if (bot.parede)
            botTurnAround(bot);
        mover(bot, bot.Cos, bot.Sin);
        if(bot.change>cfg.Stress)
        {
            botTurnAround(bot);
            bot.change = 0;
        }
        bot.change++;
    }
    
    public void botTurnAround(Figura bot){
        Theta = (float) (2*Math.PI*gerador.nextFloat());
        bot.Sin = (float) Math.sin(Theta);
        bot.Cos = (float) Math.cos(Theta);
        bot.change = 0;
    }
    
    // XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX
    // XXXXXXXXXXXXXXXXX Métodos Periódicos XXXXXXXXXXXXXXXXXXX
    // XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX
    private void timeEvents(){
        if (tempo%cfg.EventTime==0)
        {
            for(Figura bot: Bots) bot.Bullets++;
            Xp = gerador.nextInt(fase.HFase); Yp = gerador.nextInt(fase.LFase);
            Figura novaBala = new Figura(new ImageIcon(cfg.bala_).getImage(),
                Xp,Yp, 1, 0, false);
            while (colisao(novaBala, Xp/4, Yp/4)){
                Xp = gerador.nextInt(fase.HFase); Yp = gerador.nextInt(fase.LFase);
                novaBala.setXY(Xp, Yp);
            }
            Balas.add(novaBala);
            for (Figura P : Players)
                P.HP+=cfg.recover*(100-P.HP)/20;
            tempo++;
        }
    }
    // XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX
    // XXXXXXXXXXXXXXXXX Métodos de Colisão XXXXXXXXXXXXXXXXXXX
    // XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX
    
    private void physics ()
    {
        for (Figura bot: Bots)
            if (bot.toAdd)
            {
                Figura novoTiro = new Figura();
                novoTiro = bot.vBullet;
                Tiros.add(novoTiro);
                bot.toAdd = false;
            }
        for (Figura P : Players)
            if (P.toAdd)
            {
                Figura novoTiro = new Figura();
                novoTiro = P.vBullet;
                Tiros.add(novoTiro);
                P.toAdd = false;
            }
        if (!Tiros.isEmpty())
        {
            for (Figura T : Tiros)
            {
                if (!T.friendly)
                    for (Figura P : Players)
                        if (colisaoFig(T, P, T.Cos, T.Sin)){
                            P.dano(20+gerador.nextInt(20)); 
                            RemTiros.add(T);
                         }
                if (T.friendly && !RemTiros.contains(T))
                    for (Figura Bot : Bots) 
                        if (!RemTiros.contains(T))
                            if (colisaoFig(T, Bot, T.Cos, T.Sin)) {
                                Bot.dano(20+gerador.nextInt(20)); 
                                RemTiros.add(T);
                            }
                if (!RemTiros.contains(T))
                    if(!mover(T, T.Cos, T.Sin))
                        RemTiros.add(T);
            }
            if (!RemTiros.isEmpty()) {
                for (Figura T : RemTiros)
                    Tiros.remove(T);
                RemTiros.clear();
            }
        }
        
        if(!Balas.isEmpty())
        {
            for (Figura candy: Balas)
            {
                for (Figura P : Players)
                    if (colisaoFig(P, candy, 0, 0)) {
                        tempo-=10; P.Bullets+=5; RemBalas.add(candy);
                    }
                for (Figura Bot : Bots) 
                    if (!RemBalas.contains(candy))
                        if (colisaoFig(Bot, candy, 0, 0)) {
                            Bot.Bullets+=5; RemBalas.add(candy);
                }
            }
            if (!RemBalas.isEmpty()) {
                for (Figura candy : RemBalas)
                    Balas.remove(candy);
                RemBalas.clear();
            }
        }
    }
    
    public boolean mover (Figura aux, float X, float Y)
    {
        int Xs = (int)(aux.getX()/4+aux.Speed*X);
        int Ys = (int)(aux.getY()/4+aux.Speed*Y);
        aux.move = !colisao2(aux,Xs,Ys);
        if (aux.move) aux.move(X, Y);
        return aux.move;
    }
    
    public boolean colisao2 (Figura aux, int Xs, int Ys){
        boolean colisao = colisao(aux,Xs,Ys);
        for (Figura bot: Bots)
            if (aux.ID!=bot.ID)
                if (colisaoFig(aux,bot,Xs,Ys))
                    colisao = true;
        return colisao;
    }
    
    public boolean colisao (Figura aux, int Xs, int Ys)
    {
        boolean colisao = false;
        aux.setVertices(Xs, Ys);
        
        for (int i = 0; i<5 && !colisao; i++)
            if (aux.vertices[i][0]>0 && aux.vertices[i][1]>0)
                if (aux.vertices[i][0]<fase.LFase/4 && aux.vertices[i][1]<fase.HFase/4)
                    colisao = !fase.fase[aux.vertices[i][1]][aux.vertices[i][0]];
                else colisao = true;
            else colisao = true;
        
        return colisao;
    }
    
    public boolean colisaoFig (Figura aux1, Figura aux2, float X, float Y)
    {
        int Xs = (int)(aux1.getX()+aux1.Speed*X);
        int Ys = (int)(aux1.getY()+aux1.Speed*Y);
        int X2 = aux2.getX(), Y2 = aux2.getY();
        return (distance(Xs,Ys,X2,Y2) < (aux1.Radius + aux2.Radius));
    }
    
     public int distance(int Xa, int Ya, int Xb, int Yb){
        return (int)Math.sqrt((Xa-Xb)*(Xa-Xb)+(Ya-Yb)*(Ya-Yb));
    }
    public float distance(float Xa, float Ya, float Xb, float Yb){
        return (float)Math.sqrt((Xa-Xb)*(Xa-Xb)+(Ya-Yb)*(Ya-Yb));
    }
    
    // XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX
    // XXXXXXXXXXXXXXXXX Métodos de Desenho XXXXXXXXXXXXXXXXXX
    // XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX
    
    public void paintComponent(Graphics g)
    {
            if(GameOn())
            {
                super.paintComponent(g);
                AbsX = 150; AbsY = 50;
                
                g.setColor(Color.black); g.fillRect(0, 0, cfg.HTela, cfg.VTela);
                g.setColor(Color.red);   g.fillRect(5, 5, (tempo++)*cfg.HTela/cfg.Tmax, 30);
                g.setColor(Color.black); g.drawString("Tempo Restante", 10, 17); 
                                         g.drawString("  "+tempo+"/"+cfg.HTela, 10, 32);
                g.setColor(Color.red);   g.drawString("Players: "+Players.size(), 10, 70);
                                         g.drawString("Bots: "+Bots.size(), 10, 50);
                g.drawImage(fasedesign,AbsX, AbsY,this);

                if(!Balas.isEmpty())
                    for (int i = 0; i<Balas.size(); i++)
                        g.drawImage(Balas.get(i).getImagemBot(), AbsX+Balas.get(i).getX()/3, 
                                AbsY+Balas.get(i).getY()/3, this);
                if(!Tiros.isEmpty())
                    for (int i = 0; i<Tiros.size(); i++)
                        g.drawImage(Tiros.get(i).getImagemBot(), AbsX+Tiros.get(i).getX()/3, 
                                AbsY+Tiros.get(i).getY()/3, this);
                if(!Players.isEmpty())
                    for (int i = 0; i<Players.size(); i++)
                    {
                        Figura P = Players.get(i);
                        Xp = AbsX+P.getX()/3; Yp = AbsY+P.getY()/3;

                            g.drawImage(status,AbsX+P.getXc()/3-20, AbsY+P.getYc()/3-40, this);
                            g.setColor(Color.red);   g.drawString(P.nick, Xp, Yp-30);
                            g.setColor(Color.blue);  g.drawString(""+P.Bullets, Xp+45, Yp-20);
                            g.setColor(Color.green); g.fillRect(Xp-3, Yp-27, P.HP/3, 6);
                        g.drawImage(Players.get(i).getImagemPlayer(), AbsX+Players.get(i).getX()/3, 
                                AbsY+Players.get(i).getY()/3, this);
                    }
                if (!Bots.isEmpty())
                    for (int i = 0; i<Bots.size(); i++)
                        if (Bots.get(i).alive())
                        {
                            Figura B = Bots.get(i);
                            Xp = AbsX+B.getX()/3; Yp = AbsY+B.getY()/3;

                            g.drawImage(status,AbsX+B.getXc()/3-20, AbsY+B.getYc()/3-40, this);
                            g.setColor(Color.red);   g.drawString("Bot "+B.ID, Xp-5, Yp-33);
                            g.setColor(Color.blue);  g.drawString(""+B.Bullets, Xp+40, Yp-23);
                            g.setColor(Color.green); g.fillRect(Xp-6, Yp-30, B.HP/3, 6);
                            
                            g.drawImage(B.getImagemBot(),AbsX+B.getXc()/3, AbsY+B.getYc()/3, this);

                        }
            }
    }
    
    // XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX
    // XXXXXXXXXXXXXXX Métodos de Controle XXXXXXXXXXXXXX
    // XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX
    
    public void controle() 
    {
        for (Figura P : Players)
        {
            Sin = (float) Math.sin(P.getAng()*Math.PI/180); 
            Cos = (float) Math.cos(P.getAng()*Math.PI/180); 
            if (P.Keyboard.equals("W"))       mover(P, Cos, Sin);
            else if (P.Keyboard.equals("S"))  mover(P, -Cos, -Sin);
            else if (P.Keyboard.equals("A"))  mover(P, Cos, -Sin);
            else if (P.Keyboard.equals("D"))  mover(P, -Cos, Sin);
            if (P.Mouse.equals("1"))
            {
                System.out.println("click");
                if (P.atirar())
                    P.vBullet = new Figura(new ImageIcon(cfg.tiro_).getImage(), 
                                P.getX(), P.getY(), Sin, Cos, true);
            }
        }
    }
    
}