package code;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Game extends JPanel
{
    Config cfg = Config.getInstance();
    BancoDados BDados = new BancoDados();
    
    private JButton jplay, jexit, jteste, jreset;
    private JTextField jtempo, jmusica, jbots, jsaude;
    private JLabel jtexto1, jtexto2, jtexto3, jtexto4;
    private JLabel jex1, jex2, jTitle, jCredit, jload;
    
    Random gerador = new Random();
    boolean Start = false, AllAI = false, VersaoTeste = false;
    private Thread Jogo, AI;
    private ArrayList<Thread> AIvector = new ArrayList<>();
    
    float Sin, Cos, Theta;
    int AbsX, AbsY, Xp, Yp, xVet, yVet;
    int nbots, distance, tempo = 0;
    String sucess ="";
    
    Fase fase = new Fase();
    Musica Music = new Musica();
    Image intro = new ImageIcon(cfg.intro_).getImage();
    Image vitoria = new ImageIcon(cfg.vitoria_).getImage();
    Image gameover = new ImageIcon(cfg.gameover_).getImage();
    Image fasedesign = new ImageIcon(cfg.fasedesign_).getImage();
    ArrayList<Figura> Balas = new ArrayList<>();
    ArrayList<Figura> RemBalas = new ArrayList<>();
    ArrayList<Figura> Tiros = new ArrayList<>();
    ArrayList<Figura> RemTiros = new ArrayList<>();
    ArrayList<Figura> Bots = new ArrayList<>();
    private Figura P1;

    // XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX
    // XXXXXXXXXXXXXXXXX Inicialização XXXXXXXXXXXXXXXXXX
    // XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX
    
    public Game() 
    {
        if(!BDados.conecta()) sucess = "Não";
        setFocusable(true);
        setBounds(0, 0, cfg.HTela, cfg.VTela);
        setLayout(null);
        init();
        eventos();
    }
    
    // XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX
    // XXXXXXXXXXXXXXXXXXXXX Menu XXXXXXXXXXXXXXXXXXXXXXX
    // XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX
    
    public void Inicializar()
    {
        Start = true;
        removeAll(); validate();
        P1 = new Figura(new ImageIcon(cfg.player_).getImage(),
                    cfg.PlayerXinic, cfg.PlayerYinic,-1, 0, 0, true);
        for (int n = 0; n<cfg.Nbots; n++) {
                Xp = gerador.nextInt(fase.HFase); Yp = gerador.nextInt(fase.LFase);
                float Sin = 2*gerador.nextFloat()-1;
                Figura novoBot = new Figura(new ImageIcon(cfg.bot_).getImage(),
                    Xp,Yp, n, Sin, (float)Math.sqrt(1-Sin*Sin), false);
                while (colisao2(novoBot, Xp/4, Yp/4)){
                    Xp = gerador.nextInt(fase.HFase); Yp = gerador.nextInt(fase.LFase);
                    novoBot.setXY(Xp, Yp);
                }
                Bots.add(novoBot);
            }
    }
    
    public void init()
    {
        int H = 400, D = 150, V = 150;
        
        jload = new JLabel ("MySQL "+sucess+" carregado!"); jload.setBounds(600, 600, 200, 30); add(jload);
        jtexto1 = new JLabel("Música?"); jtexto1.setBounds(H, V, 200, 30); add(jtexto1);
        jtexto2 = new JLabel("Nº Bots:"); jtexto2.setBounds(H, V+50, 200, 30); add(jtexto2);
        jtexto3 = new JLabel("Regenaração?"); jtexto3.setBounds(H, V+100, 200, 30); add(jtexto3);
        jtexto4 = new JLabel("Tempo máximo:"); jtexto4.setBounds(H, V+150, 200, 30); add(jtexto4);
        jex1 = new JLabel("(1 = sim) (0 = não)"); jex1.setBounds(H-25, V+15, 200, 30); add(jex1);
        jex2 = new JLabel("(1 = sim) (0 = não) (-1 = negativa)"); jex2.setBounds(H-50, V+115, 200, 30); add(jex2);
        
        jTitle = new JLabel("Bizuca V 2.0"); 
        jTitle.setFont(new java.awt.Font("Old English Text MT", 0, 36));
        jTitle.setBounds(cfg.HTela/2-100, 0, 300, 100); add(jTitle);
        
        jCredit = new JLabel("By Felipe Tuyama & Uriel"); 
        jCredit.setFont(new java.awt.Font("Euclid Fraktur", 1, 18));
        jCredit.setBounds(cfg.HTela/2, 400, 300, 100); add(jCredit);
        
        if(cfg.MusicOn) jmusica= new JTextField("0"); else jmusica= new JTextField("1"); 
        jmusica.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jmusica.setBounds(H+D,V,200,30); add(jmusica);
        jbots = new JTextField(""+cfg.Nbots); jbots.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jbots.setBounds(H+D,V+50,200,30); add(jbots);
	jsaude = new JTextField(""+cfg.recover); jsaude.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jsaude.setBounds(H+D,V+100,200,30); add(jsaude);
        jtempo = new JTextField(""+cfg.Tmax); jtempo.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jtempo.setBounds(H+D,V+150,200,30); add(jtempo);
        
        
	jplay = new JButton ("Play"); jplay.setBounds(cfg.HTela/2, 350, 100, 40); add(jplay);
	jteste = new JButton ("Versão Teste"); jteste.setBounds(0, 0, 200, 40); add(jteste);
        jreset = new JButton ("Reset Config"); jreset.setBounds(350, 400, 150, 40); add(jreset);
	jexit = new JButton ("X"); jexit.setBounds(cfg.HTela-75, 0, 50, 25); add(jexit);
        
    }
    
    public void eventos(){
        jplay.addActionListener(new ActionListener(){
            public void actionPerformed (ActionEvent e) {
                cfg.MusicOn = (Integer.parseInt(jmusica.getText().toString())==1);
                nbots = cfg.Nbots = (Integer.parseInt(jbots.getText().toString()));
                cfg.Tmax = (Integer.parseInt(jtempo.getText().toString()));
                cfg.recover = (Integer.parseInt(jsaude.getText().toString()));
                try {
                    cfg.gerarXml();
                } catch (Exception ex) {}
                startGame();
            }	
        });	
        jteste.addActionListener(new ActionListener(){
            public void actionPerformed (ActionEvent e) {
                nbots = cfg.Nbots = 5; cfg.recover = 1; cfg.Tmax = cfg.HTela;
                VersaoTeste = true;
                startGame();
            }	
        });
        jreset.addActionListener(new ActionListener(){
            public void actionPerformed (ActionEvent e) {
                try {
                    cfg.gerarXml();
                } catch (Exception ex) {}
            }	
        });
        jexit.addActionListener(new ActionListener(){
            public void actionPerformed (ActionEvent e) {
                System.exit(1);
            }	
        });
    }

    // XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX
    // XXXXXXXXXXXXXXXX Loop Principal XXXXXXXXXXXXXXXXXX
    // XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX

    public void startGame()
    {
        Inicializar();
        Jogo = new Thread() 
        {
            public void run() 
            {
                Music.play();
                controle();
                setUpAI();
                while (GameOn()) 
                {
                    timeEvents();
                    physics();
                    repaint();
                    waitAI();
                    try {
                        Thread.sleep(cfg.SleepTime);
                    } catch (InterruptedException ex) {
                    }
                }
                if(nbots>0) Music.GO();
                else Music.VO();
                repaint();
                try {
                    Thread.sleep(10000);
                } catch (InterruptedException ex) {}
            }
        };
        Jogo.start();
    }
    
    // Verifica o término do jogo
    public boolean GameOn(){
        return P1.alive() && tempo<cfg.Tmax && nbots>0;
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
                    nbots--;
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
            if (P1.contains(xVet, yVet)) bot.atirou = true; 
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
            P1.HP+=cfg.recover*(100-P1.HP)/20;
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
        if (P1.toAdd)
        {
            Figura novoTiro = new Figura();
            novoTiro = P1.vBullet;
            Tiros.add(novoTiro);
            P1.toAdd = false;
        }
        if (!Tiros.isEmpty())
        {
            for (Figura T : Tiros)
            {
                if (!T.friendly)
                    if (colisaoFig(T, P1, T.Cos, T.Sin)){
                        P1.dano(20+gerador.nextInt(20)); 
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
                if (colisaoFig(P1, candy, 0, 0)) {
                    tempo-=10; P1.Bullets+=5; RemBalas.add(candy);
                }
                for (Figura Bot : Bots) 
                    if (!RemBalas.contains(candy))
                        if (colisaoFig(Bot, candy, 0, 0)) {
                            P1.Bullets+=5; RemBalas.add(candy);
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
    
    // XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX
    // XXXXXXXXXXXXXXXXX Métodos de Desenho XXXXXXXXXXXXXXXXXX
    // XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX
    
    public void paintComponent(Graphics g)
    {
        if(Start)
        {
            if(GameOn())
            {
                super.paintComponent(g);
                AbsX = cfg.HTela/2-P1.getX();  AbsY = cfg.VTela/2-P1.getY();
                g.drawImage(fasedesign,AbsX, AbsY,this);
                g.setColor(Color.black); g.fillRect(0, 0, cfg.HTela, 40); g.fillRect(0, 0, 70, 60);
                g.setColor(Color.red);   g.fillRect(5, 5, (tempo++)*cfg.HTela/cfg.Tmax, 30);
                g.setColor(Color.black); g.drawString("  "+tempo+"/"+cfg.HTela, 10, 30);
                g.setColor(Color.red);   g.drawString("Faltam: "+nbots, 10, 50);

                if (P1.alive())
                {
                    Xp = cfg.HTela/2-P1.Width/2; Yp = cfg.VTela/2-P1.Height/2;
                    if(VersaoTeste)
                        g.drawOval(Xp-P1.Rvisao/2, Yp-P1.Rvisao/2,P1.Rvisao,P1.Rvisao);
                    g.setColor(Color.red);   g.drawString("HP:"+P1.HP, Xp-25, Yp-30);
                    g.setColor(Color.blue);  g.drawString("B:"+P1.Bullets, Xp+25, Yp-30);
                    g.drawImage(P1.getImagemPlayer(),Xp, Yp, this);
                    g.setColor(Color.black); g.fillRect(Xp-4, Yp-24, P1.HP/2+8, 18);
                    g.setColor(Color.red);   g.fillRect(Xp, Yp-20, P1.HP/2, 10);
                }
                if(!Balas.isEmpty())
                    for (int i = 0; i<Balas.size(); i++)
                        g.drawImage(Balas.get(i).getImagemBot(), AbsX+Balas.get(i).getX(), 
                                AbsY+Balas.get(i).getY(), this);
                if(!Tiros.isEmpty())
                    for (int i = 0; i<Tiros.size(); i++)
                        g.drawImage(Tiros.get(i).getImagemBot(), AbsX+Tiros.get(i).getX(), 
                                AbsY+Tiros.get(i).getY(), this);
                for (int i = 0; i<Bots.size(); i++)
                    if (Bots.get(i).alive())
                    {
                        Figura B = Bots.get(i);
                        Xp = AbsX+B.getX(); Yp = AbsY+B.getY();

                        if(VersaoTeste)
                        {
                            g.setColor(Color.MAGENTA); g.drawLine(Xp, Yp, B.xVet, B.yVet);
                            g.setColor(Color.blue); 
                            g.drawOval(Xp-B.Rvisao/2,Yp-B.Rvisao/2,B.Rvisao,B.Rvisao);
                            if(B.atirou) g.drawString("atirou",Xp+30,Yp-30);
                            g.setColor(Color.red); 
                            if(B.parede) g.drawString("parede",Xp-30,Yp-30);
                            if(B.parede) g.drawString(""+B.dparede,Xp-30,Yp+30);
                            g.drawString(""+B.change,Xp-30,Yp+60);
                            g.drawOval(Xp-B.Rparede/2,Yp-B.Rparede/2,B.Rparede,B.Rparede);
                        }

                        g.setColor(Color.red);   g.drawString("HP:"+B.HP, Xp-25, Yp-60);
                        g.setColor(Color.blue);  g.drawString("B:"+B.Bullets, Xp+25, Yp-60);
                        g.setColor(Color.black); g.fillRect(Xp-20, Yp-60, B.HP/2+8, 18);
                        g.setColor(Color.green); g.fillRect(Xp-16, Yp-55, B.HP/2, 10);

                        g.drawImage(B.getImagemBot(),AbsX+B.getXc(), AbsY+B.getYc(), this);

                    }
            }
            else if (nbots>0) g.drawImage(gameover,0,0,this);
            else g.drawImage(vitoria,0,0,this);
        }
        else {
            super.paintComponent(g);
            g.drawImage(intro,0,0,this);
        }
    }

    // XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX
    // XXXXXXXXXXXXXXX Métodos de Controle XXXXXXXXXXXXXX
    // XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX
    
    public void CalculaSinCos()
    {
        float ax = P1.ax, ay = P1.ay;
        float Hip = distance(ax,ay,cfg.HTela/2, cfg.VTela/2);
        Sin = (ay-cfg.VTela/2)/Hip; Cos = (ax-cfg.HTela/2)/Hip;
    }
    
    public void controle() 
    {
        // XXXXXXXXXXXXXXXXXXX Controle do Mouse XXXXXXXXXXXXXXXXXXXXX
        
        addMouseListener(new MouseListener() {  
            public void mouseClicked(MouseEvent e) {  
                if (P1.atirar()) {
                    CalculaSinCos();
                    P1.vBullet = new Figura(new ImageIcon(cfg.tiro_).getImage(), 
                            P1.getX(), P1.getY(), Sin, Cos, true);
                }
            }  
            public void mousePressed(MouseEvent e) { }  
            public void mouseReleased(MouseEvent e) { }  
            public void mouseEntered(MouseEvent e) { }  
            public void mouseExited(MouseEvent e) { }  
        });  
        addMouseMotionListener(new MouseMotionListener() {
            public void mouseDragged(MouseEvent e) { }
            public void mouseMoved(MouseEvent e) {
                P1.ax = e.getX(); P1.ay = e.getY();
            }
        });
        
         // XXXXXXXXXXXXXXXXXXX Controle das Teclas XXXXXXXXXXXXXXXXXXXXX
        
        addKeyListener(new KeyListener() {
            public void keyTyped(KeyEvent e) { }
            public void keyPressed(KeyEvent e) {
                int Key = e.getKeyCode();
                CalculaSinCos();
                if (Key == 38 || Key == 87) mover(P1, Cos, Sin);
                if (Key == 40 || Key == 83) mover(P1,-Cos, -Sin);
                if (Key == 37 || Key == 68) mover(P1, -Cos, Sin);
                if (Key == 39 || Key == 65) mover(P1, Cos, -Sin);
                if (Key == 27) System.exit(0);
            }
            public void keyReleased(KeyEvent e) {
                //System.out.println(e.getKeyCode());
            }
        });
    }

    public int distance(int Xa, int Ya, int Xb, int Yb){
        return (int)Math.sqrt((Xa-Xb)*(Xa-Xb)+(Ya-Yb)*(Ya-Yb));
    }
    public float distance(float Xa, float Ya, float Xb, float Yb){
        return (float)Math.sqrt((Xa-Xb)*(Xa-Xb)+(Ya-Yb)*(Ya-Yb));
    }
}
