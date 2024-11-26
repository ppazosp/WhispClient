package whisp.utils;

import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import java.util.Base64;

public class Encrypter {

    //*******************************************************************************************
    //* CONSTANTS
    //*******************************************************************************************

    private static final int ITERATIONS = 65536;
    private static final int KEY_LENGTH = 256;
    private static final String ALGORITHM = "PBKDF2WithHmacSHA256";



    //*******************************************************************************************
    //* METHODS
    //*******************************************************************************************

    /**
     * Generado un salt para hashear contraseñas
     *
     * @return salt generado como de array de bytes
     */
    private static byte[] generateSalt() {
        SecureRandom random = new SecureRandom();
        byte[] salt = new byte[16]; // 16 bytes = 128 bits
        random.nextBytes(salt);
        return salt;
    }

    /**
     * Hashea una contraseña usando como algoritmo PBKDF2 y el salt proporcionado
     *
     * @param password contraseña en plano a hashear
     * @param salt array de bytes para usar como salt en el hash
     */
    public static String getHashedPassword(String password, byte[] salt){

        try {
            PBEKeySpec spec = new PBEKeySpec(password.toCharArray(), salt, ITERATIONS, KEY_LENGTH);
            SecretKeyFactory factory = SecretKeyFactory.getInstance(ALGORITHM);
            byte[] hashedPassword = factory.generateSecret(spec).getEncoded();
            return Base64.getEncoder().encodeToString(hashedPassword);
        }catch (NoSuchAlgorithmException | InvalidKeySpecException e){
            Logger.error("Critical error in hashing function");
            throw new IllegalStateException("This should never happen, somthing went horribly wrong", e);
        }
    }

    /**
     * Genera un salt y hashea una contraseña
     *
     * <p>
     *     Llama a la función {@code generateSalt()} para generar el salt y a
     *     {@code getHashedPassword()} para hashear la contraseña con el salt
     * </p>
     *
     * @param password contraseña en plano a hashear
     */
    public static String[] createHashPassword(String password){
        byte[] salt = generateSalt();
        String hashedPassword = getHashedPassword(password, salt);
        return new String[]{
                Base64.getEncoder().encodeToString(salt),
                hashedPassword
        };
    }
}
