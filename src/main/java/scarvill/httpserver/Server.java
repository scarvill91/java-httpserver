package scarvill.httpserver;

import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Server {
    private ServerSocket serverSocket;
    private Serveable service;

    public Server(int port, Serveable service) throws IOException {
        this.serverSocket = new ServerSocket(port);
        this.service = service;
    }

    public boolean isRunning() {
        return !serverSocket.isClosed();
    }

    public InetAddress getInetAddress() {
        return serverSocket.getInetAddress();
    }

    public int getLocalPort() {
        return serverSocket.getLocalPort();
    }

    public void start() {
        ExecutorService threadPool = Executors.newFixedThreadPool(16);

        while(isRunning()) {
            try {
                Socket clientSocket = serverSocket.accept();
                threadPool.execute(service.serve(clientSocket));
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    public void stopServingNewConnections() {
        try {
            serverSocket.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
