package org.example.server.handlers;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import org.example.polychain.PolyBlock;
import org.example.polychain.PolyChain;
import org.example.util.JSONUtils;

import java.io.IOException;
import java.io.OutputStream;
import java.net.HttpURLConnection;

public class AddHandler implements HttpHandler {

    private final PolyChain polyChain;

    public AddHandler(PolyChain polyChain) {
        this.polyChain = polyChain;
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        PolyBlock block = JSONUtils.fromJson(exchange.getRequestBody());
        if (polyChain.addBlock(block, false))
            exchange.sendResponseHeaders(HttpURLConnection.HTTP_OK, 0);
        else
            exchange.sendResponseHeaders(HttpURLConnection.HTTP_UNAVAILABLE, 0);
        OutputStream stream = exchange.getResponseBody();
        stream.close();
    }
}
