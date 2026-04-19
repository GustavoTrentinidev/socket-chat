package com.udesc.domain.server;

import com.udesc.domain.Command;
import com.udesc.domain.connection.Connection;
import com.udesc.domain.connection.ConnectionRegistry;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Optional;

public class ServerActions {

    public void listUsers(ConnectionRegistry connectionRegistry, Connection currentConnection) throws IOException {
        ServerMessageManager.writeMessageToClient(currentConnection.getSocket(), "------------------------");
        ServerMessageManager.writeMessageToClient(currentConnection.getSocket(), "Listing Connected Users:");
        for (Connection c : connectionRegistry.listConnections()) {
            boolean isYou = c.id.equals(currentConnection.id);
            String prefix = isYou ? " [YOU]: " : ": ";
            ServerMessageManager.writeMessageToClient(currentConnection.getSocket(), c.clientName + prefix + c.id);
        }
        ServerMessageManager.writeMessageToClient(currentConnection.getSocket(), "------------------------");
    }

    public void quit(ConnectionRegistry cr, Connection connection) throws IOException {
        cr.removeConnection(connection.id);
        connection.getSocket().close();
        System.out.println("Connection closed: " + connection.getSocket().getRemoteSocketAddress() + " at " + LocalDateTime.now());
    }

    public void sendMessage(ConnectionRegistry cr, Connection currentConnection, String message) throws IOException {
        String[] params = message.substring(Command.SEND_MESSAGE.value.length()).split(" ");

        if (params.length < 2) {
            ServerMessageManager.writeMessageToClient(currentConnection.getSocket(), "Invalid message format. Use /send message <user_id> <message>. Obs: remove the <> from the user_id/message");
            return;
        }

        String userId = params[0];
        String messageContent = message.substring(message.indexOf(userId) + userId.length() + 1);

        if (userId.equals(currentConnection.id)) {
            ServerMessageManager.writeMessageToClient(currentConnection.getSocket(), "You cannot send a message to yourself, choose another user.");
            return;
        }

        Optional<Connection> destinationConnection = cr.getConnectionById(userId);

        if (destinationConnection.isEmpty()) {
            ServerMessageManager.writeMessageToClient(currentConnection.getSocket(), "User id " + userId + " not found");
            return;
        }

        Connection destination = destinationConnection.get();

        ServerMessageManager.writeMessageToClient(destination.getSocket(), "[" + currentConnection.clientName + "] sent you a message: " + messageContent);

        ServerMessageManager.writeMessageToClient(currentConnection.getSocket(), "You've sent: \"" + messageContent + "\" to " + userId);
    }

}
