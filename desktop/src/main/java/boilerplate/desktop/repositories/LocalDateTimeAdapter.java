package boilerplate.desktop.repositories;

import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import com.google.gson.stream.JsonWriter;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

public class LocalDateTimeAdapter extends TypeAdapter<LocalDateTime> {
    
    private static final DateTimeFormatter ISO_FORMATTER = DateTimeFormatter.ISO_LOCAL_DATE_TIME;

    @Override
    public void write(JsonWriter out, LocalDateTime value) throws IOException {
        if (value == null) {
            out.nullValue();
            return;
        }
        out.value(value.format(ISO_FORMATTER));
    }

    @Override
    public LocalDateTime read(JsonReader in) throws IOException {
        if (in.peek() == JsonToken.NULL) {
            in.nextNull();
            return null;
        }
        String dateTimeString = in.nextString();
        if (dateTimeString == null || dateTimeString.isEmpty()) {
            return null;
        }
        
        // Try different formats
        try {
            // First, handle the common case: remove 'Z' suffix if present (LocalDateTime doesn't support timezone)
            String normalized = dateTimeString;
            if (normalized.endsWith("Z")) {
                normalized = normalized.substring(0, normalized.length() - 1);
            }
            
            // Try ISO format first
            try {
                return LocalDateTime.parse(normalized, ISO_FORMATTER);
            } catch (DateTimeParseException e1) {
                // Try with microseconds (6 digits)
                try {
                    return LocalDateTime.parse(normalized, DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSSSSS"));
                } catch (DateTimeParseException e2) {
                    // Try with milliseconds (3 digits)
                    try {
                        return LocalDateTime.parse(normalized, DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS"));
                    } catch (DateTimeParseException e3) {
                        // Try without fractional seconds
                        try {
                            return LocalDateTime.parse(normalized, DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss"));
                        } catch (DateTimeParseException e4) {
                            // Last resort: try default parsing
                            return LocalDateTime.parse(normalized);
                        }
                    }
                }
            }
        } catch (Exception e) {
            throw new IOException("Failed to parse datetime: " + dateTimeString, e);
        }
    }
}

