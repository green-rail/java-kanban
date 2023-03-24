package ru.smg.kanban.httpserver;

import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import com.google.gson.stream.JsonWriter;
import ru.smg.kanban.tasks.Task;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;

public class DateTimeTypeAdapter extends TypeAdapter<LocalDateTime> {
    @Override
    public void write(JsonWriter writer, LocalDateTime localDateTime) throws IOException {
        if (localDateTime == null) {
            writer.nullValue();
            return;
        }
        writer.value(localDateTime.format(Task.startTimeFormatter));

    }

    @Override
    public LocalDateTime read(JsonReader reader) throws IOException {
        if (reader.peek() == JsonToken.NULL) {
            reader.nextNull();
            return null;
        }
        try {
            return LocalDateTime.parse(reader.nextString(), Task.startTimeFormatter);
        } catch (DateTimeParseException e) {
            return null;
        }
    }
}
