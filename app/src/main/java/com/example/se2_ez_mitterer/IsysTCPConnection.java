package com.example.se2_ez_mitterer;

import java.net.*;
import java.io.*;

public class IsysTCPConnection {
    public static final String URI = "se2-isys.aau.at";
    public static final int PORT = 53212;

    private Socket clientSocket;
    private PrintWriter outToServer;
    private BufferedReader inFromServer;

    public IsysTCPConnection() {
    }

    public void connect() throws IOException {
        this.clientSocket = new Socket(URI, PORT);
        this.outToServer = new PrintWriter(clientSocket.getOutputStream(), true);
        this.inFromServer = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
    }

    public String getResponseFromMatrikelnummer(String matrikelnummer) throws IOException {
        outToServer.println(matrikelnummer); // wichtig ist die new line am ende, sonst wartet der Server auf weiteren Input

        // die Rueckgabe des Servers aus dem Buffer lesen
        String response = inFromServer.readLine();

        return response;
    }

    public void close() throws IOException {
        this.inFromServer.close();
        this.outToServer.close();
        this.clientSocket.close();
    }
}
