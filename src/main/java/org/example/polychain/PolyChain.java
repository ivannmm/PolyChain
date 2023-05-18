package org.example.polychain;

import lombok.Getter;
import org.example.server.PolyServer;
import org.example.strategy.NonceStrategy;
import org.example.util.JSONUtils;
import org.json.JSONArray;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

@Getter
public class PolyChain {

    private final int port;
    private final List<String> receivers;
    private final List<PolyBlock> chain;

    private final boolean isGenesis;

    NonceStrategy nonceStrategy;

    public PolyChain(int port, List<String> receivers, boolean isGenesisBlock, NonceStrategy nonceStrategy) {
        this.port = port;
        this.receivers = receivers;

        this.chain = new ArrayList<>();
        if (isGenesisBlock) {
            PolyBlock genesisBlock = generateGenesisBlock(nonceStrategy);
            addBlock(genesisBlock, true);
        } else
            reload();
        this.isGenesis = isGenesisBlock;
        this.nonceStrategy = nonceStrategy;
    }

    public List<PolyBlock> getChainAfter(int i) {
        return i < 0 || i >= chain.size() ? Collections.emptyList() : chain.subList(i, chain.size());
    }

    public boolean hashExist(String hash) {
        for (PolyBlock polyBlock : chain) {
            if (polyBlock.getHash().equals(hash))
                return true;
        }
        return false;
    }

    public boolean isEmpty() {
        return chain.isEmpty();
    }

    public PolyBlock getLastBlock() {
        return chain.isEmpty() ? null : chain.get(chain.size() - 1);
    }

    public boolean addBlock (PolyBlock block, boolean sendAll) {
        if (block == null)
            return false;

        String prevHash = null;
        PolyBlock lastBlock = getLastBlock();
        if (lastBlock != null) {
            prevHash = getLastBlock().getHash();
        }
        block.setNonceStrategy(nonceStrategy);
        chain.add(block);
        System.out.printf("[%d] Add block - %s%n, last hash - %s \n", port, block.getHash(), prevHash);

        if (sendAll)
            sendAll(block);
        return true;
    }

    public void sendAll(PolyBlock polyBlock){
        System.out.printf("[%d] Sending new block...%s\n", port, Arrays.toString(receivers.toArray()));
        System.out.println("============================================================");
        for (String receiver : receivers) {
            try {
                HttpClient tempClient = HttpClient.newHttpClient();
                HttpRequest request = buildRequest(receiver, polyBlock);

                HttpResponse<String> response = tempClient.send(request, HttpResponse.BodyHandlers.ofString());
                if (response.statusCode() != HttpURLConnection.HTTP_OK)
                    System.out.printf("[%d] Failed to add block, server - %s\n", port, receiver);
                else
                    System.out.printf("[%d] Successful to add block, server - %s\n", port, receiver);
                System.out.println("============================================================");
            } catch (IOException | InterruptedException e) {
                System.out.printf("[%d] Unsuccessful connection to the server - %s\n", port, receiver);
            }
        }
        System.out.println("");
    }

    public void reload() {
        for (String receiver : receivers) {
            try {
                int lastIndex = PolyBlock.getLastIndex();
                HttpURLConnection connection = (HttpURLConnection) new URL(String.format("%s/%s?%d", receiver, PolyServer.GET_PREFIX, lastIndex)).openConnection();
                connection.setRequestMethod(PolyServer.GET_PREFIX);

                if (connection.getResponseCode() == HttpURLConnection.HTTP_NO_CONTENT)
                    System.out.println("NO CONTENT");

                if (connection.getResponseCode() == HttpURLConnection.HTTP_OK) {
                    BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                    String stringJSON = reader.readLine();
                    reader.close();

                    JSONArray jsonArray = new JSONArray(stringJSON);
                    for (int i = 0; i < jsonArray.length(); i++) {
                        PolyBlock polyBlock = JSONUtils.fromJson(jsonArray.getJSONObject(i));
                        if (!hashExist(polyBlock.getHash()))
                            addBlock(polyBlock, false);
                    }

                }
            } catch (Exception e) {}
        }
    }

//    public void validateChain()

    static PolyBlock generateGenesisBlock(NonceStrategy nonceStrategy) {
        return new PolyBlock(PolyBlock.upIndex(), "0", "First block PolyCryptocurrency", nonceStrategy);
    }

    static HttpRequest buildRequest(String receiver, PolyBlock polyBlock) {
        return HttpRequest.newBuilder()
                .uri(URI.create(receiver + "/" + PolyServer.ADD_PREFIX))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(JSONUtils.toJson(polyBlock)))
                .build();
    }

}
