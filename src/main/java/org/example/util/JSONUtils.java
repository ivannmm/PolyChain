package org.example.util;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.polychain.PolyBlock;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;

public class JSONUtils {

    public static final ObjectMapper OBJECT_MAPPER = new ObjectMapper()
            .configure(JsonParser.Feature.AUTO_CLOSE_SOURCE, true);

    public static PolyBlock fromJson(InputStream stream) {
        try {
            return OBJECT_MAPPER.readValue(stream, PolyBlock.class);
        } catch (IOException e) {
            throw new IllegalArgumentException();
        }
    }

    public static PolyBlock fromJson(JSONObject jsonObject) {
        try {
            return PolyBlock.builder()
                    .index(jsonObject.getInt("index"))
                    .prevHash(jsonObject.getString("prevHash"))
                    .data(jsonObject.getString("data"))
                    .nonce(jsonObject.getInt("nonce"))
                    .hash(jsonObject.getString("hash"))
                    .build();
        } catch (Exception e) {
            throw new IllegalArgumentException();
        }
    }

    public static String toJson(PolyBlock polyBlock) {
        try {
            return OBJECT_MAPPER.writeValueAsString(polyBlock);
        } catch (Exception e) {
            throw new IllegalArgumentException();
        }
    }
}
