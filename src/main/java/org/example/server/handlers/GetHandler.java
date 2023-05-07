package org.example.server.handlers;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import org.example.polychain.PolyBlock;
import org.example.polychain.PolyChain;
import org.example.util.JSONUtils;

import java.io.IOException;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.util.List;

public class GetHandler implements HttpHandler {
    private final PolyChain blockchain;

    public GetHandler(PolyChain blockchain) {
        this.blockchain = blockchain;
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        String response;

        List<PolyBlock> blocks;
        String param = exchange.getRequestURI().getQuery();

        if (blockchain.getChain().isEmpty()) {
            exchange.sendResponseHeaders(HttpURLConnection.HTTP_NO_CONTENT, 0);
            OutputStream stream = exchange.getResponseBody();
            stream.write("".getBytes());
            stream.close();
            return;
        }

        if (param == null) {
            blocks = blockchain.getChain();
        } else {
            int afterIndex = getAfterParam(param);
            blocks = blockchain.getChainAfter(afterIndex);
        }

        response = JSONUtils.OBJECT_MAPPER.writeValueAsString(blocks);

        exchange.sendResponseHeaders(HttpURLConnection.HTTP_OK, response.length());
        OutputStream stream = exchange.getResponseBody();
        stream.write(response.getBytes());
        stream.close();
    }

    static int getAfterParam(String param) {
        return Integer.parseInt(param);
    }
}
