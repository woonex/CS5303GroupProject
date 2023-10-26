package edu.baylor.gitawayHotel.reservation;

import java.io.IOException;

import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;

import edu.baylor.gitawayHotel.user.User;

/**A class for simplifying users in the context of reservation JSON reading/writing
 * @author Nathan
 *
 */
class UserAdapter extends TypeAdapter<User> {
	@Override
	public void write(JsonWriter out, User user) throws IOException {
		out.beginObject();
		out.name("name").value(user.getUsername());
		out.endObject();
	}

	@Override
	public User read(JsonReader in) throws IOException {
		in.beginObject();
		String name = null;
		while (in.hasNext()) {
            String key = in.nextName();
            if ("name".equals(key)) {
                name = in.nextString();
            } else {
                in.skipValue(); // Skip any other fields
            }
        }

        in.endObject();

        if (name != null) {
           return new User(name);
        }

        return null; // If 'name' is not found in the JSON
	}
}