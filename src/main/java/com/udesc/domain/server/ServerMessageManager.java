package com.udesc.domain.server;

import com.udesc.domain.Command;
import com.udesc.domain.connection.Connection;
import com.udesc.domain.connection.ConnectionRegistry;

import java.io.IOException;
import java.net.Socket;

public class ServerMessageManager {

    public static void writeMessageToClient(Socket connection, String message) throws IOException {
        var messageWithNewLine = message + "\n";
        connection.getOutputStream().write(messageWithNewLine.getBytes());
    }

    public static void receiveMessage(ConnectionRegistry connectionRegistry, Connection currentConnection, String message) throws IOException {
        if (Command.isValidCommand(message)) {
            mediateMessage(connectionRegistry, currentConnection, message);
        } else {
            ServerMessageManager.writeMessageToClient(currentConnection.getSocket(), "SERVER WARNING: Invalid option: " + message);
        }
    }

    private static void mediateMessage(ConnectionRegistry connectionRegistry, Connection currentConnection, String message) throws IOException {
        ServerActions serverActions = new ServerActions();
        Command command = Command.getCommand(message);

        switch (command) {
            case LIST_USERS -> {
                serverActions.listUsers(connectionRegistry, currentConnection);
                break;
            }
            case SEND_MESSAGE -> {
                serverActions.sendMessage(connectionRegistry, currentConnection, message);
                break;
            }
            case SEND_FILE -> {
                break;
            }
            case QUIT -> {
                serverActions.quit(connectionRegistry, currentConnection);
                break;
            }
            case null -> {}
            default -> {
                break;
            }
        }

    }

}
