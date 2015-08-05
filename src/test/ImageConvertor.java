package test;

import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.awt.image.DirectColorModel;
import java.awt.image.ImageObserver;
import java.awt.image.WritableRaster;

import javax.swing.ImageIcon;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.ImageData;
import org.eclipse.swt.graphics.PaletteData;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;

/**
 * 
 * Change javax.swing.ImageIcon object to org.eclipse.swt.graphics.Image object.
 * 
 * @author Freshwind
 */
public class ImageConvertor {

    public ImageConvertor() {
        super();
    }

    /**
     * change ImageIcon to BufferedImage
     * 
     * @param icon
     * @return
     */
    public static BufferedImage getBufferedImage(ImageIcon icon) {
        int width = icon.getIconWidth();
        int height = icon.getIconHeight();
        ImageObserver observer = icon.getImageObserver();
        BufferedImage bufferedImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        Graphics gc = bufferedImage.createGraphics();
        gc.drawImage(icon.getImage(), 0, 0, observer);
        return bufferedImage;
    }

    /**
     * change BufferedImage to ImageData
     * 
     * @param bufferedImage
     * @return
     */
    public static ImageData getImageData(BufferedImage bufferedImage) {
        DirectColorModel colorModel = (DirectColorModel) bufferedImage.getColorModel();
        PaletteData palette = new PaletteData(colorModel.getRedMask(), colorModel.getGreenMask(), colorModel
                .getBlueMask());
        ImageData data = new ImageData(bufferedImage.getWidth(), bufferedImage.getHeight(), colorModel.getPixelSize(),
                palette);
        int whitePixel = data.palette.getPixel(new RGB(255, 255, 255)); // 将白色设定为透明色
        WritableRaster raster = bufferedImage.getRaster();
        int[] pixelArray = new int[3];
        for (int y = 0; y < data.height; y++) {
            for (int x = 0; x < data.width; x++) {
                raster.getPixel(x, y, pixelArray);
                int pixel = palette.getPixel(new RGB(pixelArray[0], pixelArray[1], pixelArray[2]));
                if (pixel == 0) // Swing读取PNG图形文件，透明的色素被置为0，所以格式转换是，将它变为白色（之前定义的透明色素）。
                    data.setPixel(x, y, whitePixel);
                else
                    data.setPixel(x, y, pixel);
            }
        }
        data.transparentPixel = whitePixel; // 将白色指定为透明色素（同理将其它颜色定为透明色素也是可行的）。
        return data;
    }

    public static void main(String[] args) {
        ImageIcon icon = new ImageIcon("E:\\mypic\\picture.png");
        BufferedImage bufferedImage = getBufferedImage(icon);
        ImageData data = getImageData(bufferedImage);

        Display display = new Display();
        Shell shell = new Shell(display);
        shell.setText("Image Convertor");
        shell.setLayout(new FillLayout());
        shell.setBackground(new Color(display, new RGB(255, 255, 255)));
        shell.setSize(350, 100);

        Label label = new Label(shell, SWT.NONE);
        // new Image with ImageData which is generated previously.
        Image image = new Image(display, data);
        label.setImage(image);
        shell.open();
        while (!shell.isDisposed()) {
            if (!display.readAndDispatch())
                display.sleep();
        }
        display.dispose();
    }
}
