
package whisp.utils;

import java.io.FileInputStream;


import java.io.FileOutputStream;
import java.security.KeyStore;
import java.util.Base64;
import javax.crypto.Cipher;
import javax.crypto.spec.GCMParameterSpec;
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

    public static String encryptMessage(String alias, String message) {
        try {

            // Cargar el KeyStore
            KeyStore keyStore = KeyStore.getInstance("JCEKS");
            try (FileInputStream fis = new FileInputStream(KEYSTORE_PATH)) {
                keyStore.load(fis, PASSWORD.toCharArray());
            }

            // Verificar alias
            if (!keyStore.containsAlias(alias)) {
                throw new IllegalArgumentException("El alias no existe en el KeyStore: " + alias);
            }

            // Recuperar la clave secreta
            KeyStore.SecretKeyEntry secretKeyEntry = (KeyStore.SecretKeyEntry) keyStore.getEntry(alias,
                    new KeyStore.PasswordProtection(PASSWORD.toCharArray()));
            SecretKey secretKey = secretKeyEntry.getSecretKey();

            // Generar IV
            byte[] iv = new byte[12]; // GCM utiliza un IV de 12 bytes
            java.security.SecureRandom secureRandom = new java.security.SecureRandom();
            secureRandom.nextBytes(iv);

            // Configurar cifrador AES/GCM
            Cipher cipher = Cipher.getInstance("AES/GCM/NoPadding");
            GCMParameterSpec gcmSpec = new GCMParameterSpec(128, iv);
            cipher.init(Cipher.ENCRYPT_MODE, secretKey, gcmSpec);

            // Cifrar mensaje
            byte[] encryptedBytes = cipher.doFinal(message.getBytes("UTF-8"));

            // Combinar IV y mensaje cifrado
            byte[] encryptedMessageWithIv = new byte[iv.length + encryptedBytes.length];
            System.arraycopy(iv, 0, encryptedMessageWithIv, 0, iv.length);
            System.arraycopy(encryptedBytes, 0, encryptedMessageWithIv, iv.length, encryptedBytes.length);

            // Codificar en Base64
            return Base64.getEncoder().encodeToString(encryptedMessageWithIv);

        } catch (Exception e) {
            e.printStackTrace();
            throw new SecurityException("Error al cifrar el mensaje", e);
        }
    }


    public static String decryptMessage(String alias, String encryptedMessage) {
        try {
            // Decodificar el mensaje de Base64
            byte[] encryptedMessageWithIv = Base64.getDecoder().decode(encryptedMessage);

            // Separar el IV del mensaje cifrado
            byte[] iv = new byte[12]; // GCM utiliza un IV de 12 bytes
            byte[] encryptedBytes = new byte[encryptedMessageWithIv.length - iv.length];

            System.arraycopy(encryptedMessageWithIv, 0, iv, 0, iv.length);
            System.arraycopy(encryptedMessageWithIv, iv.length, encryptedBytes, 0, encryptedBytes.length);

            // Cargar el KeyStore
            KeyStore keyStore = KeyStore.getInstance("JCEKS");
            try (FileInputStream fis = new FileInputStream(KEYSTORE_PATH)) {
                keyStore.load(fis, PASSWORD.toCharArray());
            }

            // Verificar alias
            if (!keyStore.containsAlias(alias)) {
                throw new IllegalArgumentException("El alias no existe en el KeyStore: " + alias);
            }

            // Recuperar la clave secreta
            KeyStore.SecretKeyEntry secretKeyEntry = (KeyStore.SecretKeyEntry) keyStore.getEntry(alias,
                    new KeyStore.PasswordProtection(PASSWORD.toCharArray()));
            SecretKey secretKey = secretKeyEntry.getSecretKey();

            // Configurar el cifrador AES/GCM para descifrar
            Cipher cipher = Cipher.getInstance("AES/GCM/NoPadding");
            GCMParameterSpec gcmSpec = new GCMParameterSpec(128, iv);
            cipher.init(Cipher.DECRYPT_MODE, secretKey, gcmSpec);

            // Descifrar el mensaje
            byte[] decryptedBytes = cipher.doFinal(encryptedBytes);

            // Convertir los bytes descifrados a texto
            return new String(decryptedBytes, "UTF-8");

        } catch (Exception e) {
            e.printStackTrace();
            throw new SecurityException("Error al descifrar el mensaje", e);

        }
    }

}
