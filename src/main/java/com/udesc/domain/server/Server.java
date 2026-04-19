package com.udesc.domain.server;

import com.udesc.domain.connection.Connection;
import com.udesc.domain.connection.ConnectionRegistry;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class Server {
    private ServerSocket serverSocket;
    public int port;
    private ConnectionRegistry connections = new ConnectionRegistry();

    public Server(int port) throws IOException {
        this.port = port;
        this.createServer();
    }

    private void createServer() throws IOException {
        this.serverSocket = new ServerSocket(this.port);
        System.out.println("Server started on port " + this.port);

        while (true) {
            System.out.println("Waiting for a connection...");
            Socket socket = this.serverSocket.accept();
            Connection connection = new Connection(connections, socket);
            connection.start();
        }
    }
}
