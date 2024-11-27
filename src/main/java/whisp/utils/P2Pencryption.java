
package whisp.utils;

import java.io.FileInputStream;


import java.io.FileOutputStream;
import java.security.KeyStore;
import java.util.Base64;
import javax.crypto.spec.SecretKeySpec;
import javax.crypto.SecretKey;

public class P2Pencryption {

    private final static String KEYSTORE_PATH = "client.keystore";
    private static final String PASSWORD = "password";

    // Crear un nuevo KeyStore y guardarlo en el archivo
    public static void createKeyStore() {
        try {
            KeyStore keyStore = KeyStore.getInstance("JCEKS");
            keyStore.load(null, PASSWORD.toCharArray());
            try (FileOutputStream fos = new FileOutputStream(KEYSTORE_PATH)) {
                keyStore.store(fos, PASSWORD.toCharArray());
            }
            Logger.info("KeyStore created successfully");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Añadir una clave secreta al KeyStore (recibiendo la clave en Base64)
    public static void addKeyToKeyStore(String alias, String base64Key) {
        try {
            // Cargar el KeyStore existente desde el archivo
            KeyStore keyStore = KeyStore.getInstance("JCEKS");
            try (FileInputStream fis = new FileInputStream(KEYSTORE_PATH)) {
                keyStore.load(fis, PASSWORD.toCharArray());
            }

            // Decodificar la clave secreta desde Base64
            byte[] decodedKey = Base64.getDecoder().decode(base64Key);

            // Crear un objeto SecretKey a partir de los bytes decodificados
            SecretKey secretKey = new SecretKeySpec(decodedKey, 0, decodedKey.length, "AES");

            // Crear la entrada del KeyStore para la clave secreta
            KeyStore.SecretKeyEntry secretKeyEntry = new KeyStore.SecretKeyEntry(secretKey);
            KeyStore.ProtectionParameter protectionParameter =
                    new KeyStore.PasswordProtection(PASSWORD.toCharArray());
            keyStore.setEntry(alias, secretKeyEntry, protectionParameter);

            // Guardar los cambios en el KeyStore
            try (FileOutputStream fos = new FileOutputStream(KEYSTORE_PATH)) {
                keyStore.store(fos, PASSWORD.toCharArray());
            }

            Logger.info("Secret key added with alias " + alias);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
