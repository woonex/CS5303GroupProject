package edu.baylor.gitawayHotel.reservation;

import java.io.IOException;
import java.lang.reflect.Type;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;

import edu.baylor.gitawayHotel.user.User;

/**A class for simplifying users in the context of reservation JSON reading/writing
 * @author Nathan
 *
 */
class UserAdapter implements JsonSerializer<User>, JsonDeserializer<User> {
   
    @Override
    public JsonElement serialize(User user, Type type, JsonSerializationContext jsonSerializationContext) {
        return new JsonPrimitive(user.getUsername());
    }

    @Override
    public User deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
        try {
        	return new User(jsonElement.getAsString());
//            return LocalDate.parse(jsonElement.getAsString(), DATE_FORMATTER);
        } catch (Exception e) {
            throw new JsonParseException(e);
        }
    }
}