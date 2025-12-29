package trafficoptimizer.utils;

import java.io.*;
import java.net.*;

public class NetworkClient {

    private Socket socket;
    private BufferedReader in;
    private PrintWriter out;

    public NetworkClient(String host, int port) {
        try {
            socket = new Socket(host, port);

            in = new BufferedReader(
                    new InputStreamReader(socket.getInputStream())
            );

            out = new PrintWriter(
                    new BufferedWriter(
                            new OutputStreamWriter(socket.getOutputStream())
                    ),
                    true
            );

            System.out.println("Connected to backend");

        } catch (IOException e) {
            System.err.println("Backend connection failed");
        }
    }

    public synchronized String send(String msg) {
        try {
            out.println(msg);
            out.flush();

            StringBuilder response = new StringBuilder();
            String line;

            while ((line = in.readLine()) != null) {
                if (line.equals("END")) break;
                response.append(line).append("\n");
            }

            return response.toString().trim();

        } catch (IOException e) {
            return "ERROR";
        }
    }
}
