package eu.joaorodrigo.demos.blockchain.adapters;

import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import eu.joaorodrigo.demos.blockchain.Block;

import java.io.IOException;

public class BlockAdapter extends TypeAdapter<Block> {
    @Override
    public void write(JsonWriter jsonWriter, Block block) throws IOException {
        jsonWriter.beginObject();
        jsonWriter.name("id");
        jsonWriter.value(block.getId());
        jsonWriter.name("hash");
        jsonWriter.value(block.getHash());
        jsonWriter.name("previousHash");
        jsonWriter.value(block.getPreviousHash());
        jsonWriter.name("previousBlockId");
        jsonWriter.value(block.getPreviousBlock() != null ? block.getPreviousBlock().getId() : 0);
        jsonWriter.endObject();
    }

    @Override
    public Block read(JsonReader jsonReader) throws IOException {
        return null;
    }
}
