package org.example.strategy;

public class IncrementStrategy extends NonceStrategy{
    @Override
    public int getNonce(int nonce) {
        return nonce + 1;
    }
}