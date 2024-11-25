package whisp.utils;

import javafx.scene.image.Image;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.nio.IntBuffer;
import java.util.Base64;
import javafx.scene.image.PixelReader;
import javafx.scene.image.WritablePixelFormat;

public class GraphicUtils {

    public static Image stringToImage(String source){
        byte[] imageBytes = Base64.getDecoder().decode(source);
        ByteArrayInputStream inputStream = new ByteArrayInputStream(imageBytes);

        return new Image(inputStream);
    }

    public static String imageToString(Image image) throws Exception {
        BufferedImage bufferedImage = getBufferedImage(image);

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        ImageIO.write(bufferedImage, "png", outputStream);
        byte[] imageBytes = outputStream.toByteArray();

        return Base64.getEncoder().encodeToString(imageBytes);
    }

    private static BufferedImage getBufferedImage(Image image) {
        int width = (int) image.getWidth();
        int height = (int) image.getHeight();

        BufferedImage bufferedImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);

        PixelReader pixelReader = image.getPixelReader();
        WritablePixelFormat<IntBuffer> format = WritablePixelFormat.getIntArgbInstance();
        int[] pixels = new int[width * height];
        pixelReader.getPixels(0, 0, width, height, format, pixels, 0, width);

        // Write pixel data to the BufferedImage
        bufferedImage.setRGB(0, 0, width, height, pixels, 0, width);
        return bufferedImage;
    }

    public static boolean isImageFile(File file) {
        String fileName = file.getName().toLowerCase();
        return fileName.endsWith(".png") || fileName.endsWith(".jpg") || fileName.endsWith(".jpeg") || fileName.endsWith(".gif");
    }
}
