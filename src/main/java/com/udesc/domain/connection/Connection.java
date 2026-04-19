package com.udesc.domain.connection;

import com.udesc.domain.server.ServerMessageManager;
import org.json.JSONObject;

import java.io.IOException;
import java.net.Socket;
import java.time.LocalDateTime;
import java.util.Scanner;

public class Connection extends Thread {
    private final Socket currentConnection;
    private ConnectionRegistry serverConnections;
    public String clientName;
    public String id;

    public Connection(ConnectionRegistry serverConnections, Socket socket) {
        System.out.println("New connection: " + socket.getRemoteSocketAddress() + " at " + LocalDateTime.now());
        this.currentConnection = socket;
        this.serverConnections = serverConnections;
    }

    public void run() {
        try {
            ServerMessageManager.writeMessageToClient(this.currentConnection, "Welcome to the server!");
            ServerMessageManager.writeMessageToClient(this.currentConnection, "Use /users to see all users connected");
            ServerMessageManager.writeMessageToClient(this.currentConnection, "Use /send message <user_id> <message> to send a message to a user");
            ServerMessageManager.writeMessageToClient(this.currentConnection, "Use /send file <user_id> <file path> to send a file to a user");
            ServerMessageManager.writeMessageToClient(this.currentConnection, "Use /quit to quit the server");

            Scanner scanner = new Scanner(this.currentConnection.getInputStream());

            while (scanner.hasNextLine()) {
                var json = new JSONObject(scanner.nextLine());
                boolean isServerMessage = json.getBoolean("isServerMessage");

                if (isServerMessage) {
                    this.clientName = json.getString("clientName");
                    this.id = json.getString("id");
                    this.serverConnections.addConnection(id, this);
                    continue;
                }

                String message = json.getString("message");

                ServerMessageManager.receiveMessage(this.serverConnections, this, message);
            }

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public Socket getSocket() {
        return this.currentConnection;
    }
}