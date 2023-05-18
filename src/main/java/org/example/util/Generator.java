package org.example.util;

import org.example.polychain.PolyBlock;
import org.example.polychain.PolyChain;

import java.nio.charset.StandardCharsets;
import java.util.Random;

public class Generator implements Runnable {

    PolyChain polyChain;

    public Generator(PolyChain polyChain) {
        this.polyChain = polyChain;
    }

    @Override
    public void run() {
        while (true) {
            try {
                if (!polyChain.isEmpty()) {
                    PolyBlock lastBlock = polyChain.getLastBlock();
                    int newIndex = (lastBlock == null) ? 0 : PolyBlock.upIndex();
                    String prevHash = (lastBlock == null) ? "0" : lastBlock.getHash();
                    PolyBlock block = new PolyBlock(newIndex, prevHash, generateData(), polyChain.getNonceStrategy());
                    polyChain.addBlock(block, true);
                }
                Thread.sleep(10000 + new Random().nextInt(20000));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public String generateData() {
        byte[] array = new byte[8];
        new Random().nextBytes(array);
        return new String(array, StandardCharsets.UTF_8);
    }
}
