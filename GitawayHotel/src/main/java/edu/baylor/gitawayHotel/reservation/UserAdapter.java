package edu.baylor.gitawayHotel.reservation;

import java.lang.reflect.Type;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

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
        	return new User(jsonElement.getAsString()); // doesnt work; getAsString only works for JsonPrimitive or JsonArray, not JsonObject
        } catch (Exception e) {
            throw new JsonParseException(e);
        }
    }
}