package alteran.dimensions;

import com.google.gson.*;

public class DimensionDescriptor {
  public String skyColor = "#ff0000";

  private static final Gson GSON = new GsonBuilder().setPrettyPrinting().disableHtmlEscaping().create();
  private static final Gson GSON_COMPACT = new GsonBuilder().disableHtmlEscaping().create();

  public void read(String json) {
    JsonParser parser = new JsonParser();
    JsonElement root = parser.parse(json);
    JsonObject object = root.getAsJsonObject();

    read(object);
  }

  public void read(JsonObject object) {
    if (object.has("sky_color")) {
      this.skyColor = object.get("sky_color").getAsString();
    } else {
      this.skyColor = "#00ff00";
    }
  }

  public String write() {
    JsonObject obj = new JsonObject();

    obj.addProperty("sky_color", this.skyColor);

    return GSON.toJson(obj);
  }

  public String compact() {
    JsonObject obj = new JsonObject();

    obj.addProperty("sky_color", this.skyColor);
    return GSON_COMPACT.toJson(obj);
  }
}
