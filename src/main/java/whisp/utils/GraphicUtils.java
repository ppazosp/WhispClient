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

    //*******************************************************************************************
    //* METHODS
    //*******************************************************************************************

    /**
     * Convierte una cadena Base64 en una imagen JavaFX.
     *
     * @param source la cadena Base64 que representa la imagen.
     * @return un objeto {@link Image} creado a partir de la cadena Base64.
     */
    public static Image stringToImage(String source) {
        byte[] imageBytes = Base64.getDecoder().decode(source);
        ByteArrayInputStream inputStream = new ByteArrayInputStream(imageBytes);

        return new Image(inputStream);
    }

    /**
     * Convierte una imagen JavaFX en una cadena Base64.
     *
     * @param image la imagen que se desea convertir a cadena Base64.
     * @return una cadena Base64 que representa la imagen.
     * @throws Exception si ocurre un error durante el procesamiento de la imagen.
     */
    public static String imageToString(Image image) throws Exception {
        BufferedImage bufferedImage = getBufferedImage(image);

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        ImageIO.write(bufferedImage, "png", outputStream);
        byte[] imageBytes = outputStream.toByteArray();

        return Base64.getEncoder().encodeToString(imageBytes);
    }

    /**
     * Convierte una imagen JavaFX en un {@link BufferedImage}.
     *
     * @param image la imagen que se desea convertir.
     * @return un objeto {@link BufferedImage} que representa la imagen proporcionada.
     */
    private static BufferedImage getBufferedImage(Image image) {
        int width = (int) image.getWidth();
        int height = (int) image.getHeight();

        BufferedImage bufferedImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);

        PixelReader pixelReader = image.getPixelReader();
        WritablePixelFormat<IntBuffer> format = WritablePixelFormat.getIntArgbInstance();
        int[] pixels = new int[width * height];
        pixelReader.getPixels(0, 0, width, height, format, pixels, 0, width);

        bufferedImage.setRGB(0, 0, width, height, pixels, 0, width);
        return bufferedImage;
    }

    /**
     * Verifica si un archivo es una imagen válida.
     *
     * @param file el archivo que se desea comprobar.
     * @return {@code true} si el archivo tiene una extensión de imagen válida (.png, .jpg, .jpeg, .gif),
     *         de lo contrario {@code false}.
     */
    public static boolean isImageFile(File file) {
        String fileName = file.getName().toLowerCase();
        return fileName.endsWith(".png") || fileName.endsWith(".jpg") || fileName.endsWith(".jpeg") || fileName.endsWith(".gif");
    }
}
