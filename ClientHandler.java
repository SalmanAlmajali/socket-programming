import java.io.*;
import java.net.Socket;
import java.util.ArrayList;

public class ClientHandler implements Runnable {
    // ArrayList untuk menyimpan instance ClientHandler dari setiap klien yang terhubung
    public static ArrayList<ClientHandler> clientHandlers = new ArrayList<>();
    
    private Socket socket;
    private BufferedReader bufferedReader;
    private BufferedWriter bufferedWriter;
    private String clientUsername;

    // Konstruktor ClientHandler
    public ClientHandler(Socket socket) {
        try {
            // Inisialisasi socket, bufferedReader, dan bufferedWriter
            this.socket = socket;
            this.bufferedWriter = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
            this.bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));

            // Membaca username dari klien
            this.clientUsername = bufferedReader.readLine();

            // Menambahkan instance ClientHandler ke ArrayList dan memberitahu klien baru telah masuk
            clientHandlers.add(this);
            broadcastMessage("SERVER : " + clientUsername + " has entered the chat!");
        } catch(IOException e) {
            // Menangani IOException dengan menutup semua sumber daya dan menghapus instance ClientHandler
            closeEverything(socket, bufferedReader, bufferedWriter);
        }
    }

    // Metode yang dijalankan saat thread ClientHandler dimulai
    @Override
    public void run() {
        String messageFromClient;

        // Menangani pesan dari klien dan menyebarkannya ke semua klien yang terhubung
        while (socket.isConnected()) {
            try {
                // Membaca pesan dari klien
                messageFromClient = bufferedReader.readLine();

                // Menyebarkan pesan ke semua klien
                broadcastMessage(clientUsername + " : " + messageFromClient);
            } catch (IOException e) {
                // Menangani IOException dengan menutup semua sumber daya dan menghapus instance ClientHandler
                closeEverything(socket, bufferedReader, bufferedWriter);
                break;
            }
        }
    }

    // Metode untuk menyebarkan pesan ke semua klien yang terhubung
    public void broadcastMessage(String messageToSend) {
        for (ClientHandler clientHandler : clientHandlers) {
            try {
                // Mengirim pesan ke klien yang terhubung, kecuali pengirim pesan
                if (!clientHandler.clientUsername.equals(clientUsername)) {
                    clientHandler.bufferedWriter.write(messageToSend);
                    clientHandler.bufferedWriter.newLine();
                    clientHandler.bufferedWriter.flush();
                }
            } catch (IOException e) {
                // Menangani IOException dengan menutup semua sumber daya dan menghapus instance ClientHandler
                closeEverything(socket, bufferedReader, bufferedWriter);
            }
        }
    }

    // Metode untuk menghapus instance ClientHandler dari ArrayList dan memberitahu klien telah keluar
    public void removeClientHandler() {
        clientHandlers.remove(this);
        broadcastMessage("Server : " + this.clientUsername + " has left the chat!");
    }

    // Metode untuk menutup semua sumber daya (socket, bufferedReader, bufferedWriter) dan menghapus instance ClientHandler
    public void closeEverything(Socket socket, BufferedReader bufferedReader, BufferedWriter bufferedWriter) {
        removeClientHandler();
        try {
            // Menutup socket, bufferedReader, dan bufferedWriter jika tidak null
            if (socket != null) {
                socket.close();
            }
            if (bufferedReader != null) {
                bufferedReader.close();
            }
            if (bufferedWriter != null) {
                bufferedWriter.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
