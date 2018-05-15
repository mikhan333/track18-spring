package ru.track.prefork;

import java.io.*;
import java.net.Socket;
import java.util.Scanner;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.track.prefork.Server.Message;

public class Client {

    static class ClientWrite extends Thread {
        private Socket socket;

        public ClientWrite(Socket socket) {
            this.socket = socket;
        }

        @Override
        public void run() {
            try {
                Scanner scanner = new Scanner(System.in);
                while (!isInterrupted()) {
                    String line = scanner.nextLine();

                    ObjectOutputStream outobj = new ObjectOutputStream(socket.getOutputStream());
                    Message mess = new Message(1, line);
                    outobj.writeObject(mess);
                    outobj.flush();
                    if ("exit".equals(line)) {
                        socket.close();
                        break;
                    }
                }
            } catch (Exception e) {
                System.out.println("Close programm");
            }
        }
    }


    static class ClientRead extends Thread {
        private Socket socket;

        public ClientRead(Socket socket) {
            this.socket = socket;
        }

        @Override
        public void run() {
            try {
                InputStream inputStream = socket.getInputStream();

                while (!isInterrupted()) {
                    ObjectInputStream inobj = new ObjectInputStream(inputStream);
                    Message mess = (Message) inobj.readObject();
                    if ("exit".equals(mess.data)) {
                        socket.close();
                        break;
                    }
                    System.out.println(mess.data);

                }
            } catch (IOException e) {
                //System.out.println("Failed to get message(client)");
                //try { socket.close(); } catch(IOException b) {}
            } catch (ClassNotFoundException b) {
                b.printStackTrace();
            }

        }
    }

    static Logger log = LoggerFactory.getLogger(Client.class);

    private int port;
    private String host;

    public Client(int port, String host) {
        this.port = port;
        this.host = host;
    }

    public void loop() throws Exception {
        Socket socket = new Socket(host, port);

        Thread p = new ClientWrite(socket);
        p.start();

        Thread t = new ClientRead(socket);
        t.start();


        p.join();
        t.interrupt();
    }

    public static void main(String[] args) throws Exception {
        Client client = new Client(9000, "localhost");
        client.loop();
    }
}
