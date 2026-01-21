import javax.crypto.*;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

public class MyProgram {

    private final String video = "C:\\Users\\lucns\\OneDrive\\Documentos\\CryptTest\\video.mp4";
    private final String encrypted = "C:\\Users\\lucns\\OneDrive\\Documentos\\CryptTest\\encrypted.mp4";
    private final String decrypted = "C:\\Users\\lucns\\OneDrive\\Documentos\\CryptTest\\decrypted.mp4";

    private final String key = "Lucas@0123456789"; // 16 bytes or 128 bit key
    private final String initVector = "0123456789@Lucas"; // 16 bytes IV

    private byte[] getFileBytes() {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        try {
            InputStream inputStream = new FileInputStream(video);
            byte[] buffer = new byte[1024];
            int read;
            while (inputStream.available() > 0) {
                read = inputStream.read(buffer);
                byteArrayOutputStream.write(buffer, 0, read);
            }
            byte[] bytes = byteArrayOutputStream.toByteArray();
            byteArrayOutputStream.reset();
            byteArrayOutputStream.close();
            return bytes;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public void encDecLocal() {
        System.out.println("encDecLocal");
        byte[] fileBytes = getFileBytes();
        if (fileBytes == null) {
            System.out.println("Fail read file input!");
            return;
        }

        byte[] encryptedBytes = new byte[fileBytes.length];
        AES_CipherFeedback256 aes = new AES_CipherFeedback256();
        aes.initialize(key.getBytes(StandardCharsets.UTF_8), initVector.getBytes(StandardCharsets.UTF_8));
        aes.encrypt(fileBytes, fileBytes.length, encryptedBytes);
        try {
            OutputStream outputStream = new FileOutputStream(encrypted);
            outputStream.write(encryptedBytes);
            outputStream.flush();
            outputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println("encrypted");

        // ---------------------------------------

        byte[] decryptedBytes = new byte[fileBytes.length];
        aes.initialize(key.getBytes(StandardCharsets.UTF_8), initVector.getBytes(StandardCharsets.UTF_8));
        aes.decrypt(encryptedBytes, encryptedBytes.length, decryptedBytes);

        try {
            OutputStream outputStream = new FileOutputStream(decrypted);
            outputStream.write(decryptedBytes);
            outputStream.flush();
            outputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println("decrypted");
    }

    public void encdecNativeJava() {
        System.out.println("encdecNativeJava");
        byte[] fileBytes = getFileBytes();
        if (fileBytes == null) {
            System.out.println("Fail read file input!");
            return;
        }

        IvParameterSpec iv = new IvParameterSpec(initVector.getBytes(StandardCharsets.UTF_8));
        SecretKeySpec keySpec = new SecretKeySpec(key.getBytes(StandardCharsets.UTF_8), "AES");
        byte[] encryptedBytes;
        try {
            Cipher cipher = Cipher.getInstance("AES/CFB/NOPADDING");
            cipher.init(Cipher.ENCRYPT_MODE, keySpec, iv);
            encryptedBytes = cipher.doFinal(fileBytes);
        } catch (NoSuchAlgorithmException | NoSuchPaddingException | InvalidKeyException |
                 InvalidAlgorithmParameterException | IllegalBlockSizeException | BadPaddingException e) {
            e.printStackTrace();
            return;
        }
        try {
            OutputStream outputStream = new FileOutputStream(encrypted);
            outputStream.write(encryptedBytes);
            outputStream.flush();
            outputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println("encrypted");

        // --------------------------------------------------------------------------------------------------------

        byte[] decryptedBytes;
        try {
            Cipher cipher = Cipher.getInstance("AES/CFB/NOPADDING");
            cipher.init(Cipher.DECRYPT_MODE, keySpec, iv);
            decryptedBytes = cipher.doFinal(encryptedBytes);
        } catch (NoSuchAlgorithmException | NoSuchPaddingException | InvalidKeyException |
                 InvalidAlgorithmParameterException | IllegalBlockSizeException | BadPaddingException e) {
            e.printStackTrace();
            return;
        }

        try {
            OutputStream outputStream = new FileOutputStream(decrypted);
            outputStream.write(decryptedBytes);
            outputStream.flush();
            outputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println("decrypted");
    }

    private SecretKey generateAESKey(String key) throws Exception {
        KeyGenerator keyGen = KeyGenerator.getInstance("AES");
        keyGen.init(128); // 128-bit key size
        return keyGen.generateKey();
    }

    private byte[] encrypt(byte[] fileData, SecretKey key) throws Exception {
        Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding"); // Note: ECB is simple but not secure for most uses
        cipher.init(Cipher.ENCRYPT_MODE, key);
        return cipher.doFinal(fileData);
    }

    private byte[] decrypt(byte[] encryptedBytes, SecretKey key) throws Exception {
        Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
        cipher.init(Cipher.DECRYPT_MODE, key);
        return cipher.doFinal(encryptedBytes);
    }
}
