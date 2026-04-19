package com.udesc.domain.client;

import org.json.JSONObject;

import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

public class ClientMessageListener extends Thread {
    private Socket socket;
    private final Map<String, OutputStream> transfers = new HashMap<>();
    private final Map<String, Path> transferPaths = new HashMap<>();

    public ClientMessageListener(Socket socket) {
        this.socket = socket;
    }

    @Override
    public void run() {
        try {
            Scanner scanner = new Scanner(this.socket.getInputStream());

            while (scanner.hasNextLine()) {
                String line = scanner.nextLine();

                if (line.startsWith("FILE_TRANSFER_START ")) {
                    String jsonPayload = line.substring("FILE_TRANSFER_START ".length());
                    JSONObject start = new JSONObject(jsonPayload);
                    String transferId = start.getString("transferId");
                    String fileName = start.getString("fileName");
                    Path outputPath = Paths.get(System.getProperty("user.dir"), fileName);
                    OutputStream outputStream = Files.newOutputStream(
                        outputPath,
                        java.nio.file.StandardOpenOption.CREATE,
                        java.nio.file.StandardOpenOption.TRUNCATE_EXISTING,
                        java.nio.file.StandardOpenOption.WRITE
                    );

                    transfers.put(transferId, outputStream);
                    transferPaths.put(transferId, outputPath);
                    continue;
                }

                if (line.startsWith("FILE_TRANSFER_CHUNK ")) {
                    String jsonPayload = line.substring("FILE_TRANSFER_CHUNK ".length());
                    JSONObject chunk = new JSONObject(jsonPayload);
                    String transferId = chunk.getString("transferId");
                    String contentBase64 = chunk.getString("contentBase64");

                    OutputStream outputStream = transfers.get(transferId);
                    if (outputStream != null) {
                        byte[] fileBytes = Base64.getDecoder().decode(contentBase64);
                        outputStream.write(fileBytes);
                    }
                    continue;
                }

                if (line.startsWith("FILE_TRANSFER_END ")) {
                    String jsonPayload = line.substring("FILE_TRANSFER_END ".length());
                    JSONObject end = new JSONObject(jsonPayload);
                    String transferId = end.getString("transferId");

                    OutputStream outputStream = transfers.remove(transferId);
                    Path outputPath = transferPaths.remove(transferId);
                    if (outputStream != null) {
                        outputStream.close();
                    }

                    if (outputPath != null) {
                        System.out.println("File received and saved to: " + outputPath.toAbsolutePath());
                    }
                    continue;
                }

                System.out.println(line);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        } finally {
            for (OutputStream outputStream : transfers.values()) {
                try {
                    outputStream.close();
                } catch (IOException ignored) {
                }
            }
            transfers.clear();
            transferPaths.clear();
        }
    }
}
