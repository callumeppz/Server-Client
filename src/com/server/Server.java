package com.server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class Server implements Runnable {

    private int port;
    private ServerSocket serverSocket;
    private boolean running = false;
    private int id = 0;
    private JFrame frame;
    private JTextArea messageArea;

    public Server(int port) {
        this.port = port;

        try {
            serverSocket = new ServerSocket(port);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void start() {
        new Thread(this).start();
        initGUI();
    }

    private void initGUI() {
        frame = new JFrame("Server");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(400, 300);

        messageArea = new JTextArea();
        messageArea.setEditable(false);
        frame.add(new JScrollPane(messageArea), BorderLayout.CENTER);

        JButton someButton = new JButton("Shutdown Server"); // Create a new JButton
        someButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                displayMessage("Shutdown Server");
                System.exit(0);
            }
        });

        JPanel buttonPanel = new JPanel(); // Create a panel to hold the button
        buttonPanel.add(someButton); // Add the button to the panel

        frame.add(buttonPanel, BorderLayout.SOUTH); // Add the panel to the frame

        frame.setVisible(true);
    }

    public void displayMessage(String message) {
        SwingUtilities.invokeLater(() -> messageArea.append(message + "\n"));
    }

    @Override
    public void run() {
        running = true;
        displayMessage("Server started on port: " + port);

        while (running) {
            try {
                Socket socket = serverSocket.accept();
                initSocket(socket);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        shutdown();
    }

    private void initSocket(Socket socket) {
        Connection connection = new Connection(socket, id, this);
        ConnectionHandler.connections.put(id, connection);
        new Thread(connection).start();
        id++;

        showPlayerJoinedDialog(socket.getInetAddress().getHostAddress());
    }

    private int playerCount = 0; // Declare the playerCount variable outside the method

    private void showPlayerJoinedDialog(String ipAddress) {
        playerCount++; // Increment the player count for each new player
        displayMessage("Player joined: " + playerCount + " " + ipAddress);
    }



    public void shutdown() {
        running = false;

        try {
            serverSocket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        Server server = new Server(5000);
        server.start();
    }
}
