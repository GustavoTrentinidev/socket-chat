package com.udesc.domain.client;

import org.json.JSONObject;

import java.io.IOException;
import java.io.PrintStream;
import java.net.Socket;
import java.util.Scanner;
import java.util.UUID;

import static com.udesc.Main.PORT;

public class Client {
    private String clientName;

    public static void main() throws IOException {
        System.out.println("Enter your name: ");
        Scanner scanner = new Scanner(System.in);
        String clientName = scanner.nextLine();

        System.out.println("Hey " + clientName + " we are connecting you to the server...");

        try(Socket socket = new Socket("127.0.0.1", PORT)) {
            new ClientMessageListener(socket).start();

            var output = new PrintStream(socket.getOutputStream());

            Client.sendClientInfo(output, clientName);

            while (scanner.hasNextLine()) {
                var message = scanner.nextLine();
                Client.sendMessage(output, message);
            }

            output.close();
            scanner.close();
        }
    }

    private static void sendClientInfo(PrintStream out, String clientName) {
        var json = new JSONObject();
        json.put("clientName", clientName);
        json.put("isServerMessage", true);
        json.put("id", UUID.randomUUID().toString());
        out.println(json);
    }

    private static void sendMessage(PrintStream out, String message) {
        var json = new JSONObject();
        json.put("message", message);
        json.put("isServerMessage", false);
        out.println(json);
    }
}
