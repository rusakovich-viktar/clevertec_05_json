package org.example.serialization;

import java.lang.reflect.Field;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.Collection;
import java.util.IdentityHashMap;
import java.util.Map;
import java.util.UUID;

public class JsonSerializer {

    private Map<Object, Boolean> serializedObjects = new IdentityHashMap<>();
    private static final Map<String, String> ESCAPE_CHARACTERS = Map.of(
            "\\", "\\\\",
            "\"", "\\\"",
            "\b", "\\b",
            "\f", "\\f",
            "\n", "\\n",
            "\r", "\\r",
            "\t", "\\t"
    );

    public String serialize(Object obj) throws IllegalAccessException {
        if (serializedObjects.containsKey(obj)) {
            return "\"cyclic reference\"";
        }
        serializedObjects.put(obj, true);

        StringBuilder json = new StringBuilder();
        json.append("{");

        Field[] fields = obj.getClass().getDeclaredFields();
        for (Field field : fields) {
            field.setAccessible(true);
            Object value = field.get(obj);
            json.append("\"").append(field.getName()).append("\":");
            if (value instanceof Number || value instanceof Boolean) {
                json.append(value.toString()).append(",");
            } else if (value instanceof String || value instanceof UUID || value instanceof LocalDate) {
                json.append("\"").append(escapeSpecialCharacters(value.toString())).append("\",");
            } else if (value instanceof OffsetDateTime) {
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");
                OffsetDateTime dateTimeValue = (OffsetDateTime) value;
                OffsetDateTime rounded = dateTimeValue.truncatedTo(ChronoUnit.SECONDS);
                json.append("\"").append(rounded.atZoneSameInstant(ZoneOffset.UTC).format(formatter)).append("\",");
            } else if (value instanceof Collection) {
                json.append("[");
                for (Object item : (Collection) value) {
                    if (item != null) {
                        json.append(serialize(item)).append(",");
                    } else {
                        json.append("null,");
                    }
                }
                if (((Collection) value).size() > 0) {
                    json.setLength(json.length() - 1); // Удаление последней запятой
                }
                json.append("],");
            } else if (value instanceof Object[]) {
                json.append("[");
                for (Object item : (Object[]) value) {
                    if (item != null) {
                        json.append(serialize(item)).append(",");
                    } else {
                        json.append("null,");
                    }
                }
                if (((Object[]) value).length > 0) {
                    json.setLength(json.length() - 1); // Удаление последней запятой
                }
                json.append("],");
            } else if (value != null) {
                json.append(serialize(value)).append(",");
            } else {
                json.append("null,");
            }
        }

        if (fields.length > 0) {
            json.setLength(json.length() - 1); // Удаление последней запятой
        }
        json.append("}");
        return json.toString();
    }

    private String escapeSpecialCharacters(String str) {
        for (Map.Entry<String, String> entry : ESCAPE_CHARACTERS.entrySet()) {
            str = str.replace(entry.getKey(), entry.getValue());
        }
        return str;
    }
}
