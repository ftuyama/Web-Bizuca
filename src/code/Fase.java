package code;
import java.awt.Image;
import java.awt.image.*;
import javax.swing.ImageIcon;

public class Fase extends Figura 
{
    int HFase, LFase;
    boolean[][] fase = new boolean[imagem.getHeight(null)][imagem.getWidth(null)];
    
    public void setFaseConfig(){
        Image imagem = new ImageIcon("src\\imagens\\fase.png").getImage();
        HFase = imagem.getHeight(null); LFase =imagem.getWidth(null);
    }
           
    public Fase() 
    {
        super(new ImageIcon("src\\imagens\\fase.png").getImage(), 0, 0);
        BufferedImage I = toBufferedImage(imagem);
        setFaseConfig();
        
        int[] pixels = ((DataBufferInt) I.getRaster().getDataBuffer()).getData();

        final int width = I.getWidth(), height = I.getHeight();
        final boolean hasAlphaChannel = I.getAlphaRaster() != null;
        if (hasAlphaChannel) 
        {
            final int pixelLength = 4;
            for (int pixel = 0, row = 0, col = 0; pixel < pixels.length; pixel += pixelLength) 
            {
                int argb = 0;
                argb += (((int) pixels[pixel] & 0xff) << 24); // alpha
                argb += ((int) pixels[pixel + 1] & 0xff); // blue
                argb += (((int) pixels[pixel + 2] & 0xff) << 8); // green
                argb += (((int) pixels[pixel + 3] & 0xff) << 16); // red
                fase[row][col] = (argb!=0);
                col++;
                if (col == width) {
                    col = 0;
                    row++;
                }
            }
        } else 
        {
            final int pixelLength = 3;
            for (int pixel = 0, row = 0, col = 0; pixel < pixels.length; pixel += pixelLength) 
            {
                int argb = 0;
                argb += -16777216; // 255 alpha
                argb += ((int) pixels[pixel] & 0xff); // blue
                argb += (((int) pixels[pixel + 1] & 0xff) << 8); // green
                argb += (((int) pixels[pixel + 2] & 0xff) << 16); // red
                fase[row][col] = (argb!=0);
                col++;
                if (col == width) {
                    col = 0;
                    row++;
                }
            }
        }
    }
}
