package de.footballmanager.backend.serializer;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import java.io.IOException;

public class CustomDateSerializer extends JsonSerializer<DateTime> {

    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormat.forPattern("yyyy-MM-dd");


    @Override
    public void serialize(DateTime value, JsonGenerator gen, SerializerProvider provider) throws IOException {
        gen.writeString(DATE_TIME_FORMATTER.print(value));
    }
}
