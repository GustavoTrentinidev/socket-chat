package com.udesc.domain.server;

import com.udesc.domain.Command;
import com.udesc.domain.connection.Connection;
import com.udesc.domain.connection.ConnectionRegistry;
import org.json.JSONObject;

import java.io.IOException;
import java.net.Socket;
import java.util.Base64;
import java.util.UUID;

public class ServerMessageManager {
    private static final int FILE_CHUNK_SIZE = 4096;

    public static void writeMessageToClient(Socket connection, String message) throws IOException {
        var messageWithNewLine = message + "\n";
        connection.getOutputStream().write(messageWithNewLine.getBytes());
    }

    public static void writeFileToClient(Socket connection, String fileName, byte[] fileBytes) throws IOException {
        String transferId = UUID.randomUUID().toString();
        int totalChunks = (int) Math.ceil((double) fileBytes.length / FILE_CHUNK_SIZE);

        JSONObject start = new JSONObject();
        start.put("transferId", transferId);
        start.put("fileName", fileName);
        start.put("totalChunks", totalChunks);
        start.put("size", fileBytes.length);
        writeMessageToClient(connection, "FILE_TRANSFER_START " + start);

        for (int offset = 0, chunkIndex = 0; offset < fileBytes.length; offset += FILE_CHUNK_SIZE, chunkIndex++) {
            int chunkLength = Math.min(FILE_CHUNK_SIZE, fileBytes.length - offset);
            byte[] chunk = new byte[chunkLength];
            System.arraycopy(fileBytes, offset, chunk, 0, chunkLength);

            JSONObject chunkPayload = new JSONObject();
            chunkPayload.put("transferId", transferId);
            chunkPayload.put("index", chunkIndex);
            chunkPayload.put("contentBase64", Base64.getEncoder().encodeToString(chunk));
            writeMessageToClient(connection, "FILE_TRANSFER_CHUNK " + chunkPayload);
        }

        JSONObject end = new JSONObject();
        end.put("transferId", transferId);
        writeMessageToClient(connection, "FILE_TRANSFER_END " + end);
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
                serverActions.sendFile(connectionRegistry, currentConnection, message);
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
