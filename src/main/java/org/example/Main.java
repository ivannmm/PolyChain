package org.example;

import org.example.polychain.PolyChain;
import org.example.server.PolyServer;
import org.example.strategy.NonceStrategy;
import org.example.util.Config;
import org.example.util.Generator;

import java.io.IOException;
import java.util.List;

public class Main {

    public static void main(String[] args) throws IOException {
        Config.init();
        startNode(Config.receivers, Config.port, Config.isMaster, Config.nonceStrategy);
    }

    public static void startNode(List<String> receivers, int port, boolean generateGenesis, NonceStrategy nonceStrategy) throws IOException {
        PolyChain blockchain = new PolyChain(port, receivers, generateGenesis, nonceStrategy);
        Thread generatorThread =  new Thread(new Generator(blockchain));
        PolyServer blockServer = new PolyServer(blockchain, port);

        blockServer.start();
        generatorThread.start();
    }
}