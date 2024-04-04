package mp9.uf1.cryptoutils;

import java.io.*;
import java.net.*;
import java.security.*;
import javax.crypto.*;
import javax.crypto.spec.SecretKeySpec;

public class Server {
    private static PublicKey clientPublicKey;
    private static PrivateKey serverPrivateKey;

    public static void main(String[] args) {
        try {
            // Generar par de claves para el servidor
            KeyPair serverKeyPair = MyCryptoUtils.randomGenerate(2048);
            serverPrivateKey = serverKeyPair.getPrivate();

            // Inicializar el socket del servidor
            ServerSocket serverSocket = new ServerSocket(1234);

            System.out.println("Servidor iniciado. Esperando conexiones...");

            // Esperar a que el cliente se conecte
            Socket clientSocket = serverSocket.accept();
            System.out.println("Cliente conectado.");

            // Obtener flujos de entrada y salida del cliente
            ObjectInputStream inputStream = new ObjectInputStream(clientSocket.getInputStream());
            ObjectOutputStream outputStream = new ObjectOutputStream(clientSocket.getOutputStream());

            // Enviar clave pública del servidor al cliente
            outputStream.writeObject(serverKeyPair.getPublic());
            outputStream.flush();

            // Recibir clave pública del cliente
            clientPublicKey = (PublicKey) inputStream.readObject();

            // Esperar y procesar mensajes del cliente
            while (true) {
                byte[] encryptedMessage = (byte[]) inputStream.readObject();
                byte[] decryptedMessage = MyCryptoUtils.decryptData(encryptedMessage, serverPrivateKey);
                String message = new String(decryptedMessage);
                System.out.println("Cliente: " + message);

                // Respuesta del servidor
                String response = "Mensaje recibido.";
                byte[] encryptedResponse = MyCryptoUtils.encryptData(response.getBytes(), clientPublicKey);
                outputStream.writeObject(encryptedResponse);
                outputStream.flush();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}