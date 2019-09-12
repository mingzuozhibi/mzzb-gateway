package mingzuozhibi.gateway;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;

import java.io.IOException;
import java.time.Instant;

public abstract class GsonUtils {

    public static Gson getGson() {
        GsonBuilder gson = new GsonBuilder();
        gson.registerTypeAdapter(Instant.class, new TypeAdapter<Instant>() {
            @Override
            public void write(JsonWriter out, Instant instant) throws IOException {
                if (instant != null) {
                    out.value(instant.toEpochMilli());
                }
            }

            @Override
            public Instant read(JsonReader in) throws IOException {
                if (in.hasNext()) {
                    return Instant.ofEpochMilli(in.nextLong());
                }
                return null;
            }
        });
        return gson.create();
    }

}
