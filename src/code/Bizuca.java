package code;
import javax.swing.*;

public class Bizuca extends JFrame 
{
    Config cfg = Config.getInstance();
    public Bizuca ()
    {
        cfg.loadXML();
        add(new Game());   
        setTitle(cfg.title);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        
        setSize(cfg.HTela, cfg.VTela);
        setVisible(true);
        setResizable(true);
    }
    
    public static void main(String[] args) {
        new Bizuca();
    }
}