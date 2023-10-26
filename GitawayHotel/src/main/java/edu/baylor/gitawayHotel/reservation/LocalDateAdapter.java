package edu.baylor.gitawayHotel.reservation;

import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;

/**A class to simplify writing and reading dates from JSON
 * @author Nathan
 *
 */
public class LocalDateAdapter extends TypeAdapter<LocalDate> {
	private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

	@Override
	public void write(JsonWriter out, LocalDate value) throws IOException {
		out.beginObject();
		out.name("date").value(formatter.format(value));
		out.endObject();
	}

	@Override
	public LocalDate read(JsonReader in) throws IOException {
		in.beginObject();
		String date = null;
		while (in.hasNext()) {
			String key = in.nextName();
			if ("date".equals(key)) {
				date = in.nextString();
			} else {
				in.skipValue();
			}
		}
		
		in.endObject();
		if (date != null) {
			return LocalDate.parse(date, formatter);
		}
		
		return null;
	}
}