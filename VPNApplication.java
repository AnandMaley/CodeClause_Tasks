import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
// import java.security.PrivateKey;
// import java.security.PublicKey;
import java.security.interfaces.RSAPublicKey;
// import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;


public class VPNApplication {

    public static void main(String[] args) {
        // Server side
        new Thread(() -> {
            try {
                ServerSocket serverSocket = new ServerSocket(8888);
                Socket clientSocket = serverSocket.accept();

                // Key Exchange
                KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("RSA");
                keyPairGenerator.initialize(2048);
                KeyPair keyPair = keyPairGenerator.generateKeyPair();

                ObjectOutputStream serverOutputStream = new ObjectOutputStream(clientSocket.getOutputStream());
                serverOutputStream.writeObject(keyPair.getPublic());

                ObjectInputStream serverInputStream = new ObjectInputStream(clientSocket.getInputStream());
                byte[] encodedEncryptedSecretKey = (byte[]) serverInputStream.readObject();

                // Use the server's private key to decrypt the secret key
                Cipher rsaCipher = Cipher.getInstance("RSA");
                rsaCipher.init(Cipher.DECRYPT_MODE, keyPair.getPrivate());
                byte[] decryptedSecretKey = rsaCipher.doFinal(encodedEncryptedSecretKey);
                SecretKey secretKey = new SecretKeySpec(decryptedSecretKey, "AES");

                // Data transmission
                Cipher cipher = Cipher.getInstance("AES");
                cipher.init(Cipher.DECRYPT_MODE, secretKey);

                BufferedReader reader = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
                String encryptedMessage;
                while ((encryptedMessage = reader.readLine()) != null) {
                    // Decode Base64 string to bytes
                    byte[] encryptedBytes = Base64.getDecoder().decode(encryptedMessage);

                    // Decrypt the bytes
                    byte[] decryptedBytes = cipher.doFinal(encryptedBytes);

                    // Convert the decrypted bytes to a string
                    String decryptedMessage = new String(decryptedBytes);
                    System.out.println("Received: " + decryptedMessage);
                }
                serverSocket.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();

        // Client side
        new Thread(() -> {
            try {
                Socket socket = new Socket("localhost", 8888);

                // Key Exchange
                ObjectInputStream clientInputStream = new ObjectInputStream(socket.getInputStream());
                RSAPublicKey serverPublicKey = (RSAPublicKey) clientInputStream.readObject();

                KeyGenerator keyGenerator = KeyGenerator.getInstance("AES");
                keyGenerator.init(128);
                SecretKey secretKey = keyGenerator.generateKey();

                // Use the server's public key to encrypt the secret key
                Cipher rsaCipher = Cipher.getInstance("RSA");
                rsaCipher.init(Cipher.ENCRYPT_MODE, serverPublicKey);
                byte[] encryptedSecretKey = rsaCipher.doFinal(secretKey.getEncoded());

                ObjectOutputStream clientOutputStream = new ObjectOutputStream(socket.getOutputStream());
                clientOutputStream.writeObject(encryptedSecretKey);

                // Data transmission
                Cipher cipher = Cipher.getInstance("AES");
                cipher.init(Cipher.ENCRYPT_MODE, secretKey);

                BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
                String message = "Hello, secure world!";

                byte[] encryptedBytes = cipher.doFinal(message.getBytes());

                String encryptedMessage = Base64.getEncoder().encodeToString(encryptedBytes);

                writer.write(encryptedMessage);
                writer.newLine();
                writer.flush();
                socket.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();
    }
}
