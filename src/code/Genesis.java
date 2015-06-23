package code;

public class Genesis 
{
    int luck1, luck2;
    float HPgain, Speedgain, Bulletsgain;
    Config cfg = Config.getInstance();

    public void GetGain(String nick, String pass, int Njogos)
    {
        Genes(nick, pass);
        HPgain = (float) ((cfg.PlayerHP+ cfg.PlayerHP*luck1/20)*(1-Math.exp(-(1+Njogos))));
        Speedgain = (float) ((1.0*cfg.PlayerSpeed + 1.0*cfg.PlayerSpeed*luck2/20));
        Bulletsgain = cfg.NBullets+(luck1+luck2)/3;
    }
    
    public void Genes (String nick, String pass)
    {
        luck1 = luck2 = 0;
        
        for (int i = 0; i<nick.length(); i++)
            luck1 += (int)nick.charAt(i);
        
        for (int i = 0; i<pass.length(); i++)
            luck2 += (int)pass.charAt(i);
        
        luck1 = luck1 % 10;
        luck2 = luck2 % 10;
    }
    
    public void Generate(Figura P, User U)
    {
        int Njogos = U.Njogos;
        String nick = U.nick, pass = U.pass;
        GetGain(nick, pass, Njogos);
        P.HP = (int) HPgain;
        P.Speed = Speedgain;
        P.Bullets = (int) Bulletsgain;
    }
    
    public void Enforce(Figura P, int n)
    {
        P.HP = cfg.BotHP + cfg.BotHP*n/10;
        P.Speed = cfg.BotSpeed + cfg.BotSpeed*n/10;
        P.Bullets = (int)(cfg.NBullets + Math.exp(n));
    }
}