package mp9.uf1.cryptoutils;
import java.io.*;
import java.net.*;
import java.security.*;
import javax.crypto.*;

public class Client {
    private static PublicKey serverPublicKey;
    private static PrivateKey clientPrivateKey;

    public static void main(String[] args) {
        try {
            // Generar par de claves para el cliente
            KeyPair clientKeyPair = MyCryptoUtils.randomGenerate(2048);
            clientPrivateKey = clientKeyPair.getPrivate();

            // Inicializar el socket del cliente y conectarse al servidor
            Socket socket = new Socket("localhost", 1234);
            System.out.println("Conexión establecida con el servidor.");

            // Obtener flujos de entrada y salida del servidor
            ObjectOutputStream outputStream = new ObjectOutputStream(socket.getOutputStream());
            ObjectInputStream inputStream = new ObjectInputStream(socket.getInputStream());

            // Recibir clave pública del servidor
            serverPublicKey = (PublicKey) inputStream.readObject();

            // Enviar clave pública del cliente al servidor
            outputStream.writeObject(clientKeyPair.getPublic());
            outputStream.flush();

            // Esperar entrada del usuario y enviar mensajes al servidor
            BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
            while (true) {
                System.out.print("Cliente: ");
                String message = reader.readLine();

                // Enviar mensaje cifrado al servidor
                byte[] encryptedMessage = MyCryptoUtils.encryptData(message.getBytes(), serverPublicKey);
                outputStream.writeObject(encryptedMessage);
                outputStream.flush();

                // Recibir respuesta cifrada del servidor
                byte[] encryptedResponse = (byte[]) inputStream.readObject();
                byte[] decryptedResponse = MyCryptoUtils.decryptData(encryptedResponse, clientPrivateKey);
                String response = new String(decryptedResponse);
                System.out.println("Servidor: " + response);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}