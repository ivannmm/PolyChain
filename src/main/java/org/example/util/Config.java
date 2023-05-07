package org.example.util;

import org.example.strategy.FibonacciStrategy;
import org.example.strategy.IncrementStrategy;
import org.example.strategy.NonceStrategy;
import org.example.strategy.RandomStrategy;

import java.util.Arrays;
import java.util.List;

public class Config {

    public static List<String> receivers;
    public static int port;
    public static boolean isMaster;

    public static NonceStrategy nonceStrategy;

    public static void init() {
        receivers = Arrays.asList(System.getenv("RECEIVERS").split(","));
        port = Integer.parseInt(System.getenv("PORT"));
        isMaster = Boolean.parseBoolean(System.getenv("MASTER"));
        String strategy = System.getenv("STRATEGY");
        switch (strategy) {
            case "rand":
                nonceStrategy = new RandomStrategy();
                break;
            case "fib":
                nonceStrategy = new FibonacciStrategy();
                break;
            case "inc":
                nonceStrategy = new IncrementStrategy();
                break;
        }
    }

}
