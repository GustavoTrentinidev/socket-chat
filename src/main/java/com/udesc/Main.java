package com.udesc;

import com.udesc.domain.server.Server;

import java.io.IOException;

//TIP To <b>Run</b> code, press <shortcut actionId="Run"/> or
// click the <icon src="AllIcons.Actions.Execute"/> icon in the gutter.
public class Main {
    public static final int PORT = 5000;

    static void main() throws IOException {
        new Server(PORT);
    }

}