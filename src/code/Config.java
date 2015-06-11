package code;

import org.w3c.dom.*;
import org.xml.sax.*;
import javax.xml.parsers.*;
import javax.xml.transform.*;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.*;
import java.util.*;

public class Config 
{
    ArrayList<User> Users = new ArrayList();
    public int defaultPort = 81;
    
    private static Config instance;
    public static Config getInstance(){
        if (instance == null) instance = new Config();
        return instance;
    }
    public String title = "Web Bizuca";
    public int HTela = 960, VTela = 680;
    
    String Imagedir_ = "src\\imagens\\";
    String bot_ = Imagedir_+"bot.png";
    String bala_ = Imagedir_+"bala.png";
    String tiro_ = Imagedir_+"tiro.png";
    String intro_ = Imagedir_+"intro.png";
    String status_ = Imagedir_+"status.png";
    String player_ = Imagedir_+"player.png";
    String fasedesign_ = Imagedir_+"fasedesign.png";
    
    public int PlayerHP = 100, BotHP = 100, SleepTime = 1, EventTime = 50;
    public int PlayerXinic = 350, PlayerYinic = 350;
    public int PlayerSpeed = 7, BotSpeed = 1, BulletSpeed = 15;
    public int NBullets = 10, Rcontain = 3, Trecharge = 100;
    public float Vparede = (float) 1.5, Vbot = 10, Vplayer = 7, Stress = 100;
    public int Scale = 25;
    
    public int Tmax = 1280, Nbots = 5, recover = 1;
    boolean MusicOn = false;
    
    /*-----------------------------------------------------------------------*/
 
    private static File file = new File("src\\config.xml");

    public void loadXML()
    {
        try{
            if (file.exists())
                lerXml();
            else gerarXml();
        } catch(Exception e){}
    }
    
    private static void salvarArquivo(String documento) throws Exception {
        FileOutputStream fos = new FileOutputStream(file, true);
        fos.write(documento.getBytes());
        fos.flush();
        fos.close();
    }
    
    /*-----------------------------------------------------------------------*/

    private static String converter(Document document) throws TransformerException {
        Transformer transformer
                = TransformerFactory.newInstance().newTransformer();

        transformer.setOutputProperty(OutputKeys.INDENT, "yes");

        StreamResult result = new StreamResult(new StringWriter());
        DOMSource source = new DOMSource(document);
        transformer.transform(source, result);

        String xmlString = result.getWriter().toString();
        return xmlString;
    }

