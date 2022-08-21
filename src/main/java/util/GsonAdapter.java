package util;

import com.google.gson.*;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import managers.InMemoryHistoryManager;
import tasks.Task;

import javax.swing.*;
import java.io.IOException;
import java.lang.reflect.Type;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Optional;

public class GsonAdapter {
    public static class LocalDateTimeSerializer implements JsonSerializer<LocalDateTime> {

        private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");


        @Override
        public JsonElement serialize(LocalDateTime localDateTime, Type srcType, JsonSerializationContext context) {
            return new JsonPrimitive(formatter.format(localDateTime));
        }


    }

    public static class LocalDateTimeDeserializer implements JsonDeserializer<LocalDateTime> {
        @Override
        public LocalDateTime deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context)
                throws JsonParseException {
            return LocalDateTime.parse(json.getAsString(),
                    LocalDateTimeSerializer.formatter);
        }
    }

/*    public static class NodeSerializer implements JsonSerializer<InMemoryHistoryManager.Node> {
*//*        @Override
        public JsonElement serialize(Dwarf src, Type typeOfSrc, JsonSerializationContext context)
        {
            JsonObject result = new JsonObject();

            result.addProperty("name", src.getName());
            result.addProperty("age", src.getDwarfAge());
            result.add("facialHair", context.serialize(src.getFacialHair()));

            JsonArray weapons = new JsonArray();
            result.add("weapons", weapons);
            for(Weapon weapon : src.getWeapons()) {
                weapons.add(
                        weapon instanceof UniqueWeapon ?
                                context.serialize(weapon) :
                                new JsonPrimitive(weapon.getType())
                );
            }

            return result;
        }*//*

        @Override
        public JsonElement serialize(InMemoryHistoryManager.Node node, Type type, JsonSerializationContext jsonSerializationContext) {
            JsonObject result = new JsonObject();

            Optional<InMemoryHistoryManager.Node> prev = Optional.ofNullable(node.getPrev());
            Optional<InMemoryHistoryManager.Node> next = Optional.ofNullable(node.getNext());
            Optional<Task> item = Optional.ofNullable(node.getItem());

            Type typeTask = new TypeToken<Task>() {
            }.getType();


            next.stream()
                    .map(InMemoryHistoryManager.Node::getItem)
                    .findAny().
                    ifPresentOrElse(x -> result.add("next", jsonSerializationContext.serialize(x, typeTask)),
                    () -> result.add("next", null));

            prev.stream().map(InMemoryHistoryManager.Node::getItem)
                    .findAny()
                    .ifPresentOrElse(x -> result.add("prev", jsonSerializationContext.serialize(x, typeTask)),
                    () -> result.add("prev", null));

            item.ifPresentOrElse(x -> result.add("item", jsonSerializationContext.serialize(x, typeTask)),
                    () -> result.add("item", null));


            return result;
        }
    }

    public static class NodeDeserializer implements JsonDeserializer<ArrayList<InMemoryHistoryManager.Node>> {

        @Override
        public ArrayList<InMemoryHistoryManager.Node> deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {


            JsonObject obj = jsonElement.getAsJsonObject();
            JsonArray jsonNodesArray = obj.get("nodes").getAsJsonArray();
            Type mapType = new TypeToken<ArrayList<InMemoryHistoryManager.Node>>() {}.getType();
            Type nodeType = new TypeToken<InMemoryHistoryManager.Node>() {}.getType();
            GsonBuilder builder = new GsonBuilder();

            Gson gson = builder
                    .registerTypeAdapter(LocalDateTime.class, new GsonAdapter.LocalDateTimeDeserializer())
                    .serializeNulls()
                    .create();;
            ArrayList<InMemoryHistoryManager.Node> nodesToImport = new ArrayList<>();
            for (JsonElement element: jsonNodesArray) {
                JsonPrimitive asJsonPrimitive = element.getAsJsonPrimitive();
                Task model = gson.fromJson(element, Task.class);

                JsonObject asJsonObject = asJsonPrimitive.getAsJsonObject();


                new InMemoryHistoryManager.Node(jsonDeserializationContext.deserialize(asJsonPrimitive,nodeType));
            }

            nodesToImport = jsonDeserializationContext.deserialize(jsonNodesArray, mapType);

            return nodesToImport;

        }
    }

    public static class NodeAdapter extends TypeAdapter<InMemoryHistoryManager.Node> {
        final Gson embedded = new Gson();

        public NodeAdapter() {
            super();
        }

        @Override
        public void write(final JsonWriter out, final InMemoryHistoryManager.Node node)
                throws IOException {
            out.beginObject();

            out.name("next");
            out.value(node.getNext().getItem().getId() != null ? node.getNext().getItem().getId().toString() : "null");
            // embedded.toJson(embedded.toJsonTree(node.getNext().getItem().getId()), out);

            out.name("prev");
            Optional<InMemoryHistoryManager.Node> prev = Optional.ofNullable(node.getPrev());
            out.value("null");
            prev.stream()
                    .map(x -> x.getItem().getId())
                    .findFirst().ifPresent(x -> {
                        try {
                            out.value(x);
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }
                    });

            //   out.value(node.getPrev().getItem().getId() != null ? node.getPrev().getItem().getId().toString() : "null");

          *//*  out.name("prev");
            embedded.toJson(embedded.toJsonTree(node.getPrev().getItem().getId()), out);*//*

         *//*   out.name("item");
            embedded.toJson(embedded.toJsonTree(node.getItem()), out);*//*

            out.endObject();
        }


*//*        @Override
        public void write(JsonWriter writer, Student student) throws IOException {
            writer.beginObject();
            writer.name("name");
            writer.value(student.getName());
            writer.name("rollNo");
            writer.value(student.getRollNo());
            writer.endObject();
        }*//*

        @Override
        public InMemoryHistoryManager.Node read(JsonReader jsonReader) throws IOException {
            throw new UnsupportedOperationException();
        }


    }*/

}


