package com.udesc.domain.client;

import java.io.IOException;
import java.net.Socket;
import java.util.Scanner;

public class ClientMessageListener extends Thread {
    private Socket socket;

    public ClientMessageListener(Socket socket) {
        this.socket = socket;
    }

    @Override
    public void run() {
        try {
            Scanner scanner = new Scanner(this.socket.getInputStream());

            while (scanner.hasNextLine()) {
                System.out.println(scanner.nextLine());
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