        public void gerarXml() throws Exception 
        {
            if (file.exists()) file.delete();
            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            DocumentBuilder db = dbf.newDocumentBuilder();
            Document doc = db.newDocument();

            Element tagConfig = doc.createElement("Config");

                Element subTagWindowConfig = doc.createElement("Config_Window");

                    Element WindowTitle = doc.createElement("Title");
                    Element WindowWidth = doc.createElement("Window_Width");
                    Element WindowHeigth = doc.createElement("Window_Height");

                    WindowTitle.setTextContent("Web Bizuca");
                    WindowWidth.setTextContent(""+HTela);
                    WindowHeigth.setTextContent(""+VTela);

                    subTagWindowConfig.appendChild(WindowTitle);
                    subTagWindowConfig.appendChild(WindowWidth);
                    subTagWindowConfig.appendChild(WindowHeigth);

                Element subTagImagensConfig = doc.createElement("Config_Imagens");

                    Element ImagemBot = doc.createElement("bot_");
                    Element ImagemBala = doc.createElement("bala_");
                    Element ImagemTiro = doc.createElement("tiro_");
                    Element ImagemIntro = doc.createElement("intro_");
                    Element ImagemPlayer = doc.createElement("player_");
                    Element ImagemFaseDesign = doc.createElement("fasedesign_");

                    ImagemBot.setTextContent(bot_);
                    ImagemBala.setTextContent(bala_);
                    ImagemTiro.setTextContent(tiro_);
                    ImagemIntro.setTextContent(intro_);
                    ImagemPlayer.setTextContent(player_);
                    ImagemFaseDesign.setTextContent(fasedesign_);

                    subTagImagensConfig.appendChild(ImagemBot);
                    subTagImagensConfig.appendChild(ImagemBala);
                    subTagImagensConfig.appendChild(ImagemTiro);
                    subTagImagensConfig.appendChild(ImagemIntro);
                    subTagImagensConfig.appendChild(ImagemPlayer);
                    subTagImagensConfig.appendChild(ImagemFaseDesign);


                Element subTagGamePlayConfig = doc.createElement("Config_Gameplay");

                    Element subTagGamePlayConfigPlayer = doc.createElement("Config_Gameplay_Player");

                        Element GamePlayPlayerHP = doc.createElement("PlayerHP");
                        Element GamePlayPlayerXinic = doc.createElement("PlayerXinic");
                        Element GamePlayPlayerYinic = doc.createElement("PlayerYinic");
                        Element GamePlayPlayerSpeed = doc.createElement("PlayerSpeed");
                        Element GamePlayVPlayer = doc.createElement("Vplayer");

                        GamePlayPlayerHP.setTextContent(""+PlayerHP);
                        GamePlayPlayerXinic.setTextContent(""+PlayerXinic);
                        GamePlayPlayerYinic.setTextContent(""+PlayerYinic);
                        GamePlayPlayerSpeed.setTextContent(""+PlayerSpeed);
                        GamePlayVPlayer.setTextContent(""+Vplayer);

                        subTagGamePlayConfigPlayer.appendChild(GamePlayPlayerHP);
                        subTagGamePlayConfigPlayer.appendChild(GamePlayPlayerXinic);
                        subTagGamePlayConfigPlayer.appendChild(GamePlayPlayerYinic);
                        subTagGamePlayConfigPlayer.appendChild(GamePlayPlayerSpeed);
                        subTagGamePlayConfigPlayer.appendChild(GamePlayVPlayer);

                    subTagGamePlayConfig.appendChild(subTagGamePlayConfigPlayer);


                    Element subTagGamePlayConfigBot = doc.createElement("Config_Gameplay_Bot");

                        Element GamePlayBotHP = doc.createElement("BotHP");
                        Element GamePlayBotSpeed = doc.createElement("BotSpeed");
                        Element GamePlayVBot = doc.createElement("Vbot");

                        GamePlayBotHP.setTextContent(""+BotHP);
                        GamePlayBotSpeed.setTextContent(""+BotSpeed);
                        GamePlayVBot.setTextContent(""+Vbot);

                        subTagGamePlayConfigBot.appendChild(GamePlayBotHP);
                        subTagGamePlayConfigBot.appendChild(GamePlayBotSpeed);
                        subTagGamePlayConfigBot.appendChild(GamePlayVBot);

                    subTagGamePlayConfig.appendChild(subTagGamePlayConfigBot);


                    Element subTagGamePlayConfigTime = doc.createElement("Config_Gameplay_Time");

                        Element GamePlaySleepTime = doc.createElement("SleepTime");
                        Element GamePlayEventTime = doc.createElement("EventTime");
                        Element GamePlayTrecharge = doc.createElement("Trecharge");

                        GamePlaySleepTime.setTextContent(""+SleepTime);
                        GamePlaySleepTime.setTextContent(""+EventTime);
                        GamePlayTrecharge.setTextContent(""+Trecharge);

                        subTagGamePlayConfigTime.appendChild(GamePlaySleepTime);
                        subTagGamePlayConfigTime.appendChild(GamePlayEventTime);
                        subTagGamePlayConfigTime.appendChild(GamePlayTrecharge);

                    subTagGamePlayConfig.appendChild(subTagGamePlayConfigTime);


                    Element subTagGamePlayConfigOther = doc.createElement("Config_Gameplay_Other");

                        Element GamePlayNBullets = doc.createElement("NBullets");
                        Element GamePlayBulletSpeed = doc.createElement("BulletSpeed");
                        Element GamePlayRcontain = doc.createElement("Rcontain");
                        Element GamePlayVparede = doc.createElement("Vparede");
                        Element GamePlayStress= doc.createElement("Stress");

                        GamePlayNBullets.setTextContent(""+NBullets);
                        GamePlayBulletSpeed.setTextContent(""+BulletSpeed);
                        GamePlayRcontain.setTextContent(""+Rcontain);
                        GamePlayVparede.setTextContent(""+Vparede);
                        GamePlayStress.setTextContent(""+Stress);

                        subTagGamePlayConfig.appendChild(GamePlayNBullets);
                        subTagGamePlayConfig.appendChild(GamePlayBulletSpeed);
                        subTagGamePlayConfig.appendChild(GamePlayRcontain);
                        subTagGamePlayConfig.appendChild(GamePlayVparede);
                        subTagGamePlayConfig.appendChild(GamePlayStress);

                    subTagGamePlayConfig.appendChild(subTagGamePlayConfigOther);

                Element subTagMenuConfig = doc.createElement("Config_Menu");

                        Element MenuTmax = doc.createElement("Tmax");
                        Element MenuNbots = doc.createElement("Nbots");
                        Element MenuRecover = doc.createElement("Recover");
                        Element MenuMusicOn = doc.createElement("MusicOn");

                        MenuTmax.setTextContent(""+Tmax);
                        MenuNbots.setTextContent(""+Nbots);
                        MenuRecover.setTextContent(""+recover);
                        MenuMusicOn.setTextContent(""+MusicOn);

                        subTagMenuConfig.appendChild(MenuTmax);
                        subTagMenuConfig.appendChild(MenuNbots);
                        subTagMenuConfig.appendChild(MenuRecover);
                        subTagMenuConfig.appendChild(MenuMusicOn);

                tagConfig.appendChild(subTagWindowConfig);
                tagConfig.appendChild(subTagImagensConfig);
                tagConfig.appendChild(subTagGamePlayConfig);
                tagConfig.appendChild(subTagMenuConfig);

            doc.appendChild(tagConfig);

            String arquivo = converter(doc);
            salvarArquivo(arquivo);
        }

