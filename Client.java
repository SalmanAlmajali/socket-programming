import java.io.*;
import java.net.Socket;
import java.util.Scanner;

public class Client {
    private Socket socket;
    private BufferedReader bufferedReader;
    private BufferedWriter bufferedWriter;
    private String username;

    // Konstruktor Client
    public Client(Socket socket, String username) {
        try {
            // Inisialisasi socket, bufferedReader, bufferedWriter, dan username
            this.socket = socket;
            this.bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            this.bufferedWriter = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
            this.username = username;
        } catch (IOException e) {
            // Menangani IOException dengan menutup semua sumber daya
            closeEverything(socket, bufferedReader, bufferedWriter);
        }
    }

    // Metode untuk mengirim pesan ke server
    public  void sendMessage() {
        try {
            // Mengirim username ke server
            bufferedWriter.write(username);
            bufferedWriter.newLine();
            bufferedWriter.flush();

            // Menggunakan Scanner untuk membaca input pengguna dan mengirim pesan ke server
            Scanner scanner = new Scanner(System.in);

            while (socket.isConnected()) {
                String messageToSend = scanner.nextLine();
                bufferedWriter.write(messageToSend);
                bufferedWriter.newLine();
                bufferedWriter.flush();
            }
        } catch (IOException e) {
            // Menangani IOException dengan menutup semua sumber daya
            closeEverything(socket, bufferedReader, bufferedWriter);
        }
    }

    // Metode untuk mendengarkan pesan dari server
    public void listenForMessage() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                String msgFromGroupChat;

                // Terus mendengarkan pesan dari server dan menampilkannya di konsol
                while (socket.isConnected()) {
                    try {
                        msgFromGroupChat = bufferedReader.readLine();
                        System.out.println(msgFromGroupChat);
                    } catch (IOException e) {
                        // Menangani IOException dengan menutup semua sumber daya
                        closeEverything(socket, bufferedReader, bufferedWriter);
                    }
                }
            }
        }).start();
    }

    // Metode untuk menutup semua sumber daya (socket, bufferedReader, bufferedWriter)
    public void closeEverything(Socket socket, BufferedReader bufferedReader, BufferedWriter bufferedWriter) {
        try  {
            // Menutup bufferedReader, bufferedWriter, dan socket jika tidak null
            if(bufferedReader != null) {
                bufferedReader.close();
            }
            if(bufferedWriter != null) {
                bufferedWriter.close();
            }
            if(socket != null) {
                socket.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) throws IOException {
        // Membaca username dari pengguna
        Scanner scanner = new Scanner(System.in);
        System.out.print("Enter your username : ");
        String username = scanner.nextLine();

        // Membuat socket dan objek Client, kemudian menjalankan metode listenForMessage() dan sendMessage()
        Socket socket = new Socket("localhost", 8000);
        Client client = new Client(socket, username);
        client.listenForMessage();
        client.sendMessage();
    }
}
