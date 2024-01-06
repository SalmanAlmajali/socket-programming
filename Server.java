import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class Server {
    private ServerSocket serverSocket;

    public Server(ServerSocket serverSocket) {
        this.serverSocket = serverSocket;
    }

    public void startServer() {
        try {
            // Menjalankan server dan menerima koneksi dari klien
            while (!serverSocket.isClosed()) {
                Socket socket = serverSocket.accept();
                System.out.println("A new Client has Connected!");

                // Membuat objek ClientHandler untuk menangani koneksi dengan klien
                ClientHandler clientHandler = new ClientHandler(socket);

                // Membuat dan menjalankan thread untuk menangani koneksi dengan klien secara paralel
                Thread thread = new Thread(clientHandler);
                thread.start();
            }
        } catch (IOException e) {
             // Menangani IOException yang dapat terjadi saat menerima koneksi atau membuat thread
        }
    }

    public void closeServerSocket() {
        try {
            if (serverSocket != null) {
                // Menutup serverSocket jika tidak null
                serverSocket.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) throws IOException {
        // Membuat serverSocket dan objek Server
        ServerSocket serverSocket = new ServerSocket(8000);
        Server server = new Server(serverSocket);

        // Memulai server untuk menerima koneksi dari klien
        server.startServer();
    }
}