        public static String getChildTagValue(Element elem, String tagName) throws Exception {
            NodeList children = elem.getElementsByTagName(tagName);
            String result = null;
            if (children == null)
                return result;
            Element child = (Element) children.item(0);
            if (child == null) 
                return result;
            result = child.getTextContent();
            return result;
        }
    
    private static void lerXml() throws Exception, SAXException, TransformerException 
    {
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        DocumentBuilder db = dbf.newDocumentBuilder();
        Document doc = db.parse(new InputSource(file.toString()));
        
        Element raiz = doc.getDocumentElement();
        
        NodeList endList = raiz.getElementsByTagName("Config_Window");
        Element endElement = (Element) endList.item(0);
        
        instance.title = getChildTagValue(endElement, "Title");
        instance.HTela = Integer.parseInt(getChildTagValue(endElement,"Window_Width"));
        instance.VTela = Integer.parseInt(getChildTagValue(endElement,"Window_Height"));
        
        endList = raiz.getElementsByTagName("Config_Imagens");
        endElement = (Element) endList.item(0);
        
        instance.bot_  = getChildTagValue(endElement, "bot_");
        instance.bala_ = getChildTagValue(endElement,"bala_");
        instance.tiro_ = getChildTagValue(endElement,"tiro_");
        instance.intro_ = getChildTagValue(endElement, "intro_");
        instance.player_ = getChildTagValue(endElement,"player_");
        instance.fasedesign_ = getChildTagValue(endElement,"fasedesign_");
        
        
        instance.PlayerHP = Integer.parseInt(getChildTagValue(raiz,"PlayerHP"));
        instance.PlayerXinic = Integer.parseInt(getChildTagValue(raiz,"PlayerXinic"));
        instance.PlayerYinic = Integer.parseInt(getChildTagValue(raiz,"PlayerYinic"));
        instance.PlayerSpeed = Integer.parseInt(getChildTagValue(raiz,"PlayerSpeed"));
        instance.Vplayer = Integer.parseInt(getChildTagValue(raiz,"Vplayer"));
        instance.BotHP = Integer.parseInt(getChildTagValue(raiz,"BotHP"));
        instance.BotSpeed = Integer.parseInt(getChildTagValue(raiz,"BotSpeed"));
        instance.Vbot = Integer.parseInt(getChildTagValue(raiz,"Vbot"));
        instance.SleepTime = Integer.parseInt(getChildTagValue(raiz,"SleepTime"));
        instance.EventTime = Integer.parseInt(getChildTagValue(raiz,"EventTime"));
        instance.Trecharge = Integer.parseInt(getChildTagValue(raiz,"Trecharge"));
        instance.NBullets = Integer.parseInt(getChildTagValue(raiz,"NBullets"));
        instance.BulletSpeed = Integer.parseInt(getChildTagValue(raiz,"BulletSpeed"));
        instance.Rcontain = Integer.parseInt(getChildTagValue(raiz,"Rcontain"));
        instance.Vparede = Integer.parseInt(getChildTagValue(raiz,"Vparede"));
        instance.Stress = Integer.parseInt(getChildTagValue(raiz,"Stress"));
        
        
        endList = raiz.getElementsByTagName("Config_Menu");
        endElement = (Element) endList.item(0);
        
        instance.Tmax  = Integer.parseInt(getChildTagValue(endElement, "Tmax"));
        instance.Nbots  = Integer.parseInt(getChildTagValue(endElement, "Nbots"));
        instance.recover  = Integer.parseInt(getChildTagValue(endElement, "Recover"));
        instance.MusicOn  = Boolean.getBoolean(getChildTagValue(endElement, "MusicOn"));
        
    }
    
}