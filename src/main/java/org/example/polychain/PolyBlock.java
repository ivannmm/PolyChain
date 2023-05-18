package org.example.polychain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.hash.Hashing;
import lombok.*;
import org.example.strategy.NonceStrategy;

import java.nio.charset.StandardCharsets;
import java.util.Objects;

@Setter
@Getter
@Builder
@AllArgsConstructor
public class PolyBlock {

    private static int lastIndex = 0;

    public static int upIndex() {
        return lastIndex++;
    }

    public static int getLastIndex() {
        return lastIndex;
    }

    public static void setLastIndex(int lastIndex) {
        PolyBlock.lastIndex = lastIndex;
    }

    @JsonProperty
    private int index;

    @JsonProperty
    private String prevHash;

    @JsonProperty
    private String data;

    @JsonProperty
    private int nonce;

    @JsonProperty
    private String hash;

    @JsonIgnore
    private NonceStrategy nonceStrategy;

    public PolyBlock() {}

    public PolyBlock(int index, String prevHash, String data, NonceStrategy nonceStrategy) {
        this.index = index;
        this.prevHash = prevHash;
        this.data = data;
        this.nonce = 0;
        this.nonceStrategy = nonceStrategy;
        this.hash = calculateHash();
    }

    public String calculateHash() {
        String calculatedHash;
        do {
            String input = String.format("%s%s%s%s", index, prevHash, data, nonce);
            calculatedHash = Hashing.sha256()
                    .hashString(input, StandardCharsets.UTF_8)
                    .toString();
            nonce = nonceStrategy.getNonce(nonce);
        } while (!isValid(calculatedHash));
        return calculatedHash;
    }

    public static boolean isValid(String curHash) {
        return curHash.endsWith("0000");
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PolyBlock otherBlock = (PolyBlock) o;
        return index == otherBlock.index && nonce == otherBlock.nonce && Objects.equals(prevHash, otherBlock.prevHash) && Objects.equals(hash, otherBlock.hash) && Objects.equals(data, otherBlock.data);
    }

    @Override
    public int hashCode() {
        return Objects.hash(index, prevHash, hash, data, nonce);
    }
}
