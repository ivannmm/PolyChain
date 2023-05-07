package org.example.strategy;

import java.util.Random;

public class RandomStrategy extends NonceStrategy {

    private final Random random = new Random();

    @Override
    public int getNonce(int nonce) {
        return random.nextInt(Integer.MAX_VALUE);
    }
}
