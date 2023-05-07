package org.example.server;

import com.sun.net.httpserver.HttpServer;
import org.example.polychain.PolyChain;
import org.example.server.handlers.AddHandler;
import org.example.server.handlers.GetHandler;

import java.io.IOException;
import java.net.InetSocketAddress;

public class PolyServer {

    public static String GET_PREFIX = "GET";

    public static String ADD_PREFIX = "ADD";

    private final PolyChain blockchain;
    private final int port;

    public PolyServer(PolyChain blockchain, int port) {
        this.blockchain = blockchain;
        this.port = port;
    }

    public void start() throws IOException {
        HttpServer server = HttpServer.create(new InetSocketAddress(port), 0);
        server.createContext("/" + GET_PREFIX, new GetHandler(blockchain));
        server.createContext("/" + ADD_PREFIX, new AddHandler(blockchain));
        server.start();
    }
}
