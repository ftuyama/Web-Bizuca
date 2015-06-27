package code;
import java.awt.*;
import java.awt.geom.*;
import java.awt.image.*;
import java.util.*;

public class Figura 
{
    Config cfg = Config.getInstance();
    public int dparede, Rvisao, Rparede, orig;
    public int ID, HP, Bullets, change, recharge;
    public boolean parede, atirou, olhou, move;
    public boolean moved, toAdd, friendly;
    
    String nick;
    public Image imagem;
    public String Keyboard, Mouse;
    private int x, y, Ang;
    public int ax, ay, AbsX, AbsY, xVet, yVet;
    public float Sin, Cos, Speed;
    public int Width, Height, Radius;
    int [][] vertices = new int[5][2];
    static int Nbalas = 0;
    
    ArrayList<Point> olhar;
    
    Figura vBullet;

    // XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX
    // XXXXXXXXXXXXXXXXX Posicionamento XXXXXXXXXXXXXXXXXXXXXXX
    // XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX
    
    public int getX () { return x; }
    public int getY () { return y; }
    public int getAng2 () { return (int) ((180/Math.PI)*Math.atan2(Sin, Cos)); }
    public int getAng () { return Ang; }
    public int getXc (){ return x - Width/2;}
    public int getYc (){ return y - Height/2;}
    public void setAng (int Ang) { this.Ang = Ang; }
    public void setXY (int X, int Y) { x = X; y = Y; }
    public void move (float Cos, float Sin){ 
        x = (int) (x + Speed*Cos); y = (int) (y + Speed*Sin); }
            
    public float distance(float Xa, float Ya, float Xb, float Yb){
        return (float)Math.sqrt((Xa-Xb)*(Xa-Xb)+(Ya-Yb)*(Ya-Yb));
    }
    public boolean contains(int Xa, int Ya){
        return (distance(x,y,Xa,Ya)<cfg.Rcontain*Radius);
    }
    public void setVertices(int Xs, int Ys)
    {
        vertices[0][0] = Xs; vertices[0][1] = Ys;
        vertices[1][0] = Xs; vertices[1][1] = Ys+Height/4;
        vertices[2][0] = Xs+Width/4; vertices[2][1] = Ys+Height/4;
        vertices[3][0] = Xs+Width/4; vertices[3][1] = Ys;
        vertices[4][0] = Xs+Width/8; vertices[4][1] = Ys+Height/8;
    }
    public void setUp (){
        Width = imagem.getHeight(null); Height = imagem.getHeight(null);
        Radius = (int) distance(0,0,Width,Height);
        if(friendly) Rvisao = (int) (cfg.Vplayer*Radius);
        else Rvisao = (int) (cfg.Vbot*Radius);
        Rparede = (int) (cfg.Vparede*Radius);
    }
    
    // XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX
    // XXXXXXXXXXXXXXXXXXXX Construtores XXXXXXXXXXXXXXXXXXXXXX
    // XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX
    
    Figura (){ }
    
    Figura (Image Im, int X, int Y){
        setImagem(Im);  x = X; y = Y; Ang = 0; setUp();
    }
    
    Figura (Image Im, int X, int Y, int ID, float Sin, float Cos, boolean F)
    {
        setImagem(Im); x = X; y = Y; Ang = 0; setUp();
        this.ID = ID; Bullets = cfg.NBullets;
        this.Keyboard = "_"; this.Mouse = "0";
        this.Sin = Sin; this.Cos = Cos;
        this.olhar = new ArrayList<>();
        vBullet = new Figura(); 
        toAdd = moved = atirou = parede = move = false;
        recharge = change = 0; 
        friendly = F;
        if (F) {
            this.Speed = cfg.PlayerSpeed;
            this.HP = cfg.PlayerHP;
        }
        else {
            this.Speed = cfg.BotSpeed;
            this.HP = cfg.BotHP;
        }
    }
    Figura (Image Im, int X, int Y, float Sin, float Cos, boolean F, int G){
        if (F) this.ID = Nbalas%100; 
        else this.ID = Nbalas%100 + 1;
        Nbalas+=2; orig = G;
        setImagem(Im); x = X; y = Y; Ang = 0; setUp(); 
        friendly = F; Speed = cfg.BulletSpeed;
        this.Sin = Sin; this.Cos = Cos;
    }
    
    // XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX
    // XXXXXXXXXXXXXXXXXXX Jogabilidade XXXXXXXXXXXXXXXXXXXXXXX
    // XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX
    
    public boolean alive() { return HP>0;}
    
    public boolean atirar (){ 
        if (Bullets>0){
            toAdd = true;
            Bullets--;
        }
        return (Bullets>=0);
    }
    public void dano (int damage){ HP-=damage; }

    // XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX
    // XXXXXXXXXXXXXXXXXXXX Imagem Soft XXXXXXXXXXXXXXXXXXXXXXX
    // XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX

    public Image getImagemPlayer () { 
        float angulo = (float) Math.atan2(ay-cfg.VTela/2, ax-cfg.HTela/2);
        return (Image)rotateImage(toBufferedImage(imagem), angulo*180/Math.PI).getScaledInstance(cfg.Scale, cfg.Scale, 100); 
    }
    public Image getImagemBot () { 
        float angulo = (float) Math.atan2(Sin,Cos);
        return (Image)rotateImage(toBufferedImage(imagem), angulo*180/Math.PI).getScaledInstance(cfg.Scale, cfg.Scale, 100);  
    }
    public Image getImagem () { 
        float angulo = (float) Math.atan2(ay-y, ax-x);
        return (Image)rotateImage(toBufferedImage(imagem), angulo*180/Math.PI).getScaledInstance(cfg.Scale, cfg.Scale, 100);  
    }
    public void setImagem (Image imagem) { this.imagem = imagem; }
    
    // XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX
    // XXXXXXXXXXXXXXXXXXXX Imagem Hard XXXXXXXXXXXXXXXXXXXXXXX
    // XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX
    
    public static BufferedImage rotateImage(BufferedImage rotateImage, double angle) 
    {
        angle %= 360;
        if (angle < 0) angle += 360;

        AffineTransform tx = new AffineTransform();
        tx.rotate(Math.toRadians(angle), rotateImage.getWidth() / 2.0, rotateImage.getHeight() / 2.0);

        double ytrans = 0, xtrans = 0;
        if (angle <= 90) {
            xtrans = tx.transform(new Point2D.Double(0, rotateImage.getHeight()), null).getX();
            ytrans = tx.transform(new Point2D.Double(0.0, 0.0), null).getY();
        } else if (angle <= 180) {
            xtrans = tx.transform(new Point2D.Double(rotateImage.getWidth(), rotateImage.getHeight()), null).getX();
            ytrans = tx.transform(new Point2D.Double(0, rotateImage.getHeight()), null).getY();
        } else if (angle <= 270) {
            xtrans = tx.transform(new Point2D.Double(rotateImage.getWidth(), 0), null).getX();
            ytrans = tx.transform(new Point2D.Double(rotateImage.getWidth(), rotateImage.getHeight()), null).getY();
        } else {
            xtrans = tx.transform(new Point2D.Double(0, 0), null).getX();
            ytrans = tx.transform(new Point2D.Double(rotateImage.getWidth(), 0), null).getY();
        }

        AffineTransform translationTransform = new AffineTransform();
        translationTransform.translate(-xtrans, -ytrans);
        tx.preConcatenate(translationTransform);

        return new AffineTransformOp(tx, AffineTransformOp.TYPE_BILINEAR).filter(rotateImage, null);
    }
    
    public static BufferedImage toBufferedImage(Image img) 
    {
        if (img instanceof BufferedImage) return (BufferedImage) img;
        BufferedImage bimage = new BufferedImage(img.getWidth(null), img.getHeight(null), BufferedImage.TYPE_INT_ARGB);
        Graphics2D bGr = bimage.createGraphics();
        bGr.drawImage(img, 0, 0, null);
        bGr.dispose();
        return bimage;
    }
}
