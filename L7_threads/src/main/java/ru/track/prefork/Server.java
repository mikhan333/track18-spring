package ru.track.prefork;

import java.io.*;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Scanner;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Serializable;

/**
 * - multithreaded +
 * - atomic counter +
 * - setName() +
 * - thread -> Worker +
 * - save threads
 * - broadcast (fail-safe)
 */


public class Server {

    static AtomicCounter counter;
    static LinkedList<pair> clients;

    static class Message implements Serializable {
        public long ts;
        public String data;

        public Message(long ts, String data) {
            this.ts = ts;
            this.data = data;
        }
    }


    static class pair {
        public Socket socket;
        public Thread th;

        pair(Socket socket, Thread thread) {
            this.socket = socket;
            this.th = thread;
        }
    }


    class AtomicCounter {
        private AtomicLong val = new AtomicLong(1);

        public long inc() {
            return val.getAndIncrement();
        }
    }


    static class ServerWrite extends Thread {
        @Override
        public void run() {
            try {
                Scanner scanner = new Scanner(System.in);
                while (!isInterrupted()) {
                    String line = scanner.nextLine();
                    if ("exit".equals(line)) {
                        break;
                    }
                    if ("list".equals(line)) {
                        for (pair elem : clients) {
                            System.out.println(elem.th.getName());
                        }
                    }
                    if ("drop".equals(line.substring(0, 4))) {
                        int IDdel = Integer.parseInt(line.substring(4).trim());
                        System.out.println(IDdel + " dropped");
                        for (pair elem : clients) {
                            String[] str = elem.th.getName().split("\\D+");
                            if (IDdel == Integer.parseInt(str[1])) {
                                elem.th.interrupt();
                            }
                        }
                    }
                }
            } catch (Exception e) {
                System.out.println("Failed to drop");
                //socket.close();
            }
        }
    }


    static class ServerThread extends Thread {

        private Socket socket;
        private long ID;

        public ServerThread(Socket socket) {
            this.socket = socket;
        }

        @Override
        public void run() {
            try {
                ID = counter.inc();
                setName("Client[" + ID + "]@" + socket.getLocalSocketAddress().toString().substring(1));
                System.out.println(Thread.currentThread().getName());

                InputStream inputStream = socket.getInputStream();

                while (!isInterrupted()) {
                    try {
                        while (inputStream.available() == 0) {
                            sleep(50);
                        }
                    } catch (InterruptedException b) {
                        ObjectOutputStream outobj = new ObjectOutputStream(socket.getOutputStream());
                        Message mess = new Message(1, "exit");
                        outobj.writeObject(mess);
                        outobj.flush();
                        break;
                    }
                    ObjectInputStream inobj = new ObjectInputStream(inputStream);
                    Message mess = (Message) inobj.readObject();
                    if ("exit".equals(mess.data)) {
                        break;
                    }
                    String str = "Client" + "@" + socket.getLocalSocketAddress().toString().substring(1) + ">";
                    String all = str + mess.data;
                    Message messAll = new Message(1, all);

                    ObjectOutputStream outobj;
                    Lock l = new ReentrantLock();
                    for (pair elem : clients) {
                        if (elem.th != this) {
                            l.lock();
                            outobj = new ObjectOutputStream(elem.socket.getOutputStream());
                            outobj.writeObject(messAll);
                            outobj.flush();
                            l.unlock();
                        }
                    }
                }
            } catch (IOException e) {
                //System.out.println("Failed to message(server)");
                //socket.close();
            } catch (ClassNotFoundException b) {
                b.printStackTrace();
            } finally {
                try {
                    for (pair elem : clients) {
                        if (elem.socket == socket) {
                            clients.remove(elem);
                        }
                    }
                    socket.close();
                } catch (Exception c) {
                }

            }
        }
    }


    static Logger log = LoggerFactory.getLogger(Server.class);

    private int port;

    public Server(int port) {
        this.port = port;
    }

    public void serve() throws Exception {
        ServerSocket serverSocket = new ServerSocket(port, 10, InetAddress.getByName("localhost"));
        counter = new AtomicCounter();
        clients = new LinkedList<pair>();

        Thread command = new ServerWrite();
        command.start();
        while (true) {

            final Socket socket = serverSocket.accept();

            Thread t = new ServerThread(socket);
            clients.add(new pair(socket, t));
            t.start();
        }
    }

    public static void main(String[] args) throws Exception {
        Server server = new Server(9000);
        server.serve();
    }
}
