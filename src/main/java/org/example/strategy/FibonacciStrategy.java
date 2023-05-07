package org.example.strategy;

public class FibonacciStrategy extends NonceStrategy{

    private int previous = 0;
    private int current = 1;

    @Override
    public int getNonce(int nonce) {
        int newNonce = previous + current;
        previous = current;
        current = newNonce;
        return newNonce;
    }
}
